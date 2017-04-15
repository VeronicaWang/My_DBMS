package sql;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import xml.Lie;
/**
 * 预处理Sql语句及sql操作
 * 对sql语句利用正则语句进行分析
 * 返回所提取的sql语句中的信息
 * 
 * @author 王仁洁
 *
 */
//对输入的SQL语句进行预处理
public class SqlPreprocess {
	String sql;
	String info="";
	//利用正则表达式来提取SQL语句
	   String regexpCreate="create\\stable\\s(.+)\\s*\\(([^\\)]+)\\)";
	   String regexpAdd="alter\\stable\\s(.+)add(.+)\\s(.+)";
	   String regexpAlter="alter\\stable\\s(.+)alter\\scolumn\\s(.+)\\s(.+)";
	   String regexpDrop="drop\\stable\\s(.+)restrict";
	   String regexpInsert="insert\\sinto\\s(.+)\\s*\\(([^\\)]+)\\)\\s*values\\s*\\(([^\\)]+)\\)";
	   String regexpUpdate="update\\s(.+)\\sset\\s(.+)\\swhere\\s(.+)";
           String regexphelp="helpdatabase\\n";
	   
	//得到错误信息，为空表示成功过
	public String getInfo() {
		return info;
	}
	
	//构造函数，将输入的SQL语句规范化
	public SqlPreprocess(String sql){
		this.sql=sql.trim();//去除两端的空格
		this.sql=this.sql.toLowerCase();//小写
		this.sql=this.sql.replaceAll("\\s{1,}"," ");//把多余空格替换成只有一个
		this.sql=this.sql.replaceAll(";","");//把;替换成只有一个
	}
	
	//得到处理后的Sql语句
	public String getSqlPre(){
		return sql;
	}
	
