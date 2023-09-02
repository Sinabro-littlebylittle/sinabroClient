package com.project.sinabro.bottomSheet.place;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.models.Place;
import com.project.sinabro.retrofit.interfaceAPIs.PlacesAPI;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationInfoActivity extends AppCompatActivity {

    private Button addPlace_btn;
    private ImageButton back_iBtn;
    private TextView placeRemove_tv, address_tv;
    private TextInputEditText placeName_editText, detailAddress_editText;
    private Dialog placeRemove_dialog;

    Boolean forModify;
    String markerId, placeId;

    private PlacesAPI placesAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_info);

        TokenManager tokenManager = TokenManager.getInstance(this);
        RetrofitService retrofitService = new RetrofitService(tokenManager);
        placesAPI = retrofitService.getRetrofit().create(PlacesAPI.class);

        /** 뒤로가기 버튼 기능 */
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        final Intent intent = getIntent();
        forModify = intent.getBooleanExtra("forModify", false);
        markerId = intent.getStringExtra("markerId_value");
        placeId = intent.getStringExtra("placeId_value");

        placeRemove_dialog = new Dialog(AddLocationInfoActivity.this);  // Dialog 초기화
        placeRemove_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        placeRemove_dialog.setContentView(R.layout.dialog_place_remove); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        placeRemove_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        placeRemove_tv = findViewById(R.id.placeRemove_tv);
        if (forModify) placeRemove_tv.setVisibility(View.VISIBLE);
        placeRemove_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_place_remove();
            }
        });

        address_tv = findViewById(R.id.address_tv);
        String address = intent.getStringExtra("address_value");
        String latitude = "" + intent.getDoubleExtra("latitude_value", 0);
        String longitude = "" + intent.getDoubleExtra("longitude_value", 0);
        address_tv.setText(address);

        placeName_editText = findViewById(R.id.placeName_editText);
        detailAddress_editText = findViewById(R.id.detailAddress_editText);
        addPlace_btn = findViewById(R.id.addPlace_btn);
        addPlace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placeName_editText.getText().toString().equals("") || detailAddress_editText.getText().toString().equals("")) {
                    new ToastWarning(getResources().getString(R.string.toast_add_place_failed), AddLocationInfoActivity.this);
                    return;
                }

                final Intent intent = new Intent(getApplicationContext(), PlaceListActivity.class);
                Place place = new Place();
                place.setPlaceName(placeName_editText.getText().toString());
                place.setDetailAddress(detailAddress_editText.getText().toString());


                if (forModify) {
                    /** PATCH 기능 추가 예정 */
                    placesAPI.updatePlace(placeId, place).enqueue(new Callback<Place>() {
                        @Override
                        public void onResponse(Call<Place> call, Response<Place> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Place placeInformation = response.body();
                                intent.putExtra("markerId_value", markerId);
                                new ToastSuccess(getResources().getString(R.string.toast_modify_list_success), AddLocationInfoActivity.this);
                                startActivity(intent);
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddLocationInfoActivity.this);
                                        // "로그인" 액티비티로 이동
                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        finish(); // 현재 액티비티 종료
                                        break;
                                    default:
                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddLocationInfoActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Place> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), AddLocationInfoActivity.this);
                        }
                    });
                } else {
                    /** POST 기능 */
                    place.setAddress(address_tv.getText().toString());
                    place.setLatitude(latitude);
                    place.setLongitude(longitude);
                    placesAPI.addPlaceInformation(place).enqueue(new Callback<Place>() {
                        @Override
                        public void onResponse(Call<Place> call, Response<Place> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Place placeInformation = response.body();
                                intent.putExtra("markerId_value", placeInformation.getMarkerId());
                                new ToastSuccess(getResources().getString(R.string.toast_add_place_success), AddLocationInfoActivity.this);
                                startActivity(intent);
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddLocationInfoActivity.this);
                                        // "로그인" 액티비티로 이동
                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        finish(); // 현재 액티비티 종료
                                        break;
                                    default:
                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddLocationInfoActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Place> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), AddLocationInfoActivity.this);
                        }
                    });
                }
            }
        });

        if (forModify) {
            String placeName_value = intent.getStringExtra("placeName_value");
            String detailAddress_value = intent.getStringExtra("detailAddress_value");

            addPlace_btn.setText("수정하기");
            placeName_editText.setText(placeName_value);
            detailAddress_editText.setText(detailAddress_value);
        }

    }

    // (dialog_place_remove) 다이얼로그를 디자인하는 함수
    public void showDialog_place_remove() {
        placeRemove_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        placeRemove_dialog.getWindow().setDimAmount(0.35f);

        // "아니오" 버튼
        Button noBtn = placeRemove_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeRemove_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "네" 버튼
        placeRemove_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeRemove_dialog.dismiss(); // 다이얼로그 닫기
                placesAPI.deletePlace(placeId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            new ToastSuccess(getResources().getString(R.string.toast_remove_place_info_success), AddLocationInfoActivity.this);
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                int remainingPlacesCnt = jsonObject.optInt("remainingPlacesCnt");
                                if (remainingPlacesCnt == 0) {
                                    final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish(); // 현재 액티비티 종료
                                    return;
                                }
                                final Intent intent = new Intent(getApplicationContext(), PlaceListActivity.class);
                                intent.putExtra("markerId_value", markerId);
                                startActivity(intent);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            switch (response.code()) {
                                case 401:
                                    new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddLocationInfoActivity.this);
                                    // "로그인" 액티비티로 이동
                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent);
                                    finish(); // 현재 액티비티 종료
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddLocationInfoActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), AddLocationInfoActivity.this);
                    }
                });
            }
        });
    }
}