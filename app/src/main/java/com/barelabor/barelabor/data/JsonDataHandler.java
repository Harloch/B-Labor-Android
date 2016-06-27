package com.barelabor.barelabor.data;

import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.EstimateModel;
import com.barelabor.barelabor.data.model.FeatureModel;
import com.barelabor.barelabor.data.model.MakeModel;
import com.barelabor.barelabor.data.model.PriceModel;
import com.barelabor.barelabor.data.model.ServiceError;
import com.barelabor.barelabor.data.model.ShopModel;
import com.barelabor.barelabor.data.model.ShopObject;
import com.barelabor.barelabor.data.model.TireModel;
import com.barelabor.barelabor.data.model.UserModel;
import com.barelabor.barelabor.debug.Log;
import com.barelabor.barelabor.util.NotImplementedException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonDataHandler {

	public static final String STRING_STATUS_OK = "ok";
	public static final String STRING_STATUS_CONFLICT = "CONFLICT";
	public static final String STRING_STATUS_NOT_FOUND = "NOT_FOUND";
	public static final String STRING_STATUS_NOT_UNAUTHORIZED = "UNAUTHORIZED";
	public static final String STRING_STATUS_SUCCESS = "success";
	public static final String STRING_STATUS_TRUE = "true";
	public static final String STRING_MESSAGE = "message";
	public static final String STRING_STATUS = "status";

	private static final String STRING_ITEM = "item";
	private static final String ARRAY_ITEMS = "items";

	private static final WebServiceRequestType[] METHODS_WITH_NO_STATUS_CODE = {

	}; // TODO




	private boolean isMethodWithNoStatusCode(WebServiceRequestType type) {

		for (WebServiceRequestType listType : METHODS_WITH_NO_STATUS_CODE) {

			if (type.equals(listType)) {
				return true;
			}

		}

		return false;

	}

	public DataObject parse(WebServiceRequest request, String response) throws DataFormatException {
		DataObject dataObject = null;
		JSONObject json;
		String status;

//		if(request.getType() == WebServiceRequestType.SubmitEstimate) {
//			dataObject = readSubmitExperienceData(response);
//			return dataObject;
//		}

		try {
			json = new JSONObject(response);
			if (json.has(STRING_STATUS)) {
				status = json.getString(STRING_STATUS);
			} else if (this.isMethodWithNoStatusCode(request.getType())) {
				// getevaluationtest doesn't return status
				status = STRING_STATUS_OK;
			} else {
				throw new DataFormatException("No status field");
			}
		} catch (JSONException e) {
			throw new DataFormatException(e.getMessage());
		}

		//if (status.equalsIgnoreCase(STRING_STATUS_OK)) {

			WebServiceRequestType type = request.getType();

			switch (type) {
				case Login:
					dataObject = readLoginData(json);
					break;
				case Register:
					dataObject = readRegisterData(json);
					break;
				case GetMake:
					dataObject = readMakeData(json);
					break;
				case GetModel:
					dataObject = readModelData(json);
					break;
				case GetFeatures:
					dataObject = readFeatureData(json);
					break;
				case GetPriceByVehicle:
					dataObject = readVehiclePriceData(json);
					break;
				case GetPriceBySize:
					dataObject = readSizePriceData(json);
					break;
				case SubmitEstimate:
					dataObject = readSubmitEstimate(json);
					break;
				case GetEstimate:
					dataObject = readGetEstimate(json);
					break;
				case SubmitExperience:
					dataObject = readSubmitExperienceData(json);
					break;
				case FindNearShop:
					dataObject = readShopData(json);
					break;
				default:
					throw new NotImplementedException();
			}
//		} else {
//			dataObject = readDataObjectError(json);
//		}

		Log.d("Status", status);

		return dataObject;
	}

	private UserModel readLoginData(JSONObject json) throws DataFormatException {

		Log.d("Login Result", json.toString());

		UserModel user = new UserModel();

		try {
			String status = json.getString(STRING_STATUS);
			user.setStatus(status);

			if (!status.equalsIgnoreCase(STRING_STATUS_NOT_FOUND)) {
				JSONObject jData = json.getJSONObject(STRING_ITEM);

				user.setUserId(jData.getString("userID"));
				user.setUserName(jData.getString("username"));
				user.setLatitude(jData.getString("userLat"));
				user.setLongitude(jData.getString("userLong"));
				user.setAddress(jData.getString("userAddress"));
				user.setPhoneNumber(jData.getString("userPhone"));
				user.setCreatedDate(jData.getString("created"));
				user.setResetToken(jData.getString("resetToken"));
				user.setAvatar(jData.getString("userAvatar"));
				user.setTimeAgo(jData.getString("timeAgo"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

	private UserModel readRegisterData(JSONObject json) throws DataFormatException {

		Log.d("Register Result", json.toString());

		UserModel user = new UserModel();

		try {
			String status = json.getString(STRING_STATUS);
			user.setStatus(status);

			if (!status.equalsIgnoreCase(STRING_STATUS_CONFLICT)){
				JSONObject jData = json.getJSONObject(STRING_ITEM);

				user.setUserId(jData.getString("userID"));
				user.setUserName(jData.getString("username"));
				user.setLatitude(jData.getString("userLat"));
				user.setLongitude(jData.getString("userLong"));
				user.setAddress(jData.getString("userAddress"));
				user.setPhoneNumber(jData.getString("userPhone"));
				user.setCreatedDate(jData.getString("created"));
				user.setResetToken(jData.getString("resetToken"));
				user.setAvatar(jData.getString("userAvatar"));
				user.setTimeAgo(jData.getString("timeAgo"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

	private MakeModel readMakeData(JSONObject json) throws DataFormatException {

		MakeModel makeModel = new MakeModel();

		try {
			Log.d("Make Result", json.getJSONArray("items").toString());

			JSONArray jData = json.getJSONArray("items");

			ArrayList<String> makeList = new ArrayList<String>();

			for (int i = 0; i < jData.length(); i++){
				makeList.add(jData.getString(i));
			}

			makeModel.setMakeList(makeList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return makeModel;
	}

	private TireModel readModelData(JSONObject json) throws DataFormatException {

		TireModel tireModel = new TireModel();

		try {
			Log.d("Model Result", json.getJSONArray("items").toString());

			JSONArray jData = json.getJSONArray("items");

			ArrayList<String> modelList = new ArrayList<String>();

			for (int i = 0; i < jData.length(); i++){
				modelList.add(jData.getString(i));
			}

			tireModel.setModelList(modelList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return tireModel;
	}

	private FeatureModel readFeatureData(JSONObject json) throws DataFormatException {

		FeatureModel featureModel = new FeatureModel();

		try {
			Log.d("Feature Result", json.getJSONArray("items").toString());

			JSONArray jData = json.getJSONArray("items");

			ArrayList<String> featureList = new ArrayList<String>();

			for (int i = 0; i < jData.length(); i++){
				featureList.add(jData.getString(i));
			}

			featureModel.setFeatureList(featureList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return featureModel;
	}

	private PriceModel readVehiclePriceData(JSONObject json) throws DataFormatException {

		PriceModel priceModel = new PriceModel();
		try {
			Log.d("Vehicle Price Result", json.getJSONArray("items").toString());

			JSONArray jData = json.getJSONArray("items");
			String ratingJSONString = json.getJSONArray("ratings").toString();
			ArrayList<String> priceList = new ArrayList<String>();

			for (int i = 0; i < jData.length(); i++){
				priceList.add(jData.getString(i));
			}

			priceModel.setPriceList(priceList);
			priceModel.setRatingString(ratingJSONString);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return priceModel;
	}

	private PriceModel readSizePriceData(JSONObject json) throws DataFormatException {

		PriceModel priceModel = new PriceModel();

		try {
			Log.d("Vehicle Price Result", json.getJSONArray("items").toString());

			JSONArray jData = json.getJSONArray("items");
			String ratingJSONString = json.getJSONArray("ratings").toString();
			ArrayList<String> priceList = new ArrayList<String>();

			for (int i = 0; i < jData.length(); i++){
				priceList.add(jData.getString(i));
			}

			priceModel.setPriceList(priceList);
			priceModel.setRatingString(ratingJSONString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return priceModel;
	}

	private DataObject readSubmitExperienceData(JSONObject response) throws DataFormatException {

		DataObject dataObject = new DataObject();

		try {
			String status = response.getString(STRING_STATUS);
			dataObject.setStatus(status);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dataObject;
	}

	private DataObject readSubmitEstimate(JSONObject response) throws DataFormatException {

		EstimateModel dataObject = new EstimateModel();

		try {
			String status = response.getString(STRING_STATUS);
			dataObject.setStatus(status);

			if(status.equalsIgnoreCase(STRING_STATUS_OK)) {
				dataObject.setEstimateId(response.getString("item"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dataObject;
	}

	private DataObject readGetEstimate(JSONObject response) throws DataFormatException {
		System.out.println("View History");
		EstimateModel dataObject = new EstimateModel();

		try {
			String status = response.getString(STRING_STATUS);
			dataObject.setStatus(status);

			if(status.equalsIgnoreCase(STRING_STATUS_OK)) {
				dataObject.setEstimateId(response.getJSONObject("item").getString("estimateID"));
				dataObject.setLowPrice(response.getJSONObject("item").getString("lowCost"));
				dataObject.setAvgPrice(response.getJSONObject("item").getString("averageCost"));
				dataObject.setHighPrice(response.getJSONObject("item").getString("highCost"));
				dataObject.setRepairArrayString(response.getJSONObject("item").getString("repairArray"));
				dataObject.setHighCostArrayStringPrice(response.getJSONObject("item").getString("highCostArray"));
				dataObject.setAverageCostArrayStringPrice(response.getJSONObject("item").getString("averageCostArray"));
				dataObject.setLowCostArrayStringPrice(response.getJSONObject("item").getString("lowCostArray"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dataObject;
	}

	private DataObject readShopData(JSONObject json) throws DataFormatException {

		ShopModel dataObject = new ShopModel();

		try {
			Log.d("Shop List Result", json.getJSONArray("items").toString());

			JSONArray jData = json.getJSONArray("items");

			//{"f_id":"14267","f_location":"Stillwater Honda Cars","f_telephone":"888-553-8819","f_address1":"4300 W 6th Ave","f_city":"Stillwater","f_state":"OK","f_zipcode":"74074","f_lat":"36.11612789","f_lng":"-97.11601084","distance":"10.723036364266122"}

			ArrayList<ShopObject> shopList = new ArrayList<ShopObject>();

			for (int i = 0; i < jData.length(); i++){

				JSONObject jShop = jData.getJSONObject(i);
				ShopObject shop = new ShopObject();

				shop.setId(jShop.getInt("f_id"));
				shop.setLocation(jShop.getString("f_location"));
				shop.setPhoneNumber(jShop.getString("f_telephone"));
				shop.setAddress(jShop.getString("f_address1"));
				shop.setCity(jShop.getString("f_city"));
				shop.setState(jShop.getString("f_state"));
				shop.setZipCode(jShop.getString("f_zipcode"));
				shop.setLat(jShop.getDouble("f_lat"));
				shop.setLng(jShop.getDouble("f_lng"));
				shop.setDistance(jShop.getDouble("distance"));

				shopList.add(shop);
			}

			dataObject.setShopList(shopList);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return dataObject;
	}

	/*
	private DataObject readShopData(JSONObject json) throws DataFormatException {
		ValuesMap valuesMap;

		valuesMap = readValuesMap(json, ARRAY_COUNTRY, ARRAY_COUNTRY_NAME, null);

		return valuesMap;
	}

	private DataObject readLanguageList(JSONObject json) throws DataFormatException {
		ValuesMap valuesMap;

		valuesMap = readValuesMap(json, ARRAY_MOTHER_TONGUE, ARRAY_MOTHER_TONGUE_NAME, ARRAY_COUNTRY);

		return valuesMap;
	}


	private ValuesMap readValuesMap(JSONObject json, String keyArray, String valuesArray, String secondaryValuesArray) throws DataFormatException {
		ValuesMap valuesMap;

		valuesMap = new ValuesMap();

		try {
			JSONArray jsonKeys;
			JSONArray jsonValues;
			JSONArray jsonSecondaryValues = null;

			jsonKeys = json.getJSONArray(keyArray);
			jsonValues = json.getJSONArray(valuesArray);
			if (secondaryValuesArray != null) {
				jsonSecondaryValues = json.getJSONArray(secondaryValuesArray);
			}

			for (int i = 0; i < jsonKeys.length(); i++) {
				valuesMap.put(jsonKeys.getString(i), jsonValues.getString(i), jsonSecondaryValues != null ? jsonSecondaryValues.getString(i) : null);
			}
		} catch (JSONException e) {
			throw new DataFormatException(e.getMessage());
		}

		return valuesMap;
	}



	private DataObject readSpecialtyList(JSONObject json) throws DataFormatException {
		ValuesMap valuesMap;

		valuesMap = new ValuesMap();
		try {
			JSONArray jsonArray;

			jsonArray = json.getJSONArray(ARRAY_SPECIALTY);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonSpecialty;

				jsonSpecialty = jsonArray.getJSONObject(i);
				valuesMap.put(jsonSpecialty.getString(STRING_SPECIALTY_ID), jsonSpecialty.getString(STRING_SPECIALTY), null);
			}

		} catch (JSONException e) {
			throw new DataFormatException(e.getMessage());
		}

		return valuesMap;
	}

	private DataObject readInterestList(JSONObject json) throws DataFormatException {
		ValuesMap valuesMap;

		valuesMap = new ValuesMap();
		try {
			JSONArray jsonArray;

			jsonArray = json.getJSONArray(ARRAY_INTERESTS);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonInterest;

				jsonInterest = jsonArray.getJSONObject(i);
				valuesMap.put(jsonInterest.getString(STRING_INTEREST_ID), jsonInterest.getString(STRING_INTEREST), null);
			}

		} catch (JSONException e) {
			throw new DataFormatException(e.getMessage());
		}

		return valuesMap;
	}
	*/

	private ServiceError readDataObjectError(JSONObject json) throws DataFormatException {
		ServiceError serviceError;

		serviceError = new ServiceError();

		try {
			serviceError.setMessage(json.getString(STRING_MESSAGE));

		} catch (JSONException e) {
			throw new DataFormatException(e.getMessage());
		}

		return serviceError;
	}
}
