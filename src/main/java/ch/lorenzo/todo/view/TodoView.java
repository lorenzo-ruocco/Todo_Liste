package ch.lorenzo.todo.view;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

public class TodoView {
    private final TodoModel model;
    private Stage stage;
    private ListView<Task> taskListView = new ListView<>();

    GridPane pane;
    
    Button addButton = new Button("+");
    Button removeButton = new Button("-");

    HBox buttonBox = new HBox();

private GridPane createPane() {
    GridPane pane = new GridPane();

    buttonBox.setFillHeight(true);
    buttonBox.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(buttonBox, Priority.ALWAYS);

    addButton.setMaxWidth(Double.MAX_VALUE);
    removeButton.setMaxWidth(Double.MAX_VALUE);

    HBox.setHgrow(addButton, Priority.ALWAYS);
    HBox.setHgrow(removeButton, Priority.ALWAYS);

    buttonBox.getChildren().addAll(addButton, removeButton);
    pane.add(buttonBox, 0, 0);
    pane.add(taskListView, 0, 1);

    GridPane.setVgrow(taskListView, Priority.ALWAYS);
    GridPane.setHgrow(taskListView, Priority.ALWAYS);
    taskListView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

    RowConstraints row0 = new RowConstraints();
    RowConstraints row1 = new RowConstraints();
    row1.setVgrow(Priority.ALWAYS);
    pane.getRowConstraints().addAll(row0, row1);

    return pane;
}


    public TodoView(TodoModel model, Stage stage){
        this.model = model;
        this.stage = stage;

        this.pane = createPane();

        stage.setTitle("Todo-List");

        Rectangle2D vb = Screen.getPrimary().getVisualBounds();
        stage.setMinHeight(vb.getHeight());
        stage.setMaxHeight(vb.getHeight());

        stage.setMinWidth(400);
        stage.setMaxWidth(400);
        
        Scene scene = new Scene(pane);
        
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
    }

    public void start(){
        stage.show();
    }

    public ListView<Task> getTaskListView(){
        return taskListView;
    }

    public Stage getStage(){
        return stage;
    }

    
}