package com.project.sinabro;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private ArrayList<Dictionary> mList;
/*뷰 홀더 어뎁터*/
    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        /*변수 선언*/
        protected TextView name;


        public BookmarkViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name_favorite_place);

        }
    }


    public BookmarkAdapter(ArrayList<Dictionary> list) {
        this.mList = list;
    }


    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favorite_place_list, viewGroup, false);

        BookmarkViewHolder viewHolder = new BookmarkViewHolder(view);

        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.BookmarkViewHolder viewholder, int position) {



        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        viewholder.name.setGravity(Gravity.CENTER);                             // 텍스트 설정

        viewholder.name.setText(mList.get(position).getName());                 // get 형식으로 텍스트를 받아옴



        /*추후 뷰홀더 클릭시 여기서 소스코드 구현 하면 됨 */
        viewholder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 뷰홀더 클릭 기능

                Log.d("ViewHolder", "onClick: 테스트 중입니다.");
            }
        });




    }

    @Override
    public int getItemCount() {
        return mList.size();                //(null != mList ? mList.size() : 0)
    }

}

