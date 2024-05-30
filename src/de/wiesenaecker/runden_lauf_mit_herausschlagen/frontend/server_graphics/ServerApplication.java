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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.server_graphics;

import java.util.List;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.FxApplicationParameters;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.client.RundenLaufMitHerausschlagenClientService;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Application.Parameters;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * <pre>
 * 
 * Das Anwendungsfenster der Server-App.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class ServerApplication extends Application {
	/**
	 * The game brand title name
	 */
	private String brandTitleName;

	/**
	 * The game brand slogan words list
	 */
	private List<String> brandBoardSloganWords;

	/**
	 * <pre>
	 * 
	 * Server business Logik.
	 * 
	 * </pre>
	 */
	private RundenLaufMitHerausschlagenServerService jeannie;
	
	private boolean serverStarted;
	
	@Override
	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		Parameters parameters = getParameters();

		String keyService = parameters.getRaw().stream().filter(s -> s.startsWith("service = ")).findFirst().get();
		keyService = keyService.replaceFirst("^service = ", "");
		
		String keyBrandTitleName = parameters.getRaw().stream().filter(s -> s.startsWith("brand_title_name = ")).findFirst().get();
		keyBrandTitleName = keyBrandTitleName.replaceFirst("^brand_title_name = ", "");
		
		String keyBrandBoardSloganWords = parameters.getRaw().stream().filter(s -> s.startsWith("brand_board_slogan_words = ")).findFirst().get();
		keyBrandBoardSloganWords = keyBrandBoardSloganWords.replaceFirst("^brand_board_slogan_words = ", "");
		
		Object jeannie = FxApplicationParameters.removeObject(keyService);
		Object brandTitleName = FxApplicationParameters.removeObject(keyBrandTitleName);
		Object brandBoardSloganWords = FxApplicationParameters.removeObject(keyBrandBoardSloganWords);
		if(
				jeannie instanceof RundenLaufMitHerausschlagenServerService ||
				brandTitleName instanceof String ||
				brandBoardSloganWords instanceof List
		) {
			this.jeannie = (RundenLaufMitHerausschlagenServerService)jeannie;
			this.brandTitleName = (String)brandTitleName;
			this.brandBoardSloganWords = (List<String>)brandBoardSloganWords;
		} else {
			Platform.exit();
		}
	}
	
	@Override
	public void stop() throws Exception {
		jeannie = null;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("SERVER - " + brandTitleName);

		StackPane root = new StackPane();
		Scene mainUi = new Scene(root, 1200, 800);

		GridPane gitter = new GridPane();
		root.getChildren().add(gitter);

		gitter.setHgap(5);
		gitter.setVgap(5);
		gitter.setAlignment(Pos.CENTER);
		gitter.setPadding(new Insets(25, 10, 25, 10));
		
		Label labelServerPort = new Label("Server port:");
		TextField eingabeServerPort = new TextField("3888");
		HBox hboxServerPort = new HBox();
		hboxServerPort.getChildren().addAll(
				labelServerPort, eingabeServerPort
		);
		
		
		Button buttonServerMode = new Button("Start server");
		buttonServerMode.setOnAction((a) -> {
			if(serverStarted) {
				buttonServerMode.setText("Start server");
			} else {
				buttonServerMode.setText("Stop server");
			}
			serverStarted = !serverStarted;
		});
		
		gitter.add(hboxServerPort, 2, 3, 3, 1);
		gitter.add(buttonServerMode, 2, 5);
		
		primaryStage.setScene(mainUi);
		primaryStage.show();
	}
}
