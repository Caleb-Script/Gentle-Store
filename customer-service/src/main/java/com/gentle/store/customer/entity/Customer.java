package com.gentle.store.customer.entity;

import com.gentle.store.customer.entity.enums.*;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.gentle.store.customer.util.Constants.*;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Collections.emptyList;

@Entity
@Table(name = "customer")
@NamedEntityGraph(name = ADDRESS_GRAPH, attributeNodes = @NamedAttributeNode("address"))
@NamedEntityGraph(name = ALL_GRAPH, attributeNodes = {
        @NamedAttributeNode("address"), @NamedAttributeNode("activities"), @NamedAttributeNode("activities")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Customer {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    private int version;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    @Size(max = NAME_MAX_LENGTH, message = "Der Nachname ist zu lang! sry:(")
    @Pattern(regexp = SURNAME_PATTERN)
    @NotBlank(message = "dein Nachname darf nicht leer sein!")
    @NotNull(message = "du musst deinen Nachname eingeben!")
    private String surname;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    @Size(max = NAME_MAX_LENGTH, message = "Der Vorname ist zu lang! sry:(")
    @Pattern(regexp = FIRST_NAME_PATTERN, message = "Dein Vorname (sollte) nur Buchstaben enthalten")
    @NotBlank(message = "dein Vorname darf nicht leer sein!")
    @NotNull(message = "du musst deinen Vornamen eingeben!")
    private String forename;

    @Column(nullable = false, unique = true, length = EMAIL_MAX_LENGTH)
    @Email(message = "keine gültige email!")
    @NotNull(message = "Die E-Mail-Adresse darf nicht null sein")
    @Size(max = EMAIL_MAX_LENGTH, message = "Die E-Mail-Adresse darf nicht länger als {max} Zeichen sein")
    private String email;

    @Column(nullable = false)
    @Min(value = MIN_CATEGORY, message = "Die Kundenkategorie muss mindestens {value} sein")
    @Max(value = MAX_CATEGORY, message = "Die Kundenkategorie darf maximal {value} sein")
    private Integer customerCategory;

    @Column(nullable = false)
    @Past(message = "Das Geburtsdatum muss in der Vergangenheit liegen")
    private LocalDate birthDate;

    @Column(nullable = false)
    private Boolean hasNewsletter;

    @NotNull(message = "Das Geschlecht darf nicht null sein")
    @Enumerated(STRING)
    @Column(nullable = false, length = 7)
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private CustomerStatusType status;

    @NotNull(message = "Der Familienstand darf nicht null sein")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private MaritalStatusType maritalStatus;

    @Transient
    @UniqueElements(message = "Die Interessenliste darf keine Duplikate enthalten")
    private List<InterestType> interests;
    @Column(name = "interests")
    private String interestsString;

    @NotNull(message = "kontak optionen dürfen nicht null sein")
    @Transient
    @UniqueElements
    private List<ContactOptionsType> contactOptions;
    @Column(name = "contact_options")
    private String contactOptionsString;

    @ToString.Exclude
    @OneToOne(fetch = LAZY, cascade = {PERSIST, REMOVE}, optional = false, orphanRemoval = true)
    @Valid
    @NotNull(groups = newValidation.class)
    private Address address;

    @NotNull
    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<CustomerActivity> activities;

    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<PhoneNumber> phoneNumberList;

    @CreationTimestamp
    private LocalDateTime created;
    @UpdateTimestamp
    private LocalDateTime updated;

    @Pattern(regexp = USERNAME_PATTERN, message = "Der Benutzername muss zwischen 4 und 20 alphanumerischen Zeichen sein")
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "Der Benutzername muss zwischen 4 und 20 Zeichen lang sein")
    @Column(nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    private String username;

    public void set(final Customer customer) {
        surname = customer.getSurname();
        forename = customer.getForename();
        email = customer.getEmail();
        customerCategory = customer.getCustomerCategory();
        hasNewsletter = customer.getHasNewsletter();
        birthDate = customer.getBirthDate();
        gender = customer.getGender();
        status = customer.getStatus();
        maritalStatus = customer.getMaritalStatus();
    }

    public void setInterestsString(final List<InterestType> interests) {
        final var interestsStringList = interests.stream()
                .map(Enum::name)
                .toList();
        this.interestsString = String.join(",", interestsStringList);
    }

    public void setContactOptionsString(final List<ContactOptionsType> optionsTypes) {
        final var contactOptionsStringList = optionsTypes.stream()
                .map(Enum::name)
                .toList();
        this.contactOptionsString = String.join(",", contactOptionsStringList);
    }

    public void removePhoneNumber(String dialingCode, String number) {
        phoneNumberList.removeIf(
                phoneNumber -> phoneNumber.getDialingCode().equals(dialingCode) &&
                        phoneNumber.getNumber().equals(number)
        );
    }

    public void removePhoneNumber2(PhoneNumber phoneNumber) {
        phoneNumberList.remove(phoneNumber);
    }

    public void addActivity(CustomerActivity customerActivity) {
        activities.add(customerActivity);
    }
    public void addPhoneNumber(PhoneNumber phoneNumber) {
        phoneNumberList.add(phoneNumber);
    }

    @PrePersist
    private void buildInterestsStr() {
        if (interests == null || interests.isEmpty()) {
            interestsString = null;
        } else {
            final var stringList = interests.stream()
                    .map(Enum::name)
                    .toList();
            interestsString = String.join(",", stringList);
        }

        if (contactOptions == null || contactOptions.isEmpty()) {
            contactOptionsString = null;
            return;
        }
        final var stringList = contactOptions.stream()
                .map(Enum::name)
                .toList();
        contactOptionsString = String.join(",", stringList);
    }

    @PostLoad
    private void loadInterests() {
        if (interestsString == null) {
            interests = emptyList();
        } else {
            final var interestsArray = interestsString.split(",");
            interests = Arrays.stream(interestsArray)
                    .map(InterestType::valueOf)
                    .collect(Collectors.toList());
        }
        if (contactOptionsString == null) {
            contactOptions = emptyList();
        }
        final var contactOptionsArray = contactOptionsString.split(",");
        contactOptions = Arrays.stream(contactOptionsArray)
                .map(ContactOptionsType::valueOf)
                .collect(Collectors.toList());
    }

    public interface newValidation {
    }
}