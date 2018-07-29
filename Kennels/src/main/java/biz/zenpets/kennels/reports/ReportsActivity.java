package biz.zenpets.kennels.reports;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.adapters.kennels.KennelsSpinnerAdapter;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.kennels.Kennel;
import biz.zenpets.kennels.utils.models.kennels.Kennels;
import biz.zenpets.kennels.utils.models.kennels.KennelsAPI;
import biz.zenpets.kennels.utils.models.statistics.KennelView;
import biz.zenpets.kennels.utils.models.statistics.KennelViews;
import biz.zenpets.kennels.utils.models.statistics.KennelViewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL'S OWNERS ID **/
    private String KENNEL_OWNER_ID = null;

    /** THE SELECTED KENNEL ID **/
    String KENNEL_ID = null;

    /** THE START AND END DATE FOR THE MYSQL QUERY **/
    String END_DATE = null;
    String START_DATE = null;

    /** AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
    ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** AN ARRAY LIST TO STORE THE NUMBER OF KENNEL VIEWS **/
    ArrayList<KennelView> arrViews = new ArrayList<>();

    KennelViewsAdapter adapter;
    private float[] yData;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnKennels) Spinner spnKennels;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
//    @BindView(R.id.kennelViewsChart) LineChart kennelViewsChart;
    @BindView(R.id.kennelViewsChart) SparkView kennelViewsChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_reports);
        ButterKnife.bind(this);

        adapter = new KennelViewsAdapter(yData);
        kennelViewsChart.setFillType(SparkView.FillType.DOWN);

        /* CALCULATE THE START AND END DATE */
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            END_DATE = format.format(date);
//            Log.e("END DATE", END_DATE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(END_DATE));

            /* CALCULATE THE END DATE */
            calendar.add(Calendar.DATE, -30);
            Date dateEnd = new Date(calendar.getTimeInMillis());
            START_DATE = format.format(dateEnd);
//            Log.e("START DATE", START_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* GET THE LOGGED IN KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();
        if (KENNEL_OWNER_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennels();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
        }

        /* SELECT A KENNEL TO SHOW IT'S REVIEWS */
        spnKennels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED KENNEL ID */
                KENNEL_ID = arrKennels.get(position).getKennelID();

                /* FETCH THE STATS FOR THE SELECTED DURATION */
                fetchKennelViewStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        kennelViewsChart.setTouchEnabled(true);
