package co.zenpets.users.utils.adapters.boardings;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import co.zenpets.users.R;

public class UnitTypesAdapter extends ArrayAdapter<String> {

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final List<String> arrUnits;

    public UnitTypesAdapter(Activity activity, int resource, List<String> arrUnits) {
        super(activity, resource);
        this.arrUnits = arrUnits;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrUnits.size();
    }

    @Override
    public String getItem(int position) {
        return arrUnits.get(position);
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
            vi = inflater.inflate(R.layout.boarding_units_dropdown_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtUnitType = vi.findViewById(R.id.txtUnitType);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE UNIT TYPE **/
        String boardingUnitType = arrUnits.get(position);
        if (boardingUnitType != null)	{
            holder.txtUnitType.setText(boardingUnitType);
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
            vi = inflater.inflate(R.layout.boarding_units_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*	CAST THE LAYOUT ELEMENTS	*/
            holder.txtUnitType = vi.findViewById(R.id.txtUnitType);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE UNIT TYPE **/
        String boardingUnitType = arrUnits.get(position);
        if (boardingUnitType != null)	{
            holder.txtUnitType.setText(boardingUnitType);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtUnitType;
    }
}