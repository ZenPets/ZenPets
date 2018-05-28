package biz.zenpets.users.utils.adapters.doctors;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.details.doctors.DoctorDetailsNew;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.classes.ZenDistanceClient;
import biz.zenpets.users.utils.helpers.clinics.distance.DistanceAPI;
import biz.zenpets.users.utils.models.clinics.images.ClinicImage;
import biz.zenpets.users.utils.models.clinics.images.ClinicImages;
import biz.zenpets.users.utils.models.clinics.images.ClinicImagesAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.reviews.Review;
import biz.zenpets.users.utils.models.reviews.Reviews;
import biz.zenpets.users.utils.models.reviews.ReviewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewDoctorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppPrefs getApp()	{
        return (AppPrefs) AppPrefs.context();
    }

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private final ArrayList<Doctor> arrDoctors;
    private Activity activity;

    private boolean isLoadingAdded = false;

    /** THE ORIGIN **/
    private LatLng LATLNG_ORIGIN;

    /** THE DATA TYPES FOR CALCULATING THE LIKES PERCENTAGE  **/
    private int TOTAL_LIKES = 0;
    private int TOTAL_VOTES = 0;

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private ArrayList<ClinicImage> arrImages = new ArrayList<>();
    private DoctorClinicImagesAdapter adapter;

    public NewDoctorsAdapter(Activity activity) {
        this.activity = activity;
        this.arrDoctors = new ArrayList<>();
        String strLatitude = getApp().getOriginLatitude();
        String strLongitude = getApp().getOriginLongitude();
        LATLNG_ORIGIN = new LatLng(Double.valueOf(strLatitude), Double.valueOf(strLongitude));
        Log.e("LAT LNG", String.valueOf(LATLNG_ORIGIN));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.endless_item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.doctors_item, parent, false);
        viewHolder = new DoctorsVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Doctor data = arrDoctors.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final DoctorsVH doctorsVH = (DoctorsVH) holder;

                /* SET THE CLINIC NAME */
                doctorsVH.txtClinicName.setText(data.getClinicName());

                /* SET THE CLINIC ADDRESS */
                String clinicAddress = data.getClinicAddress();
                String cityName = data.getCityName();
                String clinicPinCode = data.getClinicPinCode();
                doctorsVH.txtClinicAddress.setText(activity.getString(R.string.doctor_list_address_placeholder, clinicAddress, cityName, clinicPinCode));

                /* SET THE CLINIC DISTANCE */
                if (data.getClinicLatitude() != null && data.getClinicLongitude() != null)  {
                    Double latitude = Double.valueOf(data.getClinicLatitude());
                    Double longitude = Double.valueOf(data.getClinicLongitude());
                    LatLng LATLNG_DESTINATION = new LatLng(latitude, longitude);
                    String strOrigin = LATLNG_ORIGIN.latitude + "," + LATLNG_ORIGIN.longitude;
                    String strDestination = LATLNG_DESTINATION.latitude + "," + LATLNG_DESTINATION.longitude;
                    String strSensor = "false";
                    String strKey = activity.getString(R.string.google_directions_api_key);
                    DistanceAPI api = ZenDistanceClient.getClient().create(DistanceAPI.class);
                    Call<String> call = api.json(strOrigin, strDestination, strSensor, strKey);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                String strDistance = response.body();
                                JSONObject JORootDistance = new JSONObject(strDistance);
                                JSONArray array = JORootDistance.getJSONArray("routes");
                                JSONObject JORoutes = array.getJSONObject(0);
                                JSONArray JOLegs= JORoutes.getJSONArray("legs");
                                JSONObject JOSteps = JOLegs.getJSONObject(0);
                                JSONObject JODistance = JOSteps.getJSONObject("distance");
                                if (JODistance.has("text")) {
                                    String distance = JODistance.getString("text");
                                    String strTilde = activity.getString(R.string.generic_tilde);
                                    doctorsVH.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, distance));
                                } else {
                                    String distance = "Unknown";
                                    String strInfinity = activity.getString(R.string.generic_infinity);
                                    doctorsVH.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Crashlytics.logException(t);
                            String distance = "Unknown";
                            String strInfinity = activity.getString(R.string.generic_infinity);
                            doctorsVH.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
                        }
                    });
                }

                /* SET THE DOCTOR'S NAME */
                String doctorPrefix = data.getDoctorPrefix();
                String doctorName = data.getDoctorName();
                doctorsVH.txtDoctorName.setText(activity.getString(R.string.appointment_creator_details_doctor, doctorPrefix, doctorName));

                /* SET THE DOCTOR'S EXPERIENCE */
                String strExp = activity.getString(R.string.doctor_list_doctor_experience_text);
                String docExperience = data.getDoctorExperience();
                doctorsVH.txtDoctorExp.setText(activity.getString(R.string.doctor_list_doctor_experience_placeholder, docExperience, strExp));

                /* SET THE DOCTOR'S CHARGES */
                String currency = data.getCurrencySymbol();
                String charges = data.getDoctorCharges();
                doctorsVH.txtDoctorCharges.setText(activity.getString(R.string.doctor_details_doc_charges_placeholder, currency, charges));

                /* GET THE TOTAL NUMBER OF POSITIVE REVIEWS */
                ReviewsAPI apiYes = ZenApiClient.getClient().create(ReviewsAPI.class);
                Call<Reviews> callYes = apiYes.fetchPositiveReviews(data.getDoctorID(), "Yes");
                callYes.enqueue(new Callback<Reviews>() {
                    @Override
                    public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                        if (response.body() != null && response.body().getReviews() != null)    {
                            ArrayList<Review> arrReview = response.body().getReviews();
                            TOTAL_LIKES = arrReview.size();
                            TOTAL_VOTES = TOTAL_VOTES + arrReview.size();

                            /* GET THE TOTAL NUMBER OF NEGATIVE REVIEWS */
                            ReviewsAPI apiNo = ZenApiClient.getClient().create(ReviewsAPI.class);
                            Call<Reviews> callNo = apiNo.fetchNegativeReviews(data.getDoctorID(), "No");
                            callNo.enqueue(new Callback<Reviews>() {
                                @Override
                                public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                                    if (response.body() != null && response.body().getReviews() != null)    {
                                        ArrayList<Review> arrReview = response.body().getReviews();
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
                                        doctorsVH.txtDoctorLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                                    }
                                }

                                @Override
                                public void onFailure(Call<Reviews> call, Throwable t) {
                                    Crashlytics.logException(t);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Reviews> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });

                /* SET THE CLINIC IMAGES */
                ClinicImagesAPI apiImages = ZenApiClient.getClient().create(ClinicImagesAPI.class);
                Call<ClinicImages> callImages = apiImages.fetchClinicImages(data.getClinicID());
                callImages.enqueue(new Callback<ClinicImages>() {
                    @Override
                    public void onResponse(Call<ClinicImages> call, Response<ClinicImages> response) {
                        if (response.body() != null && response.body().getImages() != null) {
                            arrImages = response.body().getImages();
                            if (arrImages.size() > 0)   {
                                /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                                adapter = new DoctorClinicImagesAdapter(activity, arrImages);
                                doctorsVH.listClinicImages.setAdapter(adapter);

                                /* SHOW THE IMAGES CONTAINER */
                                doctorsVH.linlaImagesContainer.setVisibility(View.VISIBLE);
                            } else {
                                /* HIDE THE IMAGES CONTAINER */
                                doctorsVH.linlaImagesContainer.setVisibility(View.GONE);
                            }
                        } else {
                            /* HIDE THE IMAGES CONTAINER */
                            doctorsVH.linlaImagesContainer.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ClinicImages> call, Throwable t) {
                        Crashlytics.logException(t);
                    }
                });

                /* SHOW THE DOCTOR DETAILS */
                doctorsVH.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, DoctorDetailsNew.class);
                        intent.putExtra("DOCTOR_ID", data.getDoctorID());
                        intent.putExtra("CLINIC_ID", data.getClinicID());
                        activity.startActivity(intent);
                    }
                });
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrDoctors == null ? 0 : arrDoctors.size();
    }

    public void addAll(ArrayList<Doctor> arrDoctors) {
        for (Doctor doctor : arrDoctors) {
            add(doctor);
        }
    }

    private void add(Doctor doctor) {
        arrDoctors.add(doctor);
        notifyItemInserted(arrDoctors.size() - 1);
    }

    private void remove(Doctor doctor) {
        int position = arrDoctors.indexOf(doctor);
        if (position > -1) {
            arrDoctors.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Doctor());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = arrDoctors.size() - 1;
        Doctor item = getItem(position);

        if (item != null) {
            arrDoctors.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Doctor getItem(int position) {
        return arrDoctors.get(position);
    }

    /** THE DOCTORS VIEW HOLDER **/
    protected class DoctorsVH extends RecyclerView.ViewHolder {

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


    protected class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}