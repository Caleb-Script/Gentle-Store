package com.gentle.store.customer.service;

import brave.Tracer;
import com.gentle.store.customer.dto.CustomerActivityDTO;
import com.gentle.store.customer.dto.CustomerDTO;
import com.gentle.store.customer.dto.CustomerUpdateDTO;
import com.gentle.store.customer.dto.PhoneNumberDTO;
import com.gentle.store.customer.entity.Customer;
import com.gentle.store.customer.mail.Mailer;
import com.gentle.store.customer.mapper.CustomerMapper;
import com.gentle.store.customer.repository.CustomerRepository;
import com.gentle.store.customer.security.CustomUserDetailsService;
import com.gentle.store.customer.service.exception.ConstraintViolationsException;
import com.gentle.store.customer.service.exception.EmailExistsException;
import com.gentle.store.customer.service.exception.NotFoundException;
import com.gentle.store.customer.service.exception.VersionOutdatedException;
import com.gentle.store.customer.service.patch.CustomerPatcher;
import com.gentle.store.customer.service.patch.PatchOperation;
import com.gentle.store.customer.transfer.ItemDTO;
import com.gentle.store.customer.transfer.PaymentDTO;
import com.gentle.store.customer.transfer.TotalAmount;
import com.gentle.store.customer.util.MailProps;
import com.google.common.base.Splitter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.IntStream;

import static com.gentle.store.customer.entity.enums.ActivityType.CHANGE;
import static com.gentle.store.customer.entity.enums.ActivityType.SIGN_UP;
import static com.gentle.store.customer.entity.enums.CustomerStatusType.ACTIVE;
import static com.gentle.store.customer.util.Constants.*;
import static com.gentle.store.customer.util.VersionUtils.getVersion;

