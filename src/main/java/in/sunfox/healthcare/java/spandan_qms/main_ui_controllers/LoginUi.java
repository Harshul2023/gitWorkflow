package in.sunfox.healthcare.java.spandan_qms.main_ui_controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import in.sunfox.healthcare.java.spandan_qms.database_service.DatabaseUtility;
import in.sunfox.healthcare.java.spandan_qms.database_service.OnDataReceiveListener;
import in.sunfox.healthcare.java.spandan_qms.TestMain;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginUi implements Initializable {
    @FXML
    private Text alreadyRegisterLogin;

    @FXML
    private BorderPane anchorPane;
    @FXML
    private ProgressBar progressBarOfLoginBox;
    @FXML
    private ProgressBar progressBarOfRegistrationBox;

    @FXML
    private TextField registrationBoxConfirmPasswordTextField;

    @FXML
    private TextField loginBoxEmailTextField;

    @FXML
    private TextField registrationBoxEmailTextField;
    @FXML
    private Button developerModeButton;

    @FXML
    private Label errorLabelOfLoginBox;
    @FXML
    private Label errorLabelInRegistrationBox;

    @FXML
    private VBox loginBox;

    @FXML
    private TextField registrationBoxNameTextField;

    @FXML
    private PasswordField loginBoxPasswordTextField;

    @FXML
    private TextField registrationBoxPasswordTextField;

    @FXML
    private TextField registrationBoxPhoneNumberTextField;

    @FXML
    private VBox registrationBox;
    Stage primaryStage;
    @FXML
    private Label showpasswordLabel;

    @FXML
    private Button loginBoxLoginButton;

    public void writeLoginToFile(String usernameLogin) throws IOException {
        // Get the path to the resource file
        String resourcePath = "LoginDetails.txt";
        InputStream inputStream = LoginUi.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            System.out.println("Resource not found: " + resourcePath);
            return;
        }

        // Read existing lines from the resource file
        List<String> allLines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            allLines = Files.readAllLines(Paths.get(resourcePath));
        }

        // Check if the usernameLogin is already present
        if (!allLines.contains(usernameLogin)) {
            // Use FileWriter with append set to true
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resourcePath, true))) {
                writer.write(usernameLogin);
                writer.newLine(); // Add a newline for each entry
            }
            allLines.add(usernameLogin); // Add the new entry to the list for printing later
        }
    }

    @FXML
    void login() {
        loginBoxLoginButton.setDisable(true);
        String emailString = loginBoxEmailTextField.getText().trim();
        String passwordString = loginBoxPasswordTextField.getText().trim();
//        emailString = emailString.toLowerCase();
        String finalEmailString = emailString.toLowerCase();

        OnDataReceiveListener<ArrayList<Integer>> onDataReceiveListener = new OnDataReceiveListener<ArrayList<Integer>>() {
            @Override
            public void onDataReceived(ArrayList<Integer> data) {

                Platform.runLater(() -> {
                    progressBarOfLoginBox.setVisible(false);
                    try {
//                        try {
//                            writeLoginToFile(finalEmailString);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
                        primaryStage = (Stage) anchorPane.getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("MainUiSpandanLegacy.fxml"));
                        Parent loginRoot = fxmlLoader.load();
                        Scene loginScene = new Scene(loginRoot);
                        loginScene.getStylesheets().add("style.css");

//                        loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
                        MainUiSpandanLegacy controller = fxmlLoader.getController();
                        controller.setUsername(finalEmailString, Integer.parseInt(String.valueOf(data.get(0))), Integer.toBinaryString((data.get(1))));
                        Stage mainUiStage = new Stage();
                        mainUiStage.setScene(loginScene);
                        mainUiStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                        mainUiStage.setMaximized(true);
                        loginScene.getStylesheets().add("style.css");

                        mainUiStage.setTitle("Spandan QMS System");

//                        loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
                        mainUiStage.show();
                        mainUiStage.setOnCloseRequest(e -> {
                            OnDataReceiveListener<String> onDataReceiveListener1 = new OnDataReceiveListener<String>() {
                                @Override
                                public void onDataReceived(String data1) {
                                    // Set the flag here

                                    mainUiStage.show();
                                    Platform.exit();
                                    System.exit(0);
                                }

                                @Override
                                public void onDataReceiveError(String errorMsg) {

                                }
                            };
                            DatabaseUtility.clearToken(onDataReceiveListener1);

                        });
                        primaryStage.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onDataReceiveError(String errorMsg) {
                isProcessing=false;
                loginBoxLoginButton.setDisable(false);
                progressBarOfLoginBox.setVisible(false);
                Platform.runLater(() -> errorLabelOfLoginBox.setText(errorMsg));


            }
        };

        if (!isValidEmail(emailString)) {
            isProcessing=false;
            errorLabelOfLoginBox.setText("Please Enter Valid Email Id");
            loginBoxEmailTextField.setStyle("-fx-border-color: red; -fx-border-width: 0 0 1 0;");
            loginBoxEmailTextField.setFocusTraversable(true);
            loginBoxLoginButton.setDisable(false);
        }
        if (!emailString.contains("@sunfox.in")) {
            isProcessing=false;
            loginBoxEmailTextField.setStyle("-fx-border-color: red; -fx-border-width: 0 0 1 0;");
            loginBoxEmailTextField.setFocusTraversable(true);
            errorLabelOfLoginBox.setText("Enter Sunfox Email Id");
            loginBoxLoginButton.setDisable(false);
        } else if (passwordString.equals("")) {
            isProcessing=false;
            loginBoxEmailTextField.setStyle(null);
            loginBoxPasswordTextField.setStyle("-fx-border-color : red");
            loginBoxPasswordTextField.setFocusTraversable(true);
            errorLabelOfLoginBox.setText("Please Enter Password");
            loginBoxLoginButton.setDisable(false);
        } else {
            errorLabelOfLoginBox.setText("");
            loginBoxPasswordTextField.setStyle(null);
            loginBoxEmailTextField.setStyle(null);
            Thread thread = new Thread(() -> {
                DatabaseUtility.validateLogin(emailString, passwordString, onDataReceiveListener);
            });
            thread.start();
            progressBarOfLoginBox.setVisible(true);
        }
    }


    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @FXML
    void newUserSignUp() {
        loginBox.setVisible(false);
        registrationBox.setVisible(true);
    }

    @FXML
    private FontAwesomeIconView showPasswordCheckbox;

    private boolean passwordVisible = false;

    private void addTextLimiter(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength || (newValue.contains(" "))) {
                textField.setText(oldValue);
            }
        });
    }

    private List<String> loadUsernamesFromFile() throws IOException {
        // Get the path to the resource file
        String resourcePath = "LoginDetails.txt";
        InputStream inputStream = LoginUi.class.getClassLoader().getResourceAsStream(resourcePath);

        // Check if the resource exists
        if (inputStream == null) {
            System.out.println("Resource not found: " + resourcePath);
            return new ArrayList<>(); // Return an empty list if the resource is not found
        }

        // Read lines from the resource file
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }

    @FXML
    private ListView<String> suggestionsListView;
    boolean isProcessing = false;

    @FXML
    public void minimizeWindow(){
        primaryStage = (Stage) anchorPane.getScene().getWindow();
        primaryStage.setIconified(true);

    }
    @FXML
    private Button registerHere;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String env = System.getenv("databaseEnvironment");
        System.out.println("env variable value: " + env);
        if ("debug".equalsIgnoreCase(env)) {
            developerModeButton.setDisable(false);
            registerHere.setDisable(false);
        } else if(env == null ||env.equals("production")) {
            registerHere.setDisable(true);
            developerModeButton.setDisable(true);
        }
        // Add this variable to track if a request is being processed

