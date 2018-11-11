package co.zenpets.doctors.utils.adapters.clinics;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.models.clinics.ClinicData;

public class ClinicsAdapter extends RecyclerView.Adapter<ClinicsAdapter.ClinicsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<ClinicData> arrClinics;

    public ClinicsAdapter(Activity activity, ArrayList<ClinicData> arrClinics) {

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
        final ClinicData data = arrClinics.get(position);

        /* SET THE CLINIC LOGO */
        if (data.getClinicLogo() != null)   {
            Uri uri = Uri.parse(data.getClinicLogo());
            holder.imgvwClinicLogo.setImageURI(uri);
        }

        /* SET THE CLINIC NAME */
        if (data.getClinicName() != null)   {
            holder.txtClinicName.setText(data.getClinicName());
        }

        /* SET THE CLINIC DETAILS */
        if (data.getLocalityName() != null && data.getCityName() != null)   {
            String locality = data.getLocalityName();
            String city = data.getCityName();
            holder.txtClinicDetails.setText(activity.getString(R.string.profile_clinic_item_details_placeholder, city, locality));
        }

        /* SHOW THE CLINIC DETAILS */
//        holder.linlaClinicContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, ClinicDetails.class);
//                intent.putExtra("CLINIC_ID", data.getClinicID());
//                activity.startActivity(intent);
//            }
//        });
    }

    @NonNull
    @Override
    public ClinicsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.profile_clinic_item, parent, false);

        return new ClinicsVH(itemView);
    }

    class ClinicsVH extends RecyclerView.ViewHolder	{
        final LinearLayout linlaClinicContainer;
        final SimpleDraweeView imgvwClinicLogo;
        final AppCompatTextView txtClinicName;
        final AppCompatTextView txtClinicDetails;

        ClinicsVH (View v) {
            super(v);
            linlaClinicContainer = v.findViewById(R.id.linlaClinicContainer);
            imgvwClinicLogo = v.findViewById(R.id.imgvwClinicLogo);
            txtClinicName = v.findViewById(R.id.txtClinicName);
            txtClinicDetails = v.findViewById(R.id.txtClinicDetails);
        }
    }
}