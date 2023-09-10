package com.project.sinabro.search;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.bottomSheet.place.PlaceListActivity;
import com.project.sinabro.databinding.ActivitySearchKeywordBinding;
import com.project.sinabro.models.Bookmark;
import com.project.sinabro.models.SearchHistory;
import com.project.sinabro.models.SearchKeyword;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.RetrofitServiceForKakao;
import com.project.sinabro.retrofit.interfaceAPIs.KakaoAPI;
import com.project.sinabro.retrofit.interfaceAPIs.SearchHistoriesAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.sideBarMenu.bookmark.AddNewBookmarkListActivity;
import com.project.sinabro.sideBarMenu.bookmark.BookmarkListItem;
import com.project.sinabro.sideBarMenu.bookmark.BookmarkedPlacesInListActivity;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchKeywordActivity extends AppCompatActivity {

    private ActivitySearchKeywordBinding binding;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private RetrofitServiceForKakao retrofitServiceForKakao;
    private SearchHistoriesAPI searchHistoriesAPI;
    private KakaoAPI kakaoInterface;

    private SearchHistoryListViewAdapter searchHistoryListViewAdapter;
    private SearchedPlaceListViewAdapter searchedPlaceListViewAdapter;

    private List<SearchHistory> searchHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchKeywordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(getApplicationContext());
        retrofitService = new RetrofitService(tokenManager);
        retrofitServiceForKakao = new RetrofitServiceForKakao();
        searchHistoriesAPI = retrofitService.getRetrofit().create(SearchHistoriesAPI.class);
        kakaoInterface = retrofitServiceForKakao.getRetrofit().create(KakaoAPI.class);

        // 검색창 자동 포커싱 및 가상 키보드 표시
        binding.searchEdt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        searchHistoryListViewAdapter = new SearchHistoryListViewAdapter();
        searchedPlaceListViewAdapter = new SearchedPlaceListViewAdapter();
        binding.searchHistoryListView.setAdapter(searchHistoryListViewAdapter); // ListView에 어댑터 설정

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 가상 키보드 숨김
                InputMethodManager immHide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                immHide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                finish(); // 현재 액티비티 종료
            }
        });

        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 텍스트 변경 전 호출됩니다.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 텍스트가 변경될 때 호출됩니다.
                // charSequence에는 현재의 텍스트가 들어있습니다.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String keyword = editable.toString();

                if (keyword.equals("")) {
                    binding.searchedPlaceListView.setVisibility(View.GONE);
                    binding.searchHistoryListView.setVisibility(View.VISIBLE);
                    binding.closeCircleRoundedImageView.setVisibility(View.GONE);
                    return;
                }

                binding.searchedPlaceListView.setVisibility(View.VISIBLE);
                binding.searchHistoryListView.setVisibility(View.GONE);
                binding.closeCircleRoundedImageView.setVisibility(View.VISIBLE);

                Call<SearchKeyword> call_kakaoAPI_searchByKeyword = kakaoInterface.searchByKeyword(keyword);
                call_kakaoAPI_searchByKeyword.enqueue(new Callback<SearchKeyword>() {
                    @Override
                    public void onResponse(Call<SearchKeyword> call, Response<SearchKeyword> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<SearchKeyword.Document> documents = response.body().getDocuments();

                            searchedPlaceListViewAdapter.clearItems();  // 기존 아이템들을 모두 지웁니다.
                            binding.searchedPlaceListView.setAdapter(searchedPlaceListViewAdapter); // ListView에 어댑터 설정

                            for (SearchKeyword.Document document : documents) {
                                String placeName = document.getPlace_name();
                                String roadAddressName = document.getRoad_address_name();
                                String x = document.getX();
                                String y = document.getY();
                                String categories = document.getCategory_name();
                                String[] parts = categories.split(">");
                                String categoryName = parts[parts.length - 1].trim();
                                int distance = -1;

                                if (!document.getDistance().equals(""))
                                    distance = Integer.parseInt(document.getDistance());

                                searchedPlaceListViewAdapter.addItem(new
                                        SearchedPlaceItem(placeName, roadAddressName, x, y, categoryName, distance));
                            }

                            // 어댑터에게 데이터 변경을 알립니다.
                            searchedPlaceListViewAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchKeyword> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), SearchKeywordActivity.this);
                    }
                });
            }
        });

        binding.closeCircleRoundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchEdt.setText("");
            }
        });

        Call<List<SearchHistory>> call_searchHistoriesAPI_getSearchHistories = searchHistoriesAPI.getSearchHistories();
        call_searchHistoriesAPI_getSearchHistories.enqueue(new Callback<List<SearchHistory>>() {
            @Override
            public void onResponse(Call<List<SearchHistory>> call, Response<List<SearchHistory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchHistoryList = response.body();
                    if (!searchHistoryList.isEmpty()) {
                        for (int k = 0; k < searchHistoryList.size(); k++) {
                            //Adapter 안에 아이템의 정보 담기
                            searchHistoryListViewAdapter.addItem(new

                                    SearchHistoryItem(searchHistoryList.get(k).get_id(), searchHistoryList.get(k).getSearchKeyword(), searchHistoryList.get(k).getLatitude(), searchHistoryList.get(k).getLongitude(), searchHistoryList.get(k).getCreatedTime()));
                        }

                        binding.searchHistoryListView.setAdapter(searchHistoryListViewAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SearchHistory>> call, Throwable t) {
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), SearchKeywordActivity.this);
            }
        });
    }

    //* SearchedPlace 리스트뷰 어댑터 */
    public class SearchedPlaceListViewAdapter extends BaseAdapter {
        ArrayList<SearchedPlaceItem> items = new ArrayList<SearchedPlaceItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SearchedPlaceItem item) {
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
            final SearchedPlaceItem listItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.searched_list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView placeName_tv = convertView.findViewById(R.id.placeName_tv);
            TextView address_tv = convertView.findViewById(R.id.address_tv);
            TextView category_name_tv = convertView.findViewById(R.id.category_name_tv);

            placeName_tv.setText(listItem.getPlaceName());
            address_tv.setText(listItem.getRoadAddressName());
            category_name_tv.setText(listItem.getCategoryName());

            // 각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // "리스트 세부" 액티비티로 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    String placeName = listItem.getPlaceName();
                    String latitude = listItem.getY();
                    String longitude = listItem.getX();

                    intent.putExtra("searchedPlaceName", placeName);
                    intent.putExtra("searchedLatitude", latitude);
                    intent.putExtra("searchedLongitude", longitude);

                    SearchHistory searchHistory = new SearchHistory();
                    searchHistory.setSearchKeyword(placeName);
                    searchHistory.setLatitude(latitude);
                    searchHistory.setLongitude(longitude);

                    Call<SearchHistory> call_searchHistoriesAPI_getSearchHistories = searchHistoriesAPI.postSearchHistory(searchHistory);
                    call_searchHistoriesAPI_getSearchHistories.enqueue(new Callback<SearchHistory>() {
                        @Override
                        public void onResponse(Call<SearchHistory> call, Response<SearchHistory> response) {
                            if (response.isSuccessful()) {
                                Log.d("검색 기록  추가 완료", "onResponse: ");
                            }
                        }

                        @Override
                        public void onFailure(Call<SearchHistory> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), SearchKeywordActivity.this);
                        }
                    });

                    InputMethodManager immHide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immHide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    startActivity(intent);
                }
            });

            return convertView;  //뷰 객체 반환
        }

        public void clearItems() {
            items.clear();
        }
    }

    //* SearchHistory 리스트뷰 어댑터 */
    public class SearchHistoryListViewAdapter extends BaseAdapter {
        ArrayList<SearchHistoryItem> items = new ArrayList<SearchHistoryItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SearchHistoryItem item) {
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
            final SearchHistoryItem listItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.searched_history_list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView placeName_tv = convertView.findViewById(R.id.placeName_tv);
            TextView searchDate_tv = convertView.findViewById(R.id.searchDate_tv);

            placeName_tv.setText(listItem.getSearchKeyword());
            String createdTime = listItem.getCreatedTime();
            String[] parts = createdTime.split("-");
            String month = parts[1];
            String day = parts[2].substring(0, 2);
            String formattedCreatedTime = month + ". " + day + ". ";
            searchDate_tv.setText(formattedCreatedTime);

            RoundedImageView close_thin_roundedImageView = convertView.findViewById(R.id.close_thin_roundedImageView);
            close_thin_roundedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Call<ResponseBody> call_searchHistoriesAPI_deleteSearchHistory = searchHistoriesAPI.deleteSearchHistory(listItem.getId());
                    call_searchHistoriesAPI_deleteSearchHistory.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("검색 기록  삭제 완료", "onResponse: ");
                                items.remove(position);
                                notifyDataSetChanged();
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        break;
                                    default:
                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), SearchKeywordActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), SearchKeywordActivity.this);
                        }
                    });
                }
            });

            // 각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // "리스트 세부" 액티비티로 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    String placeName = listItem.getSearchKeyword();
                    String latitude = listItem.getLatitude();
                    String longitude = listItem.getLongitude();

                    intent.putExtra("searchedPlaceName", placeName);
                    intent.putExtra("searchedLatitude", latitude);
                    intent.putExtra("searchedLongitude", longitude);

                    SearchHistory searchHistory = new SearchHistory();
                    searchHistory.setSearchKeyword(placeName);
                    searchHistory.setLatitude(latitude);
                    searchHistory.setLongitude(longitude);

                    Call<SearchHistory> call_searchHistoriesAPI_getSearchHistories = searchHistoriesAPI.postSearchHistory(searchHistory);
                    call_searchHistoriesAPI_getSearchHistories.enqueue(new Callback<SearchHistory>() {
                        @Override
                        public void onResponse(Call<SearchHistory> call, Response<SearchHistory> response) {
                            if (response.isSuccessful()) {
                                Log.d("검색 기록  추가 완료", "onResponse: ");
                            }
                        }

                        @Override
                        public void onFailure(Call<SearchHistory> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), SearchKeywordActivity.this);
                        }
                    });

                    InputMethodManager immHide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immHide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    startActivity(intent);
                }
            });

            return convertView;  //뷰 객체 반환
        }

        public void clearItems() {
            items.clear();
        }
    }
}