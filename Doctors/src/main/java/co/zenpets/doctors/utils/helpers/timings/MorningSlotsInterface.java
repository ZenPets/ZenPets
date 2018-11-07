package co.zenpets.doctors.utils.helpers.timings;

import java.util.ArrayList;

import co.zenpets.doctors.utils.models.appointments.AppointmentSlotsData;

public interface MorningSlotsInterface {
    void onMorningSlotResult(ArrayList<AppointmentSlotsData> morningTimeSlotsData);
}