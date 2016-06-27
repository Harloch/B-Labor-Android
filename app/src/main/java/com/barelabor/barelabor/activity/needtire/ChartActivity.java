package com.barelabor.barelabor.activity.needtire;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.barelabor.barelabor.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener, ActionSheet.ActionSheetListener {

    //private LineChartView mChart;
    private LineChart mChart;
    private String lowPrice, highPrice, avgPrice;
    private Boolean fromNotification = false;
    private Boolean fromNeedTire = false;
    private JSONArray ratingArray;
    private String ratingJSONString;
    private String repairArrayString;
    private String highCostArrayString;
    private String averageCostArrayString;
    private String lowCostArrayString;
    private String []repairArray;
    private String []highCostArray;
    private String []averageArray;
    private String []lowCostArray;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        lowPrice = getIntent().getStringExtra("low_price");
        highPrice = getIntent().getStringExtra("high_price");
        avgPrice = getIntent().getStringExtra("avg_price");
        fromNotification = getIntent().getBooleanExtra("notification", false);
        fromNeedTire = getIntent().getBooleanExtra("from_need_tire", false);
        System.out.println("Is from Notification = " + fromNotification);
        initView();

        setData();

        // If from need a tire
        if (fromNeedTire) {
            ratingJSONString = getIntent().getStringExtra("rating_string");
            makeBarButtons();
        }
        // If from push notification and view history button
        else {
            repairArrayString = getIntent().getStringExtra("repairArrayString");
            highCostArrayString = getIntent().getStringExtra("highCostArrayString");
            averageCostArrayString = getIntent().getStringExtra("averageCostArrayString");
            lowCostArrayString = getIntent().getStringExtra("lowCostArrayString");
            repairArray = repairArrayString.split(",");
            highCostArray = highCostArrayString.split(",");
            averageArray = averageCostArrayString.split(",");
            lowCostArray = lowCostArrayString.split(",");
            System.out.println("repari array string = " + repairArray.length);
            makePushBarButtons();
        }
    }

    private void initView(){

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);
        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaxValue(1200f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawLabels(false);

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setEnabled(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextSize(15);
        xAxis.setTextColor(Color.WHITE);

        mChart.setExtraLeftOffset(25);
        mChart.setExtraRightOffset(25);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
    }



    private void setData(){

        ArrayList<Entry> entries = new ArrayList<>();

        ArrayList<String> labels = new ArrayList<String>();

        for(int i = 0; i < 20; i++){
            if (i <= 1 || i >= 19)
                entries.add(new Entry(0.0f, 0));
            else if(i < 10){
                entries.add(new Entry((float) Math.pow(2, i), i));
            }else{
                entries.add(new Entry((float) Math.pow(2, (20-i)), i));
            }

            if(i == 0)
                labels.add("$" + lowPrice);
            else if(i == 10)
                labels.add("$" + avgPrice);
            else if(i >= 19)
                labels.add("$" + highPrice);
            else
                labels.add("");
        }

        LineDataSet dataset = new LineDataSet(entries, "");
        dataset.setDrawCubic(true);
        dataset.setDrawValues(false);
        dataset.setDrawCircles(false);

        LineData data = new LineData(labels, dataset);
        mChart.setData(data);

        mChart.getLegend().setEnabled(false);
    }

    private void makeBarButtons() {
        try {
            ratingArray = new JSONArray(ratingJSONString);
            System.out.println(ratingJSONString);
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.button_container);
            int ratingArrayCount = ratingArray.length();
            int layoutWidth = getWindowManager().getDefaultDisplay().getWidth();
            int perBtnWidth = layoutWidth/(ratingArrayCount+1);
            for (int i = 0;i < ratingArrayCount;i++) {
                Button btn = new Button(this);
                btn.setTag(i);
                if (i == 0) {
                    btn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                else if (i == ratingArrayCount-1){
                    btn.setBackgroundColor(Color.parseColor("#000000"));
                }
                else {
                    btn.setBackgroundColor(Color.parseColor("#CCCCCC"));
                }
                RelativeLayout.LayoutParams btn_param = new RelativeLayout.LayoutParams(40, 200+20*i);
                btn_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                btn_param.setMargins(perBtnWidth * (i+1), 10, 10, 20);
                btn.setOnClickListener(this);

                layout.addView(btn, btn_param);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makePushBarButtons() {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.button_container);
        int repairArrayCount = repairArray.length;
        int layoutWidth = getWindowManager().getDefaultDisplay().getWidth();
        int perBtnWidth = layoutWidth/(repairArrayCount+1);
        int []colorArry = {Color.RED, Color.GREEN, Color.WHITE, Color.BLACK, Color.GRAY, Color.parseColor("#A52A2A"), Color.DKGRAY, Color.WHITE, Color.GREEN, Color.BLACK};
        Button guideLeftBtn = (Button)findViewById(R.id.guideLeftBtn);
        Button guideRightBtn = (Button)findViewById(R.id.guideRightBtn);
        guideLeftBtn.setBackgroundColor(colorArry[0]);
        guideRightBtn.setBackgroundColor(colorArry[repairArrayCount-1]);
        guideLeftBtn.setText(repairArray[0]);
        guideRightBtn.setText(repairArray[repairArrayCount-1]);
        for (int i = 0;i < repairArrayCount;i++) {
            Button btn = new Button(this);
            btn.setTag(i);
            btn.setBackgroundColor(colorArry[i]);
            RelativeLayout.LayoutParams btn_param = new RelativeLayout.LayoutParams(40, 200 + 20 * i);
            btn_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            btn_param.setMargins(perBtnWidth * (i + 1), 10, 10, 20);
            btn.setOnClickListener(this);

            layout.addView(btn, btn_param);
        }
    }
    @Override
    public void onClick(View v) {

        if(v == btnBack){

            finish();
        } else {
            if (fromNeedTire){
                int tag = (int) v.getTag();
                System.out.println("tag = " + tag);
                try {
                    JSONArray ratingArray = new JSONArray(ratingJSONString);
                    JSONObject rating = ratingArray.getJSONObject(tag);
                    String tireName = rating.getString("t_name");
                    String tirePrice = "$" + rating.getString("t_price");
                    String tireMileage = rating.getString("t_mileage");
                    String barelaborRating = rating.getString("t_rating");
                    Float barelaborRatingFloatValue = Float.parseFloat(barelaborRating);
                    String tireRanking = barelaborRatingFloatValue  > 7.56 ? "Above Average" : "Below Average";

                    View alertView = getLayoutInflater().inflate(R.layout.custom_tire_layout, null);
                    final AlertDialog alert = new AlertDialog.Builder(this)
                            .setView(alertView).create();
                    Button closeBtn = (Button)alertView.findViewById(R.id.closeBtn);
                    Button buyBtn = (Button)alertView.findViewById(R.id.buyBtn);
                    TextView tireNameTextView = (TextView)alertView.findViewById(R.id.tireNameTextView);
                    TextView tirePriceTextView = (TextView)alertView.findViewById(R.id.tirePriceTextView);
                    TextView tireMileageTextView = (TextView)alertView.findViewById(R.id.tireMileageTextView);
                    TextView barelaborRatingTextView = (TextView)alertView.findViewById(R.id.barelaborRatingTextView);
                    TextView tireRankingTextView = (TextView)alertView.findViewById(R.id.tireRankingTextView);

                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.hide();
                        }
                    });
                    buyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.hide();
                            showActionSheet();
                        }
                    });

                    tireNameTextView.setText(tireName);
                    tirePriceTextView.setText(tirePrice);
                    tireMileageTextView.setText(tireMileage);
                    barelaborRatingTextView.setText(barelaborRating);
                    tireRankingTextView.setText(tireRanking);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                int tag = (int) v.getTag();
                String repair = repairArray[tag];
                String highCost = "$" + highCostArray[tag];
                String averageCost = "$" + averageArray[tag];
                String lowCost = "$" + lowCostArray[tag];

                View alertView = getLayoutInflater().inflate(R.layout.custom_scan_layout, null);
                final AlertDialog alert = new AlertDialog.Builder(this)
                        .setView(alertView).create();
                Button closeBtn = (Button)alertView.findViewById(R.id.repairCloseBtn);
                Button shopBtn = (Button)alertView.findViewById(R.id.shopBtn);
                TextView repairTextView = (TextView)alertView.findViewById(R.id.repairTextView);
                TextView highCostTextView = (TextView)alertView.findViewById(R.id.highCostTextView);
                TextView averageCostTextView = (TextView)alertView.findViewById(R.id.averageCostTextView);
                TextView lowCostTextView = (TextView)alertView.findViewById(R.id.lowCostTextView);

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.hide();
                    }
                });
                shopBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.hide();
                        showActionSheet();
                    }
                });

                repairTextView.setText(repair);
                highCostTextView.setText(highCost);
                averageCostTextView.setText(averageCost);
                lowCostTextView.setText(lowCost);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.show();
            }
        }
    }

    public void showActionSheet() {
        if (fromNeedTire){
            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles("Tirerack", "Firestone", "NTB", "Discount Tire", "Meineke")
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }
        else{
            ActionSheet.createBuilder(this, getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles("Firestone", "NTB", "Meineke")
                    .setCancelableOnTouchOutside(true).setListener(this).show();
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        Toast.makeText(getApplicationContext(), "Actionsheet dismissed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        String urlString = null;
        if (fromNeedTire){
            switch (index){
                case 0:
                    urlString = "http://www.anrdoezrs.net/click-8048474-10399938-1431633665000";
                    break;
                case 1:
                    urlString = "http://www.dpbolvw.net/click-8048474-11275445-1408120810000";
                    break;
                case 2:
                    urlString = "http://ntb.com/tires/Tire-Services-Landing.j";
                    break;
                case 3:
                    urlString = "http://www.discounttire.com/dtcs/home.do";
                    break;
                case 4:
                    urlString = "http://www.meineke.com/services/tires-wheels/";
                    break;
                default:
                    urlString = "http://www.anrdoezrs.net/click-8048474-10399938-1431633665000";
            }
        }
        else{
            switch (index){
                case 0:
                    urlString = "http://www.dpbolvw.net/click-8048474-11275445-1408120810000";
                    break;
                case 1:
                    urlString = "http://ntb.com/tires/Tire-Services-Landing.j";
                    break;
                case 2:
                    urlString = "http://www.meineke.com/services/tires-wheels/";
                    break;
                default:
                    urlString = "http://www.dpbolvw.net/click-8048474-11275445-1408120810000";
            }
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        startActivity(browserIntent);
    }
}
