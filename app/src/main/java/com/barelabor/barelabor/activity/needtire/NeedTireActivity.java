package com.barelabor.barelabor.activity.needtire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.FeatureModel;
import com.barelabor.barelabor.data.model.MakeModel;
import com.barelabor.barelabor.data.model.PriceModel;
import com.barelabor.barelabor.data.model.TireModel;
import com.barelabor.barelabor.util.Constants;
import com.barelabor.barelabor.util.Support;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class NeedTireActivity extends BaseActivity implements OnItemSelectedListener, View.OnClickListener {

    private static final int STATUS_LOAD_MAKE = 101;
    private static final int STATUS_LOAD_MODEL = 102;
    private static final int STATUS_LOAD_FEATURE = 103;
    private static final int STATUS_LOAD_QUANTITY = 104;
    private static final int STATUS_LOAD_FINISH = 105;

    private static final int STATUS_GET_PRICEBYVEHICLE = 106;
    private static final int STATUS_GET_PRICEBYSIZE = 107;


    private TextView lblCriteria;

    private Spinner spinYear, spinMake, spinModel, spinFeature, spinQuantity1, spinQuantity2, spinWidth, spinRatio, spinRim;
    private ArrayAdapter<String> spinYearAdapter, spinMakeAdapter, spinModelAdapter, spinFeatureAdapter, spinQuantityAdapter1, spinWidthAdapter, spinRatioAdapter, spinRimAdapter, spinQuantityAdapter2;
    private List<String> yearList, makeList, modelList, featureList, quanityList, widthList, rimList, ratioList;
    private RelativeLayout rltByVehicle, rltBySize;
    private RadioButton btnByVehicle, btnBySize;
    private Button btnSubmit;

    private int status = STATUS_LOAD_MAKE;
    private boolean isByVehicle = true;

    private String selectedYear, selectedMake, selectedModel, selectedFeature, selectedQuantity1, selectedWidth, selectedRatio, selectedRim, selectedQuantity2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_tire);

        initView();

        switchBetweenVehicleAndSize();
    }

    private void initView(){

        lblCriteria = (TextView) findViewById(R.id.lblCriteria);

        btnByVehicle = (RadioButton) findViewById(R.id.btnByVehicle);
        btnBySize = (RadioButton) findViewById(R.id.btnBySize);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnByVehicle.setOnClickListener(this);
        btnBySize.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        // By Vehicle

        rltByVehicle = (RelativeLayout) findViewById(R.id.rltByVehicle);

        spinYear = (Spinner) findViewById(R.id.spinnerYear);
        spinMake = (Spinner) findViewById(R.id.spinnerMake);
        spinModel = (Spinner) findViewById(R.id.spinnerModel);
        spinFeature = (Spinner) findViewById(R.id.spinnerFeatures);
        spinQuantity1 = (Spinner) findViewById(R.id.spinnerQuantity1);

        spinYear.setOnItemSelectedListener(this);
        spinMake.setOnItemSelectedListener(this);
        spinModel.setOnItemSelectedListener(this);
        spinFeature.setOnItemSelectedListener(this);
        spinQuantity1.setOnItemSelectedListener(this);

        // By Size

        rltBySize = (RelativeLayout) findViewById(R.id.rltBySize);

        spinWidth = (Spinner) findViewById(R.id.spinnerWidth);
        spinRatio = (Spinner) findViewById(R.id.spinnerRatio);
        spinRim = (Spinner) findViewById(R.id.spinnerRim);
        spinQuantity2 = (Spinner) findViewById(R.id.spinnerQuantity2);

        spinWidth.setOnItemSelectedListener(this);
        spinRatio.setOnItemSelectedListener(this);
        spinRim.setOnItemSelectedListener(this);
        spinQuantity2.setOnItemSelectedListener(this);

        getInitData();

        spinYearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearList);
        spinMakeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, makeList);
        spinModelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modelList);
        spinFeatureAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, featureList);
        spinQuantityAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quanityList);

        spinWidthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, widthList);
        spinRatioAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ratioList);
        spinRimAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rimList);
        spinQuantityAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, quanityList);

        spinYear.setAdapter(spinYearAdapter);
        spinMake.setAdapter(spinMakeAdapter);
        spinModel.setAdapter(spinModelAdapter);
        spinFeature.setAdapter(spinFeatureAdapter);
        spinQuantity1.setAdapter(spinQuantityAdapter1);

        spinWidth.setAdapter(spinWidthAdapter);
        spinRatio.setAdapter(spinRatioAdapter);
        spinRim.setAdapter(spinRimAdapter);
        spinQuantity2.setAdapter(spinQuantityAdapter2);
    }

    private void switchBetweenVehicleAndSize(){
        if (isByVehicle){
            rltByVehicle.setVisibility(View.VISIBLE);
            rltBySize.setVisibility(View.GONE);

            lblCriteria.setText("By Vehicle");
        }else{
            rltByVehicle.setVisibility(View.GONE);
            rltBySize.setVisibility(View.VISIBLE);

            lblCriteria.setText("By Size");
        }
    }

    private void getInitData(){
        getYears();
        getMakes(null);
        getModels(null);
        getFeatures(null);
        getQuantity();
        getWidths();
        getRatio();
        getRim();
    }

    private void getYears(){

        yearList = new ArrayList<String>();

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1953; i <= thisYear; i++) {
            yearList.add(Integer.toString(i));
        }

        yearList.add("Year");

        Collections.reverse(yearList);
    }
    private void getMakes(MakeModel model){

        if (model == null)
            makeList = new ArrayList<String>();
        else
            makeList = model.getMakeList();

        makeList.add("Make");
        Collections.reverse(makeList);
    }
    private void getModels(TireModel model){

        if (model == null)
            modelList = new ArrayList<String>();
        else
            modelList = model.getModelList();

        modelList.add("Model");
        Collections.reverse(modelList);
    }
    private void getFeatures(FeatureModel model){

        if (model == null)
            featureList = new ArrayList<String>();
        else
            featureList = model.getFeatureList();

        featureList.add("Features");
        Collections.reverse(featureList);
    }
    private void getWidths(){
        widthList = new ArrayList<String>();
        int counter = 10;
        for(int i = 105; i <= 395; i = i + counter){
            if (i == 355){
                counter = 20;
            }

            widthList.add(Integer.toString(i));
        }

        for (int i = 24; i <= 42; ++i){
            widthList.add(i + "X");
        }

        widthList.add("7");
        widthList.add("7.5");
        widthList.add("8");
        widthList.add("8.75");
        widthList.add("9.5");
    }

    private void getRatio(){

        ratioList = new ArrayList<String>();

        int counter = 5;
        for(int i = 20; i <= 95; i = i + counter){
            ratioList.add(Integer.toString(i));
        }

        double doubleCounter = 0.5;
        for (double i = 7.5; i <= 18.5; i = i + doubleCounter){
            ratioList.add(Double.toString(i));
        }

        ratioList.add("0");
    }

    private void getRim(){
        rimList = new ArrayList<String>();

        int counter = 2;

        for(int i = 10; i <= 16; i = i + counter){
            rimList.add(Integer.toString(i));
            if(i == 12) {
                counter = 1;
            }
        }

        for(double i = 16.5; i  <= 20; i = i + 0.5) {
            rimList.add(Double.toString(i));
        }

        counter = 1;

        for(int i = 21; i <= 30; i = i + counter) {

            rimList.add(Integer.toString(i));
            if(i == 26) {
                counter = 2;
            }
        }
    }

    private void getQuantity(){
        quanityList = new ArrayList<String>();

        for(int i = 1; i <= 12; i++)  {
            quanityList.add(Integer.toString(i));
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        if (parent == spinYear){
            if(position == 0) {
                selectedYear = "";
                return;
            }
            selectedYear = item;
            status = STATUS_LOAD_MAKE;
        }else if(parent == spinMake){

            if(position == 0) {
                selectedMake = "";
                return;
            }

            selectedMake = item;
            status = STATUS_LOAD_MODEL;
        }else if(parent == spinModel){

            if(position == 0) {
                selectedModel = "";
                return;
            }

            selectedModel = item;
            status = STATUS_LOAD_FEATURE;
        }else if(parent == spinFeature){

            if(position == 0) {
                selectedFeature = "";
                return;
            }

            selectedFeature = item;
            status = STATUS_LOAD_QUANTITY;
            return;
        }else if(parent == spinQuantity1){

//            if(position == 0) {
//                selectedQuantity1 = "";
//                return;
//            }

            selectedQuantity1 = item;
            status = STATUS_LOAD_FINISH;
            return;
        }else if(parent == spinWidth){

            if(position == 0) {
                selectedWidth = "";
                return;
            }

            selectedWidth = item;
            return;
        }else if(parent == spinRatio){

            if(position == 0) {
                selectedRatio = "";
                return;
            }

            selectedRatio = item;
            return;
        }else if(parent == spinRim){

            if(position == 0) {
                selectedRim = "";
                return;
            }

            selectedRim = item;
            return;
        }else if(parent == spinQuantity2){

//            if(position == 0) {
//                selectedQuantity2 = "";
//                return;
//            }

            selectedQuantity2 = item;
            return;
        }

        postData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected boolean postData(DataProvider dataProvider, DataProviderCallback callback) {

//		Toast.makeText(LoginActivity.this, userInfo, Toast.LENGTH_LONG).show();
        //return dataProvider.login(userName, password, callback);

        switch (status){
            case STATUS_LOAD_MAKE:
                dataProvider.getMakeFromYear(selectedYear, callback);
                break;
            case STATUS_LOAD_MODEL:
                dataProvider.getModel(selectedYear, selectedMake, callback);
                break;
            case STATUS_LOAD_FEATURE:
                dataProvider.getFeatures(selectedYear, selectedMake, selectedModel, callback);
                break;
            case STATUS_GET_PRICEBYVEHICLE:
                dataProvider.getPriceByVehicle(selectedYear, selectedMake, selectedModel, selectedFeature, callback);
                break;
            case STATUS_GET_PRICEBYSIZE:
                dataProvider.getPriceBySize(selectedWidth, selectedRatio, selectedRim, callback);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void handlePostResponse(DataObject dataObject) {
        System.out.println("DataObject = " + dataObject);
        switch (status){
            case STATUS_LOAD_MAKE:

                getMakes((MakeModel)dataObject);

                spinMakeAdapter.clear();
                spinMakeAdapter.addAll(makeList);

                spinMakeAdapter.notifyDataSetChanged();

                break;
            case STATUS_LOAD_MODEL:

                getModels((TireModel)dataObject);

                spinModelAdapter.clear();
                spinModelAdapter.addAll(modelList);

                spinModelAdapter.notifyDataSetChanged();

                break;
            case STATUS_LOAD_FEATURE:

                getFeatures((FeatureModel) dataObject);

                spinFeatureAdapter.clear();
                spinFeatureAdapter.addAll(featureList);

                spinFeatureAdapter.notifyDataSetChanged();

                break;
            case STATUS_GET_PRICEBYVEHICLE:

                showChart((PriceModel)dataObject, selectedQuantity1);

                break;
            case STATUS_GET_PRICEBYSIZE:

                showChart((PriceModel)dataObject, selectedQuantity2);

                break;
            default:
                break;
        }

    }

    private void showChart(PriceModel priceModel, String strQuantity){
        System.out.println("Price"+priceModel.getRatingString());
        String ratingJSONString = priceModel.getRatingString();
        try {
            JSONArray ratingArray = new JSONArray(ratingJSONString);
            for (int i = 0;i < ratingArray.length();i++) {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int quantity = Integer.parseInt(strQuantity);

        ArrayList<String> priceList = (ArrayList<String>) priceModel.getPriceList();
        Double lowPrice = 0.0, maxPrice = 0.0, avgPrice = 0.0, totalPrice = 0.0;

        if (priceList.size() > 0){
            for (String strPrice : priceList){
                Double price = Double.parseDouble(strPrice);

                if (lowPrice == 0.0)
                    lowPrice = maxPrice;

                if(price < lowPrice)
                    lowPrice = price;
                if(price > maxPrice)
                    maxPrice = price;

                totalPrice += price;
            }

            avgPrice = totalPrice / priceList.size();
        }

        String strLowPrice = "" + ((int)lowPrice.doubleValue() * quantity);
        String strHighPrice = "" + ((int)maxPrice.doubleValue() * quantity);
        String strAvgPrice = "" + ((int)avgPrice.doubleValue() * quantity);


        Intent intent2 = new Intent(NeedTireActivity.this, ChartActivity.class);

        intent2.putExtra("low_price", strLowPrice);
        intent2.putExtra("high_price", strHighPrice);
        intent2.putExtra("avg_price", strAvgPrice);
        intent2.putExtra("rating_string", ratingJSONString);
        intent2.putExtra("from_need_tire", true);

        startActivity(intent2);
    }
    @Override
    public void onClick(View v) {
        if(v == btnByVehicle){
            isByVehicle = true;
            switchBetweenVehicleAndSize();
        }else if(v == btnBySize){
            isByVehicle = false;
            switchBetweenVehicleAndSize();
        }else if(v == btnSubmit){
            Toast.makeText(NeedTireActivity.this, "Submit from " + ((isByVehicle) ? "By Vehicle" : "By Size"), Toast.LENGTH_LONG).show();

            MixpanelAPI mixPanel =  MixpanelAPI.getInstance(this, Constants.MIXPANEL_TOKEN);
            mixPanel.track(((isByVehicle) ? "Vehicle" : "Size") + " Submit clicked");

            if(validate()){
                doSubmit();
            }
        }
    }

    private Boolean validate(){

        if (isByVehicle){
            if (selectedYear.length() == 0 || selectedMake.length() == 0 || selectedModel.length() == 0 || selectedFeature.length() == 0 || selectedQuantity1.length() == 0){
                Support.getMessageService(this).showError(this, "Please fill searched fields", false);
                return false;
            }
        }else{
            if (selectedWidth.length() == 0 || selectedRatio.length() == 0 || selectedRim.length() == 0 || selectedQuantity2.length() == 0){
                Support.getMessageService(this).showError(this, "Please fill searched fields", false);
                return false;
            }
        }
        return true;
    }

    private void doSubmit(){
        if(isByVehicle)
            status = STATUS_GET_PRICEBYVEHICLE;
        else
            status = STATUS_GET_PRICEBYSIZE;
        postData();
    }
}
