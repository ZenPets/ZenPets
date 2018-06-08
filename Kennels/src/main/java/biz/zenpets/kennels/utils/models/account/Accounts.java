package biz.zenpets.kennels.utils.models.account;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Accounts {

    @SerializedName("error") private Boolean error;
    @SerializedName("accounts") private ArrayList<Account> accounts = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }
}