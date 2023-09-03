package com.project.sinabro.bottomSheet.headcount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.bottomSheet.place.RemoveBookmarkFromListActivity;
import com.project.sinabro.databinding.ActivityAddHeadcountBinding;
import com.project.sinabro.databinding.ActivityRemoveBookmarkListBinding;
import com.project.sinabro.models.Headcount;
import com.project.sinabro.models.Place;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.BookmarksAPI;
import com.project.sinabro.retrofit.interfaceAPIs.HeadcountsAPI;
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

    private int serverValue;
    private String placeId;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private HeadcountsAPI headcountsAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddHeadcountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(getApplicationContext());
        retrofitService = new RetrofitService(tokenManager);
        headcountsAPI = retrofitService.getRetrofit().create(HeadcountsAPI.class);

        intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        serverValue = intent.getIntExtra("serverValue", 0);

        binding.headcountEditText.setText(String.valueOf(serverValue));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
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
                Headcount headcount = new Headcount();
                headcount.setHeadcount(serverValue);

                Call<Place> call_headcountsAPI_addPeopleNumber = headcountsAPI.addPeopleNumber(placeId, headcount);
                call_headcountsAPI_addPeopleNumber.enqueue(new Callback<Place>() {
                    @Override
                    public void onResponse(Call<Place> call, Response<Place> response) {
                        if (response.isSuccessful()) {
                            new ToastSuccess(getResources().getString(R.string.toast_success_add_headcount), AddHeadcount.this);
                            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
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
}