package common;

import java.time.LocalDateTime;

public class Commit {
    public String message;
    public String user;
    public LocalDateTime datetime;

    public Commit(String user, String message) {
        this.user = user;
        this.message = message;
        this.datetime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Commit by " + user + " on " + datetime + ": " + message;
    }
}