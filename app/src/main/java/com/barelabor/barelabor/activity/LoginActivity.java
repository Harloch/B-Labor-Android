package com.barelabor.barelabor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.data.DataProvider;
import com.barelabor.barelabor.data.DataProviderCallback;
import com.barelabor.barelabor.data.JsonDataHandler;
import com.barelabor.barelabor.data.model.DataObject;
import com.barelabor.barelabor.data.model.UserModel;
import com.barelabor.barelabor.util.Support;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private RadioButton btnSignUp, btnLogin;
    private Button btnSubmit;
    private EditText txtUserName, txtEmail, txtPassword, txtCPassword;
    private RelativeLayout layoutCPassword, layoutEmail;

    private Boolean isLogin = false;
    private String userName = "";
    private String email = "";
    private String password = "";
    private String cPassword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

    }

    private void initView(){

        layoutCPassword = (RelativeLayout)findViewById(R.id.layoutCPassword);
        layoutEmail = (RelativeLayout)findViewById(R.id.layoutEmail);

        btnSignUp = (RadioButton)findViewById(R.id.btnSignup);
        btnLogin = (RadioButton)findViewById(R.id.btnLogin);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);

        txtUserName = (EditText)findViewById(R.id.txtUserName);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtCPassword = (EditText)findViewById(R.id.txtCPassword);

        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void updateView(){
        if (isLogin) {
            layoutCPassword.setVisibility(View.GONE);
            layoutEmail.setVisibility(View.GONE);
            txtUserName.setHint(R.string.hint_username);

            //txtUserName.setText("teste");
            //txtPassword.setText("password");
        }else {
            layoutCPassword.setVisibility(View.VISIBLE);
            layoutEmail.setVisibility(View.VISIBLE);
            txtUserName.setHint(R.string.hint_create_user);
        }
    }

    private Boolean validate(){

        if (userName.length() == 0){
            showWarningDialog(Support.getText(this, R.string.warn_empty_username));
            return false;
        }else if(password.length() == 0){
            showWarningDialog(Support.getText(this, R.string.warn_empty_password));
            return false;
        }else if (!isLogin){
            if (email.length() == 0){
                showWarningDialog(Support.getText(this, R.string.warn_empty_email));
            }else if(!password.equals(cPassword)){
                showWarningDialog(Support.getText(this, R.string.warn_incorrect_password));
                return false;
            }
        }
        return true;
    }

    private void showWarningDialog(String message){
        Support.getMessageService(this).showError(this, message, false);
    }

    @Override
    protected boolean postData(DataProvider dataProvider, DataProviderCallback callback) {

//		Toast.makeText(LoginActivity.this, userInfo, Toast.LENGTH_LONG).show();

        String deviceToken = Support.getSharedPreference(this).getDeviceToken();

        if(isLogin)
            return dataProvider.login(userName, password, deviceToken, callback);
        else
            return dataProvider.signup(userName, password, email, deviceToken, callback);
    }

    @Override
    protected void handlePostResponse(DataObject dataObject) {

        UserModel userData = (UserModel) dataObject;

        if(userData.getStatus().equalsIgnoreCase(JsonDataHandler.STRING_STATUS_CONFLICT)){
            showWarningDialog("User with current name existed");
        }else if(userData.getStatus().equalsIgnoreCase(JsonDataHandler.STRING_STATUS_NOT_FOUND)){
            showWarningDialog("Please check your login or password");
        }else if(userData.getStatus().equalsIgnoreCase(JsonDataHandler.STRING_STATUS_NOT_UNAUTHORIZED)){
            showWarningDialog("Please check your login or password");
        }else{
            Support.getSharedPreference(this).storeUserId(userData.getUserId());

            Intent i = new Intent(this, introActivity.class);
            startActivity(i);
        }

    }

    @Override
    public void onClick(View v) {

        if (v == btnLogin){
            isLogin = true;
            updateView();
        }else if(v == btnSignUp){
            isLogin = false;
            updateView();
        }else if(v == btnSubmit){

            userName = txtUserName.getText().toString();
            password = txtPassword.getText().toString();

            if (!isLogin) {
                cPassword = txtCPassword.getText().toString();
                email = txtEmail.getText().toString();
            }

            if(validate()){
                postData();

//                Intent intent1 = new Intent(LoginActivity.this, ExperienceDetailActivity.class);
//                startActivity(intent1);
            }
        }
    }
}
