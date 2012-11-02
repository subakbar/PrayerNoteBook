package leeheechul.make.prayernotebook;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditPrayerActivity extends Activity implements OnDateSetListener {
	int m_prayerId;
	int m_year, m_month, m_day;
	long m_startTime;							// 종료를 위해 시작시간을 저장하는 변수
	long m_endTime;								// 종료를 위해 종료시간을 저장하는 변수
	boolean m_isCallPrayerActivity;
	boolean m_isPressedBackButton;
	
	// 그룹 컨텍스트메뉴 관련
	int m_select_group;
	boolean m_isShowGroup;
	ArrayAdapter<String> m_groupList;
	
	EditText m_target;
	Button m_dateButton;
	Button m_groupButton;
	EditText m_title;
	EditText m_answer;
	EditText m_word;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_prayer);
        
        // 윗젠 초기화
        m_target = (EditText)findViewById(R.id.eidt_prayer_target);
        m_dateButton = (Button)findViewById(R.id.eidt_prayer_date);
        m_groupButton = (Button)findViewById(R.id.eidt_prayer_group);
        m_title = (EditText)findViewById(R.id.edit_prayer_title);
        m_answer = (EditText)findViewById(R.id.edit_prayer_answer);
        m_word = (EditText)findViewById(R.id.edit_prayer_word);
        registerForContextMenu(m_groupButton);

        // 인텐트에서 정보가져오기
        Intent intent = getIntent();
        m_prayerId = intent.getIntExtra("prayerId", 0);
        m_isCallPrayerActivity = intent.getBooleanExtra("isCallPrayerActivity", false);
        String groupName = intent.getStringExtra("groupName");
        
        PrayerSQLite prayerSqlite = new PrayerSQLite(this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	Cursor cursor;
    	
    	// 데이터 초기화
    	m_groupList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
    	m_startTime = System.currentTimeMillis();
    	
    	cursor = db.rawQuery("SELECT target, date, group_id, title, answer, word FROM prayer WHERE _id = '" + m_prayerId + "';", null);
    	cursor.moveToNext();
    	
    	m_target.setText(cursor.getString(0));
    	m_dateButton.setText(cursor.getString(1));
    	int groupId = cursor.getInt(2);
    	m_title.setText(cursor.getString(3));
    	m_answer.setText(cursor.getString(4));
    	m_word.setText(cursor.getString(5));
    	cursor.close();
    	
    	if ((groupName.toLowerCase().equals("ungroup")))
    		m_groupButton.setText("Ungroup");
    	else if ((groupName.toLowerCase().equals("all"))) {
    		cursor = db.rawQuery("SELECT name FROM groupInfo WHERE _id = " + groupId + ";", null);
    		// null이 아닌 경우, Ungroup이다. 
    		if (cursor.getCount() == 0)
    			m_groupButton.setText("Ungroup"); 
    		else {
	    		cursor.moveToNext();
	    		m_groupButton.setText(cursor.getString(0));
    		}
    		cursor.close();
    	}
    	else
    		m_groupButton.setText(groupName);
        
    	db.close();
    	
    	// date 정보를 year, month, day로 분리하기
    	String strDate = m_dateButton.getText().toString();
    	m_year = Integer.parseInt(strDate.substring(0, 4));
    	m_month = Integer.parseInt(strDate.substring(5, 7));
    	if (strDate.length() == 10)
    		m_day = Integer.parseInt(strDate.substring(8, 10));
    	else
    		m_day = Integer.parseInt(strDate.substring(8, 9));
	}
	
	
	@Override
    public void onStart() {
    	super.onStart();
    	
    	// 비밀번호설정이 ture이고, 백그라운드 복귀시 비밀번호액티비티를 실행한다.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("EditPrayerActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// 현재 액티비티를 저장한다.
			app.setPreActivity("EditPrayerActivity");
    	}
    }
	
	
	/*****
	 * 
	 * 					Button Method
	 * 
	 *****/
	// cancelButton
	public void cancelButton(View view) {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	
	// removeButton
	public void removeButton(View view) {
		// GroupListActivity에서 호출시 GroupListActivity를 종료한다.
		if (m_isCallPrayerActivity == true) {
			PrayerActivity prayerActivity = (PrayerActivity)PrayerActivity.m_activity;
			prayerActivity.finish();
		}
		
		// DB에서 삭제하기
		PrayerSQLite prayerSqlite = new PrayerSQLite(EditPrayerActivity.this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	
    	db.execSQL("DELETE FROM prayer WHERE _id = " + m_prayerId + ";");
		
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	
	// okButton
	public void okButton(View view) {
		// DB새그룹명을 저장한다.
		PrayerSQLite prayerSqlite = new PrayerSQLite(EditPrayerActivity.this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	
    	String groupName = m_groupButton.getText().toString();
    	
		Cursor cursor;
        cursor = db.rawQuery("SELECT _id FROM groupInfo where name = '" + groupName + "';", null);
        
     // 1) ungroup인 경우
        if (groupName.toLowerCase().equals("ungroup")) {
			cursor.close();
			
			// prayer에 업데이트하기
			db.execSQL("UPDATE prayer SET " +
			                 "target = '" + m_target.getText().toString() + "', " +
			                 "date = '" + m_dateButton.getText().toString() + "', " +
			                 "group_id = null, " +
			                 "title = '" + m_title.getText().toString() + "', " +
			                 "answer = '" + m_answer.getText().toString() + "', " +
			                 "word = '" + m_word.getText().toString() + "' " +
			                 "WHERE _id = " + m_prayerId + ";");
        	
        }
        // 2) 새그룹이 없으면 새그룹을 추가하고, prayer을 추가한다.
        else if (cursor.getCount() == 0) {
			// 새그룹 추가하기
			db.execSQL("INSERT INTO groupInfo (name) VALUES ('" + groupName + "');");
			
			// 새그룹 ID구하기
			cursor.close();
			cursor = db.rawQuery("SELECT _id FROM groupInfo where name = '" + groupName + "';", null);
			cursor.moveToNext();
			int groupId = cursor.getInt(0);
			cursor.close();
			
			// prayer에 업데이트하기
			db.execSQL("UPDATE prayer SET " +
			                 "target = '" + m_target.getText().toString() + "', " +
			                 "date = '" + m_dateButton.getText().toString() + "', " +
			                 "group_id = " + groupId + ", " +
			                 "title = '" + m_title.getText().toString() + "', " +
			                 "answer = '" + m_answer.getText().toString() + "', " +
			                 "word = '" + m_word.getText().toString() + "' " +
			                 "WHERE _id = " + m_prayerId + ";");
		}
		// 3) 새그룹이 있으면 prayer만 추가한다.
		else {
			cursor.moveToNext();
			int groupId = cursor.getInt(0);
			cursor.close();
			
			// prayer에 업데이트하기
			db.execSQL("UPDATE prayer SET " +
			                 "target = '" + m_target.getText().toString() + "', " +
			                 "date = '" + m_dateButton.getText().toString() + "', " +
			                 "group_id = " + groupId + ", " +
			                 "title = '" + m_title.getText().toString() + "', " +
			                 "answer = '" + m_answer.getText().toString() + "', " +
			                 "word = '" + m_word.getText().toString() + "' " +
			                 "WHERE _id = " + m_prayerId + ";");
		}
		db.close();
		
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	// dateButton
	public void setDate(View view) {
		new DatePickerDialog(this, this, m_year, m_month - 1, m_day).show();
	}
	
	public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		m_year = year;
		m_month = monthOfYear + 1;
		m_day = dayOfMonth;
		
		DecimalFormat df  = new DecimalFormat("00");
        String month = df.format(m_month);
        String day = df.format(m_day);
		
		m_dateButton.setText(m_year + "/" + month + "/" + day);
	}
	
	// groupButton
	public void setGroup(View view) {
		m_isShowGroup = true;
		
		openContextMenu(view);
	}
	
	/*****
	 * 
	 * 					ContextMenu
	 * 
	 *****/
    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);   
    	
    	if (m_isShowGroup == true) {
    		// 키보드 가리기
    		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	    	// DB에서 그룹명 가져오기
			PrayerSQLite prayerSqlite = new PrayerSQLite(this);
	    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
	    	Cursor cursor;
	    	
	    	cursor = db.rawQuery("SELECT name FROM groupInfo;", null);
	    	
	    	m_groupList.clear();
	    	m_groupList.add("Ungroup");
	    	m_groupList.add("New Group");
	    	while(cursor.moveToNext())
	    		m_groupList.add(cursor.getString(0));
	    	cursor.close();
	
		    menu.setHeaderTitle(R.string.prayer_group);
		    
		    for (int i = 0; i < m_groupList.getCount(); i++)
		    	menu.add(0, i, 0, m_groupList.getItem(i).toString());
		  
		    m_isShowGroup = false;
    	}
    }
    
    
    public boolean onContextItemSelected (MenuItem item) {
    	int position = item.getItemId();
    	
    	if (position == 1) {
    		// 대화상자에 사용될 레이아웃을 지정한다.
        	final LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.alert_dialog_add_group, null);
    		
        	// 대화상자 생성 및 출력하기
        	AlertDialog alertDialog = new AlertDialog.Builder(this)
        	.setTitle(R.string.dialog_add_group_title)
        	.setView(linear)
        	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				EditText editText = (EditText)linear.findViewById(R.id.add_group_name_edit_text);	
    				String newGroupName = editText.getText().toString();
    				
    				// DB에 검색하여 all, ungroup, 기존의 그룹명이 있으면 토스트를 띄운다.
    				PrayerSQLite prayerSqlite = new PrayerSQLite(EditPrayerActivity.this);
    		    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    				Cursor cursor;
    		        cursor = db.rawQuery("SELECT name FROM groupInfo where name = '" + newGroupName + "';", null);
    		        
    		        // DB에 새그룹이 없고, all, ungroup이 아닐 경우 실행
    				if (cursor.getCount() == 0 && !(newGroupName.toLowerCase().equals("all")) && !(newGroupName.toLowerCase().equals("ungroup"))) {
    					m_groupButton.setText(newGroupName);
    				}
    				// DB에 새그룹이 있으면 토스트로 실패를 알린다.
    				else
    					Toast.makeText(EditPrayerActivity.this, R.string.dialog_add_fail, Toast.LENGTH_SHORT).show();
    				
    				cursor.close();
    				db.close();
    				
    				// 키보드 가리기
    	    		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
    	    		inputManager.hideSoftInputFromWindow(EditPrayerActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    			}
    		})
    		.setNegativeButton(R.string.button_cancel, null)
    		.show();
        	
        	// EditText 키보드 출력하기
        	alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	}
    	else 
    		m_groupButton.setText(m_groupList.getItem(position));
    	
    	return false;
    }
    
    
    /******
     * 
     * 					Option Menu Method
     * 
     ******/
    
    // 액티비티생성후 처음 한번실행되는 콜백메서드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 101, 0, R.string.Context_delete);
    	
        return true;
    }
    
    
    // menu와 context menu 아이템 핸들러 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	// 삭제하기
    	case 101 :
    		// GroupListActivity에서 호출시 GroupListActivity를 종료한다.
    		if (m_isCallPrayerActivity == true) {
    			PrayerActivity prayerActivity = (PrayerActivity)PrayerActivity.m_activity;
    			prayerActivity.finish();
    		}
    		
    		// DB에서 삭제하기
    		PrayerSQLite prayerSqlite = new PrayerSQLite(EditPrayerActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        	
        	db.execSQL("DELETE FROM prayer WHERE _id = " + m_prayerId + ";");
    		
    		finish();
    		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    		
    		return true;
    	}
    	
    	return false;
    }
    
    
    /*****
     * 
     * 					Listener Method
     * 
     *****/
    // 하드웨어 백버튼
    @Override
    public void onBackPressed() {
		m_endTime = System.currentTimeMillis();
		
		if (m_endTime - m_startTime > 2000)
			m_isPressedBackButton = false;
		
		if (m_isPressedBackButton == false) {
			m_isPressedBackButton = true;
			
			m_startTime = System.currentTimeMillis();
			
			Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 취소 됩니다.", Toast.LENGTH_SHORT).show();
		}
		else {
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
    }
}