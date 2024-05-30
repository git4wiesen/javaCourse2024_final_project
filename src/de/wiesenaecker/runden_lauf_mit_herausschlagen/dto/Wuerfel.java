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

import java.util.Random;

/**
 * <pre>
 * 
 * Würfelt zufällige Würfel-Ergebnisse aus.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class Wuerfel {
	/**
	 * <pre>
	 * 
	 * Singleton-Pattern:
	 * 
	 * Referenz zum einzigen Wuerfel-Objekt.
	 * 
	 * </pre>
	 */
	private static final Wuerfel INSTANCE = new Wuerfel();
	
	/**
	 * Eine Referenz zu einem Pseudo-Zufallszahlen-Generator.
	 */
	private final Random wuerfel = new Random();

	/**
	 * <pre>
	 * 
	 * Kontrolliere die Erzeugung von Wuerfeln und
	 * verhindere von Wuerfel erbende Klassen
	 * 
	 * </pre>
	 */
	private Wuerfel() {}
	
	/**
	 * <pre>
	 * 
	 * Singleton-Pattern:
	 * 
	 * @return gib die Referenz zum einzigen Wuerfel Objekt zurück.
	 * 
	 * </pre>
	 */
	public static Wuerfel getInstance() {
		return INSTANCE;
	}
	
	/**
	 * <pre>
	 * 
	 * Würfelt zufällig ein WuerfelErgebnis aus.
	 * @return der zufällige WuerfelErgebnis Wert
	 * 
	 * </pre>
	 */
	public WuerfelErgebnis wuerfeln() {
		WuerfelErgebnis[] ergebnisse = WuerfelErgebnis.values();
		return ergebnisse[wuerfel.nextInt(ergebnisse.length)];
	}
	
	/**
	 * <pre>
	 * 
	 * Mögliche Resultate von {@link Wuerfel#wuerfel}.
	 * @author Christian Alexander Wiesenäcker
	 * 
	 * </pre>
	 */
	public enum WuerfelErgebnis {
		EINS("1", 1),
		ZWEI("2", 2),
		DREI("3", 3),
		VIER("4", 4),
		FUENF("5", 5),
		SECHS("6", 6);
		
		private final int value;
		
		private final String arabicNumber;

		/**
		 * @param wert
		 */
		private WuerfelErgebnis(String arabicNumber, int value) {
			this.arabicNumber = arabicNumber;
			this.value = value;
		}
		
		/**
		 * <pre>
		 * 
		 * @return the value
		 * 
		 * </pre>
		 */
		public int getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return arabicNumber;
		}
	}
}
