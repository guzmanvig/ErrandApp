package com.example.errand;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class PostedRequestsActivity extends Activity {

    PostedRequestsActivity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_requests);

        final LinearLayout posted_req_layout = findViewById(R.id.posted_reqs_layout);

        Database databaseManager = new Database();
        databaseManager.retreiveOngoingRequests(new DatabaseListener() {
            @Override
            public void onOngoingErrandsFetchComplete(List<ModelErrandOngoing> list) {
            }

            @Override
            public void onOngoingRequestsFetchComplete(List<ModelErrandRequest> list) {
                for (ModelErrandRequest errand : list) {
                    String items = errand.getItems();
                    String categories = errand.getCategories();
                    String status = errand.getAcceptedStatus();

                    int font_color = ContextCompat.getColor(getActivity(), R.color.font);
                    Spannable store_span = new SpannableString(items);
                    store_span.setSpan(new StyleSpan(Typeface.BOLD), 0, store_span.length(), store_span.SPAN_EXCLUSIVE_EXCLUSIVE);
                    store_span.setSpan(new ForegroundColorSpan(font_color), 0, store_span.length(), store_span.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Spannable comments_span = new SpannableString(categories);
                    comments_span.setSpan(new ForegroundColorSpan(font_color), 0, comments_span.length(), comments_span.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Spannable status_span = new SpannableString(status);
                    status_span.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, status_span.length(), status_span.SPAN_EXCLUSIVE_EXCLUSIVE);
                    status_span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, status_span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (status != null && status.toLowerCase().contentEquals("pending")) {
                        status_span.setSpan(new ForegroundColorSpan(Color.WHITE), 0, status_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        status_span.setSpan(new BackgroundColorSpan(Color.RED), 0, status_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (status != null) {
                        status_span.setSpan(new ForegroundColorSpan(Color.WHITE), 0, status_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        status_span.setSpan(new BackgroundColorSpan(Color.GREEN), 0, status_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    SpannableStringBuilder textToDisplayLeft = new SpannableStringBuilder();
                    textToDisplayLeft.append(store_span).append("\n").append(comments_span);

                    TextView textViewLeft = new TextView(getActivity());
                    textViewLeft.setText(textToDisplayLeft);
                    textViewLeft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 170));
                    textViewLeft.setTextSize(18f);
                    textViewLeft.setGravity(Gravity.START);


                    TextView textViewRight = new TextView(getActivity());
                    textViewRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 170));
                    textViewRight.setTextSize(18f);
                    textViewRight.setGravity(Gravity.END);
                    textViewRight.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    textViewRight.setText(status_span);

                    LinearLayout buttonLayout = new LinearLayout(getActivity());
                    buttonLayout.setPadding(30, 30, 30, 30);
                    buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                    buttonLayout.setBackgroundResource(R.drawable.custom_button_listview);
//                                buttonLayout.setId();
                    buttonLayout.addView(textViewLeft);
                    buttonLayout.addView(textViewRight);

                    posted_req_layout.addView(buttonLayout);


                }

            }
        });



        /*FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posted_errands")
//                .whereEqualTo("volunteer_id", 1)
                .orderBy("sys_creation_date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Button textButton = new Button(getActivity());
                                String store = (String) document.get("store");
                                String comments = (String) document.get("comments");
                                String status = (String) document.get("accepted_status");

                                Spannable store_span = new SpannableString(store);
                                store_span.setSpan(new StyleSpan(Typeface.BOLD), 0, store_span.length(), store_span.SPAN_EXCLUSIVE_EXCLUSIVE);
                                store_span.setSpan(new ForegroundColorSpan(Color.parseColor("#1f6d43")), 0, store_span.length(), store_span.SPAN_EXCLUSIVE_EXCLUSIVE);

                                Spannable comments_span = new SpannableString(comments);
                                comments_span.setSpan(new ForegroundColorSpan(Color.parseColor("#1f6d43")), 0, comments_span.length(), comments_span.SPAN_EXCLUSIVE_EXCLUSIVE);

                                Spannable status_span = new SpannableString(status);
                                status_span.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, status_span.length(), status_span.SPAN_EXCLUSIVE_EXCLUSIVE);
                                status_span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, status_span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                if (status != null && status.toLowerCase().contentEquals("pending")) {
                                    status_span.setSpan(new ForegroundColorSpan(Color.RED), 0, status_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                } else if (status != null) {
                                    status_span.setSpan(new ForegroundColorSpan(Color.GREEN), 0, status_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                SpannableStringBuilder textToDisplayLeft = new SpannableStringBuilder();
                                textToDisplayLeft.append(store_span).append("\n").append(comments_span);

                                TextView textViewLeft = new TextView(getActivity());
                                textViewLeft.setText(textToDisplayLeft);
                                textViewLeft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 170));
//                                textViewLeft.setBackgroundResource(R.drawable.custom_button_listview);
                                textViewLeft.setTextSize(18f);
                                textViewLeft.setGravity(Gravity.START);


                                TextView textViewRight = new TextView(getActivity());
                                textViewRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 170));
//                                textViewRight.setBackgroundResource(R.drawable.custom_button_listview);
                                textViewRight.setTextSize(18f);
                                textViewRight.setGravity(Gravity.END);
                                textViewRight.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                textViewRight.setText(status_span);

                                LinearLayout buttonLayout = new LinearLayout(getActivity());
                                buttonLayout.setPadding(30, 30, 30, 30);
                                buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                                buttonLayout.setBackgroundResource(R.drawable.custom_button_listview);
//                                buttonLayout.setId();
                                buttonLayout.addView(textViewLeft);
                                buttonLayout.addView(textViewRight);

                                posted_req_layout.addView(buttonLayout);
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }); */
    }
}