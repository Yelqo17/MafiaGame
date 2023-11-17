package main.java;

import java.util.List;
public class Player {
    int playerId;
    Role role;
    boolean isAlive;
    public int votes;
    List<Player> players;
    Player(int playerId, Role role, boolean isAlive, List<Player> players) {
        this.playerId = playerId;
        this.role = role;
        this.isAlive = isAlive;
        this.players = players;
        this.votes = 0;
    }
    public boolean getStatus() {
        return isAlive;
    }

    public Role getRole() {
        return role;
    }
    public void printId() {
        System.out.println("Твой номер игрока: " + this.playerId);
    }
    public void printRole() {
        System.out.println("Твоя роль: " + this.role);
    }
    public String getCommissarCheck() {
        return "Player";
    }
}