package biz.zenpets.kennels.utils.adapters.enquiries;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.models.enquiries.Enquiry;
import de.hdodenhof.circleimageview.CircleImageView;

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
                    .into(holder.imgvwUserDisplayProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(userDisplayProfile)
                                    .noFade()
                                    .error(R.drawable.ic_person_black_24dp)
                                    .into(holder.imgvwUserDisplayProfile, new Callback() {
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

//        /* SET THE TRAINING MODULE */
//        String trainingModule = data.getTrainerModuleName();
//        String sourceString = "<b>" + trainingModule + "</b>";
//        if (sourceString != null) {
//            holder.imgvwUserDisplayProfile.setText(Html.fromHtml(activity.getString(R.string.te_training_module_placeholder, sourceString)));
//        }
//
//        /* FETCH THE LATEST MESSAGE RECEIVED FROM THE USERS */
//        String trainingMasterID = data.getTrainingMasterID();
//        String userID = data.getUserID();
//        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
//        Call<Enquiry> call = api.fetchLastTrainingEnquiryDetails(trainingMasterID, userID);
//        call.enqueue(new Callback<Enquiry>() {
//            @Override
//            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
//                Enquiry enquiry = response.body();
//                if (!enquiry.getError())    {
//                    /* GET AND SET THE ENQUIRY MESSAGE */
//                    String trainingSlaveMessage = enquiry.getTrainingSlaveMessage();
//                    if (trainingSlaveMessage != null)   {
////                        Log.e("MESSAGE", trainingSlaveMessage);
//                        holder.txtUserName.setText(trainingSlaveMessage);
//                        holder.txtUserName.setTextColor(ContextCompat.getColor(activity, android.R.color.primary_text_light));
//                    }
//
//                    /* GET AND SET THE ENQUIRY MESSAGE TIME STAMP */
//                    String messageTimeStamp = enquiry.getTrainerSlaveTimestamp();
////                    Log.e("TS", messageTimeStamp);
//                    long lngTimeStamp = Long.parseLong(messageTimeStamp) * 1000;
//                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
//                    calendar.setTimeInMillis(lngTimeStamp);
//                    Date date = calendar.getTime();
//                    PrettyTime prettyTime = new PrettyTime();
//                    holder.txtUnreadCount.setText(prettyTime.format(date));
//                } else {
//                    /* SET THE EMPTY MESSAGE TEXT */
//                    holder.txtUserName.setText("The User has opened an Enquiry window for the Training Module but hasn't sent a message yet...");
//                    holder.txtUserName.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
//
//                    /* SET THE EMPTY TIME STAMP */
//                    holder.txtUnreadCount.setText("N.A.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Enquiry> call, Throwable t) {
////                Log.e("LATEST FAILURE", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//
//        /* SET THE USER'S DISPLAY PROFILE */
//        String strUserDisplayProfile = data.getUserDisplayProfile();
//        if (strUserDisplayProfile != null)    {
//            Uri uri = Uri.parse(strUserDisplayProfile);
//            holder.imgvwUserDisplayProfile.setImageURI(uri);
//        } else {
//            ImageRequest request = ImageRequestBuilder
//                    .newBuilderWithResourceId(R.drawable.user_placeholder)
//                    .build();
//            holder.imgvwUserDisplayProfile.setImageURI(request.getSourceUri());
//        }
//
//        /* SET THE USER'S NAME */
//        String strUserName = data.getUserName();
//        if (strUserName != null)    {
//            holder.txtUserName.setText(strUserName);
//        }
//
//
//        /* GET THE NUMBER OF MESSAGES BY THE ENQUIRING USERS */
//        api = ZenApiClient.getClient().create(EnquiriesAPI.class);
//        Call<MessagesCount> callCount = api.fetchEnquiryUserMessagesCount(trainingMasterID, userID);
//        callCount.enqueue(new Callback<MessagesCount>() {
//            @Override
//            public void onResponse(Call<MessagesCount> call, Response<MessagesCount> response) {
//                MessagesCount count = response.body();
//                if (count != null)  {
//                    int messageCount = Integer.parseInt(count.getTotalMessages());
//                    Resources resModules = AppPrefs.context().getResources();
//                    String strFinalCount = null;
//                    if (messageCount == 0)   {
//                        strFinalCount = resModules.getQuantityString(R.plurals.enquiry_message, messageCount, messageCount);
//                    } else if (messageCount == 1)    {
//                        strFinalCount = resModules.getQuantityString(R.plurals.enquiry_message, messageCount, messageCount);
//                    } else if (messageCount > 1) {
//                        strFinalCount = resModules.getQuantityString(R.plurals.enquiry_message, messageCount, messageCount);
//                    }
//                    holder.txtMessageCount.setText(activity.getString(R.string.te_message_count_placeholder, strFinalCount));
//                } else {
//                    holder.txtMessageCount.setText(activity.getString(R.string.te_message_count_zero_messages));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MessagesCount> call, Throwable t) {
////                Log.e("COUNT FAILURE", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//
//        /* SHOW THE ENQUIRY CONVERSATION AND DETAILS */
//        holder.cardEnquiryDetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, TrainerEnquiryActivity.class);
//                intent.putExtra("TRAINER_ID", data.getTrainerID());
//                intent.putExtra("MODULE_ID", data.getTrainerModuleID());
//                intent.putExtra("TRAINING_MASTER_ID", data.getTrainingMasterID());
//                activity.startActivity(intent);
//            }
//        });
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
        CardView cardEnquiryDetails;
        CircleImageView imgvwUserDisplayProfile;
        TextView txtUserName;
        TextView txtTimeStamp;
        TextView txtEnquiryMessage;
        TextView txtUnreadCount;

        EnquiriesVH(View v) {
            super(v);
            cardEnquiryDetails = v.findViewById(R.id.cardEnquiryDetails);
            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
            txtEnquiryMessage = v.findViewById(R.id.txtEnquiryMessage);
            txtUnreadCount = v.findViewById(R.id.txtUnreadCount);
        }
    }
}