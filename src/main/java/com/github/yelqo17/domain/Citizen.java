package com.github.yelqo17.domain;

public class Citizen extends Player {

    public Citizen(int playerId, String playerName, int roleId, boolean status, int votes) {
        super(playerId, playerName, roleId, status, votes);
    }

    @Override
    public void printRole() {
        String citizen = rolePersistence.getById(Consts.CITIZEN_ID);
        System.out.println("Твоя роль: " + citizen + ". У вас нет особой роли.");
    }
}