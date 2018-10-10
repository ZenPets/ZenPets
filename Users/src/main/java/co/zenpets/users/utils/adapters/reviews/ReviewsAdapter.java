package co.zenpets.users.utils.adapters.reviews;

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

import co.zenpets.users.R;
import co.zenpets.users.utils.models.reviews.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Review> arrReviews;

    public ReviewsAdapter(Activity activity, ArrayList<Review> arrReviews) {

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
        Review data = arrReviews.get(position);

        /* SET THE VISIT REASON */
        if (data.getVisitReason() != null)  {
            holder.txtVisitReason.setText(data.getVisitReason());
        }

        /* SET THE VISIT EXPERIENCE */
        if (data.getDoctorExperience() != null) {
            holder.txtVisitExperience.setText(data.getDoctorExperience());
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
                inflate(R.layout.doctors_details_reviews_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder	{
        final AppCompatTextView txtVisitReason;
        final AppCompatTextView txtVisitExperience;
        final IconicsImageView imgvwLikeStatus;
        final AppCompatTextView txtUserName;
        final AppCompatTextView txtTimeStamp;

        ReviewsVH(View v) {
            super(v);
            txtVisitReason = v.findViewById(R.id.txtVisitReason);
            txtVisitExperience = v.findViewById(R.id.txtVisitExperience);
            imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
        }
    }
}