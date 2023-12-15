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
    private final int citizenCount;
    private final String userName;
    private int userId;

    private final RolePersistence rolePersistence = new RolePersistence();
    private final MyDataBase db = MyDataBase.getInstance();

    public MafiaGame(int numberOfPlayers, String playerName) {

        playerCount = numberOfPlayers;
        userName = playerName;

        if (playerCount >= Consts.MIN_PLAYERS && playerCount <= Consts.MIN_PLAYERS + 1) {
            mafiaCount = Consts.MIN_MAFIA_COUNT;
        } else if (playerCount >= Consts.MIDDLE_PLAYERS - 1 && playerCount <= Consts.MIDDLE_PLAYERS + 1) {
            mafiaCount = Consts.MAX_MAFIA_COUNT - 1;
        } else if (playerCount >= Consts.MIDDLE_PLAYERS + 1 && playerCount <= Consts.MAX_PLAYERS) {
            mafiaCount = Consts.MAX_MAFIA_COUNT;
        } else {
            throw new IllegalArgumentException("Неверное количество игроков!");
        }

        citizenCount = playerCount - mafiaCount - 1;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getMafias() {
        return mafias;
    }

    public void startGame() {
        startMessagePrinting();

        userId = ThreadLocalRandom.current().nextInt(1, playerCount + 1);

        rolesCreation();

        rolesDistribution();

        printRole();

        displayAlivePlayers();

        nightPhase();
    }

    private void startMessagePrinting(){
        if (mafiaCount == Consts.MIN_MAFIA_COUNT) {
            System.out.println("В игре " + playerCount + " игроков" + " и "+ mafiaCount + " мафия.");
        } else {
            System.out.println("В игре " + playerCount + " игроков" + " и "+ mafiaCount + " мафий.");
        }
    }

    private void rolesCreation() {
        rolePersistence.createRole("Мафия");
        rolePersistence.createRole("Комиссар");
        rolePersistence.createRole("Мирный житель");
    }

    public void rolesDistribution() {
        eliminatedIds = new ArrayList<>();

        players = new ArrayList<>();

        mafias = new ArrayList<>();

        boolean isThereCommissar = true;
        int count = 0;
        for (int i = 0; i < playerCount; i++) {
            int roleId = ThreadLocalRandom.current().nextInt(1, Consts.ROLE_ID_BOUND + 1);
            String playerName = "Игрок " + (i + 1);
            boolean singleCicleCondition;
            do {
                if (roleId == Consts.MAFIA_ID && mafias.size() < mafiaCount) {
                    Player mafia = new Mafia(i + 1, playerName, roleId, true, 0, mafiaCount, players);
                    players.add(mafia);
                    mafias.add(mafia);
                    singleCicleCondition = false;
                } else if (roleId == Consts.COMMISSAR_ID && isThereCommissar) {
                    Player commissar = new Commissar(i + 1, playerName, roleId, true, 0);
                    players.add(commissar);
                    isThereCommissar = false;
                    singleCicleCondition = false;
                } else if (roleId == Consts.CITIZEN_ID && count < citizenCount) {
                    Player citizen = new Citizen(i + 1, playerName, roleId, true, 0);
                    count++;
                    players.add(citizen);
                    singleCicleCondition = false;
                } else {
                    roleId = ThreadLocalRandom.current().nextInt(1, Consts.ROLE_ID_BOUND + 1);
                    singleCicleCondition = true;
                }
            } while (singleCicleCondition);
        }
    }

    private void printRole(){
        Player currentPlayer = getCurrentPlayer();

        currentPlayer.changeName(userName);

        currentPlayer.printRole();

        String mafia = getRoleFromDb(Consts.MAFIA_ID);
        if (currentPlayer.getRole().equals(mafia)) {
            currentPlayer.teammatesPrinting();
        }

        currentPlayer.printId();
    }

    private Player getCurrentPlayer() {
        return players.get(userId - 1);
    }

    private String getRoleFromDb(int id) {
        return rolePersistence.getById(id);
    }

    private void displayAlivePlayers() {
        System.out.print("В живых осталось " + getCountAlivePlayers() + " игроков. ");
        for (Player player : players) {
            if (player.getStatus()) {
                printColoredPlayerName(player);
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
            case Consts.PLAYER_ID_ONE:
                return Consts.ANSI_RED;
            case Consts.PLAYER_ID_TWO:
                return Consts.ANSI_GREEN;
            case Consts.PLAYER_ID_THREE:
                return Consts.ANSI_CYAN;
            case Consts.PLAYER_ID_FOUR:
                return Consts.ANSI_YELLOW;
            case Consts.PLAYER_ID_FIVE:
                return Consts.ANSI_BLUE;
            case Consts.PLAYER_ID_SIX:
                return Consts.ANSI_MAGENTA;
            case Consts.PLAYER_ID_SEVEN:
                return Consts.ANSI_BLACK;
            case Consts.PLAYER_ID_EIGHT:
                return Consts.ANSI_PINK;
            case Consts.PLAYER_ID_NINE:
                return Consts.ANSI_GRAY;
            case Consts.PLAYER_ID_TEN:
                return Consts.ANSI_BRIGHT_BLACK;
            default:
                return Consts.ANSI_RESET;
        }
    }

    private void printColoredPlayerName (Player player) {
        String colorCode = getPlayerColor(player);
        System.out.print(player.getName() + " " + colorCode + "●" + Consts.ANSI_RESET + " ");
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
                String mafia = getRoleFromDb(Consts.MAFIA_ID);
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
                        String commissar = getRoleFromDb(Consts.COMMISSAR_ID);
                        if (currentPlayer.getRole().equals(commissar) && currentPlayer.getStatus()) {
                            System.out.println("Комиссар, выбери номер игрока, которого хочешь проверить.");
                            displayEstimatedTime();
                            int targetId = scanner.nextInt();
                            Player target = getPlayerById(targetId);
                            if (target != null) {
                                printColoredPlayerName(target);
                                System.out.println("- " + target.getRole());
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
                        timer.schedule(commissarChoiceTask, Consts.TEST_TIME_IN_MILLISECONDS);
                    }
                };
                timer.schedule(mafiaDiscussionTask, Consts.TEST_TIME_IN_MILLISECONDS);
            }
        };
        timer.schedule(sleepTask, Consts.TEST_TIME_IN_MILLISECONDS);
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

    private void nighKillPrinting(Player newVictim) {
        System.out.println("Утро наступило.");
        if (newVictim == null) {
            System.out.println("Никто не был убит ночью.");
        } else if (!newVictim.getStatus()) {
            printColoredPlayerName(newVictim);
            System.out.print("был убит ночью. ");
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
                                        int targetId = getProgramVotingChoice(eliminatedIds, player.getId());
                                        Player target = getPlayerById(targetId);
                                        if (target != null && target.getStatus()) {
                                            target.incrementVotes();
                                            System.out.println(player.getName() + " проголосовал за " +
                                                    target.getName());
                                        }
                                    }
                                }

                                if (currentPlayer.getStatus()) {
                                    System.out.println(currentPlayer.getName() +
                                            ", введите номер игрока, за которого вы голосуете:");
                                    Scanner scanner = new Scanner(System.in);
                                    int targetId = scanner.nextInt();
                                    Player target = getPlayerById(targetId);
                                    if (target != null && target.getStatus()) {
                                        target.incrementVotes();
                                        System.out.println(currentPlayer.getName() + " проголосовал за " +
                                                target.getName());
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
                        timer.schedule(votingTask, Consts.TEST_TIME_IN_MILLISECONDS);
                    }
                };
                timer.schedule(discussionTask, Consts.TEST_TIME_IN_MILLISECONDS);
            }
        };
        timer.schedule(afterNightTask, Consts.TEST_TIME_IN_MILLISECONDS);
    }

    private int getProgramVotingChoice(List<Integer> eliminatedIds, int id) {
        int random;
        do {
            random = ThreadLocalRandom.current().nextInt(1, players.size() + 1);
        } while (eliminatedIds.contains(random) || random == id);

        return random;
    }

    private void displayEliminatedPlayer() {
        Player eliminatedPlayer = determineEliminatedPlayer();
        if (eliminatedPlayer != null) {
            printColoredPlayerName(eliminatedPlayer);
            System.out.println("был исключен из игры. ");
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
                String mafia = getRoleFromDb(Consts.MAFIA_ID);
                String commissar = getRoleFromDb(Consts.COMMISSAR_ID);
                String citizen = getRoleFromDb(Consts.CITIZEN_ID);
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

    private String determineWinner() {

        int mafiaAlive = 0;
        int citizensAlive = 0;

        for (Player player : players) {
            if (player.getStatus()) {
                String mafia = getRoleFromDb(Consts.MAFIA_ID);
                String commissar = getRoleFromDb(Consts.COMMISSAR_ID);
                String citizen = getRoleFromDb(Consts.CITIZEN_ID);
                if (player.getRole().equals(mafia)) {
                    mafiaAlive++;
                } else if (player.getRole().equals(citizen) || player.getRole().equals(commissar)) {
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
            String mafia = getRoleFromDb(Consts.MAFIA_ID);
            if (player.getRole().equals(mafia)) {
                printColoredPlayerName(player);
                System.out.print(" ");
            }
        }
        if (mafiaCount == Consts.MIN_MAFIA_COUNT) {
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
            s.nextLine();
        }
        db.deleteRoleTable();
        if (choice == 0) {
            System.exit(0);
        }
        startGame();
    }
}