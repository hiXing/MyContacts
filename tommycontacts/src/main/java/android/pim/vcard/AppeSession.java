package android.pim.vcard;

import java.util.List;

/**
 * 
 * 存放黑名单
 *
 */
public class AppeSession {

	private List allContacts;//联系人所有的信息。用于强屏蔽
	private List zidingyi;
	

	public List getZidingyi() {
		return zidingyi;
	}

	public void setZidingyi(List zidingyi) {
		this.zidingyi = zidingyi;
	}

	public List getAllContacts() {
		return allContacts;
	}

	public void setAllContacts(List allContacts) {
		this.allContacts = allContacts;
	}
	
	
}
