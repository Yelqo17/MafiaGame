import java.util.List;

public class Mafia extends Player {
    List<Player> players;
    public Mafia(int id, Role role, List<Player> players) {
        super(id, role, true, players);
        this.players = players;
    }

    @Override
    public void printRole() {
        System.out.println("Твоя роль: " + this.role);
        System.out.print("Номера ");
        for (Player player : players) {
            if (player.role == Role.MAFIA) {
                System.out.print(player.playerId + " ");
            }
        }
        System.out.println("мафии вместе с тобой.");
    }
}