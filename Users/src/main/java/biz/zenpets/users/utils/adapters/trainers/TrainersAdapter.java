package biz.zenpets.users.utils.adapters.trainers;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.details.trainers.TrainerDetails;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.trainers.Trainer;
import biz.zenpets.users.utils.models.trainers.modules.ModulesAPI;
import biz.zenpets.users.utils.models.trainers.modules.ModulesCount;
import biz.zenpets.users.utils.models.trainers.modules.ModulesMinMax;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReview;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReviews;
import biz.zenpets.users.utils.models.trainers.reviews.TrainerReviewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainersAdapter extends RecyclerView.Adapter<TrainersAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Trainer> arrTrainers;

    /** THE DATA TYPES FOR CALCULATING THE LIKES PERCENTAGE  **/
    private int TOTAL_LIKES = 0;
    private int TOTAL_VOTES = 0;

    public TrainersAdapter(Activity activity, ArrayList<Trainer> arrTrainers) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrTrainers = arrTrainers;
    }

    @Override
    public int getItemCount() {
        return arrTrainers.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Trainer data = arrTrainers.get(position);

        /* SET THE TRAINER'S NAME */
        if (data.getTrainerName() != null)  {
            holder.txtTrainerName.setText(data.getTrainerName());
        }

        /* GET AND SET THE TRAINER'S ADDRESS */
        String TRAINER_ADDRESS = data.getTrainerAddress();
        String CITY_NAME = data.getCityName();
        String TRAINER_PINCODE = data.getTrainerPincode();
        String STATE_NAME = data.getStateName();
        holder.txtTrainerAddress.setText(activity.getString(R.string.trainer_list_address_placeholder, TRAINER_ADDRESS, CITY_NAME, TRAINER_PINCODE, STATE_NAME));
        
        /* FETCH THE NUMBER OF TRAINING MODULES */
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<ModulesCount> call = api.fetchTrainingModulesCount(data.getTrainerID());
        call.enqueue(new Callback<ModulesCount>() {
            @Override
            public void onResponse(Call<ModulesCount> call, Response<ModulesCount> response) {
                ModulesCount count = response.body();
                if (count != null)  {
                    int moduleCount = Integer.parseInt(count.getTotal_modules());
                    Resources resModules = AppPrefs.context().getResources();
                    String strFinalCount = null;
                    if (moduleCount == 0)   {
                        strFinalCount = resModules.getQuantityString(R.plurals.training_modules, moduleCount, moduleCount);
                    } else if (moduleCount == 1)    {
                        strFinalCount = resModules.getQuantityString(R.plurals.training_modules, moduleCount, moduleCount);
                    } else if (moduleCount > 1) {
                        strFinalCount = resModules.getQuantityString(R.plurals.training_modules, moduleCount, moduleCount);
                    }
                    holder.txtTrainerModules.setText(activity.getString(R.string.trainer_list_modules_placeholder, strFinalCount));
                } else {
                    holder.txtTrainerModules.setText(activity.getString(R.string.trainer_list_modules_na_placeholder));
                }
            }

            @Override
            public void onFailure(Call<ModulesCount> call, Throwable t) {
                Log.e("COUNT FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });

        /* FETCH THE RANGE OF FEES */
        Call<ModulesMinMax> minMaxCall = api.fetchTrainingModulesMinMax(data.getTrainerID());
        minMaxCall.enqueue(new Callback<ModulesMinMax>() {
            @Override
            public void onResponse(Call<ModulesMinMax> call, Response<ModulesMinMax> response) {
                ModulesMinMax data = response.body();
                if (data != null)   {
                    /* GET THE MINIMUM TRAINING FEE */
                    String strMinTrainingFee = data.getMinTrainingFee();

                    /* GET THE MAXIMUM TRAINING FEE */
                    String strMaxTrainingFee = data.getMaxTrainingFee();

                    /* SET THE FEE RANGE */
                    holder.txtTrainerCharges.setText(activity.getString(R.string.trainer_list_charges_placeholder, strMinTrainingFee, strMaxTrainingFee));
                } else {
                    holder.txtTrainerModules.setText(activity.getString(R.string.trainer_list_charges_na_placeholder));
                }
            }

            @Override
            public void onFailure(Call<ModulesMinMax> call, Throwable t) {
                Log.e("RANGE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });

        /* GET THE TOTAL NUMBER OF POSITIVE REVIEWS */
        TrainerReviewsAPI apiYes = ZenApiClient.getClient().create(TrainerReviewsAPI.class);
        Call<TrainerReviews> callYes = apiYes.fetchPositiveTrainerReviews(data.getTrainerID(), "Yes");
        callYes.enqueue(new Callback<TrainerReviews>() {
            @Override
            public void onResponse(Call<TrainerReviews> call, Response<TrainerReviews> response) {
                if (response.body() != null && response.body().getReviews() != null)    {
                    ArrayList<TrainerReview> arrReview = response.body().getReviews();
                    TOTAL_LIKES = arrReview.size();
                    TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                    /* GET THE TOTAL NUMBER OF NEGATIVE REVIEWS */
                    TrainerReviewsAPI apiNo = ZenApiClient.getClient().create(TrainerReviewsAPI.class);
                    Call<TrainerReviews> callNo = apiNo.fetchNegativeTrainerReviews(data.getTrainerID(), "No");
                    callNo.enqueue(new Callback<TrainerReviews>() {
                        @Override
                        public void onResponse(Call<TrainerReviews> call, Response<TrainerReviews> response) {
                            if (response.body() != null && response.body().getReviews() != null)    {
                                ArrayList<TrainerReview> arrReview = response.body().getReviews();
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
                                holder.txtTrainerLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                            }
                        }

                        @Override
                        public void onFailure(Call<TrainerReviews> call, Throwable t) {
                            Crashlytics.logException(t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<TrainerReviews> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });

        /* SHOW THE TRAINER'S DETAILS */
        holder.linlaTrainerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TrainerDetails.class);
                intent.putExtra("TRAINER_ID", data.getTrainerID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.trainers_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{
        LinearLayout linlaTrainerContainer;
        AppCompatTextView txtTrainerName;
        AppCompatTextView txtTrainerAddress;
        AppCompatTextView txtTrainerLikes;
        AppCompatTextView txtTrainerModules;
        AppCompatTextView txtTrainerCharges;

        AdoptionsVH(View v) {
            super(v);

            linlaTrainerContainer = v.findViewById(R.id.linlaTrainerContainer);
            txtTrainerName = v.findViewById(R.id.txtTrainerName);
            txtTrainerAddress = v.findViewById(R.id.txtTrainerAddress);
            txtTrainerLikes = v.findViewById(R.id.txtTrainerLikes);
            txtTrainerModules = v.findViewById(R.id.txtTrainerModules);
            txtTrainerCharges = v.findViewById(R.id.txtTrainerCharges);
        }
    }
}