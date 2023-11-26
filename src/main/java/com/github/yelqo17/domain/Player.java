package com.github.yelqo17.domain;

import com.github.yelqo17.persistence.RolePersistence;

public class Player {
    private final int playerId;
    private final int roleId;
    private String name;
    private boolean status;
    private int votes;
    RolePersistence rolePersistence = new RolePersistence();
    public Player(int playerId, String name, int roleId, boolean status, int votes) {
        this.playerId = playerId;
        this.name = name;
        this.roleId = roleId;
        this.status = status;
        this.votes = votes;
    }
    public int getId() {
        return playerId;
    }
    public String getName() {
        return name;
    }
    public int getRoleId() {
        return roleId;
    }
    public String getRole() {
        return rolePersistence.getById(getRoleId());
    }
    public boolean getStatus() {
        return status;
    }
    public void changeStatus() {
        this.status = false;
    }
    public int getVotes() {
        return votes;
    }
    public void incrementVotes() {
        votes++;
    }
    public void resetVotes() {
        votes = 0;
    }
    public void changeName(String newName) {
         name = newName;
    }
    public void printId() {
        System.out.println("Твой номер игрока: " + getId());
    }
    public void printRole() {
        System.out.println("Твоя роль: " + getRole());
    }
    public void teammatesPrinting() {
        System.out.println("У вас нет сокомандников.");
    }
}