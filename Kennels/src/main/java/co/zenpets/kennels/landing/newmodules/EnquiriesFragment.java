package co.zenpets.kennels.landing.newmodules;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.adapters.enquiries.KennelEnquiriesAdapter;
import co.zenpets.kennels.utils.adapters.kennels.KennelsSpinnerAdapter;
import co.zenpets.kennels.utils.models.enquiries.Enquiries;
import co.zenpets.kennels.utils.models.enquiries.EnquiriesAPI;
import co.zenpets.kennels.utils.models.enquiries.Enquiry;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.Kennels;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnquiriesFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN KENNEL OWNER'S ID **/
    String KENNEL_OWNER_ID = null;

    /** THE SELECTED KENNEL'S ID **/
    String KENNEL_ID = null;

    /** AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
    ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** THE TRAINING ENQUIRIES ARRAY LIST **/
    ArrayList<Enquiry> arrEnquiries = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnKennels) Spinner spnKennels;
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

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchKennels();

        /* SELECT A KENNEL TO SHOW IT'S REVIEWS */
        spnKennels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED KENNEL ID */
                KENNEL_ID = arrKennels.get(position).getKennelID();

                /* CLEAR THE ENQUIRES ARRAY */
                if (arrEnquiries != null)
                    arrEnquiries.clear();

                /* SHOW THE PROGRESS AND FETCH THE LIST OF ENQUIRIES */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchKennelEnquiries();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                                getActivity(),
                                R.layout.pet_capacity_row,
                                arrKennels));
                    }
                }
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
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

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), manager.getOrientation());
        listKennelEnquiries.addItemDecoration(decoration);
    }
}