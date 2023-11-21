package com.github.yelqo17.domain;

import com.github.yelqo17.database.MyDataBase;
import com.github.yelqo17.persistence.RolePersistence;

import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;

public class MafiaGame {
    private List<Player> players;
    private List<Integer> eliminatedIds;
    private List<Player> mafias;
    private final int playerCount;
    private final int mafiaCount;
    private int currentUserId;
    private final RolePersistence rolePersistence = new RolePersistence();
    private final MyDataBase db = MyDataBase.getInstance();
    public MafiaGame(int numberOfPlayers) {
        playerCount = numberOfPlayers;

        if (playerCount >= IConsts.MIN_PLAYERS && playerCount <= IConsts.MIN_PLAYERS + 1) {
            mafiaCount = IConsts.MIN_MAFIA_COUNT;
        } else if (playerCount >= IConsts.MIDDLE_PLAYERS - 1 && playerCount <= IConsts.MIDDLE_PLAYERS + 1) {
            mafiaCount = IConsts.MAX_MAFIA_COUNT - 1;
        } else if (playerCount >= IConsts.MIDDLE_PLAYERS + 1 && playerCount <= IConsts.MAX_PLAYERS) {
            mafiaCount = IConsts.MAX_MAFIA_COUNT;
        } else {
            throw new IllegalArgumentException("Неверное количество игроков!");
        }
    }
    public void startGame() {
        currentUserId = ThreadLocalRandom.current().nextInt(1, playerCount + 1);

        startMessagePrinting();

        players = new ArrayList<>();

        eliminatedIds = new ArrayList<>();

        mafias = new ArrayList<>();

        rolesCreation();

        rolesDistribution();

        displayAlivePlayers();

        nightPhase();
    }
    private void startMessagePrinting(){
        if (mafiaCount == IConsts.MIN_MAFIA_COUNT) {
            System.out.println("В игре " + playerCount + " игроков" + " и "+ mafiaCount + " мафия.");
        } else {
            System.out.println("В игре " + playerCount + " игроков" + " и "+ mafiaCount + " мафий.");
        }
    }
    private void rolesCreation() {
        rolePersistence.createRole("Мафия");
        rolePersistence.createRole("Коммисар");
        rolePersistence.createRole("Мирный житель");
    }
    private void rolesDistribution() {
        for (int i = 0; i < mafiaCount; i++) {
            String mafiaName = "Игрок " + (i + 1);
            Player mafia = new Mafia(i + 1, mafiaName, 1, true, 0, mafiaCount);
            players.add(mafia);
            mafias.add(mafia);
        }

        String commissarName = "Игрок " + (mafiaCount + 1);
        Player commissar = new Commissar(mafiaCount + 1, commissarName, 2, true, 0);
        players.add(commissar);

        for (int i = mafiaCount + 1; i < playerCount; i++) {
            String playerName = "Игрок " + (i + 1);
            Player citizen = new Citizen(i + 1, playerName, 3, true, 0);
            players.add(citizen);
        }

        printRole();
    }
    private void printRole(){
        Player currentPlayer = getCurrentPlayer();

        currentPlayer.printRole();
        currentPlayer.printId();
    }
    private Player getCurrentPlayer() {
        return players.get(currentUserId - 1);
    }
    private void displayAlivePlayers() {
        System.out.print("В живых осталось " + getCountAlivePlayers() + " игроков. ");
        for (Player player : players) {
            if (player.getStatus()) {
                String colorCode = getPlayerColor(player);
                System.out.print("Игрок " + player.getId() + " " + colorCode + "●" + IConsts.ANSI_RESET + " ");
            }
        }
        System.out.println("живы");
    }
    private int getCountAlivePlayers() {
        int countAlivePlayers = 0;
        for (Player player : players) {
            if(player.getStatus())
                countAlivePlayers++;
        }
        return countAlivePlayers;
    }

