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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * <pre>
 * 
 * Definiert das Interface eines Spielers bei einem Runden-Lauf-Mit-Herausschlagen Spiel.
 * 
 * Ein Spieler ist serialisierbar und ein DatenbankElement.
 * 
 * Folgenden Werte müssen abrufbar sein:
 * - String name:
 * 		Der Name des Spielers
 * 
 * - Farbe farbe:
 * 		Spieler-Farbe im Spiel
 * 
 * - SpielBrettPosition startBrettPosition:
 * 		Die erste Position auf dem Spielfeld, wohin der Spieler seine Figuren als erstes hinsetzt,
 * 		wenn er eine Figur mit einer 6 aus seinem Haus herausgewürfelt hat.
 * 
 * - SpielBrettPosition endBrettPosition:
 * 		Die letzte Postion auf dem Spielbrett, welches direkt vor dem 1. Zielfeld steht.
 * 
 * - List<Figuren> startFeldFigurenListe:
 * 		Listet die Figuren des Spielers auf, die auf den Startfeldern des Spielers stehen
 * 
 * - List<Figuren> streckenFeldFigurenListe:
 * 		Listet die Figuren des Spielers auf, die auf den Spielfeldern stehen,
 * 		wo alle Spieler das Spielbrett durchlaufen und
 * 		wo sich alle Spieler gegenseitig zu den Startfeldern herausschlagen können.
 * 
 * - List<Figuren> zielFeldFigurenListe:
 * 		Listet die Figuren auf, die auf den Zielfeldern des Spielers stehen
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public interface Spieler
extends
	Serializable,
	DatenbankElement,
	WithDisplayString
{
	public static final int MAX_FIGUREN_ANZAHL_JE_SPIELER = 4;
	public static final int MAX_WUERFEL_VERSUCHE = 3;
	
	/**
	 * <pre>
	 * 
	 * Fremdschlüssel zum SpielZustand
	 * @return
	 * 
	 * </pre>
	 */
	Long getFremdSchluesselSpielZustand();
	
	/**
	 * <pre>
	 * 
	 * Setze den Fremdschlüssel zum SpielZustand
	 * @param fremdSchluessel
	 * 
	 * </pre>
	 */
	void setFremdSchluesselSpielZustand(long fremdSchluessel);
	
	/**
	 * <pre>
	 * 
	 * @return der Name des Spielers
	 * 
	 * </pre>
	 */
	String getName();
	
	/**
	 * <pre>
	 * 
	 * @return die Spielfarbe des Spielers
	 * 
	 * </pre>
	 */
	Farbe getFarbe();
	
	/**
	 * <pre>
	 * 
	 * @return
	 * 		das erste, farbige, Strecken-Feld,
	 * 		wohin der Spieler die Figuren aus seinen StartFeldern
     * 		mit einer 6 herauswürfeln kann.
	 * 
	 * </pre>
	 */
	SpielFeld getStartStreckenFeld();
	
	/**
	 * <pre>
	 * 
	 * @return
	 * 		die letzte, farblose, Strecken-Feld SpielBrettPosition,
	 * 		die vor den Zielfeld SpielBrettPositionen des Spielers liegt.
	 * 
	 * </pre>
	 */
	SpielFeld getEndStreckenFeld();
	
	/**
	 * <pre>
	 * 
	 * @return Menge der Figuren des Spielers
	 * 
	 * </pre>
	 */
	Set<Figur> getFigurenMenge();
	
	/**
	 * <pre>
	 * 
	 * @return Liste der Figuren des Spielers, die derzeit auf den Start-Feldern des Spielers stehen.
	 * 
	 * </pre>
	 */
	Set<Figur> getStartFeldFigurenMenge();
	
	/**
	 * <pre>
	 * 
	 * @return eine Liste der Figuren des Spielers, die derzeit auf den Strecken-Feldern des Spielbretts stehen.
	 * 
	 * </pre>
	 */
	Set<Figur> getStreckenFeldFigurenMenge();
	
	/**
	 * <pre>
	 * 
	 * @return eine Liste der Figuren des Spielers, die derzeit auf den Ziel-Feldern des Spielers stehen.
	 * 
	 * </pre>
	 */
	Set<Figur> getZielFeldFigurenMenge();
	
	/**
	 * <pre>
	 * 
	 * Gibt die Gewinner-Position dieses Spielers zurück.
	 * 
	 * null == kein Gewinner
	 * 0 == erster Gewinner
	 * 1 == zweiter Gewinner
	 * 
	 * @return die Gewinner-Position oder null
	 * 
	 * <pre>
	 */
	Integer getGewinnerPosition();

	/**
	 * Setzt die Gewinner-Position
	 * @param position
	 */
	void setGewinnerPosition(int position);
	
	/**
	 * <pre>
	 * 
	 * @return ein String zur Darstelling in der Konsole
	 * 
	 * </pre>
	 */
	String toDisplayString();
	
	/**
	 * <pre>
	 * 
	 * @return eine schöne kurze Darstellung
	 * 
	 * </pre>
	 */
	String toPrettyString();

}
