package biz.zenpets.users.utils.helpers.timings.slots;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.appointment.slots.AfternoonTimeSlotsData;

interface AfternoonSlotsInterface {
    void onAfternoonSlotResult(ArrayList<AfternoonTimeSlotsData> arrAfternoonSlots);
}