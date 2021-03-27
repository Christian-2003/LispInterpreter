package errorHandling;



/**
 * Diese Klasse beinhaltet alle moeglichen Fehlermeldungen, welche in Objekten der Klasse
 * "ReturnValue<ContentType>" gespeichert werden koennen.
 * 
 * @author	Christian S
 * @version	12.01.2021
 */
public class ReturnValueTypes {
	/**
	 * Bei diesem Attribut handelt es sich nicht um eine Fehlermeldung. Dieser Wert gibt an,
	 * dass eine Methode erfolgreich ausgefuehrt wurde.
	 */
	public static int SUCCESS = 0;
	
	
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer unbekannte Fehler.
	 */
	public static int ERROR_UNKNOWN = 1;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer Rechenoperationen, bei welchen eine
	 * Division durch den Wert 0 durchgefuehrt wurde.
	 */
	public static int ERROR_DIVIDE_BY_ZERO = 2;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer z.B. Rechenoperationen, bei welchen
	 * zwei Werte mit unterschiedlichem Typ (z.B. String & Nummer) miteinander verrechnet
	 * werden sollten.
	 */
	public static int ERROR_UNEQUAL_DATA = 3;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer Syntaxfehler.
	 */
	public static int ERROR_SYNTAX = 4;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer z.B. Rechenoperationen, bei welchen
	 * ein unbekannter Operator verwendet wurde.
	 */
	public static int ERROR_UNKNOWN_OPERATOR = 5;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer Ausdruecke, in welchen ein unbekannter
	 * Bezeichner eines Atoms vorkommt.
	 */
	public static int ERROR_UNKNOWN_IDENTIFIER = 6;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer z.B. Rechenoperationen, in welchen
	 * zu Wenige Operanden vorhanden sind.
	 */
	public static int ERROR_NOT_ENOUGH_OPERANDS = 7;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer z.B. Rechenoperationen, in welchen
	 * ein String in eine Nummer umgewandelt werden sollte.
	 */
	public static int ERROR_CANNOT_OFFSET_STRING_TO_NUMBER = 8;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer Ausdruecke, in welchen ein unbekannter
	 * Token vorkommt.
	 */
	public static int ERROR_UNKNOWN_TOKEN = 9;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer Ausdruecke, in welchen ein unbekanntes
	 * Schluesselwort vorkommt.
	 */
	public static int ERROR_UNKNOWN_KEYWORD = 10;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer die Deklaration von Variablen, dessen
	 * Bezeichner bereits existieren.
	 */
	public static int ERROR_VARIABLE_NAME_DOES_EXIST = 11;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer eine nicht existente Datei, welche
	 * geladen werden soll.
	 */
	public static int ERROR_FILE_DOES_NOT_EXIST = 12;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer eine Datei welche geladen werden soll,
	 * jedoch nicht gelesen werden kann.
	 */
	public static int ERROR_FILE_CANNOT_BE_READ = 13;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer einen Quellcode, in welchem keine main-
	 * Funktion enthalten ist.
	 */
	public static int ERROR_NO_MAIN_FUNCTION = 14;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer einen Fehler, bei welchem die Startfunktion
	 * einen (oder mehr) Parameter besitzt.
	 */
	public static int ERROR_MAIN_FUNCTION_HAS_PARAMETER = 15;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer einen Fehler, bei welchem eine inkorrekte
	 * Anzahl an Parametern einer Funktion uebergeben wurde.
	 */
	public static int ERROR_INCORRECT_PARAMETER_NUMBER = 16;
	
	/**
	 * Dieses Attribut speichert die Fehlermeldung fuer einen Fehler, bei welchem eine Rueckgabe einer
	 * Funktion erwartet wird, welche nicht vorhanden ist.
	 */
	public static int ERROR_NO_RETURN_VALUE = 17;
	
	/**
	 * Bei diesem Wert handelt es sich nicht um einen Fehler: Dieser Wert indiziert, dass eine Funktion
	 * beendet werden soll.
	 */
	public static int INFO_FUNCTION_RETURN = 18;
	
	/**
	 * Speichert eine Fehlermeldung fuer einen bereits vorhandenen Instanznamen.
	 */
	public static int ERROR_INSTANCE_NAME_DOES_EXIST = 19;
	
	/**
	 * Speichert die Fehlermeldung fuer einen Fehler, bei welchem der Instanzname identisch einem Klassennamen ist.
	 */
	public static int ERROR_INSTANCE_NAME_CANNOT_BE_CLASS_NAME = 20;
	
	/**
	 * Speichert die Fehlermeldung fuer einen Fehler, bei welchem eine Instanz einer unbekannten Klasse erstellt
	 * werden soll.
	 */
	public static int ERROR_UNKNOWN_CLASS = 21;
}
