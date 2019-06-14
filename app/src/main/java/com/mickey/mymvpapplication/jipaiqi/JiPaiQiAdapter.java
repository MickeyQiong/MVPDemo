package com.mickey.mymvpapplication.jipaiqi;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mickey.mymvpapplication.R;

import java.util.List;

/**
 * Description:
 * Created by Ming on 2019-6-11.
 */
public class JiPaiQiAdapter extends RecyclerView.Adapter<JiPaiQiAdapter.ViewHolder> {

    private String[] mList;
    private LayoutInflater inflater;

    public JiPaiQiAdapter(Context context,String[] mList) {
        this.mList = mList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_jipaiqi,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (i<6 || (i > 11 && i < 18)){
            viewHolder.textView.setTextColor(Color.BLACK);
        }else {
            viewHolder.textView.setTextColor(Color.RED);
        }
        viewHolder.textView.setText(mList[i]);
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }
}

