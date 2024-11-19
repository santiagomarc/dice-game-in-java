import gameutils.DiceGame;
import gameutils.Score;
import gameutils.User;
import gameutils.UserManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);  
        
        UserManager userManagerInst = new UserManager();
        Score scoreInst = new Score();
        User userInst = new User();
        DiceGame diceGame = new DiceGame(userManagerInst, scoreInst, userInst);
        String currentUser = userManagerInst.getCurrentUser();

        while (true) {
            System.out.println("\n------------------");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println("------------------\n");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                while (true) {
                    System.out.print("Enter your username (at least 4 characters), or leave blank to cancel: ");
                    String username = scanner.nextLine();

                    if (username.isEmpty() || username.isBlank()) {
                        System.out.println("\n-------------CANCELLED-------------");
                        break;
                    }
                    if (!userManagerInst.validateUsername(username)) {
                        System.out.println("\n*****Username must be at least 4 characters long.*****\n");
                        break;
                    }

                    System.out.print("Enter your password (at least 8 characters), or leave blank to cancel: ");
                    String password = scanner.nextLine();

                    if (password.isEmpty() || password.isBlank()) {
                        System.out.println("\n-------------CANCELLED-------------");
                        break;
                    }
                    if (!userManagerInst.validatePassword(password)) {
                        System.out.println("\n*****Password must be at least 8 characters long.*****");
                        break;
                    }

                    if (userManagerInst.register(username, password)) {
                        break;
                    }
                }

            } else if (choice.equals("2")) {
                System.out.println("\n-------------LOGIN-------------");
                System.out.print("Enter username: ");
                String username = scanner.nextLine();

                if (username.isEmpty() || username.isBlank()) {
                    System.out.println("\n-------------CANCELLED-------------");
                    continue;
                }

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                if (password.isEmpty() || password.isBlank()) {
                    System.out.println("\n-------------CANCELLED-------------");
                    continue;
                }

                if (userManagerInst.login(username, password)) {
                    currentUser = userManagerInst.getCurrentUser();
                    System.out.println("\n+++++++Login successful. Welcome, " + currentUser + "!+++++++");
                    diceGame.menu(currentUser); 
                } else {
                    System.out.println("\n*****Invalid username or password. Please try again.*****");
                }

            } else if (choice.equals("3")) {
                System.out.println("\n+++++++Exiting Game.+++++++");
                scanner.close();
                System.exit(0);  
            } else {
                System.out.println("\n-----Invalid option. Please input a valid number.-----");
            }
        }
    }
}
