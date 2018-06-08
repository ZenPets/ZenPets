package biz.zenpets.kennels.utils.adapters.reviews;

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

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.models.reviews.Review;

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
    public void onBindViewHolder(@NonNull final ReviewsVH holder, final int position) {
        Review data = arrReviews.get(position);

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

//        /* CHECK IF A REPLY HAS BEEN POSTED */
//        String kennelReplyStatus = data.getKennelReplyStatus();
//        if (kennelReplyStatus != null
//                && !kennelReplyStatus.equalsIgnoreCase("")
//                && !kennelReplyStatus.equalsIgnoreCase("null"))    {
//            if (kennelReplyStatus.equalsIgnoreCase("0"))    {
//                holder.txtReplyStatus.setVisibility(View.GONE);
//                holder.txtReply.setVisibility(View.GONE);
//            } else if (kennelReplyStatus.equalsIgnoreCase("1")) {
//                String kennelOwnerName = data.getKennelOwnerName();
//                String replyText = data.getKennelReplyText();
//                Date dateReply = new Date(Long.parseLong(data.getKennelReplyTimestamp()) * 1000L);
//                SimpleDateFormat sdfReply = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                String replyTimestamp = sdfReply.format(dateReply);
//                String strReplyFrom = activity.getString(R.string.review_reply_from_title) + " " + kennelOwnerName;
//                SpannableStringBuilder builder = new SpannableStringBuilder(strReplyFrom);
//                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, strReplyFrom.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                String strTimestamp = " on " + replyTimestamp;
//                builder.append(strTimestamp);
//                holder.txtReplyStatus.setText(builder);
//                holder.txtReply.setText(replyText);
//                holder.txtReplyStatus.setVisibility(View.VISIBLE);
//                holder.txtReply.setVisibility(View.VISIBLE);
//            }
//        } else {
//            holder.txtReplyStatus.setVisibility(View.GONE);
//            holder.txtReply.setVisibility(View.GONE);
//        }

//        /* SHOW THE KENNEL OPTIONS POPUP MENU */
//        holder.imgvwReviewOptions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu pm = new PopupMenu(activity, holder.imgvwReviewOptions);
//                pm.getMenuInflater().inflate(R.menu.pm_review_options, pm.getMenu());
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId())   {
//                            case R.id.menuUnhelpful:
//                                break;
//                            case R.id.menuInappropriate:
//                                break;
//                            case R.id.menuSpam:
//                                break;
//                            case R.id.menuAbusive:
//                                break;
//                            case R.id.menuViolent:
//                                break;
//                            default:
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                pm.show();
//            }
//        });
    }

    @NonNull
    @Override
    public ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.dash_reviews_item, parent, false);

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
//            txtReplyStatus = v.findViewById(R.id.txtReplyStatus);
//            txtReply = v.findViewById(R.id.txtReply);
//            imgvwReviewOptions = v.findViewById(R.id.imgvwReviewOptions);
        }
    }
}