package common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Commit implements Serializable {
	private static final long serialVersionUID = 1L;
	
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Commit by " + user + " on " + datetime.format(formatter) + ": " + message;
    }
}