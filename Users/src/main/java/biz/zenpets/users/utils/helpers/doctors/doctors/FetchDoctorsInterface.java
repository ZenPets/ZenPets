package biz.zenpets.users.utils.helpers.doctors.doctors;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.doctors.list.Doctor;

interface FetchDoctorsInterface {
    void onDoctorsList(ArrayList<Doctor> data);
}