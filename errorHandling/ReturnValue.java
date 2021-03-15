package errorHandling;



/**
 * Objekte dieser Klasse werden fuer Rueckgaben verwendet. Die verwalten ein Inhaltsobjekt
 * vom Typ ContentType. Zusaetzlich besitzt jedes Objekt dieser Klasse einen Wert vom Typ
 * int, welcher eine Information ueber die Ausfuehrung einer Methode enthaelt.
 * 
 * @author	Christian S
 * @version	13.02.2021
 */
public class ReturnValue<ContentType> {
	/**
	 * Speichert einen Wert, welcher Auskunft ueber die Ausfuehrung einer
	 * Methode gibt.
	 */
	private int nExecutionInformation;
	
	/**
	 * Speichert den Rueckgabewert.
	 */
	private ContentType cReturnValue;
	
	
	
	/**
	 * Standartkonstruktor der Klasse "CReturnValue".
	 * Erzeugt einen leeren Rueckgabewert, bei welchem von einem unbekannten
	 * Fehler ausgegangen wird.
	 */
	public ReturnValue() {
		cReturnValue = null;
		nExecutionInformation = ReturnValueTypes.ERROR_UNKNOWN;
	}
	
	
	
	/**
	 * Konstruktor der Klasse "CReturnValue".
	 * Erzeugt einen Rueckgabewert mit dem Parameter pcReturnValue als Rueckgabewert
	 * vom Typ ContentType und dem Parameter pnExecutionInformation als zusaetzliche
	 * Information ueber die Ausfuehrung einer Methode.
	 * 
	 * @param pcReturnValue				Rueckgabewert, welcher von diesem Objekt
	 * 									verwaltet werden soll.
	 * @param pnExecutionInformation	Zusatzinformation ueber die Ausfuehrung
	 * 									einer Methode.
	 */
	public ReturnValue(ContentType pcReturnValue, int pnExecutionInformation) {
		cReturnValue = pcReturnValue;
		nExecutionInformation = pnExecutionInformation;
	}
	
	
	
	/**
	 * Gibt die Zusatzinformation ueber die Ausfuehrung einer Methode zurueck.
	 * 
	 * @return	Zusatzinformation.
	 */
	public int getExecutionInformation() {
		return nExecutionInformation;
	}
	
	/**
	 * Gibt den Rueckgabewert der Methode zurueck.
	 * 
	 * @return	Rueckgabewert.
	 */
	public ContentType getReturnValue() {
		return cReturnValue;
	}
}
