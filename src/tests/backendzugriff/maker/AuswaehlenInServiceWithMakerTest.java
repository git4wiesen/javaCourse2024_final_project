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
package tests.backendzugriff.maker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;

/**
 * <pre>
 * 
 * Testest, ob das abrufen von Daten über den RundenLaufMitHerausschlagenService funktioniert.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class AuswaehlenInServiceWithMakerTest {
	/**
	 * <pre>
	 * 
	 * @param args
	 * 
	 * </pre>
	 */
	public static void main(String[] args) {
		Random wuerfel = new Random();
		Collector<CharSequence, ?, String> indentCollector = Collectors.joining("\n\t", "\t", "");

		RundenLaufMitHerausschlagenDao james = new RundenLaufMitHerausSchlagenDaoMitMaker();
		
		Set<SpielZustand> spielZustaendeMenge = new HashSet<>(james.getSpielZustaendeMenge());
		SpielZustand spielZustand = new ArrayList<>(spielZustaendeMenge).get(wuerfel.nextInt(spielZustaendeMenge.size()));
		
		RundenLaufMitHerausschlagenServerService service = new RundenLaufMitHerausschlagenServerService(james, spielZustand);
		
		System.out.println("\n" + "*".repeat(60) + "\n");
		
		System.out.println("Alle Spieler:");
		List<Spieler> alleSpieler = service.getAlleSpieler();
		alleSpieler.forEach(s ->
			System.out.println(s.toDisplayString().lines().collect(indentCollector) + "\n")
		);
		
		System.out.println("\n" + "*".repeat(60) + "\n");
		
		System.out.println("Konkrete Spieler:");
		List<Spieler> konkreteSpieler = service.getKonkreteSpieler();
		konkreteSpieler.forEach(s ->
			System.out.println(s.toDisplayString().lines().collect(indentCollector) + "\n")
		);

		System.out.println("\n" + "*".repeat(60) + "\n");
		
		System.out.println("Alle Figuren:");
		List<Figur> alleFiguren = service.getAlleFiguren();
		alleFiguren.forEach(f ->
			System.out.println(f.toDisplayString().lines().collect(indentCollector) + "\n")
		);

		System.out.println("\n" + "*".repeat(60) + "\n");
		
		System.out.println("Start Strecken:");
		List<SpielFeld> startStreckenFelder = service.getStartStreckenFelder();
		startStreckenFelder.forEach(f ->
			System.out.println(f.toDisplayString().lines().collect(indentCollector) + "\n")
		);

		System.out.println("\n" + "*".repeat(60) + "\n");
		
		System.out.println("End Strecken:");
		List<SpielFeld> endStreckenFelder = service.getEndStreckenFelder();
		endStreckenFelder.forEach(f ->
			System.out.println(f.toDisplayString().lines().collect(indentCollector) + "\n")
		);

		System.out.println("\n" + "*".repeat(60) + "\n");
		
		System.out.println("Aktiver Spieler:");
		
		Spieler aktiverSpieler = service.getAktiverSpieler();
		System.out.println(aktiverSpieler.toDisplayString().lines().collect(indentCollector) + "\n");

		System.out.println("\n" + "*".repeat(60) + "\n");
		
		System.out.println("Aktiver Spieler - Todo Aktion: " + service.getAktiverSpielerTodoAktion());
		System.out.println("Aktiver Spieler - Würfel-Ergebnis: " + service.getWuerfelErgebnis());
		System.out.println("Aktiver Spieler - Anzahl der Würfel-Versuche: " + service.getWuerfelVersuchsAnzahl());
	}

}
