package ch.lorenzo.todo.view;

import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;

public class TaskCell extends ListCell<Task> {

    private final GridPane layout = new GridPane();

    private final CheckBox checkBox = new CheckBox();

    private final TextField titleLabel = new TextField();
    private final TextArea descriptionLabel = new TextArea();

    private static final PseudoClass COMPLETED_PSEUDO = PseudoClass.getPseudoClass("completed");
    private final ChangeListener<Boolean> completedListener = (obs, oldValue, newValue) ->
            pseudoClassStateChanged(COMPLETED_PSEUDO, Boolean.TRUE.equals(newValue));

    

    public TaskCell() {
        // Styling
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        titleLabel.setPrefHeight(BASELINE_OFFSET_SAME_AS_HEIGHT);
        titleLabel.getStyleClass().add("title-field");


        descriptionLabel.setStyle("-fx-font-size: 12;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("description-field");
        descriptionLabel.setContextMenu(new ContextMenu());
        descriptionLabel.setOnContextMenuRequested(e -> e.consume());

        

        // TextArea sizing (8 Zeilen)
        descriptionLabel.setPrefRowCount(8);
        descriptionLabel.setMinHeight(Region.USE_PREF_SIZE);
        descriptionLabel.setMaxHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(descriptionLabel, Priority.NEVER);

        // Container
        VBox fieldsBox = new VBox(titleLabel, descriptionLabel);
        fieldsBox.setFillWidth(true);

        // Grid layout
        ColumnConstraints col0 = new ColumnConstraints();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        layout.getColumnConstraints().addAll(col0, col1);

        // Width/Stretch
        layout.setMaxWidth(Double.MAX_VALUE);
        fieldsBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(fieldsBox, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setContextMenu(new ContextMenu());
        titleLabel.setOnContextMenuRequested(e -> e.consume());

        // Add nodes
        layout.add(checkBox, 0, 0);
        layout.add(fieldsBox, 1, 0);
        GridPane.setValignment(checkBox, VPos.TOP);
        GridPane.setHalignment(checkBox, HPos.LEFT);
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        Task oldTask = getItem();

        // Remove old bindings to avoid memory leaks / double-binding
        if (oldTask != null) {
            titleLabel.textProperty().unbindBidirectional(oldTask.titleProperty());
            descriptionLabel.textProperty().unbindBidirectional(oldTask.descriptionProperty());
            checkBox.selectedProperty().unbindBidirectional(oldTask.completedProperty());
            oldTask.completedProperty().removeListener(completedListener);
        }

        super.updateItem(task, empty);

        // Clear the cell when nothing should be shown
        if (empty || task == null) {
            setText(null);
            setGraphic(null);
            pseudoClassStateChanged(COMPLETED_PSEUDO, false);
        } else {
            // Set up bindings for the current task object
            titleLabel.textProperty().bindBidirectional(task.titleProperty());
            descriptionLabel.textProperty().bindBidirectional(task.descriptionProperty());
            checkBox.selectedProperty().bindBidirectional(task.completedProperty());
            task.completedProperty().addListener(completedListener);
            pseudoClassStateChanged(COMPLETED_PSEUDO, task.completedProperty().get());
            setGraphic(layout);
        }
    }

}
