package co.zenpets.doctors.creator.appointment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.clients.Client;
import co.zenpets.doctors.utils.models.doctors.clients.Clients;
import co.zenpets.doctors.utils.models.doctors.clients.DoctorClientsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentClientSelector extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /** THE INCOMING DATA **/
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;
    private String APPOINTMENT_TIME = null;
    private String APPOINTMENT_DATE = null;

    /** THE CLIENTS ADAPTER AND ARRAY LIST **/
    private ClientsSelectorAdapter adapter;
    private ArrayList<Client> arrClients = new ArrayList<>();
    private final ArrayList<Client> arrFilteredResults = new ArrayList<>();

    /** THE SEARCH VIEW INSTANCE **/
    private SearchView searchView;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listClients) RecyclerView listClients;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_client_selector);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* INSTANTIATE THE ADAPTER */
        adapter = new ClientsSelectorAdapter(arrClients);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();

//        /* GET THE LOGGED IN DOCTOR'S ID */
//        DOCTOR_ID = getApp().getDoctorID();
//        if (DOCTOR_ID != null)    {
//            /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S CLIENTS */
//            linlaProgress.setVisibility(View.VISIBLE);
//            fetchDoctorClients();
//        } else {
//            Toast.makeText(this, "Failed to get required info...", Toast.LENGTH_SHORT).show();
//        }
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("CLINIC_ID")
                && bundle.containsKey("APPOINTMENT_TIME")
                && bundle.containsKey("APPOINTMENT_DATE")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            APPOINTMENT_TIME = bundle.getString("APPOINTMENT_TIME");
            APPOINTMENT_DATE = bundle.getString("APPOINTMENT_DATE");
            
            if (DOCTOR_ID != null && CLINIC_ID != null && APPOINTMENT_TIME != null && APPOINTMENT_DATE != null) {
                /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S CLIENTS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchDoctorClients();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE DOCTOR'S CLIENTS *****/
    private void fetchDoctorClients() {
        DoctorClientsAPI api = ZenApiClient.getClient().create(DoctorClientsAPI.class);
        Call<Clients> call = api.fetchDoctorClients(DOCTOR_ID);
        call.enqueue(new Callback<Clients>() {
            @Override
            public void onResponse(Call<Clients> call, Response<Clients> response) {
                arrClients = response.body().getClients();
                if (arrClients != null && arrClients.size() > 0)    {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT  */
                    listClients.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* CAST THE PRIMARY ARRAY DATA TO THE FILTERED ARRAY */
                    arrFilteredResults.addAll(arrClients);

                    /* INSTANTIATE THE CLIENTS SEARCH ADAPTER */
                    adapter = new ClientsSelectorAdapter(arrFilteredResults);

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
            public void onFailure(Call<Clients> call, Throwable t) {
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Select A Client";
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
        getMenuInflater().inflate(R.menu.activity_client_search, menu);
        MenuItem search = menu.findItem(R.id.menuSearchClients);
        searchView = (SearchView) search.getActionView();
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String search) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String search) {
        adapter.getFilter().filter(search);
        if (adapter.getItemCount() <= 0)    {
            /* SHOW THE EMPTY LAYOUT AND HIDE THE CLIENT RECYCLER VIEW */
            linlaEmpty.setVisibility(View.VISIBLE);
            listClients.setVisibility(View.GONE);
        } else if (adapter.getItemCount() > 0){
            /* SHOW THE CLIENT RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
            listClients.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
//            Log.e("RESULT", "Results found...");
        }
        return false;
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        listClients.setLayoutManager(manager);
        listClients.setHasFixedSize(true);
        listClients.setNestedScrollingEnabled(true);

        /* SET THE PATIENTS ADAPTER */
        listClients.setAdapter(adapter);
    }
    
    /***** THE CLIENT SELECTOR ADAPTER  *****/
    private class ClientsSelectorAdapter
            extends RecyclerView.Adapter<ClientsSelectorAdapter.ClientsVH>
            implements Filterable {

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<Client> mClients;
        private ArrayList<Client> mFilteredList;

        private ClientsSelectorAdapter(ArrayList<Client> mClients) {
            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.mClients = mClients;
            this.mFilteredList = mClients;
        }

        @Override
        public int getItemCount() {
            return mFilteredList.size();
        }

        @Override
        public void onBindViewHolder(ClientsVH holder, final int position) {
            final Client data = mFilteredList.get(position);

            /* CHECK IF A USER ID IS AVAILABLE */
            String userID = data.getUserID();
            if (userID == null) {
                /* SET THE CLIENTS'S NAME */
                if (data.getClientName() != null) {
                    holder.txtUserName.setText(data.getClientName());
                }

                /* SET THE CLIENTS'S PHONE */
                if (data.getClientPhoneNumber() != null && !data.getClientPhoneNumber().equalsIgnoreCase(""))   {
                    holder.txtUserPhone.setText(data.getClientPhoneNumber());
                }

                /* SET THE PLACEHOLDER IMAGE */
                ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ic_person_black_48dp).build();
                holder.imgvwUserProfile.setImageURI(request.getSourceUri());

            } else {
                /* SET THE USER'S DISPLAY PROFILE */
                if (data.getUserDisplayProfile() != null) {
                    Uri uri = Uri.parse(data.getUserDisplayProfile());
                    holder.imgvwUserProfile.setImageURI(uri);
                }

                /* SET THE USER'S NAME */
                if (data.getUserName() != null) {
                    holder.txtUserName.setText(data.getUserName());
                }

                /* SET THE USER'S PHONE */
                if (data.getUserPhoneNumber() != null && !data.getUserPhoneNumber().equalsIgnoreCase(""))   {
                    holder.txtUserPhone.setText(data.getUserPhoneNumber());
                }
            }

            /* SHOW THE CLIENT DETAILS */
            holder.cardClient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AppointmentClientSelector.this, AppointmentDetailsCreator.class);
                    intent.putExtra("CLIENT_ID", data.getClientID());
                    intent.putExtra("DOCTOR_ID", DOCTOR_ID);
                    intent.putExtra("CLINIC_ID", CLINIC_ID);
                    intent.putExtra("APPOINTMENT_TIME", APPOINTMENT_TIME);
                    intent.putExtra("APPOINTMENT_DATE", APPOINTMENT_DATE);
                    startActivity(intent);
                }
            });
        }

        @Override
        public ClientsVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.client_selector_item, parent, false);

            return new ClientsVH(itemView);
        }

        class ClientsVH extends RecyclerView.ViewHolder	{
            final CardView cardClient;
            final SimpleDraweeView imgvwUserProfile;
            final AppCompatTextView txtUserName;
            final AppCompatTextView txtUserPhone;

            ClientsVH(View v) {
                super(v);
                cardClient = v.findViewById(R.id.cardClient);
                imgvwUserProfile = v.findViewById(R.id.imgvwUserProfile);
                txtUserName = v.findViewById(R.id.txtUserName);
                txtUserPhone = v.findViewById(R.id.txtUserPhone);
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charString = constraint.toString();

                    if (charString.isEmpty()) {
                        mFilteredList = mClients;
                    } else {
                        ArrayList<Client> filteredList = new ArrayList<>();
                        for (Client data : mClients) {
                            if (data.getUserID() != null)   {
                                if (data.getUserName().toLowerCase().contains(charString)) {
                                    filteredList.add(data);
                                }
                            } else {
                                if (data.getClientName().toLowerCase().contains(charString)) {
                                    filteredList.add(data);
                                }
                            }
                        }
                        mFilteredList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mFilteredList = (ArrayList<Client>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}