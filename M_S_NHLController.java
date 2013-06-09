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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

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
	private JButton addBtn, btnClear, btnDelete, btnRetrieve, btnStandings, 
					btnClose, btnRefresh, btnCloseStandings;
	private JComboBox cmbMonth, cmbDay, cmbYear;

	private JComboBox cmbDivision;
	private static JComboBox cmbHome, cmbAway;
	private JTextField fldHome, fldAway;
	private JCheckBox chbOvertime, chbShootout;
	private ButtonListener listener, stndListener;
	
	private JFrame standings = new JFrame();
	private JPanel tablePanel = new JPanel();
	
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
		
		//serperate listener for Standings window
		stndListener = new ButtonListener();
		
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
		
		// Standings Button Panel
		JPanel stdButtonPanel = new JPanel( new GridLayout( 1, 4, 10, 10));
		JPanel tablePanel = new JPanel();
		
		JLabel lblDivision = new JLabel("Select Division: ");
		stdButtonPanel.add(lblDivision);
		
		String[] divisions = { "All Divisions", "Central Division", "Atlantic Division", "Northeast Division", 
								"Northwest Division", "Pacific Division", "Southeast Division" };
		
		
		cmbDivision = new JComboBox(divisions);
		stdButtonPanel.add(cmbDivision);
		cmbDivision.addActionListener(stndListener);
		
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
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(listener);
		stdButtonPanel.add(btnRefresh);
		
		btnCloseStandings = new JButton("Close");
		btnCloseStandings.addActionListener(listener);
		stdButtonPanel.add(btnCloseStandings);
		
		standings.add(stdButtonPanel, BorderLayout.SOUTH);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		//last line
		this.setVisible(true);
		
		// Standings window
		
		int frWidth = (int)(0.25 * this.getToolkit().getScreenSize().width);
		int frHeight = (int)(0.18 * this.getToolkit().getScreenSize().height);
		standings.setSize(frWidth, frHeight);
		standings.setLocationRelativeTo(null);
		standings.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		
	}//end constructor
	
	// main -- initialize comboboxes
	public static void main(String[] args) throws SQLException 
	{
		// anonymous object...
		new M_S_NHLController();
		
		try {
			Class.forName( DRIVER );
			
			conn = DriverManager.getConnection( DATA_SOURCE, USER_NAME, PASSWORD );
			
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
	
	// Method to add teams to combo boxes
	private static void addTeams( Statement stmt ) throws SQLException {
		String selectQuery = "SELECT * FROM NHLTracker.Teams";
		Vector<String> teamNames = new Vector<String>(30);
		
		try {
			rslt = stmt.executeQuery(selectQuery);
			
			// Get team names and store in vector 
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
	
	// Method to add game data and update appropriate teams upon 'Add' button being clicked
	private static void addRowToGames ( int gameMonth, int gameDay, int gameYear, int homeTeam, int awayTeam, 
										int goalsHome, int goalsAway, boolean overtime,
										boolean shootout ) throws SQLException {
		
		// INSERT statement for adding a new row to Games table
		String insertRow = "INSERT INTO NHLTracker.Games ( HomeTeamID, AwayTeamID, HomeTeamGoals, " +
				"AwayTeamGoals, GameMonth, GameDay, GameYear, Overtime, Shootout )\n" +
				"VALUES ( " +homeTeam +", " +awayTeam +", " +goalsHome +", " +goalsAway
							+", " +gameMonth +", " +gameDay +", " +gameYear +", "
		
							+overtime +", " +shootout +" ); ";
		
		// Update appropriate team stats
		int winningTeam;
		int losingTeam;
		
		if ( goalsHome > goalsAway ) {
			winningTeam = homeTeam;
			losingTeam = awayTeam;
		}
		else {
			winningTeam = awayTeam;
			losingTeam = homeTeam;
		}
		
		String updateWinner = "UPDATE NHLTracker.Teams\n" +
							  "SET GamesPlayed = GamesPlayed + 1, Wins = Wins + 1, Points = Points + 2\n" +
							  "WHERE TeamID = " +winningTeam +";";
		String updateLoser = "";
		
		if ( overtime ) {
			updateLoser =  "UPDATE NHLTracker.Teams\n" +
					 "SET GamesPlayed = GamesPlayed + 1, OvertimeLosses = OvertimeLosses + 1, Points = Points + 1\n" +
					 "WHERE TeamID = " +losingTeam +";";
		} else if ( shootout ) {
			updateLoser =  "UPDATE NHLTracker.Teams\n" +
					 "SET GamesPlayed = GamesPlayed +1, OvertimeLosses = OvertimeLosses + 1, ShootoutLosses = ShootoutLosses +1, Points = Points + 1\n" +
					 "WHERE TeamID = " +losingTeam +";";
		} else { // regulation loss 
			updateLoser =  "UPDATE NHLTracker.Teams\n" +
					 "SET GamesPlayed = GamesPlayed +1\n" +
					 "WHERE TeamID = " +losingTeam +";";
		}
		try {
			rslt = null;
			Class.forName( DRIVER );
			
			conn = DriverManager.getConnection( DATA_SOURCE, USER_NAME, PASSWORD );
			
			stmt = conn.createStatement();
			
			int result = stmt.executeUpdate(insertRow);
			
			if ( result == 1 ) {
				stmt.executeUpdate(updateWinner);
				stmt.executeUpdate(updateLoser);
				JOptionPane.showMessageDialog(null, "Game added!", "Add", JOptionPane.INFORMATION_MESSAGE );
			} else {
				JOptionPane.showMessageDialog(null, "Problem adding game!", "Add", JOptionPane.ERROR_MESSAGE );
			}
			
		} catch ( SQLException sqlEx ) {
			JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
					, "Error", JOptionPane.ERROR_MESSAGE, null);
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
	}
	
	// Method to delete the requested game from NHLTracker database upon 'Delete' button click
	private static void deleteRowFromGames ( int gameYear, int gameMonth, int gameDay, int homeTeam, int awayTeam,
											int goalsHome, int goalsAway, boolean overtime, boolean shootout ) throws SQLException {
		String deleteRow = "DELETE FROM NHLTracker.Games\n" +
						   "WHERE GameYear = " +gameYear +" AND GameMonth = " +gameMonth +" AND GameDay = " +gameDay
						   					 +" AND HomeTeamID = " +homeTeam +" AND AwayTeamID = " +awayTeam +";";
		
		// Update appropriate team stats
		int winningTeam;
		int losingTeam;
		
		if ( goalsHome > goalsAway ) {
			winningTeam = homeTeam;
			losingTeam = awayTeam;
		}
		else {
			winningTeam = awayTeam;
			losingTeam = homeTeam;
		}
		
		String updateWinner = "UPDATE NHLTracker.Teams\n" +
							  "SET GamesPlayed = GamesPlayed - 1, Wins = Wins - 1, Points = Points - 2\n" +
							  "WHERE TeamID = " +winningTeam +";";
		String updateLoser = "";
		
		if ( overtime ) {
			updateLoser =  "UPDATE NHLTracker.Teams\n" +
					 "SET GamesPlayed = GamesPlayed - 1, OvertimeLosses = OvertimeLosses - 1, Points = Points - 1\n" +
					 "WHERE TeamID = " +losingTeam +";";
		} else if ( shootout ) {
			updateLoser =  "UPDATE NHLTracker.Teams\n" +
					 "SET GamesPlayed = GamesPlayed +1, OvertimeLosses = OvertimeLosses - 1, ShootoutLosses = ShootoutLosses - 1, Points = Points - 1\n" +
					 "WHERE TeamID = " +losingTeam +";";
		} else { // regulation loss 
			updateLoser =  "UPDATE NHLTracker.Teams\n" +
					 "SET GamesPlayed = GamesPlayed - 1\n" +
					 "WHERE TeamID = " +losingTeam +";";
		}
		
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to delete?", "Warning", dialogButton);
		
		if ( dialogResult == JOptionPane.YES_OPTION ) {
		try {
			rslt = null;
			Class.forName( DRIVER );
			
			conn = DriverManager.getConnection( DATA_SOURCE, USER_NAME, PASSWORD );
			
			stmt = conn.createStatement();
			
			int result = stmt.executeUpdate(deleteRow);
			
			if ( result == 1 ) {
				stmt.executeUpdate(updateWinner);
				stmt.executeUpdate(updateLoser);
				JOptionPane.showMessageDialog(null, "Game deleted!", "Delete", JOptionPane.INFORMATION_MESSAGE);
			} else 
				JOptionPane.showMessageDialog(null, "Game not found!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch ( SQLException sqlEx ) {
			JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
					, "Error", JOptionPane.ERROR_MESSAGE, null);
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
		} else {
			JOptionPane.showMessageDialog(null, "Delete aborted.");
		}
	}
	
	// Method to query for goals scored in a particular game
	private static int[] getGameInfo ( int year, int month, int day, int homeTeam, int awayTeam ) throws SQLException {
		String selectString = "SELECT * FROM NHLTracker.Games\n" +
						  "WHERE GameYear = " +year +" AND GameMonth = " +month +" AND GameDay = " +day
						  +" AND HomeTeamID = " +homeTeam +" AND AwayTeamID = " +awayTeam +";";
		int[] info = new int[4];
		try {
			Class.forName( DRIVER );
			
			conn = DriverManager.getConnection( DATA_SOURCE, USER_NAME, PASSWORD );
			
			stmt = conn.createStatement();
			
			rslt = stmt.executeQuery(selectString);
			while ( rslt.next() ) {
				info[0] = rslt.getInt("HomeTeamGoals");
				info[1] = rslt.getInt("AwayTeamGoals");
				if ( rslt.getBoolean("Overtime") )
					info[2] = 1;
				if ( rslt.getBoolean("Shootout" ) )
					info[3] = 1;
			}
		} catch ( SQLException sqlEx ) {
			JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
					, "Error", JOptionPane.ERROR_MESSAGE, null);
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
		
		return info;
	}
	
	// Method to create/recreate the JTable used for the Standings window
	private void createTable() throws SQLException {
		String division = cmbDivision.getSelectedItem().toString();
		standings.setTitle(division +" Standings");
		String query = "";
				
		if ( division != "All Divisions") {
			query = "SELECT TeamName, GamesPlayed AS \"GP\", Wins, RegulationLosses AS \"Losses\", " 
							+" OvertimeLosses AS \"OTL\", ShootoutLosses AS \"SOL\", Points\n"
							+"FROM NHLTracker.Teams\n"
							+"WHERE Division = \""+division+"\"\n"
							+"ORDER BY Points DESC";
		} else {
			query = "SELECT TeamName, GamesPlayed AS \"GP\", Wins, RegulationLosses AS \"Losses\", " 
					+" OvertimeLosses AS \"OTL\", ShootoutLosses AS \"SOL\", Points\n"
					+"FROM NHLTracker.Teams\n"
					+"ORDER BY Points DESC";
		}
		Vector colNames = new Vector();
		Vector data = new Vector();
		try {
			Class.forName( DRIVER );
			
			conn = DriverManager.getConnection( DATA_SOURCE, USER_NAME, PASSWORD );
			
			stmt = conn.createStatement();
			
			rslt = stmt.executeQuery(query);
			
			ResultSetMetaData metaData = rslt.getMetaData();
			
			int numCols = metaData.getColumnCount();
			
			// get column names
			for ( int i = 1; i <= numCols; ++i )
				colNames.add( metaData.getColumnName(i));
			
			// get rows
			while ( rslt.next() ) {
				Vector row = new Vector();
				for ( int i = 1; i <= numCols; ++i ) 
					row.add(rslt.getString(i));
				data.add(row);
			}
			
			DefaultTableModel tm = new DefaultTableModel();
			tm.setRowCount(0);
			tm = new DefaultTableModel(data, colNames);
			tm.fireTableDataChanged();
			// make JTable
			TableColumn col = null;
			JTable table = new JTable(tm);
			col = table.getColumnModel().getColumn(0);
			col.setPreferredWidth(300);
			JScrollPane scrollPane = new JScrollPane(table);
			tablePanel.add(scrollPane);
			
		} catch ( SQLException sqlEx ) {
			JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
					, "Error", JOptionPane.ERROR_MESSAGE, null);
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
	}
	
	//Implement a listener for ActionEvents 
	// as an inner class
	private class ButtonListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// On combo reselection in Standings
			if ( e.getSource() == cmbDivision ) {
				try {
					tablePanel.removeAll();
					
					createTable();
					standings.add(tablePanel, BorderLayout.CENTER );
					standings.repaint();
					standings.revalidate();
					
				} catch ( SQLException sqlEx ) {
					JOptionPane.showMessageDialog(null, "SQLException:\n"+sqlEx.getMessage(), 
							"Error", JOptionPane.ERROR_MESSAGE );
				}
			}
			
			// Standings Refresh button
			if ( e.getSource() == btnRefresh ) {
				
				try {
					tablePanel.removeAll();
					
					createTable();
					standings.add(tablePanel, BorderLayout.CENTER );
					standings.repaint();
					standings.revalidate();
					
				} catch ( SQLException sqlEx ) {
					JOptionPane.showMessageDialog(null, "SQLException:\n"+sqlEx.getMessage(), 
							"Error", JOptionPane.ERROR_MESSAGE );
				}
			}
			
			// Standings Close button
			if ( e.getSource() == btnCloseStandings ) {
				standings.setVisible(false);
			}
			
			// Days of month to be reloaded each time a new month or year is chosen
			if ( e.getSource() == cmbMonth || e.getSource() == cmbYear ) {
				cmbDay.removeAllItems();
				int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
				int month = cmbMonth.getSelectedIndex() + 1;
				DateTime currMonYear = new DateTime(year, month, 1, 12, 0, 0, 000);

				int daysInMon = currMonYear.dayOfMonth().getMaximumValue();
				for ( int i = 1; i <= daysInMon; ++i )
					cmbDay.addItem(i);				
			} // end if ( e.getSource() == cmbMonth || e.getSource() == cmbYear )
			
			// Add button
			if ( e.getSource() == addBtn ) {
				// Check if fields are filled out
				// Since date and teams are always selected
				// Only need to check if goals are entered
				if ( fldHome.getText().trim().length() == 0 || fldAway.getText().trim().length() == 0 )
					JOptionPane.showMessageDialog(null, "Please insert goals scored if you wish to add a new game.", "Error", JOptionPane.ERROR_MESSAGE);
				else if ( fldHome.getText().equals(fldAway.getText() ) )
					JOptionPane.showMessageDialog(null, "Teams cannot tie.", "Error", JOptionPane.ERROR_MESSAGE);
				else if ( cmbHome.getSelectedIndex() == cmbAway.getSelectedIndex() ) // check if teams aren't same
					JOptionPane.showMessageDialog(null, "Please choose different teams.", "Error", JOptionPane.ERROR_MESSAGE);
				else {					
					int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
					int month = cmbMonth.getSelectedIndex() +1;
					int day = cmbDay.getSelectedIndex() + 1;
					int homeTeam = cmbHome.getSelectedIndex() + 1;
					int awayTeam = cmbAway.getSelectedIndex() + 1;
					int goalsHome = Integer.parseInt( fldHome.getText() );
					int goalsAway = Integer.parseInt( fldAway.getText() );
					boolean overtime = chbOvertime.isSelected();
					boolean shootout = chbShootout.isSelected();
					
					// Add row to database
					try {
						addRowToGames( month, day, year, homeTeam, awayTeam, goalsHome,
													goalsAway, overtime, shootout );
					} catch ( SQLException sqlEx ){
						JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
								, "Error", JOptionPane.ERROR_MESSAGE, null);
					}
					
					// reset fields after add
					cmbMonth.setSelectedIndex(0);
					cmbYear.setSelectedIndex(0);
					cmbDay.setSelectedIndex(0);
					cmbHome.setSelectedIndex(0);
					cmbAway.setSelectedIndex(0);
					fldHome.setText("");
					fldAway.setText("");
					if ( chbOvertime.isSelected() )
						chbOvertime.setSelected(false);
					if ( chbShootout.isSelected() )
						chbShootout.setSelected(false);
					
				}
			}// end addBtn
			
			// Delete button
			if ( e.getSource() == btnDelete ) {
				int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
				int month = cmbMonth.getSelectedIndex() +1;
				int day = cmbDay.getSelectedIndex() + 1;
				int homeTeam = cmbHome.getSelectedIndex() + 1;
				int awayTeam = cmbAway.getSelectedIndex() + 1;
				int info[] = new int[4];
				
				// query for goals and overtime/shootout info to pass to delete method
				try {
					info = getGameInfo( year, month, day, homeTeam, awayTeam );
				} catch ( SQLException sqlEx ){
					JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
							, "Error", JOptionPane.ERROR_MESSAGE, null);
				}
				
				int goalsHome = info[0];
				int goalsAway = info[1];
				boolean overtime = false;
				boolean shootout = false;
				
				if ( info[2] == 1 )
					overtime = true;
				if ( info[3] == 1 )
					shootout = true;

				
				try {
					deleteRowFromGames( year, month, day, homeTeam, awayTeam, goalsHome, goalsAway, overtime, shootout );
				} catch ( SQLException sqlEx ){
					JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
							, "Error", JOptionPane.ERROR_MESSAGE, null);
				}	
				
				// reset fields after delete
				cmbMonth.setSelectedIndex(0);
				cmbYear.setSelectedIndex(0);
				cmbDay.setSelectedIndex(0);
				cmbHome.setSelectedIndex(0);
				cmbAway.setSelectedIndex(0);
				fldHome.setText("");
				fldAway.setText("");
				if ( chbOvertime.isSelected() )
					chbOvertime.setSelected(false);
				if ( chbShootout.isSelected() )
					chbShootout.setSelected(false);
			}// end btnDelete
			
			// Retrieve button
			if ( e.getSource() == btnRetrieve ) {
				int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
				int month = cmbMonth.getSelectedIndex() +1;
				int day = cmbDay.getSelectedIndex() + 1;
				int homeTeam = cmbHome.getSelectedIndex() + 1;
				int awayTeam = cmbAway.getSelectedIndex() + 1;
				int info[] = new int[4];
				
				// query for goals and overtime/shootout info to pass to delete method
				try {
					info = getGameInfo( year, month, day, homeTeam, awayTeam );
				} catch ( SQLException sqlEx ){
					JOptionPane.showMessageDialog(null, "Error:\nSQL Excpetion" +sqlEx.getMessage()
							, "Error", JOptionPane.ERROR_MESSAGE, null);
				}
				
				int goalsHome = info[0];
				int goalsAway = info[1];
				boolean overtime = false;
				boolean shootout = false;
				
				if ( info[2] == 1 )
					overtime = true;
				if ( info[3] == 1 )
					shootout = true;
				
				// Check that there is data for the games
				if ( goalsHome > 0 && goalsAway > 0 ) {
					// set to fields
					fldHome.setText(Integer.toString( goalsHome ));
					fldAway.setText(Integer.toString( goalsAway ));
					
					if ( overtime )
						chbOvertime.setSelected(true);
					if ( shootout )
						chbShootout.setSelected(true);
				} else
					JOptionPane.showMessageDialog(null, "Game not found!", "Retrieve Error", JOptionPane.ERROR_MESSAGE);
			}// end btnRetrieve
			
			if ( e.getSource() == btnStandings ) {
				standings.setVisible(true);
				
				try {
					createTable();
				} catch ( SQLException sqlEx ) {
					JOptionPane.showMessageDialog(null, "SQLException:\n"+sqlEx.getMessage(), 
							"Error", JOptionPane.ERROR_MESSAGE );
				}
				standings.add(tablePanel);
			}// end btnStandings

			// Clear button
			if ( e.getSource() == btnClear ) {
				// reset fields
				cmbMonth.setSelectedIndex(0);
				cmbYear.setSelectedIndex(0);
				cmbDay.setSelectedIndex(0);
				cmbHome.setSelectedIndex(0);
				cmbAway.setSelectedIndex(0);
				fldHome.setText("");
				fldAway.setText("");
				if ( chbOvertime.isSelected() )
					chbOvertime.setSelected(false);
				if ( chbShootout.isSelected() )
					chbShootout.setSelected(false);
			}// end btnClear
			
			// Close button
			if ( e.getSource() == btnClose ) {
				int buttonClick = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to exit?", "Warning", buttonClick);
				if ( dialogResult == JOptionPane.YES_OPTION )
					M_S_NHLController.this.dispose();
			}// end btnClose
		
		}//end actionPerformed()
	}//end inner class
	
}//end class M_S_NHLController