package in.sunfox.healthcare.java.spandan_qms.device_fLashing_and_configuration_classes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import in.sunfox.healthcare.java.spandan_qms.database_service.DatabaseUtility;
import in.sunfox.healthcare.java.spandan_qms.database_service.OnDataReceiveListener;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static in.sunfox.healthcare.java.spandan_qms.utilitites.Constants.USER_WORKING_DIRECTORY;
import static javafx.scene.paint.Color.*;

public class FlashingNewDeviceClass {
    public void addDevice(BorderPane mainBorderPane, List<Alert> openDialogs) {
        DialogPane dialogPane = new DialogPane();
        BoxBlur boxBlur = new BoxBlur(3, 3, 7);
        Platform.runLater(() -> {
            mainBorderPane.setEffect(boxBlur);
        });
        VBox contentPane = new VBox();
        contentPane.setStyle("-fx-background-color:white");
        dialogPane.setContent(contentPane);
        BorderPane borderPane = new BorderPane();
        FontAwesomeIconView closeButton = new FontAwesomeIconView();
        closeButton.setSize(String.valueOf(15));
        closeButton.setFill(Paint.valueOf("#ff461e"));
        closeButton.setGlyphName("CLOSE");
        BorderPane.setMargin(closeButton, new Insets(5, 0, 0, 0));
        Label heading = new Label("Select Device Variant");
        heading.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD, 23));
        borderPane.setCenter(heading);
        borderPane.setRight(closeButton);
        contentPane.getChildren().add(borderPane);


        HBox hBox = new HBox();
        hBox.setPrefWidth(800);
        BorderPane borderPane1 = new BorderPane();
        hBox.setSpacing(20);
        HBox.setHgrow(borderPane1, Priority.ALWAYS); // Set vertical grow constraint
        borderPane1.setPrefSize(180, 270); // Set the preferred size for borderPane1
        borderPane1.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
        VBox spandanLegacyVbox = new VBox();
        spandanLegacyVbox.setAlignment(Pos.CENTER);
        spandanLegacyVbox.setSpacing(20);
        ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/spandanLegacy.png");
        Label SPLGlabel = new Label("Spandan Legacy");
        SPLGlabel.setFont(Font.font("Manrope", FontWeight.findByWeight(18), 18));
        CheckBox SPLGcheckBox = new CheckBox();
//        SPLGcheckBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        SPLGcheckBox.getStylesheets().add("style.css");

        SPLGcheckBox.setDisable(true);
        spandanLegacyVbox.getChildren().addAll(imageView, SPLGlabel, SPLGcheckBox);
        borderPane1.setCenter(spandanLegacyVbox);

        BorderPane borderPane2 = new BorderPane();
        HBox.setHgrow(borderPane2, Priority.ALWAYS); // Set vertical grow constraint
        borderPane2.setPrefSize(180, 270); // Set the preferred size for borderPane1
        borderPane2.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
        VBox spandanNeoVbox = new VBox();
        spandanNeoVbox.setAlignment(Pos.CENTER);
        spandanNeoVbox.setSpacing(20);
        ImageView imageView2 = new ImageView("in/sunfox/healthcare/java/spandan_qms/spandanNeo.png");
        Label SPNElabel = new Label("Spandan Neo");
        SPNElabel.setFont(Font.font("Manrope", FontWeight.findByWeight(18), 18));
        CheckBox SPNEcheckBox = new CheckBox();
//        SPNEcheckBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        SPNEcheckBox.getStylesheets().add("style.css");
        SPNEcheckBox.setDisable(true);
        spandanNeoVbox.getChildren().addAll(imageView2, SPNElabel, SPNEcheckBox);
        borderPane2.setCenter(spandanNeoVbox);

        BorderPane borderPane3 = new BorderPane();
        HBox.setHgrow(borderPane3, Priority.ALWAYS); // Set vertical grow constraint
        borderPane3.setPrefSize(180, 270); // Set the preferred size for borderPane1
        borderPane3.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
//        ImageView imageView2 =  new ImageView("com/example/demo/spandanNeo.png");
        VBox spandanProVbox = new VBox();
        spandanProVbox.setAlignment(Pos.CENTER);
        spandanProVbox.setSpacing(20);
        ImageView imageView3 = new ImageView("in/sunfox/healthcare/java/spandan_qms/a1.png");
        Label SPPrlabel = new Label("Spandan Pro");
        SPPrlabel.setFont(Font.font("Manrope", FontWeight.findByWeight(18), 18));
        CheckBox SPPrcheckBox = new CheckBox();
