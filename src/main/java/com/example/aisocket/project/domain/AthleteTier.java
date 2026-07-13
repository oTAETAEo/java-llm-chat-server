package com.example.aisocket.project.domain;

import lombok.Getter;

@Getter
public enum AthleteTier {

    PRO("PRO"),
    AMATEUR("AMATEUR");

    private final String value;

    AthleteTier(String value) {
        this.value = value;
    }

}
