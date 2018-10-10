package co.zenpets.users.utils.adapters.clinics.promoted;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.clinics.promotions.Promotion;

public class PromotedClinicsAdapter  extends RecyclerView.Adapter<PromotedClinicsAdapter.ClinicsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Promotion> arrPromoted;

    public PromotedClinicsAdapter(Activity activity, ArrayList<Promotion> arrPromoted) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrPromoted = arrPromoted;
    }

    @Override
    public int getItemCount() {
        return arrPromoted == null ? 0 : arrPromoted.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ClinicsVH holder, final int position) {
        final Promotion data = arrPromoted.get(position);

        /* SET THE CLINIC LOGO */
        String strKennelCoverPhoto = data.getClinicLogo();
        if (strKennelCoverPhoto != null
                && !strKennelCoverPhoto.equalsIgnoreCase("")
                && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
            Uri uri = Uri.parse(strKennelCoverPhoto);
            holder.imgvwClinicLogo.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwClinicLogo.setImageURI(request.getSourceUri());
        }

        /* SET THE CLINIC NAME */
        holder.txtClinicName.setText(data.getClinicName());

        /* SET THE CLINIC ADDRESS */
        String clinicAddress = data.getClinicAddress();
        String cityName = data.getCityName();
        String clinicPinCode = data.getClinicPinCode();
        holder.txtClinicAddress.setText(activity.getString(R.string.doctor_list_address_placeholder, clinicAddress, cityName, clinicPinCode));

        /* SET THE REVIEW VOTE STATS */
        holder.txtClinicReviews.setText(data.getDoctorVoteStats());

        /* SET THE CLINIC RATINGS */
        if (data.getClinicRating() != null && !data.getClinicRating().equalsIgnoreCase("null")) {
            holder.clinicRating.setRating(Float.parseFloat(data.getClinicRating()));
        } else {
            holder.clinicRating.setRating(0);
        }

        /* SET THE DOCTOR'S CHARGES */
        String currency = data.getCurrencySymbol();
        String charges = data.getDoctorCharges();
        holder.txtClinicCharges.setText(activity.getString(R.string.doctor_details_doc_charges_placeholder, currency, charges));

        /* SET THE CLINIC DISTANCE */
        String strClinicDistance = data.getClinicDistance();
        if (strClinicDistance != null && !strClinicDistance.equals("Unknown"))  {
            String strTilde = activity.getString(R.string.generic_tilde);
            holder.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, strClinicDistance));
        } else {
            String strInfinity = activity.getString(R.string.generic_infinity);
            holder.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, strClinicDistance));
        }

//        /* SET THE KENNEL COVER PHOTO */
//        String strKennelCoverPhoto = data.getKennelCoverPhoto();
//        if (strKennelCoverPhoto != null
//                && !strKennelCoverPhoto.equalsIgnoreCase("")
//                && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
//            Uri uri = Uri.parse(strKennelCoverPhoto);
//            holder.imgvwKennelCoverPhoto.setImageURI(uri);
//        } else {
//            ImageRequest request = ImageRequestBuilder
//                    .newBuilderWithResourceId(R.drawable.empty_graphic)
//                    .build();
//            holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
//        }
//
//        /* SET THE KENNEL NAME */
//        if (data.getKennelName() != null) {
//            holder.txtClinicName.setText(data.getKennelName());
//        }
//
//        /* SET THE KENNEL ADDRESS */
//        String strKennelAddress = data.getKennelAddress();
//        String cityName = data.getCityName();
//        String kennelPinCode = data.getKennelPinCode();
//        holder.txtClinicAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));
//
//        /* SET THE CAPACITY OF LARGE SIZE PETS */
//        if (data.getKennelPetCapacity() != null
//                && !data.getKennelPetCapacity().equalsIgnoreCase("")
//                && !data.getKennelPetCapacity().equalsIgnoreCase("null")) {
//            holder.txtClinicCharges.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
//        } else {
//            holder.txtClinicCharges.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
//        }
    }

    @NonNull
    @Override
    public ClinicsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.sponsored_clinics_item, parent, false);

        return new ClinicsVH(itemView);
    }

    class ClinicsVH extends RecyclerView.ViewHolder	{

        CardView cardClinic;
        SimpleDraweeView imgvwClinicLogo;
        TextView txtClinicName;
        TextView txtClinicAddress;
        TextView txtClinicReviews;
        RatingBar clinicRating;
        TextView txtClinicCharges;
        TextView txtClinicDistance;

        ClinicsVH(View v) {
            super(v);

            cardClinic = v.findViewById(R.id.cardClinic);
            imgvwClinicLogo = v.findViewById(R.id.imgvwClinicLogo);
            txtClinicName = v.findViewById(R.id.txtClinicName);
            txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
            txtClinicReviews = v.findViewById(R.id.txtClinicReviews);
            clinicRating = v.findViewById(R.id.clinicRating);
            txtClinicCharges = v.findViewById(R.id.txtClinicCharges);
            txtClinicDistance = v.findViewById(R.id.txtClinicDistance);
        }
    }
}