package view;
import controller.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogInView extends JPanel implements ActionListener 
{
	
	private static final long serialVersionUID = -8792854660482932009L;

	//main frame
	private Program frame;
	
	private JLabel backgroundLogInPic;
	private JTextField id_entry;
	private JPasswordField pw_entry;
	private JButton logInButton;
	
	public LogInView(Program frame){
		
		super();
		this.frame=frame;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(screenSize);
		
		frame.setLocation(new Point(0, 0));
		
		frame.setResizable(false);
		
		//no layout, everything is specifically placed on the picture
		setLayout(null);
		
		//button for log in
		logInButton=new JButton("Log In");
		logInButton.setSize(468, 53);
		logInButton.setLocation(1218, 746);
		logInButton.setBackground(new Color(120,106,243));
		logInButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 30));
		logInButton.addActionListener(this);
		add(logInButton);
		
		
		//Id Entry
		id_entry=new JTextField();
		id_entry.setBorder(null);
		id_entry.setSize(468, 53);
		id_entry.setLocation(1218, 423);
		id_entry.setBackground(new Color(120,106,243));
		id_entry.setFont(new Font("Trebuchet MS", Font.PLAIN, 35));
		
		add(id_entry);					//panel added to frame
		
		//password entry
		pw_entry=new JPasswordField();
		pw_entry.setBorder(null);
		pw_entry.setSize(468, 53);
		pw_entry.setLocation(1218, 606);
		pw_entry.setBackground(new Color(120,106,243));
		pw_entry.setFont(new Font("Monaco", Font.PLAIN, 25));
		add(pw_entry);

		//background photo of Harold
		backgroundLogInPic = new JLabel();
		backgroundLogInPic.setBounds(0, 0, 1920, 999);
		backgroundLogInPic.setIcon(new ImageIcon(LogInView.class.getResource("/Images/login background .jpg")));
		add(backgroundLogInPic);
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e){
		//method that runs when an actionlistener object has been clicked
		if(e.getSource().equals(logInButton)) {
			try {
				//if there is any problem with the inserted values, an error is thrown and the if statements arent even reached
				MainController.logIn(id_entry.getText(),String.valueOf(pw_entry.getPassword()));
				/*
				the frame is loaded depending on what kind of user logged in.
				either a manager that can manipulate the database,
				or a shift manager that can bring workers in and out of shifts and accept orders
				*/
				if(MainController.userType().equals("Manager")) {
					frame.changePanel(new ManagerView(frame));
					System.out.println("Manager View Loaded");
				}else if(MainController.userType().equals("ShiftManager")) {
					frame.changePanel(new ShiftManagerView(frame));
					System.out.println("Shift Manager View Loaded");
				}
			}catch(Exception ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
			}
		}	
	}
	
}