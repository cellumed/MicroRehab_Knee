package com.cellumed.healthcare.microrehab.knee.Home;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.BTConnectActivity;
import com.cellumed.healthcare.microrehab.knee.Bluetooth.IMP_CMD;
import com.cellumed.healthcare.microrehab.knee.DataBase.SqlImp;
import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

@TargetApi(18)
public class Act_admin_emg extends BTConnectActivity  implements IMP_CMD, SqlImp {

    @Bind(R.id.cb_ct1)
    CheckBox cbCt1;
    @Bind(R.id.cb_ct2)
    CheckBox cbCt2;
    @Bind(R.id.cb_ct3)
    CheckBox cbCt3;
    @Bind(R.id.cb_ct4)
    CheckBox cbCt4;
    @Bind(R.id.cb_ct5)
    CheckBox cbCt5;
    @Bind(R.id.img_ct1)
    ImageView imgCt1;
    @Bind(R.id.img_ct2)
    ImageView imgCt2;
    @Bind(R.id.img_ct3)
    ImageView imgCt3;
    @Bind(R.id.img_ct4)
    ImageView imgCt4;
    @Bind(R.id.img_ct5)
    ImageView imgCt5;

    private Context mContext;
    private boolean type;

    int[] maxY= new int[] {9999,9999,9999,9999,9999};
    int maxYLimit = 9999;
    int maxNow=9999;

    private BackPressCloseHandler backPressCloseHandler;
    private LineChart emg_chart;

    int[] mColors = ColorTemplate.VORDIPLOM_COLORS;

    String prev_data_recv="";

