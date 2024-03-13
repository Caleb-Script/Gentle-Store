package com.gentle.store.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name = "phone_number_list")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PhoneNumber {
    public static final String DIALING_CODE_PATTERN = "(((([+][0-9]{1,3})|0)[0-9]{3,})|(\\d){4})";
    public static final String PHONE_NUMBER_PATTERN = "\\d{8,}";
    private static final int PHONE_NUMBER_MIN_LENGTH = 8;


    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Vorwahl darf nicht leer sein")
    @NotNull(message = "Vorwahl darf nicht null sein.")
    @Column(nullable = false, length = 6)
    @Pattern(regexp = DIALING_CODE_PATTERN,message = "keine gültige Vorwahl!")
    private String dialingCode;

    @Pattern(regexp = PHONE_NUMBER_PATTERN,message = "ungültige Nummer!")
    @Size(message = "Die Telefonnummer muss mindestens 8 Ziffern beinhalten!", min = PHONE_NUMBER_MIN_LENGTH)
    @NotBlank(message = "Telefonnummer darf nicht leer sein!")
    @NotNull(message = "Telefonnummer darf nicht null sein!")
    @Column(nullable = false)
    private String number;

//    @NotNull
    @Column(nullable = false)
    private Boolean isDefaultPhoneNumber;
}
