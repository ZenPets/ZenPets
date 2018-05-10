package biz.zenpets.users.utils.adapters.pet.records;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.pets.records.RecordType;

public class RecordTypesAdapter extends ArrayAdapter<RecordType> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
    private LayoutInflater inflater = null;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<RecordType> arrRecordTypes;

    public RecordTypesAdapter(@NonNull Activity activity, ArrayList<RecordType> arrRecordTypes) {
        super(activity, R.layout.pet_types_row);

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrRecordTypes = arrRecordTypes;

        /* INSTANTIATE THE LAYOUT INFLATER */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrRecordTypes.size();
    }

    @Override
    public RecordType getItem(int position) {
        return arrRecordTypes.get(position);
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
            vi = inflater.inflate(R.layout.pet_types_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtPetType = vi.findViewById(R.id.txtPetType);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE RECORD TYPE NAME **/
        String strPetTypeName = arrRecordTypes.get(position).getRecordTypeName();
        if (strPetTypeName != null)	{
            holder.txtPetType.setText(strPetTypeName);
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
            vi = inflater.inflate(R.layout.pet_types_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*	CAST THE LAYOUT ELEMENTS	*/
            holder.txtPetType = vi.findViewById(R.id.txtPetType);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE RECORD TYPE NAME **/
        String strPetTypeName = arrRecordTypes.get(position).getRecordTypeName();
        if (strPetTypeName != null)	{
            holder.txtPetType.setText(strPetTypeName);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtPetType;
    }
}