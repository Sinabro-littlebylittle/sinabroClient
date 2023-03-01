package com.project.sinabro.sideBarMenu.myPage;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.R;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton back_ibtn;

    private TextView modify_my_info_tv;

    private RoundedImageView userImage_roundedImageView;

    private RelativeLayout password_relativeLayout;

    private Bitmap bitmap;

    private Button modify_complete_btn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page_activity, container, false);

        /** 뒤로가기 버튼 기능 */
        back_ibtn = (ImageButton) view.findViewById(R.id.back_iBtn);
        back_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        /** "마이페이지(fragment)"에서 "정보 수정(activity)"로 화면 전환 코드 추가 */
        modify_my_info_tv = (TextView) view.findViewById(R.id.modify_my_info_tv);
        modify_my_info_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "정보 수정" 액티비티로 이동
                // fragment이기 때문에 activity intent와는 다른 방식
                Intent intent = new Intent(getActivity(), ModifyMyInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        userImage_roundedImageView = (RoundedImageView) view.findViewById(R.id.userImage_roundedImageView);
        userImage_roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //갤러리 호출
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_PICK);
                activityResultLauncher.launch(intent);
            }
        });

        /** "마이페이지(fragment)"에서 "비밀번호 변경(activity)"로 화면 전환r 코드 추가 */
        password_relativeLayout = (RelativeLayout) view.findViewById(R.id.password_relativeLayout);
        password_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "비밀번호 변경" 액티비티로 이동
                // fragment이기 때문에 activity intent와는 다른 방식
                Intent intent = new Intent(getActivity(), ModifyPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        modify_complete_btn = (Button) view.findViewById(R.id.modify_complete_btn);
        modify_complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("테스트", "홀리몰리");
            }
        });

        return view;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        int calRatio = calculateInSampleSize(
                                result.getData().getData()
                                , getResources().getDimensionPixelSize(R.dimen.imgSize)
                                , getResources().getDimensionPixelSize(R.dimen.imgSize)
                        );

                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = calRatio;

                        try {
                            InputStream inputStream = getContext().getContentResolver().openInputStream(result.getData().getData());
                            bitmap = BitmapFactory.decodeStream(inputStream, null, option);
                            inputStream.close();
                            if (bitmap != null) {
                                modify_complete_btn.setVisibility(View.VISIBLE);
                                userImage_roundedImageView.setImageBitmap(bitmap);
                                YoYo.with(Techniques.FadeInUp)
                                        .duration(500)
                                        .repeat(0)
                                        .playOn(getView().findViewById(R.id.modify_complete_btn));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private int calculateInSampleSize(Uri fileUri, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);

            // inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
            // 로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 비율 계산
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}