	//判断是否为预设属性
	public boolean isYuSheAttr(String attr){
		String[] yushe ={"int","char","double","float"}; //将预设属性放入yushe[]数组
		for(String yu: yushe){
			Pattern pattern=Pattern.compile(yu,Pattern.CASE_INSENSITIVE); 
			Matcher matcher=pattern.matcher(attr);
			if(matcher.find()){
				return true;
			}
                        //将attr与预设属性进行比较，如果attr属于预设属性 则返回true，否则返回false
		}
		return false;
	}
	//判断是否符合正则
	public boolean isZhengZe(String regexp){
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); //给定的正则表达式编译并赋予给Pattern类
		Matcher matcher=pattern.matcher(this.sql);//matcher是一个正则表达式适配器
		if(matcher.find()){
			System.out.println("发现");
			if(regexp.equals(regexpCreate))//判断创建表的SQL语句是否符合正则表达式
                        {
				String str = matcher.group(2).trim();//返回在以前匹配操作期间由给定组捕获的输入第三个子序列
				String[] dataArr =str.split("[,]"); 
				int primary=0;
				for (String strTmp : dataArr) { 
					String attri = getAttrValue(strTmp.trim());//属性
					if(!isYuSheAttr(attri)){
						info+="存在属性不属于int|char|double|float\n";
						return false;
					}
					if(isPrimary(strTmp.trim())){
						primary++;
					}
				}
				if(primary>1){
					info+="主键多于一个\n";
					return false;
				}
			}
			else if(regexp.equals(regexpAdd)||regexp.equals(regexpAlter))//判断ALTER语句是否符合正则表达式
                        {
				if(!isYuSheAttr(matcher.group(3).trim())){
					info+="属性不属于int|char|double|float\n";//错误信息是属性不在预设值内
					return false;
				}
			}
			else if(regexp.equals(regexpInsert)){
				String str = matcher.group(2).trim();
				String str1= matcher.group(3).trim();
				String[] lieName =str.split("[,]"); 
				String[] lieValue=str1.split("[,]");
				if(lieName.length!=lieValue.length){
					info+="列的个数不等于值的个数\n";//错误信息是列的个数不等于值的个数
					return false;
				}
			}
                        else if(regexp.equals(regexphelp)){
                            return true;
                        }
			System.out.println("符合正则"); 
			return true;
			} 
		return false;
	}
	
	//返回生成表
        /*
        List接口提供的适合于自身的常用方法均与索引有关。
        这是因为List集合为列表类型，以线性方式存储对象，可以通过对象的索引操作对象。
        */
	public List<Lie> getSqlCreate(){
		List<Lie> lies = new ArrayList<Lie>();//用链表存储表的信息
		String regexp="create\\stable\\s(.+)\\s*\\(([^\\)]+)\\)[;]*";
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(this.sql);
		
		if(matcher.find()){
			System.out.println(matcher.group(1).trim()+" "+matcher.group(2).trim()); 
			String str = matcher.group(2).trim();
			String[] dataArr =str.split("[,]"); //将str按照，分隔，存进数组dataArr[]
			for (String strTmp : dataArr) //提取dataArr[]中的元素
                        { 
				Lie lie =new Lie();
				lie.setLieName(getAttrName(strTmp.trim()));//列名
				List<String> attributeName=new ArrayList<String>();//列的属性名
				List<String> attributeValue=new ArrayList<String>();//列的属性值
				attributeName.add("ATTR");//数据类型
				attributeValue.add(getAttrValue(strTmp.trim()));//判断strTmp的类型，将类型存进链表中
				if(isPrimary(strTmp.trim()))//判断是否为主键，如果是主键，则将主键加入属性名且将值设为true
                                {
					attributeName.add("PRIMARY");//数据类型
					attributeValue.add("true");
					String attrStr=getAttrValue(strTmp.trim());
					if(!(attrStr.equals("int")||attrStr.equals("INT")))//判断主键类型是否为int
                                        {
						info+="主键类型不为int";//错误信息
						lies.clear();
						return lies;
					}
				}
				if(isUnique(strTmp.trim()))//判断是否唯一，如果是唯一，则将unique加入属性名且将值设为true
                                {
					attributeName.add("UNIQUE");//数据类型
					attributeValue.add("true");
				}
				System.out.println(getAttrName(strTmp.trim())+" "+getAttrValue(strTmp.trim())); 
				lie.setAttributeName(attributeName);
				lie.setAttributeValue(attributeValue);
				lies.add(lie);
			}
		} 
		return lies;
	}
	//是否为主键
	public boolean isPrimary(String name){//判断是否为主键
		String regexp=".+\\s.+\\sprimary\\skey";
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(name);
		if(matcher.find()){
			return true;
		}
		return false;
	} 
	//是否为唯一
	public boolean isUnique(String name){//判断是否唯一，
		String regexp=".+\\s.+\\sunique";
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(name);
		if(matcher.find()){
			return true;
		}
		return false;
	} 
	//得到列名
	public String getAttrName(String name){
		String attributeName;//列的名
		String[] dataArr =name.split("[\\s]"); 
		attributeName=dataArr[0].trim();		//0
		return attributeName;
	}
	//得到属性名
	public String getAttrValue(String name){
		String attributeName;//列的属性名
		String[] dataArr =name.split("[\\s]"); 
		attributeName=dataArr[1].trim();		//1
		return attributeName;
	}
	//返回表名
	public String getBiaoName(String regexp){
		String biaoName="省缺";
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(this.sql);
		if(matcher.find()){
			biaoName=matcher.group(1).trim(); 
		} 
		return biaoName;
	}
	
	//返回增加语句的
	public Lie getSqlAdd(){
		Lie lie=new Lie();
		String regexp="alter\\stable\\s(.+)add(.+)\\s(.+)[;]*";
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(this.sql);
		if(matcher.find()){
			System.out.println(matcher.group(1).trim()+" "+matcher.group(2).trim()+" "+matcher.group(3).trim()); 
			lie.setLieName(matcher.group(2).trim());
			List<String> attributeName=new ArrayList<String>();//列的属性名
			List<String> attributeValue=new ArrayList<String>();//列的属性值
			attributeName.add("ATTR");
			attributeValue.add(matcher.group(3).trim());
			lie.setAttributeName(attributeName);
			lie.setAttributeValue(attributeValue);
			} 
		return lie;
	}
	
	//返回修改列属性语句
	public Lie getSqlColumn(){
		Lie lie=new Lie();
		String regexp="alter\\stable\\s(.+)alter\\scolumn\\s(.+)\\s(.+)[;]*";
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(this.sql);
		if(matcher.find()){
			System.out.println(matcher.group(1).trim()+" "+matcher.group(2).trim()+" "+matcher.group(3).trim()); 
			lie.setLieName(matcher.group(2).trim());
			List<String> attributeName=new ArrayList<String>();//列的属性名
			List<String> attributeValue=new ArrayList<String>();//列的属性值
			attributeName.add("ATTR");
			attributeValue.add(matcher.group(3).trim());
			lie.setAttributeName(attributeName);
			lie.setAttributeValue(attributeValue);
			} 
		return lie;
	}
	
	//返回删除语句数据
	public String getSqlDrop(){
		String biaoName=null;
		String regexp="drop\\stable\\s(.+)restrict[;]*";
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(this.sql);
		if(matcher.find()){
			System.out.println(matcher.group(1).trim());
			biaoName=matcher.group(1).trim();
			} 
		return biaoName;
	}
	
	//返回插入语句数据
	public List<Lie> getSqlInsert(){
		List<Lie> lies = new ArrayList<Lie>();
		String regexp="insert\\sinto\\s(.+)\\s*\\(([^\\)]+)\\)\\s*values\\s*\\(([^\\)]+)\\)[;]*";
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(this.sql);
		if(matcher.find()){
			System.out.println(matcher.group(1).trim()+" "+matcher.group(2).trim()+" "+matcher.group(3).trim()); 
			String str = matcher.group(2).trim();
			String str1= matcher.group(3).trim();
			String[] lieName =str.split("[,]"); 
			String[] lieValue=str1.split("[,]");
			for (int i=0;i<lieName.length;i++) { 
				String strName=lieName[i].trim();//列名
				
				String strValue=lieValue[i].trim();//列下的值
				String regexpValue="\\'(.+)\\'";//匹配带单引号的
				Pattern patternValue=Pattern.compile(regexpValue,Pattern.CASE_INSENSITIVE); 
				Matcher matcherValue=patternValue.matcher(strValue);
				if(matcherValue.find()){
					strValue=matcherValue.group(1).trim();
					//System.out.println(strValue);
				}else{//如果没有单引号
					//System.out.println(strValue);
				}
				
				List<String> value=new ArrayList<String>();//列下的值
				value.add(strValue);
				
				Lie lie =new Lie();
				lie.setLieName(strName.trim());//列名
				lie.setValue(value);
				
				lies.add(lie);
			}
		} 
		return lies;
	}
	
	//返回更新参数
	public List<Lie> getSqlUpdate(){
		List<Lie> lies = new ArrayList<Lie>();
		String regexp="update\\s(.+)\\sset\\s(.+)\\swhere\\s(.+)[;]*";
		
		System.out.println(regexp);
		Pattern pattern=Pattern.compile(regexp,Pattern.CASE_INSENSITIVE); 
		Matcher matcher=pattern.matcher(this.sql);
		if(matcher.find()){
			System.out.println(matcher.group(1).trim()+" "+matcher.group(2).trim()+" "+matcher.group(3).trim());

			//where值
			String str1= matcher.group(3).trim();
			String regexpValue="(.+)\\s*=\\s*(.+)";
			Pattern patternValue=Pattern.compile(regexpValue,Pattern.CASE_INSENSITIVE); 
			Matcher matcherValue=patternValue.matcher(str1);
			String whereName="";//列名
			String whereValue="";//列值
			if(matcherValue.find()){
				whereName=matcherValue.group(1).trim();//列名
				whereValue=matcherValue.group(2).trim();//列值
				System.out.println(whereName+" "+whereValue);
			}else{//如果没有where语句
				whereName="";//列名
				whereValue="";//列值
			}
			Lie lie =new Lie();
			List<String> whereValueList=new ArrayList<String>();//列下的值
			lie.setLieName(whereName);
			whereValueList.add(whereValue);
			lie.setValue(whereValueList);
			lies.add(lie);
			
			//修改的值
			String str = matcher.group(2).trim();
			String[] lieName =str.split("[,]"); 
			for (int i=0;i<lieName.length;i++) { 
				String strName=lieName[i].trim();//列名
				String regexpValue2="(.+)\\s*=\\s*(.+)";//匹配带单引号的
				Pattern patternValue2=Pattern.compile(regexpValue2,Pattern.CASE_INSENSITIVE); 
				Matcher matcherValue2=patternValue2.matcher(strName);
				String name="";//列名
				String value="";//列值
				if(matcherValue2.find()){
					name=matcherValue2.group(1).trim();
					value=matcherValue2.group(2).trim();
					System.out.println(name+" "+value);
				}else{//如果没有单引号
					
				}
				Lie lie2 =new Lie();
				List<String> setValues=new ArrayList<String>();//列下的值
				lie2.setLieName(name);
				setValues.add(value);
				System.out.println("add:"+value);
				lie2.setValue(setValues);
				lies.add(lie2);
			}
		} 
		return lies;
	}
}