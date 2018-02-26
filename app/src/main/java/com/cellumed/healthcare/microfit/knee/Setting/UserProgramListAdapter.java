package com.cellumed.healthcare.microfit.knee.Setting;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by test on 2016-11-06.
 */

public class UserProgramListAdapter extends BaseAdapter{

    private ArrayList<UserProgramListItem> listViewItemList = new ArrayList<UserProgramListItem>();
    private OnItemValueChangedListener listener;

    public UserProgramListAdapter() {

    }

    public UserProgramListAdapter(OnItemValueChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final ViewGroup par = parent;

        Button select = null;
        TextView title = null;
        TextView date = null;
        TextView programName = null;
        TextView userName = null;
        CustomHolder holder = null;
/*
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_program_list_item, parent, false);

            select = (Button) convertView.findViewById(R.id.selectButton);
            title = (TextView) convertView.findViewById(R.id.titleName);
            date = (TextView) convertView.findViewById(R.id.dateName);
            programName = (TextView) convertView.findViewById(R.id.programName);
            userName = (TextView) convertView.findViewById(R.id.userName);

            holder = new CustomHolder();
            holder.selectView = select;
            holder.titleView = title;
            holder.dateView = date;
            holder.programNameView = programName;
            holder.userNameView = userName;

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
            select = holder.selectView;
            title = holder.titleView;
            date = holder.dateView;
            programName = holder.programNameView;
            userName = holder.userNameView;

        }
*/
        title.setText(listViewItemList.get(position).getTitleString());
        date.setText(listViewItemList.get(position).getDateString());
        programName.setText(listViewItemList.get(position).getProgramNameString());
        userName.setText(listViewItemList.get(position).getUserNameString());
        return convertView;
    }

    public void addItem(String title, String date, String program, String user) {
        UserProgramListItem item = new UserProgramListItem();
        item.setTitleString(title);
        item.setDateString(date);
        item.setProgramNameString(program);
        item.setUserNameString(user);
        listViewItemList.add(item);
    }

    public void deleteItem(int position) {
        listViewItemList.remove(position);
        notifyDataSetChanged();
    }

    private class CustomHolder {
        Button selectView;
        TextView titleView;
        TextView dateView;
        TextView programNameView;
        TextView userNameView;
    }
    public interface OnItemValueChangedListener {
        void onItemValueChanged();
    }

    public void clear() {
        listViewItemList.clear();
    }
}