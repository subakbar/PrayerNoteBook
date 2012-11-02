package leeheechul.make.prayernotebook;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class GroupListActivity extends Activity implements OnItemClickListener {
	
	int m_position;
	int m_groupId;
	String m_groupName;
	GroupListAdapter m_adapter;
	int m_sortMode;
	boolean m_isSelectMove;
	ArrayAdapter<String> m_groupList;
	
	Button m_cancelButton;
	Button m_okButton;
	Button m_addButton;
	TextView m_title;
	TextView m_selectTitle;
	ListView m_listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        
        // 윗젠 초기화
        m_cancelButton = (Button)findViewById(R.id.group_list_activity_cancel_button);
        m_okButton = (Button)findViewById(R.id.group_list_activity_ok_botton);
        m_addButton = (Button)findViewById(R.id.group_list_activity_add_botton);
        m_title = (TextView)findViewById(R.id.group_list_activity_title);
        m_selectTitle = (TextView)findViewById(R.id.group_list_activity_title_select_mode);
        
        // 액션바 '취소', '확인'버튼 숨기기
        m_cancelButton.setVisibility(View.GONE);
        m_okButton.setVisibility(View.GONE);
        
        // 액션바의 타이틀의 위치를 변경한다.
        m_selectTitle.setVisibility(View.GONE);
        
        // 리스트 선택시 지정된 색상으로 변경한다.
        m_listView = (ListView)findViewById(R.id.group_list_activity_list);
        m_listView.setSelector(R.drawable.my_selector);
        
        // 리스트 리스터 초기화
        m_listView.setOnItemClickListener(this);
        registerForContextMenu(m_listView);
        
        // 인텐트에서 정보가져오기
        Intent intent = getIntent();
        m_groupName = intent.getStringExtra("groupName");
        
        // 타이틀을 groupName으로 수정
        m_title.setText(m_groupName);
        
        PrayerSQLite prayerSqlite = new PrayerSQLite(this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        Cursor cursor;
        
        // DB에서 그룹ID가져오기
        if (!m_groupName.equals("All") && !m_groupName.equals("Ungroup")) {
	        cursor = db.rawQuery("SELECT _id FROM groupInfo WHERE name = '" + m_groupName + "';", null);
	        cursor.moveToNext();
	        m_groupId = cursor.getInt(0);
	        cursor.close();
        }
        db.close();
        
        
        // 멤버변수 초기화
        m_adapter = new GroupListAdapter(this);
        m_groupList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        
        // sort_mode를 추출한다.
        SharedPreferences pref = getSharedPreferences("Setting", 0);
        m_sortMode = pref.getInt("sortMode", 1);
    }
    
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	// 비밀번호설정이 ture이고, 백그라운드 복귀시 비밀번호액티비티를 실행한다.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("GroupListActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// 현재 액티비티를 저장한다.
			app.setPreActivity("GroupListActivity");
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
    	ArrayList<GroupListItem> arrayListItem = new ArrayList<GroupListItem>();
        Cursor cursor;
        GroupListItem listItem;
    	
        // DB에서 정보를 가져온다.
        if(m_groupName.equals("All")) {
        	if(m_sortMode == 1)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY target ASC;", null);
        	else if(m_sortMode == 2)
            	cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY target DESC;", null);
        	else if(m_sortMode == 3)
            	cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY date ASC;", null);
        	else
            	cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY date DESC;", null);
        }
        else if (m_groupName.equals("Ungroup")) {
        	if(m_sortMode == 1)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY target ASC;", null);
        	else if(m_sortMode == 2)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY target DESC;", null);
        	else if(m_sortMode == 3)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY date ASC;", null);
        	else
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY date DESC;", null);
        }
        else {
        	if(m_sortMode == 1)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY target ASC;", null);
        	else if(m_sortMode == 2)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY target DESC;", null);
        	else if(m_sortMode == 3)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY date ASC;", null);
        	else
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY date DESC;", null);
        }
        
        // cursor의 그룹정보를 arrayList로 옮긴다.
        while (cursor.moveToNext()) {
        	listItem = new GroupListItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        	arrayListItem.add(listItem);
        }
        cursor.close();
        db.close();

        // adapter를 설정하기
        m_adapter.setArrayList(arrayListItem);
        
        // ListView에 adapter 설정하기
        ListView list = (ListView)findViewById(R.id.group_list_activity_list);
        list.setAdapter(m_adapter);

        m_adapter.notifyDataSetChanged();
    }
    
    /*****
     * 
     * 					Button Method
     * 
     *****/
    
    public void backButton(View view){
    	finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
    
    // 추가버튼 클릭시 호출되는 메서드
    public void addButton(View view) {
    	Intent intent = new Intent(this, NewPrayerActivity.class);
    	intent.putExtra("groupName", m_groupName);
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
    	if (m_isSelectMove == true) {
    		// 선택된 아이템을 이동한다.
    		openContextMenu(m_listView);
    	}
    	else {
    		// 액션바에 '취소', '확인', '추가' 버튼의 클릭핸들러를 막는다.
        	m_cancelButton.setClickable(false);
        	m_okButton.setClickable(false);
        	m_addButton.setClickable(false);
        	
	        // 선택된 아이템을 삭제한다.
	        m_adapter.removeAllCheckListItem();
	        // ListView를 선택모드로 변경한다.
	 		m_adapter.setSelectMode(false);
	 		// 아이템의 checked를 해지한다.
	        m_adapter.setAllCheckedFalse();
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
    	// 아이템 선택시 prayerActivity로 이동한다.
    	else {
    		GroupListItem listItem = (GroupListItem)parent.getItemAtPosition(position);
    		
    		Intent intent = new Intent(this, PrayerActivity.class);
    		intent.putExtra("prayerId", listItem.m_id);
    		intent.putExtra("groupName", m_groupName);
    		
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
    		finish();
    		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    	}
    }

    
    /******
     * 
     * 					Option Menu Method
     * 
     ******/
    
    // 액티비티생성후 처음 한번실행되는 콜백메서드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 101, 0, R.string.menu_select_move);
    	menu.add(0, 102, 0, R.string.menu_select_delete);
    	SubMenu sort = menu.addSubMenu(R.string.menu_sort);
    	
    	sort.add(0, 201, 0, R.string.sub_menu_sort_abc);
    	sort.add(0, 202, 0, R.string.sub_menu_sort_cba);
    	sort.add(0, 203, 0, R.string.sub_menu_sort_123);
    	sort.add(0, 204, 0, R.string.sub_menu_sort_321);
    	
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
    	SharedPreferences pref = getSharedPreferences("Setting", 0);
    	SharedPreferences.Editor edit = pref.edit();
    	
    	switch (item.getItemId()) {
    	// 선택이동
    	case 101 :
    		m_isSelectMove = true;
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
    		m_selectTitle.setText(R.string.menu_select_move);
    		
    		return true;
    	// 선택삭제
    	case 102 :
    		m_isSelectMove = false;
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
    		m_selectTitle.setText(R.string.menu_select_delete);
    		
    		return true;
        /***
         * 			Sort Mode
         ***/
    	// ㄱㄴㄷ 
    	case 201 :
    		m_sortMode = 1;
    		edit.putInt("sortMode", 1);
    		edit.commit();
    		setAdapter();
    		return true;
    	// ㄷㄴㄱ
    	case 202 :
    		m_sortMode = 2;
    		edit.putInt("sortMode", 2);
    		edit.commit();
    		setAdapter();
    		return true;
    	// 1 2 3
    	case 203 :
    		m_sortMode = 3;
    		edit.putInt("sortMode", 3);
    		edit.commit();
    		setAdapter();
    		return true;
    	// 3 2 1
    	case 204 :
    		m_sortMode = 4;
    		edit.putInt("sortMode", 4);
    		edit.commit();
    		setAdapter();
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
	    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(position);
	    	menu.setHeaderTitle(listItem.m_leftText);
	    	
	    	menu.add(0, 201, 0, R.string.context_select);
	    	menu.add(0, 202, 0, R.string.Context_edit);
	    	menu.add(0, 203, 0, R.string.Context_delete);
	    	menu.add(0, 204, 0, R.string.Context_cancel);
    	}
    	else if (m_isSelectMove == true && m_adapter.getIsSelectMode() == true) {
    		// DB에서 그룹명 가져오기
			PrayerSQLite prayerSqlite = new PrayerSQLite(this);
	    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
	    	Cursor cursor;
	    	
	    	cursor = db.rawQuery("SELECT name FROM groupInfo ORDER BY name;", null);
	    	
	    	m_groupList.clear();
	    	m_groupList.add("Ungroup");
	    	while(cursor.moveToNext())
	    		m_groupList.add(cursor.getString(0));
	    	cursor.close();
	
		    menu.setHeaderTitle(R.string.prayer_group);
		    
		    for (int i = 0; i < m_groupList.getCount(); i++)
		    	menu.add(0, i, 0, m_groupList.getItem(i).toString());
    	}
    }
    
    
    public boolean onContextItemSelected (MenuItem item) {
    	Log.i("TAG", ">>>>> itme : " + item.getItemId());
    	if (m_isSelectMove == true) {
    		int position = item.getItemId();
    		m_adapter.moveAllCheckListItem(m_groupList.getItem(position));
    		m_isSelectMove = false;
    		// m_isSelectMove=false 되었으니 버튼을 감추기 위해 한번더 실행한다.
    		okButton(null);
    	}
    	else {
    		// position정보는 m_position으로 공유한다.
        	AdapterView.AdapterContextMenuInfo adapterInfo;
        	adapterInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        	m_position = adapterInfo.position;
        	
	    	switch (item.getItemId()) {
	    	// 선택
	    	case 201 :
	    		selectPrayer();
	    		return true;
	    	// 수정
	    	case 202 :
	    		editPrayer();
	    		return true;
	    	// 삭제
	    	case 203 :
	    		deletePrayer();
	    		return true;
	    	// 취소
	    	case 204 :
	    		// 끝 ㅋㅋㅋ 
	    		return true;
	    	}
    	}

    	return false;
    }
    
    public void selectPrayer() {
    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(m_position);
		
		Intent intent = new Intent(this, PrayerActivity.class);
		intent.putExtra("prayerId", listItem.m_id);
		intent.putExtra("groupName", m_groupName);
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
    
    // ContextMenu의 edit아이템 선택시 호출되는 메서드
    public void editPrayer() {
    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(m_position);
    	
    	Intent intent = new Intent(this, EditPrayerActivity.class);
		intent.putExtra("prayerId", listItem.m_id);
		intent.putExtra("groupName", m_groupName);
		// editGroup에서 prayer삭제시 PrayerActivity를 종료한다.
		// GroupListAcitivy에서 호출함으로 false로 한다.
		intent.putExtra("isCallPrayerActivity", false);			
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
    
    
    // ContextMenu의 delete아이템 선택시 호출되는 메서드
    public void deletePrayer() {
		PrayerSQLite prayerSqlite = new PrayerSQLite(GroupListActivity.this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	
    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(m_position);
    	
    	db.execSQL("DELETE FROM prayer WHERE _id = " + listItem.m_id + ";");
    	
		db.close();
		
		setAdapter();
    }
    
    
    /******
     * 
     *					AllGorupListAdapter Class
     * 
     ******/
    
    public class GroupListAdapter extends BaseAdapter {
    	
    	Context m_context;
    	LayoutInflater m_inflater;
    	ArrayList<GroupListItem> m_arrayListItem;
    	
    	boolean m_isSelectMode;
    	
    	
    	public GroupListAdapter(Context context) {
    		m_context = context;
    		m_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		m_isSelectMode = false;
    	}
    	
    	
    	// 외부로 부터 ArrayList를 받는다.
    	public void setArrayList(ArrayList<GroupListItem> arrayListItem) {
    		m_arrayListItem = arrayListItem;
    	}

    	
    	// 전체 아이템 수를 리턴한다.
    	public int getCount() {
    		return m_arrayListItem.size();
    	}
    	

    	// 인자로 전달된 position의 ListItem을 리턴한다.
    	public Object getItem(int position) {
    		GroupListItem listItem = new GroupListItem(m_arrayListItem.get(position));
    		return listItem;
    	}

    	
    	// 아이템의 ID를 리턴한다.
    	public long getItemId(int position) {
    		return position;
    	}
    	
    	
    	// Check상태를 반전한다.
    	public void setCheckedChange(int position) {
    		m_arrayListItem.get(position).m_isChecked = !m_arrayListItem.get(position).m_isChecked;
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
    	
    	
    	// 모든 아이템의 checked를 확인하여 true시 group_id를 수정한다.
    	public void moveAllCheckListItem(String groupName) {
    		PrayerSQLite prayerSqlite = new PrayerSQLite(GroupListActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        	Cursor cursor;
        	
        	if (groupName.toLowerCase().equals("ungroup")) {
        		int i;
	    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
	    			if(m_arrayListItem.get(i).m_isChecked == true) {
	    				db.execSQL("UPDATE prayer SET group_id = null WHERE _id = " + m_arrayListItem.get(i).m_id + ";");
	    			}
	    		}
        	}
        	else {
        		// 그룹 ID찾기
	        	cursor = db.rawQuery("SELECT _id FROM groupInfo WHERE name = '" + groupName + "';", null);
	        	cursor.moveToNext();
	        	int groupId = cursor.getInt(0);
	        	cursor.close();
	        	
	        	int i;
	    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
	    			if(m_arrayListItem.get(i).m_isChecked == true) {
	    				db.execSQL("UPDATE prayer SET group_id = " + groupId + " WHERE _id = " + m_arrayListItem.get(i).m_id + ";");
	    			}
	    		}
        	}
    		db.close();
    		
    		// ListView를 선택모드로 변경한다.
	 		m_adapter.setSelectMode(false);
	 		// 아이템의 checked를 해지한다.
	        m_adapter.setAllCheckedFalse();
	 		// adapter를 업데이트한다.
	 		setAdapter();
    	}

    	
    	// 모든 아이템의 checked를 확인하여 true시 DB에서 삭제한다. 
    	public void removeAllCheckListItem() {
    		int i;
    		PrayerSQLite prayerSqlite = new PrayerSQLite(GroupListActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
    			if(m_arrayListItem.get(i).m_isChecked == true) {
    				// prayer 테이블에서 해당 정보 삭제하기
    				db.execSQL("DELETE FROM prayer " +
    						          "WHERE target = '" + m_arrayListItem.get(i).m_leftText +
    						          "' AND date = '" + m_arrayListItem.get(i).m_rightText + "';");
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
    		
    		leftText.setText(m_arrayListItem.get(position).m_leftText);
			rightText.setText(m_arrayListItem.get(position).m_rightText);
    		
    		// selectMode시 체크박스를 출력한다.
    		CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_view_checkbox);
    		if (m_isSelectMode == true) {
    			checkBox.setVisibility(View.VISIBLE);
    			checkBox.setChecked(m_arrayListItem.get(position).m_isChecked);
    		}
    		else {
    			checkBox.setVisibility(View.GONE);
    		}
    		
    		return convertView;
    	}
    }
}