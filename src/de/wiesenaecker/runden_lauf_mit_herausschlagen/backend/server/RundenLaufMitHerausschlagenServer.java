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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.backend.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.AktiverSpielerDekorator.AktiverSpielerTodoAktion;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Farbe;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Figur;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.KonkreterSpieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielFeld;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZug;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.SpielZustand;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Spieler;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.Wuerfel.WuerfelErgebnis;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.dto.server.ClientRegistrierung;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;
import javafx.scene.paint.Color;

/**
 * <pre>
 * 
 * Implementiert den Runden-Lauf-mit-herausschlagen Server.
 * 
 * Der Server soll folgendes können:
 * 
 * - eine neue ClientRegistierung annehmen.
 * - alle Spieler an die Clients ausgeben.
 * - eine ServerSpielSitzung mit mehreren Clients starten.
 * - einem Client signalisieren, dass er dran ist
 * - den SpielZustand übermitteln
 * - für einen Client würfeln und das Würfel-Ergebnis mitteilen.
 * - einen Client eine Figur ziehen lassen
 * - ...
 * 
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
@SuppressWarnings("unused")
public class RundenLaufMitHerausschlagenServer {
	/**
	 * Der Server-Port, an dem auf Verbindungen von Clients gehorcht wird.
	 */
	private final int serverPort;
	
	/**
	 * Die Registrierungen der Spiel-Clients
	 */
	private final HashSet<ClientRegistrierung> registrierungen = new HashSet<>();
	
	/**
	 * Die verwendete Datenbank-Anbindung
	 */
	private final RundenLaufMitHerausschlagenDao james;

	/**
	 * Die aktuelle Spiel-Siztung
	 */
	private RundenLaufMitHerausschlagenServerService dasSpiel;
	
	private void ausfuehren() {
		// Spiel Info
		boolean isSpielFertig = dasSpiel.isSpielFertig();
		List<Spieler> alleSpieler = dasSpiel.getAlleSpieler();
		List<Spieler> konkreteSpieler = dasSpiel.getKonkreteSpieler();
		List<Spieler> gewinnerSpieler = dasSpiel.getGewinnerSpieler();
		List<Figur> alleFiguren = dasSpiel.getAlleFiguren();
		List<SpielFeld> startStreckenFelder = dasSpiel.getStartStreckenFelder();
		List<SpielFeld> endStreckenFelder = dasSpiel.getEndStreckenFelder();		
		AktiverSpielerTodoAktion todoAktion = dasSpiel.getAktiverSpielerTodoAktion();
		WuerfelErgebnis wuerfelErgebnis1 = dasSpiel.getWuerfelErgebnis();
		Spieler spieler = dasSpiel.getAktiverSpieler();
		
		
		// wuerfeln
		dasSpiel.wuerfeln(spieler);
		WuerfelErgebnis wuerfelErgebnis2 = dasSpiel.getWuerfelErgebnis();
		
		// ziehen
		List<SpielZug> moeglicheZuege = dasSpiel.bestimmeMoeglicheSpielZuege(spieler, wuerfelErgebnis2);
		
		dasSpiel.naechsterAktiverSpieler();
		
	}
	
	/**
	 * <pre>
	 * 
	 * 
	 * 
	 * </pre>
	 */
	public RundenLaufMitHerausschlagenServer(
			int serverPort,
			RundenLaufMitHerausschlagenDao james
	) {
		this.serverPort = serverPort;
		this.james = james;
		
		this.dasSpiel = initialisiereServerSpielSitzung();
	}
	
	private RundenLaufMitHerausschlagenServerService initialisiereServerSpielSitzung() {
		Farbe rot = Farbe.from(Color.RED, "red");
		Farbe gruen = Farbe.from(Color.GREEN, "green");
		Farbe blau = Farbe.from(Color.BLUE, "blau");
		Farbe fuchsia = Farbe.from(Color.FUCHSIA, "fuchsia");
		
		Set<Figur> figurenRot = Stream.iterate(0, i -> i+1).limit(4).map(i -> Figur.erstelleFigur(
				rot,
				SpielFeld.erstelleStartFeld(rot, i)
		)).collect(Collectors.toCollection(() -> new HashSet<>()));
		
		Set<Figur> figurenGruen = Stream.iterate(0, i -> i+1).limit(4).map(i -> Figur.erstelleFigur(
				gruen,
				SpielFeld.erstelleStartFeld(gruen, i)
		)).collect(Collectors.toCollection(() -> new HashSet<>()));
		
		Set<Figur> figurenBlau = Stream.iterate(0, i -> i+1).limit(4).map(i -> Figur.erstelleFigur(
				blau,
				SpielFeld.erstelleStartFeld(blau, i)
		)).collect(Collectors.toCollection(() -> new HashSet<>()));
		
		Set<Figur> figurenFuchsia = Stream.iterate(0, i -> i+1).limit(4).map(i -> Figur.erstelleFigur(
				fuchsia,
				SpielFeld.erstelleStartFeld(fuchsia, i)
		)).collect(Collectors.toCollection(() -> new HashSet<>()));

		int spielerOffset = 10;
		List<Map.Entry<Integer, Integer>> felderPositionen4 = Arrays.asList(
				Map.entry(0, 39),
				Map.entry(10, 9),
				Map.entry(20, 19),
				Map.entry(30, 29)
		);
		Collections.shuffle(felderPositionen4);

//		int spielerOffset = 8;
//		List<Map.Entry<Integer, Integer>> felderPositionen6 = Arrays.asList(
//				Map.entry(0, 47),
//				Map.entry(8, 7),
//				Map.entry(16, 15),
//				Map.entry(24, 23),
//				Map.entry(32, 31),
//				Map.entry(40, 39)
//		);
//		Collections.shuffle(felderPositionen6);
		
		List<Map.Entry<Integer, Integer>> felderPositionen = felderPositionen4;
		
		KonkreterSpieler spielerRot = new KonkreterSpieler(
				rot,
				SpielFeld.erstelleFarbigesStreckenFeld(rot, felderPositionen.get(0).getKey()),
				SpielFeld.erstelleFarblosesStreckenFeld(felderPositionen.get(0).getValue()),
				"Alice",
				figurenRot, 
				null
		);
		
		KonkreterSpieler spielerGruen = new KonkreterSpieler(
				gruen,
				SpielFeld.erstelleFarbigesStreckenFeld(gruen, felderPositionen.get(1).getKey()),
				SpielFeld.erstelleFarblosesStreckenFeld(felderPositionen.get(1).getValue()),
				"Bob",
				figurenGruen, 
				null
		);

		KonkreterSpieler spielerBlau = new KonkreterSpieler(
				blau,
				SpielFeld.erstelleFarbigesStreckenFeld(blau, felderPositionen.get(2).getKey()),
				SpielFeld.erstelleFarblosesStreckenFeld(felderPositionen.get(2).getValue()),
				"Karla",
				figurenRot, 
				null
		);

		KonkreterSpieler spielerFuchsia = new KonkreterSpieler(
				fuchsia,
				SpielFeld.erstelleFarbigesStreckenFeld(rot, felderPositionen.get(3).getKey()),
				SpielFeld.erstelleFarblosesStreckenFeld(felderPositionen.get(3).getValue()),
				"Tim",
				figurenFuchsia, 
				null
		);
		
		Set<Spieler> alleSpieler = new HashSet<>();
		alleSpieler.add(spielerRot);
		alleSpieler.add(spielerGruen);
		alleSpieler.add(spielerBlau);
		alleSpieler.add(spielerFuchsia);
		
		KonkreterSpieler aktiverKonkreterSpieler = switch(new Random().nextInt(4)) {
		case 0 -> spielerRot;
		case 1 -> spielerGruen;
		case 2 -> spielerBlau;
		case 3 -> spielerFuchsia;
		default -> throw new AssertionError();
		};
		
		AktiverSpielerDekorator aktiverSpieler = AktiverSpielerDekorator.erstelleAktivenSpieler(
				aktiverKonkreterSpieler,
				AktiverSpielerTodoAktion.MUSS_WUERFELN,
				null, 
				0, 
				false
		);
		
		Set<Figur> alleFiguren = new HashSet<>();
		alleFiguren.addAll(figurenRot);
		alleFiguren.addAll(figurenGruen);
		alleFiguren.addAll(figurenBlau);
		alleFiguren.addAll(figurenFuchsia);
		
		int anzahlSpielerPositionen = alleSpieler.size();
		int anzahlKonkreterSpieler = (int)alleSpieler.stream().filter(s -> s instanceof KonkreterSpieler).count();
		
		registrierungen.add(ClientRegistrierung.erstellenClientRegistierung(
				1L,
				spielerRot,
				"localhost",
				20_001
		));
		registrierungen.add(ClientRegistrierung.erstellenClientRegistierung(
				2L,
				spielerGruen,
				"localhost",
				20_001
		));
		registrierungen.add(ClientRegistrierung.erstellenClientRegistierung(
				2L,
				spielerGruen,
				"localhost",
				20_001
		));;
		registrierungen.add(ClientRegistrierung.erstellenClientRegistierung(
				2L,
				spielerGruen,
				"localhost",
				20_001
		));;

		SpielZustand dasSpiel = new SpielZustand(
				aktiverSpieler,
				alleSpieler,
				alleFiguren,
				anzahlSpielerPositionen, 
				anzahlKonkreterSpieler, 
				spielerOffset
		);
		
		james.hinzufuegenSpielZustand(dasSpiel);
		
		return new RundenLaufMitHerausschlagenServerService(james, dasSpiel);
	}
	
	
	private void serverLog(String output) {
		LocalDateTime now = LocalDateTime.now();
		System.out.println(
				output.lines()
				.map(line -> now + " - Server: " + line)
				.collect(Collectors.joining("\n"))
		);
	}
	
	private class VerbindungsLauscher {
		public void lausche() {
		    try (
		    		ServerSocket lauscher = new ServerSocket(5678);
		    ) {
		    	serverLog("lausche auf neue Verbindungen: server port = " + serverPort);

				while(true) {
					try(
						Socket verbindung = lauscher.accept();
					) {
						bedieneClient(verbindung);
					} catch(IOException | ClassNotFoundException ausnahme) {
						ausnahme.printStackTrace();
					}
				}
		    } catch(IOException ausnahme) {
				ausnahme.printStackTrace();
			}
		}
		
		private void bedieneClient(Socket verbindung) throws IOException, ClassNotFoundException {
			InetAddress clientAddress = verbindung.getInetAddress();
			serverLog("ein Client hat sich verbunden: client IP/port = " + clientAddress);
			
			Object clientObjekt = null;
			try (ObjectInputStream objectLeser = new ObjectInputStream(new BufferedInputStream(verbindung.getInputStream()));) {
				clientObjekt = objectLeser.readObject();
			}

			Object serverObjekt = verarbeiteClientObjekt(clientObjekt);
			if(serverObjekt == null) {
				return;
			}

			try (ObjectOutputStream objektSchreiber = new ObjectOutputStream(new BufferedOutputStream(verbindung.getOutputStream()));) {
				objektSchreiber.writeObject(serverObjekt);
			}
		}
		
		private Object verarbeiteClientObjekt(Object clientObjekt) {
			
			return null;
		}
	}
}
