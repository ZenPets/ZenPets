package co.zenpets.kennels.landing.modules;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.enquiries.KennelEnquiriesAdapter;
import co.zenpets.kennels.utils.adapters.reviews.ReviewsAdapter;
import co.zenpets.kennels.utils.models.enquiries.Enquiries;
import co.zenpets.kennels.utils.models.enquiries.EnquiriesAPI;
import co.zenpets.kennels.utils.models.enquiries.Enquiry;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import co.zenpets.kennels.utils.models.reviews.Review;
import co.zenpets.kennels.utils.models.reviews.Reviews;
import co.zenpets.kennels.utils.models.reviews.ReviewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN KENNEL ID **/
    String KENNEL_ID = null;

    /** THE KENNEL NAME AND COVER PHOTO **/
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;

    /** THE ENQUIRIES ARRAY LIST **/
    ArrayList<Enquiry> arrEnquiries = new ArrayList<>();

    /** AN REVIEWS ARRAY LIST **/
    private ArrayList<Review> arrReviews = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwKennelCoverPhoto) ImageView imgvwKennelCoverPhoto;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.listEnquiries) RecyclerView listEnquiries;
    @BindView(R.id.linlaEmptyEnquiries) LinearLayout linlaEmptyEnquiries;
    @BindView(R.id.txtViewEnquiries) TextView txtViewEnquiries;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaEmptyReviews) LinearLayout linlaEmptyReviews;
    @BindView(R.id.txtViewReviews) TextView txtViewReviews;

    /** SHOW THE KENNEL DETAILS **/
    @OnClick(R.id.txtViewKennel) void showKennelDetails()   {
    }

    /** SHOW ALL KENNEL ENQUIRIES **/
    @OnClick(R.id.txtViewEnquiries) void showEnquiries()    {
    }

    /** SHOW ALL KENNEL REVIEWS **/
    @OnClick(R.id.txtViewReviews) void showReviews()    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.home_dashboard_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE */
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU */
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES */
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE KENNEL ID */
        KENNEL_ID = getApp().getKennelID();
//        Log.e("KENNEL ID", KENNEL_ID);

        /* FETCH THE KENNEL DETAILS */
        fetchKennelDetails();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF ENQUIRIES */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchKennelEnquiries();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF REVIEWS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchKennelReviews();
    }

    /** FETCH THE LIST OF ENQUIRIES **/
    private void fetchKennelEnquiries() {
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiries> call = api.fetchKennelDashboardEnquiries(KENNEL_ID);
        call.enqueue(new Callback<Enquiries>() {
            @Override
            public void onResponse(Call<Enquiries> call, Response<Enquiries> response) {
//                Log.e("DASH ENQUIRES RESPONSE", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getEnquiries() != null)  {
                    arrEnquiries = response.body().getEnquiries();
                    if (arrEnquiries.size() > 0)    {
                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listEnquiries.setAdapter(new KennelEnquiriesAdapter(getActivity(), arrEnquiries));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listEnquiries.setVisibility(View.VISIBLE);
                        linlaEmptyEnquiries.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmptyEnquiries.setVisibility(View.VISIBLE);
                        listEnquiries.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmptyEnquiries.setVisibility(View.VISIBLE);
                    listEnquiries.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Enquiries> call, Throwable t) {
//                Log.e("ENQUIRIES FAILURE", t.getMessage());
            }
        });
    }

    /** FETCH THE LIST OF REVIEWS **/
    private void fetchKennelReviews() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchKennelDashboardReviews(KENNEL_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
//                Log.e("DASH REVIEWS RESPONSE", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviews = response.body().getReviews();
                    if (arrReviews.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        listReviews.setVisibility(View.VISIBLE);
                        linlaEmptyReviews.setVisibility(View.GONE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new ReviewsAdapter(getActivity(), arrReviews));
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmptyReviews.setVisibility(View.VISIBLE);
                        listReviews.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmptyReviews.setVisibility(View.VISIBLE);
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

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE ENQUIRIES CONFIGURATION */
        LinearLayoutManager enquiries = new LinearLayoutManager(getActivity());
        enquiries.setOrientation(LinearLayoutManager.VERTICAL);
        listEnquiries.setLayoutManager(enquiries);
        listEnquiries.setHasFixedSize(true);
        listEnquiries.setNestedScrollingEnabled(false);

        /* SET THE ENQUIRIES ADAPTER TO THE RECYCLER VIEW */
        listEnquiries.setAdapter(new KennelEnquiriesAdapter(getActivity(), arrEnquiries));

        /* SET THE REVIEWS CONFIGURATION */
        LinearLayoutManager reviews = new LinearLayoutManager(getActivity());
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);

        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
        listReviews.setAdapter(new ReviewsAdapter(getActivity(), arrReviews));

    }

    /** FETCH THE KENNEL DETAILS **/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.fetchKennelDetails(KENNEL_ID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
//                Log.e("KENNEL RESPONSE", String.valueOf(response.raw()));
                Kennel kennel = response.body();
                if (kennel != null) {
                    /* GET THE KENNEL NAME */
                    KENNEL_NAME = kennel.getKennelName();
                    if (KENNEL_NAME != null)    {
//                        Log.e("KENNEL NAME", KENNEL_NAME);
                        txtKennelName.setText(KENNEL_NAME);
                    }

                    /* GET THE KENNEL COVER PHOTO */
                    KENNEL_COVER_PHOTO = kennel.getKennelCoverPhoto();
                    if (KENNEL_COVER_PHOTO != null) {
                        if (KENNEL_COVER_PHOTO != null) {
                            Picasso.with(getActivity())
                                    .load(KENNEL_COVER_PHOTO)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .fit()
                                    .into(imgvwKennelCoverPhoto, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
                                            Picasso.with(getActivity())
                                                    .load(KENNEL_COVER_PHOTO)
                                                    .into(imgvwKennelCoverPhoto);
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = "Kennel Dashboard";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }
}