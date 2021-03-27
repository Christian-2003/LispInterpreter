package interpreter;

import java.util.LinkedList;

import tokenizer.*;
import errorHandling.*;



/**
 * Ein Objekt dieser Klasse ist dazu geeignet Klassen, welche im Lisp-Quellcode definiert werden, zu speichern
 * und auch zu verwalten.
 * 
 * @author	Christian S
 * @version	14.03.2021
 */
public class Class {
	/**
	 * Speichert den Bezeichner der Klasse.
	 */
	private String sName;
	
	/**
	 * Speichert eine Liste an privaten Attributen der Klasse.
	 */
	private LinkedList<Atom> lPrivateAttributesObj;
	
	/**
	 * Speichert eine Liste an privaten Funktionen der Klasse.
	 */
	private LinkedList<Function> lPrivateFunctionsObj;
	
	/**
	 * Speichert eine Liste an oeffentlichen Attributen.
	 */
	private LinkedList<Atom> lPublicAttributesObj;
	
	/**
	 * Speichert eine Liste an oeffentlichen Funktionen.
	 */
	private LinkedList<Function> lPublicFunctionsObj;
	
	
	
	/**
	 * Konstruktor erstellt eine neue Klasse, welche genutzt wird um alle wichtigen Werte einer Klasse zu speichern.
	 * 
	 * @param plTokensObj	Ausdruck, welcher die Klassendefinition darstellt (alle Tokens ab einsschliesslich dem Namen).
	 */
	public Class(LinkedList<Token> plTokensObj) {
		lPrivateAttributesObj = new LinkedList<Atom>();
		lPrivateFunctionsObj = new LinkedList<Function>();
		lPublicAttributesObj = new LinkedList<Atom>();
		lPublicFunctionsObj = new LinkedList<Function>();
		
		//Den Bezeichner der Klasse identifizieren:
		sName = plTokensObj.poll().getValue();
		
		//Alle privaten Attribute und Methoden herausarbeiten:
		plTokensObj.poll(); //Erste geoeffnete Klammer entfernen.
		int nBracketsClosed = 0;
		int nBracketsOpened = 1;
		while (!plTokensObj.isEmpty()) {
			Token currentTokenObj = plTokensObj.poll();
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
				//Neuer privater Ausdruck:
				if (plTokensObj.peek().getValue().equals(KeywordTypes.KEYWORD_VAR)) {
					//Neues Attribut:
					plTokensObj.poll();
					lPrivateAttributesObj.add(new Atom(plTokensObj.poll().getValue(), "0.00", TokenTypes.TOKEN_NUMBER));
				}
				else if (plTokensObj.peek().getValue().equals(KeywordTypes.KEYWORD_DEFINE)) {
					//Neue Methode:
					plTokensObj.poll();
					int nInnerBracketsClosed = 0;
					int nInnerBracketsOpened = 1;
					LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>();
					while (!plTokensObj.isEmpty()) {
						Token currentInnerTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
						if (currentInnerTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
							nInnerBracketsOpened++;
						}
						if (currentInnerTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
							nInnerBracketsClosed++;
						}
						lFunctionTokensObj.add(currentInnerTokenObj);
						if (nInnerBracketsClosed == nInnerBracketsOpened) {
							//Die Ausdruecke der Funktion wurden herausgefunden:
							lPrivateFunctionsObj.add(new Function(lFunctionTokensObj));
							break;
						}
					}
				}
			}
			if (nBracketsOpened == nBracketsClosed) {
				//Alle privaten Attribute und Methoden herausgearbeitet:
				break;
			}
		}
		
