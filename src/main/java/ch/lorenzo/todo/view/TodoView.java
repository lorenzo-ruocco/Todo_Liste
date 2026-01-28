package ch.lorenzo.todo.view;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TodoView {

    // Model + stage
    private final TodoModel model;
    private Stage stage;

    // Lists
    private ListView<Task> taskListView = new ListView<>();
    private ListView<Task> doneListView = new ListView<>();
    GridPane pane;

    // Buttons
    Button addButton = new Button("+");
    Button removeButton = new Button("-");
    Button doneButton = new Button("Done");
    Button storageButton = new Button("Storage");

    // Title bar
    HBox buttonBox = new HBox();
    private HBox titleBar = new HBox();
    private Label titleLabel = new Label("Todo-List");
    private Button minimizeButton = new Button("—");
    private Button closeButton = new Button("✕");

    // Drag state
    private double dragOffsetX;
    private double dragOffsetY;
    private static final double SNAP_THRESHOLD = 24;

    public TodoView(TodoModel model, Stage stage) {
        this.model = model;
        this.stage = stage;

        this.titleBar = createTitleBar();
        this.pane = createPane();

        // Stage configuration
        stage.setTitle("Todo-List");
        stage.initStyle(StageStyle.UNDECORATED);

        // Fix window height to screen height
        Rectangle2D vb = Screen.getPrimary().getVisualBounds();
        stage.setMinHeight(vb.getHeight());
        stage.setMaxHeight(vb.getHeight());

        // Fix window width
        stage.setMinWidth(400);
        stage.setMaxWidth(400);

        VBox root = new VBox(titleBar, pane);
        VBox.setVgrow(pane, Priority.ALWAYS);

        Scene scene = new Scene(root);

        // Load stylesheet
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);

    }

    // Build title bar
    private HBox createTitleBar() {
        HBox bar = new HBox();
        Region spacer = new Region();

        bar.getStyleClass().add("title-bar");
        titleLabel.getStyleClass().add("title-label");
        minimizeButton.getStyleClass().add("title-button");
        closeButton.getStyleClass().add("title-button");

        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().addAll(titleLabel, spacer, minimizeButton, closeButton);

        bar.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });
        bar.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });
        bar.setOnMouseReleased(e -> snapToScreen());

        minimizeButton.setOnAction(e -> stage.setIconified(true));
        closeButton.setOnAction(e -> stage.close());

        return bar;
    }

    // Snap the window to screen edges
    private void snapToScreen() {
        Rectangle2D vb = Screen.getScreensForRectangle(
                stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()
        ).stream().findFirst().orElse(Screen.getPrimary()).getVisualBounds();

        double x = stage.getX();
        double y = stage.getY();
        double w = stage.getWidth();
        double h = stage.getHeight();

        if (Math.abs(x - vb.getMinX()) <= SNAP_THRESHOLD) {
            x = vb.getMinX();
        }
        if (Math.abs((x + w) - vb.getMaxX()) <= SNAP_THRESHOLD) {
            x = vb.getMaxX() - w;
        }
        if (Math.abs(y - vb.getMinY()) <= SNAP_THRESHOLD) {
            y = vb.getMinY();
        }
        if (Math.abs((y + h) - vb.getMaxY()) <= SNAP_THRESHOLD) {
            y = vb.getMaxY() - h;
        }

        // Clamp in case the window is outside the visible bounds.
        if (x < vb.getMinX()) {
            x = vb.getMinX();
        } else if (x + w > vb.getMaxX()) {
            x = vb.getMaxX() - w;
        }
        if (y < vb.getMinY()) {
            y = vb.getMinY();
        } else if (y + h > vb.getMaxY()) {
            y = vb.getMaxY() - h;
        }

        stage.setX(x);
        stage.setY(y);
    }

    // Build main layout
    private GridPane createPane() {
        GridPane pane = new GridPane();

        // Top button bar
        buttonBox.getStyleClass().add("button-bar");
        buttonBox.setFillHeight(true);
        buttonBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(buttonBox, Priority.ALWAYS);

        addButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setMaxWidth(Double.MAX_VALUE);
        doneButton.setMaxWidth(Double.MAX_VALUE);
        storageButton.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.ALWAYS);
        HBox.setHgrow(doneButton, Priority.ALWAYS);
        HBox.setHgrow(storageButton, Priority.ALWAYS);

        buttonBox.getChildren().addAll(addButton, removeButton, doneButton, storageButton);
        pane.add(buttonBox, 0, 0);

        // Task list
        pane.add(taskListView, 0, 1);
        GridPane.setVgrow(taskListView, Priority.ALWAYS);
        GridPane.setHgrow(taskListView, Priority.ALWAYS);
        taskListView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Row constraints
        RowConstraints row0 = new RowConstraints();
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        pane.getRowConstraints().addAll(row0, row1);

        return pane;
    }

    // Show stage
    public void start() {
        stage.show();
    }

    // Accessors
    public ListView<Task> getTaskListView() {
        return taskListView;
    }

    public ListView<Task> getDoneListView() {
        return doneListView;
    }

    public GridPane getPane() {
        return pane;
    }

    public Stage getStage() {
        return stage;
    }
}
