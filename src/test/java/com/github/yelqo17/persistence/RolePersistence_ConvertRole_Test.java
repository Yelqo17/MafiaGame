package com.github.yelqo17.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

public class RolePersistence_ConvertRole_Test {
    private static final RolePersistence rolePersistence = new RolePersistence();

    @Test
    @DisplayName("convert mafia role test")
    public void convertMafiaRoleTest() {
        Map<String, String> input = Map.of(
                "id", "1",
                "name", "Мафия"
        );
        String role = rolePersistence.convertRole(input);

        then(role).isEqualTo("Мафия");
    }

    @Test
    @DisplayName("convert commissar role test")
    public void convertCommissarRoleTest() {
        Map<String, String> input = Map.of(
                "id", "2",
                "name", "Коммисар"
        );
        String role = rolePersistence.convertRole(input);

        then(role).isEqualTo("Коммисар");
    }
    @Test
    @DisplayName("convert citizen role test")
    public void convertCitizenRoleTest() {
        Map<String, String> input = Map.of(
                "id", "3",
                "name", "Мирный житель"
        );
        String role = rolePersistence.convertRole(input);

        then(role).isEqualTo("Мирный житель");
    }

}
