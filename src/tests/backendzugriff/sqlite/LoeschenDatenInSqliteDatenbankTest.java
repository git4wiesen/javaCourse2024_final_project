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
package tests.backendzugriff.sqlite;

import java.io.File;
import java.util.Set;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitSqlDatenbank;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitSqlDatenbank.DoLogSql;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;

/**
 * <pre>
 * 
 * Sqlite Database test.
 * 
 * Füllt die Datenbank mit Werten.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
@SuppressWarnings("unused")
public class LoeschenDatenInSqliteDatenbankTest {

	/**
	 * <pre>
	 * 
	 * @param args
	 * 
	 * </pre>
	 */
	public static void main(String[] args) {
		System.out.println("\n" + "*".repeat(60) + "\n");
		System.out.println("***** Test Anfang: Löschen SqLite Datenbank mit Beispieldaten");
		System.out.println("\n" + "*".repeat(60) + "\n");

		try {
			File database = new File(new File("database"), "test_datenbank.sqlite");
			System.out.println("SqLite Datenbank: " + database.getAbsolutePath());
			if(!database.isFile()) {
				System.out.println("Keine SqLite Datenbank Datei in '" + database + "' gefunden.");
				return;
			}
			
			RundenLaufMitHerausSchlagenDaoMitSqlDatenbank backendZugriff = RundenLaufMitHerausSchlagenDaoMitSqlDatenbank.erzeugeMenschAergerDichNichtDaoMitFileSqliteDatenbank("test_datenbank");
			//backendZugriff.setLogSql(DoLogSql.erstelleDoLogSql(System.out));
			
			System.out.println("\n" + "*".repeat(60) + "\n");
	
			{
				Set<SpielZustand> zustandsMenge = backendZugriff.getSpielZustaendeMenge();
				if(zustandsMenge.isEmpty()) {
					System.out.println("SpielZustände-Menge ist leer -> Hier ist nichts zu löschen.");
				} else {
					zustandsMenge.forEach((SpielZustand deleteSpielZustand) -> {
						backendZugriff.loeschenSpielZustand(deleteSpielZustand);
						try {
							backendZugriff.getSpielZustand(deleteSpielZustand.getSchluessel());
							throw new AssertionError("ein SpielZustand sollte nach dem Löschen nicht mehr in der Datenbank sein!");
						} catch(PrimaerSchluesselException erwarteteAusnahme) {
							System.out.println("PrimaerSchluesselException gefangen -> SpielZustand löschen war erfolgreich");
						}
					});
				}
			}
	
			System.out.println("\n" + "*".repeat(60) + "\n");

			{
				Set<AktiverSpielerDekorator> aktiveSpielerMenge = backendZugriff.getAktiveSpielerMenge();
				if(aktiveSpielerMenge.isEmpty()) {
					System.out.println("Aktive-Spieler-Menge ist leer -> Hier ist nichts zu löschen.");
				} else {
					aktiveSpielerMenge.forEach((AktiverSpielerDekorator deleteAktiverSpieler) -> {
						backendZugriff.loeschenAktiverSpieler(deleteAktiverSpieler);
						try {
							backendZugriff.getAktiverSpieler(deleteAktiverSpieler.getSchluessel());
							throw new AssertionError("ein aktiver Spieler sollte nach dem Löschen nicht mehr in der Datenbank sein!");
						} catch(PrimaerSchluesselException erwarteteAusnahme) {
							System.out.println("PrimaerSchluesselException gefangen -> aktiver Spieler löschen war erfolgreich");
						}
					});
				}
			}

			System.out.println("\n" + "*".repeat(60) + "\n");

			{
				Set<Spieler> spielerMenge = backendZugriff.getSpielerMenge();
				if(spielerMenge.isEmpty()) {
					System.out.println("Spieler Menge ist leer -> Hier ist nichts zu löschen.");
				} else {
					spielerMenge.forEach((Spieler deleteSpieler) -> {
						backendZugriff.loeschenSpieler(deleteSpieler);
						try {
							backendZugriff.getSpieler(deleteSpieler.getSchluessel());
							throw new AssertionError("ein Spieler sollte nach dem Löschen nicht mehr in der Datenbank sein!");
						} catch(PrimaerSchluesselException erwarteteAusnahme) {
							System.out.println("PrimaerSchluesselException gefangen -> Spieler löschen war erfolgreich");
						}
					});
				}
			}

			System.out.println("\n" + "*".repeat(60) + "\n");
			
			{
				Set<SpielFeld> spielFelderMenge = backendZugriff.getSpielFelderMenge();
				if(spielFelderMenge.isEmpty()) {
					System.out.println("SpielFelder-Menge ist leer -> Hier ist nichts zu löschen.");
				} else {
					spielFelderMenge.forEach((SpielFeld deleteSpielFeld) -> {
						backendZugriff.loeschenSpielFeld(deleteSpielFeld);
						try {
							backendZugriff.getSpielFeld(deleteSpielFeld.getSchluessel());
							throw new AssertionError("eine SpielFeld sollte nach dem Löschen nicht mehr in der Datenbank sein!");
						} catch(PrimaerSchluesselException erwarteteAusnahme) {
							System.out.println("PrimaerSchluesselException gefangen -> SpielFeld löschen war erfolgreich");
						}
					});
				}
			}

			System.out.println("\n" + "*".repeat(60) + "\n");

			{
				Set<Figur> figurenMenge = backendZugriff.getFigurenMenge();
				if(figurenMenge.isEmpty()) {
					System.out.println("Figuren-Menge ist leer -> Hier ist nichts zu löschen.");
				} else {
					figurenMenge.forEach((Figur deleteFigur) -> {
						backendZugriff.loeschenFigur(deleteFigur);
						try {
							backendZugriff.getFigur(deleteFigur.getSchluessel());
							throw new AssertionError("eine Figur sollte nach dem Löschen nicht mehr in der Datenbank sein!");
						} catch(PrimaerSchluesselException erwarteteAusnahme) {
							System.out.println("PrimaerSchluesselException gefangen -> Figur löschen war erfolgreich");
						}
					});
				}
			}
			
			System.out.println("\n" + "*".repeat(60) + "\n");
			
			{
				Set<Farbe> farbenMenge = backendZugriff.getFarbenMenge();
				if(farbenMenge.isEmpty()) {
					System.out.println("Farben-Menge ist leer -> Hier ist nichts zu löschen.");
				} else {
					farbenMenge.forEach((Farbe deleteFarbe) -> {
						backendZugriff.loeschenFarbe(deleteFarbe);
						try {
							backendZugriff.getFarbe(deleteFarbe.getSchluessel());
							throw new AssertionError("eine Farbe sollte nach dem Löschen nicht mehr in der Datenbank sein!");
						} catch(PrimaerSchluesselException erwarteteAusnahme) {
							System.out.println("PrimaerSchluesselException gefangen -> Farbe löschen war erfolgreich");
						}
					});
				}
			}
	
			System.out.println("\n" + "*".repeat(60) + "\n");
		} finally {
			System.out.println("\n" + "*".repeat(60) + "\n");
			System.out.println("***** Test Ende: Löschen SqLite Datenbank mit Beispieldaten");
			System.out.println("\n" + "*".repeat(60) + "\n");
		}
	}
}
