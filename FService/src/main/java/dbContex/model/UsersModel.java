package dbContex.model;


public class UsersModel {
	//String _userid, String _itemid, String _value)
	private long _userid;
	private long _itemid;
	private String _value;

	public UsersModel() {
		
		// TODO Auto-generated constructor stub
	}

	public long get_userid() {
		return _userid;
	}

	public void set_userid(long _userid) {
		this._userid = _userid;
	}

	public long get_itemid() {
		return _itemid;
	}

	public void set_itemid(long _itemid) {
		this._itemid = _itemid;
	}

	public String get_value() {
		return _value;
	}

	public void set_value(String _value) {
		this._value = _value;
	}

}
