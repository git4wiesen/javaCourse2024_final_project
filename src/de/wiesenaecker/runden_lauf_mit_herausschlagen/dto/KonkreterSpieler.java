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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.FremdSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld.FeldArt;

/**
 * <pre>
 * 
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class KonkreterSpieler implements Spieler, DatenbankElement {
	/**
	 * serial version UID dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = 6456104245192288877L;

	/**
	 * der Datenbank-Schluessel des Spielers in einer eventuell genutzten Datenbank
	 */
	private Long schluessel;

	/**
	 * der Datenbank-Schluessel des SpielZustands, zudem der konkrete Spieler gehört.
	 */
	private Long fremdSchluesselSpielZustand;
	
	/**
	 * der Name des Spielers
	 */
	private final String name;
	
	/**
	 * die Farbe des Spielers
	 */
	private final Farbe farbe;
	
	/**
	 * die Start-BrettPosition des Spielers
	 */
	private final SpielFeld startStreckenFeld;
	
	/**
	 * die End-BrettPosition des Spielers
	 */
	private final SpielFeld endStreckenFeld;
	
	/**
	 * Liste von Figuren des Spielers.
	 */
	private final Set<Figur> figurenMenge;
	
	/**
	 * Die Position in der Gewinner-Liste, sobald verfügbar, ansonsten null
	 */
	private Integer gewinnerPosition;

	/**
	 * <pre>
	 * 
	 * Erstellt einen konkreten Spieler
	 * @param farbe
	 * @param startBrettPosition
	 * @param endBrettPosition
	 * @param name
	 * @param figurenMenge
	 * @param gewinnerPosition
	 * 
	 * </pre>
	 */
	public KonkreterSpieler(Farbe farbe, SpielFeld startBrettPosition, SpielFeld endBrettPosition,
			String name, Set<Figur> figurenMenge, Integer gewinnerPosition) {
		this.name = name;
		this.farbe = farbe;
		this.startStreckenFeld = startBrettPosition;
		this.endStreckenFeld = endBrettPosition;
		this.figurenMenge = figurenMenge;
		this.gewinnerPosition = gewinnerPosition;
	}
	
	@Override
	public Long getSchluessel() {
		return schluessel;
	}

	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("der Spieler ist schon in einer Datenbank");
		}
		this.schluessel = schluessel;
	}
	
	@Override
	public Long getFremdSchluesselSpielZustand() {
		return fremdSchluesselSpielZustand;
	}
	
	@Override
	public void setFremdSchluesselSpielZustand(long fremdSchluessel) {
		if(this.fremdSchluesselSpielZustand != null) {
			throw new FremdSchluesselException("konkreter Spieler gehört schon zu einem SpielZustand");
		}
		fremdSchluesselSpielZustand  = fremdSchluessel;
	}
	
	@Override
	public void setGewinnerPosition(int gewinnerPosition) {
		if(getZielFeldFigurenMenge().size() != Spieler.MAX_FIGUREN_ANZAHL_JE_SPIELER) {
			throw new IllegalStateException("Der Spieler ist kein Gewinner, da seine Figuren noch nicht alle im Ziel sind.");
		}		
		if(this.gewinnerPosition != null) {
			throw new IllegalStateException("Eine einmal gesetzte Gewinner-Position darf nicht umgesetzt werden.");
		}
		this.gewinnerPosition = gewinnerPosition;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Farbe getFarbe() {
		return farbe;
	}

	@Override
	public SpielFeld getStartStreckenFeld() {
		return startStreckenFeld;
	}

	@Override
	public SpielFeld getEndStreckenFeld() {
		return endStreckenFeld;
	}
	
	@Override
	public Set<Figur> getFigurenMenge() {
		return figurenMenge;
	}

	@Override
	public Set<Figur> getStartFeldFigurenMenge() {
		return figurenMenge.stream()
				.filter(f -> f.getSpielFeld().getFeldArt() == FeldArt.START_FELD)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Figur> getStreckenFeldFigurenMenge() {
		return figurenMenge.stream()
				.filter(f -> f.getSpielFeld().getFeldArt() == FeldArt.STRECKEN_FELD)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Figur> getZielFeldFigurenMenge() {
		return figurenMenge.stream()
				.filter(f -> f.getSpielFeld().getFeldArt() == FeldArt.ZIEL_FELD)
				.collect(Collectors.toSet());
	}
	
	@Override
	public Integer getGewinnerPosition() {
		return gewinnerPosition;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(endStreckenFeld, farbe, figurenMenge, gewinnerPosition, name, schluessel,
				startStreckenFeld);
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
		KonkreterSpieler other = (KonkreterSpieler) obj;
		return Objects.equals(endStreckenFeld, other.endStreckenFeld) && Objects.equals(farbe, other.farbe)
				&& Objects.equals(figurenMenge, other.figurenMenge)
				&& Objects.equals(gewinnerPosition, other.gewinnerPosition) && Objects.equals(name, other.name)
				&& Objects.equals(schluessel, other.schluessel)
				&& Objects.equals(startStreckenFeld, other.startStreckenFeld);
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
		KonkreterSpieler other = (KonkreterSpieler) element;
		return Objects.equals(endStreckenFeld, other.endStreckenFeld) && Objects.equals(farbe, other.farbe)
				&& Objects.equals(figurenMenge, other.figurenMenge)
				&& Objects.equals(gewinnerPosition, other.gewinnerPosition) && Objects.equals(name, other.name)
				&& Objects.equals(startStreckenFeld, other.startStreckenFeld);
	}

	@Override
	public String toDisplayString() {
		Map.Entry<Long, String> startFelderInfo = getStartFeldFigurenMenge()
				.stream()
				.collect(Collectors.teeing(
						Collectors.counting(),
						Collectors.mapping(f -> f.toPrettyString(),
								Collectors.joining("\n\t - ", "\n\t - ", "")
						),
						Map::entry
				)
		);
		Map.Entry<Long, String> streckenFelderInfo = getStreckenFeldFigurenMenge()
				.stream()
				.collect(Collectors.teeing(
						Collectors.counting(),
						Collectors.mapping(f -> f.toPrettyString(),
								Collectors.joining("\n\t - ", "\n\t - ", "")
						),
						Map::entry
				)
		);
		Map.Entry<Long, String> zielFelderInfo = getZielFeldFigurenMenge()
				.stream()
				.collect(Collectors.teeing(
						Collectors.counting(),
						Collectors.mapping(f -> f.toPrettyString(),
								Collectors.joining("\n\t - ", "\n\t - ", "")
						),
						Map::entry
				)
		);
		
		return String.format("""
				《Konkreter Spieler (schluessel: %1$1d):》
				《 - Farbe: %2$s》
				《 - Name: %3$s》
				《 - Start-Streckenfeld: %4$s》
				《 - End-Streckenfeld: %5$s》
				《 - Startfeld-Figuren (Anzahl: %6$d)%9$s》
				《 - Streckenfeld-Figuren (Anzahl: %7$d)%10$s》
				《 - Zielfeld-Figuren (Anzahl: %8$d)%11$s》
			""".replaceAll("(^|》)\\s*(《|$)", "\n").trim(),
			schluessel,
			farbe.toPrettyString(),
			name,
			startStreckenFeld.toPrettyString(),
			endStreckenFeld.toPrettyString(),
			startFelderInfo.getKey(),
			streckenFelderInfo.getKey(),
			zielFelderInfo.getKey(),
			(startFelderInfo.getKey() > 0 ? ":" + startFelderInfo.getValue() : ""),
			(streckenFelderInfo.getKey() > 0 ? ":" + streckenFelderInfo.getValue() : ""),
			(zielFelderInfo.getKey() > 0 ? ":" + zielFelderInfo.getValue() : "")
		);
	}

	@Override
	public String toPrettyString() {
		return "KonkreterSpieler [schluessel= "+ schluessel + ", farbe=" + farbe.toPrettyString() + ", name=" + name + "]";
	}

	@Override
	public String toString() {
		return "KonkreterSpieler [schluessel=" + schluessel + ", name=" + name + ", farbe=" + farbe
				+ ", startStreckenFeld=" + startStreckenFeld + ", endStreckenFeld=" + endStreckenFeld
				+ ", figurenListe=" + figurenMenge + "]";
	}
}
