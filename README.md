# Abschlussprojekt für ein Java Kurs mit Zertifikat

**Kursanbieter:** alfatraining Bildungszentrum GmbH\
**Kurswebsite:** https://www.alfatraining.de/kurse/weiterbildung-java-entwickler \
**Kurszeitraum:** 5. Februar 2024 bis 28. März 2024\
**Kursteilnehmer:** Christian Alexander Wiesenäcker\
**LinkedIn Profil:** www.linkedin.com/in/christianwiesenaecker

# Das Abschlussprojekt

**Bearbeitungslänge:** 2 Wochen\
**Finalisierung:** nicht fertig implementiert\
**Kurs-Abschluss:** Bestanden mit Zertifikat (100 von 100 Punkten)

## Lizenz: GPL v2
Dieses Java Abschlussprojekt ist unter der GPL v2 lizensiert, damit sie mit der JavaFx-Bibliothek ausgeliefert und verlinkt werden kann, die selbst unter der GPL v2 lizensiert ist.


## Aufgabenstellung: Brettspiel mit Figuren Ziehen + Schlagen

Implementierung eines Brettspiels als Konsolen / JavaFx Anwendung, welche als Client- / Server-Anwendungen gestartet werden können.

Jeder Spieler sollte eine Client-Anwendung starten und über eine Socket-Verbindung mit der Server-Anwendungen kommunizieren.

Die Server-Anwendung sollte den Zustand eines Brettspiels überwachen, Würfelergebnisse ermitteln, den aktiven Spieler bestimmen,
die gewählten Spielzüge des aktiven Spielers übernehmen und den aktuellen Spielzustand an die Spieler zurückgeben,
sowie eine Gewinnerliste ermitteln.

Die Client-Anwendungen sollten für jeden Spieler den Spielzustand anzeigen und
dem Spieler ermöglichen auf das Spiel Einfluss zu nehmen, wenn er dran ist, (würfeln anstoßen / Figur für den Zug auswählen.)


Das Spiel:
- Es gibt mehrere Spieler.
- Jeder Spieler hat mehrere Figuren und eine Spielerfarbe.
- Das Spielbrett hat mehrere Streckenfelder und für jeden Spieler jeweils 4 Start- und 4 Zielfelder.
- Die Streckenfelder sind in einem Kreis angeordnet.
- Alle Figuren der Spieler beginnen auf den Startfeldern, die um einen festen Offset auf den Streckenfeldern verteilt sind.
- Das letzte Steckenfeld vor dem jeweiligen Startstreckenfeld eines Spielers grenzt an die Zielfelder des jeweiligen Spielers.
- Sobald eine Figur das Spielfeld umrundet hat (sie hat das letzte Streckenfeld erreicht) läuft sie weiter über die Zielfelder.
- Die möglichen Spielzüge eines aktiven Spielers wird durch einen Würfel mit 6 möglichen Würfelergebnissen bestimmt:
    - Mit einer 6 muss ein Spieler seine Figur aus den Startfeldern auf sein Startstreckenfeld ziehen, wenn möglich.
    - Nach einer 6 und einem erfolgten Zug darf der aktive Spieler noch einmal Würfeln.
    - Die gewürfelte Würfelzahl bestimmt wie viele Felder eine Figur auf den Streckenfeldern und Zielfeldern vorrücken muss,
   dabei kann er eine gegnerische Figur eines anderen Spielers schlagen, wenn er genau auf ein Feld mit einer anderen Figur trifft.
   Seine eigenen Figuren kann ein Spieler nicht schlagen und auf jedem Feld darf maximal eine Figur stehen.
    - Ein Spieler muss, wenn möglich seine Figur auf seinem Startstreckenfeld wegziehen, so lange noch Figuren auf seinen Startfeldern sind.
    - Nach erfolgtem Spielzug von einer nicht-6 Würfelzahl wird der nächste Spieler zum aktiven Spieler.
    - Wenn ein Spieler alle Figuren in seinen Zielfeldern maximal vorgerückt hat und die restlichen Figuren alle auf seinen Startfeldern stehen,
   so darf er als aktiver Spieler bis zu 3 Mal würfeln, um sie mit einer 6 herauszuwürfeln.
    - Die Gewinnerliste wird in der Reihenfolge gefüllt sowie ein Spieler alle seine Figuren auf seinen Zielfeldern sizten hat.
    - Der letzte Spieler, der seine Figuren nicht auf seinen Zielfeldern hat, wird als letzter Gewinner auf die Gewinnerliste gesetzt.
