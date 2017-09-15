package dbService;

import dbService.dataSets.User;

import java.util.Optional;

public class AccountService {

    private static DBService dbService;

    static {
        dbService = new DBService();
        System.out.println("AccountService loaded");
    }

    private AccountService() {
    }


    public static boolean signUp(String login, String password) {

        boolean result = false;
        try {
            Optional<User> user = Optional.ofNullable(dbService.getUserByLogin(login));
            if (user.isPresent()) {
                result = false;
            } else {
                dbService.addUser(login, password);
                result = true;
            }

        } catch (DBException e) {
            e.printStackTrace();
        }

        return result;

    }

    public static boolean signIn(String login, String password) {

        return checkRegister(login) && checkPassword(login, password);
    }


    private static boolean checkRegister(String login) {
        boolean isRegistered = false;
        try {
            isRegistered = dbService.getUserByLogin(login) != null;
        } catch (DBException e) {
            e.printStackTrace();
        }

        return isRegistered;
    }

    private static boolean checkPassword(String login, String password) {
        boolean matched = false;
        try {
            matched = password.equals(dbService.getUsersPassword(login));
        } catch (DBException e) {
            e.printStackTrace();
        }

        return matched;
    }
}
