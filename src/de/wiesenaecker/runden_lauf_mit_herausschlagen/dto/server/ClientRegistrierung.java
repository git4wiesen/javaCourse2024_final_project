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
import java.util.Objects;
import java.util.regex.Pattern;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.PrimaerSchluesselException;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.DatenbankElement;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;

/**
 * <pre>
 * 
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class ClientRegistrierung implements DatenbankElement {
	/**
	 * Pattern for hosts
	 */
	private static final Pattern PATTERN_HOST = Pattern.compile(
			"(^[a-zA-Z_][a-zA-Z0-9_]*([.][a-zA-Z_][a-zA-Z0-9_]*)*$)|"
					+ "(^\\[0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?\\]$)|"
					+ "(^\\[[0-9a-fA-F:]+\\]$)"
	);
	
	/**
	 * Die Serial Version dieser Klasse
	 */
	@Serial
	private static final long serialVersionUID = 2195109675910455432L;

	/**
	 * der Datenbank-Schluessel der ClientRegistrierung in einer eventuell genutzten Datenbank
	 */
	private Long schluessel;
	
	/**
	 * Das Geheimnis, mit dem ein Client sich beim Server authentifiziert
	 */
	private final Long geheimnis;

	/**
	 * der konkrete Spieler im Runden-Lauf-mit-herausschlagen Spiel, den der Client spielt
	 */
	private final KonkreterSpieler derSpieler;
	
	/**
	 * der Host, wo der Client Benachrichtigungen vom Server entgegen nimmt.
	 */
	private final String clientNotifyHost;

	/**
	 * der Port, auf dem der Client auf Benachrichtigungen vom Server wartet.
	 */
	private final int clientNotifyPort;
	
	/**
	 * <pre>
	 * 
	 * @param geheimnis
	 * @param derSpielerDesClients
	 * 
	 * </pre>
	 */
	private ClientRegistrierung(
			long geheimnis,
			KonkreterSpieler derSpielerDesClients,
			String clientNotifyHost,
			int clientNotifyPort
	) {
		this.geheimnis = geheimnis;
		this.derSpieler = derSpielerDesClients;
		this.clientNotifyHost = clientNotifyHost;
		this.clientNotifyPort = clientNotifyPort;
	}
	
	public static ClientRegistrierung erstellenClientRegistierung(
			long geheimnis,
			KonkreterSpieler derSpielerDesClients,
			String clientNotifyHost,
			int clientNotifyPort
	) {
		if(derSpielerDesClients == null || derSpielerDesClients.getSchluessel() != null || derSpielerDesClients.getFremdSchluesselSpielZustand() != null) {
			throw new IllegalArgumentException();
		}
		if(clientNotifyHost == null || !PATTERN_HOST.matcher(clientNotifyHost).matches()) {
			throw new IllegalArgumentException();
		}
		if(clientNotifyPort < 1 || clientNotifyPort > 65535) {
			throw new IllegalArgumentException("Invalid port");
		}
		
		return new ClientRegistrierung(geheimnis, derSpielerDesClients, clientNotifyHost, clientNotifyPort);
	}

	@Override
	public Long getSchluessel() {
		return schluessel;
	}

	@Override
	public void setSchluessel(long schluessel) {
		if(this.schluessel != null) {
			throw new PrimaerSchluesselException("die ClientRegistrierung ist schon in einer Datenbank");
		}
		this.schluessel = schluessel;
	}
	
	/**
	 * <pre>
	 * 
	 * @return the geheimnis
	 * 
	 * </pre>
	 */
	public Long getGeheimnis() {
		return geheimnis;
	}

	/**
	 * <pre>
	 * 
	 * @return the derSpieler
	 * 
	 * </pre>
	 */
	public KonkreterSpieler getSpieler() {
		return derSpieler;
	}
	
	/**
	 * <pre>
	 * 
	 * @return the clientNotifyHost
	 * 
	 * </pre>
	 */
	public String getClientNotifyHost() {
		return clientNotifyHost;
	}

	/**
	 * <pre>
	 * 
	 * @return the clientNotifyPort
	 * 
	 * </pre>
	 */
	public int getClientNotifyPort() {
		return clientNotifyPort;
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
		ClientRegistrierung other = (ClientRegistrierung) element;
		return Objects.equals(clientNotifyHost, other.clientNotifyHost) && clientNotifyPort == other.clientNotifyPort
				&& Objects.equals(derSpieler, other.derSpieler) && Objects.equals(geheimnis, other.geheimnis);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clientNotifyHost, clientNotifyPort, derSpieler, geheimnis, schluessel);
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
		ClientRegistrierung other = (ClientRegistrierung) obj;
		return Objects.equals(clientNotifyHost, other.clientNotifyHost) && clientNotifyPort == other.clientNotifyPort
				&& Objects.equals(derSpieler, other.derSpieler) && Objects.equals(geheimnis, other.geheimnis)
				&& Objects.equals(schluessel, other.schluessel);
	}
}
