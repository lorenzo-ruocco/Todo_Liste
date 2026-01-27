package ch.lorenzo.todo.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TodoModel {

    // Task collections
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final ObservableList<Task> doneTasks = FXCollections.observableArrayList();

    private String storageFile;

    // Open tasks
    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    // Done tasks
    public ObservableList<Task> getDoneTasks() {
        return doneTasks;
    }

    public void addDoneTask(Task task) {
        doneTasks.add(task);
    }

    public void removeDoneTask(Task task) {
        doneTasks.remove(task);
    }

    // Storage file
    public String getStorageFile() {
        return storageFile;
    }

    public void setStorageFile(String storageFile) {
        this.storageFile = storageFile;
    }
}

