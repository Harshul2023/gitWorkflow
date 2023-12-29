package in.sunfox.healthcare.java.spandan_qms.main_ui_controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXSnackbar;
import in.sunfox.healthcare.java.spandan_qms.*;
import in.sunfox.healthcare.java.spandan_qms.bar_code_generator.BarCodeGenerator;
import in.sunfox.healthcare.java.spandan_qms.bar_code_generator.BarcodePrinter;
import in.sunfox.healthcare.java.spandan_qms.database_service.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import in.sunfox.healthcare.java.spandan_qms.device_fLashing_and_configuration_classes.DeviceConfigurationNeo;
import in.sunfox.healthcare.java.spandan_qms.device_fLashing_and_configuration_classes.FlashingNewDeviceClass;
import in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions.InterPretetions;
import in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions.LoadTwelveLeadReferenceSignal;
import in.sunfox.healthcare.java.spandan_qms.ecg_processsor_interpretetions.SignalsFromRPeaks;
import in.sunfox.healthcare.java.spandan_qms.pdf_generation.PdfGenerationClass;
import in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.serialcommunicationmodule.OnReceiveDataListenerFromModuleToUI;
import in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.serialcommunicationmodule.SpandanUsbCommunication;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static javafx.scene.paint.Color.*;

public class MainUiSpandanNeo implements Initializable {

    public String neoStagesermission;
    public String spandanStagesPermission;


    @FXML
    private FontAwesomeIconView InternetAvailable;

    @FXML
    private Text heartRateTextActual;

    @FXML
    private Text heartRateTextExpected;

    @FXML
    private Text prTextActual;

    @FXML
    private Text prTextExpected;

    @FXML
    private Text qrsTextActual;

    @FXML
    private Text qrsTextExpected;

    @FXML
    private Text qtTextActual;
    @FXML
    private Text qAmplitudeTextActual;
    @FXML
    private Text rAmplitudeTextActual;
    @FXML
    private Text sAmplitudeTextActual;
    private final List<Alert> openDialogs = new ArrayList<>();

    @FXML
    private Text qtTextExpected;

    @FXML
    private Text qtcTextActual;

    @FXML
    private Text qtcTextExpected;
    @FXML
    private Text qAmplitudeTextExpected;
    @FXML
    private Text rAmplitudeTextExpected;
    @FXML
    private Text sAmplitudeTextExpected;


    @FXML
    private Text connectedDeviceId;

    @FXML
    private Label deviceDetectionLabel;

    @FXML
    private Text deviceStageText;

    @FXML
    private Button retakeTest;

    @FXML
    private LineChart<Number, Number> ecgGraphLineChart;


    @FXML
    private Text stage1;

    @FXML
    private Text stage2;

    @FXML
    private Text stage3;

    @FXML
    private Text stage4;


    @FXML
    private Text stage5;

    @FXML
    private Text stage6;

    @FXML
    private Text stage7;

    @FXML
    private Label heartRateRemark;
    @FXML
    private Label qrsRemark;
    @FXML
    private Label qtRemark;
    @FXML
    private Label qtcRemark;
    @FXML
    private Label prRemark;

    @FXML
    private Label usernameLabel;
    @FXML
    private VBox boxContainingStages;

    @FXML
    private ProgressBar progressBarFetchingStage;


    @FXML
    private ScrollPane resultPane;
    public static String uniqueDeviceId = null;

    @FXML
    private AnchorPane deviceDetailsAnchorPane;

    public BooleanProperty deviceDetectedFromThread = new SimpleBooleanProperty(false);
    Thread deviceDetectingThread;
    SerialPort mySerialPort;
    public static boolean firmwareOperationInProgress;
    private boolean isGraphActive;
    //    @FXML
//    Button newDeviceButton;
    boolean testingAction = false;
    int currentStageOfPcb;
    private String username;


//    ArrayList<ArrayList<Double>> twelveLeadArrayList = new ArrayList<ArrayList<Double>>();

    public MainUiSpandanNeo() {
        Thread connectionThread = new Thread(() -> {
            while (true) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress("www.google.com", 80), 2000);
                    Platform.runLater(() -> {
                        InternetAvailable.setFill(Paint.valueOf("#159300"));
                    });
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        InternetAvailable.setFill(Paint.valueOf("#BA2707"));
                    });

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        connectionThread.start();

    }

    Label graphProgressLabel = new Label();
    //    ProgressIndicator graphLoadingProgressIndicator;
    @FXML
    private StackPane paneContainingGraph;
    //    XYChart.Series<String, Number> graphDataSeries = new XYChart.Series<>();
    final int graph_Window_Size = 2000;
    static int graphPointsIncrementer = 0;
    ArrayList<Double> ecgPointsArrayList = new ArrayList<>();
    int numberOfEcgPointsReceived = 0;
    private long timeOf1stPoint;
    @FXML
    private CheckBox referenceSignalTestingCheckBox;
    @FXML
    private Button passTestBtn;
    @FXML
    private Button failTestBtn;

    @FXML
    private Circle circle1;

    @FXML
    private Circle circle2;

    @FXML
    private Circle circle3;

    @FXML
    private Circle circle4;

    @FXML
    private Circle circle5;

    @FXML
    private Circle circle6;
    @FXML
    private Circle circle0;

    @FXML
    private Circle circle7;
    @FXML
    private Button nextLeadTwelveLeadBtn;
    public int twelveLeadIncrementer = 0;
    ArrayList<ArrayList<Double>> twelveLeadArrayList = null;
    @FXML
    private LineChart<Number, Number> v1Chart;

    @FXML
    private LineChart<Number, Number> v2Chart;

    @FXML
    private LineChart<Number, Number> v3Chart;

    @FXML
    private LineChart<Number, Number> v4Chart;

    @FXML
    private LineChart<Number, Number> v5Chart;

    @FXML
    private LineChart<Number, Number> v6Chart;
    @FXML
    private LineChart<Number, Number> l1Chart;
    @FXML
    private LineChart<Number, Number> l2Chart;

    @FXML
    private NumberAxis v1Axis;


    @FXML
    private void retakeLeadAction() {
        twelveLeadArrayList.remove(twelveLeadArrayList.size() - 1);
        nextLeadTwelveLeadBtn.setDisable(true);
        retakeLead.setDisable(true);
        if (twelveLeadIncrementer <= 7) {
            startGraphAction();
        }
    }

    @FXML
    public void nextLeadTwelveLeadAction() {
        increment12LeadAction();
        nextLeadTwelveLeadBtn.setDisable(true);
        retakeLead.setDisable(true);
        if (twelveLeadIncrementer <= 7) {
            startGraphAction();
        }
    }
    @FXML
    private VBox resultPaneVbox;
    @FXML
    private StackPane v1StackPane;
    @FXML
    private StackPane v2StackPane;
    @FXML
    private StackPane v3StackPane;
    @FXML
    private StackPane v4StackPane;
    @FXML
    private StackPane v5StackPane;
    @FXML
    private StackPane v6StackPane;
    @FXML
    private StackPane l1StackPane;
    @FXML
    private StackPane l2StackPane;
    private void clear12LeadProgress() {
        Platform.runLater(() -> {
            for (int i = 0; i <= 7; i++) {
                Scene currentScene = mainBorderPane.getScene();
                String circleLead = "circle" + i;
                Circle circle = (Circle) currentScene.lookup("#" + circleLead);
                circle.setStyle(null);
            }
        });
    }

    XYChart.Series<Number, Number> graphDataSeries = new XYChart.Series<>();

    void startGraphAction() {
        ecgPointsArrayList = new ArrayList<>();
        referenceSignalTestingCheckBox.setSelected(false);
        graphDataSeries.getData().clear();
        ecgGraphLineChart.getData().remove(graphDataSeries);

        Scene currentScene = boxContainingStages.getScene();

        Platform.runLater(() -> {
            if(!resultPaneVbox.getChildren().contains(v1StackPane)) {
                resultPaneVbox.setPrefHeight(1806);
                resultPaneVbox.getChildren().add(1, v1StackPane);
                resultPaneVbox.getChildren().add(2, v2StackPane);
                resultPaneVbox.getChildren().add(3, v3StackPane);
                resultPaneVbox.getChildren().add(4, v4StackPane);
                resultPaneVbox.getChildren().add(5, v5StackPane);
                resultPaneVbox.getChildren().add(6, v6StackPane);
                resultPaneVbox.getChildren().add(7, l1StackPane);
            }
        });
        Platform.runLater(() -> {
            String circleLead = "circle" + twelveLeadIncrementer;
            Circle circle = (Circle) currentScene.lookup("#" + circleLead);
            String style = "-fx-fill : #E37F09";
            circle.setRadius(20);
            circle.setStyle(style);
        });

        testingAction = true;
        resultPane.setVisible(false);
        if (deviceDetectedFromThread.getValue() && !isGraphActive) {
            Platform.runLater(() -> {
                timeLeftLabel.setText("");
            });
            Thread portConnectionThread = new Thread(() -> {
                Platform.runLater(() -> {
                    Font font = Font.font("System", FontWeight.BOLD, 20);
                    graphProgressLabel.setPadding(new Insets(80, 0, 0, 0)); //top, right, bottom, left
                    graphProgressLabel.setFont(font);
                    graphProgressLabel.setText("Graph Loading please wait!!!......");
                    if (!paneContainingGraph.getChildren().contains(graphProgressLabel))
                        paneContainingGraph.getChildren().add(graphProgressLabel);
                });
                try {
                    SpandanUsbCommunication.connectPort(mySerialPort);
//                    Thread.sleep(1500);
                    SpandanUsbCommunication.sendCommand("start", graphProgressLabel);
//                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            portConnectionThread.start();
            Thread graphProgressBarThread = new Thread(() -> {
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(0.0);
                });
                int i = 10;
                while (i != 0) {
                    int finalI = i;
                    Platform.runLater(() -> {
                        graphProgressBar.setProgress(graphProgressBar.getProgress() + 0.10);
                        timeLeftLabel.setText(finalI - 1 + " Seconds Remaining....");
                    });
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Platform.runLater(() -> {
                            timeLeftLabel.setText("");
                            graphProgressBar.setProgress(0);
                        });
                        break;
                    }
                }
            });

            numberOfEcgPointsReceived = 0;
            Platform.runLater(() ->
            {
//                resultShowingPane.getChildren().clear();
                ecgGraphLineChart.setVisible(true);
                NumberAxis axis = (NumberAxis) ecgGraphLineChart.getXAxis();
                axis.setLowerBound(0);
                axis.setUpperBound(graph_Window_Size);
//                ecgGraphLineChart.getData().clear();
            });
            paneContainingGraph.setVisible(true);
            ecgPointsArrayList.clear();
            graphPointsIncrementer = 0;
            isGraphActive = true;

            Platform.runLater(() -> {
                ecgGraphLineChart.getData().add(graphDataSeries);
                graphDataSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: black;" + "-fx-stroke-width: 1.5px");
            });
            ecgGraphLineChart.getStyleClass().add("chart-area");
            OnReceiveDataListenerFromModuleToUI onReceiveDataListenerFromModuleToUI = new OnReceiveDataListenerFromModuleToUI() {
                @Override
                public void usbOnDataReceive(String dataFromSerialPort) throws InterruptedException {
//                    System.out.println(dataFromSerialPort);
                    if (dataFromSerialPort.equals("error")) {
                        Platform.runLater(() -> {
                            DialogPane dialogPane = new DialogPane();
                            VBox contentPane = new VBox();
                            contentPane.setAlignment(Pos.TOP_CENTER);
                            contentPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;-fx-padding: 12px;");
                            dialogPane.setStyle("-fx-background-color:white");
                            contentPane.setSpacing(15);
                            Button button = new Button("CLOSE");
                            button.setStyle("-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;");
                            // Set hover effect using mouse entered and exited event handlers
                            button.setOnMouseEntered(event -> button.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                            button.setOnMouseExited(event -> button.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                            button.setTextFill(WHITE);
                            button.setOnAction(event -> {
                                mainBorderPane.setEffect(null);
                                Stage stage = (Stage) dialogPane.getScene().getWindow();
                                stage.close();
                            });
                            BoxBlur boxBlur = new BoxBlur(15, 15, 25);
                            mainBorderPane.setEffect(boxBlur);
                            ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/Group 7383.png");
                            VBox.setMargin(imageView, new Insets(10, 0, 0, 0));
                            Label label = new Label("""
                                    Process interrupted due to
                                    communication loss with the device.
                                    Ensure that the device is properly
                                    connected and try again.
                                    """);
                            label.setTextAlignment(TextAlignment.CENTER);

                            label.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
                            label.setTextFill(Paint.valueOf("#595959"));
                            contentPane.getChildren().addAll(imageView, label, button);
                            dialogPane.setContent(contentPane);
                            Alert customDialog = new Alert(Alert.AlertType.NONE);
                            customDialog.setDialogPane(dialogPane);
                            customDialog.initStyle(StageStyle.UNDECORATED);
                            customDialog.initModality(Modality.WINDOW_MODAL);
                            dialogPane.setPrefWidth(350); // Set your desired width
                            dialogPane.setPrefHeight(330);
                            // Set your desired height;
                            openDialogs.add(customDialog);
                            customDialog.showAndWait();
                        });

                        clear12LeadProgress();
                        isGraphActive = false;
                        Platform.runLater(() -> {
                            ecgGraphLineChart.setVisible(false);
                            paneContainingGraph.setVisible(false);
                        });
                        graphProgressBarThread.interrupt();
                        Platform.runLater(() -> {
                            graphProgressBar.setProgress(0);
                            timeLeftLabel.setText("");
                        });
                        mySerialPort.closePort();
                        mySerialPort = null;
                    } else if (dataFromSerialPort.equals("Port Closed")) {
//                        System.out.println(ecgPointsArrayList);

                        Platform.runLater(() -> {
                            graphProgressBar.setProgress(0);
                            timeLeftLabel.setText("");
                        });
                        isGraphActive = false;
                        if (twelveLeadIncrementer < 7) {

                            if (currentStageOfPcb != 2) {
                                nextLeadTwelveLeadBtn.setDisable(false);
                                Platform.runLater(() -> {
                                    timeLeftLabel.setText("Please Switch to next Lead Position");
                                });
                            }
                            retakeLead.setDisable(false);

                        }
                        if (twelveLeadIncrementer == 7 || currentStageOfPcb == 2) {
                            if (currentStageOfPcb != 2)
                                increment12LeadAction();

                            showResultAction();
                        }
                        twelveLeadArrayList.add(ecgPointsArrayList);
                        for (int i = 0; i < twelveLeadArrayList.size(); i++) {
                            System.out.println(i + "--->" + twelveLeadArrayList.get(i));
                        }

                    } else if (isGraphActive && deviceDetectedFromThread.getValue()) {
                        if (numberOfEcgPointsReceived < 5000) {
                            if (numberOfEcgPointsReceived == 0) {
                                graphProgressBarThread.start();
                            }
                            if (numberOfEcgPointsReceived == 4999) {
                                Platform.runLater(() -> {
                                    timeLeftLabel.setText("");
                                    graphProgressBar.setProgress(1);
                                });
                            }
                            numberOfEcgPointsReceived++;
                            ecgPointsArrayList.add(Double.valueOf(dataFromSerialPort));
                            Platform.runLater(() -> {
                                graphProgressLabel.setText("");
                                if (graphDataSeries.getData().size() >= graph_Window_Size) {
                                    double lowerBound = graphDataSeries.getData().size() - graph_Window_Size;
                                    double upperBound = graphDataSeries.getData().size() - 1;
                                    ecgGraphLineChart.getXAxis().setAutoRanging(false);
                                    NumberAxis axis = (NumberAxis) ecgGraphLineChart.getXAxis();
                                    axis.setUpperBound(upperBound);
                                    axis.setLowerBound(lowerBound);
                                }

                                graphDataSeries.getData().add(new XYChart.Data<>(graphPointsIncrementer++, Double.valueOf(dataFromSerialPort)));
                            });
                        } else {
                            SpandanUsbCommunication.sendCommand("stop", graphProgressLabel);
                            isGraphActive = false;
                        }
                    }
                }

                @Override
                public void usbAuthentication(String data) {
                }
            };
            try {
                SpandanUsbCommunication.onReceiveDataListenerFromModuleToUI = onReceiveDataListenerFromModuleToUI;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!isGraphActive) Platform.runLater(() -> {
            JFXSnackbar snackbar = new JFXSnackbar(paneContainingGraph);
            snackbar.show("Device Not FOUND", 750);
            deviceDetectedFromThread.set(false);
        });
    }

    private void increment12LeadAction() {


        Platform.runLater(() -> {
            Scene currentScene = boxContainingStages.getScene();
            String circleLead = "circle" + twelveLeadIncrementer;
            Circle circle = (Circle) currentScene.lookup("#" + circleLead);
            String style = "-fx-fill : #49B23E";
            circle.setStyle(style);
            circle.setRadius(13);
            twelveLeadIncrementer++;
        });

    }

    @FXML
    private Text pointsText;
    @FXML
    private Text timeTakenText;
    @FXML
    private BorderPane newDeviceBorderPane;
    @FXML
    private Label cofficientLabel;
    @FXML
    private Label testPassOrFailLabel;
    @FXML
    private Text twelveLeadEcgTestText;
    @FXML
    private Text resultHeading;
    @FXML
    private Label qAmplitudeRemark;
    @FXML
    private Label rAmplitudeRemark;
    @FXML
    private Label sAmplitudeRemark;
    @FXML
    private Text clientType;

    private void showResultAction() {


        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series4 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series5 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series6 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series7 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series8 = new XYChart.Series<>();
        Thread thread = new Thread(() -> {

            if (currentStageOfPcb == 2) {
                Platform.runLater(() -> {
                    resultPaneVbox.setPrefHeight(1100);
                    resultPaneVbox.getChildren().remove(1, 8);
                });
            }
            Platform.runLater(() -> {
                graphProgressBar.setProgress(0);
                timeLeftLabel.setText("Generating Result Please Wait");
            });

            LineChart<Number, Number>[] charts = new LineChart[]{v1Chart, v2Chart, v3Chart, v4Chart, v5Chart, v6Chart, l1Chart, l2Chart};

            for (LineChart<Number, Number> chart : charts) {
                for (XYChart.Series<Number, Number> series : chart.getData()) {
                    Platform.runLater(() -> {
                        chart.getData().remove(series);
                    });
                }
            }
            XYChart.Series<Number, Number> seriesRefV1 = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesRefV2 = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesRefV3 = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesRefV4 = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesRefV5 = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesRefV6 = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesRefL1 = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesRefL2 = new XYChart.Series<>();
            ArrayList<ArrayList<Double>> truncatedTestSignal = new ArrayList<>();

            for (ArrayList<Double> signal : twelveLeadArrayList) {
                ArrayList<Double> copySignal = new ArrayList<>(signal);
                truncatedTestSignal.add(copySignal);
            }
            SignalsFromRPeaks signalsFromRPeaks = new SignalsFromRPeaks();
            if (currentStageOfPcb != 2)
                truncatedTestSignal = signalsFromRPeaks.findRpeaks(truncatedTestSignal, 0);
            Platform.runLater(() -> {
                graphProgressBar.setProgress(0.25);
                timeLeftLabel.setText("Refernce Signal Loaded");
            });


            LoadTwelveLeadReferenceSignal loadTwelveLeadReferenceSignal = new LoadTwelveLeadReferenceSignal();
            if (currentStageOfPcb != 2) {
                LogEcgData logEcgData = loadTwelveLeadReferenceSignal.loadSignal(1);

                ArrayList<Double> dataPoints = truncatedTestSignal.get(0);
                int size = Math.min(dataPoints.size(), logEcgData.getV1().size());
                for (int j = 0; j < size; j++) {
                    series1.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefV1.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getV1().get(j)));
                }
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(0.30);

                });

                int finalSize = size;
                Platform.runLater(() -> {
                    v1Chart.getData().add(series1);
                    NumberAxis axis = (NumberAxis) v1Chart.getXAxis();
                    axis.setUpperBound(finalSize);
                    v1Chart.layout();
                });
                dataPoints = truncatedTestSignal.get(1);
                size = Math.min(dataPoints.size(), logEcgData.getV2().size());
                for (int j = 0; j < size; j++) {
                    series2.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefV2.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getV2().get(j)));
                }
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(0.35);

                });

                int finalSize1 = size;
                Platform.runLater(() -> {
                    v2Chart.getData().add(series2);
                    NumberAxis axis = (NumberAxis) v2Chart.getXAxis();
                    axis.setUpperBound(finalSize1);
                    v2Chart.layout();
                });

                dataPoints = truncatedTestSignal.get(2);
                size = Math.min(dataPoints.size(), logEcgData.getV3().size());
                for (int j = 0; j < size; j++) {
                    series3.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefV3.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getV3().get(j)));

                }
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(0.40);

                });
                int finalSize2 = size;
                Platform.runLater(() -> {
                    v3Chart.getData().add(series3);
                    NumberAxis axis = (NumberAxis) v3Chart.getXAxis();
                    axis.setUpperBound(finalSize2);
                    v3Chart.layout();

                });

                dataPoints = truncatedTestSignal.get(3);
                size = Math.min(dataPoints.size(), logEcgData.getV4().size());
                for (int j = 0; j < size; j++) {
                    series4.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefV4.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getV4().get(j)));
                }
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(0.60);
                    timeLeftLabel.setText("Loading Graphs");
                });
                int finalSize3 = size;
                Platform.runLater(() -> {
                    v4Chart.getData().add(series4);
                    NumberAxis axis = (NumberAxis) v4Chart.getXAxis();
                    axis.setUpperBound(finalSize3);
                    v4Chart.layout();
                });

                dataPoints = truncatedTestSignal.get(4);
                size = Math.min(dataPoints.size(), logEcgData.getV5().size());
                for (int j = 0; j < size; j++) {
                    series5.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefV5.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getV5().get(j)));
                }
                int finalSize4 = size;
                Platform.runLater(() -> {
                    v5Chart.getData().add(series5);
                    NumberAxis axis = (NumberAxis) v5Chart.getXAxis();
                    axis.setUpperBound(finalSize4);
                    v5Chart.layout();
                });


                dataPoints = truncatedTestSignal.get(5);
                size = Math.min(dataPoints.size(), logEcgData.getV6().size());
                for (int j = 0; j < size; j++) {
                    series6.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefV6.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getV6().get(j)));
                }
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(0.70);

                });
                int finalSize5 = size;
                Platform.runLater(() -> {
                    v6Chart.getData().add(series6);
                    NumberAxis axis = (NumberAxis) v6Chart.getXAxis();
                    axis.setUpperBound(finalSize5);
                    v6Chart.layout();

                });
                dataPoints = truncatedTestSignal.get(7);
                size = Math.min(dataPoints.size(), logEcgData.getL1().size());
                for (int j = 0; j < size; j++) {
                    series7.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefL1.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getL1().get(j)));
                }
                int finalSize6 = size;
                Platform.runLater(() -> {
                    l1Chart.getData().add(series7);
                    NumberAxis axis = (NumberAxis) l1Chart.getXAxis();
                    axis.setUpperBound(finalSize6);
                    l1Chart.layout();
                });

                dataPoints = truncatedTestSignal.get(6);
                size = Math.min(dataPoints.size(), logEcgData.getL2().size());
                for (int j = 0; j < size; j++) {
                    series8.getData().add(new XYChart.Data<>(j, dataPoints.get(j)));
                    seriesRefL2.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getL2().get(j)));
                }
                int finalSize7 = size;
                Platform.runLater(() -> {
                    l2Chart.getData().add(series8);
                    NumberAxis axis = (NumberAxis) l2Chart.getXAxis();
                    axis.setUpperBound(finalSize7);
                    l2Chart.layout();
                });
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(0.80);
                    timeLeftLabel.setText("Evaluating Result");
                });
            }


            referenceSignalTestingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {

                    Region boxRegion = (Region) referenceSignalTestingCheckBox.lookup(".box");
                    if (boxRegion != null) {
                        boxRegion.setStyle("-fx-background-color: orange;");
                    }
                    Platform.runLater(() -> {
                        if (!v1Chart.getData().contains(seriesRefV1)) {
                            v1Chart.getData().add(seriesRefV1);
                        }
                        if (!v2Chart.getData().contains(seriesRefV2)) {
                            v2Chart.getData().add(seriesRefV2);
                        }
                        if (!v3Chart.getData().contains(seriesRefV3)) {
                            v3Chart.getData().add(seriesRefV3);
                        }
                        if (!v4Chart.getData().contains(seriesRefV4)) {
                            v4Chart.getData().add(seriesRefV4);
                        }
                        if (!v5Chart.getData().contains(seriesRefV5)) {
                            v5Chart.getData().add(seriesRefV5);
                        }
                        if (!v6Chart.getData().contains(seriesRefV6)) {
                            v6Chart.getData().add(seriesRefV6);
                        }
                        if (!l1Chart.getData().contains(seriesRefL1)) {
                            l1Chart.getData().add(seriesRefL1);
                        }
                        if (!l2Chart.getData().contains(seriesRefL2)) {
                            l2Chart.getData().add(seriesRefL2);
                        }
                        seriesRefV1.getNode().setStyle("-fx-stroke: orange;");
                        seriesRefV2.getNode().setStyle("-fx-stroke: orange;");
                        seriesRefV3.getNode().setStyle("-fx-stroke: orange;");
                        seriesRefV4.getNode().setStyle("-fx-stroke: orange;");
                        seriesRefV5.getNode().setStyle("-fx-stroke: orange;");
                        seriesRefV6.getNode().setStyle("-fx-stroke: orange;");
                        seriesRefL1.getNode().setStyle("-fx-stroke: orange;");
                        seriesRefL2.getNode().setStyle("-fx-stroke: orange;");
                    });
                } else {

                    Region boxRegion = (Region) referenceSignalTestingCheckBox.lookup(".box");
                    if (boxRegion != null) {
                        boxRegion.setStyle("-fx-background-color: white;");
                    }
                    Platform.runLater(() -> {
                        v1Chart.getData().remove(seriesRefV1);
                        v2Chart.getData().remove(seriesRefV2);
                        v3Chart.getData().remove(seriesRefV3);
                        v4Chart.getData().remove(seriesRefV4);
                        v5Chart.getData().remove(seriesRefV5);
                        v6Chart.getData().remove(seriesRefV6);
                        l2Chart.getData().remove(seriesRefL2);
                        l1Chart.getData().remove(seriesRefL1);
                    });


                }
            });
            if (currentStageOfPcb == 2) {
                truncatedTestSignal = signalsFromRPeaks.findRpeaks(truncatedTestSignal, 2);
                LogEcgData logEcgData = loadTwelveLeadReferenceSignal.loadSignal(6);
                int size = Math.min(truncatedTestSignal.get(0).size(), logEcgData.getL2().size());
                for (int j = 0; j < size; j++) {
                    series8.getData().add(new XYChart.Data<>(j, truncatedTestSignal.get(0).get(j)));
                    seriesRefL2.getData().add(new XYChart.Data<Number, Number>(j, logEcgData.getL2().get(j)));
                }
                Platform.runLater(() -> {
                    l2Chart.getData().add(series8);
                    NumberAxis axis = (NumberAxis) l2Chart.getXAxis();
                    axis.setUpperBound(ecgPointsArrayList.size());
                    l2Chart.layout();
                });
                Platform.runLater(() -> {
                    graphProgressBar.setProgress(1);
                    timeLeftLabel.setText("Evaluating Result");
                });
            }
            Platform.runLater(() -> {
//                paneContainingGraph.getChildren().remove(ecgGraphVbox);
                paneContainingGraph.setVisible(false);
                resultVbox.setVisible(true);
                resultPane.setVisible(true);
            });

        });

        thread.start();
        nextLeadTwelveLeadBtn.setVisible(false);
        retakeLead.setDisable(false);


        retakeTest.setVisible(false);
        failTestBtn.setVisible(false);
        passTestBtn.setVisible(false);

        if (currentStageOfPcb == 6) {
            resultHeading.setText("Stage 6 : Quality Test 2");
        } else
            resultHeading.setText("Stage 2 : Quality Test 1");
        long timeOfLastPoint = System.currentTimeMillis();
        timeTakenText.setText(String.valueOf((timeOfLastPoint - timeOf1stPoint) / 1000.0) + " Seconds");
        pointsText.setText(String.valueOf(ecgPointsArrayList.size()));

        timeOfLastPoint = 0;
        timeOf1stPoint = 0;