// Event handlers for Enter key presses
        loginBoxPasswordTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !isProcessing) {
                isProcessing = true; // Set to true to prevent multiple submissions
                login();
            }
        });

        loginBoxEmailTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !isProcessing) {
                isProcessing = true; // Set to true to prevent multiple submissions
                login();
            }
        });

        registrationBoxPasswordTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !isProcessing) {
                isProcessing = true; // Set to true to prevent multiple submissions
                register();
            }
        });

        registrationBoxEmailTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !isProcessing) {
                isProcessing = true; // Set to true to prevent multiple submissions
                register();
            }
        });

        List<String> usernames;
        try {
            usernames = loadUsernamesFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loginBoxEmailTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            suggestionsListView.getItems().clear();

            if (!newValue.isEmpty()) {
                usernames.stream()
                        .filter(username -> username.startsWith(newValue))
                        .forEach(username -> suggestionsListView.getItems().add(username));
            }

            // Show or hide the ListView based on whether there are suggestions
            suggestionsListView.setVisible(!suggestionsListView.getItems().isEmpty());
        });

        suggestionsListView.setOnMouseClicked(event -> {
            String selectedItem = suggestionsListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                loginBoxEmailTextField.setText(selectedItem);
                suggestionsListView.setVisible(false);
            }
        });

        int emailMaxLength = 50;
        int passwordMaxLength = 50;

        addTextLimiter(loginBoxEmailTextField, emailMaxLength);
        addTextLimiter(loginBoxPasswordTextField, passwordMaxLength);
        addTextLimiter(registrationBoxEmailTextField, emailMaxLength);
        addTextLimiter(registrationBoxPasswordTextField, passwordMaxLength);
        addTextLimiter(registrationBoxNameTextField, emailMaxLength);
        addTextLimiter(registrationBoxPhoneNumberTextField, 10);
        addTextLimiter(registrationBoxConfirmPasswordTextField, passwordMaxLength);
        showPasswordCheckbox.setOnMouseClicked(event -> {
            if (passwordVisible) {
                showPasswordCheckbox.setGlyphName("EYE_SLASH");
                passwordVisible = false;
                showpasswordLabel.setText("");
            } else {
                showPasswordCheckbox.setGlyphName("EYE");
                passwordVisible = true;
                showpasswordLabel.setText(loginBoxPasswordTextField.getText());
            }
        });
