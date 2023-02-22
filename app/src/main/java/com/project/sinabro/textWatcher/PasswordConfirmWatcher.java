package com.project.sinabro.textWatcher;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;
import com.project.sinabro.R;

public class PasswordConfirmWatcher implements TextWatcher {

    private TextInputLayout passwordConfirmTextLayout;
    private TextInputLayout passwordTextLayout;
    private String errorMsg;

    public PasswordConfirmWatcher(TextInputLayout passwordTextLayout, TextInputLayout passwordConfirmTextLayout, String errorMsg) {
        this.passwordConfirmTextLayout = passwordConfirmTextLayout;
        this.passwordTextLayout = passwordTextLayout;
        this.errorMsg = errorMsg;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String password = passwordTextLayout.getEditText().getText().toString();

        if (s.toString().equals("")) {
            passwordConfirmTextLayout.setError(null);
            passwordConfirmTextLayout.setErrorEnabled(false);
            passwordConfirmTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            passwordConfirmTextLayout.setPadding(-25, 0, 0, 20);
        } else if (!s.toString().equals(password)) {
            passwordConfirmTextLayout.setError(errorMsg);
            passwordConfirmTextLayout.setErrorEnabled(true);
            passwordConfirmTextLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
        } else {
            passwordConfirmTextLayout.setError(null);
            passwordConfirmTextLayout.setErrorEnabled(false);
            passwordConfirmTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            passwordConfirmTextLayout.setPadding(-25, 0, 0, 20);
        }
    }
}
