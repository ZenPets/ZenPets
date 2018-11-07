package co.zenpets.groomers.utils.adapters.enquiry;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.groomers.R;
import co.zenpets.groomers.details.enquiry.EnquiryDetails;
import co.zenpets.groomers.utils.AppPrefs;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.enquiries.EnquiriesAPI;
import co.zenpets.groomers.utils.models.enquiries.Enquiry;
import co.zenpets.groomers.utils.models.enquiries.Enquiry;
import co.zenpets.groomers.utils.models.enquiries.UnreadCount;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomerEnquiryAdapter extends RecyclerView.Adapter<GroomerEnquiryAdapter.EnquiriesVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Enquiry> arrMessages;

    public GroomerEnquiryAdapter(Activity activity, ArrayList<Enquiry> arrMessages) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrMessages = arrMessages;
    }

    @Override
    public int getItemCount() {
        return arrMessages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final EnquiriesVH holder, final int position) {
        final Enquiry data = arrMessages.get(position);

        /* SET THE USER'S DISPLAY PROFILE */
        final String userDisplayProfile = data.getUserDisplayProfile();
        if (userDisplayProfile != null) {
            Picasso.with(activity)
                    .load(userDisplayProfile)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .noFade()
                    .resize(400, 400)
                    .into(holder.imgvwUserDisplayProfile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(userDisplayProfile)
                                    .noFade()
                                    .error(R.drawable.ic_person_black_24dp)
                                    .into(holder.imgvwUserDisplayProfile, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
//                                            Log.e("Picasso","Could not fetch image");
                                        }
                                    });
                        }
                    });
        }

        /* SET THE USER NAME */
        if (data.getUserName() != null) {
            holder.txtUserName.setText(data.getUserName());
        }

        /* FETCH THE LATEST MESSAGE RECEIVED FROM THE USERS */
        String enquiryID = data.getEnquiryID();
        String userID = data.getUserID();
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiry> call = api.fetchLastGroomerEnquiryDetails(enquiryID, userID);
        call.enqueue(new Callback<Enquiry>() {
            @Override
            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
                Log.e("LATEST REPONSE", String.valueOf(response.raw()));
                Enquiry enquiry = response.body();
                if (!enquiry.getError())    {
                    /* GET AND SET THE ENQUIRY MESSAGE */
                    String enquiryMessage = enquiry.getEnquiryMessage();
                    holder.txtEnquiryMessage.setText(enquiryMessage);

                    /* GET AND SET THE ENQUIRY MESSAGE TIME STAMP */
                    String enquiryTimestamp = enquiry.getEnquiryTimestamp();
                    long lngTimeStamp = Long.parseLong(enquiryTimestamp) * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    Date date = calendar.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    holder.txtTimeStamp.setText(prettyTime.format(date));
                }
            }

            @Override
            public void onFailure(Call<Enquiry> call, Throwable t) {
//                Log.e("LATEST FAILURE", t.getMessage());
            }
        });

        /* GET THE NUMBER OF MESSAGES BY THE ENQUIRING USERS */
        api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<UnreadCount> callCount = api.fetchGroomerEnquiryUnreadCount(enquiryID, userID);
        callCount.enqueue(new Callback<UnreadCount>() {
            @Override
            public void onResponse(Call<UnreadCount> call, Response<UnreadCount> response) {
                Log.e("COUNT RESPONSE", String.valueOf(response.raw()));
                UnreadCount count = response.body();
                if (count != null)  {
                    int messageCount = Integer.parseInt(count.getUnreadMessages());
                    Resources resModules = AppPrefs.context().getResources();
                    String strFinalCount = null;
                    if (messageCount == 0)   {
                        strFinalCount = resModules.getQuantityString(R.plurals.unread_enquiry_messages, messageCount, messageCount);
                    } else if (messageCount == 1)    {
                        strFinalCount = resModules.getQuantityString(R.plurals.unread_enquiry_messages, messageCount, messageCount);
                    } else if (messageCount > 1) {
                        strFinalCount = resModules.getQuantityString(R.plurals.unread_enquiry_messages, messageCount, messageCount);
                    }
                    holder.txtUnreadCount.setText(activity.getString(R.string.ge_unread_message_count_placeholder, strFinalCount));
                } else {
                    holder.txtUnreadCount.setText(activity.getString(R.string.ge_unread_message_count_placeholder));
                }
            }

            @Override
            public void onFailure(Call<UnreadCount> call, Throwable t) {
//                Log.e("UNREAD COUNT FAILURE", t.getMessage());
            }
        });
//
        /* SHOW THE ENQUIRY CONVERSATION AND DETAILS */
        holder.enquiryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EnquiryDetails.class);
                intent.putExtra("ENQUIRY_ID", data.getEnquiryID());
                intent.putExtra("USER_ID", data.getUserID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public EnquiriesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.groomer_enquiries_item, parent, false);

        return new EnquiriesVH(itemView);
    }

    class EnquiriesVH extends RecyclerView.ViewHolder	{
        ConstraintLayout enquiryContainer;
        CircleImageView imgvwUserDisplayProfile;
        TextView txtUserName;
        TextView txtTimeStamp;
        TextView txtEnquiryMessage;
        TextView txtUnreadCount;

        EnquiriesVH(View v) {
            super(v);
            enquiryContainer = v.findViewById(R.id.enquiryContainer);
            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
            txtEnquiryMessage = v.findViewById(R.id.txtEnquiryMessage);
            txtUnreadCount = v.findViewById(R.id.txtUnreadCount);
        }
    }
}