package biz.zenpets.users.utils.adapters.adoptions.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.adoptions.promote.PromoteAdoptionActivity;
import biz.zenpets.users.details.adoptions.TestUserAdoptionDetails;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoption;
import biz.zenpets.users.utils.models.adoptions.adoption.AdoptionsAPI;
import biz.zenpets.users.utils.models.adoptions.promotion.Promotion;
import biz.zenpets.users.utils.models.adoptions.promotion.PromotionAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestUserAdoptionsAdapter extends RecyclerView.Adapter<TestUserAdoptionsAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Adoption> arrAdoptions;

    public TestUserAdoptionsAdapter(Activity activity, ArrayList<Adoption> arrAdoptions) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrAdoptions = arrAdoptions;
    }

    @Override
    public int getItemCount() {
        return arrAdoptions.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Adoption data = arrAdoptions.get(position);

        /* SET THE ADOPTION COVER PHOTO */
        String strAdoptionCover = data.getAdoptionCoverPhoto();
        if (strAdoptionCover != null
                && !strAdoptionCover.equalsIgnoreCase("")
                && !strAdoptionCover.equalsIgnoreCase("null")) {
            Uri uri = Uri.parse(strAdoptionCover);
            holder.imgvwAdoptionCover.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwAdoptionCover.setImageURI(request.getSourceUri());
        }

        /* SET THE ADOPTION NAME */
        if (data.getAdoptionName() != null && !data.getAdoptionName().equalsIgnoreCase("")) {
            holder.txtAdoptionName.setText(data.getAdoptionName());
        } else {
            holder.txtAdoptionName.setText(activity.getString(R.string.adoption_details_unnamed));
        }

        /* SET THE PET'S GENDER */
        if (data.getAdoptionGender().equalsIgnoreCase("male"))  {
            holder.imgvwGender.setIcon("faw-mars");
            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
        } else if (data.getAdoptionGender().equalsIgnoreCase("female")) {
            holder.imgvwGender.setIcon("faw-venus");
            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
        }

        /* SET THE PET'S BREED */
        if (data.getBreedName() != null)    {
            holder.txtAdoptionBreed.setText(data.getBreedName());
        }
//
        /* SET THE TIMESTAMP (DATE OF CREATION )*/
        if (data.getAdoptionTimeStamp() != null)    {
            String adoptionTimeStamp = data.getAdoptionTimeStamp();
            long lngTimeStamp = Long.parseLong(adoptionTimeStamp) * 1000;

            /* GET THE PRETTY DATE */
            Calendar calPretty = Calendar.getInstance(Locale.getDefault());
            calPretty.setTimeInMillis(lngTimeStamp);
            Date date = calPretty.getTime();
            PrettyTime prettyTime = new PrettyTime();
            String strPrettyDate = prettyTime.format(date);
            holder.txtAdoptionTimeStamp.setText(activity.getString(R.string.adoption_details_posted_new, strPrettyDate));
        }

        /* SHOW THE ADOPTION OPTIONS */
        holder.imgvwOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(activity, holder.imgvwOptions);
                pm.getMenuInflater().inflate(R.menu.pm_adoption_options, pm.getMenu());
                if (data.getAdoptionStatus().equalsIgnoreCase("Adopted"))   {
                    pm.getMenu().findItem(R.id.menuStatus).setEnabled(false);
                    pm.getMenu().findItem(R.id.menuPromote).setEnabled(false);
                } else {
                    pm.getMenu().findItem(R.id.menuStatus).setEnabled(true);
                    pm.getMenu().findItem(R.id.menuPromote).setEnabled(true);
                }
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())   {
                            case R.id.menuEdit:
                                break;
                            case R.id.menuDetails:
                                break;
                            case R.id.menuStatus:
                                /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
                                final ProgressDialog dialogStatus = new ProgressDialog(activity);
                                dialogStatus.setMessage("Updating the Adoption status....");
                                dialogStatus.setIndeterminate(false);
                                dialogStatus.setCancelable(false);
                                dialogStatus.show();

                                AdoptionsAPI apiStatus = ZenApiClient.getClient().create(AdoptionsAPI.class);
                                Call<Adoption> callStatus = apiStatus.changeAdoptionStatus(data.getAdoptionID(), "Adopted");
                                callStatus.enqueue(new Callback<Adoption>() {
                                    @Override
                                    public void onResponse(Call<Adoption> call, Response<Adoption> response) {
                                        if (response.isSuccessful())    {
                                            data.setAdoptionStatus("Adopted");
                                            Toast.makeText(
                                                    activity,
                                                    "The Adoption was updated successfully...",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(
                                                    activity,
                                                    "Status update error...",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        /* DISMISS THE DIALOG */
                                        dialogStatus.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<Adoption> call, Throwable t) {
                                        Log.e("STATUS FAILURE", t.getMessage());
                                        Crashlytics.logException(t);
                                    }
                                });
                                break;
                            case R.id.menuPromote:
                                /* CHECK IF THE ADOPTION IS BEING PROMOTED */
                                PromotionAPI api = ZenApiClient.getClient().create(PromotionAPI.class);
                                Call<Promotion> call = api.promotionExists(data.getAdoptionID());
                                call.enqueue(new Callback<Promotion>() {
                                    @Override
                                    public void onResponse(Call<Promotion> call, Response<Promotion> response) {
                                        Promotion body = response.body();
                                        if (body != null)   {
                                            String message = body.getMessage();
                                            if (message != null)    {
                                                if (message.equalsIgnoreCase("Promotion record exists..."))   {
                                                    Toast.makeText(activity, "This Adoption listing is already being promoted...", Toast.LENGTH_LONG).show();
                                                } else if (message.equalsIgnoreCase("Promotion record doesn't exist..."))    {
                                                    Intent intentPromote = new Intent(activity, PromoteAdoptionActivity.class);
                                                    intentPromote.putExtra("ADOPTION_ID", data.getAdoptionID());
                                                    activity.startActivity(intentPromote);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Promotion> call, Throwable t) {
//                                        Log.e("CHECK FAILURE", t.getMessage());
                                        Crashlytics.logException(t);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                pm.show();
            }
        });

        /* SHOW THE ADOPTION DETAILS */
        holder.cardAdoptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TestUserAdoptionDetails.class);
                intent.putExtra("ADOPTION_ID", data.getAdoptionID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.user_adoptions_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{
        final CardView cardAdoptionContainer;
        final SimpleDraweeView imgvwAdoptionCover;
        final TextView txtAdoptionName;
        final TextView txtAdoptionBreed;
        final IconicsImageView imgvwGender;
        final IconicsImageView imgvwOptions;
        final TextView txtAdoptionTimeStamp;

        AdoptionsVH(View v) {
            super(v);
            cardAdoptionContainer = v.findViewById(R.id.cardAdoptionContainer);
            imgvwAdoptionCover = v.findViewById(R.id.imgvwAdoptionCover);
            txtAdoptionName = v.findViewById(R.id.txtAdoptionName);
            txtAdoptionBreed = v.findViewById(R.id.txtAdoptionBreed);
            imgvwGender = v.findViewById(R.id.imgvwGender);
            imgvwOptions = v.findViewById(R.id.imgvwOptions);
            txtAdoptionTimeStamp = v.findViewById(R.id.txtAdoptionTimeStamp);
        }
    }
}