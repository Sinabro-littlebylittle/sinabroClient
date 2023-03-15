package com.project.sinabro.textWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project.sinabro.R;

public class NicknameWatcher implements TextWatcher {

    private final TextInputLayout nicknameTextLayout;

    private TextInputEditText nicknameEditText;

    private TextView nicknameConfirmResultTv;

    private final String errorMsg;

    public NicknameWatcher(TextInputLayout textInputLayout, TextInputEditText textInputEditText, String errorMsg) {
        this.nicknameTextLayout = textInputLayout;
        this.nicknameEditText = textInputEditText;
        this.errorMsg = errorMsg;
    }

    public NicknameWatcher(TextInputLayout textInputLayout, TextView textView, String errorMsg) {
        this.nicknameTextLayout = textInputLayout;
        this.nicknameConfirmResultTv = textView;
        this.errorMsg = errorMsg;
    }

    public NicknameWatcher(TextInputLayout textInputLayout, String errorMsg) {
        this.nicknameTextLayout = textInputLayout;
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
            nicknameTextLayout.setError(null);
            nicknameTextLayout.setErrorEnabled(false);
            nicknameTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            nicknameTextLayout.setPadding(-34, 20, 0, 20);
        } else if (!validateNickname(s.toString())) {
            nicknameTextLayout.setError(errorMsg);
            nicknameTextLayout.setErrorEnabled(true);
            nicknameTextLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
        } else {
            nicknameTextLayout.setError(null);
            nicknameTextLayout.setErrorEnabled(false);
            nicknameTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            nicknameTextLayout.setPadding(-34, 20, 0, 20);
        }
    }

    public boolean validateNickname(String s) {
        String nicknamePattern = "^[a-zA-Z0-9가-힣 ]{2,10}$";
        return s.matches(nicknamePattern);
    }
}
