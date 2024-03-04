package common;

import java.util.ArrayList;
import java.util.List;

public class Project {
    public String name;
    public List<Commit> commits = new ArrayList<>(); // we will just save commit-message in List
    public List<String> activityLog = new ArrayList<>(); // Log of all actions performed on the project

    public Project(String name) {
        this.name = name;
    }
}