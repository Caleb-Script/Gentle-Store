package com.gentle.store.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ActivityType {

    /**Kundenanfrage, z. B. eine Anfrage zu einem Produkt oder Service.*/
    INQUIRY("Anfrage"),

    /**Kundenbeschwerde über etwas, das nicht den Erwartungen entspricht oder Probleme verursacht hat.*/
    COMPLAINT("Beschwerde"),

    /**Beratung oder Unterstützung, die der Kunde von der Bank erhalten hat.*/
    CONSULTATION("Beratung"),
    SIGN_UP("Registriert"),
    CHANGE("änderung"),

    /**Andere Arten von Kundenaktivitäten, die nicht spezifisch in den anderen Enum-Werten aufgeführt sind.*/
    OTHER("Sonstiges");

    private final String type;

    @JsonCreator
    public static ActivityType of(final String value) {
        return Stream.of(values())
                .filter(activity -> activity.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
