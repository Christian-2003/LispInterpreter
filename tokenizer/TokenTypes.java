package tokenizer;



/**
 * Speichert alle Typen von Tokens.
 * 
 * @version	06.01.2021
 * 
 * @author	Christian S
 */
public class TokenTypes {
	/**
	 * Speichert den Token-Typ fuer Bezeichner.
	 */
	static public String TOKEN_IDENTIFIER = "identifier";
	
	/**
	 * Speichert den Token-Typ fuer Strings.
	 */
	static public String TOKEN_STRING = "string";
	
	/**
	 * Speichert den Token-Typ fuer Zahlen.
	 */
	static public String TOKEN_NUMBER = "number";
	
	/**
	 * Speichert den Token-Typ fuer geoeffnete Klammern.
	 */
	static public String TOKEN_BRACKET_OPENED = "opened_bracket";
	
	/**
	 * Speichert den Token-Typ fuer geschlossene Klammern.
	 */
	static public String TOKEN_BRACKET_CLOSED = "closed_bracket";
	
	/**
	 * Speichert den Token-Typ fuer Opertoren.
	 */
	static public String TOKEN_OPERATOR = "operator";
	
	/**
	 * Speichert den Token-Typ fuer Schluesselwoerter.
	 */
	static public String TOKEN_KEYWORD = "keyword";
	
	/**
	 * Speichert den Token-Typ fuer Wahrheitswerte.
	 */
	static public String TOKEN_BOOLEAN = "boolean";
	
	/**
	 * Speichert den Token-Typ fuer boolesche Operatoren.
	 */
	static public String TOKEN_OPERATOR_BOOLEAN = "operator_boolean";
	
	/**
	 * Speichert den Token-Typ fuer den "."-Operator.
	 */
	static public String TOKEN_OPEATOR_DOT = "operator_dot";
}
