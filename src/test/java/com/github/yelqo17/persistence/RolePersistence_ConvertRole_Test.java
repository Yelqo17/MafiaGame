package com.github.yelqo17.persistence;

import java.util.Map;
import java.util.Objects;

public class RolePersistence_ConvertRole_Test {
    private static final RolePersistence rolePersistence = new RolePersistence();

    public static void main(String[] args) {
        Map<String, String> input = Map.of(
                "id", "1",
                "name", "Мафия"
        );
        String role = rolePersistence.convertRole(input);

        if (!Objects.equals(role, "Мафия")) {
            throw new RuntimeException(
                    "Invalid. Actual role: " + role
            );
        }
    }
}
