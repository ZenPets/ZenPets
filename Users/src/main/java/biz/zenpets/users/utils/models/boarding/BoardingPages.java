package biz.zenpets.users.utils.models.boarding;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoardingPages {
    @SerializedName("error") @Expose private Boolean error;
    @SerializedName("totalBoardings") @Expose private String totalBoardings;
    @SerializedName("totalPages") @Expose private String totalPages;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTotalBoardings() {
        return totalBoardings;
    }

    public void setTotalBoardings(String totalBoardings) {
        this.totalBoardings = totalBoardings;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }
}