
package in.sunfox.healthcare.java.spandan_qms.main_ui_controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import in.sunfox.healthcare.java.spandan_qms.database_service.DatabaseUtility;
import in.sunfox.healthcare.java.spandan_qms.database_service.OnDataReceiveListener;
import in.sunfox.healthcare.java.spandan_qms.database_service.UserData;
import in.sunfox.healthcare.java.spandan_qms.TestMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class AdminPanel implements Initializable {

    @FXML
    private CheckBox permission1;

    @FXML
    private CheckBox permission2;

    @FXML
    private CheckBox permission3;

    @FXML
    private CheckBox permission4;

    @FXML
    private CheckBox permission5;

    @FXML
    private CheckBox permission6;

    @FXML
    private VBox userVbox;

    @FXML
    private Button newBatchButton;
    @FXML
    private CheckBox b2bBatch;
    @FXML
    private CheckBox b2cBatch;
    @FXML
    private TextField batchDescription;

    @FXML
    private Text errorText;

    @FXML
    private TextField sizeOfBatchTextField;

    @FXML
    private Label userNameLabel;
    @FXML
    private Label userLabel;

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ChoiceBox<String> spandanVariantComboBox;
    @FXML
    private ChoiceBox<String>productionUnitComboBox;
    @FXML
    private DatePicker dateOfBatch;

    String date = null;

    @FXML
    BorderPane newBatchBorderPane;
    @FXML
    VBox permissionVbox;
    @FXML
    void createNewBatchAction() {
        System.out.println(batchId);


        long batchSize = 0;
        try {
            if (!Objects.equals(sizeOfBatchTextField.getText(), "")) {
                batchSize = Long.parseLong(sizeOfBatchTextField.getText().trim());
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                errorText.setFill(Paint.valueOf("#BA2707"));
                errorText.setText("PLease enter valid BatchSize");
                e.printStackTrace();
            });
        }
        String batchDescriptionString = batchDescription.getText();
        String username = userNameLabel.getText();

//        batchId = batchIdTextField.getText();
        System.out.println(batchSize);
        if (b2bBatch.isSelected()||b2cBatch.isSelected()) {
            if (batchSize > 0 && !username.equals("") && batchId.length()==16 && date != null) {

                OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                    @Override
                    public void onDataReceived(String data) {
                        Platform.runLater(() -> {
                            errorText.setFill(GREEN);
                            errorText.setText("Success");
//                            Stage primaryStage;
//                            primaryStage = (Stage) progressIndicator.getScene().getWindow();
//                            primaryStage.close();
                        });
                    }

                    @Override
                    public void onDataReceiveError(String errorMsg) {

                        if (errorMsg.contains("Unauthorized")) {
                            try {
                                Platform.runLater(()->{
                                    Stage primaryStage = (Stage) batchDescription.getScene().getWindow();
                                    FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("LoginUi.fxml"));
                                    Parent loginRoot = null;
                                    try {
                                        loginRoot = fxmlLoader.load();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Scene loginScene = new Scene(loginRoot);
                                    Stage loginStage = new Stage();
                                    loginStage.setScene(loginScene);
                                    loginStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                                    loginStage.setTitle("Sunfox Login");
                                    loginStage.initStyle(StageStyle.UNDECORATED);
                                    loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
                                    loginStage.show();
                                    refStage.close();
                                    primaryStage.close();
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else
                            Platform.runLater(() -> {
                                errorText.setFill(RED);
                                errorText.setText(errorMsg);
                            });
                    }
                };
                long finalBatchSize = batchSize;
                Thread thread = new Thread(() -> {
                    DatabaseUtility.createNewBatch(String.valueOf(batchId), finalBatchSize, username, date, batchDescriptionString, onDataReceiveListener);
                });
                thread.start();
                Platform.runLater(() -> {
                    errorText.setFill(Paint.valueOf("#000000"));
                    errorText.setText("please wait........");
                });
            } else {
                errorText.setFill(Paint.valueOf("#BA2707"));
                errorText.setText("Enter Details");
                if (batchSize <= 0) {
                    errorText.setText("batchSize Cannot be 0");
                }
            }
        } else {
            errorText.setFill(Paint.valueOf("#BA2707"));
            errorText.setText("Please Approve the Batch");
        }
    }
    StringBuilder batchId = new StringBuilder("SP");
    @FXML
    ProgressBar progressBar;

    @FXML
    void allowPermissionsButton() {
        errorLabel.setText("");
        errorLabel.setTextFill(RED);
        errorLabel.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD,21));
        errorLabel.setWrapText(true);
        int decimalValue = 0;
        CheckBox[] permissionCheckboxes = {
                permission1, permission2, permission3,
                permission4, permission5, permission6
        };

        boolean atLeastOneSelected = false;

        for (int i = 0; i < permissionCheckboxes.length; i++) {
            CheckBox checkBox = permissionCheckboxes[i];
            if (checkBox.isSelected()) {
                atLeastOneSelected = true;
                decimalValue |= (1 << i); // Set the corresponding bit to 1
            }
        }
        if (atLeastOneSelected && !userLabel.getText().equals("")) {
            // Use the decimalValue as needed
            OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                @Override
                public void onDataReceived(String data) {
                    errorLabel.setText(data);
                    errorLabel.setTextFill(GREEN);
                }

                @Override
                public void onDataReceiveError(String errorMsg) {
                    errorLabel.setText(errorMsg);
                }
            };
            DatabaseUtility.updateStagesPermission(userLabel.getText(),decimalValue,onDataReceiveListener);

        } else if (userLabel.getText().equals("")) {
            errorLabel.setText("User not selected");

        } else {
            // Show an error message or take appropriate action
            errorLabel.setText("Select at least one permission.");

        }
    }

    @FXML
    private Label errorLabel;
    @FXML
    public void newBatchButtonAction() {
        newBatchBorderPane.setVisible(true);
        permissionVbox.setVisible(false);
    }

    public void closeWindow() {
        newBatchBorderPane.setVisible(false);
        permissionVbox.setVisible(true);
//        userVbox.getChildren().add(progressBar);
    }

    @FXML
    FontAwesomeIconView refreshList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressBar.setVisible(true);
        date = Instant.now().toString();
        final String[] formattedDate = {""};
        spandanVariantComboBox.getItems().addAll("LG","NE","PR");
        spandanVariantComboBox.setOnAction(event -> {
            if (spandanVariantComboBox.getValue() != null) {
                if(b2cBatch.isSelected())
                    batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() +"-" + productionUnitComboBox.getValue()+"-"+ formattedDate[0]);
                else if(b2cBatch.isSelected())
                    batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() +"." + productionUnitComboBox.getValue()+"."+ formattedDate[0]);

                productionUnitComboBox.getItems().addAll("DN01");
                productionUnitComboBox.setVisible(true);
                productionUnitComboBox.setOnAction(event1 -> {
                    if (productionUnitComboBox.getValue() != null) {
                        if(b2cBatch.isSelected())
                            batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() +"-" + productionUnitComboBox.getValue()+"-"+ formattedDate[0]);
                        else if(b2cBatch.isSelected())
                            batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() +"." + productionUnitComboBox.getValue()+"."+ formattedDate[0]);
                        dateOfBatch.setVisible(true);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
                        dateOfBatch.setOnAction(event2 -> {
                            if (dateOfBatch.getValue() != null) {
                                LocalDate date = dateOfBatch.getValue();
                                formattedDate[0] = formatter.format(date);
                                if(b2cBatch.isSelected())
                                    batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() +"-" + productionUnitComboBox.getValue()+"-"+ formattedDate[0]);
                                else if(b2cBatch.isSelected())
                                    batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() +"." + productionUnitComboBox.getValue()+"."+ formattedDate[0]);
                                System.out.println(batchId);
                            } else {
                                System.out.println("date lele bhai");
                            }
                        });
                        b2cBatch.setOnAction(event2 -> {
                            b2bBatch.setSelected(false);

                            batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() + "-" + productionUnitComboBox.getValue() + "-" + formattedDate[0]);

                        });
                        b2bBatch.setOnAction(event2 -> {
                            b2cBatch.setSelected(false);
                            batchId = new StringBuilder("SP" + spandanVariantComboBox.getValue() +"." + productionUnitComboBox.getValue()+"."+ formattedDate[0]);

                        });

                    }
                });

            }
        });


        Thread thread = new Thread(() -> {
            OnDataReceiveListener<Map<String, UserData>> onDataReceiveListener = new OnDataReceiveListener<Map<String, UserData>>() {
                @Override
                public void onDataReceived(Map<String, UserData> data) {

                    Platform.runLater(()->{

                    userVbox.getChildren().clear();
                    progressBar.setVisible(false);// Clear previous content4
                    });

                    for (UserData userData : data.values()) {
                        BorderPane borderPane = new BorderPane();
                        borderPane.setStyle("-fx-background-color: #F15056");

                        Label label = new Label(userData.getEmail());
                        label.setTextFill(WHITE);
                        borderPane.setCenter(label);
                        borderPane.setOnMouseClicked(event -> {
                            permission1.setSelected(false);
                            permission2.setSelected(false);
                            permission3.setSelected(false);
                            permission4.setSelected(false);
                            permission5.setSelected(false);
                            permission6.setSelected(false);
                            userLabel.setText(label.getText());
                            String permissions = Integer.toBinaryString(data.get(userLabel.getText()).getStagesPermission());
                            StringBuilder reversed = new StringBuilder();
                            for (int i = permissions.length() - 1; i >= 0; i--) {
                                reversed.append(permissions.charAt(i));
                            }
                            for (int i = reversed.length(); i < 6; i++)
                                reversed.append('0');
                            for (int i = 0; i < 6; i++) {
                                if (reversed.charAt(i) == '1') {
                                    switch (i) {
                                        case 0:
                                            permission1.setSelected(true);
                                            break;
                                        case 1:
                                            permission2.setSelected(true);
                                            break;
                                        case 2:
                                            permission3.setSelected(true);
                                            break;
                                        case 3:
                                            permission4.setSelected(true);
                                            break;
                                        case 4:
                                            permission5.setSelected(true);
                                            break;
                                        case 5:
                                            permission6.setSelected(true);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        });

                        Platform.runLater(()->{
                        userVbox.getChildren().add(borderPane);
                        });
                        VBox.setMargin(borderPane, new Insets(10, 10, 0, 10));
                        borderPane.setPrefWidth(210);
                        borderPane.setPrefHeight(34);
                    }
                }


                @Override
                public void onDataReceiveError(String errorMsg) {
                    if (errorMsg.contains("Unauthorized")) {
                        Platform.runLater(() -> {
                            try {
                                Stage primaryStage = (Stage) userVbox.getScene().getWindow();
                                FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("LoginUi.fxml"));
                                Parent loginRoot = fxmlLoader.load();
                                Scene loginScene = new Scene(loginRoot);

                                refStage.close();
                                Stage loginStage = new Stage();
                                loginStage.setScene(loginScene);
                                loginStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                                loginStage.setTitle("Sunfox Login");
                                loginStage.initStyle(StageStyle.UNDECORATED);

                                loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                                loginStage.show();
                                primaryStage.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                }
            };
            DatabaseUtility.getAllUsersList(onDataReceiveListener);
        });
        thread.start();
        refreshList.setOnMouseClicked(event -> {
            Thread thread2 = new Thread(() -> {
                OnDataReceiveListener<Map<String, UserData>> onDataReceiveListener = new OnDataReceiveListener<Map<String, UserData>>() {
                    @Override
                    public void onDataReceived(Map<String, UserData> data) {

                        Platform.runLater(()->{

                            userVbox.getChildren().clear();
                            progressBar.setVisible(false);// Clear previous content4
                        });

                        for (UserData userData : data.values()) {
                            BorderPane borderPane = new BorderPane();
                            borderPane.setStyle("-fx-background-color: #F15056");

                            Label label = new Label(userData.getEmail());
                            label.setTextFill(WHITE);
                            borderPane.setCenter(label);
                            borderPane.setOnMouseClicked(event -> {
                                permission1.setSelected(false);
                                permission2.setSelected(false);
                                permission3.setSelected(false);
                                permission4.setSelected(false);
                                permission5.setSelected(false);
                                permission6.setSelected(false);
                                userLabel.setText(label.getText());
                                String permissions = Integer.toBinaryString(data.get(userLabel.getText()).getStagesPermission());
                                StringBuilder reversed = new StringBuilder();
                                for (int i = permissions.length() - 1; i >= 0; i--) {
                                    reversed.append(permissions.charAt(i));
                                }
                                for (int i = reversed.length(); i < 6; i++)
                                    reversed.append('0');
                                for (int i = 0; i < 6; i++) {
                                    if (reversed.charAt(i) == '1') {
                                        switch (i) {
                                            case 0:
                                                permission1.setSelected(true);
                                                break;
                                            case 1:
                                                permission2.setSelected(true);
                                                break;
                                            case 2:
                                                permission3.setSelected(true);
                                                break;
                                            case 3:
                                                permission4.setSelected(true);
                                                break;
                                            case 4:
                                                permission5.setSelected(true);
                                                break;
                                            case 5:
                                                permission6.setSelected(true);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            });

                            Platform.runLater(()->{
                                userVbox.getChildren().add(borderPane);
                            });
                            VBox.setMargin(borderPane, new Insets(10, 10, 0, 10));
                            borderPane.setPrefWidth(210);
                            borderPane.setPrefHeight(34);
                        }
                    }


                    @Override
                    public void onDataReceiveError(String errorMsg) {
                        if (errorMsg.contains("Unauthorized")) {
                            Platform.runLater(() -> {
                                try {
                                    Stage primaryStage = (Stage) userVbox.getScene().getWindow();
                                    FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("LoginUi.fxml"));
                                    Parent loginRoot = fxmlLoader.load();
                                    Scene loginScene = new Scene(loginRoot);

                                    refStage.close();
                                    Stage loginStage = new Stage();
                                    loginStage.setScene(loginScene);
                                    loginStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                                    loginStage.setTitle("Sunfox Login");
                                    loginStage.initStyle(StageStyle.UNDECORATED);

                                    loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                                    loginStage.show();
                                    primaryStage.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                    }
                };
                DatabaseUtility.getAllUsersList(onDataReceiveListener);
            });
            thread2.start();
            Platform.runLater(()->{
                userLabel.setText("");
                permission1.setSelected(false);
                permission2.setSelected(false);
                permission3.setSelected(false);
                permission4.setSelected(false);
                permission5.setSelected(false);
                permission6.setSelected(false);

            });
        });
    }
    public void setUsername(String developer) {
        userNameLabel.setText(developer);
    }
    Stage refStage;
    public void setStage(Stage currentStage) {
        this.refStage= currentStage;
        // Initialize other components and logic
    }



}
