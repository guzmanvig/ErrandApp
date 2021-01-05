package com.example.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NeedErrandActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_errand);

        findViewById(R.id.post_errand_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeedErrandActivity.this, PostErrandRequestActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.search_ongoing_errands_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeedErrandActivity.this, OngoingErrandsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.see_errands_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeedErrandActivity.this, PostedRequestsActivity.class);
                startActivity(intent);
            }
        });
    }
}
