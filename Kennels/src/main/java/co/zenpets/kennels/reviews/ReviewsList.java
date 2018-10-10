package co.zenpets.kennels.reviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.kennels.KennelsSpinnerAdapter;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.Kennels;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import co.zenpets.kennels.utils.models.reviews.Review;
import co.zenpets.kennels.utils.models.reviews.Reviews;
import co.zenpets.kennels.utils.models.reviews.ReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsList extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL'S OWNERS ID **/
    private String KENNEL_OWNER_ID = null;

    /** THE SELECTED KENNEL ID **/
    String KENNEL_ID = null;

    /** AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
    ReviewsAdapter reviewsAdapter;
    ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** THE REVIEWS ADAPTER AND ARRAY LISTS **/
    private ArrayList<Review> arrReviews = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnKennels) Spinner spnKennels;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_reviews_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* INSTANTIATE THE KENNEL REVIEWS ADAPTER */
        reviewsAdapter = new ReviewsAdapter(ReviewsList.this, arrReviews);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE LOGGED IN KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();
        if (KENNEL_OWNER_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennels();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
        }

        /* SELECT A KENNEL TO SHOW IT'S REVIEWS */
        spnKennels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED KENNEL ID */
                KENNEL_ID = arrKennels.get(position).getKennelID();

                /* CLEAR THE REVIEWS ARRAY */
                if (arrReviews != null)
                    arrReviews.clear();

                /* SHOW THE PROGRESS AND FETCH THE LIST OF REVIEWS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchKennelReviews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** FETCH THE LIST OF REVIEWS **/
    private void fetchKennelReviews() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchKennelReviews(KENNEL_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
//                Log.e("REVIEWS RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviews = response.body().getReviews();
                    if (arrReviews.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        listReviews.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* INSTANTIATE THE KENNEL REVIEWS ADAPTER */
                        reviewsAdapter = new ReviewsAdapter(ReviewsList.this, arrReviews);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(reviewsAdapter);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listReviews.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listReviews.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {

            }
        });
    }

    /** FETCH THE LIST OF KENNELS **/
    private void fetchKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByOwner(KENNEL_OWNER_ID);
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
                if (response.body() != null && response.body().getKennels() != null) {
                    arrKennels = response.body().getKennels();
                    if (arrKennels.size() > 0) {
                        /* SET THE ADAPTER TO THE KENNELS SPINNER */
                        spnKennels.setAdapter(new KennelsSpinnerAdapter(
                                ReviewsList.this,
                                R.layout.pet_capacity_row,
                                arrKennels));
                    }
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Kennel Reviews";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(this), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        listReviews.setLayoutManager(manager);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(true);

        /* SET THE EDUCATIONS ADAPTER */
        listReviews.setAdapter(reviewsAdapter);
    }
    
    /** THE REVIEWS ADAPTER **/
    private class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsVH> {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        private final Activity activity;

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<Review> list;

        ReviewsAdapter(Activity activity, ArrayList<Review> list) {

            /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
            this.activity = activity;

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.list = list;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final ReviewsAdapter.ReviewsVH holder, @SuppressLint("RecyclerView") final int position) {
            final Review data = list.get(position);

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

            /* CHECK IF A REPLY HAS BEEN POSTED */
            String kennelReplyStatus = data.getKennelReplyStatus();
            if (kennelReplyStatus != null
                    && !kennelReplyStatus.equalsIgnoreCase("")
                    && !kennelReplyStatus.equalsIgnoreCase("null"))    {
                if (kennelReplyStatus.equalsIgnoreCase("0"))    {
                    holder.txtPostReply.setVisibility(View.VISIBLE);
                    holder.txtReplyStatus.setVisibility(View.GONE);
                    holder.txtReply.setVisibility(View.GONE);
                    holder.imgvwEditReply.setVisibility(View.GONE);
                } else if (kennelReplyStatus.equalsIgnoreCase("1")) {
                    String replyText = data.getKennelReplyText();
                    Date dateReply = new Date(Long.parseLong(data.getKennelReplyPublished()) * 1000L);
                    SimpleDateFormat sdfReply = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String replyTimestamp = sdfReply.format(dateReply);
                    String strReplyFrom = activity.getString(R.string.review_reply_from_title) + " ";
                    SpannableStringBuilder builder = new SpannableStringBuilder(strReplyFrom);
                    builder.setSpan(new StyleSpan(Typeface.BOLD), 0, strReplyFrom.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(replyTimestamp);
                    holder.txtReplyStatus.setText(builder);
                    holder.txtReply.setText(replyText);
                    holder.txtPostReply.setVisibility(View.GONE);
                    holder.txtReplyStatus.setVisibility(View.VISIBLE);
                    holder.txtReply.setVisibility(View.VISIBLE);
                    holder.imgvwEditReply.setVisibility(View.VISIBLE);
                }
            } else {
                holder.txtPostReply.setVisibility(View.VISIBLE);
                holder.txtReplyStatus.setVisibility(View.GONE);
                holder.txtReply.setVisibility(View.GONE);
                holder.imgvwEditReply.setVisibility(View.GONE);
            }

            /* POST A REPLY */
            holder.txtPostReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postReply(data.getKennelReviewID(), position);
                }
            });

            /* EDIT A REPLY */
            holder.imgvwEditReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateReply(data.getKennelReviewID(), data.getKennelReplyText(), position);
                }
            });
        }

        /** POST A NEW REPLY ON A REVIEW **/
        private void postReply(final String kennelReviewID, final int position) {
            final String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            new MaterialDialog.Builder(activity)
                    .title("Post Your Reply")
                    .content(null)
                    /*.content("Post a reply on the review posted by the Pet Parent. Please keep the reply civil and decent. It always pays to be thank your patrons...")*/
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    .inputRange(5, 500)
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .positiveText("Post Reply")
                    .negativeText("Cancel")
                    .input("Type your reply...", null, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                            ReviewsAPI apiInterface = ZenApiClient.getClient().create(ReviewsAPI.class);
                            Call<Review> call = apiInterface.postKennelReviewReply(
                                    kennelReviewID, "1",
                                    input.toString(), timeStamp);
                            call.enqueue(new Callback<Review>() {
                                @Override
                                public void onResponse(Call<Review> call, Response<Review> response) {
                                    if (response.isSuccessful())    {
                                        dialog.dismiss();
                                        Toast.makeText(activity, "Successfully posted your reply...", Toast.LENGTH_SHORT).show();
                                        fetchKennelReviews();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(activity, "Reply failed...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Review> call, Throwable t) {
//                                Log.e("FAILURE", t.getMessage());
                                }
                            });
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        /** UPDATE THE KENNEL OWNER'S REPLY **/
        private void updateReply(final String kennelReviewID, String kennelReplyText, final int position) {
            final String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            new MaterialDialog.Builder(activity)
                    .title("Update your reply")
                    .content(null)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    .inputRange(5, 500)
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .positiveText("Update")
                    .negativeText("Cancel")
                    .input("Update your reply...", kennelReplyText, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                            ReviewsAPI apiInterface = ZenApiClient.getClient().create(ReviewsAPI.class);
                            Call<Review> call = apiInterface.updateKennelReviewReply(kennelReviewID, input.toString(), timeStamp);
                            call.enqueue(new Callback<Review>() {
                                @Override
                                public void onResponse(Call<Review> call, Response<Review> response) {
                                    if (response.isSuccessful())    {
                                        dialog.dismiss();
                                        Toast.makeText(activity, "Successfully updated the reply...", Toast.LENGTH_SHORT).show();
                                        fetchKennelReviews();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(activity, "Update failed...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Review> call, Throwable t) {
//                                Log.e("FAILURE", t.getMessage());
                                }
                            });
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        @NonNull
        @Override
        public ReviewsAdapter.ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.dash_reviews_item, parent, false);

            return new ReviewsAdapter.ReviewsVH(itemView);
        }

        class ReviewsVH extends RecyclerView.ViewHolder	{

            SimpleDraweeView imgvwUserDisplayProfile;
            TextView txtUserName;
            RatingBar kennelRating;
            TextView txtTimestamp;
            IconicsImageView imgvwLikeStatus;
            TextView txtKennelExperience;
            TextView txtPostReply;
//            IconicsImageView imgvwPostReply;
            TextView txtReplyStatus;
            TextView txtReply;
            IconicsImageView imgvwEditReply;

            ReviewsVH(View v) {
                super(v);

                imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
                txtUserName = v.findViewById(R.id.txtUserName);
                kennelRating = v.findViewById(R.id.kennelRating);
                txtTimestamp = v.findViewById(R.id.txtTimeStamp);
                imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
                txtKennelExperience = v.findViewById(R.id.txtKennelExperience);
                txtPostReply = v.findViewById(R.id.txtPostReply);
//                imgvwPostReply = v.findViewById(R.id.imgvwPostReply);
                txtReplyStatus = v.findViewById(R.id.txtReplyStatus);
                txtReply = v.findViewById(R.id.txtReply);
                imgvwEditReply = v.findViewById(R.id.imgvwEditReply);
            }
        }
    }
}