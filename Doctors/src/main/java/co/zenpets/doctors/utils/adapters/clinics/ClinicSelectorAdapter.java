package co.zenpets.doctors.utils.adapters.clinics;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinic;

public class ClinicSelectorAdapter extends ArrayAdapter<DoctorClinic> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    private LayoutInflater inflater = null;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<DoctorClinic> arrClinics;

    public ClinicSelectorAdapter(@NonNull Activity activity, @LayoutRes int resource, ArrayList<DoctorClinic> arrClinics) {
        super(activity, resource);

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrClinics = arrClinics;

        /* INSTANTIATE THE LAYOUT INFLATER */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrClinics.size();
    }

    @Override
    public DoctorClinic getItem(int position) {
        return arrClinics.get(position);
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
            vi = inflater.inflate(R.layout.clinic_selector_dropdown, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtClinicName = vi.findViewById(R.id.txtClinicName);
            holder.imgvwClinicLogo = vi.findViewById(R.id.imgvwClinicLogo);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE CLINIC LOGO */
        if (arrClinics.get(position).getClinicLogo() != null)   {
            Uri uri = Uri.parse(arrClinics.get(position).getClinicLogo());
            holder.imgvwClinicLogo.setImageURI(uri);
        }

        /* SET THE CLINIC NAME **/
        String strClinicName = arrClinics.get(position).getClinicName();
        if (strClinicName != null)	{
            holder.txtClinicName.setText(strClinicName);
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
            vi = inflater.inflate(R.layout.clinic_selector_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtClinicName = vi.findViewById(R.id.txtClinicName);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE CLINIC NAME **/
        String strClinicName = arrClinics.get(position).getClinicName();
        if (strClinicName != null)	{
            holder.txtClinicName.setText(strClinicName);
        }

        return vi;
    }

    private static class ViewHolder	{
        SimpleDraweeView imgvwClinicLogo;
        AppCompatTextView txtClinicName;
    }
}