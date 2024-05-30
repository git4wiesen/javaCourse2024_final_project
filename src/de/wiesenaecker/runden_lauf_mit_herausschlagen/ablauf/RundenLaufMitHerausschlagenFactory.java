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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.ablauf;

import java.util.List;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausSchlagenDaoMitSqlDatenbank;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.RundenLaufMitHerausschlagenDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.server.RundenLaufMitHerausschlagenDaoMitServer;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.server.RundenLaufMitHerausschlagenMitServerDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.RundenLaufMitHerausschlagenAppUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.client_console.ClientAppConsoleUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.client_graphics.ClientAppGraphicsUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.server_console.ServerAppConsoleUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.server_graphics.ServerAppGraphicsUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.RundenLaufMitHerausschlagenService;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.client.RundenLaufMitHerausschlagenClientService;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;

/**
 * <pre>
 * 
 * Erstellt die verschiedenen Hauptkomponenten,
 * die zu verschiedenen Anwendungen zusammengebaut werden können.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class RundenLaufMitHerausschlagenFactory {
	/**
	 * <pre>
	 * 
	 * Erstellt die Datenhaltung für die client / server Apps.
	 * @param appType
	 * @param databaseProvider
	 * @param databaseHost
	 * @param databasePort
	 * @param databaseName
	 * @return
	 * 
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public static <T extends RundenLaufMitHerausschlagenDao> T createRundenMitHerausschlagenDao(
			AppType appType,
			DatabaseProvider databaseProvider,
			String databaseHost,
			int databasePort,
			String databaseName,
			Object user,
			Object credentials
	) {
		if(appType == AppType.SERVER && databaseProvider == DatabaseProvider.GAME_SERVER) {
			throw new IllegalArgumentException("The game server cannot use itself as its database");
		}
		
		// TODO: REMOVE forced database provider setting for SERVER app type.
		databaseProvider = switch(appType) {
		case SERVER -> DatabaseProvider.SQLITE_MEMORY;
		case CLIENT -> DatabaseProvider.GAME_SERVER;
		default -> throw new UnsupportedOperationException();
		};
		
		return switch(databaseProvider) {
		case SQLITE_FILE, SQLITE, FILE -> (T)sqLiteFileDao(databaseName);
		case SQLITE_MEMORY, MEMORY -> (T)sqLiteMemoryDao();
		case MYSQL -> (T)mySqlDao(databaseHost, databasePort, databaseName, (String)user, (String)credentials);
		case GAME_SERVER -> (T)new RundenLaufMitHerausschlagenDaoMitServer((long)user, (long)credentials);
		default -> (T)sqLiteFileDao(databaseName);
		};
	}
	
	/**
	 * <pre>
	 * 
	 * Erstellt die UI Logic für die client / server apps.
	 * @param appType
	 * @param userInterface
	 * @param jeannie
	 * @return
	 * 
	 * </pre>
	 */
	public static RundenLaufMitHerausschlagenAppUi createRundenLaufMitHerausschlagenAppUi(
			AppType appType,
			UiInterface userInterface,
			String brandTitleName,
			List<String> brandBoardSloganWords,
			RundenLaufMitHerausschlagenService jeannie
	) {
		if(appType == AppType.CLIENT) {
			if(userInterface == UiInterface.CONSOLE) {
				return new ClientAppConsoleUi(brandTitleName, brandBoardSloganWords, (RundenLaufMitHerausschlagenClientService)jeannie);
			} else if(userInterface == UiInterface.GRAPHICS) {
				return new ClientAppGraphicsUi(brandTitleName, brandBoardSloganWords, (RundenLaufMitHerausschlagenClientService)jeannie);
			}
		} else if(appType == AppType.SERVER) {
			if(userInterface == UiInterface.CONSOLE) {
				return new ServerAppConsoleUi(brandTitleName, brandBoardSloganWords, (RundenLaufMitHerausschlagenServerService)jeannie);
			} else if(userInterface == UiInterface.GRAPHICS) {
				return new ServerAppGraphicsUi(brandTitleName, brandBoardSloganWords, (RundenLaufMitHerausschlagenServerService)jeannie);
			}
		}
		
		throw new UnsupportedOperationException();
	}

	/**
	 * <pre>
	 * 
	 * @param client
	 * @param james
	 * @return
	 * 
	 * </pre>
	 */
	public static RundenLaufMitHerausschlagenService createRundenLaufMitHerausschlagenService(
			AppType client,
			RundenLaufMitHerausschlagenDao james
	) {
		return switch(client) {
		case CLIENT -> new RundenLaufMitHerausschlagenClientService((RundenLaufMitHerausschlagenMitServerDao)james);
		case SERVER -> new RundenLaufMitHerausschlagenServerService(james, null);
		default -> throw new UnsupportedOperationException("Unknown app type");
		};
	}
	
	private static RundenLaufMitHerausschlagenDao mySqlDao(
			String host,
			int port,
			String datenbank,
			String user,
			String password
	) {
		return RundenLaufMitHerausSchlagenDaoMitSqlDatenbank.erzeugeMenschAergerDichNichtDaoMitMySqlDatenbank(
				host,
				port,
				datenbank,
				user,
				password
		);
	}

	private static RundenLaufMitHerausschlagenDao sqLiteMemoryDao() {
		return RundenLaufMitHerausSchlagenDaoMitSqlDatenbank.erzeugeMenschAergerDichNichtDaoMitMemorySqliteDatenbank();
	}
	
	private static RundenLaufMitHerausschlagenDao sqLiteFileDao(String datenbank) {
		return RundenLaufMitHerausSchlagenDaoMitSqlDatenbank.erzeugeMenschAergerDichNichtDaoMitFileSqliteDatenbank(datenbank);
	}
	
	/**
	 * <pre>
	 * 
	 * Legt die App Art fest
	 * @author Christian Alexander Wiesenäcker
	 * 
	 * </pre>
	 */
	public enum AppType {
		CLIENT,
		SERVER
	}
	
	/**
	 * <pre>
	 * 
	 * Optionen für den Datenbank-Provider.
	 * @author Christian Alexander Wiesenäcker
	 * 
	 * </pre>
	 */
	public enum DatabaseProvider {
		SQLITE,
		SQLITE_FILE,
		FILE,
		SQLITE_MEMORY,
		MEMORY,
		MYSQL,
		
		GAME_SERVER
	}
	
	/**
	 * <pre>
	 * 
	 * Optionen for die Nutzeroberfläche.
	 * @author Christian Alexander Wiesenäcker
	 * 
	 * </pre>
	 */
	public enum UiInterface {
		CONSOLE,
		GRAPHICS
	}
}
