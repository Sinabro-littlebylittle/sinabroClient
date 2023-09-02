package com.project.sinabro.sideBarMenu.bookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.R;
import com.project.sinabro.bottomSheet.place.AddBookmarkToListActivity;
import com.project.sinabro.databinding.ActivityAddNewListBinding;
import com.project.sinabro.models.Bookmark;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.BookmarksAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.textWatcher.ListNameWatcher;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewBookmarkListActivity extends AppCompatActivity {

    private ActivityAddNewListBinding binding;

    private Dialog selectListIconColor_dialog;

    RoundedImageView check_circle_roundedImageView,
            check_circle_roundedImageView2,
            check_circle_roundedImageView3,
            check_circle_roundedImageView4,
            check_circle_roundedImageView5,
            check_circle_roundedImageView6,
            check_circle_roundedImageView7,
            check_circle_roundedImageView8,
            check_circle_roundedImageView9,
            check_circle_roundedImageView10,
            check_circle_roundedImageView11,
            check_circle_roundedImageView12;

    final int[] checkedColorInfo = {0};

    final Boolean[] colorChecked = {false};

    private Bundle args = new Bundle();

    private String listName;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    BookmarksAPI bookmarksAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(getApplicationContext());
        retrofitService = new RetrofitService(tokenManager);
        bookmarksAPI = retrofitService.getRetrofit().create(BookmarksAPI.class);

        final Intent intent = getIntent();
        Boolean modify_clicked = intent.getBooleanExtra("modify_clicked", false);

        /** "리스트 제거 확인 안내" 다이얼로그 변수 초기화 및 설정 */
        selectListIconColor_dialog = new Dialog(AddNewBookmarkListActivity.this);  // Dialog 초기화
        selectListIconColor_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        selectListIconColor_dialog.setContentView(R.layout.dialog_select_list_icon_color); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        selectListIconColor_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        check_circle_roundedImageView = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView);
        check_circle_roundedImageView2 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView2);
        check_circle_roundedImageView3 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView3);
        check_circle_roundedImageView4 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView4);
        check_circle_roundedImageView5 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView5);
        check_circle_roundedImageView6 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView6);
        check_circle_roundedImageView7 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView7);
        check_circle_roundedImageView8 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView8);
        check_circle_roundedImageView9 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView9);
        check_circle_roundedImageView10 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView10);
        check_circle_roundedImageView11 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView11);
        check_circle_roundedImageView12 = selectListIconColor_dialog.findViewById(R.id.check_circle_roundedImageView12);

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.listNameTextInputLayout.getEditText().addTextChangedListener(new ListNameWatcher(binding.listNameTextInputLayout, getResources().getString(R.string.add_list_name_failed)));

        binding.showColorPaletteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_select_list_icon_color();
            }
        });

        binding.addNewListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (binding.listNameTextInputLayout.getError() != null || String.valueOf(binding.listNameEditText.getText()).equals("")) {
                    binding.listNameEditText.requestFocus();
                    binding.listNameTextInputLayout.setError(getResources().getString(R.string.add_list_name_failed));
                    binding.listNameTextInputLayout.setErrorEnabled(true);
                    binding.listNameTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (colorChecked[0] != true) {
                    new ToastWarning(getResources().getString(R.string.toast_add_list_failed), AddNewBookmarkListActivity.this);
                } else {
                    Boolean forModify = intent.getBooleanExtra("forModify", false);
                    Bookmark bookmark = new Bookmark();
                    bookmark.setBookmarkName(binding.listNameEditText.getText().toString());
                    bookmark.setIconColor(checkedColorInfo[0]);

                    // 북마크 리스트 정보 수정 시
                    if (forModify) {
                        String bookmarkId = intent.getStringExtra("bookmarkId");
                        Call<ResponseBody> call_bookmarksAPI_updateBookmarkList = bookmarksAPI.updateBookmarkList(bookmarkId, bookmark);
                        call_bookmarksAPI_updateBookmarkList.enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    new ToastSuccess(getResources().getString(R.string.toast_modify_list_success), AddNewBookmarkListActivity.this);
                                    finish();
                                } else {
                                    switch (response.code()) {
                                        case 401:
                                            new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddNewBookmarkListActivity.this);
                                            final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                            startActivity(intent);
                                            break;
                                        default:
                                            new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddNewBookmarkListActivity.this);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                                new ToastWarning(getResources().getString(R.string.toast_server_error), AddNewBookmarkListActivity.this);
                            }
                        });

                        return;
                    }

                    Call<Bookmark> call_bookmarksAPI_addBookmarkList = bookmarksAPI.addBookmarkList(bookmark);
                    call_bookmarksAPI_addBookmarkList.enqueue(new Callback<Bookmark>() {
                        @Override
                        public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                            if (response.isSuccessful()) {
                                new ToastSuccess(getResources().getString(R.string.toast_add_list_success), AddNewBookmarkListActivity.this);
                                finish(); // 현재 액티비티 종료
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<Bookmark> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), AddNewBookmarkListActivity.this);
                        }
                    });
                }
            }
        });

        if (modify_clicked) {
            checkedColorInfo[0] = intent.getIntExtra("listIconColor", -1);
            listName = intent.getStringExtra("listName");
            colorChecked[0] = true;
            binding.myPageFragmentTitleTv.setText("리스트 수정");
            binding.addNewListBtn.setText("수정하기");
            binding.selectedColorRoundedImageView.setImageResource(checkedColorInfo[0]);
            binding.listNameEditText.setText(listName);
        }
    }

    /**
     * (dialog_select_list_icon_color) 다이얼로그 내에서 특정 색 선택 시
     * 다른 색상의 체크 상태를 해제하기 위한 함수
     */
    private void clear_colors_checked_status() {
        check_circle_roundedImageView.setImageResource(R.color.transparent);
        check_circle_roundedImageView2.setImageResource(R.color.transparent);
        check_circle_roundedImageView3.setImageResource(R.color.transparent);
        check_circle_roundedImageView4.setImageResource(R.color.transparent);
        check_circle_roundedImageView5.setImageResource(R.color.transparent);
        check_circle_roundedImageView6.setImageResource(R.color.transparent);
        check_circle_roundedImageView7.setImageResource(R.color.transparent);
        check_circle_roundedImageView8.setImageResource(R.color.transparent);
        check_circle_roundedImageView9.setImageResource(R.color.transparent);
        check_circle_roundedImageView10.setImageResource(R.color.transparent);
        check_circle_roundedImageView11.setImageResource(R.color.transparent);
        check_circle_roundedImageView12.setImageResource(R.color.transparent);
    }

    /**
     * (dialog_select_list_icon_color) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_select_list_icon_color() {
        selectListIconColor_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        selectListIconColor_dialog.getWindow().setDimAmount(0.35f);

        check_circle_roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num1;
            }
        });

        check_circle_roundedImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView2.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num2;
            }
        });

        check_circle_roundedImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView3.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num3;
            }
        });

        check_circle_roundedImageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView4.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num4;
            }
        });

        check_circle_roundedImageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView5.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num5;
            }
        });

        check_circle_roundedImageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView6.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num6;
            }
        });

        check_circle_roundedImageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView7.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num7;
            }
        });

        check_circle_roundedImageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView8.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num8;
            }
        });

        check_circle_roundedImageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView9.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num9;
            }
        });

        check_circle_roundedImageView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView10.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num10;
            }
        });

        check_circle_roundedImageView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView11.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num11;
            }
        });

        check_circle_roundedImageView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_colors_checked_status();
                check_circle_roundedImageView12.setImageResource(R.drawable.no_bg_check_icon);
                checkedColorInfo[0] = R.color.num12;
            }
        });

        // "취소" 버튼 클릭 시 이벤트 처리 코드
        selectListIconColor_dialog.findViewById(R.id.noBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectListIconColor_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "확인" 버튼 클릭 시 이벤트 처리 코드
        selectListIconColor_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedColorInfo[0] == 0) {
                    new ToastWarning(getResources().getString(R.string.toast_select_color_failed), AddNewBookmarkListActivity.this);
                    return;
                }

                binding.selectedColorRoundedImageView.setImageResource(checkedColorInfo[0]);
                colorChecked[0] = true;
                selectListIconColor_dialog.dismiss(); // 다이얼로그 닫기
            }
        });
    }
}