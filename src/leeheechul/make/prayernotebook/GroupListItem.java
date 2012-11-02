package leeheechul.make.prayernotebook;

public class GroupListItem {
	int m_id;
	String m_leftText = null;
	String m_rightText = null;
	boolean m_isChecked = false;
	
	GroupListItem(int id, String leftText, String rightText) {
		this.m_id = id;
		this.m_leftText = leftText;
		this.m_rightText = rightText;
	}
	
	GroupListItem(GroupListItem groupListItem) {
		this.m_id = groupListItem.m_id;
		this.m_leftText = groupListItem.m_leftText;
		this.m_rightText = groupListItem.m_rightText;
	}
}
