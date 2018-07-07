package biz.zenpets.users.utils.models.trainers.reviews.votes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrainerReviewVotes {

    @SerializedName("error") private Boolean error;
    @SerializedName("votes") private ArrayList<TrainerReviewVote> votes = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<TrainerReviewVote> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<TrainerReviewVote> votes) {
        this.votes = votes;
    }
}