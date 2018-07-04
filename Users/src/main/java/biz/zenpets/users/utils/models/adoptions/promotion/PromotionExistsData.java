package biz.zenpets.users.utils.models.adoptions.promotion;

import com.google.gson.annotations.SerializedName;

public class PromotionExistsData {

    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;

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
}