package biz.zenpets.users.details.trainers.reviews;

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
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReview;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReviews;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReviewsAPI;
import biz.zenpets.users.utils.models.trainers.reviews.votes.TrainerReviewVote;
import biz.zenpets.users.utils.models.trainers.reviews.votes.TrainerReviewVotes;
import biz.zenpets.users.utils.models.trainers.reviews.votes.TrainerReviewVotesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainerReviewsActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /* THE INCOMING TRAINER ID  AND NAME */
    private String TRAINER_ID = null;
    private String TRAINER_NAME = null;

    /* THE LOGGED IN USER'S ID */
    private String USER_ID = null;

    /** THE TRAINER REVIEWS ARRAY LIST **/
    private ArrayList<TrainerReview> arrReviews = new ArrayList<>();

    /** THE HELPFUL REVIEW VOTES **/
    private String HELPFUL_REVIEW_VOTES = null;
    private String NON_HELPFUL_REVIEW_VOTES = null;
    
    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtTrainerName) TextView txtTrainerName;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listTrainerReviews) RecyclerView listTrainerReviews;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_reviews_list);
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

    /** FETCH THE TRAINER'S REVIEWS **/
    private void fetchTrainerReviews() {
        TrainerReviewsAPI api = ZenApiClient.getClient().create(TrainerReviewsAPI.class);
        Call<TrainerReviews> call = api.fetchTrainerReviews(TRAINER_ID);
        call.enqueue(new Callback<TrainerReviews>() {
            @Override
            public void onResponse(Call<TrainerReviews> call, Response<TrainerReviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviews = response.body().getReviews();
                    if (arrReviews.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        linlaEmpty.setVisibility(View.GONE);
                        listTrainerReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listTrainerReviews.setAdapter(new AllReviewsAdapter(arrReviews));
                    } else {
                        /* SHOW THE NO REVIEWS LAYOUT */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listTrainerReviews.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO REVIEWS LAYOUT */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listTrainerReviews.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<TrainerReviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /* GET THE INCOMING DATA */
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("TRAINER_ID")
                && bundle.containsKey("TRAINER_NAME")) {
            TRAINER_ID = bundle.getString("TRAINER_ID");
            TRAINER_NAME = bundle.getString("TRAINER_NAME");
            if (TRAINER_ID != null && TRAINER_NAME != null)  {
                /* SET THE TRAINER'S NAME */
                txtTrainerName.setText(TRAINER_NAME);

                /* SHOW THE PROGRESS AND FETCH THE TRAINER'S REVIEWS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchTrainerReviews();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listTrainerReviews.setLayoutManager(manager);
        listTrainerReviews.setHasFixedSize(true);

        /* SET THE ADAPTER */
//        listTrainerReviews.setAdapter(new AllReviewsAdapter(arrReviews));
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
        private final ArrayList<TrainerReview> reviews;

        private AllReviewsAdapter(ArrayList<TrainerReview> arrReviews) {
            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.reviews = arrReviews;
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final AllReviewsAdapter.ReviewsVH holder, final int position) {
            final TrainerReview data = reviews.get(position);

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

            /* SET THE VISIT EXPERIENCE */
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

            /* GET THE NUMBER OF HELPFUL VOTES */
            fetchHelpfulVotes(holder.txtHelpfulYes, data.getTrainerReviewID());

            /* GET THE NUMBER OF NON HELPFUL VOTES */
            fetchNotHelpfulVotes(holder.txtHelpfulNo, data.getTrainerReviewID());

            /* ADD A HELPFUL VOTE */
            holder.txtHelpfulYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrainerReviewVotesAPI api = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
                    Call<TrainerReviewVote> call = api.checkUserTrainerReviewVote(data.getTrainerReviewID(), USER_ID);
                    call.enqueue(new Callback<TrainerReviewVote>() {
                        @Override
                        public void onResponse(Call<TrainerReviewVote> call, Response<TrainerReviewVote> response) {
                            TrainerReviewVote vote = response.body();
                            /* PROCESS THE HELPFUL RESULT */
                            processHelpfulResult(vote, holder.txtHelpfulYes, holder.txtHelpfulNo, data.getTrainerReviewID());
                        }

                        @Override
                        public void onFailure(Call<TrainerReviewVote> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }
            });

            /* ADD A NOT HELPFUL VOTE */
            holder.txtHelpfulNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrainerReviewVotesAPI api = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
                    Call<TrainerReviewVote> call = api.checkUserTrainerReviewVote(data.getTrainerReviewID(), USER_ID);
                    call.enqueue(new Callback<TrainerReviewVote>() {
                        @Override
                        public void onResponse(Call<TrainerReviewVote> call, Response<TrainerReviewVote> response) {
                            TrainerReviewVote vote = response.body();
                            /* PROCESS THE NOT NOT HELPFUL RESULT */
                            processNotHelpfulResult(vote, holder.txtHelpfulYes, holder.txtHelpfulNo, data.getTrainerReviewID());
                        }

                        @Override
                        public void onFailure(Call<TrainerReviewVote> call, Throwable t) {
//                            Log.e("CHECK VOTE FAILURE", t.getMessage());
                            Crashlytics.logException(t);
                        }
                    });
                }
            });
        }

        @NonNull
        @Override
        public AllReviewsAdapter.ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.doctor_reviews_item_new, parent, false);

            return new AllReviewsAdapter.ReviewsVH(itemView);
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
            TrainerReviewVote vote,
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
                TrainerReviewVotesAPI apiUpdate = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
                Call<TrainerReviewVote> callUpdate = apiUpdate.updateTrainerReviewVoteYes(
                        reviewVoteID, "Yes");
                callUpdate.enqueue(new Callback<TrainerReviewVote>() {
                    @Override
                    public void onResponse(Call<TrainerReviewVote> call, Response<TrainerReviewVote> response) {
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
                    public void onFailure(Call<TrainerReviewVote> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });
            }
        } else {
            String reviewVoteTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
            TrainerReviewVotesAPI apiNew = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
            Call<TrainerReviewVote> callNew = apiNew.newTrainerReviewVote(
                    reviewID, USER_ID, "Yes", reviewVoteTimestamp
            );
            callNew.enqueue(new Callback<TrainerReviewVote>() {
                @Override
                public void onResponse(Call<TrainerReviewVote> call, Response<TrainerReviewVote> response) {
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
                public void onFailure(Call<TrainerReviewVote> call, Throwable t) {
                    Crashlytics.logException(t);
                }
            });
        }
    }

    /***** GET AND SET THE NUMBER OF HELPFUL VOTES *****/
    private void fetchHelpfulVotes(final AppCompatTextView txtHelpfulYes, String reviewID) {
        TrainerReviewVotesAPI api = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
        Call<TrainerReviewVotes> call = api.fetchPositiveTrainerReviewVotes(reviewID, "Yes");
        call.enqueue(new Callback<TrainerReviewVotes>() {
            @Override
            public void onResponse(Call<TrainerReviewVotes> call, Response<TrainerReviewVotes> response) {
                if (response.isSuccessful())    {
                    ArrayList<TrainerReviewVote> arrYes = response.body().getVotes();
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
            public void onFailure(Call<TrainerReviewVotes> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** PROCESS THE NOT HELPFUL RESULT *****/
    private void processNotHelpfulResult(
            TrainerReviewVote vote,
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
                TrainerReviewVotesAPI apiUpdate = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
                Call<TrainerReviewVote> callUpdate = apiUpdate.updateTrainerReviewVoteNo(
                        reviewVoteID, "No");
                callUpdate.enqueue(new Callback<TrainerReviewVote>() {
                    @Override
                    public void onResponse(Call<TrainerReviewVote> call, Response<TrainerReviewVote> response) {
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
                    public void onFailure(Call<TrainerReviewVote> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });
            }
        } else {
            String reviewVoteTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
            TrainerReviewVotesAPI apiNew = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
            Call<TrainerReviewVote> callNew = apiNew.newTrainerReviewVote(
                    reviewID, USER_ID, "No", reviewVoteTimestamp
            );
            callNew.enqueue(new Callback<TrainerReviewVote>() {
                @Override
                public void onResponse(Call<TrainerReviewVote> call, Response<TrainerReviewVote> response) {
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
                public void onFailure(Call<TrainerReviewVote> call, Throwable t) {
                    Crashlytics.logException(t);
                }
            });
        }
    }

    /***** GET THE NUMBER OF NOT HELPFUL VOTES *****/
    private void fetchNotHelpfulVotes(final AppCompatTextView txtHelpfulNo, String reviewID) {
        TrainerReviewVotesAPI api = ZenApiClient.getClient().create(TrainerReviewVotesAPI.class);
        Call<TrainerReviewVotes> call = api.fetchNegativeTrainerReviewVotes(reviewID, "No");
        call.enqueue(new Callback<TrainerReviewVotes>() {
            @Override
            public void onResponse(Call<TrainerReviewVotes> call, Response<TrainerReviewVotes> response) {
                if (response.isSuccessful())    {
                    ArrayList<TrainerReviewVote> arrNo = response.body().getVotes();
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
            public void onFailure(Call<TrainerReviewVotes> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }
}