package co.zenpets.users.utils.models.doctors.list;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Doctors {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("totalPages") private Integer totalPages;
    @SerializedName("pageNumber") private Integer pageNumber;
    @SerializedName("doctors") private ArrayList<Doctor> doctors = null;

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

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }
}