package in.sunfox.healthcare.java.spandan_qms.main_ui_controllers;

import in.sunfox.healthcare.java.spandan_qms.database_service.DatabaseUtility;
import in.sunfox.healthcare.java.spandan_qms.database_service.DeviceLogData;
import in.sunfox.healthcare.java.spandan_qms.database_service.OnDataReceiveListener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

public class ReturnDevice {
    VBox getRadioButtons(String uniqueDeviceId, String username, int stage) {
        VBox root = new VBox();
        root.setSpacing(20);
        Font font = Font.font("Times New Roman", FontPosture.REGULAR, 30);
        Text errorText = new Text("");
        Platform.runLater(() -> {
            errorText.setStyle("-fx-text-fill: red");
        });
        Text returnSuccessText = new Text("The Device Is Successfully Marked as returned with current Stage of -1");
        returnSuccessText.setFont(Font.font("Times New Roman", FontPosture.REGULAR, 20));
        BorderPane borderPane2 = new BorderPane();

        Label deviceId = new Label(uniqueDeviceId);

        deviceId.setFont(font);
        borderPane2.setLeft(deviceId);

        Text showHeading = new Text("Please Enter The Reason to return the device");
        VBox.setMargin(showHeading, new Insets(20, 0, 0, 0));
        ToggleGroup group = new ToggleGroup();
        RadioButton button1 = new RadioButton("Erase Device and Set Stage as 0");
        RadioButton button2 = new RadioButton("Go to Last Quality Test ");
        button1.setToggleGroup(group);
        button2.setToggleGroup(group);
        Button setStage = new Button("Set Stage");
        Button submitButton = new Button("Submit");


        TextField logDescription = new TextField();


        submitButton.setOnAction(actionEvent -> {


            if (!logDescription.getText().isEmpty()) {

                System.out.println(logDescription.getText());
                button1.setVisible(true);
                button2.setVisible(true);
                errorText.setText("");
                setStage.setVisible(true);
                long timeStamp = System.currentTimeMillis() / 1000;
                DeviceLogData deviceLogData = new DeviceLogData(username, timeStamp, -1, uniqueDeviceId, "pass", null, -1, null, logDescription.getText());
                OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                    @Override
                    public void onDataReceived(String data) {
                        Platform.runLater(() -> {
                            root.getChildren().removeAll(showHeading, logDescription, submitButton);
                            root.getChildren().add(0, returnSuccessText);
                        });

                    }

                    @Override
                    public void onDataReceiveError(String errorMsg) {
                        Platform.runLater(() -> {
                            errorText.setText(errorMsg);
                        });
                    }
                };
                DatabaseUtility.updateListOfLogOperationsInDatabase(deviceLogData, onDataReceiveListener);

            } else {
                System.out.println("Please fill the reason to return device");
                errorText.setText("Please fill the reason to return device");
            }
        });
        VBox.setMargin(button1, new Insets(20, 0, 0, 0));

        setStage.setOnAction(actionEvent -> {
            Toggle selectedToggle = group.getSelectedToggle();
            if (selectedToggle != null) {
                long timeStamp = System.currentTimeMillis() / 1000;
                RadioButton selectedRadioButton = (RadioButton) selectedToggle;
                String selectedText = selectedRadioButton.getText();
                int stageToBeUpdated = 0;
                String successText;
                if (selectedText.contains("Go to Last Quality Test ")) {

                    if (uniqueDeviceId.contains("SPPR") || uniqueDeviceId.contains("SPLG")) {
                        stageToBeUpdated = 6;

                    }
                    else {
                        stageToBeUpdated = 4;
                    }
                    successText = "Stage Has Been Successfully Set To "+stageToBeUpdated+" Kindle Reconnect The Device And Perform Quality Test 2";
                } else {
                    successText = "Stage Has Been Successfully Set To 0 Kindly Remove the device Erase It using St-Link and Add A New Firmware File .";
                }


                DeviceLogData deviceLogData = new DeviceLogData(username, timeStamp, stageToBeUpdated, uniqueDeviceId, "pass", null, 0, null, null);
                OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                    @Override
                    public void onDataReceived(String data) {
                        Platform.runLater(() -> {

                           root.getChildren().removeAll();
                           Text text = new Text(successText);
                           root.getChildren().add(text);
                        });

                    }

                    @Override
                    public void onDataReceiveError(String errorMsg) {
                        Platform.runLater(() -> {
                            errorText.setText(errorMsg);
                        });
                    }
                };
                DatabaseUtility.updateListOfLogOperationsInDatabase(deviceLogData, onDataReceiveListener);
                errorText.setText("");


            } else {
                errorText.setText("No option selected");
            }
        });
        if (stage != -1) {
            button1.setVisible(false);
            button2.setVisible(false);
            setStage.setVisible(false);
            root.getChildren().addAll(borderPane2, showHeading, logDescription, submitButton, button1, button2, setStage, errorText);
        } else {
            root.getChildren().addAll(returnSuccessText, borderPane2, button1, button2, setStage, errorText);
        }
//        root.getChildren().addAll(borderPane2,showHeading, logDescription, submitButton, button1, button2, setStage,errorText);
        return root;
    }
}