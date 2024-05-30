/**
 * Client/Server board game implementation, where 4/6 Players
 * move each 4 figures from start to finish positions.
 * 
 * Copyright (C) 2024 Christian Alexander Wiesenäcker (Hessen, Germany) All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.VakanterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator.AktiverSpielerTodoAktion;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld.FeldArt;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;

/**
 * <pre>
 * 
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class RundenLaufMitHerausSchlagenDaoMitSqlDatenbank implements RundenLaufMitHerausschlagenDao {
	/**
	 * Pattern for hosts
	 */
	private static final Pattern PATTERN_HOST = Pattern.compile(
			"(^[a-zA-Z_][a-zA-Z0-9_]*([.][a-zA-Z_][a-zA-Z0-9_]*)*$)|"
					+ "(^\\[0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?\\]$)|"
					+ "(^\\[[0-9a-fA-F:]+\\]$)"
	);

	/**
	 * Pattern for database table names
	 */
	private static final Pattern PATTERN_DATABASE_NAME = Pattern.compile(
			"[a-zA-Z0-9_]+"
	);
	
	private static final String REGEX_MULTILINE_STRING = "(^|》)\\s*(《|$)";
	
	/**
	 * Specifies the database backend provider
	 */
	private final DatabaseBackendProvider backendProvider;
	
	/**
	 * Database directory, where a file based backend stores its database.
	 */
	private final File databaseDirectory;
	
	/**
	 * the database url
	 */
	private final String url;
	
	/**
	 * the username, that used to eventually authenicate at the database
	 */
	private final String user;
	
	/**
	 * the password, that is used to eventually authenticate at the database
	 */
	private final String passwort;

	private LogSql logSql = new NoLogSql();
	
	/**
	 * Gibt an, ob die {@link #erstellenFarbenTabelle()} zur potentiellen Erstellung der Farben Tabelle schon aufgerufen wurde
	 */
	private boolean aufgerufenErstellenFarbenTabelle;
	
	/**
	 * Gibt an, ob die {@link #erstellenFigurenTabelle()} zur potentiellen Erstellung der Figuren Tabelle schon aufgerufen wurde
	 */
	private boolean aufgerufenErstellenFigurenTabelle;
	
	/**
	 * Gibt an, ob die {@link #erstellenSpielerTabelle()} zur potentiellen Erstellung der Spieler Tabelle schon aufgerufen wurde
	 */
	private boolean aufgerufenErstellenSpielerTabelle;
	
	/**
	 * Gibt an, ob die {@link #erstellenAktiveSpielerTabelle()} zur potentiellen Erstellung der AktiveSpieler Tabelle schon aufgerufen wurde
	 */
	private boolean aufgerufenErstellenAktiverSpielerTabelle;
	
	/**
	 * Gibt an, ob die {@link #erstellenSpielFelderTabelle()} zur potentiellen Erstellung der SpielFelder Tabelle schon aufgerufen wurde
	 */
	private boolean aufgerufenErstellenSpielFelderTabelle;
	
	/**
	 * Gibt an, ob die {@link erstellenSpielZustaendeTabelle()} zur potentiellen Erstellung der SpielZustaende Tabelle schon aufgerufen wurde
	 */
	private boolean aufgerufenErstellenSpielZustaendeTabelle;
	
	/**
	 * <pre>
	 * 
	 * 
	 * @param backendProvider gibt die spezifische SQL Backend implementierung an
	 * @param databaseDirectory der Pfad zum Datenbank-Ordner, wenn der Datenbank-Backend dateibasiert ist
	 * @param url die Url zur abzufragenden Datenbank
	 * @param user der Username zur Authentifizierung
	 * @param passwort das Passwort zur Authentifizierung
	 * 
	 * </pre>
	 */
	private RundenLaufMitHerausSchlagenDaoMitSqlDatenbank(DatabaseBackendProvider backendProvider, File databaseDirectory, String url,
			String user, String passwort) {
		this.backendProvider = backendProvider;
		this.databaseDirectory = databaseDirectory;
		this.url = url;
		this.user = user;
		this.passwort = passwort;
	}

	/**
	 * <pre>
	 * 
	 * Verbindet zu einer MySQL Datenbank, welche auf (host + ":" + port) auf neue Verbindungen wartet, und
	 * greift auf eine Datenbank zu, deren Name gleich dem übergebenen Parameterwert aus tabelle entspricht.
	 * 
	 * Die Datenbank wird neu erstellt, wenn sie noch nicht verfügbar ist.
	 * 
	 * @param host der Datenbank-Host
	 * @param prot der Port, an der die Datenbank auf neue Verbindungen wartet
	 * @param datenBankName der Datenbank-Name
	 * @return
	 * 
	 * </pre>
	 */
	public static RundenLaufMitHerausSchlagenDaoMitSqlDatenbank erzeugeMenschAergerDichNichtDaoMitMySqlDatenbank(
			String host,
			int port,
			String datenBankName,
			String user,
			String passwort
	) {
		if(!PATTERN_HOST.matcher(host).matches()) {
			throw new IllegalArgumentException("invalid host");
		}
		if(port < 1 || port > 65535) {
			throw new IllegalArgumentException("Invalid port");
		}
		if(!PATTERN_DATABASE_NAME.matcher(datenBankName).matches()) {
			throw new IllegalArgumentException("Invalid table name");
		}
		
		// MySQL url =  "jdbc:mysql://localhost:3306/amigurumi_db?createDatabaseIfNotExist=true"
		return new RundenLaufMitHerausSchlagenDaoMitSqlDatenbank(
				DatabaseBackendProvider.MYSQL,
				null,
				"jdbc:mysql://" + host + ":" + port + "/" + datenBankName + "?createDatabaseIfNotExist=true",
				user,
				passwort
		);
	}
	
	/**
	 * <pre>
	 * 
	 * @param datenBankName
	 * @return
	 * 
	 * </pre>
	 */
	public static RundenLaufMitHerausSchlagenDaoMitSqlDatenbank erzeugeMenschAergerDichNichtDaoMitMemorySqliteDatenbank() {
		return new RundenLaufMitHerausSchlagenDaoMitSqlDatenbank(
				DatabaseBackendProvider.SQLITE,
				null,
				"jdbc:sqlite::memory:",
				null,
				null
		);
	}
	
	/**
	 * <pre>
	 * 
	 * @param datenBankName
	 * @return
	 * 
	 * </pre>
	 */
	public static RundenLaufMitHerausSchlagenDaoMitSqlDatenbank erzeugeMenschAergerDichNichtDaoMitFileSqliteDatenbank(String datenBankName) {
		if(!PATTERN_DATABASE_NAME.matcher(datenBankName).matches()) {
			throw new IllegalArgumentException("Ungültiger Datenbank-Name");
		}

		//String urlSqlite = "jdbc:sqlite:amigurumi_db.sqlite";
		return new RundenLaufMitHerausSchlagenDaoMitSqlDatenbank(
				DatabaseBackendProvider.SQLITE,
				new File("database"),
				"jdbc:sqlite:database/" + datenBankName + ".sqlite",
				null,
				null
		);
	}
	
	/**
	 * <pre>
	 * 
	 * Set logSql to enable / disable SQL query logging
	 * @param logSql the logSql
	 * 
	 * </pre>
	 */
	public void setLogSql(LogSql logSql) {
		if(logSql == null) {
			throw new NullPointerException();
		}
		this.logSql = logSql;
	}
	
	// ******************************************************************************************
	// **** Start Erstellen / Löschen Tabellen
	// ******************************************************************************************

	public void erstellenAlleTabellen() {
		erstellenAlleTabellen(false);
	}
	
	public void erstellenAlleTabellen(boolean minimal) {
		List<Boolean> test = Arrays.asList(
				aufgerufenErstellenFarbenTabelle,
				aufgerufenErstellenFigurenTabelle,
				aufgerufenErstellenSpielFelderTabelle,
				aufgerufenErstellenAktiverSpielerTabelle,
				aufgerufenErstellenSpielerTabelle,
				aufgerufenErstellenSpielZustaendeTabelle
		);
		if(test.stream().allMatch(s -> s)) {
			return;
		}
		
		if(!minimal || !aufgerufenErstellenFarbenTabelle) {
			erstellenFarbenTabelle();
			aufgerufenErstellenFarbenTabelle = true;
		}
		
		if(!minimal || !aufgerufenErstellenFigurenTabelle) {
			erstellenFigurenTabelle();
			aufgerufenErstellenFigurenTabelle = true;
		}
		
		if(!minimal || !aufgerufenErstellenSpielFelderTabelle) {
			erstellenSpielFelderTabelle();
			aufgerufenErstellenSpielFelderTabelle = true;
		}
		
		if(!minimal || !aufgerufenErstellenSpielerTabelle) {
			erstellenSpielerTabelle();
			aufgerufenErstellenSpielerTabelle = true;
		}
		
		if(!minimal || !aufgerufenErstellenAktiverSpielerTabelle) {
			erstellenAktiveSpielerTabelle();
			aufgerufenErstellenAktiverSpielerTabelle = true;
		}
		
		if(!minimal || !aufgerufenErstellenSpielZustaendeTabelle) {
			erstellenSpielZustaendeTabelle();
			aufgerufenErstellenSpielZustaendeTabelle = true;
		}
	}
	
	/**
	 * <pre>
	 * 
	 * Erstellt die Tabelle für SpielZustände.
	 * 
	 * </pre>
	 */
	public void erstellenSpielZustaendeTabelle() {
		logSql.enter();
		try {
			if(databaseDirectory != null) {
				databaseDirectory.mkdirs();
			}
			
			String sqlGeneral = """
					《CREATE TABLE IF NOT EXISTS SpielZustaende(》
					《		schluessel %1$s NOT NULL PRIMARY KEY %2$s,》
					《		spielStart TIMESTAMP NULL,》
					《		spielEnde TIMESTAMP NULL,》
					《		fkAktiverSpieler %1$s NOT NULL,》
					《		anzahlSpielerPositionen INT NOT NULL,》
					《		anzahlKonkreterSpieler INT NOT NULL,》
					《		spielerOffset INT NOT NULL,》
					
					《		FOREIGN KEY(fkAktiverSpieler) REFERENCES AktiveSpieler(schluessel)》
					《)》
			""".replaceAll(REGEX_MULTILINE_STRING, "\n").trim();
	
			String sqlQuery = String.format(
					sqlGeneral,
					backendProvider.getSqlPrimaryKeyTypeName(),
					backendProvider.getSqlAutoIncrement()
			);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
			) {
				logSql.logSql(sqlQuery);
				uebersetzer.execute(sqlQuery);
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}

	/**
	 * <pre>
	 * 
	 * Erstellt die Tabelle für Farben.
	 * 
	 * </pre>
	 */
	public void erstellenFarbenTabelle() {
		logSql.enter();
		try {
			if(databaseDirectory != null) {
				databaseDirectory.mkdirs();
			}
			
			String sqlGeneral = """
					《CREATE TABLE IF NOT EXISTS Farben(》
					《		schluessel %1$s NOT NULL PRIMARY KEY %2$s,》
					《		displayName VARCHAR(128) NULL,》
					《		rot DOUBLE NOT NULL,》
					《		gruen DOUBLE NOT NULL,》
					《		blau DOUBLE NOT NULL,》
					《		farbwert INT NOT NULL》
					《)》
			""".replaceAll(REGEX_MULTILINE_STRING, "\n").trim();

			String sqlQuery = String.format(
					sqlGeneral,
					backendProvider.getSqlPrimaryKeyTypeName(),
					backendProvider.getSqlAutoIncrement()
			);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
			) {
				logSql.logSql(sqlQuery);
				uebersetzer.execute(sqlQuery);
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}

	/**
	 * <pre>
	 * 
	 * Erstellt die Tabelle für Figuren.
	 * 
	 * Referenzierte Tabellen:
	 * - SpielZustaende {@link #erstellenSpielZustaendeTabelle()}
	 * - Spieler {@link #erstellenSpielerTabelle()}
	 * - Farben {@link #erstellenFarbenTabelle()}
	 * - SpielFelder {@link #erstellenSpielFelderTabelle()}
	 * 
	 * </pre>
	 */
	public void erstellenFigurenTabelle() {
		logSql.enter();
		try {
			if(databaseDirectory != null) {
				databaseDirectory.mkdirs();
			}
			
			String sqlGeneral = """
					《CREATE TABLE IF NOT EXISTS Figuren(》
					《		schluessel %1$s NOT NULL PRIMARY KEY %2$s,》
					《		fkSpielZustand %1$s NULL,》
					《		fkSpieler %1$s NULL,》
					《		fkFarbe %1$s NOT NULL,》
					《		fkSpielFeld %1$s NOT NULL,》
					
					《		FOREIGN KEY(fkSpielZustand) REFERENCES SpielZustand(schluessel),》
					《		FOREIGN KEY(fkSpieler) REFERENCES Farbe(spieler),》
					《		FOREIGN KEY(fkFarbe) REFERENCES Farbe(schluessel),》
					《		FOREIGN KEY(fkSpielFeld) REFERENCES SpielFelder(schluessel)》
					《)》
			""".replaceAll(REGEX_MULTILINE_STRING, "\n").trim();

			String sqlQuery = String.format(
					sqlGeneral,
					backendProvider.getSqlPrimaryKeyTypeName(),
					backendProvider.getSqlAutoIncrement()
			);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
			) {
				logSql.logSql(sqlQuery);
				uebersetzer.execute(sqlQuery);
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}

	/**
	 * <pre>
	 * 
	 * Erstellt die Tabelle für aktive Spieler.
	 * 
	 * Referenzierte Tabellen:
	 * - SpielZustand {@link #erstellenSpielZustaendeTabelle()}
	 * - Spieler {@link #erstellenSpielerTabelle()}
	 * 
	 * </pre>
	 */
	public void erstellenAktiveSpielerTabelle() {
		logSql.enter();
		try {
			if(databaseDirectory != null) {
				databaseDirectory.mkdirs();
			}
			
			String sqlGeneral = """
					《CREATE TABLE IF NOT EXISTS AktiveSpieler(》
					《		schluessel %1$s NOT NULL PRIMARY KEY %2$s,》
					《		fkSpieler %1$s NOT NULL,》
					《		todoAktion %3$s NOT NULL,》
					《		wuerfelErgebnis %4$s NULL,》
					《		anzahlWuerfelVersuche INT NOT NULL,》
					《		gezogen BOOLEAN NOT NULL,》
					
					《		FOREIGN KEY(fkSpieler) REFERENCES Spieler(schluessel)》
					《)》
			""".replaceAll(REGEX_MULTILINE_STRING, "\n").trim();

			//《		fkSpielZustand %1$s NOT NULL,》
			//《		FOREIGN KEY(fkSpielZustand) REFERENCES SpielZustaende(schluessel),》

			String sqlQuery = String.format(
					sqlGeneral,
					backendProvider.getSqlPrimaryKeyTypeName(),
					backendProvider.getSqlAutoIncrement(),
					backendProvider.enumPattern("todoAktion", "MUSS_WUERFELN", "MUSS_ZIEHEN", "FERTIG"),
					backendProvider.enumPattern("wuerfelErgebnis", "EINS", "ZWEI", "DREI", "VIER", "FUENF", "SECHS")
			);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
			) {
				logSql.logSql(sqlQuery);
				uebersetzer.execute(sqlQuery);
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}

	/**
	 * <pre>
	 * 
	 * Erstellt die Tabelle für Spieler.
	 * 
	 * Referenzierte Tabellen:
	 * - SpielZustaende {@link #erstellenSpielZustaendeTabelle()}
	 * - Farben {@link #erstellenFarbenTabelle()}
	 * - SpielFelder {@link #erstellenSpielFelderTabelle()}
	 * 
	 * </pre>
	 */
	public void erstellenSpielerTabelle() {
		logSql.enter();
		try {
			if(databaseDirectory != null) {
				databaseDirectory.mkdirs();
			}
			
			String sqlGeneral = """
					《CREATE TABLE IF NOT EXISTS Spieler(》
					《		schluessel %1$s NOT NULL PRIMARY KEY %2$s,》
					《		type %3$s NOT NULL,》
					《		name VARCHAR(112) NULL,》
					《		fkSpielZustand %1$s NULL,》
					《		fkFarbe %1$s NOT NULL,》
					《		fkStartStreckenFeld %1$s NOT NULL,》
					《		fkEndStreckenFeld %1$s NOT NULL,》
					《		gewinnerPosition INT NULL,》
					
					《		FOREIGN KEY(fkSpielZustand) REFERENCES SpielZustand(schluessel),》
					《		FOREIGN KEY(fkFarbe) REFERENCES Farben(schluessel),》
					《		FOREIGN KEY(fkStartStreckenFeld) REFERENCES SpielFelder(schluessel),》
					《		FOREIGN KEY(fkEndStreckenFeld) REFERENCES SpielFelder(schluessel)》
					《)》
			""".replaceAll(REGEX_MULTILINE_STRING, "\n").trim();

			String sqlQuery = String.format(
					sqlGeneral,
					backendProvider.getSqlPrimaryKeyTypeName(),
					backendProvider.getSqlAutoIncrement(),
					backendProvider.enumPattern("type", "KONKRET", "VAKANT")
			);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
			) {
				logSql.logSql(sqlQuery);
				uebersetzer.execute(sqlQuery);
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}

	/**
	 * <pre>
	 * 
	 * Erstellt die Tabelle für SpielFelder.
	 * 
	 * Referenzierte Tabellen:
	 * - Farben {@link #erstellenFarbenTabelle()}
	 * 
	 * </pre>
	 */
	public void erstellenSpielFelderTabelle() {
		logSql.enter();
		try {
			if(databaseDirectory != null) {
				databaseDirectory.mkdirs();
			}
			
			String sqlGeneral = """
					《CREATE TABLE IF NOT EXISTS SpielFelder(》
					《		schluessel %1$s NOT NULL PRIMARY KEY %2$s,》
					《		feldArt %3$s NOT NULL,》
					《		feldPosition INT NOT NULL,》
					《		fkFarbe %1$s NULL,》
					
					《		FOREIGN KEY(fkFarbe) REFERENCES Farben(schluessel)》
					《)》
			""".replaceAll(REGEX_MULTILINE_STRING, "\n").trim();
			
			String sqlQuery = String.format(
					sqlGeneral,
					backendProvider.getSqlPrimaryKeyTypeName(),
					backendProvider.getSqlAutoIncrement(),
					backendProvider.enumPattern("feldArt", "START_FELD", "STRECKEN_FELD", "ZIEL_FELD")
			);
			logSql.logSql(sqlQuery);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
			) {
				uebersetzer.execute(sqlQuery);
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}
	
	private void loeschenTabelle(String tabellenName) {
		logSql.enter();
		try {
		
			if(databaseDirectory != null) {
				databaseDirectory.mkdirs();
			}
			
			String sqlQuery = "DROP TABLE IF EXISTS " + tabellenName;
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
			) {
				logSql.logSql(sqlQuery);
				uebersetzer.execute(sqlQuery);
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}
	
	/**
	 * <pre>
	 * 
	 * Löscht die Tabelle für SpielZustände.
	 * 
	 * </pre>
	 */
	public void loeschenSpielZustaendeTabelle() {
		aufgerufenErstellenSpielZustaendeTabelle = false;
		loeschenTabelle("SpielZustaende");
	}

	/**
	 * <pre>
	 * 
	 * Löscht die Tabelle für Farben.
	 * 
	 * </pre>
	 */
	public void loeschenFarbenTabelle() {
		aufgerufenErstellenFarbenTabelle = false;
		loeschenTabelle("Farben");
	}

	/**
	 * <pre>
	 * 
	 * Löscht die Tabelle für Figuren.
	 * 
	 * </pre>
	 */
	public void loeschenFigurenTabelle() {
		aufgerufenErstellenFigurenTabelle = false;
		loeschenTabelle("Figuren");
	}

	/**
	 * <pre>
	 * 
	 * Löscht die Tabelle für aktive Spieler.
	 * 
	 * </pre>
	 */
	public void loeschenAktiveSpielerTabelle() {
		aufgerufenErstellenAktiverSpielerTabelle = false;
		loeschenTabelle("AktiveSpieler");
	}

	/**
	 * <pre>
	 * 
	 * Löscht die Tabelle für Spieler.
	 * 
	 * </pre>
	 */
	public void loeschenSpielerTabelle() {
		aufgerufenErstellenSpielerTabelle = false;
		loeschenTabelle("Spieler");
	}

	/**
	 * <pre>
	 * 
	 * Löscht die Tabelle für SpielFelder.
	 * 
	 * </pre>
	 */
	public void loeschenSpielFelderTabelle() {
		aufgerufenErstellenSpielFelderTabelle = false;
		loeschenTabelle("SpielFelder");
	}

	// ******************************************************************************************
	// **** Start Access / Modify data
	// ******************************************************************************************
	@Override
	public Set<Long> getSpielZustaendeSchluessel() {
		return getAlleSchluessel("SpielZustaende");
	}
	
	@Override
	public Set<SpielZustand> getSpielZustaendeMenge() {
		return getSpielZustaendeMenge(null);
	}
	
	@Override
	public SpielZustand getSpielZustand(long schluessel) {
		return getSpielZustaendeMenge(schluessel).stream().findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("der SpielZustand ist nicht in der Datenbank"));
	}

	private Set<SpielZustand> getSpielZustaendeMenge(Long schluessel) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			HashSet<SpielZustand> ergebnis = new HashSet<>();
			
			String sqlQuery = "SELECT * FROM SpielZustaende";
			if(schluessel != null) {
				sqlQuery = "SELECT * FROM SpielZustaende WHERE schluessel = ?";
			}
			logSql.logSql(sqlQuery);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery)
			) {
				if(schluessel != null) {
					uebersetzer.setLong(1, schluessel);
				}
	
				uebersetzer.execute();
				
				ResultSet antwort = uebersetzer.getResultSet();
				while(antwort.next()) {
					try {
						Long derSchluessel = antwort.getLong("schluessel");
						
						LocalDateTime spielStart = null;
						LocalDateTime spielEnde = null;
						{
							Timestamp tsSpielStart = antwort.getTimestamp("spielStart");
							Timestamp tsSpielEnde = antwort.getTimestamp("spielEnde");
							if(tsSpielStart != null) {
								spielStart = tsSpielStart.toLocalDateTime();
							}
							if(tsSpielEnde != null) {
								spielEnde = tsSpielEnde.toLocalDateTime();
							}
						}
		
						int anzahlSpielerPositionen = antwort.getInt("anzahlSpielerPositionen");
						int anzahlKonkreterSpieler = antwort.getInt("anzahlKonkreterSpieler");
						int spielerOffset = antwort.getInt("spielerOffset");
		
						long fkAktiverSpieler = antwort.getLong("fkAktiverSpieler");
						AktiverSpielerDekorator aktiverSpieler = getAktiverSpieler(fkAktiverSpieler);
						Set<Spieler> alleSpielerEinesSpiels = getSpielerMengeBySpielZustandSchluessel(derSchluessel);
						Set<Figur> alleFigurenEinesSpiels = getFigurenMengeBySpielZustandSchluessel(derSchluessel);
						
						SpielZustand derSpielZustand = new SpielZustand(
								aktiverSpieler,
								alleSpielerEinesSpiels,
								alleFigurenEinesSpiels,
								anzahlSpielerPositionen,
								anzahlKonkreterSpieler,
								spielerOffset
						);
						derSpielZustand.setSchluessel(derSchluessel);
						derSpielZustand.setSpielStart(spielStart);
						derSpielZustand.setSpielEnde(spielEnde);
						ergebnis.add(derSpielZustand);
					} catch(PrimaerSchluesselException ausnahme) {
						ausnahme.printStackTrace();
					}
				}
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
			return ergebnis;
		} finally {
			logSql.exit();
		}
	}

	private PreparedStatementConsumer erstellePreparedStatementConsumer(SpielZustand spielZustand) {
		return uebersetzer -> {
			LocalDateTime spielStart = spielZustand.getSpielStart();
			LocalDateTime spielEnde = spielZustand.getSpielEnde();
			long fkAktiverSpieler = spielZustand.getAktiverSpieler().getSchluessel();
			int anzahlSpielerPositionen = spielZustand.getAnzahlSpielerPositionen();
			int anzahlKonkreterSpieler = spielZustand.getAnzahlKonkreterSpieler();
			int spielerOffset = spielZustand.getSpielerOffset();

			if(spielStart == null) {
				uebersetzer.setNull(1, Types.TIMESTAMP);
			} else {
				uebersetzer.setTimestamp(1, Timestamp.from(Instant.from(spielStart)));
			}
			if(spielEnde == null) {
				uebersetzer.setNull(2, Types.TIMESTAMP);
			} else {
				uebersetzer.setTimestamp(2, Timestamp.from(Instant.from(spielEnde)));
			}
			
			uebersetzer.setLong(3, fkAktiverSpieler);
			
			uebersetzer.setInt(4, anzahlSpielerPositionen);
			uebersetzer.setInt(5, anzahlKonkreterSpieler);
			uebersetzer.setInt(6, spielerOffset);
			
			Long schluessel = spielZustand.getSchluessel();
			if(schluessel != null) {
				uebersetzer.setLong(7, schluessel);
			}
		};
	}
	
	@Override
	public void hinzufuegenSpielZustand(SpielZustand spielZustand) {
		logSql.enter();
		try {
			if(spielZustand.getSchluessel() != null) {
				throw new PrimaerSchluesselException("der SpielZustand ist schon in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			AktiverSpielerDekorator aktiverSpieler = spielZustand.getAktiverSpieler();
			//aktiverSpieler.setFremdSchluesselSpielZustand(spielZustand.getSchluessel());
			if(aktiverSpieler.getSchluessel() == null) {
				hinzufuegenAktiverSpieler(aktiverSpieler);
			} else {
				updateAktiverSpieler(aktiverSpieler);
			}
			
			hinzufuegenElement(
					"""
							INSERT INTO SpielZustaende (
									spielStart,
									spielEnde,
									fkAktiverSpieler,
									anzahlSpielerPositionen,
									anzahlKonkreterSpieler,
									spielerOffset
							) VALUES (?, ?, ?, ?, ?, ?)
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(spielZustand),
					spielZustand::setSchluessel
			);		
	
			spielZustand.getFigurenMenge().forEach(figuren -> {
				figuren.setFremdSchluesselSpielZustand(spielZustand.getSchluessel());
			});
			
			Set<Spieler> spielerMenge = spielZustand.getSpielerMenge();
			spielerMenge.forEach(spieler -> {
				spieler.setFremdSchluesselSpielZustand(spielZustand.getSchluessel());
				if(spieler.getSchluessel() == null) {
					hinzufuegenSpieler(spieler);
				} else {
					updateSpieler(spieler);
				}
			});
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void updateSpielZustand(SpielZustand spielZustand) {
		logSql.enter();
		try {
			if(spielZustand.getSchluessel() == null) {
				throw new PrimaerSchluesselException("der SpielZustand ist noch nicht in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			updateElement(
					"""
							UPDATE SpielZustaende SET
									spielStart = ?,
									spielEnde = ?,
									fkAktiverSpieler = ?,
									anzahlSpielerPositionen = ?,
									anzahlKonkreterSpieler = ?,
									spielerOffset = ?
							WHERE schluessel = ?
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(spielZustand)
			);		
	
			spielZustand.getFigurenMenge().forEach(figuren -> {
				figuren.setFremdSchluesselSpielZustand(spielZustand.getSchluessel());
			});
			
			Set<Spieler> spielerMenge = spielZustand.getSpielerMenge();
			spielerMenge.forEach(spieler -> {
				spieler.setFremdSchluesselSpielZustand(spielZustand.getSchluessel());
				if(spieler.getSchluessel() == null) {
					hinzufuegenSpieler(spieler);
				} else {
					updateSpieler(spieler);
				}
			});
			
			AktiverSpielerDekorator aktiverSpieler = spielZustand.getAktiverSpieler();
			aktiverSpieler.setFremdSchluesselSpielZustand(spielZustand.getSchluessel());
			if(aktiverSpieler.getSchluessel() == null) {
				hinzufuegenAktiverSpieler(aktiverSpieler);
			} else {
				updateAktiverSpieler(aktiverSpieler);
			}
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void loeschenSpielZustand(long schluessel) {
		loeschenBySchluessel("SpielZustaende", schluessel);
	}

	@Override
	public void loeschenSpielZustand(SpielZustand zuLoeschen) {
		if(zuLoeschen.getSchluessel() == null) {
			throw new PrimaerSchluesselException("Das Element ist nicht in der Datenbank.");
		}
		loeschenBySchluessel("SpielZustaende", zuLoeschen.getSchluessel());
	}

	@Override
	public Set<Long> getAktiveSpielerSchluessel() {
		return getAlleSchluessel("AktiveSpieler");
	}
	
	@Override
	public Set<AktiverSpielerDekorator> getAktiveSpielerMenge() {
		return getAktiveSpielerMenge(null);
	}
	
	@Override
	public AktiverSpielerDekorator getAktiverSpieler(long schluessel) {
		return getAktiveSpielerMenge(schluessel).stream().findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("der aktive Spieler ist nicht in der Datenbank"));
	}

	private Set<AktiverSpielerDekorator> getAktiveSpielerMenge(Long schluessel) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			HashSet<AktiverSpielerDekorator> ergebnis = new HashSet<>();
			
			String sqlQuery = "SELECT * FROM AktiveSpieler";
			if(schluessel != null) {
				sqlQuery = "SELECT * FROM AktiveSpieler WHERE schluessel = ?";
			}
			logSql.logSql(sqlQuery);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery)
			) {
				if(schluessel != null) {
					uebersetzer.setLong(1, schluessel);
				}
	
				uebersetzer.execute();
				
				ResultSet antwort = uebersetzer.getResultSet();
				while(antwort.next()) {
					try {
						Long derSchluessel = antwort.getLong("schluessel");
		
						//Long fkSpielZustand = antwort.getLong("fkSpielZustand");
						Long fkSpieler = antwort.getLong("fkSpieler");
						
						String textTodoAktion = antwort.getString("todoAktion");
						String textWuerfelErgebnis = antwort.getString("wuerfelErgebnis");
						int anzahlWuerfelVersuche = antwort.getInt("anzahlWuerfelVersuche");
						boolean gezogen = antwort.getBoolean("gezogen");
						
						Spieler spieler = getSpieler(fkSpieler);
						if(!(spieler instanceof KonkreterSpieler)) {
							throw new SQLDataException();
						}
						KonkreterSpieler dekorierterSpieler = (KonkreterSpieler)spieler;
						
						AktiverSpielerTodoAktion todoAktion = AktiverSpielerTodoAktion.valueOf(textTodoAktion.toUpperCase(Locale.ENGLISH));
						WuerfelErgebnis wuerfelErgebnis = null;
						if(textWuerfelErgebnis != null) {
							wuerfelErgebnis = WuerfelErgebnis.valueOf(textWuerfelErgebnis.toUpperCase(Locale.ENGLISH));
						}
						
						AktiverSpielerDekorator derAktiveSpieler = AktiverSpielerDekorator.erstelleAktivenSpieler(
								dekorierterSpieler,
								todoAktion,
								wuerfelErgebnis,
								anzahlWuerfelVersuche,
								gezogen
						);
						derAktiveSpieler.setSchluessel(derSchluessel);
						//derAktiveSpieler.setFremdSchluesselSpielZustand(fkSpielZustand);
						ergebnis.add(derAktiveSpieler);
					} catch(PrimaerSchluesselException ausnahme) {
						ausnahme.printStackTrace();
					}
				}
			} catch (SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
			return ergebnis;
		} finally {
			logSql.exit();
		}
	}

	private PreparedStatementConsumer erstellePreparedStatementConsumerAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
		return uebersetzer -> {
			//Long fkSpielZustand = aktiverSpieler.getFremdSchluesselSpielZustand();
			Long fkSpieler = aktiverSpieler.getDerSpieler().getSchluessel();
			String textTodoAktion = aktiverSpieler.getAktuelleTodoAktion().name().toUpperCase(Locale.ENGLISH);
			WuerfelErgebnis wuerfelErgebnis = aktiverSpieler.getWuerfelErgebnis();
			String textWuerfelErgebnis = null;
			if(wuerfelErgebnis != null) {
				textWuerfelErgebnis = aktiverSpieler.getWuerfelErgebnis().name().toUpperCase(Locale.ENGLISH);
			}
			int anzahlWuerfelVersuche = aktiverSpieler.getWuerfelVersuchsAnzahl();
			boolean gezogen = aktiverSpieler.isHatGezogen();

			//uebersetzer.setLong(1, fkSpielZustand);
			uebersetzer.setLong(1, fkSpieler);
			uebersetzer.setString(2, textTodoAktion);
			if(textWuerfelErgebnis != null) {
				uebersetzer.setString(3, textWuerfelErgebnis);
			} else {
				uebersetzer.setNull(3, Types.VARCHAR);
			}
			uebersetzer.setInt(4, anzahlWuerfelVersuche);
			uebersetzer.setBoolean(5, gezogen);
			
			Long schluessel = aktiverSpieler.getSchluessel();
			if(schluessel != null) {
				uebersetzer.setLong(6, schluessel);
			}
		};
	}

	@Override
	public void hinzufuegenAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
		logSql.enter();
		try {
			if(aktiverSpieler.getSchluessel() != null) {
				throw new PrimaerSchluesselException("der aktive Spieler ist schon in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Spieler derSpieler = aktiverSpieler.getDerSpieler();
			if(derSpieler.getSchluessel() == null) {
				hinzufuegenSpieler(derSpieler);
			} else {
				updateSpieler(derSpieler);
			}
			
			hinzufuegenElement(
			//					fkSpielZustand,
					"""
							INSERT INTO AktiveSpieler (
								fkSpieler,
								todoAktion,
								wuerfelErgebnis,
								anzahlWuerfelVersuche,
								gezogen
							) VALUES (?, ?, ?, ?, ?)
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumerAktiverSpieler(aktiverSpieler),
					aktiverSpieler::setSchluessel
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void updateAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
		logSql.enter();
		try {
			if(aktiverSpieler.getSchluessel() == null) {
				throw new PrimaerSchluesselException("der aktive Spieler ist noch nicht in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Spieler derSpieler = aktiverSpieler.getDerSpieler();
			if(derSpieler.getSchluessel() == null) {
				hinzufuegenSpieler(derSpieler);
			} else {
				updateSpieler(derSpieler);
			}
			
			updateElement(
					//          fkSpielZustand = ?,
					"""
							UPDATE AktiveSpieler SET
								fkSpieler = ?,
								todoAktion = ?,
								wuerfelErgebnis = ?,
								anzahlWuerfelVersuche = ?,
								gezogen = ?
							WHERE schluessel = ?
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumerAktiverSpieler(aktiverSpieler)
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void loeschenAktiverSpieler(long schluessel) {
		loeschenBySchluessel("AktiveSpieler", schluessel);
	}

	@Override
	public void loeschenAktiverSpieler(AktiverSpielerDekorator zuLoeschen) {
		if(zuLoeschen.getSchluessel() == null) {
			throw new PrimaerSchluesselException("Das Element ist nicht in der Datenbank.");
		}
		loeschenBySchluessel("AktiveSpieler", zuLoeschen.getSchluessel());
	}

	@Override
	public Set<Long> getSpielerSchluessel() {
		return getAlleSchluessel("Spieler");
	}

	@Override
	public Set<Spieler> getSpielerMenge() {
		return getSpielerMenge(null, null);
	}

	@Override
	public Set<Spieler> getSpielerMengeBySpielZustandSchluessel(long spielZustandSchluessel) {
		return getSpielerMenge(null, spielZustandSchluessel);
	}

	@Override
	public Spieler getSpieler(long schluessel) {
		return getSpielerMenge(schluessel, null).stream().findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("der Spieler ist nicht in der Datenbank"));
	}

	private Set<Spieler> getSpielerMenge(Long schluessel, Long spielZustandSchluessel) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			HashSet<Spieler> ergebnis = new HashSet<>();
	
			Long value = null;
			String sqlQuery = "SELECT * FROM Spieler";
			if(schluessel != null) {
				sqlQuery = "SELECT * FROM Spieler WHERE schluessel = ?";
				value = schluessel;
			} else if(spielZustandSchluessel != null) {
				sqlQuery = "SELECT * FROM Spieler WHERE fkSpielZustand = ?";
				value = spielZustandSchluessel;
			}
			logSql.logSql(sqlQuery);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery)
			) {
				if(value != null) {
					uebersetzer.setLong(1, value);
				}
	
				uebersetzer.execute();
				
				ResultSet antwort = uebersetzer.getResultSet();
				while(antwort.next()) {
					try {
						Long derSchluessel = antwort.getLong("schluessel");
						String spielerType = antwort.getString("type");
		
						Long fkSpielZustand = antwort.getLong("fkSpielZustand");
						Long fkFarbe = antwort.getLong("fkFarbe");
						Long fkStartStreckenFeld = antwort.getLong("fkStartStreckenFeld");
						Long fkEndStreckenFeld = antwort.getLong("fkEndStreckenFeld");
						
						Farbe farbe = getFarbe(fkFarbe);
						SpielFeld startStreckenFeld = getSpielFeld(fkStartStreckenFeld);
						SpielFeld endStreckenFeld = getSpielFeld(fkEndStreckenFeld);
						
						Spieler derSpieler;
						switch(spielerType.toUpperCase(Locale.ENGLISH)) {
						case "KONKRET" -> {
							String name = antwort.getString("name");
							Set<Figur> alleFigurenDesSpielers = getFigurenMengeBySpielerSchluessel(derSchluessel);
							Integer gewinnerPosition = antwort.getInt("gewinnerPosition");
							if(antwort.wasNull()) {
								gewinnerPosition = null;
							}
		
							derSpieler = new KonkreterSpieler(
									farbe,
									startStreckenFeld,
									endStreckenFeld,
									name,
									alleFigurenDesSpielers,
									gewinnerPosition
							);
						}
						case "VAKANT" -> {
							derSpieler = new VakanterSpieler(
									farbe,
									startStreckenFeld,
									endStreckenFeld
							);
						}
						default -> throw new SQLDataException("unbekannte Spieler-Art");
						};
								
						derSpieler.setSchluessel(derSchluessel);
						if(derSpieler.getFremdSchluesselSpielZustand() == null) {
							derSpieler.setFremdSchluesselSpielZustand(fkSpielZustand);
						}
						ergebnis.add(derSpieler);
					} catch(PrimaerSchluesselException ausnahme) {
						ausnahme.printStackTrace();
					}
				}
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
			return ergebnis;
		} finally {
			logSql.exit();
		}
	}

	private PreparedStatementConsumer erstellePreparedStatementConsumerSpieler(Spieler spieler) {
		return uebersetzer -> {
			String textType;
			String name = null;
			Integer gewinnerPosition = null;
			Long fkSpielZustand = spieler.getFremdSchluesselSpielZustand();
			Long fkFarbe = spieler.getFarbe().getSchluessel();
			Long fkStartStreckenFeld = spieler.getStartStreckenFeld().getSchluessel();
			Long fkEndStreckenFeld = spieler.getEndStreckenFeld().getSchluessel();
			if(spieler instanceof KonkreterSpieler) {
				textType = "KONKRET";
				name = spieler.getName();
				gewinnerPosition = spieler.getGewinnerPosition(); 
			} else if(spieler instanceof VakanterSpieler) {
				textType = "VAKANT";
			} else {
				throw new IllegalArgumentException("kein unterstuetzter Spielertyp");
			}
			
			uebersetzer.setString(1, textType);
			if(name != null) {
				uebersetzer.setString(2, name);
			} else {
				uebersetzer.setNull(2, Types.VARCHAR);
			}
			if(fkSpielZustand != null) {
				uebersetzer.setLong(3, fkSpielZustand);
			} else {
				uebersetzer.setNull(3, switch(this.backendProvider) {
				case MYSQL -> Types.BIGINT;
				case SQLITE -> Types.INTEGER;
				});
			}
			uebersetzer.setLong(4,  fkFarbe);
			uebersetzer.setLong(5,  fkStartStreckenFeld);
			uebersetzer.setLong(6,  fkEndStreckenFeld);
			if(gewinnerPosition != null) {
				uebersetzer.setInt(7, gewinnerPosition);
			} else {
				uebersetzer.setNull(7, Types.INTEGER);
			}
			
			Long schluessel = spieler.getSchluessel();
			if(schluessel != null) {
				uebersetzer.setLong(8, schluessel);
			}
		};
	}

	@Override
	public void hinzufuegenSpieler(Spieler spieler) {
		logSql.enter();
		try {
			if(spieler.getSchluessel() != null) {
				throw new PrimaerSchluesselException("der Spieler ist schon in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Farbe farbe = spieler.getFarbe();
			if(farbe.getSchluessel() == null) {
				hinzufuegenFarbe(farbe);
			} else {
				updateFarbe(farbe);
			}

			SpielFeld startStreckenFeld = spieler.getStartStreckenFeld();
			if(startStreckenFeld.getSchluessel() == null) {
				hinzufuegenSpielFeld(startStreckenFeld);
			} else {
				updateSpielFeld(startStreckenFeld);
			}
			
			SpielFeld endStreckenFeld = spieler.getEndStreckenFeld();
			if(endStreckenFeld.getSchluessel() == null) {
				hinzufuegenSpielFeld(endStreckenFeld);
			} else {
				updateSpielFeld(endStreckenFeld);
			}
	
			hinzufuegenElement(
					"""
							INSERT INTO Spieler (
								type,
								name,
								fkSpielZustand,
								fkFarbe,
								fkStartStreckenFeld,
								fkEndStreckenFeld,
								gewinnerPosition
							) VALUES (?, ?, ?, ?, ?, ?, ?)
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumerSpieler(spieler),
					spieler::setSchluessel
			);
		
			spieler.getFigurenMenge().forEach(figur -> {
				if(figur.getFremdSchluesselSpielZustand() == null && spieler.getFremdSchluesselSpielZustand() != null) {
					figur.setFremdSchluesselSpielZustand(spieler.getFremdSchluesselSpielZustand());
				}
				figur.setFremdSchluesselSpieler(spieler.getSchluessel());
				figur.setFarbe(farbe);
				if(figur.getSchluessel() == null) {
					hinzufuegenFigur(figur);
				} else {
					updateFigur(figur);
				}
			});
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void updateSpieler(Spieler spieler) {
		logSql.enter();
		try {
			if(spieler.getSchluessel() == null) {
				throw new PrimaerSchluesselException("der Spieler ist noch nicht in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Farbe farbe = spieler.getFarbe();
			if(farbe.getSchluessel() == null) {
				hinzufuegenFarbe(farbe);
			} else {
				updateFarbe(farbe);
			}
	
			updateElement(
					"""
							UPDATE Spieler SET
								type = ?,
								name = ?,
								fkSpielZustand = ?,
								fkFarbe = ?,
								fkStartStreckenFeld = ?,
								fkEndStreckenFeld = ?,
								gewinnerPosition = ?
							WHERE schluessel = ?
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumerSpieler(spieler)
			);
			
			spieler.getFigurenMenge().forEach(figur -> {
				if(figur.getFremdSchluesselSpielZustand() == null) {
					figur.setFremdSchluesselSpielZustand(spieler.getFremdSchluesselSpielZustand());
				}
				if(figur.getFremdSchluesselSpieler() == null) {
					figur.setFremdSchluesselSpieler(spieler.getSchluessel());
				}
				figur.setFarbe(farbe);
				if(figur.getSchluessel() == null) {
					hinzufuegenFigur(figur);
				} else {
					updateFigur(figur);
				}
			});
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void loeschenSpieler(long schluessel) {
		loeschenBySchluessel("Spieler", schluessel);
	}

	@Override
	public void loeschenSpieler(Spieler zuLoeschen) {
		if(zuLoeschen.getSchluessel() == null) {
			throw new PrimaerSchluesselException("Das Element ist nicht in der Datenbank.");
		}
		loeschenBySchluessel("Spieler", zuLoeschen.getSchluessel());
	}

	@Override
	public Set<Long> getFarbenSchluessel() {
		return getAlleSchluessel("Farben");
	}
	
	@Override
	public Set<Farbe> getFarbenMenge() {
		return getFarbenMenge(null);
	}

	@Override
	public Farbe getFarbe(long schluessel) {
		return getFarbenMenge(schluessel).stream().findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("die Farbe war nicht in der Datenbank"));
	}

	private Set<Farbe> getFarbenMenge(Long schluessel) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			HashSet<Farbe> ergebnis = new HashSet<>();
			
			String sqlQuery = "SELECT * FROM Farben";
			if(schluessel != null) {
				sqlQuery = "SELECT * FROM Farben WHERE schluessel = ?";
			}
			logSql.logSql(sqlQuery);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery)
			) {
				if(schluessel != null) {
					uebersetzer.setLong(1, schluessel);
				}
	
				uebersetzer.execute();
				
				ResultSet antwort = uebersetzer.getResultSet();
				while(antwort.next()) {
					try {
						Long derSchluessel = antwort.getLong("schluessel");
						
						String displayName = antwort.getString("displayName");
						double rot = antwort.getDouble("rot");
						double gruen = antwort.getDouble("gruen");
						double blau = antwort.getDouble("blau");
						int farbwert = antwort.getInt("farbwert");
						
						Farbe dieFarbe = new Farbe(displayName, rot, gruen, blau, farbwert);
						dieFarbe.setSchluessel(derSchluessel);
						ergebnis.add(dieFarbe);
					} catch(PrimaerSchluesselException ausnahme) {
						ausnahme.printStackTrace();
					}
				}
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
			return ergebnis;
		} finally {
			logSql.exit();
		}
	}

	private PreparedStatementConsumer erstellePreparedStatementConsumer(Farbe farbe) {
		return uebersetzer -> {
			String displayName = farbe.getDisplayName();
			if(displayName == null || displayName.isBlank()) {
				displayName = null;
			} else {
				displayName.trim();
			}
			double rot = farbe.getRot();
			double gruen = farbe.getGruen();
			double blau = farbe.getBlau();
			int farbwert = farbe.getFarbwert();
			
			if(displayName != null) {
				uebersetzer.setString(1, displayName);
			} else {
				uebersetzer.setNull(7, Types.VARCHAR);
			}
			uebersetzer.setDouble(2,  rot);
			uebersetzer.setDouble(3,  gruen);
			uebersetzer.setDouble(4,  blau);
			uebersetzer.setInt(5, farbwert);
			
			Long schluessel = farbe.getSchluessel();
			if(schluessel != null) {
				uebersetzer.setLong(6, schluessel);
			}
		};
	}

	@Override
	public void hinzufuegenFarbe(Farbe farbe) {
		logSql.enter();
		try {
			if(farbe.getSchluessel() != null) {
				throw new PrimaerSchluesselException("die Farbe ist schon in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			hinzufuegenElement(
					"""
							INSERT INTO Farben (
								displayName,
								rot,
								gruen,
								blau,
								farbwert
							) VALUES (?, ?, ?, ?, ?)
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(farbe),
					farbe::setSchluessel
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void updateFarbe(Farbe farbe) {
		logSql.enter();
		try {
			if(farbe.getSchluessel() == null) {
				throw new PrimaerSchluesselException("die Farbe ist noch nicht in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			updateElement(
					"""
							UPDATE Farben SET
								displayName = ?,
								rot = ?,
								gruen = ?,
								blau = ?,
								farbwert = ?
							WHERE schluessel = ?
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(farbe)
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void loeschenFarbe(long schluessel) {
		loeschenBySchluessel("Farben", schluessel);
	}

	@Override
	public void loeschenFarbe(Farbe zuLoeschen) {
		if(zuLoeschen.getSchluessel() == null) {
			throw new PrimaerSchluesselException("Das Element ist nicht in der Datenbank.");
		}
		loeschenBySchluessel("Farben", zuLoeschen.getSchluessel());
	}

	@Override
	public Set<Long> getFigurenSchluessel() {
		return getAlleSchluessel("Figuren");
	}
	
	@Override
	public Set<Figur> getFigurenMenge() {
		return getFigurenMenge(null, null, null);
	}

	@Override
	public Set<Figur> getFigurenMengeBySpielerSchluessel(long spielerSchluessel) {
		return getFigurenMenge(null, spielerSchluessel, null);
	}

	@Override
	public Set<Figur> getFigurenMengeBySpielZustandSchluessel(long spielZustandSchluessel) {
		return getFigurenMenge(null, null, spielZustandSchluessel);
	}

	@Override
	public Figur getFigur(long schluessel) {
		return getFigurenMenge(schluessel, null, null).stream().findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("die Figur war nicht in der Datenbank"));
	}

	private Set<Figur> getFigurenMenge(
			Long schluessel,
			Long spielerSchluessel,
			Long spielZustandSchluessel
	) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			HashSet<Figur> ergebnis = new HashSet<>();
	
			Long value = null;
			String sqlQuery = "SELECT * FROM Figuren";
			if(schluessel != null) {
				sqlQuery = "SELECT * FROM Figuren WHERE schluessel = ?";
				value = schluessel;
			} else if(spielerSchluessel != null) {
				sqlQuery = "SELECT * FROM Figuren WHERE fkSpieler = ?";
				value = spielerSchluessel;
			} else if(spielZustandSchluessel != null) {
				sqlQuery = "SELECT * FROM Figuren WHERE fkSpielZustand = ?";
				value = spielZustandSchluessel;
			}
			
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery)
			) {
				if(value != null) {
					uebersetzer.setLong(1, value);
				}
				logSql.logSql(sqlQuery);
				uebersetzer.execute();
				
				ResultSet antwort = uebersetzer.getResultSet();
				
				while(antwort.next()) {
					try {
						Long derSchluessel = antwort.getLong("schluessel");
		
						Long fkSpielZustand = antwort.getLong("fkSpielZustand");
						Long fkSpieler = antwort.getLong("fkSpieler");
						Long fkFarbe = antwort.getLong("fkFarbe");
						Long fkSpielFeld = antwort.getLong("fkSpielFeld");
		
						Farbe farbe = getFarbe(fkFarbe);
						SpielFeld spielFeld = getSpielFeld(fkSpielFeld);
		
						Figur dieFigur = Figur.erstelleFigur(farbe, spielFeld);
						dieFigur.setSchluessel(derSchluessel);
						dieFigur.setFremdSchluesselSpielZustand(fkSpielZustand);
						dieFigur.setFremdSchluesselSpieler(fkSpieler);
						ergebnis.add(dieFigur);
					} catch(PrimaerSchluesselException ausnahme) {
						ausnahme.printStackTrace();
					}
				}
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
			return ergebnis;
		} finally {
			logSql.exit();
		}
	}

	private PreparedStatementConsumer erstellePreparedStatementConsumer(Figur figur) {
		return uebersetzer -> {
			Long fkSpielZustand = figur.getFremdSchluesselSpielZustand();
			Long fkSpieler = figur.getFremdSchluesselSpieler();
			long fkFarbe = figur.getFarbe().getSchluessel();
			long fkSpielFeld = figur.getSpielFeld().getSchluessel();
			
			if(fkSpielZustand != null) {
				uebersetzer.setLong(1, fkSpielZustand);
			} else {
				uebersetzer.setNull(1, backendProvider.getSqlPrimaryKeyType());
			}
			
			if(fkSpieler != null) {
				uebersetzer.setLong(2, fkSpieler);
			} else {
				uebersetzer.setNull(2, backendProvider.getSqlPrimaryKeyType());
			}

			uebersetzer.setLong(3, fkFarbe);
			uebersetzer.setLong(4, fkSpielFeld);
			
			Long schluessel = figur.getSchluessel();
			if(schluessel != null) {
				uebersetzer.setLong(5, schluessel);
			}
		};
	}

	@Override
	public void hinzufuegenFigur(Figur figur) {
		logSql.enter();
		try {
			if(figur.getSchluessel() != null) {
				throw new PrimaerSchluesselException("die Farbe ist schon in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Farbe farbe = figur.getFarbe();
			if(farbe.getSchluessel() == null) {
				hinzufuegenFarbe(farbe);
			} else {
				updateFarbe(farbe);
			}
	
			SpielFeld spielFeld = figur.getSpielFeld();
			if(spielFeld.getSchluessel() == null) {
				hinzufuegenSpielFeld(spielFeld);
			} else {
				updateSpielFeld(spielFeld);
			}
			
			hinzufuegenElement(
					"""
							INSERT INTO Figuren (
								fkSpielZustand,
								fkSpieler,
								fkFarbe,
								fkSpielFeld
							) VALUES (?, ?, ?, ?)
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(figur),
					figur::setSchluessel
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void updateFigur(Figur figur) {
		logSql.enter();
		try {
			if(figur.getSchluessel() == null) {
				throw new PrimaerSchluesselException("die Farbe ist noch nicht in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Farbe farbe = figur.getFarbe();
			if(farbe.getSchluessel() == null) {
				hinzufuegenFarbe(farbe);
			} else {
				updateFarbe(farbe);
			}
	
			SpielFeld spielFeld = figur.getSpielFeld();
			if(spielFeld.getSchluessel() == null) {
				hinzufuegenSpielFeld(spielFeld);
			} else {
				updateSpielFeld(spielFeld);
			}
			
			updateElement(
					"""
							UPDATE Figuren SET
								fkSpielZustand = ?,
								fkSpieler = ?,
								fkFarbe = ?,
								fkSpielFeld = ?
							WHERE schluessel = ?
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(figur)
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void loeschenFigur(long schluessel) {
		loeschenBySchluessel("Figuren", schluessel);
	}

	@Override
	public void loeschenFigur(Figur zuLoeschen) {
		if(zuLoeschen.getSchluessel() == null) {
			throw new PrimaerSchluesselException("Das Element ist nicht in der Datenbank.");
		}
		loeschenBySchluessel("Figuren", zuLoeschen.getSchluessel());
	}

	@Override
	public Set<Long> getSpielFelderSchluessel() {
		return getAlleSchluessel("SpielFelder");
	}

	@Override
	public Set<SpielFeld> getSpielFelderMenge() {
		return getSpielFelderMenge(null);
	}

	@Override
	public SpielFeld getSpielFeld(long schluessel) {
		return getSpielFelderMenge(schluessel).stream().findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("das SpielFeld war nicht in der Datenbank"));
	}


	private Set<SpielFeld> getSpielFelderMenge(Long schluessel) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			HashSet<SpielFeld> ergebnis = new HashSet<>();
	
			String sqlQuery = "SELECT * FROM SpielFelder";
			if(schluessel != null) {
				sqlQuery = "SELECT * FROM SpielFelder WHERE schluessel = ?";
			}
			
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery)
			) {
				if(schluessel != null) {
					uebersetzer.setLong(1, schluessel);
				}
	
				logSql.logSql(sqlQuery);
				uebersetzer.execute();
				
				ResultSet antwort = uebersetzer.getResultSet();
				
				while(antwort.next()) {
					try {
						Long derSchluessel = antwort.getLong("schluessel");
						Long fkFarbe = antwort.getLong("fkFarbe");
						if(antwort.wasNull()) {
							fkFarbe = null;
						}
						String textFeldArt = antwort.getString("feldArt");
						
						Farbe farbe = null;
						if(fkFarbe != null) {
							farbe = getFarbe(fkFarbe);
						}
						
						FeldArt feldArt = FeldArt.valueOf(textFeldArt.toUpperCase(Locale.ENGLISH));
						int feldPosition = antwort.getInt("feldPosition");
		
						SpielFeld dasSpielFeld = new SpielFeld(farbe, feldArt, feldPosition);
						dasSpielFeld.setSchluessel(derSchluessel);
						ergebnis.add(dasSpielFeld);
					} catch(PrimaerSchluesselException ausnahme) {
						ausnahme.printStackTrace();
					}
				}
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
			return ergebnis;
		} finally {
			logSql.exit();
		}
	}

	private PreparedStatementConsumer erstellePreparedStatementConsumer(SpielFeld spielfeld) {
		return uebersetzer -> {
			String textFeldArt = spielfeld.getFeldArt().toString().toUpperCase(Locale.ENGLISH);
			int feldPosition = spielfeld.getFeldPosition();
			Long fkFarbe = null;
			if(spielfeld.getFeldFarbe() != null) {
				fkFarbe = spielfeld.getFeldFarbe().getSchluessel();
			}
			
			uebersetzer.setString(1, textFeldArt);
			uebersetzer.setInt(2, feldPosition);
			if(fkFarbe != null) {
				uebersetzer.setLong(3, fkFarbe);
			} else {
				uebersetzer.setNull(3, backendProvider.getSqlPrimaryKeyType());
			}
			
			Long schluessel = spielfeld.getSchluessel();
			if(schluessel != null) {
				uebersetzer.setLong(4, schluessel);
			}
		};
	}

	@Override
	public void hinzufuegenSpielFeld(SpielFeld spielFeld) {
		logSql.enter();
		try {
			if(spielFeld.getSchluessel() != null) {
				throw new PrimaerSchluesselException("das SpielFeld ist schon in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Farbe farbe = spielFeld.getFeldFarbe();
			if(farbe != null) {
				if(farbe.getSchluessel() == null) {
					hinzufuegenFarbe(farbe);
				} else {
					updateFarbe(farbe);
				}
			}
			
			hinzufuegenElement(
					"""
							INSERT INTO SpielFelder (
								feldArt,
								feldPosition,
								fkFarbe
							) VALUES (?, ?, ?)
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(spielFeld),
					spielFeld::setSchluessel
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void updateSpielFeld(SpielFeld spielFeld) {
		logSql.enter();
		try {
			if(spielFeld.getSchluessel() == null) {
				throw new PrimaerSchluesselException("das Spielfeld ist noch nicht in einer Datenbank");
			}
			erstellenAlleTabellen(true);
			
			Farbe farbe = spielFeld.getFeldFarbe();
			if(farbe != null) {
				if(farbe.getSchluessel() == null) {
					hinzufuegenFarbe(farbe);
				} else {
					updateFarbe(farbe);
				}
			}
			
			updateElement(
					"""
							UPDATE SpielFelder SET
								feldArt = ?,
								feldPosition = ?,
								fkFarbe = ?
							WHERE schluessel = ?
					""".replaceAll("\\s+", " "),
					erstellePreparedStatementConsumer(spielFeld)
			);
		} finally {
			logSql.exit();
		}
	}

	@Override
	public void loeschenSpielFeld(long schluessel) {
		loeschenBySchluessel("SpielFelder", schluessel);
	}

	@Override
	public void loeschenSpielFeld(SpielFeld zuLoeschen) {
		if(zuLoeschen.getSchluessel() == null) {
			throw new PrimaerSchluesselException("Das Element ist nicht in der Datenbank.");
		}
		loeschenBySchluessel("SpielFelder", zuLoeschen.getSchluessel());
	}

	private void hinzufuegenElement(
			String sqlQuery,
			PreparedStatementConsumer fillPreparedStatement,
			Consumer<Long> notifySchluessel
	) {
		logSql.logSql(sqlQuery);
		try (
				Connection verbindung = DriverManager.getConnection(url, user, passwort);
				PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS)
		) {
			fillPreparedStatement.accept(uebersetzer);
			uebersetzer.execute();

			if(uebersetzer.getUpdateCount() != 1) {
				throw new PrimaerSchluesselException("Das Element wurde nicht hinzugefuegt.");
			}

			ResultSet antwortImUebersetzer = uebersetzer.getGeneratedKeys();
			antwortImUebersetzer.next();
			notifySchluessel.accept(antwortImUebersetzer.getLong(1));
		} catch(SQLException ausnahme) {
			ausnahme.printStackTrace();
		}
	}
	
	private HashSet<Long> getAlleSchluessel(String tabelle) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			HashSet<Long> schluesselListe = new HashSet<>();
			
			String sqlQuery = "SELECT schluessel FROM " + tabelle;
			logSql.logSql(sqlQuery);
			try (
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					Statement uebersetzer = verbindung.createStatement();
					ResultSet ergebnis = uebersetzer.executeQuery(sqlQuery);
			) {
				while(ergebnis.next()) {
					schluesselListe.add(ergebnis.getLong(1));
				}
			} catch(SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
			return schluesselListe;
		} finally {
			logSql.exit();
		}
	}
	
	private void updateElement(
			String sqlQuery,
			PreparedStatementConsumer fillPreparedStatement
	) {
		try (
				Connection verbindung = DriverManager.getConnection(url, user, passwort);
				PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS)
		) {
			fillPreparedStatement.accept(uebersetzer);
			logSql.logSql(sqlQuery);
			uebersetzer.execute();

			if(uebersetzer.getUpdateCount() != 1) {
				throw new PrimaerSchluesselException("Das Element wurde nicht hinzugefuegt.");
			}
		} catch(SQLException ausnahme) {
			ausnahme.printStackTrace();
		}
	}
	
	private void loeschenBySchluessel(String tabelle, long schluessel) {
		logSql.enter();
		try {
			erstellenAlleTabellen(true);
			
			String sqlQuery = "DELETE FROM " + tabelle + " WHERE schluessel = ?";
			try(
					Connection verbindung = DriverManager.getConnection(url, user, passwort);
					PreparedStatement uebersetzer = verbindung.prepareStatement(sqlQuery);
			) {
				uebersetzer.setLong(1, schluessel);
				logSql.logSql(sqlQuery);
				uebersetzer.execute();
				
				if(uebersetzer.getUpdateCount() != 1) {
					throw new PrimaerSchluesselException("Das Element war nicht in der Datenbank.");
				}
			} catch (SQLException ausnahme) {
				ausnahme.printStackTrace();
			}
		} finally {
			logSql.exit();
		}
	}
	
	interface PreparedStatementConsumer {
		void accept(PreparedStatement uebersetzer) throws SQLException;
	}
	
	public interface LogSql {
		/**
		 * <pre>
		 * 
		 * 
		 * </pre>
		 */
		default void enter() {}

		/**
		 * <pre>
		 * 
		 * @param sql
		 * 
		 * </pre>
		 */
		default void logSql(String sql) {}

		/**
		 * <pre>
		 * 
		 * 
		 * </pre>
		 */
		default void exit() {}
		
	}
	
	public static class NoLogSql implements LogSql {}
	
	public static class DoLogSql implements LogSql {
		private int depth = 0;

		private final PrintStream output;

		/**
		 * <pre>
		 * 
		 * Log to some PrintStream
		 * @param output
		 * 
		 * </pre>
		 */
		public DoLogSql() {
			this.output = System.out;
		}
		
		/**
		 * <pre>
		 * 
		 * Log to some PrintStream
		 * @param output
		 * 
		 * </pre>
		 */
		private DoLogSql(PrintStream output) {
			this.output = output;
		}
		
		public static DoLogSql erstelleDoLogSql(PrintStream output) {
			if(output == null) {
				throw new NullPointerException();
			}
			return new DoLogSql(output);
		}
		
		@Override
		public void enter() {
			depth = depth+1;
			if(depth == 1) {
				output.println("*".repeat(60));
				output.println("***** Start SQL executions");
				output.println("*".repeat(60) + "\n");
			}
			
		}

		@Override
		public void exit() {
			depth = depth-1;
			if(depth == 0) {
				output.println("\n" + "*".repeat(60));
				output.println("***** Ende SQL executions");
				output.println("*".repeat(60));
			}
		}
		
		@Override
		public void logSql(String sql) {
			if(depth > 1) {
				output.println("\n" + "*".repeat(60));
			}
			
			System.out.println("- EXECUTE SQL query:");
			sql.lines().forEach(line -> output.println("\t" + line));
		}
	}

	private enum DatabaseBackendProvider {
		MYSQL(
				Types.BIGINT,
				"BIGINT",
				"AUTO_INCREMENT",
				(String columnName, String textEnumValues) -> "ENUM(" + textEnumValues + ")"
		),
		SQLITE(
				Types.INTEGER, 
				"INTEGER",
				"AUTOINCREMENT",
				(String columnName, String textEnumValues) -> "VARCHAR(20) CHECK(" + columnName + " IN (" + textEnumValues + "))"
		);
		
		private final int sqlPrimaryKeyType;
		
		private final String sqlPrimaryKeyTypeName;
		
		private final String sqlAutoIncrement;
		
		private final BiFunction<String, String, String> enumPatternLambda;
		
		private DatabaseBackendProvider(
				int sqlPrimaryKeyType,
				String sqlPrimaryKeyTypeName,
				String sqlAutoIncrement,
				BiFunction<String, String, String> enumPatternLambda
		) {
			this.sqlPrimaryKeyType = sqlPrimaryKeyType;
			this.sqlPrimaryKeyTypeName = sqlPrimaryKeyTypeName;
			this.sqlAutoIncrement = sqlAutoIncrement;
			this.enumPatternLambda = enumPatternLambda;
		}
		
		/**
		 * <pre>
		 * 
		 * Gibt den genutzten Datentyp für Primärschlüssel in der verwendeten SQL Datenbank an.
		 * Die Datenbank unterstützt für diesen Datentyp AUTO_INCREMENT.
		 * @return
		 * 
		 * </pre>
		 */
		public int getSqlPrimaryKeyType() {
			return sqlPrimaryKeyType;
		}

		/**
		 * <pre>
		 * 
		 * Gibt den genutzten Datentyp-Namen für Primärschlüssel in der verwendeten SQL Datenbank an.
		 * Die Datenbank unterstützt für diesen Datentyp AUTO_INCREMENT.
		 * @return
		 * 
		 * </pre>
		 */
		public String getSqlPrimaryKeyTypeName() {
			return sqlPrimaryKeyTypeName;
		}
		
		/**
		 * <pre>
		 * 
		 * Gibt den Datenbank spezifischen Namen für die 'Auto-Increment' Eigenschaft an.
		 * @return the sqlAutoIncrement
		 * 
		 * </pre>
		 */
		public String getSqlAutoIncrement() {
			return sqlAutoIncrement;
		}
		
		public String enumPattern(String columnName, String... enumValues) {
			String textEnumValues = Arrays.asList(enumValues).stream().collect(
					Collectors.joining("', '", "'", "'")
			);
			return enumPatternLambda.apply(columnName, textEnumValues);
		}
	}
}
