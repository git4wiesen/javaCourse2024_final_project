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
import java.util.Random;
import java.util.Set;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitSqlDatenbank;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator.AktiverSpielerTodoAktion;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;

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
public class UpdateDatenInSqliteDatenbankTest {

	/**
	 * <pre>
	 * 
	 * @param args
	 * 
	 * </pre>
	 */
	public static void main(String[] args) {
		System.out.println("\n" + "*".repeat(60) + "\n");
		System.out.println("***** Test Anfang: Update SqLite Datenbank");
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
	
			Set<SpielZustand> spielZustaendeMenge = backendZugriff.getSpielZustaendeMenge();
			
			if(spielZustaendeMenge.isEmpty()) {
				System.out.println("Die Datenbank ist leer.");
				return;
			}
			
			Random wuerfel = new Random();
			spielZustaendeMenge.forEach(alterSpielZustand -> {
				
				KonkreterSpieler neuerSpieler = null;
				
				AktiverSpielerTodoAktion[] todoAktionenArray = AktiverSpielerTodoAktion.values();
				AktiverSpielerTodoAktion todoAktion = todoAktionenArray[wuerfel.nextInt(todoAktionenArray.length)];
				WuerfelErgebnis[] wuerfelErgebnisArray = WuerfelErgebnis.values();
				WuerfelErgebnis wuerfelErgebnis = wuerfelErgebnisArray[wuerfel.nextInt(wuerfelErgebnisArray.length)];
				AktiverSpielerDekorator neuerAktiverSpieler = AktiverSpielerDekorator.erstelleAktivenSpieler(
						neuerSpieler,
						todoAktion,
						todoAktion != AktiverSpielerTodoAktion.MUSS_WUERFELN && wuerfel.nextBoolean() ? wuerfelErgebnis : null,
						wuerfel.nextInt(10),
						wuerfel.nextBoolean()
				);
				
				// TODO fertig Schreiben
				SpielZustand neuerZustand = new SpielZustand(
						neuerAktiverSpieler,
						null,
						null,
						0,
						0,
						0
				);
				
//				int oldPreis = tier.getPreis();
//				LocalDate oldVerfuegbarAb = tier.getVerfuegbarAb();
//				
//				int newPreis = oldPreis;
//				LocalDate newVerfuegbarAb = oldVerfuegbarAb;
//				while(oldPreis == newPreis && oldVerfuegbarAb == newVerfuegbarAb) {
//					newPreis = wuerfel.nextInt(1000_00);
//					newVerfuegbarAb = LocalDate.ofEpochDay(wuerfel.nextLong(-36500, 36500)); 
//				}
//				
//				Amigurumi newTier = new Amigurumi(
//						tier.getName(),
//						tier.getArt(),
//						tier.isKleinkindGeeignet(),
//						newPreis,
//						newVerfuegbarAb
//				);
//				newTier.setSchluessel(tier.getSchluessel());
//
//				System.out.println("Update Tier " + tier);
//				System.out.println("\t-> mit neuem Tier " + newTier);
//				
//				datenbankzugriff.updateAmigurumi(newTier);
//				
//				List<Amigurumi> neueListe = datenbankzugriff.getAllAmigurumis();
//				if(neueListe.contains(tier) || !neueListe.contains(newTier)) {
//					throw new AssertionError("Element wurde nicht geupdatet");
//				}
				
				System.out.println("\t-> Update war erfolgreich");
			});
			
	
			System.out.println("\n" + "*".repeat(60) + "\n");
		} finally {
			System.out.println("\n" + "*".repeat(60) + "\n");
			System.out.println("***** Test Anfang: Update SqLite Datenbank");
			System.out.println("\n" + "*".repeat(60) + "\n");
		}
	}
}
