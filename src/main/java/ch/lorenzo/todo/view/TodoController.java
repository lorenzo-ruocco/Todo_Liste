package ch.lorenzo.todo.view;

import ch.lorenzo.todo.view.TodoModel;
import ch.lorenzo.todo.view.TodoView;

public class TodoController {

    private final TodoModel model;
    private final TodoView view;

    protected TodoController(TodoModel model, TodoView view) {
        this.model = model;
        this.view = view;

        view.getTaskListView().setItems(model.getTasks());
        view.getTaskListView().setCellFactory(lv -> new TaskCell());

        view.addButton.setOnAction(e ->
                model.addTask(new Task("", ""))
        );

        view.removeButton.setOnAction(e ->
                model.removeTask(view.getTaskListView().getSelectionModel().getSelectedItem())
        );
    }
}

