package in.sunfoxhealthcare.java.commons.serialcommunicationmodule;


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
        if (data.equals("Transmission Finished")) {
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
                validData = validData.replace("\n", "");
                validData = validData.replace("\r", "");
                onReceiveDataListenerFromModuleToUI.usbOnDataReceive(validData);
                bufferData = bufferData.substring(bufferData.toUpperCase().indexOf("Y") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static SerialCommunication serialCom = new SerialCommunication(onDataReceiveListenerFromThread);
    private static void startTransmission(Label graphProgressLabel) {
        serialCom.startTransmission("1",graphProgressLabel);
        bufferData = "";
    }
    private static void stopTransmission() {
        serialCom.stopTransmission("0");
    }

    public static void sendCommand(String command, Label graphProgressLabel) throws InterruptedException {
        {
            switch (command) {
                case "start" -> {
                    startTransmission(graphProgressLabel);
                }
                case "stop" -> stopTransmission();
            }
        }
    }
    public static void connectPort(SerialPort mySerialPort) throws InterruptedException {
        serialCom.connectPort(mySerialPort);
    }
}