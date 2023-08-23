package com.project.sinabro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.project.sinabro.bottomSheet.place.AddBookmarkToListActivity;
import com.project.sinabro.bottomSheet.place.AddLocationInfoActivity;
import com.project.sinabro.bottomSheet.place.AddPlaceGuideActivity;
import com.project.sinabro.bottomSheet.place.PlaceListActivity;
import com.project.sinabro.bottomSheet.place.RemoveBookmarkFromListActivity;
import com.project.sinabro.models.CoordinateToAddress;
import com.project.sinabro.models.Headcount;
import com.project.sinabro.retrofit.KakaoAPI;
import com.project.sinabro.retrofit.headcountsAPI;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.RetrofitServiceForKakao;
import com.project.sinabro.retrofit.UserAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.sideBarMenu.settings.CheckPasswordActivity;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    /**
     * 위치 권한 요청 코드의 상숫값
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1981;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2981;

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * Callback for Location events.
     */

    private static MapView mapView;
    private static ViewGroup mapViewContainer;
    private MapPoint currPoint, prevPoint;
    private MapPOIItem marker;
    static private MapPOIItem selectedMarker;


    private ArrayList<MapPOIItem> markers = new ArrayList<>();
    private LocationSettingsRequest mLocationSettingsRequest;
    private Button currentLocation_btn, mapZoomIn_btn, mapZoomOut_btn, peopleScan_btn, editLocation_btn, placeList_btn;
    private TextView placeName_tv, detailAddress_tv, peopleCount_tv, headcountUnit_tv, updateElapsedTime_tv, username_tv;
    private static Button bookmarkEmpty_btn, bookmarkFilled_btn;

    private ImageButton hamburger_ibtn;

    private RelativeLayout layout_navigation_header;

    private DrawerLayout drawerlayout;

    private NavigationView navigationView;

    private BottomSheetBehavior bottomSheetBehavior;

    /**
     * 현재 MainActivity의 context, activity값을
     * 다른 클래스로 보내주기 위해 변수로도 저장함
     */
    private Context context = this;
    private Activity activity = this;

    /**
     * 위치 설정에 대한 객체 변수
     */
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;
    private static double selectedLatitude, selectedLongitude;
    private int addedMakerCnt;
    private String addedPlaceInfoState;
    static private String selectedPeopleCount, selectedPlaceName, selectedDetailAddress, selectedPlaceId, selectedMarkerId;

    //모델관련 변수 선언
    private Bitmap mBitmap = null;
    private Module mModule = null;
    private float mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY;

    private Dialog ask_add_or_cancel_bookmark_dialog;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    headcountsAPI peopleNumbersAPI;
    UserAPI userAPI;

    Boolean bookmarked = true, isDetected;

    //모델 에셋 경로 설정 함수
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = TokenManager.getInstance(this);
        retrofitService = new RetrofitService(tokenManager);
        peopleNumbersAPI = retrofitService.getRetrofit().create(headcountsAPI.class);
        userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        Call<List<Headcount>> call = peopleNumbersAPI.getPlaceInformations();
        call.enqueue(new Callback<List<Headcount>>() {
            @Override
            public void onResponse(Call<List<Headcount>> call, Response<List<Headcount>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Headcount> placeInformations = response.body();
                    if (!placeInformations.isEmpty()) {
                        for (int k = 0; k < placeInformations.size(); k++) {
                            int peopleNum = placeInformations.get(k).getHeadcount();
                            double latitude = Double.parseDouble(placeInformations.get(k).getPlaceId().getMarkerId().getLatitude());
                            double longitude = Double.parseDouble(placeInformations.get(k).getPlaceId().getMarkerId().getLongitude());
                            String placeName = placeInformations.get(k).getPlaceId().getPlaceName();
                            String detailAddress = placeInformations.get(k).getPlaceId().getDetailAddress();
                            long updateElapsedTime = placeInformations.get(k).getUpdateElapsedTime();
                            String placeId = String.valueOf(placeInformations.get(k).getPlaceId().getId());
                            String markerId = String.valueOf(placeInformations.get(k).getPlaceId().getMarkerId().getId());
                            Log.d("데이터: ", "" + peopleNum + "/" + latitude + "/" + longitude + "/" + placeName + "/" + detailAddress + "/" + updateElapsedTime);
                            addMakerToMap(peopleNum, latitude, longitude, placeName, detailAddress, updateElapsedTime, placeId, markerId);
                        }
                    } else {
                        Log.d("데이터", "The list of places is empty");
                    }
                } else {
                    Log.d("데이터", "Response was not successful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<Headcount>> call, Throwable t) {
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
            }
        });

        /** 다음 카카오맵 지도를 띄우는 코드 */
        if (mapView == null) {
            mapView = new MapView(this);
        }

        if (mapViewContainer != null) {
            mapViewContainer.removeView(mapView); // existing mapView is removed
            mapView = new MapView(this);
            MapPoint prevMapPoint = MapPoint.mapPointWithGeoCoord(selectedLatitude, selectedLongitude);
            mapView.setMapCenterPoint(prevMapPoint, false);
        }
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        /** 현재 나의 위치에 점을 갱신하며 찍어줌 */
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);

        /** 앱 초기 실행 시 위치 권한 동의 여부에 따라서
         * (권한 획득 요청) 및 (현재 위치 표시)를 수행 */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocation();
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            /** 사용자의 현재 위치 */
            GetMyLocation getMyLocation = new GetMyLocation(this, this);
            Location userLocation = getMyLocation.getMyLocation();
            if (userLocation != null) {
                double latitude = userLocation.getLatitude();
                double longitude = userLocation.getLongitude();
                System.out.println("////////////현재 내 위치값 : " + latitude + "," + longitude);
                currPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

                // 앱 초기 실행 시에만 현재 위치를 지도 중심점으로 위치시킴
                if (selectedLongitude == 0) {
                    /** 중심점 변경 */
                    mapView.setMapCenterPoint(currPoint, true);
                }

            }
        }

        /** 위치 설정 기능 수행 */
        init();

        /** 현재 위치 갱신 정의 및 이벤트 리스너 추가 */
        currentLocation_btn = (Button) findViewById(R.id.currentLocation_btn);
        currentLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();
            }
        });

        /** 지도 확대 버튼 클릭 시 기능 수행 */
        mapZoomIn_btn = (Button) findViewById(R.id.mapZoomIn_btn);
        mapZoomIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomIn(true);
            }
        });

        /** 지도 축소 버튼 클릭 시 기능 수행 */
        mapZoomOut_btn = (Button) findViewById(R.id.mapZoomOut_btn);
        mapZoomOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.zoomOut(true);
            }
        });

        /** 장소 리스트 버튼 클릭 시 기능 수행 */
        placeList_btn = findViewById(R.id.placeList_btn);
        placeList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), PlaceListActivity.class);
                intent.putExtra("markerId_value", selectedMarkerId);
                startActivity(intent);
            }
        });

        /** "즐겨찾기 추가/취소 확인" 다이얼로그 변수 초기화 및 설정 */
        ask_add_or_cancel_bookmark_dialog = new Dialog(MainActivity.this);  // Dialog 초기화
        ask_add_or_cancel_bookmark_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        ask_add_or_cancel_bookmark_dialog.setContentView(R.layout.dialog_ask_add_or_cancel_bookmark); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        ask_add_or_cancel_bookmark_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** ========================= Sidebar Navigation Drawer(사이드 메뉴바) ========================= */
        drawerlayout = findViewById(R.id.drawer_layout);
        /** 햄버거 버튼 클릭 시 sidebar navigation을 나타나도록 하는 코드 */
        hamburger_ibtn = (ImageButton) findViewById(R.id.hamburger_ibtn);
        hamburger_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call_userAPI_getUserSelfInfo = userAPI.getUserSelfInfo();
                call_userAPI_getUserSelfInfo.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                tokenManager.saveUserInfo(jsonObject);
                                String username = tokenManager.getUsername();
                                username_tv.setText(username);


                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            switch (response.code()) {
                                case 401:
                                    username_tv.setText("로그인해주세요.");
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), MainActivity.this);
                            }
                        }

                        // sidebar navigation을 보여줌
                        drawerlayout.openDrawer(GravityCompat.START);
                        // bottom sheet layout을 사라지게 함
                        bottomSheetBehavior.setPeekHeight(0);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                    }
                });
            }
        });

        /** Sidebar Navigation Header(헤더) */
        navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        username_tv = headerView.findViewById(R.id.username_tv);

        layout_navigation_header = headerView.findViewById(R.id.layout_navigation_header);
        layout_navigation_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call_userAPI_getUserSelfInfo = userAPI.getUserSelfInfo();
                call_userAPI_getUserSelfInfo.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                tokenManager.saveUserInfo(jsonObject);
                                Intent intent = new Intent(getApplicationContext(), CheckPasswordActivity.class);
                                intent.putExtra("departActivityName", "MainActivity");
                                intent.putExtra("destActivityName", "MyPageActivity");
                                startActivity(intent);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            switch (response.code()) {
                                case 401:
                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), MainActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                    }
                });
            }
        });

        /** sidebar navigation 내의 menu item(메뉴) 클릭 시 해당 액티비티로
         *  넘어갈 수 있도록 하는 코드 */
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);


        /** ========================= bottom sheet 레이아웃 ========================= */
