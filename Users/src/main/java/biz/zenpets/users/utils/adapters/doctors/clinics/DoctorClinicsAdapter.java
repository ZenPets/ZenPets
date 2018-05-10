package biz.zenpets.users.utils.adapters.doctors.clinics;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.clinics.ratings.ClinicRating;
import biz.zenpets.users.utils.models.clinics.ratings.ClinicRatingsAPI;
import biz.zenpets.users.utils.models.doctors.clinics.Clinic;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorClinicsAdapter extends RecyclerView.Adapter<DoctorClinicsAdapter.ClinicsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Clinic> arrClinics;

    public DoctorClinicsAdapter(Activity activity, ArrayList<Clinic> arrClinics) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrClinics = arrClinics;
    }

    @Override
    public int getItemCount() {
        return arrClinics.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ClinicsVH holder, final int position) {
        final Clinic data = arrClinics.get(position);

        /* SET THE CLINIC NAME */
        if (data.getClinicName() != null)   {
            holder.txtClinicName.setText(data.getClinicName());
        }

        /* SET THE CLINIC ADDRESS */
        if (data.getClinicAddress() != null) {
            String CLINIC_ADDRESS = data.getClinicAddress();
            String CLINIC_CITY = data.getCityName();
            String CLINIC_STATE = data.getStateName();
            String CLINIC_PIN_CODE = data.getClinicPinCode();
            holder.txtClinicAddress.setText(activity.getString(R.string.doctor_details_address_placeholder, CLINIC_ADDRESS, CLINIC_CITY, CLINIC_STATE, CLINIC_PIN_CODE));
        }

        /* FETCH THE CLINIC RATINGS */
        ClinicRatingsAPI api = ZenApiClient.getClient().create(ClinicRatingsAPI.class);
        Call<ClinicRating> call = api.fetchClinicRatings(data.getClinicID());
        call.enqueue(new Callback<ClinicRating>() {
            @Override
            public void onResponse(Call<ClinicRating> call, Response<ClinicRating> response) {
                ClinicRating rating = response.body();
                if (rating != null) {
                    String clinicRating = rating.getClinicRating();
                    if (clinicRating != null
                            && !clinicRating.equalsIgnoreCase("")
                            && !clinicRating.equalsIgnoreCase("null"))   {
                        holder.clinicRating.setVisibility(View.VISIBLE);
                        holder.clinicRating.setRating(Float.parseFloat(clinicRating));
                    } else {
                        holder.clinicRating.setVisibility(View.GONE);
                    }
                } else {
                    holder.clinicRating.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ClinicRating> call, Throwable t) {
                Log.e("RATINGS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @NonNull
    @Override
    public ClinicsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctor_clinics_item, parent, false);

        return new ClinicsVH(itemView);
    }

    class ClinicsVH extends RecyclerView.ViewHolder	{
        AppCompatTextView txtClinicName;
        AppCompatRatingBar clinicRating;
        AppCompatTextView txtClinicAddress;
        ClinicsVH(View v) {
            super(v);

            txtClinicName = v.findViewById(R.id.txtClinicName);
            clinicRating = v.findViewById(R.id.clinicRating);
            txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
        }
    }
}