package biz.zenpets.users.details.questions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mikepenz.iconics.view.IconicsImageView;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.details.doctors.DoctorProfileActivity;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.consultations.bookmarks.Bookmark;
import biz.zenpets.users.utils.models.consultations.bookmarks.BookmarksAPI;
import biz.zenpets.users.utils.models.consultations.consultations.Consultation;
import biz.zenpets.users.utils.models.consultations.consultations.ConsultationsAPI;
import biz.zenpets.users.utils.models.consultations.replies.ConsultationReplies;
import biz.zenpets.users.utils.models.consultations.replies.ConsultationRepliesAPI;
import biz.zenpets.users.utils.models.consultations.replies.ConsultationReply;
import biz.zenpets.users.utils.models.consultations.views.ConsultationView;
import biz.zenpets.users.utils.models.consultations.views.ConsultationViews;
import biz.zenpets.users.utils.models.consultations.views.ConsultationViewsAPI;
import biz.zenpets.users.utils.models.consultations.votes.Vote;
import biz.zenpets.users.utils.models.consultations.votes.Votes;
import biz.zenpets.users.utils.models.consultations.votes.VotesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING CONSULTATION ID **/
    private static String CONSULTATION_ID = null;

    /** THE USER ID **/
    private static String USER_ID = null;

    /** THE REPLIES ADAPTER AND ARRAY LIST **/
    private RepliesAdapter repliesAdapter;
    private ArrayList<ConsultationReply> arrReplies = new ArrayList<>();

    /** THE BOOKMARK STATUS FLAG **/
    private boolean blnBookmarkStatus = false;

    /** THE HELPFUL VOTES **/
    private String HELPFUL_VOTES = null;
    private String NON_HELPFUL_VOTES = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.txtConsultationTitle)AppCompatTextView txtConsultationTitle;
    @BindView(R.id.txtConsultationDescription) AppCompatTextView txtConsultationDescription;
    @BindView(R.id.txtConsultationFor) AppCompatTextView txtConsultationFor;
    @BindView(R.id.imgvwHasAttachment) IconicsImageView imgvwHasAttachment;
    @BindView(R.id.txtConsultationTimestamp) AppCompatTextView txtConsultationTimestamp;
    @BindView(R.id.txtConsultationViews) AppCompatTextView txtConsultationViews;
    @BindView(R.id.txtConsultationReplies) AppCompatTextView txtConsultationReplies;
    @BindView(R.id.linlaAttachment) LinearLayout linlaAttachment;
    @BindView(R.id.imgvwConsultationPicture) SimpleDraweeView imgvwConsultationPicture;
    @BindView(R.id.linlaRepliesProgress) LinearLayout linlaRepliesProgress;
    @BindView(R.id.listAnswers) RecyclerView listAnswers;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_details);
        ButterKnife.bind(this);

        /* INSTANTIATE THE ADAPTER */
        repliesAdapter = new RepliesAdapter(QuestionDetails.this, arrReplies);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE USER ID**/
        USER_ID = getApp().getUserID();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* FETCH THE BOOKMARK STATUS */
        fetchBookmarkStatus();
    }

    /***** FETCH THE CONSULTATION DETAILS *****/
    private void fetchConsultationDetails() {
        ConsultationsAPI api = ZenApiClient.getClient().create(ConsultationsAPI.class);
        Call<Consultation> call = api.consultationDetails(CONSULTATION_ID);
        call.enqueue(new Callback<Consultation>() {
            @Override
            public void onResponse(Call<Consultation> call, Response<Consultation> response) {
                Consultation data = response.body();
                if (data != null)   {
                    /* GET AND SET THE CONSULTATION TITLE */
                    String CONSULTATION_TITLE = data.getConsultationTitle();
                    if (CONSULTATION_TITLE != null) {
                        txtConsultationTitle.setText(CONSULTATION_TITLE);
                    }

                    /* GET AND SET THE CONSULTATION DESCRIPTION */
                    String CONSULTATION_DESCRIPTION = data.getConsultationDescription();
                    if (CONSULTATION_DESCRIPTION != null)   {
                        txtConsultationDescription.setText(CONSULTATION_DESCRIPTION);
                    }

                    /* GET AND SET THE CONSULTATION PICTURE */
                    String CONSULTATION_PICTURE = data.getConsultationPicture();
                    if (CONSULTATION_PICTURE != null && !CONSULTATION_PICTURE.equalsIgnoreCase("null"))   {
                        Uri uri = Uri.parse(CONSULTATION_PICTURE);
                        imgvwConsultationPicture.setImageURI(uri);
                        linlaAttachment.setVisibility(View.VISIBLE);
                        imgvwHasAttachment.setVisibility(View.VISIBLE);
                    } else {
                        linlaAttachment.setVisibility(View.GONE);
                        imgvwHasAttachment.setVisibility(View.GONE);
                    }

                    /* GET AND SET THE CONSULTATION TIME STAMP */
                    String CONSULTATION_TIMESTAMP = data.getConsultationTimestamp();
                    long lngTimeStamp = Long.parseLong(CONSULTATION_TIMESTAMP) * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    Date date = calendar.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    CONSULTATION_TIMESTAMP = prettyTime.format(date);
                    if (CONSULTATION_TIMESTAMP != null) {
                        txtConsultationTimestamp.setText(CONSULTATION_TIMESTAMP);
                    }

                    /* GET AND SET THE "CONSULTATION FOR" TEXT */
                    String PET_GENDER = data.getPetGender();
                    String BREED_NAME = data.getBreedName();
                    String petDOB = data.getPetDOB();
                    String PET_AGE;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date dtDOB = format.parse(petDOB);
                        Calendar calDOB = Calendar.getInstance();
                        calDOB.setTime(dtDOB);
                        int dobYear = calDOB.get(Calendar.YEAR);
                        int dobMonth = calDOB.get(Calendar.MONTH) + 1;
                        int dobDate = calDOB.get(Calendar.DATE);

                        /* CALCULATE THE PET'S AGE **/
                        Period petAge = getPetAge(dobYear, dobMonth, dobDate);
                        PET_AGE = petAge.getYears() + " year old";
                        txtConsultationFor.setText(getString(R.string.question_details_asked_for_placeholder, PET_AGE, PET_GENDER, BREED_NAME));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /* SET THE NUMBER OF VIEWS */
                    fetchConsultationViews();

                    /* SET THE NUMBER OF REPLIES */
                    ConsultationRepliesAPI apiReplies = ZenApiClient.getClient().create(ConsultationRepliesAPI.class);
                    Call<ConsultationReplies> callReplies = apiReplies.fetchConsultationReplies(data.getConsultationID());
                    callReplies.enqueue(new Callback<ConsultationReplies>() {
                        @Override
                        public void onResponse(Call<ConsultationReplies> call, Response<ConsultationReplies> response) {
                            ArrayList<ConsultationReply> list = response.body().getReplies();
                            if (list != null && list.size() > 0)    {
                                String replies = String.valueOf(list.size());
                                txtConsultationReplies.setText(getString(R.string.question_details_replies_placeholder, replies));
                            } else {
                                txtConsultationReplies.setText(getString(R.string.question_details_replies_placeholder, "0"));
                            }

                        }

                        @Override
                        public void onFailure(Call<ConsultationReplies> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }

                /* HIDE THE PRIMARY PROGRESS BAR */
                linlaProgress.setVisibility(View.GONE);

                /* SHOW THE REPLIES PROGRESS AND FETCH THE CONSULTATION REPLIES */
                linlaRepliesProgress.setVisibility(View.VISIBLE);
                fetchReplies();
            }

            @Override
            public void onFailure(Call<Consultation> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CALCULATE HE PET'S AGE *****/
    private Period getPetAge(int year, int month, int date) {
        LocalDate dob = new LocalDate(year, month, date);
        LocalDate now = new LocalDate();
        return Period.fieldDifference(dob, now);
    }

    /***** FETCH THE CONSULTATION REPLIES *****/
    private void fetchReplies() {
        ConsultationRepliesAPI api = ZenApiClient.getClient().create(ConsultationRepliesAPI.class);
        Call<ConsultationReplies> call = api.fetchConsultationReplies(CONSULTATION_ID);
        call.enqueue(new Callback<ConsultationReplies>() {
            @Override
            public void onResponse(Call<ConsultationReplies> call, Response<ConsultationReplies> response) {
                if (response.body() != null && response.body().getReplies() != null)    {
                    arrReplies = response.body().getReplies();

                    /* CHECK IF RESULTS WERE RETURNED */
                    if (arrReplies != null && arrReplies.size() > 0)  {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY VIEW */
                        listAnswers.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE ADAPTER */
                        listAnswers.setAdapter(new RepliesAdapter(QuestionDetails.this, arrReplies));
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listAnswers.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listAnswers.setVisibility(View.GONE);
                }

                /* HIDE THE REPLIES PROGRESS AFTER FETCHING THE DATA */
                linlaRepliesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ConsultationReplies> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CONSULTATION_ID"))  {
            CONSULTATION_ID = bundle.getString("CONSULTATION_ID");
            if (CONSULTATION_ID != null)    {
                /* SHOW THE PRIMARY PROGRESS AND FETCH THE CONSULTATION DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchConsultationDetails();

                /* CHECK IF THE USER HAS VIEWED THIS CONSULTATION */
                fetchConsultationViewStatus();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info..", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info..", Toast.LENGTH_SHORT).show();
        }
    }

    /***** CHECK IF THE USER HAS VIEWED THIS CONSULTATION *****/
    private void fetchConsultationViewStatus() {
        ConsultationViewsAPI api = ZenApiClient.getClient().create(ConsultationViewsAPI.class);
        Call<ConsultationView> call = api.getConsultationViewStatus(USER_ID, CONSULTATION_ID);
        call.enqueue(new Callback<ConsultationView>() {
            @Override
            public void onResponse(Call<ConsultationView> call, Response<ConsultationView> response) {
                ConsultationView view = response.body();
                if (view != null)   {
                    String consultationViewID = view.getConsultationViewID();
                    if (consultationViewID == null) {
                        /* PUBLISH A CONSULTATION VIEW */
                        publishConsultationView();
                    }
                } else {
                    /* PUBLISH A CONSULTATION VIEW */
                    publishConsultationView();
                }
            }

            @Override
            public void onFailure(Call<ConsultationView> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** PUBLISH A CONSULTATION VIEW *****/
    private void publishConsultationView() {
        ConsultationViewsAPI api = ZenApiClient.getClient().create(ConsultationViewsAPI.class);
        Call<ConsultationView> call = api.newConsultationView(USER_ID, CONSULTATION_ID);
        call.enqueue(new Callback<ConsultationView>() {
            @Override
            public void onResponse(Call<ConsultationView> call, Response<ConsultationView> response) {
                if (response.isSuccessful())    {
                    /* REFRESH THE NUMBER OF VIEWS */
                    fetchConsultationViews();
                }
            }

            @Override
            public void onFailure(Call<ConsultationView> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** REFRESH THE NUMBER OF VIEWS *****/
    private void fetchConsultationViews() {
        ConsultationViewsAPI api = ZenApiClient.getClient().create(ConsultationViewsAPI.class);
        Call<ConsultationViews> call = api.fetchConsultationViews(CONSULTATION_ID);
        call.enqueue(new Callback<ConsultationViews>() {
            @Override
            public void onResponse(Call<ConsultationViews> call, Response<ConsultationViews> response) {
                ArrayList<ConsultationView> list = response.body().getViews();
                if (list != null && list.size() > 0)    {
                    String views = String.valueOf(list.size());
                    txtConsultationViews.setText(getString(R.string.question_details_views_placeholder, views));
                } else {
                    txtConsultationViews.setText(getString(R.string.question_details_views_placeholder, "0"));
                }
            }

            @Override
            public void onFailure(Call<ConsultationViews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE BOOKMARK STATUS *****/
    private void fetchBookmarkStatus() {
        BookmarksAPI api = ZenApiClient.getClient().create(BookmarksAPI.class);
        Call<Bookmark> call = api.getUserConsultationBookmarkStatus(CONSULTATION_ID, USER_ID);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                if (response.isSuccessful())    {
                    Bookmark data = response.body();
                    if (data != null)   {
                        String bookmarkID = data.getBookmarkID();
                        if (bookmarkID != null) {
                            blnBookmarkStatus = true;

                            /* INVALIDATE THE OPTIONS MENU */
                            invalidateOptionsMenu();
                        } else {
                            blnBookmarkStatus = false;

                            /* INVALIDATE THE OPTIONS MENU */
                            invalidateOptionsMenu();
                        }
                    }
                } else {
                    blnBookmarkStatus = false;

                    /* INVALIDATE THE OPTIONS MENU */
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Question";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(QuestionDetails.this);
        inflater.inflate(R.menu.activity_question_details, menu);

        if (blnBookmarkStatus) {
            menu.findItem(R.id.menuBookmark).setIcon(R.drawable.ic_bookmark_white_24dp);
        } else {
            menu.findItem(R.id.menuBookmark).setIcon(R.drawable.ic_bookmark_border_white_24dp);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuBookmark:
                /* CHECK IF A BOOKMARK IS ALREADY ADDED */
                if (blnBookmarkStatus)  {
                    /* REMOVE THE BOOKMARK */
                    removeBookmark();
                } else {
                    /* ADD A NEW BOOKMARK */
                    addNewBookmark();
                }
                break;
            default:
                break;
        }
        return false;
    }

    /***** ADD A NEW BOOKMARK *****/
    private void addNewBookmark() {
        BookmarksAPI api = ZenApiClient.getClient().create(BookmarksAPI.class);
        Call<Bookmark> call = api.newConsultationBookmark(CONSULTATION_ID, USER_ID);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                if (response.isSuccessful())    {
                    /* FETCH THE BOOKMARK STATUS */
                    fetchBookmarkStatus();

                    /* INVALIDATE THE OPTIONS MENU */
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** REMOVE THE BOOKMARK *****/
    private void removeBookmark() {
        BookmarksAPI api = ZenApiClient.getClient().create(BookmarksAPI.class);
        Call<Bookmark> call = api.removeConsultationBookmark(CONSULTATION_ID, USER_ID);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                if (response.isSuccessful())    {
                    /* FETCH THE BOOKMARK STATUS */
                    fetchBookmarkStatus();

                    /* INVALIDATE THE OPTIONS MENU */
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listAnswers.setLayoutManager(manager);
        listAnswers.setHasFixedSize(true);
        listAnswers.setNestedScrollingEnabled(false);
        listAnswers.setAdapter(repliesAdapter);
    }

    /***** THE REPLIES ADAPTER *****/
    private class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ConsultationsVH> {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        private final Activity activity;

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<ConsultationReply> arrReplies;

        RepliesAdapter(Activity activity, ArrayList<ConsultationReply> arrReplies) {

            /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
            this.activity = activity;

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrReplies = arrReplies;
        }

        @Override
        public int getItemCount() {
            return arrReplies.size();
        }

        @Override
        public void onBindViewHolder(final ConsultationsVH holder, final int position) {
            final ConsultationReply data = arrReplies.get(position);

            /* SET THE REPLY TEXT */
            if (data.getReplyText() != null)    {
                holder.txtReplyText.setText(data.getReplyText());
            }

            /* SET THE TIME STAMP */
            if (data.getReplyTimestamp() != null)   {
                String consultationTimestamp = data.getReplyTimestamp();
                long lngTimeStamp = Long.parseLong(consultationTimestamp) * 1000;
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(lngTimeStamp);
                Date date = calendar.getTime();
                PrettyTime prettyTime = new PrettyTime();
                holder.txtReplyTimestamp.setText(prettyTime.format(date));
            }

            /* SET THE DOCTOR'S NAME */
            if (data.getDoctorPrefix() != null && data.getDoctorName() != null)   {
                String prefix = data.getDoctorPrefix();
                String name = data.getDoctorName();
                holder.txtDoctorName.setText(activity.getString(R.string.doctor_details_doc_name_placeholder, prefix, name));
            }

            /* SET THE DOCTOR PROFILE */
            if (data.getDoctorDisplayProfile() != null)   {
                Uri uri = Uri.parse(data.getDoctorDisplayProfile());
                holder.imgvwDoctorProfile.setImageURI(uri);
            } else {
                holder.imgvwDoctorProfile.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.beagle));
            }

            /* SET THE DOCTOR'S LOCATION */
            if (data.getStateName() != null && data.getCityName() != null)  {
                String city = data.getCityName();
                String state = data.getStateName();
                holder.txtDoctorLocation.setText(getString(R.string.question_details_location_placeholder, city, state));
                holder.txtDoctorLocation.setVisibility(View.VISIBLE);
            } else {
                holder.txtDoctorLocation.setVisibility(View.GONE);
            }

            /* FETCH THE NUMBER OF VOTES */
            fetchVotes(holder.txtReplyHelpful, data.getReplyID());

            /* MARK REPLY HELPFUL ("YES) */
            holder.txtHelpfulYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VotesAPI api = ZenApiClient.getClient().create(VotesAPI.class);
                    Call<Vote> call = api.checkUserVote(data.getReplyID(), USER_ID);
                    call.enqueue(new Callback<Vote>() {
                        @Override
                        public void onResponse(Call<Vote> call, Response<Vote> response) {
                            Vote vote = response.body();

                            /* PROCESS THE YES CLICK */
                            processYesClick(vote, holder.txtReplyHelpful, data.getReplyID());
                        }

                        @Override
                        public void onFailure(Call<Vote> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }
            });

            /* MARK REPLY NOT HELPFUL ("NO") */
            holder.txtHelpfulNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VotesAPI api = ZenApiClient.getClient().create(VotesAPI.class);
                    Call<Vote> call = api.checkUserVote(data.getReplyID(), USER_ID);
                    call.enqueue(new Callback<Vote>() {
                        @Override
                        public void onResponse(Call<Vote> call, Response<Vote> response) {
                            Vote vote = response.body();

                            /* PROCESS THE NO CLICK */
                            processNoClick(vote, holder.txtReplyHelpful, data.getReplyID());
                        }

                        @Override
                        public void onFailure(Call<Vote> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }
            });

            /* SHOW THE DOCTOR'S PROFILE */
            holder.imgvwDoctorProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(QuestionDetails.this, DoctorProfileActivity.class);
                    intent.putExtra("DOCTOR_ID", data.getDoctorID());
                    activity.startActivity(intent);
                }
            });
        }

        @Override
        public RepliesAdapter.ConsultationsVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.replies_item, parent, false);

            return new RepliesAdapter.ConsultationsVH(itemView);
        }

        class ConsultationsVH extends RecyclerView.ViewHolder	{
            final SimpleDraweeView imgvwDoctorProfile;
            final AppCompatTextView txtDoctorName;
            final AppCompatTextView txtDoctorLocation;
            final AppCompatTextView txtReplyTimestamp;
            final AppCompatTextView txtReplyText;
            final AppCompatTextView txtReplyHelpful;
            final IconicsImageView imgvwFlagReply;
            final AppCompatTextView txtHelpfulYes;
            final AppCompatTextView txtHelpfulNo;

            ConsultationsVH(View v) {
                super(v);
                imgvwDoctorProfile = v.findViewById(R.id.imgvwDoctorProfile);
                txtDoctorName = v.findViewById(R.id.txtDoctorName);
                txtDoctorLocation = v.findViewById(R.id.txtDoctorLocation);
                txtReplyTimestamp = v.findViewById(R.id.txtReplyTimestamp);
                txtReplyText = v.findViewById(R.id.txtReplyText);
                txtReplyHelpful = v.findViewById(R.id.txtReplyHelpful);
                imgvwFlagReply = v.findViewById(R.id.imgvwFlagReply);
                txtHelpfulYes = v.findViewById(R.id.txtHelpfulYes);
                txtHelpfulNo = v.findViewById(R.id.txtHelpfulNo);
            }
        }
    }

    /***** PROCESS THE YES CLICK *****/
    private void processYesClick(Vote vote, final AppCompatTextView txtReplyHelpful, final String replyID) {
        if (!vote.getError() && vote != null)   {
            String voteStatus = vote.getVoteStatus();
            String voteID = vote.getVoteID();
            if (voteStatus.equalsIgnoreCase("1"))   {
                Toast.makeText(
                        getApplicationContext(),
                        "You have already voted this reply as helpful",
                        Toast.LENGTH_SHORT).show();
            } else {
                /* UPDATE THE USER'S VOTE TO A "YES" VOTE*/
                VotesAPI apiUpdate = ZenApiClient.getClient().create(VotesAPI.class);
                Call<Vote> calUpdate = apiUpdate.updateReplyVoteYes(voteID, "1");
                calUpdate.enqueue(new Callback<Vote>() {
                    @Override
                    public void onResponse(Call<Vote> call, Response<Vote> response) {
                        if (response.isSuccessful())    {
                            /* FETCH THE NUMBER OF VOTES */
                            fetchVotes(txtReplyHelpful, replyID);
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Update failed...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Vote> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });
            }
        } else {
            /* PUBLISH A NEW "YES" VOTE ON THE DOCTOR'S REPLY */
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            VotesAPI apiPublish = ZenApiClient.getClient().create(VotesAPI.class);
            Call<Vote> callPublish = apiPublish.newReplyVote(
                    replyID, USER_ID, "1", timeStamp
            );
            callPublish.enqueue(new Callback<Vote>() {
                @Override
                public void onResponse(Call<Vote> call, Response<Vote> response) {
                    if (response.isSuccessful())    {
                        /* FETCH THE NUMBER OF VOTES */
                        fetchVotes(txtReplyHelpful, replyID);
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Vote failed...",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Vote> call, Throwable t) {
                    Crashlytics.logException(t);
                }
            });
        }
    }

    /* PROCESS THE NO CLICK */
    private void processNoClick(Vote vote, final AppCompatTextView txtReplyHelpful, final String replyID) {
        if (!vote.getError() && vote != null)   {
            String voteStatus = vote.getVoteStatus();
            String voteID = vote.getVoteID();
            if (voteStatus.equalsIgnoreCase("0"))   {
                Toast.makeText(
                        getApplicationContext(),
                        "You have already voted this reply as not helpful",
                        Toast.LENGTH_SHORT).show();
            } else {
                /* UPDATE THE USER'S VOTE TO A "NO" VOTE*/
                VotesAPI apiUpdate = ZenApiClient.getClient().create(VotesAPI.class);
                Call<Vote> calUpdate = apiUpdate.updateReplyVoteNo(voteID, "0");
                calUpdate.enqueue(new Callback<Vote>() {
                    @Override
                    public void onResponse(Call<Vote> call, Response<Vote> response) {
                        if (response.isSuccessful())    {
                            /* FETCH THE NUMBER OF VOTES */
                            fetchVotes(txtReplyHelpful, replyID);
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Update failed...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Vote> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });
            }
        } else {
            /* PUBLISH A NEW "NO" VOTE ON THE DOCTOR'S REPLY */
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            VotesAPI apiPublish = ZenApiClient.getClient().create(VotesAPI.class);
            Call<Vote> callPublish = apiPublish.newReplyVote(
                    replyID, USER_ID, "0", timeStamp
            );
            callPublish.enqueue(new Callback<Vote>() {
                @Override
                public void onResponse(Call<Vote> call, Response<Vote> response) {
                    if (response.isSuccessful())    {
                        /* FETCH THE NUMBER OF VOTES */
                        fetchVotes(txtReplyHelpful, replyID);
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Vote failed...",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Vote> call, Throwable t) {
                    Crashlytics.logException(t);
                }
            });
        }
    }

    /***** FETCH THE NUMBER OF VOTES *****/
    private void fetchVotes(final AppCompatTextView txtReplyHelpful, final String replyID) {
        VotesAPI apiYes = ZenApiClient.getClient().create(VotesAPI.class);
        Call<Votes> callYes = apiYes.fetchYesVotes(replyID, "1");
        callYes.enqueue(new Callback<Votes>() {
            @Override
            public void onResponse(Call<Votes> call, Response<Votes> response) {
                ArrayList<Vote> listYes = response.body().getVotes();
                if (listYes != null && listYes.size() > 0)  {
                    HELPFUL_VOTES = String.valueOf(listYes.size());
                } else {
                    HELPFUL_VOTES = "0";
                }

                /* FETCH THE NUMBER OF "NO" VOTES */
                VotesAPI apiNo = ZenApiClient.getClient().create(VotesAPI.class);
                Call<Votes> callNo = apiNo.fetchNoVotes(replyID, "0");
                callNo.enqueue(new Callback<Votes>() {
                    @Override
                    public void onResponse(Call<Votes> call, Response<Votes> response) {
                        ArrayList<Vote> listNo = response.body().getVotes();
                        if (listNo != null && listNo.size() > 0)    {
                            NON_HELPFUL_VOTES = String.valueOf(listNo.size());
                        } else {
                            NON_HELPFUL_VOTES = "0";
                        }

                        /* SET THE NUMBER OF "YES" AND "NO" VOTES */
                        txtReplyHelpful.setText(getString(R.string.question_details_votes_placeholder, HELPFUL_VOTES, NON_HELPFUL_VOTES));
                    }

                    @Override
                    public void onFailure(Call<Votes> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });
            }

            @Override
            public void onFailure(Call<Votes> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }
}