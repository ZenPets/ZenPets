package biz.zenpets.users.utils.adapters.kennels.reviews;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReview;

public class KennelReviewsAdapter extends RecyclerView.Adapter<KennelReviewsAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<KennelReview> arrReviews;

    public KennelReviewsAdapter(Activity activity, ArrayList<KennelReview> arrReviews) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrReviews = arrReviews;
    }

    @Override
    public int getItemCount() {
        return arrReviews.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsVH holder, final int position) {
        KennelReview data = arrReviews.get(position);

        /* SET THE USER'S DISPLAY PROFILE */
        String userDisplayProfile = data.getUserDisplayProfile();
        if (userDisplayProfile != null) {
            Uri uri = Uri.parse(userDisplayProfile);
            holder.imgvwUserDisplayProfile.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                    .build();
            holder.imgvwUserDisplayProfile.setImageURI(request.getSourceUri());
        }

        /* SET THE USER NAME */
        if (data.getUserName() != null) {
            holder.txtUserName.setText(data.getUserName());
        }

        /* SET THE RECOMMEND STATUS */
        String strRecommendStatus = data.getKennelRecommendStatus();
        if (strRecommendStatus.equalsIgnoreCase("Yes")) {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_up2");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
        } else if (strRecommendStatus.equalsIgnoreCase("No"))   {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_down2");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
        }

        /* SET THE REVIEW RATING */
        String kennelRating = data.getKennelRating();
        if (kennelRating != null && !kennelRating.equalsIgnoreCase("null")) {
            holder.kennelRating.setRating(Float.parseFloat(kennelRating));
        } else {
            holder.kennelRating.setRating(0);
        }

        /* SET THE TIME STAMP */
        Date date = new Date(Long.parseLong(data.getKennelReviewTimestamp()) * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String timeStamp = sdf.format(date);
        holder.txtTimestamp.setText(timeStamp);

        /* SET THE USER'S KENNEL EXPERIENCE / REVIEW */
        if (data.getKennelExperience() != null) {
            holder.txtKennelExperience.setText(data.getKennelExperience());
        }
    }

    @NonNull
    @Override
    public ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennel_review_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder	{

        SimpleDraweeView imgvwUserDisplayProfile;
        TextView txtUserName;
        RatingBar kennelRating;
        TextView txtTimestamp;
        IconicsImageView imgvwLikeStatus;
        TextView txtKennelExperience;

        ReviewsVH(View v) {
            super(v);

            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            kennelRating = v.findViewById(R.id.kennelRating);
            txtTimestamp = v.findViewById(R.id.txtTimestamp);
            imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
            txtKennelExperience = v.findViewById(R.id.txtKennelExperience);
        }
    }
}