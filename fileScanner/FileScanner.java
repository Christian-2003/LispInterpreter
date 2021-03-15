package fileScanner;
import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import errorHandling.*;



/**
 * Die Klasse "FileScanner" dient dazu den Lisp-Quellcode aus einer Datei zu lesen.
 * 
 * @author	Christian S
 * @version	13.02.2021
 */
public class FileScanner {
	/**
	 * Diese Methode laedt den Quellcode aus der Datei, wessen Dateiname als Parameter
	 * psFileName uebergebn wurde. Der Inhalt wird dann als Liste an Strings zurueck-
	 * gegeben, wobei jedes Element in der Liste einen Lisp-Ausdruck darstellt.
	 * Wenn ein Fehler auftritt (z.B. wenn die Datei nicht vorhanden ist) wird eine leere
	 * Liste zurueckgegeben und es wird eine Fehlermedlung in der Konsole angezeigt.
	 * 
	 * @param psFileName	Der Pfad, der Name und die Endung der Datei aus welcher der
	 * 						Quellcode geladen werden soll.
	 * @return				Listen an Lisp-Ausdruecken als Strings.
	 */
	public static ReturnValue<LinkedList<String>> readFile(String psFileName) {
		Path filePathObj = Paths.get(psFileName); //Speichert den Pfad der Datei.
		
		//Ueberpruefen ob die Datei korrekt geoeffnet werden kann:
		File fileObj = new File(psFileName);
		if (!fileObj.exists()) {
			//FEHLER: Die Datei existiert nicht:
			return new ReturnValue<LinkedList<String>>(null, ReturnValueTypes.ERROR_FILE_DOES_NOT_EXIST);
		}
		else if (!fileObj.isFile()) {
			//FEHLER: Es handelt sich nicht um eine Datei:
			return new ReturnValue<LinkedList<String>>(null, ReturnValueTypes.ERROR_FILE_DOES_NOT_EXIST);
		}
		else if (!fileObj.canRead()) {
			//FEHLER: Die Datei kann nicht gelesen werden:
			return new ReturnValue<LinkedList<String>>(null, ReturnValueTypes.ERROR_FILE_CANNOT_BE_READ);
		}
		
		//Inhalt aus der Datei lesen:
		String sFileContent = "";
		try {
			sFileContent = Files.readString(filePathObj);
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		//Inhalt der Datei in einzelne Lisp-Ausdruecke unterteilen:
		LinkedList<String> lsSourceCode = new LinkedList<String>(); //Speichert die einzelnen Ausdruecke.
		String sCurrentExpression = ""; //Speichert den aktuellen Ausdruck.
		int nBracketsClosed = 0; //Speichert die Anzahl an geschlossenen Klammern.
		int nBracketsOpened = 0; //Speichert die Anzahl an geoeffneten Klammern.
		
		//Zeilenumbrueche und Tabulatoren aus dem Quellcode entfernen, da diese den Tokenizer verwirren:
		sFileContent = sFileContent.replaceAll("\t", "");
		sFileContent = sFileContent.replaceAll("\n", "");
		sFileContent = sFileContent.replaceAll("\r", "");
		
		//Quellcode zeichenweise durchlaufen um diesen in Ausdruecke zu unterteilen:
		while (!sFileContent.isEmpty()) {
			char chCurrentCharacter = sFileContent.charAt(0); //Speichert das aktuelle Zeichen im Quellcode.
			sFileContent = sFileContent.substring(1); //Erstes (aktuelles) Zeichen aus dem String entfernen.
			if (chCurrentCharacter == '(') {
				//Eine Klammer wurde geoeffnet:
				nBracketsOpened++;
			}
			else if (chCurrentCharacter == ')') {
				//Eine Klammer wurde geschlossen:
				nBracketsClosed++;
			}
			
			sCurrentExpression += chCurrentCharacter; //Aktuelles Zeichen dem aktuellen Ausdruck anfuegen.
			
			if ((nBracketsOpened != 0 && nBracketsClosed != 0) && (nBracketsOpened == nBracketsClosed)) {
				//Es wurden gleich viele Klammern geoeffnet und geschlossen:
				lsSourceCode.add(sCurrentExpression);
				sCurrentExpression = "";
				nBracketsClosed = 0;
				nBracketsOpened = 0;
			}
		}
		
		if (sCurrentExpression.length() != 0) {
			//SYNTAXFEHLER: Es befinden sich noch Zeichen im String:
			return new ReturnValue<LinkedList<String>>(lsSourceCode, ReturnValueTypes.ERROR_SYNTAX);
		}
		
		return new ReturnValue<LinkedList<String>>(lsSourceCode, ReturnValueTypes.SUCCESS);
	}
}
