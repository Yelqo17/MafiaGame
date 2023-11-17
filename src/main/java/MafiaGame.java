package main.java;

import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;

public class MafiaGame {
    private List<Player> players;
    private List<Integer> eliminatedIds;
    private static int playerCount;
    private static int mafiaCount;
    private final int citizenCount;
    private final int currentUserId;
    public MafiaGame(int numberOfPlayers) {
        playerCount = numberOfPlayers;

        currentUserId = ThreadLocalRandom.current().nextInt(1, playerCount + 1) ;

        if (playerCount >= IConsts.MIN_PLAYERS && playerCount <= IConsts.MIN_PLAYERS + 1) {
            mafiaCount = IConsts.MIN_MAFIA_COUNT;
            citizenCount = playerCount - mafiaCount - IConsts.COMMISSAR_COUNT;
        } else if (playerCount >= IConsts.MIDDLE_PLAYERS - 1 && playerCount <= IConsts.MIDDLE_PLAYERS + 1) {
            mafiaCount = IConsts.MAX_MAFIA_COUNT - 1;
            citizenCount = playerCount - mafiaCount - IConsts.COMMISSAR_COUNT;
        } else if (playerCount >= IConsts.MIDDLE_PLAYERS + 1 && playerCount <= IConsts.MAX_PLAYERS) {
            mafiaCount = IConsts.MAX_MAFIA_COUNT;
            citizenCount = playerCount - mafiaCount - IConsts.COMMISSAR_COUNT;
        } else {
            throw new IllegalArgumentException("Неверное количество игроков!");
        }
    }
    public void startGame() {
        startMessagePrinting();

        players = new ArrayList<>();

        eliminatedIds = new ArrayList<>();

        rolesDistribution();

        displayAlivePlayers();

        nightPhase();
    }
    private void startMessagePrinting(){
        if (mafiaCount == IConsts.MIN_MAFIA_COUNT) {
            System.out.println("В игре " + playerCount + " игроков" + " и "+ MafiaGame.mafiaCount + " мафия.");
        } else {
            System.out.println("В игре " + playerCount + " игроков" + " и "+ MafiaGame.mafiaCount + " мафий.");
        }
    }
    private void rolesDistribution() {
        for (int i = 0; i < mafiaCount; i++) {
            Player mafia = new Mafia(i + 1, Role.MAFIA, players, mafiaCount);
            players.add(mafia);
        }

        Player commissar = new Commissar(mafiaCount + 1, Role.COMMISSAR, players);
        players.add(commissar);

        for (int i = mafiaCount + 1; i < playerCount; i++) {
            Player citizen = new Citizen(i + 1,  Role.CITIZEN, players);
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
            if(player.getStatus())
                System.out.print("Игрок " + player.playerId + " ");
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
                if (currentPlayer.role == Role.MAFIA && currentPlayer.isAlive) {
                    System.out.println("Мафия, выберите, кого хотите убить этой ночью.");
                    displayEstimatedTime();
                    int targetId = scanner.nextInt();
                    victim = getPlayerById(targetId);
                    if (victim != null && victim.isAlive) {
                        System.out.println("Ваш выбор принят.");
                        eliminatedIds.add(targetId);
                        removePlayer(targetId);
                    }
                } else {
                    int targetId = getProgramChoice(eliminatedIds);
                    victim = getPlayerById(targetId);
                    eliminatedIds.add(targetId);
                    removePlayer(targetId);
                }

                Player finalVictim = victim;
                TimerTask mafiaDiscussionTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (currentPlayer.role == Role.COMMISSAR && currentPlayer.isAlive) {
                            System.out.println("Комиссар, выбери номер игрока, которого хочешь проверить.");
                            displayEstimatedTime();
                            int targetId = scanner.nextInt();
                            Player target = getPlayerById(targetId);
                            if (target != null) {
                                System.out.println("Роль игрока " + target.playerId + " - " + target.getCommissarCheck());
                            }
                        } else {
                            // todo
                        }

                        TimerTask commissarChoiceTask = new TimerTask() {
                            @Override
                            public void run() {
                                System.out.println("Ночь закончена. Переход к дневной фазе.");
                                System.out.println("-----------------");

                                dayPhase(finalVictim);
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
        System.out.println("У вас есть 10 секунд.");
    }
    private Player getPlayerById(int id) {
        for (Player player : players) {
            if (player.playerId == id) {
                return player;
            }
        }
        return null;
    }
    private void removePlayer(int targetId){
        for (Player player : players) {
            if (player.playerId == targetId) {
                player.isAlive = false;
            }
        }
    }
    private int getProgramChoice(List<Integer> eliminatedIds) {
        int random;
        do {
            random = ThreadLocalRandom.current().nextInt(1, players.size() + 1);
        } while (eliminatedIds.contains(random));

        return random;
    }
    private void dayPhase(Player newVictim) {

        System.out.println("Утро наступило.");
        if (newVictim == null) {
            System.out.println("Никто не был убит ночью.");
        } else if (!newVictim.isAlive) {
            System.out.println("Игрок " + newVictim.playerId + " был убит ночью.");
        }

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
                                int currentPlayerId = currentPlayer.playerId;

                                for (Player player : players) {
                                    if (player.playerId != currentPlayerId && player.isAlive) {
                                        int targetId = getProgramChoice(eliminatedIds);
                                        Player target = getPlayerById(targetId);
                                        if (target != null && target.isAlive) {
                                            target.votes++;
                                            System.out.println("Игрок " + player.playerId + " проголосовал за " + targetId);
                                        }
                                    }
                                }

                                if (currentPlayer.isAlive) {
                                    System.out.println("Игрок " + currentPlayerId + ", введите номер игрока, за которого вы голосуете:");
                                    Scanner scanner = new Scanner(System.in);
                                    int targetId = scanner.nextInt();
                                    Player target = getPlayerById(targetId);
                                    if (target != null && target.isAlive) {
                                        target.votes++;
                                        System.out.println("Игрок " + currentPlayerId + " проголосовал за " + targetId);
                                    }
                                }

                                Player eliminatedPlayer = determineEliminatedPlayer();

                                if (eliminatedPlayer != null) {
                                    System.out.println("Игрок " + eliminatedPlayer.playerId + " был исключен из игры.");
                                    eliminatedIds.add(eliminatedPlayer.playerId);
                                    eliminatedPlayer.isAlive = false;
                                } else {
                                    System.out.println("Игрокам не удалось договориться и никто не исключен.");
                                }
                                resetPlayersVotes();

                                if (checkForWinner()) {
                                    System.out.println("Игра окончена.");
                                    if (determineWinner().equals("Mafia")) {
                                        System.out.println("Победила мафия!");
                                    } else if (determineWinner().equals("Civilians")) {
                                        System.out.println("Победили мирные жители!");
                                    }
                                    endMessagePrinting();
                                    System.exit(0);
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
    private Player determineEliminatedPlayer() {
        Player eliminatedPlayer = null;
        int maxVotes = 0;

        for (Player player : players) {
            if (player.votes > maxVotes && player.isAlive) {
                maxVotes = player.votes;
                eliminatedPlayer = player;
            }
        }

        int countMaxVotes = 0;

        for (Player player : players) {
            if (player.votes == maxVotes && player.isAlive) {
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
            if (player.votes != 0 && player.isAlive) {
                player.votes = 0;
            }
        }
    }
    private boolean checkForWinner() {
        int mafiaAlive = 0;
        int citizensAlive = 0;

        for (Player player : players) {
            if (player.isAlive) {
                if (player.role == Role.MAFIA) {
                    mafiaAlive++;
                } else if (player.role == Role.CITIZEN || player.role == Role.COMMISSAR) {
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
            if (player.isAlive) {
                if (player.role == Role.MAFIA) {
                    mafiaAlive++;
                } else if (player.role == Role.CITIZEN) {
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
    private void endMessagePrinting() {;
        for (Player player : players) {
            if(player.getRole() == Role.MAFIA) {
                System.out.print("Игрок " + player.playerId + " ");
            }
        }
        if (mafiaCount == IConsts.MIN_MAFIA_COUNT) {
            System.out.println("был мафией!");
        }
        else {
            System.out.println("были мафиями!");
        }
    }
}