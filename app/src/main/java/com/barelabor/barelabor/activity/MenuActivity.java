package com.barelabor.barelabor.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.activity.experience.ExperienceActivity;
import com.barelabor.barelabor.activity.find.FindActivity;
import com.barelabor.barelabor.activity.needtire.ChartActivity;
import com.barelabor.barelabor.activity.needtire.NeedTireActivity;
import com.barelabor.barelabor.activity.scan.ScanActivity;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.EstimateModel;
import com.barelabor.barelabor.util.Constants;
import com.barelabor.barelabor.util.Support;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class MenuActivity extends BaseActivity implements View.OnClickListener{

    private final int PERMISSION_LOCATION_REQUEST_CODE = 1001;

    Button btnScan, btnNeedTire, btnFind, btnExperience, btnHistory, btnTutor;

    private String userId, estimateId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initView();

        Boolean fromNotification = getIntent().getBooleanExtra("notification", false);
        if(fromNotification){
            String lowPrice = getIntent().getStringExtra("low_price");
            String highPrice = getIntent().getStringExtra("high_price");
            String avgPrice = getIntent().getStringExtra("avg_price");

            Intent intent = new Intent(this, ChartActivity.class);
            intent.putExtra("low_price", lowPrice);
            intent.putExtra("high_price", highPrice);
            intent.putExtra("avg_price", avgPrice);
            intent.putExtra("notification", true);

            startActivity(intent);
        }
    }

    private void initView(){
        btnScan = (Button) findViewById(R.id.btnScan);
        btnTutor = (Button) findViewById(R.id.btnTutor);
        btnNeedTire = (Button) findViewById(R.id.btnTire);
        btnFind = (Button) findViewById(R.id.btnFind);
        btnExperience = (Button) findViewById(R.id.btnExperience);
        btnHistory = (Button) findViewById(R.id.btnHistory);

        btnScan.setOnClickListener(this);
        btnNeedTire.setOnClickListener(this);
        btnFind.setOnClickListener(this);
        btnExperience.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnTutor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btnScan){
            startActivity(new Intent(MenuActivity.this, ScanActivity.class));
        }else if(v == btnNeedTire){

//            View alertView = getLayoutInflater().inflate(R.layout.custom_tire_layout, null);
//            AlertDialog alert = new AlertDialog.Builder(this)
//                    .setView(alertView).create();
//            alert.show();
            startActivity(new Intent(MenuActivity.this, NeedTireActivity.class));
            //startActivity(new Intent(MenuActivity.this, ChartActivity.class));

        }else if(v == btnFind){

            MixpanelAPI mixPanel =  MixpanelAPI.getInstance(this, Constants.MIXPANEL_TOKEN);
            mixPanel.track("Find A Location clicked");

            if(!checkPermission(MenuActivity.this)){

                /*
                if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Support.getMessageService(this).showError(this, "Permission denied", false);
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_LOCATION_REQUEST_CODE);
                }
                */
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE},
                        PERMISSION_LOCATION_REQUEST_CODE);

            }else{
                startActivity(new Intent(MenuActivity.this, FindActivity.class));
            }

        }else if(v == btnExperience){
            startActivity(new Intent(MenuActivity.this, ExperienceActivity.class));
        }
        else if(v == btnTutor) {
            startActivity(new Intent(MenuActivity.this, introActivity.class));
        }else
        if(v == btnHistory){
//            startActivity(new Intent(MenuActivity.this, HistoryActivity.class));

            userId = Support.getSharedPreference(this).getUserId();
            estimateId = Support.getSharedPreference(this).getEstimateID();

            if (userId.equalsIgnoreCase("") || estimateId.equalsIgnoreCase("")){
                Support.getMessageService(this).showError(this, "No History is Available.", false);
            }else {
                postData();
            }
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    startActivity(new Intent(MenuActivity.this, FindActivity.class));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Support.getMessageService(this).showError(this, "Permission denied", false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected boolean postData(DataProvider dataProvider, DataProviderCallback callback) {

//		Toast.makeText(LoginActivity.this, userInfo, Toast.LENGTH_LONG).show();

        Support.getDataProvider(this).getEstimate(userId, estimateId, callback);

        return true;
    }

    @Override
    protected void handlePostResponse(DataObject dataObject) {

        EstimateModel data = (EstimateModel) dataObject;

        String lowPrice = data.getLowPrice();
        String avgPrice = data.getAvgPrice();
        String highPrice = data.getHighPrice();
        String repairArrayString = data.getRepairArrayString();
        String highCostArrayString = data.getHighCostArrayStringPrice();
        String averageCostArrayString = data.getAverageCostArrayStringPrice();
        String lowCostArrayString = data.getLowCostArrayStringPrice();
        if(data.getLowPrice().equalsIgnoreCase("") || data.getAvgPrice().equalsIgnoreCase("") || data.getHighPrice().equalsIgnoreCase("")){
            Support.getMessageService(this).showError(this, "Your last submission was not confirmed yet. Please try again soon.", false);
        }else{

            Intent intent2 = new Intent(MenuActivity.this, ChartActivity.class);
            intent2.putExtra("low_price", lowPrice);
            intent2.putExtra("high_price", highPrice);
            intent2.putExtra("avg_price", avgPrice);
            intent2.putExtra("repairArrayString", repairArrayString);
            intent2.putExtra("highCostArrayString", highCostArrayString);
            intent2.putExtra("averageCostArrayString", averageCostArrayString);
            intent2.putExtra("lowCostArrayString", lowCostArrayString);

            startActivity(intent2);

        }
    }
}
