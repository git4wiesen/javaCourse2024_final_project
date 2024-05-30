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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * 
 * Es gibt keinen sauberen Weg nicht String Parameter an
 * eine JavaFx Application Klasse zu übergeben.
 * 
 * Hier ist ein unschöner Weg von mir.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class FxApplicationParameters {
	/**
	 * Generator, um zufällige Identifier zu generieren.
	 */
	private static Random wuerfel = new Random();
	
	/**
	 * Objekt-Lager
	 */
	private static Map<String, Object> storage = new ConcurrentHashMap<>();
	
	public static Object removeObject(String identifier) {
		return storage.remove(identifier);
	}
	
	public static String addObject(Object object) {
		String key = null;
		
		for(int i=100; i>0; i--) {
			String stringA = Long.toHexString(wuerfel.nextLong(0x0_fff_ffff_ffff_ffffL));
			stringA = "0".repeat(15 - stringA.length()) + stringA;

			String stringB = Long.toHexString(wuerfel.nextLong(0x0_fff_ffff_ffff_ffffL));
			stringA = "0".repeat(15 - stringB.length()) + stringB;
			
			key = stringA + stringB;
			
			if(!storage.containsKey(key)) {
				storage.put(key, object);
				return key;
			}
		}
		
		return null;
	}

}
