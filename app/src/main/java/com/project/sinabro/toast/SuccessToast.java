package com.project.sinabro.toast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sinabro.R;

public class SuccessToast {
    public SuccessToast(String message, Activity activity) {
        Toast toast = new Toast(activity);
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.toast_modify_success, null);

        TextView tvMessage = view.findViewById(R.id.toast_modify_success_tv);
        tvMessage.setText(message);

        toast.setView(view);
        toast.show();
    }
}
