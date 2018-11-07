package co.zenpets.groomers.details.enquiry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.groomers.R;
import co.zenpets.groomers.utils.adapters.enquiry.GroomerEnquiryMessagesAdapter;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.enquiries.EnquiryMessage;
import co.zenpets.groomers.utils.models.enquiries.EnquiryMessages;
import co.zenpets.groomers.utils.models.enquiries.EnquiryMessagesAPI;
import retrofit2.Call;
import retrofit2.Response;

public class EnquiryDetails extends AppCompatActivity {

    /** THE INCOMING ENQUIRY ID AND USER ID **/
    String ENQUIRY_ID = null;
    String USER_ID = null;

    /** THE MESSAGES ARRAY LIST **/
    private ArrayList<EnquiryMessage> arrMessages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwGroomerLogo) SimpleDraweeView imgvwGroomerLogo;
    @BindView(R.id.txtGroomerName) TextView txtGroomerName;
    @BindView(R.id.linlaMessagesProgress) LinearLayout linlaMessagesProgress;
    @BindView(R.id.listMessages) RecyclerView listMessages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.edtMessage) EditText edtMessage;
    @BindView(R.id.imgbtnPostMessage) ImageButton imgbtnPostMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enquiry_details);
        ButterKnife.bind(this);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /* FETCH THE ENQUIRY MESSAGES */
    private void fetchEnquiryMessages() {
        EnquiryMessagesAPI api = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
        Call<EnquiryMessages> call = api.fetchGroomerEnquiryMessages(ENQUIRY_ID);
        call.enqueue(new retrofit2.Callback<EnquiryMessages>() {
            @Override
            public void onResponse(Call<EnquiryMessages> call, Response<EnquiryMessages> response) {
                Log.e("MESSAGES RESPONSE", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getMessages() != null)   {
                    arrMessages = response.body().getMessages();
                    if (arrMessages.size() > 0) {

                        /* SET THE ADAPTER */
                        listMessages.setAdapter(new GroomerEnquiryMessagesAdapter(EnquiryDetails.this, arrMessages));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listMessages.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listMessages.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listMessages.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<EnquiryMessages> call, Throwable t) {
//                Log.e("MESSAGES FAILURE", t.getMessage());

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("ENQUIRY_ID") && bundle.containsKey("USER_ID"))    {
            ENQUIRY_ID = bundle.getString("ENQUIRY_ID");
            USER_ID = bundle.getString("USER_ID");
            if (ENQUIRY_ID != null && USER_ID != null)  {
                /* FETCH THE ENQUIRY MESSAGES */
                fetchEnquiryMessages();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setStackFromEnd(true);
        listMessages.setLayoutManager(manager);
        listMessages.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(listMessages, false);

        /* SET THE ADAPTER */
        listMessages.setAdapter(new GroomerEnquiryMessagesAdapter(EnquiryDetails.this, arrMessages));
    }
}