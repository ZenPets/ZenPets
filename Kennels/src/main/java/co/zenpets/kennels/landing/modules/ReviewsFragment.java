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
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.reviews.ReviewsAdapter;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.reviews.Review;
import co.zenpets.kennels.utils.models.reviews.Reviews;
import co.zenpets.kennels.utils.models.reviews.ReviewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN KENNEL ID **/
    String KENNEL_ID = null;

//    /** AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
//    ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** THE REVIEWS ADAPTER AND ARRAY LISTS **/
    ReviewsAdapter reviewsAdapter;
    private ArrayList<Review> arrReviews = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.dash_reviews_list, container, false);
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

        /* INSTANTIATE THE KENNEL REVIEWS ADAPTER */
        reviewsAdapter = new ReviewsAdapter(getActivity(), arrReviews);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE LOGGED IN KENNEL OWNER'S ID */
        KENNEL_ID = getApp().getKennelID();
        if (KENNEL_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE LIST OF REVIEWS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennelReviews();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
        }
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
                        reviewsAdapter = new ReviewsAdapter(getActivity(), arrReviews);

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

//    /** FETCH THE LIST OF KENNELS **/
//    private void fetchKennels() {
//        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
//        Call<Kennels> call = api.fetchKennelsListByOwner(KENNEL_ID);
//        call.enqueue(new Callback<Kennels>() {
//            @Override
//            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
////                Log.e("RAW", String.valueOf(response.raw()));
//                if (response.body() != null && response.body().getKennels() != null) {
//                    arrKennels = response.body().getKennels();
//                    if (arrKennels.size() > 0) {
//                        /* SET THE ADAPTER TO THE KENNELS SPINNER */
//                        spnKennels.setAdapter(new KennelsSpinnerAdapter(
//                                getActivity(),
//                                R.layout.pet_capacity_row,
//                                arrKennels));
//                    }
//                }
//
//                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
//                linlaProgress.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<Kennels> call, Throwable t) {
////                Log.e("KENNELS FAILURE", t.getMessage());
//            }
//        });
//    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = "Kennel Reviews";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
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
        listReviews.setAdapter(reviewsAdapter);
    }
}