    int data_cnt=0; // debug

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_admin_emg);

        CheckBox[] cb_list = { cbCt1, cbCt2, cbCt3, cbCt4, cbCt5};
        setTitle("");
        ButterKnife.bind(this);
        mContext = this;
        BudUtil.actList.add(this);

        setCustomActionbar();

        backPressCloseHandler = new BackPressCloseHandler(this);


        // chart setup
        emg_chart=(LineChart)findViewById(R.id.emgchart);
        emg_chart.getDescription().setEnabled(false);
        emg_chart.setNoDataTextTypeface(typeface);

        emg_chart.getLegend().setTypeface(typeface);
        emg_chart.getLegend().setEnabled(false);
        /*
        List<Entry> v1 = new ArrayList<Entry>();
        List<Entry> v2 = new ArrayList<Entry>();
        List<Entry> v3 = new ArrayList<Entry>();
        List<Entry> v4 = new ArrayList<Entry>();
        List<Entry> v5 = new ArrayList<Entry>();

        //set value
        v1.add(new Entry(100.0f,0));
        v1.add(new Entry(70.0f,1));
        v1.add(new Entry(80.0f,2));

        v2.add(new Entry(10.0f,0));
        v2.add(new Entry(30.0f,1));
        v2.add(new Entry(50.0f,2));

        LineDataSet s1 = new LineDataSet(v1, "VV1");
        s1.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet s2 = new LineDataSet(v2, "VV2");
        s2.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(s1);
        dataSets.add(s2);
*/
        // set x val

        //
        XAxis xAxis = emg_chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(9f);
        xAxis.setDrawAxisLine(true);
        xAxis.setTypeface(typeface);

        // y축은 최소값 0, 최대값은 ch3의 경우가 0.0002736으로
        // 다른 채널도 0.0002정도 수준.
        // uV단위니까 파라미터 측정 데이터가 e-06정도이므로 *1
        // 즉 0~ 275정도까지로 선정
        YAxis leftAxis = emg_chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawLabels(true); // no axis labels
        //leftAxis.setDrawAxisLine(true); // no axis line
       // leftAxis.setDrawGridLines(true); // no grid lines
        leftAxis.setTypeface(typeface);
        leftAxis.setTextSize(12f); // set the text size
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0.0f); // start at zero
        //leftAxis.setAxisMaximum(100f); // the axis maximum is 100
        leftAxis.setAxisMaximum(9999f); // the axis maximum is 100
        //
        // leftAxis.setGranularity(2f); // interval 1

        emg_chart.getAxisRight().setEnabled(false); // no right axis

        // empty data object
        LineData data=new LineData();
        data.setValueTypeface(typeface);
        emg_chart.setData(data);
        emg_chart.invalidate();




        LineData ldata = emg_chart.getData();
        ldata.setValueTypeface(typeface);
        for (int i = 0; i < 5; i++) {
            ILineDataSet set = ldata.getDataSetByIndex(i);

            if (set == null) {
                set = createSet(i);
                ldata.addDataSet(set);
                set.setHighlightEnabled(false);
                set.setValueTypeface(typeface);

            }
            ldata.addEntry(new Entry(ldata.getDataSetByIndex(i).getEntryCount(), (int)0), i);

        }

        cbCt1.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton comBtn, boolean b)
                {
                    ILineDataSet lset = ldata.getDataSetByIndex(0);
                    if(lset!=null) {

                        if (cbCt1.isChecked())
                        {
                            lset.setVisible(true);
                        }
                        else
                        {
                            lset.setVisible(false);
                        }
                            // recalculate y axis.
                        // back to static limit to 9999
                        /*
                        if (cbCt1.isChecked())
                        {
                            lset.setVisible(true);
                            if(maxNow < maxY[0])
                            {
                                YAxis leftAxis = emg_chart.getAxisLeft();
                                leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                            }

                        }
                        else
                        {
                            lset.setVisible(false);
                            if(maxNow == maxY[0])
                            {
                                maxNow=100;
                                if(cbCt1.isChecked() && maxNow < maxY[0]) maxNow=maxY[0];
                                if(cbCt1.isChecked() && maxNow < maxY[1]) maxNow=maxY[1];
                                if(cbCt1.isChecked() && maxNow < maxY[2]) maxNow=maxY[2];
                                if(cbCt1.isChecked() && maxNow < maxY[3]) maxNow=maxY[3];
                                if(cbCt1.isChecked() && maxNow < maxY[4]) maxNow=maxY[4];

                                YAxis leftAxis = emg_chart.getAxisLeft();
                                leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                            }
                        }
                        */
                        emg_chart.notifyDataSetChanged();
                        emg_chart.invalidate();
                    }
                }
            });

        imgCt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbCt1.toggle();
            }
        });

        cbCt2.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton comBtn, boolean b)
            {
                ILineDataSet lset = ldata.getDataSetByIndex(1);
                if(lset!=null) {
                    if (cbCt2.isChecked())
                    {
                        lset.setVisible(true);
                    }
                    else
                    {
                        lset.setVisible(false);
                    }
                    // recalculate y axis.
                    // back to static limit to 9999
                        /*

                    if (cbCt2.isChecked())
                    {
                        lset.setVisible(true);
                        if(maxNow < maxY[1])
                        {
                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }

                    }
                    else
                    {
                        lset.setVisible(false);
                        if(maxNow == maxY[1])
                        {
                            maxNow=100;
                            if(cbCt1.isChecked() && maxNow < maxY[0]) maxNow=maxY[0];
                            if(cbCt1.isChecked() && maxNow < maxY[1]) maxNow=maxY[1];
                            if(cbCt1.isChecked() && maxNow < maxY[2]) maxNow=maxY[2];
                            if(cbCt1.isChecked() && maxNow < maxY[3]) maxNow=maxY[3];
                            if(cbCt1.isChecked() && maxNow < maxY[4]) maxNow=maxY[4];

                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }
                    }
                    */
                    emg_chart.notifyDataSetChanged();
                    emg_chart.invalidate();
                 }
            }
        });

        imgCt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbCt2.toggle();
            }
        });

        cbCt3.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton comBtn, boolean b)
            {
                ILineDataSet lset = ldata.getDataSetByIndex(2);
                if(lset!=null) {
                    if (cbCt3.isChecked())
                    {
                        lset.setVisible(true);
                    }
                    else
                    {
                        lset.setVisible(false);
                    }
                    /*
                    if (cbCt3.isChecked())
                    {
                        lset.setVisible(true);
                        if(maxNow < maxY[2])
                        {
                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }

                    }
                    else
                    {
                        lset.setVisible(false);
                        if(maxNow == maxY[2])
                        {
                            maxNow=100;
                            if(cbCt1.isChecked() && maxNow < maxY[0]) maxNow=maxY[0];
                            if(cbCt1.isChecked() && maxNow < maxY[1]) maxNow=maxY[1];
                            if(cbCt1.isChecked() && maxNow < maxY[2]) maxNow=maxY[2];
                            if(cbCt1.isChecked() && maxNow < maxY[3]) maxNow=maxY[3];
                            if(cbCt1.isChecked() && maxNow < maxY[4]) maxNow=maxY[4];

                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }
                    }
                    */
                    emg_chart.notifyDataSetChanged();
                    emg_chart.invalidate();
                }
            }
        });

        imgCt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbCt3.toggle();
            }
        });

        cbCt4.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton comBtn, boolean b)
            {
                ILineDataSet lset = ldata.getDataSetByIndex(3);
                if(lset!=null) {
                    if (cbCt4.isChecked())
                    {
                        lset.setVisible(true);
                    }
                    else
                    {
                        lset.setVisible(false);
                    }
                    //Log.e("TAG","cb4 "+ cbCt4.isChecked());
                    // recalculate y axis.
                    // back to static limit to 9999
                        /*

                    if (cbCt4.isChecked())
                    {
                        lset.setVisible(true);
                        if(maxNow < maxY[3])
                        {
                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }

                    }
                    else
                    {
                        lset.setVisible(false);
                        if(maxNow == maxY[3])
                        {
                            maxNow=100;
                            if(cbCt1.isChecked() && maxNow < maxY[0]) maxNow=maxY[0];
                            if(cbCt1.isChecked() && maxNow < maxY[1]) maxNow=maxY[1];
                            if(cbCt1.isChecked() && maxNow < maxY[2]) maxNow=maxY[2];
                            if(cbCt1.isChecked() && maxNow < maxY[3]) maxNow=maxY[3];
                            if(cbCt1.isChecked() && maxNow < maxY[4]) maxNow=maxY[4];

                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }
                    }
                    */
                    emg_chart.notifyDataSetChanged();
                    emg_chart.invalidate();
                }
            }
        });

        imgCt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbCt4.toggle();
            }
        });

        cbCt5.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton comBtn, boolean b)
            {
                ILineDataSet lset = ldata.getDataSetByIndex(4);
                if(lset!=null) {
                    if (cbCt5.isChecked())
                    {
                        lset.setVisible(true);
                    }
                    else
                    {
                        lset.setVisible(false);
                    }
                    //Log.e("TAG","cb5 "+ cbCt5.isChecked());
                    // recalculate y axis.
                    // back to static limit to 9999
                        /*

                    if (cbCt5.isChecked())
                    {
                        lset.setVisible(true);
                        if(maxNow < maxY[4])
                        {
                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }

                    }
                    else
                    {
                        lset.setVisible(false);
                        if(maxNow == maxY[4])
                        {
                            maxNow=100;
                            if(cbCt1.isChecked() && maxNow < maxY[0]) maxNow=maxY[0];
                            if(cbCt1.isChecked() && maxNow < maxY[1]) maxNow=maxY[1];
                            if(cbCt1.isChecked() && maxNow < maxY[2]) maxNow=maxY[2];
                            if(cbCt1.isChecked() && maxNow < maxY[3]) maxNow=maxY[3];
                            if(cbCt1.isChecked() && maxNow < maxY[4]) maxNow=maxY[4];

                            YAxis leftAxis = emg_chart.getAxisLeft();
                            leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                        }
                    }
                    */
                    emg_chart.notifyDataSetChanged();
                    emg_chart.invalidate();
                }
            }
        });

        imgCt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbCt5.toggle();
            }
        });


        //dummy
