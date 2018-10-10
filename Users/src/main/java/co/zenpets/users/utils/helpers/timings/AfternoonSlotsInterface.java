package co.zenpets.users.utils.helpers.timings;

import java.util.ArrayList;

import co.zenpets.users.utils.models.appointment.slots.AfternoonTimeSlotsData;

public interface AfternoonSlotsInterface {
    void onAfternoonSlotResult(ArrayList<AfternoonTimeSlotsData> arrAfternoonSlots);
}