package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;

import controller.MainController;
import model.Employee;
import model.Order;

import javax.swing.JScrollPane;

public class ShiftManagerView  extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 5717094931086026314L;
	private static int screenSize=70;
	private static int listSize=400;

	private Program frame;
	
	private JLabel homeBackground;
	
	private JPanel mainPanel;//contains the tabbed panels
	private JPanel lowerPanel;//contains the log out button
	private JButton logOutButton;
	
	private Color blueTwo=new Color(120,106,241);//light blue
	
	private JTabbedPane tabbedPane;
	
	private Font buttonFont;
	
	//panels for organization
	private JPanel homeTab;
	private JPanel ordersTab;
	private JPanel shiftsTab;
	private Dimension sizeOfTabPanels;
	private Dimension sizeOfLists;
	private Dimension sizeOfOrderLists;
	
	//Orders
	private Order listOfOrders[];
	private ArrayList<Order> waitingOrders;
	private ArrayList<Order> inProgressOrders;
	private JTextArea orderInfoPanel;
	private JScrollPane orderInfoScrollPane;
	
	//orders waiting to be started
	private JPanel waitingOrderList;
	private JScrollPane waitingOrderScrollPane;
	private ArrayList<JToggleButton> waitingOrderButtons;
	private JTextArea waitingOrdersHeader;
	
	//orders in progress
	private JPanel inProgressOrderList;
	private JScrollPane inProgressOrderScrollPane;
	private ArrayList<JToggleButton> inProgressOrderButtons;
	private JTextArea inProgressOrdersHeader;
	
	//middle action panel
	private JPanel actionOrderPanel;
	private JButton startOrderButton;
	private JButton finishOrderButton;
	private JButton refreshOrderButton;
	
	//Shifts
	private JPanel shiftList;
	private JScrollPane shiftScrollPane;
	private JTextArea shiftsHeader;
	
	private JPanel employeeList;
	private JScrollPane employeeScrollPane;
	private JTextArea employeesHeader;
	
	private Employee listOfEmployees[];
	private JToggleButton listOfEmployeeButtons[];
	
	//action panel
	private JPanel actionShiftPanel;
	private JButton addToShiftButton;
	private JButton removeFromShiftButton;
	private JButton refreshShiftButton;
	
	//to know if to enable or disable shift buttons
	private int selectedEmployee;
	
	//constructor
	public ShiftManagerView(Program frame){
		super();
		this.frame=frame;
		//main panel specifications
		setPreferredSize(new Dimension(16*screenSize,9*screenSize));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		frame.setResizable(false);
		frame.setLocation(new Point(400, 200));
		
		buttonFont=new Font("Trebuchet MS", Font.PLAIN, 20);
		
		//define global specifications
		sizeOfTabPanels=new Dimension(16*screenSize,(int)8*screenSize);
		sizeOfLists=new Dimension(listSize,listSize);
		sizeOfOrderLists=new Dimension(listSize,(int)(listSize*0.5));
		
		//start up main panel
		mainPanel=new JPanel();
		mainPanel.setPreferredSize(new Dimension(16*screenSize,8*screenSize));
		
		//building tabs

		setTabbedPane();

		add(mainPanel);
		
		setLowerPanel();
	}
	//methods
	private void setTabbedPane()  {
		//tabbed pane for user friendly ui
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
		
		//calling all different tab building methods
		buildHomePanel();
		
		buildOrderPanel();
		
		buildShiftPanel();

		mainPanel.add(tabbedPane);
	}
	private void setLowerPanel() {
		//lower panel contains log out button
		lowerPanel=new JPanel();
		lowerPanel.setPreferredSize(new Dimension(15*screenSize,screenSize));
		lowerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		lowerPanel.setBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY));

		logOutButton = new JButton("Log Out");
		logOutButton.setForeground(Color.WHITE);
		logOutButton.setPreferredSize(new Dimension(screenSize*2,screenSize/2));
		logOutButton.setBackground(blueTwo);//light blue
		logOutButton.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
		logOutButton.addActionListener(this);
		
		lowerPanel.add(logOutButton);
		
		add(lowerPanel);
	}
	private void buildHomePanel(){
		homeTab=new JPanel();
		homeTab.setPreferredSize(sizeOfTabPanels);
		
		//home background
		homeBackground = new JLabel();
		homeBackground.setBounds(0, 0, 1, 999);
		homeBackground.setIcon(new ImageIcon(ShiftManagerView.class.getResource("/Images/shift_mgr_bg.png")));
		homeTab.add(homeBackground);
		
		tabbedPane.addTab("Home", homeTab);
	}
	//Orders
	private void buildOrderPanel(){
		ordersTab=new JPanel();
		ordersTab.setPreferredSize(sizeOfTabPanels);
		loadOrderPanelContainers();
		tabbedPane.addTab("Orders", ordersTab);
	}
	private void loadOrderPanelContainers(){
		//load the internal parts of the orders tab
		getOrders();
		setUpWaitingOrders();
		loadOrdersActionPanel();
		setUpInProgressOrders();
		loadOrderInfoPanel();
	}
	private void getOrders() {
		//get the orders and organize them into two arrays
		try {
			//get the orders from the maincontroller
			listOfOrders=MainController.fetchOrders();
			//array for orders waiting to be started
			waitingOrders=new ArrayList<>();
			//array for orders currently in progress
			inProgressOrders=new ArrayList<>();
			for(Order o: listOfOrders) {
				if(o.isWaiting())
					waitingOrders.add(o);
				else if(o.isInProgress())
					inProgressOrders.add(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setUpWaitingOrders() {
		//build the list along with buttons and actionlisteners
		waitingOrderList = new JPanel();//inner jpanel of orders
		waitingOrderList.setBackground(Color.WHITE);
		
		waitingOrderList.setPreferredSize(new Dimension(300,(waitingOrders.size()+1)*35));
		waitingOrderList.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
		
		waitingOrdersHeader=new JTextArea("Orders Waiting: ");
		waitingOrdersHeader.setBackground(Color.WHITE);
		waitingOrdersHeader.setBorder(null);
		waitingOrdersHeader.setFont(new Font("Monaco", Font.BOLD, 25));
		waitingOrdersHeader.setPreferredSize(new Dimension(380,35));
		waitingOrdersHeader.setEditable(false);
		waitingOrderList.add(waitingOrdersHeader);
		
		waitingOrderButtons=new ArrayList<>();
		
		//make a button for each order
		for(int i=0;i<waitingOrders.size();i++) {
			waitingOrderButtons.add(createOrderItem(waitingOrders.get(waitingOrders.size()-i-1),i+1));
			waitingOrderList.add(waitingOrderButtons.get(i));
		}
		
		//scrollpane to help view whole list of products
		waitingOrderScrollPane = new JScrollPane(waitingOrderList);
		waitingOrderScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		waitingOrderScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		waitingOrderScrollPane.setPreferredSize(sizeOfOrderLists);
		ordersTab.add(waitingOrderScrollPane);
		revalidate();
	}
	private void loadOrdersActionPanel() {
		//set up the middle actionpanel
		actionOrderPanel=new JPanel();
		actionOrderPanel.setPreferredSize(new Dimension(200,(int)(listSize*0.5)));
		actionOrderPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		
		startOrderButton=new JButton("Start Order");
		finishOrderButton=new JButton("Order Finished");
		refreshOrderButton=new JButton("Refresh");
		
		startOrderButton.setPreferredSize(new Dimension(180,40));
		finishOrderButton.setPreferredSize(new Dimension(180,40));
		refreshOrderButton.setPreferredSize(new Dimension(180,60));

		startOrderButton.setFont(buttonFont);
		finishOrderButton.setFont(buttonFont);
		refreshOrderButton.setFont(buttonFont);
		
		startOrderButton.addActionListener(this);
		finishOrderButton.addActionListener(this);
		refreshOrderButton.addActionListener(this);
		
		actionOrderPanel.add(startOrderButton);
		actionOrderPanel.add(finishOrderButton);
		actionOrderPanel.add(refreshOrderButton);
		
		//these buttuns are disabled by default
		//they are enabled according to which list the order clicked belngs to
		startOrderButton.setEnabled(false);
		finishOrderButton.setEnabled(false);
		
		ordersTab.add(actionOrderPanel);
	}
	private void setUpInProgressOrders() {
		//set specifications for list
		inProgressOrderList = new JPanel();//inner jpanel of orders
		inProgressOrderList.setBackground(Color.WHITE);
		
		inProgressOrderList.setPreferredSize(new Dimension(300,(inProgressOrders.size()+1)*35));
		inProgressOrderList.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
		
		inProgressOrdersHeader=new JTextArea("Orders in Progress: ");
		inProgressOrdersHeader.setBackground(Color.WHITE);
		inProgressOrdersHeader.setBorder(null);
		inProgressOrdersHeader.setFont(new Font("Monaco", Font.BOLD, 25));
		inProgressOrdersHeader.setPreferredSize(new Dimension(380,35));
		inProgressOrdersHeader.setEditable(false);
		inProgressOrderList.add(inProgressOrdersHeader);
		
		inProgressOrderButtons=new ArrayList<>();
		
		//button for each order
		for(int i=0;i<inProgressOrders.size();i++) {
			inProgressOrderButtons.add(createOrderItem(inProgressOrders.get(inProgressOrders.size()-i-1),i+1));
			inProgressOrderList.add(inProgressOrderButtons.get(i));
		}
		
		inProgressOrderScrollPane = new JScrollPane(inProgressOrderList);
		inProgressOrderScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		inProgressOrderScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		inProgressOrderScrollPane.setPreferredSize(sizeOfOrderLists);
		ordersTab.add(inProgressOrderScrollPane);
		revalidate();
	}
	private JToggleButton createOrderItem(Order o,int priority) {
		//create list item panel which stacks onto the the main list panel
		JToggleButton orderButton=new JToggleButton();
		orderButton.setBackground(Color.WHITE);
		orderButton.setBorder(null);
		
		JLabel name=new JLabel(" Priority: "+priority+"    |     Order num.: "+o.getOrderId()+"    |     Prep Time: " + o.getTotalTime());
		name.setFont(new Font("Monaco", Font.BOLD, 15));
		
		orderButton.add(name);
		orderButton.setPreferredSize(new Dimension(380,35));
		orderButton.addActionListener(this);
		return orderButton;
	}
	private void loadOrderInfoPanel() {
		//panel that holds an order's info, including the products in said order
		orderInfoPanel = new JTextArea("\n  [Select an order to see its information.] ");
		orderInfoPanel.setBorder(null);
		orderInfoPanel.setFont(new Font("Trebuchet MS",Font.PLAIN,20));
		orderInfoPanel.setEditable(false);
		
		orderInfoScrollPane=new JScrollPane(orderInfoPanel);
		orderInfoScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		orderInfoScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		orderInfoScrollPane.setPreferredSize(new Dimension(1000,300));
		ordersTab.add(orderInfoScrollPane);
		validate();
	}
	private void loadOrderInfo(Order o) {
		//present the order's info
		orderInfoPanel.setPreferredSize(new Dimension(1000,25*((o.getMyList().size()*3)+5)));
		orderInfoPanel.setText(o.toString());
	}
	private void refreshOrderList() {
		//remove the components and them added them again
		//which will fetch all the info 
		ordersTab.removeAll();//refreshing list
		loadOrderPanelContainers();
	}
	
	//shifts
	private void buildShiftPanel() {
		shiftsTab=new JPanel();
		shiftsTab.setPreferredSize(sizeOfTabPanels);
		loadShiftPanelContainers();
		tabbedPane.addTab("Shift", shiftsTab);
	}
	private void loadShiftPanelContainers() {
		loadEmployees();
	
		//scroll pane to allow the manager to view all employees regardless of the amount
		shiftScrollPane = new JScrollPane(shiftList);
		shiftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		shiftScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		shiftScrollPane.setPreferredSize(sizeOfLists);
		shiftsTab.add(shiftScrollPane);
		
		loadShiftsActionPanel();
		
		//scroll pane to allow the manager to view all employees regardless of the amount
		employeeScrollPane = new JScrollPane(employeeList);
		employeeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		employeeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		employeeScrollPane.setPreferredSize(sizeOfLists);
		shiftsTab.add(employeeScrollPane);
		
		revalidate();
	}
	private void loadShiftsActionPanel() {
		//build the action panel
		actionShiftPanel=new JPanel();
		actionShiftPanel.setPreferredSize(new Dimension(200,listSize));
		actionShiftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		
		addToShiftButton=new JButton("Add To Shift");
		removeFromShiftButton=new JButton("Remove From Shift");
		refreshShiftButton=new JButton("Refresh");
		
		addToShiftButton.setPreferredSize(new Dimension(180,40));
		removeFromShiftButton.setPreferredSize(new Dimension(180,40));
		refreshShiftButton.setPreferredSize(new Dimension(180,60));

		addToShiftButton.setFont(buttonFont);
		removeFromShiftButton.setFont(buttonFont);
		refreshShiftButton.setFont(buttonFont);
		
		addToShiftButton.addActionListener(this);
		removeFromShiftButton.addActionListener(this);
		refreshShiftButton.addActionListener(this);
		
		actionShiftPanel.add(addToShiftButton);
		actionShiftPanel.add(removeFromShiftButton);
		actionShiftPanel.add(refreshShiftButton);
		
		//buttons are disabled by default
		//they are enabled/disabled according to the employee's shift status
		addToShiftButton.setEnabled(false);
		removeFromShiftButton.setEnabled(false);
		
		shiftsTab.add(actionShiftPanel);
	}
	private void loadEmployees() {
		try {
			employeeList = new JPanel();//list of employees not in shift
			employeeList.setBackground(Color.WHITE);
			
			shiftList = new JPanel();//list of employees in shift
			shiftList.setBackground(Color.WHITE);

			listOfEmployees=MainController.getEmployees();//returns an array of Employees
			listOfEmployeeButtons=new JToggleButton[listOfEmployees.length];
			
			employeeList.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
			shiftList.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));

			//list with the employees not in the sift
			employeesHeader=new JTextArea("Not in current shift: ");
			employeesHeader.setBackground(Color.WHITE);
			employeesHeader.setBorder(null);
			employeesHeader.setFont(new Font("Monaco", Font.BOLD, 25));
			employeesHeader.setPreferredSize(new Dimension(380,35));
			employeesHeader.setEditable(false);
			employeeList.add(employeesHeader);
			
			//list with the employees in the current shift
			shiftsHeader=new JTextArea("Current shift: ");
			shiftsHeader.setBackground(Color.WHITE);                
			shiftsHeader.setBorder(null);                           
			shiftsHeader.setFont(new Font("Monaco", Font.BOLD, 25));
			shiftsHeader.setPreferredSize(new Dimension(380,35));   
			shiftsHeader.setEditable(false);
			shiftList.add(shiftsHeader);
			
			//count each list to determine the size of the panels that will hold the lists
			int activeCount=1,inactiveCount=1;
			
			//add to lists according to shift status and count
			for(int i=0;i<listOfEmployees.length;i++)//loop that adds converted employee panels to list
			{
				listOfEmployeeButtons[i]=createEmployeeItem(listOfEmployees[i]);
				if(listOfEmployees[i].isActive()) {
					shiftList.add(listOfEmployeeButtons[i]);
					activeCount++;
				}else {
					employeeList.add(listOfEmployeeButtons[i]);
					inactiveCount++;
				}
			}
			
			//use the counters to set the size
			employeeList.setPreferredSize(new Dimension(300,activeCount*35));
			shiftList.setPreferredSize(new Dimension(300,inactiveCount*35));
			
		}catch(Exception e) {
			System.out.println("Managerview.loadEmployeeList error********");
			JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
		}
	}
	private JToggleButton createEmployeeItem(Employee e) {
		//create list item panel which stacks onto the the main list panel
		JToggleButton employeeButton=new JToggleButton();
		employeeButton.setBackground(Color.WHITE);
		employeeButton.setBorder(null);
		
		JLabel name=new JLabel(" ["+e.getEmployeeId()+"]: "+e.getFullName());
		name.setFont(new Font("Monaco", Font.BOLD, 20));//the font is what determines the size since swing doesn't give a single shit what i want
		
		employeeButton.add(name);
		employeeButton.setPreferredSize(new Dimension(380,35));
		employeeButton.addActionListener(this);
		return employeeButton;
	}
	private void refreshShiftList() {
		shiftsTab.removeAll();//refreshing list
		loadShiftPanelContainers();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		//method runs only when an object with an ationlistener is pressed
		
		//log out button
		if(e.getSource().equals(logOutButton)) {
			frame.changePanel(new LogInView(frame));
			System.out.println("Log In Panel Loaded");
			MainController.logOut();
			System.out.println("User Logged Out");
		}
		//shifts Tab
		for(int i=0;i<listOfEmployeeButtons.length;i++) {
			if(e.getSource().equals(listOfEmployeeButtons[i])) {
				for(int k=0;k<listOfEmployeeButtons.length;k++) {
					if(listOfEmployeeButtons[k]!=listOfEmployeeButtons[i]) {//make sure to keep selected object marked
						listOfEmployeeButtons[k].setSelected(false);//unmark all other objects in list
						repaint();
					}else {
						selectedEmployee=k;
						if(listOfEmployees[k].isActive()) {
							addToShiftButton.setEnabled(false);//employees already in the shift cant be added again
							removeFromShiftButton.setEnabled(true);
						}else {
							addToShiftButton.setEnabled(true);//employees not in the shift cant be removed
							removeFromShiftButton.setEnabled(false);
						}
						repaint();
					}
				}
			}
		}
		
		if(e.getSource().equals(addToShiftButton)) {
			try {
				//to insert an employee to the shift, his status is altered in the database
				//then the list is reloaded from the database
				MainController.updateEmployeeStatus(listOfEmployees[selectedEmployee].getEmployeeId(),"active");
				refreshShiftList();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(e.getSource().equals(removeFromShiftButton)) {
			try {
				//to remove an employee from the shift, his status is altered in the database
				//then the list is reloaded from the database
				MainController.updateEmployeeStatus(listOfEmployees[selectedEmployee].getEmployeeId(),"inactive");
				refreshShiftList();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
			}
		}
		if(e.getSource().equals(refreshShiftButton)) {
			refreshShiftList();
		}
		
		//Orders Tab
		for(JToggleButton selectedButton : waitingOrderButtons) {
			if(e.getSource().equals(selectedButton)) {
				for(int i=0;i<waitingOrderButtons.size();i++){
					if(selectedButton!=waitingOrderButtons.get(i)) {//make sure to keep selected object marked
						waitingOrderButtons.get(i).setSelected(false);//unmark all other objects in list
						repaint();
					}else {
						Order o = waitingOrders.get(waitingOrders.size()-1-i);
						loadOrderInfo(o);
					}
				}
				for(JToggleButton btn : inProgressOrderButtons){
						btn.setSelected(false);//unmark all other objects in list
						repaint();
				}
				//if the order pressed is waiting, the start order button is enabled
				//and the finish order button disabled
				finishOrderButton.setEnabled(false);
				startOrderButton.setEnabled(true);
			}
		}
		
		for(JToggleButton selectedButton : inProgressOrderButtons) {
			if(e.getSource().equals(selectedButton)) {
				for(int i=0;i<inProgressOrderButtons.size();i++){
					if(selectedButton!=inProgressOrderButtons.get(i)) {//make sure to keep selected object marked
						inProgressOrderButtons.get(i).setSelected(false);//unmark all other objects in list
						repaint();
					}else {
						Order o = inProgressOrders.get(inProgressOrders.size()-1-i);
						loadOrderInfo(o);
					}
				}
				for(JToggleButton btn : waitingOrderButtons){
						btn.setSelected(false);//unmark all other objects in list
						repaint();
				}
				//if the order pressed is in progress, the finish order button is enabled
				//and the start order button disabled
				startOrderButton.setEnabled(false);
				finishOrderButton.setEnabled(true);
			}
		}
		
		if(e.getSource().equals(startOrderButton)) {
			for(int i=0;i<waitingOrderButtons.size();i++){
				if(waitingOrderButtons.get(i).isSelected()) {
					Order o= waitingOrders.get(waitingOrders.size()-1-i);
					try {
						//update status in db then reload
						MainController.startOrder(o.getOrderId());
						refreshOrderList();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
		if(e.getSource().equals(finishOrderButton)) {
			for(int i=0;i<inProgressOrderButtons.size();i++){
				if(inProgressOrderButtons.get(i).isSelected()) {
					Order o= inProgressOrders.get(inProgressOrders.size()-1-i);
					try {
						//update status in db then reload
						MainController.finishOrder(o.getOrderId());
						refreshOrderList();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		if(e.getSource().equals(refreshOrderButton)) {
			refreshOrderList();
		}
	}
}
