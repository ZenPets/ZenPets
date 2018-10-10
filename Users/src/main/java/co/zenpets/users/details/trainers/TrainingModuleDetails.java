package co.zenpets.users.details.trainers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.trainers.modules.Module;
import co.zenpets.users.utils.models.trainers.modules.ModuleImage;
import co.zenpets.users.utils.models.trainers.modules.ModuleImages;
import co.zenpets.users.utils.models.trainers.modules.ModuleImagesAPI;
import co.zenpets.users.utils.models.trainers.modules.ModulesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingModuleDetails extends AppCompatActivity {

    /** THE INCOMING MODULE ID **/
    private String MODULE_ID = null;

    /** THE TRAINING MODULE IMAGES ARRAY LIST **/
    private ArrayList<ModuleImage> arrImages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.appBar) AppBarLayout appBar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
//    @BindView(R.id.txtModuleName) AppCompatTextView txtModuleName;
//    @BindView(R.id.txtModuleDuration) AppCompatTextView txtModuleDuration;
//    @BindView(R.id.txtModuleDetails) AppCompatTextView txtModuleDetails;
//    @BindView(R.id.imgvwModuleFormat) AppCompatImageView imgvwModuleFormat;
//    @BindView(R.id.txtModuleFormat) AppCompatTextView txtModuleFormat;
//    @BindView(R.id.txtModuleFees) AppCompatTextView txtModuleFees;
//    @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
    @BindView(R.id.listModuleImages) RecyclerView listModuleImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_module_details);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE APP BAR LAYOUT */
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            /* BOOLEAN TO TRACK IF TOOLBAR IS SHOWING */
            boolean isShow = false;

            /* SCROLL RANGE */
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1)  {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    toolbarLayout.setTitleEnabled(true);
                } else if (isShow) {
                    isShow = false;
                    toolbarLayout.setTitleEnabled(false);
                }
            }
        });

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /***** FETCH THE MODULE DETAILS *****/
    private void fetchModuleDetails() {
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Module> call = api.fetchModuleDetails(MODULE_ID);
        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
//                Log.e("DETAILS RAW", String.valueOf(response.raw()));

                /* FETCH THE TRAINING MODULE IMAGES */
                fetchTrainingModuleImages();
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TRAINING MODULE IMAGES *****/
    private void fetchTrainingModuleImages() {
        ModuleImagesAPI apiImages = ZenApiClient.getClient().create(ModuleImagesAPI.class);
        Call<ModuleImages> callImages = apiImages.fetchTrainingModuleImages(MODULE_ID);
        callImages.enqueue(new Callback<ModuleImages>() {
            @Override
            public void onResponse(Call<ModuleImages> call, Response<ModuleImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0)   {
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ModuleImages> call, Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("MODULE_ID"))  {
            MODULE_ID = bundle.getString("MODULE_ID");
            if (MODULE_ID != null)  {
                /* FETCH THE MODULE DETAILS */
                fetchModuleDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required data...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required data...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(TrainingModuleDetails.this);
        inflater.inflate(R.menu.activity_trainer_details, menu);
        return super.onCreateOptionsMenu(menu);
    }
}