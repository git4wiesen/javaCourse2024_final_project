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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.server;

import java.io.Serial;
import java.io.Serializable;

/**
 * <pre>
 * 
 * Stellen eine Anfrage vom Client zum Server dar, um
 * - Informationen anzufordern
 * - Aktionen auszuloesen
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class ClientRequest implements Serializable {
	/**
	 * Die Serial Version dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = 3556879805405553953L;

	private final long spielerSchluessel;
	
	private final long geheimnis;
	
	private final ClientRequestInfo info;
	
	/**
	 * <pre>
	 * 
	 * @param info
	 * 
	 * </pre>
	 */
	private ClientRequest(
			long spielerSchluessel,
			long geheimnis,
			ClientRequestInfo info
	) {
		this.spielerSchluessel = spielerSchluessel;
		this.geheimnis = geheimnis;
		this.info = info;
	}
	
	public static ClientRequest erstellenClientRequest(
			long spielerSchluessel,
			long geheimnis,
			ClientRequestInfo info
	) {
		if(info == null) {
			throw new NullPointerException();
		}
		return new ClientRequest(spielerSchluessel, geheimnis, info);
	}

	/**
	 * <pre>
	 * 
	 * @return the spielerSchluessel
	 * 
	 * </pre>
	 */
	public long getSpielerSchluessel() {
		return spielerSchluessel;
	}

	/**
	 * <pre>
	 * 
	 * @return the geheimnis
	 * 
	 * </pre>
	 */
	public long getGeheimnis() {
		return geheimnis;
	}

	/**
	 * <pre>
	 * 
	 * @return the info
	 * 
	 * </pre>
	 */
	public ClientRequestInfo getInfo() {
		return info;
	}

	/**
	 * <pre>
	 * 
	 * Die Verschiedenen ClientRequest-Arten, die der Client an den Server stellen kann.
	 * @author Christian Alexander Wiesenäcker
	 * 
	 * </pre>
	 */
	public enum ClientRequestInfo {
		SPIEL_INFO,
		MOEGLICHE_ZUEGE_INFO,
		WUERFELN,
		ZIEHEN,
		GEHE_ZUM_NAECHSTER_SPIELER
	}
}
