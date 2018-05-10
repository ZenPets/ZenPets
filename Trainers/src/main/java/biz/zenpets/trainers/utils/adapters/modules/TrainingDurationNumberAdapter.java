package biz.zenpets.trainers.utils.adapters.modules;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import biz.zenpets.trainers.R;

public class TrainingDurationNumberAdapter extends ArrayAdapter<String> {

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final List<String> arrSessions;

    public TrainingDurationNumberAdapter(Activity activity, int resource, List<String> arrSessions) {
        super(activity, resource);
        this.arrSessions = arrSessions;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrSessions.size();
    }

    @Override
    public String getItem(int position) {
        return arrSessions.get(position);
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
            vi = inflater.inflate(R.layout.training_module_sessions_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtModuleSessions = vi.findViewById(R.id.txtModuleSessions);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE MODULE SESSIONS TEXT */
        String strSessions = arrSessions.get(position);
        if (strSessions != null)	{
            holder.txtModuleSessions.setText(strSessions);
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
            vi = inflater.inflate(R.layout.training_module_sessions_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE */
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtModuleSessions = vi.findViewById(R.id.txtModuleSessions);

            /* SET THE TAG TO "vi" */
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE */
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE MODULE SESSIONS TEXT */
        String strSessions = arrSessions.get(position);
        if (strSessions != null)	{
            holder.txtModuleSessions.setText(strSessions);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtModuleSessions;
    }
}