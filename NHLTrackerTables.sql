-- File Name: NHLTrackerTables.sql
-- Date: Jun 6, 2013
-- Author: Milan Sobat
-- Student #: 0469245
-- Course: INFO-5051
-- Purpose: Used to create the NHLTracker database with the tables: Games 
-- 			and Teams. Games contains all game records between the NHL
-- 			teams and Teams hold all the team information.

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