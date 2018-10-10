package co.zenpets.groomers.landing.modules;

import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.zenpets.groomers.R;
import co.zenpets.groomers.details.groomer.GroomerDetails;
import co.zenpets.groomers.utils.AppPrefs;
import co.zenpets.groomers.utils.TypefaceSpan;
import co.zenpets.groomers.utils.adapters.reviews.ReviewsAdapter;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.dashboard.DashboardAPI;
import co.zenpets.groomers.utils.models.groomers.Groomer;
import co.zenpets.groomers.utils.models.groomers.GroomersAPI;
import co.zenpets.groomers.utils.models.reviews.Review;
import co.zenpets.groomers.utils.models.reviews.Reviews;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN GROOMER ACCOUNT ID **/
    private String GROOMER_ID = null;

    /** THE GROOMER'S DETAILS **/
    private  String GROOMER_NAME = null;
    private  String GROOMER_LOGO = null;

    /** THE REVIEWS ARRAY LIST **/
    private ArrayList<Review> arrReviewsSubset = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwGroomerLogo) ImageView imgvwGroomerLogo;
    @BindView(R.id.txtGroomerName) TextView txtGroomerName;
    @BindView(R.id.listEnquiries) RecyclerView listEnquiries;
    @BindView(R.id.txtViewEnquiries) TextView txtViewEnquiries;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.txtViewReviews) TextView txtViewReviews;

    /** SHOW THE GROOMER'S PROFILE **/
    @OnClick(R.id.txtViewProfile) void showProfile()    {
        Intent intent = new Intent(getActivity(), GroomerDetails.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.home_dashboard_frag, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* GET THE GROOMER ACCOUNT ID */
        GROOMER_ID = getApp().getGroomerID();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SHOW THE PROGRESS AND FETCH THE GROOMER'S DETAILS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchGroomerDetails();

        /* FETCH THE THREE (3) RECENT REVIEWS */
        fetchGroomerReviews();
    }

    /** FETCH THE GROOMER'S DETAILS **/
    private void fetchGroomerDetails() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.fetchGroomerDetails(GROOMER_ID);
        call.enqueue(new Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
                Groomer groomer = response.body();
                if (groomer != null)    {

                    /* GET AND SET THE GROOMER'S NAME */
                    GROOMER_NAME = groomer.getGroomerName();
                    if (GROOMER_NAME != null)    {
                        txtGroomerName.setText(GROOMER_NAME);
                    }

                    /* GET THE DOCTOR'S DISPLAY PROFILE */
                    GROOMER_LOGO = groomer.getGroomerLogo();
                    if (GROOMER_LOGO != null) {
                        Picasso.with(getActivity())
                                .load(GROOMER_LOGO)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .fit()
                                .into(imgvwGroomerLogo, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getActivity())
                                                .load(GROOMER_LOGO)
                                                .into(imgvwGroomerLogo);
                                    }
                                });
                    }
                }

                /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable throwable) {
                Log.e("DETAILS FAILURE", throwable.getMessage());
            }
        });
    }

    /** FETCH THE FIRST 3 REVIEWS FOR THE GROOMER **/
    private void fetchGroomerReviews() {
        DashboardAPI api = ZenApiClient.getClient().create(DashboardAPI.class);
        Call<Reviews> call = api.fetchGroomerDashboardReviews(GROOMER_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
//                Log.e("REVIEWS RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getReviews() != null)    {
                    arrReviewsSubset = response.body().getReviews();
                    if (arrReviewsSubset.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
//                        linlaNoReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.VISIBLE);
//                        txtAllReviews.setVisibility(View.VISIBLE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new ReviewsAdapter(getActivity(), arrReviewsSubset));
                    } else {
                        /* SHOW THE NO REVIEWS LAYOUT */
//                        linlaNoReviews.setVisibility(View.VISIBLE);
//                        txtAllReviews.setVisibility(View.GONE);
                        listReviews.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO REVIEWS LAYOUT */
//                    linlaNoReviews.setVisibility(View.VISIBLE);
//                    txtAllReviews.setVisibility(View.GONE);
                    listReviews.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE REVIEWS */
//                linlaReviewsProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        String strTitle = "Dashboard";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager reviews = new LinearLayoutManager(getActivity());
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        reviews.isAutoMeasureEnabled();
        listReviews.setLayoutManager(reviews);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(false);
        listReviews.setAdapter(new ReviewsAdapter(getActivity(), arrReviewsSubset));

        LinearLayoutManager llmEnquiries = new LinearLayoutManager(getActivity());
        llmEnquiries.setOrientation(LinearLayoutManager.VERTICAL);
        llmEnquiries.isAutoMeasureEnabled();
        listEnquiries.setLayoutManager(llmEnquiries);
        listEnquiries.setHasFixedSize(true);
        listEnquiries.setNestedScrollingEnabled(false);
//        listEnquiries.setAdapter(new KennelImagesAdapter(getActivity(), arrImages));
    }
}