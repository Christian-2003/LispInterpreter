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
	 * Speichert das Schluesselwort zum definieren einer neuen Klasse.
	 */
	public static String KEYWORD_CLASS = "class";
	
	/**
	 * Speichert das Schluesselwort fuer eine Rueckgabe einer Funktion.
	 */
	public static String KEYWORD_RETURN = "return";
	
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
	
	/**
	 * Speichert das Schluesselwort zum importieren ander Quellcode-Dateien.
	 */
	public static String KEYWORD_IMPORT = "import";
	
	
	
	// --- BEZEICHNER DER MAIN-FUNKTION --------------------------------------------------------------------------
	
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
	
	
	
	// --- OPERATOREN --------------------------------------------------------------------------
	
	/**
	 * Speichert den Punkt-Operator.
	 */
	public static String OPERATOR_DOT = ".";
	
	public static String OPERATOR_MODULO = "%";
	
	
	
	// --- BEZEICHNER VON FUNKTIONEN --------------------------------------------------------------------------
	
	/**
	 * Speichert den Bezeichner der "length()"-Funktion.
	 */
	public static String FUNCTION_LENGTH = "length";
	
	/**
	 * Speichert den Bezeichner der "isNumber()"-Funktion.
	 */
	public static String FUNCTION_ISNUMBER = "isnumber";
	
	/**
	 * Speichert den Bezeichner der "sin()"-Funktion.
	 */
	public static String FUNCTION_SIN = "sin";
	
	/**
	 * Speichert den Bezeichner der "cos()"-Funktion.
	 */
	public static String FUNCTION_COS = "cos";
	
	/**
	 * Speichert den Bezeichner der "tan()"-Funktion.
	 */
	public static String FUNCTION_TAN = "tan";
	
	/**
	 * Speichert den Bezeichner der "sqrt()"-Funktion.
	 */
	public static String FUNCTION_SQRT = "sqrt";
	
	/**
	 * Speichert den Bezeichner der "charAt()"-Funktion.
	 */
	public static String FUNCTION_CHARAT = "charat";
	
	/**
	 * Speichert den Bezeichner der "substring()"-Funktion.
	 */
	public static String FUNCTION_SUBSTRING = "substr";
}
