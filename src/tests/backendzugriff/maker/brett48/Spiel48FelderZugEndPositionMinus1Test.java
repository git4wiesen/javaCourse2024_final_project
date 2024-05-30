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
package tests.backendzugriff.maker.brett48;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.InitialeFigurPlatzierung;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZug;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld.FeldArt;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;
import javafx.scene.paint.Color;

/**
 * <pre>
 * 
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class Spiel48FelderZugEndPositionMinus1Test {
	public static void main(String[] argv) {
		System.out.println("*".repeat(60));
		System.out.println("***** Test Anfang: 48 Spielfelder - Teste Zuege auf Endpositionen minus 1");
		System.out.println("*".repeat(60) + "\n");

		System.out.println("Erstelle ein Spiel mit 48 Streckenfeldern und 6 Teilnehmern");
		RundenLaufMitHerausschlagenDao james = new RundenLaufMitHerausSchlagenDaoMitMaker(
				new RundenLaufMitHerausSchlagenMaker(
						true,
						InitialeFigurPlatzierung.START_FELDER,
						6,
						6,
						8
				)
		);
		SpielZustand spielZustand = james.getSpielZustaendeMenge().stream().findFirst().get();
		
		RundenLaufMitHerausschlagenServerService service = new RundenLaufMitHerausschlagenServerService(james, spielZustand);
		
		Farbe black = Farbe.from(Color.BLACK, "black");
		
		int minusOffset = 1;
		Map<WuerfelErgebnis, SpielFeld> erwartungen = new EnumMap<>(Wuerfel.WuerfelErgebnis.class);
		for(Spieler spieler : service.getAlleSpieler()) {
			erwartungen.put(
					WuerfelErgebnis.EINS,
					SpielFeld.erstelleFarblosesStreckenFeld(
							spieler.getEndStreckenFeld().getFeldPosition()
					)
			);
			erwartungen.put(WuerfelErgebnis.ZWEI, SpielFeld.erstelleZielFeld(black, 0));
			erwartungen.put(WuerfelErgebnis.DREI, SpielFeld.erstelleZielFeld(black, 1));
			erwartungen.put(WuerfelErgebnis.VIER, SpielFeld.erstelleZielFeld(black, 2));
			erwartungen.put(WuerfelErgebnis.FUENF, SpielFeld.erstelleZielFeld(black, 3));
			erwartungen.put(WuerfelErgebnis.SECHS, null);

			for(Map.Entry<WuerfelErgebnis, SpielFeld> entry : erwartungen.entrySet()) {
				WuerfelErgebnis wuerfelErgebnis = entry.getKey();
				SpielFeld entryValue = entry.getValue();
				
				SpielFeld erwartungSpielfeld = null;
				if(entryValue != null) {
					if(entryValue.getFeldArt() == FeldArt.ZIEL_FELD) {
						erwartungSpielfeld = SpielFeld.erstelleZielFeld(spieler.getFarbe(), entryValue.getFeldPosition());
					} else {
						erwartungSpielfeld = entryValue;
					}
				}

				List<Figur> figurenListe = new ArrayList<>(spieler.getFigurenMenge());
				for(int i=1; i<4; i++) {
					figurenListe.get(i).setSpielFeld(
							SpielFeld.erstelleFarblosesStreckenFeld(
									(spieler.getStartStreckenFeld().getFeldPosition() + 20) % spielZustand.getAnzahlStreckenFelder()
							)
					);
				}
				
				int figurPosition = spieler.getEndStreckenFeld().getFeldPosition() - minusOffset;
				figurenListe.get(0).setSpielFeld(
						SpielFeld.erstelleFarblosesStreckenFeld(figurPosition)
				);
				List<SpielZug> zuege = service.bestimmeMoeglicheSpielZuege(spieler, wuerfelErgebnis);
				SpielZug zug = zuege.stream().filter(zug1 -> zug1.getZugFeld().getFeldPosition() == figurPosition).findFirst().orElse(null);
		
				if(
						!Objects.equals(erwartungSpielfeld==null, zug==null)
						|| (zug!=null  && !Objects.equals(erwartungSpielfeld.getFeldArt(), zug.getZielFeld().getFeldArt()))
						|| (zug!=null  && !Objects.equals(erwartungSpielfeld.getFeldPosition(), zug.getZielFeld().getFeldPosition()))
				) {
					throw new AssertionError();
				}
			}
		}
		
		System.out.println("Alle Züge auf den Endpositionen minus 1 resultierten in die erwarteten Ziel-Feldern.");
		
		System.out.println("\n" + "*".repeat(60));
		System.out.println("***** Test Ende: 48 Spielfelder - Teste Zuege auf Endpositionen minus 1");
		System.out.println("*".repeat(60));
	}
}