//

        loginBoxPasswordTextField.textProperty
                ().addListener((observable, oldValue, newValue) -> {
            if (showPasswordCheckbox.getGlyphName().equals("EYE")) {
                showpasswordLabel.setText(newValue);
            }


        });
        loginBox.setVisible(true);
//        loginBox.setAlignment(Pos.CENTER);
        alreadyRegisterLogin.setOnMouseClicked(event -> {
            registrationBox.setVisible(false);
            loginBox.setVisible(true);
        });
    }

    public void closeWindow() {


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // Use tasklist to list all running processes
                ProcessBuilder listProcesses = new ProcessBuilder("tasklist");
                Process listProcess = listProcesses.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    // Check if the process is a Command Prompt window (cmd.exe)
                    if (line.contains("SpandanApplication")) {
                        // Extract the PID (Process ID) and terminate the process and its subprocesses
                        String[] parts = line.split(" ");
                        String pid = parts[1];
                        ProcessBuilder killProcess = new ProcessBuilder("taskkill", "/F", "/T", "/PID", pid);
                        killProcess.start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle this exception appropriately
            }
        }));

        Platform.exit();
        System.exit(0);
    }

    public void developerModeAction() {

        try {
            primaryStage = (Stage) anchorPane.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("MainUiSpandanLegacy.fxml"));
            Parent loginRoot = fxmlLoader.load();
            Scene loginScene = new Scene(loginRoot);
//            loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            MainUiSpandanLegacy controller = fxmlLoader.getController();
            controller.setUsername("DEVELOPER", 0, "111111");
            // Create a new Stage for the login scene
            Stage mainUiStage = new Stage();
            mainUiStage.setScene(loginScene);
            mainUiStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
            mainUiStage.setTitle("Spandan QMS System");
            // Maximize the loginStage
            mainUiStage.setMaximized(true);
            loginScene.getStylesheets().add("style.css");

//            loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            mainUiStage.show();
            mainUiStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    Platform.exit();
                    System.exit(0);
                }

            });


            primaryStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register() {
        String registrationBoxFieldName = registrationBoxNameTextField.getText().trim();
        String registrationBoxFieldEmail = registrationBoxEmailTextField.getText().trim();
        String registrationBoxFieldPhoneNumber = registrationBoxPhoneNumberTextField.getText().trim();
        String registrationBoxFieldPassword = registrationBoxPasswordTextField.getText().trim();
        String registrationBocFieldConfirmPassword = registrationBoxConfirmPasswordTextField.getText().trim();
        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
            @Override
            public void onDataReceived(String data) {
                progressBarOfRegistrationBox.setVisible(false);
                registrationBox.setVisible(false);
                loginBox.setVisible(true);
            }
            @Override
            public void onDataReceiveError(String errorMsg) {
                Platform.runLater(() -> {
                    progressBarOfRegistrationBox.setVisible(false);
                    errorLabelInRegistrationBox.setText(errorMsg);
                });
            }
        };
        if (!registrationBoxFieldName.equals("")) {
            errorLabelInRegistrationBox.setText("");
            registrationBoxNameTextField.setStyle(null);
            if (isValidEmail(registrationBoxEmailTextField.getText())) {
                if (!registrationBoxEmailTextField.getText().contains("@sunfox.in")) {
                    errorLabelInRegistrationBox.setText("Enter Sunfox Email Id");
                } else {
                    errorLabelInRegistrationBox.setText("");
                    registrationBoxEmailTextField.setStyle(null);
                    if (registrationBoxFieldPhoneNumber.length() == 10) {
                        registrationBoxPhoneNumberTextField.setStyle(null);
                        errorLabelInRegistrationBox.setText("");
                        if (registrationBoxFieldPassword.equals(registrationBocFieldConfirmPassword) && !registrationBoxFieldPassword.equals("")) {
                            registrationBoxConfirmPasswordTextField.setStyle(null);
                            errorLabelInRegistrationBox.setText("");
                            Thread thread = new Thread(() -> {
                                DatabaseUtility.newUserSignUp(registrationBoxFieldName, registrationBoxFieldEmail, registrationBoxFieldPhoneNumber, registrationBoxFieldPassword, onDataReceiveListener);
                            });
                            progressBarOfRegistrationBox.setVisible(true);
                            thread.start();


                        } else {
                            registrationBoxConfirmPasswordTextField.setStyle("-fx-border-color : red");
                            errorLabelInRegistrationBox.setText("Password do not match");
                            if (registrationBoxFieldPassword.equals("")) {
                                errorLabelInRegistrationBox.setText("Enter password");
                            }
                        }
                    } else {
                        Platform.runLater(() -> {
                            registrationBoxPhoneNumberTextField.setStyle("-fx-border-color : red");
                            errorLabelInRegistrationBox.setText("Enter Correct Phone Number");
                        });
                    }
                }
            } else {
                Platform.runLater(() -> {
                    registrationBoxEmailTextField.setStyle("-fx-border-color : red");
                    errorLabelInRegistrationBox.setText("Enter Correct Email");
                });

            }
        } else {
            errorLabelInRegistrationBox.setText("Enter Correct Name");
            registrationBoxNameTextField.setStyle("-fx-border-color : red");
        }
    }

}

