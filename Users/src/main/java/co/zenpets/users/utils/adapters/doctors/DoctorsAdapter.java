package co.zenpets.users.utils.adapters.doctors;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.details.doctors.DoctorDetailsNew;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.clinics.promoted.PromotedClinicsAdapter;
import co.zenpets.users.utils.models.clinics.images.ClinicImage;
import co.zenpets.users.utils.models.clinics.promotions.Promotion;
import co.zenpets.users.utils.models.doctors.list.Doctor;

public class DoctorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppPrefs getApp()	{
        return (AppPrefs) AppPrefs.context().getApplicationContext();
    }

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    private static final int ITEM = 0;
    private static final int LOADING = 2;

    private boolean isLoadingAdded = false;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private ArrayList<Doctor> arrDoctors = new ArrayList<>();

    /** THE PROMOTED CLINICS ADAPTER AND ARRAY LIST **/
    private PromotedClinicsAdapter adapterPromoted;
    private ArrayList<Promotion> arrPromotions = new ArrayList<>();

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private DoctorClinicImagesAdapter adapterImages;
    private ArrayList<ClinicImage> arrImages = new ArrayList<>();

    /** THE DATA TYPES FOR CALCULATING THE LIKES PERCENTAGE  **/
    private int TOTAL_LIKES = 0;
    private int TOTAL_VOTES = 0;

    /** A LATLNG INSTANCE TO HOLD THE CURRENT COORDINATES **/
    private LatLng LATLNG_ORIGIN;

    /** GET THE USER ID AND THE CURRENT DATE **/
    String USER_ID = null;
    String CURRENT_DATE = null;

    public DoctorsAdapter(Activity activity, ArrayList<Doctor> arrDoctors, LatLng LATLNG_ORIGIN) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrDoctors = arrDoctors;

        /* CAST THE CONTENTS OF THE LATLNG INSTANCE TO THE LOCAL INSTANCE */
        this.LATLNG_ORIGIN = LATLNG_ORIGIN;

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
//        Log.e("USER ID", USER_ID);

        /* GET THE CURRENT DATE IN THE REQUIRED FORMAT */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        CURRENT_DATE = format.format(date);
//        Log.e("CURRENT DATE", CURRENT_DATE);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Doctor data = arrDoctors.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final KennelsVH vh = (KennelsVH) holder;

                arrPromotions = data.getPromotions();
                if (arrPromotions != null && arrPromotions.size() > 0)    {
                    /* SHOW THE LIST OF PROMOTED ADOPTIONS AND HIDE THE ADOPTION ITEM */
                    adapterPromoted = new PromotedClinicsAdapter(activity, arrPromotions);
                    vh.listPromoted.setAdapter(adapterPromoted);
                    vh.listPromoted.setVisibility(View.VISIBLE);
                    vh.cardDoctor.setVisibility(View.GONE);
                } else if (data.getClinicID() != null) {

                    ArrayList<ClinicImage> arrImages = data.getImages();
                    if (arrImages != null)  {
                        if (arrImages.size() > 0)   {
                            /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                            adapterImages = new DoctorClinicImagesAdapter(activity, arrImages);
                            vh.listClinicImages.setAdapter(adapterImages);

                            /* SHOW THE IMAGES CONTAINER */
                            vh.linlaImagesContainer.setVisibility(View.VISIBLE);
                        } else {
                            /* HIDE THE IMAGES CONTAINER */
                            vh.linlaImagesContainer.setVisibility(View.GONE);
                        }
                    } else {
                        /* HIDE THE IMAGES CONTAINER */
                        vh.linlaImagesContainer.setVisibility(View.GONE);
                    }

                    /* SET THE CLINIC NAME */
                    vh.txtClinicName.setText(data.getClinicName());

                    /* SET THE CLINIC ADDRESS */
                    String clinicAddress = data.getClinicAddress();
                    String cityName = data.getCityName();
                    String clinicPinCode = data.getClinicPinCode();
                    vh.txtClinicAddress.setText(activity.getString(R.string.doctor_list_address_placeholder, clinicAddress, cityName, clinicPinCode));

//                    /* SET THE CLINIC DISTANCE */
//                    Double latitude = Double.valueOf(data.getClinicLatitude());
//                    Double longitude = Double.valueOf(data.getClinicLongitude());
//                    LatLng LATLNG_DESTINATION = new LatLng(latitude, longitude);
//                    String strOrigin = LATLNG_ORIGIN.latitude + "," + LATLNG_ORIGIN.longitude;
//                    String strDestination = LATLNG_DESTINATION.latitude + "," + LATLNG_DESTINATION.longitude;
//                    String strSensor = "false";
//                    String strKey = activity.getString(R.string.google_directions_api_key);
//                    DistanceAPI api = ZenDistanceClient.getClient().create(DistanceAPI.class);
//                    Call<String> call = api.json(strOrigin, strDestination, strSensor, strKey);
//                    call.enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            Log.e("DISTANCE RAW", String.valueOf(response.raw()));
//                            try {
//                                String strDistance = response.body();
//                                JSONObject JORootDistance = new JSONObject(strDistance);
//                                JSONArray array = JORootDistance.getJSONArray("routes");
//                                JSONObject JORoutes = array.getJSONObject(0);
//                                JSONArray JOLegs= JORoutes.getJSONArray("legs");
//                                JSONObject JOSteps = JOLegs.getJSONObject(0);
//                                JSONObject JODistance = JOSteps.getJSONObject("distance");
//                                if (JODistance.has("text")) {
//                                    String distance = JODistance.getString("text");
//                                    String strTilde = activity.getString(R.string.generic_tilde);
//                                    vh.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, distance));
//                                } else {
//                                    String distance = "Unknown";
//                                    String strInfinity = activity.getString(R.string.generic_infinity);
//                                    vh.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Crashlytics.logException(t);
//                            String distance = "Unknown";
//                            String strInfinity = activity.getString(R.string.generic_infinity);
//                            vh.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
//                        }
//                    });

                    /* SET THE CLINIC DISTANCE */
                    String strClinicDistance = data.getClinicDistance();
                    if (strClinicDistance != null
                            && !strClinicDistance.equals("Unknown")
                            && !strClinicDistance.equalsIgnoreCase("null"))  {
                        String strTilde = activity.getString(R.string.generic_tilde);
                        vh.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, strClinicDistance));
                    } else {
                        String strInfinity = activity.getString(R.string.generic_infinity);
                        vh.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, strClinicDistance));
                    }

                    /* SET THE DOCTOR'S NAME */
                    String doctorPrefix = data.getDoctorPrefix();
                    String doctorName = data.getDoctorName();
                    vh.txtDoctorName.setText(activity.getString(R.string.appointment_creator_details_doctor, doctorPrefix, doctorName));

                    /* SET THE DOCTOR'S EXPERIENCE */
                    String strExp = activity.getString(R.string.doctor_list_doctor_experience_text);
                    String docExperience = data.getDoctorExperience();
                    vh.txtDoctorExp.setText(activity.getString(R.string.doctor_list_doctor_experience_placeholder, docExperience, strExp));

                    /* SET THE DOCTOR'S CHARGES */
                    String currency = data.getCurrencySymbol();
                    String charges = data.getDoctorCharges();
                    vh.txtDoctorCharges.setText(activity.getString(R.string.doctor_details_doc_charges_placeholder, currency, charges));

                    /* SET THE REVIEW VOTE STATS */
                    vh.txtDoctorLikes.setText(data.getDoctorVoteStats());

