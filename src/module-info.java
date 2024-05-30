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
module de.wiesenaecker.runden_lauf_mit_herausschlagen {
	requires javafx.base;
	requires javafx.controls;
//	requires javafx.fxml;
	requires javafx.graphics;
	requires java.sql;
	requires args4j;
//	requires javafx.media;
//	requires javafx.swing;
//	requires javafx.web;
//	requires javafx.swt;

	opens de.wiesenaecker.runden_lauf_mit_herausschlagen.ablauf;
	opens de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.client_graphics;
	opens de.wiesenaecker.runden_lauf_mit_herausschlagen.frontend.server_graphics;
}
