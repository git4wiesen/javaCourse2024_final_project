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
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;

/**
 * <pre>
 * 
 * Stellt den SpielZustand des Runden-Lauf-Mit-Herausschlagen Spiels dar.
 * 
 * Ein SpielZustand:
 * - ist serialisierbar
 * - ist ein DatenbankElement
 * - hat einen spielStart (Datum + Uhrzeit)
 * - hat einen spielEnde (Datum + Uhrzeit), sobald das Spiel fertig ist
 * - hat zu jeder Zeit einen aktiven Spieler
 * - hat eine Liste aller Spieler, deren Reihenfolge die Spieler-Reihenfolge als aktiver Spieler bestimmt.
 * - hat eine Liste aller Spielfiguren auf dem Brett
 * - kann mit gegebener Figur und wuerfelErgebnis das Ziel-Feld bestimmen
 * - kann mit gegebener Figur und wuerfelErgebnis eine mögliche geschlagene Figur bestimmen
 * - kann mit gegebener Figur und wuerfelErgebnis eine Figur, wenn möglich,
 *   auf das Ziel-Feld setzen und eine mögliche geschlagene Figur auf ein Start-Feld des Spielers umsetzen.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class SpielZustand implements Serializable, DatenbankElement {
	/**
	 * Die Serial Version dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = -4110955064738420074L;

	/**
	 * der Datenbank-Schluessel des Spiel-Zustands in einer eventuell genutzten Datenbank
	 */
	private Long schluessel;

	/**
	 * der Zeitpunkt, wo das Spiel gestartet wurde.
	 */
	private LocalDateTime spielStart;

	/**
	 * der Zeitpunkt, wo das Spiel beendet wurde / wo das Spiel fertig ist.
	 */
	private LocalDateTime spielEnde;
	
	/**
	 * die Anzahl der Spieler-Positionen
	 */
	private final int anzahlSpielerPositionen;

	/**
	 * die Anzahl der konkreten Spieler
	 */
	private final int anzahlKonkreterSpieler;
	
	/**
	 * <pre>
	 * 
	 * die Anzahl der Streckenfelder von einem Start-Streckenfeld einer Spieler-Position
	 * zum nächsten Start-Streckenfeld der nächsten Spieler-Position.
	 * 
	 * </pre>
	 */
	private final int spielerOffset;
	
	/**
	 * der aktive Spieler, der zur Zeit dran ist.
	 */
	private final AktiverSpielerDekorator aktiverSpieler;
	
	/**
	 * Die Menge aller Spieler deren Reihenfolge, die Reihenfolge der Spieler als aktivenSpieler bestimmt.
	 */
	private final Set<Spieler> alleSpieler;
	
	/**
	 * Die Menge aller Figuren auf dem Spielbrett.
	 */
	private final Set<Figur> alleFiguren;
	
	/**
	 * <pre>
	 * 
	 * @param schluessel
	 * @param spielStart
	 * @param spielEnde
	 * @param aktiverSpieler
	 * @param spielerListe
	 * @param gewinnerListe
	 * @param alleFiguren
	 * 
	 * </pre>
	 */
	public SpielZustand(
			AktiverSpielerDekorator aktiverSpieler,
			Set<Spieler> alleSpieler,
			Set<Figur> alleFiguren,
			int anzahlSpielerPositionen,
			int anzahlKonkreterSpieler,
			int spielerOffset
	) {
		this.aktiverSpieler = aktiverSpieler;
		this.alleSpieler = alleSpieler;
		this.alleFiguren = alleFiguren;
		this.anzahlSpielerPositionen = anzahlSpielerPositionen;
		this.anzahlKonkreterSpieler = anzahlKonkreterSpieler;
		this.spielerOffset = spielerOffset;
	}

	@Override
	public Long getSchluessel() {
		return schluessel;
	}

	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("der Spiel Zustand ist schon in einer Datenbank");
		}
		this.schluessel = schluessel;
	}
	
	/**
	 * @return the anzahlSpielerPositionen
	 */
	public int getAnzahlSpielerPositionen() {
		return anzahlSpielerPositionen;
	}

	/**
	 * @return the anzahlKonkreterSpieler
	 */
	public int getAnzahlKonkreterSpieler() {
		return anzahlKonkreterSpieler;
	}

	/**
	 * @return the spielerOffset
	 */
	public int getSpielerOffset() {
		return spielerOffset;
	}
	
	/**
	 * @return the anzahlStreckenFelder
	 */
	public int getAnzahlStreckenFelder() {
		return anzahlSpielerPositionen * spielerOffset;
	}

	/**
	 * <pre>
	 * 
	 * @return the spielStart
	 * 
	 * </pre>
	 */
	public LocalDateTime getSpielStart() {
		return spielStart;
	}

	/**
	 * <pre>
	 * 
	 * @return the spielEnde
	 * 
	 * </pre>
	 */
	public LocalDateTime getSpielEnde() {
		return spielEnde;
	}

	/**
	 * <pre>
	 * 
	 * @return the aktiverSpieler
	 * 
	 * </pre>
	 */
	public AktiverSpielerDekorator getAktiverSpieler() {
		return aktiverSpieler;
	}

	/**
	 * <pre>
	 * 
	 * @return the alleSpieler
	 * 
	 * </pre>
	 */
	public Set<Spieler> getSpielerMenge() {
		return alleSpieler;
	}
	
	/**
	 * <pre>
	 * 
	 * @return the gewinnerListe
	 * 
	 * </pre>
	 */
	public List<Spieler> getGewinnerListe() {
		List<Spieler> gewinner = getSpielerMenge()
				.stream()
				.filter(s -> s.getGewinnerPosition() != null)
				.collect(Collectors.toList());
		Collections.sort(gewinner, (a, b) -> 
			a.getGewinnerPosition().compareTo(b.getGewinnerPosition())
		);
		return gewinner;
	}

	/**
	 * <pre>
	 * 
	 * @return the alleFiguren
	 * 
	 * </pre>
	 */
	public Set<Figur> getFigurenMenge() {
		return alleFiguren;
	}
	
	/**
	 * <pre>
	 * 
	 * @param spielStart the spielStart to set
	 * 
	 * </pre>
	 */
	public void setSpielStart(LocalDateTime spielStart) {
		this.spielStart = spielStart;
	}
	
	/**
	 * <pre>
	 * 
	 * @param spielEnde the spielEnde to set
	 * 
	 * </pre>
	 */
	public void setSpielEnde(LocalDateTime spielEnde) {
		this.spielEnde = spielEnde;
	}

