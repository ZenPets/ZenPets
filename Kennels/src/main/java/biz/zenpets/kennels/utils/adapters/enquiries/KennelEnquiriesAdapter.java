package biz.zenpets.kennels.utils.adapters.enquiries;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.enquiry.KennelEnquiryActivity;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.models.enquiries.EnquiriesAPI;
import biz.zenpets.kennels.utils.models.enquiries.Enquiry;
import biz.zenpets.kennels.utils.models.enquiries.UnreadCount;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelEnquiriesAdapter extends RecyclerView.Adapter<KennelEnquiriesAdapter.EnquiriesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Enquiry> arrEnquiries;

    public KennelEnquiriesAdapter(Activity activity, ArrayList<Enquiry> arrEnquiries) {

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
        String kennelEnquiryID = data.getKennelEnquiryID();
        String userID = data.getUserID();
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiry> call = api.fetchLastKennelEnquiryDetails(kennelEnquiryID, userID);
        call.enqueue(new Callback<Enquiry>() {
            @Override
            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
                Enquiry enquiry = response.body();
                if (!enquiry.getError())    {
                    /* GET AND SET THE ENQUIRY MESSAGE */
                    String kennelEnquiryMessage = enquiry.getKennelEnquiryMessage();
                    holder.txtEnquiryMessage.setText(kennelEnquiryMessage);

                    /* GET AND SET THE ENQUIRY MESSAGE TIME STAMP */
                    String messageTimeStamp = enquiry.getKennelEnquiryTimestamp();
                    long lngTimeStamp = Long.parseLong(messageTimeStamp) * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    Date date = calendar.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    holder.txtTimeStamp.setText(prettyTime.format(date));
                }
            }

            @Override
            public void onFailure(Call<Enquiry> call, Throwable t) {
                Log.e("LATEST FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });

        /* GET THE NUMBER OF MESSAGES BY THE ENQUIRING USERS */
        api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<UnreadCount> callCount = api.fetchKennelEnquiryUnreadCount(kennelEnquiryID, userID);
        callCount.enqueue(new Callback<UnreadCount>() {
            @Override
            public void onResponse(Call<UnreadCount> call, Response<UnreadCount> response) {
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
                    holder.txtUnreadCount.setText(activity.getString(R.string.ke_unread_message_count_placeholder, strFinalCount));
                } else {
                    holder.txtUnreadCount.setText(activity.getString(R.string.ke_unread_message_count_placeholder));
                }
            }

            @Override
            public void onFailure(Call<UnreadCount> call, Throwable t) {
                Log.e("UNREAD COUNT FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
//
        /* SHOW THE ENQUIRY CONVERSATION AND DETAILS */
        holder.enquiryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, KennelEnquiryActivity.class);
                intent.putExtra("KENNEL_ID", data.getKennelID());
                intent.putExtra("KENNEL_NAME", data.getKennelName());
                intent.putExtra("ENQUIRY_ID", data.getKennelEnquiryID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public EnquiriesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.enquiries_item, parent, false);

        return new EnquiriesVH(itemView);
    }

    class EnquiriesVH extends RecyclerView.ViewHolder   {
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