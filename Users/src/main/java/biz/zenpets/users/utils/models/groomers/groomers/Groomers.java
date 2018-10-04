package biz.zenpets.users.utils.models.groomers.groomers;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Groomers {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("totalPages") private Integer totalPages;
    @SerializedName("pageNumber") private Integer pageNumber;
    @SerializedName("groomers") private ArrayList<Groomer> groomers = null;

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

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public ArrayList<Groomer> getGroomers() {
        return groomers;
    }

    public void setGroomers(ArrayList<Groomer> groomers) {
        this.groomers = groomers;
    }
}