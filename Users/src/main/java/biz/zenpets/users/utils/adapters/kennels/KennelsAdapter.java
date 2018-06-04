package biz.zenpets.users.utils.adapters.kennels;

import android.app.Activity;
import android.content.Intent;
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
import biz.zenpets.users.details.kennels.KennelDetails;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;

public class KennelsAdapter extends RecyclerView.Adapter<KennelsAdapter.KennelsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Kennel> arrKennels;

    public KennelsAdapter(Activity activity, ArrayList<Kennel> arrKennels) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrKennels = arrKennels;
    }

    @Override
    public int getItemCount() {
        return arrKennels.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final KennelsVH holder, final int position) {
        final Kennel data = arrKennels.get(position);

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

        /* SET THE KENNEL COVER PHOTO */
        String strKennelCoverPhoto = data.getKennelCoverPhoto();
        if (strKennelCoverPhoto != null) {
            Uri uri = Uri.parse(strKennelCoverPhoto);
            holder.imgvwKennelCoverPhoto.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
        }

        /* SET THE KENNEL NAME */
        if (data.getKennelName() != null)   {
            holder.txtKennelName.setText(data.getKennelName());
        }

        /* SET THE KENNEL ADDRESS */
        String strKennelAddress = data.getKennelAddress();
        String cityName = data.getCityName();
        String kennelPinCode = data.getKennelPinCode();
        holder.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));

        /* SET THE CAPACITY OF LARGE SIZE PETS */
        if (data.getKennelPetCapacity() != null)   {
            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
        } else {
            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
        }

        /* SET THE KENNEL'S DISTANCE FROM THE USER'S LOCATION */
        String kennelDistance = data.getKennelDistance();
        holder.txtKennelDistance.setText(kennelDistance);

        /* SET THE LIKES PERCENTAGE AND NUMBER OF VOTES */
        String strVotes = data.getKennelVotes();
        String strLikesPercentage = data.getKennelLikesPercent();
        String open = activity.getString(R.string.doctor_list_votes_open);
        String close = activity.getString(R.string.doctor_list_votes_close);
        holder.txtKennelLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));

        /* SHOW THE KENNEL DETAILS */
        holder.cardKennel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, KennelDetails.class);
                intent.putExtra("KENNEL_ID", data.getKennelID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public KennelsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennels_item, parent, false);

        return new KennelsVH(itemView);
    }

    class KennelsVH extends RecyclerView.ViewHolder	{
        CardView cardKennel;
        SimpleDraweeView imgvwKennelCoverPhoto;
        TextView txtKennelName;
        TextView txtKennelAddress;
        TextView txtPetCapacity;
        TextView txtKennelLikes;
        TextView txtKennelDistance;

        KennelsVH(View v) {
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