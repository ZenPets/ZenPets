package co.zenpets.users.utils.helpers.timings;

import java.util.ArrayList;

import co.zenpets.users.utils.models.appointment.slots.MorningTimeSlotsData;

public interface MorningSlotsInterface {
    void onMorningSlotResult(ArrayList<MorningTimeSlotsData> morningTimeSlotsData);
}