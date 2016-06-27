package com.barelabor.barelabor.activity.scan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.activity.MenuActivity;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.JsonDataHandler;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.EstimateModel;
import com.barelabor.barelabor.data.model.UserModel;
import com.barelabor.barelabor.util.Constants;
import com.barelabor.barelabor.util.Support;

public class PreviewActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivPreview;
    private Button btnRetake, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initView();

        if (Constants.bitmap != null)
        ivPreview.setImageBitmap(Constants.bitmap);
    }

    private void initView(){

        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        btnRetake = (Button) findViewById(R.id.btnRetake);
        btnDone = (Button) findViewById(R.id.btnDone);

        btnRetake.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    @Override
    protected boolean postData(DataProvider dataProvider, DataProviderCallback callback) {

//		Toast.makeText(LoginActivity.this, userInfo, Toast.LENGTH_LONG).show();
        String userId = Support.getSharedPreference(this).getUserId();

        dataProvider.submitEstimateImage(userId, Constants.bitmap, callback);

        return true;
    }

    @Override
    protected void handlePostResponse(DataObject dataObject) {

        EstimateModel data = (EstimateModel)dataObject;

        if(dataObject.getStatus().equalsIgnoreCase(JsonDataHandler.STRING_STATUS_OK)){
            Support.getMessageService(this).showInfo(this, "Please sit tight. We will bring you the estimated pricing shortly. Thank you.", true);
        }else{
            Support.getMessageService(this).showError(this, "Could not upload estimate.", true);
        }

        Support.getSharedPreference(this).storeEstimateID(data.getEstimateId());
    }

    @Override
    public void onClick(View v) {

        if (v == btnRetake){
            finish();
        }else if (v == btnDone){
            postData();
        }
    }
}
