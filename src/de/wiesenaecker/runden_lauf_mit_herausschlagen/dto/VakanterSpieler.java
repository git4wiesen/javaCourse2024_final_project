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
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.FremdSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;

/**
 * <pre>
 * 
 * Stellt einen vakanten Spieler dar.
 * 
 * Objekte dieser Klasse dienen als Platzhalter,
 * damit weniger Spieler auf einem großen Runden-Lauf-Mit-Herausschlagen
 * Spiel für eine größere Anzahl Spieler spielen können.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class VakanterSpieler implements Spieler {
	/**
	 * serial version UID dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = 6803213114779307972L;

	/**
	 * der Datenbank-Schluessel des Spielers in einer eventuell genutzten Datenbank
	 */
	private Long schluessel;
	
	/**
	 * der Datenbank-Schluessel des SpielZustands, zudem der vakante Spieler gehört.
	 */
	private Long fremdSchluesselSpielZustand;

	/**
	 * die Farbe des Spielers
	 */
	private final Farbe farbe;
	
	private final SpielFeld startStreckenFeld;
	
	private final SpielFeld endStreckenFeld;

	/**
	 * <pre>
	 * 
	 * Erstellt einen vakanten Spieler mit einer Farbe
	 * @param farbe die Farbe des vakanten Spielers
	 * @param startStreckenFeld das Start-Streckenfeld des vakanten Spielers
	 * @param endStreckenFeld das End-Streckenfeld des vakanten Spielers
	 * 
	 * </pre>
	 */
	public VakanterSpieler(
			Farbe farbe,
			SpielFeld startStreckenFeld,
			SpielFeld endStreckenFeld
	) {
		this.farbe = farbe;
		this.startStreckenFeld = startStreckenFeld;
		this.endStreckenFeld = endStreckenFeld;
	}

	@Override
	public Long getSchluessel() {
		return schluessel;
	}

	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("vakanter Spieler ist schon in einer Datenbank");
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
			throw new FremdSchluesselException("vakanter Spieler gehört schon zu einem SpielZustand");
		}
		fremdSchluesselSpielZustand  = fremdSchluessel;
	}

	@Override
	public String getName() {
		return "";
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
		return Collections.emptySet();
	}
	
	@Override
	public Set<Figur> getStartFeldFigurenMenge() {
		return Collections.emptySet();
	}

	@Override
	public Set<Figur> getStreckenFeldFigurenMenge() {
		return Collections.emptySet();
	}

	@Override
	public Set<Figur> getZielFeldFigurenMenge() {
		return Collections.emptySet();
	}
	
	@Override
	public Integer getGewinnerPosition() {
		return null;
	}
	
	@Override
	public void setGewinnerPosition(int position) {
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(endStreckenFeld, farbe, schluessel, startStreckenFeld);
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
		VakanterSpieler other = (VakanterSpieler) obj;
		return Objects.equals(endStreckenFeld, other.endStreckenFeld) && Objects.equals(farbe, other.farbe)
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
		VakanterSpieler other = (VakanterSpieler) element;
		return Objects.equals(endStreckenFeld, other.endStreckenFeld) && Objects.equals(farbe, other.farbe)
				&& Objects.equals(startStreckenFeld, other.startStreckenFeld);
	}

	@Override
	public String toDisplayString() {
		return String.format("""
				《Vakanter Spieler (schluessel: %1$1d):》
				《 - Farbe: %2$s》
				《 - Start-Streckenfeld: %3$s》
				《 - End-Streckenfeld: %4$s》
			""".replaceAll("(^|》)\\s*(《|$)", "\n").trim(),
			schluessel,
			farbe.toPrettyString(),
			startStreckenFeld.toPrettyString(),
			endStreckenFeld.toPrettyString()
		);
	}

	@Override
	public String toPrettyString() {
		return "VakanterSpieler [schluessel=" + schluessel + ", farbe=" + farbe.toPrettyString() + ", startStreckenFeld="
				+ startStreckenFeld.toPrettyString() + ", endStreckenFeld=" + endStreckenFeld.toPrettyString() + "]";
	}

	@Override
	public String toString() {
		return "VakanterSpieler [schluessel=" + schluessel + ", farbe=" + farbe + ", startStreckenFeld="
				+ startStreckenFeld + ", endStreckenFeld=" + endStreckenFeld + "]";
	}
}
