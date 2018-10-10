package co.zenpets.kennels.utils.models.test;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestKennels {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennels") private ArrayList<TestKennel> kennels = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<TestKennel> getKennels() {
        return kennels;
    }

    public void setKennels(ArrayList<TestKennel> kennels) {
        this.kennels = kennels;
    }
}