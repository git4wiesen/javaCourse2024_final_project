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

/**
 * <pre>
 * 
 * Geschmissen, wenn der aktive Spieler versucht eine Aktion durchzuführen,
 * die aktuell von {@link AktiverSpielerDekorator#getAktuelleTodoAktion()} nicht erlaubt ist.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
@SuppressWarnings("serial")
public class IllegaleSpielerAktionException extends RuntimeException {
	/**
	 * <pre>
	 * 
	 * Erstellt eine IllegaleAktiverSpielerAktionException ohne einer Nachricht.
	 * 
	 * </pre>
	 */
	public IllegaleSpielerAktionException() {}

	/**
	 * <pre>
	 * 
	 * Erstellt eine IllegaleAktiverSpielerAktionException mit der Nachricht message.
	 * @param message
	 * 
	 * </pre>
	 */
	public IllegaleSpielerAktionException(String message) {
		super(message);
	}
}
