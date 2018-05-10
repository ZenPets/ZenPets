package biz.zenpets.users.utils.models.doctors.modules;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface QualificationsAPI {

    /** FETCH THE DOCTOR'S QUALIFICATIONS **/
    @GET("fetchDoctorEducation")
    Call<Qualifications> fetchDoctorEducation(@Query("doctorID") String doctorID);

    /** FETCH THE DOCTOR'S QUALIFICATIONS **/
    @GET("fetchDoctorEducation")
    Call<String> fetchDoctorEducationString(@Query("doctorID") String doctorID);
}