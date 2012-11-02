package leeheechul.make.prayernotebook;

public class AllGroupListItem {
	
	String m_leftText = null;
	String m_rightText = null;
	boolean m_isChecked = false;
	
	AllGroupListItem(String leftText, String rightText) {
		this.m_leftText = leftText;
		this.m_rightText = rightText;
	}
	
	AllGroupListItem(AllGroupListItem listItem) {
		this.m_leftText = listItem.m_leftText;
		this.m_rightText = listItem.m_rightText;
	}
}
