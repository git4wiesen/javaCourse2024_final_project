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

import java.io.Serial;
import java.util.Objects;
import java.util.Set;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.FremdSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;

/**
 * <pre>
 * 
 * Dekorator Pattern:
 * 
 * Dekoriert einen Spieler als aktiven Spieler.
 * 
 * Ein AktiverSpielerDekorator:
 * - ist ein Spieler.
 * - ist ein DatenbankElement mit eigenem Primärschlüssel.
 * - hat eine aktuelle TodoAktion.
 * - hat ein Würfelcounter, um 3 Mal würfeln zu können, wenn alle seine Figuren auf seinen Startfeldern sind.
 * - kann würfeln, wenn seine aktuelle TodoAktion gleich MUSS_WUERFELN ist.
 * - kann ziehen, wenn seine aktuelle TodoAktion gleich MUSS_ZIEHEN ist.
 * - kann einen anderen dekorierten Spieler setzen, wenn seine aktuelle TodoAtion gleich FERTIG ist.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class AktiverSpielerDekorator implements Spieler, DatenbankElement {
	/**
	 * die serial version UID dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = -2658036921647694399L;
	
	/**
	 * eigener Datenbank Schlüssel
	 */
	private Long schluessel;

	/**
	 * der Datenbank-Schluessel des SpielZustands, zudem der aktive Spieler gehört.
	 */
	private Long fremdSchluesselSpielZustand;
	
	/**
	 * der dekorierte Spieler
	 */
	private Spieler derSpieler;
	
	/**
	 * Gibt an, was der aktive Spieler als nächstes tuen muss.
	 */
	private AktiverSpielerTodoAktion todoAktion;
	
	/**
	 * Gibt an, welches WuerfelErgebnis der aktive Spieler zuletzt hatte.
	 */
	private WuerfelErgebnis wuerfelErgebnis;
	
	/**
	 * Gibt an, wie oft der aktive Spieler seit er aktiv ist, schon gewürfelt hat.
	 */
	private int wuerfelVersuchsAnzahl;
	
	/**
	 * Gibt an, ob der aktive Spieler schon gezogen hat.
	 */
	private boolean gezogen;

	/**
	 * <pre>
	 * 
	 * 
	 * @param derSpieler
	 * @param aktuelleTodoAktion
	 * @param wuerfelErgebnis
	 * @param wuerfelVersuchsAnzahl
	 * 
	 * </pre>
	 */
	private AktiverSpielerDekorator(
			Spieler derSpieler,
			AktiverSpielerTodoAktion aktuelleTodoAktion,
			WuerfelErgebnis wuerfelErgebnis,
			int wuerfelVersuchsAnzahl,
			boolean gezogen
	) {
		this.derSpieler = derSpieler;
		this.todoAktion = aktuelleTodoAktion;
		this.wuerfelErgebnis = wuerfelErgebnis;
		this.wuerfelVersuchsAnzahl = wuerfelVersuchsAnzahl;
		this.gezogen = gezogen;
	}
	
	/**
	 * 
	 * <pre>
	 * 
	 * Erstellt die aktive Version des übergebenen Spielers
	 * @param derSpieler der zu dekorierende, konkrete Spieler
	 * @param aktuelleTodoAktion die aktuelle Todo-Aktion des Spielers
	 * @param wuerfelErgebnis das letzte Würfel-Ergebnis
	 * @param wuerfelCount Anzahl der Würfel-Versuche, seit der Spieler aktiv ist
	 * @return der dekorierte aktive Spieler
	 * @throws NullPointerException wenn einer der Paramter derzeit nicht null sein darf
	 * 
	 * </pre>
	 */
	public static AktiverSpielerDekorator erstelleAktivenSpieler(
			KonkreterSpieler derSpieler,
			AktiverSpielerTodoAktion aktuelleTodoAktion,
			WuerfelErgebnis wuerfelErgebnis,
			int wuerfelVersuchsAnzahl,
			boolean gezogen
	) {
		if(derSpieler == null) {
			throw new NullPointerException("der Spieler darf nicht null sein");
		}
		if(aktuelleTodoAktion == null) {
			throw new NullPointerException("die aktuelle Todo-Aktion darf nicht null sein");
		}
		if(wuerfelErgebnis == null && aktuelleTodoAktion != AktiverSpielerTodoAktion.MUSS_WUERFELN) {
			throw new NullPointerException("wuerfelErgebnis darf nicht null sein, wenn todo aktion gleich '" + aktuelleTodoAktion + "' ist");
		}
		if(wuerfelVersuchsAnzahl < 0 || wuerfelVersuchsAnzahl >= 4) {
			throw new IllegalArgumentException("der Würfel count muss zwischen 0 (inklusive) und 4 (exklusive) liegen.");
		}
		return new AktiverSpielerDekorator(derSpieler, aktuelleTodoAktion, wuerfelErgebnis, wuerfelVersuchsAnzahl, gezogen);
	}

	@Override
	public Long getSchluessel() {
		return schluessel;
	}
	
	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("der aktive Spieler ist schon in einer Datenbank");
		}
		this.schluessel = schluessel;
	}
	
	@Override
	public Long getFremdSchluesselSpielZustand() {
		return fremdSchluesselSpielZustand;
	}
	
	@Override
	public void setFremdSchluesselSpielZustand(long fremdSchluessel) {
		if(this.schluessel != null) {
			throw new FremdSchluesselException("aktiver Spieler gehört schon zu einem SpielZustand");
		}
		fremdSchluesselSpielZustand  = fremdSchluessel;
	}

	/**
	 * <pre>
	 * 
	 * @return the derSpieler
	 * 
	 * </pre>
	 */
	public Spieler getDerSpieler() {
		return derSpieler;
	}

	/**
	 * <pre>
	 * 
	 * @param derSpieler the derSpieler to set
	 * @throws IllegaleSpielerAktionException wenn der derzeit aktive Spieler nicht fertig ist.
	 * 
	 * </pre>
	 */
	public void naechsterAktiverSpieler(Spieler derSpieler) {
		this.todoAktion = AktiverSpielerTodoAktion.MUSS_WUERFELN;
		this.wuerfelErgebnis = null;
		this.wuerfelVersuchsAnzahl = 0;
		this.gezogen = false;
		this.derSpieler = derSpieler;
	}

	/**
	 * <pre>
	 * 
	 * @return die aktuell erlaubte Aktion
	 * 
	 * </pre>
	 */
	public AktiverSpielerTodoAktion getAktuelleTodoAktion() {
		return todoAktion;
	}
	
	/**
	 * <pre>
	 * 
	 * @return the letztesWuerfelErgebnis
	 * 
	 * </pre>
	 */
	public WuerfelErgebnis getWuerfelErgebnis() {
		return wuerfelErgebnis;
	}
	
	/**
	 * <pre>
	 * 
	 * @return the wuerfelCount
	 * 
	 * </pre>
	 */
	public int getWuerfelVersuchsAnzahl() {
		return wuerfelVersuchsAnzahl;
	}
	
	public boolean isHatGezogen() {
		return gezogen;
	}

	/**
	 * <pre>
	 * 
	 * @param wuerfeln
	 * 
	 * </pre>
	 */
	public void setWuerfelErgebnis(WuerfelErgebnis wuerfelErgebnis) {
		this.wuerfelErgebnis = wuerfelErgebnis;
		this.wuerfelVersuchsAnzahl++;
	}

	/**
	 * <pre>
	 * 
	 * @param mussZiehen
	 * 
	 * </pre>
	 */
	public void setTodoAktion(AktiverSpielerTodoAktion todoAktion) {
		this.todoAktion = todoAktion;
	}
	
	/**
	 * <pre>
	 * 
	 * @param hatGezogen
	 * 
	 * </pre>
	 */
	public void setHatGezogen(boolean gezogen) {
		this.gezogen = gezogen;
	}
	
	/* --------------------------------------------------------------- */
	// ---- Spieler Implementation - Anfang
	// ----
	// ---- leite die Aufrufe von Spieler Methoden an
	// ---- den dekorierten Spieler weiter
	/* --------------------------------------------------------------- */

	@Override
	public String getName() {
		return derSpieler.getName();
	}

	@Override
	public Farbe getFarbe() {
		return derSpieler.getFarbe();
	}

	@Override
	public SpielFeld getStartStreckenFeld() {
		return derSpieler.getStartStreckenFeld();
	}

	@Override
	public SpielFeld getEndStreckenFeld() {
		return derSpieler.getEndStreckenFeld();
	}
	
	@Override
	public Set<Figur> getFigurenMenge() {
		return derSpieler.getFigurenMenge();
	}

	@Override
	public Set<Figur> getStartFeldFigurenMenge() {
		return derSpieler.getStartFeldFigurenMenge();
	}

	@Override
	public Set<Figur> getStreckenFeldFigurenMenge() {
		return derSpieler.getStreckenFeldFigurenMenge();
	}

	@Override
	public Set<Figur> getZielFeldFigurenMenge() {
		return derSpieler.getZielFeldFigurenMenge();
	}
	
	@Override
	public Integer getGewinnerPosition() {
		return derSpieler.getGewinnerPosition();
	}
	
	@Override
	public void setGewinnerPosition(int position) {
		derSpieler.setGewinnerPosition(position);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(derSpieler, gezogen, schluessel, todoAktion, wuerfelErgebnis, wuerfelVersuchsAnzahl);
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
		AktiverSpielerDekorator other = (AktiverSpielerDekorator) obj;
		return Objects.equals(derSpieler, other.derSpieler) && gezogen == other.gezogen
				&& Objects.equals(schluessel, other.schluessel) && todoAktion == other.todoAktion
				&& wuerfelErgebnis == other.wuerfelErgebnis && wuerfelVersuchsAnzahl == other.wuerfelVersuchsAnzahl;
	}
	
	@Override
	public boolean equalsDaten(DatenbankElement element) {
		if (this == element) {
			return true;
		}
		if (element == null) {
			return false;
		}
		if (getClass() != element.getClass()) {
			return false;
		}
		AktiverSpielerDekorator other = (AktiverSpielerDekorator) element;
		return Objects.equals(derSpieler, other.derSpieler) && gezogen == other.gezogen
				&& todoAktion == other.todoAktion && wuerfelErgebnis == other.wuerfelErgebnis
				&& wuerfelVersuchsAnzahl == other.wuerfelVersuchsAnzahl;
	}

	@Override
	public String toDisplayString() {
		return String.format("""
				《AktiverSpielerDekorator (schluessel: %1$1d):》
				《 - Dekorierter Spieler: %2$s》
				《 - Todo-Aktion: %3$s》
				《 - Würfel-Ergebnis: %4$s》
				《 - Anzahl der Würfel-Versuche: %5$d》
			""".replaceAll("(^|》)\\s*(《|$)", "\n").trim(),
			schluessel,
			derSpieler.toPrettyString(),
			todoAktion,
			wuerfelErgebnis,
			wuerfelVersuchsAnzahl
		);
	}

	@Override
	public String toString() {
		return "AktiverSpielerDekorator [schluessel=" + schluessel + ", spieler schluessel = " + derSpieler.getSchluessel()
				+ ", spieler name =" + derSpieler.getName() + ", spieler farbe =" + derSpieler.getFarbe() + ", spieler startBrettPosition = "
				+ derSpieler.getStartStreckenFeld() + ", spieler zielFeldFigurenListe =" + derSpieler.getZielFeldFigurenMenge()
				+ ", spieler endBrettPosition =" + derSpieler.getEndStreckenFeld() + ", spieler startFeldFigurenListe ="
				+ derSpieler.getStartFeldFigurenMenge() + ", spieler streckenFeldFigurenListe =" + derSpieler.getStreckenFeldFigurenMenge() 
				+ ", aktuelleTodoAktion=" + todoAktion + ", wuerfelErgebnis=" + wuerfelErgebnis
				+ ", wuerfelVersuchsAnzahl=" + wuerfelVersuchsAnzahl + "]";
	}

	@Override
	public String toPrettyString() {
		return "AktiverSpielerDekorator [schluessel= "+ schluessel + ", farbe=" + derSpieler.getFarbe().toPrettyString() + ", name=" + derSpieler.getName() + "]";
	}

	/* --------------------------------------------------------------- */
	// ---- Spieler Implementation - Ende
	/* --------------------------------------------------------------- */
	
	/**
	 * <pre>
	 * 
	 * Gibt an, welche Aktion vom aktiven Spieler als nächstes ausgeführt werden muss.
	 * @author Christian Alexander Wiesenäcker
	 * 
	 * </pre>
	 */
	public enum AktiverSpielerTodoAktion {
		MUSS_WUERFELN,
		MUSS_ZIEHEN,
		FERTIG
	}
}
