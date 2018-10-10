package co.zenpets.kennels.utils.adapters.capacity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import co.zenpets.kennels.R;

public class PetCapacityAdapter extends ArrayAdapter<String> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final List<String> arrCapacity;

    public PetCapacityAdapter(Activity activity, int resource, List<String> arrCapacity) {
        super(activity, resource);

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrCapacity = arrCapacity;

        /* INSTANTIATE THE LAYOUT INFLATER INSTANCE */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrCapacity.size();
    }

    @Override
    public String getItem(int position) {
        return arrCapacity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE */
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE */
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS */
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi */
            vi = inflater.inflate(R.layout.pet_capacity_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtPetCapacity = vi.findViewById(R.id.txtPetCapacity);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE PET CAPACITY TEXT */
        String strPetCapacity = arrCapacity.get(position);
        if (strPetCapacity != null)	{
            holder.txtPetCapacity.setText(strPetCapacity);
        }

        return vi;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE */
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE */
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS */
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi */
            vi = inflater.inflate(R.layout.pet_capacity_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtPetCapacity = vi.findViewById(R.id.txtPetCapacity);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE PET CAPACITY TEXT */
        String strPetCapacity = arrCapacity.get(position);
        if (strPetCapacity != null)	{
            holder.txtPetCapacity.setText(strPetCapacity);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtPetCapacity;
    }
}