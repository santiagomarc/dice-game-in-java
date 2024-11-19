package gameutils;

import java.io.*;
import java.util.*;

public class DiceGame {
    private UserManager userManagerInst;
    private Score scoreInst;
    private User userInst;

    public DiceGame(UserManager userManagerInst, Score scoreInst, User userInst) {
        this.userManagerInst = userManagerInst;
        this.scoreInst = scoreInst;
        this.userInst = userInst;
        loadScore();
    }

    public void loadScore() {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File rankingsFile = new File("data/rankings.txt");
        if (!rankingsFile.exists()) {
            try {
                rankingsFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error creating rankings.txt: " + e.getMessage());
            }
        }
    }

    public void saveScore(String username, String gameId, int points, int wins) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/rankings.txt", true))) {
            writer.write(username + "," + gameId + "," + points + "," + wins + "\n");
        } catch (IOException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }

    public void playGame(String currentUser) {
        int userWins = 0;
        int userPoints = 0;
        int stage = 0;

        Random random = new Random();

        while (true) {
            int CPU_roundWon = 0;
            int userRoundWon = 0;

            String gameId = String.format("%04d", random.nextInt(10000));

            try (BufferedReader reader = new BufferedReader(new FileReader("data/rankings.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(gameId)) {
                        gameId = String.format("%04d", random.nextInt(10000));
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading rankings.txt: " + e.getMessage());
            }

            System.out.println("\n-------Starting game as " + currentUser + "-------");

            for (int i = 0; i < 3; i++) {
                System.out.println("\n++++++++++++++++++++++++++++++++++");
                int userRolls = random.nextInt(6) + 1;
                int CPURolls = random.nextInt(6) + 1;

                System.out.println(currentUser + " rolled " + userRolls);
                System.out.println("CPU rolled " + CPURolls);

                if (userRolls == CPURolls) {
                    System.out.println("It's a tie!");
                    System.out.println("++++++++++++++++++++++++++++++++++");
                } else if (userRolls > CPURolls) {
                    System.out.println("You won this round " + currentUser);
                    System.out.println("++++++++++++++++++++++++++++++++++");
                    userRoundWon++;
                    userPoints++;
                } else {
                    System.out.println("CPU wins this round!");
                    System.out.println("++++++++++++++++++++++++++++++++++");
                    CPU_roundWon++;
                }
            }

            if (userRoundWon == CPU_roundWon) {
                System.out.println("\n++++++++The stage is tie!+++++++++");
                System.out.println("         Additional roll!\n");
                while (userRoundWon == CPU_roundWon) {
                    int userRolls = random.nextInt(6) + 1;
                    int CPURolls = random.nextInt(6) + 1;

                    System.out.println(currentUser + " rolled " + userRolls);
                    System.out.println("CPU rolled " + CPURolls);

                    if (userRolls == CPURolls) {
                        System.out.println("\n---------It's a tie again!--------\n");
                    } else if (userRolls > CPURolls) {
                        System.out.println("\n   You won the tie-breaker " + currentUser + "!");
                        System.out.println("++++++++++++++++++++++++++++++++++");
                        userRoundWon++;
                    } else {
                        System.out.println("\n-----CPU wins the tie-breaker!-----");
                        System.out.println("++++++++++++++++++++++++++++++++++");
                        CPU_roundWon++;
                    }
                }
            }

            if (userRoundWon >= 2) {
                System.out.println("\n======You won this stage " + currentUser + "!======");
                userPoints += 3;
                userWins++;
                System.out.println("\n" + currentUser + ": Total points - " + userPoints + ", Stages won: " + userWins);

                Scanner scanner = new Scanner(System.in);
                System.out.print("Do you want to continue to the next stage? (1 = YES, 0 = NO): ");
                String choice = scanner.nextLine();

                while (!choice.equals("1") && !choice.equals("0")) {
                    System.out.print("Invalid input. Please enter 1 to continue or 0 to stop: ");
                    choice = scanner.nextLine();
                }

                if (choice.equals("1")) {
                    stage++;
                    continue;
                } else if (choice.equals("0")) {
                    stage++;
                    System.out.println("\n-------The game has ended.-------");
                    saveScore(currentUser, gameId, userPoints, userWins);
                    menu(currentUser);
                    return;
                }

            } else if (CPU_roundWon >= 2) {
                System.out.println("\n-------You lost this stage " + currentUser + ".-------");
                if (stage == 0) {
                    System.out.println("\n    Game over. You didn't win any stages.\n");
                } else {
                    System.out.println("\n    Game over. You reached " + stage + " stages.\n");
                }
                System.out.println(currentUser + ": Total points - " + userPoints + ", Stages won: " + userWins);
                saveScore(currentUser, gameId, userPoints, userWins);
                menu(currentUser);
                return;
            }
        }
    }

    public void showTopScores(String currentUser) {
        List<String[]> scoresList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/rankings.txt"))) {
            System.out.println("\n                         TOP SCORES");
            System.out.println("*****************************************************************");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    scoresList.add(data);
                }
            }

            if (scoresList.isEmpty()) {
                System.out.println("-------No games played yet. Play a game to see top scores!-------");
                menu(currentUser);
                return;
            }

            scoresList.sort((a, b) -> Integer.compare(Integer.parseInt(b[2]), Integer.parseInt(a[2])));
            scoresList = scoresList.subList(0, Math.min(10, scoresList.size()));

            for (int i = 0; i < scoresList.size(); i++) {
                String[] score = scoresList.get(i);
                System.out.println((i + 1) + ". " + score[0] + " --- Game ID #" + score[1] + " --- Points - " + score[2] + ", Wins: " + score[3]);
            }
            System.out.println("*****************************************************************\n");

        } catch (IOException e) {
            System.out.println("Error reading rankings.txt: " + e.getMessage());
        }

        menu(currentUser);
    }

    public void logout(String currentUser) {
        System.out.println("\n------User: " + currentUser + " LOGGED OUT!------");
    }

    public void menu(String currentUser) {
        System.out.println("\n-------------");
        System.out.println("1. Play Game");
        System.out.println("2. Show top scores");
        System.out.println("3. Logout");
        System.out.println("-------------\n");

        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();

        switch (option) {
            case "1":
                playGame(currentUser);
                break;
            case "2":
                showTopScores(currentUser);
                break;
            case "3":
                logout(currentUser);
                break;
            default:
                System.out.println("\n*******Invalid option. Please try again.*******");
                menu(currentUser);
                break;
        }
    }
}
