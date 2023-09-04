package com.project.sinabro.bottomSheet.headcount;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityAddHeadcountBinding;
import com.project.sinabro.models.Headcount;
import com.project.sinabro.models.Place;
import com.project.sinabro.models.UserInfo;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.HeadcountsAPI;
import com.project.sinabro.retrofit.interfaceAPIs.UserAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddHeadcount extends AppCompatActivity {

    private ActivityAddHeadcountBinding binding;

    private Intent intent;

    public int serverValue;
    private String placeId;

    private Dialog addHeadcountSuccess_dialog;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private HeadcountsAPI headcountsAPI;
    private UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddHeadcountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(getApplicationContext());
        retrofitService = new RetrofitService(tokenManager);
        headcountsAPI = retrofitService.getRetrofit().create(HeadcountsAPI.class);
        userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        serverValue = intent.getIntExtra("serverValue", 0);

        binding.headcountEditText.setText(String.valueOf(serverValue));

        /** "인원수 정보 등록 완료 안내" 다이얼로그 변수 초기화 및 설정 */
        addHeadcountSuccess_dialog = new Dialog(AddHeadcount.this);  // Dialog 초기화
        addHeadcountSuccess_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        addHeadcountSuccess_dialog.setContentView(R.layout.dialog_add_headcount_success); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        addHeadcountSuccess_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
            }
        });

        /** 인원수 내용 지우기 X 버튼 클릭 시  */
        binding.headcountTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverValue = 0;
                binding.headcountEditText.setText("");
            }
        });

        /** PLUS(+) 버튼 기능 */
        binding.increaseHeadcountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.headcountEditText.setText(String.valueOf(++serverValue));
            }
        });

        /** MINUS(-) 버튼 기능 */
        binding.decreaseHeadcountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serverValue == 0) return;
                binding.headcountEditText.setText(String.valueOf(--serverValue));
            }
        });

        binding.addHeadcountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (String.valueOf(binding.headcountEditText.getText()).equals("")) {
                    binding.headcountEditText.requestFocus();
                    new ToastWarning(getResources().getString(R.string.toast_headcount_editText_valid_failed), AddHeadcount.this);
                    return;
                }

                serverValue = Integer.parseInt(binding.headcountEditText.getText().toString());
                Log.d("최종 값: ", "" + serverValue);

                Headcount headcount = new Headcount();
                headcount.setHeadcount(serverValue);

                Call<Place> call_headcountsAPI_addPeopleNumber = headcountsAPI.addPeopleNumber(placeId, headcount);
                call_headcountsAPI_addPeopleNumber.enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        if (response.isSuccessful()) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.setPoint(100);

                            Call<ResponseBody> call_headcountsAPI_changePoint = userAPI.changePoint(userInfo);
                            call_headcountsAPI_changePoint.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        showDialog_add_headcount_success();
                                    } else {
                                        switch (response.code()) {
                                            case 401:
                                                new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddHeadcount.this);
                                                final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                startActivity(intent);
                                                break;
                                            default:
                                                new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddHeadcount.this);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                                    new ToastWarning(getResources().getString(R.string.toast_server_error), AddHeadcount.this);
                                }
                            });

                            showDialog_add_headcount_success();
                        } else {
                            switch (response.code()) {
                                case 401:
                                    new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddHeadcount.this);
                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddHeadcount.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Place> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), AddHeadcount.this);
                    }
                });
            }
        });
    }

    /**
     * (dialog_add_headcount_success) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_add_headcount_success() {
        addHeadcountSuccess_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        addHeadcountSuccess_dialog.getWindow().setDimAmount(0.35f);

        // "확인" 버튼 클릭 시 이벤트 처리 코드
        addHeadcountSuccess_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHeadcountSuccess_dialog.dismiss(); // 다이얼로그 닫기
                new ToastSuccess(getResources().getString(R.string.toast_success_add_headcount), AddHeadcount.this);
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void displayHeadcountEditTextValidFailedToastMessage() {
        new ToastWarning(getResources().getString(R.string.toast_headcount_editText_valid_failed), AddHeadcount.this);
    }
}