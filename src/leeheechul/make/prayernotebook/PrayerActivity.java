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
        
        // ���� �ʱ�ȭ
        m_activityTitle = (TextView)findViewById(R.id.prayer_activity_title);
        m_target = (EditText)findViewById(R.id.prayer_target);
        m_dateButton = (Button)findViewById(R.id.prayer_date);
        m_groupButton = (Button)findViewById(R.id.prayer_group);
        m_title = (EditText)findViewById(R.id.prayer_title);
        m_answer = (EditText)findViewById(R.id.prayer_answer);
        m_word = (EditText)findViewById(R.id.prayer_word);
        
        // ����Ʈ���� ������������
        Intent intent = getIntent();
        m_prayerId = intent.getIntExtra("prayerId", 0);
        m_groupName = intent.getStringExtra("groupName");
        
        // ��Ƽ��Ƽ Ÿ��Ʋ ����
        m_activityTitle.setText(m_groupName);
        
        // ������� �ʱ�ȭ
        m_activity = this;
	}
	
	
	@Override
    public void onStart() {
    	super.onStart();
    	
    	// ��й�ȣ������ ture�̰�, ��׶��� ���ͽ� ��й�ȣ��Ƽ��Ƽ�� �����Ѵ�.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("PrayerActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// ���� ��Ƽ��Ƽ�� �����Ѵ�.
			app.setPreActivity("PrayerActivity");
    	}
    }
	
	
	// ��Ƽ��Ƽ�� ���׶��ɶ����� setAdapter�� ���� �����͸� �ʱ�ȭ�Ѵ�.
    @Override
    public void onResume() {
    	super.onResume();

    	setPrayer();
    }
    
    public void setPrayer() {
    	PrayerSQLite prayerSqlite = new PrayerSQLite(this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	Cursor cursor;
    	
    	
    	// Prayer ����
    	cursor = db.rawQuery("SELECT target, date, group_id, title, answer, word FROM prayer WHERE _id = '" + m_prayerId + "';", null);
    	cursor.moveToNext();
    	
    	int group_id = cursor.getInt(2);
    	m_target.setText(cursor.getString(0));
    	m_dateButton.setText(cursor.getString(1));
    	m_title.setText(cursor.getString(3));
    	m_answer.setText(cursor.getString(4));
    	m_word.setText(cursor.getString(5));
    	cursor.close();
    	
    	// �׷� �̸� ã��
    	cursor = db.rawQuery("SELECT name FROM groupInfo WHERE _id = " + group_id + ";", null);
    	
    	// �׷� �̸��� ������, ungroup���� ��Ÿ����.
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
		// editGroup���� prayer������ PrayerActivity�� �����Ѵ�.
		intent.putExtra("isCallPrayerActivity", true);			
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	/******
     * 
     * 					Option Menu Method
     * 
     ******/
    
    // ��Ƽ��Ƽ������ ó�� �ѹ�����Ǵ� �ݹ�޼���
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 101, 0, R.string.Context_delete);
    	menu.add(0, 102, 0, R.string.Context_edit);
    	
        return true;
    }
    
    
    // menu�� context menu ������ �ڵ鷯 �޼���
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	// �����ϱ�
    	case 101 :
    		// DB���� �����ϱ�
    		PrayerSQLite prayerSqlite = new PrayerSQLite(PrayerActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        	
        	db.execSQL("DELETE FROM prayer WHERE _id = " + m_prayerId + ";");
    		
    		finish();
    		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    		
    		return true;
    	// �����ϱ�
    	case 102 :
    		Intent intent = new Intent(this, EditPrayerActivity.class);
    		intent.putExtra("prayerId", m_prayerId);
    		intent.putExtra("groupName", m_groupName);
    		// editGroup���� prayer������ PrayerActivity�� �����Ѵ�.
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
	// �ϵ���� ���ư
    @Override
    public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	
    }
}