		//Alle oeffentlichen Attribute und Methoden herausarbeiten:
		plTokensObj.poll(); //Erste geoeffnete Klammer entfernen.
		nBracketsClosed = 0;
		nBracketsOpened = 1;
		while (!plTokensObj.isEmpty()) {
			Token currentTokenObj = plTokensObj.poll();
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
				//Neuer privater Ausdruck:
				if (plTokensObj.peek().getValue().equals(KeywordTypes.KEYWORD_VAR)) {
					//Neues Attribut:
					plTokensObj.poll();
					lPublicAttributesObj.add(new Atom(plTokensObj.poll().getValue(), "0.00", TokenTypes.TOKEN_NUMBER));
				}
				else if (plTokensObj.peek().getValue().equals(KeywordTypes.KEYWORD_DEFINE)) {
					//Neue Methode:
					plTokensObj.poll();
					int nInnerBracketsClosed = 0;
					int nInnerBracketsOpened = 1;
					LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>();
					while (!plTokensObj.isEmpty()) {
						Token currentInnerTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
						if (currentInnerTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
							nInnerBracketsOpened++;
						}
						if (currentInnerTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
							nInnerBracketsClosed++;
						}
						lFunctionTokensObj.add(currentInnerTokenObj);
						if (nInnerBracketsClosed == nInnerBracketsOpened) {
							//Die Ausdruecke der Funktion wurden herausgefunden:
							lPublicFunctionsObj.add(new Function(lFunctionTokensObj));
							break;
						}
					}
				}
			}
			if (nBracketsOpened == nBracketsClosed) {
				//Alle oeffentlichen Attribute und Methoden herausgearbeitet:
				break;
			}
		}
		
		/*
		//-------------------------- DEBUG --------------------------------------
		System.out.println("NEW CLASS DEFINED");
		for (int i = 0; i < lPrivateAttributesObj.size(); i++) {
			System.out.println("private attribute>" + lPrivateAttributesObj.get(i).getName());
		}
		System.out.println();
		for (int i = 0; i < lPrivateFunctionsObj.size(); i++) {
			System.out.println("private function>" + lPrivateFunctionsObj.get(i).getName());
		}
		System.out.println();
		for (int i = 0; i < lPublicAttributesObj.size(); i++) {
			System.out.println("public attribute>" + lPublicAttributesObj.get(i).getName());
		}
		System.out.println();
		for (int i = 0; i < lPublicFunctionsObj.size(); i++) {
			System.out.println("public function>" + lPublicFunctionsObj.get(i).getName());
		}
		System.out.println();
		//-------------------------- DEBUG --------------------------------------
		*/
	}
	
	/**
	 * Konstruktor erzeugt eine neue leere Klasse, ohne Inhalt.
	 */
	public Class() {
		sName = "";
		lPrivateAttributesObj = new LinkedList<Atom>();
		lPrivateFunctionsObj = new LinkedList<Function>();
		lPublicAttributesObj = new LinkedList<Atom>();
		lPublicFunctionsObj = new LinkedList<Function>();
	}
	
	
	
	/**
	 * Diese Methode gibt den Bezeichner der Klasse zurueck.
	 * 
	 * @return	Bezeichner der Klasse.
	 */
	public String getName() {
		return sName;
	}
	
	/**
	 * Diese Methode gibt alle privaten Attribute zurueck.
	 * 
	 * @return	Liste der privaten Attribute.
	 */
	public LinkedList<Atom> getPrivateAttributes() {
		return lPrivateAttributesObj;
	}
	
	/**
	 * Diese Methode gibt alle oeffentlichen Attribute zurueck.
	 * 
	 * @return	Liste der oeffentlichen Attribute.
	 */
	public LinkedList<Atom> getPublicAttributes() {
		return lPublicAttributesObj;
	}
	
	/**
	 * Diese Methode gibt alle privaten Methoden zurueck.
	 * 
	 * @return	Liste der privaten Methoden.
	 */
	public LinkedList<Function> getPrivateFunctions() {
		return lPrivateFunctionsObj;
	}
	
	/**
	 * Diese Methode gibt alle oeffentlichen Methoden zurueck.
	 * 
	 * @return	Liste der oeffentlichen Methoden.
	 */
	public LinkedList<Function> getPublicFunctions() {
		return lPublicFunctionsObj;
	}
	
	/**
	 * Diese Methode gibt die oeffentliche Funktion zurueck, welche den als Parameter angegebenen Namen traegt.
	 * 
	 * @param psFunctionName	Name der gesuchten Funktion.
	 * @return					Falls vorhanden, gesuchte Funktion.
	 */
	public ReturnValue<Function> getPublicFunctionByName(String psFunctionName) {
		for (int i = 0; i < lPublicFunctionsObj.size(); i++) {
			if (lPublicFunctionsObj.get(i).getName().equals(psFunctionName)) {
				return new ReturnValue<Function>(lPublicFunctionsObj.get(i), ReturnValueTypes.SUCCESS);
			}
		}
		return new ReturnValue<Function>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
	}
	
	/**
	 * Diese Methode gibt die private Funktion zurueck, welche den als Parameter angegebenen Namen traegt.
	 * 
	 * @param psFunctionName	Name der gesuchten Funktion.
	 * @return					Falls vorhanden, gesuchte Funktion.
	 */
	public ReturnValue<Function> getPrivateFunctionByName(String psFunctionName) {
		for (int i = 0; i < lPrivateFunctionsObj.size(); i++) {
			if (lPrivateFunctionsObj.get(i).getName().equals(psFunctionName)) {
				return new ReturnValue<Function>(lPrivateFunctionsObj.get(i), ReturnValueTypes.SUCCESS);
			}
		}
		return new ReturnValue<Function>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
	}
}
