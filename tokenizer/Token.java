package tokenizer;



/**
 * Stellt einen Token dar.
 * 
 * @version	06.01.2021
 * 
 * @author	Christian S
 *
 */
public class Token {
	/**
	 * Speichert den Inhalt des Tokens.
	 */
	private String sValue;
	
	/**
	 * Speichert den Typen des Tokens.
	 */
	private String sType;
	
	
	
	/**
	 * Konstruktor der Klasse "CToken".
	 * 
	 * @param psValue	Inhalt des Tokens.
	 * @param psType	Typ des Tokens.
	 */
	public Token(String psValue, String psType) {
		sValue = psValue;
		sType = psType;
	}
	
	
	
	/**
	 * Gibt den Inhalt des Tokens als String zurueck.
	 * 
	 * @return	Inhalt des Tokens.
	 */
	public String getValue() {
		return sValue;
	}
	
	/**
	 * Gibt den Typen des Tokens als String zurueck.
	 * 
	 * @return	Typ des Tokens.
	 */
	public String getType() {
		return sType;
	}
}
