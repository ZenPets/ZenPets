package co.zenpets.users.utils.models.trainers.modules;

import com.google.gson.annotations.SerializedName;

public class ModulesMinMax {

    @SerializedName("error") private Boolean error;
    @SerializedName("minTrainingFee") private String minTrainingFee;
    @SerializedName("maxTrainingFee") private String maxTrainingFee;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMinTrainingFee() {
        return minTrainingFee;
    }

    public void setMinTrainingFee(String minTrainingFee) {
        this.minTrainingFee = minTrainingFee;
    }

    public String getMaxTrainingFee() {
        return maxTrainingFee;
    }

    public void setMaxTrainingFee(String maxTrainingFee) {
        this.maxTrainingFee = maxTrainingFee;
    }
}