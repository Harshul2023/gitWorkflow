package in.sunfox.healthcare.java.spandan_qms.database_service;


import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.Map;

public class DatabaseUtility {
    static WebService webService = new WebService();
//    public static void updateDeviceCurrentState(String uniqueDeviceId, String newState, OnDataReceiveListener<String> onDataReceiveListenerUpdate) {
//        webService.updateOperationInDatabase(uniqueDeviceId, newState, onDataReceiveListenerUpdate);
//    }
    public static void getCurrentStateOfDevice(String uniqueDeviceId, OnDataReceiveListener<DeviceLogData> onDataReceiveListenerStateChange) {
        webService.getStateFromDatabase(uniqueDeviceId, onDataReceiveListenerStateChange);
    }
    public static void updateListOfLogOperationsInDatabase(DeviceLogData stateChangeList, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.setListInDatabase(stateChangeList, onDataReceiveListener);
    }
    public static void newUserSignUp(String name, String email, String phoneNumber, String password, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.createNewUser(name, email, phoneNumber, password, onDataReceiveListener);
    }
    public static void validateLogin(String emailString, String passwordString, OnDataReceiveListener<ArrayList<Integer>> onDataReceiveListener) {
        webService.validateLogin(emailString, passwordString, onDataReceiveListener);
    }
    public static void creatNewDevice(String uniqueDeviceId, String userName, long timeStamp, String batchNumber, String deviceVariant, OnDataReceiveListener<String> onDataReceiveListener, String microControllerId, String version, String productionPlantId, String clientName, String masterKey) {
//        localService.addDevice(uniqueDeviceId,userName,timeStamp,microControllerId);
        webService.createNewDevice(uniqueDeviceId, microControllerId,batchNumber, deviceVariant, onDataReceiveListener,version,productionPlantId,clientName,masterKey);
    }
    public static void mapAccessories(String chestCable, String connectingCable, OnDataReceiveListener<String> onDataReceiveListener, String uniqueDeviceId) {
        webService.mapAccessories(chestCable, connectingCable, uniqueDeviceId, onDataReceiveListener);
    }
    public static void getAllBatchesList(OnDataReceiveListener<Map<String,BatchData>> onDataReceiveListener) {
        webService.getAllBatchesList(onDataReceiveListener);
    }
    public static void updateLatestDeviceNumberInBatch(String batchNo, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.updateLatestDeviceNumberInBatch(batchNo, onDataReceiveListener);
    }
//    public static void getLastUniqueDeviceIdOfParticularBatch(String value, OnDataReceiveListener<String> onDataReceiveListener) {
//        webService.getLastUniqueDeviceIdOfParticularBatch(value, onDataReceiveListener);
//    }
    public static void createNewBatch(String batchId, Long batchSize, String username, String date, String batchDescriptionString, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.creatNewBatch(batchId,batchSize,username,date,batchDescriptionString,onDataReceiveListener);
    }
    public static void fetchLatestFirmwareFile(String fileVersion,OnDataReceiveListener<String>onDataReceiveListener) {
        webService.fetchLatestFirmwareFile(fileVersion,onDataReceiveListener);
    }

    public static void clearToken(OnDataReceiveListener<String> onDataReceiveListener) {
        webService.clearToken(onDataReceiveListener);
    }

    public static void getAllUsersList(OnDataReceiveListener<Map<String, UserData>> onDataReceiveListener) {
        webService.getAllUsers(onDataReceiveListener);
    }

    public static void updateStagesPermission(String emailId, int decimalValue, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.updateStagesPermission(emailId,decimalValue,onDataReceiveListener);
    }
    public static void getAllLogsOfDevice(String uniqueDeviceId,OnDataReceiveListener<DeviceLogData[]> onDataReceiveListener){
        webService.getAllLogsOfDevice(uniqueDeviceId,onDataReceiveListener);
    }

    public static void getLogEcgData(String uniqueDeviceId, OnDataReceiveListener<String> onDataReceiveListener,String stage) {
        webService.getLogEcgData(uniqueDeviceId,onDataReceiveListener,stage);
    }
    public static void getDeviceData(String uniqueDeviceId, OnDataReceiveListener<DeviceData> onDataReceiveListener) {
        webService.getDeviceData(uniqueDeviceId,onDataReceiveListener);
    }

    public static void fetchLatestExeFile(String s, OnDataReceiveListener<String> onDataReceiveListener, BorderPane mainBorderPane, Thread progressThread) {
        webService.fetchLatestExe(s,onDataReceiveListener,mainBorderPane,progressThread);
    }

    public static void fetchLatestFirmwareFileNeo(String fileVersion, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.fetchLatestFirmwareFileSpandanNeo(fileVersion,onDataReceiveListener);
    }

    public static void fetchLatestFirmwareFilePro(String fileVersion, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.fetchLatestFirmwareFileSpandanPro(fileVersion,onDataReceiveListener);
    }

    public static void getDeviceIdFromMid(String s, OnDataReceiveListener<ArrayList<String>> onReceiveDataListener) {
        webService.getDeviceIdFromMid(s,onReceiveDataListener);
    }
    public static void getB2bCLients(OnDataReceiveListener<Map<String, B2BOrganization>> onReceiveDataListener) {
        webService.getB2bClients(onReceiveDataListener);
    }

    public static void updateDeviceTable(String uniqueDeviceId, String firmwareVersion, OnDataReceiveListener<String> onDataReceiveListener) {
        webService.updateDeviceTable(uniqueDeviceId,firmwareVersion,onDataReceiveListener);
    }
}



