package ch.lorenzo.todo.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class TodoMain extends Application {

    // Core MVC components
    private TodoModel model;
    private TodoView view;
    private TodoController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Create model, view, controller
        model = new TodoModel();
        view = new TodoView(model, stage);
        controller = new TodoController(model, view);
        controller.initFromStorage();

        // Show the UI
        view.start();
    }

    @Override
    public void stop() {
        // Persist on shutdown
        if (controller != null) {
            controller.autoSaveToStorage();
        }
    }
}

