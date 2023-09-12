package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.bottomSheet.place.AddBookmarkToListActivity;
import com.project.sinabro.bottomSheet.place.PlaceListActivity;
import com.project.sinabro.databinding.ActivityWithdrawalStep2Binding;
import com.project.sinabro.models.UserWithdrawalReason;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.AuthAPI;
import com.project.sinabro.retrofit.interfaceAPIs.BookmarksAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawalStep2Activity extends AppCompatActivity {

    private ActivityWithdrawalStep2Binding binding;

    private Dialog askAcceptWithdrawal_dialog;

    private String withdrawalReason;

    private Intent intent;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private AuthAPI authAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawalStep2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(getApplicationContext());
        retrofitService = new RetrofitService(tokenManager);
        authAPI = retrofitService.getRetrofit().create(AuthAPI.class);

        intent = getIntent();
        withdrawalReason = intent.getStringExtra("withdrawalReason");

        /** "회원 탈퇴 질문 안내" 다이얼로그 변수 초기화 및 설정 */
        askAcceptWithdrawal_dialog = new Dialog(WithdrawalStep2Activity.this);  // Dialog 초기화
        askAcceptWithdrawal_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        askAcceptWithdrawal_dialog.setContentView(R.layout.dialog_ask_accept_withdrawal); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        askAcceptWithdrawal_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        binding.checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_accept_withdrawal();
            }
        });
    }

    /**
     * (dialog_ask_accept_withdrawal) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_accept_withdrawal() {
        askAcceptWithdrawal_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        askAcceptWithdrawal_dialog.getWindow().setDimAmount(0.35f);

        // "아니오" 버튼
        Button noBtn = askAcceptWithdrawal_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askAcceptWithdrawal_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "확인" 버튼
        askAcceptWithdrawal_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserWithdrawalReason userWithdrawalReason = new UserWithdrawalReason();
                userWithdrawalReason.setWithdrawalReason(withdrawalReason);
                String feedback = binding.feedbackEdt.getText().toString();
                if (feedback.equals(""))
                    feedback = "없음";
                userWithdrawalReason.setFeedback(feedback);

                Call<ResponseBody> call_authAPI_deleteAccount = authAPI.deleteAccount(userWithdrawalReason);
                call_authAPI_deleteAccount.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            tokenManager.logout();
                            askAcceptWithdrawal_dialog.dismiss(); // 다이얼로그 닫기
                            new ToastSuccess(getResources().getString(R.string.toast_withdrawal_success), WithdrawalStep2Activity.this);
                            // 메인 액티비티로 이동
                            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            switch (response.code()) {
                                case 401:
                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), WithdrawalStep2Activity.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), WithdrawalStep2Activity.this);
                    }
                });
            }
        });
    }
}