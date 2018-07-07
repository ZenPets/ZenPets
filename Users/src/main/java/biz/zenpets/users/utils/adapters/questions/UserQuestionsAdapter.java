package biz.zenpets.users.utils.adapters.questions;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.details.questions.QuestionDetails;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.consultations.consultations.Consultation;
import biz.zenpets.users.utils.models.consultations.replies.ConsultationReplies;
import biz.zenpets.users.utils.models.consultations.replies.ConsultationRepliesAPI;
import biz.zenpets.users.utils.models.consultations.replies.ConsultationReply;
import biz.zenpets.users.utils.models.consultations.views.ConsultationView;
import biz.zenpets.users.utils.models.consultations.views.ConsultationViews;
import biz.zenpets.users.utils.models.consultations.views.ConsultationViewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserQuestionsAdapter extends RecyclerView.Adapter<UserQuestionsAdapter.ConsultationsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Consultation> arrQuestions;

    public UserQuestionsAdapter(Activity activity, ArrayList<Consultation> arrQuestions) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrQuestions = arrQuestions;
    }

    @Override
    public int getItemCount() {
        return arrQuestions.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ConsultationsVH holder, final int position) {
        final Consultation data = arrQuestions.get(position);

        /* SET THE TITLE */
        if (data.getConsultationTitle() != null)    {
            holder.txtConsultationTitle.setText(data.getConsultationTitle());
        }

        /* SET THE DESCRIPTION */
        if (data.getConsultationDescription() != null) {
            holder.txtConsultationDescription.setText(data.getConsultationDescription());
        }

        /* CHECK IF THE QUESTION HAS AN IMAGE */
        if (data.getConsultationPicture() != null && !data.getConsultationPicture().equalsIgnoreCase("null"))  {
            holder.imgvwHasAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.imgvwHasAttachment.setVisibility(View.GONE);
        }

        /* SET THE TIME STAMP */
        if (data.getConsultationTimestamp() != null)    {
            String consultationTimestamp = data.getConsultationTimestamp();
            long lngTimeStamp = Long.parseLong(consultationTimestamp) * 1000;
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(lngTimeStamp);
            Date date = calendar.getTime();
            PrettyTime prettyTime = new PrettyTime();
            String strDate = prettyTime.format(date);
            holder.txtConsultationTimestamp.setText(strDate);
        }

        /* SET THE NUMBER OF VIEWS */
        ConsultationViewsAPI api = ZenApiClient.getClient().create(ConsultationViewsAPI.class);
        Call<ConsultationViews> call = api.fetchConsultationViews(data.getConsultationID());
        call.enqueue(new Callback<ConsultationViews>() {
            @Override
            public void onResponse(Call<ConsultationViews> call, Response<ConsultationViews> response) {
                ArrayList<ConsultationView> list = response.body().getViews();
                if (list != null && list.size() > 0)    {
                    String views = String.valueOf(list.size());
                    holder.txtConsultationViews.setText(activity.getString(R.string.question_details_views_placeholder, views));
                } else {
                    holder.txtConsultationViews.setText(activity.getString(R.string.question_details_views_placeholder, "0"));
                }
            }

            @Override
            public void onFailure(Call<ConsultationViews> call, Throwable t) {
//                Log.e("VIEWS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });

        /* SET THE NUMBER OF REPLIES */
        ConsultationRepliesAPI apiReplies = ZenApiClient.getClient().create(ConsultationRepliesAPI.class);
        Call<ConsultationReplies> callReplies = apiReplies.fetchConsultationReplies(data.getConsultationID());
        callReplies.enqueue(new Callback<ConsultationReplies>() {
            @Override
            public void onResponse(Call<ConsultationReplies> call, Response<ConsultationReplies> response) {
                ArrayList<ConsultationReply> list = response.body().getReplies();
                if (list != null && list.size() > 0)    {
                    String replies = String.valueOf(list.size());
                    holder.txtConsultationReplies.setText(activity.getString(R.string.question_details_replies_placeholder, replies));
                } else {
                    holder.txtConsultationReplies.setText(activity.getString(R.string.question_details_replies_placeholder, "0"));
                }
            }

            @Override
            public void onFailure(Call<ConsultationReplies> call, Throwable t) {
//                Log.e("REPLIES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });

        /* SHOW THE CONSULTATION DETAILS */
        holder.linlaConsultationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, QuestionDetails.class);
                intent.putExtra("CONSULTATION_ID", data.getConsultationID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ConsultationsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.fragment_question_item, parent, false);

        return new ConsultationsVH(itemView);
    }

    class ConsultationsVH extends RecyclerView.ViewHolder	{
        final LinearLayout linlaConsultationContainer;
        final AppCompatTextView txtConsultationTitle;
        final AppCompatTextView txtConsultationDescription;
        final IconicsImageView imgvwHasAttachment;
        final AppCompatTextView txtConsultationTimestamp;
        final AppCompatTextView txtConsultationViews;
        final AppCompatTextView txtConsultationReplies;

        ConsultationsVH(View v) {
            super(v);
            linlaConsultationContainer = v.findViewById(R.id.linlaConsultationContainer);
            txtConsultationTitle = v.findViewById(R.id.txtConsultationTitle);
            txtConsultationDescription = v.findViewById(R.id.txtConsultationDescription);
            imgvwHasAttachment = v.findViewById(R.id.imgvwHasAttachment);
            txtConsultationTimestamp = v.findViewById(R.id.txtConsultationTimestamp);
            txtConsultationViews = v.findViewById(R.id.txtConsultationViews);
            txtConsultationReplies = v.findViewById(R.id.txtConsultationReplies);
        }
    }
}