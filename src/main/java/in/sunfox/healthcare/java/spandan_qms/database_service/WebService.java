package in.sunfox.healthcare.java.spandan_qms.database_service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import static in.sunfox.healthcare.java.spandan_qms.utilitites.Constants.API_URL;
import static in.sunfox.healthcare.java.spandan_qms.utilitites.Constants.USER_WORKING_DIRECTORY;
import static javafx.scene.paint.Color.WHITE;


public class WebService {

    public WebService() {
//        String env = System.getenv("databaseEnvironment");
//        System.out.println("env variable value: " + env);
//        if ("debug".equalsIgnoreCase(env)) {
//            API_URL = "https://api.sunfox.in/qms/dev/v1";
//        } else if(env == null ||env.equals("production")) {
//            API_URL = "https://api.sunfox.in/qms/prod/v1";
//        }
        System.out.println(API_URL);
    }

    void setListInDatabase(DeviceLogData deviceLogData, OnDataReceiveListener<String> onDataReceiveListener) {
        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(deviceLogData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL + "/logs");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("token", retrieveToken());
            StringEntity entity = new StringEntity(jsonPayload.toString());
            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                if (responseBody.contains("Log added successfully"))
                    onDataReceiveListener.onDataReceived("Log Created");
                else {
                    Gson gsonResponse = new Gson();
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());

        }
    }

    public void updateStagesPermission(String emailId, int stagePermission, OnDataReceiveListener<String> onDataReceiveListener) {
        UserData userData = new UserData();

        userData.setEmail(emailId);
        userData.setStagesPermission(stagePermission);
        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(userData);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch httpPatch = new HttpPatch(API_URL + "/user");
            httpPatch.setHeader("Content-Type", "application/json");
            httpPatch.setHeader("token", retrieveToken());
            StringEntity entity = new StringEntity(jsonPayload);
            httpPatch.setEntity(entity);
            httpPatch.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("Stage permission updated successfully"))
                    onDataReceiveListener.onDataReceived("Stage permission updated successfully Kindly ask the user to relogin and continue");
                else
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
        }
    }

    public void getAllUsers(OnDataReceiveListener<Map<String, UserData>> onDataReceiveListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/user");
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", retrieveToken());

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);

                if (responseBody.contains("Unauthorized")) {
                    Gson gsonResponse = new Gson();
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                } else {
                    Gson gson = new GsonBuilder().create();
                    UserResponseWrapper userResponseWrapper = gson.fromJson(responseBody, UserResponseWrapper.class);
                    List<UserData> userDataList = userResponseWrapper.getUsers();

                    // Convert the list to a HashMap with user IDs as keys
                    Map<String, UserData> userDataMap = new HashMap<>();
                    for (UserData userData : userDataList) {
                        userDataMap.put(userData.getEmail(), userData);
                    }

                    onDataReceiveListener.onDataReceived(userDataMap);
                }
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void getStateFromDatabase(String uniqueDeviceId, OnDataReceiveListener<DeviceLogData> onDataReceiveListenerStateChange) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/logs/current/" + uniqueDeviceId);
            System.out.println(retrieveToken());
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                if (responseBody.contains("error") || responseBody.contains("Not a Device") || responseBody.contains("Unauthorized")) {
                    Gson gsonResponse = new Gson();
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onDataReceiveListenerStateChange.onDataReceiveError(errorResponse.getError());
                } else {
                    Gson gson = new GsonBuilder().create();
                    DeviceLogData deviceLogData = gson.fromJson(responseBody, DeviceLogData.class);
                    onDataReceiveListenerStateChange.onDataReceived(deviceLogData);
                }
            }
        } catch (Exception e) {
            onDataReceiveListenerStateChange.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
//        DeviceLogData deviceLogData = new DeviceLogData(null,0,6,null,null,null,0,null);
//        deviceLogData.setStage(2);
//        onDataReceiveListenerStateChange.onDataReceived(deviceLogData);
    }

    public void createNewUser(String name, String email, String phoneNumber, String password, OnDataReceiveListener<String> onDataReceiveListener) {
        UserData userData = new UserData();
        userData.setName(name);
        userData.setEmail(email);
        userData.setPhoneNumber(phoneNumber);
        userData.setPassword(password);
        userData.setRole(1);
        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(userData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL + "/user");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("token", "null");
            StringEntity entity = new StringEntity(jsonPayload.toString());
            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("User Created"))
                    onDataReceiveListener.onDataReceived("User Created");
                else {
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void validateLogin(String emailString, String passwordString, OnDataReceiveListener<ArrayList<Integer>> onDataReceiveListener) {
        UserData userData = new UserData();
        userData.setEmail(emailString);
        userData.setPassword(passwordString);
        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(userData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL + "/user/login");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("token", "null");
            StringEntity entity = new StringEntity(jsonPayload.toString());
            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("Wrong Email Or Password")) {
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                } else if (responseBody.contains("error")) {
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                } else {
                    UserData userData1 = gson.fromJson(responseBody, UserData.class);
                    saveToken(userData1.getToken());
                    ArrayList<Integer> result = new ArrayList<>();
                    result.add(userData1.getRole());
                    result.add(userData1.getStagesPermission());
                    System.out.println(result);
                    onDataReceiveListener.onDataReceived(result);
                }
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
        }
    }

    private void saveToken(String token) {
        Preferences preferences = Preferences.userRoot().node("com.sunfox.spandan-desktop");
        preferences.put("jwt_token", token);
    }

    private String retrieveToken() {
        Preferences preferences = Preferences.userRoot().node("com.sunfox.spandan-desktop");
        return preferences.get("jwt_token", null);
    }


    void createNewDevice(String uniqueDeviceId, String microControllerId, String batchId, String deviceVariant, OnDataReceiveListener<String> onDataReceiveListener, String version, String productionPlantId, String clientName, String masterKey) {
        DeviceData deviceData = new DeviceData();
        deviceData.setUniqueDeviceId(uniqueDeviceId);
        deviceData.setDeviceVariant(deviceVariant);
        deviceData.setBatchId(batchId);
        deviceData.setMicroControllerId(microControllerId);
        deviceData.setProductionPlantId(productionPlantId);
        deviceData.setFirmwareVersion(version);
        if (clientName.isEmpty())
            clientName = null;
        deviceData.setClientName(clientName);
        deviceData.setMasterKey(masterKey);

        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(deviceData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL + "/devices");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("token", retrieveToken());
            StringEntity entity = new StringEntity(jsonPayload.toString());
            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("Device created successfully"))
                    onDataReceiveListener.onDataReceived("Device created successfully");
                else {
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
        }
    }

    public void mapAccessories(String chestCable, String connectingCable, String uniqueDeviceId, OnDataReceiveListener<String> onDataReceiveListener) {
        Map<String, String> accessoriesMap = new HashMap<>();
        accessoriesMap.put("chestCable", chestCable);
        accessoriesMap.put("connectingCable", connectingCable);

        DeviceData deviceData = new DeviceData();
        deviceData.setAccessories(accessoriesMap);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Map.class, new DeviceData.AccessoriesSerializer())
                .create();
        String jsonPayload = gson.toJson(deviceData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch httpPatch = new HttpPatch(API_URL + "/devices/" + uniqueDeviceId);
//            HttpPost httpPost = new HttpPost("https://6b6psz28rd.execute-api.ap-south-1.amazonaws.com/dev/devices/mappingAccessories");
            httpPatch.setHeader("Content-Type", "application/json");
            httpPatch.setHeader("token", retrieveToken());
            StringEntity entity = new StringEntity(jsonPayload.toString());
            httpPatch.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("Accessories added successfully"))
                    onDataReceiveListener.onDataReceived("success");
                else
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
        }
    }

    public void getAllBatchesList(OnDataReceiveListener<Map<String, BatchData>> onDataReceiveListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/batches");
//            HttpPost httpPost = new HttpPost("https://6b6psz28rd.execute-api.ap-south-1.amazonaws.com/dev/getAllBatchesList");
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();

                if (responseBody.contains("error") || responseBody.contains("Unauthorized") || responseBody.contains("All Batches are full. No Batches Available. Ask the admin to create a new Batch")) {
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                } else {
                    Gson gson = new GsonBuilder().create();
                    Type batchListType = new TypeToken<List<BatchData>>() {
                    }.getType();

                    // Parse the JSON array directly into a List<BatchData>
                    List<BatchData> batchDataList = gson.fromJson(responseBody, batchListType);

                    // Create a HashMap with batchId as the key and BatchData as the value
                    Map<String, BatchData> batchDataMap = new HashMap<>();
                    for (BatchData batchData : batchDataList) {
                        batchDataMap.put(batchData.getBatchId(), batchData);
                    }
                    onDataReceiveListener.onDataReceived(batchDataMap);
                }
            }

        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }


    public void updateLatestDeviceNumberInBatch(String batchId, OnDataReceiveListener<String> onDataReceiveListener) {
        BatchData batchData = new BatchData();
        batchData.setBatchId(batchId);
        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(batchData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch httpPatch = new HttpPatch(API_URL + "/batches/" + batchId);
//            HttpPost httpPost = new HttpPost("https://6b6psz28rd.execute-api.ap-south-1.amazonaws.com/dev/updateLatestDeviceNumberInBatch");
            httpPatch.setHeader("Content-Type", "application/json");
            httpPatch.setHeader("token", retrieveToken());

            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("success") || responseBody.contains("full"))
                    onDataReceiveListener.onDataReceived("success");
                else
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());

            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void creatNewBatch(String batchId, Long batchSize, String username, String date, String batchDescriptionString, OnDataReceiveListener<String> onDataReceiveListener) {
        BatchData batchData = new BatchData();
        batchData.setBatchId(batchId);
        batchData.setBatchSize(batchSize);
        batchData.setUsername(username);
        batchData.setDate(date);
        batchData.setBatchDescriptionString(batchDescriptionString);
        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(batchData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL + "/batches");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("token", retrieveToken());
            StringEntity entity = new StringEntity(jsonPayload.toString());
            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("Batch created successfully"))
                    onDataReceiveListener.onDataReceived("Batch created successfully");
                else
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void fetchLatestExe(String version, OnDataReceiveListener<String> onDataReceiveListener, BorderPane mainBorderPane, Thread progressThread) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/getLatestexe/?version=" + version);
            httpGet.setHeader("Content-Type", "application/json");
            System.out.println(retrieveToken());
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                if (responseBody.contains("url")) {
                    FileUrlFetchedFromAws fileUrlFetchedFromAws = gsonResponse.fromJson(responseBody, FileUrlFetchedFromAws.class);
                    System.out.println(fileUrlFetchedFromAws.getUrl());

                    String targetFolderPath = System.getProperty("user.home") + File.separator + "Downloads";
                    String filename = fileUrlFetchedFromAws.getFileName();
                    onDataReceiveListener.onDataReceiveError(filename);
                    try {
                        // Construct the path for the new file
                        Path newExeFile = Paths.get(targetFolderPath, filename);
                        InputStream in = new URL(fileUrlFetchedFromAws.getUrl().toString()).openStream();
                        Files.copy(in, newExeFile);
                        System.out.println("File downloaded to: " + newExeFile.toString());
                        Platform.runLater(() -> {
                            progressThread.interrupt();
                            Alert customDialog = new Alert(Alert.AlertType.NONE);
                            DialogPane dialogPane = new DialogPane();
                            VBox contentPane = new VBox();
                            contentPane.setAlignment(Pos.CENTER);
                            contentPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;-fx-padding: 12px;");
                            dialogPane.setStyle("-fx-background-color:white");
                            contentPane.setSpacing(15);
                            Button button = new Button("Okay ");
                            button.setStyle("-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;");
                            // Set hover effect using mouse entered and exited event handlers
                            button.setOnMouseEntered(event -> button.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                            button.setOnMouseExited(event -> button.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                            button.setTextFill(WHITE);
                            button.setOnAction(event -> {
                                Stage primaryStage = (Stage) mainBorderPane.getScene().getWindow();
                                primaryStage.close();
                                mainBorderPane.setEffect(null);
                                Stage stage = (Stage) dialogPane.getScene().getWindow();
                                stage.close();
                                Platform.exit();
                                Thread thread = new Thread(() -> {
                                    try {
                                        Path exePath = Paths.get(targetFolderPath, filename);

                                        ProcessBuilder processBuilder = new ProcessBuilder(exePath.toString());

                                        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                                        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

                                        Process process = processBuilder.start();

                                        int exitCode = process.waitFor();
                                        if (exitCode == 0) {
                                            System.out.println("Execution completed successfully.");
                                        } else {
                                            System.err.println("Execution failed with exit code: " + exitCode);
                                        }
                                    } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                                thread.start();

                                Thread thread1 = new Thread(() -> {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.exit(0);
                                });
                                thread1.start();
                            });
                            BoxBlur boxBlur = new BoxBlur(15, 15, 25);
                            mainBorderPane.setEffect(boxBlur);
                            Label label = new Label("Latest Version Of Application is available in your downloads Folder kindly reinstall it .");
                            label.setTextAlignment(TextAlignment.CENTER);
                            label.setWrapText(true);
                            label.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
                            label.setTextFill(Paint.valueOf("#595959"));
                            contentPane.getChildren().addAll(label, button);
                            dialogPane.setContent(contentPane);
                            customDialog.setDialogPane(dialogPane);
                            customDialog.initStyle(StageStyle.UNDECORATED);
                            customDialog.initModality(Modality.WINDOW_MODAL);
                            dialogPane.setPrefWidth(350);
                            dialogPane.setPrefHeight(300);
                            customDialog.showAndWait();
                        });
                    } catch (Exception e) {
                        onDataReceiveListener.onDataReceiveError(e.toString());
                        System.err.println("Error occurred while fetching the EXE: " + e.getMessage());
                    }
                } else {
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            }
        } catch (IOException e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            System.err.println("Error occurred while fetching the EXE: " + e.getMessage());
        }
    }

    public void fetchLatestFirmwareFile(String fileVersion, OnDataReceiveListener<String> onDataReceiveListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/getLatestVersionFile?version=" + fileVersion);
            httpGet.setHeader("Content-Type", "application/json");
            System.out.println(retrieveToken());
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                if (responseBody.contains("url")) {
                    FileUrlFetchedFromAws fileUrlFetchedFromAws = gsonResponse.fromJson(responseBody, FileUrlFetchedFromAws.class);
                    System.out.println(fileUrlFetchedFromAws.getUrl());

                    String targetFolderPath = USER_WORKING_DIRECTORY + "/SPLGFirmware/";
                    String filename = fileUrlFetchedFromAws.getFileName();

                    // Delete all previous files in the "Firmware" folder

                    try {
                        InputStream in = new URL(fileUrlFetchedFromAws.getUrl().toString()).openStream();
                        deleteAllFilesInFolder(Paths.get(targetFolderPath));
                        Path targetPath = Paths.get(targetFolderPath + filename);

                        // Create the target folder if it doesn't exist
                        if (Files.notExists(Paths.get(targetFolderPath))) {
                            Files.createDirectories(Paths.get(targetFolderPath));
                        }

                        // Check if the target file already exists and replace it
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);

                        System.out.println("New firmware file downloaded and saved successfully!");
                        onDataReceiveListener.onDataReceived("SUCCESS");
                    } catch (IOException e) {
                        onDataReceiveListener.onDataReceiveError(e.toString());
                        System.err.println("Error occurred while updating the firmware: " + e.getMessage());
                        e.printStackTrace(); // Log the full stack trace for debugging
                    }

                } else {
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            }
        } catch (IOException e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            System.err.println("Error occurred while fetching the firmware: " + e.getMessage());
        }
    }

    public void fetchLatestFirmwareFileSpandanNeo(String fileVersion, OnDataReceiveListener<String> onDataReceiveListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/getLatestVersionFileNeo?version=" + fileVersion);
            httpGet.setHeader("Content-Type", "application/json");
            System.out.println(retrieveToken());
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                if (responseBody.contains("url")) {
                    FileUrlFetchedFromAws fileUrlFetchedFromAws = gsonResponse.fromJson(responseBody, FileUrlFetchedFromAws.class);
                    System.out.println(fileUrlFetchedFromAws.getUrl());

                    String targetFolderPath = USER_WORKING_DIRECTORY + "/SPNEFirmware/";
                    String filename = fileUrlFetchedFromAws.getFileName();

                    try {
                        InputStream in = new URL(fileUrlFetchedFromAws.getUrl().toString()).openStream();
                        deleteAllFilesInFolder(Paths.get(targetFolderPath));
                        Path targetPath = Paths.get(targetFolderPath + filename);

                        // Create the target folder if it doesn't exist
                        if (Files.notExists(Paths.get(targetFolderPath))) {
                            Files.createDirectories(Paths.get(targetFolderPath));
                        }

                        // Check if the target file already exists and replace it
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);

                        System.out.println("New firmware file downloaded and saved successfully!");
                        onDataReceiveListener.onDataReceived("SUCCESS");
                    } catch (IOException e) {
                        onDataReceiveListener.onDataReceiveError(e.toString());
                        System.err.println("Error occurred while updating the firmware: " + e.getMessage());
                        e.printStackTrace(); // Log the full stack trace for debugging
                    }
                } else {
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            }
        } catch (IOException e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            System.err.println("Error occurred while fetching the firmware: " + e.getMessage());
        }
    }

    public void fetchLatestFirmwareFileSpandanPro(String fileVersion, OnDataReceiveListener<String> onDataReceiveListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/getLatestVersionFilePro?version=" + fileVersion);
            httpGet.setHeader("Content-Type", "application/json");
            System.out.println(retrieveToken());
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                if (responseBody.contains("url")) {
                    FileUrlFetchedFromAws fileUrlFetchedFromAws = gsonResponse.fromJson(responseBody, FileUrlFetchedFromAws.class);
                    System.out.println(fileUrlFetchedFromAws.getUrl());
                    String targetFolderPath = USER_WORKING_DIRECTORY + "/SPPrFirmware/";
//                    String targetFolderPath = "SPPrFirmware/";
                    String filename = fileUrlFetchedFromAws.getFileName();

                    try {
                        InputStream in = new URL(fileUrlFetchedFromAws.getUrl().toString()).openStream();
                        deleteAllFilesInFolder(Paths.get(targetFolderPath));
                        Path targetPath = Paths.get(targetFolderPath + filename);

                        // Create the target folder if it doesn't exist
                        if (Files.notExists(Paths.get(targetFolderPath))) {
                            Files.createDirectories(Paths.get(targetFolderPath));
                        }

                        // Check if the target file already exists and replace it
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);

                        System.out.println("New firmware file downloaded and saved successfully!");
                        onDataReceiveListener.onDataReceived("SUCCESS");
                    } catch (IOException e) {
                        onDataReceiveListener.onDataReceiveError(e.toString());
                        System.err.println("Error occurred while updating the firmware: " + e.getMessage());
                        e.printStackTrace(); // Log the full stack trace for debugging
                    }
                } else {
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            }
        } catch (IOException e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            System.err.println("Error occurred while fetching the firmware: " + e.getMessage());
        }
    }


    private static void deleteAllFilesInFolder(Path folder) throws IOException {
        if (Files.exists(folder) && Files.isDirectory(folder)) {
            Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            System.err.println("Error deleting file: " + file + ", " + e.getMessage());
                        }
                    });
        }
    }


    public void clearToken(OnDataReceiveListener<String> onDataReceiveListener) {

        Preferences preferences = Preferences.userRoot().node("com.sunfox.spandan-desktop");
        try {
            preferences.clear();
            onDataReceiveListener.onDataReceived("succcess");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllLogsOfDevice(String uniqueDeviceId, OnDataReceiveListener<DeviceLogData[]> onDataReceiveListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/logs/" + uniqueDeviceId); // Use the provided uniqueDeviceId
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);


                if (responseBody.contains("error")) {
                    Gson gsonResponse = new Gson();
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                } else {
                    Gson gson = new Gson();
                    // Convert the JSON response into a LogsResponse object
                    LogsResponse logsResponse = gson.fromJson(responseBody, LogsResponse.class);
                    // Get the logs list from the LogsResponse and convert it to an array
                    DeviceLogData[] deviceLogDataArray = logsResponse.getLogs().toArray(new DeviceLogData[0]);
                    if (deviceLogDataArray.length < 1) {
                        onDataReceiveListener.onDataReceiveError("Not a Device");
                    } else {
                        onDataReceiveListener.onDataReceived(deviceLogDataArray);
                    }
                }
            }

        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void getLogEcgData(String uniqueDeviceId, OnDataReceiveListener<String> onDataReceiveListener, String stage) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/logs/ecgdata/" + uniqueDeviceId);
            httpGet.setHeader("Content-Type", "application/json");
            System.out.println(retrieveToken());
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                if (responseBody.contains("url")) {
                    FileUrlFetchedFromAws fileUrlFetchedFromAws = gsonResponse.fromJson(responseBody, FileUrlFetchedFromAws.class);
                    System.out.println(fileUrlFetchedFromAws.getUrl());
                    try {
                        StringBuilder fileContent = new StringBuilder();
                        InputStream in = new URL(fileUrlFetchedFromAws.getUrl().toString()).openStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            fileContent.append(line);
                        }


                        onDataReceiveListener.onDataReceived(String.valueOf(fileContent));

                    } catch (Exception e) {
                        onDataReceiveListener.onDataReceiveError("Error occurred : " + e.getMessage());

                    }
                } else {
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    System.out.println(errorResponse.getError());
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                }
            } catch (Exception e) {
                onDataReceiveListener.onDataReceiveError("Error occurred : " + e.getMessage());

            }


        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError("Error occurred : " + e.getMessage());
        }
    }

    public void getDeviceData(String uniqueDeviceId, OnDataReceiveListener<DeviceData> onDataReceiveListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/devices/" + uniqueDeviceId); // Use the provided uniqueDeviceId
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);


                if (responseBody.contains("error")) {
                    Gson gsonResponse = new Gson();
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());
                } else {
                    Gson gson = new GsonBuilder().create();
                    DeviceData deviceData = gson.fromJson(responseBody, DeviceData.class);
                    onDataReceiveListener.onDataReceived(deviceData);
                }
            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void getDeviceIdFromMid(String microControllerId, OnDataReceiveListener<ArrayList<String>> onReceiveDataListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/devices/microControllerId/" + microControllerId); // Use the provided uniqueDeviceId
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);

                if (responseBody.contains("error")) {
                    Gson gsonResponse = new Gson();
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onReceiveDataListener.onDataReceiveError(errorResponse.getError());
                } else {
                    Gson gson = new GsonBuilder().create();
                    DeviceData deviceData = gson.fromJson(responseBody, DeviceData.class);
                    ArrayList<String> result = new ArrayList<>();
                    result.add(deviceData.getUniqueDeviceId());
                    result.add(deviceData.getMasterKey());
                    onReceiveDataListener.onDataReceived(result);
                }
            }
        } catch (Exception e) {
            onReceiveDataListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void getB2bClients(OnDataReceiveListener<Map<String, B2BOrganization>> onReceiveDataListener) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(API_URL + "/b2b-organizations"); // Use the provided uniqueDeviceId
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("token", retrieveToken());
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);

                if (responseBody.contains("error")) {
                    Gson gsonResponse = new Gson();
                    ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                    onReceiveDataListener.onDataReceiveError(errorResponse.getError());
                } else {
                    Gson gson = new GsonBuilder().create();
                    Type batchListType = new TypeToken<List<B2BOrganization>>() {
                    }.getType();

                    // Parse the JSON array directly into a List<BatchData>
                    List<B2BOrganization> b2BOrganizationList = gson.fromJson(responseBody, batchListType);

                    // Create a HashMap with batchId as the key and BatchData as the value
                    Map<String, B2BOrganization> batchDataMap = new HashMap<>();
                    for (B2BOrganization data : b2BOrganizationList) {
                        batchDataMap.put(data.getOrganisation_name(), data);
                    }
                    onReceiveDataListener.onDataReceived(batchDataMap);
                    System.out.println(batchDataMap);

                }
            }
        } catch (Exception e) {
            onReceiveDataListener.onDataReceiveError(e.toString());
            e.printStackTrace();
        }
    }

    public void updateDeviceTable(String uniqueDeviceId, String firmwareVersion, OnDataReceiveListener<String> onDataReceiveListener) {

        DeviceData deviceData = new DeviceData();
        deviceData.setFirmwareVersion(firmwareVersion);
        Gson gson = new GsonBuilder().create();
        String jsonPayload = gson.toJson(deviceData);
        System.out.println(jsonPayload);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch httpPatch = new HttpPatch(API_URL + "/devices/update/" + uniqueDeviceId);
//            HttpPost httpPost = new HttpPost("https://6b6psz28rd.execute-api.ap-south-1.amazonaws.com/dev/updateLatestDeviceNumberInBatch");
            httpPatch.setHeader("Content-Type", "application/json");
            httpPatch.setHeader("token", retrieveToken());
            StringEntity entity = new StringEntity(jsonPayload.toString());
            httpPatch.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Status Code: " + statusCode);
                System.out.println("Response Body: " + responseBody);
                Gson gsonResponse = new Gson();
                ErrorResponse errorResponse = gsonResponse.fromJson(responseBody, ErrorResponse.class);
                if (responseBody.contains("success") || responseBody.contains("full"))
                    onDataReceiveListener.onDataReceived("success");
                else
                    onDataReceiveListener.onDataReceiveError(errorResponse.getError());

            }
        } catch (Exception e) {
            onDataReceiveListener.onDataReceiveError(e.toString());
            e.printStackTrace();

        }
    }
}