//        timeTakenLabel.setText(timeTaken);
        ecgGraphLineChart.setVisible(true);
        paneContainingGraph.setVisible(true);
        heartRateTextExpected.setText("78-82");
        prTextExpected.setText("140-165");
        qrsTextExpected.setText("82-112");
        qtTextExpected.setText("350-415");
        qtcTextExpected.setText("411-471");
        qAmplitudeTextExpected.setText("-0.0542834469 to -0.0663464351");
        rAmplitudeTextExpected.setText("0.4458549807 to 0.5449338653");
        sAmplitudeTextExpected.setText("-0.0311819463 to -0.0381112677");
        Platform.runLater(() ->
        {
            try {
                InterPretetions interPretetions = new InterPretetions();
                Map<String, String> result = null;
                if (currentStageOfPcb == 2)
                    result = interPretetions.doLEAD2Interpretetions(twelveLeadArrayList);
                else {
                    result = interPretetions.doInterpretetions(twelveLeadArrayList);
                }
                heartRateTextActual.setText(result.get("heartRate"));
                prTextActual.setText(result.get("pr"));
                qrsTextActual.setText(result.get("qrs"));
                qtcTextActual.setText(result.get("qtc"));
                qtTextActual.setText(result.get("qt"));
                qAmplitudeTextActual.setText(result.get("qAmplitude"));
                rAmplitudeTextActual.setText(result.get("rAmplitude"));
                sAmplitudeTextActual.setText(result.get("sAmplitude"));
                System.out.println("RAmplitude:----->" + result.get("rAmplitude"));
                System.out.println("qAmplitude:----->" + result.get("qAmplitude"));
                System.out.println("sAmplitude:----->" + result.get("sAmplitude"));

                if (result.get("heartRateEvaluation").equals("OK"))
                    heartRateRemark.setTextFill(GREEN);
                else
                    heartRateRemark.setTextFill(RED);
                heartRateRemark.setText(result.get("heartRateEvaluation"));
                if (result.get("prEvaluation").equals("OK"))
                    prRemark.setTextFill(GREEN);
                else
                    prRemark.setTextFill(RED);
                prRemark.setText(result.get("prEvaluation"));
                if (result.get("qrsEvaluation").equals("OK"))
                    qrsRemark.setTextFill(GREEN);
                else
                    qrsRemark.setTextFill(RED);
                qrsRemark.setText(result.get("qrsEvaluation"));
                if (result.get("qtcEvaluation").equals("OK"))
                    qtcRemark.setTextFill(GREEN);
                else
                    qtcRemark.setTextFill(RED);
                qtcRemark.setText(result.get("qtcEvaluation"));
                if (result.get("qtEvaluation").equals("OK"))
                    qtRemark.setTextFill(GREEN);
                else
                    qtRemark.setTextFill(RED);
                qtRemark.setText(result.get("qtEvaluation"));

                if (result.get("rAmplitudeEvaluation").equals("OK"))
                    rAmplitudeRemark.setTextFill(GREEN);
                else
                    rAmplitudeRemark.setTextFill(RED);
                rAmplitudeRemark.setText(result.get("rAmplitudeEvaluation"));

                if (result.get("qAmplitudeEvaluation").equals("OK"))
                    qAmplitudeRemark.setTextFill(GREEN);
                else
                    qAmplitudeRemark.setTextFill(RED);
                qAmplitudeRemark.setText(result.get("qAmplitudeEvaluation"));

                if (result.get("sAmplitudeEvaluation").equals("OK"))
                    sAmplitudeRemark.setTextFill(GREEN);
                else
                    sAmplitudeRemark.setTextFill(RED);
                sAmplitudeRemark.setText(result.get("sAmplitudeEvaluation"));

                if (testingAction) {
                    failTestBtn.setVisible(true);
                    long timeStamp = System.currentTimeMillis() / 1000;
                    final String[] failReason = {null};
                    cofficientLabel.setText(result.get("Correlation"));
                    passTestBtn.setVisible(true);
                    failTestBtn.setVisible(true);
                    if (result.get("overAllEvaluation").equals("OK")) {
                        testPassOrFailLabel.setText("Test Passed ✅");
                        testPassOrFailLabel.setTextFill(GREEN);
//                        passTestBtn.setVisible(true);
                    } else {
                        testPassOrFailLabel.setText("Test Failed ");
                        testPassOrFailLabel.setTextFill(RED);
//                        failTestBtn.setVisible(true);
                    }
                    retakeTest.setVisible(true);
                    passTestBtn.setOnAction(event -> {

                        if (currentStageOfPcb == 2)
                            pushListToDatabase(uniqueDeviceId, timeStamp, username, currentStageOfPcb, "pass", "", null, false, null);
                        else
                            pushListToDatabase(uniqueDeviceId, timeStamp, username, currentStageOfPcb, "pass", "", twelveLeadArrayList, false, null);

                        retakeTest.setVisible(true);
                    });


                    failTestBtn.setOnAction(event -> {
                        DialogPane dialogPane = new DialogPane();
                        BoxBlur boxBlur = new BoxBlur(3, 3, 7);
                        mainBorderPane.setEffect(boxBlur);
                        VBox contentPane = new VBox();
                        contentPane.setSpacing(10);
                        contentPane.setStyle("-fx-background-color:white");
                        dialogPane.setContent(contentPane);


                        Text text = new Text("Test Fail Reason");
                        text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 25));
                        BorderPane borderPane = new BorderPane();
                        borderPane.setCenter(text);
                        FontAwesomeIconView closeButton = new FontAwesomeIconView();
                        closeButton.setSize(String.valueOf(15));
                        closeButton.setFill(Paint.valueOf("#ff461e"));
                        closeButton.setGlyphName("CLOSE");
                        closeButton.setOnMouseClicked(e -> {

                            mainBorderPane.setEffect(null);
                            Stage stage = (Stage) dialogPane.getScene().getWindow();
                            stage.close();
                        });
                        borderPane.setRight(closeButton);

                        VBox.setMargin(borderPane, new Insets(0, 50, 0, 50));
                        Label label = new Label("Choose a Test Fail Reason");
                        label.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
                        VBox.setMargin(label, new Insets(0, 50, 0, 50));
                        VBox.setMargin(closeButton, new Insets(20, -20, 0, 0));
                        VBox vBox = new VBox();


                        contentPane.getChildren().add(borderPane);
                        contentPane.getChildren().add(label);

                        contentPane.getChildren().add(vBox);

                        String[] week_days = {
                                "Device Fitment Issue", "Device Audio Jack Not Working", "USB jack is not working properly",
                                "Straight line", "Uneven Graph",
                                "LED Not Blinking", "R & S Amplitude Value not Acceptable", "QRS duration Value not Acceptable",
                                "Connecting Cable Not working", "Chest Lead Not working", "Device Not working",
                        };

                        for (int i = 0; i < 11; i++) {
                            String borderPaneString = "borderPane" + i;
                            String checkBoxString = "checkBox" + i;
                            BorderPane border = new BorderPane();
                            Label label1 = new Label(week_days[i]);
                            label1.setId(borderPaneString);
                            CheckBox checkBox = new CheckBox();
                            checkBox.setId(checkBoxString);
                            checkBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                            checkBox.getStyleClass().addAll("check-box");
                            border.setLeft(label1);
                            border.setRight(checkBox);
                            contentPane.getChildren().add(border);
                            Separator separator = new Separator();
                            separator.setOrientation(Orientation.HORIZONTAL);
                            contentPane.getChildren().add(separator);
                            VBox.setMargin(border, new Insets(0, 50, 0, 50));
                            vBox.setStyle("-fx-background-color:#E3EBFF;");
                        }


                        Button submitButton = new Button("DONE");
                        submitButton.setPrefWidth(450);
                        submitButton.setPrefHeight(40);
                        submitButton.setStyle(null);
                        submitButton.setTextFill(WHITE);
                        submitButton.setStyle("-fx-background-color:#F15056");
                        VBox.setMargin(submitButton, new Insets(20, 50, 0, 50));
                        contentPane.getChildren().add(submitButton);
                        contentPane.setStyle("-fx-background-color:#E3EBFF;");

                        final CheckBox[] lastSelectedCheckBox = {null}; // To keep track of the last selected checkbox

                        for (int i = 0; i < 11; i++) {
                            String checkBoxString = "checkBox" + i;
                            CheckBox checkBox = (CheckBox) dialogPane.lookup("#" + checkBoxString);

                            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue) {
                                    if (lastSelectedCheckBox[0] != null && lastSelectedCheckBox[0] != checkBox) {
                                        lastSelectedCheckBox[0].setSelected(false); // Clear the selection of the previous checkbox
                                    }
                                    lastSelectedCheckBox[0] = checkBox; // Set the current checkbox as the last selected one
                                }
                            });
                        }
                        submitButton.setOnAction(e -> {
                            String selectedReason = null;
                            for (int i = 0; i < 11; i++) {
                                String checkBoxString = "checkBox" + i;
                                CheckBox checkBox = (CheckBox) dialogPane.lookup("#" + checkBoxString);
                                if (checkBox.isSelected()) {
                                    String borderPaneString = "borderPane" + i;
                                    Label label1 = (Label) dialogPane.lookup("#" + borderPaneString);
                                    selectedReason = label1.getText();
                                    break; // Only one reason can be selected, so no need to continue
                                }
                            }
                            if (selectedReason != null) {
                                System.out.println("Selected Reason: " + selectedReason);
                                failReason[0] = selectedReason;
                            } else {
                                System.out.println("No reason selected.");
                            }

                            mainBorderPane.setEffect(null);
                            Stage stage = (Stage) dialogPane.getScene().getWindow();
                            stage.close();
                            pushListToDatabase(uniqueDeviceId, timeStamp, username, currentStageOfPcb, "Fail", failReason[0], twelveLeadArrayList, false, null);

                        });

                        DropShadow dropShadow = new DropShadow();
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
                        dialogPane.setStyle("-fx-background-color:#E3EBFF;");
                        customDialog.initStyle(StageStyle.TRANSPARENT);
                        dialogPane.setPrefWidth(500); // Set your desired width
                        dialogPane.setPrefHeight(300); // Set your desired height;
                        openDialogs.add(customDialog);
                        customDialog.showAndWait();


                    });
                }
            } catch (Exception e) {

                retakeTest.setVisible(true);
                e.printStackTrace();
                Platform.runLater(() -> {
                    generateReport.setVisible(true);
//                    graphProgressBar.setProgress(0.0);
//                    timeLeftLabel.setText("");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Something Went Wrong");
                    alert.setContentText("Please Retry");
                    alert.initStyle(StageStyle.UNDECORATED);
//                        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                    dialogPane.getStyleClass().add("myDialog");
                    alert.showAndWait();

                });
            }
            testingAction = false;
        });
    }

    @FXML
    void generatePdfAction() {
        Platform.runLater(() -> {

            DialogPane dialogPane = new DialogPane();
            VBox contentPane = new VBox();
            contentPane.setAlignment(Pos.TOP_CENTER);
            contentPane.setSpacing(15);
            BorderPane borderPane = new BorderPane();
            Label heading = new Label("Generate Report");
            heading.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD, 24));
            FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
            fontAwesomeIconView.setGlyphName("CLOSE");
            fontAwesomeIconView.setFill(RED);
            fontAwesomeIconView.setSize(String.valueOf(20));
            borderPane.setCenter(heading);
            borderPane.setRight(fontAwesomeIconView);


            ProgressBar progressBar = new ProgressBar(0);
            progressBar.setVisible(false);
            progressBar.setPrefWidth(420);
            VBox.setMargin(progressBar, new Insets(30, 0, 0, 0));
            dialogPane.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
            BorderPane borderPane1 = new BorderPane();
            Label deviceId = new Label("Device Id :  ");

            deviceId.setFont(Font.font("Manrope", FontWeight.BOLD, 19));
            Label uniqueDeviceId = new Label(MainUiSpandanNeo.uniqueDeviceId);
            BorderPane.setMargin(uniqueDeviceId, new Insets(0, 0, 0, -165));
            uniqueDeviceId.setUnderline(true);
            uniqueDeviceId.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD, 16));
            borderPane1.setLeft(deviceId);
            borderPane1.setCenter(uniqueDeviceId);
            VBox.setMargin(borderPane1, new Insets(40, 0, 0, 0));
            Label status = new Label("Please Wait Your Report is Generating");
            status.setVisible(false);
            VBox.setMargin(status, new Insets(-15, 0, 0, 0));
            status.setFont(Font.font("Manrope", FontWeight.SEMI_BOLD, 16));
            status.setWrapText(true);
            Button button = new Button("Generate Pdf");
            button.setStyle("-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 40; -fx-pref-width: 420;");
            // Set hover effect using mouse entered and exited event handlers

            button.setOnMouseEntered(event -> button.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 40; -fx-pref-width: 420;"));
            button.setOnMouseExited(event -> button.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color : #4369CE;-fx-text-fill: white; -fx-pref-height: 40; -fx-pref-width: 420;"));
            button.setTextFill(WHITE);

            button.setOnAction(event -> {

                Thread thread = new Thread(() -> {
                    Platform.runLater(() -> {
                        progressBar.setProgress(0.0);
                    });
                    while (!Thread.currentThread().isInterrupted()) {
                        Platform.runLater(() -> {
                            progressBar.setProgress(progressBar.getProgress() + 0.025);
                        });
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            Platform.runLater(() -> {
                                progressBar.setProgress(0);
                            });
                            break;
                        }
                    }
                });
                thread.start();

                contentPane.setAlignment(Pos.TOP_LEFT);
                progressBar.setVisible(true);
                status.setVisible(true);
                button.setDisable(true);
                button.setText("Download Report");
                status.setText("Please Wait Your Report is Generating");
                OnDataReceiveListener<PDDocument> onDataReceiveListenerPdfListener = new OnDataReceiveListener<PDDocument>() {
                    @Override
                    public void onDataReceived(PDDocument data) {
                        Platform.runLater(() -> {
                            button.setOnAction(event -> {
                                status.setVisible(true);
                                String downloadsFolderPath = System.getProperty("user.home") + File.separator + "Downloads";
                                String pdfFileName = data.getDocumentInformation().getTitle() + ".pdf";
                                File outputFile = new File(downloadsFolderPath, pdfFileName);
                                try {
                                    data.save(outputFile);
                                    data.close();
                                    System.out.println("PDF saved successfully. Saved to: " + outputFile.getAbsolutePath());
                                    status.setText("PDF saved successfully. Saved to: " + outputFile.getAbsolutePath());
                                    button.setDisable(true);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            status.setText("");
                            contentPane.getChildren().remove(progressBar);
                            thread.interrupt();
                            BorderPane borderPane1 = new BorderPane();
                            FontAwesomeIconView fontAwesomeIconView1 = new FontAwesomeIconView();
                            fontAwesomeIconView1.setGlyphName("FILE");
                            fontAwesomeIconView1.setFill(Paint.valueOf("#F15056"));
                            fontAwesomeIconView1.setSize(String.valueOf(20));
                            borderPane1.setLeft(fontAwesomeIconView1);
                            Label label = new Label(data.getDocumentInformation().getTitle() + ".pdf");
                            label.setFont(Font.font("Manrope", FontWeight.NORMAL, 16));
                            label.setTextFill(Paint.valueOf("#F15056"));
                            borderPane1.setCenter(label);
                            FontAwesomeIconView fontAwesomeIconView2 = new FontAwesomeIconView();
                            fontAwesomeIconView2.setGlyphName("CHECK_CIRCLE");
                            fontAwesomeIconView2.setFill(GREEN);
                            fontAwesomeIconView2.setSize(String.valueOf(20));
                            borderPane1.setRight(fontAwesomeIconView2);
                            borderPane1.setStyle("-fx-background-color : #F4F7FE");
                            borderPane1.setPrefHeight(32);
                            BorderPane.setMargin(fontAwesomeIconView1, new Insets(7.5, 0, 0, 35));
                            BorderPane.setMargin(fontAwesomeIconView2, new Insets(7.5, 30, 0, 10));
                            BorderPane.setMargin(label, new Insets(0, 0, 0, -95));
                            contentPane.getChildren().add(2, borderPane1);
                            button.setDisable(false);
                        });
                    }

                    @Override
                    public void onDataReceiveError(String errorMsg) {
                        thread.interrupt();
                        Platform.runLater(() -> {
                            button.setText("Generate Report");
                            button.setDisable(false);
                            status.setVisible(true);
                            progressBar.setProgress(0);
                            status.setText(errorMsg);
                        });
                    }
                };
                Thread thread2 = new Thread(() -> {
                    PdfGenerationClass pdfGenerationClass = new PdfGenerationClass();
                    try {
                        pdfGenerationClass.loadPdf(MainUiSpandanNeo.uniqueDeviceId, twelveLeadArrayList, onDataReceiveListenerPdfListener, String.valueOf(currentStageOfPcb));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                thread2.start();
            });
            fontAwesomeIconView.setOnMouseClicked(event -> {
                mainBorderPane.setEffect(null);
                Stage stage = (Stage) dialogPane.getScene().getWindow();
                stage.close();
            });
            BoxBlur boxBlur = new BoxBlur(5, 5, 5);
            mainBorderPane.setEffect(boxBlur);
            contentPane.getChildren().addAll(borderPane, borderPane1, progressBar, status, button);
            dialogPane.setContent(contentPane);
            Alert customDialog = new Alert(Alert.AlertType.NONE);
            customDialog.setDialogPane(dialogPane);
            customDialog.initStyle(StageStyle.UNDECORATED);
            customDialog.initModality(Modality.APPLICATION_MODAL);
            dialogPane.setPrefWidth(450); // Set your desired width
            dialogPane.setPrefHeight(330);
            // Set your desired height;
            openDialogs.add(customDialog);
            customDialog.showAndWait();
        });


    }

    @FXML
    private Button updateExeBtn;
    private static final double TOTAL_SIZE = 180 * 1024 * 1024;  // 125 MB in bytes
    private static final String DOWNLOAD_FOLDER = System.getProperty("user.home") + File.separator + "Downloads";
    private static String EXE_FILE_NAME = "";  // replace with your exe file name

    @FXML
    private void findExeVersion() {
        updateExeBtn.setDisable(true);
        exeUpdateProgressBar.setVisible(true);
        exeUpdateProgressBar.setProgress(0);
        Thread progressThread = new Thread(() -> {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                Path exePath = Paths.get(DOWNLOAD_FOLDER, EXE_FILE_NAME);
                File exeFile = exePath.toFile();
                if (exeFile.exists()) {
                    double currentSize = exeFile.length();
                    double percentage = currentSize / TOTAL_SIZE;

                    // Update the UI on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        exeUpdateProgressBar.setProgress(percentage);
                    });
                }
            }, 0, 1, TimeUnit.SECONDS);
        });
        progressThread.start();

        String productVersion = "1";

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MainUiSpandanLegacy.class.getResourceAsStream("/product.json")), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            EXEInfo productInfo = gson.fromJson(reader, EXEInfo.class);

            if (productInfo != null) {
                productVersion = productInfo.getProductVersionNumber();
                System.out.println("Product Version: " + productVersion);
            } else {
                System.err.println("JSON data not found or invalid format.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int versionNumber = Integer.parseInt(productVersion);
        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
            @Override
            public void onDataReceived(String data) {
                Platform.runLater(() -> {
                    exeUpdateProgressBar.setVisible(false);
                    exeUpdateProgressBar.setProgress(0);
                    updateExeBtn.setDisable(false);
                });
            }

            @Override
            public void onDataReceiveError(String errorMsg) {
                if (errorMsg.contains("Spandan")) {
                    EXE_FILE_NAME = errorMsg;
                } else {
                    System.err.println("Error while fetching EXE file: " + errorMsg);
                    progressThread.interrupt();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Something Went Wrong");
                        alert.setContentText(errorMsg);
                        alert.showAndWait();
                        updateExeBtn.setDisable(false);
                    });

                }
            }
        };
        Thread thread = new Thread(() -> {
            DatabaseUtility.fetchLatestExeFile(String.valueOf(versionNumber), onDataReceiveListener, mainBorderPane, progressThread);
        });
        thread.start();
    }

    public void pushListToDatabase(String deviceNumber, long timeStamp, String username, int currentStageOfPcb, String result, String failReason, ArrayList<ArrayList<Double>> twelveLeadArrayList, boolean b, DialogPane o) {
        int logOfStage = currentStageOfPcb;
        if (result.equals("pass"))
            currentStageOfPcb++;
        int finalCurrentStageOfPcb = currentStageOfPcb;
        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
            @Override
            public void onDataReceived(String data) {


                Platform.runLater(() -> {
                    if (b) {

                        Stage stage = (Stage) o.getScene().getWindow();
                        stage.close();
                        mainBorderPane.setEffect(null);

                    }
                    passTestBtn.setVisible(false);
                    failTestBtn.setVisible(false);
                    generateReport.setVisible(true);
                    if (result.equals("pass")) {
                        deviceStateChangingSlider(finalCurrentStageOfPcb);
                    }
                });
            }

            @Override
            public void onDataReceiveError(String errorMsg) {
                if (errorMsg.contains("Unauthorized")) {
                    logOutBoxAction();
                }
                Platform.runLater(() -> {
                    failTestBtn.setVisible(false);
                    passTestBtn.setVisible(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    alert.setHeaderText("Something Went Wrong");
                    alert.setContentText(errorMsg);
                    alert.initStyle(StageStyle.UNDECORATED);
//                        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                    dialogPane.getStyleClass().add("myDialog");
                    alert.showAndWait();
                });
            }
        };
        LogEcgData logEcgData = null;
        if (twelveLeadArrayList != null && twelveLeadArrayList.size() >= 8) {
            logEcgData = new LogEcgData(
                    twelveLeadArrayList.get(0),
                    twelveLeadArrayList.get(1),
                    twelveLeadArrayList.get(2),
                    twelveLeadArrayList.get(3),
                    twelveLeadArrayList.get(4),
                    twelveLeadArrayList.get(5),
                    twelveLeadArrayList.get(7),
                    twelveLeadArrayList.get(6)
            );
            Gson gson = new GsonBuilder().create();
            String jsonPayload = gson.toJson(logEcgData);
            DeviceLogData logDevice = new DeviceLogData(username, timeStamp, currentStageOfPcb, deviceNumber, result, failReason, logOfStage, jsonPayload,null);


            DatabaseUtility.updateListOfLogOperationsInDatabase(logDevice, onDataReceiveListener);
        } else {
            DeviceLogData logDevice = new DeviceLogData(username, timeStamp, currentStageOfPcb, deviceNumber, result, failReason, logOfStage, "",null);
            DatabaseUtility.updateListOfLogOperationsInDatabase(logDevice, onDataReceiveListener);
        }
    }

    @FXML
    private Label connectedDeviceHash;
    @FXML
    private Text firmwareNeo;

    private String[] getDeviceNumberFromDevice(SerialPort mySerialPort) throws InterruptedException {

        String[] result = new String[3];
        mySerialPort.setComPortParameters(115200,
                8,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY);
        mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);
        mySerialPort.openPort();
        Thread.sleep(500);
        mySerialPort.flushIOBuffers();
        String s = "";
        String variant;

        String command = "c";
        byte[] writeBytes = command.getBytes();
        mySerialPort.writeBytes(writeBytes, command.getBytes().length);
        byte[] readBuffer = new byte[1024];
        try {
            {
                while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                    s = new String(readBuffer, StandardCharsets.UTF_8).trim();

                    System.out.println(mySerialPort.getLastErrorLocation());
                    if (mySerialPort.getLastErrorLocation() == 763 || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029 || mySerialPort.getLastErrorLocation() == 955 || s.equals("")) {
                        mySerialPort.closePort();
                        return null;
                    }

                    System.out.println("c---->" + s);
                    if (s.contains("spne")) {
                        variant = "spne";
                        result[0] = variant;
                        firmwareNeo.setText(s.substring(5,11));
                        break;
                    }
                    if (s.contains("sppr")) {
                        variant = "sppr";
                        result[0] = variant;
                        result[1] = "";
                        mySerialPort.closePort();
                        return result;
                    } else if (s.contains("spdn") || (s.contains("splg"))) {
                        variant = "splg";
                        result[0] = variant;
                        result[1] = "";
                        mySerialPort.closePort();
                        return result;
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        command = "GET_HAS";
        writeBytes = command.getBytes();
        mySerialPort.writeBytes(writeBytes, command.getBytes().length);
        readBuffer = new byte[1024];
        while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
            s = new String(readBuffer, StandardCharsets.UTF_8).trim();
            System.out.println(mySerialPort.getLastErrorLocation());
            if (mySerialPort.getLastErrorLocation() == 763 || s.equals("") || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029 || mySerialPort.getLastErrorLocation() == 955) {
                mySerialPort.closePort();
                return null;
            }
            System.out.println("hash---->" + s);
            s = s.substring(7);
            if (s.length() != 0) {
                String finalS = s;
                Platform.runLater(() -> {
                    if(finalS.contains("�����������")) {
                        connectedDeviceHash.setText("");
                        result[2] = "";
                    } else {
                        result[2] = finalS;
                        Tooltip tooltip = new Tooltip(finalS);
                        connectedDeviceHash.setTooltip(tooltip);
                        connectedDeviceHash.setText(finalS.substring(0, 20) + "..");
                        result[2] = finalS;
                    }
                });
            }

        }
        writeBytes = "GET_DID".getBytes();
        mySerialPort.writeBytes(writeBytes, writeBytes.length);
        readBuffer = new byte[100];
        String deviceId = "";
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
            System.out.println(mySerialPort.getLastErrorLocation());
            deviceId = new String(readBuffer, StandardCharsets.UTF_8).trim();
            if (mySerialPort.getLastErrorLocation() == 763 || deviceId.equals("") || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029 || mySerialPort.getLastErrorLocation() == 955) {
                mySerialPort.flushIOBuffers();
                mySerialPort.closePort();
                result[1] = null;
                return result;
            }
            System.out.println("s--->" + deviceId);
            try {
                final String[] finalDeviceId1 = {deviceId};
                Platform.runLater(() -> {
                    if (finalDeviceId1[0].contains("ACK_DID�������������"))
                        connectedDeviceId.setText("New Device");
                    else if (finalDeviceId1[0].length() != 0)
                        connectedDeviceId.setText(finalDeviceId1[0].substring(7));
                });
                break;
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("ComPort Not Detected Try closing all applications using Com Port");
                    alert.setHeaderText("Something went Wrong!!");
                    alert.initStyle(StageStyle.UNDECORATED);
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                    dialogPane.getStyleClass().add("myDialog");
                    alert.showAndWait();
                });
            }
        }
        if (deviceId.contains("ACK_DID�������������")) {
            mySerialPort.closePort();
            result[1] = "New Device";
            return result;
        }
        if (deviceId.equals("ACK_DID")) {
            mySerialPort.closePort();
            result[1] = "New Device";
            return result;
        }
        if (deviceId.equals("")) {
            mySerialPort.closePort();
            result[1] = "";
            return result;
        }
        mySerialPort.closePort();
        if (deviceId.length() == 0) {
            result[1] = "";
            return result;
        }
        result[1] = deviceId.substring(7);
        return result;
    }

    @FXML
    private Label generateReport;
    @FXML
    private Button retakeLead;

    private boolean fetchUniqueDeviceIdWithLatestBatchNumber(ComboBox<String> comboBox, Label udid, Label
            errorLabel, Label deviceVariantB2bB2c, ComboBox<String> comboBox1, Label masterKey, HBox hbox, Button stage1Button, HBox downloadClientHbox) {
        // Use an AtomicBoolean to track the success or failure
        AtomicBoolean success = new AtomicBoolean(true);

        OnDataReceiveListener<Map<String, BatchData>> onDataReceiveListener = new OnDataReceiveListener<Map<String, BatchData>>() {
            @Override
            public void onDataReceived(Map<String, BatchData> data) {
                Set<String> batchIdSet = data.keySet();
                Set<String> filteredBatchIds = batchIdSet.stream()
                        .filter(batchId -> batchId.contains("SPNE"))
                        .collect(Collectors.toSet());

                Platform.runLater(() -> {

                    comboBox.getItems().addAll(filteredBatchIds);
                    if (comboBox.getItems().isEmpty()) {
                        udid.setText("");
                        errorLabel.setText("No Batches Available");
                        errorLabel.setWrapText(true);
                    } else {
                        comboBox.setOnAction(event -> {
                            try {
                                udid.setText(comboBox.getValue() + data.get(comboBox.getValue()).getLatestDeviceNumber());
                                stage1Button.setDisable(true);
                                masterKey.setText("");
                                comboBox1.setValue("");
                                if (udid.getText().contains(".")) {
                                    deviceVariantB2bB2c.setText("B2B");
                                    downloadClientHbox.setVisible(true);
                                    downloadClientHbox.getChildren().get(0).setOnMouseClicked(event1 -> {
                                        OnDataReceiveListener<Map<String, B2BOrganization>> onReceiveDataListener = new OnDataReceiveListener<Map<String, B2BOrganization>>() {
                                            @Override
                                            public void onDataReceived(Map<String, B2BOrganization> data) {
                                                Collection<B2BOrganization> b2BOrganizations = data.values();
                                                Set<String> organizationNames = b2BOrganizations.stream()
                                                        .map(B2BOrganization::getOrganisation_name)
                                                        .collect(Collectors.toSet());

                                                Platform.runLater(() -> {
                                                    comboBox1.getItems().addAll(organizationNames);
                                                    masterKey.setText("");
                                                    comboBox1.setValue("");
                                                    comboBox1.setPromptText("Select b2b Batch");
                                                    hbox.setVisible(true);
                                                    stage1Button.setDisable(true);
                                                });

                                                comboBox1.setOnAction(e -> {
                                                    String selectedOrganizationName = comboBox1.getValue();
                                                    if (selectedOrganizationName != null) {
                                                        B2BOrganization selectedOrganization = data.get(selectedOrganizationName);

                                                        if (selectedOrganization != null) {
                                                            String apiKey = selectedOrganization.getApi_key();
                                                            if (apiKey != null) {
                                                                masterKey.setText(apiKey);
                                                                masterKeyText[0] = apiKey;
                                                                stage1Button.setDisable(false);
                                                            } else {
                                                                masterKey.setText("Master Key is null");
                                                                stage1Button.setDisable(true);
                                                            }
                                                        } else {
                                                            masterKey.setText("Selected organization is null");
                                                            stage1Button.setDisable(true);
                                                        }
                                                    } else {
                                                        masterKey.setText("Selected organization name is null");
                                                        stage1Button.setDisable(true);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onDataReceiveError(String errorMsg) {

                                            }
                                        };
                                        DatabaseUtility.getB2bCLients(onReceiveDataListener);

                                    });
                                } else if (udid.getText().contains("-")) {
                                    downloadClientHbox.setVisible(false);
                                    masterKey.setText("");
                                    hbox.setVisible(false);

                                    stage1Button.setDisable(false);
                                    deviceVariantB2bB2c.setText("B2C");

                                }
                            } catch (Exception e) {
                                System.out.println("error");
                                Platform.runLater(() -> {
//
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onDataReceiveError(String errorMsg) {
                if (errorMsg.contains("Unauthorized")) {
                    logOutBoxAction();
                }
                Platform.runLater(() -> {
                    udid.setText("");
                    errorLabel.setText(errorMsg);
                    errorLabel.setWrapText(true);
                });
                success.set(false);
            }
        };
        DatabaseUtility.getAllBatchesList(onDataReceiveListener);
        return success.get();
    }

    @FXML
    private VBox resultVbox;

    private Boolean setDeviceId(String uniqueDeviceId, SerialPort mySerialPort) {
        mySerialPort.setComPortParameters(115200,
                8,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY);
        mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);
        mySerialPort.openPort();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mySerialPort.flushIOBuffers();
        String s;


        String command = "ADMIN_SUNFOX";
        byte[] writeBytes = command.getBytes();

        int bytesTxed = mySerialPort.writeBytes(writeBytes, command.getBytes().length);
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        command = "SET_DID" + uniqueDeviceId;
        writeBytes = command.getBytes();
        bytesTxed = mySerialPort.writeBytes(writeBytes, command.getBytes().length);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(" Bytes Transmitted -> " + bytesTxed + "\n");
        if (bytesTxed < 20) {
            mySerialPort.closePort();
            return false;
        }
        mySerialPort.closePort();
        return true;

    }

    private String getMid(SerialPort mySerialPort) {
        mySerialPort.setComPortParameters(115200,
                8,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY);
        mySerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 0);
        mySerialPort.openPort();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mySerialPort.flushIOBuffers();
        String s;
        String variant;

        String command = "GET_MID";
        byte[] writeBytes = command.getBytes();
        mySerialPort.writeBytes(writeBytes, command.getBytes().length);
        byte[] readBuffer = new byte[1024];
        try {
            while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                System.out.println(mySerialPort.getLastErrorLocation());
                s = new String(readBuffer, StandardCharsets.UTF_8).trim();
                if (mySerialPort.getLastErrorLocation() == 763 || s.equals("") || mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029 || mySerialPort.getLastErrorLocation() == 955) {
                    mySerialPort.closePort();
                    return null;
                }
                s = new String(readBuffer, StandardCharsets.UTF_8).trim();
                System.out.println("c---->" + s);
                if (s.equals("")) {
                    mySerialPort.closePort();
                    return "";
                } else {
                    mySerialPort.closePort();
                    return s;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mySerialPort.closePort();
        return "";
    }
    final String[] masterKeyText = {null};


    private void deviceStateChangingSlider(int stageOfPcb) {
        generateReport.setVisible(true);
        Platform.runLater(() -> {
            resultPane.setVisible(false);
            paneContainingGraph.getChildren().clear();
            ecgGraphVbox.setVisible(false);
//            stageDescriptionPane.getChildren().clear();
            deviceStageText.setText(String.valueOf(stageOfPcb));
            paneContainingGraph.setVisible(true);
            resultVbox.setVisible(false);
        });


        if (stageOfPcb == 5) {

            Scene currentScene = deviceDetectionLabel.getScene();

            Platform.runLater(() -> {
                deviceStageText.setText("5");
                for (int i = 1; i < 5; i++) {
                    String stage = "stage" + i;
                    Text text2 = (Text) currentScene.lookup("#" + stage);
                    AnchorPane anchorPane2 = (AnchorPane) currentScene.lookup("#" + stage + "Box");
                    anchorPane2.setStyle(null);
                    text2.getStyleClass().clear();
                    text2.setStyle("-fx-fill :  #159300");
                }
                DialogPane dialogPane = new DialogPane();

                VBox contentPane = new VBox();
                contentPane.setSpacing(10);
                contentPane.setStyle("-fx-background-color:white");
                contentPane.setAlignment(Pos.TOP_CENTER);
                Label label1 = new Label("All Stages Completed");
                label1.setTextFill(Paint.valueOf("4369CE"));
                label1.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 23));
                BorderPane borderPane = new BorderPane();

                borderPane.setCenter(label1);
                ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/wpf_disconnected.png/Vector (1).png");
                Label label = new Label("Device Has Passed all Stages");
                label.setFont(Font.font("Times New Roman", FontPosture.REGULAR, 25));
                Label label11 = new Label("Summary");
                label11.setPrefWidth(500);
                label11.setStyle("-fx-background-color: #F7FBFF");
                label11.setFont(Font.font("Times New Roman", FontPosture.REGULAR, 20));
                label11.setAlignment(Pos.CENTER);

                Font font = Font.font("Times New Roman", FontPosture.REGULAR, 20);
                BorderPane borderPane1 = new BorderPane();
                Label batchId = new Label("Batch Id");
                batchId.setFont(font);
                Label batchIdValue = new Label(uniqueDeviceId.substring(0, 16));
                batchIdValue.setFont(font);
                borderPane1.setLeft(batchId);
                borderPane1.setRight(batchIdValue);

                BorderPane borderPane2 = new BorderPane();
                Label deviceId = new Label("Device Id");
                deviceId.setFont(font);
                Label deviceIdValue = new Label(uniqueDeviceId);
                deviceIdValue.setFont(font);
                borderPane2.setLeft(deviceId);
                borderPane2.setRight(deviceIdValue);

                BorderPane borderPane3 = new BorderPane();
                Label manufacturingTime = new Label("Manufacturing Time");
                manufacturingTime.setFont(font);
                long timeStamp = System.currentTimeMillis() / 1000;
                Date date = new Date(timeStamp * 1000);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
                String formattedDateTime = sdf.format(date);
                System.out.println(formattedDateTime);
                Label manufacturingTimeValue = new Label(formattedDateTime);
                manufacturingTimeValue.setFont(font);
                borderPane3.setLeft(manufacturingTime);
                borderPane3.setRight(manufacturingTimeValue);

                BorderPane borderPane4 = new BorderPane();
                Label clientType = new Label("Client Type");
                clientType.setFont(font);
                String clientTypeResult = "";
                if (uniqueDeviceId.contains("."))
                    clientTypeResult = "B2B";
                else if (uniqueDeviceId.contains("-"))
                    clientTypeResult = "B2C";
                Label clientTypeValue = new Label(clientTypeResult);
                clientTypeValue.setFont(font);
                borderPane4.setLeft(clientType);
                borderPane4.setRight(clientTypeValue);
                Thread thread = new Thread(() -> {
                    OnDataReceiveListener<DeviceData> onDataReceiveListener = new OnDataReceiveListener<DeviceData>() {
                        @Override
                        public void onDataReceived(DeviceData data) {
                            Platform.runLater(()->{

                                BorderPane borderPane6 = new BorderPane();
                                Label firmwareVersion = new Label("Firmware Version");
                                firmwareVersion.setFont(font);
                                String firmwareVersionResult = data.getFirmwareVersion();
                                Label firmwareVersionValue = new Label(firmwareVersionResult);
                                firmwareVersionValue.setFont(font);
                                borderPane6.setLeft(firmwareVersion);
                                borderPane6.setRight(firmwareVersionValue);
                                contentPane.getChildren().add(borderPane6);
                                VBox.setMargin(borderPane6, new Insets(10, 180, 0, 180));
                                BorderPane.setMargin(firmwareVersion, new Insets(0, 100, 0, 0));


                                if (uniqueDeviceId.contains(".")) {
                                    BorderPane borderPane5 = new BorderPane();
                                    Label clientId = new Label("Client Name");
                                    clientId.setFont(font);
                                    String clientIdResult = data.getClientName();
                                    Label clientIdValue = new Label(clientIdResult);
                                    clientIdValue.setFont(font);
                                    borderPane5.setLeft(clientId);
                                    borderPane5.setRight(clientIdValue);
                                    contentPane.getChildren().add(borderPane5);
                                    VBox.setMargin(borderPane5, new Insets(10, 180, 0, 180));
                                    BorderPane.setMargin(clientId, new Insets(0, 100, 0, 0));

                                    BorderPane borderPane7 = new BorderPane();
                                    Label masterKey = new Label("MasterKey");
                                    masterKey.setFont(font);
                                    String masterKeyResult = data.getMasterKey();
                                    Label masterKeyValue = new Label(masterKeyResult);
                                    masterKeyValue.setFont(font);
                                    borderPane7.setLeft(masterKey);
                                    borderPane7.setRight(masterKeyValue);
                                    contentPane.getChildren().add(borderPane7);
                                    VBox.setMargin(borderPane7, new Insets(10, 180, 0, 180));
                                    BorderPane.setMargin(masterKey, new Insets(0, 100, 0, 0));
                                }
                            });


                        }

                        @Override
                        public void onDataReceiveError(String errorMsg) {

                        }
                    };
                    DatabaseUtility.getDeviceData(uniqueDeviceId, onDataReceiveListener);
                });

                thread.start();


                contentPane.getChildren().addAll(borderPane, imageView, label, label11, borderPane1, borderPane2, borderPane3, borderPane4);


                VBox.setMargin(borderPane1, new Insets(10, 180, 0, 180));
                VBox.setMargin(borderPane2, new Insets(10, 180, 0, 180));
                VBox.setMargin(borderPane3, new Insets(10, 180, 0, 180));
                VBox.setMargin(borderPane4, new Insets(10, 180, 0, 180));
                BorderPane.setMargin(batchId, new Insets(0, 100, 0, 0));
                BorderPane.setMargin(deviceId, new Insets(0, 100, 0, 0));
                BorderPane.setMargin(manufacturingTime, new Insets(0, 100, 0, 0));
                BorderPane.setMargin(clientType, new Insets(0, 100, 0, 0));


                dialogPane.setContent(contentPane);
//

                dialogPane.setStyle("-fx-background-color:white");

                paneContainingGraph.getChildren().add(dialogPane);
                StackPane.setMargin(dialogPane, new Insets(150, 200, 0, 130));
                ecgGraphLineChart.setVisible(false);
                paneContainingGraph.setVisible(true);

            });
        } else {
            char permission = neoStagesermission.charAt(stageOfPcb - 1);
            if (permission == '1') {
                Scene currentScene = boxContainingStages.getScene();
                testingAction = false;
                this.currentStageOfPcb = stageOfPcb;
                Platform.runLater(() -> {
                    deviceStageText.setVisible(true);
                    deviceStageText.setText(String.valueOf(stageOfPcb));
                    String currentStage = "stage" + stageOfPcb;
                    AnchorPane anchorPane = (AnchorPane) currentScene.lookup("#" + currentStage + "Box");
                    String style = "-fx-background-color : #E3EBFF;-fx-border-width : 0 0 0 10;-fx-border-color :  #4369CE";
                    Text text = (Text) currentScene.lookup("#" + currentStage);
                    text.getStyleClass().clear();
                    text.setStyle("-fx-fill :  #4369CE");
                    anchorPane.setStyle(style);

                    for (int i = 1; i < stageOfPcb; i++) {
                        String stage = "stage" + i;
                        Text text2 = (Text) currentScene.lookup("#" + stage);
                        AnchorPane anchorPane2 = (AnchorPane) currentScene.lookup("#" + stage + "Box");
                        anchorPane2.setStyle(null);
                        text2.getStyleClass().clear();
                        text2.setStyle("-fx-fill :  #159300");
                    }
                });
                progressBarFetchingStage.setVisible(false);

                switch (stageOfPcb) {
                    case 1 -> {
                        final String[] firmwareVersion = {""};
                        final Boolean[] deviceIdKnown = {true};
                        ecgGraphLineChart.setVisible(false);
                        paneContainingGraph.setVisible(true);
                        Button configureDeviceBtn = new Button("Configure Device");
                        configureDeviceBtn.setTextFill(Paint.valueOf("#f4f0f0"));
                        configureDeviceBtn.setPrefWidth(149);
                        configureDeviceBtn.setPrefHeight(38);
                        configureDeviceBtn.setStyle("-fx-background-color: #4369CE");
                        Text stageName = new Text("Stage 1 : Device Configuration");
                        stageName.setFill(Paint.valueOf("#4369CE"));
                        stageName.setFont(new Font("Times New Roman", 24));
                        Text label = new Text("""
                                • After the programming of PCB, Device Is Configured.
                                • The Unique Device Id is mapped with the pcb and Micro Controlled Id is Fetched after successfull
                                Configuration of Device The Device is ready for further stages""");
                        label.setFont(new Font("Times New Roman", 19));
                        VBox vBox = new VBox(stageName, label, configureDeviceBtn);
                        vBox.setSpacing(10);
                        Platform.runLater(() -> {
                            VBox.setMargin(label, new Insets(0, 0, 0, 60));
                            VBox.setMargin(stageName, new Insets(10, 0, 0, 40));
                            VBox.setMargin(configureDeviceBtn, new Insets(0, 0, 0, 40));
                            graphProgressLabel.setText("");
                            paneContainingGraph.getChildren().addAll(vBox);
                        });
                        configureDeviceBtn.setOnAction(event -> {
                            DialogPane dialogPane = new DialogPane();

                            VBox contentPane = new VBox();
                            contentPane.setSpacing(10);
                            contentPane.setStyle("-fx-background-color:white");
                            dialogPane.setContent(contentPane);

                            BorderPane borderPane = new BorderPane();
                            FontAwesomeIconView closeButton = new FontAwesomeIconView();
                            closeButton.setSize(String.valueOf(15));
                            closeButton.setFill(Paint.valueOf("#ff461e"));
                            closeButton.setGlyphName("CLOSE");
//                    Button closeButton = new Button("X");
                            Label heading = new Label("Configure Device");
                            heading.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 23));
                            borderPane.setCenter(heading);

                            VBox vBox1 = new VBox();
//                    vBox1.setAlignment(Pos.CENTER);
                            vBox1.setSpacing(10);
                            Label label1 = new Label("Select Batch Id");
                            label1.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
                            ComboBox<String> selectBatchId = new ComboBox<>();

                            selectBatchId.setPromptText("Select Batch Id From Drop Down");
                            selectBatchId.setPrefWidth(400);
                            selectBatchId.setPrefHeight(28);
                            selectBatchId.setStyle("-fx-background-color: #F4F7FE");
                            selectBatchId.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                            VBox.setMargin(selectBatchId, new Insets(-10, 0, 0, 0));
                            Label label2 = new Label("Generate Device Id");
                            label2.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
                            VBox.setMargin(label2, new Insets(10, 0, 0, 0));
                            Label udid = new Label("");
                            udid.setPrefWidth(400);
                            udid.setPrefHeight(25);
                            udid.setStyle("-fx-background-color: #F2F9FF");
                            udid.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 17));
                            VBox.setMargin(udid, new Insets(-10, 0, 0, 0));
                            ProgressBar stage1ProgressBar = new ProgressBar();
                            stage1ProgressBar.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                            stage1ProgressBar.setProgress(0);
                            stage1ProgressBar.setPrefWidth(400);
                            VBox.setMargin(stage1ProgressBar, new Insets(10, 0, 0, 0));
                            Button stage1Button = new Button("Configure Device");
                            stage1Button.setPrefWidth(400);
                            stage1Button.setPrefHeight(35);
                            stage1Button.setStyle("-fx-background-color:#4369CE");
                            stage1Button.setTextFill(WHITE);
                            Label errorLabel = new Label("");
                            errorLabel.setWrapText(true);
                            errorLabel.setStyle("-fx-text-fill:red");
                            Label variantB2bB2C = new Label();
                            variantB2bB2C.setStyle("-fx-background-color: rgba(135, 216, 250, 0.7);-fx-text-fill: blue;-fx-background-radius:15px");
                            variantB2bB2C.setPrefWidth(50);
                            variantB2bB2C.setFont(Font.font("manrope", FontWeight.BOLD, 20));
                            variantB2bB2C.setAlignment(Pos.CENTER);
                            FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
                            fontAwesomeIconView.setGlyphName("DOWNLOAD");
                            fontAwesomeIconView.setSize("20");
                            fontAwesomeIconView.setCursor(Cursor.HAND);
                            HBox downloadClientHbox = new HBox(fontAwesomeIconView);
                            downloadClientHbox.getChildren().add(new Label("Get Clients"));
                            downloadClientHbox.setSpacing(10);
                            downloadClientHbox.setVisible(false);

                            ComboBox<String> comboBox1 = new ComboBox<>();
                            comboBox1.setPromptText("Select b2b Client");

                            HBox hbox = new HBox(new Label("Choose B2B Client "));
                            comboBox1.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

                            comboBox1.getStyleClass().add("myComboBox");

                            comboBox1.setStyle("-fx-background-color: #F4F7FE");
                            Label masterKey = new Label("");
                            hbox.getChildren().add(comboBox1);

                            hbox.setVisible(false);
                            hbox.setSpacing(10);

                            CheckBox checkbox = new CheckBox("Get Device Id From Mid");
                            checkbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

                            checkbox.setOnAction(event1 -> {
                                if (checkbox.isSelected()) {
                                    stage1ProgressBar.setProgress(-1);

                                }

                                downloadClientHbox.setVisible(false);
                                masterKey.setText("");
                                hbox.setVisible(false);
                                selectBatchId.setDisable(checkbox.isSelected());
                                Thread thread = new Thread(() -> {

                                    String s = getMid(mySerialPort);
                                    Platform.runLater(() -> {
                                        errorLabel.setText(s);
                                    });
                                    OnDataReceiveListener<ArrayList<String>> onDataReceiveListener = new OnDataReceiveListener<ArrayList<String>>() {
                                        @Override
                                        public void onDataReceived(ArrayList<String> data) {

                                            uniqueDeviceId = data.get(0);
                                            System.out.println(data);
                                            Platform.runLater(() -> {
                                                stage1Button.setDisable(true);
                                                stage1ProgressBar.setProgress(0);
                                                udid.setText(data.get(0));
                                                connectedDeviceId.setText(data.get(0));
                                            });
                                            ArrayList<String> result = new ArrayList<>();

                                            DeviceConfigurationNeo deviceConfiguration = new DeviceConfigurationNeo();
                                            try {
                                                result = deviceConfiguration.configureDevice(data.get(0), data.get(1), mySerialPort, errorLabel);
                                            } catch (NoSuchAlgorithmException e) {
                                                throw new RuntimeException(e);
                                            }

                                            if (result == null) {
                                                Platform.runLater(() -> {
                                                    errorLabel.setStyle("-fx-text-fill:red");
                                                    stage1Button.setDisable(false);
                                                    errorLabel.setText("""
                                                            Error : Configuration process interrupted due to
                                                            communication loss with the device.
                                                            Ensure that the device is properly connected
                                                            and try again.
                                                            """);
                                                    stage1ProgressBar.setProgress(0);
                                                });

                                            } else {
                                                firmwareVersion[0] =result.get(1);
                                                Platform.runLater(() -> {
                                                    stage1Button.setText("Move To Next Stage");
                                                    stage1Button.setStyle("-fx-background-color:#4369CE");
                                                    stage1Button.setDisable(false);
                                                    stage1ProgressBar.setProgress(0);
                                                });
                                            }


                                        }


                                        @Override
                                        public void onDataReceiveError(String errorMsg) {

                                            System.out.println(errorMsg);
                                            Platform.runLater(() -> {
                                                selectBatchId.setDisable(false);
                                                stage1Button.setDisable(false);
                                                checkbox.setSelected(false);
                                                deviceIdKnown[0] = !deviceIdKnown[0];
                                                stage1ProgressBar.setProgress(0);
                                                errorLabel.setText(errorMsg);
                                            });

                                        }
                                    };
                                    if (!s.equals(""))
                                        DatabaseUtility.getDeviceIdFromMid(s.substring(7), onDataReceiveListener);
                                });
                                if (checkbox.isSelected()) {

                                    udid.setText("");

                                    thread.start();
                                } else {
                                    selectBatchId.setValue("");
                                    udid.setText("");
                                    mySerialPort.closePort();
                                }
                                deviceIdKnown[0] = !deviceIdKnown[0];
                            });


                            vBox1.getChildren().addAll(checkbox, label1, selectBatchId, label2, udid, downloadClientHbox, hbox, masterKey, stage1ProgressBar, stage1Button, errorLabel);


                            borderPane.setRight(closeButton);
                            borderPane.setLeft(variantB2bB2C);
                            VBox.setMargin(vBox1, new Insets(30, 30, 0, 30));
                            contentPane.getChildren().addAll(borderPane, vBox1);

                            closeButton.setOnMouseClicked(e -> {

                                mainBorderPane.setEffect(null);
                                Stage stage = (Stage) dialogPane.getScene().getWindow();
                                stage.close();
                            });
                            final String[] clientName = {null};
                            Thread thread = new Thread(() -> {
                                if ((fetchUniqueDeviceIdWithLatestBatchNumber(selectBatchId, udid, errorLabel, variantB2bB2C, comboBox1, masterKey, hbox, stage1Button, downloadClientHbox))) {
                                    BoxBlur boxBlur = new BoxBlur(3, 3, 7);
                                    mainBorderPane.setEffect(boxBlur);
                                    stage1Button.setOnAction(new EventHandler<>() {
                                        @Override
                                        public void handle(ActionEvent event) {
//
                                            if (deviceIdKnown[0]) {
                                                stage1Button.setDisable(true);
//
                                                stage1Button.setText("Re-Configure Device");
                                                stage1ProgressBar.setProgress(0.0);
                                                stage1Button.setStyle("-fx-background-color: #E37F09");
                                                errorLabel.setText("");
                                                Thread progressThread = new Thread(() -> {
                                                    while (!Thread.currentThread().isInterrupted()) {
                                                        try {
                                                            Thread.sleep(200);
                                                        } catch (Exception e) {
                                                            stage1ProgressBar.setProgress(0);
                                                            break;
                                                        }
                                                        Platform.runLater(() -> {
                                                            stage1ProgressBar.setProgress(stage1ProgressBar.getProgress() + 0.0140);
                                                        });
                                                    }
                                                });

                                                Thread thread = new Thread(() -> {
                                                    String mId = "";
                                                    String version = "";
                                                    String did = udid.getText();
                                                    DeviceConfigurationNeo deviceConfiguration = new DeviceConfigurationNeo();
                                                    ArrayList<String> result = null;

                                                    if (udid.getText().equals("")) {
                                                        Platform.runLater(() -> {
                                                            stage1Button.setDisable(false);
                                                            errorLabel.setText("Select batch Id from drop down menu");
                                                        });

                                                    } else {
                                                        progressThread.start();

                                                        try {
                                                            result = deviceConfiguration.configureDevice(did, masterKeyText[0], mySerialPort, errorLabel);
                                                        } catch (NoSuchAlgorithmException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                        if (result == null) {
                                                            Platform.runLater(() -> {
                                                                stage1Button.setDisable(false);
                                                                errorLabel.setStyle("-fx-text-fill:red");
                                                                errorLabel.setText("""
                                                                        Error : Configuration process interrupted due to
                                                                        communication loss with the device.
                                                                        Ensure that the device is properly connected
                                                                        and try again.
                                                                        """);
                                                                stage1ProgressBar.setProgress(0);
                                                            });
                                                            progressThread.interrupt();

                                                        } else {
                                                            mId = result.get(0);
                                                            version = result.get(1);
                                                            long timeStamp = System.currentTimeMillis() / 1000;
                                                            uniqueDeviceId = did;
                                                            version = version.substring(0, version.length() - 1);
                                                            if(masterKey.getText().contains("null"))
                                                                masterKeyText[0] = null;
                                                            clientName[0] = comboBox1.getValue();
                                                            createNewDevice(did, username, timeStamp, mId, selectBatchId.getValue(), progressThread, dialogPane, version, clientName[0], masterKeyText[0]);
                                                        }
                                                    }
                                                });

                                                thread.start();
                                                generateReport.setVisible(false);

                                            } else {

                                                long timeStamp = System.currentTimeMillis() / 1000;
                                                if (udid.getText().length() == 20) {
                                                    OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                                                        @Override
                                                        public void onDataReceived(String data) {
                                                            pushListToDatabase(uniqueDeviceId, timeStamp, username, currentStageOfPcb, "pass", "", null, true, dialogPane);

                                                        }@Override
                                                        public void onDataReceiveError(String errorMsg) {
                                                            Platform.runLater(() -> {
                                                                errorLabel.setText("""
                                                                Error : Updating the device failed.
                                                                """);
                                                            });

                                                        }
                                                    };
                                                    DatabaseUtility.updateDeviceTable(uniqueDeviceId,firmwareVersion[0],onDataReceiveListener);
                                                    Platform.runLater(() -> {
                                                        connectedDeviceId.setText(uniqueDeviceId);
                                                        if (uniqueDeviceId.contains(".")) {
                                                            clientType.setText("B2B");
                                                        } else if (uniqueDeviceId.contains("-"))
                                                            clientType.setText("B2C");

                                                    });

                                                } else {
                                                    Platform.runLater(() -> {
                                                        errorLabel.setText("""
                                                                Error : Configuration process interrupted due to
                                                                communication loss with the device.
                                                                Ensure that the device is properly connected
                                                                and try again.
                                                                """);
                                                    });

                                                }
                                            }

                                        }


                                    });


                                }
                            });
                            thread.start();
                            DropShadow dropShadow = new DropShadow();

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
                            dialogPane.setPrefWidth(400); // Set your desired width
                            dialogPane.setPrefHeight(600); // Set your desired height;
                            openDialogs.add(customDialog);
                            customDialog.showAndWait();


                        });
                        ecgGraphLineChart.setVisible(false);
                    }

                case 2 -> {
                    ecgGraphLineChart.setVisible(false);
//                stageDescriptionPane.setVisible(true);
                    paneContainingGraph.setVisible(true);
                    Button startTestButton = new Button("Start Test");
                    startTestButton.setTextFill(Paint.valueOf("#f4f0f0"));
                    startTestButton.setPrefWidth(149);
                    startTestButton.setPrefHeight(38);
                    startTestButton.setStyle("-fx-background-color: #4369CE");
                    Text stageName = new Text("Stage 2 : Quality Test 1");
                    stageName.setFill(Paint.valueOf("#4369CE"));
                    stageName.setFont(new Font("Times New Roman", 24));
                    Text label = new Text("""
                            • After the programming of PCB, quality test 1 is performed and the result is recorded.
                            • In case the test fails, the PCB is sent to repair where node analysis is performed and the
                             critical component is identified and changed and the process of testing is repeated.""");
                    label.setFont(new Font("Times New Roman", 19));

                    VBox vBox = new VBox(stageName, label, startTestButton);
                    vBox.setSpacing(10);
                    Platform.runLater(() -> {
                        VBox.setMargin(label, new Insets(0, 0, 0, 60));
                        VBox.setMargin(stageName, new Insets(10, 0, 0, 40));
                        VBox.setMargin(startTestButton, new Insets(0, 0, 0, 40));
                        graphProgressLabel.setText("");
                        paneContainingGraph.getChildren().addAll(vBox);

                    });
                    startTestButton.setOnMouseClicked(event -> {
                        generateReport.setVisible(false);
                        paneContainingGraph.setStyle(null);
                        try {
                            if (!testingAction)
                                testButtonAction();
                            testingAction = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    stage1.setOnMouseClicked(null);
                }
                case 3 -> {

                    ecgGraphLineChart.setVisible(false);
//                stageDescriptionPane.setVisible(true);
                    paneContainingGraph.setVisible(true);
                    Button generateBarCode = new Button("Generate Bar Code");
                    generateBarCode.setTextFill(Paint.valueOf("#f4f0f0"));
                    generateBarCode.setPrefWidth(149);
                    generateBarCode.setPrefHeight(38);
                    generateBarCode.setStyle("-fx-background-color: #4369CE");
                    Text stageName = new Text("Stage 4 : Barcode Generation");
                    stageName.setFill(Paint.valueOf("#4369CE"));
                    stageName.setFont(new Font("Times New Roman", 24));
                    Text label = new Text("""
                            • After Device Assembly and Quality Test 1 Device Barcode Genereation is performed.
                            • This Barcode is Downloadedd in your download folder.Print this barcode and paste it on the device.""");
                    label.setFont(new Font("Times New Roman", 19));
                    VBox vBox = new VBox(stageName, label, generateBarCode);

                    vBox.setSpacing(20);
                    Platform.runLater(() -> {
                        VBox.setMargin(label, new Insets(0, 0, 0, 60));
                        VBox.setMargin(stageName, new Insets(10, 0, 0, 40));
                        VBox.setMargin(generateBarCode, new Insets(0, 0, 0, 40));
                        graphProgressLabel.setText("");
                        paneContainingGraph.getChildren().addAll(vBox);
                    });

                    generateBarCode.setOnMouseClicked(e -> {
                        generateBarCode.setDisable(true);
                        generateReport.setVisible(false);
                        paneContainingGraph.setStyle(null);
                        try {
                            GridPane barcodePane = new GridPane();
                            barcodePane.setStyle("-fx-background-color:white");
                            BarCodeGenerator barCodeGenerator = new BarCodeGenerator();
                            Image image = barCodeGenerator.formBar(uniqueDeviceId);
                            ImageView barcodeImage = new ImageView(image);
                            Text printButton = new Text("Print Barcode");
                            printButton.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
                            printButton.setUnderline(true);
                            printButton.setFill(Paint.valueOf("#DD5C5C"));
                            printButton.setUnderline(true);
                            printButton.setOnMouseEntered(ev -> {

                                printButton.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 22));
                            });
                            printButton.setOnMouseExited(ev2 -> {
                                printButton.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
                            });
                            printButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

                                @Override
                                public void handle(MouseEvent event) {
                                    generateReport.setVisible(true);
                                    DialogPane dialogPane = new DialogPane();
                                    BoxBlur boxBlur = new BoxBlur(3, 3, 7);
                                    mainBorderPane.setEffect(boxBlur);
                                    VBox contentPane = new VBox();
                                    contentPane.setAlignment(Pos.CENTER);
//                                contentPane.setSpacing(10);
                                    contentPane.setStyle("-fx-background-color:white");
                                    dialogPane.setContent(contentPane);
                                    BorderPane borderPane = new BorderPane();
                                    FontAwesomeIconView closeButton = new FontAwesomeIconView();
                                    closeButton.setSize(String.valueOf(15));
                                    closeButton.setFill(Paint.valueOf("#ff461e"));
                                    closeButton.setGlyphName("CLOSE");
                                    Label heading = new Label("Bar Code Print");
                                    heading.setFont(Font.font("Times New Roman", FontWeight.BOLD, 23));
                                    borderPane.setCenter(heading);
                                    borderPane.setRight(closeButton);
                                    contentPane.getChildren().add(borderPane);
                                    VBox vBox1 = new VBox();
                                    vBox1.setAlignment(Pos.CENTER);
                                    VBox.setMargin(vBox1, new Insets(70, 20, 0, 20)); // Adjust the top margin here
                                    ImageView imageView = new ImageView(image);
                                    Button printButton = new Button("Print Bar Code");
                                    printButton.setStyle("-fx-background-color:#F15056");
                                    printButton.setTextFill(Color.WHITE);
                                    printButton.setPrefWidth(280);
                                    printButton.setPrefHeight(40);
                                    VBox.setMargin(printButton, new Insets(40, 0, 0, 0));
                                    printButton.setOnMouseEntered(event2 -> printButton.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color:#F15056;-fx-text-fill: white; -fx-pref-height: 40; -fx-pref-width: 280;"));
                                    printButton.setOnMouseExited(event2 -> printButton.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color:#F15056;-fx-text-fill: white; -fx-pref-height: 40; -fx-pref-width: 280;"));
                                    printButton.setCursor(Cursor.HAND);
                                    vBox1.getChildren().addAll(imageView, printButton);
                                    contentPane.getChildren().add(vBox1);
                                    closeButton.setOnMouseClicked(e -> {
                                        mainBorderPane.setEffect(null);
                                        Stage stage = (Stage) dialogPane.getScene().getWindow();
                                        stage.close();
                                    });
                                    printButton.setOnAction(event1 -> {
                                        printButton.setDisable(true);
                                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                                        BarcodePrinter barcodePrinter = new BarcodePrinter();
                                        barcodePrinter.printBarcode(bufferedImage, printButton);

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
                                    dialogPane.setPrefWidth(300); // Set your desired width
                                    dialogPane.setPrefHeight(400); // Set your desired height;
                                    openDialogs.add(customDialog);
                                    customDialog.showAndWait();
                                }
                            });
                            Button proceedToNextStage = new Button("Move To Stage 4");
                            proceedToNextStage.setTextFill(WHITE);
                            proceedToNextStage.setPrefWidth(149);
                            proceedToNextStage.setPrefHeight(38);
                            proceedToNextStage.setStyle("-fx-background-color:#4369CE");
                            barcodeImage.setStyle("-fx-background-color:transparent");
                            BorderPane borderPane = new BorderPane();
                            Label label1 = new Label("Barcode Generated Successfully");
                            label1.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 23));
                            label1.setTextFill(GREEN);
                            ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/Vector (1).png");
                            imageView.setFitWidth(20);
                            imageView.setFitHeight(20);
                            borderPane.setLeft(imageView);
                            borderPane.setCenter(label1);
                            barcodePane.add(borderPane, 0, 0);
                            barcodePane.add(barcodeImage, 0, 1);
                            BorderPane.setMargin(imageView, new Insets(3, 10, 0, 0));
                            GridPane.setMargin(borderPane, new Insets(10, 10, 0, 60));
                            GridPane.setMargin(barcodeImage, new Insets(10, 10, 0, 100));
                            GridPane.setMargin(printButton, new Insets(10, 10, 30, -100));
                            GridPane.setMargin(proceedToNextStage, new Insets(10, 10, 30, 60));
                            barcodePane.add(printButton, 1, 2);
                            barcodePane.add(proceedToNextStage, 0, 2);

                            proceedToNextStage.setOnAction(event -> {
                                proceedToNextStage.setDisable(true);
                                generateReport.setVisible(true);
                                int logOfStage = currentStageOfPcb;
                                String result = "pass";
                                currentStageOfPcb++;
                                long timeStamp = System.currentTimeMillis() / 1000;
                                DeviceLogData deviceLogData = new DeviceLogData(username, timeStamp, currentStageOfPcb, uniqueDeviceId, result, null, logOfStage, null,null);
                                OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
                                    @Override
                                    public void onDataReceived(String data) {
                                        deviceStateChangingSlider(currentStageOfPcb++);


                                    }

                                    @Override
                                    public void onDataReceiveError(String errorMsg) {
                                        if (errorMsg.contains("Unauthorized")) {
                                            logOutBoxAction();
                                        }
                                        Platform.runLater(() -> {
                                            proceedToNextStage.setDisable(false);
                                        });

                                    }
                                };
                                DatabaseUtility.updateListOfLogOperationsInDatabase(deviceLogData, onDataReceiveListener);

                            });
                            vBox.getChildren().removeAll(label, generateBarCode);
                            VBox.setMargin(barcodePane, new Insets(0, 30, 0, 30));
                            vBox.getChildren().add(barcodePane);

                        } catch (Exception exception) {
                            generateBarCode.setDisable(false);
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("exception");
                            alert.setHeaderText("Something Went Wrong");
                            alert.initStyle(StageStyle.UNDECORATED);
                            DialogPane dialogPane = alert.getDialogPane();
                            dialogPane.getStylesheets().add(
                                    Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                            dialogPane.getStyleClass().add("myDialog");
                            alert.showAndWait();
                            exception.printStackTrace();
                        }
                    });
                }
                case 4 -> {


                    ecgGraphLineChart.setVisible(false);
                    paneContainingGraph.setVisible(true);
                    paneContainingGraph.setVisible(true);
                    Button startTestButton = new Button("Start Test");
                    startTestButton.setTextFill(Paint.valueOf("#f4f0f0"));
                    startTestButton.setPrefWidth(149);
                    startTestButton.setPrefHeight(38);
                    startTestButton.setStyle("-fx-background-color: #4369CE");
                    Text stageName = new Text("Stage 4 : Quality Test 2");
                    stageName.setFill(Paint.valueOf("#4369CE"));
                    stageName.setFont(new Font("Times New Roman", 24));
                    Text label = new Text("""
                            • After mapping the accessories, the device is tested for a last time before final packaging and a report of 12-lead ECG test is uploaded
                              in the Google drive as quality record and the data is fed into the QC form.
                            • In case of failure, the device is discarded and sent back to PCB station for dismantle and repair.""");
                    label.setFont(new Font("Times New Roman", 19));

                    VBox vBox = new VBox(stageName, label, startTestButton);
                    vBox.setSpacing(10);
                    Platform.runLater(() -> {
                        VBox.setMargin(label, new Insets(0, 0, 0, 60));
                        VBox.setMargin(stageName, new Insets(10, 0, 0, 40));
                        VBox.setMargin(startTestButton, new Insets(0, 0, 0, 40));
                        graphProgressLabel.setText("");
                        paneContainingGraph.getChildren().addAll(vBox);
                    });
                    startTestButton.setOnMouseClicked(event -> {
                        try {

                            if (!testingAction)
                                testButtonAction();
                            testingAction = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                }

            }
        } else{
            Platform.runLater(() -> {
                Platform.runLater(() -> {
                    for (int i = 1; i <= 4; i++) {
                        Scene currentScene = boxContainingStages.getScene();
                        String stage = "stage" + i;
                        Text text2 = (Text) currentScene.lookup("#" + stage);
                        text2.setStyle(null);
                        AnchorPane anchorPane2 = (AnchorPane) currentScene.lookup("#" + stage + "Box");
                        anchorPane2.setStyle(null);
                    }

                });

                DialogPane dialogPane = new DialogPane();

                VBox contentPane = new VBox();
                contentPane.setAlignment(Pos.CENTER);
                dialogPane.setStyle("-fx-background-color:white");
                contentPane.setStyle("-fx-background-color:white");
                contentPane.setSpacing(20);
                Button button = new Button("CLOSE");
                button.setStyle("-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;");
                // Set hover effect using mouse entered and exited event handlers
                button.setOnMouseEntered(event -> button.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                button.setOnMouseExited(event -> button.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));

                button.setTextFill(WHITE);
                button.setOnAction(event -> {
                    mainBorderPane.setEffect(null);
                    Stage stage = (Stage) dialogPane.getScene().getWindow();
                    stage.close();
                });
                BoxBlur boxBlur = new BoxBlur(3, 3, 7);
                mainBorderPane.setEffect(boxBlur);
                ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/Tiny people standing near stop sign flat vector illustration 1.png");
                Label label = new Label("""
                            Stage is not permitted Ask the
                                 admin to give stage
                            Permissions to proceed further.
                        """);
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                label.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
                label.setTextFill(RED);
                contentPane.getChildren().addAll(imageView, label, button);
                dialogPane.setContent(contentPane);
                Alert customDialog = new Alert(Alert.AlertType.NONE);
                customDialog.setDialogPane(dialogPane);
                customDialog.initStyle(StageStyle.TRANSPARENT);
                dialogPane.setPrefWidth(400); // Set your desired width
                dialogPane.setPrefHeight(300);
                openDialogs.add(customDialog);
                customDialog.showAndWait();
            });

        }
    }

}

    @FXML
    private VBox ecgGraphVbox;
    @FXML
    private Label timeLeftLabel;
    @FXML
    private ProgressBar graphProgressBar;

    private void testButtonAction() {

        if (currentStageOfPcb == 2) {
            twelveLeadIncrementer = 6;
            Platform.runLater(() -> {
                twelveLeadEcgTestText.setText("Stage 2 : Quality Test 1");
            });
        } else {
            twelveLeadIncrementer = 0;
            Platform.runLater(() -> {
                twelveLeadEcgTestText.setText("Stage 4: Quality Test 2");
            });
        }
        generateReport.setVisible(false);
        paneContainingGraph.getChildren().clear();
        paneContainingGraph.setVisible(true);
        paneContainingGraph.getChildren().add(ecgGraphVbox);

        ecgGraphVbox.setVisible(true);

        clear12LeadProgress();
        nextLeadTwelveLeadBtn.setVisible(true);
        retakeLead.setVisible(true);
//        nextLeadTwelveLeadBtn.setDisable(false);
        twelveLeadArrayList = new ArrayList<>();
        timeOf1stPoint = System.currentTimeMillis();

        resultVbox.setVisible(false);

        nextLeadTwelveLeadBtn.setDisable(true);
        retakeLead.setDisable(true);
        System.out.println("new");
        graphProgressBar.setProgress(0);
        timeLeftLabel.setText("");

        startGraphAction();
    }

    Boolean closeWindow = false;
    @FXML
    private VBox detectiionsAndGraphVbox;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ProgressBar exeUpdateProgressBar;
    @FXML
    private ImageView deviceConnectingImg;
    @FXML
    private BorderPane upperBoxPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        Dimension screenSize = new Dimension(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());

        if (screenSize.width < 1450) {
            VBox.setMargin(paneContainingGraph, new Insets(0, 120, 0, 40));
            VBox.setMargin(resultPane, new Insets(0, 120, 0, 40));
            VBox.setMargin(upperBoxPane, new Insets(0, 50, 0, 0));
        }
        printBarCode.setOnMouseEntered(event -> {
            printBarCode.setFont(Font.font("Times New Roman", 18));
        });
        printBarCode.setOnMouseExited(event -> {
            printBarCode.setFont(Font.font("Times New Roman", 17));
        });
        generateReport.setOnMouseEntered(event -> {
            generateReport.setFont(Font.font("Times New Roman", 18));
        });
        generateReport.setOnMouseExited(event -> {
            generateReport.setFont(Font.font("Times New Roman", 17));
        });
        uniqueDeviceId = null;
        closeWindow = false;
        deviceDetectingThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!closeWindow) {
                try {
                    Thread.sleep(10);
                    SerialPort[] availablePorts = SerialPort.getCommPorts();
                    boolean foundSpandan = false;  // Flag to track if "Spandan" port is found
                    for (SerialPort port : availablePorts) {
                        if (port.getPortDescription().contains("Spandan")) {
                            try {
                                port.getLastErrorLocation();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mySerialPort = port;
                            deviceDetectedFromThread.set(true);
                            foundSpandan = true;
                            break;
                        }
                    }
                    if (!foundSpandan) {
                        deviceDetectedFromThread.set(false);
                    }
                } catch (Exception e) {
                    deviceDetectedFromThread.set(false);
                }
            }
            deviceDetectingThread.interrupt();
        });
        deviceDetectingThread.start();
        retakeTest.setOnAction(event -> {
            testingAction = false;
            testButtonAction();
        });

        deviceDetectedFromThread.addListener((observableValue, deviceDisconnectedListener, deviceConnectedListener) -> {
            if (deviceConnectedListener && !firmwareOperationInProgress) {
                String[] result = new String[2];
//                Platform.runLater(() -> {
//                    deviceDetailsAnchorPane.setVisible(true);
//                    deviceDetectionLabel.setStyle("-fx-background-color : #49B23E; background-radius:10");
//                    deviceDetectionLabel.setText("Device Connected");
//                    Image image = new Image("com/example/demo/wpf_connected.png");
//                    deviceConnectingImg.setImage(image);
//                    paneContainingGraph.getChildren().clear();
//                    detectiionsAndGraphVbox.getChildren().remove(newDeviceBorderPane);
//                });
                Platform.runLater(() -> {
                    deviceDetailsAnchorPane.setVisible(true);
                    deviceDetectionLabel.setStyle("-fx-background-color : #49B23E; background-radius:10;");
                    deviceDetectionLabel.setText("Device Connected");
                    Image image = new Image("in/sunfox/healthcare/java/spandan_qms/deviceConnectedSymbol.png");
                    deviceConnectingImg.setImage(image);
                    paneContainingGraph.getChildren().clear();
                    detectiionsAndGraphVbox.getChildren().remove(newDeviceBorderPane);
                });
                progressBarFetchingStage.setVisible(true);
                try {
                    if (!firmwareOperationInProgress && uniqueDeviceId == null) {
                        Thread.sleep(1000);

                        result = getDeviceNumberFromDevice(mySerialPort);
                        if (result != null)
                            uniqueDeviceId = result[1];
                        else
                            uniqueDeviceId = "";
                        if (result != null && !result[0].equals("splg") && uniqueDeviceId.equals("") && !result[0].equals("sppr")) {

                            progressBarFetchingStage.setVisible(false);
                            Platform.runLater(() -> {
                                DialogPane dialogPane = new DialogPane();
                                VBox contentPane = new VBox();
                                contentPane.setAlignment(Pos.TOP_CENTER);
                                contentPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;-fx-padding: 12px;");
                                dialogPane.setStyle("-fx-background-color:white");
                                contentPane.setSpacing(15);
                                Button button = new Button("CLOSE");
                                button.setStyle("-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;");
                                // Set hover effect using mouse entered and exited event handlers
                                button.setOnMouseEntered(event -> button.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                                button.setOnMouseExited(event -> button.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                                button.setTextFill(WHITE);
                                button.setOnAction(event -> {
                                    mainBorderPane.setEffect(null);
                                    Stage stage = (Stage) dialogPane.getScene().getWindow();
                                    stage.close();
                                });
                                BoxBlur boxBlur = new BoxBlur(15, 15, 25);
                                mainBorderPane.setEffect(boxBlur);
                                ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/Group 7383.png");
                                VBox.setMargin(imageView, new Insets(10, 0, 0, 0));
                                Label label = new Label("""
                                        Process interrupted due to
                                        communication loss with the device.
                                        Ensure that the device is properly
                                        connected and try again.
                                        """);
                                label.setTextAlignment(TextAlignment.CENTER);

                                label.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
                                label.setTextFill(Paint.valueOf("#595959"));
                                contentPane.getChildren().addAll(imageView, label, button);
                                dialogPane.setContent(contentPane);
                                Alert customDialog = new Alert(Alert.AlertType.NONE);
                                customDialog.setDialogPane(dialogPane);
                                customDialog.initStyle(StageStyle.UNDECORATED);
                                customDialog.initModality(Modality.WINDOW_MODAL);
                                dialogPane.setPrefWidth(350); // Set your desired width
                                dialogPane.setPrefHeight(330);
                                // Set your desired height;
                                openDialogs.add(customDialog);
                                customDialog.showAndWait();
                            });

                        } else if (result != null && result[0].contains("splg")) {
                            if (mySerialPort != null)
                                mySerialPort.closePort();
                            mySerialPort = null;
                            for (Alert dialog : openDialogs) {
                                Platform.runLater(() -> {
                                    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                                    stage.close();
                                });
                            }
                            openDialogs.clear();
                            closeWindow = true;
                            Platform.runLater(() -> {
                                try {

                                    Stage primaryStage = (Stage) paneContainingGraph.getScene().getWindow();
                                    FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("MainUiSpandanLegacy.fxml"));
                                    Parent loginRoot = fxmlLoader.load();
                                    Scene neoScene = new Scene(loginRoot);
                                    neoScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                                    MainUiSpandanLegacy controller = fxmlLoader.getController();
                                    int role = 0;
                                    if (!roleText.getText().equals("ADMIN")) {
                                        role = 1;
                                    }

                                    controller.setUsername(username, role, spandanStagesPermission);
                                    Stage mainUiStage = new Stage();
                                    mainUiStage.setScene(neoScene);
                                    mainUiStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                                    mainUiStage.setTitle("Spandan QMS System");
                                    // Maximize the loginStage
                                    mainUiStage.setMaximized(true);
                                    neoScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                                    mainUiStage.show();
                                    primaryStage.close();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        } else if (result != null && result[0].contains("sppr")) {
                            if (mySerialPort != null)
                                mySerialPort.closePort();
                            mySerialPort = null;
                            for (Alert dialog : openDialogs) {
                                Platform.runLater(() -> {
                                    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                                    stage.close();
                                });
                            }
                            openDialogs.clear();
                            closeWindow = true;
                            Platform.runLater(() -> {
                                try {

                                    Stage primaryStage = (Stage) paneContainingGraph.getScene().getWindow();
                                    FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("MainUiSpandanPro.fxml"));
                                    Parent loginRoot = fxmlLoader.load();
                                    Scene neoScene = new Scene(loginRoot);
                                    neoScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                                    MainUiSpandanPro controller = fxmlLoader.getController();
                                    int role = 0;
                                    if (!roleText.getText().equals("ADMIN")) {
                                        role = 1;
                                    }

                                    controller.setUsername(username, role, spandanStagesPermission);
                                    Stage mainUiStage = new Stage();
                                    mainUiStage.setScene(neoScene);
                                    mainUiStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                                    mainUiStage.setTitle("Spandan QMS System");
                                    // Maximize the loginStage
                                    mainUiStage.setMaximized(true);
                                    neoScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                                    mainUiStage.show();
                                    primaryStage.close();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        }
                        if (uniqueDeviceId.equals("New Device")) {
                            progressBarFetchingStage.setVisible(false);
                            deviceStateChangingSlider(1);
                        }


                    }

                } catch (Exception e) {
                    progressBarFetchingStage.setVisible(false);
                    Platform.runLater(() -> {
                        DialogPane dialogPane = new DialogPane();
                        VBox contentPane = new VBox();
                        contentPane.setAlignment(Pos.TOP_CENTER);
                        contentPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px;-fx-padding: 12px;");
                        dialogPane.setStyle("-fx-background-color:white");
                        contentPane.setSpacing(15);
                        Button button = new Button("CLOSE");
                        button.setStyle("-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;");
                        // Set hover effect using mouse entered and exited event handlers
                        button.setOnMouseEntered(event -> button.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                        button.setOnMouseExited(event -> button.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color :  #4369CE;-fx-text-fill: white; -fx-pref-height: 30; -fx-pref-width: 150;"));
                        button.setTextFill(WHITE);
                        button.setOnAction(event -> {
                            mainBorderPane.setEffect(null);
                            Stage stage = (Stage) dialogPane.getScene().getWindow();
                            stage.close();
                        });
                        BoxBlur boxBlur = new BoxBlur(15, 15, 25);
                        mainBorderPane.setEffect(boxBlur);
                        ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/Group 7383.png");
                        VBox.setMargin(imageView, new Insets(10, 0, 0, 0));
                        Label label = new Label("""
                                Process interrupted due to
                                communication loss with the device.
                                Ensure that the device is properly
                                connected and try again.
                                """);
                        label.setTextAlignment(TextAlignment.CENTER);

                        label.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 20));
                        label.setTextFill(Paint.valueOf("#595959"));
                        contentPane.getChildren().addAll(imageView, label, button);
                        dialogPane.setContent(contentPane);
                        Alert customDialog = new Alert(Alert.AlertType.NONE);
                        customDialog.setDialogPane(dialogPane);
                        customDialog.initStyle(StageStyle.UNDECORATED);
                        customDialog.initModality(Modality.WINDOW_MODAL);
                        dialogPane.setPrefWidth(350); // Set your desired width
                        dialogPane.setPrefHeight(330);
                        // Set your desired height;
                        openDialogs.add(customDialog);
                        customDialog.showAndWait();
                    });
                }

                OnDataReceiveListener<DeviceLogData> stateReceiveListener = new OnDataReceiveListener<DeviceLogData>() {
                    @Override
                    public void onDataReceived(DeviceLogData deviceLogData) {
                        generateReport.setVisible(true);
                        if (deviceLogData.getStage() == 5) {
                            Platform.runLater(() -> {
                                resultPane.setVisible(false);
                                paneContainingGraph.getChildren().clear();
                                ecgGraphVbox.setVisible(false);
                                deviceStageText.setText(String.valueOf(deviceLogData.getStage()));
                            });
                            Scene currentScene = deviceDetectionLabel.getScene();
                            Platform.runLater(() -> {
                                deviceStageText.setText("5");
                                for (int i = 1; i < deviceLogData.getStage(); i++) {
                                    String stage = "stage" + i;
                                    Text text2 = (Text) currentScene.lookup("#" + stage);
                                    AnchorPane anchorPane2 = (AnchorPane) currentScene.lookup("#" + stage + "Box");
                                    anchorPane2.setStyle(null);
                                    text2.getStyleClass().clear();
                                    text2.setStyle("-fx-fill :  #159300");
                                }
                                DialogPane dialogPane = new DialogPane();
                                VBox contentPane = new VBox();
                                contentPane.setSpacing(10);
                                contentPane.setAlignment(Pos.TOP_CENTER);
                                Label label1 = new Label("All Stages Completed");
                                label1.setTextFill(Paint.valueOf("4369CE"));
                                label1.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 23));
                                BorderPane borderPane = new BorderPane();
                                borderPane.setCenter(label1);
                                ImageView imageView = new ImageView("in/sunfox/healthcare/java/spandan_qms/Vector (1).png");
                                Label label = new Label("Device Has Passed all Stages");
                                label.setFont(Font.font("Times New Roman", FontPosture.REGULAR, 25));
                                Label label11 = new Label("Summary");
                                label11.setPrefWidth(500);
                                label11.setStyle("-fx-background-color: #F7FBFF");
                                label11.setFont(Font.font("Times New Roman", FontPosture.REGULAR, 20));
                                label11.setAlignment(Pos.CENTER);

                                Font font = Font.font("Times New Roman", FontPosture.REGULAR, 20);
                                BorderPane borderPane1 = new BorderPane();
                                Label batchId = new Label("Batch Id");
                                batchId.setFont(font);
                                Label batchIdValue = new Label(uniqueDeviceId.substring(0, 16));
                                batchIdValue.setFont(font);
                                borderPane1.setLeft(batchId);
                                borderPane1.setRight(batchIdValue);

                                BorderPane borderPane2 = new BorderPane();
                                Label deviceId = new Label("Device Id");
                                deviceId.setFont(font);
                                Label deviceIdValue = new Label(uniqueDeviceId);
                                deviceIdValue.setFont(font);
                                borderPane2.setLeft(deviceId);
                                borderPane2.setRight(deviceIdValue);

                                BorderPane borderPane3 = new BorderPane();
                                Label manufacturingTime = new Label("Manufacturing Time");
                                manufacturingTime.setFont(font);
                                Date date = new Date(deviceLogData.getTimeStamp() * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
                                String formattedDateTime = sdf.format(date);
                                System.out.println(formattedDateTime);
                                Label manufacturingTimeValue = new Label(formattedDateTime);
                                manufacturingTimeValue.setFont(font);
                                borderPane3.setLeft(manufacturingTime);
                                borderPane3.setRight(manufacturingTimeValue);

                                BorderPane borderPane4 = new BorderPane();
                                Label clientType = new Label("Client Type");
                                clientType.setFont(font);
                                String clientTypeResult = "";
                                if (uniqueDeviceId.contains("."))
                                    clientTypeResult = "B2B";
                                else if (uniqueDeviceId.contains("-"))
                                    clientTypeResult = "B2C";
                                Label clientTypeValue = new Label(clientTypeResult);
                                clientTypeValue.setFont(font);
                                borderPane4.setLeft(clientType);
                                borderPane4.setRight(clientTypeValue);
                                Thread thread = new Thread(() -> {
                                    OnDataReceiveListener<DeviceData> onDataReceiveListener = new OnDataReceiveListener<DeviceData>() {
                                        @Override
                                        public void onDataReceived(DeviceData data) {
                                            Platform.runLater(()->{

                                                BorderPane borderPane6 = new BorderPane();
                                                Label firmwareVersion = new Label("Firmware Version");
                                                firmwareVersion.setFont(font);
                                                String firmwareVersionResult = data.getFirmwareVersion();
                                                Label firmwareVersionValue = new Label(firmwareVersionResult);
                                                firmwareVersionValue.setFont(font);
                                                borderPane6.setLeft(firmwareVersion);
                                                borderPane6.setRight(firmwareVersionValue);
                                                contentPane.getChildren().add(borderPane6);
                                                VBox.setMargin(borderPane6, new Insets(10, 180, 0, 180));
                                                BorderPane.setMargin(firmwareVersion, new Insets(0, 100, 0, 0));

                                                if (uniqueDeviceId.contains(".")) {
                                                    BorderPane borderPane5 = new BorderPane();
                                                    Label clientId = new Label("Client Name");
                                                    clientId.setFont(font);
                                                    String clientIdResult = data.getClientName();
                                                    Label clientIdValue = new Label(clientIdResult);
                                                    clientIdValue.setFont(font);
                                                    borderPane5.setLeft(clientId);
                                                    borderPane5.setRight(clientIdValue);
                                                    contentPane.getChildren().add(borderPane5);
                                                    VBox.setMargin(borderPane5, new Insets(10, 180, 0, 180));
                                                    BorderPane.setMargin(clientId, new Insets(0, 100, 0, 0));

                                                    BorderPane borderPane7 = new BorderPane();
                                                    Label masterKey = new Label("MasterKey");
                                                    masterKey.setFont(font);
                                                    String masterKeyResult = data.getMasterKey();
                                                    Label masterKeyValue = new Label(masterKeyResult);
                                                    masterKeyValue.setFont(font);
                                                    borderPane7.setLeft(masterKey);
                                                    borderPane7.setRight(masterKeyValue);
                                                    contentPane.getChildren().add(borderPane7);
                                                    VBox.setMargin(borderPane7, new Insets(10, 180, 0, 180));
                                                    BorderPane.setMargin(masterKey, new Insets(0, 100, 0, 0));
                                                }
                                            });


                                        }

                                        @Override
                                        public void onDataReceiveError(String errorMsg) {

                                        }
                                    };
                                    DatabaseUtility.getDeviceData(uniqueDeviceId, onDataReceiveListener);
                                });

                                thread.start();


                                contentPane.getChildren().addAll(borderPane, imageView, label, label11, borderPane1, borderPane2, borderPane3, borderPane4);


                                VBox.setMargin(borderPane1, new Insets(10, 180, 0, 180));
                                VBox.setMargin(borderPane2, new Insets(10, 180, 0, 180));
                                VBox.setMargin(borderPane3, new Insets(10, 180, 0, 180));
                                VBox.setMargin(borderPane4, new Insets(10, 180, 0, 180));
//                                VBox.setMargin(borderPane5, new Insets(10, 180, 0, 180));
                                BorderPane.setMargin(batchId, new Insets(0, 100, 0, 0));
                                BorderPane.setMargin(deviceId, new Insets(0, 100, 0, 0));
                                BorderPane.setMargin(manufacturingTime, new Insets(0, 100, 0, 0));
                                BorderPane.setMargin(clientType, new Insets(0, 100, 0, 0));
//                                BorderPane.setMargin(clientId, new Insets(0, 100, 0, 0));

                                dialogPane.setContent(contentPane);
                                dialogPane.setStyle("-fx-background-color:white");

                                paneContainingGraph.getChildren().add(dialogPane);
                                StackPane.setMargin(dialogPane, new Insets(150, 200, 0, 130));
                                ecgGraphLineChart.setVisible(false);
                                paneContainingGraph.setVisible(true);
                            });
                        } else
                            deviceStateChangingSlider(deviceLogData.getStage());

                        progressBarFetchingStage.setVisible(false);

                    }

                    @Override
                    public void onDataReceiveError(String errorMsg) {

                        if (errorMsg.contains("Not a Device")) {
                            deviceStateChangingSlider(1);
                        } else if (errorMsg.contains("Unauthorized")) {
                            logOutBoxAction();
                        } else {
//                            newDeviceButton.setVisible(true);
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText("Something Went Wrong");
                                alert.setContentText(errorMsg);
                                alert.initStyle(StageStyle.UNDECORATED);
//                        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
                                DialogPane dialogPane = alert.getDialogPane();
                                dialogPane.setStyle("-fx-border-color:red");
                                dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

                                dialogPane.getStyleClass().add("myDialog");
                                alert.showAndWait();
                            });
                            progressBarFetchingStage.setVisible(false);

                        }
                    }

                };
                if (uniqueDeviceId != null) {
//                    newDeviceButton.setVisible(false);

                    if (uniqueDeviceId.length() >= 13 && uniqueDeviceId.contains("SPNE"))
                        DatabaseUtility.getCurrentStateOfDevice(uniqueDeviceId, stateReceiveListener);
                }
                String[] finalResult = result;

                Platform.runLater(() -> {
                    printBarCode.setVisible(true);
                    if (finalResult != null && finalResult[2] != null && !finalResult[2].equals("")) {
                        connectedDeviceHash.setText(finalResult[2].substring(0, 20) + "..");
                        connectedDeviceId.setText(uniqueDeviceId);
                        if (uniqueDeviceId.contains(".")) {
                            clientType.setText("B2B");
                        } else if (uniqueDeviceId.contains("-"))
                            clientType.setText("B2C");
                    }
                });


            } else if (!firmwareOperationInProgress) {
                deviceDetailsAnchorPane.setVisible(false);
                progressBarFetchingStage.setVisible(false);
                isGraphActive = false;
                uniqueDeviceId = null;
                for (Alert dialog : openDialogs) {
                    Platform.runLater(() -> {
                        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                        stage.close();
                    });
                }
                openDialogs.clear();
                Scene currentScene = boxContainingStages.getScene();
                Platform.runLater(() -> {
                    mainBorderPane.setEffect(null);
                    printBarCode.setVisible(false);
                    paneContainingGraph.getChildren().clear();
                    paneContainingGraph.setVisible(false);
                    deviceDetectionLabel.setStyle("-fx-background-color : #DD5C5C; background-radius:10");
                    deviceDetectionLabel.setText("Device Disconnected");
                    firmwareNeo.setText("");
                    masterKeyText[0]=null;
                    Image image = new Image("in/sunfox/healthcare/java/spandan_qms/wpf_disconnected.png");
                    deviceConnectingImg.setImage(image);

                    generateReport.setVisible(false);
                    twelveLeadArrayList = null;
                    ecgPointsArrayList = null;
                    detectiionsAndGraphVbox.getChildren().add(1, newDeviceBorderPane);
                    VBox.setMargin(newDeviceBorderPane, new Insets(-180, 0, 0, 0));
                    connectedDeviceId.setText("");
                    deviceStageText.setText("");
                    connectedDeviceHash.setText("");
                    resultPane.setVisible(false);
                    clientType.setText("");
                });
                Platform.runLater(() -> {
                    for (int i = 1; i <= 4; i++) {
                        String stage = "stage" + i;
                        Text text2 = (Text) currentScene.lookup("#" + stage);
                        text2.setStyle(null);
                        AnchorPane anchorPane2 = (AnchorPane) currentScene.lookup("#" + stage + "Box");
                        anchorPane2.setStyle(null);
                    }

                });
            }
        });
    }

    @FXML
    private Label roleText;

    @FXML
    private void openAdminPanel() {
        if (roleText.getText().equals("ADMIN")) {
            try {
                roleText.setDisable(true);
                FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("AdminPanel.fxml"));
                Parent loginRoot = fxmlLoader.load();
                Scene loginScene = new Scene(loginRoot);
                loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                Stage AdminStage = new Stage();
                AdminStage.setScene(loginScene);
                AdminStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                AdminStage.setTitle("Welcome");
                AdminPanel controller = fxmlLoader.getController();
                AdminStage.initModality(Modality.APPLICATION_MODAL);
                controller.setUsername(username);
                Stage currentStage = (Stage) mainBorderPane.getScene().getWindow();
                controller.setStage(currentStage);
                loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                AdminStage.show();
                AdminStage.setOnCloseRequest(event -> {
                    roleText.setDisable(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setUsername(String developer, int role, String spandanStagesPermission) {
        this.spandanStagesPermission = spandanStagesPermission;
        StringBuilder reversed = new StringBuilder();
        for (int i = spandanStagesPermission.length() - 1; i >= 0; i--) {
            reversed.append(spandanStagesPermission.charAt(i));
        }
        reversed.append("0".repeat(Math.max(0, 6 - reversed.length())));
        String temp = "";
        temp += reversed.charAt(0);
        temp += reversed.charAt(1);
        temp += reversed.charAt(3);
        temp += reversed.charAt(5);
        this.neoStagesermission = temp;
        this.spandanStagesPermission = spandanStagesPermission;
        System.out.println("NEo Permission--->" + this.neoStagesermission);
        System.out.println("Spandan Permission--->" + this.spandanStagesPermission);
        if (role == 0) {
            roleText.setText("ADMIN");
        } else if (role == 1) {
            roleText.setText("Operator");
        }
        this.username = developer;
        usernameLabel.setText(username);
    }

    @FXML
    void logOutBoxAction() {
        if (mySerialPort != null)
            mySerialPort.closePort();
        for (Alert dialog : openDialogs) {
            Platform.runLater(() -> {
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.close();
            });
        }
        openDialogs.clear();

        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<String>() {
            @Override
            public void onDataReceived(String data) {
                closeWindow = true;

                Platform.runLater(() -> {
                    try {

                        Stage primaryStage = (Stage) paneContainingGraph.getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("LoginUi.fxml"));
                        Parent loginRoot = fxmlLoader.load();
                        Scene loginScene = new Scene(loginRoot);

                        Stage loginStage = new Stage();
                        loginStage.setScene(loginScene);
                        loginStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                        loginStage.setTitle("Sunfox Login");
                        loginStage.initStyle(StageStyle.UNDECORATED);

                        loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                        loginStage.show();

                        // Close the primaryStage
                        primaryStage.close();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


            }

            @Override
            public void onDataReceiveError(String errorMsg) {

            }
        };

        DatabaseUtility.clearToken(onDataReceiveListener);

    }


    public void createNewDevice(String uniqueDeviceId, String userName, long timeStamp, String
            microControllerId, String batchNo, Thread progressThread, DialogPane dialogPane, String version, String clientName, String masterKey) {
        OnDataReceiveListener<String> onDataReceiveListener = new OnDataReceiveListener<>() {
            @Override
            public void onDataReceived(String data) {
                Platform.runLater(() -> {
                    Stage stage = (Stage) dialogPane.getScene().getWindow();
                    stage.close();
                    mainBorderPane.setEffect(null);
                });
                progressThread.interrupt();
                pushListToDatabase(uniqueDeviceId, timeStamp, username, 1, "pass", null, null, false, null);
                Platform.runLater(() -> {
                    connectedDeviceId.setText(uniqueDeviceId);
                    if (uniqueDeviceId.contains(".")) {
                        clientType.setText("B2B");
                    } else if (uniqueDeviceId.contains("-"))
                        clientType.setText("B2C");
                });

            }

            @Override
            public void onDataReceiveError(String errorMsg) {

                if (errorMsg.contains("Unauthorized")) {
                    try {
                        Stage primaryStage = (Stage) paneContainingGraph.getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(TestMain.class.getResource("LoginUi.fxml"));
                        Parent loginRoot = fxmlLoader.load();
                        Scene loginScene = new Scene(loginRoot);

                        Stage loginStage = new Stage();
                        loginStage.setScene(loginScene);
                        loginStage.getIcons().add(new Image(Objects.requireNonNull(TestMain.class.getResourceAsStream("logo.png"))));
                        loginStage.setTitle("Sunfox Login");
                        loginStage.initStyle(StageStyle.UNDECORATED);

                        loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                        loginStage.show();

                        // Close the primaryStage
                        primaryStage.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressThread.interrupt();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Something Went Wrong");
                    alert.setContentText(errorMsg);
                    alert.initStyle(StageStyle.UNDECORATED);
//                        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
                    dialogPane.getStyleClass().add("myDialog");
                    alert.showAndWait();
                });
            }
        };
        System.out.println(uniqueDeviceId);
        String deviceVariant = uniqueDeviceId.substring(0, 4);
        String productionPlantId = uniqueDeviceId.substring(5, 9);
        DatabaseUtility.creatNewDevice(uniqueDeviceId, userName, timeStamp, batchNo, deviceVariant, onDataReceiveListener, microControllerId, version, productionPlantId, clientName, masterKey);
    }

    @FXML
    private Label printBarCode;

    public void printBarCodeAction() {
        DialogPane dialogPane = new DialogPane();
        BoxBlur boxBlur = new BoxBlur(3, 3, 7);
        mainBorderPane.setEffect(boxBlur);
        VBox contentPane = new VBox();
//                                contentPane.setSpacing(10);
        contentPane.setAlignment(Pos.TOP_CENTER);
        contentPane.setStyle("-fx-background-color:white");
        dialogPane.setContent(contentPane);
        BorderPane borderPane = new BorderPane();
        FontAwesomeIconView closeButton = new FontAwesomeIconView();
        closeButton.setSize(String.valueOf(15));
        closeButton.setFill(Paint.valueOf("#ff461e"));
        closeButton.setGlyphName("CLOSE");
        Label heading = new Label("Bar Code Print");
        heading.setFont(Font.font("Times New Roman", FontWeight.BOLD, 23));
        borderPane.setCenter(heading);
        borderPane.setRight(closeButton);
        contentPane.getChildren().add(borderPane);

        VBox vBox1 = new VBox();
        VBox.setMargin(vBox1, new Insets(70, 20, 0, 20)); // Adjust the top margin here
        BarCodeGenerator barCodeGenerator = new BarCodeGenerator();
        Image image = barCodeGenerator.formBar(uniqueDeviceId);

        ImageView imageView = new ImageView(image);


        Button printButton = new Button("Print Bar Code");
        printButton.setStyle("-fx-background-color:#F15056");

        VBox.setMargin(printButton, new Insets(40, 0, 0, 0));
        printButton.setTextFill(Color.WHITE);

        printButton.setPrefWidth(280);
        printButton.setPrefHeight(40);
        printButton.setTextFill(WHITE);
        printButton.setOnMouseEntered(event -> printButton.setStyle("-fx-scale-y: 0.9; -fx-scale-x: 0.9;-fx-background-color:#F15056;-fx-text-fill: white; -fx-pref-height: 40; -fx-pref-width: 280;"));
        printButton.setOnMouseExited(event -> printButton.setStyle("-fx-scale-y: 1; -fx-scale-x: 1;-fx-background-color:#F15056;-fx-text-fill: white; -fx-pref-height: 40; -fx-pref-width: 280;"));
        printButton.setCursor(Cursor.HAND);

        FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
        fontAwesomeIconView.setGlyphName("PRINT");
        fontAwesomeIconView.setSize("35");
        fontAwesomeIconView.setFill(Paint.valueOf("#F15056"));
        VBox.setMargin(fontAwesomeIconView, new Insets(20, 0, 0, 0));

        printButton.setOnAction(event -> {
            printButton.setDisable(true);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            BarcodePrinter barcodePrinter = new BarcodePrinter();
            barcodePrinter.printBarcode(bufferedImage, printButton);

        });
        vBox1.setAlignment(Pos.CENTER);
        vBox1.getChildren().addAll(imageView, fontAwesomeIconView, printButton);
        contentPane.getChildren().add(vBox1);

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
        dialogPane.setPrefWidth(300); // Set your desired width
        dialogPane.setPrefHeight(400); // Set your desired height;
        openDialogs.add(customDialog);
        customDialog.showAndWait();
    }


    @FXML
    private void addNewDevice() {
        FlashingNewDeviceClass flashingNewDeviceClass = new FlashingNewDeviceClass();
        flashingNewDeviceClass.addDevice(mainBorderPane, openDialogs);
    }
}
