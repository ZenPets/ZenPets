package co.zenpets.doctors.utils.models.doctors.claims;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DoctorClaimsAPI {

    /* MAP A DOCTOR TO A CLINIC */
    @POST("newDoctorClaim")
    @FormUrlEncoded
    Call<Claim> newDoctorClaim(
            @Field("doctorID") String doctorID,
            @Field("doctorClaimEmail") String doctorClaimEmail,
            @Field("doctorClaimPhone") String doctorClaimPhone,
            @Field("doctorClaimStatus") String doctorClaimStatus,
            @Field("doctorClaimApproved") String doctorClaimApproved,
            @Field("doctorClaimTimestamp") String doctorClaimTimestamp);

    /** FETCH THE CLAIM DETAILS **/
    @GET("fetchClaimDetails")
    Call<ClaimStatus> fetchClaimDetails(@Query("doctorClaimID") String doctorClaimID);
}