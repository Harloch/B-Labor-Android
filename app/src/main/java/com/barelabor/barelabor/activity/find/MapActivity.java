package com.barelabor.barelabor.activity.find;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.data.model.ShopObject;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.util.GPSTracker;
import com.barelabor.barelabor.util.Support;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

    private TextView txtShopName, txtAddress;
    private Button btnCall;
    private GoogleMap googleMap;

    GPSTracker gpsTracker;
    private ShopObject data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        data = (ShopObject) getIntent().getSerializableExtra("data");
        gpsTracker = new GPSTracker(MapActivity.this);

        initView();

        txtShopName.setText(data.getLocation());
        txtAddress.setText(Support.getFullAddress(data));
    }

    private void initView(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (googleMap == null) {
                Toast.makeText(this,
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        Log.e("Send", "Call Here " + (googleMap != null));
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            //googleMap.setInfoWindowAdapter(new MarkerInfoWindow());
            googleMap.setOnInfoWindowClickListener(this);
            googleMap.setMyLocationEnabled(true);

            // Change Position of current position

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            // Find myLocationButton view

            /*
            View myLocationButton = mapFragment.getView().findViewById(0x2);

            if (myLocationButton != null
                    && myLocationButton.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                // ZoomControl is inside of RelativeLayout
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLocationButton
                        .getLayoutParams();
                // Align it to - parent BOTTOM|LEFT
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

                // Update margins, set to 10dp
                final int margin = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
                                .getDisplayMetrics());
                params.setMargins(margin, margin, margin, margin);

                myLocationButton.setLayoutParams(params);
            }
            */

            // Change Position of current position

            final LatLng latLng = new LatLng(gpsTracker.getLatitude(),
                    gpsTracker.getLongitude());
            Log.e("Send", "Other 1 >");
            // googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    Log.e("Send", " Listner " + latLng.latitude + " long "
                            + latLng.longitude);
                    // Move camera.
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    // Remove listener to prevent position reset on camera move.
                    googleMap.setOnCameraChangeListener(null);
                }
            });

            LatLng pos = new LatLng(data.getLat(), data.getLng());

            googleMap.addMarker(createMarker(data.getLocation(), Support.getFullAddress(data), pos));
        }

        txtShopName = (TextView) findViewById(R.id.txtShopName);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        btnCall = (Button) findViewById(R.id.btnCall);

        btnCall.setOnClickListener(this);
    }

    private MarkerOptions createMarker(String title, String snippet, LatLng lng) {
        MarkerOptions markeroption = new MarkerOptions();
        markeroption.position(lng);
        markeroption.snippet(snippet);
        markeroption.title(title);


        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_marker_small);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        markeroption.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        return markeroption;
    }

    @Override
    public void onClick(View v) {

        if (v == btnCall){
            String phoneNumber = Support.trimPhoneNumber(data.getPhoneNumber());

            try {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    Support.getMessageService(MapActivity.this).showWarning(MapActivity.this, "No permission", false);
                    return;
                }else{
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                }

            } catch (ActivityNotFoundException activityException) {
                Log.e("Calling a Phone Number", "Call failed", activityException);
            }
        }
    }



    @Override
    public void onInfoWindowClick(Marker marker) {

    }
}
