package co.zenpets.kennels.reports;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.helpers.MyMarkerView;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.statistics.KennelView;
import co.zenpets.kennels.utils.models.statistics.KennelViews;
import co.zenpets.kennels.utils.models.statistics.KennelViewsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN KENNEL ID **/
    String KENNEL_ID = null;

    /** THE START AND END DATE FOR THE MYSQL QUERY **/
    String END_DATE = null;
    String START_DATE = null;

    /** THE SELECTED DURATION **/
    int SELECTED_DURATION = 30;

    /** AN ARRAY LIST TO STORE THE NUMBER OF KENNEL VIEWS **/
    ArrayList<KennelView> arrViews = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.groupDurationSelector) SegmentedButtonGroup groupDurationSelector;
    @BindView(R.id.txtViews) TextView txtViews;
    @BindView(R.id.txtClicks) TextView txtClicks;
    @BindView(R.id.txtEnquiries) TextView txtEnquiries;
    @BindView(R.id.txtReviews) TextView txtReviews;
    @BindView(R.id.kennelViewsChart) LineChart kennelViewsChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_reports);
        ButterKnife.bind(this);

        /* GET THE START AND END DATE FOR THE DURATION */
        calculateDates();

        /* GET THE LOGGED IN KENNEL'S ID */
        KENNEL_ID = getApp().getKennelID();
        if (KENNEL_ID != null)    {

            /* SHOW THE PROGRESS AND FETCH THE STATS FOR THE SELECTED DURATION */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennelViewStats();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
        }

        /* CONFIGURE THE LINE CHART */
        configLineChart();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        groupDurationSelector.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                if (position == 0)  {
                    /* GET THE START AND END DATE FOR THE DURATION AGAIN */
                    SELECTED_DURATION = 30;
                    calculateDates();
//                    txtSummaryLabel.setText(getString(R.string.report_duration_summary_placeholder, String.valueOf(SELECTED_DURATION)));
                    fetchKennelViewStats();
                } else if (position == 1)   {
                    /* GET THE START AND END DATE FOR THE DURATION AGAIN */
                    SELECTED_DURATION = 60;
                    calculateDates();
//                    txtSummaryLabel.setText(getString(R.string.report_duration_summary_placeholder, String.valueOf(SELECTED_DURATION)));
                    fetchKennelViewStats();
                } else if (position == 2)   {
                    /* GET THE START AND END DATE FOR THE DURATION AGAIN */
                    SELECTED_DURATION = 90;
                    calculateDates();
//                    txtSummaryLabel.setText(getString(R.string.report_duration_summary_placeholder, String.valueOf(SELECTED_DURATION)));
                    fetchKennelViewStats();
                }
            }
        });
    }

    /** FETCH THE STATS FOR THE SELECTED DURATION **/
    private void fetchKennelViewStats() {
        KennelViewsAPI api = ZenApiClient.getClient().create(KennelViewsAPI.class);
        Call<KennelViews> call = api.fetchDashboardKennelViews(START_DATE, END_DATE, KENNEL_ID);
        call.enqueue(new Callback<KennelViews>() {
            @Override
            public void onResponse(Call<KennelViews> call, Response<KennelViews> response) {
//                Log.e("VIEWS RAW", String.valueOf(response.raw()));

                /* GET THE TOTAL VIEWS */
                int totalViews = Integer.parseInt(response.body().getTotalViews());
                Resources resViews = AppPrefs.context().getResources();
                String strFinalViews = null;
                if (totalViews == 0)   {
                    strFinalViews = resViews.getQuantityString(R.plurals.views, totalViews, totalViews);
                } else if (totalViews == 1)    {
                    strFinalViews = resViews.getQuantityString(R.plurals.views, totalViews, totalViews);
                } else if (totalViews > 1) {
                    strFinalViews = resViews.getQuantityString(R.plurals.views, totalViews, totalViews);
                }
                txtViews.setText(strFinalViews);

                /* GET THE TOTAL CLICKS */
                int totalClicks = Integer.parseInt(response.body().getTotalClicks());
                Resources resClicks = AppPrefs.context().getResources();
                String strFinalClicks = null;
                if (totalViews == 0)   {
                    strFinalClicks = resClicks.getQuantityString(R.plurals.clicks, totalClicks, totalClicks);
                } else if (totalViews == 1)    {
                    strFinalClicks = resClicks.getQuantityString(R.plurals.clicks, totalClicks, totalClicks);
                } else if (totalViews > 1) {
                    strFinalClicks = resClicks.getQuantityString(R.plurals.clicks, totalClicks, totalClicks);
                }
                txtClicks.setText(strFinalClicks);

                /* GET THE TOTAL ENQUIRIES */
                int totalEnquiries = Integer.parseInt(response.body().getTotalEnquiries());
                Resources resEnquiries = AppPrefs.context().getResources();
                String strFinalEnquiries = null;
                if (totalEnquiries == 0)   {
                    strFinalEnquiries = resEnquiries.getQuantityString(R.plurals.enquiries, totalEnquiries, totalEnquiries);
                } else if (totalEnquiries == 1)    {
                    strFinalEnquiries = resEnquiries.getQuantityString(R.plurals.enquiries, totalEnquiries, totalEnquiries);
                } else if (totalEnquiries > 1) {
                    strFinalEnquiries = resEnquiries.getQuantityString(R.plurals.enquiries, totalEnquiries, totalEnquiries);
                }
                txtEnquiries.setText(strFinalEnquiries);

                /* GET THE TOTAL REVIEWS */
                int totalReviews = Integer.parseInt(response.body().getTotalReviews());
                Resources resReviews = AppPrefs.context().getResources();
                String strFinalReviews = null;
                if (totalReviews == 0)   {
                    strFinalReviews = resReviews.getQuantityString(R.plurals.reviews, totalReviews, totalReviews);
                } else if (totalReviews == 1)    {
                    strFinalReviews = resReviews.getQuantityString(R.plurals.reviews, totalReviews, totalReviews);
                } else if (totalReviews > 1) {
                    strFinalReviews = resReviews.getQuantityString(R.plurals.reviews, totalReviews, totalReviews);
                }
                txtReviews.setText(strFinalReviews);

                /* GET THE KENNEL VIEWS DETAILS */
                if (response.body() != null && response.body().getViews() != null)  {
                    arrViews = response.body().getViews();
                    if (arrViews.size() > 0)    {
                        ArrayList<String> xValues = new ArrayList<>();
                        ArrayList<Entry> yValues = new ArrayList<>();
                        for (int i = 0; i < arrViews.size(); i++) {
                            KennelView view = arrViews.get(i);

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

                            /* ADD THE Y VALUES (KENNEL VIEWS) */
                            yValues.add(new Entry(i, Float.parseFloat(view.getKennelViews())));
                        }

                        LineDataSet dataSet = new LineDataSet(yValues, "Kennel Views");
                        dataSet.setDrawFilled(true);
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setDrawIcons(true);
                        dataSet.enableDashedLine(10f, 5f, 0f);
                        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
                        dataSet.setColor(Color.BLACK);
                        dataSet.setCircleColor(Color.BLACK);
                        dataSet.setLineWidth(1f);
                        dataSet.setCircleRadius(3f);
                        dataSet.setDrawCircleHole(false);
                        dataSet.setValueTextSize(9f);
                        dataSet.setDrawFilled(true);
                        dataSet.setFormLineWidth(1f);
                        dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                        dataSet.setFormSize(15.f);

                        if (Utils.getSDKInt() >= 18) {
                            Drawable drawable = ContextCompat.getDrawable(ReportsActivity.this, R.drawable.fade_red);
                            dataSet.setFillDrawable(drawable);
                        }
                        else {
                            dataSet.setFillColor(Color.BLACK);
                        }

                        LineData lineData = new LineData(dataSet);
//                        lineData.setValueFormatter(new ReportChartXAxisValueFormatter(xValues));

                        kennelViewsChart.setData(lineData);
                        kennelViewsChart.invalidate();
                    } else {
                    }
                } else {
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<KennelViews> call, Throwable t) {
//                Log.e("STATS FAILURE", t.getMessage());
            }
        });
    }

//    /**  THE X AXIS LABELS **/
//    private class ReportChartXAxisValueFormatter implements IValueFormatter {
//        ArrayList<String> labels;
//        ReportChartXAxisValueFormatter(ArrayList<String> labels) {
//            this.labels = labels;
//        }
//
//        @Override
//        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//            try {
//                int index = (int) value;
////                Log.e("X AXIS", this.labels.get(index));
//                return this.labels.get(index);
//            } catch (Exception e)   {
//                return null;
//            }
//        }
//    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Kennel Reports";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /** CONFIGURE THE LINE CHART **/
    private void configLineChart() {
        // no description text
        kennelViewsChart.getDescription().setEnabled(false);

        // enable touch gestures
        kennelViewsChart.setTouchEnabled(true);

        // enable scaling and dragging
        kennelViewsChart.setDragEnabled(true);
        kennelViewsChart.setScaleEnabled(true);
        kennelViewsChart.setScaleXEnabled(true);
        kennelViewsChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        kennelViewsChart.setPinchZoom(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(kennelViewsChart); // For bounds control
        kennelViewsChart.setMarker(mv); // Set the marker to the chart

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = kennelViewsChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        /* SET THE CUSTOM FONT */
//        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(100f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
//        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-20f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
//        ll2.setTypeface(tf);

        YAxis leftAxis = kennelViewsChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(110f);
        leftAxis.setAxisMinimum(-20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        kennelViewsChart.getAxisRight().setEnabled(false);

        kennelViewsChart.animateXY(1000, 1000);

        // get the legend (only possible after setting data)
        Legend l = kennelViewsChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }

    /** GET THE START AND END DATE FOR THE DURATION **/
    private void calculateDates() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            END_DATE = format.format(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(END_DATE));

            /* CALCULATE THE END DATE */
            calendar.add(Calendar.DATE, -SELECTED_DURATION);
            Date dateEnd = new Date(calendar.getTimeInMillis());
            START_DATE = format.format(dateEnd);

//            Log.e("START DATE", START_DATE);
//            Log.e("END DATE", END_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}