//        SPPrcheckBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        SPPrcheckBox.getStylesheets().add("style.css");
        SPPrcheckBox.setDisable(true);
        spandanProVbox.getChildren().addAll(imageView3, SPPrlabel, SPPrcheckBox);
        borderPane3.setCenter(spandanProVbox);


        Button confirmBtn = new Button("Confirm");
        confirmBtn.setPrefWidth(470);
        confirmBtn.setPrefHeight(100);
        confirmBtn.setStyle("-fx-background-color: #F15056;");
        confirmBtn.setTextFill(WHITE);
        confirmBtn.setVisible(false);
        VBox.setMargin(hBox, new Insets(30, 15, 0, 15));
        hBox.getChildren().addAll(borderPane1, borderPane2, borderPane3);
        borderPane1.setCursor(Cursor.HAND);
        borderPane2.setCursor(Cursor.HAND);
        scaleTransition(borderPane1, borderPane2, borderPane3);
        ScrollPane scrollPane = new ScrollPane(null);
        scrollPane.setContent(hBox);

        borderPane2.setCursor(Cursor.HAND);
        borderPane3.setCursor(Cursor.HAND);

        confirmBtn.setCursor(Cursor.HAND);
        borderPane3.setOnMouseClicked(event -> {
            scaleTransition(borderPane1, borderPane2);
            borderPane3.setOnMouseEntered(null);
            borderPane3.setOnMouseExited(null);
            scrollPane.setHvalue(1);
            borderPane3.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px;-fx-border-color:#49B23E;-fx-border-radius:10px;-fx-borderwidth:5 5 5 5");
            SPPrcheckBox.setSelected(true);
            borderPane2.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
            borderPane1.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
            SPNEcheckBox.setSelected(false);
            SPLGcheckBox.setSelected(false);
            confirmBtn.setVisible(true);
        });
        borderPane1.setOnMouseClicked(event -> {
            borderPane1.setOnMouseEntered(null);
            borderPane1.setOnMouseExited(null);
            scaleTransition(borderPane2, borderPane3);
            scrollPane.setHvalue(0);
            borderPane1.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px;-fx-border-color:#49B23E;-fx-border-radius:10px;-fx-borderwidth:5 5 5 5");
            SPLGcheckBox.setSelected(true);
            borderPane2.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
            borderPane3.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
            SPNEcheckBox.setSelected(false);
            SPPrcheckBox.setSelected(false);
            confirmBtn.setVisible(true);
        });
        borderPane2.setOnMouseClicked(event -> {
            borderPane2.setOnMouseEntered(null);
            borderPane3.setOnMouseExited(null);
            scaleTransition(borderPane2, borderPane3);
            scrollPane.setHvalue(0.5);
            borderPane2.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px;-fx-border-color:#49B23E;-fx-border-radius:10px;-fx-borderwidth:5 5 5 5");
            SPNEcheckBox.setSelected(true);
            borderPane1.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
            borderPane3.setStyle("-fx-background-color: #F2F9FF;-fx-background-radius: 10px");
            SPLGcheckBox.setSelected(false);
            SPPrcheckBox.setSelected(false);
            confirmBtn.setVisible(true);
        });
        confirmBtn.setOnAction(event -> {
            if (SPLGcheckBox.isSelected()) {
                Thread thread = new Thread(() -> {
                    Platform.runLater(() -> {
//                        mainBorderPane.setEffect(null);
                        Stage stage = (Stage) dialogPane.getScene().getWindow();
                        stage.close();
                    });
                });
                thread.start();
                spLgAction(mainBorderPane, openDialogs);
            } else if (SPNEcheckBox.isSelected()) {
                Thread thread = new Thread(() -> {
                    Platform.runLater(() -> {
                        Stage stage = (Stage) dialogPane.getScene().getWindow();
                        stage.close();
                    });
                });
                thread.start();
                spNeAction(mainBorderPane, openDialogs);
            } else if (SPPrcheckBox.isSelected()) {
                Thread thread = new Thread(() -> {
                    Platform.runLater(() -> {
                        Stage stage = (Stage) dialogPane.getScene().getWindow();
                        stage.close();
                    });
                });
                thread.start();
                spPrAction(mainBorderPane, openDialogs);

            }
        });

        VBox.setMargin(confirmBtn, new Insets(20, 15, 0, 15));

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStylesheets().add("style.css");
//        scrollPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        HBox.setMargin(borderPane1, new Insets(20, 10, 5, 20));
        HBox.setMargin(borderPane2, new Insets(20, 10, 5, 0));
        HBox.setMargin(borderPane3, new Insets(20, 10, 5, 0));
        hBox.setPrefHeight(250);
        scrollPane.setPrefHeight(330);
        scrollPane.setStyle("-fx-background-color:white");
        hBox.setStyle("-fx-background-color:white");

        contentPane.getChildren().addAll(scrollPane, confirmBtn);
        closeButton.setOnMouseClicked(e -> {
            mainBorderPane.setEffect(null);
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.close();
        });
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(2);
        dropShadow.setSpread(0.2);
        dialogPane.setEffect(dropShadow);
        dialogPane.setStyle("-fx-background-color:white");
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(2);
        innerShadow.setChoke(0.2);
        dialogPane.setEffect(innerShadow);
        Alert customDialog = new Alert(Alert.AlertType.NONE);
        customDialog.setDialogPane(dialogPane);
        customDialog.initStyle(StageStyle.TRANSPARENT);
        dialogPane.setPrefWidth(500); // Set your desired width
        dialogPane.setPrefHeight(400); // Set your desired height;
        openDialogs.add(customDialog);
        customDialog.showAndWait();
    }

    private void scaleTransition(BorderPane... borderPane) {
        for (BorderPane b : borderPane) {
            ScaleTransition scaleTransition2 = new ScaleTransition(Duration.millis(200), b);
            scaleTransition2.setFromX(0.85);
            scaleTransition2.setFromY(0.85);
            scaleTransition2.setToX(1.0); // Increase the X scale by 20%
            scaleTransition2.setToY(1.0);
            // Play the scale transition when the mouse enters
            scaleTransition2.playFromStart();
            b.setOnMouseEntered(event -> {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), b);
                scaleTransition.setFromX(1.0);
                scaleTransition.setFromY(1.0);
                scaleTransition.setToX(0.85); // Increase the X scale by 20%
                scaleTransition.setToY(0.85);
                // Play the scale transition when the mouse enters
                scaleTransition.playFromStart();
            });

            b.setOnMouseExited(event -> {
                // Reverse the scale transition when the mouse exits
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), b);
                scaleTransition.setFromX(0.85);
                scaleTransition.setFromY(0.85);
                scaleTransition.setToX(1.0); // Increase the X scale by 20%
                scaleTransition.setToY(1.0);
                // Play the scale transition when the mouse enters
                scaleTransition.playFromStart();
            });
        }

    }

    public void spNeAction(BorderPane mainBorderPane, List<Alert> openDialogs) {
        final Boolean[] flag = {true};

        DialogPane dialogPane = new DialogPane();
        BoxBlur boxBlur = new BoxBlur(3, 3, 7);
        Platform.runLater(() -> {
            mainBorderPane.setEffect(boxBlur);
        });

        VBox contentPane = new VBox();


        contentPane.setSpacing(10);
        contentPane.setAlignment(Pos.CENTER);
        dialogPane.getStylesheets().add("style.css");
//        dialogPane.getStylesheets().add(
//                Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        BorderPane borderPane = new BorderPane();
        VBox.setMargin(borderPane, new Insets(-30, 10, 10, 10));
        Text text = new Text("Create New Device");
        text.setFont(Font.font("Times New Roman", 20));
        borderPane.setLeft(text);
        Button closeButton = new Button("X");
        borderPane.setRight(closeButton);
        contentPane.getChildren().add(borderPane);
        ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/20945402 1.png");
        Text text1 = new Text("Please Connect the Device In St-Link Mode");
        contentPane.getChildren().add(imageView);
        contentPane.getChildren().add(text1);
        dialogPane.setContent(contentPane);

        Thread thread = new Thread(() -> {
            while (flag[0]) {
                ProcessBuilder stLinkProcessBuilder = new ProcessBuilder("st_link_utility" +
                        "\\ST-LINK_CLI.exe", "-P");
                stLinkProcessBuilder.redirectErrorStream(true);
                Process stLinkProcess = null;
                try {
                    stLinkProcess = stLinkProcessBuilder.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                BufferedReader stLinkReader = new BufferedReader(new InputStreamReader(stLinkProcess.getInputStream()));
                String line;
                boolean deviceConnected = false;
                while (true) {
                    try {
                        if ((line = stLinkReader.readLine()) == null) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (line.contains(" Connected via SWD") || line.contains("Device family :STM32F301x4-x6-x8/F302x4-x6-x8/F318xx")) {
                        deviceConnected = true;
                        break;
                    }
                }
                if (deviceConnected) {
                    System.out.println("Device is connected to ST-LINK.");
                    Platform.runLater(() -> {
                        text1.setText("Device is connected to ST-LINK.");
                    });
                    break;
                } else {
                    Platform.runLater(() -> {
                        System.out.println("No device is connected to ST-LINK.");
                        text1.setText("No device is connected to ST-LINK.");
                    });
                }
            }
            Platform.runLater(() -> {
                contentPane.setAlignment(Pos.CENTER_LEFT);
                BorderPane borderPane1 = new BorderPane();
                ImageView vector = new ImageView("in/sunfox/healthcare/java/spandan_qms/Vector (1).png");
                vector.setFitWidth(20);
                vector.setFitHeight(20);
                borderPane1.setLeft(vector);
                Text text2 = new Text("Device Connected Successfully");
                text2.setFont(Font.font("Manrope", 17));
                borderPane1.setCenter(text2);
                BorderPane.setMargin(text2, new Insets(0, 0, 0, -200));
                BorderPane.setMargin(vector, new Insets(0, 0, 0, 10));
                Text firmwareDetails = new Text("Firmware Details");
                VBox.setMargin(firmwareDetails, new Insets(0, 0, 0, 10));
                firmwareDetails.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD, 17));
                String currentWorkingDirectory = USER_WORKING_DIRECTORY;
                String relativePathToFirmware = currentWorkingDirectory + File.separator + "SPNEFirmware";
                Text firmwareName = new Text();
                File firmwareFolder = new File(relativePathToFirmware);
                VBox.setMargin(firmwareName, new Insets(0, 0, 0, 10));
                Text firmwareVersion = new Text();
                VBox.setMargin(firmwareVersion, new Insets(-10, 0, 0, 10));
                Hyperlink hyperlink = new Hyperlink("Check For Updates");
                Text status = new Text("");
                Label errorLbl = new Label();
                final File[] firmwareFile = {null};
                final File[] finalFirmwareFile = {firmwareFile[0]};


                VBox.setMargin(hyperlink, new Insets(10, 0, 0, 10));
                VBox buttonBox = new VBox();
                buttonBox.setAlignment(Pos.CENTER);
                Button button = new Button("Start Flashing");
                button.setStyle(null);
                button.setPrefWidth(400);
                button.setPrefHeight(50);
                button.setStyle("-fx-background-color: #E37F09; -fx-text-fill: white;");
                buttonBox.getChildren().add(button);
                hyperlink.setOnAction(event1 -> {
                    Platform.runLater(() -> {
                        button.setDisable(true);

                        if (!contentPane.getChildren().contains(errorLbl))
                            contentPane.getChildren().add(errorLbl);
                        errorLbl.setText("");
                    });
                    if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                        File[] files = firmwareFolder.listFiles();

                        String regex = "v(\\d+\\.\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        String version = "0";
                        Matcher matcher = null;
                        if (files != null && files.length > 0) {
                            matcher = pattern.matcher(files[0].getName());
                            if (matcher.find()) {
                                version = matcher.group(1);
                            }
                        }
                        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                            @Override
                            public void onDataReceived(String data) {
                                Platform.runLater(() -> {
                                    button.setDisable(false);
                                    errorLbl.setText("Latest Firmware Updated");
                                    hyperlink.setVisited(false);
                                });
                                if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                                    File[] files = firmwareFolder.listFiles();
                                    if (files != null && files.length > 0) {
                                        firmwareFile[0] = files[0]; // Select the first file (you can modify this as needed)
                                        firmwareName.setText("Name : " + firmwareFile[0].getName());
                                        // Extract the firmware version name from the file name
                                        String regex = "v(\\d+\\.\\d+)";
                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher matcher = pattern.matcher(firmwareFile[0].getName());
                                        if (matcher.find()) {
                                            firmwareVersion.setText("Version : " + matcher.group(1));
                                        } else {
                                            firmwareVersion.setText("Version Not Found");
                                        }
                                    } else {
                                        System.out.println("Folder is empty.");
                                    }
                                } else {
                                    System.out.println("Firmware folder not found or is not a directory.");
                                    Platform.runLater(() -> {
                                        errorLbl.setText("Firmware folder not found or is not a directory.");
                                    });
                                }
                                finalFirmwareFile[0] = firmwareFile[0];
                            }

                            @Override
                            public void onDataReceiveError(String errorMsg) {
                                Platform.runLater(() -> {
                                    button.setDisable(false);
                                    hyperlink.setVisited(false);
                                    errorLbl.setText(errorMsg);
                                });
                            }
                        };
                        DatabaseUtility.fetchLatestFirmwareFileNeo(version, onDataReceiveListener);
                    }
                });

                if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                    File[] files = firmwareFolder.listFiles();
                    if (files != null && files.length > 0) {
                        firmwareFile[0] = files[0]; // Select the first file (you can modify this as needed)
                        firmwareName.setText("Name : " + firmwareFile[0].getName());
                        // Extract the firmware version name from the file name
                        String regex = "v(\\d+\\.\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(firmwareFile[0].getName());
                        if (matcher.find()) {
                            firmwareVersion.setText("Version : " + matcher.group(1));
                        } else {
                            firmwareVersion.setText("Version Not Found");
                        }
                    } else {
                        System.out.println("Folder is empty.");
                    }
                } else {
                    System.out.println("Firmware folder not found or is not a directory.");
                    Platform.runLater(() -> {
                        errorLbl.setText("Firmware folder not found or is not a directory.");
                    });

                    buttonBox.getChildren().remove(button);
                }

                finalFirmwareFile[0] = firmwareFile[0];


                ProgressBar progressBar = new ProgressBar(0);
                status.setWrappingWidth(400);

                button.setOnAction(event2 -> {
                    Platform.runLater(() -> {
                        contentPane.getChildren().remove(errorLbl);
                        hyperlink.setVisible(false);
                    });
                    status.setFill(BLACK);

                    // Initialize UI components
                    VBox.setMargin(status, new Insets(10, 0, 0, 10));
                    buttonBox.getChildren().remove(progressBar);
                    contentPane.getChildren().remove(status);
                    buttonBox.getChildren().add(progressBar);
                    contentPane.getChildren().add(status);
                    progressBar.setPrefWidth(450);
//                        progressBar.setPrefHeight(50);
                    buttonBox.getChildren().remove(button);

                    Thread progressThread = new Thread(() -> {
                        while (!Thread.currentThread().isInterrupted()) {
                            Platform.runLater(() -> {
                                progressBar.setProgress(Math.min(progressBar.getProgress() + 0.01, 0.99));
                                double progressPercentage = progressBar.getProgress() * 100;
                                status.setText(String.format("%.0f%%", progressPercentage));
                            });
                            try {
                                Thread.sleep(310);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    });
                    Thread thread1 = new Thread(() -> {
                        try {
                            String command = "cmd /c \"cd st_link_utility" +
                                    " && ST-LINK_CLI.exe -ME\"";
                            Process process = Runtime.getRuntime().exec(command);
                            int exitCode = process.waitFor();

                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            String errorLine;
                            while ((errorLine = errorReader.readLine()) != null) {
                                System.out.println("Error: " + errorLine);
                            }

                            boolean errorDetected = false;
                            if (exitCode == 0) {

//                                command = "cmd /c \"cd st_link_utility" +
//                                        " && ST-LINK_CLI.exe -P \"" + finalFirmwareFile[0] + "\" 0x08000000 -V\"";
                                // Build the command to execute
                                command = "cmd /c \"cd st_link_utility && ST-LINK_CLI.exe -P \"" + finalFirmwareFile[0] + "\" 0x08000000 -V\"";
                                System.out.println(command);

                                // Start the process
                                process = Runtime.getRuntime().exec(command);


                                exitCode = process.waitFor();
                                BufferedReader stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                String stdLine;
                                while ((stdLine = stdReader.readLine()) != null) {
                                    if (stdLine.contains("error") || stdLine.contains("fail") || stdLine.contains("unable") || stdLine.contains("Elf Loader could not be transfered to device") || stdLine.contains("Could not disable Read Out Protection") || stdLine.contains("STLink USB communication error")) {
                                        errorDetected = true;
                                        break;
                                    }
                                    System.out.println(stdLine);
                                }

                                BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                                String errLine;
                                while ((errLine = errReader.readLine()) != null) {
                                    System.out.println(errLine);

                                    // Check for errors or failures in the output
                                    if (errLine.contains("error") || errLine.contains("failed") || errLine.contains("unable") || errLine.contains("Elf Loader could not be transfered to device") || errLine.contains("Could not disable Read Out Protection") || errLine.contains("STLink USB communication error")) {
                                        errorDetected = true;
                                        break;
                                    }
                                }
                            }

                            if (exitCode != 0 || errorDetected) {
                                Platform.runLater(() -> {
                                    progressBar.setProgress(0);
                                    status.setText("Error: Flashing process interrupted due to communication loss with the device. Ensure that the device is properly connected to the flashing tool and try again.");
                                    progressThread.interrupt();
                                    buttonBox.getChildren().remove(progressBar);
                                    status.setFill(RED);
                                    buttonBox.getChildren().add(button);
                                    hyperlink.setVisible(true);
                                });
                            } else {

                                Platform.runLater(() -> {
                                    contentPane.getChildren().remove(1, contentPane.getChildren().size());

                                    VBox.setMargin(borderPane, new Insets(-60, 10, 60, 10));
                                    contentPane.setAlignment(Pos.CENTER);
                                    Label heading = new Label("Device created successfully");
                                    heading.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 22));
                                    text.setText("Successfull!");
                                    text.setFill(Paint.valueOf("#49B23E"));
                                    FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
                                    fontAwesomeIconView.setGlyphName("CHECK_CIRCLE");
                                    fontAwesomeIconView.setFill(Paint.valueOf("#49B23E"));
                                    fontAwesomeIconView.setSize("60");
                                    Label label = new Label("Please disconnect the device from ST Link Mode and connect it in the USB Mode");
                                    label.setFont(Font.font("Times Nw Roman", FontPosture.REGULAR, 19));
                                    label.setTextFill(Paint.valueOf("#595959"));
                                    label.setWrapText(true);
                                    label.setAlignment(Pos.CENTER);
                                    label.setTextAlignment(TextAlignment.CENTER);

                                    contentPane.getChildren().addAll(fontAwesomeIconView, heading, label);
//                                        progressBar.setProgress(1.0);
//                                        status.setText("Flashing Completed: The firmware has been successfully flashed!");
//                                        status.setFill(GREEN);
//                                        System.out.println("Firmware update successful.");
                                });
                                progressThread.interrupt();
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                hyperlink.setVisible(true);
                                progressBar.setProgress(0);
                                status.setText("Error: Flashing process interrupted due to communication loss with the device. Ensure that the device is properly connected to the flashing tool and try again.");
                                progressThread.interrupt();
                                buttonBox.getChildren().remove(progressBar);
                                buttonBox.getChildren().add(button);
                            });
                        }
                    });
                    thread1.start();
                    progressThread.start();
                });
                VBox.setMargin(button, new Insets(10, 0, 0, 10));
                contentPane.getChildren().add(borderPane1);
                contentPane.getChildren().add(firmwareDetails);
                contentPane.getChildren().add(firmwareName);
                contentPane.getChildren().add(firmwareVersion);
                contentPane.getChildren().add(hyperlink);
                contentPane.getChildren().add(buttonBox);
                contentPane.getChildren().remove(imageView);
                contentPane.getChildren().remove(text1);
            });

        });
        thread.start();
        DropShadow dropShadow = new DropShadow();

        closeButton.setOnAction(e -> {
            thread.interrupt();
            flag[0] = false;
            mainBorderPane.setEffect(null);
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.close();
        });
        dropShadow.setRadius(2);
        dropShadow.setSpread(0.2);
        dialogPane.setEffect(dropShadow);
        dialogPane.setStyle("-fx-background-color:white");

        // Add inner shadow effect to the dialog content
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(2);
        innerShadow.setChoke(0.2);
        dialogPane.setEffect(innerShadow);
        Alert customDialog = new Alert(Alert.AlertType.NONE);
        customDialog.setDialogPane(dialogPane);
        customDialog.initStyle(StageStyle.TRANSPARENT);
        dialogPane.setPrefWidth(500); // Set your desired width
        dialogPane.setPrefHeight(400); // Set your desired height
        customDialog.setOnHidden(e -> {
            borderPane.setEffect(null);
        });
        openDialogs.add(customDialog);
        customDialog.showAndWait();

    }

    public void spLgAction(BorderPane mainBorderPane, List<Alert> openDialogs) {

        final Boolean[] flag = {true};

        DialogPane dialogPane = new DialogPane();
        BoxBlur boxBlur = new BoxBlur(3, 3, 7);
        Platform.runLater(() -> {
            mainBorderPane.setEffect(boxBlur);
        });
        VBox contentPane = new VBox();


        contentPane.setSpacing(10);
        contentPane.setAlignment(Pos.CENTER);
        // Apply the custom styles from the "style.css" file
        dialogPane.getStylesheets().add("style.css");
//        dialogPane.getStylesheets().add(
//                Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        BorderPane borderPane = new BorderPane();
        VBox.setMargin(borderPane, new Insets(-30, 10, 10, 10));
        Text text = new Text("Create New Device");
        text.setFont(Font.font("Times New Roman", 20));
        borderPane.setLeft(text);
        Button closeButton = new Button("X");
        borderPane.setRight(closeButton);
        contentPane.getChildren().add(borderPane);
        ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/20945402 1.png");
        Text text1 = new Text("Please Connect the Device In St-Link Mode");
        contentPane.getChildren().add(imageView);
        contentPane.getChildren().add(text1);
        dialogPane.setContent(contentPane);

        Thread thread = new Thread(() -> {
            while (flag[0]) {
                ProcessBuilder stLinkProcessBuilder = new ProcessBuilder("st_link_utility" +
                        "\\ST-LINK_CLI.exe", "-P");
                stLinkProcessBuilder.redirectErrorStream(true);
                Process stLinkProcess = null;
                try {
                    stLinkProcess = stLinkProcessBuilder.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                BufferedReader stLinkReader = new BufferedReader(new InputStreamReader(stLinkProcess.getInputStream()));
                String line;
                boolean deviceConnected = false;
                while (true) {
                    try {
                        if ((line = stLinkReader.readLine()) == null) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (line.contains(" Connected via SWD") || line.contains("Device family :STM32F301x4-x6-x8/F302x4-x6-x8/F318xx")) {
                        deviceConnected = true;
                        break;
                    }
                }
                if (deviceConnected) {
                    System.out.println("Device is connected to ST-LINK.");
                    Platform.runLater(() -> {
                        text1.setText("Device is connected to ST-LINK.");
                    });
                    break;
                } else {
                    Platform.runLater(() -> {
                        System.out.println("No device is connected to ST-LINK.");
                        text1.setText("No device is connected to ST-LINK.");
                    });
                }
            }
            Platform.runLater(() -> {
                contentPane.setAlignment(Pos.CENTER_LEFT);
                BorderPane borderPane1 = new BorderPane();
                ImageView vector = new ImageView("in/sunfox/healthcare/java/spandan_qms/Vector (1).png");
                vector.setFitWidth(20);
                vector.setFitHeight(20);
                borderPane1.setLeft(vector);
                Text text2 = new Text("Device Connected Successfully");
                text2.setFont(Font.font("Manrope", 17));
                borderPane1.setCenter(text2);
                BorderPane.setMargin(text2, new Insets(0, 0, 0, -200));
                BorderPane.setMargin(vector, new Insets(0, 0, 0, 10));
                Text firmwareDetails = new Text("Firmware Details");
                VBox.setMargin(firmwareDetails, new Insets(0, 0, 0, 10));
                firmwareDetails.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD, 17));
                String currentWorkingDirectory = USER_WORKING_DIRECTORY;
                String relativePathToFirmware = currentWorkingDirectory + File.separator + "SPLGFirmware";
                Text firmwareName = new Text();
                File firmwareFolder = new File(relativePathToFirmware);
                VBox.setMargin(firmwareName, new Insets(0, 0, 0, 10));
                Text firmwareVersion = new Text();
                VBox.setMargin(firmwareVersion, new Insets(-10, 0, 0, 10));
                Hyperlink hyperlink = new Hyperlink("Check For Updates");
                Text status = new Text("");
                Label errorLbl = new Label();
                final File[] firmwareFile = {null};

                final File[] finalFirmwareFile = {firmwareFile[0]};

                VBox.setMargin(hyperlink, new Insets(10, 0, 0, 10));
                VBox buttonBox = new VBox();
                buttonBox.setAlignment(Pos.CENTER);
                Button button = new Button("Start Flashing");
                button.setStyle(null);
                button.setPrefWidth(400);
                button.setPrefHeight(50);
                button.setStyle("-fx-background-color: #E37F09; -fx-text-fill: white;");
                buttonBox.getChildren().add(button);
                hyperlink.setOnAction(event1 -> {
                    Platform.runLater(() -> {
                        button.setDisable(true);
                        if (!contentPane.getChildren().contains(errorLbl))
                            contentPane.getChildren().add(errorLbl);
                        errorLbl.setText("");
                    });
                    if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                        File[] files = firmwareFolder.listFiles();

                        String regex = "V(\\d+\\.\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        String version = "0";
                        Matcher matcher = null;
                        if (files != null && files.length > 0) {
                            matcher = pattern.matcher(files[0].getName());
                            if (matcher.find()) {
                                version = matcher.group(1);
                            }
                        }

                        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                            @Override
                            public void onDataReceived(String data) {
                                Platform.runLater(() -> {
                                    button.setDisable(false);
                                    errorLbl.setText("Latest Firmware Updated");
                                    hyperlink.setVisited(false);
                                });
                                if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                                    File[] files = firmwareFolder.listFiles();
                                    if (files != null && files.length > 0) {
                                        firmwareFile[0] = files[0]; // Select the first file (you can modify this as needed)
                                        firmwareName.setText("Name : " + firmwareFile[0].getName());
                                        // Extract the firmware version name from the file name
                                        String regex = "V(\\d+\\.\\d+)";
                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher matcher = pattern.matcher(firmwareFile[0].getName());
                                        if (matcher.find()) {
                                            firmwareVersion.setText("Version : " + matcher.group(1));
                                        } else {
                                            firmwareVersion.setText("Version Not Found");
                                        }
                                    } else {
                                        System.out.println("Folder is empty.");

                                    }
                                } else {
                                    System.out.println("Firmware folder not found or is not a directory.");
                                }
                                finalFirmwareFile[0] = firmwareFile[0];
                            }

                            @Override
                            public void onDataReceiveError(String errorMsg) {
                                Platform.runLater(() -> {
                                    button.setDisable(false);
                                    hyperlink.setVisited(false);
                                    errorLbl.setText(errorMsg);
                                });
                            }
                        };
                        DatabaseUtility.fetchLatestFirmwareFile(version, onDataReceiveListener);


                    }
                });


                if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                    File[] files = firmwareFolder.listFiles();
                    if (files != null && files.length > 0) {
                        firmwareFile[0] = files[0]; // Select the first file (you can modify this as needed)
                        firmwareName.setText("Name : " + firmwareFile[0].getName());
                        // Extract the firmware version name from the file name
                        String regex = "V(\\d+\\.\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(firmwareFile[0].getName());
                        if (matcher.find()) {
                            firmwareVersion.setText("Version : " + matcher.group(1));
                        } else {
                            firmwareVersion.setText("Version Not Found");
                        }
                    } else {
                        System.out.println("Folder is empty.");
                    }
                } else {
                    System.out.println("Firmware folder not found or is not a directory.");
                }

                finalFirmwareFile[0] = firmwareFile[0];


                ProgressBar progressBar = new ProgressBar(0);
                status.setWrappingWidth(400);

                button.setOnAction(event2 -> {
                    Platform.runLater(() -> {
                        contentPane.getChildren().remove(errorLbl);
                        hyperlink.setVisible(false);
                    });
                    status.setFill(BLACK);

                    // Initialize UI components
                    VBox.setMargin(status, new Insets(10, 0, 0, 10));
                    buttonBox.getChildren().remove(progressBar);
                    contentPane.getChildren().remove(status);
                    buttonBox.getChildren().add(progressBar);
                    contentPane.getChildren().add(status);
                    progressBar.setPrefWidth(450);
//                        progressBar.setPrefHeight(50);
                    buttonBox.getChildren().remove(button);

                    Thread progressThread = new Thread(() -> {
                        while (!Thread.currentThread().isInterrupted()) {
                            Platform.runLater(() -> {
                                progressBar.setProgress(Math.min(progressBar.getProgress() + 0.01, 0.99));
                                double progressPercentage = progressBar.getProgress() * 100;
                                status.setText(String.format("%.0f%%", progressPercentage));
                            });
                            try {
                                Thread.sleep(310);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    });

                    // Start a thread to handle firmware flashing
                    Thread thread1 = new Thread(() -> {
                        try {
                            String command = "cmd /c \"cd st_link_utility" +
                                    " && ST-LINK_CLI.exe -ME\"";
                            Process process = Runtime.getRuntime().exec(command);
                            int exitCode = process.waitFor();

                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            String errorLine;
                            while ((errorLine = errorReader.readLine()) != null) {
                                System.out.println("Error: " + errorLine);
                            }
                            boolean errorDetected = false;

                            // Build the command to execute
                            command = "cmd /c \"cd st_link_utility" +
                                    " && ST-LINK_CLI.exe -P \"" + finalFirmwareFile[0] + "\" 0x08000000 -V\"";
                            System.out.println(command);

                            // Start the process
                            process = Runtime.getRuntime().exec(command);
                            exitCode = process.waitFor();
                            BufferedReader stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String stdLine;
                            while ((stdLine = stdReader.readLine()) != null) {
                                if (stdLine.contains("error") || stdLine.contains("fail") || stdLine.contains("unable") || stdLine.contains("Elf Loader could not be transfered to device") || stdLine.contains("Could not disable Read Out Protection") || stdLine.contains("STLink USB communication error")) {
                                    errorDetected = true;
                                    break;
                                }
                                System.out.println(stdLine);
                            }

                            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            String errLine;
                            while ((errLine = errReader.readLine()) != null) {
                                System.out.println(errLine);

                                // Check for errors or failures in the output
                                if (errLine.contains("error") || errLine.contains("failed") || errLine.contains("unable") || errLine.contains("Elf Loader could not be transfered to device") || errLine.contains("Could not disable Read Out Protection") || errLine.contains("STLink USB communication error")) {
                                    errorDetected = true;
                                    break;
                                }
                            }


                            if (exitCode != 0 || errorDetected) {
                                Platform.runLater(() -> {
                                    progressBar.setProgress(0);
                                    status.setText("Error: Flashing process interrupted due to communication loss with the device. Ensure that the device is properly connected to the flashing tool and try again.");
                                    progressThread.interrupt();
                                    buttonBox.getChildren().remove(progressBar);
                                    status.setFill(RED);
                                    buttonBox.getChildren().add(button);
                                    hyperlink.setVisible(true);
                                });
                            } else {

                                Platform.runLater(() -> {
                                    contentPane.getChildren().remove(1, contentPane.getChildren().size());

                                    VBox.setMargin(borderPane, new Insets(-60, 10, 60, 10));
                                    contentPane.setAlignment(Pos.CENTER);
                                    Label heading = new Label("Device created successfully");
                                    heading.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 22));
                                    text.setText("Successfull!");
                                    text.setFill(Paint.valueOf("#49B23E"));
                                    FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
                                    fontAwesomeIconView.setGlyphName("CHECK_CIRCLE");
                                    fontAwesomeIconView.setFill(Paint.valueOf("#49B23E"));
                                    fontAwesomeIconView.setSize("60");
                                    Label label = new Label("Please disconnect the device from ST Link Mode and connect it in the USB Mode");
                                    label.setFont(Font.font("Times Nw Roman", FontPosture.REGULAR, 19));
                                    label.setTextFill(Paint.valueOf("#595959"));
                                    label.setWrapText(true);
                                    label.setAlignment(Pos.CENTER);
                                    label.setTextAlignment(TextAlignment.CENTER);

                                    contentPane.getChildren().addAll(fontAwesomeIconView, heading, label);
//                                        progressBar.setProgress(1.0);
//                                        status.setText("Flashing Completed: The firmware has been successfully flashed!");
//                                        status.setFill(GREEN);
//                                        System.out.println("Firmware update successful.");
                                });
                                progressThread.interrupt();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                hyperlink.setVisible(true);
                                progressBar.setProgress(0);
                                status.setText("Error: Flashing process interrupted due to communication loss with the device. Ensure that the device is properly connected to the flashing tool and try again.");
                                progressThread.interrupt();
                                buttonBox.getChildren().remove(progressBar);
                                buttonBox.getChildren().add(button);
                            });
                        }
                    });
                    thread1.start();
                    progressThread.start();
                });
                VBox.setMargin(button, new Insets(10, 0, 0, 10));
                contentPane.getChildren().add(borderPane1);
                contentPane.getChildren().add(firmwareDetails);
                contentPane.getChildren().add(firmwareName);
                contentPane.getChildren().add(firmwareVersion);
                contentPane.getChildren().add(hyperlink);
                contentPane.getChildren().add(buttonBox);
                contentPane.getChildren().remove(imageView);
                contentPane.getChildren().remove(text1);
            });

        });
        thread.start();
        DropShadow dropShadow = new DropShadow();

        closeButton.setOnAction(e -> {
            thread.interrupt();
            flag[0] = false;
            mainBorderPane.setEffect(null);
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.close();
        });
        dropShadow.setRadius(2);
        dropShadow.setSpread(0.2);
        dialogPane.setEffect(dropShadow);
        dialogPane.setStyle("-fx-background-color:white");

        // Add inner shadow effect to the dialog content
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(2);
        innerShadow.setChoke(0.2);
        dialogPane.setEffect(innerShadow);
        Alert customDialog = new Alert(Alert.AlertType.NONE);
        customDialog.setDialogPane(dialogPane);
        customDialog.initStyle(StageStyle.TRANSPARENT);
        dialogPane.setPrefWidth(500); // Set your desired width
        dialogPane.setPrefHeight(400); // Set your desired height
        customDialog.setOnHidden(e -> {
            borderPane.setEffect(null);
        });
        openDialogs.add(customDialog);
        customDialog.showAndWait();
    }

    public void spPrAction(BorderPane mainBorderPane, List<Alert> openDialogs) {

        final Boolean[] flag = {true};

        DialogPane dialogPane = new DialogPane();
        BoxBlur boxBlur = new BoxBlur(3, 3, 7);
        Platform.runLater(() -> {
            mainBorderPane.setEffect(boxBlur);
        });
        VBox contentPane = new VBox();


        contentPane.setSpacing(10);
        contentPane.setAlignment(Pos.CENTER);

        dialogPane.getStylesheets().add("style.css");
        dialogPane.getStyleClass().add("myDialog");
        BorderPane borderPane = new BorderPane();
        VBox.setMargin(borderPane, new Insets(-30, 10, 10, 10));
        Text text = new Text("Create New Device");
        text.setFont(Font.font("Times New Roman", 20));
        borderPane.setLeft(text);
        Button closeButton = new Button("X");
        borderPane.setRight(closeButton);
        contentPane.getChildren().add(borderPane);
        ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/20945402 1.png");
        Text text1 = new Text("Please Connect the Device In St-Link Mode");
        contentPane.getChildren().add(imageView);
        contentPane.getChildren().add(text1);
        dialogPane.setContent(contentPane);

        Thread thread = new Thread(() -> {
            while (flag[0]) {
                ProcessBuilder stLinkProcessBuilder = new ProcessBuilder("st_link_utility" +
                        "\\ST-LINK_CLI.exe", "-P");
                stLinkProcessBuilder.redirectErrorStream(true);
                Process stLinkProcess = null;
                try {
                    stLinkProcess = stLinkProcessBuilder.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                BufferedReader stLinkReader = new BufferedReader(new InputStreamReader(stLinkProcess.getInputStream()));
                String line;
                boolean deviceConnected = false;
                while (true) {
                    try {
                        if ((line = stLinkReader.readLine()) == null) break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (line.contains(" Connected via SWD") || line.contains("Device family :STM32F301x4-x6-x8/F302x4-x6-x8/F318xx")) {
                        deviceConnected = true;
                        break;
                    }
                }
                if (deviceConnected) {
                    System.out.println("Device is connected to ST-LINK.");
                    Platform.runLater(() -> {
                        text1.setText("Device is connected to ST-LINK.");
                    });
                    break;
                } else {
                    Platform.runLater(() -> {
                        System.out.println("No device is connected to ST-LINK.");
                        text1.setText("No device is connected to ST-LINK.");
                    });
                }
            }
            Platform.runLater(() -> {
                contentPane.setAlignment(Pos.CENTER_LEFT);
                BorderPane borderPane1 = new BorderPane();
                ImageView vector = new ImageView("in/sunfox/healthcare/java/spandan_qms/Vector (1).png");
                vector.setFitWidth(20);
                vector.setFitHeight(20);
                borderPane1.setLeft(vector);
                Text text2 = new Text("Device Connected Successfully");
                text2.setFont(Font.font("Manrope", 17));
                borderPane1.setCenter(text2);
                BorderPane.setMargin(text2, new Insets(0, 0, 0, -200));
                BorderPane.setMargin(vector, new Insets(0, 0, 0, 10));
                Text firmwareDetails = new Text("Firmware Details");
                VBox.setMargin(firmwareDetails, new Insets(0, 0, 0, 10));
                firmwareDetails.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD, 17));
                String currentWorkingDirectory = USER_WORKING_DIRECTORY;
                String relativePathToFirmware = currentWorkingDirectory + File.separator + "SPPRFirmware";
                Text firmwareName = new Text();
                File firmwareFolder = new File(relativePathToFirmware);
                VBox.setMargin(firmwareName, new Insets(0, 0, 0, 10));
                Text firmwareVersion = new Text();
                VBox.setMargin(firmwareVersion, new Insets(-10, 0, 0, 10));
                Hyperlink hyperlink = new Hyperlink("Check For Updates");
                Text status = new Text("");
                Label errorLbl = new Label();
                final File[] firmwareFile = {null};

                final File[] finalFirmwareFile = {firmwareFile[0]};

                VBox.setMargin(hyperlink, new Insets(10, 0, 0, 10));
                VBox buttonBox = new VBox();
                buttonBox.setAlignment(Pos.CENTER);
                Button button = new Button("Start Flashing");
                button.setStyle(null);
                button.setPrefWidth(400);
                button.setPrefHeight(50);
                button.setStyle("-fx-background-color: #E37F09; -fx-text-fill: white;");
                buttonBox.getChildren().add(button);
                hyperlink.setOnAction(event1 -> {
                    Platform.runLater(() -> {
                        button.setDisable(true);
                        if (!contentPane.getChildren().contains(errorLbl))
                            contentPane.getChildren().add(errorLbl);
                        errorLbl.setText("");
                    });
                    if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                        File[] files = firmwareFolder.listFiles();

                        String regex = "V(\\d+\\.\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        String version = "0";
                        Matcher matcher = null;
                        if (files != null && files.length > 0) {
                            matcher = pattern.matcher(files[0].getName());
                            if (matcher.find()) {
                                version = matcher.group(1);
                            }
                        }
                        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                            @Override
                            public void onDataReceived(String data) {
                                Platform.runLater(() -> {
                                    button.setDisable(false);
                                    errorLbl.setText("Latest Firmware Updated");
                                    hyperlink.setVisited(false);
                                });
                                if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                                    File[] files = firmwareFolder.listFiles();
                                    if (files != null && files.length > 0) {
                                        firmwareFile[0] = files[0]; // Select the first file (you can modify this as needed)
                                        firmwareName.setText("Name : " + firmwareFile[0].getName());
                                        // Extract the firmware version name from the file name
                                        String regex = "V(\\d+\\.\\d+)";
                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher matcher = pattern.matcher(firmwareFile[0].getName());
                                        if (matcher.find()) {
                                            firmwareVersion.setText("Version : " + matcher.group(1));
                                        } else {
                                            firmwareVersion.setText("Version Not Found");
                                        }
                                    } else {
                                        System.out.println("Folder is empty.");
                                    }
                                } else {
                                    System.out.println("Firmware folder not found or is not a directory.");
                                }
                                finalFirmwareFile[0] = firmwareFile[0];
                            }

                            @Override
                            public void onDataReceiveError(String errorMsg) {
                                Platform.runLater(() -> {
                                    button.setDisable(false);
                                    hyperlink.setVisited(false);
                                    errorLbl.setText(errorMsg);
                                });
                            }
                        };

                        DatabaseUtility.fetchLatestFirmwareFilePro(version, onDataReceiveListener);


                    }
                });


                if (firmwareFolder.exists() && firmwareFolder.isDirectory()) {
                    File[] files = firmwareFolder.listFiles();
                    if (files != null && files.length > 0) {
                        firmwareFile[0] = files[0]; // Select the first file (you can modify this as needed)
                        firmwareName.setText("Name : " + firmwareFile[0].getName());
                        // Extract the firmware version name from the file name
                        String regex = "V(\\d+\\.\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(firmwareFile[0].getName());
                        if (matcher.find()) {
                            firmwareVersion.setText("Version : " + matcher.group(1));
                        } else {
                            firmwareVersion.setText("Version Not Found");
                        }
                    } else {
                        System.out.println("Folder is empty.");
                    }
                } else {
                    System.out.println("Firmware folder not found or is not a directory.");
                }

                finalFirmwareFile[0] = firmwareFile[0];


                ProgressBar progressBar = new ProgressBar(0);
                status.setWrappingWidth(400);

                button.setOnAction(event2 -> {
                    Platform.runLater(() -> {
                        contentPane.getChildren().remove(errorLbl);
                        hyperlink.setVisible(false);
                    });
                    status.setFill(BLACK);

                    // Initialize UI components
                    VBox.setMargin(status, new Insets(10, 0, 0, 10));
                    buttonBox.getChildren().remove(progressBar);
                    contentPane.getChildren().remove(status);
                    buttonBox.getChildren().add(progressBar);
                    contentPane.getChildren().add(status);
                    progressBar.setPrefWidth(450);
//                        progressBar.setPrefHeight(50);
                    buttonBox.getChildren().remove(button);

                    Thread progressThread = new Thread(() -> {
                        while (!Thread.currentThread().isInterrupted()) {
                            Platform.runLater(() -> {
                                progressBar.setProgress(Math.min(progressBar.getProgress() + 0.01, 0.99));
                                double progressPercentage = progressBar.getProgress() * 100;
                                status.setText(String.format("%.0f%%", progressPercentage));
                            });
                            try {
                                Thread.sleep(310);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    });

                    // Start a thread to handle firmware flashing
                    Thread thread1 = new Thread(() -> {
                        try {
                            String command = "cmd /c \"cd st_link_utility" +
                                    " && ST-LINK_CLI.exe -ME\"";
                            Process process = Runtime.getRuntime().exec(command);
                            int exitCode = process.waitFor();

                            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            String errorLine;
                            while ((errorLine = errorReader.readLine()) != null) {
                                System.out.println("Error: " + errorLine);
                            }
                            boolean errorDetected = false;

                            // Build the command to execute
                            command = "cmd /c \"cd st_link_utility" +
                                    " && ST-LINK_CLI.exe -P \"" + finalFirmwareFile[0] + "\" 0x08000000 -V\"";
                            System.out.println(command);

                            // Start the process
                            process = Runtime.getRuntime().exec(command);
                            exitCode = process.waitFor();
                            BufferedReader stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String stdLine;
                            while ((stdLine = stdReader.readLine()) != null) {
                                if (stdLine.contains("error") || stdLine.contains("fail") || stdLine.contains("unable") || stdLine.contains("Elf Loader could not be transfered to device") || stdLine.contains("Could not disable Read Out Protection") || stdLine.contains("STLink USB communication error")) {
                                    errorDetected = true;
                                    break;
                                }
                                System.out.println(stdLine);
                            }

                            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            String errLine;
                            while ((errLine = errReader.readLine()) != null) {
                                System.out.println(errLine);

                                // Check for errors or failures in the output
                                if (errLine.contains("error") || errLine.contains("failed") || errLine.contains("unable") || errLine.contains("Elf Loader could not be transfered to device") || errLine.contains("Could not disable Read Out Protection") || errLine.contains("STLink USB communication error")) {
                                    errorDetected = true;
                                    break;
                                }
                            }


                            if (exitCode != 0 || errorDetected) {
                                Platform.runLater(() -> {
                                    progressBar.setProgress(0);
                                    status.setText("Error: Flashing process interrupted due to communication loss with the device. Ensure that the device is properly connected to the flashing tool and try again.");
                                    progressThread.interrupt();
                                    buttonBox.getChildren().remove(progressBar);
                                    status.setFill(RED);
                                    buttonBox.getChildren().add(button);
                                    hyperlink.setVisible(true);
                                });
                            } else {

                                Platform.runLater(() -> {
                                    contentPane.getChildren().remove(1, contentPane.getChildren().size());

                                    VBox.setMargin(borderPane, new Insets(-60, 10, 60, 10));
                                    contentPane.setAlignment(Pos.CENTER);
                                    Label heading = new Label("Device created successfully");
                                    heading.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 22));
                                    text.setText("Successfull!");
                                    text.setFill(Paint.valueOf("#49B23E"));
                                    FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
                                    fontAwesomeIconView.setGlyphName("CHECK_CIRCLE");
                                    fontAwesomeIconView.setFill(Paint.valueOf("#49B23E"));
                                    fontAwesomeIconView.setSize("60");
                                    Label label = new Label("Please disconnect the device from ST Link Mode and connect it in the USB Mode");
                                    label.setFont(Font.font("Times Nw Roman", FontPosture.REGULAR, 19));
                                    label.setTextFill(Paint.valueOf("#595959"));
                                    label.setWrapText(true);
                                    label.setAlignment(Pos.CENTER);
                                    label.setTextAlignment(TextAlignment.CENTER);

                                    contentPane.getChildren().addAll(fontAwesomeIconView, heading, label);
//                                        progressBar.setProgress(1.0);
//                                        status.setText("Flashing Completed: The firmware has been successfully flashed!");
//                                        status.setFill(GREEN);
//                                        System.out.println("Firmware update successful.");
                                });
                                progressThread.interrupt();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                hyperlink.setVisible(true);
                                progressBar.setProgress(0);
                                status.setText("Error: Flashing process interrupted due to communication loss with the device. Ensure that the device is properly connected to the flashing tool and try again.");
                                progressThread.interrupt();
                                buttonBox.getChildren().remove(progressBar);
                                buttonBox.getChildren().add(button);
                            });
                        }
                    });
                    thread1.start();
                    progressThread.start();
                });
                VBox.setMargin(button, new Insets(10, 0, 0, 10));
                contentPane.getChildren().add(borderPane1);
                contentPane.getChildren().add(firmwareDetails);
                contentPane.getChildren().add(firmwareName);
                contentPane.getChildren().add(firmwareVersion);
                contentPane.getChildren().add(hyperlink);
                contentPane.getChildren().add(buttonBox);
                contentPane.getChildren().remove(imageView);
                contentPane.getChildren().remove(text1);
            });

        });
        thread.start();
        DropShadow dropShadow = new DropShadow();

        closeButton.setOnAction(e -> {
            thread.interrupt();
            flag[0] = false;
            mainBorderPane.setEffect(null);
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.close();
        });
        dropShadow.setRadius(2);
        dropShadow.setSpread(0.2);
        dialogPane.setEffect(dropShadow);
        dialogPane.setStyle("-fx-background-color:white");

        // Add inner shadow effect to the dialog content
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(2);
        innerShadow.setChoke(0.2);
        dialogPane.setEffect(innerShadow);
        Alert customDialog = new Alert(Alert.AlertType.NONE);
        customDialog.setDialogPane(dialogPane);
        customDialog.initStyle(StageStyle.TRANSPARENT);
        dialogPane.setPrefWidth(500); // Set your desired width
        dialogPane.setPrefHeight(400); // Set your desired height
        customDialog.setOnHidden(e -> {
            borderPane.setEffect(null);
        });
        openDialogs.add(customDialog);
        customDialog.showAndWait();
    }

}
