package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public List<String> activityLog;
    public List<Commit> commits;
    public ConcurrentHashMap<String, File> files; // Keys to retrieve files are now Strings
    
    public Project(String name) {
        this.name = name;
        this.activityLog = new ArrayList<>();
        this.commits = new ArrayList<>();
        this.files = new ConcurrentHashMap<>();
    }

    public Project(Project another) {
        this.name = another.name;
        this.activityLog = new ArrayList<String>(another.activityLog); // new ArrayList for deep copy
        this.commits = new ArrayList<Commit>(another.commits); // new ArrayList for deep copy
        this.files = new ConcurrentHashMap<String, File>(); // new ConcurrentHashMap makes sure this.files is never null
        if(another.files != null) { // Checks if files in the original project is not null before trying to copy
            for(Map.Entry<String, File> entry : another.files.entrySet()) {
                this.files.put(entry.getKey(), new File(entry.getValue())); // deep copy for each File
            }
        }
    }
}