//	@Override
//	public int hashCode() {
//		return Objects.hash(aktiverSpieler, alleFiguren, anzahlKonkreterSpieler, anzahlSpielerPositionen, schluessel,
//				spielEnde, spielStart, spielerListe, spielerOffset);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		}
//		if (obj == null) {
//			return false;
//		}
//		if (getClass() != obj.getClass()) {
//			return false;
//		}
//		SpielZustand other = (SpielZustand) obj;
//		return Objects.equals(aktiverSpieler, other.aktiverSpieler) && Objects.equals(alleFiguren, other.alleFiguren)
//				&& anzahlKonkreterSpieler == other.anzahlKonkreterSpieler
//				&& anzahlSpielerPositionen == other.anzahlSpielerPositionen
//				&& Objects.equals(schluessel, other.schluessel) && Objects.equals(spielEnde, other.spielEnde)
//				&& Objects.equals(spielStart, other.spielStart) && Objects.equals(spielerListe, other.spielerListe)
//				&& spielerOffset == other.spielerOffset;
//	}
	
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
		SpielZustand other = (SpielZustand) element;
		return Objects.equals(aktiverSpieler, other.aktiverSpieler) && Objects.equals(alleFiguren, other.alleFiguren)
				&& anzahlKonkreterSpieler == other.anzahlKonkreterSpieler
				&& anzahlSpielerPositionen == other.anzahlSpielerPositionen
				&& Objects.equals(spielEnde, other.spielEnde) && Objects.equals(spielStart, other.spielStart)
				&& Objects.equals(alleSpieler, other.alleSpieler) && spielerOffset == other.spielerOffset;
	}

	public String toDisplayString() {
		List<Spieler> konkreteSpieler = alleSpieler
				.stream()
				.filter(s -> s instanceof KonkreterSpieler)
				.collect(Collectors.toList());

		String spielerString = "";
		
		String spielerReihenfolge = konkreteSpieler
				.stream()
				.map(s -> s.toPrettyString())
				.collect(Collectors.joining("\n\t -> ", "\n\t ", ""));
		
		String spielerPositionen = alleSpieler
				.stream()
				.map(s -> s.toPrettyString())
				.collect(Collectors.joining("\n\t -> ", "\n\t", ""));
		
		String gewinnerString = "";
		String figurenString = "";
		return String.format("""
					《SpielZustand (schluessel: %1$10d):》
					《 - Spiel-Start: %2$s》
					《 - Spiel-Ende: %3$s》
					《 - Max Spieleranzahl auf dem Brett: %4$d》
					《 - Spieler (Anzahl: %5$d)%11$s》
					《 - Spieler Reihenfolge: %6$s》
					《 - Spieler-Positionen: %7$s》
					《 - Gewinner (Anzahl: %8$d)%12$s》
					《 - Figuren (Anzahl: %9$d)%13$s》
					《%10$s》
				""".replaceAll("(^|》)\\s*(《|$)", "\n").trim(),
				schluessel,
				spielStart,
				spielEnde,
				alleSpieler.size(),
				konkreteSpieler.size(),
				spielerReihenfolge,
				spielerPositionen,
				getGewinnerListe().size(),
				alleFiguren.size(),
				aktiverSpieler.toString()
					.lines()
					.collect(Collectors.joining("\n\t", " - ", "")),
				spielerString,
				gewinnerString,
				figurenString
		);
	}

	@Override
	public String toString() {
		return "SpielZustand [schluessel=" + schluessel + ", spielStart=" + spielStart + ", spielEnde=" + spielEnde
				+ ", aktiverSpieler=" + aktiverSpieler + ", alleSpieler=" + alleSpieler + ", gewinnerListe="
				+ getGewinnerListe() + ", alleFiguren=" + alleFiguren + "]";
	}
}
