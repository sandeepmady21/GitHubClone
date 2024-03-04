package common;

import java.io.Serializable;

public class File implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private StringBuilder content;  // use StringBuilder for efficient string concatenation
    private boolean exists;

    public File(String name) {
        this.name = name;
        this.content = new StringBuilder();
        this.exists = true;
    }
    
    // Deep copy constructor
    public File(File original) {
        this.name = original.name;
        this.content = new StringBuilder(original.content.toString());
        this.exists = original.exists; 
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content.toString();
    }
    
    public boolean exists() {    
        return exists;
    }

    public void appendContent(String addition) {
        this.content.append(addition);
    }

    //equals and hashCode methods are added to use File objects as keys in a map
    @Override 
    public boolean equals(Object obj) { 
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 
        File file = (File) obj; 
        return name.equals(file.name);
    } 

    @Override 
    public int hashCode() { 
        return name.hashCode(); 
    }
}