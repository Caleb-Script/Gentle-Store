package com.gentle.store.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum CustomerStatusType {
    /** Aktiver Kundenstatus.
     *  Dieser Status zeigt an, dass der Kunde ein aktives Konto oder eine aktive Beziehung zur Bank hat.
     *  Ein aktiver Kunde kann Transaktionen durchführen und auf die angebotenen Dienstleistungen zugreifen. */
    ACTIVE("Aktiv"),

    /** Inaktiver Kundenstatus.
     *  Dieser Status zeigt an, dass das Konto des Kunden vorübergehend inaktiv ist.
     *  Dies kann beispielsweise der Fall sein, wenn der Kunde seit einiger Zeit keine Transaktionen durchgeführt hat oder das Konto vorübergehend gesperrt wurde. */
    INACTIVE("Inaktiv"),

    /** Blockierter Kundenstatus.
     *  Dieser Status zeigt an, dass das Konto des Kunden blockiert wurde, möglicherweise aufgrund von Sicherheitsbedenken oder auf Wunsch des Kunden selbst. */
    BLOCKED("Blockiert"),

    /** Geschlossener Kundenstatus.
     *  Dieser Status zeigt an, dass das Konto des Kunden geschlossen wurde, entweder auf Wunsch des Kunden oder aufgrund einer Entscheidung der Bank, das Konto zu schließen. */
    CLOSED("Geschlossen");

    private final String  type;

    @JsonCreator
    public static CustomerStatusType of(final String value) {
        return Stream.of(values())
                .filter(contactOptions -> contactOptions.type.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException(STR."Invalid EmploymentStatus: \{value}"));
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
