package view;
import controller.*;
import model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.File;


public class ManagerView extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -1300461070144653453L;
	//atributes
	private static int screenSize=70;
	private static int listSize=400;
	
	private Program frame;
	
	private JPanel mainPanel;//contains the tabbed panels
	private JPanel lowerPanel;//contains the log out button
	private JButton logOutButton;
	
	Color blueOne=null;//dark blue
	Color blueTwo=new Color(120,106,241);//light blue
	Color blueThree=new Color(199,222,240);//barely even blue
	
	//the different panels were made for organizational purposes
	private JTabbedPane tabbedPane;
	
	private JLabel homeBackground;
	
	//different tabs for each purpose
	private JPanel homeTab;
	private JPanel productsTab;
	private JPanel employeeTab;
	
	//predefined sizes and fonts
	private Dimension sizeOfTabPanels;
	private Dimension sizeOfLists;
	private Dimension sizeOfInputField;
	private Font actionPanelFont;
	private Dimension actionPanelButtonSize;
	private Dimension actionPanelSize;
	private Dimension infoPanelSize;
	private Object[] options = { "CONFIRM","CANCEL" };
	
	//Home Tab
	//manager sign up
	//organizational panels
	private JPanel addManagementUserPanel;
	private JLabel addManagementUserTitleLabel;
	private JPanel addManagementUserIdPanel;
	private JPanel addManagementUserFullNamePanel;
	private JPanel addManagementUserFirstNamePanel;
	private JPanel addManagementUserLastNamePanel;
	private JPanel addManagementUserPasswordPanel;
	private JPanel addManagementUserTypePanel;
	
	//predefined sizes
	private Dimension sizeOfAddManagerInputField;
	private Dimension sizeOfAddManagerNameInputField;
	private Font addManagerInputFont;

	//entry fields
	private JTextField addManagementUserIdField;
	private JTextField addManagementUserFirstNameField;
	private JTextField addManagementUserLastNameField;
	private JPasswordField addManagementUserPasswordField;
	private JComboBox<User.UserType> addManagementUserTypeComboBox;
	
	//confirmation button
	private JButton addManagementUserConfirmButton;
	
	//Product Tab
	private JPanel productList;//panel that holds the products
	private Product listOfProducts[];
	private JToggleButton listOfProductButtons[];//button for each product
	private JScrollPane productScrollPane;//scroll pane to view all the products
	private JTextArea productInfoPanel;//info of product is presented
	private JTextField searchEntryField;//search entry for a product
	private JButton searchButton;
	private JPanel productsPanelWithSearch;//the complete panel that holds all the above product objects
	
	//middle panel holds the action panel and a jlabel to present the a product's image
	private JPanel midPanel;
	//image
	private JLabel imageView;
	//action panel
	private JPanel actionProductPanel;
	private JButton addProductButton;
	private JButton removeProductButton;
	private JButton editProductButton; 
	private JButton refreshProductButton;
	
	//a joptionpane window opens to add aproduct
	private JPanel addProductInputPanel;//in joptionpane to add a new product
	private JPanel addProductPanel[];
	private String addProductPanelTitle[]= {"ID","Name","Amount","Weight","Price","Area","Row","Prep Time","Type","Image"};
	private JTextField addProductInputField[];
	private JButton addProductImageButton;//button to choose image from computer
	private JLabel addProductImageView;//view the image before accepting
	private String addProductImagePath;//path of image in computer files
	
	//a joptiopane pops up for editing a product
	private JPanel editProductInputPanel;//in joptionpane to add a new product
	private JPanel editProductPanel[];
	private String editProductPanelTitle[]= {"ID","Name","Amount","Weight","Price","Area","Row","Prep Time","Type","Image"};
	private JTextField editProductInputField[];
	private JButton editProductImageButton;
	private JLabel editProductImageView;
	private String editProductImagePath;
	//choose between categories
	private JComboBox<Product.ProductType> productTypeList;
	
	//Employee Tab
	private JPanel employeeList;
	private Employee listOfEmployees[];
	private JToggleButton listOfEmployeeButtons[];
	private JScrollPane employeeScrollPane;
	private JTextArea employeeInfoPanel;
	
	//no midpanel needed for workers as they do not have pictures, just the info
	private JPanel actionEmployeePanel;
	private JButton addEmployeeButton;
	private JButton removeEmployeeButton;
	private JButton editEmployeeButton; 
	private JButton refreshEmployeeButton;
	
	//a joptionpane pops up for adding a new employee
	private JPanel addEmployeeInputPanel;
	private JPanel addEmployeePanel[];//holds the text input boxes
	private String addEmployeePanelTitle[]= {"ID","First Name","Last Name","Phone Number","Gender"};
	private JTextField addEmployeeInputField[];
	
	//a new joptionpane pops up for editing the selected worker
	private JPanel editEmployeeInputPanel;
	private JPanel editEmployeePanel[];//holds the text input boxes
	private String editEmployeePanelTitle[]= {"ID","First Name","Last Name","Phone Number","Gender"};
	private JTextField editEmployeeInputField[];
	//choose from genders (only 2 so far)
	private JComboBox<Employee.Gender> employeeGenderList;
	
	//constructor
	public ManagerView(Program frame){
		super();
		setBackground(blueOne);//dark blue
		this.frame=frame;
		//set panel specifications
		setPreferredSize(new Dimension(16*screenSize,9*screenSize));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		frame.setResizable(false);
		frame.setLocation(new Point(400, 200));
		
		//define sizes and fonts
		sizeOfAddManagerInputField=new Dimension(300,30);
		sizeOfAddManagerNameInputField=new Dimension(150,30);
		addManagerInputFont=new Font("Trebuchet MS",Font.PLAIN,20);
		sizeOfTabPanels=new Dimension(16*screenSize,(int)8*screenSize);//to leave space for lower panel
		sizeOfLists=new Dimension(listSize,listSize+50);
		actionPanelFont=new Font("Trebuchet MS",Font.PLAIN,20);
		actionPanelButtonSize=new Dimension(180,40);
		actionPanelSize=new Dimension(220,500);
		infoPanelSize=new Dimension(450,500);
		
		//start up main panel
		mainPanel=new JPanel();
		mainPanel.setBorder(null);
		mainPanel.setBackground(blueOne);//dark blue
		mainPanel.setPreferredSize(new Dimension(16*screenSize,8*screenSize));
		
		setTabbedPane();
	
		add(mainPanel);
		
		setLowerPanel();

	}
	private void setTabbedPane() {
		//set up a tabbed pane for easy ui
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setFont(new Font("Trebuchet MS", Font.BOLD, 30));
		
		//calling all different tab building methods
		buildHomePanel();
		
		buildProductPanel();
		
		buildEmployeePanel();
	
		mainPanel.add(tabbedPane);
	}
	private void setLowerPanel() {
		//lower panel holds the logout button but can also be added to if needed
		lowerPanel=new JPanel();
		lowerPanel.setBackground(blueOne);
		lowerPanel.setPreferredSize(new Dimension(15*screenSize,screenSize));
		lowerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		lowerPanel.setBorder(null);

		logOutButton = new JButton("Log Out");
		logOutButton.setForeground(Color.WHITE);
		logOutButton.setBackground(blueTwo);//light blue
		logOutButton.setPreferredSize(new Dimension(screenSize*2,screenSize/2));
		logOutButton.setFont(new Font("Trebuchet MS", Font.BOLD, 25));
		logOutButton.addActionListener(this);
		
		lowerPanel.add(logOutButton);
		
		add(lowerPanel);
	}
	//home methods
	private void buildHomePanel()  {
		homeTab=new JPanel();
		homeTab.setBackground(blueThree);
		homeTab.setBorder(null);
		homeTab.setPreferredSize(sizeOfTabPanels);
		
		loadHomePanelContainers();
		
		//home background
		homeBackground = new JLabel();
		homeBackground.setBounds(0, 0, 1120, 510);
		homeBackground.setIcon(new ImageIcon(ManagerView.class.getResource("/Images/mgr_bg.png")));
		homeTab.add(homeBackground);
	
		
		tabbedPane.addTab("Home", homeTab);
		tabbedPane.setEnabledAt(0, true);
		tabbedPane.setBackgroundAt(0, blueOne);//dark bue
		tabbedPane.setForegroundAt(0, Color.BLACK);
	}
	private void loadHomePanelContainers(){
		loadManagerSignUp();
	}
	private void loadManagerSignUp() {
		//panel that holds signup
		addManagementUserPanel=new JPanel();
		addManagementUserPanel.setBounds(650, 20, 450, 500);
		addManagementUserPanel.setPreferredSize(infoPanelSize);
		addManagementUserPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		addManagementUserPanel.setBackground(blueThree);
		
		loadInnerAddManagementUserFields();
		homeTab.setLayout(null);
		
		homeTab.add(addManagementUserPanel);
	}
	private void loadInnerAddManagementUserFields() {
		//generic label
		addManagementUserTitleLabel=new JLabel();
		addManagementUserTitleLabel.setText("Add New Management User");
		addManagementUserTitleLabel.setFont(new Font("Trebuchet MS",Font.BOLD,18));
				
		//panels the will hold the entry textfields fro mgr signup
		addManagementUserIdPanel=new JPanel();
		addManagementUserFullNamePanel=new JPanel();
		addManagementUserFirstNamePanel=new JPanel();
		addManagementUserLastNamePanel=new JPanel();
		addManagementUserPasswordPanel=new JPanel();
		addManagementUserTypePanel=new JPanel();
		
		addManagementUserIdPanel.setBorder(new TitledBorder(null, "Personal ID :", TitledBorder.LEADING,TitledBorder.TOP,null, null));
		addManagementUserFullNamePanel.setBorder(new TitledBorder(null, "Name :", TitledBorder.LEADING,TitledBorder.TOP,null, null));
		addManagementUserFirstNamePanel.setBorder(new TitledBorder(null, "First :", TitledBorder.LEADING,TitledBorder.TOP,null, null));
		addManagementUserLastNamePanel.setBorder(new TitledBorder(null, "Last :", TitledBorder.LEADING,TitledBorder.TOP,null, null));
		addManagementUserPasswordPanel.setBorder(new TitledBorder(null, "Password :", TitledBorder.LEADING,TitledBorder.TOP,null, null));
		addManagementUserTypePanel.setBorder(new TitledBorder(null, "Type :", TitledBorder.LEADING,TitledBorder.TOP,null, null));
		
		addManagementUserIdPanel.setBackground(blueThree);
		addManagementUserFullNamePanel.setBackground(blueThree);
		addManagementUserFirstNamePanel.setBackground(blueThree);
		addManagementUserLastNamePanel.setBackground(blueThree);
		addManagementUserPasswordPanel.setBackground(blueThree);
		addManagementUserTypePanel.setBackground(blueThree);
		
		//full name panel will hold name panels horizontally
		addManagementUserFullNamePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		//entry textfields that take user input
		addManagementUserIdField=new JTextField();
		addManagementUserFirstNameField=new JTextField();
		addManagementUserLastNameField=new JTextField();
		addManagementUserPasswordField=new JPasswordField();
		addManagementUserTypeComboBox=new JComboBox<>(User.UserType.values());
		
		addManagementUserIdField.setPreferredSize(sizeOfAddManagerInputField);
		addManagementUserFirstNameField.setPreferredSize(sizeOfAddManagerNameInputField);
		addManagementUserLastNameField.setPreferredSize(sizeOfAddManagerNameInputField);
		addManagementUserPasswordField.setPreferredSize(sizeOfAddManagerInputField);
		addManagementUserTypeComboBox.setPreferredSize(sizeOfAddManagerInputField);

		addManagementUserIdField.setFont(addManagerInputFont);
		addManagementUserFirstNameField.setFont(addManagerInputFont);
		addManagementUserLastNameField.setFont(addManagerInputFont);
		addManagementUserPasswordField.setFont(addManagerInputFont);
		addManagementUserTypeComboBox.setFont(addManagerInputFont);
		
		//setting the combo box to be empty
		addManagementUserTypeComboBox.setSelectedIndex(-1);
		addManagementUserTypeComboBox.setBackground(Color.WHITE);
		
		//adding the entry fields to their panels
		addManagementUserIdPanel.add(addManagementUserIdField);
		addManagementUserFirstNamePanel.add(addManagementUserFirstNameField);
		addManagementUserLastNamePanel.add(addManagementUserLastNameField);
		addManagementUserPasswordPanel.add(addManagementUserPasswordField);
		addManagementUserTypePanel.add(addManagementUserTypeComboBox);
		
		//full name panel will hold separate name panels
		addManagementUserFullNamePanel.add(addManagementUserFirstNamePanel);
		addManagementUserFullNamePanel.add(addManagementUserLastNamePanel);
		
		//Button
		addManagementUserConfirmButton=new JButton("Confirm");
		addManagementUserConfirmButton.setPreferredSize(sizeOfAddManagerInputField);
		addManagementUserConfirmButton.addActionListener(this);
		addManagementUserConfirmButton.setBackground(blueTwo);
		addManagementUserConfirmButton.setForeground(Color.WHITE);
		addManagementUserConfirmButton.setFont(actionPanelFont);
		
		//adding the panels into the panel
		addManagementUserPanel.add(addManagementUserTitleLabel);
		addManagementUserPanel.add(addManagementUserIdPanel);
		addManagementUserPanel.add(addManagementUserFullNamePanel);
		addManagementUserPanel.add(addManagementUserPasswordPanel);
		addManagementUserPanel.add(addManagementUserTypePanel);
		addManagementUserPanel.add(addManagementUserConfirmButton);
	}
	private boolean addManagerIsValid() {
		boolean isValid=true;
		
		String id=addManagementUserIdField.getText().toString();
		String firstName=addManagementUserFirstNameField.getText().toString();
		String lastName=addManagementUserLastNameField.getText().toString();
		String pass=String.valueOf(addManagementUserPasswordField.getPassword());
		int type=addManagementUserTypeComboBox.getSelectedIndex();
		
		if(!id.isEmpty()) {
			try {
				//if the id doesnt parse then it contains a letter
				Integer.parseInt(id);
				addManagementUserIdField.setBackground(Color.WHITE);
			}catch(Exception e) {
				isValid=false;
				addManagementUserIdField.setBackground(Color.RED);
			}
			if(id.length()>9 && isValid) {
				isValid=false;
				addManagementUserIdField.setBackground(Color.RED);
			}
		}else {
			//if the id is left empty
			isValid=false;
			addManagementUserIdField.setBackground(Color.RED);
		}
		
		if(!firstName.isEmpty()) {
			//if name contains a number
			if(firstName.matches(".*\\d.*")) {
				isValid=false;
				addManagementUserFirstNameField.setBackground(Color.RED);
			}else 
				addManagementUserFirstNameField.setBackground(Color.WHITE);
		}else {
			//if the name is left empty
			isValid=false;
			addManagementUserFirstNameField.setBackground(Color.RED);
		}
		
		if(!lastName.isEmpty()) {
			//if name contains a number
			if(lastName.matches(".*\\d.*")) {
				isValid=false;
				addManagementUserLastNameField.setBackground(Color.RED);
			}else 
				addManagementUserLastNameField.setBackground(Color.WHITE);
		}else {
			//if the name is left empty
			isValid=false;
			addManagementUserLastNameField.setBackground(Color.RED);
		}
		
		if(pass.isEmpty()) {
			isValid=false;
			addManagementUserPasswordField.setBackground(Color.RED);
		}else {
			addManagementUserPasswordField.setBackground(Color.WHITE);
		}
		
		if(type==-1) {
			isValid=false;
			addManagementUserTypeComboBox.setBackground(Color.RED);
		}else 
			addManagementUserTypeComboBox.setBackground(Color.WHITE);
		return isValid;
	}
	private void addNewManager() {
		String id=addManagementUserIdField.getText().toString();
		String firstName=addManagementUserFirstNameField.getText().toString();
		String lastName=addManagementUserLastNameField.getText().toString();
		String pass=String.valueOf(addManagementUserPasswordField.getPassword());
		String type=addManagementUserTypeComboBox.getSelectedItem().toString();
		try {
			MainController.addNewManager(id,firstName,lastName,pass,type);
			refreshAddManager();
			JOptionPane.showMessageDialog(null, "Manager Created Successfully", null, JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void refreshAddManager() {
		addManagementUserPanel.removeAll();//refreshing list
		loadInnerAddManagementUserFields();
	}
	//product methods 
	private void buildProductPanel()  {
		productsTab=new JPanel();
		productsTab.setBackground(blueThree);
		productsTab.setPreferredSize(sizeOfTabPanels);
			
		loadProductPanelContainers("");
			
		tabbedPane.addTab("Products", productsTab);
		tabbedPane.setEnabledAt(1, true);
		tabbedPane.setBackgroundAt(1, blueOne);
		tabbedPane.setForegroundAt(1, Color.BLACK);
	}
	private void loadProductPanelContainers(String searchEntry) {
		//loads the parts of the tab
		loadProductList(searchEntry);
		loadMidPanel();
		loadProductInfo(productsTab);
	}
	private void loadProductList(String searchEntry) {
		try {
			productList = new JPanel();//list of products for manager to choose from
			productList.setBackground(Color.WHITE);

			listOfProducts=MainController.getProducts(searchEntry);//returns an array list of products
			listOfProductButtons=new JToggleButton[listOfProducts.length];
			
			productList.setPreferredSize(new Dimension(300,listOfProducts.length*35));
			productList.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));

			for(int i=0;i<listOfProducts.length;i++)//loop that adds converted product panels to list
			{
				listOfProductButtons[i]=createProductItem(listOfProducts[i]);
				productList.add(listOfProductButtons[i]);
			}
			
			productScrollPane = new JScrollPane(productList);
			productScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			productScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			productScrollPane.setPreferredSize(sizeOfLists);
			
			searchEntryField=new JTextField();
			searchEntryField.setBackground(Color.WHITE);
			searchEntryField.setFont(new Font("Trebuchet MS",Font.PLAIN,20));
			searchEntryField.setPreferredSize(new Dimension(275,30));
			searchEntryField.setText(searchEntry);
			
			searchButton=new JButton("Search");
			searchButton.setFont(new Font("Trebuchet MS",Font.PLAIN,20));
			searchButton.setPreferredSize(new Dimension(100,30));
			searchButton.addActionListener(this);
			
			productsPanelWithSearch=new JPanel();
			productsPanelWithSearch.setPreferredSize(new Dimension(400,500));
			productsPanelWithSearch.setBackground(blueThree);
			productsPanelWithSearch.add(searchEntryField);
			productsPanelWithSearch.add(searchButton);
			productsPanelWithSearch.add(productScrollPane);
			
			productsTab.add(productsPanelWithSearch);
			revalidate();
			
		}catch(Exception e) {
			System.out.println("Managerview.loadProductList error");
			e.printStackTrace();
		}
	}
	private void refreshProductList(String searchEntry) {

		productsTab.removeAll();//refreshing list
		loadProductPanelContainers(searchEntry);
	}
	private JToggleButton createProductItem(Product p) {
		//create list item panel which stacks onto the the main list panel
		
		JToggleButton productButton=new JToggleButton();
		productButton.setBackground(Color.WHITE);
		productButton.setBorder(null);
		
		JLabel name=new JLabel(" ["+p.getProductId()+"]: "+p.getProductName());
		name.setFont(new Font("Monaco", Font.BOLD, 20));//the font is what determines the size since swing doesn't give a single shit what i want
		
		productButton.add(name);
		productButton.setPreferredSize(new Dimension(380,35));
		productButton.addActionListener(this);
		return productButton;
	}
	private void loadMidPanel() {
		midPanel=new JPanel();
		midPanel.setBackground(blueThree);
		midPanel.setPreferredSize(new Dimension(220,500));
		midPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		
		loadProductActionPanel();//panel with buttons to choose from
		loadImageView();
		productsTab.add(midPanel);
	}
	private void loadProductActionPanel() {
		actionProductPanel=new JPanel();
		actionProductPanel.setBackground(blueThree);
		actionProductPanel.setPreferredSize(new Dimension(220,250));
		actionProductPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		
		addProductButton=new JButton("Add");
		editProductButton=new JButton("Edit");
		removeProductButton=new JButton("Remove");
		refreshProductButton=new JButton("Refresh");
		
		addProductButton.setPreferredSize(actionPanelButtonSize);
		editProductButton.setPreferredSize(actionPanelButtonSize);
		removeProductButton.setPreferredSize(actionPanelButtonSize);
		refreshProductButton.setPreferredSize(actionPanelButtonSize);
		
		addProductButton.addActionListener(this);
		editProductButton.addActionListener(this);
		removeProductButton.addActionListener(this);
		refreshProductButton.addActionListener(this);
		
		addProductButton.setFont(actionPanelFont);
		editProductButton.setFont(actionPanelFont);
		removeProductButton.setFont(actionPanelFont);
		refreshProductButton.setFont(actionPanelFont);
		
		actionProductPanel.add(addProductButton);
		actionProductPanel.add(editProductButton);
		actionProductPanel.add(removeProductButton);
		actionProductPanel.add(refreshProductButton);
		
		midPanel.add(actionProductPanel);
	}
	private void loadImageView() {
		//prepare image holder
		imageView=new JLabel();
		imageView.setBackground(Color.WHITE);
		imageView.setPreferredSize(new Dimension(200,200));
		midPanel.add(imageView);
	}
	private void loadProductInfo(JPanel p) {
		productInfoPanel = new JTextArea("\n  [Select a product to see its information.] ");
		productInfoPanel.setPreferredSize(infoPanelSize);
		productInfoPanel.setBorder(new MatteBorder(1,1,1,1,Color.BLACK));
		productInfoPanel.setFont(new Font("Trebuchet MS",Font.BOLD,18));
		productInfoPanel.setEditable(false);
		
		p.add(productInfoPanel);
		validate();
	}
	private void setProductInfo(Product p) {
		productInfoPanel.setText(p.toString());
		if(p.getImage()==null)//empty JLabel
			imageView.setIcon(null);
		else
			imageView.setIcon(ResizeImage(imageView,p));//product image
		
	}
	private ImageIcon ResizeImage(JLabel imageView,Product p){
		//resize image to fit JLabel
        ImageIcon MyImage = new ImageIcon(p.getImage());
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(imageView.getWidth(), imageView.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        return image;
    }
	private void addProduct() {
		addProductInputPanel=new JPanel();
		addProductInputPanel.setPreferredSize(new Dimension(400, 520));
		addProductInputPanel.setLayout(new FlowLayout());
		sizeOfInputField=new Dimension(150,20);
		//---------------------------------------
		int numOfInputPanels=10;//amount of overall input panels
		int numOfTextInputs=8;//amount of text inputs
		addProductPanel=new JPanel[numOfInputPanels];
		addProductInputField=new JTextField[numOfTextInputs];
		for(int i=0;i<numOfInputPanels;i++)// set up the product value entries
		{
				//shaping the panels that will hold the input JTextFields
			addProductPanel[i] = new JPanel();
			addProductPanel[i].setBorder(new TitledBorder(null, addProductPanelTitle[i]+":", TitledBorder.LEADING,TitledBorder.TOP,null, null));
			addProductInputPanel.add(addProductPanel[i]);
			if(i==numOfInputPanels-2) {
					//JComboBox added to choose from list of enums
				productTypeList=new JComboBox<>(Product.ProductType.values());
				productTypeList.setPreferredSize(sizeOfInputField);
				productTypeList.setSelectedIndex(-1);	//empty type
				productTypeList.setBackground(Color.WHITE);
				addProductPanel[i].add(productTypeList);			
			}
			else if(i==numOfInputPanels-1) {
				//button for image search
				addProductImageButton=new JButton("Upload Image");
				addProductImageButton.setPreferredSize(sizeOfInputField);
				addProductPanel[i].add(addProductImageButton);	
				addProductImageButton.addActionListener(this);		
			}
			else {
					//JTextFields for input
				addProductInputField[i]=new JTextField();//
				addProductInputField[i].setPreferredSize(sizeOfInputField);
				addProductPanel[i].add(addProductInputField[i]);
			}
		}
		addProductImageView=new JLabel();
		addProductImageView.setBackground(Color.WHITE);
		addProductImageView.setPreferredSize(new Dimension(200,200));
		addProductInputPanel.add(addProductImageView);
		//start of product addition
		boolean inputIsValid;
		int choice=1;//choice 1 is cancel
		do {
			inputIsValid=true;
			choice=JOptionPane.showOptionDialog(null, addProductInputPanel, "Add a new Product",JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
			if(choice==0)//choice 0 is confirm
			{
				for(int i=0;i<numOfTextInputs;i++)
				{
					//if an input field is left empty
					if(addProductInputField[i].getText().length()<=0){ //if value is left empty
						addProductInputField[i].setBackground(new Color(0.9f, 0.5f, 0.5f));//red color to for incorrect input
						inputIsValid=false;
					}
					//calls boolean function that contains multiple constraints and conditions
					else if(!addProductInputIsValid(i)) {
						addProductInputField[i].setBackground(new Color(0.9f, 0.5f, 0.5f));//red color to for incorrect input
						inputIsValid=false;
					}
					else addProductInputField[i].setBackground(Color.WHITE);
				}
				if(productTypeList.getSelectedIndex()==-1) {
					productTypeList.setBackground(new Color(0.9f, 0.5f, 0.5f));
					inputIsValid=false;
				}
				else
					productTypeList.setBackground(Color.WHITE);
				if(inputIsValid) {
					try {
						MainController.addNewProduct(
								Integer.parseInt(addProductInputField[0].getText().trim()),
								addProductInputField[1].getText().trim(),
								productTypeList.getSelectedItem().toString(),
								Integer.parseInt(addProductInputField[2].getText().trim()),
								Double.parseDouble(addProductInputField[3].getText().trim()),
								Double.parseDouble(addProductInputField[4].getText().trim()),
								addProductInputField[5].getText().trim(),
								Integer.parseInt(addProductInputField[6].getText().trim()),
								Integer.parseInt(addProductInputField[7].getText().trim()),
								addProductImagePath
								);
						//refreshes only if product addition has been executed successfully
						refreshProductList("");
					} catch (NumberFormatException e) {
						System.out.println("*****add product number casting problem should not be happening*******");
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}
					choice=1;//to exit loop
				}
			}
		}while(choice!=1 && choice != -1); //-1 is the top corner 'X'
	}
	private ImageIcon ResizeImage(JLabel imageView,String imgPath){
        ImageIcon MyImage = new ImageIcon(imgPath);
        Image img = MyImage.getImage();
        Image newImage = img.getScaledInstance(imageView.getWidth(), imageView.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImage);
        return image;
    }
	private boolean addProductInputIsValid(int index) {
		/*
		 * this Method checks every input according to index
		 * the index determines which type of checks should be implemented on the input:
		 * 0 is the product id
		 * 1 is the product name
		 * 2 is the starting amount
		 * 3 is the weight per unit
		 * 4 is the price per unit 
		 * 5 is the area
		 * 6 is the row
		 * 7 is the prep time
		 */
		String tempString=addProductInputField[index].getText().trim();
		if(index==0) {
			//1. is a proper integer number with no decimals or fractions
			try {
				Integer.parseInt(tempString);//makes sure the input is a proper int
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. maximum of 11 digits
			if(tempString.length()>11)
				return false;
			
			//3. make sure it doesn't already exists
			for(int i=0;i<listOfProducts.length;i++){
				if(Integer.parseInt(tempString)==listOfProducts[i].getProductId())
					return false;
			}
		}
		else if(index==1) {
			//1. maximum of 50 letters
			if(tempString.length()>50) 
				return false;
			
			//2. name cannot contain digits
			if(tempString.matches(".*\\d.*"))
				return false;
			
			//3. name cannot already exist
			for(int i=0;i<listOfProducts.length;i++){
				if(tempString.equals(listOfProducts[i].getProductName()))
					return false;
			}
		}
		else if(index==2) {
			//1. is a proper integer number with no decimals or fractions
			try {
				Integer.parseInt(tempString);//makes sure the input is a proper int
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. maximum of 11 digits
			if(tempString.length()>11)
				return false; 
			
			//3. if amount is less than zero 
			if(Integer.parseInt(tempString)<0)
				return false;
		}
		else if(index==3) {
			//1. is a proper type with suitable content
			try {
				Double.parseDouble(tempString);
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. item has to have positive weight
			//balloons can be 0 :3
			if(Double.parseDouble(tempString)<0.0)
				return false;
		}
		else if(index==4) {
			//1. is a proper type with suitable content
			try {
				Double.parseDouble(tempString);
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. cannot be negatively priced or cost 0 (free)
			if(Double.parseDouble(tempString)<=0.0)
				return false;
		}
		else if(index==5) {
			//areas have only one letter
			if(tempString.length()!=1) 
				return false;
			//area names have to be capital letters
			if(tempString.toLowerCase().equals(tempString))
				return false;
			if(!(tempString.equals("A") || 
					tempString.equals("B") || 
					tempString.equals("C") || 
					tempString.equals("D") || 
					tempString.equals("E") || 
					tempString.equals("F") || 
					tempString.equals("G") || 
					tempString.equals("H") || 
					tempString.equals("I") || 
					tempString.equals("J") || 
					tempString.equals("K") || 
					tempString.equals("L"))) 
				return false;
		}
		else if(index==6) {
			int row=-1;
			//1. is a proper integer number with no decimals or fractions
			try {
				row=Integer.parseInt(tempString);//makes sure the input is a proper int
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. maximum of 1 digit for a row
			if(tempString.length()!=1)
				return false;
			
			String area = addProductInputField[5].getText().toString();
			//3. areas A to F have different rows
			if((area.equals("A") || 
					area.equals("B") || 
					area.equals("C") || 
					area.equals("D") || 
					area.equals("E") || 
					area.equals("F")) &&
					(row<0 ||
					row>4)) 
				return false;
			//4. areas G to L dont have different rows so the row must be 0
			if((area.equals("G") || 
					area.equals("H") || 
					area.equals("I") || 
					area.equals("J") || 
					area.equals("K") || 
					area.equals("L")) &&
					row!=0) 
				return false;
		}
		else if(index==7) {
			//1. is a proper integer number with no decimals or fractions
			try {
				int prep=Integer.parseInt(tempString);//makes sure the input is a proper int
				//minimum prep time is 10 seconds
				if(prep<10)
					return false;
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. maximum of 11 digits
			if(tempString.length()>11)
				return false;
			
		}
		
		//all conditions are met
		return true;
	}
	private void editProduct(Product productForEdit) {
		editProductInputPanel=new JPanel();
		editProductInputPanel.setPreferredSize(new Dimension(400, 520));
		editProductInputPanel.setLayout(new FlowLayout());
		sizeOfInputField=new Dimension(150,20);
		//---------------------------------------
		int numOfInputPanels=10;//amount of overall input panels
		int numOfTextInputs=8;//amount of text inputs
		editProductImagePath=null;
		editProductPanel=new JPanel[numOfInputPanels];
		editProductInputField=new JTextField[numOfTextInputs];
		for(int i=1;i<numOfInputPanels;i++)// set up the product value entries
		{
			editProductPanel[i] = new JPanel();
			editProductPanel[i].setBorder(new TitledBorder(null, editProductPanelTitle[i]+":", TitledBorder.LEADING,TitledBorder.TOP,null, null));
			editProductInputPanel.add(editProductPanel[i]);
			if(i==numOfInputPanels-2) {
				productTypeList=new JComboBox<>(Product.ProductType.values());
				productTypeList.setPreferredSize(sizeOfInputField);
				productTypeList.setSelectedIndex(-1);//empty type
				productTypeList.setBackground(Color.WHITE);
				editProductPanel[i].add(productTypeList);
			}
			else if(i==numOfInputPanels-1) {
				//button for image search
				editProductImageButton=new JButton("Upload Image");
				editProductImageButton.setPreferredSize(sizeOfInputField);
				editProductPanel[i].add(editProductImageButton);	
				editProductImageButton.addActionListener(this);	
			}
			else {
				editProductInputField[i]=new JTextField();
				editProductInputField[i].setPreferredSize(sizeOfInputField);
				editProductPanel[i].add(editProductInputField[i]);
			}
		}
		editProductImageView=new JLabel();
		editProductImageView.setBackground(Color.WHITE);
		editProductImageView.setPreferredSize(new Dimension(200,200));
		editProductInputPanel.add(editProductImageView);
		//start of editting
		boolean inputIsValid;
		int choice=1;//choice 1 is cancel
		do {
			inputIsValid=true;
			choice=JOptionPane.showOptionDialog(null, editProductInputPanel, "Edit a Product",JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
			if(choice==0)//choice 0 is confirm
			{
				for(int i=1;i<numOfTextInputs;i++){
					if(!editProductInputField[i].getText().equals("")){
						//calls boolean function that contains multiple constraints and conditions
						if(!editProductInputIsValid(i)) {
							editProductInputField[i].setBackground(new Color(0.9f, 0.5f, 0.5f));//red color to for incorrect input
							inputIsValid=false;
						}
						else
							try{
								MainController.editProduct(productForEdit.getProductId(),i,editProductInputField[i].getText().trim());
								//refreshes only if product editing has been executed successfully
								refreshProductList("");
							}catch(Exception e) {
								//errors that reach this point are printed in a JOptionPane dialog box
								JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
							}
					}
					else editProductInputField[i].setBackground(Color.WHITE);
				}
				if(productTypeList.getSelectedIndex()!=-1)	//if an option was selected
					try{
						MainController.editProduct(productForEdit.getProductId(),numOfInputPanels-2,productTypeList.getSelectedItem().toString());
					}catch(Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}
				if(editProductImagePath!=null)
					try{
						MainController.editProduct(productForEdit.getProductId(),numOfInputPanels-1,editProductImagePath);
					}catch(Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "Image Alert", JOptionPane.ERROR_MESSAGE);
					}
				if(inputIsValid) {
					choice=1;//to exit loop
				}
			}
		}while(choice!=1 && choice != -1); //-1 is the top corner 'X'
	}
	private boolean editProductInputIsValid(int index) {
		/*
		 * this Method checks every input according to index
		 * the index determines which type of checks should be implemented on the input:
		 * 1 is the product name
		 * 2 is the starting amount
		 * 3 is the weight per unit
		 * 4 is the price per unit 
		 */
		String tempString=editProductInputField[index].getText().trim();
		if(index==1) {
			//1. maximum of 50 letters
			if(tempString.length()>50) 
				return false;
			
			//2. name cannot contain digits
			if(tempString.matches(".*\\d.*"))
				return false;
			
			//3. name cannot already exist
			for(int i=0;i<listOfProducts.length;i++){
				if(tempString.equals(listOfProducts[i].getProductName()))
					return false;
			}
		}
		else if(index==2) {
			//1. is a proper integer number with no decimals or fractions
			try {
				Integer.parseInt(tempString);//makes sure the input is a proper int
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. maximum of 11 digits
			if(tempString.length()>11)
				return false; 
			
			//3. if amount is less than zero 
			if(Integer.parseInt(tempString)<0)
				return false;
		}
		else if(index==3) {
			//1. is a proper type with suitable content
			try {
				Double.parseDouble(tempString);
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. item has to have positive weight
			//balloons can be 0 :3
			if(Double.parseDouble(tempString)<0.0)
				return false;
		}
		else if(index==4) {
			//1. is a proper type with suitable content
			try {
				Double.parseDouble(tempString);
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. cannot be negatively priced or cost 0 (free)
			if(Double.parseDouble(tempString)<=0.0)
				return false;
		}
		
		//all conditions are met
		return true;
	}
	private void removeProduct(int productId) {
		try {
			int reply = JOptionPane.showConfirmDialog(null, "This product will be deleted permanently, proceed?", "Delete Product", JOptionPane.YES_NO_OPTION);
		    if (reply == JOptionPane.YES_OPTION) {
		    	MainController.deleteProduct(productId);
		        refreshProductList("");
		    }
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
		}
	}
	//employee methods start here
	private void buildEmployeePanel() {
		employeeTab=new JPanel();
		employeeTab.setBackground(blueThree);
		//employeeTab.setBackground(blueTwo);
		employeeTab.setPreferredSize(sizeOfTabPanels);
		
		loadEmployeePanelContainers();
		
		tabbedPane.addTab("Employees", employeeTab);
		tabbedPane.setEnabledAt(2, true);
		tabbedPane.setBackgroundAt(2, blueOne);
		tabbedPane.setForegroundAt(2, Color.BLACK);
	}
	private void loadEmployeePanelContainers() {
		loadEmployeeList();
		loadEmployeeActionPanel();
		loadEmployeeInfo(employeeTab);
	}
	private void loadEmployeeList() {
		try {
			employeeList = new JPanel();//list of products for manager to choose from
			employeeList.setBackground(Color.WHITE);

			listOfEmployees=MainController.getEmployees();//returns an array of Employees
			listOfEmployeeButtons=new JToggleButton[listOfEmployees.length];
			
			employeeList.setPreferredSize(new Dimension(300,listOfEmployees.length*35));
			employeeList.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));

			for(int i=0;i<listOfEmployees.length;i++)//loop that adds converted product panels to list
			{
				listOfEmployeeButtons[i]=createEmployeeItem(listOfEmployees[i]);
				employeeList.add(listOfEmployeeButtons[i]);
			}
			//scroll pane to allow the manager to view all employees regardless of the amount
			employeeScrollPane = new JScrollPane(employeeList);
			employeeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			employeeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			employeeScrollPane.setPreferredSize(sizeOfLists);
			employeeTab.add(employeeScrollPane);
			revalidate();
			
		}catch(Exception e) {
			System.out.println("Managerview.loadEmployeeList error********");
			e.printStackTrace();
		}
	}
	private void refreshEmployeeList() {

		employeeTab.removeAll();//refreshing list
		loadEmployeePanelContainers();
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
	private void loadEmployeeActionPanel() {
		actionEmployeePanel=new JPanel();
		actionEmployeePanel.setBackground(blueThree);
		actionEmployeePanel.setPreferredSize(actionPanelSize);
		actionEmployeePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		
		addEmployeeButton=new JButton("Add");
		editEmployeeButton=new JButton("Edit");
		removeEmployeeButton=new JButton("Remove");
		refreshEmployeeButton=new JButton("Refresh");
		
		addEmployeeButton.setPreferredSize(actionPanelButtonSize);
		editEmployeeButton.setPreferredSize(actionPanelButtonSize);
		removeEmployeeButton.setPreferredSize(actionPanelButtonSize);
		refreshEmployeeButton.setPreferredSize(actionPanelButtonSize);
		
		addEmployeeButton.addActionListener(this);
		editEmployeeButton.addActionListener(this);
		removeEmployeeButton.addActionListener(this);
		refreshEmployeeButton.addActionListener(this);
		
		addEmployeeButton.setFont(actionPanelFont);
		editEmployeeButton.setFont(actionPanelFont);
		removeEmployeeButton.setFont(actionPanelFont);
		refreshEmployeeButton.setFont(actionPanelFont);
		
		actionEmployeePanel.add(addEmployeeButton);
		actionEmployeePanel.add(editEmployeeButton);
		actionEmployeePanel.add(removeEmployeeButton);
		actionEmployeePanel.add(refreshEmployeeButton);
		
		employeeTab.add(actionEmployeePanel);
	}
	private void loadEmployeeInfo(JPanel p) {
		employeeInfoPanel = new JTextArea("\n  [Select an employee to see their information.] ");
		employeeInfoPanel.setPreferredSize(infoPanelSize);
		employeeInfoPanel.setBorder(new MatteBorder(1,1,1,1,Color.BLACK));
		employeeInfoPanel.setFont(new Font("Trebuchet MS",Font.PLAIN,20));
		employeeInfoPanel.setEditable(false);
		
		p.add(employeeInfoPanel);
		validate();
	}
	private void addEmployee() {
		addEmployeeInputPanel=new JPanel();
		addEmployeeInputPanel.setPreferredSize(new Dimension(175, 300));
		addEmployeeInputPanel.setLayout(new FlowLayout());
		sizeOfInputField=new Dimension(150,20);
		//---------------------------------------
		int numOfInputPanels=5;//amount of overall input panels
		int numOfTextInputs=4;//amount of text inputs
		addEmployeePanel=new JPanel[numOfInputPanels];
		addEmployeeInputField=new JTextField[numOfTextInputs];
		for(int i=0;i<numOfInputPanels;i++)// set up the employee value entries
		{
				//shaping the panels that will hold the input JTextFields
			addEmployeePanel[i] = new JPanel();
			addEmployeePanel[i].setBorder(new TitledBorder(null, addEmployeePanelTitle[i]+":", TitledBorder.LEADING,TitledBorder.TOP,null, null));
			addEmployeeInputPanel.add(addEmployeePanel[i]);
			if(i==4) {
					//JComboBox added to choose from list of enums
				employeeGenderList=new JComboBox<>(Employee.Gender.values());
				employeeGenderList.setPreferredSize(sizeOfInputField);
				employeeGenderList.setSelectedIndex(-1);	//empty type
				employeeGenderList.setBackground(Color.WHITE);
				addEmployeePanel[i].add(employeeGenderList);
			}
			else {
					//JTextFields for input
				addEmployeeInputField[i]=new JTextField();//
				addEmployeeInputField[i].setPreferredSize(sizeOfInputField);
				addEmployeePanel[i].add(addEmployeeInputField[i]);
			}
		}
		
		boolean inputIsValid;
		int choice=1;//choice 1 is cancel
		do {
			inputIsValid=true;
			choice=JOptionPane.showOptionDialog(null, addEmployeeInputPanel, "Add a new employee",JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
			if(choice==0)//choice 0 is confirm
			{
				for(int i=0;i<numOfTextInputs;i++)
				{
					//if an input field is left empty
					if( addEmployeeInputField[i].getText().length()<=0){ //if value is left empty
						addEmployeeInputField[i].setBackground(new Color(0.9f, 0.5f, 0.5f));//red color to for incorrect input
						inputIsValid=false;
					}
					//calls boolean function that contains multiple constraints and conditions
					else if(!addEmployeeInputIsValid(i)) {
						addEmployeeInputField[i].setBackground(new Color(0.9f, 0.5f, 0.5f));//red color to for incorrect input
						inputIsValid=false;
					}
					else addEmployeeInputField[i].setBackground(Color.WHITE);
				}
				if(employeeGenderList.getSelectedIndex()==-1) {
					employeeGenderList.setBackground(new Color(0.9f, 0.5f, 0.5f));
					inputIsValid=false;
				}
				else
					employeeGenderList.setBackground(Color.WHITE);
				if(inputIsValid) {
					try {
						MainController.addNewEmployee(
								Integer.parseInt(addEmployeeInputField[0].getText().trim()),
								addEmployeeInputField[1].getText().trim(),
								addEmployeeInputField[2].getText().trim(),
								addEmployeeInputField[3].getText().trim(),
								employeeGenderList.getSelectedItem().toString()
								);
						//refreshes only if employee addition has been executed successfully
						refreshEmployeeList();
					} catch (NumberFormatException e) {
						System.out.println("*****add employee number casting problem should not be happening*******");
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}
					choice=1;//to exit loop
				}
			}
		}while(choice!=1 && choice != -1); //-1 is the top corner 'X'
	}
	private boolean addEmployeeInputIsValid(int index) {
		/*
		 * this Method checks every input according to index
		 * the index determines which type of checks should be implemented on the input:
		 * 0 is the employee id
		 * 1 is the first name
		 * 2 is the last name
		 * 3 is the phone number 
		 */
		String tempString=addEmployeeInputField[index].getText().trim();
		if(index==0) {
			//1. is a proper integer number with no decimals or fractions
			try {
				Integer.parseInt(tempString);//makes sure the input is a proper int
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//2. a proper id has 8 to 9 digits
			if(!(tempString.length()==8 || tempString.length()==9))
				return false;
			
			//3. make sure it doesn't already exists
			for(int i=0;i<listOfEmployees.length;i++){
				if(Integer.parseInt(tempString)==listOfEmployees[i].getEmployeeId())
					return false;
			}
		}
		else if(index==1) {
			//1. maximum of 50 letters
			if(tempString.length()>50) 
				return false;
			
			//2. name cannot contain digits
			if(tempString.matches(".*\\d.*"))
				return false;
		}
		else if(index==2) {
			//1. maximum of 50 letters
			if(tempString.length()>50) 
				return false;
			
			//2. name cannot contain digits
			if(tempString.matches(".*\\d.*"))
				return false;
			
			//3. if full name exists
			for(int i=0;i<listOfEmployees.length;i++)
				if((addEmployeeInputField[1].getText().trim()+" "+addEmployeeInputField[2].getText().trim()).equals(listOfEmployees[i].getFullName()))
					return false;
		}
		else if(index==3) {
			//1. is a proper number with no decimals or fractions
			try {
				Integer.parseInt(tempString);//makes sure the input is a proper int
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//proper phone numbers have 10 digits
			if(tempString.length()!=10)
				return false;
			
			//phone number prefix has to be a 05
			if(!tempString.startsWith("05"))
				return false;
		}
		
		//all conditions are met
		return true;
	}
	private void editEmployee(Employee employeeForEdit) {
		editEmployeeInputPanel=new JPanel();//main panel in the joptionpane
		editEmployeeInputPanel.setPreferredSize(new Dimension(175, 325));
		editEmployeeInputPanel.setLayout(new FlowLayout());
		sizeOfInputField=new Dimension(150,20);
		//---------------------------------------
		int numOfInputPanels=5;//amount of overall input panels
		int numOfTextInputs=4;//amount of text inputs
		
		editEmployeePanel=new JPanel[numOfInputPanels];
		editEmployeeInputField=new JTextField[numOfTextInputs];
		for(int i=1;i<numOfInputPanels;i++)// set up the product value entries
		{
			editEmployeePanel[i] = new JPanel();
			editEmployeePanel[i].setBorder(new TitledBorder(null, editEmployeePanelTitle[i]+":", TitledBorder.LEADING,TitledBorder.TOP,null, null));
			editEmployeeInputPanel.add(editEmployeePanel[i]);
			if(i==4) {
				employeeGenderList=new JComboBox<>(Employee.Gender.values());
				employeeGenderList.setPreferredSize(sizeOfInputField);
				employeeGenderList.setSelectedIndex(-1);//empty type
				employeeGenderList.setBackground(Color.WHITE);
				editEmployeePanel[i].add(employeeGenderList);
			}
			else {
				editEmployeeInputField[i]=new JTextField();
				editEmployeeInputField[i].setPreferredSize(sizeOfInputField);
				editEmployeePanel[i].add(editEmployeeInputField[i]);
			}
		}
		boolean inputIsValid;
		int choice=1;//choice 1 is cancel
		do {
			inputIsValid=true;
			choice=JOptionPane.showOptionDialog(null, editEmployeePanel, "Edit an employee",JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
			if(choice==0)//choice 0 is confirm
			{
				for(int i=1;i<numOfTextInputs;i++)
				{
					if(!editEmployeeInputField[i].getText().equals("")){
						//calls boolean function that contains multiple constraints and conditions
						if(!editEmployeeInputIsValid(i)) {
							editEmployeeInputField[i].setBackground(new Color(0.9f, 0.5f, 0.5f));//red color to for incorrect input
							inputIsValid=false;
						}
						else
							try{
								MainController.editEmployee(employeeForEdit.getEmployeeId(),i,editEmployeeInputField[i].getText().trim());
								//refreshes only if employee editing has been executed successfully
								refreshEmployeeList();
							}catch(Exception e) {
								//errors that reach this point are printed in a JOptionPane dialog box
								JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
							}
					}
					else editEmployeeInputField[i].setBackground(Color.WHITE);
				}
				if(employeeGenderList.getSelectedIndex()!=-1)	//if an option was selected
					try{
						MainController.editEmployee(employeeForEdit.getEmployeeId(),5,employeeGenderList.getSelectedItem().toString());
					}catch(Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}
				if(inputIsValid) {
					choice=1;//to exit loop
				}
			}
		}while(choice!=1 && choice != -1); //-1 is the top corner 'X'
	}
	private boolean editEmployeeInputIsValid(int index) {
		/*
		 * this Method checks every input according to index
		 * the index determines which type of checks should be implemented on the input:
		 * 0 is the employee id (will never receive 0)
		 * 1 is the first name
		 * 2 is the last name
		 * 3 is the phone number 
		 */
		String tempString=editEmployeeInputField[index].getText().trim();
		if(index==1) {
			//1. maximum of 50 letters
			if(tempString.length()>50) 
				return false;
			
			//2. name cannot contain digits
			if(tempString.matches(".*\\d.*"))
				return false;
		}
		else if(index==2) {
			//1. maximum of 50 letters
			if(tempString.length()>50) 
				return false;
			
			//2. name cannot contain digits
			if(tempString.matches(".*\\d.*"))
				return false;
		}
		else if(index==3) {
			//1. is a proper number with no decimals or fractions
			try {
				Integer.parseInt(tempString);//makes sure the input is a proper int
			}catch(NumberFormatException e) { 
				e.printStackTrace();
				return false;
			}
			
			//proper phone numbers have 10 digits
			if(tempString.length()!=10)
				return false;
			
			//phone number prefix has to be a 05
			if(!tempString.startsWith("05"))
				return false;
		}
		
		//all conditions are met
		return true;
	}
	private void removeEmployee(int employeeId) {
		try {
			int reply = JOptionPane.showConfirmDialog(null, "This Employee will be deleted permanently, proceed?", "Delete Employee", JOptionPane.YES_NO_OPTION);
		    if (reply == JOptionPane.YES_OPTION) {
		    	MainController.deleteEmployee(employeeId);
		        refreshEmployeeList();
		    }
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
		}
	}
	//global action method
	@Override
	public void actionPerformed(ActionEvent e) {
		//log out 
		if(e.getSource().equals(logOutButton)) {
			frame.changePanel(new LogInView(frame));
			System.out.println("Log In Panel Loaded");
			MainController.logOut();
			System.out.println("User Logged Out");
		}
		//HomeTab actions
		
		if(e.getSource().equals(addManagementUserConfirmButton)) {
			if(addManagerIsValid()) {
				addNewManager();
			}
		}
		
		//product actions start here
		//fetch products all over again from database with the search entry
		if(e.getSource().equals(searchButton)) {
			refreshProductList(searchEntryField.getText().toString());
		}
		
		for(int i=0;i<listOfProductButtons.length;i++) {
			//if a button is selected the others are unselected
			if(e.getSource().equals(listOfProductButtons[i])) {
				for(int k=0;k<listOfProductButtons.length;k++) {
					if(listOfProductButtons[k]!=listOfProductButtons[i]) {//make sure to keep selected object marked
						listOfProductButtons[k].setSelected(false);//unmark all other objects in list
						repaint();
					}
				}
				setProductInfo(listOfProducts[i]);
			}
		}
		if(e.getSource().equals(addProductButton)) {
			addProduct();//opens an option dialog that contains info input
		}
		if(e.getSource().equals(addProductImageButton)) {
			//a file explorer window opens up letting you select an image
			JFileChooser fileChooser = new JFileChooser();
	         fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	         FileNameExtensionFilter filter = new FileNameExtensionFilter("*.IMAGE", "jpg","gif","png");
	         fileChooser.addChoosableFileFilter(filter);
	         int result = fileChooser.showSaveDialog(null);
	         if(result == JFileChooser.APPROVE_OPTION){
	             File selectedFile = fileChooser.getSelectedFile();
	             String path = selectedFile.getAbsolutePath();
	             //the icon is turned into an image and then previewed to the manager
	             addProductImageView.setIcon(ResizeImage(addProductImageView,path));
	             addProductImagePath = path;
	         }
	         else if(result == JFileChooser.CANCEL_OPTION){
	             System.out.println("No Data");
	         }
		}
		if(e.getSource().equals(editProductButton)) {
			//sends selected product for editing
			for(int k=0;k<listOfProductButtons.length;k++) {
				if(listOfProductButtons[k].isSelected()) {
					editProduct(listOfProducts[k]);
					break;
				}
			}
		}
		if(e.getSource().equals(editProductImageButton)) {
			//a file explorer window opens up letting you select an image
			JFileChooser fileChooser = new JFileChooser();
	         fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	         FileNameExtensionFilter filter = new FileNameExtensionFilter("*.IMAGE", "jpg","gif","png");
	         fileChooser.addChoosableFileFilter(filter);
	         int result = fileChooser.showSaveDialog(null);
	         if(result == JFileChooser.APPROVE_OPTION){
	             File selectedFile = fileChooser.getSelectedFile();
	             String path = selectedFile.getAbsolutePath();
	             //the icon is turned into an image and then previewed to the manager
	             editProductImageView.setIcon(ResizeImage(editProductImageView,path));
	             editProductImagePath = path;
	         }
	         else if(result == JFileChooser.CANCEL_OPTION){
	             System.out.println("No Data");
	         }
		}
		if(e.getSource().equals(removeProductButton)) {
			//sends selected product for deletion
			for(int k=0;k<listOfProductButtons.length;k++) {
				if(listOfProductButtons[k].isSelected()) {
					removeProduct(listOfProducts[k].getProductId());
					break;
				}
			}
		}
		if(e.getSource().equals(refreshProductButton)) {
			//refresh list with an empty string to start fresh
			refreshProductList("");
		}
		
		//employee actions start here
		for(int i=0;i<listOfEmployeeButtons.length;i++) {
			if(e.getSource().equals(listOfEmployeeButtons[i])) {
				for(int k=0;k<listOfEmployeeButtons.length;k++) {
					if(listOfEmployeeButtons[k]!=listOfEmployeeButtons[i]) {//make sure to keep selected object marked
						listOfEmployeeButtons[k].setSelected(false);//unmark all other objects in list
					repaint();
					}
				}
				employeeInfoPanel.setText(listOfEmployees[i].toString());
			}
		}
		if(e.getSource().equals(addEmployeeButton)) {
			addEmployee();//opens an option dialog that contains info input
		}
		if(e.getSource().equals(editEmployeeButton)) {
			for(int k=0;k<listOfEmployeeButtons.length;k++) {
				if(listOfEmployeeButtons[k].isSelected()) {
					editEmployee(listOfEmployees[k]);
					break;
				}
			}
		}
		if(e.getSource().equals(removeEmployeeButton)) {
			for(int k=0;k<listOfEmployeeButtons.length;k++) {
				if(listOfEmployeeButtons[k].isSelected()) {
					removeEmployee(listOfEmployees[k].getEmployeeId());
					break;
				}
			}
		}
		if(e.getSource().equals(refreshEmployeeButton)) {
			refreshEmployeeList();
		}
	}
}