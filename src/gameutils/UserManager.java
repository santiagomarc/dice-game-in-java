package gameutils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, String> users;
    private String currentUser;

    public UserManager() {
        this.users = new HashMap<>();
        this.currentUser = null;
        loadUsers();
    }

    public void loadUsers() {
        File dataDir = new File("data");
        File usersFile = new File("data/users.txt");

        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userInfo = line.split(",");
                if (userInfo.length == 2) {
                    users.put(userInfo[0], userInfo[1]);  
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/users.txt"))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validateUsername(String username) {
        return username.length() >= 4;
    }

    public boolean validatePassword(String password) {
        return password.length() >= 8;
    }

    public boolean register(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("Username already exists. Please choose a different one.");
            return false;
        }
        if (!validateUsername(username)) {
            System.out.println("Username must be at least 4 characters long.");
            return false;
        }
        if (!validatePassword(password)) {
            System.out.println("Password must be at least 8 characters long.");
            return false;
        }

        users.put(username, password);
        saveUsers();
        System.out.println("Registration successful!");
        return true;
    }

    public boolean login(String username, String password) {
        if (users.containsKey(username) && users.get(username).equals(password)) {
            currentUser = username;
            return true;
        }
        return false;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
