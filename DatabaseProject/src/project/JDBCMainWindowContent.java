package project;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import java.sql.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;


import java.io.FileWriter;
import java.io.PrintWriter;


@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener
{
	// DB Connectivity Attributes
	private JComboBox comboEducation = new JComboBox();
	private JComboBox comboSeatbelt = new JComboBox();
	private JComboBox comboCheckSeatbelt = new JComboBox();
	
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	private Container content;
	
	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
	private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;
	
	private Border lineBorder;
	
	private JLabel RecordIDLabel=new JLabel("Record ID:");
	private JLabel ageLabel=new JLabel("Age:");
	private JLabel bmiLabel=new JLabel("BMI:");
	private JLabel yearLabel=new JLabel("Year of Drive:");
	private JLabel speedLabel=new JLabel("Speed Limit:");
	private JLabel educationLabel=new JLabel("Education:");
	private JLabel seatbeltLabel=new JLabel("Seat Belt:");
	
	
	private JTextField RecordIDTF= new JTextField(5);
	private JTextField ageTF=new JTextField(10);
	private JTextField bmiTF=new JTextField(10);
	private JTextField yearTF=new JTextField(10);
	private JTextField speedTF=new JTextField(10);
	private JTextField educationTF=new JTextField(10);
	private JTextField seatbeltTF=new JTextField(10);

			
	private static QueryTableModel TableModel = new QueryTableModel();
	
	//Add the models to JTabels
	private JTable TableofDBContents=new JTable(TableModel);
	
	//Buttons for inserting, and updating members
	//also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton  = new JButton("Export");
	private JButton deleteButton  = new JButton("Delete");
	private JButton clearButton  = new JButton("Clear");

	private JButton ageSeatbelt  = new JButton("Avg Age for Seat Belt status");
	private JTextField ageSeatbeltTF  = new JTextField(12);
	private JButton ageSpeedlimit  = new JButton("Percentage for Speed limit");
	private JTextField ageSpeedlimitTF  = new JTextField(12);
	private JButton averageAge  = new JButton("Average Age for all drivers");
	private JButton chartSeatbelt  = new JButton("Chart on Seat Belt status");
	


	public JDBCMainWindowContent( String aTitle)
	{	
		//setting up the GUI
		super(aTitle, false,false,false,false);
		setEnabled(true);
		
		initiate_db_conn();
		//add the 'main' panel to the Internal Frame
		content=getContentPane();
		content.setLayout(null);
		content.setBackground(Color.lightGray);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.red, Color.black);
	
		//setup details panel and add the components to it
		detailsPanel=new JPanel();
		detailsPanel.setLayout(new GridLayout(7,2));
		detailsPanel.setBackground(Color.lightGray);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Profile Details"));
			
		//----compo box
		comboEducation.setRenderer(new ComboToolTipRenderer(comboEducation));
	    comboEducation.addItemListener(e -> System.out.println(comboEducation.getSelectedItem().toString()));
	    comboEducation.addItem("Level 6 & Below");
	    comboEducation.addItem("Level 7");
	    comboEducation.addItem("Level 8");
	    comboEducation.addItem("level 9 & Above");
	    
		comboSeatbelt.setRenderer(new ComboToolTipRenderer(comboSeatbelt));
	    comboSeatbelt.addItemListener(e -> System.out.println(comboSeatbelt.getSelectedItem().toString()));
	    comboSeatbelt.addItem("Never");
	    comboSeatbelt.addItem("Seldom");
	    comboSeatbelt.addItem("Frequently");
	    comboSeatbelt.addItem("Always");
	    
		comboCheckSeatbelt.setRenderer(new ComboToolTipRenderer(comboCheckSeatbelt));
	    comboCheckSeatbelt.addItemListener(e -> System.out.println(comboCheckSeatbelt.getSelectedItem().toString()));
	    comboCheckSeatbelt.addItem("Never");
	    comboCheckSeatbelt.addItem("Seldom");
	    comboCheckSeatbelt.addItem("Frequently");
	    comboCheckSeatbelt.addItem("Always");
		
		//---
		detailsPanel.add(RecordIDLabel);			
		detailsPanel.add(RecordIDTF);
		detailsPanel.add(ageLabel);		
		detailsPanel.add(ageTF);
		detailsPanel.add(bmiLabel);	
		detailsPanel.add(bmiTF);
		detailsPanel.add(yearLabel);		
		detailsPanel.add(yearTF);
		detailsPanel.add(speedLabel);
		detailsPanel.add(speedTF);
		detailsPanel.add(educationLabel);
		detailsPanel.add(comboEducation);
		detailsPanel.add(seatbeltLabel);
		detailsPanel.add(comboSeatbelt);
		
		//setup details panel and add the components to it
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(3,2));
		exportButtonPanel.setBackground(Color.lightGray);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		
		ageSeatbelt.addActionListener(this);
		ageSpeedlimit.addActionListener(this);
		averageAge.addActionListener(this);
		chartSeatbelt.addActionListener(this);
		
		exportButtonPanel.add(ageSeatbelt);
		exportButtonPanel.add(comboCheckSeatbelt);
		exportButtonPanel.add(ageSpeedlimit);
		exportButtonPanel.add(ageSpeedlimitTF);
		exportButtonPanel.add(averageAge);
		exportButtonPanel.add(chartSeatbelt);		
		exportButtonPanel.setSize(450, 240);
		exportButtonPanel.setLocation(3, 260);
		content.add(exportButtonPanel);		
	
		insertButton.setSize(80, 30);
		updateButton.setSize(80, 30);
		exportButton.setSize (80, 30);
		deleteButton.setSize (80, 30);
		clearButton.setSize (80, 30);
		
		insertButton.setLocation(370, 10);
		updateButton.setLocation(370, 110);
		exportButton.setLocation (370, 160);
		deleteButton.setLocation (370, 60);
		clearButton.setLocation (370, 210);
		
		insertButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		deleteButton.addActionListener(this);
		clearButton.addActionListener(this);

		
		content.add(insertButton);
		content.add(updateButton);
		content.add(exportButton);
		content.add(deleteButton);
		content.add(clearButton);

				
		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));
	
		dbContentsPanel=new JScrollPane(TableofDBContents,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.lightGray);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder,"Database Content"));
		
		detailsPanel.setSize(360, 250);
		detailsPanel.setLocation(3,0);
		dbContentsPanel.setSize(700, 500);
		dbContentsPanel.setLocation(477, 0);
		
		content.add(detailsPanel);
		content.add(dbContentsPanel);
		
		setSize(982,645);
		setVisible(true);
	
		TableModel.refreshFromDB(stmt);
	}
	
	public void initiate_db_conn()
	{
		try
		{
			// Load the JConnector Driver
			Class.forName("com.mysql.jdbc.Driver");
			// Specify the DB Name
			String url="jdbc:mysql://localhost:3306/DBAssignment";
			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "Change01");
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}
	
	//event handling for members desktop
	public void actionPerformed(ActionEvent e)
	{
		 Object target=e.getSource();
		 //add extra button function
		 if(target == ageSeatbelt){
			 System.out.println("ageSeatbelt pressed");
			 String status = comboCheckSeatbelt.getSelectedItem().toString();
			 String cmd = "select avg(age) as 'Average Age for " + status + " wearing Seat Belt' from dbassignment.sociodemographictable "
			 		+ "where seatbelt = " + "'" + status + "';";
			 try {
					rs = stmt.executeQuery(cmd);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			 writeToFile(rs);
		 }
		 
		 if(target == ageSpeedlimit){
			 System.out.println("ageSpeedlimit pressed");
			 String speed = ageSpeedlimitTF.getText();
			 String cmd = "select count(*) from dbassignment.sociodemographictable where speedlimit >= "
					 +speed+";";
			 String overSpeed = null, underSpeed = null;
			 try {
				rs = stmt.executeQuery(cmd);
				while(rs.next()){
					overSpeed = rs.getString(1);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			 ResultSet rs2 = null;
			 cmd = "select count(*) from dbassignment.sociodemographictable where speedlimit < "
					 +speed+";";
			 try {
					rs2 = stmt.executeQuery(cmd);
					while(rs2.next()){
						underSpeed = rs2.getString(1);
					}
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			 
			 pieGraph2(overSpeed, underSpeed, speed); 
			 
		 }
		 
		 if(target == averageAge){
			 System.out.println("average age for all drivers pressed");
			 String cmd = "select avg(age) as 'Average Age' from dbassignment.sociodemographictable;";
			 try {
					rs = stmt.executeQuery(cmd);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			 writeToFile(rs);		
			 
		 }
		 
		 if(target == chartSeatbelt){
			 System.out.println("chartSeatbelt pressed");
			 String cmd = "select seatbelt, sum(rec_id) from dbassignment.sociodemographictable group by seatbelt;";
			 try {
				rs = stmt.executeQuery(cmd);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			 pieGraph(rs, "Seat Belt Chart"); 
			 			 
		 }
		 
		 if(target == exportButton){
			 System.out.println("export button pressed");
			 String cmd = "select * from dbassignment.sociodemographictable;";
			 try {
					rs = stmt.executeQuery(cmd);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			 writeToFile(rs);		 
	 
		 }
		 
		 //end extra buttons
		 if (target == clearButton)
		 {
			 RecordIDTF.setText("");
			 ageTF.setText("");
			 bmiTF.setText("");
			 yearTF.setText("");
			 speedTF.setText("");
			 educationTF.setText("");
			 seatbeltTF.setText("");

			 	 
		 }
		
		 if (target == insertButton)
		 {		 
	 		try
	 		{
 				String updateTemp ="INSERT INTO SOCIODEMOGRAPHICTABLE VALUES ('"+
 		 				RecordIDTF.getText()+"','"+
 						ageTF.getText()+"','"+
 		 				bmiTF.getText()+"','"+
 						yearTF.getText()+"','"+
 		 				speedTF.getText()+"','"+
 						comboEducation.getSelectedItem().toString()+"','"+
 						comboSeatbelt.getSelectedItem().toString()+
 		 				"');";
 				
 						
 				stmt.executeUpdate(updateTemp);
 			
	 		}
	 		catch (SQLException sqle)
	 		{
	 			System.err.println("Error with members insert:\n"+sqle.toString());
	 		}
	 		finally
	 		{
	 			TableModel.refreshFromDB(stmt);
			}
		 }
		 
		 if (target == deleteButton)
		 {
		 	
	 		try
	 		{
 				String updateTemp ="DELETE FROM SOCIODEMOGRAPHICTABLE WHERE Rec_id = "+RecordIDTF.getText()+";"; 
 				stmt.executeUpdate(updateTemp);
 			
	 		}
	 		catch (SQLException sqle)
	 		{
	 			System.err.println("Error with delete:\n"+sqle.toString());
	 		}
	 		finally
	 		{
	 			TableModel.refreshFromDB(stmt);
			}
		 }
		 
		 if (target == updateButton)
		 {	 	
	 		try
	 		{ 			
 				String updateTemp ="UPDATE SOCIODEMOGRAPHICTABLE SET age = "+ageTF.getText()+
 									
 									", bmi = " 			+ bmiTF.getText()		+
 									", yearofdrive = "	+ yearTF.getText()		+
 									", speedlimit = "	+ speedTF.getText()		+
 									", education = '"	+ comboEducation.getSelectedItem().toString()	+ "'" +
 									", seatbelt = '"	+ comboSeatbelt.getSelectedItem().toString()	+ "'" +
 									" where Rec_id = "	+ RecordIDTF.getText();;	
 				 				
 				System.out.println(updateTemp);
 				stmt.executeUpdate(updateTemp);
 				//these lines do nothing but the table updates when we access the db.
 				rs = stmt.executeQuery("SELECT * from SOCIODEMOGRAPHICTABLE ");
 				rs.next();
 				rs.close();	
 			}
	 		catch (SQLException sqle){
	 			System.err.println("Error with members insert:\n"+sqle.toString());
	 		}
	 		finally{
	 			TableModel.refreshFromDB(stmt);
			}
		 }		 	
	}
	
	
	public  void pieGraph2(String over, String under, String speed) {
		try {
			DefaultPieDataset dataset = new DefaultPieDataset();

			String category = "Speed Limit >= " + speed + " km/h total: " + over ;				
			dataset.setValue(category, new Double(over));
			
			category = "Speed Limit < " + speed + " km/h total: " + under;
			dataset.setValue(category, new Double(under));
						
			JFreeChart chart = ChartFactory.createPieChart(
					title,  
					dataset,             
					false,              
					true,
					true
			);

			ChartFrame frame = new ChartFrame(title, chart);
			chart.setBackgroundPaint(Color.WHITE);
			frame.pack();
			frame.setVisible(true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public  void pieGraph(ResultSet rs, String title) {
		try {
			DefaultPieDataset dataset = new DefaultPieDataset();

			while (rs.next()) {
				String category = rs.getString(1);
				String value = rs.getString(2);
				dataset.setValue(category+ " "+value, new Double(value));
			}
			JFreeChart chart = ChartFactory.createPieChart(
					title,  
					dataset,             
					false,              
					true,
					true
			);

			ChartFrame frame = new ChartFrame(title, chart);
			chart.setBackgroundPaint(Color.WHITE);
			frame.pack();
			frame.setVisible(true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeToFile(ResultSet rs){
		try{
			FileWriter outputFile = new FileWriter("CellOutput.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for(int i=0;i<numColumns;i++){
				printWriter.print(rsmd.getColumnLabel(i+1)+",");
			}
			printWriter.print("\n");
			while(rs.next()){
				for(int i=0;i<numColumns;i++){
					printWriter.print(rs.getString(i+1)+",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		}
		catch(Exception e){e.printStackTrace();}
	}

	

}

//--- combo class
class ComboToolTipRenderer extends BasicComboBoxRenderer {
	  static final long serialVersionUID = 1L;
	  JComboBox combo;
	  JList comboList;
	  ComboToolTipRenderer(JComboBox combo) {
	    this.combo = combo;
	  }

	 /* @Override
	  public Component getListCellRendererComponent(JList list, Object value,
	      int index, boolean isSelected, boolean cellHasFocus) {
	    super.getListCellRendererComponent(list, value, index, isSelected,
	        cellHasFocus);
	    if (comboList == null) {
	      comboList = list;
	      KeyAdapter listener = new KeyAdapter() {

	        @Override
	        public void keyReleased(KeyEvent e) {
	          if (e.getKeyCode() == KeyEvent.VK_DOWN
	              || e.getKeyCode() == KeyEvent.VK_UP) {
	            int x = 5;
	            int y = comboList.indexToLocation(comboList.getSelectedIndex()).y;
	            System.out.println(comboList.getSelectedIndex());
	          }
	        }
	      };
	      combo.addKeyListener(listener);
	      combo.getEditor().getEditorComponent().addKeyListener(listener);
	    }
	    if (isSelected) {
	      System.out.println(value.toString());
	    }
	    return this;
	  }*/
	}
