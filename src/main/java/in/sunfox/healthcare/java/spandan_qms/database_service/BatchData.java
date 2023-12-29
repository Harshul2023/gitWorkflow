package in.sunfox.healthcare.java.spandan_qms.database_service;

import com.google.gson.annotations.SerializedName;

public class BatchData {
    @SerializedName("batchId")
    private String batchId;

    @SerializedName("batchSize")
    private Long batchSize;

    @SerializedName("username")
    private String username;

    @SerializedName("date")
    private String date;

    @SerializedName("batchDescriptionString")
    private String batchDescriptionString;
    @SerializedName("latestDeviceNumber")
    private String latestDeviceNUmber;

    // Constructors
    public BatchData() {
    }

    public BatchData(String batchId, Long batchSize, String username, String date, String batchDescriptionString,String latestDeviceNUmber) {
        this.batchId = batchId;
        this.batchSize = batchSize;
        this.username = username;
        this.date = date;
        this.batchDescriptionString = batchDescriptionString;
        this.latestDeviceNUmber = latestDeviceNUmber;
    }

    // Getter and Setter methods
    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Long getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Long batchSize) {
        this.batchSize = batchSize;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBatchDescriptionString() {
        return batchDescriptionString;
    }

    public void setBatchDescriptionString(String batchDescriptionString) {
        this.batchDescriptionString = batchDescriptionString;
    }
    public void setLatestDeviceNUmber(String latestDeviceNUmber){
        this.latestDeviceNUmber = latestDeviceNUmber;
    }

    public String getLatestDeviceNumber() {
        return latestDeviceNUmber;
    }
}
