package com.barelabor.barelabor.activity.find;

import android.os.Bundle;
import android.widget.ListView;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.adapter.ShopListingAdapter;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.ShopModel;
import com.barelabor.barelabor.data.model.ShopObject;
import com.barelabor.barelabor.util.GPSTracker;

import java.util.ArrayList;

public class FindActivity extends BaseActivity {

    private ListView mListView;
    private ShopListingAdapter mListViewAdapter;

    private ArrayList<ShopObject> shopList = new ArrayList();

    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        initView();

        gpsTracker = new GPSTracker(FindActivity.this);
        postData();
    }

    private void initView(){
        mListView = (ListView) findViewById(R.id.lvShopList);
        mListViewAdapter = new ShopListingAdapter(this, shopList);

        mListView.setAdapter(mListViewAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                // TODO Auto-generated method stub
//
//                MailData message = messages.get(position);
//                Intent intent = new Intent(MailboxActivity.this, MailDetailActivity.class);
//                intent.putExtra("message", message);
//
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected boolean postData(DataProvider dataProvider, DataProviderCallback callback) {

//		Toast.makeText(LoginActivity.this, userInfo, Toast.LENGTH_LONG).show();

        String lat = Double.toString(gpsTracker.getLatitude());
        String lng = Double.toString(gpsTracker.getLongitude());

        dataProvider.getNearestLocationWithLocationLatitude(lat, lng, callback);

        return true;
    }

    @Override
    protected void handlePostResponse(DataObject dataObject) {

        if(dataObject instanceof ShopModel) {
            shopList = (ArrayList) ((ShopModel) dataObject).getShopList();
            mListViewAdapter.refereshAdapter(shopList);
        }
    }

    private void loadShop(){

    }
}
