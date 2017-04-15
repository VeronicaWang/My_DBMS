package swing;
/*
定义用户登录界面
*/
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import xml.User;


public class Login {

   public Login(){
       Login();
   }
	public static void Login() {
		
		JFrame jFrame = new JFrame("登陆界面");
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		jFrame.setBounds(((int)dimension.getWidth() - 200) / 2, ((int)dimension.getHeight() - 300) / 2, 200, 200);
		jFrame.setResizable(false);
		jFrame.setLayout(null);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel label1 = new JLabel("姓名");
		label1.setBounds(10, 10, 100, 35);
		jFrame.add(label1);
		
		JLabel label2 = new JLabel("密码");
		label2.setBounds(10, 60, 100, 35);
		jFrame.add(label2);
               
                
		final JTextField text1 = new JTextField();
		text1.setBounds(50, 15, 130, 30);
		jFrame.add(text1);
		
		final JPasswordField text2 = new JPasswordField();
		text2.setBounds(50, 60, 130, 30);
		jFrame.add(text2);
		
                
		JButton button1 = new JButton("登录");
                JButton button2 = new JButton("注册");
		button1.setBounds(10, 110,80, 30);
                
                button2.setBounds(100, 110, 80, 30);
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                            String name=text1.getText();
                            String password=text2.getText();
                            for(int i=0;i<Registe.users.size();i++)
                            {
                                User user=new User();
                                user=Registe.users.get(i);
                                System.out.println(user.UserName);
                                System.out.println(user.PassWord);
				if(name.equals(user.UserName) && password.equals(user.PassWord)) 
                                {       
					JOptionPane.showMessageDialog(null, "登陆成功!", "提示", JOptionPane.INFORMATION_MESSAGE);
                                            View  swingControlDemo = new View();     //定义本类 
                                            swingControlDemo.showTextAreaDemo();
                                        
				} 
                                else {
					JOptionPane.showMessageDialog(null, "账户或密码错误！", "提示", JOptionPane.ERROR_MESSAGE);
					text1.setText("");
					text2.setText("");
				}
			}
                        }
		});
                button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                         Registe registe=new Registe();
                         registe.Registe();
				
			}
		});
		jFrame.add(button1);
		jFrame.add(button2);
		jFrame.setVisible(true);
	}

}