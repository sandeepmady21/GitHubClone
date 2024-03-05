package common;

import java.io.Serializable;

public class File implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes of the File class
    private String name; // Name of the file
    private StringBuilder content; // Content of the file stored as a StringBuilder
    private boolean exists; // Boolean flag indicating whether the file exists

    // Constructor to create a new File object with a given name
    public File(String name) {
        this.name = name; // Set the file name
        this.content = new StringBuilder(); // Initialize the content as an empty StringBuilder
        this.exists = true; // Set the exists flag to true, indicating that the file exists
    }
    
    // Deep copy constructor to create a copy of another File object
    public File(File original) {
        this.name = original.name; // Copy the name from the original file
        this.content = new StringBuilder(original.content.toString()); // Deep copy of the content
        this.exists = original.exists; // Copy the exists flag from the original file
    }

    // Getter method to retrieve the name of the file
    public String getName() {
        return name;
    }

    // Getter method to retrieve the content of the file as a string
    public String getContent() {
        return content.toString();
    }
    
    // Method to check if the file exists
    public boolean exists() {    
        return exists;
    }

    // Method to append content to the file
    public void appendContent(String addition) {
        this.content.append(addition); // Append the specified content to the StringBuilder
    }

    // Override the equals method to compare File objects based on their names
    @Override 
    public boolean equals(Object obj) { 
        if (this == obj) return true; // If the objects are the same instance, return true
        if (obj == null || getClass() != obj.getClass()) return false; // If the classes are different, return false
        File file = (File) obj; // Cast the object to a File
        return name.equals(file.name); // Compare the names of the files
    } 

    // Override the hashCode method to generate a hash code based on the name of the file
    @Override 
    public int hashCode() { 
        return name.hashCode(); // Generate a hash code based on the name of the file
    }
}
