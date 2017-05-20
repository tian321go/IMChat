package  cn.ipmsgchat.android.sql;

/*该类存放是用户信息
 * 所有用户信息的属性是私有的
 *能够通过该类的公用方法获取里面的私有信息
 */
public class UserInfo {
	private int id; // 用户ID
	private String name; // 用户名字
	private String sex; // 用户性别
	private String imei; // 用户手机序列码
	private String ipAddr; // 用户IP地址
	private String lastDate; // 最后登录时间
	private int avater; // 用户头像
	private String device; // 手机型号

	// 以下为用户的构造函数

	public UserInfo() {
		super();
	}

	public UserInfo(String name, String imei) {
		this.name = name;
		this.imei = imei;
	}

	public UserInfo(String name, String sex, String imei,
			int avater) {
		this(name, imei);
		this.sex = sex;
		this.avater = avater;
	}

	public UserInfo(String name, String sex, String imei,
			String ipAddr, int avater) {
		this(name, sex, imei, avater);
		this.ipAddr = ipAddr;
	}

	public UserInfo(int id, String name, String sex, String imei,
			String ipAddr, int avater) {
		this(name, sex, imei, ipAddr, avater);
		this.id = id;
	}

	public UserInfo(int id, String name, String sex, String imei,
			String ipAddr,  int avater, String lastDate,
			String device) {
		this(id, name, sex, imei, ipAddr, avater);
		this.lastDate = lastDate;
		this.device = device;
	}

	// 设置ID函数
	public void setId(int id) {
		this.id = id;
	}

	// 获取ID函数
	public int getId() {
		return id;
	}

	// 设置用户名函数
	public void setName(String name) {
		this.name = name;
	}

	// 获取用户名函数
	public String getName() {
		return name;
	}

	// 设置用户性别
	public void setSex(String sex) {
		this.sex = sex;
	}

	// 获取用户性别
	public String getSex() {
		return sex;
	}

	// 设置手机序列码
	public void setIMEI(String imei) {
		this.imei = imei;
	}

	// 获取手机序列码
	public String getIMEI() {
		return imei;
	}

	// 设置IP地址
	public void setIPAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	// 获取IP地址
	public String getIPAddr() {
		return ipAddr;
	}

	// 设置用户头像
	public void setAvater(int avater) {
		this.avater = avater;
	}

	// 获取用户头像
	public int getAvater() {
		return avater;
	}

	// 设置最后登录时间
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	// 获取最后登录时间
	public String getLastDate() {
		return lastDate;
	}

	/* 获取设备类型 */
	public String getDevice() {
		return device;
	}

	/* 设置设备类型 */
	public void setDevice(String device) {
		this.device = device;
	}

	// 输出所有用户信息
	public String toString() {
		return "id:" + getId() + " name:" + getName() + " sex:" + getSex()
				+ " IMEI:" + getIMEI() + " ip:" + getIPAddr() + " avaert:"
				+ getAvater() + " lastDate:" + getLastDate() + " device:"
				+ getDevice() ;
	}
}
