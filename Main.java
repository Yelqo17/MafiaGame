import java.util.Scanner;

public class Main {
    static int m;

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в игру Мафия! Выберите режим игры: режим игры с ПК - 1");
        System.out.print("режим игры с другими игроками - 0: ");

        Scanner s = new Scanner(System.in);

        while (true) {
            if (s.hasNextInt()) {
                m = s.nextInt();
                if (m == 1 || m == 0) {
                    break;
                }
            }
            System.out.println("Введите 1 или 0");
            s.nextLine();
        }
        boolean gameMode = m == 1;

        if (gameMode) {
            System.out.println("Режим игры с ПК");
            System.out.println("Введите количество игроков от 8 до 12: ");
            int playersCount = s.nextInt();
            new MafiaGame(playersCount);
        } else {
            System.out.println("Режим игры с другими игроками");
            // todo
        }
    }
}
