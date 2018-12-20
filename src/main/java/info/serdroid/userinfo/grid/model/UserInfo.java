package info.serdroid.userinfo.grid.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="UserInfo")
@Table(name="USERINFO")
public class UserInfo implements Serializable {
	private static final long serialVersionUID = 5204492089966732074L;
	
	@Id
	@Column(name="userid")
    private String userid; 
	@Column(name="password")
    private String password;
	@Column(name="watchword")
    private String watchword;
	@Column(name="name")
    private String name;
	@Column(name="lastname")
    private String lastname;
	@Column(name="personid")
    private String personid;
	@Column(name="accountid")
    private String accountid;
	@Column(name="usertype")
    private int usertype;
	@Column(name="accountstate")
    private int accountstate;
	@Column(name="lastupdated")
    private String lastupdated;
	@Column(name="partitionid")
    private int partitionid;
    
    public UserInfo() {
    }

    public UserInfo(String userid, String password, String watchword, String name, String lastname, String personid,
			String accountid, int usertype, int accountstate, String lastupdated, int partitionid) {
		this.userid = userid;
		this.password = password;
		this.watchword = watchword;
		this.name = name;
		this.lastname = lastname;
		this.personid = personid;
		this.accountid = accountid;
		this.usertype = usertype;
		this.accountstate = accountstate;
		this.lastupdated = lastupdated;
		this.partitionid = partitionid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getWatchword() {
		return watchword;
	}

	public void setWatchword(String watchword) {
		this.watchword = watchword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPersonid() {
		return personid;
	}

	public void setPersonid(String personid) {
		this.personid = personid;
	}

	public String getAccountid() {
		return accountid;
	}

	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public int getAccountstate() {
		return accountstate;
	}

	public void setAccountstate(int accountstate) {
		this.accountstate = accountstate;
	}

	public String getLastupdated() {
		return lastupdated;
	}

	public void setLastupdated(String lastupdated) {
		this.lastupdated = lastupdated;
	}

	public int getPartitionid() {
		return partitionid;
	}

	public void setPartitionid(int partitionid) {
		this.partitionid = partitionid;
	}

	@Override
	public String toString() {
		return "UserInfo [userid=" + userid + ", name=" + name + ", lastname=" + lastname + ", personid=" + personid
				+ ", accountid=" + accountid + "]";
	}

}
