package co.zenpets.doctors.utils.adapters.clinics;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.creator.clinic.SearchClinicDetails;
import co.zenpets.doctors.utils.models.clinics.ClinicData;

public class ClinicsSearchAdapter extends RecyclerView.Adapter<ClinicsSearchVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<ClinicData> arrClinics;

    public ClinicsSearchAdapter(Activity activity, ArrayList<ClinicData> arrClinics) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrClinics = arrClinics;
    }

    @Override
    public ClinicsSearchVH onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.clinic_search_item, parent, false);

        return new ClinicsSearchVH(itemView);
    }

    @Override
    public void onBindViewHolder(ClinicsSearchVH holder, int position) {
        final ClinicData data = arrClinics.get(position);

        /* SET THE CLINIC'S LOGO */
        if (data.getClinicLogo() != null)   {
            Uri uri = Uri.parse(arrClinics.get(position).getClinicLogo());
            holder.imgvwClinicLogo.setImageURI(uri);
        }

        /* SET THE CLINIC'S NAME */
        if (data.getClinicName() != null)   {
            holder.txtClinicName.setText(data.getClinicName());
        }

        /* SET THE CLINIC'S ADDRESS */
        if (data.getClinicAddress() != null)    {
            String address = data.getClinicAddress();
            String city = data.getCityName();
            String state = data.getStateName();
            String pin = data.getClinicPinCode();
            String landmark = data.getClinicLandmark();
            holder.txtClinicAddress.setText(address + "\n" + landmark + ",\n" + city + ", " + state + ", " + pin);
        }

        /* SHOW THE CLINIC DETAILS */
        holder.linlaClinicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SearchClinicDetails.class);
                intent.putExtra("CLINIC_ID", data.getClinicID());
                intent.putExtra("DOCTOR_ID", data.getDoctorID());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrClinics.size();
    }
}