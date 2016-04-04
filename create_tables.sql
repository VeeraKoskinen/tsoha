Luontikyselyt tauluille:

CREATE TABLE Kayttaja (
	id integer PRIMARY KEY,
	sahkoposti string UNIQUE,
	kayttajanimi string NOT NULL,
	salasana string NOT NULL, 
	moderaattori boolean NOT NULL              
	);

CREATE TABLE Alue (
	id integer PRIMARY KEY,
	otsikko string NOT NULL
	);

CREATE TABLE Keskustelu (
	id integer PRIMARY KEY,
	otsikko string NOT NULL,
	alue integer,
	FOREIGN KEY (alue) REFERENCES Keskustelualue(id)
	);

CREATE TABLE Viesti (
	id integer PRIMARY KEY,
	viesti text,
	aika timestamp,  //pitää löytää oikea tietomuoto jotta oikea päivä ja aika saadaan automaattisesti tauluun
	kayttaja integer,
	keskustelu integer,
	FOREIGN KEY (kayttaja) REFERENCES Kayttaja (id),
	FOREIGN KEY (keskustelu) REFERENCES Keskustelu (id)
	);

