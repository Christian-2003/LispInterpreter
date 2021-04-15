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
}
