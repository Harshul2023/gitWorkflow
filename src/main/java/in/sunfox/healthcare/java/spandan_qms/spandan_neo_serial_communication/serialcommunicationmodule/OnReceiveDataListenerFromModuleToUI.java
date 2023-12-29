package in.sunfox.healthcare.java.spandan_qms.spandan_neo_serial_communication.serialcommunicationmodule;

public interface
OnReceiveDataListenerFromModuleToUI {
    void usbOnDataReceive(String data) throws Exception;
    void usbAuthentication(String data);
}
