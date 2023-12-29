package in.sunfox.healthcare.java.spandan_qms.database_service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LogEcgData{
    @SerializedName("v1")
    private ArrayList<Double> v1;
    @SerializedName("v2")
    private ArrayList<Double> v2;
    @SerializedName("v3")
    private ArrayList<Double> v3;
    @SerializedName("v4")
    private ArrayList<Double> v4;
    @SerializedName("v5")
    private ArrayList<Double> v5;
    @SerializedName("v6")
    private ArrayList<Double> v6;
    @SerializedName("l1")
    private ArrayList<Double> l1;
    @SerializedName("l2")
    private ArrayList<Double> l2;

    public LogEcgData() {

    }

    public ArrayList<Double> getV1() {
        return v1;
    }
    public void setV1(ArrayList<Double> v1) {
        this.v1 = v1;
    }

    public LogEcgData(ArrayList<Double> v1, ArrayList<Double> v2, ArrayList<Double> v3, ArrayList<Double> v4, ArrayList<Double> v5, ArrayList<Double> v6, ArrayList<Double> l1, ArrayList<Double> l2) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.v5 = v5;
        this.v6 = v6;
        this.l1 = l1;
        this.l2 = l2;
    }

    public ArrayList<Double> getV2() {
        return v2;
    }

    public void setV2(ArrayList<Double> v2) {
        this.v2 = v2;
    }

    public ArrayList<Double> getV3() {
        return v3;
    }

    public void setV3(ArrayList<Double> v3) {
        this.v3 = v3;
    }

    public ArrayList<Double> getV4() {
        return v4;
    }

    public void setV4(ArrayList<Double> v4) {
        this.v4 = v4;
    }

    public ArrayList<Double> getV5() {
        return v5;
    }

    public void setV5(ArrayList<Double> v5) {
        this.v5 = v5;
    }

    public ArrayList<Double> getV6() {
        return v6;
    }

    public void setV6(ArrayList<Double> v6) {
        this.v6 = v6;
    }

    public ArrayList<Double> getL1() {
        return l1;
    }

    public void setL1(ArrayList<Double> l1) {
        this.l1 = l1;
    }

    public ArrayList<Double> getL2() {
        return l2;
    }

    public void setL2(ArrayList<Double> l2) {
        this.l2 = l2;
    }
}
