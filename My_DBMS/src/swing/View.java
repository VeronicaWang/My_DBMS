package swing;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import sql.SqlPreprocess;
import xml.Lie;
import xml.MyXml;
/**
 * 界面及操作
 * 根据用户输入的sql语句，转化为对虚拟数据库的操作
 *
 */
public class View {
    
   private JFrame mainFrame;
   private JPanel controlPanel;//输入sql语句面板
   private JPanel controlPanel2;//显示结果面板
   //利用正则表达式来提取输入的SQL命令
   String regexpCreate="create\\stable\\s(.+)\\s*\\(([^\\)]+)\\)";
   String regexpAdd="alter\\stable\\s(.+)add(.+)\\s(.+)";
   String regexpAlter="alter\\stable\\s(.+)alter\\scolumn\\s(.+)\\s(.+)";
   String regexpDrop="drop\\stable\\s(.+)restrict";
   String regexpInsert="insert\\sinto\\s(.+)\\s*\\(([^\\)]+)\\)\\s*values\\s*\\(([^\\)]+)\\)";
   String regexpUpdate="update\\s(.+)\\sset\\s(.+)\\swhere\\s(.+)";
   String regexphelp="helpdatabase\\n";
   //构造函数
   public  View(){
       prepareGUI();
   }

   //判断表是否存在
 	public static boolean isFile(String path){
 		path=path+".xml";
 		File dir = new File(path);    
 	    if (dir.exists()) {    
 	         return true;
 	    }
 	    return false;
 	}
 	//删除表
 	public static boolean deletFile(String path){
 		if(isFile(path)){
 			path=path+".xml";
 			File file=new File(path);
 			file.delete();
 			return true;
 		}
 		return false;
 	}	
   

