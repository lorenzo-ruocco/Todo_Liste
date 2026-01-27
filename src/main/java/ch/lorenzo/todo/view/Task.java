package ch.lorenzo.todo.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Task {
    // Static counter to assign incremental IDs
    private static int idCounter = 0;

    // Immutable ID for this task instance
    private final int id;

    // Observable properties for UI bindings
    private StringProperty title = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private BooleanProperty completed = new SimpleBooleanProperty(false);

    public Task(String title, String description) {
        this.id = idCounter++;
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
    }

    // ID accessor
    public int getId() {
        return id;
    }

    // Property accessors for bindings
    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public BooleanProperty completedProperty() {
        return completed;
    }
}
