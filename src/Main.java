import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в игру Мафия!");
        Scanner s = new Scanner(System.in);

        System.out.println("Введите количество игроков от 8 до 12: ");
        int numberOfPlayers = s.nextInt();
        MafiaGame mafiaGame = new MafiaGame(numberOfPlayers);

        mafiaGame.startGame();
    }
}
