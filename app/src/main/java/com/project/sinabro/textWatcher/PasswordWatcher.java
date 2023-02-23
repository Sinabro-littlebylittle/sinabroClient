package com.project.sinabro.textWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project.sinabro.R;

public class PasswordWatcher implements TextWatcher {

    private final TextInputLayout passwordTextLayout;

    private TextInputEditText passwordEditText;

    private TextView passwordTextView;

    private final String errorMsg;

    public PasswordWatcher(TextInputLayout textInputLayout, TextInputEditText textInputEditText, String errorMsg) {
        this.passwordTextLayout = textInputLayout;
        this.passwordEditText = textInputEditText;
        this.errorMsg = errorMsg;
    }

    public PasswordWatcher(TextInputLayout textInputLayout, TextView textView, String errorMsg) {
        this.passwordTextLayout = textInputLayout;
        this.passwordTextView = textView;
        this.errorMsg = errorMsg;
    }

    public PasswordWatcher(TextInputLayout textInputLayout, String errorMsg) {
        this.passwordTextLayout = textInputLayout;
        this.errorMsg = errorMsg;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().equals("")) {
            passwordTextLayout.setError(null);
            passwordTextLayout.setErrorEnabled(false);
            passwordTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            passwordTextLayout.setPadding(-25, 0, 0, 20);
        } else if (!validatePassword(s.toString())) {
            passwordTextLayout.setError(errorMsg);
            passwordTextLayout.setErrorEnabled(true);
            passwordTextLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
        } else {
            passwordTextLayout.setError(null);
            passwordTextLayout.setErrorEnabled(false);
            passwordTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            passwordTextLayout.setPadding(-25, 0, 0, 20);
        }
    }

    public boolean validatePassword(String s) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,20}.$";
        return s.matches(passwordPattern);
    }
}