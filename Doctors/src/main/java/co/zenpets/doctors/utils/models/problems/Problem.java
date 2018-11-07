package co.zenpets.doctors.utils.models.problems;

import com.google.gson.annotations.SerializedName;

public class Problem {

    @SerializedName("problemID") private String problemID;
    @SerializedName("problemText") private String problemText;

    public String getProblemID() {
        return problemID;
    }

    public void setProblemID(String problemID) {
        this.problemID = problemID;
    }

    public String getProblemText() {
        return problemText;
    }

    public void setProblemText(String problemText) {
        this.problemText = problemText;
    }
}