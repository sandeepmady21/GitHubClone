package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes of the Project class
    public String name; // Name of the project
    public List<String> activityLog; // Log of activities performed on the project
    public List<Commit> commits; // List of commits made to the project
    public ConcurrentHashMap<String, File> files; // Map of files in the project, keyed by file name

    // Constructor to create a new Project object with a given name
    public Project(String name) {
        this.name = name; // Set the project name
        this.activityLog = new ArrayList<>(); // Initialize the activity log as an empty ArrayList
        this.commits = new ArrayList<>(); // Initialize the list of commits as an empty ArrayList
        this.files = new ConcurrentHashMap<>(); // Initialize the files map as an empty ConcurrentHashMap
    }

    // Copy constructor to create a deep copy of another Project object
    public Project(Project another) {
        this.name = another.name; // Copy the name from the original project
        this.activityLog = new ArrayList<>(another.activityLog); // Deep copy of the activity log
        this.commits = new ArrayList<>(another.commits); // Deep copy of the list of commits

        // Initialize the files map as an empty ConcurrentHashMap
        this.files = new ConcurrentHashMap<>();

        // Copy files from the original project if it's not null
        if (another.files != null) {
            // Iterate over each entry in the files map of the original project
            for (Map.Entry<String, File> entry : another.files.entrySet()) {
                // Create a deep copy of each File object and put it into the files map of the new project
                this.files.put(entry.getKey(), new File(entry.getValue()));
            }
        }
    }
}
