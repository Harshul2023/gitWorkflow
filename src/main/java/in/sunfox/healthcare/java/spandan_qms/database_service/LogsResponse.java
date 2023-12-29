package in.sunfox.healthcare.java.spandan_qms.database_service;

import java.util.List;

public class LogsResponse {
    private List<DeviceLogData> logs;

    public List<DeviceLogData> getLogs() {
        return logs;
    }

    public void setLogs(List<DeviceLogData> logs) {
        this.logs = logs;
    }
}
