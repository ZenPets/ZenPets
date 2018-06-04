package biz.zenpets.users.utils.adapters.kennels.reviews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
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

        /* SET THE VISIT EXPERIENCE */
        if (data.getKennelExperience() != null) {
            holder.txtKennelExperience.setText(data.getKennelExperience());
        }

        /* SET THE USER NAME */
        if (data.getUserName() != null) {
            holder.txtUserName.setText(data.getUserName());
        }

        /* SET THE TIMESTAMP */
        if (data.getKennelReviewTimestamp() != null)  {
            String reviewTimestamp = data.getKennelReviewTimestamp();
            long lngTimeStamp = Long.parseLong(reviewTimestamp) * 1000;
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(lngTimeStamp);
            Date date = calendar.getTime();
            PrettyTime prettyTime = new PrettyTime();
            String strDate = prettyTime.format(date);
            holder.txtTimeStamp.setText(strDate);
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

        TextView txtKennelExperience;
        IconicsImageView imgvwLikeStatus;
        TextView txtUserName;
        TextView txtTimeStamp;

        ReviewsVH(View v) {
            super(v);

            txtKennelExperience = v.findViewById(R.id.txtKennelExperience);
            imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
        }
    }
}