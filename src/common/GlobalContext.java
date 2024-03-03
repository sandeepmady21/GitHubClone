package common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalContext {
    public static AtomicInteger connectedUsers = new AtomicInteger(0);
    public static ConcurrentHashMap<String, Project> projects = new ConcurrentHashMap<>();
}