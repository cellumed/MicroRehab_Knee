package com.cellumed.healthcare.microfit.knee.Home;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microfit.knee.DAO.DAO_UserInfo;
import com.cellumed.healthcare.microfit.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microfit.knee.Dialog.CallbackDialog;
import com.cellumed.healthcare.microfit.knee.Dialog.DialogUserInfoEdit;
import com.cellumed.healthcare.microfit.knee.R;
import com.cellumed.healthcare.microfit.knee.Util.BudUtil;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Act_UserInfo extends AppCompatActivity {

    final String TAG = "Act_UserInfo";

    ImageButton backBtn;
    ListView userList;
    View updated;
    UserInfoListViewAdapter userInfoListViewAdapter;
    listAdapter adapter;

    private Context context;
    private String currentUserId = "";

    ArrayList<String> arr_user = new ArrayList<>();
    int position1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_user_info);
        context = this;
        ButterKnife.bind(this);

        setCustomActionbar();

        userList = (ListView)findViewById(R.id.user_list);
        userList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //userList.setOnItemClickListener(onItemClickListener);
        userInfoListViewAdapter = new UserInfoListViewAdapter();

        adapter = new listAdapter(getLayoutInflater(),Act_UserInfo.this,arr_user);
        userList.setAdapter(adapter);

        ArrayList<DAO_UserInfo> user_list = new DBQuery(context).getALLUserInfo();
        for(int i =0;i<user_list.size();i++){
            String msg = user_list.get(i).getName() + "," + user_list.get(i).getGender() + ","+ user_list.get(i).getBirth() +","+user_list.get(i).getLegPart();
            arr_user.add(msg);
        }

        //userList.setAdapter(userInfoListViewAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(ManageDeviceConfiguration.getInstance().getUserId().isEmpty()){
            currentUserId = "";
        } else {
            currentUserId = ManageDeviceConfiguration.getInstance().getUserId();
        }

        //userInfoListViewAdapter.notifyDataSetChanged();
        //loadUserInfoTable();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onTop");
    }

    public void onClickDelete(View v){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder
                .title(getString(R.string.DeleteUser))
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("사용자 정보를 삭제하시겠습니까?")
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .negativeColor(Color.DKGRAY)
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) -> {
                    Log.d("tag","data5 : " + arr_user.size() +" "+userList.getSelectedItemPosition());
                    arr_user.remove(position1);
                    adapter.notifyDataSetChanged();
                    if (new DBQuery(context).deleteUserInfo(currentUserId)) {
                        Log.d(TAG, "Delete UserInfo ID: " + currentUserId);
                    }

                    DAO_UserInfo user = new DAO_UserInfo().init();

                    // SharedPreference Update
                    ManageDeviceConfiguration.getInstance().updateUser(user);

//                    userInfoListViewAdapter.removeItem();
//                    userInfoListViewAdapter.notifyDataSetChanged();
                    //loadUserInfoTable();
                    dialog.dismiss();

                })  .onNegative((dialog1, which1) -> dialog1.dismiss()
        ).show();





    }

    public void onClickAdd(View v) {
        ArrayList<DAO_UserInfo> user_list = new DBQuery(context).getALLUserInfo();
        Log.d("tag","user list size : "+ user_list.size());
        if(user_list.size()<10){
            new DialogUserInfoEdit(this, new CallbackDialog() {
                @Override
                public void onPositive() {
                    currentUserId = ManageDeviceConfiguration.getInstance().getUserId();
                    arr_user.add("111,222,333,444");
                    adapter.notifyDataSetChanged();
                    //loadUserInfoTable();
                }

                @Override
                public void onNegative() {

                }
            });
            //String msg = user_list.get().getName() + "," + user_list.get(i).getGender() + ","+ user_list.get(i).getBirth() +","+user_list.get(i).getLegPart();



        }else{
            Toast.makeText(getApplicationContext(),"사용자 등록은 최대 10명까지 가능합니다.",Toast.LENGTH_LONG).show();
        }




        Log.v(TAG, "onClickAdd");
    }

    public void loadUserInfoTable(){

        // Load DB user info table
        ArrayList<DAO_UserInfo> user_list = new DBQuery(context).getALLUserInfo();
        Log.d(TAG, "User count:" + user_list);

        String msg = "";

        for(int i=0; i<user_list.size(); i++) {
            msg = user_list.get(i).getName() + "," + user_list.get(i).getGender() + ","+ user_list.get(i).getBirth() +","+user_list.get(i).getLegPart();
//            msg += user_list.get(i).getName();
//            msg += "(";
//            msg += user_list.get(i).getGender();
//            msg += ")\n";
//            msg += user_list.get(i).getBirth();
//            msg += ", ";
//            msg += user_list.get(i).getLegPart();
            arr_user.add(msg);
/*
            if( currentUserId.isEmpty()){
                userInfoListViewAdapter.addItem(false, user_list.get(i).getId(), msg);
                Log.v(TAG, "UnSelected User: " + user_list.get(i).getId() + "," + msg);
            } else {

                if (currentUserId.equals(user_list.get(i).getId())) {
                    //userInfoListViewAdapter.addItem(ContextCompat.getDrawable(this,R.mipmap.ic_done), msg);
                    currentUserId = ManageDeviceConfiguration.getInstance().getUserId();
                    userInfoListViewAdapter.addItem(true, user_list.get(i).getId(), msg);
                    Log.v(TAG, "Selected User: " + user_list.get(i).getId() + "," + msg);
                } else {
                    //userInfoListViewAdapter.addItem(null, msg);
                    userInfoListViewAdapter.addItem(false, user_list.get(i).getId(), msg);
                    Log.v(TAG, "UnSelected User: " + user_list.get(i).getId() + "," + msg);
                }
            }
*/
            msg = "";
        }
    }

    public void setCurrentUserId(String userID){

        Log.d(TAG,"setCurrentUserId: " + userID);
        currentUserId = userID;

        DAO_UserInfo userInfo = new DBQuery(context).getUserInfoFromId(currentUserId);

        // SharedPreference 도 저장
        ManageDeviceConfiguration.getInstance().updateUser(userInfo);
        Log.d(TAG, "id:" + ManageDeviceConfiguration.getInstance().getUserId());
        Log.d(TAG, "name" + ManageDeviceConfiguration.getInstance().getUserName());
        Log.d(TAG, "birth" + ManageDeviceConfiguration.getInstance().getUserBirth());
        Log.d(TAG, "gender" + ManageDeviceConfiguration.getInstance().getUserGender());
        Log.d(TAG, "leg" + ManageDeviceConfiguration.getInstance().getUserLegPart());
    }

