package biz.zenpets.trainers.utils.adapters.enquiries;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.details.trainers.TrainerEnquiryActivity;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.enquiries.EnquiriesAPI;
import biz.zenpets.trainers.utils.models.trainers.enquiries.Enquiry;
import biz.zenpets.trainers.utils.models.trainers.enquiries.MessagesCount;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingEnquiriesAdapter extends RecyclerView.Adapter<TrainingEnquiriesAdapter.EnquiriesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Enquiry> arrEnquiries;

    public TrainingEnquiriesAdapter(Activity activity, ArrayList<Enquiry> arrEnquiries) {

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrEnquiries = arrEnquiries;
    }

    @Override
    public int getItemCount() {
        return arrEnquiries.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final EnquiriesVH holder, int position) {
        final Enquiry data = arrEnquiries.get(position);

        /* SET THE TRAINING MODULE */
        String trainingModule = data.getTrainerModuleName();
        String sourceString = "<b>" + trainingModule + "</b>";
        if (sourceString != null) {
            holder.txtTrainingModule.setText(Html.fromHtml(activity.getString(R.string.te_training_module_placeholder, sourceString)));
        }

        /* FETCH THE LATEST MESSAGE RECEIVED FROM THE USERS */
        String trainingMasterID = data.getTrainingMasterID();
        String userID = data.getUserID();
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiry> call = api.fetchLastTrainingEnquiryDetails(trainingMasterID, userID);
        call.enqueue(new Callback<Enquiry>() {
            @Override
            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
                Enquiry enquiry = response.body();
                if (!enquiry.getError())    {
                    /* GET AND SET THE ENQUIRY MESSAGE */
                    String trainingSlaveMessage = enquiry.getTrainingSlaveMessage();
                    if (trainingSlaveMessage != null)   {
//                        Log.e("MESSAGE", trainingSlaveMessage);
                        holder.txtUsersMessage.setText(trainingSlaveMessage);
                        holder.txtUsersMessage.setTextColor(ContextCompat.getColor(activity, android.R.color.primary_text_light));
                    }

                    /* GET AND SET THE ENQUIRY MESSAGE TIME STAMP */
                    String messageTimeStamp = enquiry.getTrainerSlaveTimestamp();
//                    Log.e("TS", messageTimeStamp);
                    long lngTimeStamp = Long.parseLong(messageTimeStamp) * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    Date date = calendar.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    holder.txtUserTimeStamp.setText(prettyTime.format(date));
                } else {
                    /* SET THE EMPTY MESSAGE TEXT */
                    holder.txtUsersMessage.setText("The User has opened an Enquiry window for the Training Module but hasn't sent a message yet...");
                    holder.txtUsersMessage.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));

                    /* SET THE EMPTY TIME STAMP */
                    holder.txtUserTimeStamp.setText("N.A.");
                }
            }

            @Override
            public void onFailure(Call<Enquiry> call, Throwable t) {
                Log.e("LATEST FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });

        /* SET THE USER'S DISPLAY PROFILE */
        String strUserDisplayProfile = data.getUserDisplayProfile();
        if (strUserDisplayProfile != null)    {
            Uri uri = Uri.parse(strUserDisplayProfile);
            holder.imgvwUserDisplayProfile.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.user_placeholder)
                    .build();
            holder.imgvwUserDisplayProfile.setImageURI(request.getSourceUri());
        }

        /* SET THE USER'S NAME */
        String strUserName = data.getUserName();
        if (strUserName != null)    {
            holder.txtUserName.setText(strUserName);
        }


        /* GET THE NUMBER OF MESSAGES BY THE ENQUIRING USERS */
        api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<MessagesCount> callCount = api.fetchEnquiryUserMessagesCount(trainingMasterID, userID);
        callCount.enqueue(new Callback<MessagesCount>() {
            @Override
            public void onResponse(Call<MessagesCount> call, Response<MessagesCount> response) {
                MessagesCount count = response.body();
                if (count != null)  {
                    int messageCount = Integer.parseInt(count.getTotalMessages());
                    Resources resModules = AppPrefs.context().getResources();
                    String strFinalCount = null;
                    if (messageCount == 0)   {
                        strFinalCount = resModules.getQuantityString(R.plurals.enquiry_message, messageCount, messageCount);
                    } else if (messageCount == 1)    {
                        strFinalCount = resModules.getQuantityString(R.plurals.enquiry_message, messageCount, messageCount);
                    } else if (messageCount > 1) {
                        strFinalCount = resModules.getQuantityString(R.plurals.enquiry_message, messageCount, messageCount);
                    }
                    holder.txtMessageCount.setText(activity.getString(R.string.te_message_count_placeholder, strFinalCount));
                } else {
                    holder.txtMessageCount.setText(activity.getString(R.string.te_message_count_zero_messages));
                }
            }

            @Override
            public void onFailure(Call<MessagesCount> call, Throwable t) {
                Log.e("COUNT FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });

        /* SHOW THE ENQUIRY CONVERSATION AND DETAILS */
        holder.cardEnquiryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TrainerEnquiryActivity.class);
                intent.putExtra("TRAINER_ID", data.getTrainerID());
                intent.putExtra("MODULE_ID", data.getTrainerModuleID());
                intent.putExtra("TRAINING_MASTER_ID", data.getTrainingMasterID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public EnquiriesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.enquiries_fragment_item, parent, false);

        return new EnquiriesVH(itemView);
    }

    class EnquiriesVH extends RecyclerView.ViewHolder   {
        CardView cardEnquiryContainer;
        TextView txtTrainingModule;
        TextView txtUsersMessage;
        SimpleDraweeView imgvwUserDisplayProfile;
        TextView txtUserName;
        TextView txtUserTimeStamp;
        TextView txtMessageCount;

        EnquiriesVH(View v) {
            super(v);
            cardEnquiryContainer = v.findViewById(R.id.cardEnquiryContainer);
            txtTrainingModule = v.findViewById(R.id.txtTrainingModule);
            txtUsersMessage = v.findViewById(R.id.txtUsersMessage);
            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtUserTimeStamp = v.findViewById(R.id.txtUserTimeStamp);
            txtMessageCount = v.findViewById(R.id.txtMessageCount);
        }
    }
}