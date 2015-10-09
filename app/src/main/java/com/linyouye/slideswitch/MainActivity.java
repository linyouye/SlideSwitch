package com.linyouye.slideswitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SlideSwitch mSlideSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlideSwitch = (SlideSwitch) findViewById(R.id.slideSwitch);
        mSlideSwitch.setOnCheckedChangeListener(new SlideSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean isChecked) {
                Toast.makeText(MainActivity.this, isChecked ? "checked" : "unchecked", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
