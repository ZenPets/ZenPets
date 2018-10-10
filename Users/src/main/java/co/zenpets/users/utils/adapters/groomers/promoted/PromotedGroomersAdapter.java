package co.zenpets.users.utils.adapters.groomers.promoted;

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
import co.zenpets.users.utils.models.groomers.promotion.Promotion;

@SuppressWarnings("ConstantConditions")
public class PromotedGroomersAdapter extends RecyclerView.Adapter<PromotedGroomersAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Promotion> arrPromoted;

    public PromotedGroomersAdapter(Activity activity, ArrayList<Promotion> arrPromoted) {

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
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Promotion data = arrPromoted.get(position);
        /* SET THE GROOMER'S LOGO */
        String groomerLogo = data.getGroomerLogo();
        if (groomerLogo != null
                && !groomerLogo.equalsIgnoreCase("")
                && !groomerLogo.equalsIgnoreCase("null")) {
            Uri uri = Uri.parse(groomerLogo);
            holder.imgvwGroomerLogo.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwGroomerLogo.setImageURI(request.getSourceUri());
        }

        /* SET THE GROOMER'S NAME */
        if (data.getGroomerName() != null) {
            holder.txtGroomerName.setText(data.getGroomerName());
        }

        /* SET THE GROOMER'S ADDRESS */
        String groomerAddress = data.getGroomerAddress();
        String cityName = data.getCityName();
        String groomerPincode = data.getGroomerPincode();
        holder.txtGroomerAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, groomerAddress, cityName, groomerPincode));

        /* SET THE GROOMER'S DISTANCE */
        String groomerDistance = data.getGroomerDistance();
        String strTilde = activity.getString(R.string.generic_tilde);
        holder.txtGroomerDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, groomerDistance));

        /* SET THE GROOMER'S REVIEW VOTE STATS */
        holder.txtGroomerLikes.setText(data.getGroomerVoteStats());

        /* SET THE AVERAGE GROOMER'S AVERAGE RATING */
        Float dblRating = Float.valueOf(data.getGroomerRating());
        holder.groomerRating.setRating(dblRating);
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.promoted_groomer_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{

        CardView cardGroomer;
        SimpleDraweeView imgvwGroomerLogo;
        TextView txtGroomerName;
        TextView txtGroomerAddress;
        TextView txtGroomerLikes;
        RatingBar groomerRating;
        TextView txtGroomerDistance;

        AdoptionsVH(View v) {
            super(v);

            cardGroomer = v.findViewById(R.id.cardGroomer);
            imgvwGroomerLogo = v.findViewById(R.id.imgvwGroomerLogo);
            txtGroomerName = v.findViewById(R.id.txtGroomerName);
            txtGroomerAddress = v.findViewById(R.id.txtGroomerAddress);
            txtGroomerLikes = v.findViewById(R.id.txtGroomerLikes);
            groomerRating = v.findViewById(R.id.groomerRating);
            txtGroomerDistance = v.findViewById(R.id.txtGroomerDistance);
        }
    }
}