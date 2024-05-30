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
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.RundenLaufMitHerausschlagenAppUi;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.client_graphics.ClientApplication;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.RundenLaufMitHerausschlagenService;
import de.wiesenaecker.runden_lauf_mit_herausschlagen.middletier.server.RundenLaufMitHerausschlagenServerService;
import javafx.application.Application;

/**
 * <pre>
 * 
 * Implementiert die graphische Benutzeroberfläche für die Server App.
 * @author Christian Alexander Wiesenäcker
 * 
 * </pre>
 */
public class ServerAppGraphicsUi implements RundenLaufMitHerausschlagenAppUi {
	/**
	 * The game brand title name
	 */
	private final String brandTitleName;

	/**
	 * The game brand slogan words list
	 */
	private final List<String> brandBoardSloganWords;

	/**
	 * <pre>
	 * 
	 * Server business Logik.
	 * 
	 * </pre>
	 */
	private final RundenLaufMitHerausschlagenServerService jeannie;
	
	public ServerAppGraphicsUi(
			String brandTitleName,
			List<String> brandBoardSloganWords,
			RundenLaufMitHerausschlagenServerService jeannie
	) {
		this.brandTitleName = brandTitleName;
		this.brandBoardSloganWords = brandBoardSloganWords;
		this.jeannie = jeannie;
	}

	@Override
	public void launch() {
		if(jeannie == null) {
			return;
		}
		String keyService = FxApplicationParameters.addObject(jeannie);
		if(keyService == null) {
			return;
		}
		
		String keyBrandTitleName = FxApplicationParameters.addObject(brandTitleName);
		if(keyBrandTitleName == null) {
			return;
		}
		
		String keyBrandBoardSloganWords = FxApplicationParameters.addObject(brandBoardSloganWords);
		if(keyBrandBoardSloganWords == null) {
			return;
		}
		
		Application.launch(
				ServerApplication.class,
				"service = " + keyService,
				"brand_title_name = " + keyBrandTitleName,
				"brand_board_slogan_words = " + keyBrandBoardSloganWords
		);
	}
}
