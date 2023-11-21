package com.github.yelqo17.domain;


import com.github.yelqo17.persistence.RolePersistence;

import java.util.List;
public class Player {
    private final int player_id;
    private final String player_name;
    private final int role_id;
    private boolean status;
    private int votes;
    private final RolePersistence rolePersistence = new RolePersistence();
    List<Player> players;
    public Player(int player_id, String player_name, int role_id, boolean status, int votes) {
        this.player_id = player_id;
        this.player_name = player_name;
        this.role_id = role_id;
        this.status = status;
        this.votes = votes;
    }
    public int getId() {
        return player_id;
    }
    public String getName() {
        return player_name;
    }
    public int getRoleId() {
        return role_id;
    }
    public String getRole() {
        return rolePersistence.getById(getRoleId());
    }

    public boolean getStatus() {
        return status;
    }
    public void changeStatus() {
        status = false;
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
    public void printId() {
        System.out.println("Твой номер игрока: " + getId());
    }
    public void printRole() {
        System.out.println("Твоя роль: " + this.role_id);
    }
}