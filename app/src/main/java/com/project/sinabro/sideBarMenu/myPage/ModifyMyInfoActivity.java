package com.project.sinabro.sideBarMenu.myPage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityModifyMyInfoBinding;
import com.project.sinabro.databinding.ActivitySignUpStep2Binding;
import com.project.sinabro.sideBarMenu.authentication.SignIn;
import com.project.sinabro.sideBarMenu.authentication.SignUpStep2;
import com.project.sinabro.textWatcher.EmailWatcher;
import com.project.sinabro.textWatcher.NicknameWatcher;
import com.project.sinabro.toast.SuccessToast;

public class ModifyMyInfoActivity extends AppCompatActivity {

    private ActivityModifyMyInfoBinding binding;

    public static Boolean emailConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        emailConfirm = true;

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.nicknameTextInputLayout.getEditText().addTextChangedListener(new NicknameWatcher(binding.nicknameTextInputLayout, getResources().getString(R.string.sign_up_nickname_failed)));
        binding.emailTextInputLayout.getEditText().addTextChangedListener(new EmailWatcher(binding.emailTextInputLayout, binding.emailConfirmResultTv, getResources().getString(R.string.sign_up_email), "ModifyMyInfoActivity"));

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

        binding.modifyCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (String.valueOf(binding.nicknameEditText.getText()).equals("")) {
                    binding.nicknameEditText.requestFocus();
                    binding.nicknameTextInputLayout.setError(getResources().getString(R.string.sign_up_nickname_failed));
                    binding.nicknameTextInputLayout.setErrorEnabled(true);
                    binding.nicknameTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (!emailConfirm || String.valueOf(binding.emailEditText.getText()).equals("") || !emailConfirm) {
                    binding.emailEditText.requestFocus();
                    binding.emailTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_email_validation_failed));
                    binding.emailTextInputLayout.setErrorEnabled(true);
                    binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else {
                    // 모든 입력이 정상적으로 완료되었을 때
                    new SuccessToast(getResources().getString(R.string.toast_modify_my_info_success), ModifyMyInfoActivity.this);
                    onBackPressed(); // 뒤로가기 기능 수행
                    finish(); // 현재 액티비티 종료
                }
            }
        });
    }
}