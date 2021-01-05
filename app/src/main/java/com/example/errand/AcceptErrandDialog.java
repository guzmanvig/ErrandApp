package com.example.errand;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

public class AcceptErrandDialog extends DialogFragment {

    private ModelErrandRequest mModel;

    public AcceptErrandDialog(ModelErrandRequest model) {
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
        View layout = inflater.inflate(R.layout.accept_errand_dialog_layout, null);

        TextView name = layout.findViewById(R.id.name_text_view);
        name.setText(mModel.getRequesterName());

        TextView time = layout.findViewById(R.id.items_text_view);
        time.setText(mModel.getItems());

        TextView store = layout.findViewById(R.id.reward_text_view);
        store.setText(mModel.getReward());

        TextView reward = layout.findViewById(R.id.text_view_categories);
        reward.setText(mModel.getCategories());

        TextView vulnerable = layout.findViewById(R.id.vulnerable_text);
        if (mModel.isRequesterIsVulnerable()) {
            vulnerable.setVisibility(View.VISIBLE);
        } else {
            vulnerable.setVisibility(View.INVISIBLE);
        }

        layout.findViewById(R.id.post_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptErrandDialog.this.getDialog().cancel();
            }
        });

        layout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptErrandDialog.this.getDialog().cancel();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.setView(layout,0,0,0,0);
        return dialog;
    }
}
