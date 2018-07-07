package biz.zenpets.users.utils.adapters.pet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.details.pets.PetDetailsNew;
import biz.zenpets.users.utils.models.pets.pets.Pet;

public class UserPetsAdapter extends RecyclerView.Adapter<UserPetsAdapter.PetsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Pet> arrPets;

    public UserPetsAdapter(Activity activity, ArrayList<Pet> arrPets) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrPets = arrPets;
    }

    @Override
    public int getItemCount() {
        return arrPets.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final PetsVH holder, final int position) {
        final Pet data = arrPets.get(position);

        /* SET THE PET'S NAME */
        if (data.getPetName() != null)  {
            holder.txtPetName.setText(data.getPetName());
        }

        /* SET THE PET'S PROFILE PICTURE */
        if (data.getPetDisplayProfile() != null)   {
            Uri uri = Uri.parse(data.getPetDisplayProfile());
            holder.imgvwPetProfile.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.animal_paw_print_64)
                    .build();
            holder.imgvwPetProfile.setImageURI(request.getSourceUri());
        }

        /* SHOW THE PET POPUP MENU */
        holder.imgvwPetOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(activity, v);
                pm.getMenuInflater().inflate(R.menu.pm_user_pet_item, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())   {
                            case R.id.menuDetails:
                                Intent intent = new Intent(activity, PetDetailsNew.class);
                                intent.putExtra("PET_ID", data.getPetID());
                                activity.startActivity(intent);
                                break;
                            case R.id.menuEdit:
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

        /* SHOW THE PET DETAILS */
        holder.petCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PetDetailsNew.class);
                intent.putExtra("PET_ID", data.getPetID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public PetsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.user_pets_item, parent, false);

        return new PetsVH(itemView);
    }

    class PetsVH extends RecyclerView.ViewHolder	{
        final CardView petCard;
        final AppCompatImageView imgvwPetOptions;
        final SimpleDraweeView imgvwPetProfile;
        final AppCompatTextView txtPetName;
//        final AppCompatTextView txtPetAge;

        PetsVH(View v) {
            super(v);
            petCard = v.findViewById(R.id.petCard);
            imgvwPetOptions = v.findViewById(R.id.imgvwPetOptions);
            imgvwPetProfile = v.findViewById(R.id.imgvwPetProfile);
            txtPetName = v.findViewById(R.id.txtPetName);
//            txtPetAge = v.findViewById(R.id.txtPetAge);
        }
    }
}