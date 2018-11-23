package co.zenpets.doctors.utils.adapters.doctors;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.Doctor;
import co.zenpets.doctors.utils.models.doctors.reviews.DoctorReviewsAPI;
import co.zenpets.doctors.utils.models.doctors.reviews.ReviewData;
import co.zenpets.doctors.utils.models.doctors.reviews.ReviewsData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ClinicsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Doctor> arrDoctors;

    /** THE DATA TYPES FOR CALCULATING THE LIKES PERCENTAGE  **/
    private int TOTAL_LIKES = 0;
    private int TOTAL_VOTES = 0;

    public DoctorsAdapter(Activity activity, ArrayList<Doctor> arrDoctors) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrDoctors = arrDoctors;
    }

    @Override
    public int getItemCount() {
        return arrDoctors.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ClinicsVH holder, final int position) {
        final Doctor data = arrDoctors.get(position);

        /* SET THE DOCTOR'S DISPLAY PROFILE */
        if (data.getDoctorDisplayProfile() != null)   {
            Uri uri = Uri.parse(data.getDoctorDisplayProfile());
            holder.imgvwDoctorProfile.setImageURI(uri);
        }

        /* SET THE DOCTOR'S NAME */
        if (data.getDoctorPrefix() != null && data.getDoctorName() != null)   {
            String prefix = data.getDoctorPrefix();
            String name = data.getDoctorName();
            holder.txtDoctorName.setText(activity.getString(R.string.doctor_item_name_placeholder, prefix, name));
        }

        /* SET THE DOCTOR'S EXPERIENCE */
        String strExp = activity.getString(R.string.doctor_list_doctor_experience_text);
        String docExperience = data.getDoctorExperience();
        holder.txtDoctorExp.setText(activity.getString(R.string.doctor_list_doctor_experience_placeholder, docExperience, strExp));

        /* SET THE DOCTOR'S CHARGES */
        if (data.getDoctorCharges() != null && data.getCurrencySymbol() != null)    {
            String charges = data.getDoctorCharges();
            String currency = data.getCurrencySymbol();
            holder.txtDoctorCharges.setText(activity.getString(R.string.doctor_item_charges_placeholder, currency, charges));
        }

        /* GET THE TOTAL NUMBER OF POSITIVE REVIEWS */
        DoctorReviewsAPI apiYes = ZenApiClient.getClient().create(DoctorReviewsAPI.class);
        Call<ReviewsData> callYes = apiYes.fetchPositiveReviews(data.getDoctorID(), "Yes");
        callYes.enqueue(new Callback<ReviewsData>() {
            @Override
            public void onResponse(@NonNull Call<ReviewsData> call, @NonNull Response<ReviewsData> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    ArrayList<ReviewData> arrReview = response.body().getReviews();
                    TOTAL_LIKES = arrReview.size();
                    TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                    /* GET THE TOTAL NUMBER OF NEGATIVE REVIEWS */
                    DoctorReviewsAPI apiNo = ZenApiClient.getClient().create(DoctorReviewsAPI.class);
                    Call<ReviewsData> callNo = apiNo.fetchNegativeReviews(data.getDoctorID(), "No");
                    callNo.enqueue(new Callback<ReviewsData>() {
                        @Override
                        public void onResponse(@NonNull Call<ReviewsData> call, @NonNull Response<ReviewsData> response) {
                            if (response.body() != null && response.body().getReviews() != null)    {
                                ArrayList<ReviewData> arrReview = response.body().getReviews();
                                TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                                /* CALCULATE THE PERCENTAGE OF LIKES */
                                double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
                                int finalPercentLikes = (int)percentLikes;
                                String strLikesPercentage = String.valueOf(finalPercentLikes) + "%";

                                /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
                                Resources resReviews = AppPrefs.context().getResources();
                                String reviewQuantity = null;
                                if (TOTAL_VOTES == 0)   {
                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                                } else if (TOTAL_VOTES == 1)    {
                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                                } else if (TOTAL_VOTES > 1) {
                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                                }
                                String strVotes = reviewQuantity;
                                String open = activity.getString(R.string.doctor_list_votes_open);
                                String close = activity.getString(R.string.doctor_list_votes_close);
                                holder.txtDoctorLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ReviewsData> call, @NonNull Throwable t) {
//                            Crashlytics.logException(t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsData> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });

//        /* SHOW THE DOCTOR DETAILS */
//        holder.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, DoctorDetails.class);
//                intent.putExtra("DOCTOR_ID", data.getDoctorID());
//                intent.putExtra("CLINIC_ID", data.getClinicID());
//                activity.startActivity(intent);
//            }
//        });
    }

    @NonNull
    @Override
    public ClinicsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctors_item, parent, false);

        return new ClinicsVH(itemView);
    }

    class ClinicsVH extends RecyclerView.ViewHolder	{
        final LinearLayout linlaDoctorContainer;
        final SimpleDraweeView imgvwDoctorProfile;
        final AppCompatTextView txtDoctorName;
        final AppCompatTextView txtDoctorLikes;
        final AppCompatTextView txtDoctorExp;
        final AppCompatTextView txtDoctorCharges;


        ClinicsVH (View v) {
            super(v);
            linlaDoctorContainer = v.findViewById(R.id.linlaDoctorContainer);
            imgvwDoctorProfile = v.findViewById(R.id.imgvwDoctorProfile);
            txtDoctorName = v.findViewById(R.id.txtDoctorName);
            txtDoctorLikes = v.findViewById(R.id.txtDoctorLikes);
            txtDoctorExp = v.findViewById(R.id.txtDoctorExp);
            txtDoctorCharges = v.findViewById(R.id.txtDoctorCharges);
        }
    }
}