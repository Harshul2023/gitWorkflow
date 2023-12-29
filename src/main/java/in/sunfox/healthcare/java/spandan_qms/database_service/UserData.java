package in.sunfox.healthcare.java.spandan_qms.database_service;

import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("password")
    private String password;
    @SerializedName("token")
    private String token;
    @SerializedName("stagesPermission")
    private int stagesPermission;
    @SerializedName("role")
    private int role;

    public int getStagesPermission() {
        return stagesPermission;
    }

    public void setStagesPermission(int stagesPermission) {
        this.stagesPermission = stagesPermission;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
