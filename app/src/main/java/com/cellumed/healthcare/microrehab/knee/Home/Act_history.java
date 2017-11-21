package com.cellumed.healthcare.microrehab.knee.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microrehab.knee.DAO.DAO_Program;
import com.cellumed.healthcare.microrehab.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.Dialog.DialogRehabInfo;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Act_history extends BTConnectActivity implements  IMP_CMD, SqlImp {
    @Bind(R.id.bt_data_extraction)
    ImageButton bt_ext;

    ImageButton backBtn;
    private Context mContext;


    private int mCheckedLog =0;   // checked
    private String selectedTimeName="";
    private String selectedTime="";

    private ProgressDialog dialog;

    private HistoryListAdapter mHistoryListAdapter;

    ListView historyList;
    View updated;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.act_history);
        ButterKnife.bind(this);
        BudUtil.actList.add(this);
        setCustomActionbar();


        historyList = (ListView) findViewById(R.id.history_list);
        historyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        historyList.setOnItemClickListener(tempHistory);
        bt_ext.setOnClickListener(extractHistory);

    }


    @Override
    protected void onResume() {
        int resId;
        super.onResume();

        mHistoryListAdapter = new HistoryListAdapter();
        History_List_View_Item n;
        final DBQuery dbQuery = new DBQuery(mContext);

        dbQuery.programRemoveNotComplete();

        final ArrayList<DAO_Program> progList = dbQuery.getALLProgram();
        int i;
        for (i = 0; i < progList.size(); i++) {
            n= new History_List_View_Item();
            n.setHistoryName(progList.get(i).getProgramName());
            n.setHistoryTime(progList.get(i).getProgramStartDate());
            mHistoryListAdapter.addHistory(n);
        }

        historyList.setAdapter( mHistoryListAdapter);

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

    ListView.OnItemClickListener tempHistory = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final History_List_View_Item hh = mHistoryListAdapter.getHistory(position);
            if (hh == null) {
                return;
            }

            if(updated==null)   // first time
            {
                //bt_ext.setBackgroundResource(R.drawable.btn_extract);
            }
            else
                updated.setBackgroundColor(Color.TRANSPARENT);

            updated = view;
            selectedTimeName = hh.getHistoryTimeName();
            selectedTime = hh.getHistoryTime();
            Log.e("TAG", hh.getHistoryTimeName());
            view.setBackgroundColor(Color.GRAY);
        }
    };

    public void checkPopup () {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(getString(R.string.SystemState))
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("먼저 평가기록을 선택해주세요.")
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {

                    dialog.dismiss();


                }).show();
    }
    Button.OnClickListener extractHistory = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (selectedTimeName == "") {
                Log.e("TAG", "Not selected");
                checkPopup();
                return;
            }

            // 데이터 추출
           if(selectedTime.length()!=0)
           {
               final DBQuery dbQuery = new DBQuery(mContext);
               new DialogRehabInfo(mContext, dbQuery.getProgramFromStartTime(selectedTime)).show();

           }

             Log.e("TAG", "extract " + deviceAddress);

        }
    };

    @Override
    protected void connectedDevice() {}

    @Override
    protected void onPause() {
        super.onPause();
    }




    @Override
    protected void dataAvailableCheck(String data) {

        Log.d("TAG","DATA recved act activity");

        String cmd=data.split(" ")[3];
        if(cmd.length()==1) cmd = "0" + cmd;


    }


    public class History_List_View_Item  {
        private String historyTime;
        private String historyName;

        private boolean touched = false;
        private Handler handler = new Handler();

        public synchronized boolean getTouched() { return touched;}
        public synchronized void setTouched(boolean b) { touched = b; }

        public Handler getHandler() { return handler; }

        public void setHistoryTime(String t) {
            historyTime = t;
        }

        public void setHistoryName(String n) {
            historyName = n;
        }

        public String getHistoryTimeName()
        {
            return historyTime + " " + historyName;
        }

        public String getHistoryTime()
        {
            return historyTime;
        }

    }

    private class HistoryListAdapter extends BaseAdapter {

        private ArrayList<History_List_View_Item> mHistories;
        private LayoutInflater mInflater;

        public HistoryListAdapter() {
            super();
            mHistories = new ArrayList<History_List_View_Item>();
            mInflater = Act_history.this.getLayoutInflater();
        }

        public void addHistory(History_List_View_Item hh) {
            if (!mHistories.contains(hh)) {
                mHistories.add(hh);
            }
        }

        public History_List_View_Item getHistory(int position) {
            return mHistories.get(position);
        }

        public void clear() {
            mHistories.clear();
        }

        @Override
        public int getCount() {
            return mHistories.size();
        }

        @Override
        public Object getItem(int position) {
            return mHistories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Act_history.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_history, null);
                viewHolder = new Act_history.ViewHolder();
                viewHolder.historyName = (TextView) convertView.findViewById(R.id.history_timename);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (Act_history.ViewHolder) convertView.getTag();
            }

            History_List_View_Item hh = mHistories.get(position);
            final String historyTimeName = hh.getHistoryTimeName();
            if (historyTimeName != null && historyTimeName.length() > 0)
                viewHolder.historyName.setText(historyTimeName);
            else
                viewHolder.historyName.setText("Unknown");  //error

            return convertView;
        }

    }

    static class ViewHolder {
        TextView historyName;
    }


}
