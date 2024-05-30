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

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.FremdSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld.FeldArt;

/**
 * <pre>
 * 
 * Stellt eine Figur auf dem Runden-Lauf-Mit-Herausschlagen Spielbrett dar.
 * 
 * Eine Figur:
 * - ist serialisierbar.
 * - ist ein DatenbankElement.
 * - hat eine Farbe.
 * - hat eine Feld-Platzierung
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class Figur
implements
	Serializable,
	DatenbankElement,
	WithDisplayString
{
	/**
	 * serial version UID dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = -2241411982768486461L;
	
	/**
	 * Primärschlüssel dieses Objekts in einer Datenbank
	 */
	private Long schluessel;

	/**
	 * der Datenbank-Schluessel des SpielZustands, zu dem die Figur gehört.
	 */
	private Long fremdSchluesselSpielZustand;

	/**
	 * der Datenbank-Schluessel des Spielers, zu dem die Figur gehört.
	 */
	private Long fremdSchluesselSpieler;
	
	/**
	 * Die Farbe der Figur. 
	 */
	private Farbe farbe;
	
	/**
	 * <pre>
	 * 
	 * Die Position der Figur auf dem Spielbrett.
	 * 
	 * </pre>
	 */
	private SpielFeld spielFeld;
	
	/**
	 * <pre>
	 * 
	 * Erstellt eine farbige BrettFigur und platziert sie auf das übergebene Spielfeld.
	 * @param farbe die Figur-Farbe
	 * @param spielFeld das initiale Spielfeld, auf die die Figur gesetzt wird.
	 * 
	 * </pre>
	 */
	private Figur(Farbe farbe, SpielFeld spielFeld) {
		this.farbe = farbe;
		this.spielFeld = spielFeld;
	}
	
	/**
	 * 
	 * <pre>
	 * 
	 * @param besitzer der Spieler, der diese Figur spielt
	 * @param spielFeld Feld, auf das die Figur gesetzt wird
	 * @return die Figur
	 * @throws IllegaleSpielFigurPositionException
	 * 		falls, die Figur nicht auf das übergebene Spielfeld gesetzt werden darf
	 * 
	 * </pre>
	 */
	public static Figur erstelleFigur(Farbe farbe, SpielFeld spielFeld) {
		if(farbe == null || spielFeld == null) {
			throw new NullPointerException("die Parameter dürfen nicht null sein");
		}

		FeldArt feldArt = spielFeld.getFeldArt();
		Farbe feldFarbe = spielFeld.getFeldFarbe();
		if(
				(feldArt == FeldArt.START_FELD || feldArt == FeldArt.ZIEL_FELD)
				&& !farbe.equals(feldFarbe)
		) {
			throw new IllegaleSpielFigurPositionException(
					"Eine Figur darf nicht auf ein gegnerisches Start- oder Zielfeld gesetzt werden."
			);
		}
		
		return new Figur(farbe, spielFeld);
	}

	@Override
	public Long getSchluessel() {
		return schluessel;
	}

	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("die Figur ist schon in einer Datenbank");
		}
		this.schluessel = schluessel;
	}
	
	/**
	 * <pre>
	 * 
	 * Fremdschlüssel zum SpielZustand
	 * @return
	 * 
	 * </pre>
	 */
	public Long getFremdSchluesselSpielZustand() {
		return fremdSchluesselSpielZustand;
	}
	
	/**
	 * <pre>
	 * 
	 * Setze den Fremdschlüssel zum SpielZustand
	 * @param fremdSchluessel
	 * 
	 * </pre>
	 */
	public void setFremdSchluesselSpielZustand(long fremdSchluessel) {
		fremdSchluesselSpielZustand  = fremdSchluessel;
	}
	
	/**
	 * <pre>
	 * 
	 * Fremdschlüssel zum SpielZustand
	 * @return
	 * 
	 * </pre>
	 */
	public Long getFremdSchluesselSpieler() {
		return fremdSchluesselSpieler;
	}
	
	/**
	 * <pre>
	 * 
	 * Setze den Fremdschlüssel zum SpielZustand
	 * @param fremdSchluessel
	 * 
	 * </pre>
	 */
	public void setFremdSchluesselSpieler(long fremdSchluessel) {
		if(this.fremdSchluesselSpieler != null) {
			throw new FremdSchluesselException("Figur gehört schon zu einem Spieler");
		}
		fremdSchluesselSpieler  = fremdSchluessel;
	}

	/**
	 * <pre>
	 * 
	 * @return the figurFarbe
	 * 
	 * </pre>
	 */
	public Farbe getFarbe() {
		return farbe;
	}

	/**
	 * <pre>
	 * 
	 * @return the spielFeld
	 * 
	 * </pre>
	 */
	public SpielFeld getSpielFeld() {
		return spielFeld;
	}

	/**
	 * <pre>
	 * 
	 * @parem farbe die Figur-Farbe
	 * 
	 * </pre>
	 */
	public void setFarbe(Farbe farbe) {
		this.farbe = farbe;
	}

	/**
	 * <pre>
	 * 
	 * @param spielFeld the spielFeld to set
	 * 
	 * </pre>
	 */
	public void setSpielFeld(SpielFeld spielFeld) {
		this.spielFeld = spielFeld;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(farbe, schluessel, spielFeld);
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
		Figur other = (Figur) obj;
		return Objects.equals(farbe, other.farbe) && Objects.equals(schluessel, other.schluessel)
				&& Objects.equals(spielFeld, other.spielFeld);
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
		Figur other = (Figur) element;
		return Objects.equals(farbe, other.farbe) && Objects.equals(spielFeld, other.spielFeld);
	}

	@Override
	public String toDisplayString() {
		return String.format("""
				《Figur (schluessel: %1$1d):》
				《 - Farbe: %2$s》
				《 - Spielfeld: %3$s》
			""".replaceAll("(^|》)\\s*(《|$)", "\n").trim(),
			schluessel,
			farbe.toPrettyString(),
			spielFeld.toPrettyString()
		);
	}

	/**
	 * <pre>
	 * 
	 * @return
	 * 
	 * </pre>
	 */
	public String toPrettyString() {
		String prettyFarbe = farbe != null ? farbe.toPrettyString() : "";
		return "Figur [schluessel=" + schluessel + ", figurFarbe=" + prettyFarbe + ", feldPatzierung=" + spielFeld.toPrettyString()
				+ "]";
	}

	@Override
	public String toString() {
		return "Figur [schluessel=" + schluessel + ", farbe=" + farbe + ", feldPatzierung=" + spielFeld
				+ "]";
	}
}
