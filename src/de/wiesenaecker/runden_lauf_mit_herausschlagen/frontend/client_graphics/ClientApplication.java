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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.client_graphics;

import java.util.List;

import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.FxApplicationParameters;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.client.RundenLaufMitHerausschlagenClientService;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.client.SpielBrettMaker;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.client.SpielBrettMakerPlus;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * <pre>
 * 
 * Das Anwendungsfenster der Server-App.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class ClientApplication extends Application {
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
	 * Client business Logik.
	 * 
	 * </pre>
	 */
	private RundenLaufMitHerausschlagenClientService jeannie;
	
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
				jeannie instanceof RundenLaufMitHerausschlagenClientService ||
				brandTitleName instanceof String ||
				brandBoardSloganWords instanceof List
		) {
			this.jeannie = (RundenLaufMitHerausschlagenClientService)jeannie;
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
		primaryStage.setTitle("CLIENT - " + brandTitleName);

//		StackPane root = new StackPane();
//		Scene mainUi = new Scene(root, 1200, 800);
//
//		GridPane gitter = new GridPane();
//		gitter.setGridLinesVisible(true);
//		root.getChildren().add(gitter);
//
//		gitter.setHgap(0);
//		gitter.setVgap(0);
//		gitter.setAlignment(Pos.CENTER);
//		gitter.setPadding(new Insets(25, 10, 25, 10));
//		
//		Label labelServerPort = new Label("Server port:");
//		TextField eingabeServerPort = new TextField("3888");
//		HBox hboxServerPort = new HBox();
//		hboxServerPort.getChildren().addAll(
//				labelServerPort, eingabeServerPort
//		);
//		
//		List<String> brandBoardSloganWords = this.brandBoardSloganWords;
//		if(brandBoardSloganWords != null) {
//			int size = brandBoardSloganWords.size();
//			String wort0 = size > 0 ? brandBoardSloganWords.get(0) : null;
//			String wort1 = size > 1 ? brandBoardSloganWords.get(1) : null;
//			String wort2 = size > 2 ? brandBoardSloganWords.get(2) : null;
//			String wort3 = size > 3 ? brandBoardSloganWords.get(3) : null;
//
//			mainUi.getStylesheets().add("https://fonts.googleapis.com/css2?family=Courgette&display=swap");
//			String textStyle = "-fx-font-family: \"Courgette\", cursive; -fx-font-size: 20;";
//			
//
//			gitter.getColumnConstraints().addAll(
//					new ColumnConstraints(200),
//					new ColumnConstraints(200),
//					new ColumnConstraints(200)
//			);
//			gitter.getRowConstraints().addAll(
//					new RowConstraints(),
//					new RowConstraints(200),
//					new RowConstraints()
//			);
//			
//			
//			if(wort0 != null && !wort0.isBlank()) {
//				Label wortEins = new Label(wort0);
//				wortEins.setStyle(textStyle);
//				gitter.add(wortEins, 0, 0);
//				GridPane.setHalignment(wortEins, HPos.CENTER);
//			}
//			
//			if(wort1 != null && !wort1.isBlank()) {
//				Label wortZwei = new Label(wort1);
//				wortZwei.setStyle(textStyle);
//				gitter.add(wortZwei, 2, 0);
//				GridPane.setHalignment(wortZwei, HPos.CENTER);
//			}
//			
//			if(wort2 != null && !wort2.isBlank()) {
//				Label wortDrei = new Label(wort2);
//				wortDrei.setStyle(textStyle);
//				gitter.add(wortDrei, 0, 2);
//				GridPane.setHalignment(wortDrei, HPos.CENTER);
//			}
//			
//			if(wort3 != null && !wort3.isBlank()) {
//				Label wortVier = new Label(wort3);
//				wortVier.setStyle(textStyle);
//				gitter.add(wortVier, 2, 2);
//				GridPane.setHalignment(wortVier, HPos.CENTER);
//			}
//			
//			
//		}
		
		SpielBrettMaker spielBrettMaker = new SpielBrettMakerPlus();
		
		
		//gitter.add(hboxServerPort, 2, 3, 3, 1);
		
		primaryStage.setScene(spielBrettMaker.getSpielBrett());
		primaryStage.show();
	}
}
