package biz.zenpets.location;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class CitiesAdapter extends ArrayAdapter<String> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final List<String> arrCities;

    public CitiesAdapter(Activity activity, int resource, List<String> arrCities) {
        super(activity, resource);
        this.activity = activity;
        this.arrCities = arrCities;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrCities.size();
    }

    @Override
    public String getItem(int position) {
        return arrCities.get(position);
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
            vi = inflater.inflate(R.layout.cities_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtCityName = vi.findViewById(R.id.txtCityName);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE CITY NAME */
        String strCityName = arrCities.get(position);
        if (strCityName != null)	{
            holder.txtCityName.setText(strCityName);
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
            vi = inflater.inflate(R.layout.cities_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtCityName = vi.findViewById(R.id.txtCityName);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE CITY NAME */
        String strCityName = arrCities.get(position);
        if (strCityName != null)	{
            holder.txtCityName.setText(strCityName);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtCityName;
    }
}