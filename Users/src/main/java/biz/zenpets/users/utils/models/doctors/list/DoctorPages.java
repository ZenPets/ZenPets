package biz.zenpets.users.utils.models.doctors.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoctorPages {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("totalDoctors") @Expose private String totalDoctors;
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

    public String getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(String totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }
}