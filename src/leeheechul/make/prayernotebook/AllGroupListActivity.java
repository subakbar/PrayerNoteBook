package leeheechul.make.prayernotebook;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AllGroupListActivity extends Activity implements OnItemClickListener {
	
	int m_position;								// ListView에 선택된 아이템의 위치를 공유하는 변수
	long m_startTime;							// 종료를 위해 시작시간을 저장하는 변수
	long m_endTime;								// 종료를 위해 종료시간을 저장하는 변수
	boolean m_isPressedBackButton;		// 백버튼이 눌러졌는지 확인하는 변수
	AllGroupListAdapter m_adapter;			// ListView에 사용될 Adapter변수
	
	// 윗젠 변수
	Button m_cancelButton;
	Button m_okButton;
	Button m_addButton;
	TextView m_title;
	TextView m_selectTitle;
	ListView m_listView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_group_list);
        
        // 윗젠 초기화
        m_cancelButton = (Button)findViewById(R.id.all_group_list_activity_cancel_button);
        m_okButton = (Button)findViewById(R.id.all_group_list_activity_ok_botton);
        m_addButton = (Button)findViewById(R.id.all_group_list_activity_add_group_botton);
        m_title = (TextView)findViewById(R.id.all_group_list_activity_title);
        m_selectTitle = (TextView)findViewById(R.id.all_group_list_activity_title_select_mode);
        m_listView = (ListView)findViewById(R.id.all_group_list_activity_list);
        
        // 액션바 '취소', '확인'버튼 숨기기
        m_cancelButton.setVisibility(View.GONE);
        m_okButton.setVisibility(View.GONE);
        
        // 액션바의 타이틀의 위치를 변경한다.
        m_selectTitle.setVisibility(View.GONE);
        
        // 리스트 선택시 지정된 색상으로 변경한다.
        m_listView.setSelector(R.drawable.my_selector);
        
        // 리스트 리스터 초기화
        m_listView.setOnItemClickListener(this);
        registerForContextMenu(m_listView);
        
        // 멤버변수 초기화
        m_adapter = new AllGroupListAdapter(this);
        m_isPressedBackButton = false;
        m_startTime = System.currentTimeMillis();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	// 비밀번호설정이 ture이고, 백그라운드 복귀시 비밀번호액티비티를 실행한다.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("AllGroupListActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// 현재 액티비티를 저장한다.
			app.setPreActivity("AllGroupListActivity");
    	}
    }
 
    
    // 액티비티가 포그라운될때마다 setAdapter를 통해 데이터를 초기화한다.
    @Override
    public void onResume() {
    	super.onResume();

    	setAdapter();
    }
    
    
    // DB에서 adapter정보를 초기화한다.
    public void setAdapter() {
    	PrayerSQLite prayerSqlite = new PrayerSQLite(this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	ArrayList<AllGroupListItem> arrayListItem = new ArrayList<AllGroupListItem>();
        Cursor cursor;
        AllGroupListItem listItem;
        
        // DB에서 All_count를 구한다.
        cursor = db.rawQuery("SELECT count(_id) FROM prayer;", null);
        cursor.moveToNext();
        listItem = new AllGroupListItem("All", cursor.getString(0));
        arrayListItem.add(listItem);
        cursor.close();
        
        // DB에서 Ungroup_count를 구한다.
        cursor = db.rawQuery("SELECT count(_id) FROM prayer WHERE group_id IS NULL;", null);
        cursor.moveToNext();
        listItem = new AllGroupListItem("Ungroup", cursor.getString(0));
        arrayListItem.add(listItem);
        cursor.close();
    	
        // DB에서 그룹정보를 가져온다.
        cursor = db.rawQuery("SELECT name, (SELECT count(group_id) FROM prayer where group_id = groupInfo._id) count " +
        		                      "FROM groupInfo " +
        		                      "ORDER BY name;", null);
        
        // cursor의 그룹정보를 arrayList로 옮긴다.
        while (cursor.moveToNext()) {
        	listItem = new AllGroupListItem(cursor.getString(0), cursor.getString(1));
        	arrayListItem.add(listItem);
        }
        cursor.close();
        db.close();

        // adapter를 설정하기
        m_adapter.setArrayList(arrayListItem);
        
        // ListView에 adapter 설정하기
        m_listView.setAdapter(m_adapter);

        m_adapter.notifyDataSetChanged();
    }
    
    
    /*****
     * 
     * 					Button Method
     * 
     *****/
    // 추가버튼 클릭시 호출되는 메서드
    public void addButton(View view) {
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
				
				// DB에서 중복되는 새그룹명이 존재하는지 확인한다.
				PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
		    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
				Cursor cursor;
		        cursor = db.rawQuery("SELECT name FROM groupInfo where name = '" + newGroupName + "';", null);
		        
		        // DB에 새그룹이 없고, all, ungroup이 아닐경우 실행
		        if (cursor.getCount() == 0 && !(newGroupName.toLowerCase().equals("all")) && !(newGroupName.toLowerCase().equals("ungroup"))) {
					db.execSQL("INSERT INTO groupInfo (name) VALUES ('" + newGroupName + "');");
					
					// 데이터베이스 갱신
					setAdapter();
				}
				// DB에 새그룹이 있으면 토스트로 실패를 알린다.
				else
					Toast.makeText(AllGroupListActivity.this, R.string.dialog_add_fail, Toast.LENGTH_SHORT).show();
				cursor.close();
				db.close();
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
    	
    	// EditText 키보드 출력하기
    	alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
    
    public void cancelButton(View view) {
    	// 액션바에 '취소', '확인', '추가' 버튼의 클릭핸들러를 막는다.
    	m_cancelButton.setClickable(false);
    	m_okButton.setClickable(false);
    	m_addButton.setClickable(false);
        
        // 아이템의 checked를 해지한다.
        m_adapter.setAllCheckedFalse();
        
        // ListView를 선택모드로 변경한다.
		m_adapter.setSelectMode(false);
		m_adapter.notifyDataSetChanged();
		
		// 액션바의 버튼 비쥬얼을 바꾸고, 클릭핸들러를 허용한다.
		m_cancelButton.setVisibility(View.GONE);
		m_cancelButton.setClickable(true);
		m_okButton.setVisibility(View.GONE);
		m_okButton.setClickable(true);
		m_addButton.setVisibility(View.VISIBLE);
		m_addButton.setClickable(true);
		
		// 액션바의 타이틀의 위치를 변경한다.
        m_title.setVisibility(View.VISIBLE);
        m_selectTitle.setVisibility(View.GONE);
    }
    
    
    public void okButton(View view) {
    	// 액션바에 '취소', '확인', '추가' 버튼의 클릭핸들러를 막는다.
    	m_cancelButton.setClickable(false);
    	m_okButton.setClickable(false);
    	m_addButton.setClickable(false);
        
        // 선택된 아이템을 삭제한다.
        m_adapter.removeAllCheckListItem();
        
        // ListView를 선택모드로 변경한다.
 		m_adapter.setSelectMode(false);
 		
 		// adapter를 업데이트한다.
 		setAdapter();
 		
 		// 액션바의 버튼 비쥬얼을 바꾸고, 클릭핸들러를 허용한다.
 		m_cancelButton.setVisibility(View.GONE);
 		m_cancelButton.setClickable(true);
 		m_okButton.setVisibility(View.GONE);
 		m_okButton.setClickable(true);
 		m_addButton.setVisibility(View.VISIBLE);
 		m_addButton.setClickable(true);
 		
 		// 액션바의 타이틀의 위치를 변경한다.
 		m_title.setVisibility(View.VISIBLE);
 		m_selectTitle.setVisibility(View.GONE);
    }
    
    
    /******
     * 
     *					Listener Method 
     * 
     ******/
    
    // OnItemClickListener.onItemClick : Listview 탭시 호출되는 메서드
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	// 선택 삭제시 아래 코드를 활성화한다.
    	if (m_adapter.getIsSelectMode() == true) {
    		m_adapter.setCheckedChange(position);
    		m_adapter.notifyDataSetChanged();
    	}
    	// 아이템 선택시 해당 그룹으로 이동한다.
    	else {
    		AllGroupListItem listItem = (AllGroupListItem)parent.getItemAtPosition(position);
    		
    		Intent intent = new Intent(this, GroupListActivity.class);
    		intent.putExtra("groupName", listItem.m_leftText);
    		
    		startActivity(intent);
    		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    	}
    }
    
    // 하드웨어 백버튼
    @Override
    public void onBackPressed() {
    	if (m_adapter.getIsSelectMode() == true)
    		cancelButton(null);
    	else {
    		m_endTime = System.currentTimeMillis();
    		
    		if (m_endTime - m_startTime > 2000)
    			m_isPressedBackButton = false;
    		
    		if (m_isPressedBackButton == false) {
    			m_isPressedBackButton = true;
    			
    			m_startTime = System.currentTimeMillis();
    			
    			Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료 됩니다.", Toast.LENGTH_SHORT).show();
    		}
    		else {
    			finish();
	    		System.exit(0);
	    		android.os.Process.killProcess(android.os.Process.myPid());
    		}
    	}
    }
    

    
    /******
     * 
     * 					Menu Method
     * 
     ******/
    
    // 액티비티생성후 처음 한번실행되는 콜백메서드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 101, 0, R.string.menu_settings);
    	menu.add(0, 102, 0, R.string.menu_select_delete);
    	
        return true;
    }
    
    // menu호출시 매 호출되는 메서드
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	// 선택모드일때 메뉴를 숨긴다.
    	if (m_adapter.getIsSelectMode() == true)
    		menu.setGroupVisible(0, false);
    	else
    		menu.setGroupVisible(0, true);
    	
    	return true;
    }
    
    
    // menu와 context menu 아이템 핸들러 메서드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	// 환경설정
    	case 101 :
    		Intent intent = new Intent(this, SettingActivity.class);
    		
    		startActivity(intent);
    		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    		
    		return true;
    	// 선택삭제
    	case 102 :
    		// ListView를 선택모드로 변경한다.
    		m_adapter.setSelectMode(true);
    		m_adapter.notifyDataSetChanged();
    		
    		// 액션바에 '취소', '확인'버튼을 보이게하고, '그룹추가'버튼을 숨긴다.
    		m_cancelButton.setVisibility(View.VISIBLE);
    		m_okButton.setVisibility(View.VISIBLE);
    		m_addButton.setVisibility(View.GONE);
            // 액션바의 타이틀의 위치를 변경한다.
            m_title.setVisibility(View.GONE);
            m_selectTitle.setVisibility(View.VISIBLE);
    		
    		return true;
    	}
    	
    	return false;
    }
    
    
    /******
     * 
     * 					ContextMenu Method
     * 
     ******/
    // ContextMenu호출시 콜백함수로 계속 호출된다.
    // menu와 달리 Preapare 콜백함수가 존재하지 않는다.
    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);    	
    	
    	// 선택모드가 아닐때 컨텍스트뷰를 실행한다.
    	if (m_adapter.getIsSelectMode() == false) {
	    	int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
	    	AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(position);
	    	menu.setHeaderTitle(listItem.m_leftText);
	    	
	    	// ListView의 아이템 0, 1이 아닐때 실행된다.
	    	if (position == 0 || position == 1) {
	        	menu.add(0, 201, 0, R.string.context_select);
	        	menu.add(0, 204, 0, R.string.Context_cancel);
	    	}
	    	else {
		    	menu.add(0, 201, 0, R.string.context_select);
		    	menu.add(0, 202, 0, R.string.Context_edit);
		    	menu.add(0, 203, 0, R.string.Context_delete);
		    	menu.add(0, 204, 0, R.string.Context_cancel);
			}
    	}
    }
    
    
    public boolean onContextItemSelected (MenuItem item) {
    	// m_position 정보를 editGorupName()과 deleteGroup()에서 사용된다.
    	AdapterView.AdapterContextMenuInfo adapterInfo;
    	adapterInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	m_position = adapterInfo.position;

    	switch (item.getItemId()) {
    	// 선택
    	case 201 :
    		// gorupActivity로 이동
    		return true;
    	// 수정
    	case 202 :
    		editGroupName();
    		return true;
    	// 삭제
    	case 203 :
    		deleteGroup();
    		return true;
    	// 취소
    	case 204 :
    		// 끝 ㅋㅋㅋ 
    		return true;
    	}
    	
    	return false;
    }
    
    // ContextMenu의 edit아이템 선택시 호출되는 메서드
    public void editGroupName() {
    	// 대화상자에 사용될 레이아웃을 지정한다.
    	final LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.alert_dialog_add_group, null);
    	EditText editText = (EditText)linear.findViewById(R.id.add_group_name_edit_text);
    	AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(m_position);
		editText.setHint(listItem.m_leftText);
    	// 대화상자 생성 및 출력하기
    	AlertDialog alertDialog = new AlertDialog.Builder(this)
    	.setTitle(getString(R.string.dialog_edit_group_title))
    	.setView(linear)
    	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText editText = (EditText)linear.findViewById(R.id.add_group_name_edit_text);
				String newGroupName = editText.getText().toString();
				
				PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
		    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();				
		    	Cursor cursor;
		        
		    	cursor = db.rawQuery("SELECT name FROM groupInfo where name = '" + newGroupName + "';", null);
		    	
		        // DB에 새그룹명이 없으면 새그룹명으로 변경한다.
				if (cursor.getCount() == 0) {
					// DB에서 gorup_id를 찾는다.
					cursor.close();
					AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(m_position);
					cursor = db.rawQuery("SELECT _id FROM groupInfo WHERE name = '" + listItem.m_leftText + "';", null);
					cursor.moveToNext();
					int id = cursor.getInt(0);
					cursor.close();
					
					// DB에서 그룹명을 변경한다.
					db.execSQL("UPDATE groupInfo SET name = '" + newGroupName + "' WHERE _id = " + id +";");
					
					// 데이터베이스 갱신
					setAdapter();
				}
				// DB에 그룹명이 있으면 토스트로 실패를 알린다.
				else {
					Toast.makeText(AllGroupListActivity.this, R.string.dialog_add_fail, Toast.LENGTH_SHORT).show();
					cursor.close();
				}
				db.close();
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
    	
    	// EditText 키보드 출력하기
    	alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
    
    // ContextMenu의 delete아이템 선택시 호출되는 메서드
    public void deleteGroup() {
		PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	Cursor cursor;
		
		// group_id가져오기
    	AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(m_position);
		cursor = db.rawQuery("SELECT _id " +
				                      "FROM groupInfo " +
				                      "WHERE name = '" + listItem.m_leftText +
				                      "';", null);
		cursor.moveToNext();
		int id = cursor.getInt(0);
		cursor.close();

		// prayer 테이블에서 group_id 삭제하기
		db.execSQL("DELETE FROM prayer " +
				          "WHERE group_id = " + id +
				          ";");
		// groupInfo 테이블에서 _id 삭제하기
		db.execSQL("DELETE FROM groupInfo " +
				         "WHERE _id = " + id + 
				         ";");
		db.close();
		
		setAdapter();
    }
    
    
    /******
     * 
     *					GorupListAdapter Class
     * 
     ******/
    
    public class AllGroupListAdapter extends BaseAdapter {
    	
    	Context m_context;
    	LayoutInflater m_inflater;
    	ArrayList<AllGroupListItem> m_arrayListItem;
    	
    	boolean m_isSelectMode;
    	
    	
    	public AllGroupListAdapter(Context context) {
    		m_context = context;
    		m_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		m_isSelectMode = false;
    	}
    	
    	
    	// 외부로 부터 ArrayList를 받는다.
    	public void setArrayList(ArrayList<AllGroupListItem> arrayListItem) {
    		m_arrayListItem = arrayListItem;
    	}

    	
    	// 전체 아이템 수를 리턴한다.
    	public int getCount() {
    		// 선택모드일때는 all, ungroup아이템 두개를 제외한다.
    		if (m_isSelectMode == true) {
    			int count = m_arrayListItem.size() - 2;
    			// 0보다 적은 경우 0으로 설정한다.
    			if (count < 0)
    				return 0;
    			else
    				return count;
    		}
    		else
    			return m_arrayListItem.size();
    	}
    	

    	// 인자로 전달된 position의 아이템의 '그룹이름'을 리턴한다.
    	public Object getItem(int position) {
    		AllGroupListItem listItem = new AllGroupListItem(m_arrayListItem.get(position));
    		return listItem;
    	}

    	
    	// 아이템의 ID를 리턴한다.
    	public long getItemId(int position) {
    		return position;
    	}
    	
    	
    	// Check상태를 반전한다.
    	public void setCheckedChange(int position) {
    		m_arrayListItem.get(position+2).m_isChecked = !m_arrayListItem.get(position+2).m_isChecked;
    	}
    	
    	
    	// selectMode를 설정한다.
    	public void setSelectMode(boolean isSelectMode) {
    		m_isSelectMode = isSelectMode;
    	}
    	
    	// selectmode를 확인하기
    	public boolean getIsSelectMode() {
    		return m_isSelectMode;
    	}
    	
    	
    	// 모든 아이템의 checked를 false로 설정한다.
    	public void setAllCheckedFalse() {
    		int i;
    		for (i = m_arrayListItem.size()-1; i >= 0; i--)
    			m_arrayListItem.get(i).m_isChecked = false;
    	}

    	
    	// 모든 아이템의 checked를 확인하여 true시 DB에서 삭제한다. 
    	public void removeAllCheckListItem() {
    		int i;
    		PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        	Cursor cursor;
    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
    			if(m_arrayListItem.get(i).m_isChecked == true) {
    				// group_id가져오기
    				cursor = db.rawQuery("SELECT _id " +
    						                      "FROM groupInfo " +
    						                      "WHERE name = '" + m_arrayListItem.get(i).m_leftText +
    						                      "';", null);
    				cursor.moveToNext();
    				int id = cursor.getInt(0);
    				cursor.close();

    				// prayer 테이블에서 group_id 삭제하기
    				db.execSQL("DELETE FROM prayer " +
    						          "WHERE group_id = " + id +
    						          ";");
    				
    				// groupInfo 테이블에서 _id 삭제하기
    				db.execSQL("DELETE FROM groupInfo " +
    						         "WHERE _id = " + id + 
    						         ";");
    			}
    		}
    		
    		db.close();
    	}
    	
    	// 해당 position의 아이템을 제작한다.
    	public View getView(int position, View convertView, ViewGroup parent) {
    		// convertView가 null이면 처음 생성하는 아이템이니, 아이템을 생성한다.
    		if (convertView == null) {
    				convertView = m_inflater.inflate(R.layout.list_view, parent, false);
    				
    				// CheckBox의 리스너를 사용하지 않고, ListView의 리스너를 사용한다.
    				CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_view_checkbox);
    				checkBox.setClickable(false);
    				checkBox.setFocusable(false);
    		}
    		
    		TextView leftText = (TextView)convertView.findViewById(R.id.list_view_left_text);
    		TextView rightText = (TextView)convertView.findViewById(R.id.list_view_right_text);
    		
    		// selectMode시 체크박스를 출력한다.
    		CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_view_checkbox);
    		if (m_isSelectMode == true) {
    			checkBox.setVisibility(View.VISIBLE);
    			checkBox.setChecked(m_arrayListItem.get(position+2).m_isChecked);
    			
    			leftText.setText(m_arrayListItem.get(position+2).m_leftText);
    			rightText.setText(m_arrayListItem.get(position+2).m_rightText);
    		}
    		else {
    			checkBox.setVisibility(View.GONE);
    			
    			leftText.setText(m_arrayListItem.get(position).m_leftText);
    			rightText.setText(m_arrayListItem.get(position).m_rightText);
    		}
    		
    		return convertView;
    	}
    }
}