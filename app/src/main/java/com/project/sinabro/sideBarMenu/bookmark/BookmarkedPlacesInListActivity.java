package com.project.sinabro.sideBarMenu.bookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.sinabro.R;
import com.project.sinabro.bottomSheet.place.AddLocationInfoActivity;
import com.project.sinabro.bottomSheet.place.PlaceItem;
import com.project.sinabro.databinding.ActivityBookmarkedPlacesInListBinding;
import com.project.sinabro.models.Headcount;
import com.project.sinabro.models.requests.PlaceIdRequest;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.BookmarksAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkedPlacesInListActivity extends AppCompatActivity {

    private ActivityBookmarkedPlacesInListBinding binding;

    private ListViewAdapter adapter;

    private Dialog placeInfo_dialog, ask_add_or_cancel_bookmark_dialog;

    private PlaceItem clickedPlaceItem;

    private static Button bookmarkEmpty_btn, bookmarkFilled_btn;

    private static String bookmarkId;

    ArrayList<Boolean> bookmarkedList = new ArrayList<>();

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    static BookmarksAPI bookmarksAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookmarkedPlacesInListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(getApplicationContext());
        retrofitService = new RetrofitService(tokenManager);
        bookmarksAPI = retrofitService.getRetrofit().create(BookmarksAPI.class);

        final Intent intent = getIntent();
        binding.listNameTv.setText(intent.getStringExtra("listName"));
        bookmarkId = intent.getStringExtra("bookmarkId");

        /** "장소정보 확인" 다이얼로그 변수 초기화 및 설정 */
        placeInfo_dialog = new Dialog(BookmarkedPlacesInListActivity.this);  // Dialog 초기화
        placeInfo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        placeInfo_dialog.setContentView(R.layout.dialog_place_info); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        placeInfo_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** "즐겨찾기 추가/취소 확인" 다이얼로그 변수 초기화 및 설정 */
        ask_add_or_cancel_bookmark_dialog = new Dialog(BookmarkedPlacesInListActivity.this);  // Dialog 초기화
        ask_add_or_cancel_bookmark_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        ask_add_or_cancel_bookmark_dialog.setContentView(R.layout.dialog_ask_add_or_cancel_bookmark); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        ask_add_or_cancel_bookmark_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        adapter = new ListViewAdapter();

        Call<List<Headcount>> call_bookmarksAPI_getBookmarkedPlaceInformations = bookmarksAPI.getBookmarkedPlaceInformations(bookmarkId);
        call_bookmarksAPI_getBookmarkedPlaceInformations.enqueue(new Callback<List<Headcount>>() {
            @Override
            public void onResponse(Call<List<Headcount>> call, Response<List<Headcount>> response) {
                if (response.isSuccessful()) {
                    List<Headcount> placeInformations = response.body();
                    if (!placeInformations.isEmpty()) {
                        bookmarkedList.add(true);
                        for (int k = 0; k < placeInformations.size(); k++) {
                            int peopleNum = placeInformations.get(k).getHeadcount();
                            String placeName = placeInformations.get(k).getPlaceId().getPlaceName();
                            String address = placeInformations.get(k).getPlaceId().getAddress();
                            String detailAddress = placeInformations.get(k).getPlaceId().getDetailAddress();
                            long updateElapsedTime = placeInformations.get(k).getUpdateElapsedTime();
                            String placeId = String.valueOf(placeInformations.get(k).getPlaceId().getId());
                            //Adapter 안에 아이템의 정보 담기

                            Log.d("테스트", "" + address + "/" + placeName + "/" + detailAddress + "/" + peopleNum + "/" + updateElapsedTime + "/" + placeId);
                            adapter.addItem(new PlaceItem(address, placeName, detailAddress, peopleNum, updateElapsedTime, placeId));
                        }

                        // 리스트뷰에 Adapter 설정
                        binding.placeListView.setAdapter(adapter);
                        binding.loadingTv.setVisibility(View.GONE);
                    } else {
                        binding.loadingTv.setText("정보 없음");
                    }
                } else {
                    switch (response.code()) {
                        case 401:
                            new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), BookmarkedPlacesInListActivity.this);
                            final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            new ToastWarning(getResources().getString(R.string.toast_none_status_code), BookmarkedPlacesInListActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Headcount>> call, Throwable t) {
                Log.e("API Error", t.getMessage(), t);
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), BookmarkedPlacesInListActivity.this);
            }
        });

        // 리스트뷰에 Adapter 설정
        binding.placeListView.setAdapter(adapter);
    }

    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<PlaceItem> items = new ArrayList<PlaceItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(PlaceItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final PlaceItem placeItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.place_list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView placeName_tv = convertView.findViewById(R.id.placeName_tv);
            TextView placeInfo_tv = convertView.findViewById(R.id.placeInfo_tv);
            TextView peopleCount_tv = convertView.findViewById(R.id.peopleCount_tv);

            placeName_tv.setText(placeItem.getPlaceName());
            placeInfo_tv.setText(placeItem.getDetailAddress());
            if (placeItem.getPeopleCnt() > 0) {
                peopleCount_tv.setText("" + placeItem.getPeopleCnt());
            } else {
                peopleCount_tv.setText("?");
            }

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog_placeInfo(placeItem);
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

    /**
     * (dialog_ask_add_or_cancel_bookmark) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_add_or_delete_bookmark(PlaceItem placeItem) {
        ask_add_or_cancel_bookmark_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        ask_add_or_cancel_bookmark_dialog.getWindow().setDimAmount(0.35f);

        Button bookmarkFilled_btn = placeInfo_dialog.findViewById(R.id.bookmarkFilled_btn);
        TextView dialog_tv = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.dialog_tv);
        if (bookmarkFilled_btn.getVisibility() == View.VISIBLE) {
            dialog_tv.setText(getResources().getString(R.string.dialog_cancel_bookmark));
        } else {
            dialog_tv.setText(getResources().getString(R.string.dialog_add_bookmark));
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
                if (placeItem.getBookmarked()) {
                    List<String> bookmarkIds = new ArrayList<>();
                    bookmarkIds.add(bookmarkId);
                    Call<ResponseBody> call_bookmarksAPI_deleteBookmarkedPlaceInBookmarkListForPlace = bookmarksAPI.deleteBookmarkedPlaceInBookmarkListForPlace(placeItem.getPlaceId(), bookmarkIds);
                    call_bookmarksAPI_deleteBookmarkedPlaceInBookmarkListForPlace.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                placeItem.setBookmarked(false);
                                bookmarkFilled_btn.setVisibility(View.GONE);
                                bookmarkEmpty_btn.setVisibility(View.VISIBLE);
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), BookmarkedPlacesInListActivity.this);
                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        break;
                                    default:
                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), BookmarkedPlacesInListActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), BookmarkedPlacesInListActivity.this);
                        }
                    });
                } else {
                    List<String> bookmarkIds = new ArrayList<>();
                    bookmarkIds.add(bookmarkId);
                    Call<ResponseBody> call_bookmarksAPI_addBookmarkedPlaceInBookmarkList = bookmarksAPI.addBookmarkedPlaceInBookmarkList(placeItem.getPlaceId(), bookmarkIds);
                    call_bookmarksAPI_addBookmarkedPlaceInBookmarkList.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                placeItem.setBookmarked(true);
                                bookmarkFilled_btn.setVisibility(View.VISIBLE);
                                bookmarkEmpty_btn.setVisibility(View.GONE);
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), BookmarkedPlacesInListActivity.this);
                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        break;
                                    default:
                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), BookmarkedPlacesInListActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), BookmarkedPlacesInListActivity.this);
                        }
                    });
                }
            }
        });
    }

    // (dialog_place_info) 다이얼로그를 디자인하는 함수
    public void showDialog_placeInfo(PlaceItem placeItem) {
        clickedPlaceItem = placeItem;
        placeInfo_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        placeInfo_dialog.getWindow().setDimAmount(0.35f);

        TextView placeName_tv = placeInfo_dialog.findViewById(R.id.placeName_tv);
        TextView peopleCount_tv = placeInfo_dialog.findViewById(R.id.peopleCount_tv);
        TextView roadNameAddress_tv = placeInfo_dialog.findViewById(R.id.roadNameAddress_tv);
        TextView detailAddress_tv = placeInfo_dialog.findViewById(R.id.detailAddress_tv);
        TextView updateElapsedTime_tv = placeInfo_dialog.findViewById(R.id.updateElapsedTime_tv);

        // 즐겨찾기 버튼
        bookmarkEmpty_btn = placeInfo_dialog.findViewById(R.id.bookmarkEmpty_btn);
        bookmarkFilled_btn = placeInfo_dialog.findViewById(R.id.bookmarkFilled_btn);

        placeName_tv.setText(placeItem.getPlaceName());
        if (placeItem.getPeopleCnt() > 0) {
            peopleCount_tv.setText("" + placeItem.getPeopleCnt());
        } else {
            peopleCount_tv.setText("?");
        }
        long updateElapsedTime = placeItem.getUpdateElapsedTime();
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
        roadNameAddress_tv.setText(roadNameAddress_tv.getText().toString());
        detailAddress_tv.setText(placeItem.getDetailAddress());
        if (placeItem.getBookmarked()) {
            bookmarkFilled_btn.setVisibility(View.VISIBLE);
            bookmarkEmpty_btn.setVisibility(View.GONE);
        } else {
            bookmarkFilled_btn.setVisibility(View.GONE);
            bookmarkEmpty_btn.setVisibility(View.VISIBLE);
        }

        // Dialog 닫기 버튼
        ImageButton dialogClose_iBtn = placeInfo_dialog.findViewById(R.id.dialogClose_iBtn);
        dialogClose_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 카메라 스캔 버튼
        Button peopleScan_btn = placeInfo_dialog.findViewById(R.id.peopleScan_btn);
        peopleScan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 장소 수정 버튼
        Button editLocation_btn = placeInfo_dialog.findViewById(R.id.editLocation_btn);
        editLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
                final Intent intent = new Intent(getApplicationContext(), AddLocationInfoActivity.class);

                intent.putExtra("modify_clicked", true);
                intent.putExtra("placeName_value", placeName_tv.getText().toString());
                intent.putExtra("detailAddress_value", detailAddress_tv.getText().toString());

                startActivity(intent);
            }
        });

        if (placeItem.getBookmarked()) {
            bookmarkFilled_btn.setVisibility(View.VISIBLE);
            bookmarkEmpty_btn.setVisibility(View.GONE);
        } else {
            bookmarkFilled_btn.setVisibility(View.GONE);
            bookmarkEmpty_btn.setVisibility(View.VISIBLE);
        }

        bookmarkEmpty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_add_or_delete_bookmark(placeItem);
            }
        });

        bookmarkFilled_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_add_or_delete_bookmark(placeItem);
            }
        });
    }
}