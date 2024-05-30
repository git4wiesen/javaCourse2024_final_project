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

import java.io.Serializable;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;

/**
 * <pre>
 * 
 * Wird von allen Elementen implementiert,
 * die in eine Datenbank abgelegt werden können.
 * 
 * Alle Datenbank-Elemente sollen auch serialisierbar sein.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public interface DatenbankElement extends Serializable {
	/**
	 * <pre>
	 * 
	 * Gibt den Primärschlüssel zurück, wenn das Element in einer Datenbank abgelegt wurde.
	 * Ansonsten wird null zurückgegeben
	 * 
	 * @return der Primärschlüssel, falls vorhanden, ansonsten null
	 * 
	 * </pre>
	 */
	Long getSchluessel();
	
	/**
	 * <pre>
	 * 
	 * Setzt den Primärschlüssel des DatenbankElements.
	 * 
	 * Nach einmaligem Setzen soll der Schlüssel nicht mehr änderbar sein.
	 * @param schluessel der zu setzende Datenbank Primärschlüssel.
	 * @throws PrimaerSchluesselException wenn ein bereits gesetzter Schlüssel geändert werden soll
	 * 
	 * </pre>
	 */
	void setSchluessel(long schluessel);
	
	/**
	 * <pre>
	 * 
	 * Verhält sich wie equals, ohne dass die Schlüssel verglichen werden.
	 * @return
	 * 
	 * </pre>
	 */
	boolean equalsDaten(DatenbankElement element);
}
