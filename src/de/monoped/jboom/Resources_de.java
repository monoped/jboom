package de.monoped.jboom;

/* This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * monoped@users.sourceforge.net
 */

import de.monoped.utils.KeyBundle;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/** German texts. */

public class Resources_de
    extends KeyBundle
{
    public Resources_de()
    {
        p("about",          "Über jBoom", 0, "Über jBoom");
        p("activ",          "Aktiv", KeyEvent.VK_A, "Aktive Verbindung (eins/aus)");
        p("activeLabel",    "Aktiv:");
        p("askCreateProp",  "Datei %s erzeugen?");
        p("askrunning",     "Ein jBoom-Programm scheint schon zu laufen.\nFortfahren?");
        p("askSave",        "Vor Speichern fragen");
        p("askUpload",      "Änderungen speichern?");
        p("authfail",       "Authentifizierung fehlgeschlagen");
        p("browse",         "Durchsuchen", KeyEvent.VK_D);
        p("cancel",         "Abbrechen", KeyEvent.VK_C, "Änderungen verwerfen und Fenster schließen");
        p("cantWrite",      "Fehler beim Schreiben in Datei %s");
        p("case",           "Groß-/Klein", 0, "Groß- und Kleinschreibung berücksichtigen");
        p("changeFolder",   "Ordner ändern"); 
        p("changeMark",     "Lesezeichen ändern");
        p("close",          null, 0, "Fenster schließen" + keyText(KeyEvent.VK_ESCAPE));
        p("config",         "Konfiguration:");
        p("configDown",     "Herunterladen der Konfiguration");
        p("configLabel",    "Konfiguration");
        p("configUp",       "Hochladen der Konfiguration");
        p("confirm",        "Bitte bestätigen");
        p("connections",    "Verbindungen:");
        p("copy",           "Kopieren");
        p("createFolder",   "Neuen Ordner erzeugen ");
        p("createMail",     "Neue Mailadresse erzeugen");
        p("createMark",     "Neue Lesezeichen erzeugen");
        p("csvFiles",       "CSV-Dateien");
        p("cut",            "Ausschneiden");
        p("delConn",        null, 0, "Verbindung löschen" + keyText(KeyEvent.VK_DELETE));
        p("doc",            "Dokumentation:");
        p("down",           null, 0, "Einträge nach unten schieben" 
                                + keyText(KeyEvent.VK_DOWN, InputEvent.ALT_MASK));
        p("download",       null, 0, "Herunterladen" 
                                + keyText(KeyEvent.VK_PAGE_DOWN, InputEvent.ALT_MASK));
        p("downloading",    "Herunterladen von %s...");
        p("edit",           "Ändern");
        p("emptyFile",      "Keine Daten vorhanden");
        p("error",          "Fehler");
        p("exit",           "Programm beenden");
        p("exitTray",       "Aus Systemleiste entfernen");
        p("expected",       "'%s1' erwartet (Zeile %s2)");
        p("exportMarks",    "HTML exportieren (nur Lesezeichen)");
        p("exportMail",     "HTML exportieren (nur Mail)");
        p("exportMarksMail","HTML exportieren (alles)");
        p("exportHTMLTitle","Lesezeichen in HTML-Datei exportieren");
        p("extract",        "Namen aus Titel", KeyEvent.VK_T, "Seiten-Titel als Namen verwenden");
        p("fileNotExist",   "Datei '%s' existiert nicht");
        p("find",           "Suchen:", 0, "Suchen"
                                + keyText(KeyEvent.VK_F3));
        p("findNext",       null, 0, "Vorwärts weitersuchen"
                                + keyText(KeyEvent.VK_F3));
        p("findPrev",       null, 0, "Rückwärts weitersuchen"
                                + keyText(KeyEvent.VK_F3, InputEvent.SHIFT_MASK));
        p("folderFirst",    "Ordner zuerst", KeyEvent.VK_Z, "Ordner vor Lesezeichen anordnen (ein/aus)");
        p("folderLabel",    "Ordner:");
        p("go",             null, 0, "Zeige diese Seite an"
                                + keyText(KeyEvent.VK_ENTER));
        p("host",           "Rechner:", KeyEvent.VK_R);
        p("htmlFiles",      "HTML-Dateien");
        p("importCSV",      "Mailadr. importieren");
        p("importCSVTitle", "Mailadressen aus CSV-Datei importieren");
        p("importHTML",     "HTML importieren");
        p("importHTMLTitle","Lesezeichen aus HTML-Datei importieren");
        p("incomplete",     "Unvollständige Optionen für ");
        p("initwarn",       "Änderungen verwerfen und Baum initialisieren?");
        p("insert",         "Einfügen");
        p("isrunning",      "Ein anderes jBoom scheint schon aktiv zu sein!");
        p("jboomhome",      "JBoom - Projektseite");
        p("keyFile",        "Private Schlüsseldatei:", KeyEvent.VK_S);
        p("label-mail",     "Mailadresse:", KeyEvent.VK_M);
        p("label-url",      "URL:", KeyEvent.VK_U);
        p("loading",        "Lade");
        p("local",          "lokal");
        p("localFile",      "Lokale Datei:", KeyEvent.VK_L);
        p("mailcommand",    "Mailer:", KeyEvent.VK_M);
        p("maillist",       "Mail-Liste");
        p("mailAdr",        "Adresse");
        p("mailName",       "Name");
        p("mailText",       "Text");
        p("mailTableExpl",  "<html><font color='green'>Rechtsklick auf einen Spaltenkopf, um die Spalte mit einem Mail-Feld zu verknüpfen. Mit '-' markierte Spalten werden ignoriert.");
        p("myMarks",        "Meine Lesezeichen");
        p("name",           "Name:", KeyEvent.VK_N);
        p("names",          "Namen", 0, "Lesezeichen- und Ordnernamen durchsuchen");
        p("nameExists",     "Der Name '%s' existiert schon");
        p("newConn",        "Neue Verbindung", 0, "Neue Verbindung erzeugen" 
                                + keyText(KeyEvent.VK_INSERT)); 
        p("newFolder",      "Neuer Ordner", 0, "Neuer Ordner"
                                + keyText(KeyEvent.VK_F, InputEvent.CTRL_MASK));       
        p("newFolderName",  "Neuer Ordner");
        p("newMail",        "Neue Mailadresse");     
        p("newMark",        "Neues Lesezeichen", 0, "Neues Lesezeichen" 
                                + keyText(KeyEvent.VK_B, InputEvent.CTRL_MASK));
        p("newMarkName",    "Neues Lesezeichen");
        p("nomarks",        "<html>Keine Lesezeichen gefunden.<p>Ist das eine Firefox-Bookmarks-Datei?");
        p("noreponse",      "Keine Antwort vpm Ftp-Server");
        p("normalSize",     "Normale Größe");
        p("opterr",         "Falsche Option '%s' ignoriert");
        p("ok",             "OK", KeyEvent.VK_O);
        p("passwd",         "Passwort:", KeyEvent.VK_P);
        p("passwdprompt",   "Passwort für ");
        p("passwdTitle",    "Passwort wird benötigt");
        p("position",       "Position und Größe merken", KeyEvent.VK_P, "Position und Größe des Hauptfensters beim nächsten "
                                + "Start wiederherstellen"); 
        p("prefs",          null, 0, "Einstellungen bearbeiten"
                                + keyText(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        p("propsError",     "Fehler beim Schreiben der Optionsdatei");
        p("recursive",      "Rekursiv", KeyEvent.VK_V, "Unterordner rekursiv sortieren (ein/aus()");
        p("recvUrl",        "Herunterladen-URL:", KeyEvent.VK_U);
        p("remFile",        "Entfernte Datei:", KeyEvent.VK_E);
        p("retrFail",       "Konnte Datei %s nicht herunterladen");
        p("root",           null, 0, "Wurzel anzeigen ein/aus"
                                + keyText(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        p("rootDir",        "Wurzelordner", KeyEvent.VK_W, "Ordner zur Wurzel machen");
        p("search",         "Lesezeichen durchsuchen" 
                                + keyText(KeyEvent.VK_F3));
        p("sendUrl",        "Hochladen-URL:", KeyEvent.VK_H);
        p("separator",      "Trennzeichen: ");
        p("skip1st",        "Erste Zeile überspringen");
        p("sort",           null, 0, "Ordner sortieren");
        p("sortLabel",      "Sortierung");
        p("srvClosed",      "Server hat Verbindung geschlossen");
        p("startmin",       "Minimiert starten", KeyEvent.VK_I, 
                                "Mit minimiertem Fenster starten (und in Systemleiste aufnehmen, wenn Option rechts gewählt).");
        p("storeFail",      "Konnte Datei %s nicht hochladen");
        p("tagIncompl",     "Unvollständiger Tag (Zeile %s)");
        p("text",           "Text:", KeyEvent.VK_T);
        p("textIncompl",    "Text unvollständig (Zeile %s)");
        p("totray",         "In Systemleiste", KeyEvent.VK_Y, "Beim Minimieren in die Systemleiste aufnehmen.");
        p("up",             null, 0, "Einträge nach oben schieben" 
                                + keyText(KeyEvent.VK_UP, InputEvent.ALT_MASK)); 
        p("upload",         null, 0, "Hochladen" 
                                + keyText(KeyEvent.VK_PAGE_UP, InputEvent.ALT_MASK)); 
        p("uploading",      "Hochladen auf %s...");
        p("urlcommand",     "Browser:",KeyEvent.VK_W);
        p("urlerr",         "Falsche URL: %s");
        p("urls",           "URLs", 0, "URLs durchsuchen");
        p("useLocal",       "<html>Fehler beim Lesen der Datei <font color=\"red\">%s</font>.<P>Wollen Sie eine lokale Lesezeichendatei verwenden?");
        p("usingLocal",     "Fehler beim Herunterladen; verwende lokale Lesezeichen.");
        p("user",           "Benutzername:", KeyEvent.VK_B);
        p("wait",           "Bitte warten");
        p("warn",           "Warnung");
    };
}

