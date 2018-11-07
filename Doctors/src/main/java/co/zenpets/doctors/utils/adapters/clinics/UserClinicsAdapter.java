package co.zenpets.doctors.utils.adapters.clinics;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.details.clinic.ClinicDetails;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinic;

public class UserClinicsAdapter extends RecyclerView.Adapter<UserClinicsAdapter.ClinicsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<DoctorClinic> arrClinics;

    public UserClinicsAdapter(Activity activity, ArrayList<DoctorClinic> arrClinics) {

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
    public void onBindViewHolder(final ClinicsVH holder, final int position) {
        final DoctorClinic data = arrClinics.get(position);

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
                Intent intent = new Intent(activity, ClinicDetails.class);
                intent.putExtra("CLINIC_ID", data.getClinicID());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public ClinicsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.home_clinics_fragment_item, parent, false);

        return new ClinicsVH(itemView);
    }

    class ClinicsVH extends RecyclerView.ViewHolder	{
        final LinearLayout linlaClinicContainer;
        final SimpleDraweeView imgvwClinicLogo;
        final AppCompatTextView txtClinicName;
        final AppCompatTextView txtClinicAddress;

        ClinicsVH (View v) {
            super(v);
            linlaClinicContainer = v.findViewById(R.id.linlaClinicContainer);
            imgvwClinicLogo = v.findViewById(R.id.imgvwClinicLogo);
            txtClinicName = v.findViewById(R.id.txtClinicName);
            txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
        }
    }
}