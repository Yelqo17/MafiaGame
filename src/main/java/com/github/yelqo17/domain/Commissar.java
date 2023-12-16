package com.github.yelqo17.domain;

public class Commissar extends Player {

    public Commissar(int playerId, String playerName, int roleId, boolean status, int votes) {
        super(playerId, playerName, roleId, status, votes);
    }

    @Override
    public void printRole() {
        String commissar = rolePersistence.getById(Consts.COMMISSAR_ID);
        System.out.println("Твоя роль: " + commissar + ". Вы можете проверить роль игрока ночью.");
    }
}