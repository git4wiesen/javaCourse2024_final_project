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
package de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.client;

import java.util.Arrays;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * <pre>
 * 
 * Baut ein Spielbrett:
 * - die StartFelder sind in 4er Blöcken an den Spielbrett-Ecken.
 * - die Streckenfelder sind in Plus-Form abgebildet.
 * - die Zielfelder gehen in gerader Line Richtung Spielbrett-Mitte
 *   ausgehend vom letzten Streckenfeld des jeweiligen Spielers.
 * 
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
@SuppressWarnings("unused")
public class SpielBrettMakerPlus implements SpielBrettMaker {
	/**
	 * Szene die zusammengestellt wird.
	 */
	private Scene spielBrett;
	
	private Pane root;

	@Override
	public Scene getSpielBrett() {
		root = new Pane();
		
//		Circle kreis = new Circle();
//		kreis.setRadius(20);
//		kreis.setCenterY(100);
//		kreis.setCenterX(600);
//		
//		kreis.setFill(Color.RED);
//		kreis.setStrokeWidth(3);
//		kreis.setStroke(Color.BLACK);
//		
//		root.getChildren().addAll(kreis);

		// StartFelder
		erstelleStartFeldKreise(100, 100, Color.YELLOW, Color.BLACK);
		erstelleStartFeldKreise(600, 100, Color.GREEN, Color.BLACK);
		erstelleStartFeldKreise(100, 600, Color.BLACK, Color.BLACK);
		erstelleStartFeldKreise(600, 600, Color.RED, Color.BLACK);
		
		
		// Spieler - YELLOW
		erstelleFeld(100, 315, Color.YELLOW, Color.BLACK); // StartStrecken-Feld
		erstelleFeld(100, 375, Color.TRANSPARENT, Color.BLACK); // End-Streckenfeld
		erstelleHorizontaleFeldKreise(100, 375, Color.YELLOW, Color.BLACK, true); // Ziel-Streckenfelder
		
		// Spieler - Spieler Black
		erstelleFeld(340, 600, Color.BLACK, Color.BLACK);  // StartStrecken-Feld
		
		return new Scene(root, 1200, 800);
	}
	
	private void erstelleStartFeldKreise(
			double offsetX,
			double offsetY,
			Color fillColor,
			Color strokeColor
	) {
		double radius = 20;
		
		Circle kreis1 = new Circle();
		kreis1.setCenterX(offsetX);
		kreis1.setCenterY(offsetY);

		Circle kreis2 = new Circle();
		kreis2.setCenterX(offsetX + radius * 3);
		kreis2.setCenterY(offsetY);

		Circle kreis3 = new Circle();
		kreis3.setCenterX(offsetX);
		kreis3.setCenterY(offsetY + radius * 3);
		
		Circle kreis4 = new Circle();
		kreis4.setCenterX(offsetX + radius * 3);
		kreis4.setCenterY(offsetY + radius * 3);
		
		Circle[] kreise = {kreis1, kreis2, kreis3, kreis4};
		Arrays.stream(kreise).forEach(kreis -> {
			kreis.setRadius(radius);
			kreis.setFill(fillColor);
			kreis.setStroke(strokeColor);
			kreis.setStrokeWidth(3);
		});
		root.getChildren().addAll(kreise);
	}
	
	private void erstelleHorizontaleFeldKreise(
			double offsetX,
			double offsetY,
			Color fillColor,
			Color strokeColor,
			boolean direction
	) {
		double radius = 20;
		
		Circle kreis1 = new Circle();
		kreis1.setCenterX(offsetX + radius * 3);
		kreis1.setCenterY(offsetY);

		Circle kreis2 = new Circle();
		kreis2.setCenterX(offsetX + radius * 3 * 2);
		kreis2.setCenterY(offsetY);

		Circle kreis3 = new Circle();
		kreis3.setCenterX(offsetX + radius * 3 * 3);
		kreis3.setCenterY(offsetY);
		
		Circle kreis4 = new Circle();
		kreis4.setCenterX(offsetX + radius * 3 * 4);
		kreis4.setCenterY(offsetY);
		
		Circle[] kreise = {kreis1, kreis2, kreis3, kreis4};
		Arrays.stream(kreise).forEach(kreis -> {
			kreis.setRadius(radius);
			kreis.setFill(fillColor);
			kreis.setStroke(strokeColor);
			kreis.setStrokeWidth(3);
		});
		root.getChildren().addAll(kreise);
	}

	private void erstelleFeld(
			double offsetX,
			double offsetY,
			Color fillColor,
			Color strokeColor
	) {
		double radius = 20;
		
		Circle kreis = new Circle();
		kreis.setCenterX(offsetX);
		kreis.setCenterY(offsetY);

		kreis.setRadius(radius);
		kreis.setFill(fillColor);
		kreis.setStroke(strokeColor);
		kreis.setStrokeWidth(3);

		root.getChildren().addAll(kreis);
	}
}