//        bottomSheet_layout = (FrameLayout) findViewById(R.id.bottomSheet_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(

                findViewById(R.id.bottomSheet_layout));
//        bottomSheetBehavior.setPeekHeight(200);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//            }
//        });
        placeName_tv =

                findViewById(R.id.placeName_tv);

        detailAddress_tv =

                findViewById(R.id.detailAddress_tv);

        peopleCount_tv =

                findViewById(R.id.peopleCount_tv);

        updateElapsedTime_tv =

                findViewById(R.id.updateElapsedTime_tv);

//        mResultView =
//
//                findViewById(R.id.resultView);
//        mResultView.setVisibility(View.INVISIBLE);
        /** 카메라 촬영 버튼 */
        peopleScan_btn =

                findViewById(R.id.peopleScan_btn);
        peopleScan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitude = Double.parseDouble("" + selectedLatitude);
                Double longitude = Double.parseDouble("" + selectedLongitude);

                // 위도와 경도가 한국 범위를 벗어났을 때
                if (latitude <= 33.0 || latitude >= 38.0 || longitude <= 124.0 || longitude >= 132.0) {
                    new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                    return;
                }

                RetrofitServiceForKakao retrofitServiceForKakao = new RetrofitServiceForKakao();
                KakaoAPI kakaoInterface = retrofitServiceForKakao.getRetrofit().create(KakaoAPI.class);
                Call<CoordinateToAddress> call = kakaoInterface.getAddress("WGS84", longitude, latitude);
                call.enqueue(new Callback<CoordinateToAddress>() {
                    @Override
                    public void onResponse(Call<CoordinateToAddress> call, Response<CoordinateToAddress> response) {
                        if (response.isSuccessful()) {
                            CoordinateToAddress responseData = response.body();
                            List<CoordinateToAddress.Document> documents = responseData.getDocuments();
                            if (!documents.isEmpty()) {
                                CoordinateToAddress.Document document = documents.get(0);
                                CoordinateToAddress.RoadAddress roadAddress = document.getRoadAddress();

                                Call<ResponseBody> call_userAPI_getUserSelfInfo = userAPI.getUserSelfInfo();
                                call_userAPI_getUserSelfInfo.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body().string());
                                                tokenManager.saveUserInfo(jsonObject);

                                                if (addedPlaceInfoState.equals("0")) {
                                                    final Intent intent = new Intent(MainActivity.this, AddPlaceGuideActivity.class);
                                                    startActivity(intent);
                                                    return;
                                                }

                                                // 이곳에 카메라 촬영으로 이어지는 코드가 추가하면 됩니다.
                                                finish();
                                                final Intent intent = new Intent(MainActivity.this, ObjectDetectionActivity.class);
                                                startActivity(intent);
                                            } catch (JSONException | IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            switch (response.code()) {
                                                case 401:
                                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                    startActivity(intent);
                                                    break;
                                                default:
                                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), MainActivity.this);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                                    }
                                });
                            } else {
                                new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CoordinateToAddress> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                    }
                });
            }
        });

        /** 장소 등록/수정 버튼 */
        editLocation_btn =

                findViewById(R.id.editLocation_btn);
        editLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitude = Double.parseDouble("" + selectedLatitude);
                Double longitude = Double.parseDouble("" + selectedLongitude);

                // 위도와 경도가 한국 범위를 벗어났을 때
                if (latitude <= 33.0 || latitude >= 38.0 || longitude <= 124.0 || longitude >= 132.0) {
                    new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                    return;
                }

                // 이곳에 장소 등록 액티비티로 이어지는 코드를 추가하면 됩니다.
                final Intent intent = new Intent(MainActivity.this, AddLocationInfoActivity.class);
                if (Integer.parseInt(addedPlaceInfoState) == 0) {
                    intent.putExtra("forModify", false);
                } else
                    intent.putExtra("forModify", true);

                RetrofitServiceForKakao retrofitServiceForKakao = new RetrofitServiceForKakao();
                KakaoAPI kakaoInterface = retrofitServiceForKakao.getRetrofit().create(KakaoAPI.class);
                Call<CoordinateToAddress> call = kakaoInterface.getAddress("WGS84", longitude, latitude);
                call.enqueue(new Callback<CoordinateToAddress>() {
                    @Override
                    public void onResponse(Call<CoordinateToAddress> call, Response<CoordinateToAddress> response) {
                        if (response.isSuccessful()) {
                            CoordinateToAddress responseData = response.body();
                            List<CoordinateToAddress.Document> documents = responseData.getDocuments();
                            if (!documents.isEmpty()) {
                                CoordinateToAddress.Document document = documents.get(0);
                                CoordinateToAddress.RoadAddress roadAddress = document.getRoadAddress();
                                if (roadAddress != null) {
                                    intent.putExtra("address_value", roadAddress.getAddressName());
                                } else {
                                    // If 'roadAddress' is null, try to get 'address' and its 'address_name'
                                    CoordinateToAddress.Address address = document.getAddress();
                                    if (address != null) {
                                        intent.putExtra("address_value", address.getAddressName());
                                    } else {
                                        Toast.makeText(MainActivity.this, "No address found..", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                intent.putExtra("markerId_value", selectedMarkerId);
                                intent.putExtra("latitude_value", latitude);
                                intent.putExtra("longitude_value", longitude);
                                intent.putExtra("placeName_value", selectedPlaceName);
                                intent.putExtra("detailAddress_value", selectedDetailAddress);
                                intent.putExtra("placeId_value", selectedPlaceId);
                                intent.putExtra("markerId_value", selectedMarkerId);

                                Call<ResponseBody> call_userAPI_getUserSelfInfo = userAPI.getUserSelfInfo();
                                call_userAPI_getUserSelfInfo.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body().string());
                                                tokenManager.saveUserInfo(jsonObject);
                                                startActivity(intent);
                                            } catch (JSONException | IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            switch (response.code()) {
                                                case 401:
                                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                    startActivity(intent);
                                                    break;
                                                default:
                                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), MainActivity.this);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                                    }
                                });
                            } else {
                                new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CoordinateToAddress> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                    }
                });
            }
        });

        /** "북마크 등록" 버튼 */
        bookmarkEmpty_btn =

                findViewById(R.id.bookmarkEmpty_btn);
        bookmarkEmpty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitude = Double.parseDouble("" + selectedLatitude);
                Double longitude = Double.parseDouble("" + selectedLongitude);

                // 위도와 경도가 한국 범위를 벗어났을 때
                if (latitude <= 33.0 || latitude >= 38.0 || longitude <= 124.0 || longitude >= 132.0) {
                    new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                    return;
                }

                RetrofitServiceForKakao retrofitServiceForKakao = new RetrofitServiceForKakao();
                KakaoAPI kakaoInterface = retrofitServiceForKakao.getRetrofit().create(KakaoAPI.class);
                Call<CoordinateToAddress> call = kakaoInterface.getAddress("WGS84", longitude, latitude);
                call.enqueue(new Callback<CoordinateToAddress>() {
                    @Override
                    public void onResponse(Call<CoordinateToAddress> call, Response<CoordinateToAddress> response) {
                        if (response.isSuccessful()) {
                            CoordinateToAddress responseData = response.body();
                            List<CoordinateToAddress.Document> documents = responseData.getDocuments();
                            if (!documents.isEmpty()) {
                                CoordinateToAddress.Document document = documents.get(0);
                                CoordinateToAddress.RoadAddress roadAddress = document.getRoadAddress();

                                Call<ResponseBody> call_userAPI_getUserSelfInfo = userAPI.getUserSelfInfo();
                                call_userAPI_getUserSelfInfo.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body().string());
                                                tokenManager.saveUserInfo(jsonObject);

                                                showDialog_ask_add_or_delete_bookmark();
                                            } catch (JSONException | IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            switch (response.code()) {
                                                case 401:
                                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                    startActivity(intent);
                                                    break;
                                                default:
                                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), MainActivity.this);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                                    }
                                });
                            } else {
                                new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CoordinateToAddress> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                    }
                });
            }
        });

        /** "북마크 제거" 버튼 */
        bookmarkFilled_btn =

                findViewById(R.id.bookmarkFilled_btn);
        bookmarkFilled_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitude = Double.parseDouble("" + selectedLatitude);
                Double longitude = Double.parseDouble("" + selectedLongitude);

                // 위도와 경도가 한국 범위를 벗어났을 때
                if (latitude <= 33.0 || latitude >= 38.0 || longitude <= 124.0 || longitude >= 132.0) {
                    new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                    return;
                }

                RetrofitServiceForKakao retrofitServiceForKakao = new RetrofitServiceForKakao();
                KakaoAPI kakaoInterface = retrofitServiceForKakao.getRetrofit().create(KakaoAPI.class);
                Call<CoordinateToAddress> call = kakaoInterface.getAddress("WGS84", longitude, latitude);
                call.enqueue(new Callback<CoordinateToAddress>() {
                    @Override
                    public void onResponse(Call<CoordinateToAddress> call, Response<CoordinateToAddress> response) {
                        if (response.isSuccessful()) {
                            CoordinateToAddress responseData = response.body();
                            List<CoordinateToAddress.Document> documents = responseData.getDocuments();
                            if (!documents.isEmpty()) {
                                CoordinateToAddress.Document document = documents.get(0);
                                CoordinateToAddress.RoadAddress roadAddress = document.getRoadAddress();

                                Call<ResponseBody> call_userAPI_getUserSelfInfo = userAPI.getUserSelfInfo();
                                call_userAPI_getUserSelfInfo.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body().string());
                                                tokenManager.saveUserInfo(jsonObject);

                                                showDialog_ask_add_or_delete_bookmark();
                                            } catch (JSONException | IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            switch (response.code()) {
                                                case 401:
                                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                    startActivity(intent);
                                                    break;
                                                default:
                                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), MainActivity.this);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                                    }
                                });
                            } else {
                                new ToastWarning(getResources().getString(R.string.toast_cannot_access_area), MainActivity.this);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CoordinateToAddress> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), MainActivity.this);
                    }
                });
            }
        });
    }

    public void updateBookmarkBtnState(Boolean bookmarked) {
        if (bookmarked) {
            bookmarkEmpty_btn.setVisibility(View.GONE);
            bookmarkFilled_btn.setVisibility(View.VISIBLE);
        } else {
            bookmarkEmpty_btn.setVisibility(View.VISIBLE);
            bookmarkFilled_btn.setVisibility(View.GONE);
        }
    }

    /**
     * (dialog_ask_add_or_cancel_bookmark) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_add_or_delete_bookmark() {
        ask_add_or_cancel_bookmark_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        ask_add_or_cancel_bookmark_dialog.getWindow().setDimAmount(0.35f);

        Button bookmarkFilled_btn = findViewById(R.id.bookmarkFilled_btn);
        TextView dialog_tv = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.dialog_tv);
        if (bookmarkFilled_btn.getVisibility() == View.VISIBLE) {
            dialog_tv.setText(getResources().getString(R.string.dialog_cancel_bookmark));
            bookmarked = true;
        } else {
            dialog_tv.setText(getResources().getString(R.string.dialog_add_bookmark));
            bookmarked = false;
        }

        // "아니오" 버튼
        Button noBtn = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_add_or_cancel_bookmark_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "확인" 버튼
        Button yesBtn = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_add_or_cancel_bookmark_dialog.dismiss(); // 다이얼로그 닫기
                if (bookmarked) {
                    final Intent intent = new Intent(getApplicationContext(), RemoveBookmarkFromListActivity.class);
                    intent.putExtra("fromMainActivity", true);
                    startActivity(intent);
                } else {
                    final Intent intent = new Intent(getApplicationContext(), AddBookmarkToListActivity.class);
                    intent.putExtra("fromMainActivity", true);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("\n\nre" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        mBitmap = (Bitmap) data.getExtras().get("data");
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90.0f);
                        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
                        //mImageView.setImageBitmap(mBitmap);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                mBitmap = BitmapFactory.decodeFile(picturePath);
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90.0f);
                                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
                                //mImageView.setImageBitmap(mBitmap);
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void init() {
        if (mFusedLocationClient == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }

        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20 * 1000);
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocation() {
        if (isPermissionGranted()) startLocationUpdates();
        else requestPermissions();
    }

    private boolean isPermissionGranted() {
        for (String permission : PERMISSIONS) {
            if (permission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                continue;
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (PackageManager.PERMISSION_GRANTED != result)
                return false;
        }
        return true;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                /** 사용자의 현재 위치 */
                GetMyLocation getMyLocation = new GetMyLocation(context, activity);
                Location userLocation = getMyLocation.getMyLocation();
                if (userLocation != null) {
                    double currLatitude = userLocation.getLatitude();
                    double currLongitude = userLocation.getLongitude();
                    System.out.println("////////////현재 내 위치값 : " + currLatitude + "," + currLongitude);
                    currPoint = MapPoint.mapPointWithGeoCoord(currLatitude, currLongitude);

                    /** 중심점 변경 */
                    mapView.setMapCenterPoint(currPoint, true);
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    resolveLocationSettings(e);
                }
            }
        });
    }

    public void resolveLocationSettings(Exception exception) {
        ResolvableApiException resolvable = (ResolvableApiException) exception;
        try {
            resolvable.startResolutionForResult(this, REQUEST_CODE_LOCATION_SETTINGS);
        } catch (IntentSender.SendIntentException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                startLocationUpdates();
            } else {
                // Permission denied.
                for (String permission : permissions) {
                    if ("android.permission.ACCESS_FINE_LOCATION".equals(permission)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                        builder.setTitle("알림");
                        builder.setMessage("지도 사용을 위해 위치 권한을 허용해 주세요. (필수권한)");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                /** 위치 정보 설정창에서 '설정으로 이동' 클릭 시 */
                                Intent intent = new Intent();
                                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                /** 위치 정보 설정창에서 '취소' 클릭 시 */
//                                Toast.makeText(MainActivity.this, "Cancel Click", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0, 133, 254));
                                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(123, 123, 123));
                            }
                        });
                        alertDialog.show();
                    }
                }
            }
        }
    }

    /**
     * 마커 등록
     */
    public void addMakerToMap(int peopleNum, double latitude, double longitude, String
            placeName, String detailAddress, long updateElapsedTime, String placeId, String markerId) {
        marker = new MapPOIItem();
        if (peopleNum == -1)
            marker.setItemName("정보 없음");
        else
            marker.setItemName(peopleNum + "명");
        marker.setTag(markers.size());
        // 원하는 위치에 마커 추가
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude); // 마커의 위도, 경도 설정
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        String markerInfo = peopleNum + "/" + placeName + "/" + detailAddress + "/" + updateElapsedTime + "/" + placeId + "/" + markerId + "/" + 1;
        marker.setUserObject(markerInfo);
        mapView.addPOIItem(marker);
        markers.add(marker);
        addedMakerCnt++;
    }

    /**
     * 카카오맵 이벤트 리스너
     */
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    /**
     * 지도 한 번 클릭 시
     */
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        marker = new MapPOIItem();
        if (addedMakerCnt < markers.size()) {
            mapView.removePOIItem(markers.get(markers.size() - 1));
            markers.remove(markers.size() - 1);
        }

        marker.setItemName("새로운 장소");
        marker.setTag(markers.size());
        marker.setMapPoint(mapPoint);
        selectedLatitude = mapPoint.getMapPointGeoCoord().latitude;
        selectedLongitude = mapPoint.getMapPointGeoCoord().longitude;
        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        peopleCount_tv.setText("?");
        headcountUnit_tv.setVisibility(View.GONE);
        updateElapsedTime_tv.setText("정보 없음");
        placeName_tv.setText("장소명(새로운 장소)");
        detailAddress_tv.setText("상세 주소");
        addedPlaceInfoState = "0";

        String markerInfo = "-1" + "/" + "새로운 장소" + "/" + "상세 주소" + "/" + "-1/null/null/0";
        marker.setUserObject(markerInfo);
        mapView.addPOIItem(marker);
        markers.add(marker);

        bottomSheetBehavior.setPeekHeight(85);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * 지도 두 번 클릭 시
     */
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        mapView.zoomIn(true);
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        Object userObject = mapPOIItem.getUserObject();
        String[] splittedStrings = userObject.toString().split("/");
        String peopleCount_str = Integer.parseInt(splittedStrings[0]) == -1 ? "?" : "" + splittedStrings[0];
        peopleCount_tv.setText(peopleCount_str);
        if (peopleCount_str.equals("?")) headcountUnit_tv.setVisibility(View.GONE);
        else headcountUnit_tv.setVisibility(View.VISIBLE);
        placeName_tv.setText(splittedStrings[1]);
        detailAddress_tv.setText(splittedStrings[2]);
        long updateElapsedTime = Integer.parseInt(splittedStrings[3]);
        String updateElapsedTime_str;
        if (updateElapsedTime == -1) {
            updateElapsedTime_str = "정보 없음";
        } else if (updateElapsedTime < 60) {
            updateElapsedTime_str = "약 " + updateElapsedTime + "초 전";
        } else if (updateElapsedTime < 3600) {
            updateElapsedTime_str = updateElapsedTime / 60 + "분 전";
        } else if (updateElapsedTime < 86400) {
            updateElapsedTime_str = updateElapsedTime / 3600 + "시간 전";
        } else if (updateElapsedTime < 86400 * 365) {
            updateElapsedTime_str = updateElapsedTime / 86400 + "일 전";
        } else {
            updateElapsedTime_str = updateElapsedTime / 86400 * 365 + "일 전";
        }
        updateElapsedTime_tv.setText(updateElapsedTime_str);
        addedPlaceInfoState = splittedStrings[6];

        selectedPeopleCount = splittedStrings[0];
        selectedPlaceName = splittedStrings[1];
        selectedDetailAddress = splittedStrings[2];
        selectedPlaceId = splittedStrings[4];
        selectedMarkerId = splittedStrings[5];

        selectedLatitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        selectedLongitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;

        selectedMarker = mapPOIItem;

        bottomSheetBehavior.setPeekHeight(85);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem
            mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint
            mapPoint) {
    }
}