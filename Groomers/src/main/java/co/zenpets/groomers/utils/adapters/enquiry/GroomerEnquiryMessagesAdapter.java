package co.zenpets.groomers.utils.adapters.enquiry;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.groomers.R;
import co.zenpets.groomers.utils.models.enquiries.EnquiryMessage;

public class GroomerEnquiryMessagesAdapter extends RecyclerView.Adapter<GroomerEnquiryMessagesAdapter.MessagesVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<EnquiryMessage> arrMessages;

    public GroomerEnquiryMessagesAdapter(Activity activity, ArrayList<EnquiryMessage> arrMessages) {

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
    public void onBindViewHolder(@NonNull final MessagesVH holder, final int position) {
        EnquiryMessage message = arrMessages.get(position);

        /* CHECK IF THE MESSAGE IS FROM THE GROOMER OR THE USER */
        if (message.getGroomerID() != null) {
            /* SHOW THE TRAINER LAYOUT AND HIDE THE USER LAYOUT */
            holder.linlaTrainer.setVisibility(View.VISIBLE);
            holder.linlaUser.setVisibility(View.GONE);

            /* SET HE GROOMER'S LOGO */
            String groomerLogo = message.getGroomerLogo();
            if (groomerLogo != null)    {
                Uri uri = Uri.parse(groomerLogo);
                holder.imgvwTrainerDisplayProfile.setImageURI(uri);
            } else {
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                        .build();
                holder.imgvwTrainerDisplayProfile.setImageURI(request.getSourceUri());
            }

            /* SET THE ENQUIRY MESSAGE */
            String enquiryMessage = message.getEnquiryMessage();
            if (enquiryMessage != null)    {
                holder.txtTrainersMessage.setText(enquiryMessage);
            }

            /* SET THE GROOMER'S NAME */
            String groomerName = message.getGroomerName();
            if (groomerName != null)    {
                holder.txtTrainerName.setText(groomerName);
            }

            /* SET THE ENQUIRY MESSAGE TIME STAMP */
            if (message.getEnquiryTimestamp() != null) {
                String enquiryTimestamp = message.getEnquiryTimestamp();
                long lngTimeStamp = Long.parseLong(enquiryTimestamp) * 1000;
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(lngTimeStamp);
                Date date = calendar.getTime();
                PrettyTime prettyTime = new PrettyTime();
                holder.txtTrainerTimeStamp.setText(prettyTime.format(date));
            }
        } else {
            /* SHOW THE USER LAYOUT AND HIDE THE TRAINER LAYOUT */
            holder.linlaUser.setVisibility(View.VISIBLE);
            holder.linlaTrainer.setVisibility(View.GONE);

            /* SET THE ENQUIRY MESSAGE */
            String enquiryMessage = message.getEnquiryMessage();
            if (enquiryMessage != null)    {
                holder.txtUsersMessage.setText(enquiryMessage);
            }

            /* SET THE USER'S DISPLAY PROFILE */
            String strUserDisplayProfile = message.getUserDisplayProfile();
            if (strUserDisplayProfile != null)    {
                Uri uri = Uri.parse(strUserDisplayProfile);
                holder.imgvwUserDisplayProfile.setImageURI(uri);
            } else {
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithResourceId(R.drawable.ic_action_user_light)
                        .build();
                holder.imgvwUserDisplayProfile.setImageURI(request.getSourceUri());
            }

            /* SET THE USER'S NAME */
            String strUserName = message.getUserName();
            if (strUserName != null)    {
                holder.txtUserName.setText(strUserName);
            }

            /* SET THE ENQUIRY MESSAGE TIME STAMP */
            if (message.getEnquiryTimestamp() != null) {
                String enquiryTimestamp = message.getEnquiryTimestamp();
                long lngTimeStamp = Long.parseLong(enquiryTimestamp) * 1000;
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(lngTimeStamp);
                Date date = calendar.getTime();
                PrettyTime prettyTime = new PrettyTime();
                holder.txtUserTimeStamp.setText(prettyTime.format(date));
            }
        }
    }

    @NonNull
    @Override
    public MessagesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.groomer_enquiry_item, parent, false);

        return new MessagesVH(itemView);
    }

    class MessagesVH extends RecyclerView.ViewHolder	{
        final LinearLayout linlaTrainer;
        final SimpleDraweeView imgvwTrainerDisplayProfile;
        final TextView txtTrainersMessage;
        final TextView txtTrainerName;
        final TextView txtTrainerTimeStamp;
        final LinearLayout linlaUser;
        final SimpleDraweeView imgvwUserDisplayProfile;
        final TextView txtUsersMessage;
        final TextView txtUserName;
        final TextView txtUserTimeStamp;

        MessagesVH(View v) {
            super(v);

            linlaTrainer = v.findViewById(R.id.linlaTrainer);
            imgvwTrainerDisplayProfile = v.findViewById(R.id.imgvwTrainerDisplayProfile);
            txtTrainersMessage = v.findViewById(R.id.txtTrainersMessage);
            txtTrainerName = v.findViewById(R.id.txtTrainerName);
            txtTrainerTimeStamp = v.findViewById(R.id.txtTrainerTimeStamp);
            linlaUser = v.findViewById(R.id.linlaUser);
            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUsersMessage = v.findViewById(R.id.txtUsersMessage);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtUserTimeStamp = v.findViewById(R.id.txtUserTimeStamp);
        }
    }
}