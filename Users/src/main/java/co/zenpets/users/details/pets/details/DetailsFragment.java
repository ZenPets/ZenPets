package co.zenpets.users.details.pets.details;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.pets.pets.Pet;
import co.zenpets.users.utils.models.pets.pets.PetsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {

    /** THE INCOMING PET ID **/
    private String PET_ID = null;

    /** THE PET DETAILS **/
    private String PET_TYPE_ID = null;
    private String PET_TYPE_NAME = null;
    private String BREED_ID = null;
    private String BREED_NAME = null;
    private String PET_NAME = null;
    private String PET_GENDER = null;
    private String PET_DOB = null;
    private String PET_AGE = null;
    private String PET_NEUTERED = null;
    private String PET_DISPLAY_PROFILE = null;
    private String PET_ACTIVE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwPetProfile) SimpleDraweeView imgvwPetProfile;
    @BindView(R.id.txtPetName) AppCompatTextView txtPetName;
    @BindView(R.id.txtPetBreed) AppCompatTextView txtPetBreed;
    @BindView(R.id.txtPetAge) AppCompatTextView txtPetAge;
    @BindView(R.id.txtPetGender) AppCompatTextView txtPetGender;
    @BindView(R.id.txtNeutered) AppCompatTextView txtNeutered;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.pet_details_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /** FETCH THE PET'S DETAILS **/
    private void fetchPetDetails() {
        PetsAPI api = ZenApiClient.getClient().create(PetsAPI.class);
        Call<Pet> call = api.fetchPetDetails(PET_ID);
        call.enqueue(new Callback<Pet>() {
            @Override
            public void onResponse(Call<Pet> call, Response<Pet> response) {
//                Log.e("DETAILS RESPONSE", String.valueOf(response.raw()));
                Pet pet = response.body();
                if (!pet.getError()) {

                    /* GET THE PET DETAILS */
                    PET_TYPE_ID = pet.getPetTypeID();
                    PET_TYPE_NAME = pet.getPetTypeName();
                    BREED_ID = pet.getBreedID();
                    BREED_NAME = pet.getBreedName();
                    PET_NAME = pet.getPetName();
                    PET_GENDER = pet.getPetGender();
                    String strPetDOB = pet.getPetDOB();
                    PET_NEUTERED = pet.getPetNeutered();
                    PET_DISPLAY_PROFILE = pet.getPetDisplayProfile();
                    PET_ACTIVE = pet.getPetActive();

                    /* CALCULATE THE PET'S AGE */
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        /* SET THE DATE OF BIRTH TO A CALENDAR DATE */
                        Date dtDOB = format.parse(strPetDOB);
                        Calendar calDOB = Calendar.getInstance();
                        calDOB.setTime(dtDOB);
                        int dobYear = calDOB.get(Calendar.YEAR);
                        int dobMonth = calDOB.get(Calendar.MONTH) + 1;
                        int dobDate = calDOB.get(Calendar.DATE);

                        /* SET THE CURRENT DATE TO A CALENDAR INSTANCE */
                        Calendar calNow = Calendar.getInstance();
                        int nowYear = calNow.get(Calendar.YEAR);
                        int nowMonth = calNow.get(Calendar.MONTH) + 1;
                        int nowDate = calNow.get(Calendar.DATE);

                        LocalDate dateDOB = new LocalDate(dobYear, dobMonth, dobDate);
                        LocalDate dateNOW = new LocalDate(nowYear, nowMonth, nowDate);
                        Period period = new Period(dateDOB, dateNOW, PeriodType.yearMonthDay());
                        Resources resources = AppPrefs.context().getResources();
                        if (period.getYears() == 0)   {
                            PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
                        } else if (period.getYears() == 1)    {
                            PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
                        } else if (period.getYears() > 1) {
                            PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /* SET THE PET'S BREED */
                    txtPetBreed.setText(BREED_NAME);

                    /* SET THE PET'S GENDER */
                    txtPetGender.setText(PET_GENDER);

                    /* SET THE PET'S AGE */
                    txtPetAge.setText(PET_AGE);

                    /* SET THE PET'S NEUTERED STATUS */
                    if (PET_NEUTERED != null)   {
                        if (PET_NEUTERED.equalsIgnoreCase("1"))   {
                            txtNeutered.setText(R.string.pet_details_status_neutered);
                        } else {
                            txtNeutered.setText(R.string.pet_details_status_not_neutered);
                        }
                    } else {
                        txtNeutered.setText(R.string.pet_details_status_not_neutered);
                    }

                    /* SET THE PET'S DISPLAY PROFILE */
                    if (PET_DISPLAY_PROFILE != null)    {
                        Uri uri = Uri.parse(PET_DISPLAY_PROFILE);
                        imgvwPetProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwPetProfile.setImageURI(request.getSourceUri());
                    }

                    /* SET THE PET'S NAME */
                    txtPetName.setText(PET_NAME);
                } else {
                    Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE PET DETAILS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Pet> call, Throwable t) {
//                Log.e("PET FAILURE", t.getMessage());
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID")) {
            PET_ID = bundle.getString("PET_ID");
            if (PET_ID != null) {
                /* SHOW THE PROGRESS AND FETCH THE PET'S DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchPetDetails();
            } else {
                Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }
}