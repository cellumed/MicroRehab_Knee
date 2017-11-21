package com.cellumed.healthcare.microrehab.knee.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class SqliteToExcel{

	private Context mContext;
	private SQLiteDatabase database;
	private String mDbName;
	private ExportListener mListener;
	private String mExportPath;
	
	private final static int MESSAGE_START = 0;
	private final static int MESSAGE_COMPLETE = 1;
	private final static int MESSAGE_ERROR = 2;
	

	public SqliteToExcel(Context context,String dbName){
		mContext = context;
		mDbName = dbName;
//		final  File file = new File("/storage/usbcard1/EMS");
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File rootDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			Log.e("마운트", rootDirectory.getAbsolutePath());
		}else{
			Log.e("낫마운트", Environment.getRootDirectory().getAbsolutePath());
		}
		mExportPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+File.separator+"ems"+File.separator;

		File temp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "ems");
		if (!temp.exists()) {
			temp.mkdir();
		}

//		mExportPath = file.getPath()+File.separator;
		Log.d("getPath", Environment.getExternalStorageDirectory().getPath());
		Log.e("mExportPath", mExportPath);


		try {
			database = SQLiteDatabase.openOrCreateDatabase(mContext.getDatabasePath(mDbName).getAbsolutePath(), null);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "데이터 또는 외장메모리가 없습니다.", Toast.LENGTH_SHORT).show();
		}

	}

	public SqliteToExcel(Context context,String dbName,String exportPath){
		mContext = context;
		mDbName = dbName;
		mExportPath = exportPath;

		try {
			database = SQLiteDatabase.openOrCreateDatabase(mContext.getDatabasePath(mDbName).getAbsolutePath(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private ArrayList<String> getAllTables(){
		ArrayList<String> tables=new ArrayList<String>();
		Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null); 
		while(cursor.moveToNext()){  
			tables.add(cursor.getString(0));
	    }
		cursor.close();
		return tables;
	}
	

	private ArrayList<String> getColumns(String table){
		ArrayList<String> columns=new ArrayList<String>();
		Cursor cursor = database.rawQuery("PRAGMA table_info("+table+")", null);
		while(cursor.moveToNext()){  
			columns.add(cursor.getString(1));
	    }
		cursor.close();
		return columns;
	}

	public String getColumnName(int i)
	{
		String empty="";
		if(i==0) return new String("번호"); 		//0        + "idx Integer PRIMARY KEY AUTOINCREMENT,"
		if(i==1) return new String("사전수행시간");//	+  PreTime+" TEXT  ," // in sec
		if(i==2) return new String("사전무릎굴곡");//	+PreAngleMin+" TEXT  ,"
		if(i==3) return new String("사전무릎신전");//	+PreAngleMax+" TEXT  ,"
		if(i==4) return new String("CH1평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==5) return new String("CH1최대");//		+PreEmgMax+" TEXT  ,"
		if(i==6) return new String("CH1토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==7) return new String("CH2평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==8) return new String("CH2최대");//		+PreEmgMax+" TEXT  ,"
		if(i==9) return new String("CH2토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==10) return new String("CH3평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==11) return new String("CH3최대");//		+PreEmgMax+" TEXT  ,"
		if(i==12) return new String("CH3토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==13) return new String("CH4평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==14) return new String("CH4최대");//		+PreEmgMax+" TEXT  ,"
		if(i==15) return new String("CH4토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==16) return new String("CH5평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==17) return new String("CH5최대");//		+PreEmgMax+" TEXT  ,"
		if(i==18) return new String("CH5토탈");//		+PreEmgTotal+" TEXT  ,"

		if(i==19) return new String("사후수행시간");//	+  PreTime+" TEXT  ," // in sec
		if(i==20) return new String("사후무릎굴곡");//	+PreAngleMin+" TEXT  ,"
		if(i==21) return new String("사후무릎신전");//	+PreAngleMax+" TEXT  ,"
		if(i==22) return new String("CH1평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==23) return new String("CH1최대");//		+PreEmgMax+" TEXT  ,"
		if(i==24) return new String("CH1토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==25) return new String("CH2평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==26) return new String("CH2최대");//		+PreEmgMax+" TEXT  ,"
		if(i==27) return new String("CH2토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==28) return new String("CH3평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==29) return new String("CH3최대");//		+PreEmgMax+" TEXT  ,"
		if(i==30) return new String("CH3토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==31) return new String("CH4평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==32) return new String("CH4최대");//		+PreEmgMax+" TEXT  ,"
		if(i==33) return new String("CH4토탈");//		+PreEmgTotal+" TEXT  ,"
		if(i==34) return new String("CH5평균");//		+PreEmgAvr+" TEXT  ,"
		if(i==35) return new String("CH5최대");//		+PreEmgMax+" TEXT  ,"
		if(i==36) return new String("CH5토탈");//		+PreEmgTotal+" TEXT  ,"

		if(i==37) return new String("프로그램타입");//37		+ ProgramType +" TEXT  ,"
		if(i==38) return new String("상태");	//		+ ProgramState +" TEXT,"
		if(i==39) return new String("시작시간");//			+ ProgramStartDate +" TEXT,"
		if(i==40) return new String("종료시간");//			+ ProgramEndDate +" TEXT,"\
		if(i==41) return new String("신호타입"); //			+ ProgramSignalType +" TEXT,"
		if(i==42) return new String("프로그램");//			+ ProgramName +" TEXT  ,"
		if(i>=43) return empty;
		// 아래는 모든 재활이 동일함
// 			+ ProgramTime+" TEXT,"
//			+ ProgramTimeProgress+" TEXT,"
//			+ ProgramFrequency+" TEXT,"
//			+ ProgramFrequencyProgress+" TEXT,"
//			+ ProgramPulseOperationTime+" TEXT,"
//			+ ProgramPulseOperationTimeProgress+" TEXT,"
//			+ ProgramPulsePauseTime+" TEXT,"
//			+ ProgramPulsePauseTimeProgress+" TEXT,"
//			+ ProgramPulseRiseTime+" TEXT,"
//			+ ProgramPulseRiseTimeProgress+" TEXT,"
//			+ ProgramPulseWidth+" TEXT,"
//			+ ProgramPulseWidthProgress+" TEXT );";
		return empty;
	}


	// check skip idx in program table
	public boolean skip_idx(int i)
	{
		//0        + "idx Integer PRIMARY KEY AUTOINCREMENT,"
		//	+  PreTime+" TEXT  ," // in sec
		//	+PreAngleMin+" TEXT  ,"
		//	+PreAngleMax+" TEXT  ,"
		//		+PreEmgAvr+" TEXT  ,"
		//		+PreEmgMax+" TEXT  ,"
		//		+PreEmgTotal+" TEXT  ,"
		//		+PreEmgAvr2+" TEXT  ,"
		//		+PreEmgMax2+" TEXT  ,"
		//		+PreEmgTotal2+" TEXT  ,"
		//10		+PreEmgAvr3+" TEXT  ,"
		//		+PreEmgMax3+" TEXT  ,"
		//		+PreEmgTotal3+" TEXT  ,"
		//		+PreEmgAvr4+" TEXT  ,"
		//		+PreEmgMax4+" TEXT  ,"
		//		+PreEmgTotal4+" TEXT  ,"
		//		+PreEmgAvr5+" TEXT  ,"
		//		+PreEmgMax5+" TEXT  ,"
		//		+PreEmgTotal5+" TEXT  ,"

		//19		+PostTime+" TEXT  ," // in sec
		//		+PostAngleMax+" TEXT  ,"
		//		+PostAngleMin+" TEXT  ,"
		//		+PostEmgAvr+" TEXT  ,"
		//		+PostEmgMax+" TEXT  ,"
		//		+PostEmgTotal+" TEXT  ,"
		//		+PostEmgAvr2+" TEXT  ,"
		//		+PostEmgMax2+" TEXT  ,"
		//		+PostEmgTotal2+" TEXT  ,"
		//		+PostEmgAvr3+" TEXT  ,"
		//29		+PostEmgMax3+" TEXT  ,"
		//		+PostEmgTotal3+" TEXT  ,"
		//		+PostEmgAvr4+" TEXT  ,"
		//		+PostEmgMax4+" TEXT  ,"
		//		+PostEmgTotal4+" TEXT  ,"
		//		+PostEmgAvr5+" TEXT  ,"
		//		+PostEmgMax5+" TEXT  ,"
		//		+PostEmgTotal5+" TEXT  ,"

		if(i==37) return true;	//37		+ ProgramType +" TEXT  ,"
		if(i==38) return true;	//		+ ProgramState +" TEXT,"
//			+ ProgramStartDate +" TEXT,"
//			+ ProgramEndDate +" TEXT,"\
		if(i==41) return true;//			+ ProgramSignalType +" TEXT,"
//			+ ProgramName +" TEXT  ,"
		if(i>=43) return true;
		// 아래는 모든 재활이 동일함
// 			+ ProgramTime+" TEXT,"
//			+ ProgramTimeProgress+" TEXT,"
//			+ ProgramFrequency+" TEXT,"
//			+ ProgramFrequencyProgress+" TEXT,"
//			+ ProgramPulseOperationTime+" TEXT,"
//			+ ProgramPulseOperationTimeProgress+" TEXT,"
//			+ ProgramPulsePauseTime+" TEXT,"
//			+ ProgramPulsePauseTimeProgress+" TEXT,"
//			+ ProgramPulseRiseTime+" TEXT,"
//			+ ProgramPulseRiseTimeProgress+" TEXT,"
//			+ ProgramPulseWidth+" TEXT,"
//			+ ProgramPulseWidthProgress+" TEXT );";
		return false;
	}

	private void exportItems(String table,String fileName){
		mHandler.sendEmptyMessage(MESSAGE_START);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(table);
		createSheet(table, sheet);
		FileOutputStream fos = null;
		try {
			File file = new File(mExportPath, fileName);

			fos = new FileOutputStream(file);
			workbook.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MESSAGE_ERROR);
		}finally{
			if (fos != null)
			{
				try
				{
					fos.flush();
					fos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					mHandler.sendEmptyMessage(MESSAGE_ERROR);
				}
			}
		}
		try {
			workbook.close();
			mHandler.sendEmptyMessage(MESSAGE_COMPLETE);
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MESSAGE_ERROR);
		}
	}



	private void exportAllItems(String fileName){
		mHandler.sendEmptyMessage(MESSAGE_START);
		ArrayList<String> tables = getAllTables();
		HSSFWorkbook workbook = new HSSFWorkbook();
		for(int i = 0;i<tables.size();i++){
			HSSFSheet sheet = workbook.createSheet(tables.get(i));
			createSheet(tables.get(i), sheet);
		}
		FileOutputStream fos = null;
		try {
			File file = new File(fileName);
			fos = new FileOutputStream(file);
			workbook.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MESSAGE_ERROR);
		}finally{
			if (fos != null) 
            {
                try 
                {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MESSAGE_ERROR);
                }
            }
		}
		try {
			workbook.close();
			mHandler.sendEmptyMessage(MESSAGE_COMPLETE);
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MESSAGE_ERROR);
		}
	}
	

	public void startExportSingleTable(final String table, final String fileName, ExportListener listener){
		mListener = listener;
		new Thread(new Runnable() {
			@Override
			public void run() {
				exportItems(table, fileName);
			}
		}).start();
	}

	public void startExportAllTables(final String fileName,ExportListener listener){
		mListener = listener;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				exportAllItems(fileName);
			}
		}).start();
	}
	

	private void createSheet(String table,HSSFSheet sheet){
		HSSFRow rowA = sheet.createRow(0);
		ArrayList<String> columns = getColumns(table);
		int c=0;

		// 번호
		HSSFCell cellA = rowA.createCell(c);
		cellA.setCellValue(new HSSFRichTextString("" + getColumnName(0)));
		c++;

		// 42
		cellA = rowA.createCell(c);
		cellA.setCellValue(new HSSFRichTextString("" + getColumnName(42)));
		c++;

		// 39 start
		cellA = rowA.createCell(c);
		cellA.setCellValue(new HSSFRichTextString("" + getColumnName(39)));
		c++;
		// 40 end
		cellA = rowA.createCell(c);
		cellA.setCellValue(new HSSFRichTextString("" + getColumnName(40)));
		c++;



		//for(int i = 0; i<columns.size();i++){
		for(int i = 1; i<37;i++){
			if(!skip_idx(i)) {
				cellA = rowA.createCell(c);
				cellA.setCellValue(new HSSFRichTextString("" + getColumnName(i)));
				c++;
			}
		}
		insertItemToSheet(table, sheet, columns);
	}
	

	private void insertItemToSheet(String table,HSSFSheet sheet,ArrayList<String> columns){
		Cursor cursor = database.rawQuery("select * from "+table, null);
		cursor.moveToFirst();
		int n=1;
		int c=0;
		while(!cursor.isAfterLast())
		{
			HSSFRow rowA = sheet.createRow(n);
			c=0;

			// 번호
			HSSFCell cellA = rowA.createCell(c);
			cellA.setCellValue(new HSSFRichTextString(Integer.toString(n)));
			c++;

			// 42
			cellA = rowA.createCell(c);
			cellA.setCellValue(new HSSFRichTextString(cursor.getString(42)));
			c++;

			// 39 start
			cellA = rowA.createCell(c);
			cellA.setCellValue(new HSSFRichTextString(cursor.getString(39)));
			c++;
			// 40 end
			cellA = rowA.createCell(c);
			cellA.setCellValue(new HSSFRichTextString(cursor.getString(40)));
			c++;

			for(int j=1;j<37;j++){
				//db중 skip할 column선택
				if(!skip_idx(j)) {
					cellA = rowA.createCell(c);
					cellA.setCellValue(new HSSFRichTextString(cursor.getString(j)));
					c++;
				}
			}
			n++;
			cursor.moveToNext();
		}
		cursor.close();
	}
	

	public interface ExportListener{
		void onStart();
		void onComplete();
		void onError();
	}

	private Handler mHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            int msgId = msg.what;  
            switch (msgId) {  
                case MESSAGE_START:
                	mListener.onStart();
                    break;    
                case MESSAGE_COMPLETE:
                	mListener.onComplete();
                	break;
                case MESSAGE_ERROR:
                	mListener.onError();
                	break;
            }  
        }  
    };
}
