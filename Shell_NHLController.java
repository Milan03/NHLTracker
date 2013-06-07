/**
 * Program Name:Shell_NHLController.java
 * Purpose: this is the shell code for displaying the NHLController GUI.
 * Author: Bill Pulling
 * Date: Jul 15, 2012
 */


import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;


public class Shell_NHLController extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	//factors for sizing and placing GUI according to screen resolution
	private static final double FR_WIDTH_FACTOR = 0.5;
	private static final double FR_HEIGHT_FACTOR = 0.45;
	
	//class scope declarations
	private JButton addBtn, btnClear, btnDelete, btnRetrieve, btnStandings, btnClose;
	private JComboBox cmbMonth, cmbDay, cmbYear;
	private JComboBox cmbHome, cmbAway;
	private JTextField fldHome, fldAway;
	private JCheckBox chbOvertime, chbShootout;
	private ButtonListener listener;
	
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rslt = null;
	
	//cponstructor
	public Shell_NHLController() throws HeadlessException
	{
		// Set up the basic JFrame
		this.setTitle("NHL Game Tracker");
		this.setSize((int)(getToolkit().getScreenSize().width * FR_WIDTH_FACTOR),
								(int)(getToolkit().getScreenSize().height * FR_HEIGHT_FACTOR));
		this.setLocationRelativeTo(null);
		// set default as do nothing so that user can deny exit confirmation dialog
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//check when user clicks the frame's "Close" button. 
		this.addWindowListener(new WindowAdapter() 
		{
		  public void windowClosing(WindowEvent e)
		  {
			  int n = JOptionPane.showConfirmDialog(
						Shell_NHLController.this, "Are you sure you want to exit?",
						"User Confirmation", JOptionPane.YES_NO_OPTION);
				
				if ( n == JOptionPane.YES_OPTION)
				{
					//destory the frame object
					Shell_NHLController.this.dispose();					
				}			
		  }//end method
		});//end method addWindowListener() arg list
		
		//create one button listener object for all buttons
		listener = new ButtonListener();
		// GameInfoPanel
		JPanel gameInfoPanel = new JPanel();
		
		gameInfoPanel.setBorder( BorderFactory.createTitledBorder("Game Information"));
		gameInfoPanel.setLayout(new GridLayout(5, 1, 20, 20));		
		
		// Date Panel
		JPanel datePanel = new JPanel();		
		datePanel.setLayout( new GridLayout(1, 4, 20, 20) );		
		datePanel.add(new JLabel("<< Date >> (Month/Day/Year)",  JLabel.RIGHT));
		
		cmbMonth = new JComboBox();
		//load months
		cmbMonth.addItem("January");
		cmbMonth.addItem("February");
		cmbMonth.addItem("March");
		cmbMonth.addItem("April");
		cmbMonth.addItem("May");
		cmbMonth.addItem("June");
		cmbMonth.addItem("July");
		cmbMonth.addItem("August");
		cmbMonth.addItem("September");
		cmbMonth.addItem("October");
		cmbMonth.addItem("November");
		cmbMonth.addItem("December");
		
		cmbMonth.addActionListener(listener);
		
		datePanel.add(cmbMonth);
		
		cmbDay = new JComboBox();
		datePanel.add(cmbDay);
		//load with years
		cmbYear = new JComboBox();
		 for ( int year = 2012; year <= 2020; ++year) 
		 {
			cmbYear.addItem(year);
		 }//end for
		datePanel.add(cmbYear);
		cmbYear.addActionListener(listener);
		
		gameInfoPanel.add(datePanel);	
		
		JPanel title = new JPanel();		
		title.setLayout( new GridLayout(1, 3, 20, 20) );		
		title.add(new JLabel(""));
		title.add(new JLabel("HOME", JLabel.CENTER));
		title.add(new JLabel("AWAY",  JLabel.CENTER));
		
		gameInfoPanel.add(title);
	
		JPanel team = new JPanel();		
		team.setLayout( new GridLayout(1, 3, 20, 20));		
		team.add(new JLabel("<< Team >> ",  JLabel.RIGHT));
		
		cmbHome = new JComboBox();
		team.add(cmbHome);
		
		cmbAway = new JComboBox();
		team.add(cmbAway);		
		gameInfoPanel.add(team);
		
		JPanel goal = new JPanel();		
		goal.setLayout( new GridLayout(1, 3, 20, 20));		
		goal.add(new JLabel("Goals", JLabel.RIGHT));
		
		fldHome = new JTextField("");
		fldHome.setHorizontalAlignment(JTextField.CENTER) ;
		goal.add(fldHome);
		
		fldAway = new JTextField("");
		fldAway.setHorizontalAlignment(JTextField.CENTER) ;
		goal.add(fldAway);		
		gameInfoPanel.add(goal);
		
		JPanel overtime = new JPanel();		
		overtime.setLayout( new GridLayout(1, 3, 20, 20));		
		overtime.add(new JLabel("Overtime?", JLabel.RIGHT));
		
		chbOvertime= new JCheckBox();
		overtime.add(chbOvertime);
		
		overtime.add(new JLabel("Shootout?", JLabel.RIGHT));
		chbShootout = new JCheckBox("");
		overtime.add(chbShootout);		
		gameInfoPanel.add(overtime);		
		this.add(gameInfoPanel, BorderLayout.CENTER);
		
		// Button Panel
		JPanel buttonPanel = new JPanel();		
		buttonPanel.setLayout( new GridLayout(2, 3, 20, 20) );
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		addBtn = new JButton("Add");
		addBtn.addActionListener(listener);
		buttonPanel.add(addBtn);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(listener);
		buttonPanel.add(btnClear);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(listener);
		buttonPanel.add(btnDelete);
		
		btnRetrieve = new JButton("<< Retrieve >> ");
		btnRetrieve.addActionListener(listener);
		buttonPanel.add(btnRetrieve);
		
		btnStandings = new JButton("Standings");
		btnStandings.addActionListener(listener);
		buttonPanel.add(btnStandings);
		
		btnClose = new JButton("Close");
		btnClose.addActionListener(listener);
		buttonPanel.add(btnClose);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		//last line
		this.setVisible(true);
		
		
	}//end constructor
	
	public static void main(String[] args)
	{
		// anonymous object...
		new Shell_NHLController();
	}//end main
	
	//Implement a listener for ActionEvents 
	// as an inner class
	private class ButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			//code to respond to the buttons goes here
				
		}//end actionPerformed()
	}//end inner class
	
	//methods here for the various database operations
	
}//end class Shell_NHLController