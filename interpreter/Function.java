package interpreter;

import tokenizer.*;

import java.util.LinkedList;

import errorHandling.*;



/**
 * Objekte dieser Klasse speichern alle wichtigen Elemente einer Funktion.
 * 
 * @author	Christian S
 * @version	16.03.2021
 */
public class Function {
	/**
	 * Speichert den Namen der Funktion.
	 */
	private String sName;
	
	/**
	 * Speichert die Liste an Parametern
	 */
	private LinkedList<Atom> lParametersObj;
	
	/**
	 * Speichert alle Ausdruecke als Tokens
	 */
	private LinkedList<LinkedList<Token>> llExpressionsObj;
	
	
	
	/**
	 * Konstruktor der Klasse "Function" erstellt eine neue Funktion, die genutzt wird um alle wichtigen Werte
	 * einer Funktion zu speichern.
	 * 
	 * @param plFunctionObj	Ausdruck, welcher die Funktionsdefinition darstellt (alle Tokens ab einschliesslich dem Namen).
	 */
	public Function(LinkedList<Token> plFunctionObj) {
		lParametersObj = new LinkedList<Atom>();
		llExpressionsObj = new LinkedList<LinkedList<Token>>();
		
		int nBracketsOpened = 0;
		int nBracketsClosed = 0;
		
		//Den Namen der Funktion identifizieren:
		sName = plFunctionObj.poll().getValue();
		
		//Liste an Parametern identifizieren:
		while (!plFunctionObj.isEmpty()) {
			Token currentTokenObj = plFunctionObj.poll();
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			if (nBracketsClosed == nBracketsOpened) {
				//Es wurde gleich viele Klammern geoeffnet und geschlossen:
				nBracketsOpened = 0;
				nBracketsClosed = 0;
				break;
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
				//Aktueller Token ist ein Bezeichner (Parameter):
				lParametersObj.add(new Atom(currentTokenObj.getValue(), "0.00", TokenTypes.TOKEN_NUMBER));
			}
		}
		
		//Liste an Ausdruecken identifizieren:
		plFunctionObj.poll(); //Erste geoeffnete Klammer entfernen.
		LinkedList<Token> lCurrentExpressionObj = new LinkedList<Token>(); //Speichert den aktuellen Ausdruck.
		while (!plFunctionObj.isEmpty()) {
			Token currentTokenObj = plFunctionObj.poll();
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			lCurrentExpressionObj.add(currentTokenObj);
			if ((nBracketsClosed == nBracketsOpened) && (nBracketsClosed != 0 && nBracketsOpened != 0)) {
				//Ein neuer Ausdruck gefunden:
				llExpressionsObj.add(new LinkedList<Token>(lCurrentExpressionObj));
				lCurrentExpressionObj.clear();
				System.out.println();
			}
			if (nBracketsClosed == nBracketsOpened + 1) {
				//Alle Ausdrücke wurden herausgearbeitet:
				break;
			}
		}
		/*
		//-------------------------- DEBUG --------------------------------------
		System.out.println("NEW FUNCTION DEFINED");
		System.out.println("name>" + sName);
		System.out.print("parameters>");
		for (int i = 0; i < lParametersObj.size(); i++) {
			System.out.print(lParametersObj.get(i).getName());
			if (i != lParametersObj.size() - 1) {
				System.out.print(",");
			}
		}
		System.out.println();
		for (int i = 0; i < llExpressionsObj.size(); i++) {
			System.out.print("expression" + i + ">");
			for (int j = 0; j < llExpressionsObj.get(i).size(); j++) {
				System.out.print(llExpressionsObj.get(i).get(j).getValue() + " ");
			}
			System.out.println();
		}
		//-------------------------- DEBUG --------------------------------------
		*/
	}
	
	/**
	 * Standartkonstruktor der Klasse "Function" erstellt eine leere Funktion, ohne Quellcode und Parameter.
	 */
	public Function() {
		lParametersObj = new LinkedList<Atom>();
		llExpressionsObj = new LinkedList<LinkedList<Token>>();
	}
	
	
	
	/**
	 * Diese Methode gibt den Bezeichner der Funktion zurueck.
	 * 
	 * @return	Name der Funktion.
	 */
	public String getName() {
		return sName;
	}
	
	/**
	 * Diese Methode gibt die Liste an Parametern zurueck.
	 * 
	 * @return	Liste der Parameter
	 */
	public LinkedList<Atom> getParameters() {
		return lParametersObj;
	}
	
	/**
	 * Diese Methode gibt die Anzahl an Parametern zurueck.
	 * 
	 * @return	Anzahl der Parameter.
	 */
	public int getParameterAmount() {
		return lParametersObj.size();
	}
	
	/**
	 * Diese Methode gibt den Ausdruck mit dem Index nIndex als Token-Liste zurueck.
	 * 
	 * @param pnIndex	Index des Ausdrucks.
	 * 
	 * @return			Ausdruck als Token-Liste.
	 */
	public LinkedList<Token> getExpression(int pnIndex) {
		return llExpressionsObj.get(pnIndex);
	}
	
	/**
	 * Diese Methode gibt die Anzahl der Ausdruecke innerhalb der Funktion zurueck.
	 * 
	 * @return	Anzahl der Ausdruecke.
	 */
	public int getExpressionAmount() {
		return llExpressionsObj.size();
	}
}
