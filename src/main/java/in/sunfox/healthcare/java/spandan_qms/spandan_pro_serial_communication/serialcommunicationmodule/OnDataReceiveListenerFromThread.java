package in.sunfox.healthcare.java.spandan_qms.spandan_pro_serial_communication.serialcommunicationmodule;

public interface OnDataReceiveListenerFromThread<T> {
    void onDataReceived(T data) throws Exception;
    void usbAuthentication(String data);
}
