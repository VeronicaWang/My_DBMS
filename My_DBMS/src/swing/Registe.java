/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swing;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import xml.Lie;
import xml.User;

/**
 *
 * @author lenovo
 */
public class Registe {
    static List<User> users = new ArrayList<User>();
    public Registe(){
        Registe();
    }
    public static void Registe() {
		
		JFrame jFrame = new JFrame("注册界面");
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		jFrame.setBounds(((int)dimension.getWidth() - 200) / 2, ((int)dimension.getHeight() - 300) / 2, 300, 300);
		jFrame.setResizable(false);
		jFrame.setLayout(null);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel label1 = new JLabel("姓名：");
		label1.setBounds(10, 10, 100, 35);
		jFrame.add(label1);
		
		JLabel label2 = new JLabel("密码：");
		label2.setBounds(10, 60, 100, 35);
		jFrame.add(label2);
               
               JLabel label3 = new JLabel("确认密码：");
		label3.setBounds(10, 120, 110, 35);
		jFrame.add(label3);
                
		final JTextField text1 = new JTextField();
		text1.setBounds(50, 15, 230, 30);
		jFrame.add(text1);
		
		final JPasswordField text2 = new JPasswordField();
		text2.setBounds(50, 70, 230, 30);
		jFrame.add(text2);
		
                final JPasswordField text3 = new JPasswordField();
		text3.setBounds(80, 120, 200, 30);
		jFrame.add(text3);
                
		JButton button1 = new JButton("summit");
             
		button1.setBounds(100, 180,80, 30);
                
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                            
				if(text2.getText().equals(text3.getText()))//两次输入密码一致
                                {       
					
                                        String name=text1.getText();
                                        String password=text2.getText();
                                        User user=new User();
                                        user.PassWord=password;
                                        user.UserName=name;
                                        users.add(user);
                                        Login login=new Login();
                                       // JOptionPane.showMessageDialog(null, "注册成功!", "提示", JOptionPane.INFORMATION_MESSAGE);
                                        login.Login();
                                        
				} 
                                else {
					JOptionPane.showMessageDialog(null, "两次输入密码不一致！", "提示", JOptionPane.ERROR_MESSAGE);
					text1.setText("");
					text2.setText("");
				}
			}
		});
		jFrame.add(button1);
		jFrame.setVisible(true);
	}
    
}
