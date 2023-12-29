package in.sunfox.healthcare.java.spandan_qms;

import in.sunfoxhealthcare.java.commons.serialcommunicationmodule.SpandanUsbCommunication;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.util.Objects;
import java.util.prefs.Preferences;

import static in.sunfox.healthcare.java.spandan_qms.utilitites.Constants.environment;

public class TestMain extends Application {

    public boolean exeMode() {
        boolean isAdmin = false;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "net", "session");
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                isAdmin = (exitCode == 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        environment = System.getenv("databaseEnvironment");
        System.out.println("environment variable value: " + environment);
        if ("debug".equalsIgnoreCase(environment)) {
            return true;
        } else if (environment == null || environment.equals("production")) {
            return isAdmin;
        }
        return true;
    }

    @Override
    public void start(Stage primaryStage) {
        if (!exeMode()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Admin Privileges Required");
            alert.setHeaderText(null);
            alert.setContentText("This application requires administrative privileges.\n"
                    + "Please close the application and run it as an administrator.");
            alert.showAndWait();
            Platform.exit();
            System.exit(0);
        } else {
//            primaryStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
            Image splashImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("splashScreen.png")));
            StackPane root = new StackPane();
            ImageView imageView = new ImageView(splashImage);
            root.getChildren().add(imageView);
            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
//
            imageView.setOpacity(0);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.75), imageView);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
//        primaryStage.setMaximized(true);
            primaryStage.show();


            Stage loginStage = new Stage();
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("LoginUi.fxml"));
                    Parent loginRoot = fxmlLoader.load();
                    Scene loginScene = new Scene(loginRoot);
                    loginStage.setScene(loginScene);
                    loginStage.initStyle(StageStyle.UNDECORATED);
                    loginStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
//                loginStage.setMaximized(true);
                    loginStage.setTitle("Sunfox Login");
//                loginStage.setFullScreen(true);

//                loginStage.setFullScreenExitHint("");
                    loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                    loginStage.show();
                    primaryStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            pause.play();
            loginStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    Platform.exit();
                    System.exit(0);
                }
            });

            primaryStage.setOnCloseRequest(event -> {
                try {
                    // Perform cleanup operations
                    SpandanUsbCommunication.sendCommand("stop", new Label());
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle this exception appropriately
                }

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        // Use tasklist to list all running processes
                        ProcessBuilder listProcesses = new ProcessBuilder("tasklist");
                        Process listProcess = listProcesses.start();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
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
            });


        }
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Preferences preferences = Preferences.userRoot().node("com.sunfox.spandan-desktop");
        System.out.println(preferences.get("jwt_token", null));

        String appDirectory = System.getProperty("user.dir");
        String stLinkUtilitySource = appDirectory + File.separator + "app" + File.separator + "ST_LINK_Utility";
        String stLinkUtilityTarget = appDirectory + File.separator + "ST_LINK_Utility";
        String SPLGfirmwareSource = appDirectory + File.separator + "app" + File.separator + "SPLGFirmware";
        String SPLGfirmwareTarget = appDirectory + File.separator + "SPLGFirmware";
        String SPNEfirmwareSource = appDirectory + File.separator + "app" + File.separator + "SPNEFirmware";
        String SPNEfirmwareTarget = appDirectory + File.separator + "SPNEFirmware";
        String SPPRfirmwareSource = appDirectory + File.separator + "app" + File.separator + "SPPRFirmware";
        String SPPRfirmwareTarget = appDirectory + File.separator + "SPPRFirmware";

        File stLinkUtilityTargetDir = new File(stLinkUtilityTarget);
        File SPLGfirmwareTargetDir = new File(SPLGfirmwareTarget);
        File SPNEfirmwareTargetDir = new File(SPNEfirmwareTarget);
        File SPPRfirmwareTargetDir = new File(SPPRfirmwareTarget);


        Thread thread = new Thread(() -> {
            if (!stLinkUtilityTargetDir.exists()) {
                try {
                    stLinkUtilityTargetDir.mkdirs();
                    Process processStLinkUtility = Runtime.getRuntime().exec("cmd /c xcopy \"" + stLinkUtilitySource + "\" \"" + stLinkUtilityTarget + "\" /s /e /i /h");
                    processStLinkUtility.waitFor();
                    processStLinkUtility.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread1 = new Thread(() -> {
            if (!SPLGfirmwareTargetDir.exists()) {
                try {
                    SPLGfirmwareTargetDir.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            if (!SPNEfirmwareTargetDir.exists()) {
                try {
                    SPNEfirmwareTargetDir.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread4 = new Thread(() -> {
            if (!SPPRfirmwareTargetDir.exists()) {
                try {
                    SPPRfirmwareTargetDir.mkdirs();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        if (!"debug".equalsIgnoreCase(environment)) {
            thread1.start();
            thread2.start();
            thread4.start();
        }
        launch(args);
    }


}

//jpackage -t exe --description "SpandanApplication" --app-version 1.3 --input . --icon "C:\Users\Sunfo\Downloads\qms_logo.ico" --dest . --name "SpandanApplication" --main-jar Spandan-Desktop-Application-1.0-SNAPSHOT-all.jar --module-path javafx --add-modules javafx.controls,java.naming,javafx.graphics,java.sql,javafx.fxml,javafx.base,java.logging,jdk.crypto.ec --win-shortcut --win-console