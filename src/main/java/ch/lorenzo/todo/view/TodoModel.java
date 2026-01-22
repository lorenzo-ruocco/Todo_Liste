package ch.lorenzo.todo.view;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TodoModel {
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

}
