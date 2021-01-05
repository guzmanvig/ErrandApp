package com.example.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DoingErrandActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_errand);

        findViewById(R.id.post_errand_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoingErrandActivity.this, PostOngoingErrandActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.search_ongoing_errands_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoingErrandActivity.this, OngoingRequestsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.see_errands_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoingErrandActivity.this, PostedErrandsActivity.class);
                startActivity(intent);
            }
        });
    }
}
