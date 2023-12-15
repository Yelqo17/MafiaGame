package com.github.yelqo17.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class MafiaGame_RolesDistribution_Test {
    @Test
    @DisplayName("check valid players count")
    public void checkValidPlayersCount() {
        MafiaGame mafiaGame = new MafiaGame(10, "test");

        int playerCount = 10;

        mafiaGame.rolesDistribution();
        then(playerCount).isEqualTo(mafiaGame.getPlayers().size());
    }

    @Test
    @DisplayName("check invalid players count")
    public void checkInvalidPlayersCount() {
        MafiaGame mafiaGame = new MafiaGame(10, "test");

        int playerCount = 9;

        mafiaGame.rolesDistribution();
        then(playerCount).isNotEqualTo(mafiaGame.getPlayers().size());
    }

    @Test
    @DisplayName("check valid mafias count")
    public void checkValidMafiasCount() {
        MafiaGame mafiaGame = new MafiaGame(9, "test");

        int mafiaCount = 3;

        mafiaGame.rolesDistribution();
        then(mafiaCount).isEqualTo(mafiaGame.getMafias().size());
    }

    @Test
    @DisplayName("check invalid mafias count")
    public void checkInvalidMafiasCount() {
        MafiaGame mafiaGame = new MafiaGame(8, "test");

        int mafiaCount = 3;

        mafiaGame.rolesDistribution();
        then(mafiaCount).isNotEqualTo(mafiaGame.getMafias().size());
    }


    @Test
    @DisplayName("check valid citizen count")
    public void checkValidCitizenCount() {
        MafiaGame mafiaGame = new MafiaGame(10, "test");

        int playerCount = 10;
        int mafiaCount = 3;
        int citizenCount = playerCount - mafiaCount;

        mafiaGame.rolesDistribution();
        then(citizenCount).isEqualTo(mafiaGame.getPlayers().size() - mafiaGame.getMafias().size());
    }

    @Test
    @DisplayName("check invalid citizen count")
    public void checkInvalidCitizenCount() {
        MafiaGame mafiaGame = new MafiaGame(9, "test");

        int playerCount = 10;
        int mafiaCount = 3;
        int citizenCount = playerCount - mafiaCount;

        mafiaGame.rolesDistribution();
        then(citizenCount).isNotEqualTo(mafiaGame.getPlayers().size() - mafiaGame.getMafias().size());
    }
}
