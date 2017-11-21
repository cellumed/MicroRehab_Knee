package com.cellumed.healthcare.microrehab.knee.Setting;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cellumed.healthcare.microrehab.knee.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Adapter_SettingList extends RecyclerView.Adapter<Adapter_SettingList.ViewHolder> {


    private List<String> mDataList;
    private Context mContext;
    private boolean isFirst = true;
    private  List<ViewHolder> mList  = new ArrayList<>();
    private OnAdapterClick mAdapterClick;


    public Adapter_SettingList(Context context,OnAdapterClick mAdapterClick, List<String> dataList) {
        super();
        this.mContext = context;
        this.mDataList = dataList;
        this.mAdapterClick = mAdapterClick;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_setting_menu, viewGroup, false);
        v.setTag(v);
        ViewHolder viewHolder = new ViewHolder(v);
        mList.add(viewHolder);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvMenu.setText(mDataList.get(position));
        if (position % 2 == 0) {
            holder.tvMenu.setBackgroundColor(Color.parseColor("#FF999999"));
        } else {
            holder.tvMenu.setBackgroundColor(Color.parseColor("#FF8F8F8F"));
        }
        if (isFirst) {
            isFirst = false;
            holder.v.setVisibility(View.VISIBLE);
        } else {
            holder.v.setVisibility(View.INVISIBLE);
        }
        holder.rlMain.setOnClickListener(v -> {
            holder.v.setVisibility(View.VISIBLE);
            mAdapterClick.onAdapterClick(position);
            final int adapterPosition = holder.getLayoutPosition();
            for (int i = 0; i < getItemCount(); i++) {
                if (i != adapterPosition) {
                    mList.get(i).v.setVisibility(View.INVISIBLE);
                } else {
                    mList.get(i).v.setVisibility(View.VISIBLE);
                    holder.v.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    @Override
    public int getItemCount() {

        try {
            return mDataList.size();
        } catch (NullPointerException e) {
            return 0;
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_menu)
        TextView tvMenu;
        @Bind(R.id.v)
        View v;
        @Bind(R.id.rl_main)
        RelativeLayout rlMain;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

        }

    }

}
