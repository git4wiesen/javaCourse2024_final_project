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
import java.util.Objects;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;

/**
 * <pre>
 * 
 * Stellt ein Feld auf einem Runden-Lauf-Mit-Herausschlagen Spiel dar.
 * 
 * Ein Feld kann eine Farbe haben, die zu einem Spieler gehört:
 *   - Start- / Zielfeld
 *   - 1. Streckenfeld eines Spielers, wohin dieser sich rausspielen kann.
 * 
 * Ein Feld hat eine Feldart (Startfeld / Streckenfeld / Zielfeld).
 * 
 * Ein Feld hat eine positive, integer Feldposition innerhalb seiner Feldart.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class SpielFeld
implements
	Serializable,
	DatenbankElement,
	WithDisplayString
{
	/**
	 * Die Serial Version dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = -6879293239563991871L;

	/**
	 * Primärschlüssel dieses Objekts in einer Datenbank
	 */
	private Long schluessel;
	
	/**
	 * Die Farbe des zugehörigen Spielers,
	 * 
	 * - wenn feldArt gleich StartFeld oder gleich ZielFeld ist oder
	 * - wenn das Feld das 1. Streckenfeld des zugehörigen Spielers ist.
	 */
	private final Farbe farbe;
	
	/**
	 * Art des Spielfeldes
	 */
	private final FeldArt feldArt;

	/**
	 * Position des Spielfeldes innerhalb seiner FeldArt
	 */
	private final int feldPosition;
	
	/**
	 * <pre>
	 * 
	 * @param farbe die Farbe des Feldes, falls das Feld im Bezug zu einem Spieler steht
	 * @param feldArt eines der 3 Feldarten, die das Spiel Mensch-ärger-dich-nicht hat
	 * @param position die Position innerhalb der Feldart
	 * 
	 * </pre>
	 */
	public SpielFeld(Farbe farbe, FeldArt feldArt, int position) {
		this.farbe = farbe;
		this.feldArt = feldArt;
		this.feldPosition = position;
	}
	
	/**
	 * <pre>
	 * 
	 * Erstelle eine farbiges StartFeld.
	 * @param color
	 * @param position
	 * @return
	 * 
	 * </pre>
	 */
	public static SpielFeld erstelleStartFeld(Farbe color, int position) {
		return new SpielFeld(color, FeldArt.START_FELD, position);
	}
	
	/**
	 * <pre>
	 * 
	 * Erstelle ein farbiges Strecken-Feld als 1. Streckenfeld eines Spielers, wohin dieser sich rausspielen kann..
	 * 
	 * @param color
	 * @param position
	 * @return
	 * 
	 * </pre>
	 */
	public static SpielFeld erstelleFarbigesStreckenFeld(Farbe color, int position) {
		return new SpielFeld(color, FeldArt.STRECKEN_FELD, position);
	}

	/**
	 * <pre>
	 * 
	 * Erstelle ein farbloses Strecken-Feld.
	 * 
	 * @param farbe
	 * @param position
	 * @return
	 * 
	 * </pre>
	 */
	public static SpielFeld erstelleFarblosesStreckenFeld(int position) {
		return new SpielFeld(null, FeldArt.STRECKEN_FELD, position);
	}

	/**
	 * <pre>
	 * 
	 * Erstelle ein farbiges Ziel-Feld.
	 * @param color die Farbe des zugehörigen Spielers
	 * @param position die Position auf dem Spielbrett innerhalb der FeldArt
	 * @return die neue SpielBrettPosition
	 * 
	 * </pre>
	 */
	public static SpielFeld erstelleZielFeld(Farbe color, int position) {
		return new SpielFeld(color, FeldArt.ZIEL_FELD, position);
	}
	
	@Override
	public Long getSchluessel() {
		return schluessel;
	}
	
	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("das Spielfeld ist schon in einer Datenbank");
		}
		this.schluessel = schluessel;
	}

	/**
	 * <pre>
	 * 
	 * @return the color
	 * 
	 * </pre>
	 */
	public Farbe getFeldFarbe() {
		return farbe;
	}

	/**
	 * <pre>
	 * 
	 * @return the feldArt
	 * 
	 * </pre>
	 */
	public FeldArt getFeldArt() {
		return feldArt;
	}

	/**
	 * <pre>
	 * 
	 * @return the position
	 * 
	 * </pre>
	 */
	public int getFeldPosition() {
		return feldPosition;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(farbe, feldArt, feldPosition, schluessel);
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
		SpielFeld other = (SpielFeld) obj;
		return Objects.equals(farbe, other.farbe) && feldArt == other.feldArt && feldPosition == other.feldPosition
				&& Objects.equals(schluessel, other.schluessel);
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
		SpielFeld other = (SpielFeld) element;
		return Objects.equals(farbe, other.farbe) && feldArt == other.feldArt && feldPosition == other.feldPosition;
	}

	@Override
	public String toDisplayString() {
		String prettyFarbe = farbe != null ? farbe.toPrettyString() : null;
		return String.format("""
				《SpielFeld (schluessel: %1$1d):》
				《 - Farbe: %2$s》
				《 - Feld-Art: %3$s》
				《 - Feld-Position: %4$d》
			""".replaceAll("(^|》)\\s*(《|$)", "\n").trim(),
			schluessel,
			prettyFarbe,
			feldArt,
			feldPosition
		);
	}

	@Override
	public String toString() {
		return "SpielFeld [schluessel=" + schluessel + ", feldFarbe=" + farbe + ", feldArt=" + feldArt
				+ ", feldPosition=" + feldPosition + "]";
	}

	/**
	 * <pre>
	 * 
	 * @return
	 * 
	 * </pre>
	 */
	public String toPrettyString() {
		String prettyFarbe = farbe != null ? farbe.toPrettyString() : null;
		return "SpielFeld [schluessel=" + schluessel + ", feldFarbe=" + prettyFarbe + ", feldArt=" + feldArt
				+ ", feldPosition=" + feldPosition + "]";
	}

	/**
	 * <pre>
	 * 
	 * Spezifiziert die unterschiedlichen Feldarten,
	 * die auf einem Mensch-Ärger-dich-nicht Spielbrett vorhanden sind.
	 * @author Christian Alexander Wiesenäcker
	 * 
	 * </pre>
	 */
	public enum FeldArt {
		START_FELD,
		STRECKEN_FELD,
		ZIEL_FELD
	}
}
