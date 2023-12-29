package in.sunfox.healthcare.java.spandan_qms.database_service;

import com.google.gson.annotations.SerializedName;

public class DeviceLogData {
    @SerializedName("username")
    private String username;
    @SerializedName("timeStamp")
    private long timeStamp;
    @SerializedName("stage")
    private int stage;
    @SerializedName("logOfStage")
    private int logOfStage;
    @SerializedName("uniqueDeviceId")
    private String uniqueDeviceId;
    @SerializedName("result")
    private String result;
    @SerializedName("failReason")
    private String failReason;
    @SerializedName("logEcgData") // Field for LogEcgData object
    private String logEcgData;
    @SerializedName("logDescription")
    private String logDescription;

    public String getLogEcgData() {
        return logEcgData;
    }
    public void setLogEcgData(String logEcgData) {
        this.logEcgData = logEcgData;
    }
    public DeviceLogData(String username, long timeStamp, int stage, String uniqueDeviceId, String result, String failReason, int logOfStage, String logEcgData,String logDescription){
        this.uniqueDeviceId = uniqueDeviceId;
        this.result = result;
        this.timeStamp = timeStamp;
        this.username = username;
        this.stage =stage;
        this.failReason = failReason;

        this.logOfStage = logOfStage;
        this.logEcgData = logEcgData;
        this.logDescription = logDescription;
    }
    public int getLogOfStage() {
        return logOfStage;
    }

    public void setLogOfStage(int logOfStage) {
        this.logOfStage = logOfStage;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public int getStage() {
        return stage;
    }

    public String getResult() {
        return result;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getUsername() {
        return username;
    }
    public String getFailReason(){
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogDescription() {
        return logDescription;
    }

    public void setLogDescription(String logDescription) {
        this.logDescription = logDescription;
    }
}
