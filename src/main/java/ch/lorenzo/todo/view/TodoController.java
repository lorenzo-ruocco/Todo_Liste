package ch.lorenzo.todo.view;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class TodoController {
    private boolean showingDone = false;

    // Model + View references
    private final TodoModel model;
    private final TodoView view;

    protected TodoController(TodoModel model, TodoView view) {
        this.model = model;
        this.view = view;

        // Bind list content to the model
        view.getTaskListView().setItems(model.getTasks());

        // Provide custom cell rendering
        view.getTaskListView().setCellFactory(lv -> new TaskCell());

        // Add a new empty task
        view.addButton.setOnAction(e ->
                model.addTask(new Task("", ""))
        );

        // Remove selected task
        view.removeButton.setOnAction(e ->
                model.removeTask(view.getTaskListView().getSelectionModel().getSelectedItem())
        );

        view.getTaskListView().setFixedCellSize(200);
        view.getTaskListView().setCellFactory(lv ->{
            TaskCell cell = new TaskCell();
            cell.prefWidthProperty().bind(lv.widthProperty().subtract(18));
            return cell;
        });

        view.getDoneListView().setItems(model.getDoneTasks());
        view.getDoneListView().setCellFactory(lv -> {
            TaskCell cell = new TaskCell();
            cell.prefWidthProperty().bind(lv.widthProperty().subtract(18));
            return cell;
        });
        view.getDoneListView().setFixedCellSize(200);
        
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
                showingDone = true;
            } else {
                pane.getChildren().remove(view.getDoneListView());
                if (!pane.getChildren().contains(view.getTaskListView())) {
                    pane.add(view.getTaskListView(), 0, 1);
                    GridPane.setVgrow(view.getTaskListView(), Priority.ALWAYS);
                    GridPane.setHgrow(view.getTaskListView(), Priority.ALWAYS);
                    view.getTaskListView().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
                showingDone = false;
            }
        });
    }
}


