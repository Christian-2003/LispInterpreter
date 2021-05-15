package interpreter;



/**
 * Objekte dieser Klasse werden dazu verwendet um Variablebn zu speichern. Dabei werden
 * jeweils der Bezeichner, der Wert und der Typ der Variablen gespeichert.
 * 
 * @author	Christian S
 * @version	09.02.2021
 */
public class Atom {
	/**
	 * Dieses Attribut speichert den Bezeichner der Variablen.
	 */
	private String sName;
	
	/**
	 * Dieses Attribut speichert den Wert der Variablen.
	 */
	private String sValue;
	
	/**
	 * Dieses Attribut speichert den Typen der Variablen.
	 */
	private String sType;
	
	
	
	/**
	 * Konstruktor der Klasse "Atom".
	 * Erzeugt ein neues Atom mit den als Parametern angegebenen Werten.
	 * 
	 * @param psName	Bezeichner der Variablen.
	 * @param psValue	Wert der Variablen als String.
	 * @param psType	Typ der Variablen.
	 */
	public Atom(String psName, String psValue, String psType) {
		sName = psName;
		sValue = psValue;
		sType = psType;
	}
	
	
	
	/**
	 * Gibt den Bezeichner der Variablen zurueck.
	 * 
	 * @return	Bezeichner.
	 */
	public String getName() {
		return sName;
	}
	
	/**
	 * Gibt den Wert der Variablen zurueck.
	 * 
	 * @return	Inhalt.
	 */
	public String getValue() {
		return sValue;
	}
	
	/**
	 * Gibt den Typen der Variablen zurueck.
	 * 
	 * @return	Typ.
	 */
	public String getType() {
		return sType;
	}
	
	
	
	/**
	 * Aendert den Wert der Variablen.
	 * 
	 * @param psValue	Neuer Inhalt.
	 */
	public void setValue(String psValue) {
		sValue = psValue;
	}
	
	/**
	 * Aendert den Typen der Variablen.
	 * 
	 * @param psType	Neuer Typ.
	 */
	public void setType(String psType) {
		sType = psType;
	}
}
