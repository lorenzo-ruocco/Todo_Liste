package ch.lorenzo.todo.view;


public class TodoController {

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
    }
}


