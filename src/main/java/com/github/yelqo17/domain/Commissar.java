package com.github.yelqo17.domain;

public class Commissar extends Player {
    public Commissar(int player_id, String player_name, int role_id, boolean status, int votes) {
        super(player_id, player_name, role_id, status, votes);
    }
    @Override
    public void printRole() {
        String commissar = rolePersistence.getById(IConsts.COMMISSAR_ID);
        System.out.println("Твоя роль: " + commissar + ". Вы можете проверить роль игрока ночью.");
    }
}