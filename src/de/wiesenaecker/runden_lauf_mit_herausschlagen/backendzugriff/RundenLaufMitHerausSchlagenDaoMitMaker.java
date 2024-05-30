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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff;

import java.util.Set;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.RundenLaufMitHerausSchlagenMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.DatenbankElement;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;

/**
 * <pre>
 * 
 * Implementiert das RundenLaufMitRausSchlagenDao Interface.
 * 
 * Greift auf den RundenLaufMitRausSchlagenMaker als Datenhaltung zu.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class RundenLaufMitHerausSchlagenDaoMitMaker implements RundenLaufMitHerausschlagenDao {
	/**
	 * Simulation einer dauerhaften Datenhalung
	 */
	private RundenLaufMitHerausSchlagenMaker datenhaltung;

	/**
	 * <pre>
	 * 
	 * Erstellt ein MenschAergerDichNichtDao mit einem zufällig gefüllten
	 * MenschAergerDichNichtMaker als Datenhaltung
	 * 
	 * </pre>
	 */
	public RundenLaufMitHerausSchlagenDaoMitMaker() {
		this.datenhaltung = new RundenLaufMitHerausSchlagenMaker(true);
	}

	/**
	 * <pre>
	 * 
	 * Erstellt ein MenschAergerDichNichtDao mit einem MenschAergerDichNichtMaker als Datenhaltung
	 * 
	 * </pre>
	 */
	public RundenLaufMitHerausSchlagenDaoMitMaker(RundenLaufMitHerausSchlagenMaker datenhaltung) {
		this.datenhaltung = datenhaltung;
	}

	@Override
	public Set<SpielZustand> getSpielZustaendeMenge() {
		return datenhaltung.getSpielZustaendeMenge();
	}

	@Override
	public SpielZustand getSpielZustand(long schluessel) {
		return datenhaltung.getSpielZustand(schluessel);
	}

	@Override
	public void hinzufuegenSpielZustand(SpielZustand spielZustand) {
		datenhaltung.hinzufuegenSpielZustand(spielZustand);
	}

	@Override
	public void updateSpielZustand(SpielZustand spielZustand) {
	}

	@Override
	public void loeschenSpielZustand(long schluessel) {
		datenhaltung.loeschenSpielZustand(schluessel);
	}
	
	@Override
	public void loeschenSpielZustand(SpielZustand zuLoeschen) {
		datenhaltung.loeschenSpielZustand(zuLoeschen);
	}

	@Override
	public Set<AktiverSpielerDekorator> getAktiveSpielerMenge() {
		return datenhaltung.getAktiveSpielerMenge();
	}

	@Override
	public AktiverSpielerDekorator getAktiverSpieler(long schluessel) {
		return datenhaltung.getAktiverSpieler(schluessel);
	}

	@Override
	public void hinzufuegenAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
		datenhaltung.hinzufuegenAktiverSpieler(aktiverSpieler);
	}

	@Override
	public void updateAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
	}

	@Override
	public void loeschenAktiverSpieler(long schluessel) {
		datenhaltung.loeschenAktiverSpieler(schluessel);
	}
	
	@Override
	public void loeschenAktiverSpieler(AktiverSpielerDekorator zuLoeschen) {
		datenhaltung.loeschenAktiverSpieler(zuLoeschen);
	}

	@Override
	public Set<Spieler> getSpielerMenge() {
		return datenhaltung.getSpielerMenge();
	}
	
	@Override
	public Set<Spieler> getSpielerMengeBySpielZustandSchluessel(long spielZustandSchluessel) {
		return datenhaltung.getSpielZustand(spielZustandSchluessel).getSpielerMenge();
	}

	@Override
	public Spieler getSpieler(long schluessel) {
		return datenhaltung.getSpieler(schluessel);
	}

	@Override
	public void hinzufuegenSpieler(Spieler spieler) {
		datenhaltung.hinzufuegenSpieler(spieler);
	}

	@Override
	public void updateSpieler(Spieler spieler) {
	}

	@Override
	public void loeschenSpieler(long schluessel) {
		datenhaltung.loeschenSpieler(schluessel);
	}
	
	@Override
	public void loeschenSpieler(Spieler zuLoeschen) {
		datenhaltung.loeschenSpieler(zuLoeschen);
	}

	@Override
	public Set<Farbe> getFarbenMenge() {
		return datenhaltung.getFarbenMenge();
	}

	@Override
	public Farbe getFarbe(long schluessel) {
		return datenhaltung.getFarbe(schluessel);
	}

	@Override
	public void hinzufuegenFarbe(Farbe farbe) {
		datenhaltung.hinzufuegenFarbe(farbe);
	}

	@Override
	public void updateFarbe(Farbe farbe) {
	}

	@Override
	public void loeschenFarbe(long schluessel) {
		datenhaltung.loeschenFarbe(schluessel);
	}
	
	@Override
	public void loeschenFarbe(Farbe zuLoeschen) {
		datenhaltung.loeschenFarbe(zuLoeschen);
	}

	@Override
	public Set<Figur> getFigurenMenge() {
		return datenhaltung.getFigurenMenge();
	}
	
	@Override
	public Set<Figur> getFigurenMengeBySpielZustandSchluessel(long spielZustandSchluessel) {
		return datenhaltung.getSpielZustand(spielZustandSchluessel).getFigurenMenge();
	}

	@Override
	public Set<Figur> getFigurenMengeBySpielerSchluessel(long spielerSchluessel) {
		return datenhaltung.getSpieler(spielerSchluessel).getFigurenMenge();
	}

	@Override
	public Figur getFigur(long schluessel) {
		return datenhaltung.getFigur(schluessel);
	}
	
	@Override
	public void hinzufuegenFigur(Figur figur) {
		datenhaltung.hinzufuegenFigur(figur);
	}

	@Override
	public void updateFigur(Figur figur) {
	}

	@Override
	public void loeschenFigur(long schluessel) {
		datenhaltung.loeschenFigur(schluessel);
	}
	
	@Override
	public void loeschenFigur(Figur zuLoeschen) {
		datenhaltung.loeschenFigur(zuLoeschen);
	}

	@Override
	public Set<SpielFeld> getSpielFelderMenge() {
		return datenhaltung.getSpielFelderMenge();
	}

	@Override
	public SpielFeld getSpielFeld(long schluessel) {
		return datenhaltung.getSpielFeld(schluessel);
	}

	@Override
	public void hinzufuegenSpielFeld(SpielFeld spielFeld) {
		datenhaltung.hinzufuegenSpielFeld(spielFeld);
	}

	@Override
	public void updateSpielFeld(SpielFeld spielFeld) {
	}

	@Override
	public void loeschenSpielFeld(long schluessel) {
		datenhaltung.loeschenSpielFeld(schluessel);
	}

	@Override
	public void loeschenSpielFeld(SpielFeld zuLoeschen) {
		datenhaltung.loeschenSpielFeld(zuLoeschen);
	}
	
	@Override
	public Set<Long> getSpielZustaendeSchluessel() {
		return getAlleSchluessel(datenhaltung.getSpielZustaendeMenge());
	}

	@Override
	public Set<Long> getAktiveSpielerSchluessel() {
		return getAlleSchluessel(datenhaltung.getAktiveSpielerMenge());
	}

	@Override
	public Set<Long> getSpielerSchluessel() {
		return getAlleSchluessel(datenhaltung.getSpielerMenge());
	}

	@Override
	public Set<Long> getFarbenSchluessel() {
		return getAlleSchluessel(datenhaltung.getFarbenMenge());
	}

	@Override
	public Set<Long> getFigurenSchluessel() {
		return getAlleSchluessel(datenhaltung.getFigurenMenge());
	}

	@Override
	public Set<Long> getSpielFelderSchluessel() {
		return getAlleSchluessel(datenhaltung.getSpielFelderMenge());
	}
	
	private <T extends DatenbankElement> Set<Long> getAlleSchluessel(Set<T> datenbankElementMenge) {
		return datenbankElementMenge.stream()
				.map(element -> element.getSchluessel())
				.filter(s -> s != null)
				.collect(Collectors.toSet());
	}
}
