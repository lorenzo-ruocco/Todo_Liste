package ch.lorenzo.todo.view;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javafx.stage.FileChooser;

public class TodoController {
    // View state
    private boolean showingDone = false;
    // Listener registry
    private final Set<Task> hookedTasks = Collections.newSetFromMap(new IdentityHashMap<>());

    //Storage path chosen by user
    private File storageFile;

    // Model + View references
    private final TodoModel model;
    private final TodoView view;

    protected TodoController(TodoModel model, TodoView view) {
        this.model = model;
        this.view = view;

        // Bind open tasks
        view.getTaskListView().setItems(model.getTasks());

        // Cell rendering for open tasks
        view.getTaskListView().setCellFactory(lv -> new TaskCell());

        // Add a new empty task
        view.addButton.setOnAction(e -> {
            Task task = new Task("", "");
            hookTask(task);
            model.addTask(task);
        });

        // Remove selected task
        view.removeButton.setOnAction(e ->
                model.removeTask(view.getTaskListView().getSelectionModel().getSelectedItem())
        );

        // Size and width binding for open tasks
        view.getTaskListView().setFixedCellSize(200);
        view.getTaskListView().setCellFactory(lv ->{
            TaskCell cell = new TaskCell();
            cell.prefWidthProperty().bind(lv.widthProperty().subtract(18));
            return cell;
        });

        // Bind done tasks
        view.getDoneListView().setItems(model.getDoneTasks());
        // Cell rendering for done tasks
        view.getDoneListView().setCellFactory(lv -> {
            TaskCell cell = new TaskCell();
            cell.prefWidthProperty().bind(lv.widthProperty().subtract(18));
            return cell;
        });
        // Size and width binding for done tasks
        view.getDoneListView().setFixedCellSize(200);

        // Hook existing tasks and any tasks added later
        model.getTasks().forEach(this::hookTask);
        model.getDoneTasks().forEach(this::hookTask);
        model.getTasks().addListener((ListChangeListener<Task>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Task task : change.getAddedSubList()) {
                        hookTask(task);
                    }
                }
            }
        });
        
        // Toggle between open and done list
        view.doneButton.setOnAction(e -> {
            GridPane pane = view.getPane();

            if (!showingDone) {
                pane.getChildren().remove(view.getTaskListView());
                if (!pane.getChildren().contains(view.getDoneListView())) {
                    pane.add(view.getDoneListView(), 0, 1);
                    GridPane.setVgrow(view.getDoneListView(), Priority.ALWAYS);
                    GridPane.setHgrow(view.getDoneListView(), Priority.ALWAYS);
                    view.getDoneListView().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
                view.doneButton.getStyleClass().add("button-active");
                showingDone = true;
            } else {
                pane.getChildren().remove(view.getDoneListView());
                if (!pane.getChildren().contains(view.getTaskListView())) {
                    pane.add(view.getTaskListView(), 0, 1);
                    GridPane.setVgrow(view.getTaskListView(), Priority.ALWAYS);
                    GridPane.setHgrow(view.getTaskListView(), Priority.ALWAYS);
                    view.getTaskListView().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
                view.doneButton.getStyleClass().remove("button-active");
                showingDone = false;
            }
        });

        // Storage file chooser
        view.storageButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Storage File");
            chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
            );
            chooser.setInitialFileName("todo-storage.json");

            File chosen = chooser.showSaveDialog(view.getStage());
            if (chosen != null) {
                storageFile = chosen;
            }

            if (storageFile != null) {
                try {
                    Path p = storageFile.toPath();
                    if (Files.notExists(p)) {
                        Files.writeString(
                            p,
                            "{ \"open\": [], \"done\": [] }",
                            StandardOpenOption.CREATE_NEW
                        );
                    }
                } catch (IOException ex) {
                    // Optional: log or show error
                }
            }

        });
    }

    private void hookTask(Task task) {
        if (!hookedTasks.add(task)) {
            return;
        }
        // Move tasks when checkbox changes
        task.completedProperty().addListener((obs, wasDone, isDone) -> {
            if (isDone) {
                animateRemoval(view.getTaskListView(), task, () -> {
                    if (model.getTasks().contains(task)) {
                        model.getTasks().remove(task);
                    }
                    if (!model.getDoneTasks().contains(task)) {
                        model.getDoneTasks().add(task);
                    }
                });
            } else {
                animateRemoval(view.getDoneListView(), task, () -> {
                    if (model.getDoneTasks().contains(task)) {
                        model.getDoneTasks().remove(task);
                    }
                    if (!model.getTasks().contains(task)) {
                        model.getTasks().add(task);
                    }
                });
            }
        });
    }

    private void animateRemoval(ListView<Task> listView, Task task, Runnable after) {
        // Fade out the cell before moving it
        Platform.runLater(() -> {
            ListCell<Task> cell = findCell(listView, task);
            if (cell == null) {
                after.run();
                return;
            }
            FadeTransition ft = new FadeTransition(Duration.millis(350), cell);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                cell.setOpacity(1.0);
                after.run();
            });
            ft.play();
        });
    }

    private ListCell<Task> findCell(ListView<Task> listView, Task task) {
        // Find the visible cell for this task
        for (Node node : listView.lookupAll(".list-cell")) {
            if (node instanceof ListCell) {
                @SuppressWarnings("unchecked")
                ListCell<Task> cell = (ListCell<Task>) node;
                if (cell.getItem() == task) {
                    return cell;
                }
            }
        }
        return null;
    }
}


