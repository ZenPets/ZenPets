package biz.zenpets.users.utils.models.trainers.enquiry;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.trainers.modules.Module;

class TrainingEnquiries {

    @SerializedName("error") private Boolean error;
    @SerializedName("modules") private ArrayList<Module> modules = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public void setModules(ArrayList<Module> modules) {
        this.modules = modules;
    }
}