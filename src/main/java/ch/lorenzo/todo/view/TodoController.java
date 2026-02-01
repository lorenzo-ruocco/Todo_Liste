package ch.lorenzo.todo.view;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Properties;
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
import javafx.stage.FileChooser;

public class TodoController {
    // Storage settings
    private static final String SETTINGS_FILE = "settings.properties";
    private static final String STORAGE_PATH_KEY = "storagePath";

    // View state
    private boolean showingDone = false;
    // Listener registry
    private final Set<Task> hookedTasks = Collections.newSetFromMap(new IdentityHashMap<>());

    // Storage path chosen by user
    private File storageFile;

    // Model + View references
    private final TodoModel model;
    private final TodoView view;

    protected TodoController(TodoModel model, TodoView view) {
        this.model = model;
        this.view = view;

        // List bindings
        // Bind open tasks
        view.getTaskListView().setItems(model.getTasks());

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

        
        view.getTaskListView().setCellFactory(lv -> {
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

        // Task listeners
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
        loadStoredPath();
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
                persistStoragePath(storageFile);
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
                    ex.printStackTrace();
                }
            }

        });
    }

    // Hook task state changes
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

    // Load last chosen storage path
    private void loadStoredPath() {
        Path settingsPath = getSettingsPath();
        if (!Files.exists(settingsPath)) {
            return;
        }
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(settingsPath)) {
            props.load(reader);
        } catch (IOException e) {
            return;
        }
        String path = props.getProperty(STORAGE_PATH_KEY);
        if (path == null || path.isBlank()) {
            return;
        }
        storageFile = new File(path);
    }

    // Persist chosen storage path
    private void persistStoragePath(File file) {
        Path settingsPath = getSettingsPath();
        Properties props = new Properties();
        props.setProperty(STORAGE_PATH_KEY, file.getAbsolutePath());
        try {
            Files.createDirectories(settingsPath.getParent());
        } catch (IOException e) {
            return;
        }
        try (Writer writer = Files.newBufferedWriter(settingsPath)) {
            props.store(writer, "Todo storage settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Resolve settings file path
    private Path getSettingsPath() {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null || localAppData.isBlank()) {
            localAppData = System.getProperty("user.home");
        }
        return Path.of(localAppData, "Todo_Liste", SETTINGS_FILE);
    }

    // Init hook called on app start
    public void initFromStorage() {
        if (storageFile == null || !storageFile.exists()) {
            return;
        }
        try {
            String json = Files.readString(storageFile.toPath());
            loadFromJson(json, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Auto-save hook called on app close
    public void autoSaveToStorage() {
        if (storageFile == null) {
            return;
        }
        try {
            List<String> existingDone = new ArrayList<>();
            if (Files.exists(storageFile.toPath())) {
                String existingJson = Files.readString(storageFile.toPath());
                existingDone = extractObjectList(existingJson, "done");
            }
            String json = buildJsonWithExistingDone(existingDone);
            Files.writeString(storageFile.toPath(), json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            // Optional: log or show error
        }
    }

    // Build JSON for open + done tasks
    private String buildJsonWithExistingDone(List<String> existingDone) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"open\": ").append(buildTaskArray(model.getTasks())).append(",\n");
        sb.append("  \"done\": ").append(buildDoneArray(existingDone, model.getDoneTasks())).append("\n");
        sb.append("}\n");
        return sb.toString();
    }

    // Serialize open tasks
    private String buildTaskArray(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\n    ");
            sb.append("{");
            sb.append("\"title\":\"").append(escapeJson(task.titleProperty().get())).append("\",");
            sb.append("\"description\":\"").append(escapeJson(task.descriptionProperty().get())).append("\",");
            LocalDateTime createdAt = task.createdAtProperty().get();
            sb.append("\"createdAt\":");
            if (createdAt != null) {
                sb.append("\"").append(createdAt.toString()).append("\"");
            } else {
                sb.append("null");
            }
            sb.append("}");
        }
        if (!tasks.isEmpty()) {
            sb.append("\n  ");
        }
        sb.append("]");
        return sb.toString();
    }

    // Serialize done tasks with append semantics
    private String buildDoneArray(List<String> existingDone, List<Task> newDone) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (String obj : existingDone) {
            String trimmed = obj.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (!first) {
                sb.append(",");
            }
            sb.append("\n    ").append(trimmed);
            first = false;
        }
        for (Task task : newDone) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\n    ").append(buildTaskObject(task));
            first = false;
        }
        if (!first) {
            sb.append("\n  ");
        }
        sb.append("]");
        return sb.toString();
    }

    // Serialize a single task object
    private String buildTaskObject(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"title\":\"").append(escapeJson(task.titleProperty().get())).append("\",");
        sb.append("\"description\":\"").append(escapeJson(task.descriptionProperty().get())).append("\",");
        LocalDateTime createdAt = task.createdAtProperty().get();
        sb.append("\"createdAt\":");
        if (createdAt != null) {
            sb.append("\"").append(createdAt.toString()).append("\"");
        } else {
            sb.append("null");
        }
        sb.append("}");
        return sb.toString();
    }

    // Parse JSON into tasks
    private void loadFromJson(String json, boolean includeDone) {
        model.getTasks().clear();
        model.getDoneTasks().clear();
        List<String> openObjects = extractObjectList(json, "open");
        for (String obj : openObjects) {
            Task task = buildTaskFromObject(obj, false);
            if (task != null) {
                hookTask(task);
                model.addTask(task);
            }
        }
        if (includeDone) {
            List<String> doneObjects = extractObjectList(json, "done");
            for (String obj : doneObjects) {
                Task task = buildTaskFromObject(obj, true);
                if (task != null) {
                    hookTask(task);
                    model.addDoneTask(task);
                }
            }
        }
    }

    // Build a Task from JSON fields
    private Task buildTaskFromObject(String obj, boolean done) {
        String title = extractStringField(obj, "title");
        String description = extractStringField(obj, "description");
        String createdAt = extractStringField(obj, "createdAt");
        Task task = new Task(title != null ? title : "", description != null ? description : "");
        if (createdAt != null && !createdAt.isBlank()) {
            try {
                task.createdAtProperty().set(LocalDateTime.parse(createdAt));
            } catch (Exception ignored) {
                // Ignore parse errors
            }
        }
        task.completedProperty().set(done);
        return task;
    }

    // Extract object list from a JSON array
    private List<String> extractObjectList(String json, String arrayName) {
        List<String> objects = new ArrayList<>();
        int arrayStart = findArrayStart(json, arrayName);
        if (arrayStart < 0) {
            return objects;
        }
        int arrayEnd = findMatchingBracket(json, arrayStart);
        if (arrayEnd < 0) {
            return objects;
        }
        String arrayBody = json.substring(arrayStart + 1, arrayEnd);
        int i = 0;
        while (i < arrayBody.length()) {
            int objStart = findNextChar(arrayBody, i, '{');
            if (objStart < 0) {
                break;
            }
            int objEnd = findMatchingBrace(arrayBody, objStart);
            if (objEnd < 0) {
                break;
            }
            objects.add(arrayBody.substring(objStart, objEnd + 1));
            i = objEnd + 1;
        }
        return objects;
    }

    // Find the start of a named array
    private int findArrayStart(String json, String arrayName) {
        String key = "\"" + arrayName + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) {
            return -1;
        }
        int colon = json.indexOf(":", idx + key.length());
        if (colon < 0) {
            return -1;
        }
        return findNextChar(json, colon + 1, '[');
    }

    // Match [] scope
    private int findMatchingBracket(String s, int start) {
        return findMatching(s, start, '[', ']');
    }

    // Match {} scope
    private int findMatchingBrace(String s, int start) {
        return findMatching(s, start, '{', '}');
    }

    // Match nested scopes while ignoring strings
    private int findMatching(String s, int start, char open, char close) {
        boolean inString = false;
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && !isEscaped(s, i)) {
                inString = !inString;
            }
            if (inString) {
                continue;
            }
            if (c == open) {
                depth++;
            } else if (c == close) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    // Find next occurrence of a char outside strings
    private int findNextChar(String s, int start, char target) {
        boolean inString = false;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && !isEscaped(s, i)) {
                inString = !inString;
            }
            if (!inString && c == target) {
                return i;
            }
        }
        return -1;
    }

    // Extract a string field from a JSON object
    private String extractStringField(String obj, String field) {
        String key = "\"" + field + "\"";
        int idx = obj.indexOf(key);
        if (idx < 0) {
            return null;
        }
        int colon = obj.indexOf(":", idx + key.length());
        if (colon < 0) {
            return null;
        }
        int i = colon + 1;
        while (i < obj.length() && Character.isWhitespace(obj.charAt(i))) {
            i++;
        }
        if (i >= obj.length()) {
            return null;
        }
        if (obj.startsWith("null", i)) {
            return null;
        }
        if (obj.charAt(i) != '"') {
            return null;
        }
        int start = i + 1;
        StringBuilder out = new StringBuilder();
        for (int p = start; p < obj.length(); p++) {
            char c = obj.charAt(p);
            if (c == '"' && !isEscaped(obj, p)) {
                return out.toString();
            }
            if (c == '\\' && p + 1 < obj.length()) {
                char n = obj.charAt(p + 1);
                switch (n) {
                    case 'n' -> out.append('\n');
                    case 'r' -> out.append('\r');
                    case 't' -> out.append('\t');
                    case '"' -> out.append('"');
                    case '\\' -> out.append('\\');
                    default -> out.append(n);
                }
                p++;
            } else {
                out.append(c);
            }
        }
        return null;
    }

    // Check if a quote is escaped
    private boolean isEscaped(String s, int index) {
        int count = 0;
        for (int i = index - 1; i >= 0 && s.charAt(i) == '\\'; i--) {
            count++;
        }
        return (count % 2) == 1;
    }

    // Escape JSON string content
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
