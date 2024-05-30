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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff;

/**
 * <pre>
 * 
 * Eine RuntimeException, die geschmissen wird:
 * 
 *  - wenn der Primärschlüssel eines Datenbank-Elements mehrfach gesetzt werden sollte oder
 *  - wenn ein Datenbank-Element nicht vorhanden ist, obwohl er in die Datenbank eingelesen wurde.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
@SuppressWarnings("serial")
public class PrimaerSchluesselException extends RuntimeException {
	/**
	 * <pre>
	 * 
	 * Erstellt ein neues PrimaerSchluesselException Objekt ohne Nachricht
	 * 
	 * </pre>
	 */
	public PrimaerSchluesselException() {}

	/**
	 * <pre>
	 * 
	 * Erstellt ein neues PrimaerSchluesselException Objekt mit der übergebenen Nachricht aus message.
	 * @param message eine zu übermittelnde Nachricht
	 * 
	 * </pre>
	 */
	public PrimaerSchluesselException(String message) {
		super(message);
	}
}
