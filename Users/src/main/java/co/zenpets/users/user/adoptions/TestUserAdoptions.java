package co.zenpets.users.user.adoptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.creator.adoption.AdoptionCreatorNew;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.adoptions.user.TestUserAdoptionsAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.adoptions.adoption.Adoption;
import co.zenpets.users.utils.models.adoptions.adoption.Adoptions;
import co.zenpets.users.utils.models.adoptions.adoption.AdoptionsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestUserAdoptions extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE ADOPTION ADAPTER AND ARRAY LISTS **/
    private ArrayList<Adoption> arrAdoptions = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listAdoptions) RecyclerView listAdoptions;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** CREATE A NEW ADOPTION LISTING **/
    @OnClick(R.id.linlaEmpty) void newAdoptionListing()    {
        Intent intent = new Intent(TestUserAdoptions.this, AdoptionCreatorNew.class);
        startActivityForResult(intent, 101);
    }

    /** CREATE A NEW ADOPTION LISTING **/
    @OnClick(R.id.fabNewAdoptionListing) void fabNewAdoptionListing()  {
        Intent intent = new Intent(TestUserAdoptions.this, AdoptionCreatorNew.class);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_adoptions_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
        if (USER_ID != null) {
            /* SHOW THE PROGRESS AND FETCH THE USER'S ADOPTIONS*/
            arrAdoptions.clear();
            linlaProgress.setVisibility(View.VISIBLE);
            fetchUserAdoptions();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
        }
    }

    /* FETCH THE USER'S ADOPTION LISTINGS */
    private void fetchUserAdoptions() {
        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<Adoptions> call = api.listTestUserAdoptions(USER_ID);
        call.enqueue(new Callback<Adoptions>() {
            @Override
            public void onResponse(Call<Adoptions> call, Response<Adoptions> response) {
                if (response.body() != null && response.body().getAdoptions() != null)  {
                    arrAdoptions = response.body().getAdoptions();

                    /* CHECK THE ARRAY LIST SIZE AND SHOW THE APPROPRIATE LAYOUT */
                    if (arrAdoptions.size() > 0)    {
                        /* SET THE ADAPTER TO THE ADOPTIONS RECYCLER */
                        listAdoptions.setAdapter(new TestUserAdoptionsAdapter(TestUserAdoptions.this, arrAdoptions));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listAdoptions.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listAdoptions.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listAdoptions.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS BAR AFTER LOADING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Adoptions> call, Throwable t) {
//                Log.e("ADOPTIONS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "My Adoption Listings";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)  {
            /* CLEAR THE ARRAY LIST */
            arrAdoptions.clear();

            /* SHOW THE PROGRESS AND FETCH THE USER'S ADOPTIONS*/
            linlaProgress.setVisibility(View.VISIBLE);
            fetchUserAdoptions();
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listAdoptions.setLayoutManager(manager);
        listAdoptions.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listAdoptions.setAdapter(new TestUserAdoptionsAdapter(TestUserAdoptions.this, arrAdoptions));
    }
}