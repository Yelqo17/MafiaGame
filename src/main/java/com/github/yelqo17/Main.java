package com.github.yelqo17;

import com.github.yelqo17.domain.MafiaGame;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в игру Мафия!");
        Scanner s = new Scanner(System.in);
        System.out.print("Введите имя игрока: ");
        String player_name = s.nextLine();
        System.out.println("Введите количество игроков от 4 до 10: ");
        int numberOfPlayers = s.nextInt();
        MafiaGame mafiaGame = new MafiaGame(numberOfPlayers, player_name);

        mafiaGame.startGame();
    }
}