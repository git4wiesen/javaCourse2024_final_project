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

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.InitialeFigurPlatzierung;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitSqlDatenbank;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitSqlDatenbank.DoLogSql;

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
public class FuellerDatenInSqliteDatenbankTest {

	/**
	 * <pre>
	 * 
	 * @param args
	 * 
	 * </pre>
	 */
	public static void main(String[] args) {
		System.out.println("\n" + "*".repeat(60) + "\n");
		System.out.println("***** Test Anfang: Fuelle SqLite Datenbank mit Beispieldaten");
		System.out.println("\n" + "*".repeat(60) + "\n");

		File database = new File(new File("database"), "test_datenbank.sqlite");
		database.delete();
		
		System.out.println("SqLite Datenbank: " + database.getAbsolutePath());
		
		RundenLaufMitHerausSchlagenMaker beispielDatenGenerator = new RundenLaufMitHerausSchlagenMaker(
				false,
				InitialeFigurPlatzierung.ZUFAELLIG
		);
		
		RundenLaufMitHerausSchlagenDaoMitSqlDatenbank backendZugriff = RundenLaufMitHerausSchlagenDaoMitSqlDatenbank.erzeugeMenschAergerDichNichtDaoMitFileSqliteDatenbank("test_datenbank");
		//backendZugriff.setLogSql(DoLogSql.erstelleDoLogSql(System.out));
		
		beispielDatenGenerator.getSpielZustaendeMenge().forEach(spielZustand -> backendZugriff.hinzufuegenSpielZustand(spielZustand));
		
		System.out.println("\n" + "*".repeat(60) + "\n");
		System.out.println("***** Test Ende: Fuelle SqLite Datenbank mit Beispieldaten");
		System.out.println("\n" + "*".repeat(60) + "\n");
	}
}




































