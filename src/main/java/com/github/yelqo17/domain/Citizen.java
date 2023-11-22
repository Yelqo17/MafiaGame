package com.github.yelqo17.domain;

public class Citizen extends Player {
    public Citizen(int player_id, String player_name, int role_id, boolean status, int votes) {
        super(player_id, player_name, role_id, status, votes);
    }
    @Override
    public void printRole() {
        String citizen = rolePersistence.getById(IConsts.CITIZEN_ID);
        System.out.println("Твоя роль: " + citizen + ". У вас нет особой роли.");
    }
}