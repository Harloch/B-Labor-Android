package com.barelabor.barelabor.adapter;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.activity.find.MapActivity;
import com.barelabor.barelabor.data.model.ShopObject;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.util.Support;

public class ShopListingAdapter extends BaseAdapter {

	private Activity mContext;
	private ArrayList<ShopObject> mShopList = null;

	public ShopListingAdapter(Activity activity, ArrayList<ShopObject> list) {
		mContext = activity;
		mShopList = list;
	}

	public ArrayList<ShopObject> getDataSource() {
		return mShopList;
	}

	public void refereshAdapter(ArrayList<ShopObject> list) {
		mShopList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mShopList == null)
			return 0;
		else
			return mShopList.size();
	}

	@Override
	public Object getItem(int arg0) {

		return mShopList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		Holder holder = null;
		final ShopObject data = mShopList.get(position);

		if (convertView == null) {
			holder = new Holder();

			RelativeLayout ll = (RelativeLayout) mContext.getLayoutInflater().inflate(
					R.layout.row_shop_listing, null);

			holder.txtShopName = (TextView) ll.findViewById(R.id.txtShopName);
			holder.txtAddress = (TextView) ll.findViewById(R.id.txtAddress);
			holder.btnCall = (TextView) ll.findViewById(R.id.btnCall);
			holder.btnLocation = (TextView) ll.findViewById(R.id.btnLocation);

			holder.container = ll;
			holder.container.setTag(holder);

			convertView = holder.container;
		} else
			holder = (Holder) convertView.getTag();

		holder.txtShopName.setText(data.getLocation());
		holder.txtAddress.setText(Support.getFullAddress(data));
		holder.btnCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String phoneNumber = Support.trimPhoneNumber(data.getPhoneNumber());

				try {
					if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						Support.getMessageService(mContext).showWarning(mContext, "No permission", false);

						return;
					}else{
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:" + phoneNumber));
						mContext.startActivity(callIntent);
					}

				} catch (ActivityNotFoundException activityException) {
					Log.e("Calling a Phone Number", "Call failed", activityException);
				}
			}
		});

		holder.btnLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MapActivity.class);
				intent.putExtra("data", data);
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}

	private class Holder {
		public RelativeLayout container;
		public TextView txtShopName, txtAddress, btnCall, btnLocation;
	}


}