//        kennelViewsChart.setDragEnabled(true);
//        kennelViewsChart.setScaleEnabled(true);
//        kennelViewsChart.setDrawGridBackground(false);
//
//        LimitLine llXAxis = new LimitLine(10f, "Index 10");
//        llXAxis.setLineWidth(4f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
//        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        llXAxis.setTextSize(10f);
//
////        XAxis xAxis = kennelViewsChart.getXAxis();
////        xAxis.enableGridDashedLine(10f, 10f, 0f);
//
//        LimitLine upper_limit = new LimitLine(20f, "Upper Limit");
//        upper_limit.setLineWidth(4f);
//        upper_limit.enableDashedLine(10f, 10f, 0f);
//        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        upper_limit.setTextSize(10f);
//
//        LimitLine lower_limit = new LimitLine(-10f, "Lower Limit");
//        lower_limit.setLineWidth(4f);
//        lower_limit.enableDashedLine(10f, 10f, 0f);
//        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        lower_limit.setTextSize(10f);
//
//        YAxis leftAxis = kennelViewsChart.getAxisLeft();
//        leftAxis.removeAllLimitLines();
//        leftAxis.addLimitLine(upper_limit);
//        leftAxis.addLimitLine(lower_limit);
////        leftAxis.setAxisMaxValue(20f);
////        leftAxis.setAxisMinValue(-0f);
//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
//        leftAxis.setDrawZeroLine(true);
//
//        kennelViewsChart.animateXY(2000, 2000);
//
//        kennelViewsChart.getAxisRight().setEnabled(false);
//
//        /* SET THE DESCRIPTION */
//        Description description = new Description();
//        description.setText("Views in the last 30 days");
//        kennelViewsChart.setDescription(description);
//
//        Legend l = kennelViewsChart.getLegend();
//        l.setForm(Legend.LegendForm.LINE);
    }

    /** FETCH THE STATS FOR THE SELECTED DURATION **/
    private void fetchKennelViewStats() {
        KennelViewsAPI api = ZenApiClient.getClient().create(KennelViewsAPI.class);
        Call<KennelViews> call = api.fetchDashboardKennelViews(START_DATE, END_DATE, KENNEL_ID);
        call.enqueue(new Callback<KennelViews>() {
            @Override
            public void onResponse(Call<KennelViews> call, Response<KennelViews> response) {
                Log.e("VIEWS RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getViews() != null)  {
                    arrViews = response.body().getViews();
                    if (arrViews.size() > 0)    {
                        ArrayList<String> xValues = new ArrayList<>();
                        ArrayList<Entry> yValues = new ArrayList<>();
                        yData = new float[arrViews.size()];
                        for (int i = 0; i < arrViews.size(); i++) {
                            KennelView view = arrViews.get(i);
//                        Log.e("DATE", view.getRecordDate());
//                        Log.e("VIEWS", view.getKennelViews());

                            /* ADD THE X VALUES (VIEW DATES) */
                            try {
                                String strCurrentDate = view.getRecordDate();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date newDate = format.parse(strCurrentDate);

                                format = new SimpleDateFormat("dd", Locale.getDefault());
                                String date = format.format(newDate);
                                xValues.add(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            adapter = new KennelViewsAdapter(yData);
                            kennelViewsChart.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            /* ADD THE KENNEL VIEWS */
                            yData[i] = Float.parseFloat(view.getKennelViews());

                            /* ADD THE Y VALUES (KENNEL VIEWS) */
//                        yValues.add(new Entry(Float.parseFloat(view.getKennelViews()), i));
                        }

                        Log.e("X SIZE", String.valueOf(xValues.size()));
                        Log.e("Y SIZE", String.valueOf(yValues.size()));
                        for (int i = 0; i < yValues.size(); i++) {
                            Log.e("VALUE", String.valueOf(yValues.get(i)));
                        }

//                    LineDataSet dataSet = new LineDataSet(yValues, "Kennel Views");
////                    dataSet.setDrawFilled(true);
////                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
////                    dataSet.setDrawIcons(true);
////                    dataSet.enableDashedLine(10f, 5f, 0f);
////                    dataSet.enableDashedHighlightLine(10f, 5f, 0f);
////                    dataSet.setColor(Color.BLACK);
////                    dataSet.setCircleColor(Color.BLACK);
////                    dataSet.setLineWidth(1f);
////                    dataSet.setCircleRadius(3f);
////                    dataSet.setDrawCircleHole(false);
////                    dataSet.setValueTextSize(9f);
////                    dataSet.setDrawFilled(true);
////                    dataSet.setFormLineWidth(1f);
////                    dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
////                    dataSet.setFormSize(15.f);
//
////                    if (Utils.getSDKInt() >= 18) {
////                        // fill drawable only supported on api level 18 and above
////                        Drawable drawable = ContextCompat.getDrawable(ReportsActivity.this, R.drawable.fade_red);
////                        dataSet.setFillDrawable(drawable);
////                    }
////                    else {
////                        dataSet.setFillColor(Color.BLACK);
////                    }
//
//                    LineData lineData = new LineData(dataSet);
//                    lineData.setValueFormatter(new ReportChartXAxisValueFormatter(xValues));
//
//                    kennelViewsChart.setData(lineData);
//                    kennelViewsChart.invalidate();
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<KennelViews> call, Throwable t) {
                Log.e("STATS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /**  THE X AXIS LABELS **/
    private class ReportChartXAxisValueFormatter implements IValueFormatter {
        ArrayList<String> labels;
        ReportChartXAxisValueFormatter(ArrayList<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            try {
                int index = (int) value;
//                Log.e("X AXIS", this.labels.get(index));
                return this.labels.get(index);
            } catch (Exception e)   {
                return null;
            }
        }
    }

    /** FETCH THE LIST OF KENNELS **/
    private void fetchKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByOwner(KENNEL_OWNER_ID);
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                Log.e("RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getKennels() != null) {
                    arrKennels = response.body().getKennels();
                    if (arrKennels.size() > 0) {
                        /* SET THE ADAPTER TO THE KENNELS SPINNER */
                        spnKennels.setAdapter(new KennelsSpinnerAdapter(
                                ReportsActivity.this,
                                R.layout.pet_capacity_row,
                                arrKennels));
                    }
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** THE ADAPTER TO DISPLAY THE KENNEL VIEWS **/
    private class KennelViewsAdapter extends SparkAdapter {

//        /** THE ARRAY INSTANCE **/
//        ArrayList<KennelView> list = new ArrayList<>();

        float[] floats;

        KennelViewsAdapter(float[] data) {
            this.floats = data;
        }

        @Override
        public int getCount() {
            return floats.length;
        }

        @Override
        public Object getItem(int index) {
            return floats[index];
        }

        @Override
        public float getY(int index) {
            return floats[index];
        }
    }
}