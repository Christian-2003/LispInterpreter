package interpreter;

import java.lang.Math;



/**
 * Diese Klasse enthaelt statische Methoden, welche jeweils vordefinierte Funktionen darstellen. Diese koennen dann vom
 * Interpreter ausgefuehrt werden, ohne selbst definiert zu werden.
 * 
 * @author	Christian S
 * @version	14.04.2021
 */
public class PreDefinedFunctions {
	/**
	 * Gibt die Laenge eines Strings zurueck.
	 * 
	 * @param psString	String, wessen Laenge gemessen werden soll.
	 * @return			Laenge des Strings.
	 */
	public static double length(String psString) {
		return (double)psString.length();
	}
	
	/**
	 * Speichert die Anzahl der Parameter der "length()"-Funktion:
	 */
	public static int LENGTH_PARAMETERS = 1;

	
	
	
	
	/**
	 * Gibt an, ob es sich bei einem String um eine Zahl handelt.
	 * 
	 * @param psString	String, welcher geprueft werden soll.
	 * @return			Ob es sich um eine Zahl handelt.
	 */
	public static String isNumber(String psString) {
		try {
			double nNumber = Double.parseDouble(psString);
		}
		catch (NumberFormatException exceptionObj) {
			return "nil";
		}
		return "t";
	}
	
	/**
	 * Speichert die Anzahl der Parameter der "isNumber()"-Funktion.
	 */
	public static int ISNUMBER_PARAMETERS = 1;

	
	
	
	
	/**
	 * Gibt den Funktionswert des als Paramters engegebenen Wertes der sin()-Funktion
	 * zurueck.
	 * 
	 * @param psString	String, welcher den Funktionsparameter speichert.
	 * @return			Liefert den Funktionswert zurueck.
	*/
	public static String sin(String psString) {
		try {
			double x = Double.parseDouble(psString);
			return String.valueOf(Math.sin(x));
		}
		catch (NumberFormatException exceptionObj) {
			return "0.00";
		}
	}
	
	/**
	 * Speichert die Anzahl der Parameter der "isNumber()"-Funktion.
	 */
	public static int SIN_PARAMETERS = 1;

	
	
	
	
	/**
	 * Gibt den Funktionswert des als Paramters engegebenen Wertes der cos()-Funktion
	 * zurueck.
	 * 
	 * @param psString	String, welcher den Funktionsparameter speichert.
	 * @return			Liefert den Funktionswert zurueck.
	*/
	public static String cos(String psString) {
		try {
			double x = Double.parseDouble(psString);
			return String.valueOf(Math.cos(x));
		}
		catch (NumberFormatException exceptionObj) {
			return "0.00";
		}
	}
	
	/**
	 * Speichert die Anzahl der Parameter der "isNumber()"-Funktion.
	 */
	public static int COS_PARAMETERS = 1;

	
	
	
	
	/**
	 * Gibt den Funktionswert des als Paramters engegebenen Wertes der tan()-Funktion
	 * zurueck.
	 * 
	 * @param psString	String, welcher den Funktionsparameter speichert.
	 * @return			Liefert den Funktionswert zurueck.
	*/
	public static String tan(String psString) {
		try {
			double x = Double.parseDouble(psString);
			return String.valueOf(Math.tan(x));
		}
		catch (NumberFormatException exceptionObj) {
			return "0.00";
		}
	}
	
	/**
	 * Speichert die Anzahl der Parameter der "isNumber()"-Funktion.
	 */
	public static int TAN_PARAMETERS = 1;

	
	
	
	
	/**
	 * Gibt den Funktionswert des als Paramters engegebenen Wertes der sqrt()-Funktion
	 * zurueck.
	 * 
	 * @param psString	String, welcher den Funktionsparameter speichert.
	 * @return			Liefert den Funktionswert zurueck.
	*/
	public static String sqrt(String psString) {
		try {
			double x = Double.parseDouble(psString);
			return String.valueOf(Math.sqrt(x));
		}
		catch (NumberFormatException exceptionObj) {
			return "0.00";
		}
	}
	
	/**
	 * Speichert die Anzahl der Parameter der "isNumber()"-Funktion.
	 */
	public static int SQRT_PARAMETERS = 1;
	
	
	
	
	/**
	 * Gibt das Zeichen, welches sich an der Position pnPos im String psString befindet zurueck.
	 * 
	 * @param psString	String, in welchem das Zeichen gesucht wird.
	 * @param psPos		Position, dessen Zeichen zurueckgegeben werden soll.
	 * @return			Zeichen an der Position im String.
	 */
	public static String charAt(String psString, String psPos) {
		try {
			//Da alle Zahlen in diesem Lisp den Typen "double" aufweisen, hier jedoch ein index (also ein Integer) notwendig ist, muss die Eingabe zuerst
			//ueber "Double.parseDouble()" in einen double Wert umgewandelt werden, und anschliessend ueber einen Typecast in einen Integer umgewandelt
			//werden. Sonst kommt es zu einer "NumberFormatException", obwohl eine korrekte Zahl angegeben wurde.
			double nPosAsDouble = Double.parseDouble(psPos);
			int nPos = (int)nPosAsDouble;
			
			if (nPos < 0) {
				//Position ist zu klein. Es wird (falls vorhanden) das erste Zeichen des Strings zurueckgegeben:
				if (psString.length() >= 1) {
					return String.valueOf(psString.charAt(0));
				}
				else {
					return " ";
				}
			}
			if (psString.length() > nPos) {
				//Zeichen an der Position wird zurueckgegeben:
				return String.valueOf(psString.charAt(nPos));
			}
			else {
				//Position ist zu gross. Es wird (falls vorhanden) das letzte Zeichen des Strings zurueckgegeben:
				if (psString.length() >= 1) {
					return String.valueOf(psString.charAt(psString.length() - 1));
				}
				else {
					return " ";
				}
			}
		}
		catch (NumberFormatException exceptionObj) {
			//Es ist keine gueltige Zahl als Position uebergeben worden. Es wird (falls vorhanden) das erste Zeichen des Strings zurueckgegben:
			if (psString.length() >= 1) {
				return String.valueOf(psString.charAt(0));
			}
			else {
				return " ";
			}
		}
	}
	
	/**
	 * Speichert die Anzahl der "charAt()"-Funktion.
	 */
	public static int CHARAT_PARAMETERS = 2;
	
	
	
	
	
	/**
	 * Gibt den Substring des psString zwischen psBegin und psEnd zurueck.
	 * 
	 * @param psString	String, welcher zerlegt werden soll.
	 * @param psBegin	Index des ersten Zeichens des Substrings.
	 * @param psEnd		Index des letzten Zeichens des Substrings.
	 * @return			Substring zwischen psBegin und psEnd.
	 */
	public static String substring(String psString, String psBegin, String psEnd) {
		try {
			double nBeginAsDouble = Double.parseDouble(psBegin);
			double nEndAsDouble = Double.parseDouble(psEnd);
			int nBegin = (int)nBeginAsDouble;
			int nEnd = (int)nEndAsDouble;
			
			if (nBegin < 0 || nEnd > psString.length() - 1) {
				return psString;
			}
			else {
				return psString.substring(nBegin, nEnd);
			}
		}
		catch (NumberFormatException exceptionObj) {
			return psString;
		}
	}
	
	/**
	 * Speichert die Anzahl an Parametern der "substring()"-Funktion.
	 */
	public static int SUBSTRING_PARAMETERS = 3;
}
