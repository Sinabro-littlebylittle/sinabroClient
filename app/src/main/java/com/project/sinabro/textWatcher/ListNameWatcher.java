package com.project.sinabro.textWatcher;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project.sinabro.R;

public class ListNameWatcher implements TextWatcher {

    private TextInputLayout listNameTextLayout;

    private TextInputEditText listNameEditText;

    private String errorMsg;

    public ListNameWatcher(TextInputLayout textInputLayout, String errorMsg) {
        this.listNameTextLayout = textInputLayout;
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
            listNameTextLayout.setError(null);
            listNameTextLayout.setErrorEnabled(false);
            listNameTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            listNameTextLayout.setPadding(-34, 20, 0, 20);
        } else if (!validateNickname(s.toString())) {
            listNameTextLayout.setError(errorMsg);
            listNameTextLayout.setErrorEnabled(true);
            listNameTextLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
        } else {
            listNameTextLayout.setError(null);
            listNameTextLayout.setErrorEnabled(false);
            listNameTextLayout.setBackgroundResource(R.drawable.edt_bg_selector);
            listNameTextLayout.setPadding(-34, 20, 0, 20);
        }
    }

    public boolean validateNickname(String s) {
        String listNamePattern = "^[a-zA-Z0-9가-힣 ]{2,14}$";
        return s.matches(listNamePattern);
    }
}