/**
 * Anwendungslogik für Kunden auch mit Bean Validation.
 * <img src="../../../../../asciidoc/CustomerWriteService.svg" alt="Klassendiagramm">
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteService {
    private final CustomerRepository customerRepository;
    private final Validator validator;
    private final CustomUserDetailsService userService;
    private final Mailer mailer;
    private final MailProps props;
    private final WebClient.Builder webClientBuilder;
    private final CustomerMapper customerMapper;
    private final CustomerPatcher customerPatcher;
    private final Tracer tracer;

    public Customer create(final CustomerDTO customerDTO, final UserDetails user) {
        log.debug("create: customerDTO={}", customerDTO);
        log.debug("create: user={}", user);

        final var kpp = tracer.nextSpan().name("hmm");
        try(final var lll = tracer.withSpanInScope(kpp.start())) {

        customerDTO.activities().add(new CustomerActivityDTO(SIGN_UP, STR."benutzer \{user.getUsername()} wurde erstellt."));
        final var customer = customerMapper.toCustomer(customerDTO);
        customer.setUsername(user.getUsername());
        customer.setStatus(ACTIVE);

        log.debug("create: customer={}", customer);
        log.debug("create: address={}", customer.getAddress());
        log.debug("create: phoneNumber={}", customer.getPhoneNumberList());
        log.debug("create: activities={}", customer.getActivities());

        final var violations = validator.validate(customer, Default.class, Customer.newValidation.class);
        if (!violations.isEmpty()) {
            log.debug("create: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }

        if (customerRepository.existsByEmail(customer.getEmail()))
            throw new EmailExistsException(customer.getEmail());


        final var login = userService.save(user);
        log.trace("create: login={}", login);

        final var newPhoneNumbers = new ArrayList<>(customer.getPhoneNumberList());

        final var customerDb = customerRepository.save(customer);
        newPhoneNumbers.forEach(customerDb::addPhoneNumber);

        IntStream.range(0, newPhoneNumbers.size()).forEach(phoneNumber -> customerDb.getPhoneNumberList().removeFirst());

        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

        props.setTo(customerDb.getEmail());
        mailer.send(customerDb);

        log.debug("create: customerDb={}", customerDb);

        final var path = STR."\{customerDb.getId()}";
        final var  cartClientResponse = shoppingCartClient(path, null,  user);
        log.debug("create: cartClientResponse={}",cartClientResponse);
        return customerDb;
        } finally {
            kpp.finish();
        }
    }

    public String removeItems(UUID id, List<ItemDTO> itemDTOs) {

        final var path = STR."remove/\{id}";
        return shoppingCartClient(path, itemDTOs, null);
    }

    public String addItem(UUID id, List<ItemDTO> orderedItemDTOs) {

        final var path = STR."add/\{id}";
        return shoppingCartClient(path, orderedItemDTOs, null);
    }

    public Customer put (final UUID id, final CustomerUpdateDTO customerUpdateDTO, final Optional<String> version, final HttpServletRequest request) {
        log.debug("put: id={}, customerUpdateDTO={}", id, customerUpdateDTO);

        final int versionInt = getVersion(version, request);
        log.debug("put: version={}",versionInt);

        customerUpdateDTO.activities().add(new CustomerActivityDTO(CHANGE, "Benützerdaten wurden geändert"));
        final var customer = customerMapper.toCustomer(customerUpdateDTO);
        return update(customer, id, versionInt);
    }

    public Customer patch (final UUID id, final Collection<PatchOperation> operations, final Optional<String> version, final HttpServletRequest request){
        log.debug("patch: id={}, version={}, operations={}", id, version, operations);

        final int versionInt = getVersion(version, request);
        log.debug("patch: version={}",versionInt);

        final var customerDb = customerRepository.findByIdFetchAll(id).orElseThrow(NotFoundException::new);
        log.debug("patch: customer={}",customerDb);

        customerPatcher.patch(customerDb, operations, request);
        log.debug("patch: {}", customerDb);

        final var customerUpdateDTO = customerMapper.toCustomerUpdateDTO(customerDb);
        customerUpdateDTO.activities().add(new CustomerActivityDTO(CHANGE, "Benützerdaten wurden geändert"));

        if (!customerDb.getPhoneNumberList().isEmpty()) {
            operations.stream()
                    .filter(path -> path.path().equals("phoneNumber"))
                    .map(PatchOperation::value)
                    .forEach(phoneNumberString -> {
                        final var phoneNumber = Splitter.on("/").splitToList(phoneNumberString);
                        if (phoneNumber.size() == 2) {
                            final var dialingCode = phoneNumber.getFirst();
                            final var number = phoneNumber.getLast();
                            customerUpdateDTO.phoneNumberList().add(new PhoneNumberDTO(dialingCode, number, false));
                            log.debug("Phone number: {}/{}", dialingCode, number);
                        }
                    });
        }

        final var customer = customerMapper.toCustomer(customerUpdateDTO);
        log.debug("patch: customer={}", customer);
        log.debug("patch: address={}", customer.getAddress());
        log.debug("patch: phoneNumber={}", customer.getPhoneNumberList());
        log.debug("patch: activities={}", customer.getActivities());

        return update(customer, id, versionInt);
    }

    public Customer update(final Customer customer, final UUID id, final int version) {
        log.debug("update: id_{} version={}", id, version);
        log.debug("update: customer={}", customer);
        log.debug("update: address={}", customer.getAddress());
        log.debug("update: phoneNumber={}", customer.getPhoneNumberList());
        log.debug("update: activities={}", customer.getActivities());

        final var violations = validator.validate(customer);
        if (!violations.isEmpty()) {
            log.debug("update: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }
        log.trace("update: Keine Constraints verletzt");

        final var customerDb = customerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.debug("update: version={}, customerDb={}", version, customerDb);
        log.debug("update: activities={}", customerDb.getActivities());


        if (version < customerDb.getVersion()) {
            log.error("version ist nicht die Aktuelle Version");
            throw new VersionOutdatedException(version);
        }
        if (version > customerDb.getVersion()) {
            log.error("version gibt es noch nicht");
            throw new VersionOutdatedException(version);
        }

        final var email = customer.getEmail();
        if (!Objects.equals(email, customerDb.getEmail()) && customerRepository.existsByEmail(email)) {
            log.error("update: email {} existiert", email);
            throw new EmailExistsException(email);
        }
        log.trace("update: Kein Konflikt mit der Emailadresse");


        customerDb.set(customer);
        if (customer.getInterests() != null) {
            customerDb.setInterestsString(customer.getInterests());
            log.trace("NEW interestsString: {}", customerDb.getInterestsString());
        }
        customerDb.setContactOptionsString(customer.getContactOptions());
        log.trace("NEW contactOptionsString: {}", customerDb.getContactOptionsString());


        final var newPhoneNumber = !customer.getPhoneNumberList().isEmpty()
                ? customer.getPhoneNumberList().getLast()
                : null;

        final var updatedCustomerDb = customerRepository.save(customerDb);
        customerDb.addActivity(customer.getActivities().getLast());

        if (!customer.getPhoneNumberList().isEmpty()) {
            customerDb.getPhoneNumberList()
                    .stream()
                    .filter(phoneNumber -> phoneNumber.getNumber() == null)
                    .toList()
                    .forEach(customerDb::removePhoneNumber2);
            customerDb.addPhoneNumber(newPhoneNumber);
        }

        log.debug("update: customer={}", updatedCustomerDb);
        log.debug("update: address={}", updatedCustomerDb.getAddress());
        log.debug("update: phoneNumber={}", updatedCustomerDb.getPhoneNumberList());
        log.debug("update: activities={}", updatedCustomerDb.getActivities());

        return updatedCustomerDb;
    }

    /**
     * Einen Kunden löschen.
     *
     * @param id Die ID des zu löschenden Kunden.
     */
    public String deleteById(final UUID id) {
        log.debug("deleteById: id={}", id);

        final var customer = customerRepository.findById(id).orElseThrow(NotFoundException::new);
        customerRepository.delete(customer);
        return deleteCart(id);
    }


    public String makePayment(final String orderNumber, final UUID id) {
        log.info("makePayment: id={} orderNumber={}", id, orderNumber);

        final var totalAmount = completeOrder(orderNumber).totalAmount();
        final var paymentDetails = new PaymentDTO(orderNumber,totalAmount);
        log.debug("makePayment: paymentDTO={}",paymentDetails);

        pay(paymentDetails,id);
        log.info("makePayment: payed!");
        return "asd";
    }


    public String placeOrder(List<ItemDTO> itemDTO, final UUID id) {
        log.debug("placeOrder: id={} orderDto={}", id, itemDTO);

        final var path = STR."order/\{id}";
        return shoppingCartClient(path, itemDTO, null);

    }

    private TotalAmount completeOrder(String orderNumber) {
        log.debug("completeOrder: orderNumber={}",orderNumber);



        log.info("completeOrder: pay()");
        log.info("completeOrder: starting communication with the order-service...");

        return webClientBuilder.build()
                .post()
                .uri(STR."\{ORDER_CLIENT}/buy/\{orderNumber}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(""))
                .retrieve()
                .bodyToMono(TotalAmount.class)
                .block();

    }

    private void pay(final PaymentDTO paymentDetails, final UUID id) {
        log.debug("pay: id={} paymentDetails={}", id, paymentDetails);

        webClientBuilder.build()
                .post()
                .uri(STR."\{PAYMENT_CLIENT}/\{id}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(paymentDetails))
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
    public String deleteCart(final UUID id) {
        log.info("deleteCart!");

        return webClientBuilder.build()
                .delete()
                .uri(STR."\{SHOPPING_CART_CLIENT}/\{id}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }



    private String shoppingCartClient(final String path, final List<ItemDTO> orderedItems, final UserDetails user) {
        log.debug("cartClient: path={}",path);


        if(orderedItems != null) {
            log.debug("cartClient (add Items): orderedItems={}", orderedItems);
        }
        if(user != null) {
            log.debug("cartClient user={}", user);
        }

        var body = (orderedItems == null)
                ? user
                : orderedItems;


        return webClientBuilder.build()
                .post()
                .uri(STR."\{SHOPPING_CART_CLIENT}/\{path}")
                .header("Authorization",ADMIN_BASIC_AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
