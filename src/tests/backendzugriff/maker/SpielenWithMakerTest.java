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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.InitialeFigurPlatzierung;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.SpielParameter;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZug;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator.AktiverSpielerTodoAktion;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;

/**
 * <pre>
 * 
 * Testest ob der Spiel-Verlauf richtig umgesetzt wurde.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class SpielenWithMakerTest {
	/**
	 * <pre>
	 * 
	 * @param args
	 * 
	 * </pre>
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		System.out.println("*".repeat(60));
		System.out.println("***** Test Anfang: Einfach spielen - mit 2 Spielern auf einem 40 Streckenfeldern großen Spielbrett");
		System.out.println("*".repeat(60) + "\n");
		
		Random wuerfel = new Random();
		SpielParameter[] parameterArray = new SpielParameter[10];
		Arrays.setAll(
				parameterArray,
				i -> new SpielParameter(
						InitialeFigurPlatzierung.START_FELDER,
						2,
						4,
						10
				)
		);
		RundenLaufMitHerausschlagenDao james = new RundenLaufMitHerausSchlagenDaoMitMaker(
				new RundenLaufMitHerausSchlagenMaker(true, parameterArray)
		);
		Set<SpielZustand> spielZustaendeMenge = new HashSet<>(james.getSpielZustaendeMenge());
		SpielZustand spielZustand = new ArrayList<>(spielZustaendeMenge).get(wuerfel.nextInt(spielZustaendeMenge.size()));
		
		RundenLaufMitHerausschlagenServerService service = new RundenLaufMitHerausschlagenServerService(james, spielZustand);

		while(!service.isSpielFertig()) {
			Spieler aktiverSpieler = service.getAktiverSpieler();
			AktiverSpielerTodoAktion todoAktion = service.getAktiverSpielerTodoAktion();
			WuerfelErgebnis wuerfelErgebnis = service.getWuerfelErgebnis();
			int wuerfelVersuchsAnzahl = service.getWuerfelVersuchsAnzahl();
			System.out.println("Aktiver Spieler: " + aktiverSpieler.toPrettyString());
			System.out.println("Todo aktion: " + todoAktion);
			System.out.println("Letztes Würfel-Ergebnis: " + wuerfelErgebnis);
			System.out.println("Anzahl Würfel-Versuche: " + wuerfelVersuchsAnzahl);
			
			System.out.println("\nWeiter? Alles, außer '0' als Eingabe.");
			
			String input = new Scanner(System.in).nextLine();
			if("0".equals(input)) {
				break;
			}
			
			switch(todoAktion) {
			case MUSS_WUERFELN -> {
				System.out.println("Initiiere würfeln für den aktuellen Spieler.");
				service.wuerfeln(aktiverSpieler);
			}
			case MUSS_ZIEHEN -> {
				int i = 0;
				List<SpielZug> moeglicheZuge = service.bestimmeMoeglicheSpielZuege(aktiverSpieler, wuerfelErgebnis);
				
				if(!moeglicheZuge.isEmpty()) {
					System.out.println("Bestimme mögliche Züge für den aktuellen Spieler.\n\nMöglicheZüge:");
					for(SpielZug zug : moeglicheZuge) {
						List<String> lines = zug.toDisplayString().lines().collect(Collectors.toList());
						System.out.printf(
								" - %1$d. %2$s\n",
								(i + 1),
								lines.get(0)
						);
						lines.subList(1, lines.size()).forEach(line -> System.out.println("\t" + line));
						i++;
					}
	
					int zugIndex;
					if(moeglicheZuge.size() == 1) {
						zugIndex = 0;
						System.out.println("\nNur ein möglicher Zug. Weiter?");
						new Scanner(System.in).nextLine();
					} else {
						System.out.println("\nWähle eine Figur. Gib die Listenzahl ein (1 bis " + moeglicheZuge.size() + ")");
						zugIndex = new Scanner(System.in).nextInt() - 1;
					}
					if(0 <= zugIndex && zugIndex < moeglicheZuge.size()) {
						SpielZug derSpielZug = moeglicheZuge.get(zugIndex);
						System.out.println("\nFühre SpielZug durch:");
						System.out.println(
								derSpielZug
								.toDisplayString()
								.lines()
								.collect(Collectors.joining(
										"\n\t",
										" - ",
										"\n"
								))
						);
						service.ausfuehrenSpielZug(derSpielZug);
					}
				}
			}
			case FERTIG -> {
				System.out.println("Wechsle zum nächsten aktiven Spieler.");
				service.naechsterAktiverSpieler();
			}
			}
		}

		System.out.println("\n" + "*".repeat(60) + "\n");
		System.out.println("Das Spiel ist nun fertig.\n\nHier sind die Gewinner:");
		
		int i = 1;
		for(Spieler gewinner : service.getGewinnerSpieler()) {
			System.out.println(i + ". " + gewinner.toPrettyString());
		}
		
		System.out.println("\n" + "*".repeat(60) + "\n");

		System.out.println("\n" + "*".repeat(60));
		System.out.println("***** Test Anfang: Einfach spielen - mit 2 Spielern auf einem 40 Streckenfeldern großen Spielbrett");
		System.out.println("*".repeat(60));
	}

}
