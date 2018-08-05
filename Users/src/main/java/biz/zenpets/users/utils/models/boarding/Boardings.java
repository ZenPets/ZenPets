package biz.zenpets.users.utils.models.boarding;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Boardings {
    @SerializedName("error") private Boolean error;
    @SerializedName("totalPages") private Integer totalPages;
    @SerializedName("pageNumber") private Integer pageNumber;
    @SerializedName("boardings") private ArrayList<Boarding> boardings = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
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

    public ArrayList<Boarding> getBoardings() {
        return boardings;
    }

    public void setBoardings(ArrayList<Boarding> boardings) {
        this.boardings = boardings;
    }
}