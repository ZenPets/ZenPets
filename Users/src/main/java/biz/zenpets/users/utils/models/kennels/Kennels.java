package biz.zenpets.users.utils.models.kennels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Kennels {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;
    @SerializedName("pageNumber")
    @Expose
    private Integer pageNumber;
    @SerializedName("kennels")
    @Expose
    private ArrayList<Kennel> kennels = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Kennels withError(Boolean error) {
        this.error = error;
        return this;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Kennels withTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Kennels withPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public ArrayList<Kennel> getKennels() {
        return kennels;
    }

    public void setKennels(ArrayList<Kennel> kennels) {
        this.kennels = kennels;
    }

    public Kennels withKennels(ArrayList<Kennel> kennels) {
        this.kennels = kennels;
        return this;
    }
}