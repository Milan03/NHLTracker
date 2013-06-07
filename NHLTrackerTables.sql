CREATE DATABASE NHLTracker;

CREATE TABLE NHLTracker.Games
(
	GameID INT NOT NULL AUTO_INCREMENT,
	HomeTeamID INT,
	AwayTeamID INT,
	HomeTeamGoals INT,
	AwayTeamGoals INT,
	GameMonth VARCHAR(9),
	GameDay INT,
	GameYear INT,
	Overtime BOOL,
	Shootout BOOL,
	PRIMARY KEY ( GameID ),
	FOREIGN KEY ( HomeTeamID ) REFERENCES 
		NHLTracker.Teams( TeamID ),
	FOREIGN KEY ( AwayTeamID ) REFERENCES 
		NHLTracker.Teams( TeamID )	
);

CREATE TABLE NHLTracker.Teams
(
	TeamID INT NOT NULL AUTO_INCREMENT,
	TeamName VARCHAR(50) NOT NULL,
	Division VARCHAR(30),
	GamesPlayed INT,
	Wins INT,
	RegulationLosses INT,
	OvertimeLosses INT,
	ShootoutLosses INT,
	Points INT,
	PRIMARY KEY ( TeamID )
)