   //设置界面布局的大致位置
   public void prepareGUI(){
      mainFrame = new JFrame("数据库管理系统");
      mainFrame.setSize(700,450);//窗口大小
      mainFrame.setLocation(300,100);//窗口位置
      mainFrame.setLayout(new GridLayout(2, 1));//布局管理器，将窗口布局设置为网格式布局，网格的行数和列数分别是3和1.
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });         
      controlPanel = new JPanel();
      controlPanel.setLayout(new FlowLayout());
      controlPanel2 = new JPanel();
      controlPanel2.setLayout(new FlowLayout());
      mainFrame.add(controlPanel);
      mainFrame.add(controlPanel2);
      mainFrame.setVisible(true);  
   }

   //主要的界面设计
   public void showTextAreaDemo(){
      JButton showButton0 = new JButton("登录");
      JLabel  commentlabel= new JLabel("执行:", JLabel.RIGHT);//在布局的右方，创建标签组件
      final JTextArea commentTextArea = new JTextArea("请输入SQL语句:",10,50);
      JScrollPane scrollPane = new JScrollPane(commentTextArea); //创建一个滚动面板，   
      JButton showButton = new JButton("执行");
      JLabel  commentlabel2= new JLabel("结果:", JLabel.RIGHT);
      final JTextArea commentTextArea2 = new JTextArea("",10,50);
      JScrollPane scrollPane2 = new JScrollPane(commentTextArea2);    
      JButton showButton2 = new JButton("刷新");
      
       //将各控件添加到窗体中，并显示
      controlPanel.add(commentlabel);
      controlPanel.add(scrollPane);        
      controlPanel.add(showButton);
      controlPanel2.add(commentlabel2);
      controlPanel2.add(scrollPane2);        
      controlPanel2.add(showButton2);
      mainFrame.setVisible(true);  
      
      //执行按钮Listener，即点击执行按钮后执行以下内容
      showButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) { 
        	  
         	String sql=commentTextArea.getText().trim();//获得输入的语句
         	SqlPreprocess sqlpre =new SqlPreprocess(sql);//由SqlPreproccess类预处理sql语句
         	 
         	//创建表
         	/*
         	 * 先判断是否符合各自语句的正则关系
         	 * 如果不符合，会得到错误的信息，以供之后显示
         	 * 如果符合，根据各自的正则语句，提取出表名，然后判断<表名.xml>文件是否存在
         	 * 如果存在说明此表之前已经创建，不能再创建。然后读取之前创建的表输出显示在文本框里；
         	 * 如果不存在，就创建<表名.xml>文件，然后用Lie类作为数据结构获得语句的各种名字和其对应值
         	 * 如果此时发生错误，会返回空，并将错误信息保存在info中，以供显示
         	 * 如果不为空，将获得的名字和其对应值写入<表名.xml>文件中，并输出各列。
         	 * 
         	 * 其他语句类似
         	 * 
         	 * */
         	if(sqlpre.isZhengZe(regexpCreate))//如果Create语句符合正则表达式
                {
         		if(isFile(sqlpre.getBiaoName(regexpCreate))){//如果表已存在就读取
        			System.out.println("表已经存在");
        			commentTextArea2.setText("");
        			commentTextArea2.append("表已经存在,不可创建\n");
        			
        			MyXml myxml = new MyXml(sqlpre.getBiaoName(regexpCreate));//根据预处理之后的SQL语句读取表的名字
        			myxml.readXml();//读表
        			
        			//输出表
        			myxml.printXml(myxml.getNodeListXml());
        			String str =myxml.getStr();
        			commentTextArea2.append(str);
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
        		}
                        else {//如果表不存在就创建一个
        			MyXml myxml = new MyXml(sqlpre.getBiaoName(regexpCreate));
        			myxml.createXml();//创建表
        			System.out.println("是创建语句");
        			
    				List<Lie> lies =new ArrayList<Lie>();
    				lies=sqlpre.getSqlCreate();
    				if(lies.isEmpty()){//如果为空
    					commentTextArea2.setText("");
            			commentTextArea2.append("表创建失败\n原因可能是："+sqlpre.getInfo());
    				}else{
    					myxml.createLie(lies);	
    					commentTextArea2.setText("");
            			commentTextArea2.append("表创建成功\n");
    				}
    				
    				//输出表
        			myxml.printXml(myxml.getNodeListXml());
        			String str =myxml.getStr();
        			commentTextArea2.append(str);
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
        		}
         	}
    		//删除表
         	else if(sqlpre.isZhengZe(regexpDrop)){
    			if(deletFile(sqlpre.getSqlDrop())){
    				System.out.println("删除成功");
    				commentTextArea2.setText("删除成功");
    			}else{
    				System.out.println("删除失败");
    				commentTextArea2.setText("删除失败");
    			}
    		}
    		//修改列属性
         	else if(sqlpre.isZhengZe(regexpAlter)){
         		if(isFile(sqlpre.getBiaoName(regexpAlter))){//如果表已存在就读取
         			Lie lie=sqlpre.getSqlColumn();
        			MyXml myxml =new MyXml(sqlpre.getBiaoName(regexpAlter));
        			myxml.readXml();
        			
        			if(myxml.isLie(lie.getLieName()))
                                {
        				if(myxml.isLieAttribute(lie.getLieName(),lie.getAttributeName().get(0))){
        					myxml.alterLie(lie);
        					commentTextArea2.setText("");
                			commentTextArea2.append("修改成功\n");
        				}else{
        					System.out.println("不存在属性");
        					commentTextArea2.setText("");
                			commentTextArea2.append("不存在属性"+lie.getAttributeName().get(0)+"\n");
        				}
        			}else{
        				System.out.println("不存在列");
        				commentTextArea2.setText("");
            			commentTextArea2.append("不存在列"+lie.getLieName()+"\n");
        			}
        			
        			//输出表
        			myxml.printXml(myxml.getNodeListXml());
        			String str =myxml.getStr();
        			commentTextArea2.append(str);
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
         		}
                        else
                        {
         			commentTextArea2.setText("");
        			commentTextArea2.append("表不存在"+"\n");
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
         		}
    		}
    		//添加列
         	else if(sqlpre.isZhengZe(regexpAdd)){
         		if(isFile(sqlpre.getBiaoName(regexpAdd))){//如果表已存在就读取
         			Lie lie=sqlpre.getSqlAdd();
        			MyXml myxml =new MyXml(sqlpre.getBiaoName(regexpAdd));
        			myxml.readXml();
        			if(myxml.isLie(lie.getLieName())){
        				System.out.println("列已经存在不可添加");
        				commentTextArea2.setText("");
            			commentTextArea2.append(lie.getLieName()+"列已经存在不可添加"+"\n");
        			}else{
        				myxml.addLie(lie);//增加列
        				System.out.println("添加列成功");
        				commentTextArea2.setText("");
            			commentTextArea2.append(lie.getLieName()+"列添加成功"+"\n");
        			}
        			//输出表
        			myxml.printXml(myxml.getNodeListXml());
        			String str =myxml.getStr();
        			commentTextArea2.append(str);
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
         		}else{
         			commentTextArea2.setText("");
        			commentTextArea2.append("表不存在"+"\n");
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
         		}
    		}
         	//插入
         	else if(sqlpre.isZhengZe(regexpInsert)){
    			if(isFile(sqlpre.getBiaoName(regexpInsert))){//如果表已存在就读取
    				List<Lie> lies = sqlpre.getSqlInsert();
    				MyXml myxml = new MyXml(sqlpre.getBiaoName(regexpInsert));
        			myxml.readXml();
        			if(myxml.existLie(lies)){
        				String infoInsert=myxml.InsertXml(lies);
        				if(!infoInsert.endsWith("")){
        					commentTextArea2.setText("");
                			commentTextArea2.append("表存在,已插入\n");
        				}else{
        					commentTextArea2.setText("");
                			commentTextArea2.append(infoInsert);
        				}
        			}else{
        				commentTextArea2.setText("");
            			commentTextArea2.append("失败！插入不存在的列\n");
        			}
        			//输出表
        			myxml.printXml(myxml.getNodeListXml());
        			String str =myxml.getStr();
        			commentTextArea2.append(str);
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
        		}else {//如果表不存在
        			
        			commentTextArea2.setText("");
        			commentTextArea2.append("表不存在\n");
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
        		}
    		}
         	//更新
         	else if(sqlpre.isZhengZe(regexpUpdate)){
         		if(isFile(sqlpre.getBiaoName(regexpUpdate))){//如果表已存在就读取
    				List<Lie> lies = sqlpre.getSqlUpdate();
    				MyXml myxml = new MyXml(sqlpre.getBiaoName(regexpUpdate));
        			myxml.readXml();
        			
        			if(myxml.existLie(lies)){
        				String infoInsert=myxml.UpdateXml(lies);
        				if(!infoInsert.equals("")){
        					commentTextArea2.setText("");
                			commentTextArea2.append("表存在,已更新\n");
        				}else{
        					commentTextArea2.setText("");
                			commentTextArea2.append(infoInsert);
        				}
        				
        			}else{
        				commentTextArea2.setText("");
            			commentTextArea2.append("失败！更新不存在的列\n");
        			}
        			
        			//输出表
        			myxml.printXml(myxml.getNodeListXml());
        			String str =myxml.getStr();
        			commentTextArea2.append(str);
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
        		}
                        else {//如果表不存在
        			
        			commentTextArea2.setText("");
        			commentTextArea2.append("表不存在\n");
        			commentTextArea2.paintImmediately(commentTextArea2.getBounds());
        		}
    		}
                else if(sqlpre.isZhengZe(regexphelp)){
                    
                }
         	else {
         		commentTextArea2.setText("你输入的sql语句不正确\n原因可能是："+sqlpre.getInfo());
         	}
         	
          }
       }); 
      //刷新按钮Listener
      showButton2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {     
        	 commentTextArea2.setText("");
         }
      }); 
      
     
   }
}  