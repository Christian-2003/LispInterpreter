package tokenizer;



/**
 * In dieser Klasse werden alle Schluesselwoerter gespeichert.
 * 
 * @author	Christian S
 * @version	16.03.2021
 */
public class KeywordTypes {
	// --- SCHLUESSELWOERTER --------------------------------------------------------------------------
	
	/**
	 * Speichert das Schluesselwort zum definieren einer neuen Funktion.
	 */
	public static String KEYWORD_DEFINE = "defun";
	
	/**
	 * Speichert das Schluesselwort zum Scannen der Eingabe des Benutzers.
	 */
	public static String KEYWORD_SCAN = "scan";
	
	/**
	 * Speichert das Schluesselwort fuer eine while-Schleife.
	 */
	public static String KEYWORD_WHILE = "while";
	
	/**
	 * Speichert das Schluesselwort fuer eine if-Verzweigung.
	 */
	public static String KEYWORD_IF = "if";
	
	/**
	 * Speichert das Schluesselwort zum Ausgeben von Text in der Konsole und einem Zeilenumbruch.
	 */
	public static String KEYWORD_PRINTLN = "princln";
	
	/**
	 * Speicher das Schluesselwort zum ausgeben von Text in der Konsole.
	 */
	public static String KEYWORD_PRINT = "princ";
	
	/**
	 * Speichert das Schluesselwort zum aendern des Wertes einer Variablen.
	 */
	public static String KEYWORD_SETF = "setf";
	
	/**
	 * Speichert das Schluesselwort zum deklarieren einer neuen Variable.
	 */
	public static String KEYWORD_VAR = "var";
	
	
	
	// --- BEZEICHNER DER MEIN-FUNKTION --------------------------------------------------------------------------
	
	public static String FUNCTION_MAIN = "main";
	
	
	
	// --- WAHRHEITSWERTE --------------------------------------------------------------------------
	
	/**
	 * Speichert den Wahrheitswert "true".
	 */
	public static String BOOLEAN_T = "t";
	
	/**
	 * Speichert den Wahrheitswert "false".
	 */
	public static String BOOLEAN_F = "nil";
}
