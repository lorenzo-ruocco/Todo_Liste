package ch.lorenzo.todo.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class TodoMain extends Application {
    private TodoView view;
    private TodoModel model;
    private TodoController controller;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        model = new TodoModel();
        view = new TodoView(model, stage);
        controller = new TodoController(model, view);

        view.start();
    }
}
