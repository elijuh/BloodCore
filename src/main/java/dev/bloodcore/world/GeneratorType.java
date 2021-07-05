package dev.bloodcore.world;

import lombok.Getter;

@Getter
public enum GeneratorType {

    NORMAL("Normal World"),
    VOID("Void World");


    private final String name;
    GeneratorType(String s) {
        this.name = s;
    }

}
