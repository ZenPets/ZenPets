package biz.zenpets.trainers.utils.adapters.location;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.utils.models.location.LocalityData;

public class LocalitiesAdapter extends ArrayAdapter<LocalityData> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<LocalityData> arrLocalities;

    public LocalitiesAdapter(Activity activity, ArrayList<LocalityData> arrLocalities) {
        super(activity, R.layout.localities_row);

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrLocalities = arrLocalities;

        /** INSTANTIATE THE LAYOUT INFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrLocalities.size();
    }

    @Override
    public LocalityData getItem(int position) {
        return arrLocalities.get(position);
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
            vi = inflater.inflate(R.layout.localities_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtLocalityName = vi.findViewById(R.id.txtLocalityName);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE LOCALITIES NAME */
        String strLocalitiesName = arrLocalities.get(position).getLocalityName();
        if (strLocalitiesName != null)	{
            holder.txtLocalityName.setText(strLocalitiesName);
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
            vi = inflater.inflate(R.layout.localities_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtLocalityName = vi.findViewById(R.id.txtLocalityName);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE LOCALITIES NAME */
        String strLocalitiesName = arrLocalities.get(position).getLocalityName();
        if (strLocalitiesName != null)	{
            holder.txtLocalityName.setText(strLocalitiesName);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtLocalityName;
    }
}