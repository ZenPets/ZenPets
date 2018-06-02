package biz.zenpets.users.details.kennels.reviews;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.kennels.KennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelReviewCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE INCOMING KENNEL ID AND THE KENNEL OWNER'S ID **/
    private String KENNEL_ID = null;
    private String KENNEL_OWNER_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwKennelCoverPhoto) SimpleDraweeView imgvwKennelCoverPhoto;
    @BindView(R.id.txtKennelName) TextView txtKennelName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_review_creator);
        ButterKnife.bind(this);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* CONFIGURE THE TOOLBAR **/
        configTB();

        /* GET THE INCOMING DATA **/
        getIncomingData();
    }

    /** FETCH THE KENNEL'S DETAILS **/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.fetchKennelDetails(KENNEL_ID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                if (!response.body().getError()) {
                    Kennel kennel = response.body();

                    /* GET AND SET THE KENNEL NAME */
                    String kennelName = kennel.getKennelName();
                    txtKennelName.setText(kennelName);

                    /* GET AND SET THE KENNEL COVER PHOTO */
                    String kennelCoverPhoto = kennel.getKennelCoverPhoto();
                    if (kennelCoverPhoto != null
                            && !kennelCoverPhoto.equalsIgnoreCase("")
                            && !kennelCoverPhoto.equalsIgnoreCase("null"))    {
                        Uri uriClinic = Uri.parse(kennelCoverPhoto);
                        imgvwKennelCoverPhoto.setImageURI(uriClinic);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                                .build();
                        imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("KENNEL_ID") && bundle.containsKey("KENNEL_OWNER_ID"))   {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            KENNEL_OWNER_ID = bundle.getString("KENNEL_OWNER_ID");
            if (KENNEL_ID != null)  {
                /* FETCH THE KENNEL'S DETAILS */
                fetchKennelDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Post A Review";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(getApplicationContext());
        inflater.inflate(R.menu.activity_review_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSubmit:
                /* VALIDATE REVIEW DATA */
                validateData();
                break;
            default:
                break;
        }
        return false;
    }

    /** VALIDATE REVIEW DATA **/
    private void validateData() {
    }
}