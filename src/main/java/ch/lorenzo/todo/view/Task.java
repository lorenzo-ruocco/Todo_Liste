package ch.lorenzo.todo.view;

import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    // ID generator
    private static int idCounter = 0;
    // Creation timestamp
    private final LocalDateTime createdAt = LocalDateTime.now();

    // Immutable ID for this task instance
    private final int id;
    // Property form of the timestamp
    private final ObjectProperty<LocalDateTime> createdAtProperty = new SimpleObjectProperty<>(LocalDateTime.now());

    // Observable properties
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

    // Property accessors
    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAtProperty;
    }
}
