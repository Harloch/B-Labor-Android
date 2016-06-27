package com.barelabor.barelabor.activity.experience;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.barelabor.barelabor.R;

public class ExperienceActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnPositive, btnNegative;
    private Boolean isPositive = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);

        initView();
    }

    private void initView(){

        btnPositive = (Button) findViewById(R.id.btnPositive);
        btnNegative = (Button) findViewById(R.id.btnNegative);

        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == btnPositive){
            isPositive = true;
        }else{
            isPositive = false;
        }

        Intent intent = new Intent(ExperienceActivity.this, ExperienceDetailActivity.class);
        intent.putExtra("isPositive", isPositive);

        startActivity(intent);
    }
}
