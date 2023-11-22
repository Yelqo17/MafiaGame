package com.github.yelqo17.domain;

public class Mafia extends Player {

    public Mafia(int player_id, String player_name, int role_id, boolean status, int votes,
                 int mafiaCount) {
        super(player_id, player_name, role_id, status, votes);
    }

    @Override
    public void printRole() {
        String mafia = rolePersistence.getById(IConsts.MAFIA_ID);
        System.out.println("Твоя роль: " + mafia);
    }
}