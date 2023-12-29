package in.sunfox.healthcare.java.spandan_qms.database_service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Map;

public class DeviceData {

    @SerializedName("clientName")
    private String clientName;
    @SerializedName("masterKey")
    private String masterKey;
    @SerializedName("uniqueDeviceId")
    private String uniqueDeviceId;

    @SerializedName("microControllerId")
    private String microControllerId;

    @SerializedName("batchId")
    private String batchId;

    @SerializedName("deviceVariant")
    private String deviceVariant;

    @SerializedName("accessories")
    private Map<String, String> accessories;

    @SerializedName("firmwareVersion")
    private String firmwareVersion;

    public DeviceData() {

    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getProductionPlantId() {
        return productionPlantId;
    }

    public void setProductionPlantId(String productionPlantId) {
        this.productionPlantId = productionPlantId;
    }

    @SerializedName("productionPlantId")
    private String productionPlantId;


    public DeviceData(String uniqueDeviceId, String microControllerId, String batchId, String deviceVariant) {
        this.uniqueDeviceId = uniqueDeviceId;
        this.microControllerId = microControllerId;
        this.batchId = batchId;
        this.deviceVariant = deviceVariant;
    }

    public String getUniqueDeviceId() {
        return uniqueDeviceId;
    }

    public void setUniqueDeviceId(String uniqueDeviceId) {
        this.uniqueDeviceId = uniqueDeviceId;
    }

    public String getMicroControllerId() {
        return microControllerId;
    }

    public void setMicroControllerId(String microControllerId) {
        this.microControllerId = microControllerId;
    }

    public String getBatchNumber() {
        return batchId;
    }

    public void setBatchNumber(String batchId) {
        this.batchId = batchId;
    }

    public String getDeviceVariant() {
        return deviceVariant;
    }

    public void setDeviceVariant(String deviceVariant) {
        this.deviceVariant = deviceVariant;
    }

    public Map<String, String> getAccessories() {
        return accessories;
    }

    public void setAccessories(Map<String, String> accessories) {
        this.accessories = accessories;
    }


    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public static class AccessoriesSerializer implements JsonSerializer<Map<String, String>> {
        @Override
        public JsonElement serialize(Map<String, String> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("chestCable", src.get("chestCable"));
            jsonObject.addProperty("connectingCable", src.get("connectingCable"));
            return jsonObject;
        }


    }
    // Additional methods if needed
}
