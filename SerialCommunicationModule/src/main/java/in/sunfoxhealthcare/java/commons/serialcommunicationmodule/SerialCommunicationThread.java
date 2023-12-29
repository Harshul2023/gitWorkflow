package in.sunfoxhealthcare.java.commons.serialcommunicationmodule;


import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.nio.charset.StandardCharsets;

public class SerialCommunicationThread extends Thread {
    private final String spandanInputCommand;
    private SerialPort mySerialPort;
    public Label progressLabel;
    private OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread;

    public SerialCommunicationThread(SerialPort mySerialPort, String spandanInputCommand) {
        this.mySerialPort = mySerialPort;
        this.spandanInputCommand = spandanInputCommand;
    }

    public void run() {
        String s;
        try {
            if (!mySerialPort.isOpen()) {
                mySerialPort.closePort();
                onDataReceiveListenerFromThread.onDataReceived("error");
                return;
            }
            if (spandanInputCommand.equals("0")) {
                byte[] writeByte = spandanInputCommand.getBytes();
                mySerialPort.writeBytes(writeByte, spandanInputCommand.getBytes().length);
                mySerialPort.flushIOBuffers();
                mySerialPort.closePort();
                Thread.sleep(1000);
                onDataReceiveListenerFromThread.onDataReceived("Port Closed");
                return;
            }
        } catch (Exception e) {
            mySerialPort.flushIOBuffers();
            mySerialPort.closePort();
            try {
                onDataReceiveListenerFromThread.onDataReceived("error");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
    e.printStackTrace();
        }
        if (spandanInputCommand.equals("1")) {

            try {
                mySerialPort.openPort();
                if (mySerialPort.isOpen()) Platform.runLater(() -> {
                            progressLabel.setText("");
                        }
                );
                byte[] WriteByte = spandanInputCommand.getBytes();
                mySerialPort.writeBytes(WriteByte, spandanInputCommand.getBytes().length);
                byte[] readBuffer = new byte[4];
                while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                    if (mySerialPort.isOpen()) {
                        s = new String(readBuffer, StandardCharsets.UTF_8);
                        if (mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029) {
                            mySerialPort.flushIOBuffers();
                            mySerialPort.closePort();
                            onDataReceiveListenerFromThread.onDataReceived("error");
                            Platform.runLater(() -> {
                                progressLabel.setText("Error Occurred in Transmission");
                            });
                            mySerialPort = null;
                            System.out.println("Error occurred");
                            return;
                        }
                        if (!s.trim().equals("")) {
//                            System.out.println(s);
                            onDataReceiveListenerFromThread.onDataReceived(s);

                        } else
                            break;
                    } else return;
                }

                Platform.runLater(() -> {
                    progressLabel.setText("Error Occurred in Transmission");
                });
                onDataReceiveListenerFromThread.onDataReceived("error");
                System.out.println("Transmission finished writing" + spandanInputCommand);
                mySerialPort.closePort();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setOnDataReceiveListener(OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread) {
        this.onDataReceiveListenerFromThread = onDataReceiveListenerFromThread;

//        }

    }
}