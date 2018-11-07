package co.zenpets.doctors.utils.models.problems;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Problems {

    @SerializedName("error") private Boolean error;
    @SerializedName("problems") private ArrayList<Problem> problems = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Problem> getProblems() {
        return problems;
    }

    public void setProblems(ArrayList<Problem> problems) {
        this.problems = problems;
    }
}