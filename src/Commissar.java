import java.util.List;

public class Commissar extends Player {
    List<Player> players;
    public Commissar(int id, Role role, List<Player> players) {
        super(id, role, true, players);
        this.players = players;
    }

    public void printRole() {
        System.out.println("Твоя роль: Комиссар. Вы можете проверить роль игрока ночью.");
    }
}