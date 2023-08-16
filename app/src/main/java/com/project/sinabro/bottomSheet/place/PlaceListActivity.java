package com.project.sinabro.bottomSheet.place;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.models.PeopleNumber;
import com.project.sinabro.retrofit.headcountsAPI;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceListActivity extends AppCompatActivity {

    private ImageButton back_iBtn, dialogClose_iBtn;
    private Button goAddPlace_btn, peopleScan_btn, editLocation_btn;
    private static Button bookmarkEmpty_btn, bookmarkFilled_btn;
    private ListView listview;
    private ListViewAdapter adapter;
    private Dialog placeInfo_dialog, ask_add_or_cancel_bookmark_dialog;
    private TextView address_tv, placeName_tv_inDialog, peopleCount_tv_inDialog, address_tv_inDialog, detailAddress_tv_inDialog, updateElapsedTime_tv_inDialog;

    private PlaceItem clickedPlaceItem;

    private String markerId, address;
    private Double latitude, longitude;

    Boolean bookmarked = true;

    @Override
    public void onBackPressed() {
        // 프로그래머가 원하는 액티비티로 이동
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        TokenManager tokenManager = TokenManager.getInstance(this);
        RetrofitService retrofitService = new RetrofitService(tokenManager);
        headcountsAPI peopleNumbersAPI = retrofitService.getRetrofit().create(headcountsAPI.class);

        /** "장소정보 확인" 다이얼로그 변수 초기화 및 설정 */
        placeInfo_dialog = new Dialog(PlaceListActivity.this);  // Dialog 초기화
        placeInfo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        placeInfo_dialog.setContentView(R.layout.dialog_place_info); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        placeInfo_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** "즐겨찾기 추가/취소 확인" 다이얼로그 변수 초기화 및 설정 */
        ask_add_or_cancel_bookmark_dialog = new Dialog(PlaceListActivity.this);  // Dialog 초기화
        ask_add_or_cancel_bookmark_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        ask_add_or_cancel_bookmark_dialog.setContentView(R.layout.dialog_ask_add_or_cancel_bookmark); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        ask_add_or_cancel_bookmark_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 뒤로가기 버튼 기능
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        final Intent intent = getIntent();
        markerId = intent.getStringExtra("markerId_value");

        address_tv = findViewById(R.id.address_tv);

        goAddPlace_btn = findViewById(R.id.goAddPlace_btn);
        goAddPlace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), AddLocationInfoActivity.class);
                intent.putExtra("markerId_value", markerId);
                intent.putExtra("address_value", address);
                intent.putExtra("latitude_value", latitude);
                intent.putExtra("longitude_value", longitude);
                startActivity(intent);
            }
        });

        listview = findViewById(R.id.placeList_listView);
        adapter = new ListViewAdapter();

        Call<List<PeopleNumber>> call = peopleNumbersAPI.getPlaceInformationsById(markerId);
        call.enqueue(new Callback<List<PeopleNumber>>() {
            @Override
            public void onResponse(Call<List<PeopleNumber>> call, Response<List<PeopleNumber>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PeopleNumber> placeInformations = response.body();
                    if (!placeInformations.isEmpty()) {
                        address = placeInformations.get(0).getPlaceId().getAddress();
                        latitude = Double.valueOf(placeInformations.get(0).getPlaceId().getMarkerId().getLatitude());
                        longitude = Double.valueOf(placeInformations.get(0).getPlaceId().getMarkerId().getLongitude());
                        address_tv.setText(address);
                        for (int k = 0; k < placeInformations.size(); k++) {
                            int peopleNum = placeInformations.get(k).getPeopleCount();
                            String placeName = placeInformations.get(k).getPlaceId().getPlaceName();
                            String detailAddress = placeInformations.get(k).getPlaceId().getDetailAddress();
                            long updateElapsedTime = placeInformations.get(k).getUpdateElapsedTime();
                            String placeId = String.valueOf(placeInformations.get(k).getPlaceId().getId());
                            //Adapter 안에 아이템의 정보 담기
                            adapter.addItem(new PlaceItem(address, placeName, detailAddress, peopleNum, updateElapsedTime, placeId));
                        }

                        // 리스트뷰에 Adapter 설정
                        listview.setAdapter(adapter);
                    } else {
                        Log.d("데이터", "The list of places is empty");
                    }
                } else {
                    Log.d("데이터", "Response was not successful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<PeopleNumber>> call, Throwable t) {
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), PlaceListActivity.this);
            }
        });
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

    public void updateBookmarkBtnState(Boolean bookmarked) {
        if (bookmarked) {
            bookmarkEmpty_btn.setVisibility(View.GONE);
            bookmarkFilled_btn.setVisibility(View.VISIBLE);
        } else {
            bookmarkEmpty_btn.setVisibility(View.VISIBLE);
            bookmarkFilled_btn.setVisibility(View.GONE);
        }
    }

    // (dialog_place_info) 다이얼로그를 디자인하는 함수
    public void showDialog_placeInfo(PlaceItem placeItem) {
        clickedPlaceItem = placeItem;
        placeInfo_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        placeInfo_dialog.getWindow().setDimAmount(0.35f);

        placeName_tv_inDialog = placeInfo_dialog.findViewById(R.id.placeName_tv);
        peopleCount_tv_inDialog = placeInfo_dialog.findViewById(R.id.peopleCount_tv);
        address_tv_inDialog = placeInfo_dialog.findViewById(R.id.roadNameAddress_tv);
        detailAddress_tv_inDialog = placeInfo_dialog.findViewById(R.id.detailAddress_tv);
        updateElapsedTime_tv_inDialog = placeInfo_dialog.findViewById(R.id.updateElapsedTime_tv);

        placeName_tv_inDialog.setText(placeItem.getPlaceName());
        if (placeItem.getPeopleCnt() > 0) {
            peopleCount_tv_inDialog.setText("" + placeItem.getPeopleCnt());
        } else {
            peopleCount_tv_inDialog.setText("?");
        }
        address_tv_inDialog.setText(address_tv.getText().toString());
        detailAddress_tv_inDialog.setText(placeItem.getDetailAddress());
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
        updateElapsedTime_tv_inDialog.setText(updateElapsedTime_str);

        // Dialog 닫기 버튼
        dialogClose_iBtn = placeInfo_dialog.findViewById(R.id.dialogClose_iBtn);
        dialogClose_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 카메라 스캔 버튼
        peopleScan_btn = placeInfo_dialog.findViewById(R.id.peopleScan_btn);
        peopleScan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 장소 수정 버튼
        editLocation_btn = placeInfo_dialog.findViewById(R.id.editLocation_btn);
        editLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
                final Intent intent = new Intent(getApplicationContext(), AddLocationInfoActivity.class);
                intent.putExtra("forModify", true);
                intent.putExtra("markerId_value", markerId);
                intent.putExtra("placeId_value", placeItem.getPlaceId());
                intent.putExtra("address_value", placeItem.getAddress());
                intent.putExtra("placeName_value", placeName_tv_inDialog.getText().toString());
                intent.putExtra("detailAddress_value", detailAddress_tv_inDialog.getText().toString());
                startActivity(intent);
            }
        });

        /** "북마크 등록" 버튼 */
        bookmarkEmpty_btn = placeInfo_dialog.findViewById(R.id.bookmarkEmpty_btn);
        bookmarkEmpty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_add_or_delete_bookmark();
            }
        });

        /** "북마크 제거" 버튼 */
        bookmarkFilled_btn = placeInfo_dialog.findViewById(R.id.bookmarkFilled_btn);
        bookmarkFilled_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_add_or_delete_bookmark();
            }
        });
    }

    /**
     * (dialog_ask_add_or_cancel_bookmark) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_add_or_delete_bookmark() {
        placeInfo_dialog.dismiss();
        ask_add_or_cancel_bookmark_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        ask_add_or_cancel_bookmark_dialog.getWindow().setDimAmount(0.35f);

        Button bookmarkFilled_btn = placeInfo_dialog.findViewById(R.id.bookmarkFilled_btn);
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
                showDialog_placeInfo(clickedPlaceItem);
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
                    startActivity(intent);
                } else {
                    final Intent intent = new Intent(getApplicationContext(), AddBookmarkToListActivity.class);
                    intent.putExtra("fromPlaceListActivity", true);
                    startActivity(intent);
                }
                showDialog_placeInfo(clickedPlaceItem);
            }
        });
    }
}