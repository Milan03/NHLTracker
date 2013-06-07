/*
 * File Name: M_S_NHLController.java
 * Date:      Jun 6, 2013
 * Author:    Milan Sobat
 * Student #: 0469245
 * Course:    INFO-5051
 * Purpose:   Used to control the NHL Tracker database contents. The database records
 * 			  results of regular season games between teams in the Nation Hockey League.
 * 
 * 			  Displays the regular season standings of the teams in any or all six 
 * 			  divisions of the NHL.
 */



import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

import java.util.Vector;
import org.joda.time.*;



public class M_S_NHLController extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	//factors for sizing and placing GUI according to screen resolution
	private static final double FR_WIDTH_FACTOR = 0.5;
	private static final double FR_HEIGHT_FACTOR = 0.45;
	
	//database information constants
	private static final String DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
	private static final String DATA_SOURCE = "jdbc:odbc:NHL2013db";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "ereader12";
	
	//class scope declarations
	private JButton addBtn, btnClear, btnDelete, btnRetrieve, btnStandings, btnClose;
	private JComboBox cmbMonth, cmbDay, cmbYear;
	private static JComboBox cmbHome;

	private static JComboBox cmbAway;
	private JTextField fldHome, fldAway;
	private JCheckBox chbOvertime, chbShootout;
	private ButtonListener listener;
	
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rslt = null;
	
	private enum months {
		JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER,
		OCTOBER, NOVEMBER, DECEMBER
	}
	
	
	
	//cponstructor
	public M_S_NHLController() throws HeadlessException
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
						M_S_NHLController.this, "Are you sure you want to exit?",
						"User Confirmation", JOptionPane.YES_NO_OPTION);
				
				if ( n == JOptionPane.YES_OPTION)
				{
					//destory the frame object
					M_S_NHLController.this.dispose();					
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
		
		// Add enum months to cmbMonth
		cmbMonth = new JComboBox(months.values());
		
		cmbMonth.addActionListener(listener);
		
		datePanel.add(cmbMonth);
		
		cmbDay = new JComboBox();
		datePanel.add(cmbDay);
		//load with years
		cmbYear = new JComboBox();
		 for ( int year = 2012; year <= 2021; ++year) 
		 {
			cmbYear.addItem(year);
		 }//end for
		datePanel.add(cmbYear);
		cmbYear.addActionListener(listener);
		
		//days of month
		int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
		int month = cmbMonth.getSelectedIndex() + 1;;
		DateTime currMonYear = new DateTime(year, month, 1, 12, 0, 0, 000);

		int daysInMon = currMonYear.dayOfMonth().getMaximumValue();
		for ( int i = 1; i <= daysInMon; ++i )
			cmbDay.addItem(i);
		
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
	
	public static void main(String[] args) throws SQLException 
	{
		// anonymous object...
		new M_S_NHLController();
		
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			
			conn = DriverManager.getConnection("jdbc:odbc:TunesPeddlerDataSource", "root", "ereader12");
			
			stmt = conn.createStatement();
			
			addTeams(stmt);
		} catch ( SQLException sqlEx ) {
			JOptionPane.showMessageDialog(null, "SQL Exception: " +sqlEx.getMessage());
		} catch ( Exception ex ) {
			JOptionPane.showMessageDialog(null, "Exception: " +ex.getMessage());
		} finally {
			if ( rslt != null )
				rslt.close();
			if ( stmt != null )
				stmt.close();
			if ( conn != null )
				conn.close();
		}
	}//end main
	
	private static void addTeams( Statement stmt ) throws SQLException {
		String selectQuery = "SELECT * FROM NHLTracker.Teams";
		Vector<String> teamNames = new Vector<String>(30);
		Vector dataVector = new Vector();
		try {
			rslt = stmt.executeQuery(selectQuery);
			
			while ( rslt.next() ) {
				teamNames.addElement(rslt.getString("TeamName"));
			}
			
			// Add teams to comboboxes 
			for ( String s : teamNames ) {
				cmbHome.addItem(s);
				cmbAway.addItem(s);	
			}
					
		} catch ( SQLException sqlEx ) {
			JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
					, "Error", JOptionPane.ERROR_MESSAGE, null);
		}
	}
	
	//Implement a listener for ActionEvents 
	// as an inner class
	private class ButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// Days of month to be reloaded each time a new mont or year is choosen
			if ( e.getSource() == cmbMonth || e.getSource() == cmbYear ) {
				cmbDay.removeAllItems();
				int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
				int month = cmbMonth.getSelectedIndex() + 1;;
				DateTime currMonYear = new DateTime(year, month, 1, 12, 0, 0, 000);

				int daysInMon = currMonYear.dayOfMonth().getMaximumValue();
				for ( int i = 1; i <= daysInMon; ++i )
					cmbDay.addItem(i);
				
				
			}
				
		}//end actionPerformed()
	}//end inner class
	
}//end class M_S_NHLController