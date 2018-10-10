package co.zenpets.users.utils.adapters.doctors;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.details.doctors.DoctorDetailsNew;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.models.clinics.images.ClinicImage;
import co.zenpets.users.utils.models.doctors.list.Doctor;

@SuppressWarnings("ConstantConditions")
public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.DoctorsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Doctor> arrDoctors;

    /** THE ORIGIN **/
    private final LatLng LATLNG_ORIGIN;

    /** THE DATA TYPES FOR CALCULATING THE LIKES PERCENTAGE  **/
    private int TOTAL_LIKES = 0;
    private int TOTAL_VOTES = 0;

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private ArrayList<ClinicImage> arrImages = new ArrayList<>();
    private DoctorClinicImagesAdapter adapter;

    public DoctorsListAdapter(Activity activity, ArrayList<Doctor> arrDoctors, LatLng LATLNG_ORIGIN) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrDoctors = arrDoctors;

        /* CAST THE ORIGIN TO THE GLOBAL INSTANCE */
        this.LATLNG_ORIGIN = LATLNG_ORIGIN;
    }

    @Override
    public int getItemCount() {
        return arrDoctors.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final DoctorsVH holder, final int position) {
        final Doctor data = arrDoctors.get(position);

        ArrayList<ClinicImage> arrImages = data.getImages();
        if (arrImages != null)  {
            if (arrImages.size() > 0)   {
                /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                adapter = new DoctorClinicImagesAdapter(activity, arrImages);
                holder.listClinicImages.setAdapter(adapter);

                /* SHOW THE IMAGES CONTAINER */
                holder.linlaImagesContainer.setVisibility(View.VISIBLE);
            } else {
                /* HIDE THE IMAGES CONTAINER */
                holder.linlaImagesContainer.setVisibility(View.GONE);
            }
        } else {
            /* HIDE THE IMAGES CONTAINER */
            holder.linlaImagesContainer.setVisibility(View.GONE);
        }

        /* SET THE CLINIC NAME */
        holder.txtClinicName.setText(data.getClinicName());

        /* SET THE CLINIC ADDRESS */
        String clinicAddress = data.getClinicAddress();
        String cityName = data.getCityName();
        String clinicPinCode = data.getClinicPinCode();
        holder.txtClinicAddress.setText(activity.getString(R.string.doctor_list_address_placeholder, clinicAddress, cityName, clinicPinCode));

        /* SET THE CLINIC DISTANCE */
        String strClinicDistance = data.getClinicDistance();
        if (strClinicDistance != null && !strClinicDistance.equals("Unknown"))  {
            String strTilde = activity.getString(R.string.generic_tilde);
            holder.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, strClinicDistance));
        } else {
            String strInfinity = activity.getString(R.string.generic_infinity);
            holder.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, strClinicDistance));
        }

        /* SET THE DOCTOR'S NAME */
        String doctorPrefix = data.getDoctorPrefix();
        String doctorName = data.getDoctorName();
        holder.txtDoctorName.setText(activity.getString(R.string.appointment_creator_details_doctor, doctorPrefix, doctorName));

        /* SET THE DOCTOR'S EXPERIENCE */
        String strExp = activity.getString(R.string.doctor_list_doctor_experience_text);
        String docExperience = data.getDoctorExperience();
        holder.txtDoctorExp.setText(activity.getString(R.string.doctor_list_doctor_experience_placeholder, docExperience, strExp));

        /* SET THE DOCTOR'S CHARGES */
        String currency = data.getCurrencySymbol();
        String charges = data.getDoctorCharges();
        holder.txtDoctorCharges.setText(activity.getString(R.string.doctor_details_doc_charges_placeholder, currency, charges));

        /* SET THE TOTAL NUMBER OF POSITIVE REVIEWS AND PERCENTAGE OF TOTAL REVIEWS */
        String doctorReviews = data.getDoctorReviews();
        String doctorPositives = data.getDoctorPositives();
        if (doctorReviews != null && !doctorReviews.equals("null") && !doctorReviews.equals(""))    {
            TOTAL_VOTES = Integer.parseInt(doctorReviews);
            TOTAL_LIKES = Integer.parseInt(doctorPositives);

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
        } else {
            TOTAL_VOTES = 0;
            TOTAL_LIKES = 0;

            String strLikesPercentage = "0%";

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

//        /* SET THE CLINIC IMAGES */
//        ClinicImagesAPI apiImages = ZenApiClient.getClient().create(ClinicImagesAPI.class);
//        Call<ClinicImages> callImages = apiImages.fetchClinicImages(data.getClinicID());
//        callImages.enqueue(new Callback<ClinicImages>() {
//            @Override
//            public void onResponse(Call<ClinicImages> call, Response<ClinicImages> response) {
//                if (response.body() != null && response.body().getImages() != null) {
//                    arrImages = response.body().getImages();
//                    if (arrImages.size() > 0)   {
//                        /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
//                        adapter = new DoctorClinicImagesAdapter(activity, arrImages);
//                        holder.listClinicImages.setAdapter(adapter);
//
//                        /* SHOW THE IMAGES CONTAINER */
//                        holder.linlaImagesContainer.setVisibility(View.VISIBLE);
//                    } else {
//                        /* HIDE THE IMAGES CONTAINER */
//                        holder.linlaImagesContainer.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* HIDE THE IMAGES CONTAINER */
//                    holder.linlaImagesContainer.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ClinicImages> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });

        /* SHOW THE DOCTOR DETAILS */
        holder.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DoctorDetailsNew.class);
                intent.putExtra("DOCTOR_ID", data.getDoctorID());
                intent.putExtra("CLINIC_ID", data.getClinicID());
                intent.putExtra("ORIGIN_LATITUDE", String.valueOf(LATLNG_ORIGIN.latitude));
                intent.putExtra("ORIGIN_LONGITUDE", String.valueOf(LATLNG_ORIGIN.longitude));
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public DoctorsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctors_item, parent, false);

        return new DoctorsVH(itemView);
    }

    class DoctorsVH extends RecyclerView.ViewHolder	{

        final LinearLayout linlaDoctorContainer;
        final AppCompatTextView txtDoctorName;
        final AppCompatTextView txtDoctorLikes;
        final AppCompatTextView txtClinicName;
        final AppCompatTextView txtClinicAddress;
        final LinearLayout linlaImagesContainer;
        final RecyclerView listClinicImages;
        final LinearLayout linlaDoctorExp;
        final AppCompatTextView txtDoctorExp;
        final LinearLayout linlaDoctorCharges;
        final AppCompatTextView txtDoctorCharges;
        final LinearLayout linlaClinicDistance;
        final AppCompatTextView txtClinicDistance;

        DoctorsVH(View v) {
            super(v);

            linlaDoctorContainer = v.findViewById(R.id.linlaDoctorContainer);
            txtDoctorName = v.findViewById(R.id.txtDoctorName);
            txtDoctorLikes = v.findViewById(R.id.txtDoctorLikes);
            txtClinicName = v.findViewById(R.id.txtClinicName);
            txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
            linlaImagesContainer = v.findViewById(R.id.linlaImagesContainer);
            listClinicImages = v.findViewById(R.id.listClinicImages);
            linlaDoctorExp = v.findViewById(R.id.linlaDoctorExp);
            txtDoctorExp = v.findViewById(R.id.txtDoctorExp);
            linlaDoctorCharges = v.findViewById(R.id.linlaDoctorCharges);
            txtDoctorCharges = v.findViewById(R.id.txtDoctorCharges);
            linlaClinicDistance = v.findViewById(R.id.linlaClinicDistance);
            txtClinicDistance = v.findViewById(R.id.txtClinicDistance);

            /* CONFIGURE THE RECYCLER VIEW */
            LinearLayoutManager llmAppointments = new LinearLayoutManager(activity);
            llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
            llmAppointments.setAutoMeasureEnabled(true);
            listClinicImages.setLayoutManager(llmAppointments);
            listClinicImages.setHasFixedSize(true);
            listClinicImages.setNestedScrollingEnabled(false);

            /* CONFIGURE THE ADAPTER */
            adapter = new DoctorClinicImagesAdapter(activity, arrImages);
            listClinicImages.setAdapter(adapter);
        }
    }
}