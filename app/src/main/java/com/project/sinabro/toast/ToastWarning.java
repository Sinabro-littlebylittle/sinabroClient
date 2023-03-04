package com.project.sinabro.toast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sinabro.R;

public class ToastWarning {
    public ToastWarning(String message, Activity activity) {
        Toast toast = new Toast(activity);
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.toast_warning, null);

        TextView tvMessage = view.findViewById(R.id.toast_check_failed_tv);
        tvMessage.setText(message);

        toast.setView(view);
        toast.show();
    }
}
