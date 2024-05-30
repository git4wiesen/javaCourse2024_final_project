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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.server;

import java.util.Set;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;

/**
 * <pre>
 * 
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class RundenLaufMitHerausschlagenDaoMitServer implements RundenLaufMitHerausschlagenMitServerDao {

	/**
	 * <pre>
	 * 
	 * 
	 * @param user
	 * @param credentials
	 * 
	 * </pre>
	 */
	public RundenLaufMitHerausschlagenDaoMitServer(long user, long credentials) {
	}

	@Override
	public Set<Long> getSpielZustaendeSchluessel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SpielZustand> getSpielZustaendeMenge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpielZustand getSpielZustand(long schluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hinzufuegenSpielZustand(SpielZustand spielZustand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSpielZustand(SpielZustand spielZustand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenSpielZustand(long schluessel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenSpielZustand(SpielZustand zuLoeschen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getAktiveSpielerSchluessel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<AktiverSpielerDekorator> getAktiveSpielerMenge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AktiverSpielerDekorator getAktiverSpieler(long schluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hinzufuegenAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAktiverSpieler(AktiverSpielerDekorator aktiverSpieler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenAktiverSpieler(long schluessel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenAktiverSpieler(AktiverSpielerDekorator zuLoeschen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getSpielerSchluessel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Spieler> getSpielerMenge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Spieler> getSpielerMengeBySpielZustandSchluessel(long spielZustandSchluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Spieler getSpieler(long schluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hinzufuegenSpieler(Spieler spieler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSpieler(Spieler spieler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenSpieler(long schluessel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenSpieler(Spieler zuLoeschen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getFarbenSchluessel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Farbe> getFarbenMenge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Farbe getFarbe(long schluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hinzufuegenFarbe(Farbe farbe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFarbe(Farbe farbe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenFarbe(long schluessel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenFarbe(Farbe zuLoeschen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getFigurenSchluessel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Figur> getFigurenMenge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Figur> getFigurenMengeBySpielerSchluessel(long spielerSchluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Figur> getFigurenMengeBySpielZustandSchluessel(long spielZustandSchluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Figur getFigur(long schluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hinzufuegenFigur(Figur figur) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFigur(Figur figur) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenFigur(long schluessel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenFigur(Figur zuLoeschen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Long> getSpielFelderSchluessel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SpielFeld> getSpielFelderMenge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpielFeld getSpielFeld(long schluessel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hinzufuegenSpielFeld(SpielFeld spielFeld) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSpielFeld(SpielFeld spielFeld) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenSpielFeld(long schluessel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loeschenSpielFeld(SpielFeld zuLoeschen) {
		// TODO Auto-generated method stub
		
	}

}
