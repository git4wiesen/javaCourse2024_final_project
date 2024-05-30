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
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.InitialeFigurPlatzierung;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;

/**
 * <pre>
 * 
 * Testet ob richtig erkannt wird ob ein Spiel fertig ist.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class SpielFertigTest {
	public static void main(String[] argv) {
		System.out.println("*".repeat(60));
		System.out.println("***** Test Anfang: Teste 'Spiel ist fertig' Logik");
		System.out.println("*".repeat(60) + "\n");

		System.out.println("Erstelle zufälliges Spiel, wo alle Figuren auf die Zielfelder gesetzt werden");
		Random wuerfel = new Random();
		RundenLaufMitHerausschlagenDao james = new RundenLaufMitHerausSchlagenDaoMitMaker(
				new RundenLaufMitHerausSchlagenMaker(
						true, 
						InitialeFigurPlatzierung.ZIEL_FELDER,
						2,
						4,
						10
				)
		);
		Set<SpielZustand> spielZustaendeMenge = new HashSet<>(james.getSpielZustaendeMenge());
		SpielZustand spielZustand = new ArrayList<>(spielZustaendeMenge).get(wuerfel.nextInt(spielZustaendeMenge.size()));
		
		RundenLaufMitHerausschlagenServerService service = new RundenLaufMitHerausschlagenServerService(james, spielZustand);
		
		if(!service.isSpielFertig()) {
			throw new AssertionError("Das Spiel sollte fertig sein, wenn alle Figuren auf den Zielfeldern stehen...");
		}
		System.out.println("Das Spiel ist richtiger Weise fertig.");
		
		System.out.println("\n" + "*".repeat(60) + "\n");
		
		List<Spieler> konkreteSpielerListe = spielZustand.getSpielerMenge().stream().filter(f -> f instanceof KonkreterSpieler).collect(Collectors.toList());
		Spieler spieler0 = konkreteSpielerListe.get(0);
		Spieler spieler1 = konkreteSpielerListe.get(1);
		
		System.out.println("Setze eine Figur von Spieler 0 auf ein anderes nicht Zielfeld");
		SpielFeld spielFeld = switch(wuerfel.nextInt(4)) {
		case 0 -> SpielFeld.erstelleStartFeld(spieler0.getFarbe(), wuerfel.nextInt(4));
		case 1 -> spieler0.getStartStreckenFeld();
		case 3 -> SpielFeld.erstelleFarblosesStreckenFeld(wuerfel.nextInt(0, spielZustand.getAnzahlStreckenFelder()));
		case 2 -> spieler0.getEndStreckenFeld();
		default -> throw new AssertionError();
		};
		new ArrayList<>(spieler0.getFigurenMenge()).get(wuerfel.nextInt(Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER)).setSpielFeld(spielFeld);

		if(!service.isSpielFertig()) {
			throw new AssertionError("Das Spiel sollte fertig sein, wenn alle Figuren außer von einem Spieler auf den Zielfeldern stehen...");
		}
		System.out.println("Das Spiel ist richtiger Weise immer noch fertig.");

		System.out.println("\n" + "*".repeat(60) + "\n");

		System.out.println("Setze eine Figur von Spieler 1 auf ein anderes nicht Zielfeld");
		spielFeld = switch(wuerfel.nextInt(4)) {
		case 0 -> SpielFeld.erstelleStartFeld(spieler1.getFarbe(), wuerfel.nextInt(4));
		case 1 -> spieler1.getStartStreckenFeld();
		case 3 -> SpielFeld.erstelleFarblosesStreckenFeld(wuerfel.nextInt(0, spielZustand.getAnzahlStreckenFelder()));
		case 2 -> spieler1.getEndStreckenFeld();
		default -> throw new AssertionError();
		};
		new ArrayList<>(spieler1.getFigurenMenge()).get(wuerfel.nextInt(Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER)).setSpielFeld(spielFeld);

		if(service.isSpielFertig()) {
			throw new AssertionError("Das Spiel sollte nun nicht mehr fertig sein, da nun 2 Spieler nicht alle Figuren auf den Zielfeldern stehen haben...");
		}
		System.out.println("Das Spiel ist richtiger Weise nicht mehr fertig.");
		
		System.out.println("Der Test war erfolgreich");
		
		System.out.println("\n" + "*".repeat(60));
		System.out.println("***** Test Ende: Teste 'Spiel ist fertig' Logik");
		System.out.println("*".repeat(60) + "\n");
	}
}
