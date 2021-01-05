package com.example.errand;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class RequestErrandDialog extends DialogFragment {

    private ModelErrandOngoing mModel;

    public RequestErrandDialog(ModelErrandOngoing model) {
        mModel = model;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return new FrameLayout(getContext());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.request_errand_dialog_layout, null);

        TextView name = layout.findViewById(R.id.name_text_view);
        name.setText(mModel.getName());

        TextView time = layout.findViewById(R.id.date_text_view);
        time.setText(mModel.getDate());

        TextView store = layout.findViewById(R.id.store_text_view);
        store.setText(mModel.getStore());

        TextView reward = layout.findViewById(R.id.reward_text_view);
        reward.setText(mModel.getReward());

        TextView categories = layout.findViewById(R.id.text_view_categories);
        categories.setText(mModel.getCategory());

        layout.findViewById(R.id.post_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestErrandDialog.this.getDialog().cancel();
                Intent intent = new Intent(getContext(), PostErrandRequestActivity.class);
                intent.putExtra("ErrandId", mModel.getOngoingErrandId());
                startActivity(intent);
            }
        });

        layout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestErrandDialog.this.getDialog().cancel();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.setView(layout,0,0,0,0);
        return dialog;
    }
}
