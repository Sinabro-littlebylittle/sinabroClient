package com.project.sinabro.sideBarMenu.authentication;

import androidx.appcompat.app.AppCompatActivity;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivitySignUpStep1Binding;
import com.project.sinabro.textWatcher.EmailWatcher;
import com.project.sinabro.textWatcher.PasswordConfirmWatcher;
import com.project.sinabro.textWatcher.PasswordWatcher;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

public class SignUpStep1 extends AppCompatActivity {

    private ActivitySignUpStep1Binding binding;

    public static Boolean emailConfirm;

    private Boolean password_toggle = true,
            passwordConfirm_toggle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpStep1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        emailConfirm = false;

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("뒤로 감", "아잉눈");
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.emailTextInputLayout.getEditText().addTextChangedListener(new EmailWatcher(binding.emailTextInputLayout, binding.emailConfirmResultTv, getResources().getString(R.string.sign_up_email), "SignUpStep1"));
        binding.passwordTextInputLayout.getEditText().addTextChangedListener(new PasswordWatcher(binding.passwordTextInputLayout, getResources().getString(R.string.sign_up_password)));
        binding.passwordConfirmTextInputLayout.getEditText().addTextChangedListener(new PasswordConfirmWatcher(binding.passwordTextInputLayout, binding.passwordConfirmTextInputLayout, getResources().getString(R.string.sign_up_step1_password_confirm_validation_failed)));

        /** "로그인" textView 클릭 시 로그인 액티비티로 이동하는 코드 */
        binding.signInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

        /** 이메일 내용 지우기 X 버튼 클릭 시  */
        binding.emailTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something when the end icon is clicked
                emailConfirm = false;
                binding.emailEditText.setText("");
                binding.emailConfirmResultTv.setVisibility(View.GONE);
            }
        });

        /** "비밀번호 입력 란" 비밀번호 show/hidden 아이콘 클릭 시 */
        binding.passwordTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_toggle) {
                    binding.passwordEditText.setPadding(34, 50, 0, 25);
                    binding.passwordEditText.setTransformationMethod(null);
                } else
                    binding.passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                password_toggle = !password_toggle;
            }
        });

        /** "비밀번호 재확인 입력 란" 비밀번호 show/hidden 아이콘 클릭 시 */
        binding.passwordConfirmTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordConfirm_toggle) {
                    binding.passwordConfirmEditText.setTransformationMethod(null);
                    binding.passwordConfirmEditText.setPadding(34, 50, 0, 25);
                } else
                    binding.passwordConfirmEditText.setTransformationMethod(new PasswordTransformationMethod());
                passwordConfirm_toggle = !passwordConfirm_toggle;
            }
        });

        /** 이메일 확인 버튼 클릭 시 이벤트 처리 코드 */
        binding.emailConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 이곳에 API 호출 코드가 추가되어야 합니다. */

                // 입력된 이메일이 DB 내에 존재하지 않을 때 (사용 가능) 관련 문구 표기하기
                binding.emailTextInputLayout.setErrorEnabled(false);
                binding.emailConfirmResultTv.setTextColor(getResources().getColor(R.color.blue));
                binding.emailConfirmResultTv.setText(getResources().getString(R.string.sign_up_email_confirm_success));
                binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_selector);
                binding.emailTextInputLayout.setPadding(-34, 20, 0, 20);
                emailConfirm = true;

                // 입력된 이메일이 DB 내에 이미 존재할 때 (사용 불가) 관련 문구 표기하기
//                binding.emailConfirmResultTv.setTextColor(getResources().getColor(R.color.red));
//                binding.emailConfirmResultTv.setText(getResources().getString(R.string.signup_email_confirm_failed));

                binding.emailConfirmResultTv.setVisibility(View.VISIBLE);
                binding.emailEditText.clearFocus();
            }
        });

        /** "다음" 버튼 클릭 시 회원가입 단계(2) 액티비티로 이동하는 코드 */
        binding.goSignUpStep2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (!emailConfirm || String.valueOf(binding.emailEditText.getText()).equals("")) {
                    binding.emailEditText.requestFocus();
                    binding.emailTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_email_validation_failed));
                    binding.emailTextInputLayout.setErrorEnabled(true);
                    binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (binding.passwordTextInputLayout.getError() != null || String.valueOf(binding.passwordEditText.getText()).equals("")) {
                    binding.passwordEditText.requestFocus();
                    binding.passwordTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_password_validation_failed));
                    binding.passwordTextInputLayout.setErrorEnabled(true);
                    binding.passwordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (!String.valueOf(binding.passwordEditText.getText()).equals(String.valueOf(binding.passwordConfirmEditText.getText()))) {
                    binding.passwordConfirmEditText.requestFocus();
                    binding.passwordConfirmTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_current_password_validation_failed));
                    binding.passwordConfirmTextInputLayout.setErrorEnabled(true);
                    binding.passwordConfirmTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if ((binding.passwordConfirmTextInputLayout.getError() != null || String.valueOf(binding.passwordConfirmEditText.getText()).equals("")) && !String.valueOf(binding.passwordEditText.getText()).equals(String.valueOf(binding.passwordConfirmEditText.getText()))) {
                    binding.passwordConfirmEditText.requestFocus();
                    binding.passwordConfirmTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_password_confirm_validation_failed));
                    binding.passwordConfirmTextInputLayout.setErrorEnabled(true);
                    binding.passwordConfirmTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else {
                    // "회원가입 2단계" 액티비티로 이동
                    final Intent intent = new Intent(getApplicationContext(), SignUpStep2.class);
                    startActivity(intent);
                }
            }
        });
    }
}