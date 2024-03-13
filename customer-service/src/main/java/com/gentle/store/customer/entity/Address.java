package com.gentle.store.customer.entity;

import com.gentle.store.customer.entity.enums.StateType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

import static com.gentle.store.customer.util.Constants.*;
import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Address {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Die Straße darf nicht leer sein")
    @Pattern(message = "Ungültiges Straßenformat. Es werden Buchstaben erwartet.", regexp = GERMAN_STREET_PATTERN)
    @NotNull(message = "Straße darf nicht null sein")
    @Column(nullable = false, length = STREET_MAX_LENGTH)
    private String street;

    @Column(nullable = false, length = HOUSE_NUMBER_MAX_LENGTH)
    @NotNull(message = "Hausnummer darf nicht null sein")
    @NotBlank(message = "Die Hausnummer darf nicht leer sein")
    private String houseNumber;

    @Column(nullable = false, length = ZIP_CODE_MAX_LENGTH)
    @NotBlank(message = "Die Postleitzahl darf nicht leer sein")
    @NotNull(message = "Die Postleitzahl darf nicht null sein")
    @Pattern(message = "Ungültiges Postleitzahlenformat. Es werden 5 Ziffern erwartet.", regexp = ZIP_CODE_PATTERN)
    private String zipCode;

    @NotNull(message = "Das Bundesland darf nicht null sein")
    @Enumerated(STRING)
    @Column(nullable = false, length = STATE_MAX_LENGTH)
    private StateType state;

    @Column(nullable = false, length = CITY_MAX_LENGTH)
    @NotBlank(message = "Die Stadt darf nicht leer sein")
    private String city;

}