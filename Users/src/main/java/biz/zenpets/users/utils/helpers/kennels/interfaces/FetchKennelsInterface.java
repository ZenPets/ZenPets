package biz.zenpets.users.utils.helpers.kennels.interfaces;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.kennels.Kennel;

public interface FetchKennelsInterface {
    void onKennels(ArrayList<Kennel> data);
}