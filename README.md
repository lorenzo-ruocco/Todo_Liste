# Todo_Liste

Simple JavaFX todo app with open and done tasks, JSON storage, and a custom UI.

## Requirements
- Java 21+
- Maven 3.9+

## Run (dev)
```powershell
mvn javafx:run
```

## Storage
- Choose a JSON file via the **Storage** button.
- The chosen path is saved in `%LOCALAPPDATA%\Todo_Liste\settings.properties`.
- On start, open tasks are loaded from the JSON file.
- On exit, open tasks overwrite `open`, done tasks append to `done`.
