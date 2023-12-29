package in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.serialcommunicationmodule;

import com.fazecast.jSerialComm.SerialPort;

public class SerialCommunication {
    private SerialPort serialPort;

    private final OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread;

    int BaudRate = 115200;
    int DataBits = 8;
    int StopBits = SerialPort.ONE_STOP_BIT;
    int Parity = SerialPort.NO_PARITY;

    SerialCommunication(OnDataReceiveListenerFromThread<String> onDataReceiveListenerFromThread) {
        this.onDataReceiveListenerFromThread = onDataReceiveListenerFromThread;
    }

    SerialCommunicationThread startThread;

    public void startTransmission(String command) {
        startThread = new SerialCommunicationThread(serialPort, command);
        startThread.setName("startThread");
        startThread.setOnDataReceiveListener(onDataReceiveListenerFromThread);
        startThread.start();

    }

    public void stopTransmission(String command) {
        try {
            if (startThread.isAlive())
                startThread.interrupt();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Exception in serial communication");
        }
        SerialCommunicationThread stopThread = new SerialCommunicationThread(serialPort, command);
        stopThread.setName("stopThread");
        stopThread.setOnDataReceiveListener(onDataReceiveListenerFromThread);
        stopThread.start();

    }

    public void startCTransmission(String command) {
        SerialCommunicationThread thread = new SerialCommunicationThread(serialPort, command);
        thread.setOnDataReceiveListener(onDataReceiveListenerFromThread);
        thread.start();

    }

    public void connectPort(SerialPort mySerialPort) throws InterruptedException {
        this.serialPort = mySerialPort;
        System.out.println("Opening port");
        if (!serialPort.isOpen()) {
            serialPort.setComPortParameters(BaudRate, DataBits, StopBits, Parity);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
            serialPort.openPort(0); //open the port
            if (serialPort.isOpen()) {
                System.out.println("Port Opened from class");
            }
        } else
            System.out.println("Port is open");
        serialPort.isOpen();
    }

}