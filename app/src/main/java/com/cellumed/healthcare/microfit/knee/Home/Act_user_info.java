package com.cellumed.healthcare.microfit.knee.Home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cellumed.healthcare.microfit.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.knee.Dialog.DialogUserInfoEdit;
import com.cellumed.healthcare.microfit.knee.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Act_user_info extends AppCompatActivity {

    final String TAG = "Act_user_info";

    //@Bind(R.id.bt_check_user);
    ImageButton btCheck;

    ImageButton backBtn;
    ListView userList;
    UserInfoListViewAdapter userInfoListViewAdapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_user_info);
        context = this;
        ButterKnife.bind(this);

        setCustomActionbar();

        userList = (ListView)findViewById(R.id.user_list);
        userList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        userInfoListViewAdapter = new UserInfoListViewAdapter();

        userList.setAdapter(userInfoListViewAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Load DB user info table
        ArrayList<String> user_list = new DBQuery(context).getALLUserInfo();
        Log.v(TAG, "User count:" + user_list.size());

        for(int i=0; i<user_list.size(); i++) {
            if (ManageDeviceConfiguration.getInstance().getUserId().equals(user_list.get(i))) {
                userInfoListViewAdapter.addItem(ContextCompat.getDrawable(this,R.mipmap.ic_done), "selected user");
                Log.v(TAG, "selected user");
            } else {
                userInfoListViewAdapter.addItem(null, "non selected user");
                Log.v(TAG, "none selected user");
            }
        }
    }

    public void onClickDelete(View v){

    }

    public void onClickAdd(View v){
        new DialogUserInfoEdit(this);
    }

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_05);


        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        backBtn = (ImageButton) findViewById(R.id.custom_back_btn);
        backBtn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );

    }

    public class UserInfoListViewItem{

        private Drawable icon;
        private String content;

        public void setIcon(Drawable icon) { this.icon = icon; }
        public void setContent(String content) { this.content = content; }
        public Drawable getIcon() { return icon; }
        public String getContent() { return content; }
    }

    public class UserInfoListViewAdapter extends BaseAdapter {
        private ArrayList<UserInfoListViewItem> listViewItems = new ArrayList<UserInfoListViewItem>();

        @Override
        public int getCount(){
            return listViewItems.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            final int pos = position;
            final Context context = parent.getContext();

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.custom_userinfo_listview, parent, false);
            }

            ImageView icon = (ImageView)convertView.findViewById(R.id.ivIcon);
            TextView content = (TextView)convertView.findViewById(R.id.tvContent);

            UserInfoListViewItem userInfoListViewItem = listViewItems.get(position);

            icon.setImageDrawable(userInfoListViewItem.getIcon());
            content.setText(userInfoListViewItem.getContent());

            return convertView;
        }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public Object getItem(int position) { return listViewItems.get(position); }

        public void addItem(Drawable icon, String content) {
            UserInfoListViewItem item = new UserInfoListViewItem();
            item.setIcon(icon);
            item.setContent(content);

            listViewItems.add(item);
        }
    }
}
