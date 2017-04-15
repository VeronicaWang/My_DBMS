package xml;

/**
 * 定义用户的基本数据结构
 * 作为存储数据的介质
 *
 */
public class User{
	public String UserName;//用户名
	public String PassWord;//用户名
        public String UserGrand;//用户权限

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String PassWord) {
        this.PassWord = PassWord;
    }

    public String getUserGrand() {
        return UserGrand;
    }

    public void setUserGrand(String UserGrand) {
        this.UserGrand = UserGrand;
    }
	
}
                                         