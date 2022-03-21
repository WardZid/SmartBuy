package view;
import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Program extends JFrame {
	
	private static final long serialVersionUID = -2237799039364410583L;
	//mainPanel will hold the panel currently showing
	private JPanel mainPanel;
	public Program() throws HeadlessException{
		super("SmartBuy Management");//window title
		
		try {
			//fetch icon image from images package
			setIconImage(ImageIO.read(Program.class.getResourceAsStream("/Images/cart_icon.png")));
		} catch (IOException e) {
			System.out.println("Icon not loaded");
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel=new LogInView(this);//start with the login always
		getContentPane().add(mainPanel);
		pack(); //packs the frame around the loaded panel
		setVisible(true);
		
	}
	public void changePanel(JPanel newPanel){//panel exchange on the frame
		invalidate();
		//replace panel with new panel
		remove(this.mainPanel);
		this.mainPanel=newPanel;
		getContentPane().add(this.mainPanel);
		pack();						//packs the frame around the loaded panel
		revalidate();
	}
	//main method that starts up the whole program
	public static void main(String[] args) {
		//the gui starts up on a different method
		//not much faster but easier for the pc
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Program();
			}
		});
	}
	
}