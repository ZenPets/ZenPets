package biz.zenpets.trainers.utils.models.trainers;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Trainers {

    @SerializedName("error") private Boolean error;
    @SerializedName("trainers") private ArrayList<Trainer> trainers = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(ArrayList<Trainer> trainers) {
        this.trainers = trainers;
    }
}