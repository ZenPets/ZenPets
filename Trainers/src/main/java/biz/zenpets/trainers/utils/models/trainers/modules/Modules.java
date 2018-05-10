
package biz.zenpets.trainers.utils.models.trainers.modules;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Modules {

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