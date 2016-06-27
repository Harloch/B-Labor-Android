package com.barelabor.barelabor.activity;

import android.content.Intent;
import android.os.Bundle;

import com.barelabor.barelabor.R;
import com.github.paolorotolo.appintro.AppIntro;

public class introActivity extends AppIntro {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        setFadeAnimation();
        addSlide(SampleSlide.newInstance(R.layout.intro1));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));
        addSlide(SampleSlide.newInstance(R.layout.intro5));
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        startActivity(new Intent(introActivity.this, MenuActivity.class));
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        System.out.println("done");
        startActivity(new Intent(introActivity.this, MenuActivity.class));
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
        System.out.println("next");
    }

}