package ch.lorenzo.todo.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TodoModel {

    // Backing list for tasks
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final ObservableList<Task> doneTasks = FXCollections.observableArrayList();

    // Add a task to the list
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Remove a task from the list
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    // Expose tasks for bindings/view
    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public ObservableList<Task> getDoneTasks() {
        return doneTasks;
    }

    public void addDoneTask(Task task) {
        doneTasks.add(task);
    }

    public void removeDoneTask(Task task) {
        doneTasks.remove(task);
    }

    
}

