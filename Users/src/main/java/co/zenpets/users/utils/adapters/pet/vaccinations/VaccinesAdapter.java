package co.zenpets.users.utils.adapters.pet.vaccinations;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.pets.vaccines.Vaccine;

public class VaccinesAdapter extends ArrayAdapter<Vaccine> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    private LayoutInflater inflater = null;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Vaccine> arrVaccines;

    public VaccinesAdapter(@NonNull Activity activity, ArrayList<Vaccine> arrVaccines) {
        super(activity, R.layout.vaccines_row);

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrVaccines = arrVaccines;

        /* INSTANTIATE THE LAYOUT INFLATER */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrVaccines.size();
    }

    @Override
    public Vaccine getItem(int position) {
        return arrVaccines.get(position);
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
            vi = inflater.inflate(R.layout.vaccines_dropdown_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtVaccineName = vi.findViewById(R.id.txtVaccineName);
            holder.txtVaccineDescription = vi.findViewById(R.id.txtVaccineDescription);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE VACCINE NAME **/
        String strVaccineName = arrVaccines.get(position).getVaccineName();
        if (strVaccineName != null)	{
            holder.txtVaccineName.setText(strVaccineName);
        }

        /* SET THE VACCINE DESCRIPTION */
        String strVaccineDescription = arrVaccines.get(position).getVaccineDescription();
        if (strVaccineDescription != null)  {
            holder.txtVaccineDescription.setText(strVaccineDescription);
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
            vi = inflater.inflate(R.layout.vaccines_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtVaccineName = vi.findViewById(R.id.txtVaccineName);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE VACCINE NAME **/
        String strVaccineName = arrVaccines.get(position).getVaccineName();
        if (strVaccineName != null)	{
            holder.txtVaccineName.setText(strVaccineName);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtVaccineName, txtVaccineDescription;
    }
}