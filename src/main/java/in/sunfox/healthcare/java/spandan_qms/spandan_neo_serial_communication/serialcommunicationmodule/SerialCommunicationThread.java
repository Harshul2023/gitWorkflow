package in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.serialcommunicationmodule;


import com.fazecast.jSerialComm.SerialPort;
import in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.SpandanUnlocking.UnlockMechanism;

import java.nio.charset.StandardCharsets;

public class SerialCommunicationThread extends Thread {
    private final String spandanInputCommand;
    private final SerialPort mySerialPort;
    private OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread;

    public SerialCommunicationThread(SerialPort mySerialPort, String spandanInputCommand) {
        this.mySerialPort = mySerialPort;
        this.spandanInputCommand = spandanInputCommand;
    }

    public void run() {
        try {
            if (!mySerialPort.isOpen()) {
                onDataReceiveListenerFromThread.onDataReceived("error");
                return;
            }

            if (spandanInputCommand.equals("0")) {
                byte[] writeByte ="STP".getBytes();
                mySerialPort.writeBytes(writeByte, "STP".getBytes().length);
                mySerialPort.flushIOBuffers();
                mySerialPort.closePort();
                Thread.sleep(1000);
                onDataReceiveListenerFromThread.onDataReceived("Port Closed");
                return;
            }
        } catch (Exception e) {
            System.out.println(
                    "Exception in thread"
            );
            return;
        }
        if (spandanInputCommand.equals("1") || (spandanInputCommand.equals("c"))) {
//            mySerialPort.closePort();
            UnlockMechanism unlockMechanism = new UnlockMechanism();
            try {
                if (unlockMechanism.unlocking2("USER_MODE",mySerialPort)) {
                    mySerialPort.openPort();
                    byte[] WriteByte ="STA".getBytes();
                    mySerialPort.writeBytes(WriteByte, "STA".getBytes().length);
                    byte[] readBuffer = new byte[4];
                    while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                        if (mySerialPort.isOpen()) {
                            String s = new String(readBuffer, StandardCharsets.UTF_8);
                            if (mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029) {
                                mySerialPort.closePort();
                                onDataReceiveListenerFromThread.onDataReceived("error");
                                System.out.println("Error occurred");
                                return;
                            }
                            if (!s.trim().equals("")) {
                                onDataReceiveListenerFromThread.onDataReceived(s);
                            } else
                                break;
                        }
                        else return;
                    }
                    System.out.println("Transmission finished writing" + spandanInputCommand);
                }
                else{
                    mySerialPort.closePort();
                    onDataReceiveListenerFromThread.onDataReceived("error");
                }
            } catch (Exception e) {
                mySerialPort.closePort();
                try {
                    onDataReceiveListenerFromThread.onDataReceived("error");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
            }
        }
    }
    public void setOnDataReceiveListener(OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread) {
        this.onDataReceiveListenerFromThread = onDataReceiveListenerFromThread;

//        }

    }
}