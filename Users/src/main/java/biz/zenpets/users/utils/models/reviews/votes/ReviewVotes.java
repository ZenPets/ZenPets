package biz.zenpets.users.utils.models.reviews.votes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewVotes {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("votes")
    @Expose
    private ArrayList<ReviewVote> votes = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ReviewVote> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<ReviewVote> votes) {
        this.votes = votes;
    }
}