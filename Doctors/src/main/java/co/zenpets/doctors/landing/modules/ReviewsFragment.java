package co.zenpets.doctors.landing.modules;

import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.reviews.ReviewsAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.reviews.DoctorReviewsAPI;
import co.zenpets.doctors.utils.models.doctors.reviews.ReviewData;
import co.zenpets.doctors.utils.models.doctors.reviews.ReviewsData;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /* THE REVIEWS ARRAY LIST */
    private ArrayList<ReviewData> arrReviews = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.home_reviews_fragment_list, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE LOGGED IN DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();
        if (DOCTOR_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S REVIEWS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchDoctorReviews();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
        }
    }

    /***** FETCH THE DOCTOR'S REVIEWS *****/
    private void fetchDoctorReviews() {
        DoctorReviewsAPI api = ZenApiClient.getClient().create(DoctorReviewsAPI.class);
        Call<ReviewsData> call = api.fetchDoctorReviews(DOCTOR_ID);
        call.enqueue(new Callback<ReviewsData>() {
            @Override
            public void onResponse(Call<ReviewsData> call, Response<ReviewsData> response) {
                arrReviews = response.body().getReviews();
                if (arrReviews != null && arrReviews.size() > 0)    {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT  */
                    listReviews.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                    listReviews.setAdapter(new ReviewsAdapter(getActivity(), arrReviews));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listReviews.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ReviewsData> call, Throwable t) {
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        listReviews.setLayoutManager(manager);
        listReviews.setHasFixedSize(true);
        listReviews.setNestedScrollingEnabled(true);

        /* SET THE EDUCATIONS ADAPTER */
        listReviews.setAdapter(new ReviewsAdapter(getActivity(), arrReviews));
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = "Reviews";
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