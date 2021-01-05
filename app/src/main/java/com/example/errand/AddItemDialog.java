package com.example.errand;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class AddItemDialog extends DialogFragment {


    AddItemDialogListener mListener;

    public AddItemDialog(AddItemDialogListener listener) {
        mListener =  listener;
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
        View layout = inflater.inflate(R.layout.add_item_dialog_layout, null);

        final EditText nameEditText = layout.findViewById(R.id.item_name);
        final EditText sizeEditText = layout.findViewById(R.id.item_size);
        final EditText amountEditText = layout.findViewById(R.id.item_number);


        layout.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog.this.getDialog().cancel();
                Item item = new Item(nameEditText.getText().toString(),
                                    sizeEditText.getText().toString(),
                                    amountEditText.getText().toString());
                mListener.onItemAdded(item);
            }
        });

        layout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialog.this.getDialog().cancel();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.setView(layout,0,0,0,0);
        return dialog;
    }
}
