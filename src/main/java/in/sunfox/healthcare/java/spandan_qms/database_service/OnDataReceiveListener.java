package in.sunfox.healthcare.java.spandan_qms.database_service;

public interface OnDataReceiveListener<T> {
    void onDataReceived(T data);
    void onDataReceiveError(String errorMsg);
}
