package com.project.sinabro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GetMyLocation {
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;
    private Context context;
    private Activity activity;

    public GetMyLocation(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public Location getMyLocation() {
        Location currentLocation = null;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("//////////// 사용자에게 권한을 요청해야 함");
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);
            int permission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.INTERNET);
            int permission2 = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int permission3 = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            // 권한이 열려있는지 확인
            if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
                // 마쉬멜로우 이상버전부터 권한을 물어본다
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 권한 체크(READ_PHONE_STATE)의 requestCode를 1000으로 세팅
                    activity.requestPermissions(
                            new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            1000);
                }
            }
//            getMyLocation(); //이건 써도되고 안써도 되지만, 전 권한 승인하면 즉시 위치값 받아오려고 썼습니다!
        } else {
            System.out.println("//////////// 권한 요청 안해도 됨");

//            // 수동으로 위치 구하기
//            String locationProvider = LocationManager.GPS_PROVIDER;
//            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
        }
        return currentLocation;
    }
}
