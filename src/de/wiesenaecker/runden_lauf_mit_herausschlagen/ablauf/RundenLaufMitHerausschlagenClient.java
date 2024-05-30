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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.ablauf.RundenLaufMitHerausschlagenFactory.AppType;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.ablauf.RundenLaufMitHerausschlagenFactory.DatabaseProvider;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.ablauf.RundenLaufMitHerausschlagenFactory.UiInterface;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.backendzugriff.server.RundenLaufMitHerausschlagenMitServerDao;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.RundenLaufMitHerausschlagenAppUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.RundenLaufMitHerausschlagenService;

/**
 * <pre>
 * 
 * Der Start der Client-Anwendung:
 * 
 * Folgende Bibliotheken wurden verwendet:
 * 
 * - jdbc MySQL connector 8.3.0 von MySQL
 *   Description:				JDBC driver implementation to connect to a MySQL database
 *   Website:					https://dev.mysql.com/
 *   License:					GPL v2 with classpath exception
 *   Download:					https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.3.0.zip
 * 
 * - jdbc sqlite 3.45.2.0 von Taro L. Saito
 *   Description:				JDBC driver implementation to connect to a sqlite database
 *   Website:					https://xerial.org/leo/
 *   GitHub:					https://github.com/xerial/sqlite-jdbc
 *   License:					Apache v2
 *   Download:					https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.2.0/sqlite-jdbc-3.45.2.0.jar
 *   Dependencies:				slf4j-api (1.7.36), slf4j-nop (1.7.36)
 * 
 * - slf4j-api 1.7.36
 *   Description:				Simple Logging Facade for Java
 *   Website:					http://www.slf4j.org
 *   License:					MIT License
 *   Download: 					https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar
 * 
 * - slf4j-api 2.0.12
 *   Description:				Simple Logging Facade for Java
 *   Website:					http://www.slf4j.org
 *   License:					MIT License
 *   Download:					https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.12/slf4j-api-2.0.12.jar
 * 
 * - slf4j-nop 1.7.36
 *   Description:				Simple Logging Facade for Java
 *   Website:					http://www.slf4j.org
 *   License:					MIT License
 *   Download:					https://repo1.maven.org/maven2/org/slf4j/slf4j-nop/1.7.36/slf4j-nop-1.7.36.jar
 * 
 * - JavaFX 17.0.10 von Gluon
 *   Description:				plattform independent GUI Framework
 *   Website:					https://openjfx.io/
 *   License:					GPL v2 with classpath exception
 *   JavaDoc:					https://openjfx.io/javadoc/17/
 *   Download - Windows:		https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_windows-x64_bin-sdk.zip
 *   Download - Mac x64:		https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_osx-x64_bin-sdk.zip
 *   Download - Mac aarch64:	https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_osx-aarch64_bin-sdk.zip
 *   Download - Linux x64:		https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_linux-x64_bin-sdk.zip
 * 
 * - Gson 2.10.1 von Google
 *   Description:				Json library
 *   GitHub:					https://github.com/google/gson
 *   License:					Apache v2
 *   Download:					https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
 * 
 * - args4J 2.37
 *   Description:				Command Line Options Parser
 *   Website:					https://args4j.kohsuke.org/
 *   License:					MIT License
 *   Download:					https://repo1.maven.org/maven2/args4j/args4j/2.37/args4j-2.37.jar
 *   JavaDoc:					https://repo1.maven.org/maven2/args4j/args4j/2.37/args4j-2.37-javadoc.jar
 *   
 *   Command Line Library Overview:
 *   https://www.innoq.com/en/articles/2022/01/java-cli-libraries/
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class RundenLaufMitHerausschlagenClient {
    static class Options {
        @Option(
        		required = true,
        		name = "-ui",
        		aliases = "--user-interface",
                usage = "Specifies, which user interface type shall be used for user interaction",
                metaVar = "console / graphics"
        )
        public UiInterface uiInterface;

        @Option(
        		name = "-brand-title",
        		aliases = "--brand-name-title",
                usage = "Specifies the brand title of the app, which will be displayed as the apps title",
                metaVar = "<BRAND_TITLE_NAME>"
        )
        public String brandTitleName = "Rundenlauf mit herausschlagen";

        @Option(
        		name = "-brand-slogan",
        		aliases = "--brand-slogan-board-text-parts",
                usage = "Specifies a list of text parts, which will be displayed on the apps game board. (Use at most 4 text parts)",
                metaVar = "\"<PART_1>\" \"<PART_2>\" \"<PART_3>\" \"<PART_3>\""
                , handler = StringArrayOptionHandler.class
        )
        public List<String> brandBoardSloganWords = Stream.of("Rundenlauf", "mit", "heraus-", "schlagen").collect(Collectors.toList());
        
        @Option(
        		name = "-server-host",
        		aliases = "--game-server-host",
                usage = "Host of the game server to connect to",
                metaVar = "<HOST>"
        )
        public String gameServerHost = "localhost";
        
        @Option(
        		name = "-server-port",
        		aliases = "--game-server-port",
                usage = "Port of the game server to connect to",
                metaVar = "<PORT>"
        )
        public int gameServerPort = 4666;
        
        @Option(
        		required = true,
        		name = "-player-key",
        		aliases = "--game-player-key",
                usage = "The game player key of the player, that is already known by the server",
                metaVar = "<PLAYER_KEY>"
        )
        public long gamePlayerKey;
        
        @Option(
        		required = true,
        		name = "-player-secret",
        		aliases = "--game-player-secret",
                usage = "Specifies a player secret, that is known by the server and used to authenticate player requests to the game server",
                metaVar = "<PLAYER_SECRET>"
        )
        public long gamePlayerSecret;
    }
	
	/**
	 * <pre>
	 * 
	 * @param args
	 * 
	 * </pre>
	 */
	public static void main(String[] args) {
		Options options = new Options();
		RundenLaufMitHerausschlagenMitServerDao james;
		RundenLaufMitHerausschlagenService jeanie;
		RundenLaufMitHerausschlagenAppUi interaktion;
		CmdLineParser parser = new CmdLineParser(options);
		try {
			parser.parseArgument(args);
			james = RundenLaufMitHerausschlagenFactory.createRundenMitHerausschlagenDao(
					AppType.CLIENT,
					DatabaseProvider.GAME_SERVER,
					options.gameServerHost,
					options.gameServerPort,
					null,
					options.gamePlayerKey,
					options.gamePlayerSecret
			);
			jeanie = RundenLaufMitHerausschlagenFactory.createRundenLaufMitHerausschlagenService(
					AppType.CLIENT,
					james
			);
			interaktion = RundenLaufMitHerausschlagenFactory.createRundenLaufMitHerausschlagenAppUi(
					AppType.CLIENT,
					options.uiInterface,
					options.brandTitleName,
					options.brandBoardSloganWords,
					jeanie
			);
		} catch(CmdLineException | IllegalArgumentException ausnahme) {
			System.out.println("""
					《Command line options:》
					《》
					《	-ui (--user-interface) <UI>》
					《		Specifies, which user interface type shall be》
					《		used for user interaction》
					《》
					《		o For console:		console》
					《		o For graphics:		graphics》
					《》
					《	-brand-title (--brand-name-title) "<BRAND_TITLE_NAME>"》
					《		Specifies the brand title of the app, which will be displayed as the apps title》
					《》
					《		Default: "Rundenlauf mit herausschlagen"》
					《》
					《	-brand-slogan (--brand-slogan-board-text-parts) "<PART_1>" "<PART_2>" "<PART_3>" "<PART_4>"》
					《		Specifies a list of text parts, which will be displayed on the apps game board.》
					《		(Use at most 4 text parts)》
					《》
					《		Default: "Rundenlauf" "mit" "heraus-" "schlagen"》
					《》
					《	-server-host (--game-server-host) <HOST>  						(default: localhost)》
					《		Host of the game server to connect to》
					《》
					《	-server-port (--game-server-port) <PORT>							(default: 4666)》
					《		Port of the game server to connect to》
					《》
					《	-player-key (--game-player-key) <PLAYER_KEY>》
					《		Specifies a known player key, that is already known to the game server》
					《》
					《	-player-secret (--game-player-secret) <PLAYER_SECRET>》
					《		Specifies a player secret, that is known by the server and used to authenticate》
					《		player requests to the game server》
			""".replaceAll("(^|》)\\s*(《|$)", "\n").trim());
			return;
		}
		interaktion.launch();
	}	
}
