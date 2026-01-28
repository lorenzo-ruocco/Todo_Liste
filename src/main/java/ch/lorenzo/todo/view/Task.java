package ch.lorenzo.todo.view;

import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    // Created-at timestamp (property for UI binding)
    private final ObjectProperty<LocalDateTime> createdAtProperty =
            new SimpleObjectProperty<>(LocalDateTime.now());

    // Observable task fields
    private StringProperty title = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private BooleanProperty completed = new SimpleBooleanProperty(false);

    public Task(String title, String description) {
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
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

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAtProperty;
    }
}
