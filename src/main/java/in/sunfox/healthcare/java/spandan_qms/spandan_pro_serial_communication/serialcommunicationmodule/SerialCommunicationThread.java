package in.sunfox.healthcare.java.spandan_qms.spandan_pro_serial_communication.serialcommunicationmodule;


import com.fazecast.jSerialComm.SerialPort;
import in.sunfox.healthcare.java.spandan_qms.spandan_pro_serial_communication.SpandanUnlocking.UnlockMechanism;

import java.nio.charset.StandardCharsets;

public class SerialCommunicationThread extends Thread {
    private final String spandanInputCommand;
    private final SerialPort mySerialPort;

    private OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread;

    public SerialCommunicationThread(SerialPort mySerialPort, String spandanInputCommand) {
        this.mySerialPort = mySerialPort;
        this.spandanInputCommand = spandanInputCommand;

    }

    public synchronized void run() {
        try {
//            if (!mySerialPort.isOpen()) {
//                onDataReceiveListenerFromThread.onDataReceived("error");
//                return;
//            }

            if (spandanInputCommand.equals("0")) {
                byte[] writeByte = "STP".getBytes();
                mySerialPort.writeBytes(writeByte, "STP".getBytes().length);
                mySerialPort.flushIOBuffers();
                mySerialPort.closePort();

                Thread.sleep(1500);
                onDataReceiveListenerFromThread.onDataReceived("Port Closed");
                return;
            }
        } catch (Exception e) {
            System.out.println(
                    "Exception in thread"
            );
            return;
        }
        if (spandanInputCommand.contains("STA") || (spandanInputCommand.equals("c"))) {
//            mySerialPort.closePort();
            UnlockMechanism unlockMechanism = new UnlockMechanism();
            try {
                if (unlockMechanism.unlocking2("USER_MODE", mySerialPort)) {
                    mySerialPort.openPort();
                    byte[] WriteByte = spandanInputCommand.getBytes();


                    if (spandanInputCommand.contains("STA_V1"))
                        mySerialPort.writeBytes("SET_LEAD_V1".getBytes(), ("SET_LEAD_V1").length());


                    else if (spandanInputCommand.contains("STA_V2"))
                        mySerialPort.writeBytes("SET_LEAD_V2".getBytes(), ("SET_LEAD_V2").length());

                    else if (spandanInputCommand.contains("STA_V3"))
                        mySerialPort.writeBytes("SET_LEAD_V3".getBytes(), ("SET_LEAD_V3").length());
                    else if (spandanInputCommand.contains("STA_V4"))
                        mySerialPort.writeBytes("SET_LEAD_V4".getBytes(), ("SET_LEAD_V4").length());
                    else if (spandanInputCommand.contains("STA_V5"))
                        mySerialPort.writeBytes("SET_LEAD_V5".getBytes(), ("SET_LEAD_V5").length());
                    else if (spandanInputCommand.contains("STA_V6"))
                        mySerialPort.writeBytes("SET_LEAD_V6".getBytes(), ("SET_LEAD_V6").length());
                    else if (spandanInputCommand.contains("STA_LEAD_2"))
                        mySerialPort.writeBytes("SET_LEAD_2".getBytes(), ("SET_LEAD_2").length());
                    else if (spandanInputCommand.contains("STA_LEAD_1"))
                        mySerialPort.writeBytes("SET_LEAD_1".getBytes(), ("SET_LEAD_1").length());

                System.out.println("send set");
                    Thread.sleep(1500);
//                    if (spandanInputCommand.contains("STA_V1"))
//                        mySerialPort.writeBytes(WriteByte, ("SET_LEAD_V1").length());
                    mySerialPort.writeBytes(WriteByte, spandanInputCommand.getBytes().length);
                System.out.println(spandanInputCommand);
//                    mySerialPort.flushIOBuffers();
                    byte[] readBuffer = new byte[4];
                    while (mySerialPort.readBytes(readBuffer, readBuffer.length) != 0) {
                        if (mySerialPort.isOpen()) {
                            String s = new String(readBuffer, StandardCharsets.UTF_8);
                            if (mySerialPort.getLastErrorLocation() == 1024 || mySerialPort.getLastErrorLocation() == 1029) {
                                mySerialPort.closePort();

                                return;
                            }
                            if (!s.trim().isEmpty()) {

                                onDataReceiveListenerFromThread.onDataReceived(s);

                            } else
                                break;
                        } else return;
                    }
                    System.out.println("Transmission finished writing" + spandanInputCommand);
                } else {
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