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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.VakanterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.ZuWenigeSpielerException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator.AktiverSpielerTodoAktion;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld.FeldArt;
import javafx.scene.paint.Color;

/**
 * <pre>
 * 
 * Erstellt BeispielDaten für eine "Runden Lauf mit Rausschlagen"-Datenbank
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class RundenLaufMitHerausSchlagenMaker {
	/**
	 * Bestimmt of Primär-Schlüssel gesetzt werden oder nicht.
	 */
	private final boolean setzeSchluessel;
	
	private long maxSpielZustandSchluessel = 0L;
	
	private long maxFarbeSchluessel = 0L;
	
	private long maxSpielFeldSchluessel = 0L;
	
	private long maxSpielerSchluessel = 0L;
	
	private long maxAktiverSpielerSchluessel = 0L;
	
	private long maxFigurenSchluessel = 0L;
	
	/**
	 * Farben Namen
	 */
	private final List<String> farbenNamen = new ArrayList<>(Farbe.ALL_COLOR_NAMES);

	/**
	 * Spieler Namen
	 */
	private final List<String> spielerNamen = Arrays.asList(
			"Alice", "Bob", "Charlie", "David",
			"Julia", "Jennifer", "Melanie",
			"Phillipp", "Axel", "Peter", "Tobias",
			"Max", "Nils", "Stephanie"
	);
	
	/**
	 * Menge der Spiel-Zustände
	 */
	private Set<SpielZustand> spielZustandsMenge = new HashSet<>();
	
	private Set<AktiverSpielerDekorator> aktiveSpielerMenge = new HashSet<>();
	
	private Set<Spieler> spielerMenge = new HashSet<>();
	
	private Set<Farbe> farbenMenge = new HashSet<>();
	
	private Set<Figur> figurenMenge = new HashSet<>();
	
	private Set<SpielFeld> spielFelderMenge = new HashSet<>();
	
	/**
	 * <pre>
	 * 
	 * Generiert eine Datenhaltung mit 10 zufälligen Spiel-Zuständen am Spiel-Start.
	 * 
	 * </pre>
	 */
	public RundenLaufMitHerausSchlagenMaker(
			boolean setzeSchluessel,
			InitialeFigurPlatzierung initialePositionierung
	) {
		this.setzeSchluessel = setzeSchluessel;

		Random wuerfel = new Random();
		SpielParameter[] parameterArray = new SpielParameter[10];
		Arrays.setAll(parameterArray, i -> {
			boolean kleinesBrett = wuerfel.nextBoolean();
			return new SpielParameter(
					initialePositionierung,
					wuerfel.nextInt(2, kleinesBrett ? 4 : 6),
					kleinesBrett ? 4 : 6,
					kleinesBrett ? 10 : 8
			);
		});
		
		for(SpielParameter parameter : parameterArray) {
			int anzahlKonkreterSpieler = parameter.getAnzahlKonkreterSpieler();
			int anzahlSpielerPositionen = parameter.getAnzahlSpielerPositionen();
			int streckenOffset = parameter.getStreckenOffset();

			List<Spieler> spielerListe = erstellenSpielerListe(initialePositionierung, anzahlKonkreterSpieler, anzahlSpielerPositionen, streckenOffset);
			SpielZustand spielZustand = erstellenSpielZustand(spielerListe, anzahlKonkreterSpieler, anzahlSpielerPositionen, streckenOffset);
			
			if(setzeSchluessel) {
				spielZustand.setSchluessel(maxSpielZustandSchluessel);
				maxSpielZustandSchluessel++;
			}
			
			spielZustandsMenge.add(spielZustand);
		}
	}
	
	/**
	 * <pre>
	 * 
	 * Generiert eine Datenhaltung mit genau einem Element
	 * 
	 * @param setzeSchluessel
	 * 		bestimme ob Schlüssel gesetzt werden oder nicht
	 * @param initialePositionierung
	 * 		bestimme wie die Figuren aufgestellt werden
	 * @param anzahlKonkreterSpieler
	 * 		die Anzahl der Spieler
	 * @param anzahlSpielerPositionen
	 * 		die maximale Anzahl der Spieler, für die das Spielbrett ausgelegt ist
	 * @param streckenOffset
	 * 		Anzahl der Streckenfelder von einem Start-Streckenfeld eines Spielers
	 * 		bis zum nächsten StartStreckenfeld des nächsten Spielers
	 * 
	 * </pre>
	 */
	public RundenLaufMitHerausSchlagenMaker(
			boolean setzeSchluessel,
			InitialeFigurPlatzierung initialePositionierung,
			int anzahlKonkreterSpieler,
			int anzahlSpielerPositionen,
			int streckenOffset
	) {
		this.setzeSchluessel = setzeSchluessel;
		
		List<Spieler> spielerListe = erstellenSpielerListe(initialePositionierung, anzahlKonkreterSpieler, anzahlSpielerPositionen, streckenOffset);
		SpielZustand spielZustand = erstellenSpielZustand(spielerListe, anzahlKonkreterSpieler, anzahlSpielerPositionen, streckenOffset);
		
		if(setzeSchluessel) {
			spielZustand.setSchluessel(maxSpielZustandSchluessel);
			maxSpielZustandSchluessel++;
		}
		
		spielZustandsMenge.add(spielZustand);
	}
	
	public RundenLaufMitHerausSchlagenMaker(
			boolean setzeSchluessel,
			SpielParameter... parameterArray
	) {
		this.setzeSchluessel = setzeSchluessel;
		
		for(SpielParameter parameter : parameterArray) {
			InitialeFigurPlatzierung initialePositionierung = parameter.getInitialePositionierung();
			int anzahlKonkreterSpieler = parameter.getAnzahlKonkreterSpieler();
			int anzahlSpielerPositionen = parameter.getAnzahlSpielerPositionen();
			int streckenOffset = parameter.getStreckenOffset();

			List<Spieler> spielerListe = erstellenSpielerListe(initialePositionierung, anzahlKonkreterSpieler, anzahlSpielerPositionen, streckenOffset);
			SpielZustand spielZustand = erstellenSpielZustand(spielerListe, anzahlKonkreterSpieler, anzahlSpielerPositionen, streckenOffset);
			
			if(setzeSchluessel) {
				spielZustand.setSchluessel(maxSpielZustandSchluessel);
				maxSpielZustandSchluessel++;
			}
			
			spielZustandsMenge.add(spielZustand);
		}
	}
	
	private List<Spieler> erstellenSpielerListe(
			InitialeFigurPlatzierung initialePositionierung,
			int anzahlKonkreterSpieler,
			int anzahlSpielerPositionen,
			int streckenOffset
	) {
		if(anzahlKonkreterSpieler < 2) {
			throw new ZuWenigeSpielerException();
		}
		Collections.shuffle(spielerNamen);
		Collections.shuffle(farbenNamen);
		
		int streckenLength = anzahlSpielerPositionen * streckenOffset;

		List<Spieler> spielerListe = new ArrayList<>();
		Figur[] alleFigurenArray = new Figur[anzahlKonkreterSpieler * Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER];
		List<Figur> alleFigurenList = Arrays.asList(alleFigurenArray);
		for(int i = 0; i<anzahlSpielerPositionen; i++) {
			int indexStartStreckenFeld = i * streckenOffset;
			int indexEndStreckenFeld = (indexStartStreckenFeld + streckenLength - 1) % streckenLength;
			String spielerFarbenName = farbenNamen.get(i);
			Farbe spielerFarbe = Farbe.from(Color.valueOf(spielerFarbenName), spielerFarbenName);
			SpielFeld startStreckenFeld = SpielFeld.erstelleFarbigesStreckenFeld(spielerFarbe, indexStartStreckenFeld);
			SpielFeld endStreckenFeld = SpielFeld.erstelleFarblosesStreckenFeld(indexEndStreckenFeld);
			
			if(setzeSchluessel) {
				spielerFarbe.setSchluessel(maxFarbeSchluessel);
				maxFarbeSchluessel++;
				startStreckenFeld.setSchluessel(maxSpielFeldSchluessel);
				maxSpielFeldSchluessel++;
				endStreckenFeld.setSchluessel(maxSpielFeldSchluessel);
				maxSpielFeldSchluessel++;
			}
			
			farbenMenge.add(spielerFarbe);
			
			Spieler spieler;
			if(i >= anzahlKonkreterSpieler) {
				spieler = new VakanterSpieler(
						spielerFarbe,
						startStreckenFeld,
						endStreckenFeld
				);
			} else {
				Random wuerfel = new Random();
				String spielerName = spielerNamen.get(i);
				
				Figur[] figurenArray = new Figur[Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER];
				Arrays.setAll(
						figurenArray,
						index -> {
							SpielFeld spielFeld = switch(initialePositionierung){
							case START_FELDER -> SpielFeld.erstelleStartFeld(spielerFarbe, index);
							case ZIEL_FELDER -> SpielFeld.erstelleZielFeld(spielerFarbe, index);
							case START_STRECKENFELD_START_FELDER -> switch(index) {
							case 0 -> startStreckenFeld;
							default -> SpielFeld.erstelleStartFeld(spielerFarbe, index);
							};
							case ZUFAELLIG -> ((Function<Void, SpielFeld>) (p -> {
								return switch(wuerfel.nextInt(5)) {
								case 0 -> SpielFeld.erstelleStartFeld(spielerFarbe, index);
								case 1 -> SpielFeld.erstelleZielFeld(spielerFarbe, index);
								case 2 -> startStreckenFeld;
								case 3 -> endStreckenFeld;
								case 4 -> ((Function<Void, SpielFeld>) (q -> {
									int nextPosition = wuerfel.nextInt(1, startStreckenFeld.getFeldPosition() + streckenLength - 1);
									while(true) {
										int vergleicheNextPosition = nextPosition;
										boolean isDuplicate = alleFigurenList.stream().anyMatch(f -> 
												f != null
												&& f.getSpielFeld().getFeldArt() == FeldArt.STRECKEN_FELD
												&& f.getSpielFeld().getFeldPosition() == vergleicheNextPosition
										);
										if(!isDuplicate) {
											break;
										}
										nextPosition = wuerfel.nextInt(1, startStreckenFeld.getFeldPosition() + streckenLength - 1);
									}
									return SpielFeld.erstelleFarblosesStreckenFeld(nextPosition);
								})).apply(null);
								default -> throw new AssertionError();
								};
							})).apply(null);
							};
							
							Figur figur = Figur.erstelleFigur(
									spielerFarbe,
									spielFeld
							);
							if(setzeSchluessel) {
								spielFeld.setSchluessel(maxSpielFeldSchluessel);
								maxSpielFeldSchluessel++;
								
								figur.setSchluessel(maxFigurenSchluessel);
								maxFigurenSchluessel++;
							}
							
							spielFelderMenge.add(spielFeld);
							
							figurenMenge.add(figur);
							
							return figur;
						}
				);
				spieler = new KonkreterSpieler(
						spielerFarbe,
						startStreckenFeld, 
						endStreckenFeld,
						spielerName, 
						new HashSet<>(Arrays.asList(figurenArray)), 
						null
				);
			}
			
			if(setzeSchluessel) {
				spieler.setSchluessel(maxSpielerSchluessel);
				maxSpielerSchluessel++;
			}
			spielerMenge.add(spieler);
			
			spielerListe.add(spieler);
		}
		Collections.shuffle(spielerListe);
		return spielerListe;
	}

	private SpielZustand erstellenSpielZustand(
			List<Spieler> alleSpieler,
			int anzahlKonkreterSpieler,
			int anzahlSpielerPositionen,
			int streckenOffset
	) {
		LinkedHashSet<Spieler> setAllerSpieler = alleSpieler
				.stream()
				.filter(s -> s instanceof KonkreterSpieler || s instanceof VakanterSpieler)
				.distinct()
				.collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
		
		Set<Figur> setAllerFiguren = setAllerSpieler
				.stream()
				.flatMap(s -> s.getFigurenMenge().stream())
				.collect(Collectors.toSet());
		
		KonkreterSpieler aktiverSpieler = (KonkreterSpieler)setAllerSpieler.stream().filter(s -> s instanceof KonkreterSpieler).findFirst().get();
		AktiverSpielerDekorator dekoratorAktiverSpieler = AktiverSpielerDekorator.erstelleAktivenSpieler(
				aktiverSpieler,
				AktiverSpielerTodoAktion.MUSS_WUERFELN,
				null,
				0,
				false
		);
		if(setzeSchluessel) {
			dekoratorAktiverSpieler.setSchluessel(maxAktiverSpielerSchluessel);
			maxAktiverSpielerSchluessel++;
		}
		aktiveSpielerMenge.add(dekoratorAktiverSpieler);
		
		return new SpielZustand(
				dekoratorAktiverSpieler,
				setAllerSpieler,
				setAllerFiguren,
				anzahlSpielerPositionen,
				anzahlKonkreterSpieler,
				streckenOffset
		);
	}
	
	/**
	 * <pre>
	 * 
	 * @return the spielZustandsListe
	 * 
	 * </pre>
	 */
	public Set<SpielZustand> getSpielZustaendeMenge() {
		return spielZustandsMenge;
	}
	
	/**
	 * <pre>
	 * 
	 * @return den SpielZustand mit schluessel
	 * 
	 * </pre>
	 */
	public SpielZustand getSpielZustand(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts löschen");
		}
		Long wrapSchluessel = (Long)schluessel;
		return spielZustandsMenge
				.stream()
				.filter(s -> wrapSchluessel.equals(s.getSchluessel()))
				.findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException(
						"SpielZustand mit dem Schlüssel "
						+ schluessel
						+ " ist nicht in der Datenbank"
				));
	}

	/**
	 * <pre>
	 * 
	 * @param spielZustand
	 * 
	 * </pre>
	 */
	public void hinzufuegenSpielZustand(SpielZustand spielZustand) {
		if(spielZustand.getSchluessel() != null) {
			throw new PrimaerSchluesselException("der SpielZustand ist bereits in einer Datenbank");
		}
		
		if(setzeSchluessel) {
			spielZustand.setSchluessel(maxSpielZustandSchluessel);
		}
		spielZustandsMenge.add(spielZustand);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * 
	 * </pre>
	 */
	public void loeschenSpielZustand(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts löschen");
		}
		boolean wasRemoved = spielZustandsMenge.removeIf(s -> ((Long)schluessel).equals(s.getSchluessel()));
		if(!wasRemoved) {
			throw new PrimaerSchluesselException("der SpielZustand war nicht in der Datenbank");
		}
	}

	/**
	 * <pre>
	 * 
	 * @param zuLoeschen
	 * 
	 * </pre>
	 */
	public void loeschenSpielZustand(SpielZustand zuLoeschen) {
		boolean wasRemoved = spielZustandsMenge.remove(zuLoeschen);
		if(!wasRemoved) {
			throw new PrimaerSchluesselException("der SpielZustand war nicht in der Datenbank");
		}
	}

	/**
	 * <pre>
	 * 
	 * @return
	 * 
	 * </pre>
	 */
	public Set<AktiverSpielerDekorator> getAktiveSpielerMenge() {
		return new HashSet<>(aktiveSpielerMenge);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * @return
	 * 
	 * </pre>
	 */
	public AktiverSpielerDekorator getAktiverSpieler(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entnehmen");
		}
		return aktiveSpielerMenge.stream()
				.filter(aktiverSpieler -> ((Long)schluessel).equals(aktiverSpieler.getSchluessel()))
				.findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("der aktive Spieler war nicht in der Datenbank"));
	}

	/**
	 * <pre>
	 * 
	 * @param aktiverSpieler
	 * 
	 * </pre>
	 */
	public void hinzufuegenAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
		if(aktiverSpieler.getSchluessel() != null) {
			throw new PrimaerSchluesselException("der aktive Spieler ist schon in einer Datenbank");
		}
		if(setzeSchluessel) {
			aktiverSpieler.setSchluessel(maxAktiverSpielerSchluessel);
			maxAktiverSpielerSchluessel++;
		}
		aktiveSpielerMenge.add(aktiverSpieler);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * 
	 * </pre>
	 */
	public void loeschenAktiverSpieler(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entfernen");
		}
		aktiveSpielerMenge.removeIf(aktiverSpieler -> schluessel == aktiverSpieler.getSchluessel());
	}

	/**
	 * <pre>
	 * 
	 * @param zuLoeschen
	 * 
	 * </pre>
	 */
	public void loeschenAktiverSpieler(AktiverSpielerDekorator zuLoeschen) {
		boolean wasRemoved = aktiveSpielerMenge.remove(zuLoeschen);
		if(!wasRemoved) {
			throw new PrimaerSchluesselException("der aktive Spieler war nicht in der Datenbank");
		}
	}

	/**
	 * <pre>
	 * 
	 * @return
	 * 
	 * </pre>
	 */
	public Set<Spieler> getSpielerMenge() {
		return new HashSet<>(spielerMenge);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * @return
	 * 
	 * </pre>
	 */
	public Spieler getSpieler(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entnehmen");
		}
		return spielerMenge.stream()
				.filter(spieler -> ((Long)schluessel).equals(spieler.getSchluessel()))
				.findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("der Spieler war nicht in der Datenbank"));
	}

	/**
	 * <pre>
	 * 
	 * @param spieler
	 * 
	 * </pre>
	 */
	public void hinzufuegenSpieler(Spieler spieler) {
		if(spieler.getSchluessel() != null) {
			throw new PrimaerSchluesselException("der Spieler ist schon in einer Datenbank");
		}
		if(setzeSchluessel) {
			spieler.setSchluessel(maxAktiverSpielerSchluessel);
			maxSpielerSchluessel++;
		}
		spielerMenge.add(spieler);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * 
	 * </pre>
	 */
	public void loeschenSpieler(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entfernen");
		}
		spielerMenge.removeIf(aktiverSpieler -> schluessel == aktiverSpieler.getSchluessel());
	}

	/**
	 * <pre>
	 * 
	 * @param zuLoeschen
	 * 
	 * </pre>
	 */
	public void loeschenSpieler(Spieler zuLoeschen) {
		boolean wasRemoved = spielerMenge.remove(zuLoeschen);
		if(!wasRemoved) {
			throw new PrimaerSchluesselException("der Spieler war nicht in der Datenbank");
		}
	}

	/**
	 * <pre>
	 * 
	 * @return
	 * 
	 * </pre>
	 */
	public Set<Farbe> getFarbenMenge() {
		return new HashSet<>(farbenMenge);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * @return
	 * 
	 * </pre>
	 */
	public Farbe getFarbe(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entnehmen");
		}
		return farbenMenge.stream()
				.filter(farbe -> ((Long)schluessel).equals(farbe.getSchluessel()))
				.findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("die Farbe war nicht in der Datenbank"));
	}

	/**
	 * <pre>
	 * 
	 * @param spieler
	 * 
	 * </pre>
	 */
	public void hinzufuegenFarbe(Farbe farbe) {
		if(farbe.getSchluessel() != null) {
			throw new PrimaerSchluesselException("die Farbe ist schon in einer Datenbank");
		}
		if(setzeSchluessel) {
			farbe.setSchluessel(maxAktiverSpielerSchluessel);
			maxSpielerSchluessel++;
		}
		farbenMenge.add(farbe);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * 
	 * </pre>
	 */
	public void loeschenFarbe(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entfernen");
		}
		farbenMenge.removeIf(farbe -> schluessel == farbe.getSchluessel());
	}

	/**
	 * <pre>
	 * 
	 * @param zuLoeschen
	 * 
	 * </pre>
	 */
	public void loeschenFarbe(Farbe zuLoeschen) {
		boolean wasRemoved = farbenMenge.remove(zuLoeschen);
		if(!wasRemoved) {
			throw new PrimaerSchluesselException("der Farbe war nicht in der Datenbank");
		}
	}

	/**
	 * <pre>
	 * 
	 * @return
	 * 
	 * </pre>
	 */
	public Set<Figur> getFigurenMenge() {
		return new HashSet<>(figurenMenge);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * @return
	 * 
	 * </pre>
	 */
	public Figur getFigur(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entnehmen");
		}
		return figurenMenge.stream()
				.filter(figur -> ((Long)schluessel).equals(figur.getSchluessel()))
				.findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("die Figur war nicht in der Datenbank"));
	}

	/**
	 * <pre>
	 * 
	 * @param spieler
	 * 
	 * </pre>
	 */
	public void hinzufuegenFigur(Figur figur) {
		if(figur.getSchluessel() != null) {
			throw new PrimaerSchluesselException("die Figur ist schon in einer Datenbank");
		}
		if(setzeSchluessel) {
			figur.setSchluessel(maxAktiverSpielerSchluessel);
			maxSpielerSchluessel++;
		}
		figurenMenge.add(figur);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * 
	 * </pre>
	 */
	public void loeschenFigur(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entfernen");
		}
		figurenMenge.removeIf(farbe -> schluessel == farbe.getSchluessel());
	}

	/**
	 * <pre>
	 * 
	 * @param zuLoeschen
	 * 
	 * </pre>
	 */
	public void loeschenFigur(Figur zuLoeschen) {
		boolean wasRemoved = figurenMenge.remove(zuLoeschen);
		if(!wasRemoved) {
			throw new PrimaerSchluesselException("die Figur war nicht in der Datenbank");
		}
	}

	/**
	 * <pre>
	 * 
	 * @return
	 * 
	 * </pre>
	 */
	public Set<SpielFeld> getSpielFelderMenge() {
		return new HashSet<>(spielFelderMenge);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * @return
	 * 
	 * </pre>
	 */
	public SpielFeld getSpielFeld(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entnehmen");
		}
		return spielFelderMenge.stream()
				.filter(figur -> ((Long)schluessel).equals(figur.getSchluessel()))
				.findFirst()
				.orElseThrow(() -> new PrimaerSchluesselException("das SpielFeld war nicht in der Datenbank"));
	}

	/**
	 * <pre>
	 * 
	 * @param spieler
	 * 
	 * </pre>
	 */
	public void hinzufuegenSpielFeld(SpielFeld spielFeld) {
		if(spielFeld.getSchluessel() != null) {
			throw new PrimaerSchluesselException("das SpielFeld ist schon in einer Datenbank");
		}
		if(setzeSchluessel) {
			spielFeld.setSchluessel(maxAktiverSpielerSchluessel);
			maxSpielerSchluessel++;
		}
		spielFelderMenge.add(spielFeld);
	}

	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * 
	 * </pre>
	 */
	public void loeschenSpielFeld(long schluessel) {
		if(!setzeSchluessel) {
			throw new UnsupportedOperationException("kann ohne Schlüssel nichts entfernen");
		}
		spielFelderMenge.removeIf(farbe -> schluessel == farbe.getSchluessel());
	}

	/**
	 * <pre>
	 * 
	 * @param zuLoeschen
	 * 
	 * </pre>
	 */
	public void loeschenSpielFeld(SpielFeld zuLoeschen) {
		boolean wasRemoved = spielFelderMenge.remove(zuLoeschen);
		if(!wasRemoved) {
			throw new PrimaerSchluesselException("das SpielFeld war nicht in der Datenbank");
		}
	}
	
	/**
	 * Gibt die Strategie an wie die Figuren anfangs platziert werden sollen.
	 */
	public enum InitialeFigurPlatzierung {
		START_FELDER,
		ZIEL_FELDER,
		START_STRECKENFELD_START_FELDER,
		ZUFAELLIG
	}
	
	public static class SpielParameter {
		/**
		 * Gibt an, wie die Figuren initial aufgestellt werden.
		 */
		private final InitialeFigurPlatzierung initialePositionierung;
		
		/**
		 * Wie viele Spieler spielen mit
		 */
		private final int anzahlKonkreterSpieler;
		
		/**
		 * Für wie viele Spieler ist das Spielbrett ausgelegt
		 */
		private final int anzahlSpielerPositionen;
		
		/**
		 * Wie groß ist der Strecken-Offset von einem zum nächsten Spieler
		 */
		private final int streckenOffset;
		
		/**
		 * <pre>
		 * 
		 * @param initialePlatzierung
		 * @param anzahlKonkreterSpieler
		 * @param anzahlSpielerPositionen
		 * @param streckenOffset
		 * 
		 * </pre>
		 */
		public SpielParameter(
				InitialeFigurPlatzierung initialePositionierung,
				int anzahlKonkreterSpieler,
				int anzahlSpielerPositionen,
				int streckenOffset
			) {
			this.initialePositionierung = initialePositionierung;
			this.anzahlKonkreterSpieler = anzahlKonkreterSpieler;
			this.anzahlSpielerPositionen = anzahlSpielerPositionen;
			this.streckenOffset = streckenOffset;
		}
		
		/**
		 * <pre>
		 * 
		 * @return the initialePositionierung
		 * 
		 * </pre>
		 */
		public InitialeFigurPlatzierung getInitialePositionierung() {
			return initialePositionierung;
		}

		/**
		 * <pre>
		 * 
		 * @return the spielerAnzahl
		 * 
		 * </pre>
		 */
		public int getAnzahlKonkreterSpieler() {
			return anzahlKonkreterSpieler;
		}

		/**
		 * <pre>
		 * 
		 * @return the maxSpielerAnzahl
		 * 
		 * </pre>
		 */
		public int getAnzahlSpielerPositionen() {
			return anzahlSpielerPositionen;
		}

		/**
		 * <pre>
		 * 
		 * @return the streckenOffset
		 * 
		 * </pre>
		 */
		public int getStreckenOffset() {
			return streckenOffset;
		}

		@Override
		public int hashCode() {
			return Objects.hash(anzahlSpielerPositionen, anzahlKonkreterSpieler, streckenOffset);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			SpielParameter other = (SpielParameter) obj;
			return anzahlSpielerPositionen == other.anzahlSpielerPositionen && anzahlKonkreterSpieler == other.anzahlKonkreterSpieler
					&& streckenOffset == other.streckenOffset;
		}

		@Override
		public String toString() {
			return "SpielParameter [spielerAnzahl=" + anzahlKonkreterSpieler + ", maxSpielerAnzahl=" + anzahlSpielerPositionen
					+ ", streckenOffset=" + streckenOffset + "]";
		}
	}
}
