package biz.zenpets.users.details.pets.details;

import android.net.Uri;
import android.os.Bundle;
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

import biz.zenpets.users.R;
import biz.zenpets.users.utils.helpers.pets.pet.FetchPetDetails;
import biz.zenpets.users.utils.helpers.pets.pet.FetchPetDetailsInterface;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment implements FetchPetDetailsInterface {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    @Override
    public void petDetails(String[] result) {
        PET_TYPE_ID = result[0];
        PET_TYPE_NAME = result[1];
        BREED_ID = result[2];
        BREED_NAME = result[3];
        PET_NAME = result[4];
        PET_GENDER = result[5];
        PET_AGE = result[6];
        PET_NEUTERED = result[7];
        PET_DISPLAY_PROFILE = result[8];
        PET_ACTIVE = result[9];

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

        /* HIDE THE PROGRESS AFTER FETCHING THE PET DETAILS */
        linlaProgress.setVisibility(View.GONE);
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID")) {
            PET_ID = bundle.getString("PET_ID");
            if (PET_ID != null) {
                /* SHOW THE PROGRESS AND FETCH THE PET'S DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                new FetchPetDetails(this).execute(PET_ID);
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