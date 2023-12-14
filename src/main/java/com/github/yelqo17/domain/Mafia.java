package com.github.yelqo17.domain;

import java.util.List;

public class Mafia extends Player {

    private final int mafiaCount;

    private final List<Player> players;

    public Mafia(int playerId, String playerName, int roleId, boolean status, int votes, int mafiaCount, List<Player> players) {
        super(playerId, playerName, roleId, status, votes);
        this.mafiaCount = mafiaCount;
        this.players = players;
    }

    @Override
    public void teammatesPrinting() {
        if (mafiaCount > Consts.MIN_MAFIA_COUNT) {
            System.out.print("Номера ");
            String mafia = rolePersistence.getById(Consts.MAFIA_ID);
            for (Player player : players) {
                if (player.getRole().equals(mafia)) {
                    System.out.print(player.getId() + " ");
                }
            }
            System.out.println("мафии вместе с тобой.");
        }
    }
}