package biz.zenpets.users.utils.adapters.trainers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReview;

public class TrainerReviewsAdapter extends RecyclerView.Adapter<TrainerReviewsAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<TrainerReview> arrReviews;

    public TrainerReviewsAdapter(Activity activity, ArrayList<TrainerReview> arrReviews) {

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
        TrainerReview data = arrReviews.get(position);

        /* SET THE EXPERIENCE WITH THE TRAINER */
        if (data.getTrainerExperience() != null) {
            holder.txtVisitExperience.setText(data.getTrainerExperience());
        }

        /* SET THE USER NAME */
        if (data.getUserName() != null) {
            holder.txtUserName.setText(data.getUserName());
        }

        /* SET THE TIMESTAMP */
        if (data.getReviewTimestamp() != null)  {
            String reviewTimestamp = data.getReviewTimestamp();
            long lngTimeStamp = Long.parseLong(reviewTimestamp) * 1000;
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(lngTimeStamp);
            Date date = calendar.getTime();
            PrettyTime prettyTime = new PrettyTime();
            String strDate = prettyTime.format(date);
            holder.txtTimeStamp.setText(strDate);
        }

        /* SET THE RECOMMEND STATUS */
        String strRecommendStatus = data.getRecommendStatus();
        if (strRecommendStatus.equalsIgnoreCase("Yes")) {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_up2");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
        } else if (strRecommendStatus.equalsIgnoreCase("No"))   {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_down2");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
        }
    }

    @NonNull
    @Override
    public ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.trainer_reviews_subset_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder	{
        final AppCompatTextView txtUserName;
        final AppCompatTextView txtVisitExperience;
        final IconicsImageView imgvwLikeStatus;
        final AppCompatTextView txtTimeStamp;

        ReviewsVH(View v) {
            super(v);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtVisitExperience = v.findViewById(R.id.txtVisitExperience);
            imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
        }
    }
}