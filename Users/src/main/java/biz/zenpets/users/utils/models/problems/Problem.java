package biz.zenpets.users.utils.models.problems;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Problem {

    @SerializedName("problemID") @Expose private String problemID;
    @SerializedName("problemText") @Expose private String problemText;

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