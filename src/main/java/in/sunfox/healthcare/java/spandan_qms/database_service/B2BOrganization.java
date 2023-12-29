package in.sunfox.healthcare.java.spandan_qms.database_service;

import com.google.gson.annotations.SerializedName;

public class B2BOrganization {
    @SerializedName("api_key")
    private String api_key;
    @SerializedName("organisation_name")
    private String organisation_name;


    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getOrganisation_name() {
        return organisation_name;
    }

    public void setOrganisation_name(String organisation_name) {
        this.organisation_name = organisation_name;
    }
}
