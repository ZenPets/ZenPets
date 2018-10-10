package co.zenpets.trainers.utils.adapters.enquiries;

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

import co.zenpets.trainers.R;
import co.zenpets.trainers.utils.models.trainers.enquiries.EnquiryMessage;

public class TrainingMessagesAdapter extends RecyclerView.Adapter<TrainingMessagesAdapter.MessagesVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<EnquiryMessage> arrMessages;

    public TrainingMessagesAdapter(Activity activity, ArrayList<EnquiryMessage> arrMessages) {

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
    public void onBindViewHolder(final MessagesVH holder, final int position) {
        EnquiryMessage message = arrMessages.get(position);

        /* CHECK IF THE MESSAGE IS FROM THE TRAINER OR THE USER */
        if (message.getTrainerID() != null) {
            /* SHOW THE TRAINER LAYOUT AND HIDE THE USER LAYOUT */
            holder.linlaTrainer.setVisibility(View.VISIBLE);
            holder.linlaUser.setVisibility(View.GONE);

            /* SET HE TRAINER'S DISPLAY PROFILE */
            String strTrainerDisplayProfile = message.getTrainerDisplayProfile();
            if (strTrainerDisplayProfile != null)    {
                Uri uri = Uri.parse(strTrainerDisplayProfile);
                holder.imgvwTrainerDisplayProfile.setImageURI(uri);
            } else {
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithResourceId(R.drawable.user_placeholder)
                        .build();
                holder.imgvwTrainerDisplayProfile.setImageURI(request.getSourceUri());
            }

            /* SET THE ENQUIRY MESSAGE */
            String strTrainingSlaveMessage = message.getTrainingSlaveMessage();
            if (strTrainingSlaveMessage != null)    {
                holder.txtTrainersMessage.setText(strTrainingSlaveMessage);
            }

            /* SET THE ENQUIRY MESSAGE TIME STAMP */
            if (message.getTrainerSlaveTimestamp() != null) {
                String messageTimeStamp = message.getTrainerSlaveTimestamp();
                long lngTimeStamp = Long.parseLong(messageTimeStamp) * 1000;
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
            String strTrainingSlaveMessage = message.getTrainingSlaveMessage();
            if (strTrainingSlaveMessage != null)    {
                holder.txtUsersMessage.setText(strTrainingSlaveMessage);
            }

            /* SET THE USER'S DISPLAY PROFILE */
            String strUserDisplayProfile = message.getUserDisplayProfile();
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
            String strUserName = message.getUserName();
            if (strUserName != null)    {
                holder.txtUserName.setText(strUserName);
            }

            /* SET THE ENQUIRY MESSAGE TIME STAMP */
            if (message.getTrainerSlaveTimestamp() != null) {
                String messageTimeStamp = message.getTrainerSlaveTimestamp();
                long lngTimeStamp = Long.parseLong(messageTimeStamp) * 1000;
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
                inflate(R.layout.trainer_enquiry_item, parent, false);

        return new MessagesVH(itemView);
    }

    class MessagesVH extends RecyclerView.ViewHolder	{
        LinearLayout linlaUser;
        SimpleDraweeView imgvwUserDisplayProfile;
        TextView txtUsersMessage;
        TextView txtUserName;
        TextView txtUserTimeStamp;
        LinearLayout linlaTrainer;
        SimpleDraweeView imgvwTrainerDisplayProfile;
        TextView txtTrainersMessage;
        TextView txtTrainerName;
        TextView txtTrainerTimeStamp;

        MessagesVH(View v) {
            super(v);
            linlaUser = v.findViewById(R.id.linlaUser);
            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUsersMessage = v.findViewById(R.id.txtUsersMessage);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtUserTimeStamp = v.findViewById(R.id.txtUserTimeStamp);
            linlaTrainer = v.findViewById(R.id.linlaTrainer);
            imgvwTrainerDisplayProfile = v.findViewById(R.id.imgvwTrainerDisplayProfile);
            txtTrainersMessage = v.findViewById(R.id.txtTrainersMessage);
            txtTrainerName = v.findViewById(R.id.txtTrainerName);
            txtTrainerTimeStamp = v.findViewById(R.id.txtTrainerTimeStamp);
        }
    }
}