/*
           for (int i = 0; i < 500; i++) {
                   ILineDataSet set = ldata.getDataSetByIndex(i%5);

                    if (set == null) {
                        set = createSet(i%5);
                        ldata.addDataSet(set);
                        set.setHighlightEnabled(false);

                    }

                   // String k = "122";

                   ldata.addEntry(new Entry(ldata.getDataSetByIndex(i%5).getEntryCount(), (int)(Math.random()*270 )), i%5);
                    ldata.notifyDataChanged();

               }
*/
//==

        cbCt1.setChecked(true);
        cbCt2.setChecked(true);
        cbCt3.setChecked(true);
        cbCt4.setChecked(true);
        cbCt5.setChecked(true);

        // let the chart know it's data has changed
        emg_chart.notifyDataSetChanged();

        emg_chart.setVisibleXRangeMaximum(60);
        //mChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
        //
        //            // this automatically refreshes the chart (calls invalidate())
        emg_chart.moveViewTo(ldata.getEntryCount() - 7, 50f, AxisDependency.LEFT);


    }

    @Override
    public void onResume() {

        super.onResume();
        if(mBluetoothConnectService!=null) {
            mBluetoothConnectService.send(CMD_START_SENS, "00020001");    // resp=ack?, type=raw, rehab_type=xx, sens_type= emg
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothConnectService.send(CMD_STOP_SENS, "");
    }

    @Override
    protected void connected_callback()
    {
        if(mBluetoothConnectService!=null) {
            mBluetoothConnectService.send(CMD_START_SENS, "00020001");    // resp=ack?, type=raw, rehab_type=xx, sens_type= emg
        }
    }


    private LineDataSet createSet(int i) {

        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        //set.setCircleRadius(0f);
        if(i==0)  set.setColor(Color.rgb(240, 99, 99));
        else if(i==1) set.setColor(Color.rgb(99, 240, 99));
        else if(i==2) set.setColor(Color.rgb(99, 99, 240));
        else if(i==3) set.setColor(Color.rgb(99, 99, 99));
        else if(i==4) set.setColor(Color.rgb(240, 240, 240));

        set.setDrawValues(false);
      //  set.setCircleColor(Color.rgb(240, 99, 99));
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(AxisDependency.LEFT);
        //set.setValueTextSize(10f);

        return set;
    }


    @Override
    public void onBackPressed() {
        mBluetoothConnectService.send(CMD_STOP_SENS, "");
        super.onBackPressed();

    }



    @Override
    protected void connectedDevice() {}

    @Override
    protected void dataAvailableCheck(String data) {


        String[] sp_new=data.split(" ");
        if(sp_new.length!=20 || sp_new[0].equals("21")!=true || sp_new[19].equals("75")!=true ) {
            Log.e("BLE","Pkt dropped. s= "+data);
            return;
        }

        String cmd=sp_new[3];
        if(cmd.length()==1) cmd = "0" + cmd;

        CheckBox cb=cbCt1;
        int interval;

        if (cmd.equals(CMD_RESP_RAW_SENS)) {

            LineData ldata = emg_chart.getData();

            for (int i = 0; i < 5; i++) {
                if(i==0) cb=cbCt1;
                else if(i==1) cb=cbCt2;
                else if(i==2) cb=cbCt3;
                else if(i==3) cb=cbCt4;
                else if(i==4) cb=cbCt5;

                //if (cb.isChecked()) {
                // 체크여부 상관없이 기록
                ILineDataSet set = ldata.getDataSetByIndex(i);

                if (set == null) {
                    set = createSet(i);
                    ldata.addDataSet(set);
                    set.setValueTypeface(typeface);

                }

                String back=sp_new[6+i*2];
                if(back.length()==1) back="0"+back;
                String k = sp_new[5+i*2]  + back;

                Log.e("TAG","h="+k + " dint="+Integer.parseInt(k,16));
                ldata.addEntry(new Entry(ldata.getDataSetByIndex(i).getEntryCount(), Integer.parseInt(k,16)), i);
                ldata.notifyDataChanged();

                // no need to recalculate y axis.
                // back to static limit to 9999
                     /*

                if(maxY[i] < Integer.parseInt(k,16))
                {
                    maxY[i] = Integer.parseInt(k,16);
                    if(maxY[i] > maxYLimit) maxY[i]=maxYLimit;

                    if(cb.isChecked() && maxNow < maxY[i]) {
                        maxNow=maxY[i];
                        YAxis leftAxis = emg_chart.getAxisLeft();
                        leftAxis.setAxisMaximum(maxNow); // the axis maximum is 100
                    }
                }*/

                data_cnt++;
                Log.w("Data","RCV = "+ data_cnt);

                //}
            }



            // let the chart know it's data has changed
            emg_chart.notifyDataSetChanged();

            emg_chart.setVisibleXRangeMaximum(60);
            //mChart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
            //
            //            // this automatically refreshes the chart (calls invalidate())
            emg_chart.moveViewTo(ldata.getEntryCount() - 7, 50f, AxisDependency.LEFT);
        }

    }


    private void setCustomActionbar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        //((ImageButton) findViewById(R.id.custom_back_btn)).setBackground(null);
        //((ImageButton) findViewById(R.id.custom_back_btn)).setEnabled(false);
        ((TextView) findViewById(R.id.custom_name)).setBackgroundResource(R.drawable.widetxt_01);



        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);

        ImageButton btn = (ImageButton) findViewById(R.id.custom_back_btn);
        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBluetoothConnectService.send(CMD_STOP_SENS, "");
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
        );
    }

}
