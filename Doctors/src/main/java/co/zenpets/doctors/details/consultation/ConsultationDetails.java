package co.zenpets.doctors.details.consultation;

import android.app.ProgressDialog;
import android.content.res.Resources;
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
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
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

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.consultations.RepliesAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.consultations.ReplyData;
import co.zenpets.doctors.utils.models.consultations.consultations.Consultation;
import co.zenpets.doctors.utils.models.consultations.consultations.ConsultationsAPI;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationReplies;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationRepliesAPI;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationReply;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationStatus;
import co.zenpets.doctors.utils.models.consultations.views.ConsultationView;
import co.zenpets.doctors.utils.models.consultations.views.ConsultationViews;
import co.zenpets.doctors.utils.models.consultations.views.ConsultationViewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE INCOMING CONSULTATION ID **/
    private String CONSULTATION_ID = null;

    /** THE ARRAY LIST **/
    private ArrayList<ConsultationReply> arrReplies = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** A BOOLEAN STATUS TO CHECK IF THE DOCTOR HAS REPLIED **/
    private Boolean blnReplyStatus = false;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.txtConsultationTitle)AppCompatTextView txtConsultationTitle;
    @BindView(R.id.txtConsultationDescription) AppCompatTextView txtConsultationDescription;
    @BindView(R.id.txtConsultationFor) AppCompatTextView txtConsultationFor;
    @BindView(R.id.txtConsultationTimestamp) AppCompatTextView txtConsultationTimestamp;
    @BindView(R.id.txtConsultationViews) AppCompatTextView txtConsultationViews;
    @BindView(R.id.txtConsultationReplies) AppCompatTextView txtConsultationReplies;
    @BindView(R.id.imgvwHasAttachment) IconicsImageView imgvwHasAttachment;
    @BindView(R.id.linlaAttachment) LinearLayout linlaAttachment;
    @BindView(R.id.imgvwConsultationPicture) SimpleDraweeView imgvwConsultationPicture;
    @BindView(R.id.linlaRepliesProgress) LinearLayout linlaRepliesProgress;
    @BindView(R.id.listAnswers) RecyclerView listAnswers;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_details);
        ButterKnife.bind(this);

        /* GET THE DOCTOR ID */
        DOCTOR_ID = getApp().getDoctorID();
        if (DOCTOR_ID != null)  {

            /* GET THE INCOMING DATA */
            getIncomingData();

            /* CONFIGURE THE TOOLBAR */
            configTB();

            /* CONFIGURE THE RECYCLER VIEW */
            configRecycler();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE CONSULTATION DETAILS *****/
    private void fetchConsultationDetails() {
        ConsultationsAPI api = ZenApiClient.getClient().create(ConsultationsAPI.class);
        Call<Consultation> call = api.consultationDetails(CONSULTATION_ID);
        call.enqueue(new Callback<Consultation>() {
            @Override
            public void onResponse(@NonNull Call<Consultation> call, @NonNull Response<Consultation> response) {
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
                        public void onResponse(@NonNull Call<ConsultationReplies> call, @NonNull Response<ConsultationReplies> response) {
                            ArrayList<ConsultationReply> list = response.body().getReplies();
                            if (list != null && list.size() > 0)    {
                                int TOTAL_REPLIES = list.size();

                                /* GET THE TOTAL NUMBER OF REPLIES */
                                Resources resReviews = AppPrefs.context().getResources();
                                String replyQuantity = null;
                                if (TOTAL_REPLIES == 0)   {
                                    replyQuantity = resReviews.getQuantityString(R.plurals.replies, TOTAL_REPLIES, TOTAL_REPLIES);
                                } else if (TOTAL_REPLIES == 1)    {
                                    replyQuantity = resReviews.getQuantityString(R.plurals.replies, TOTAL_REPLIES, TOTAL_REPLIES);
                                } else if (TOTAL_REPLIES > 1) {
                                    replyQuantity = resReviews.getQuantityString(R.plurals.replies, TOTAL_REPLIES, TOTAL_REPLIES);
                                }
                                txtConsultationReplies.setText(replyQuantity);
                            } else {
                                txtConsultationReplies.setText(getString(R.string.question_details_replies_placeholder, "0"));
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<ConsultationReplies> call, @NonNull Throwable t) {
//                            Crashlytics.logException(t);
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
            public void onFailure(@NonNull Call<Consultation> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CHECK THE DOCTOR'S REPLY STATUS *****/
    private void checkReplyStatus() {
        ConsultationRepliesAPI api = ZenApiClient.getClient().create(ConsultationRepliesAPI.class);
        Call<ConsultationStatus> callStatus = api.consultationDoctorReplied(CONSULTATION_ID, DOCTOR_ID);
        callStatus.enqueue(new Callback<ConsultationStatus>() {
            @Override
            public void onResponse(@NonNull Call<ConsultationStatus> call, @NonNull Response<ConsultationStatus> response) {
                ConsultationStatus status = response.body();
                if (status != null) {
                    blnReplyStatus = status.getError();
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConsultationStatus> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** REFRESH THE NUMBER OF VIEWS *****/
    private void fetchConsultationViews() {
        ConsultationViewsAPI api = ZenApiClient.getClient().create(ConsultationViewsAPI.class);
        Call<ConsultationViews> call = api.fetchConsultationViews(CONSULTATION_ID);
        call.enqueue(new Callback<ConsultationViews>() {
            @Override
            public void onResponse(@NonNull Call<ConsultationViews> call, @NonNull Response<ConsultationViews> response) {
                ArrayList<ConsultationView> list = response.body().getViews();
                if (list != null && list.size() > 0)    {
                    String views = String.valueOf(list.size());
                    txtConsultationViews.setText(getString(R.string.question_details_views_placeholder, views));
                } else {
                    txtConsultationViews.setText(getString(R.string.question_details_views_placeholder, "0"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConsultationViews> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE CONSULTATION REPLIES *****/
    private void fetchReplies() {
        ConsultationRepliesAPI api = ZenApiClient.getClient().create(ConsultationRepliesAPI.class);
        Call<ConsultationReplies> call = api.fetchConsultationReplies(CONSULTATION_ID);
        call.enqueue(new Callback<ConsultationReplies>() {
            @Override
            public void onResponse(@NonNull Call<ConsultationReplies> call, @NonNull Response<ConsultationReplies> response) {
                if (response.body() != null && response.body().getReplies() != null)    {
                    arrReplies = response.body().getReplies();

                    /* CHECK IF RESULTS WERE RETURNED */
                    if (arrReplies != null && arrReplies.size() > 0)  {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY VIEW */
                        listAnswers.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE ADAPTER */
                        listAnswers.setAdapter(new RepliesAdapter(ConsultationDetails.this, arrReplies));
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

                /* CHECK THE DOCTOR'S REPLY STATUS */
                checkReplyStatus();
            }

            @Override
            public void onFailure(@NonNull Call<ConsultationReplies> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CONSULTATION_ID"))    {
            CONSULTATION_ID = bundle.getString("CONSULTATION_ID");
            if (CONSULTATION_ID != null)    {
                /* SHOW THE PROGRESS AND FETCH THE CONSULTATION DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchConsultationDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listAnswers.setLayoutManager(manager);
        listAnswers.setHasFixedSize(true);
        listAnswers.setNestedScrollingEnabled(false);
        listAnswers.setAdapter(new RepliesAdapter(ConsultationDetails.this, arrReplies));
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Details";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(ConsultationDetails.this);
        inflater.inflate(R.menu.activity_consultation_reply, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuReply:
                if (blnReplyStatus) {
                    /* SHOW THE NEW REPLY DIALOG */
                    showReplyDialog();
                } else {
                    /* SHOW THE ALREADY REPLIED DIALOG */
                    showRepliedDialog();
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void showRepliedDialog() {
        new MaterialDialog.Builder(ConsultationDetails.this)
                .icon(ContextCompat.getDrawable(ConsultationDetails.this, R.drawable.ic_info_outline_black_24dp))
                .title("Already Answered")
                .cancelable(true)
                .content("You have already posted an answer to this consultation question. You cannot post more than one answer to a consultation question")
                .positiveText("Okay")
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showReplyDialog() {
        new MaterialDialog.Builder(ConsultationDetails.this)
                .title("Post an answer")
                .content("Post a new answer to this consultation question. Add as many details as required.")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(20, 500)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .positiveText("Post Reply")
                .negativeText("Cancel")
                .input("Post an answer....", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
                        progressDialog = new ProgressDialog(ConsultationDetails.this);
                        progressDialog.setMessage("Please wait while we post your reply...");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        ConsultationsAPI api = ZenApiClient.getClient().create(ConsultationsAPI.class);
                        Call<ReplyData> call = api.newConsultationReply(
                                CONSULTATION_ID, DOCTOR_ID, input.toString(),
                                String.valueOf(System.currentTimeMillis() / 1000));
                        call.enqueue(new Callback<ReplyData>() {
                            @Override
                            public void onResponse(@NonNull Call<ReplyData> call, @NonNull Response<ReplyData> response) {
                                if (response.isSuccessful())    {
                                    /* CLEAR THE ARRAY LIST */
                                    if (arrReplies != null)
                                        arrReplies.clear();

                                    /* SHOW THE PROGRESS AND START FETCHING THE REPLIES (AGAIN) */
                                    linlaRepliesProgress.setVisibility(View.VISIBLE);
                                    fetchReplies();
                                } else {
                                    /* SHOW THE ERROR MESSAGE */
                                    Toast.makeText(getApplicationContext(), "There was an error posting your reply. Please try again...", Toast.LENGTH_LONG).show();
                                }

                                /* DISMISS THE PROGRESS DIALOG */
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(@NonNull Call<ReplyData> call, @NonNull Throwable t) {
                                /* SHOW THE ERROR MESSAGE */
                                Toast.makeText(getApplicationContext(), "There was an error posting your reply. Please try again...", Toast.LENGTH_LONG).show();
//                                Log.e("FAILURE", t.getMessage());
//                                Crashlytics.logException(t);
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

    /***** CALCULATE HE PET'S AGE *****/
    private Period getPetAge(int year, int month, int date) {
        LocalDate dob = new LocalDate(year, month, date);
        LocalDate now = new LocalDate();
        return Period.fieldDifference(dob, now);
    }
}