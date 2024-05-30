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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.server_console;

import java.util.List;
import java.util.Scanner;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.RundenLaufMitHerausschlagenAppUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;

/**
 * <pre>
 * 
 * Implementiert die Konsolen UI für den Client.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class ServerAppConsoleUi implements RundenLaufMitHerausschlagenAppUi {
	/**
	 * The game brand title name
	 */
	private final String brandTitleName;

	/**
	 * The game brand slogan words list
	 */
	private final List<String> brandBoardSloganWords;

	/**
	 * <pre>
	 * 
	 * Server business Logik.
	 * 
	 * </pre>
	 */
	private final RundenLaufMitHerausschlagenServerService jeannie;

	/**
	 * <pre>
	 * 
	 * Hier werden nur Attribute initialisiert, der Start der UI wird in einer anderen Methode beauftragt.
	 * @param jeannie
	 * 
	 * </pre>
	 */
	public ServerAppConsoleUi(
			String brandTitleName,
			List<String> brandBoardSloganWords,
			RundenLaufMitHerausschlagenServerService jeannie
	) {
		this.brandTitleName = brandTitleName;
		this.brandBoardSloganWords = brandBoardSloganWords;
		this.jeannie = jeannie;
	}
	
	public void launch() {
		System.out.println("\t\tWillkommen in der Welt der Amigurumis!\n");
		navigieren();
	}

	/**
	 * <pre>
	 * 
	 * // Diese Methode ruft sich selbst auf Rekursive Methode.
	 * // Rekursive Methoden können statt Schleifen verwendet werden.
	 * // Rekursionen müssen beendet werden: explizites return.
	 * 
	 * </pre>
	 */
	private void navigieren() {
		// Option 1: Schleife:
		while(true) {
			anzeigenHauptmenueOptionen();
			
			// Im ersten Schritt, keine Falscheingaben abfangen!!!
			@SuppressWarnings("resource")
			Scanner leser = new Scanner(System.in);
			int auswahl = leser.nextInt();
			switch(auswahl) {
			case 1 -> anzeigenAllerArten();
			case 2 -> anzeigenAllerNamen();
			case 3 -> anzeigenAllerEinhoerner();
			case 4 -> anzeigenAllerPetsFuerKleinkinder();
			case 5 -> aufnehmenNeuerAmigurumis();
			default -> {
				anzeigenAbschied();
				return;
			}
			};
			
			System.out.println("\n");
			
//			// Option 2: Rekursion
//			navigieren();
		}
	}
	
	/**
	 * <pre>
	 * 
	 * 
	 * </pre>
	 */
	private void anzeigenHauptmenueOptionen() {
//		System.out.println("\nBitte wählen:\n\t"
//				+ "1: Diese Arten von Amigurumis kannst du bei uns entdecken\n\t"
//				+ "2: Dein Amigurumi soll einen besonderen Namen haben? Die Namen findest du bei uns\n\t"
//				+ "3: Einhörner? Natürlich! Hier findest du alle unsere Einhörner\n\t"
//				+ "4: Die Kleinsten kommen bei uns nicht zu kurz: Amigurumis für Kids gibt es hier\n\t"
//				+ "5: Du hast gehäkelt? Lade dein Amigurumi hoch und wir finden ein neues Zuhause\n\t"
//				+ "\n\tSchließe unseren Shop mit jeder anderen Zahl."
//		);
		//char zeilenAnfang = '《';	// \u300A
		//char zeilenEnde = '》';		// \u300B
		
		String text = """
				《	1: Diese Arten von Amigurumis kannst du bei uns entdecken》
				《	2: Dein Amigurumi soll einen besonderen Namen haben? Die Namen findest du bei uns》
				《	3: Einhörner? Natürlich! Hier findest du alle unsere Einhörner》
				《	4: Die Kleinsten kommen bei uns nicht zu kurz: Amigurumis für Kids gibt es hier》
				《	5: Du hast gehäkelt? Lade dein Amigurumi hoch und wir finden ein neues Zuhause》
				《》
				《	Schließe unseren Shop mit jeder anderen Zahl.》
		""".replaceAll("((^|》)\\s*《)|(》\\s*(《|$))", "\n");
		text = text.substring(1, text.length()-1);
		System.out.println(text);
	}

	/**
	 * <pre>
	 * 
	 * 
	 * </pre>
	 */
	private void anzeigenAbschied() {
		System.out.println("Vielen Dank, mehr Amigurumis findest du auch auf "
				+ "unserer Webseite www.derNerdHaekelt.de.");
	}

	/**
	 * <pre>
	 * 
	 * 
	 * </pre>
	 */
	private void anzeigenAllerArten() {
		// TODO
		System.out.println("Anzeigen aller Arten");
		
//		Random wuerfel = new Random();
//		String[] marketingText = {
//				" ist unverzichtbar bei schlechtem Wetter",
//				" zaubert ein Lächeln auf jedes Gesicht",
//				" ist dein bester Freund",
//				" kann fliegen"
//		};
//		
//		Set<String> alleArten = jeannie.zussammenstellenAllerArtenImPortfolio();
//		alleArten.forEach(art -> System.out.println("Ein Amigurumi der Art " + art + marketingText[wuerfel.nextInt(marketingText.length)]));
//
//		// Frage an den User: Ist eine Art dabei, die dir gefällt?
//		// Frage an den User: Welche Art fehlt dir?
//		// Frage an den User: Zu welcher Art sollen wird dir Amigurumis zeigen?
	}

	/**
	 * <pre>
	 * 
	 * 
	 * </pre>
	 */
	private void anzeigenAllerNamen() {
		// TODO
		System.out.println("Anzeigen aller Namen");
	}

	/**
	 * <pre>
	 * 
	 * 
	 * </pre>
	 */
	private void anzeigenAllerEinhoerner() {
		// TODO
		System.out.println("Anzeigen aller Einhörner");
	}

	/**
	 * <pre>
	 * 
	 * 
	 * </pre>
	 */
	private void anzeigenAllerPetsFuerKleinkinder() {
		// TODO
		System.out.println("Anzeigen aller Pets für Kleinkinder");
	}

	/**
	 * <pre>
	 * 
	 * 
	 * </pre>
	 */
	private void aufnehmenNeuerAmigurumis() {
		// TODO
		System.out.println("Aufnehmen neuer Amigurumis");
//		
//		@SuppressWarnings("resource")
//		Scanner leser = new Scanner(System.in);
//		
//		System.out.println("Du hast gehäkelt und suchst eine neue Familie für dein Amigurumi");
//
//		System.out.println("Welche Art hat das Amigurumi?");
//		String art = leser.nextLine();
//		
//		System.out.println("Wie heißt das Amigurumi?");
//		String name = leser.nextLine();
//		
//		System.out.println("Ist das Amigurumi frei von Kleinteilen? Zum Beispiel Wackelaugen.");
//		String kleinteilfrei = leser.nextLine();
//		
//		System.out.println("Amigurumis, die ein neues Zuhause suchen kosten immer 12 €.");
//		System.out.println("Dein Amigurumi ist bereit adoptiert zu werden, es ist ab heute verfügbar.");
//		
//		Amigurumi neu = new Amigurumi(
//				name,
//				art,
//				false, // TODO: später
//				12_00,
//				LocalDate.now()
//		);
//		jeannie.aufnehmenNeuerAmigurumis(neu);
	}
}
