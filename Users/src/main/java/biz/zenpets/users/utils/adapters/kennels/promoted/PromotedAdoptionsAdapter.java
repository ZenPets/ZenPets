package biz.zenpets.users.utils.adapters.kennels.promoted;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.kennels.promotion.Promotion;

@SuppressWarnings("ConstantConditions")
public class PromotedAdoptionsAdapter extends RecyclerView.Adapter<PromotedAdoptionsAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Promotion> arrPromoted;

    public PromotedAdoptionsAdapter(Activity activity, ArrayList<Promotion> arrPromoted) {

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
        
        /* SET THE KENNEL COVER PHOTO */
        String strKennelCoverPhoto = data.getKennelCoverPhoto();
        if (strKennelCoverPhoto != null
                && !strKennelCoverPhoto.equalsIgnoreCase("")
                && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
            Uri uri = Uri.parse(strKennelCoverPhoto);
            holder.imgvwKennelCoverPhoto.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
        }

        /* SET THE KENNEL NAME */
        if (data.getKennelName() != null) {
            holder.txtKennelName.setText(data.getKennelName());
        }

        /* SET THE KENNEL ADDRESS */
        String strKennelAddress = data.getKennelAddress();
        String cityName = data.getCityName();
        String kennelPinCode = data.getKennelPinCode();
        holder.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));

        /* SET THE CAPACITY OF LARGE SIZE PETS */
        if (data.getKennelPetCapacity() != null
                && !data.getKennelPetCapacity().equalsIgnoreCase("")
                && !data.getKennelPetCapacity().equalsIgnoreCase("null")) {
            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
        } else {
            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
        }
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.promoted_kennel_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{

        CardView cardKennel;
        SimpleDraweeView imgvwKennelCoverPhoto;
        TextView txtKennelName;
        TextView txtKennelAddress;
        TextView txtPetCapacity;
        TextView txtKennelLikes;
        TextView txtKennelDistance;

        AdoptionsVH(View v) {
            super(v);

            cardKennel = v.findViewById(R.id.cardKennel);
            imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
            txtKennelName = v.findViewById(R.id.txtKennelName);
            txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
            txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
            txtKennelLikes = v.findViewById(R.id.txtKennelLikes);
            txtKennelDistance = v.findViewById(R.id.txtKennelDistance);
        }
    }
}