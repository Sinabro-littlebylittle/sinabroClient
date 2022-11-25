package com.project.sinabro;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.CustomViewHolder> {

    private ArrayList<Dictionary> mList;
/*뷰 홀더 어뎁터*/
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        //protected TextView english;
        //protected TextView korean;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.id_listitem);

        }
    }


    public BookmarkAdapter(ArrayList<Dictionary> list) {
        this.mList = list;
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favorite_place_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        //viewholder.english.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
       // viewholder.korean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        viewholder.name.setGravity(Gravity.CENTER);
       // viewholder.english.setGravity(Gravity.CENTER);
        //viewholder.korean.setGravity(Gravity.CENTER);



        viewholder.name.setText(mList.get(position).getName());
        //viewholder.english.setText(mList.get(position).getEnglish());
        //viewholder.korean.setText(mList.get(position).getKorean());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}

