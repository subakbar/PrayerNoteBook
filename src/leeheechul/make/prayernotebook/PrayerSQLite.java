package leeheechul.make.prayernotebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PrayerSQLite extends SQLiteOpenHelper {
	
	// DB 생성자
	public PrayerSQLite(Context context) {
		
		// 전달인자 : 1. DB를 생성하는 컨텍스트, 2. DB 파일명, 3. CursorFactory, 4.Version
		super(context, "PrayerDB.db", null, 1);
	}

	// DB가 처음 만들어질때 호출된다. 여기서 테이블을 만들고 초기 레코드를 삽입한다.
	public void onCreate(SQLiteDatabase db) {
		
		// groupInfo 테이블
		db.execSQL("CREATE TABLE groupInfo (" +
				         "_id INTEGER PRIMARY KEY, " +			// 기본키로 사용될 그룹 ID
				         "name TEXT" +								// 그룹명
				         ");");
		
		// prayer 테이블
		db.execSQL("CREATE TABLE prayer (" +
				         "_id INTEGER PRIMARY KEY, " +          // 기본키로 사용될 기도 ID 
				         "target TEXT, " +							// 대상자
				         "date TEXT, " +								// 날짜
				         "group_id INTEGER, " +						// 외부키로 사용될 그룹 ID
				         "title TEXT, " +								// 기도제목
				         "answer TEXT, " +							// 응답내용
				         "word TEXT, " +								// 말씀내용
				         "FOREIGN KEY(group_id) REFERENCES groupInfo(_id)" +		// group_id를 외부키로 지정
				         ");");
	}
	
	// DB를 업그레이드할 때 호출된다. 기존 테이블을 삭제하고 새로 만들거나 ALTER TABLE로 스키마를 수정한다.
	// DB version은 DB 생성자를 통해 Version인자로 확인하여 필요시 onUpgrade를 호출한다.
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}