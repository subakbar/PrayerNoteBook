package leeheechul.make.prayernotebook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PrayerActivity extends Activity {
	
	int m_prayerId;
	String m_groupName;
	public static Activity m_activity;
	
	TextView m_activityTitle;
	EditText m_target;
	Button m_dateButton;
	Button m_groupButton;
	EditText m_title;
	EditText m_answer;
	EditText m_word;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);
        
        // 윗젠 초기화
        m_activityTitle = (TextView)findViewById(R.id.prayer_activity_title);
        m_target = (EditText)findViewById(R.id.prayer_target);
        m_dateButton = (Button)findViewById(R.id.prayer_date);
        m_groupButton = (Button)findViewById(R.id.prayer_group);
        m_title = (EditText)findViewById(R.id.prayer_title);
        m_answer = (EditText)findViewById(R.id.prayer_answer);
        m_word = (EditText)findViewById(R.id.prayer_word);
        
        // 인텐트에서 정보가져오기
        Intent intent = getIntent();
        m_prayerId = intent.getIntExtra("prayerId", 0);
        m_groupName = intent.getStringExtra("groupName");
        
        // 액티비티 타이틀 변경
        m_activityTitle.setText(m_groupName);
        
        // 멤버변수 초기화
        m_activity = this;
	}
	
	
	@Override
    public void onStart() {
    	super.onStart();
    	
    	// 비밀번호설정이 ture이고, 백그라운드 복귀시 비밀번호액티비티를 실행한다.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("PrayerActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// 현재 액티비티를 저장한다.
			app.setPreActivity("PrayerActivity");
    	}
    }
	
	
	// 액티비티가 포그라운될때마다 setAdapter를 통해 데이터를 초기화한다.
    @Override
    public void onResume() {
    	super.onResume();

    	setPrayer();
    }
    
    public void setPrayer() {
    	PrayerSQLite prayerSqlite = new PrayerSQLite(this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	Cursor cursor;
    	
    	
    	// Prayer 정보
    	cursor = db.rawQuery("SELECT target, date, group_id, title, answer, word FROM prayer WHERE _id = '" + m_prayerId + "';", null);
    	cursor.moveToNext();
    	
    	int group_id = cursor.getInt(2);
    	m_target.setText(cursor.getString(0));
    	m_dateButton.setText(cursor.getString(1));
    	m_title.setText(cursor.getString(3));
    	m_answer.setText(cursor.getString(4));
    	m_word.setText(cursor.getString(5));
    	cursor.close();
    	
    	// 그룹 이름 찾기
    	cursor = db.rawQuery("SELECT name FROM groupInfo WHERE _id = " + group_id + ";", null);
    	
    	// 그룹 이름이 없으면, ungroup임을 나타낸다.
    	if (cursor.getCount() == 0) {
    		m_groupButton.setText("Ungroup");
    	}
    	else {
	    	cursor.moveToNext();
	    	m_groupName = cursor.getString(0);
	    	m_groupButton.setText(m_groupName);
    	}
    	cursor.close();
    	db.close();
    }
	
    
	/*****
	 * 
	 * 					Button Method
	 * 
	 *****/
	public void backButton(View view) {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	public void editButton(View view) {
		Intent intent = new Intent(this, EditPrayerActivity.class);
		intent.putExtra("prayerId", m_prayerId);
		intent.putExtra("groupName", m_groupName);
		// editGroup에서 prayer삭제시 PrayerActivity를 종료한다.
		intent.putExtra("isCallPrayerActivity", true);			
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
    	menu.add(0, 102, 0, R.string.Context_edit);
    	
        return true;
    }
    
    
    // menu와 context menu 아이템 핸들러 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	// 삭제하기
    	case 101 :
    		// DB에서 삭제하기
    		PrayerSQLite prayerSqlite = new PrayerSQLite(PrayerActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        	
        	db.execSQL("DELETE FROM prayer WHERE _id = " + m_prayerId + ";");
    		
    		finish();
    		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    		
    		return true;
    	// 편집하기
    	case 102 :
    		Intent intent = new Intent(this, EditPrayerActivity.class);
    		intent.putExtra("prayerId", m_prayerId);
    		intent.putExtra("groupName", m_groupName);
    		// editGroup에서 prayer삭제시 PrayerActivity를 종료한다.
    		intent.putExtra("isCallPrayerActivity", true);			
    		
    		startActivity(intent);
    		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    		
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
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	
    }
}