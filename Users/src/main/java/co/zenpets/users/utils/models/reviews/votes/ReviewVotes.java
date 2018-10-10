package co.zenpets.users.utils.models.reviews.votes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewVotes {

    @SerializedName("error") private Boolean error;
    @SerializedName("votes") private ArrayList<ReviewVote> votes = null;

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