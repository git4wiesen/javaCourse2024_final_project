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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.IllegaleSpielerAktionException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZug;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator.AktiverSpielerTodoAktion;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld.FeldArt;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.RundenLaufMitHerausschlagenService;

/**
 * <pre>
 * 
 * Business logic of the game server.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class RundenLaufMitHerausschlagenServerService implements RundenLaufMitHerausschlagenService {
	private final RundenLaufMitHerausschlagenDao james;
	
	private SpielZustand aktivesSpiel;
	
	public RundenLaufMitHerausschlagenServerService(
			RundenLaufMitHerausschlagenDao james,
			SpielZustand aktivesSpiel
	) {
		this.james = james;
		this.aktivesSpiel = aktivesSpiel;
	}
	
	/**
	 * <pre>
	 * 
	 * @return alle Spieler im Spiel
	 * 
	 * </pre>
	 */
	public List<Spieler> getAlleSpieler() {
		return new ArrayList<>(aktivesSpiel.getSpielerMenge());
	}
	
	/**
	 * <pre>
	 * 
	 * @return alle Spieler im Spiel
	 * 
	 * </pre>
	 */
	public List<Spieler> getKonkreteSpieler() {
		return new ArrayList<>(
				aktivesSpiel.getSpielerMenge()
					.stream()
					.filter(s -> s instanceof KonkreterSpieler)
					.collect(Collectors.toList())
		);
	}

	public List<SpielFeld> getStartStreckenFelder() {
		return new ArrayList<>(
				aktivesSpiel.getSpielerMenge()
					.stream()
					.map(s -> s.getStartStreckenFeld())
					.collect(Collectors.toList())
		);
	}

	public List<SpielFeld> getEndStreckenFelder() {
		return new ArrayList<>(
				aktivesSpiel.getSpielerMenge()
					.stream()
					.map(s -> s.getEndStreckenFeld())
					.collect(Collectors.toList())
		);
	}
	
	/**
	 * <pre>
	 * 
	 * @return gewinner des Spiels
	 * 
	 * </pre>
	 */
	public List<Spieler> getGewinnerSpieler() {
		return new ArrayList<>(aktivesSpiel.getGewinnerListe());
	}
	
	public boolean isSpielFertig() {
		long anzahlFertigerSpieler = aktivesSpiel.getFigurenMenge().stream().filter(f ->
				f.getSpielFeld().getFeldArt() == FeldArt.ZIEL_FELD
		).collect(Collectors.groupingBy(
				f -> f.getFarbe(),
				Collectors.counting()
		)).entrySet().stream().filter(
				e -> e.getValue() == Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER
		).count();
		return anzahlFertigerSpieler >= aktivesSpiel.getAnzahlKonkreterSpieler() - 1;
	}
	
	/**
	 * <pre>
	 * 
	 * @return alle Figuren im Spiel
	 * 
	 * </pre>
	 */
	public List<Figur> getAlleFiguren() {
		return new ArrayList<>(aktivesSpiel.getFigurenMenge());
	}
	
	/**
	 * <pre>
	 * 
	 * @return aktiver Spieler
	 * 
	 * </pre>
	 */
	public Spieler getAktiverSpieler() {
		return aktivesSpiel.getAktiverSpieler().getDerSpieler();
	}
	
	/**
	 * <pre>
	 * 
	 * @return die Todo-Aktion des aktiven Spielers
	 * 
	 * </pre>
	 */
	public AktiverSpielerTodoAktion getAktiverSpielerTodoAktion() {
		return aktivesSpiel.getAktiverSpieler().getAktuelleTodoAktion();
	}
	
	/**
	 * <pre>
	 * 
	 * Wechselt zum naechsten Spieler als aktivenSpieler.
	 * 
	 * </pre>
	 */
	public void naechsterAktiverSpieler() {
		if(isSpielFertig()) {
			throw new IllegaleSpielerAktionException("das Spiel ist fertig");
		}
		
		Spieler aktiverSpielerAlsKonkreterSpieler = getAktiverSpieler();
		if(getAktiverSpielerTodoAktion() != AktiverSpielerTodoAktion.FERTIG) {
			throw new IllegaleSpielerAktionException("der aktive Spieler mit der Farbe " + aktiverSpielerAlsKonkreterSpieler.getFarbe().toPrettyString() + " ist noch nicht fertig.");
		}
		
		List<Spieler> gewinner = new ArrayList<>(aktivesSpiel.getGewinnerListe());
		gewinner.remove(aktiverSpielerAlsKonkreterSpieler);
		
		List<Spieler> konkreteSpieler = aktivesSpiel.getSpielerMenge()
				.stream()
				.filter(s -> s instanceof KonkreterSpieler)
				.collect(Collectors.toList());
		konkreteSpieler.removeAll(gewinner);
		
		if(konkreteSpieler.size() == 1) {
			return;
		}
		
		int indexAktiverSpieler = konkreteSpieler.indexOf(aktiverSpielerAlsKonkreterSpieler);
		int indexNaechsterAktiverSpieler = (indexAktiverSpieler + 1) % konkreteSpieler.size();
		Spieler naechsterAktiverSpieler = konkreteSpieler.get(indexNaechsterAktiverSpieler);
		
		AktiverSpielerDekorator aktiverSpieler = aktivesSpiel.getAktiverSpieler();
		aktiverSpieler.naechsterAktiverSpieler(naechsterAktiverSpieler);
		james.updateAktiverSpieler(aktiverSpieler);
	}
	
	/**
	 * <pre>
	 * 
	 * @return das aktuelle WurfelErgebnis des aktiven Spielers
	 * 
	 * </pre>
	 */
	public WuerfelErgebnis getWuerfelErgebnis() {
		return aktivesSpiel.getAktiverSpieler().getWuerfelErgebnis();
	}
	
	/**
	 * <pre>
	 * 
	 * @return die aktuelle Anzahl an Würfel-Versuchen des aktuellen Spielers.
	 * 
	 * </pre>
	 */
	public int getWuerfelVersuchsAnzahl() {
		return aktivesSpiel.getAktiverSpieler().getWuerfelVersuchsAnzahl();
	}
	
	/**
	 * <pre>
	 * 
	 * Veranlasst den übergebenen Spieler zu würfeln, wenn er das darf
	 * 
	 * @param spieler der würfelnde Spieler
	 * @throws IllegaleSpielerAktionException
	 * 		- wenn der übergebende Spieler nicht aktiv ist
	 * 		- wenn der aktive Spieler gerade nicht würfeln darf
	 * 
	 * </pre>
	 */
	public void wuerfeln(Spieler spieler) {
		if(!(spieler instanceof KonkreterSpieler)) {
			throw new IllegalArgumentException("nur konkrete Spieler sind als Parameter erlaubt");
		}
		
		AktiverSpielerDekorator aktiverSpieler = aktivesSpiel.getAktiverSpieler();
		
		if(!(aktiverSpieler.getDerSpieler().equals(spieler))) {
			throw new IllegaleSpielerAktionException("der Spieler mit der Farbe " + spieler.getFarbe().toPrettyString() + " ist nicht dran");
		} else if(aktiverSpieler.getAktuelleTodoAktion() != AktiverSpielerTodoAktion.MUSS_WUERFELN) {
			throw new IllegaleSpielerAktionException("der aktiver Spieler mit der Farbe " + aktiverSpieler.getFarbe().toPrettyString() + " darf jetzt nicht würfeln");
		}
		
		Wuerfel wuerfel = Wuerfel.getInstance();

		WuerfelErgebnis wuerfelErgebnis = wuerfel.wuerfeln();
		aktiverSpieler.setWuerfelErgebnis(wuerfelErgebnis);

		int zielFeldFigurenMask = 0;
		for(Figur zielFeldFigur : aktiverSpieler.getZielFeldFigurenMenge()) {
			zielFeldFigurenMask = zielFeldFigurenMask | (1 << zielFeldFigur.getSpielFeld().getFeldPosition());
		}
		
		List<SpielZug> moeglicheZuege = bestimmeMoeglicheSpielZuege(aktiverSpieler, wuerfelErgebnis);
		if(!moeglicheZuege.isEmpty()) {
			aktiverSpieler.setTodoAktion(AktiverSpielerTodoAktion.MUSS_ZIEHEN);
		} else if(wuerfelErgebnis == WuerfelErgebnis.SECHS) {
			aktiverSpieler.setTodoAktion(AktiverSpielerTodoAktion.MUSS_WUERFELN);
		} else if(
				!aktiverSpieler.isHatGezogen()
				&& aktiverSpieler.getWuerfelVersuchsAnzahl() < 3
				&& aktiverSpieler.getStreckenFeldFigurenMenge().isEmpty()
				&& Arrays.asList(0, 8, 12, 14).contains(zielFeldFigurenMask)
		) {
			aktiverSpieler.setTodoAktion(AktiverSpielerTodoAktion.MUSS_WUERFELN);
		} else {
			aktiverSpieler.setTodoAktion(AktiverSpielerTodoAktion.FERTIG);
		}
		
		james.updateAktiverSpieler(aktiverSpieler);
	}

	/**
	 * <pre>
	 * 
	 * @param spieler
	 * @param wuerfelErgebnis
	 * @return
	 * 
	 * </pre>
	 */
	public List<SpielZug> bestimmeMoeglicheSpielZuege(Spieler spieler, WuerfelErgebnis wuerfelErgebnis) {
		if(spieler == null || wuerfelErgebnis == null) {
			throw new IllegalArgumentException("Parameter dürfen nicht null sein");
		}
		
		// Ermittle alle möglichen Züge
		List<SpielZug> moeglicheZuege = spieler.getFigurenMenge().stream().map(zugFigur -> {
			if(!spieler.getFarbe().equals(zugFigur.getFarbe())) {
				return null;
			}
			
			SpielFeld zielFeld = null;
			Figur schlagFigur = null;
			Farbe spielerFarbe = spieler.getFarbe();
			SpielFeld zugFigurFeld = zugFigur.getSpielFeld();
			FeldArt zugFigurFeldArt= zugFigurFeld.getFeldArt();

			switch(zugFigurFeldArt) {
			case START_FELD -> {
				if(wuerfelErgebnis == WuerfelErgebnis.SECHS) {
					zielFeld = spieler.getStartStreckenFeld();
				}
			}
			case STRECKEN_FELD -> {
				final int anzahlStreckenFelder = aktivesSpiel.getAnzahlStreckenFelder();
				final SpielFeld startStreckenFeld = spieler.getStartStreckenFeld();
				final SpielFeld endStreckenFeld = spieler.getEndStreckenFeld();

				final int startStreckenFeldPosition = startStreckenFeld.getFeldPosition();
				final int endStreckenFeldPosition = endStreckenFeld.getFeldPosition();
				final int figurStartPosition = zugFigurFeld.getFeldPosition();
				final int figurZielPosition = figurStartPosition + wuerfelErgebnis.getValue();
				
				int vergleichFigurZielPosition = figurZielPosition;
				int vergleichEndStreckenFeldPosition = endStreckenFeldPosition;
				if(startStreckenFeldPosition != 0) {
					vergleichEndStreckenFeldPosition += anzahlStreckenFelder;
					if(figurStartPosition < startStreckenFeldPosition) {
						vergleichFigurZielPosition += anzahlStreckenFelder;
					}
				}

				if(vergleichFigurZielPosition == vergleichEndStreckenFeldPosition) {
					zielFeld = endStreckenFeld;
				} else if(vergleichFigurZielPosition < vergleichEndStreckenFeldPosition) {
					zielFeld = SpielFeld.erstelleFarblosesStreckenFeld(
							figurZielPosition % anzahlStreckenFelder
					);
				} else if(vergleichFigurZielPosition < (vergleichEndStreckenFeldPosition + 1) + Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER) {
					zielFeld = SpielFeld.erstelleZielFeld(
							spielerFarbe,
							vergleichFigurZielPosition - (vergleichEndStreckenFeldPosition + 1)
					);
				}
			}
			case ZIEL_FELD -> {
				int figurStartPosition = zugFigurFeld.getFeldPosition();
				int figurZielPosition = figurStartPosition + wuerfelErgebnis.getValue();
				
				if(figurZielPosition < Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER) {
					zielFeld = SpielFeld.erstelleZielFeld(spielerFarbe, figurZielPosition);
				}
			}
			}

			if(zielFeld == null) {
				return null;
			}
				
			SpielFeld vergleichZielFeld = zielFeld;
			Figur andereFigur = aktivesSpiel.getFigurenMenge()
					.stream()
					.filter(f -> !zugFigur.equals(f) && vergleichZielFeld.equals(f.getSpielFeld()))
					.findFirst()
					.orElse(null);
			if(andereFigur != null) {
				if(spielerFarbe.equals(andereFigur.getFarbe())) {
					zielFeld = null;
				} else {
					schlagFigur = andereFigur;
				}
			}

			if(zielFeld == null) {
				return null;
			}

			return new SpielZug(
					zugFigur,
					wuerfelErgebnis,
					zielFeld,
					schlagFigur
			);
		}).filter(s -> s != null).collect(Collectors.toList());

		if(moeglicheZuege.isEmpty()) {
			return Collections.emptyList();
		}
		
		// Wenn der Spieler wartende Figuren auf seinen Startfeldern hat,
		// dann muss das Start-Streckenfeld freigemacht werden, wenn möglich.
		if(!spieler.getStartFeldFigurenMenge().isEmpty()) {
			List<SpielZug> moeglicheZuegeStartStreckenFeld = moeglicheZuege.stream().filter(zug ->
				zug.getZugFeld().getFeldArt() == FeldArt.STRECKEN_FELD
				&& zug.getZugFeld().getFeldPosition() == spieler.getStartStreckenFeld().getFeldPosition()
			).collect(Collectors.toList());
			if(!moeglicheZuegeStartStreckenFeld.isEmpty()) {
				return moeglicheZuegeStartStreckenFeld;
			}
		}
		
		if(wuerfelErgebnis != WuerfelErgebnis.SECHS) {
			return moeglicheZuege;
		}

		// Wenn der Spieler wartende Figuren auf seinen Startfeldern hat,
		// müssen sie zuerst auf das Start-Streckenfeld gespielt werden.
		List<SpielZug> moeglicheZuegeStartFelder = moeglicheZuege.stream().filter(zug ->
			zug.getZugFeld().getFeldArt() == FeldArt.START_FELD
		).collect(Collectors.toList());
		if(!moeglicheZuegeStartFelder.isEmpty()) {
			return moeglicheZuegeStartFelder;
		}
		
		return moeglicheZuege;
	}
	
	public void ausfuehrenSpielZug(SpielZug spielZug) {
		AktiverSpielerDekorator aktiverSpieler = aktivesSpiel.getAktiverSpieler();
		WuerfelErgebnis letztesWuerfelErgebnis = aktiverSpieler.getWuerfelErgebnis();
		
		Farbe aktiverSpielerFarbe = aktiverSpieler.getFarbe();

		AktiverSpielerTodoAktion todoAktion = aktiverSpieler.getAktuelleTodoAktion();
		if(todoAktion != AktiverSpielerTodoAktion.MUSS_ZIEHEN) {
			throw new IllegaleSpielerAktionException("der aktiver Spieler mit der Farbe " + aktiverSpielerFarbe.toPrettyString() + " darf jetzt nicht ziehen");
		}
		
		Figur zugFigur = spielZug.getZugFigur();
		if(!aktiverSpielerFarbe.equals(zugFigur.getFarbe())) {
			throw new IllegaleSpielerAktionException("der aktiver Spieler mit der Farbe " + aktiverSpielerFarbe.toPrettyString() + " darf nicht Figuren mit der Farbe " + zugFigur.getFarbe().toPrettyString() + " ziehen");
		}

		if(!letztesWuerfelErgebnis.equals(spielZug.getWuerfelErgebnis())) {
			throw new IllegaleSpielerAktionException("der aktiver Spieler mit der Farbe " + aktiverSpielerFarbe.toPrettyString() + " muss seine Figur um die zuletzt gewürfelte Würfelzahl ziehen lassen.");
		}
		
		aktiverSpieler.setHatGezogen(true);
		zugFigur.setSpielFeld(spielZug.getZielFeld());
		
		Figur schlagFigur = spielZug.getSchlagFigur();
		if(schlagFigur != null) {
			Spieler gegner = aktivesSpiel.getSpielerMenge()
				.stream()
				.filter(s -> schlagFigur.getFarbe().equals(s.getFarbe()))
				.findFirst()
				.get();
			
			List<Integer> startPositions = IntStream.range(0, Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER)
				.mapToObj(i -> i)
				.collect(Collectors.toList());
			startPositions.removeAll(
					gegner.getStartFeldFigurenMenge()
						.stream()
						.map(f -> f.getSpielFeld().getFeldPosition())
						.collect(Collectors.toList())
			);
			schlagFigur.setSpielFeld(SpielFeld.erstelleStartFeld(
					gegner.getFarbe(),
					startPositions.get(0)
			));
		}

		WuerfelErgebnis wuerfelErgebnis = aktiverSpieler.getWuerfelErgebnis();
		if(wuerfelErgebnis == WuerfelErgebnis.SECHS && aktiverSpieler.getZielFeldFigurenMenge().size() != Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER) {
			aktiverSpieler.setTodoAktion(AktiverSpielerTodoAktion.MUSS_WUERFELN);
		} else {
			aktiverSpieler.setTodoAktion(AktiverSpielerTodoAktion.FERTIG);
		}
		
		james.updateAktiverSpieler(aktiverSpieler);
		james.updateFigur(schlagFigur);
		james.updateFigur(zugFigur);
		
	}
}
