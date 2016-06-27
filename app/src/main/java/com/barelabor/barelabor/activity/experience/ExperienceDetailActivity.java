package com.barelabor.barelabor.activity.experience;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.IDataProviderCallback;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.UserModel;
import com.barelabor.barelabor.util.MessageService;
import com.barelabor.barelabor.util.Support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ExperienceDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout lltQuestions;
    private EditText txtName, txtEmail, txtShopName, txtComments;
    private Button btnSubmit;

    private Boolean isPositive = true;

    private ArrayList<RadioGroup> radioGroups = new ArrayList<RadioGroup>();

    private String selectedAnswers, name, email, shopName, comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_detail);

        isPositive = getIntent().getBooleanExtra("isPositive", true);

        initView();
        loadQuestions();
    }

    private void loadQuestions(){
        String strQuestions = Support.loadJSONFromAsset(this, "questions.json");
        try {
            JSONObject jsonObject = new JSONObject(strQuestions);
            JSONArray jsonQuestions = jsonObject.getJSONArray("questions");

            for (int i = 0; i < jsonQuestions.length(); i++){
                String question = jsonQuestions.getJSONObject(i).getString("question");
                JSONArray jsonAnswers = jsonQuestions.getJSONObject(i).getJSONArray("answers");

                View view = getQuestionView(question, jsonAnswers);
                lltQuestions.addView(view);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        lltQuestions = (LinearLayout) findViewById(R.id.lltQuestions);

        txtName = (EditText) findViewById(R.id.txtName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtShopName = (EditText) findViewById(R.id.txtShopName);
        txtComments = (EditText) findViewById(R.id.txtComments);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
    }

    private View getQuestionView(String question, JSONArray answers){

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = (View) inflater.inflate(R.layout.layout_question, null, false);

        TextView lblQuestion = (TextView)view.findViewById(R.id.lblQuestion);
        RadioGroup rgAnswers = (RadioGroup)view.findViewById(R.id.rgAnswers);

        lblQuestion.setText(question);

        try {
            for (int i = 0; i < answers.length(); i++){
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(answers.getString(i));
                radioButton.setTextColor(Color.WHITE);
                rgAnswers.addView(radioButton);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    protected boolean postData(DataProvider dataProvider, DataProviderCallback callback) {

        String userId = Support.getSharedPreference(this).getUserId();
		Toast.makeText(this, userId, Toast.LENGTH_LONG).show();
        dataProvider.sumbitExperience(userId, (isPositive) ? "1":"0", selectedAnswers, name, email, shopName, comments, callback);

        return true;
    }

    @Override
    protected void handlePostResponse(DataObject dataObject) {
        if (dataObject != null){
            Support.getMessageService(this).showInfo(this, "Successfully Sent", false);
        }else{
            Support.getMessageService(this).showWarning(this, "Connection Trouble", false);
        }
    }

    private Boolean validate(){

        if (selectedAnswers.length() == 0 || name.length() == 0 || email.length() == 0 || shopName.length() == 0 || comments.length() == 0) {
            Support.getMessageService(this).showWarning(this, "Please fill all fields", false);
            return false;
        }

        return true;
    }
    @Override
    public void onClick(View v) {
        if (v == btnSubmit){

            getRadioGroups(lltQuestions);

            ArrayList<Integer> selectedIndexs = new ArrayList<Integer>();
            for( RadioGroup radioGroup : radioGroups) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();

                if (radioButtonID == -1) {
                    Support.getMessageService(this).showWarning(this, "Please select all answers", false);
                    return;
                }
                View radioButton = radioGroup.findViewById(radioButtonID);
                int index = radioGroup.indexOfChild(radioButton);

                selectedIndexs.add(index);
            }

            selectedAnswers = TextUtils.join(",", selectedIndexs);

            name = txtName.getText().toString();
            email = txtEmail.getText().toString();
            shopName = txtShopName.getText().toString();
            comments = txtComments.getText().toString();

            if (validate())
                postData();
        }
    }

    private void getRadioGroups(ViewGroup viewGroup) {

        radioGroups.clear();

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            ViewGroup view = (ViewGroup) viewGroup.getChildAt(i);

            if(view.getChildAt(1) instanceof RadioGroup)
                radioGroups.add((RadioGroup)view.getChildAt(1));
        }
    }
}
