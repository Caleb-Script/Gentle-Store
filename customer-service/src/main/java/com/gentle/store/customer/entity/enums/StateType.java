package com.gentle.store.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum StateType {
    SCHLESWIG_HOLSTEIN("Schleswig-Holstein"),
    HAMBURG("Hamburg"),
    NIEDERSACHSEN("Niedersachsen"),
    BRANDENBURG("Brandenburg"),
    MECKLENBURG_VORPOMMERN("Mecklenburg-Vorpommern"),
    SAARLAND("Saarland"),
    BERLIN("Berlin"),
    BADEN_WUERTTEMBERG("Baden-Württemberg"),
    BAYERN("Bayern"),
    HESSEN("Hessen"),
    RHEINLAND_PFALZ("Rheinland-Pfalz"),
    NORDRHEIN_WESTFALEN("Nordrhein-Westfalen"),
    SACHSEN("Sachsen"),
    SACHSEN_ANHALT("Sachsen-Anhalt"),
    THUERINGEN("Thüringen"),
    BREMEN("Bremen");

    private final String  type;

    @JsonCreator
    public static StateType of(final String value) {
        return Stream.of(values())
                .filter(state -> state.type.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException(STR."Invalid EmploymentStatus: \{value}"));
    }

    @JsonValue
    public String getType() {
        return type;
    }
}

