package co.zenpets.users.utils.adapters.groomers.reviews;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.groomers.review.GroomerReview;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroomerReviewsAdapter extends RecyclerView.Adapter<GroomerReviewsAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<GroomerReview> arrReviews;

    public GroomerReviewsAdapter(Activity activity, ArrayList<GroomerReview> arrReviews) {

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
    public void onBindViewHolder(@NonNull final ReviewsVH holder, final int position) {
        final GroomerReview data = arrReviews.get(position);

        /* SET THE USER'S DISPLAY PROFILE */
        final String userDisplayProfile = data.getUserDisplayProfile();
        if (userDisplayProfile != null) {
            Picasso.with(activity)
                    .load(userDisplayProfile)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .noFade()
                    .resize(400, 400)
                    .into(holder.imgvwUserDisplayProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(userDisplayProfile)
                                    .noFade()
                                    .error(R.drawable.ic_person_black_24dp)
                                    .into(holder.imgvwUserDisplayProfile, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
//                                            Log.e("Picasso","Could not fetch image");
                                        }
                                    });
                        }
                    });
        }

        /* SET THE USER NAME */
        if (data.getUserName() != null) {
            holder.txtUserName.setText(data.getUserName());
        }

        /* SET THE RECOMMEND STATUS */
        String strRecommendStatus = data.getGroomerRecommendStatus();
        if (strRecommendStatus.equalsIgnoreCase("Yes")) {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_up2");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
        } else if (strRecommendStatus.equalsIgnoreCase("No"))   {
            holder.imgvwLikeStatus.setIcon("faw_thumbs_down2");
            holder.imgvwLikeStatus.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
        }

        /* SET THE REVIEW RATING */
        String kennelRating = data.getGroomerRating();
        if (kennelRating != null && !kennelRating.equalsIgnoreCase("null")) {
            holder.kennelRating.setRating(Float.parseFloat(kennelRating));
        } else {
            holder.kennelRating.setRating(0);
        }

        /* SET THE TIME STAMP */
        Date date = new Date(Long.parseLong(data.getGroomerReviewTimestamp()) * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String timeStamp = sdf.format(date);
        holder.txtTimestamp.setText(timeStamp);

        /* SET THE USER'S KENNEL EXPERIENCE / REVIEW */
        if (data.getGroomerExperience() != null) {
            holder.txtKennelExperience.setText(data.getGroomerExperience());
        }

        /* CHECK IF A REPLY HAS BEEN POSTED */
        String kennelReplyStatus = data.getGroomerReplyStatus();
        if (kennelReplyStatus != null
                && !kennelReplyStatus.equalsIgnoreCase("")
                && !kennelReplyStatus.equalsIgnoreCase("null"))    {
            if (kennelReplyStatus.equalsIgnoreCase("0"))    {
                holder.txtReplyStatus.setVisibility(View.GONE);
                holder.txtReply.setVisibility(View.GONE);
            } else if (kennelReplyStatus.equalsIgnoreCase("1")) {
                String replyText = data.getGroomerReplyText();
                Date dateReply = new Date(Long.parseLong(data.getGroomerReplyPublished()) * 1000L);
                SimpleDateFormat sdfReply = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String replyTimestamp = sdfReply.format(dateReply);
                String strReplyFrom = activity.getString(R.string.review_reply_from_title) + " ";
                SpannableStringBuilder builder = new SpannableStringBuilder(strReplyFrom);
                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, strReplyFrom.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(replyTimestamp);
                holder.txtReplyStatus.setText(builder);
                holder.txtReply.setText(replyText);
                holder.txtReplyStatus.setVisibility(View.VISIBLE);
                holder.txtReply.setVisibility(View.VISIBLE);
            }
        } else {
            holder.txtReplyStatus.setVisibility(View.GONE);
            holder.txtReply.setVisibility(View.GONE);
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

        CircleImageView imgvwUserDisplayProfile;
        TextView txtUserName;
        RatingBar kennelRating;
        TextView txtTimestamp;
        IconicsImageView imgvwLikeStatus;
        TextView txtKennelExperience;
        TextView txtReplyStatus;
        TextView txtReply;

        ReviewsVH(View v) {
            super(v);

            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            kennelRating = v.findViewById(R.id.kennelRating);
            txtTimestamp = v.findViewById(R.id.txtTimestamp);
            imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
            txtKennelExperience = v.findViewById(R.id.txtKennelExperience);
            txtReplyStatus = v.findViewById(R.id.txtReplyStatus);
            txtReply = v.findViewById(R.id.txtReply);
        }
    }
}