    private String getPlayerColor(Player player) {
        switch (player.getId()) {
            case IConsts.PLAYER_ID_ONE:
                return IConsts.ANSI_RED;
            case IConsts.PLAYER_ID_TWO:
                return IConsts.ANSI_GREEN;
            case IConsts.PLAYER_ID_THREE:
                return IConsts.ANSI_CYAN;
            case IConsts.PLAYER_ID_FOUR:
                return IConsts.ANSI_YELLOW;
            case IConsts.PLAYER_ID_FIVE:
                return IConsts.ANSI_BLUE;
            case IConsts.PLAYER_ID_SIX:
                return IConsts.ANSI_MAGENTA;
            case IConsts.PLAYER_ID_SEVEN:
                return IConsts.ANSI_BLACK;
            case IConsts.PLAYER_ID_EIGHT:
                return IConsts.ANSI_PINK;
            case IConsts.PLAYER_ID_NINE:
                return IConsts.ANSI_GRAY;
            case IConsts.PLAYER_ID_TEN:
                return IConsts.ANSI_BRIGHT_BLACK;
            default:
                return IConsts.ANSI_RESET;
        }
    }
    private void nightPhase() {
        Scanner scanner = new Scanner(System.in);
        Player currentPlayer = getCurrentPlayer();
        System.out.println("-----------------");
        System.out.println("Все игроки засыпают. Мафия и комиссар делают ход...");

        Timer timer = new Timer();

        TimerTask sleepTask = new TimerTask() {
            @Override
            public void run() {
                Player victim;
                String mafia = rolePersistence.getById(1);
                if (currentPlayer.getRole().equals(mafia) && currentPlayer.getStatus()) {
                    System.out.println("Мафия, выберите, кого хотите убить этой ночью.");
                    displayEstimatedTime();
                    int targetId = scanner.nextInt();
                    victim = getPlayerById(targetId);
                    if (victim != null && victim.getStatus()) {
                        System.out.println("Ваш выбор принят.");
                        eliminatedIds.add(targetId);
                        removePlayer(targetId);
                    }
                } else {
                    int targetId = getProgramKillingChoice(mafias, eliminatedIds);
                    victim = getPlayerById(targetId);
                    eliminatedIds.add(targetId);
                    removePlayer(targetId);
                }

                Player finalVictim = victim;
                TimerTask mafiaDiscussionTask = new TimerTask() {
                    @Override
                    public void run() {
                        String commissar = rolePersistence.getById(2);
                        if (currentPlayer.getRole().equals(commissar) && currentPlayer.getStatus()) {
                            System.out.println("Комиссар, выбери номер игрока, которого хочешь проверить.");
                            displayEstimatedTime();
                            int targetId = scanner.nextInt();
                            Player target = getPlayerById(targetId);
                            if (target != null) {
                                String colorCode = getPlayerColor(target);
                                System.out.println("Роль игрока " + target.getId() + " " + colorCode + "●" + IConsts.ANSI_RESET + " - " + target.getRole());
                            }
                        }

                        TimerTask commissarChoiceTask = new TimerTask() {
                            @Override
                            public void run() {
                                System.out.println("Ночь закончена. Переход к дневной фазе.");
                                System.out.println("-----------------");

                                nighKillPrinting(finalVictim);
                                dayPhase();
                            }
                        };
                        timer.schedule(commissarChoiceTask, IConsts.TEST_TIME_IN_MILLISECONDS);
                    }
                };
                timer.schedule(mafiaDiscussionTask, IConsts.TEST_TIME_IN_MILLISECONDS);
            }
        };
        timer.schedule(sleepTask, IConsts.TEST_TIME_IN_MILLISECONDS);
    }
    private void displayEstimatedTime() {
        System.out.println("У вас есть 5 секунд.");
    }
    private Player getPlayerById(int id) {
        for (Player player : players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }
    private void removePlayer(int targetId){
        for (Player player : players) {
            if (player.getId() == targetId) {
                player.changeStatus();
            }
        }
    }
    private int getProgramKillingChoice(List<Player> mafias, List<Integer> eliminatedIds) {
        int random;
        Player player;
        do {
            random = ThreadLocalRandom.current().nextInt(1, players.size() + 1);
            player = getPlayerById(random);
        } while (eliminatedIds.contains(random) || mafias.contains(player));

        return random;
    }
    private int getProgramVotingChoice(List<Integer> eliminatedIds) {
        int random;
        do {
            random = ThreadLocalRandom.current().nextInt(1, players.size() + 1);
        } while (eliminatedIds.contains(random));

        return random;
    }

    private void nighKillPrinting(Player newVictim) {
        System.out.println("Утро наступило.");
        if (newVictim == null) {
            System.out.println("Никто не был убит ночью.");
        } else if (!newVictim.getStatus()) {
            String colorCode = getPlayerColor(newVictim);
            System.out.print("Игрок " + newVictim.getId() + " " + colorCode + "●" + IConsts.ANSI_RESET + " был убит ночью. ");
        }
    }
    private void dayPhase() {

        displayAlivePlayers();

        Timer timer = new Timer();
        TimerTask afterNightTask = new TimerTask() {
            @Override
            public void run() {
                TimerTask discussionTask = new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Игроки, обсудите события и попытайтесь убедить других.");
                        displayEstimatedTime();
                        TimerTask votingTask = new TimerTask() {
                            @Override
                            public void run() {
                                System.out.println("Игроки, теперь голосуйте за игрока, которого вы подозреваете.");
                                displayEstimatedTime();

                                Player currentPlayer = getCurrentPlayer();
                                int currentPlayerId = currentPlayer.getId();

                                for (Player player : players) {
                                    if (player.getId() != currentPlayerId && player.getStatus()) {
                                        int targetId = getProgramVotingChoice(eliminatedIds);
                                        Player target = getPlayerById(targetId);
                                        if (target != null && target.getStatus()) {
                                            target.incrementVotes();
                                            System.out.println("Игрок " + player.getId() + " проголосовал за " + targetId);
                                        }
                                    }
                                }

                                if (currentPlayer.getStatus()) {
                                    System.out.println("Игрок " + currentPlayerId + ", введите номер игрока, за которого вы голосуете:");
                                    Scanner scanner = new Scanner(System.in);
                                    int targetId = scanner.nextInt();
                                    Player target = getPlayerById(targetId);
                                    if (target != null && target.getStatus()) {
                                        target.incrementVotes();
                                        System.out.println("Игрок " + currentPlayerId + " проголосовал за " + targetId);
                                    }
                                }

                                displayEliminatedPlayer();

                                resetPlayersVotes();

                                if (checkForWinner()) {
                                    System.out.println("Игра окончена.");
                                    if (determineWinner().equals("Mafia")) {
                                        System.out.println("Победила мафия!");
                                    } else if (determineWinner().equals("Civilians")) {
                                        System.out.println("Победили мирные жители!");
                                    }
                                    endMessagePrinting();
                                    newGameProposal();
                                } else {
                                    nightPhase();
                                }
                            }
                        };
                        timer.schedule(votingTask, IConsts.TEST_TIME_IN_MILLISECONDS);
                    }
                };
                timer.schedule(discussionTask, IConsts.TEST_TIME_IN_MILLISECONDS);
            }
        };
        timer.schedule(afterNightTask, IConsts.TEST_TIME_IN_MILLISECONDS);
    }
    private void displayEliminatedPlayer() {
        Player eliminatedPlayer = determineEliminatedPlayer();
        if (eliminatedPlayer != null) {
            String colorCode = getPlayerColor(eliminatedPlayer);
            System.out.println("Игрок " + eliminatedPlayer.getId() + " " + colorCode + "●" + IConsts.ANSI_RESET + " был исключен из игры. ");
            eliminatedIds.add(eliminatedPlayer.getId());
            eliminatedPlayer.changeStatus();
        } else {
            System.out.println("Игрокам не удалось договориться и никто не исключен.");
        }
    }
    private Player determineEliminatedPlayer() {
        Player eliminatedPlayer = null;
        int maxVotes = 0;

        for (Player player : players) {
            if (player.getVotes() > maxVotes && player.getStatus()) {
                maxVotes = player.getVotes();
                eliminatedPlayer = player;
            }
        }

        int countMaxVotes = 0;

        for (Player player : players) {
            if (player.getVotes() == maxVotes && player.getStatus()) {
                countMaxVotes++;
            }
        }

        if (countMaxVotes > 1) {
            return null;
        }

        return eliminatedPlayer;
    }
    private void resetPlayersVotes() {
        for (Player player : players) {
            if (player.getVotes() != 0 && player.getStatus()) {
                player.resetVotes();
            }
        }
    }
    private boolean checkForWinner() {
        int mafiaAlive = 0;
        int citizensAlive = 0;

        for (Player player : players) {
            if (player.getStatus()) {
                String mafia = rolePersistence.getById(1);
                String commissar = rolePersistence.getById(2);
                String citizen = rolePersistence.getById(3);
                if (player.getRole().equals(mafia)) {
                    mafiaAlive++;
                } else if (player.getRole().equals(citizen) || player.getRole().equals(commissar)) {
                    citizensAlive++;
                }
            }
        }

        if (mafiaAlive >= citizensAlive) {
            return true;
        }

        return mafiaAlive == 0;
    }
    private String determineWinner(){
        int mafiaAlive = 0;
        int citizensAlive = 0;

        for (Player player : players) {
            if (player.getStatus()) {
                String mafia = rolePersistence.getById(1);
                String citizen = rolePersistence.getById(3);
                if (player.getRole().equals(mafia)) {
                    mafiaAlive++;
                } else if (player.getRole().equals(citizen)) {
                    citizensAlive++;
                }
            }
        }

        if (mafiaAlive >= citizensAlive) {
            return "Mafia";
        } else if (mafiaAlive == 0) {
            return "Civilians";
        }
        else {
            return "Error";
        }
    }
    private void endMessagePrinting() {
        for (Player player : players) {
            String mafia = rolePersistence.getById(1);
            String colorCode = getPlayerColor(player);
            if(player.getRole().equals(mafia)) {
                System.out.print("Игрок " + player.getId() + " " + colorCode + "●" + IConsts.ANSI_RESET + " ");
            }
        }
        if (mafiaCount == IConsts.MIN_MAFIA_COUNT) {
            System.out.println("был мафией!");
        }
        else {
            System.out.println("были мафиями!");
        }
    }

    private void newGameProposal() {
        Scanner s = new Scanner(System.in);
        System.out.println("Введите 1, если хотите начать игру заново.");
        System.out.println("Введите 0, если хотите выйти из игры.");
        int choice;
        while (true) {
            if (s.hasNextInt()) {
                choice = s.nextInt();
                if (choice == 1 || choice == 0) {
                    break;
                }
            }
            System.out.println("Введите 1, если хотите начать игру заново.");
            System.out.println("Введите 0, если хотите выйти из игры.");
            s.nextLine();
        }
        db.deleteDataRoleTable();
        if (choice == 0) {
            System.exit(0);
        }

        startGame();
    }
}