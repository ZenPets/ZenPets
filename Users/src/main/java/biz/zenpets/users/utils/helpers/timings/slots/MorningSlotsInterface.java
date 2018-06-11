package biz.zenpets.users.utils.helpers.timings.slots;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.appointment.slots.MorningTimeSlotsData;

interface MorningSlotsInterface {
    void onMorningSlotResult(ArrayList<MorningTimeSlotsData> morningTimeSlotsData);
}