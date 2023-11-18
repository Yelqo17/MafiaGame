package com.github.yelqo17.domain;

import java.util.List;

public class Mafia extends Player {
    List<Player> players;
    private final int mafiaCount;

    public Mafia(int id, Role role, List<Player> players, int mafiaCount) {
        super(id, role, true, players);
        this.players = players;
        this.mafiaCount = mafiaCount;
    }

    @Override
    public void printRole() {
        System.out.println("Твоя роль: Мафия");
        if (mafiaCount > IConsts.MIN_MAFIA_COUNT) {
            System.out.print("Номера ");
            for (Player player : players) {
                if (player.role == Role.MAFIA) {
                    System.out.print(player.playerId + " ");
                }
            }
            System.out.println("мафии вместе с тобой.");
        }
    }

    @Override
    public String getCommissarCheck() {
        return "Мафия";
    }
}