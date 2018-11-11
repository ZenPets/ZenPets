package co.zenpets.doctors.utils.adapters.consultations;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationReply;
import co.zenpets.doctors.utils.models.consultations.votes.Vote;
import co.zenpets.doctors.utils.models.consultations.votes.Votes;
import co.zenpets.doctors.utils.models.consultations.votes.VotesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.RepliesVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<ConsultationReply> arrReplies;

    /** THE HELPFUL VOTES **/
    private String HELPFUL_VOTES = null;
    private String NON_HELPFUL_VOTES = null;

    public RepliesAdapter(Activity activity, ArrayList<ConsultationReply> arrReplies) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrReplies = arrReplies;
    }

    @Override
    public int getItemCount() {
        return arrReplies.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final RepliesVH holder, final int position) {
        final ConsultationReply data = arrReplies.get(position);

            /* SET THE REPLY TEXT */
        if (data.getReplyText() != null)    {
            holder.txtReplyText.setText(data.getReplyText());
        }

            /* SET THE TIME STAMP */
        if (data.getReplyTimestamp() != null)   {
            String consultationTimestamp = data.getReplyTimestamp();
            long lngTimeStamp = Long.parseLong(consultationTimestamp) * 1000;
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(lngTimeStamp);
            Date date = calendar.getTime();
            PrettyTime prettyTime = new PrettyTime();
            holder.txtReplyTimestamp.setText(prettyTime.format(date));
        }

            /* SET THE DOCTOR'S NAME */
        if (data.getDoctorPrefix() != null && data.getDoctorName() != null)   {
            String strPrefix = data.getDoctorPrefix();
            String strName = data.getDoctorName();
            holder.txtDoctorName.setText(activity.getString(R.string.doctor_profile_name_placeholder, strPrefix, strName));
        }

            /* SET THE DOCTOR PROFILE */
        if (data.getDoctorDisplayProfile() != null)   {
            Uri uri = Uri.parse(data.getDoctorDisplayProfile());
            holder.imgvwDoctorProfile.setImageURI(uri);
        } else {
            holder.imgvwDoctorProfile.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.beagle));
        }

            /* SET THE DOCTOR'S LOCATION */
        if (data.getStateName() != null && data.getCityName() != null)  {
            String city = data.getCityName();
            String state = data.getStateName();
            holder.txtDoctorLocation.setText(activity.getString(R.string.question_details_location_placeholder, city, state));
            holder.txtDoctorLocation.setVisibility(View.VISIBLE);
        } else {
            holder.txtDoctorLocation.setVisibility(View.GONE);
        }

        /* FETCH THE NUMBER OF VOTES */
        fetchVotes(holder.txtReplyHelpful, data.getReplyID());
    }

    @NonNull
    @Override
    public RepliesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.replies_item, parent, false);

        return new RepliesVH(itemView);
    }

    class RepliesVH extends RecyclerView.ViewHolder	{
        final SimpleDraweeView imgvwDoctorProfile;
        final AppCompatTextView txtDoctorName;
        final AppCompatTextView txtDoctorLocation;
        final AppCompatTextView txtReplyTimestamp;
        final AppCompatTextView txtReplyText;
        final AppCompatTextView txtReplyHelpful;
        final IconicsImageView imgvwFlagReply;

        RepliesVH(View v) {
            super(v);
            imgvwDoctorProfile = v.findViewById(R.id.imgvwDoctorProfile);
            txtDoctorName = v.findViewById(R.id.txtDoctorName);
            txtDoctorLocation = v.findViewById(R.id.txtDoctorLocation);
            txtReplyTimestamp = v.findViewById(R.id.txtReplyTimestamp);
            txtReplyText = v.findViewById(R.id.txtReplyText);
            txtReplyHelpful = v.findViewById(R.id.txtReplyHelpful);
            imgvwFlagReply = v.findViewById(R.id.imgvwFlagReply);
        }
    }

    /***** FETCH THE NUMBER OF VOTES *****/
    private void fetchVotes(final AppCompatTextView txtReplyHelpful, final String replyID) {
        VotesAPI apiYes = ZenApiClient.getClient().create(VotesAPI.class);
        Call<Votes> callYes = apiYes.fetchYesVotes(replyID, "1");
        callYes.enqueue(new Callback<Votes>() {
            @Override
            public void onResponse(@NonNull Call<Votes> call, @NonNull Response<Votes> response) {
                ArrayList<Vote> listYes = response.body().getVotes();
                if (listYes != null && listYes.size() > 0)  {
                    HELPFUL_VOTES = String.valueOf(listYes.size());
                } else {
                    HELPFUL_VOTES = "0";
                }

                /* FETCH THE NUMBER OF "NO" VOTES */
                VotesAPI apiNo = ZenApiClient.getClient().create(VotesAPI.class);
                Call<Votes> callNo = apiNo.fetchNoVotes(replyID, "0");
                callNo.enqueue(new Callback<Votes>() {
                    @Override
                    public void onResponse(@NonNull Call<Votes> call, @NonNull Response<Votes> response) {
                        ArrayList<Vote> listNo = response.body().getVotes();
                        if (listNo != null && listNo.size() > 0)    {
                            NON_HELPFUL_VOTES = String.valueOf(listNo.size());
                        } else {
                            NON_HELPFUL_VOTES = "0";
                        }

                        /* SET THE NUMBER OF "YES" AND "NO" VOTES */
                        txtReplyHelpful.setText(activity.getString(R.string.question_details_votes_placeholder, HELPFUL_VOTES, NON_HELPFUL_VOTES));
                    }

                    @Override
                    public void onFailure(@NonNull Call<Votes> call, @NonNull Throwable t) {
//                        Crashlytics.logException(t);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<Votes> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }
}