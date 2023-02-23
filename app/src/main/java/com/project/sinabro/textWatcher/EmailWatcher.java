package com.project.sinabro.textWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project.sinabro.R;
import com.project.sinabro.sideBarMenu.authentication.SignUpStep1;

public class EmailWatcher implements TextWatcher {

    private final TextInputLayout emailTextLayout;

    private TextInputEditText emailEditText;

    private TextView emailConfirmResultTv;

    private final String errorMsg;

    private String calledActivityName;

    private final SignUpStep1 signUpStep1 = new SignUpStep1();

    public EmailWatcher(TextInputLayout textInputLayout, TextInputEditText textInputEditText, String errorMsg, String calledActivityName) {
        this.emailTextLayout = textInputLayout;
        this.emailEditText = textInputEditText;
        this.errorMsg = errorMsg;
        this.calledActivityName = calledActivityName;
    }

    public EmailWatcher(TextInputLayout textInputLayout, TextView textView, String errorMsg, String calledActivityName) {
        this.emailTextLayout = textInputLayout;
        this.emailConfirmResultTv = textView;
        this.errorMsg = errorMsg;
        this.calledActivityName = calledActivityName;
    }

    public EmailWatcher(TextInputLayout textInputLayout, String errorMsg, String calledActivityName) {
        this.emailTextLayout = textInputLayout;
        this.errorMsg = errorMsg;
        this.calledActivityName = calledActivityName;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // SignUpStep1 액티비티에서 호출된 EmailWatcher의 경우, 하단의 if문 실행
        if (calledActivityName.equals("SignUpStep1") && signUpStep1.emailConfirm) {
            signUpStep1.emailConfirm = false;
            emailConfirmResultTv.setVisibility(View.GONE);
        }

        if (s.toString().equals("")) {
            emailTextLayout.setError(null);
            emailTextLayout.setErrorEnabled(false);
            emailTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            emailTextLayout.setPadding(-25, 0, 0, 20);
        } else if (!validateEmail(s.toString())) {
            emailTextLayout.setError(errorMsg);
            emailTextLayout.setErrorEnabled(true);
            emailTextLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
        } else {
            emailTextLayout.setError(null);
            emailTextLayout.setErrorEnabled(false);
            emailTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            emailTextLayout.setPadding(-25, 0, 0, 20);
        }
    }

    public boolean validateEmail(String s) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return s.matches(emailPattern);
    }
}