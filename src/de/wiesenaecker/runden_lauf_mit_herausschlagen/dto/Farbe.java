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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;
import javafx.scene.paint.Color;

/**
 * <pre>
 * 
 * Stellt eine rgb-Farbe dar.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class Farbe implements Serializable, DatenbankElement {
	/**
	 * die serial version UID dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = 7277601599365526289L;

	/**
	 * Pattern zur Erkennung einer Hexadezimalzahl.
	 */
	private static final Pattern PATTERN_HEX_COLOR = Pattern.compile("^0x[0-9a-fA-F]+$");

	/**
	 * Pattern zur Erkennung einer Hexadezimalzahl.
	 */
	private static final Pattern PATTERN_TEXT = Pattern.compile("^[a-zA-Z]+$");
	
	public static final List<String> ALL_COLOR_NAMES = Collections.unmodifiableList(Arrays.asList(
            "aliceblue", "antiquewhite", "aqua", "aquamarine", "azure",
            "beige", "bisque", "black", "blanchedalmond", "blue", "blueviolet", "brown", "burlywood",
            "cadetblue", "chartreuse", "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson", "cyan",
            "darkblue", "darkcyan", "darkgoldenrod", "darkgray", "darkgreen", "darkgrey", "darkkhaki", "darkmagenta", "darkolivegreen", "darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen", "darkslateblue", "darkslategray", "darkslategrey", "darkturquoise", "darkviolet", "deeppink", "deepskyblue", "dimgray", "dimgrey", "dodgerblue",
            "firebrick", "floralwhite", "forestgreen", "fuchsia",
            "gainsboro", "ghostwhite", "gold", "goldenrod", "gray", "green", "greenyellow", "grey",
            "honeydew", "hotpink",
            "indianred", "indigo", "ivory",
            "khaki",
            "lavender", "lavenderblush", "lawngreen", "lemonchiffon", "lightblue", "lightcoral", "lightcyan", "lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey", "lightpink", "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightslategrey", "lightsteelblue", "lightyellow", "lime", "limegreen", "linen",
            "magenta", "maroon", "mediumaquamarine", "mediumblue", "mediumorchid", "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise", "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin",
            "navajowhite", "navy",
            "oldlace", "olive", "olivedrab", "orange", "orangered", "orchid",
            "palegoldenrod", "palegreen", "paleturquoise", "palevioletred", "papayawhip", "peachpuff", "peru", "pink", "plum", "powderblue", "purple",
            "red", "rosybrown", "royalblue",
            "saddlebrown", "salmon", "sandybrown", "seagreen", "seashell", "sienna", "silver", "skyblue", "slateblue", "slategray", "slategrey", "snow", "springgreen", "steelblue",
            "tan", "teal", "thistle", "tomato", "turquoise",
            "violet",
            "wheat", "white", "whitesmoke",
            "yellow", "yellowgreen"
	));
	
	/**
	 * Datenbank-Schlüssel, falls die Farbe in einer Datenbank abgelegt wird.
	 */
	private Long schluessel;
	
	/**
	 * der Anzeigename, falls verfügbar.
	 */
	private String displayName;
	
	/**
	 * der Rot-Anteil der dargestellten Farbe
	 */
	private final double rot;
	
	/**
	 * der Grün-Anteil der dargestellten Farbe
	 */
	private final double gruen;
	
	/**
	 * der Blau-Anteil der dargestellten Farbe
	 */
	private final double blau;
	
	/**
	 * der Hex-Farbcode der dargestellten Farbe
	 */
	private final int farbwert;

	/**
	 * <pre>
	 * 
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param hexColorCode
	 * 
	 * </pre>
	 */
	public Farbe(String displayName, double red, double green, double blue, int hexColorCode) {
		this.displayName = displayName;
		this.rot = red;
		this.gruen = green;
		this.blau = blue;
		this.farbwert = hexColorCode;
	}
	
	public static Farbe from(Color color) {
		double red = color.getRed();
		double green = color.getGreen();
		double blue = color.getBlue();

		long zahlFarbe = 0x0L;
		zahlFarbe = zahlFarbe | (((int)(red   * 255) & 0x0FF) << 4);
		zahlFarbe = zahlFarbe | (((int)(green * 255) & 0x0FF) << 2);
		zahlFarbe = zahlFarbe |  ((int)(blue  * 255) & 0x0FF);
		zahlFarbe = 0xFF_000000L;
		
		return new Farbe(null, red, green, blue, (int)zahlFarbe);
	}
	
	public static Farbe from(Color color, String displayName) {
		Farbe ergebnis = from(color);
		if(displayName != null && !displayName.isBlank()) {
			ergebnis.setDisplayName(displayName);
		}
		return ergebnis;
	}
	
	public static Farbe valueOf(int farbwert) {
		long longFarbwert = (((long)farbwert) & 0x00_FFFFFFL) | 0xFF_000000L;
		String hexFarbwert = "0x" + Long.toHexString(longFarbwert);

		Color color = Color.valueOf(hexFarbwert);
		double red = color.getRed();
		double green = color.getGreen();
		double blue = color.getBlue();
		return new Farbe(null, red, green, blue, (int)longFarbwert);
	}

	/**
	 * <pre>
	 * 
	 * Ermittelt die Farbe vom übergebenen String
	 * @param value enthält eine Zeichenkette, die eine Farbe identifiziert.
	 * @return
	 * 
	 * </pre>
	 */
	public static Farbe valueOf(String value) {
		if("transparent".equals(value)) {
			return new Farbe("black", 0, 0, 0, 0);
		}
		
		Long zahlFarbe = null;
		boolean isValueText = PATTERN_TEXT.matcher(value).matches();
		if(!isValueText && PATTERN_HEX_COLOR.matcher(value).matches()) {
			zahlFarbe = (Long.parseLong(value) & 0x00_FFFFFFL) | 0xFF_000000L;
			value = "0x" + Long.toHexString(zahlFarbe);
		}
		
		Color color = Color.valueOf(value);
		double red = color.getRed();
		double green = color.getGreen();
		double blue = color.getBlue();
		
		String displayName = null;
		if(isValueText) {
			displayName = value.toLowerCase(Locale.ENGLISH);
		}
		
		if(zahlFarbe == null) {
			zahlFarbe = 0x0L;
			zahlFarbe = zahlFarbe | (((int)(red   * 255) & 0x0FF) << 4);
			zahlFarbe = zahlFarbe | (((int)(green * 255) & 0x0FF) << 2);
			zahlFarbe = zahlFarbe |  ((int)(blue  * 255) & 0x0FF);
			zahlFarbe = 0xFF_000000L;
		}
		return new Farbe(displayName, red, green, blue, (int)(long)zahlFarbe);
	}
	
	@Override
	public Long getSchluessel() {
		return this.schluessel;
	}
	
	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("Die Farbe ist schon in der Datenbank.");
		}
		this.schluessel = schluessel;
	}

	/**
	 * <pre>
	 * 
	 * @return the corresponding JavaFx color
	 * 
	 * </pre>
	 */
	public Color toFxColor() {
		return new Color(
				this.rot,
				this.gruen,
				this.blau,
				1.0
		);
	}
	
	/**
	 * <pre>
	 * 
	 * @return the displayName
	 * 
	 * </pre>
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * <pre>
	 * 
	 * @param displayName the displayName to set
	 * 
	 * </pre>
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * <pre>
	 * 
	 * @return the red
	 * 
	 * </pre>
	 */
	public double getRot() {
		return rot;
	}

	/**
	 * <pre>
	 * 
	 * @return the green
	 * 
	 * </pre>
	 */
	public double getGruen() {
		return gruen;
	}

	/**
	 * <pre>
	 * 
	 * @return the blue
	 * 
	 * </pre>
	 */
	public double getBlau() {
		return blau;
	}

	/**
	 * <pre>
	 * 
	 * @return the hexColorCode
	 * 
	 * </pre>
	 */
	public int getFarbwert() {
		return farbwert;
	}

	@Override
	public int hashCode() {
		return Objects.hash(blau, displayName, farbwert, gruen, rot, schluessel);
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
		Farbe other = (Farbe) obj;
		return Double.doubleToLongBits(blau) == Double.doubleToLongBits(other.blau)
				&& Objects.equals(displayName, other.displayName) && farbwert == other.farbwert
				&& Double.doubleToLongBits(gruen) == Double.doubleToLongBits(other.gruen)
				&& Double.doubleToLongBits(rot) == Double.doubleToLongBits(other.rot)
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
		Farbe other = (Farbe) element;
		return Double.doubleToLongBits(blau) == Double.doubleToLongBits(other.blau)
				&& Objects.equals(displayName, other.displayName) && farbwert == other.farbwert
				&& Double.doubleToLongBits(gruen) == Double.doubleToLongBits(other.gruen)
				&& Double.doubleToLongBits(rot) == Double.doubleToLongBits(other.rot);
	}

	public String toPrettyString() {
		String displayName = this.displayName;
		if(displayName != null && !displayName.isBlank()) {
			return displayName.trim();
		}
		return "0x" + Long.toHexString(farbwert);
	}

	@Override
	public String toString() {
		return "Farbe [schluessel=" + schluessel + ", displayName=" + displayName + ", rot=" + rot + ", gruen=" + gruen
				+ ", blau=" + blau + ", farbwert=" + farbwert + "]";
	}
}
