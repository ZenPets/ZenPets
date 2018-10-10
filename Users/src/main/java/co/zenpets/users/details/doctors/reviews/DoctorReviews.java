package co.zenpets.users.details.doctors.reviews;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.reviews.Review;
import co.zenpets.users.utils.models.reviews.Reviews;
import co.zenpets.users.utils.models.reviews.ReviewsAPI;
import co.zenpets.users.utils.models.reviews.votes.ReviewVote;
import co.zenpets.users.utils.models.reviews.votes.ReviewVotes;
import co.zenpets.users.utils.models.reviews.votes.ReviewVotesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorReviews extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** THE LOGGED IN USER'S ID **/
    private String USER_ID = null;

    /** THE REVIEWS ARRAY LIST **/
    private ArrayList<Review> arrReviews = new ArrayList<>();

    /** THE HELPFUL REVIEW VOTES **/
    private String HELPFUL_REVIEW_VOTES = null;
    private String NON_HELPFUL_REVIEW_VOTES = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtFeedback) AppCompatTextView txtFeedback;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listDoctorReviews) RecyclerView listDoctorReviews;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_reviews_list);
        ButterKnife.bind(this);

        /* GET THE LOGGED IN USER'S ID */
        USER_ID = getApp().getUserID();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /* GET THE INCOMING DATA */
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("DOCTOR_PREFIX")
                && bundle.containsKey("DOCTOR_NAME")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            String DOCTOR_PREFIX = bundle.getString("DOCTOR_PREFIX");
            String DOCTOR_NAME = bundle.getString("DOCTOR_NAME");
            if (DOCTOR_ID != null && DOCTOR_PREFIX != null && DOCTOR_NAME != null)  {
                /* SET THE DOCTOR'S NAME */
                txtDoctorName.setText(getString(R.string.review_doctor_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));

                /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S REVIEWS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchDoctorReviews();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /***** FETCH THE DOCTOR'S REVIEWS *****/
    private void fetchDoctorReviews() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchDoctorReviews(DOCTOR_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                /* GET THE REVIEWS */
                arrReviews = response.body().getReviews();

                /* CHECK FOR RESULTS */
                if (arrReviews != null && arrReviews.size() > 0)    {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    listDoctorReviews.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE ADAPTER TO THE RECYCLER VIEW */
                    listDoctorReviews.setAdapter(new AllReviewsAdapter(arrReviews));

                    /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
                    linlaProgress.setVisibility(View.GONE);
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listDoctorReviews.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
//                Log.e("REVIEWS FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctorReviews.setLayoutManager(manager);
        listDoctorReviews.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listDoctorReviews.setAdapter(new AllReviewsAdapter(arrReviews));
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(null);
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

    /***** THE REVIEWS ADAPTER *****/
    private class AllReviewsAdapter extends RecyclerView.Adapter<AllReviewsAdapter.ReviewsVH> {

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<Review> reviews;

        private AllReviewsAdapter(ArrayList<Review> arrReviews) {
            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.reviews = arrReviews;
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final ReviewsVH holder, final int position) {
            final Review data = reviews.get(position);

            /* SET THE RECOMMEND STATUS */
            String strRecommendStatus = data.getRecommendStatus();
            if (strRecommendStatus.equalsIgnoreCase("Yes")) {
                holder.imgvwLikeStatus.setIcon("faw_thumbs_up2");
                holder.imgvwLikeStatus.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_dark));
            } else if (strRecommendStatus.equalsIgnoreCase("No"))   {
                holder.imgvwLikeStatus.setIcon("faw_thumbs_down2");
                holder.imgvwLikeStatus.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            }

            /* SET THE USER'S DISPLAY PROFILE */
            if (data.getUserDisplayProfile() != null)   {
                Uri uri = Uri.parse(data.getUserDisplayProfile());
                holder.imgvwUserProfile.setImageURI(uri);
            } else {
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                        .build();
                holder.imgvwUserProfile.setImageURI(request.getSourceUri());
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

            /* GET THE NUMBER OF HELPFUL VOTES */
            fetchHelpfulVotes(holder.txtHelpfulYes, data.getReviewID());

            /* GET THE NUMBER OF NON HELPFUL VOTES */
            fetchNotHelpfulVotes(holder.txtHelpfulNo, data.getReviewID());

            /* ADD A HELPFUL VOTE */
            holder.txtHelpfulYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReviewVotesAPI api = ZenApiClient.getClient().create(ReviewVotesAPI.class);
                    Call<ReviewVote> call = api.checkUserReviewVote(data.getReviewID(), USER_ID);
                    call.enqueue(new Callback<ReviewVote>() {
                        @Override
                        public void onResponse(Call<ReviewVote> call, Response<ReviewVote> response) {
                            ReviewVote vote = response.body();
                            /* PROCESS THE HELPFUL RESULT */
                            processHelpfulResult(vote, holder.txtHelpfulYes, holder.txtHelpfulNo, data.getReviewID());
                        }

                        @Override
                        public void onFailure(Call<ReviewVote> call, Throwable t) {
                        }
                    });
                }
            });

            /* ADD A NOT HELPFUL VOTE */
            holder.txtHelpfulNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReviewVotesAPI api = ZenApiClient.getClient().create(ReviewVotesAPI.class);
                    Call<ReviewVote> call = api.checkUserReviewVote(data.getReviewID(), USER_ID);
                    call.enqueue(new Callback<ReviewVote>() {
                        @Override
                        public void onResponse(Call<ReviewVote> call, Response<ReviewVote> response) {
                            ReviewVote vote = response.body();
                            /* PROCESS THE NOT NOT HELPFUL RESULT */
                            processNotHelpfulResult(vote, holder.txtHelpfulYes, holder.txtHelpfulNo, data.getReviewID());
                        }

                        @Override
                        public void onFailure(Call<ReviewVote> call, Throwable t) {
//                            Log.e("CHECK VOTE FAILURE", t.getMessage());
                        }
                    });
                }
            });
        }

        @NonNull
        @Override
        public ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.doctor_reviews_item_new, parent, false);

            return new ReviewsVH(itemView);
        }

        class ReviewsVH extends RecyclerView.ViewHolder	{
            final SimpleDraweeView imgvwUserProfile;
            final AppCompatTextView txtUserName;
            final AppCompatTextView txtTimeStamp;
            final IconicsImageView imgvwLikeStatus;
            final AppCompatTextView txtVisitExperience;
            final AppCompatTextView txtVisitReason;
            final AppCompatTextView txtHelpfulYes;
            final AppCompatTextView txtHelpfulNo;
            final IconicsImageView imgvwFlagReply;

            ReviewsVH(View v) {
                super(v);
                imgvwUserProfile = v.findViewById(R.id.imgvwUserProfile);
                txtUserName = v.findViewById(R.id.txtUserName);
                txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
                imgvwLikeStatus = v.findViewById(R.id.imgvwLikeStatus);
                txtVisitExperience = v.findViewById(R.id.txtVisitExperience);
                txtVisitReason = v.findViewById(R.id.txtVisitReason);
                txtHelpfulYes = v.findViewById(R.id.txtHelpfulYes);
                txtHelpfulNo = v.findViewById(R.id.txtHelpfulNo);
                imgvwFlagReply = v.findViewById(R.id.imgvwFlagReply);
            }
        }
    }

    /***** PROCESS THE HELPFUL RESULT *****/
    private void processHelpfulResult(
            ReviewVote vote,
            final AppCompatTextView txtHelpfulYes,
            final AppCompatTextView txtHelpfulNo,
            final String reviewID) {
        if (!vote.getError()) {
            String reviewVoteText = vote.getReviewVoteText();
            String reviewVoteID = vote.getReviewVoteID();
            if (reviewVoteText != null && reviewVoteText.equalsIgnoreCase("Yes"))   {
                Toast.makeText(
                        getApplicationContext(),
                        "You have already marked this Review as helpful",
                        Toast.LENGTH_SHORT).show();
            } else {
                ReviewVotesAPI apiUpdate = ZenApiClient.getClient().create(ReviewVotesAPI.class);
                Call<ReviewVote> callUpdate = apiUpdate.updateReviewVoteYes(
                        reviewVoteID, "Yes");
                callUpdate.enqueue(new Callback<ReviewVote>() {
                    @Override
                    public void onResponse(Call<ReviewVote> call, Response<ReviewVote> response) {
                        if (response.isSuccessful())    {
                            /* GET THE NUMBER OF HELPFUL VOTES */
                            fetchHelpfulVotes(txtHelpfulYes, reviewID);

                            /* GET THE NUMBER OF NOT HELPFUL VOTES */
                            fetchNotHelpfulVotes(txtHelpfulNo, reviewID);
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Update failed...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewVote> call, Throwable t) {
                    }
                });
            }
        } else {
            String reviewVoteTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
            ReviewVotesAPI apiNew = ZenApiClient.getClient().create(ReviewVotesAPI.class);
            Call<ReviewVote> callNew = apiNew.newReviewVote(
                    reviewID, USER_ID, "Yes", reviewVoteTimestamp
            );
            callNew.enqueue(new Callback<ReviewVote>() {
                @Override
                public void onResponse(Call<ReviewVote> call, Response<ReviewVote> response) {
                    if (response.isSuccessful())    {
                        /* GET THE NUMBER OF HELPFUL VOTES */
                        fetchHelpfulVotes(txtHelpfulYes, reviewID);

                        /* GET THE NUMBER OF NOT HELPFUL VOTES */
                        fetchNotHelpfulVotes(txtHelpfulNo, reviewID);
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Vote failed...",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ReviewVote> call, Throwable t) {
                }
            });
        }
    }

    /***** GET AND SET THE NUMBER OF HELPFUL VOTES *****/
    private void fetchHelpfulVotes(final AppCompatTextView txtHelpfulYes, String reviewID) {
        ReviewVotesAPI api = ZenApiClient.getClient().create(ReviewVotesAPI.class);
        Call<ReviewVotes> call = api.fetchPositiveReviewVotes(reviewID, "Yes");
        call.enqueue(new Callback<ReviewVotes>() {
            @Override
            public void onResponse(Call<ReviewVotes> call, Response<ReviewVotes> response) {
                if (response.isSuccessful())    {
                    ArrayList<ReviewVote> arrYes = response.body().getVotes();
                    if (arrYes != null && arrYes.size() > 0)    {
                        HELPFUL_REVIEW_VOTES = String.valueOf(arrYes.size());
                    } else {
                        HELPFUL_REVIEW_VOTES = "0";
                    }
                } else {
                    HELPFUL_REVIEW_VOTES = "0";
                }

                /* SET THE NUMBER OF HELPFUL VOTES */
                if (HELPFUL_REVIEW_VOTES != null)   {
                    txtHelpfulYes.setText(getString(R.string.review_helpful_votes_placeholder, HELPFUL_REVIEW_VOTES));
                }

            }

            @Override
            public void onFailure(Call<ReviewVotes> call, Throwable t) {
            }
        });
    }

    /***** PROCESS THE NOT HELPFUL RESULT *****/
    private void processNotHelpfulResult(
            ReviewVote vote,
            final AppCompatTextView txtHelpfulYes,
            final AppCompatTextView txtHelpfulNo,
            final String reviewID) {
        if (!vote.getError()) {
            String reviewVoteText = vote.getReviewVoteText();
            String reviewVoteID = vote.getReviewVoteID();
            if (reviewVoteText!= null && reviewVoteText.equalsIgnoreCase("No")) {
                Toast.makeText(
                        getApplicationContext(),
                        "You have already voted this reply as not helpful",
                        Toast.LENGTH_SHORT).show();
            } else {
                ReviewVotesAPI apiUpdate = ZenApiClient.getClient().create(ReviewVotesAPI.class);
                Call<ReviewVote> callUpdate = apiUpdate.updateReviewVoteNo(
                        reviewVoteID, "No");
                callUpdate.enqueue(new Callback<ReviewVote>() {
                    @Override
                    public void onResponse(Call<ReviewVote> call, Response<ReviewVote> response) {
                        if (response.isSuccessful())    {
                            /* GET THE NUMBER OF HELPFUL VOTES */
                            fetchHelpfulVotes(txtHelpfulYes, reviewID);

                            /* GET THE NUMBER OF NOT HELPFUL VOTES */
                            fetchNotHelpfulVotes(txtHelpfulNo, reviewID);
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Update failed...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewVote> call, Throwable t) {
                    }
                });
            }
        } else {
            String reviewVoteTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
            ReviewVotesAPI apiNew = ZenApiClient.getClient().create(ReviewVotesAPI.class);
            Call<ReviewVote> callNew = apiNew.newReviewVote(
                    reviewID, USER_ID, "No", reviewVoteTimestamp
            );
            callNew.enqueue(new Callback<ReviewVote>() {
                @Override
                public void onResponse(Call<ReviewVote> call, Response<ReviewVote> response) {
                    if (response.isSuccessful())    {
                        /* GET THE NUMBER OF HELPFUL VOTES */
                        fetchHelpfulVotes(txtHelpfulYes, reviewID);

                        /* GET THE NUMBER OF NOT HELPFUL VOTES */
                        fetchNotHelpfulVotes(txtHelpfulNo, reviewID);
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Vote failed...",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ReviewVote> call, Throwable t) {
                }
            });
        }
    }

    /***** GET THE NUMBER OF NOT HELPFUL VOTES *****/
    private void fetchNotHelpfulVotes(final AppCompatTextView txtHelpfulNo, String reviewID) {
        ReviewVotesAPI api = ZenApiClient.getClient().create(ReviewVotesAPI.class);
        Call<ReviewVotes> call = api.fetchNegativeReviewVotes(reviewID, "No");
        call.enqueue(new Callback<ReviewVotes>() {
            @Override
            public void onResponse(Call<ReviewVotes> call, Response<ReviewVotes> response) {
                if (response.isSuccessful())    {
                    ArrayList<ReviewVote> arrNo = response.body().getVotes();
                    if (arrNo != null && arrNo.size() > 0)  {
                        NON_HELPFUL_REVIEW_VOTES = String.valueOf(arrNo.size());
                    } else {
                        NON_HELPFUL_REVIEW_VOTES = "0";
                    }
                } else {
                    NON_HELPFUL_REVIEW_VOTES = "0";
                }

                /* SET THE NUMBER OF NOT HELPFUL VOTES */
                if (NON_HELPFUL_REVIEW_VOTES != null)   {
                    txtHelpfulNo.setText(getString(R.string.review_not_helpful_votes_placeholder, NON_HELPFUL_REVIEW_VOTES));
                }

            }

            @Override
            public void onFailure(Call<ReviewVotes> call, Throwable t) {
            }
        });
    }
}