//    ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Log.d(TAG, "ListView Item Click");
//
//            UserInfoListViewItem item = (UserInfoListViewItem)parent.getAdapter().getItem(position);
//
//            item.setPostion(-1);
//            setCurrentUserId(item.getUserId());
//            //userInfoListViewAdapter.notifyDataSetChanged();
//            Log.d(TAG, "Current UserInfo ID: " + currentUserId);
//        }
//    };

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.title_09);

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

    public class UserInfoListViewItem {

        private int postion;
        private Drawable icon;
        private String content;
        private String userId;
        private boolean visiable;

        public void setPostion(int pos) { this.postion = pos; }
        public void setIcon(Drawable icon) { this.icon = icon; }
        public void setContent(String content) { this.content = content; }
        public void setUserId(String userid) { this.userId = userid; }
        public void setVisiable(boolean visiable) { this.visiable = visiable; };
        public int getPosition() { return postion; }
        public Drawable getIcon() { return icon; }
        public String getContent() { return content; }
        public String getUserId() { return userId; }
        public boolean getVisable(){ return visiable; }
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

//            if(userInfoListViewItem.getPosition() == -1) {
//                icon.setVisibility(View.VISIBLE);
//            } else {
//                icon.setVisibility(View.INVISIBLE);
//            }

            userInfoListViewItem.setPostion(position);

            for(int a=0;a<arr_user.size();a++){
                Log.d("tag","data1 : "+ arr_user.get(a));
                String[] aa = arr_user.get(a).split(",");
                content.setText(aa[0] + "," + aa[1] +",");

            }

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

        public void addItem(boolean visiable, String userId, String content) {
            UserInfoListViewItem item = new UserInfoListViewItem();
            if(visiable) {
                item.setPostion(-1);
            } else {
                item.setPostion(listViewItems.size() + 1);
            }
            item.setUserId(userId);
            item.setContent(content);

            listViewItems.add(item);
        }

        public void removeItem(){
            listViewItems.clear();
        }
    }

    class listAdapter extends BaseAdapter {
        ArrayList<String> data;
        LayoutInflater inflater;
        Context context;

        // 생성자
        public listAdapter(LayoutInflater inflater, Context context, ArrayList<String> data) {
            this.data = data;
            this.inflater = inflater;
            this.context = context;
        }

        // 총 개수를 리턴함.
        @Override
        public int getCount() {
            return data.size();
        }

        // 해당 위치의 object를 리턴함.
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        // 위치값을 리턴함.
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d("aa", "POSITION : " + position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_userinfo_listview, null);
            }
            String[] aaa = arr_user.get(position).split(",");

            TextView content = (TextView) convertView.findViewById(R.id.tvContent);
            ImageView icon = (ImageView)convertView.findViewById(R.id.ivIcon);
            content.setText(aaa[0] + "(" + aaa[1] +")\n" + aaa[2] + " , " + aaa[3]);
            content.setTextColor(Color.WHITE);
            Log.d("tag","data 2 : "+arr_user.get(position));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserInfoListViewItem item = (UserInfoListViewItem)AdapterView.getAdapter().getItem(position);

                    item.setPostion(-1);
                    setCurrentUserId(item.getUserId());

                    position1 = position;
                    for(int a=0;a<data.size();a++){

                        if(a==position){
                            icon.setVisibility(View.VISIBLE);
                            Log.d("tag","data3 : ");
                        }else{
                            icon.setVisibility(View.INVISIBLE);
                            Log.d("tag","data4 : ");
                        }
                    }

                }
            });
            return convertView;

        }
    }
}
