package info.serdroid.userinfo.grid.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SUBJECTKEY")
public class SubjectKey {
	@Id
	private int sId;
	private short sTypeId;
	private String sKey;

	public SubjectKey() {}
	
	public SubjectKey(int sId, short sType, String sKey) {
		this.sId = sId;
		this.sTypeId = sType;
		this.sKey = sKey;
	}

	public int getSId() {
		return sId;
	}
	public void setSId(int sId) {
		this.sId = sId;
	}
	public short getSTypeId() {
		return sTypeId;
	}
	public void setSTypeId(short sType) {
		this.sTypeId = sType;
	}
	public String getSKey() {
		return sKey;
	}
	public void setSKey(String sKey) {
		this.sKey = sKey;
	}

	@Override
	public String toString() {
		return "SubjectKey [sId=" + sId + ", sTypeId=" + sTypeId + ", sKey=" + sKey + "]";
	}
}
