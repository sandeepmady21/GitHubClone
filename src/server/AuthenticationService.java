package server;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    
    private static final Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("user1", "pass1");
        USERS.put("user2", "pass2");
        USERS.put("user3", "pass3");
        USERS.put("user4", "pass4");
        USERS.put("user5", "pass5");
    }

    public static Boolean authenticate(String username, String password) {
        String validPassword = USERS.get(username);
        return validPassword != null && validPassword.equals(password);
    }
}