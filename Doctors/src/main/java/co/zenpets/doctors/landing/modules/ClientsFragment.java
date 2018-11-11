package co.zenpets.doctors.landing.modules;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.clients.ClientsAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.clients.Client;
import co.zenpets.doctors.utils.models.doctors.clients.Clients;
import co.zenpets.doctors.utils.models.doctors.clients.DoctorClientsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE SEARCH VIEW INSTANCE **/
    private SearchView searchView;

    /** THE CLIENTS ADAPTER AND ARRAY LIST **/
    private ClientsAdapter adapter;
    private ArrayList<Client> arrClients = new ArrayList<>();
    private final ArrayList<Client> arrFilteredResults = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listClients) RecyclerView listClients;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.home_clients_frag_list, container, false);
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

        /* INSTANTIATE THE ADAPTER */
        adapter = new ClientsAdapter(getActivity(), arrClients);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE LOGGED IN DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();
        if (DOCTOR_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S CLIENTS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchDoctorClients();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
        }
    }

    /***** FETCH THE DOCTOR'S CLIENTS *****/
    private void fetchDoctorClients() {
        DoctorClientsAPI api = ZenApiClient.getClient().create(DoctorClientsAPI.class);
        Call<Clients> call = api.fetchDoctorClients(DOCTOR_ID);
        call.enqueue(new Callback<Clients>() {
            @Override
            public void onResponse(@NonNull Call<Clients> call, @NonNull Response<Clients> response) {
                arrClients = response.body().getClients();
                if (arrClients != null && arrClients.size() > 0)    {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT  */
                    listClients.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* CAST THE PRIMARY ARRAY DATA TO THE FILTERED ARRAY */
                    arrFilteredResults.addAll(arrClients);

                    /* INSTANTIATE THE LOCATION SEARCH ADAPTER */
                    adapter = new ClientsAdapter(getActivity(), arrFilteredResults);

                    /* SET THE ADAPTER TO THE LOCALITIES RECYCLER VIEW */
                    listClients.setAdapter(adapter);
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listClients.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Clients> call, @NonNull Throwable t) {
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = "Your Clients";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_client_search, menu);
        MenuItem search = menu.findItem(R.id.menuSearchClients);
        searchView = (SearchView) search.getActionView();
        searchView.setIconified(true);
        searchView.setQueryHint("Search Clients...");
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String search) {
        adapter.getFilter().filter(search);
        return true;
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        listClients.setLayoutManager(manager);
        listClients.setHasFixedSize(true);
        listClients.setNestedScrollingEnabled(true);

        /* SET THE PATIENTS ADAPTER */
        listClients.setAdapter(adapter);
    }
}