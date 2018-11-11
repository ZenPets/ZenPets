package co.zenpets.doctors.utils.adapters.consultations;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.doctors.R;
import co.zenpets.doctors.details.consultation.ConsultationDetails;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.consultations.consultations.Consultation;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationReplies;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationRepliesAPI;
import co.zenpets.doctors.utils.models.consultations.replies.ConsultationReply;
import co.zenpets.doctors.utils.models.consultations.views.ConsultationView;
import co.zenpets.doctors.utils.models.consultations.views.ConsultationViews;
import co.zenpets.doctors.utils.models.consultations.views.ConsultationViewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationsAdapter extends RecyclerView.Adapter<ConsultationsAdapter.ConsultationsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Consultation> arrConsultations;

    public ConsultationsAdapter(Activity activity, ArrayList<Consultation> arrConsultations) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrConsultations = arrConsultations;
    }

    @Override
    public int getItemCount() {
        return arrConsultations.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ConsultationsVH holder, final int position) {
        final Consultation data = arrConsultations.get(position);

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
            public void onResponse(@NonNull Call<ConsultationViews> call, @NonNull Response<ConsultationViews> response) {
                ArrayList<ConsultationView> list = response.body().getViews();
                if (list != null && list.size() > 0)    {
                    int TOTAL_VIEWS = list.size();

                    /* GET THE TOTAL NUMBER OF VIEWS */
                    Resources resViews = AppPrefs.context().getResources();
                    String viewsQuantity = null;
                    if (TOTAL_VIEWS == 0)   {
                        viewsQuantity = resViews.getQuantityString(R.plurals.views, TOTAL_VIEWS, TOTAL_VIEWS);
                    } else if (TOTAL_VIEWS == 1)    {
                        viewsQuantity = resViews.getQuantityString(R.plurals.views, TOTAL_VIEWS, TOTAL_VIEWS);
                    } else if (TOTAL_VIEWS > 1) {
                        viewsQuantity = resViews.getQuantityString(R.plurals.views, TOTAL_VIEWS, TOTAL_VIEWS);
                    }
                    holder.txtConsultationViews.setText(viewsQuantity);
                } else {
                    holder.txtConsultationViews.setText(activity.getString(R.string.question_details_views_placeholder, "0"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConsultationViews> call, @NonNull Throwable t) {
//                Log.e("VIEWS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });

        /* SET THE NUMBER OF REPLIES */
        ConsultationRepliesAPI apiReplies = ZenApiClient.getClient().create(ConsultationRepliesAPI.class);
        Call<ConsultationReplies> callReplies = apiReplies.fetchConsultationReplies(data.getConsultationID());
        callReplies.enqueue(new Callback<ConsultationReplies>() {
            @Override
            public void onResponse(@NonNull Call<ConsultationReplies> call, @NonNull Response<ConsultationReplies> response) {
                ArrayList<ConsultationReply> list = response.body().getReplies();
                if (list != null && list.size() > 0)    {
                    int TOTAL_REPLIES = list.size();

                    /* GET THE TOTAL NUMBER OF REPLIES */
                    Resources resReviews = AppPrefs.context().getResources();
                    String replyQuantity = null;
                    if (TOTAL_REPLIES == 0)   {
                        replyQuantity = resReviews.getQuantityString(R.plurals.replies, TOTAL_REPLIES, TOTAL_REPLIES);
                    } else if (TOTAL_REPLIES == 1)    {
                        replyQuantity = resReviews.getQuantityString(R.plurals.replies, TOTAL_REPLIES, TOTAL_REPLIES);
                    } else if (TOTAL_REPLIES > 1) {
                        replyQuantity = resReviews.getQuantityString(R.plurals.replies, TOTAL_REPLIES, TOTAL_REPLIES);
                    }
                    holder.txtConsultationReplies.setText(replyQuantity);
                } else {
                    holder.txtConsultationReplies.setText(activity.getString(R.string.question_details_replies_placeholder, "0"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConsultationReplies> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });

        /* SHOW THE CONSULTATION DETAILS */
        holder.linlaConsultationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ConsultationDetails.class);
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
                inflate(R.layout.home_consultation_fragment_item, parent, false);

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