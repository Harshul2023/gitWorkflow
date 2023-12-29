package in.sunfoxhealthcare.java.commons.serialcommunicationmodule;

public interface OnDataReceiveListenerFromThread<T> {
    void onDataReceived(T data) throws Exception;
    void usbAuthentication(String data);
}