//                    /* SET THE TOTAL NUMBER OF POSITIVE REVIEWS AND PERCENTAGE OF TOTAL REVIEWS */
//                    String doctorReviews = data.getDoctorReviews();
//                    String doctorPositives = data.getDoctorPositives();
//                    if (doctorReviews != null && !doctorReviews.equals("null") && !doctorReviews.equals(""))    {
//                        TOTAL_VOTES = Integer.parseInt(doctorReviews);
//                        TOTAL_LIKES = Integer.parseInt(doctorPositives);
//
//                        /* CALCULATE THE PERCENTAGE OF LIKES */
//                        double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
//                        int finalPercentLikes = (int)percentLikes;
//                        String strLikesPercentage = String.valueOf(finalPercentLikes) + "%";
//
//                        /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
//                        Resources resReviews = AppPrefs.context().getResources();
//                        String reviewQuantity = null;
//                        if (TOTAL_VOTES == 0)   {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        } else if (TOTAL_VOTES == 1)    {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        } else if (TOTAL_VOTES > 1) {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        }
//                        String strVotes = reviewQuantity;
//                        String open = activity.getString(R.string.doctor_list_votes_open);
//                        String close = activity.getString(R.string.doctor_list_votes_close);
//                        vh.txtDoctorLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
//                    } else {
//                        TOTAL_VOTES = 0;
//                        TOTAL_LIKES = 0;
//
//                        String strLikesPercentage = "0%";
//
//                        /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
//                        Resources resReviews = AppPrefs.context().getResources();
//                        String reviewQuantity = null;
//                        if (TOTAL_VOTES == 0)   {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        } else if (TOTAL_VOTES == 1)    {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        } else if (TOTAL_VOTES > 1) {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        }
//                        String strVotes = reviewQuantity;
//                        String open = activity.getString(R.string.doctor_list_votes_open);
//                        String close = activity.getString(R.string.doctor_list_votes_close);
//                        vh.txtDoctorLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
//                    }

                    /* SHOW THE DOCTOR DETAILS */
                    vh.cardDoctor.setOnClickListener(new View.OnClickListener() {
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

                    /* SHOW THE KENNEL ITEM AND HIDE THE LIST OF PROMOTED ADOPTION */
                    vh.cardDoctor.setVisibility(View.VISIBLE);
                    vh.listPromoted.setVisibility(View.GONE);
                } else {
                    vh.cardDoctor.setVisibility(View.GONE);
                    vh.listPromoted.setVisibility(View.GONE);
                }
                break;
            case LOADING:
                break;
        }
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
        viewHolder = new KennelsVH(v1);
        return viewHolder;
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

    private Doctor getItem(int position) {
        return arrDoctors.get(position);
    }

    class KennelsVH extends RecyclerView.ViewHolder {

        RecyclerView listPromoted;
        CardView cardDoctor;
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

        KennelsVH(View v) {
            super(v);

            listPromoted = v.findViewById(R.id.listPromoted);
            cardDoctor = v.findViewById(R.id.cardDoctor);
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

            /* CONFIGURE THE CLINIC IMAGES ADAPTER */
            adapterImages = new DoctorClinicImagesAdapter(activity, arrImages);
            listClinicImages.setAdapter(adapterImages);

            /* CONFIGURE THE RECYCLER VIEW */
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            manager.setAutoMeasureEnabled(true);
            listPromoted.setLayoutManager(manager);
            listPromoted.setHasFixedSize(true);
            listPromoted.setNestedScrollingEnabled(false);

            /* CONFIGURE THE PROMOTED CLINICS ADAPTER */
            adapterPromoted = new PromotedClinicsAdapter(activity, arrPromotions);
            listPromoted.setAdapter(adapterPromoted);
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}