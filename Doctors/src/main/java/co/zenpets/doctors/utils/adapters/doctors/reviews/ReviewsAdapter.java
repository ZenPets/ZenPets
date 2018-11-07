package co.zenpets.doctors.utils.adapters.doctors.reviews;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.models.doctors.reviews.ReviewData;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<ReviewData> arrReviews;

    public ReviewsAdapter(Activity activity, ArrayList<ReviewData> arrReviews) {

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
    public void onBindViewHolder(ReviewsVH holder, final int position) {
        ReviewData data = arrReviews.get(position);

        /* SET THE RECOMMEND STATUS */
        String strRecommendStatus = data.getRecommendStatus();
        if (strRecommendStatus.equalsIgnoreCase("Yes")) {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_up");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
        } else if (strRecommendStatus.equalsIgnoreCase("No"))   {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_down");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
        }

        /* SET THE USER'S DISPLAY PROFILE */
        if (data.getUserDisplayProfile() != null)   {
            Uri uri = Uri.parse(data.getUserDisplayProfile());
            holder.imgvwUserProfile.setImageURI(uri);
        }

        /* SET THE VISIT REASON */
        if (data.getVisitReason() != null)  {
            holder.txtVisitReason.setText("Visited for " + data.getVisitReason());
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
    }

    @Override
    public ReviewsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctors_details_reviews_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder	{
        final SimpleDraweeView imgvwUserProfile;
        final AppCompatTextView txtUserName;
        final AppCompatTextView txtTimeStamp;
        final IconicsImageView imgvwLikeStatus;
        final AppCompatTextView txtVisitExperience;
        final AppCompatTextView txtVisitReason;

        ReviewsVH(View v) {
            super(v);
            imgvwUserProfile = v.findViewById(R.id.imgvwUserProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
            imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
            txtVisitExperience = v.findViewById(R.id.txtVisitExperience);
            txtVisitReason = v.findViewById(R.id.txtVisitReason);
        }
    }
}