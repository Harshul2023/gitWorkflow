package in.sunfoxhealthcare.java.commons.serialcommunicationmodule;

public interface
OnReceiveDataListenerFromModuleToUI {
    void usbOnDataReceive(String data) throws Exception;
    void usbAuthentication(String data);
}
