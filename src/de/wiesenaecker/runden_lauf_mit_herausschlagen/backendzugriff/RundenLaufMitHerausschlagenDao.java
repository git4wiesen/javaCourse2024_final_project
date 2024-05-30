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
public interface RundenLaufMitHerausschlagenDao {
	// ******************************************************************************************
	// **** Start Access / Modify data
	// ******************************************************************************************
	
	/**
	 * <pre>
	 * 
	 * Holt alle Schlüssel der vorhandenen Spiel-Zustände aus der Datenhaltung.
	 * 
	 * @return Liste der Schlüssel aller Spiel-Zustände
	 * 
	 * </pre>
	 */
	Set<Long> getSpielZustaendeSchluessel();
	
	/**
	 * <pre>
	 * 
	 * Holt alle Spiel-Zustände aus der Datenhaltung.
	 * 
	 * @param schluessel
	 * @return Liste aller Spiel-Zustände
	 * 
	 * </pre>
	 */
	Set<SpielZustand> getSpielZustaendeMenge();

	/**
	 * <pre>
	 * 
	 * Holt den Spiel-Zustände mit dem Schluessel schluessel aus der Datenhaltung.
	 * 
	 * @param schluessel
	 * @return den SpielZustand
	 * 
	 * </pre>
	 */
	SpielZustand getSpielZustand(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Speichert einen neuen SpielZustand in der Datenhaltung.
	 * @param spielZustand der neue SpielZustand
	 * @throws PrimaerSchluesselException wenn das Element schon in einer Datenbank ist
	 * 
	 * </pre>
	 */
	void hinzufuegenSpielZustand(SpielZustand spielZustand);
	
	/**
	 * <pre>
	 * 
	 * Updated einen spezifischen SpielZustand in der Datenhaltung.
	 * @param spielZustand der SpielZustand
	 * @throws PrimaerSchluesselException wenn das Element nicht der Datenbank war
	 * 
	 * </pre>
	 */
	void updateSpielZustand(SpielZustand spielZustand);
	
	/**
	 * <pre>
	 * 
	 * Löscht einen spezifischen SpielZustand aus der Datenhaltung.
	 * @param schluessel der Schlüssel des SpielZustands
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenSpielZustand(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Löscht den übergebenen SpielZustand aus der Datenhaltung.
	 * @param zuLoeschen der zu löschende SpielZustand
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenSpielZustand(SpielZustand zuLoeschen);

	/**
	 * <pre>
	 * 
	 * Holt alle Schlüssel der vorhandenen, aktiven Spieler aus der Datenhaltung.
	 * 
	 * @return Liste der Schlüssel aller aktiven Spieler
	 * 
	 * </pre>
	 */
	Set<Long> getAktiveSpielerSchluessel();
	
	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten aktiven Spieler zurück.
	 * @return die aktiven Spieler
	 * 
	 * </pre>
	 */
	Set<AktiverSpielerDekorator> getAktiveSpielerMenge();
	
	/**
	 * <pre>
	 * 
	 * Holt den aktiven Spieler mit dem Schluessel schluessel aus der Datenhaltung.
	 * 
	 * @param schluessel
	 * @return der aktive Spieler
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	AktiverSpielerDekorator getAktiverSpieler(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Speichert einen neuen aktiven Spieler in der Datenhaltung.
	 * @param aktiverSpieler
	 * @throws PrimaerSchluesselException wenn das Element schon in einer Datenbank ist
	 * 
	 * </pre>
	 */
	void hinzufuegenAktiverSpieler(AktiverSpielerDekorator aktiverSpieler);
	
	/**
	 * <pre>
	 * 
	 * Updated einen spezifischen aktiven Spieler in der Datenhaltung.
	 * @param aktiverSpieler der aktive Spieler
	 * @throws PrimaerSchluesselException wenn das Element nicht der Datenbank war
	 * 
	 * </pre>
	 */
	void updateAktiverSpieler(AktiverSpielerDekorator aktiverSpieler);
	
	/**
	 * <pre>
	 * 
	 * Löscht einen spezifischen aktiven Spieler aus der Datenhaltung.
	 * @param schluessel der Schlüssel des aktiven Spielers 
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenAktiverSpieler(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Löscht den übergebenen aktiven Spieler aus der Datenhaltung.
	 * @param zuLoeschen der zu löschende aktive Spieler
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenAktiverSpieler(AktiverSpielerDekorator zuLoeschen);

	/**
	 * <pre>
	 * 
	 * Holt alle Schlüssel der vorhandenen Spieler aus der Datenhaltung.
	 * 
	 * @return Liste der Schlüssel aller Spieler
	 * 
	 * </pre>
	 */
	Set<Long> getSpielerSchluessel();
	
	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten konkreten / vakanten Spieler zurück.
	 * @return die Spieler
	 * 
	 * </pre>
	 */
	Set<Spieler> getSpielerMenge();
	
	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten konkreten / vakanten Spieler zurück, die zu einem bestimmten SpielZustand gehören.
	 * @param spielZustandSchluessel der Schlüssel zum SpielZustand
	 * @return
	 * 
	 * </pre>
	 */
	Set<Spieler> getSpielerMengeBySpielZustandSchluessel(long spielZustandSchluessel);
	
	/**
	 * <pre>
	 * 
	 * Holt den Spieler mit dem Schluessel schluessel aus der Datenhaltung.
	 * 
	 * @param schluessel
	 * @return der Spieler
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	Spieler getSpieler(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Speichert einen neuen Spieler in der Datenhaltung.
	 * @param aktiverSpieler
	 * @throws PrimaerSchluesselException wenn das Element schon in einer Datenbank ist
	 * @throws IllegalArgumentException wenn spieler vom Typ AktiverSpielerDekorator ist
	 * 
	 * </pre>
	 */
	void hinzufuegenSpieler(Spieler spieler);
	
	/**
	 * <pre>
	 * 
	 * Updated einen spezifischen spieler in der Datenhaltung.
	 * @param spieler der spieler
	 * @throws PrimaerSchluesselException wenn das Element nicht der Datenbank war
	 * @throws IllegalArgumentException wenn spieler vom Typ AktiverSpielerDekorator ist
	 * 
	 * </pre>
	 */
	void updateSpieler(Spieler spieler);
	
	/**
	 * <pre>
	 * 
	 * Löscht einen spezifischen Spieler aus der Datenhaltung.
	 * @param schluessel der Schlüssel des Spielers 
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenSpieler(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Löscht den übergebenen konkreten / vakanten Spieler aus der Datenhaltung.
	 * @param zuLoeschen der zu löschende konkrete / vakante Spieler
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * @throws IllegalArgumentException wenn der übergebene Spieler ein aktiver Spieler ist
	 * 
	 * </pre>
	 */
	void loeschenSpieler(Spieler zuLoeschen);

	/**
	 * <pre>
	 * 
	 * Holt alle Schlüssel der vorhandenen Farben aus der Datenhaltung.
	 * 
	 * @return Liste der Schlüssel aller Farben
	 * 
	 * </pre>
	 */
	Set<Long> getFarbenSchluessel();
	
	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten Farben zurück.
	 * @return die Farben
	 * 
	 * </pre>
	 */
	Set<Farbe> getFarbenMenge();
	
	/**
	 * <pre>
	 * 
	 * Holt die Farbe mit dem Schluessel schluessel aus der Datenhaltung.
	 * 
	 * @param schluessel
	 * @return die Farbe
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	Farbe getFarbe(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Speichert eine neue Farbe in der Datenhaltung.
	 * @param farbe die neue Farbe
	 * @throws PrimaerSchluesselException wenn das Element schon in einer Datenbank ist
	 * 
	 * </pre>
	 */
	void hinzufuegenFarbe(Farbe farbe);
	
	/**
	 * <pre>
	 * 
	 * Updated eine spezifische Farbe in der Datenhaltung.
	 * @param farbe die Farbe
	 * @throws PrimaerSchluesselException wenn das Element nicht der Datenbank war
	 * 
	 * </pre>
	 */
	void updateFarbe(Farbe farbe);
	
	/**
	 * <pre>
	 * 
	 * Löscht eine spezifische Farbe aus der Datenhaltung.
	 * @param schluessel der Schlüssel des aktiven Spielers 
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenFarbe(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Löscht die übergebenen Farbe aus der Datenhaltung.
	 * @param zuLoeschen die zu löschende Farbe
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenFarbe(Farbe zuLoeschen);

	/**
	 * <pre>
	 * 
	 * Holt alle Schlüssel der vorhandenen Figuren aus der Datenhaltung.
	 * 
	 * @return Liste der Schlüssel aller Figuren
	 * 
	 * </pre>
	 */
	Set<Long> getFigurenSchluessel();

	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten Figuren zurück.
	 * @return die Figuren
	 * 
	 * </pre>
	 */
	Set<Figur> getFigurenMenge();
	
	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten Figuren eines Spielers mit spielerSchluessel zurück.
	 * @param spielerSchluessel
	 * @return
	 * 
	 * </pre>
	 */
	Set<Figur> getFigurenMengeBySpielerSchluessel(long spielerSchluessel);

	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten Figuren eines SpielZustands mit spielZustandSchluessel zurück.
	 * @param spielZustandSchluessel
	 * @return
	 * 
	 * </pre>
	 */
	Set<Figur> getFigurenMengeBySpielZustandSchluessel(long spielZustandSchluessel);
	
	/**
	 * <pre>
	 * 
	 * Holt die Figur mit dem Schluessel schluessel aus der Datenhaltung.
	 * 
	 * @param schluessel
	 * @return die Figur
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	Figur getFigur(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Speichert eine neue Figur in der Datenhaltung.
	 * @param figur die neue Figur
	 * 
	 * </pre>
	 */
	void hinzufuegenFigur(Figur figur);
	
	/**
	 * <pre>
	 * 
	 * Updated eine spezifische Figur in der Datenhaltung.
	 * @param figur die Figur
	 * @throws PrimaerSchluesselException wenn das Element nicht der Datenbank war
	 * 
	 * </pre>
	 */
	void updateFigur(Figur figur);
	
	/**
	 * <pre>
	 * 
	 * Löscht eine spezifische Figur aus der Datenhaltung.
	 * @param schluessel der Schlüssel des aktiven Spielers 
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenFigur(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Löscht die übergebenen Figur aus der Datenhaltung.
	 * @param zuLoeschen die zu löschende Figur
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenFigur(Figur zuLoeschen);

	/**
	 * <pre>
	 * 
	 * Holt alle Schlüssel der vorhandenen SpielFelder aus der Datenhaltung.
	 * 
	 * @return Liste der Schlüssel aller SpielFelder
	 * 
	 * </pre>
	 */
	Set<Long> getSpielFelderSchluessel();

	/**
	 * <pre>
	 * 
	 * Gibt alle gespeicherten SpielFelder zurück.
	 * @return die SpielFelder
	 * 
	 * </pre>
	 */
	Set<SpielFeld> getSpielFelderMenge();
	
	/**
	 * <pre>
	 * 
	 * Holt das SpielFeld mit dem Schluessel schluessel aus der Datenhaltung.
	 * 
	 * @param schluessel
	 * @return das SpielFeld
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	SpielFeld getSpielFeld(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Speichert ein neues SpielFeld in der Datenhaltung.
	 * @param spielFeld das neue SpielFeld
	 * @throws PrimaerSchluesselException wenn das Element schon in einer Datenbank ist
	 * 
	 * </pre>
	 */
	void hinzufuegenSpielFeld(SpielFeld spielFeld);
	
	/**
	 * <pre>
	 * 
	 * Updated ein spezifisches SpielFeld in der Datenhaltung.
	 * @param spielFeld das SpielFeld
	 * @throws PrimaerSchluesselException wenn das Element nicht der Datenbank war
	 * 
	 * </pre>
	 */
	void updateSpielFeld(SpielFeld spielFeld);
	
	/**
	 * <pre>
	 * 
	 * Löscht ein spezifisches SpielFeld aus der Datenhaltung.
	 * @param schluessel der Schlüssel des SpielFelds
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenSpielFeld(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Löscht das übergebene SpielFeld aus der Datenhaltung.
	 * @param zuLoeschen das zu löschende SpielFeld
	 * @throws PrimaerSchluesselException wenn das Element nicht in der Datenbank war
	 * 
	 * </pre>
	 */
	void loeschenSpielFeld(SpielFeld zuLoeschen);
}
