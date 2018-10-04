package biz.zenpets.users.utils.models.groomers.groomers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroomerPages {
    @SerializedName("error") @Expose private Boolean error;
    @SerializedName("message") @Expose private String message;
    @SerializedName("totalGroomers") @Expose private String totalGroomers;
    @SerializedName("totalPages") @Expose private String totalPages;

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

    public String getTotalGroomers() {
        return totalGroomers;
    }

    public void setTotalGroomers(String totalGroomers) {
        this.totalGroomers = totalGroomers;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }
}