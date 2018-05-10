package biz.zenpets.users.utils.adapters.pet;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.pets.pets.Pet;

public class PetSpinnerAdapter extends ArrayAdapter<Pet> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    private LayoutInflater inflater = null;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Pet> arrPets;

    public PetSpinnerAdapter(@NonNull Activity activity, ArrayList<Pet> arrPets) {
        super(activity, R.layout.pet_row_item);

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrPets = arrPets;

        /* INSTANTIATE THE LAYOUT INFLATER */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrPets.size();
    }

    @Override
    public Pet getItem(int position) {
        return arrPets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.pet_row_item, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.imgvwPetProfile = vi.findViewById(R.id.imgvwPetProfile);
            holder.txtPetName = vi.findViewById(R.id.txtPetName);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE PET NAME **/
        String strPetName = arrPets.get(position).getPetName();
        if (strPetName != null)	{
            holder.txtPetName.setText(strPetName);
        }

        /* SET THE PET'S DISPLAY PROFILE */
        if (arrPets.get(position).getPetDisplayProfile() != null)  {
            Uri uri = Uri.parse(arrPets.get(position).getPetDisplayProfile());
            holder.imgvwPetProfile.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.animal_paw_print_64)
                    .build();
            holder.imgvwPetProfile.setImageURI(request.getSourceUri());
        }

        return vi;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.pet_row_item, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.imgvwPetProfile = vi.findViewById(R.id.imgvwPetProfile);
            holder.txtPetName = vi.findViewById(R.id.txtPetName);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE PET NAME **/
        String strPetName = arrPets.get(position).getPetName();
        if (strPetName != null)	{
            holder.txtPetName.setText(strPetName);
        }

        /* SET THE PET'S DISPLAY PROFILE */
        if (arrPets.get(position).getPetDisplayProfile() != null) {
            Uri uri = Uri.parse(arrPets.get(position).getPetDisplayProfile());
            holder.imgvwPetProfile.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.animal_paw_print_64)
                    .build();
            holder.imgvwPetProfile.setImageURI(request.getSourceUri());
        }

        return vi;
    }

    private static class ViewHolder	{
        SimpleDraweeView imgvwPetProfile;
        AppCompatTextView txtPetName;
    }
}