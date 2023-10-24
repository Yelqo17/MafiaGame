import java.util.List;

public class Citizen extends Player {
    List<Player> players;
    public Citizen(int id, Role role, List<Player> players) {
        super(id, role, true, players);
        this.players = players;
    }

    public void printRole() {
        System.out.println("Твоя роль: " + this.role + ". У вас нет особой роли.");
    }
}