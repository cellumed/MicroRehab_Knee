package com.cellumed.healthcare.microrehab.knee.Setting;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.DAO.DAO_Program;
import com.cellumed.healthcare.microrehab.knee.DataBase.DBQuery;
import com.cellumed.healthcare.microrehab.knee.Dialog.DialogUserInfoEdit;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;
import com.cellumed.healthcare.microrehab.knee.Util.SqliteToExcel;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class Act_Setting extends BTConnectActivity implements OnAdapterClick {
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.act_setting);
        ButterKnife.bind(this);

        BudUtil.actList.add(this);
        setCustomActionbar();



    }

    @Override
    protected void onDestroy() {
        //RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }



    public void userInfoEdit (View view) {
        new DialogUserInfoEdit(mContext);

    }

    public void dataExtraction (View view) {

        final DBQuery dbQuery = new DBQuery(mContext);

        dbQuery.programRemoveNotComplete();

        final ArrayList<DAO_Program> progList = dbQuery.getALLProgram();

        if (0 == progList.size()) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
            builder
                    .title(getString(R.string.notice))
                    .titleColor(Color.parseColor("#000000"))
                    .backgroundColor(Color.parseColor("#aec7d5"))
                    .content("평가기록에 등록된 내용이 없습니다")
                    .positiveText(getString(R.string.ok))
                    .positiveColor(Color.parseColor("#000000"))
                    .onPositive(((dialog, which) -> {
                        dialog.dismiss();
                    }))
                    .show();
        } else {
            File file1 = Environment.getExternalStorageDirectory();
            File file2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


            SqliteToExcel ste = new SqliteToExcel(this, "ems.db");
            ste.startExportSingleTable("ProgramTable", "ems.xls", new SqliteToExcel.ExportListener() {
                @Override
                public void onStart() {
                    Log.e("Frag_DataExtraction", "onStart");
                }

                @Override
                public void onComplete() {
                    Log.e("Frag_DataExtraction", "onComplete");
                    final String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "ems" + File.separator+"ems.xls";
                    sendEmail(filePath);
                    Toast to=Toast.makeText(mContext, "데이터 저장이 완료되었습니다.", Toast.LENGTH_SHORT);
                    //setGlobalFont(to.getView());
                    to.show();
                }

                @Override
                public void onError() {

                }
            });
        }


    }

    private void sendEmail(String filePath) {

        File file = new File(filePath);
        Uri path = Uri.fromFile(file);

        AccountManager accountManager = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
        final Account account = accountManager.getAccounts()[0];

        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] address = {account.name};

        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("application/excel");
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[EMS] Training Data");
        intent.putExtra(Intent.EXTRA_STREAM, path);

        int requestCode = 1000;

        startActivityForResult(
                Intent.createChooser(intent, "Send mail via..."), requestCode);

    }

    public void systemInit (View view) {
        systemInitDialog();
    }

    public void versionInfo (View view) {

        PackageInfo pi = null;
        try {
            pi = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
        } catch(PackageManager.NameNotFoundException e) {
            Log.e("TAG", "Package Name don't find");
        }

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title(getString(R.string.SystemState))
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content("APP Version:" + pi.versionName + "\nH/W Version  " + BudUtil.getInstance().HWVersion + "\nF/W Version  " + BudUtil.getInstance().FWVersion)
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                });

        //setGlobalFont(builder.build().getCustomView());
        builder.show();


    }

    public  void systemInitDialog(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext);
        builder
                .title(getString(R.string.warning))
                .titleColor(Color.parseColor("#000000"))
                .backgroundColor(Color.parseColor("#aec7d5"))
                .content(mContext.getString(R.string.SystemInit_Msg))
                .positiveText(getString(R.string.ok))
                .positiveColor(Color.parseColor("#000000"))
                .negativeColor(Color.DKGRAY)
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                    String sfName="EMS_USER_INFO";   // shared 저장용
                    SharedPreferences sf = mContext.getSharedPreferences(sfName, Context.MODE_MULTI_PROCESS);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.clear();
                    editor.commit();


                    new  com.cellumed.healthcare.microrehab.knee.DataBase.DataBases(mContext).reset();
                    MaterialDialog.Builder confirm = new MaterialDialog.Builder(mContext);
                    confirm.
                            title("알림")
                            .titleColor(Color.parseColor("#000000"))
                            .backgroundColor(Color.parseColor("#aec7d5"))
                            .content("초기화 되었습니다.")
                            .positiveColor(Color.parseColor("#000000"))
                            .positiveText(getString(R.string.ok))
                            .onPositive((dialog2, which2) -> dialog2.dismiss()).show();
                })  .onNegative((dialog1, which1) -> dialog1.dismiss()
        ).show();
    }

    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {

    }


    @Override
    public void onAdapterClick(int pos) {
    }

    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff6669")));
        ((TextView) findViewById(R.id.custom_name)).setBackground(getResources().getDrawable(R.drawable.title_08));


        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        ImageButton btn = (ImageButton) findViewById(R.id.custom_back_btn);
        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public  void onClick(View v) {
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );

    }

}
