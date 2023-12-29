package in.sunfox.healthcare.java.spandan_qms.database_service;

public class SpandanDevice {
    private String deviceNumber;
    private String pcbNumber;
    private String pcbState;

    public SpandanDevice(String deviceNumber, String pcbNumber, String pcbState) {
        this.deviceNumber = deviceNumber;
        this.pcbNumber = pcbNumber;
        this.pcbState = pcbState;
    }
    public SpandanDevice(){

        System.out.println("DEvice");
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public String getPcbNumber() {
        return pcbNumber;
    }

    public String getPcbState() {
        return pcbState;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public void setPcbState(String pcbState) {
        this.pcbState = pcbState;
    }

    public void setPcbNumber(String pcbNumber) {
        this.pcbNumber = pcbNumber;
    }
}
