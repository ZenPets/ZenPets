package co.zenpets.doctors.utils.helpers.classes.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.location.CountriesData;
import co.zenpets.doctors.utils.models.location.CountryData;
import co.zenpets.doctors.utils.models.location.LocationAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class CountrySelectorActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /** THE COUNTRIES ADAPTER AND ARRAY LIST INSTANCE **/
    private CountriesListAdapter adapter;
    private ArrayList<CountryData> arrCountries = new ArrayList<>();
    private final ArrayList<CountryData> arrFilteredResults = new ArrayList<>();

    /** THE SEARCH VIEW INSTANCE **/
    private SearchView searchView;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listCountries) RecyclerView listCountries;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_selector_list);
        ButterKnife.bind(this);

        /* INSTANTIATE THE ADAPTER */
        adapter = new CountriesListAdapter(arrCountries);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* FETCH THE LIST OF COUNTRIES */
        fetchCountries();
    }

    /***** FETCH THE LIST OF COUNTRIES *****/
    private void fetchCountries() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<CountriesData> call = api.allCountries();
        call.enqueue(new Callback<CountriesData>() {
            @Override
            public void onResponse(Call<CountriesData> call, Response<CountriesData> response) {
                if (response.body() != null && response.body().getCountries() != null)  {
                    arrCountries = response.body().getCountries();
                    if (arrCountries.size() > 0)    {

                        /* CAST THE PRIMARY ARRAY DATA TO THE FILTERED ARRAY */
                        arrFilteredResults.addAll(arrCountries);

                        /* INSTANTIATE THE COUNTRY SEARCH ADAPTER */
                        adapter = new CountriesListAdapter(arrFilteredResults);

                        /* SET THE ADAPTER TO THE COUNTRIES RECYCLER VIEW */
                        listCountries.setAdapter(adapter);

                        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getApplicationContext(), "An error occurred fetching the list of countries", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "An error occurred fetching the list of countries", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CountriesData> call, Throwable t) {
//                Log.e("COUNTRIES FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Select Your Country";
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
        getMenuInflater().inflate(R.menu.activity_country_search, menu);
        MenuItem search = menu.findItem(R.id.menuSearchCountries);
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

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listCountries.setLayoutManager(manager);
        listCountries.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listCountries.setAdapter(adapter);
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

    /***** THE COUNTRIES ADAPTER *****/
    private class CountriesListAdapter
            extends RecyclerView.Adapter<CountriesListAdapter.CountriesVH>
            implements Filterable {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<CountryData> mArrayList;
        private ArrayList<CountryData> mFilteredList;

        private CountriesListAdapter(ArrayList<CountryData> arrCountries) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.mArrayList = arrCountries;
            this.mFilteredList = arrCountries;
        }

        @Override
        public int getItemCount() {
            return mFilteredList.size();
        }

        @Override
        public void onBindViewHolder(final CountriesVH holder, final int position) {
            final CountryData data = mFilteredList.get(position);

            /* SET THE COUNTRY NAME */
            if (data.getCountryName() != null)
                holder.txtCountryName.setText(data.getCountryName());

            /* SET THE CURRENCY SYMBOL */
            if (data.getCurrencySymbol() != null)
                holder.txtCurrencySymbol.setText(data.getCurrencySymbol());

            /* SEND THE SELECTED COUNTRY BACK TO THE CALLING ACTIVITY */
            holder.txtCountryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchView.clearFocus();
                    Intent intent = new Intent();
                    intent.putExtra("COUNTRY_ID", data.getCountryID());
                    intent.putExtra("COUNTRY_NAME", data.getCountryName());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public CountriesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.country_row, parent, false);

            return new CountriesVH(itemView);
        }

        class CountriesVH extends RecyclerView.ViewHolder	{
            final AppCompatTextView txtCountryName;
            final AppCompatTextView txtCurrencySymbol;
            CountriesVH (View v) {
                super(v);
                txtCountryName = v.findViewById(R.id.txtCountryName);
                txtCurrencySymbol = v.findViewById(R.id.txtCurrencySymbol);
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String charString = constraint.toString();

                    if (charString.isEmpty()) {
                        mFilteredList = mArrayList;
                    } else {
                        ArrayList<CountryData> filteredList = new ArrayList<>();
                        for (CountryData data : mArrayList) {
                            if (data.getCountryName().toLowerCase().contains(charString)) {
                                filteredList.add(data);
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
                    mFilteredList = (ArrayList<CountryData>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}