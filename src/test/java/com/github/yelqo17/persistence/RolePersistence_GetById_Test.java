package com.github.yelqo17.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class RolePersistence_GetById_Test {
    private final RolePersistence rolePersistence = new RolePersistence();

    @Test
    @DisplayName("when role isn't exists in DB then return null")
    public void whenRoleIsNotExistsInDbThenReturnNull() {
        String role = rolePersistence.getById(-1);

        then(role).isNull();
    }

    @Test
    @DisplayName("when role exists in DB then return null")
    public void whenRoleExistsInDbThenReturnNull() {
        rolePersistence.createRole("Мафия");
        String role = rolePersistence.getById(1);

        then(role).isNotNull();
    }
}
