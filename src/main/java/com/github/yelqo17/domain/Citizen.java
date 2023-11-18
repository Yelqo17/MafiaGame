package com.github.yelqo17.domain;

import java.util.List;

public class Citizen extends Player {
    List<Player> players;
    public Citizen(int id, Role role, List<Player> players) {
        super(id, role, true, players);
        this.players = players;
    }
    @Override
    public void printRole() {
        System.out.println("Твоя роль: Мирный житель. У вас нет особой роли.");
    }
    @Override
    public String getCommissarCheck() {
        return "Мирный житель";
    }
}