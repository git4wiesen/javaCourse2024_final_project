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
package tests.backend;

import java.util.Random;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.InitialeFigurPlatzierung;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.VakanterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator.AktiverSpielerTodoAktion;

/**
 * <pre>
 * 
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class DaoMitMakerErstelleEinSpielTest {
	public static void main(String[] argv) {
		System.out.println("Test Anfang - erstelle ein SpielZustand\n");
		
		Random wuerfel = new Random();
		
		boolean isKlein = wuerfel.nextBoolean();
		int maxSpielerAnzahl = isKlein ? 4 : 6;
		int spielerAnzahl = wuerfel.nextInt(2, maxSpielerAnzahl + 1);
		int schrittAnzahl = isKlein ? 10 : 8;
		
		RundenLaufMitHerausSchlagenMaker backend = new RundenLaufMitHerausSchlagenMaker(
				true,
				InitialeFigurPlatzierung.START_FELDER,
				spielerAnzahl,
				maxSpielerAnzahl,
				schrittAnzahl
		);
		
		SpielZustand spielZustand = backend.getSpielZustaendeMenge().stream().findFirst().get();
		System.out.println(spielZustand);
		
		System.out.println("\n" + "*".repeat(60) + "\n");

		int erhaltenAlleSpielerCount = spielZustand.getSpielerMenge().size();
		if(erhaltenAlleSpielerCount != maxSpielerAnzahl) {
			throw new AssertionError(
					"die Anzahl der Spieler Plätze ist falsch (erwartet: "
					+ maxSpielerAnzahl
					+ ", erhalten: "
					+ erhaltenAlleSpielerCount
					+ ")"
			);
		}
		System.out.println("Maximale Spielerplatz-Anzahl ist korrekt");

		int erhaltenKonkreteSpielerCount = (int)spielZustand.getSpielerMenge()
				.stream()
				.filter(s -> s instanceof KonkreterSpieler)
				.count();
		if(erhaltenKonkreteSpielerCount != spielerAnzahl) {
			throw new AssertionError(
					"die Anzahl der konkreten Spieler ist falsch (erwartet: "
					+ spielerAnzahl
					+ ", erhalten: "
					+ erhaltenKonkreteSpielerCount
					+ ")"
			);
		}
		System.out.println("Konkrete Spieler-Anzahl ist korrekt");

		int erhaltenVakanteSpielerCount = (int)spielZustand.getSpielerMenge()
				.stream()
				.filter(s -> s instanceof VakanterSpieler)
				.count();
		if(erhaltenVakanteSpielerCount != maxSpielerAnzahl - spielerAnzahl) {
			throw new AssertionError(
					"die Anzahl der vakanten Spieler Plätze ist falsch (erwartet: "
					+ (maxSpielerAnzahl - spielerAnzahl)
					+ ", erhalten: "
					+ erhaltenVakanteSpielerCount
					+ ")"
			);
		}
		System.out.println("Vakante Spieler-Anzahl ist korrekt");
		
		int anzahlFiguren = spielZustand.getFigurenMenge().size();
		if(anzahlFiguren != spielerAnzahl * Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER) {
			throw new AssertionError(
					"die Anzahl der Figuren ist falsch (erwartet: "
					+ (spielerAnzahl * Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER)
					+ ", erhalten: "
					+ anzahlFiguren
					+ ")"
			);
		}
		System.out.println("Die Figur-Anzahl ist korrekt");
		
		if(!AktiverSpielerTodoAktion.MUSS_WUERFELN.equals(spielZustand.getAktiverSpieler().getAktuelleTodoAktion())) {
			throw new AssertionError("der aktive Spieler muss am Anfang gleich Würfeln");
		}
		System.out.println("Aktive Spieler Todo ist korrekt auf MUSS_WUERFELN gesetzt.");

		System.out.println("\nTest Ende - erstelle ein SpielZustand");
	}
}
