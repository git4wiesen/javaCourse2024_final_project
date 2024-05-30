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
package tests.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.InitialeFigurPlatzierung;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker.SpielParameter;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.VakanterSpieler;

/**
 * <pre>
 * 
 * Testet die toDisplayString methode
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class DtoDiplayStringFormatTest {
	/**
	 * <pre>
	 * 
	 * @param args
	 * 
	 * </pre>
	 */
	public static void main(String[] args) {
		Random wuerfel = new Random();
		SpielParameter parameters[] = new SpielParameter[10];
		Arrays.setAll(parameters, i -> new SpielParameter(InitialeFigurPlatzierung.START_FELDER, 3, 6, 8));
		RundenLaufMitHerausSchlagenMaker maker = new RundenLaufMitHerausSchlagenMaker(
				true,
				parameters
		);
		
		System.out.println("\n" + "*".repeat(60) + "\n");
		
		Set<SpielZustand> spielZustaendeMenge = maker.getSpielZustaendeMenge();
		int selection = wuerfel.nextInt(spielZustaendeMenge.size());
		SpielZustand spielZustand = new ArrayList<>(spielZustaendeMenge).get(selection);
		spielZustand.setSpielStart(LocalDateTime.of(
				LocalDate.ofEpochDay(wuerfel.nextLong(-1 * 365 * 1_000, 365 * 1_000 + 1)),
				LocalTime.of(
						wuerfel.nextInt(1, 24),
						wuerfel.nextInt(1, 60),
						wuerfel.nextInt(1, 60)
				)
		));
		spielZustand.setSpielEnde(LocalDateTime.of(
				LocalDate.ofEpochDay(wuerfel.nextLong(-1 * 365 * 1_000, 365 * 1_000 + 1)),
				LocalTime.of(
						wuerfel.nextInt(1, 24),
						wuerfel.nextInt(1, 60),
						wuerfel.nextInt(1, 60)
				)
		));
		
		System.out.println("Teste SpielZustand::toDisplayString:\n" + spielZustand.toDisplayString());
		
		Map.Entry<KonkreterSpieler, VakanterSpieler> spielerEntry = spielZustand.getSpielerMenge().stream().collect(Collectors.teeing(
				Collectors.filtering(
						s -> s instanceof KonkreterSpieler,
						Collectors.toList()
				),
				Collectors.filtering(
						s -> s instanceof VakanterSpieler,
						Collectors.toList()
				),
				(a, b) -> Map.entry(
						(KonkreterSpieler)a.get(wuerfel.nextInt(a.size())),
						(VakanterSpieler)b.get(wuerfel.nextInt(b.size()))
				)
		));
		
		System.out.println("\n" + "*".repeat(60) + "\n");
		
		AktiverSpielerDekorator aktiverSpieler = spielZustand.getAktiverSpieler();
		System.out.println("Teste AktiverSpielerDekorator::toDisplayString:\n" + aktiverSpieler.toDisplayString());

		System.out.println("\n" + "*".repeat(60) + "\n");
		
		KonkreterSpieler konkreterSpieler = spielerEntry.getKey();
		System.out.println("Teste KonkreterSpieler::toDisplayString:\n" + konkreterSpieler.toDisplayString());

		System.out.println("\n" + "*".repeat(60) + "\n");

		VakanterSpieler vakanterSpieler = spielerEntry.getValue();
		System.out.println("Teste VakanterSpieler::toDisplayString:\n" + vakanterSpieler.toDisplayString());
		
		System.out.println("\n" + "*".repeat(60) + "\n");
		
		
	}

}
