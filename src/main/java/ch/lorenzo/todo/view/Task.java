package ch.lorenzo.todo.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Task {
    private static int idCounter = 0;

    private final int id;
    private StringProperty title = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private BooleanProperty completed = new SimpleBooleanProperty(false);

    public Task(String title, String description) {
        this.id = idCounter++;
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
    }

    public int getId() {
        return id;
    }

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