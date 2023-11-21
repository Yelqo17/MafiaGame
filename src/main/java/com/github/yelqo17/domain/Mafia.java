package com.github.yelqo17.domain;

import com.github.yelqo17.persistence.RolePersistence;

public class Mafia extends Player {
    private final int mafiaCount;
    private final RolePersistence rolePersistence = new RolePersistence();

    public Mafia(int player_id, String player_name, int role_id, boolean status, int votes,
                 int mafiaCount) {
        super(player_id, player_name, role_id, status, votes);
        this.mafiaCount = mafiaCount;
    }

    @Override
    public void printRole() {
        System.out.println("Твоя роль: Мафия");
        if (mafiaCount > IConsts.MIN_MAFIA_COUNT) {
            String mafia = rolePersistence.getById(1);
            System.out.print("Номера ");
            for (Player player : players) {
                if (player.getRole().equals(mafia)) {
                    System.out.print(player.getId() + " ");
                }
            }
            System.out.println("мафии вместе с тобой.");
        }
    }
}