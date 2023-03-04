package com.project.sinabro.sideBarMenu.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivitySignInBinding;
import com.project.sinabro.sideBarMenu.settings.HandleUserInformationActivity;
import com.project.sinabro.sideBarMenu.settings.OthersUseUserInformationActivity;
import com.project.sinabro.sideBarMenu.settings.StoreUserInformationActivity;
import com.project.sinabro.sideBarMenu.settings.UseServiceAgreementActivity;
import com.project.sinabro.textWatcher.EmailWatcher;
import com.project.sinabro.textWatcher.PasswordWatcher;
import com.project.sinabro.toast.ToastWarning;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    private Boolean password_toggle = true;

    private Dialog agree_our_policies_dialog;

    private Boolean check_policies_1_toggle = false, check_policies_2_toggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** "비밀번호 변경 실패 안내" 다이얼로그 변수 초기화 및 설정 */
        agree_our_policies_dialog = new Dialog(SignInActivity.this);  // Dialog 초기화
        agree_our_policies_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        agree_our_policies_dialog.setContentView(R.layout.dialog_agree_our_policies); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        agree_our_policies_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.emailTextInputLayout.getEditText().addTextChangedListener(new EmailWatcher(binding.emailTextInputLayout, getResources().getString(R.string.sign_in_email_failed), "SignIn"));
        binding.passwordTextInputLayout.getEditText().addTextChangedListener(new PasswordWatcher(binding.passwordTextInputLayout, getResources().getString(R.string.sign_in_password_failed)));

        /** "회원가입" textView 클릭 시 회원가입 액티비티로 이동하는 코드 */
        binding.signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_agree_our_policies();
            }
        });

        /** "비밀번호 입력 란" 비밀번호 show/hidden 아이콘 클릭 시 */
        binding.passwordTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_toggle) {
                    binding.passwordEditText.setTransformationMethod(null);
                    binding.passwordEditText.setPadding(34, 50, 0, 25);
                } else
                    binding.passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                password_toggle = !password_toggle;
            }
        });

        /** "비밀번호 찾기" textView 클릭 시 비밀번호 초기화 액티비티로 이동하는 코드 */
        binding.resetPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        /** "로그인" 버튼 클릭 시 이벤트 처리 코드 */
        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (String.valueOf(binding.emailEditText.getText()).equals("")) {
                    binding.emailEditText.requestFocus();
                    binding.emailTextInputLayout.setError(getResources().getString(R.string.sign_in_email_failed));
                    binding.emailTextInputLayout.setErrorEnabled(true);
                    binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (String.valueOf(binding.passwordEditText.getText()).equals("")) {
                    binding.passwordEditText.requestFocus();
                    binding.passwordTextInputLayout.setError(getResources().getString(R.string.sign_in_password_failed));
                    binding.passwordTextInputLayout.setErrorEnabled(true);
                    binding.passwordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else {
                    /* 이곳에 API 호출 코드가 추가되어야 합니다. */
                    binding.signInFailedTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * (dialog_agree_our_policies) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_agree_our_policies() {
        WindowManager.LayoutParams params = agree_our_policies_dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        params.dimAmount = 0.35f;
        agree_our_policies_dialog.getWindow().setAttributes(params);
        agree_our_policies_dialog.show(); // 다이얼로그 띄우기

        RoundedImageView check_circle_roundedImageView = agree_our_policies_dialog.findViewById(R.id.check_circle_roundedImageView);
        RoundedImageView check_circle_roundedImageView2 = agree_our_policies_dialog.findViewById(R.id.check_circle_roundedImageView2);

        // Dialog 닫기 버튼
        agree_our_policies_dialog.findViewById(R.id.dialogClose_iBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agree_our_policies_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "첫 번째 항목 체크" 버튼 클릭 시
        agree_our_policies_dialog.findViewById(R.id.check_relativeLayout_1).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (check_policies_1_toggle)
                            check_circle_roundedImageView.setImageResource(R.drawable.check_circle_grey);
                        else
                            check_circle_roundedImageView.setImageResource(R.drawable.check_circle_green);
                        check_policies_1_toggle = !check_policies_1_toggle;
                    }
                });

        // "두 번째 항목 체크" 버튼 클릭 시
        agree_our_policies_dialog.findViewById(R.id.check_relativeLayout_2).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (check_policies_2_toggle)
                            check_circle_roundedImageView2.setImageResource(R.drawable.check_circle_grey);
                        else
                            check_circle_roundedImageView2.setImageResource(R.drawable.check_circle_green);
                        check_policies_2_toggle = !check_policies_2_toggle;
                    }
                });

        // "서비스 이용약관 동의 (필수)" 클릭 시
        agree_our_policies_dialog.findViewById(R.id.use_service_agreement_relativeLayout).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getApplicationContext(), UseServiceAgreementActivity.class);
                        startActivity(intent);
                    }
                });

        // "개인정보 처리 방침 (필수)" 클릭 시
        agree_our_policies_dialog.findViewById(R.id.handle_user_information_relativeLayout).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getApplicationContext(), HandleUserInformationActivity.class);
                        startActivity(intent);
                    }
                });

        // "개인정보 수집 및 이용 동의 (필수)" 클릭 시
        agree_our_policies_dialog.findViewById(R.id.store_user_information_relativeLayout).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getApplicationContext(), StoreUserInformationActivity.class);
                        startActivity(intent);
                    }
                });

        // "개인정보 제 3자 제공 동의 (필수)" 클릭 시
        agree_our_policies_dialog.findViewById(R.id.others_use_user_information_tv).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getApplicationContext(), OthersUseUserInformationActivity.class);
                        startActivity(intent);
                    }
                });

        // "확인" 버튼
        agree_our_policies_dialog.findViewById(R.id.yesBtn).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!check_policies_1_toggle) {
                            new ToastWarning(getResources().getString(R.string.toast_agree_required_failed), SignInActivity.this);
                            return;
                        }

                        agree_our_policies_dialog.dismiss(); // 다이얼로그 닫기
                        // 회원가입 단계(1) 액티비티로 이동
                        final Intent intent = new Intent(getApplicationContext(), SignUpStep1Activity.class);
                        startActivity(intent);
                    }
                });
    }
}