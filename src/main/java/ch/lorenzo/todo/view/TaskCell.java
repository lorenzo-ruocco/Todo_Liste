package ch.lorenzo.todo.view;

import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;

public class TaskCell extends ListCell<Task> {

    private final CheckBox checkBox = new CheckBox();
    private final TextField titleLabel = new TextField();
    private final TextField descriptionLabel = new TextField();
    private final GridPane layout = new GridPane();
    

    public TaskCell(){
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
        titleLabel.setPrefHeight(BASELINE_OFFSET_SAME_AS_HEIGHT);
        layout.add(checkBox, 0, 0);
        layout.add(new VBox(titleLabel, descriptionLabel), 1, 0);
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);

        if (getItem() != null) {
            titleLabel.textProperty().unbindBidirectional(getItem().titleProperty());
            descriptionLabel.textProperty().unbindBidirectional(getItem().descriptionProperty());
            checkBox.selectedProperty().unbindBidirectional(getItem().completedProperty());
        }

        if (empty || task == null) {
            setText(null);
            setGraphic(null);
        } else {
            titleLabel.textProperty().bindBidirectional(task.titleProperty());
            descriptionLabel.textProperty().bindBidirectional(task.descriptionProperty());
            checkBox.selectedProperty().bindBidirectional(task.completedProperty());
            setGraphic(layout);
        }
    }


}
