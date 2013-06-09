-- File Name: NHLTrackerInserts.sql
-- Date: Jun 6, 2013
-- Author: Milan Sobat
-- Student #: 0469245
-- Course: INFO-5051
-- Purpose: Used to fill the NHLTracker.Teams table with all current NHL teams.

INSERT INTO NHLTracker.Teams ( TeamName, Division, GamesPlayed, Wins, 
								RegulationLosses, OvertimeLosses, ShootoutLosses, Points )
VALUES ( "Chicago Blackhawks", "Central Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Columbus Blue Jackets", "Central Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Detriot Red Wings", "Central Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Nashville Predators", "Central Division", 0, 0, 0, 0, 0, 0 ),
	   ( "St. Louis Blues", "Central Division", 0, 0, 0, 0, 0, 0 ),
	   ( "New Jersey Devils", "Atlantic Division", 0, 0, 0, 0, 0, 0 ),
	   ( "New York Islanders", "Atlantic Division", 0, 0, 0, 0, 0, 0 ),
	   ( "New York Rangers", "Atlantic Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Philadelphia Flyers", "Atlantic Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Pittsburgh Penguins", "Atlantic Division", 0, 0, 0, 0, 0, 0 );

INSERT INTO NHLTracker.Teams ( TeamName, Division, GamesPlayed, Wins, 
								RegulationLosses, OvertimeLosses, ShootoutLosses, Points )
VALUES ( "Calgary Flames", "Northwest Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Colorado Avalanche", "Northwest Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Edmonton Oilers", "Northwest Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Minnesota Wild", "Northwest Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Vancouver Canucks", "Northwest Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Boston Bruins", "Northeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Buffalo Sabres", "Northeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Montréal Canadiens", "Northeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Ottawa Senators", "Northeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Toronto Maple Leafs", "Northeast Division", 0, 0, 0, 0, 0, 0 );

INSERT INTO NHLTracker.Teams ( TeamName, Division, GamesPlayed, Wins, 
								RegulationLosses, OvertimeLosses, ShootoutLosses, Points )
VALUES ( "Anaheim Ducks", "Pacific Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Dallas Stars", "Pacific Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Los Angeles Kings", "Pacific Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Pheonix Coyotes", "Pacific Division", 0, 0, 0, 0, 0, 0 ),
	   ( "San Jose Sharks", "Pacific Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Carolina Hurricanes", "Southeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Florida Panthers", "Southeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Tampa Bay Lightning", "Southeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Washington Capitals", "Southeast Division", 0, 0, 0, 0, 0, 0 ),
	   ( "Winnipeg Jets", "Southeast Division", 0, 0, 0, 0, 0, 0 );

Select * from nhltracker.teams