package in.sunfox.healthcare.java.spandan_qms.database_service;

import java.net.URL;

public class FileUrlFetchedFromAws {
    private URL url;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
