package in.sunfox.healthcare.java.spandan_qms.spandan_pro_serial_communication.serialcommunicationmodule;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.control.Label;

public class SpandanUsbCommunication {
    private static String bufferData = "";

    public static OnReceiveDataListenerFromModuleToUI onReceiveDataListenerFromModuleToUI;

    public SpandanUsbCommunication(OnReceiveDataListenerFromModuleToUI onReceiveDataListenerFromModuleToUI) {
        SpandanUsbCommunication.onReceiveDataListenerFromModuleToUI = onReceiveDataListenerFromModuleToUI;
    }
    static OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread = new OnDataReceiveListenerFromThread<String>() {
        @Override
        public void onDataReceived(String data) throws Exception {
            performOperationOnBytes(data);
        }
        @Override
        public void usbAuthentication(String data) {
            onReceiveDataListenerFromModuleToUI.usbAuthentication(data);
        }
    };

    public static void performOperationOnBytes(String data) throws Exception {
        if (data.equals("error")) {
            onReceiveDataListenerFromModuleToUI.usbOnDataReceive(data);
            bufferData = "";
            return;
        }
        if (data.equals("Port Closed")) {
            onReceiveDataListenerFromModuleToUI.usbOnDataReceive(data);
            bufferData = "";
            return;
        }
        bufferData += data;
        String tempData = "";
        try {
            while (bufferData.toUpperCase().contains("Y")) {
                if (!bufferData.toUpperCase().contains("Y"))
                    serialCom.stopTransmission("0");
                tempData = bufferData.substring(0, bufferData.toUpperCase().indexOf("Y"));
                String validData = tempData;
                StringBuilder validDecodedData = new StringBuilder();
                validData = validData.replace("\n", "");
                validData = validData.replace("\r", "");
                for (int i = 0; i < validData.length(); i++) {
                    String sTemp = String.valueOf(validData.charAt(i));
                    switch (sTemp) {
                        case "2" -> sTemp = "0";
                        case "3" -> sTemp = "1";
                        case "1" -> sTemp = "2";
                        case "5" -> sTemp = "3";
                        case "7" -> sTemp = "4";
                        case "9" -> sTemp = "5";
                        case "4" -> sTemp = "6";
                        case "6" -> sTemp = "7";
                        case "0" -> sTemp = "8";
                        case "8" -> sTemp = "9";
                        default -> String.valueOf(validData.charAt(i));
                    }
                    validDecodedData.append(sTemp);
                }
                onReceiveDataListenerFromModuleToUI.usbOnDataReceive(validDecodedData.toString());
                bufferData = bufferData.substring(bufferData.toUpperCase().indexOf("Y") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static SerialCommunication serialCom = new SerialCommunication(onDataReceiveListenerFromThread);
    private static void startTransmission(Label graphProgressLabel, String command) {
        serialCom.startTransmission(command);
        bufferData = "";
    }

    private static void stopTransmission() {
        serialCom.stopTransmission("0");
    }

    private static void initAuthentication() {
        serialCom.startCTransmission("c");
    }

    public static void sendCommand(String command, Label graphProgressLabel) throws InterruptedException {
        {
            switch (command) {
                case "start0" -> {
                    startTransmission(graphProgressLabel,"STA_V1");
                }
                case "start1" -> {
                    startTransmission(graphProgressLabel,"STA_V2");
                }
                case "start2" -> {
                    startTransmission(graphProgressLabel,"STA_V3");
                }
                case "start3" -> {
                    startTransmission(graphProgressLabel,"STA_V4");
                }
                case "start4" -> {
                    startTransmission(graphProgressLabel,"STA_V5");
                }
                case "start5" -> {
                    startTransmission(graphProgressLabel,"STA_V6");
                }
                case "start6" -> {
                    startTransmission(graphProgressLabel,"STA_LEAD_2");
                }
                case "start7" -> {
                    startTransmission(graphProgressLabel,"STA_LEAD_1");
                }
                case "stop" -> stopTransmission();
                case "authenticate" -> initAuthentication();
            }
        }
    }
    public static void connectPort(SerialPort mySerialPort) throws InterruptedException {
        serialCom.connectPort(mySerialPort);
    }
}