Testidatan lisäyslauseet:

INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES (“mikko.supermallikas@kuukkeli.com”, “MikkoMallikas”, “mi84as”, true); // id = 1
INSERT INTO Kayttaja (sahkoposti, kayttajanimi, salasana, moderaattori) VALUES (“sanni.supermallikas@kuukkeli.com”, “SanniMallikas”, “sa91as”, false); // id = 2
INSERT INTO Alue (otsikko) VALUES (“Virkistäytyminen”); // id = 1
INSERT INTO Alue (otiskko) VALUES (“Työpaikkahyvinvointi”);  // id = 2
INSERT INTO Keskustelu (otsikko, alue) VALUES (“Virkistyspäivä 2.8.2016”, 1); // id = 1
INSERT INTO Keskustelu (otsikko, alue) VALUES (“Retki Fazerille 2.5.”, 1); // id = 2
INSERT INTO Keskustelu (otsikko, alue) VALUES (“Uudet penkit ovat tulleet!”, 2); // id = 3
INSERT INTO Viesti (viesti, kayttaja, keskustelu) VALUES(“uudet punaiset penkit noudettavissa krs. 3!!”, 1, 3);
INSERT INTO Viesti (viesti, kayttaja, keskustelu) VALUES(“eikö sinisiä tullutkaan?? :(”, 2, 3); 
INSERT INTO Viesti (viesti, kayttaja, keskustelu) VALUES (“Bussilla vai kimppakyydeillä??”, 2, 2);

