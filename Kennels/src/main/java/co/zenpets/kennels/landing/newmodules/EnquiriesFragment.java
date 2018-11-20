package co.zenpets.kennels.landing.newmodules;

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
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.enquiries.KennelEnquiriesAdapter;
import co.zenpets.kennels.utils.models.enquiries.Enquiries;
import co.zenpets.kennels.utils.models.enquiries.EnquiriesAPI;
import co.zenpets.kennels.utils.models.enquiries.Enquiry;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnquiriesFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN KENNEL ID **/
    String KENNEL_ID = null;

    /** THE TRAINING ENQUIRIES ARRAY LIST **/
    ArrayList<Enquiry> arrEnquiries = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennelEnquiries) RecyclerView listKennelEnquiries;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) TextView txtEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.enquiries_list, container, false);
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

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_ID = getApp().getKennelID();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF ENQUIRIES */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchKennelEnquiries();
    }

    /** FETCH THE LIST OF ENQUIRIES **/
    private void fetchKennelEnquiries() {
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiries> call = api.fetchKennelEnquiries(KENNEL_ID);
        call.enqueue(new Callback<Enquiries>() {
            @Override
            public void onResponse(Call<Enquiries> call, Response<Enquiries> response) {
                if (response.body() != null && response.body().getEnquiries() != null)  {
                    arrEnquiries = response.body().getEnquiries();
                    if (arrEnquiries.size() > 0)    {
                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listKennelEnquiries.setAdapter(new KennelEnquiriesAdapter(getActivity(), arrEnquiries));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listKennelEnquiries.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listKennelEnquiries.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listKennelEnquiries.setVisibility(View.GONE);
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

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listKennelEnquiries.setLayoutManager(manager);
        listKennelEnquiries.setHasFixedSize(true);

        /* SET THE ADAPTER TO THE RECYCLER VIEW */
        listKennelEnquiries.setAdapter(new KennelEnquiriesAdapter(getActivity(), arrEnquiries));
//
//        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), manager.getOrientation());
//        listKennelEnquiries.addItemDecoration(decoration);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = "Kennel Enquiries";
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