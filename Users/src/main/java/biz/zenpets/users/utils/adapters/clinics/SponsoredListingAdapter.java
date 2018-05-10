package biz.zenpets.users.utils.adapters.clinics;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.doctors.DoctorsData;

public class SponsoredListingAdapter extends RecyclerView.Adapter<SponsoredListingAdapter.ClinicsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<DoctorsData> arrDoctors;

    public SponsoredListingAdapter(Activity activity, ArrayList<DoctorsData> arrDoctors) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrDoctors = arrDoctors;
    }

    @Override
    public int getItemCount() {
        return arrDoctors.size();
    }

    @Override
    public void onBindViewHolder(ClinicsVH holder, final int position) {
        final DoctorsData data = arrDoctors.get(position);

        /* SET THE CLINIC NAME */
        holder.txtClinicName.setText(data.getClinicName());

        /* SET THE CLINIC CITY AND LOCALITY */
        String cityName = data.getCityName();
        String localityName = data.getLocalityName();
        holder.txtClinicAddress.setText(activity.getString(R.string.sponsored_clinic_address_placeholder, cityName, localityName));

        /* SET THE CLINIC DISTANCE FROM THE USER'S CURRENT LOCATION */
        String tilde = activity.getString(R.string.generic_tilde);
        String distance = data.getClinicDistance();
        String km = activity.getString(R.string.sponsored_clinic_distance_km);
        holder.txtClinicDistance.setText(activity.getString(R.string.sponsored_clinic_distance_placeholder, tilde, distance, km));

        /* SET THE DOCTOR'S CHARGES */
        holder.txtClinicCharges.setText(data.getDoctorCharges());
    }

    @Override
    public ClinicsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.sponsored_clinics_item, parent, false);

        return new ClinicsVH(itemView);
    }

    class ClinicsVH extends RecyclerView.ViewHolder	{

        final LinearLayout linlaClinicContainer;
        final AppCompatTextView txtClinicName;
        final AppCompatTextView txtClinicAddress;
        final LinearLayout linlaClinicCharges;
        final AppCompatTextView txtClinicCharges;
        final LinearLayout linlaClinicReviews;
        final AppCompatTextView txtClinicReviews;
        final LinearLayout linlaClinicDistance;
        final AppCompatTextView txtClinicDistance;

        ClinicsVH(View v) {
            super(v);

            linlaClinicContainer = v.findViewById(R.id.linlaClinicContainer);
            txtClinicName = v.findViewById(R.id.txtClinicName);
            txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
            linlaClinicCharges = v.findViewById(R.id.linlaClinicCharges);
            txtClinicCharges = v.findViewById(R.id.txtClinicCharges);
            linlaClinicReviews = v.findViewById(R.id.linlaClinicReviews);
            txtClinicReviews = v.findViewById(R.id.txtClinicReviews);
            linlaClinicDistance = v.findViewById(R.id.linlaClinicDistance);
            txtClinicDistance = v.findViewById(R.id.txtClinicDistance);
        }
    }
}