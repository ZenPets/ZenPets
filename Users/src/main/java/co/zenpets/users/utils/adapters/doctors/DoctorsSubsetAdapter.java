package co.zenpets.users.utils.adapters.doctors;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.doctors.list.Doctor;

public class DoctorsSubsetAdapter extends RecyclerView.Adapter<DoctorsSubsetAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Doctor> arrDoctors;

    public DoctorsSubsetAdapter(Activity activity, ArrayList<Doctor> arrDoctors, LatLng LATLNG_ORIGIN) {

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
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Doctor data = arrDoctors.get(position);

        /* SET THE DOCTOR'S DISPLAY PROFILE */
        String DOCTOR_PROFILE = data.getDoctorDisplayProfile();
        if (DOCTOR_PROFILE != null) {
            Uri uri = Uri.parse(DOCTOR_PROFILE);
            holder.imgvwDoctorProfile.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                    .build();
            holder.imgvwDoctorProfile.setImageURI(request.getSourceUri());
        }

        /* SET THE CLINIC LOGO */
        String CLINIC_LOGO = data.getClinicLogo();
        if (CLINIC_LOGO != null)    {
            Uri uriClinic = Uri.parse(CLINIC_LOGO);
            holder.imgvwClinicCover.setImageURI(uriClinic);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                    .build();
            holder.imgvwClinicCover.setImageURI(request.getSourceUri());
        }

        /* SET THE DOCTOR'S NAME */
        String doctorPrefix = data.getDoctorPrefix();
        String doctorName = data.getDoctorName();
        holder.txtDoctorName.setText(activity.getString(R.string.appointment_creator_details_doctor, doctorPrefix, doctorName));

        /* SET THE REVIEW VOTE STATS */
        holder.txtDoctorLikes.setText(data.getDoctorVoteStats());

        /* SET THE DOCTOR'S EXPERIENCE */
        String strExp = activity.getString(R.string.doctor_list_doctor_experience_text);
        String docExperience = data.getDoctorExperience();
        holder.txtDoctorExp.setText(activity.getString(R.string.doctor_list_doctor_experience_placeholder, docExperience, strExp));

        /* SET THE DOCTOR'S CHARGES */
        String currency = data.getCurrencySymbol();
        String charges = data.getDoctorCharges();
        holder.txtDoctorCharges.setText(activity.getString(R.string.doctor_details_doc_charges_placeholder, currency, charges));

        /* SET THE CLINIC DISTANCE */
        String strClinicDistance = data.getClinicDistance();
        if (strClinicDistance != null
                && !strClinicDistance.equals("Unknown")
                && !strClinicDistance.equalsIgnoreCase("null"))  {
            String strTilde = activity.getString(R.string.generic_tilde);
            holder.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, strClinicDistance));
        } else {
            String strTilde = activity.getString(R.string.generic_tilde);
            String strInfinity = activity.getString(R.string.generic_infinity);
            holder.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, strInfinity));
        }
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctors_subset_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{

        SimpleDraweeView imgvwClinicCover;
        SimpleDraweeView imgvwDoctorProfile;
        TextView txtDoctorName;
        TextView txtDoctorLikes;
        TextView txtDoctorExp;
        TextView txtDoctorCharges;
        TextView txtClinicDistance;

        AdoptionsVH(View v) {
            super(v);

            imgvwClinicCover = v.findViewById(R.id.imgvwClinicCover);
            imgvwDoctorProfile = v.findViewById(R.id.imgvwDoctorProfile);
            txtDoctorName = v.findViewById(R.id.txtDoctorName);
            txtDoctorLikes = v.findViewById(R.id.txtDoctorLikes);
            txtDoctorExp = v.findViewById(R.id.txtDoctorExp);
            txtDoctorCharges = v.findViewById(R.id.txtDoctorCharges);
            txtClinicDistance = v.findViewById(R.id.txtClinicDistance);
        }
    }
}