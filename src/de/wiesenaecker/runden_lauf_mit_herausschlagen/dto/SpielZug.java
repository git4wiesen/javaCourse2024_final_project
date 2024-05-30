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
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;

/**
 * <pre>
 * 
 * Stellt einen Spielzug eines Spielers dar.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class SpielZug
implements
	Serializable,
	DatenbankElement,
	WithDisplayString
{
	/**
	 * Die Serial Version dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = -74060243066515962L;

	/**
	 * der Datenbank-Schluessel des Spiel-Zugs in einer eventuell genutzten Datenbank
	 */
	private Long schluessel;
	
	/**
	 * die Zug-Figur
	 */
	private final Figur zugFigur;
	
	/**
	 * das WürfelErgebnis
	 */
	private final WuerfelErgebnis wuerfelErgebnis;
	
	/**
	 * das ermittelte Zielfeld
	 */
	private final SpielFeld zielFeld;
	
	/**
	 * eine geschlagene, gegnerische Figur
	 */
	private final Figur schlagFigur;
	
	/**
	 * <pre>
	 * 
	 * 
	 * @param zugFigur
	 * @param wuerfelErgebnis
	 * @param zielFeld
	 * @param schlagFigur
	 * 
	 * </pre>
	 */
	public SpielZug(Figur zugFigur, WuerfelErgebnis wuerfelErgebnis, SpielFeld zielFeld, Figur schlagFigur) {
		this.zugFigur = zugFigur;
		this.wuerfelErgebnis = wuerfelErgebnis;
		this.zielFeld = zielFeld;
		this.schlagFigur = schlagFigur;
	}
	
	@Override
	public Long getSchluessel() {
		return schluessel;
	}
	
	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("der Spiel Zug ist schon in einer Datenbank");
		}
		this.schluessel = schluessel;
	}

	/**
	 * <pre>
	 * 
	 * @return the zugFigur
	 * 
	 * </pre>
	 */
	public Figur getZugFigur() {
		return zugFigur;
	}

	/**
	 * <pre>
	 * 
	 * @return the wuerfelErgebnis
	 * 
	 * </pre>
	 */
	public WuerfelErgebnis getWuerfelErgebnis() {
		return wuerfelErgebnis;
	}

	/**
	 * <pre>
	 * 
	 * @return the zugFeld
	 * 
	 * </pre>
	 */
	public SpielFeld getZugFeld() {
		return zugFigur.getSpielFeld();
	}
	
	/**
	 * <pre>
	 * 
	 * @return the zielFeld
	 * 
	 * </pre>
	 */
	public SpielFeld getZielFeld() {
		return zielFeld;
	}

	/**
	 * <pre>
	 * 
	 * @return the schlagFigur
	 * 
	 * </pre>
	 */
	public Figur getSchlagFigur() {
		return schlagFigur;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(schlagFigur, schluessel, wuerfelErgebnis, zielFeld, zugFigur);
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
		SpielZug other = (SpielZug) obj;
		return Objects.equals(schlagFigur, other.schlagFigur) && Objects.equals(schluessel, other.schluessel)
				&& wuerfelErgebnis == other.wuerfelErgebnis && Objects.equals(zielFeld, other.zielFeld)
				&& Objects.equals(zugFigur, other.zugFigur);
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
		SpielZug other = (SpielZug) element;
		return Objects.equals(schlagFigur, other.schlagFigur) && wuerfelErgebnis == other.wuerfelErgebnis
				&& Objects.equals(zielFeld, other.zielFeld) && Objects.equals(zugFigur, other.zugFigur);
	}

	@Override
	public String toDisplayString() {
		return String.format("""
				《Spielzug (schluessel: %1$1d):》
				《 - Würfel-Ergebnis: %2$s》
				《 - Zug-Figur: %3$s》
				《 - Zug-Feldart: %4$s》
				《 - Zug-Feldposition: %5$d》
				《 - Ziel-Feldart: %6$s》
				《 - Ziel-Feldposition: %7$d》
				《 - Geschlagene Figur: %8$s》
			""".replaceAll("(^|》)\\s*(《|$)", "\n").trim(),
			schluessel,
			wuerfelErgebnis,
			zugFigur.toPrettyString(),
			zugFigur.getSpielFeld().getFeldArt(),
			zugFigur.getSpielFeld().getFeldPosition(),
			zielFeld.getFeldArt(),
			zielFeld.getFeldPosition(),
			schlagFigur != null ? schlagFigur.toPrettyString() : null
		);
	}

	@Override
	public String toString() {
		String formatString = schlagFigur != null
				? "SpielZug [Figur %1$s auf %2$s %3$d schlägt Figur %7$s auf %4$s %5$d mit Würfel-Ergebnis %6$s]"
				: "SpielZug [Figur %1$s auf %2$s %3$d zieht auf %4$s %5$d mit Würfel-Ergebnis %6$s]";
		String farbeSchlagFigur = schlagFigur != null ? schlagFigur.getFarbe().toPrettyString() : "";
		return String.format(
				formatString,
				zugFigur.getFarbe().toPrettyString(),
				zugFigur.getSpielFeld().getFeldArt(),
				zugFigur.getSpielFeld().getFeldPosition(),
				zielFeld.getFeldArt(),
				zielFeld.getFeldPosition(),
				wuerfelErgebnis,
				farbeSchlagFigur
		);
	}
}
