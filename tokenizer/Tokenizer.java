package tokenizer;

import java.util.LinkedList;



/**
 * Tokenizer fuer den Lisp-Interpreter.
 * 
 * @version	06.01.2021
 * @author	Christian S
 */
public class Tokenizer {
	/**
	 * Speichert die Tokens.
	 */
	private LinkedList<Token> lTokens;
	
	/**
	 * Speichert den Quellcode, welcher zerlegt werden soll.
	 */
	private String sSourceCode;
	
	
	
	/**
	 * Findet die geoeffnete Klammer im Quellcode heraus und fuegt diese der Liste an Tokens hinzu.
	 * 
	 * @param pnCurrent		Position im Quellcode, an welcher sich die geoeffnete Klammer befindet beginnt.
	 * 
	 * @return				Position, an welcher die geoeffnete Klammer endet.
	 */
	private int tokenizeBracketOpened(int pnPosition) {
		Token currentTokenObj = new Token(String.valueOf(sSourceCode.charAt(pnPosition)), TokenTypes.TOKEN_BRACKET_OPENED);
		lTokens.add(currentTokenObj);
		return pnPosition;
	}
	
	/**
	 * Findet die geschlossene Klammer im Quellcode heraus und fuegt diese der Liste an Tokens hinzu.
	 * 
	 * @param pnCurrent		Position im Quellcode, an welcher sich die geschlossene Klammer befindet beginnt.
	 * 
	 * @return				Position, an welcher die geschlossene Klammer endet.
	 */
	private int tokenizeBracketClosed(int pnPosition) {
		Token currentTokenObj = new Token(String.valueOf(sSourceCode.charAt(pnPosition)), TokenTypes.TOKEN_BRACKET_CLOSED);
		lTokens.add(currentTokenObj);
		return pnPosition;
	}
	
	/**
	 * Findet den Operator im Quellcode heraus und fuegt diese der Liste an Tokens hinzu.
	 * 
	 * @param pnCurrent		Position im Quellcode, an welcher sich der Operator befindet beginnt.
	 * 
	 * @return				Position, an welcher der Operator endet.
	 */
	private int tokenizeOperator(int pnPosition) {
		if (sSourceCode.charAt(pnPosition) =='-') {
			//Bei dem Zeichen handelt es sich um ein Minus:
			if (sSourceCode.charAt(pnPosition + 1) >= 48 && sSourceCode.charAt(pnPosition + 1) <= 57) {
				//Bei dem naechsten Zeichen im Quellcode handelt es sich um eine Ziffer einer Zahl. Das Minus ist kein
				//Operator, sondern ein vorzeichen:
				return tokenizeNumber(pnPosition);
			}
			
		}
		
		Token currentTokenObj = new Token(String.valueOf(sSourceCode.charAt(pnPosition)), TokenTypes.TOKEN_OPERATOR);
		lTokens.add(currentTokenObj);
		return pnPosition;
	}
	
	/**
	 * Findet den Operator im Quellcode heraus und fuegt diese der Liste an Tokens hinzu.
	 * 
	 * @param pnCurrent		Position im Quellcode, an welcher sich der Operator befindet beginnt.
	 * 
	 * @return				Position, an welcher der Operator endet.
	 */
	private int tokenizeOperatorBoolean(int pnPosition) {
		Token currentTokenObj = new Token(String.valueOf(sSourceCode.charAt(pnPosition)), TokenTypes.TOKEN_OPERATOR_BOOLEAN);
		lTokens.add(currentTokenObj);
		return pnPosition;
	}
	
	/**
	 * Findet die Zahl im Quellcode heraus und fuegt diese der Liste an Tokens hinzu.
	 * 
	 * @param pnCurrent		Position im Quellcode, an welcher sich die Zahl befindet beginnt.
	 * 
	 * @return				Position, an welcher die Zahl endet.
	 */
	private int tokenizeNumber(int pnPosition) {
		String sNumber = ""; //Speichert die Zahl als String.
		boolean bFoundComma = false; //Speichert, ob bereits ein Komma gefunden wurde.
		
		//Quellcode Zeichenweise durchlaufen:
		for (int i = pnPosition; i < sSourceCode.length(); i++) {
			char chCurrentCharacter = sSourceCode.charAt(i);
			
			if (chCurrentCharacter == '-' && i == pnPosition) {
				//Es handelt sich um ein Minuszeichen an erster Stelle der Zahl (Vorzeichen):
				sNumber += chCurrentCharacter;
			}
			else if (chCurrentCharacter >= 48 && chCurrentCharacter <= 57) {
				//Es handelt sich um eine Ziffer der Zahl:
				sNumber += chCurrentCharacter;
			}
			else if (chCurrentCharacter == 46) {
				//Es handelt sich um ein Kommazeichen:
				if (bFoundComma) {
					//Es wurde bereits zuvor ein Komma gefunden:
					lTokens.add(new Token(sNumber, TokenTypes.TOKEN_NUMBER));
					return i - 1;
				}
				else {
					//Es wurde noch kein Komma gefunden:
					sNumber += chCurrentCharacter;
					bFoundComma = true;
				}
			}
			else {
				//Ende der Zahl:
				lTokens.add(new Token(sNumber, TokenTypes.TOKEN_NUMBER));
				return i - 1;
			}
		}
		
		lTokens.add(new Token(sNumber, TokenTypes.TOKEN_NUMBER));
		return sSourceCode.length();
	}
	
	/**
	 * Findet den Bezeichner im Quellcode heraus und fuegt diesen der Liste an Tokens hinzu.
	 * 
	 * @param pnCurrent		Position im Quellcode, an welcher sich der Bezeichner befindet beginnt.
	 * 
	 * @return				Position, an welcher der Bezeichner endet.
	 */
	private int tokenizeIdentifier(int pnPosition) {
		String sIdentifier = ""; //Speichert den Bezeichner.
		
		//Quellcode Zeichenweise durchlaufen:
		for (int i = pnPosition; i < sSourceCode.length(); i++) {
			char chCurrentCharacter = sSourceCode.charAt(i); //Aktuelles Zeichen des Bezeichners.
			
			if (chCurrentCharacter == '(' || chCurrentCharacter == ')' || chCurrentCharacter == ' ' || chCurrentCharacter == ';') {
				//Ende des Bezeichners:
				sIdentifier = sIdentifier.toLowerCase(); //-> Damit Gross- / Kleinschreibung nicht "wichtig" ist.
				Token currentTokenObj;
				//Ueberpruefen, ob es sich beim aktuellen Bezeichner um ein Schluesselwort handelt:
				if (sIdentifier.equals(KeywordTypes.KEYWORD_VAR)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.KEYWORD_SETF)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.KEYWORD_PRINT)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.KEYWORD_PRINTLN)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.KEYWORD_IF)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.KEYWORD_WHILE)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.KEYWORD_SCAN)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.KEYWORD_DEFINE)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
				}
				else if (sIdentifier.equals(KeywordTypes.BOOLEAN_T)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_BOOLEAN);
				}
				else if (sIdentifier.equals(KeywordTypes.BOOLEAN_F)) {
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_BOOLEAN);
				}
				else {
					//Es handelt sich nicht um ein Schluesselwort:
					currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_IDENTIFIER);
				}
				lTokens.add(currentTokenObj);
				return i - 1;
			}
			
			sIdentifier += chCurrentCharacter;
		}
		
		Token currentTokenObj;
		sIdentifier = sIdentifier.toLowerCase(); //-> Damit Gross- / Kleinschreibung nicht "wichtig" ist.
		//Ueberpruefen, ob es sich beim aktuellen Bezeichner um ein Schluesselwort handelt:
		if (sIdentifier.equals(KeywordTypes.KEYWORD_VAR)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.KEYWORD_SETF)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.KEYWORD_PRINT)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.KEYWORD_PRINTLN)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.KEYWORD_IF)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.KEYWORD_WHILE)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.KEYWORD_SCAN)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.KEYWORD_DEFINE)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_KEYWORD);
		}
		else if (sIdentifier.equals(KeywordTypes.BOOLEAN_T)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_BOOLEAN);
		}
		else if (sIdentifier.equals(KeywordTypes.BOOLEAN_F)) {
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_BOOLEAN);
		}
		else {
			//Es handelt sich nicht um ein Schluesselwort:
			currentTokenObj = new Token(sIdentifier, TokenTypes.TOKEN_IDENTIFIER);
		}
		lTokens.add(currentTokenObj);
		return sSourceCode.length();
	}
	
	/**
	 * Findet den String im Quellcode heraus und fuegt diesen der Liste an Tokens hinzu.
	 * 
	 * @param pnCurrent		Position im Quellcode, an welcher der String beginnt.
	 * 
	 * @return				Position, an welcher der String endet.
	 */
	private int tokenizeString(int pnPosition) {
		String sString = "" + sSourceCode.charAt(pnPosition); //Speichert den String, inklusive Anfuehrungszeichen.
		
		//Restlcihen String herausfinden:
		for (int i = pnPosition + 1; i < sSourceCode.length(); i++) {
			char chCurrentCharacter = sSourceCode.charAt(i); //Speichert das aktuelle Zeichen im String.
			sString += chCurrentCharacter;
			
			if (chCurrentCharacter == '\"') {
				//Ende des Strings:
				Token currentTokenObj = new Token(sString.substring(1, sString.length() - 1), TokenTypes.TOKEN_STRING); //Neuen Token erstellen.
				lTokens.add(currentTokenObj);
				return i;
			}
		}
		
		return sSourceCode.length();
	}
	
	
	
	/**
	 * Standartkonstruktor der Klasse "CTokenizer".
	 */
	public Tokenizer() {
		lTokens = new LinkedList<Token>();
	}
	
	
	
	/**
	 * Zerlegt den Quellcode in Tokens.
	 * 
	 * @param psSourceCode	Quellcode, welcher zerlegt werden soll
	 * @return				Liste an Tokens.
	 */
	public LinkedList<Token> tokenize(String psSourceCode) {
		lTokens.clear();
		sSourceCode = psSourceCode;
		
		//Quellcode Zeichenweise durchlaufen:
		for (int i = 0; i < sSourceCode.length(); i++) {
			char chCurrentCharacter = sSourceCode.charAt(i); //Speichert das aktuelle Zeichen im Quellcode.
			
			//Aktuelles Zeichen verarbeiten.
			if (chCurrentCharacter == '(') {
				//Geoeffnete Klammer:
				i = tokenizeBracketOpened(i);
			}
			
			else if (chCurrentCharacter == ')') {
				//Geschlossene Klammer:
				i = tokenizeBracketClosed(i);
			}
			
			else if (chCurrentCharacter == ' ') {
				//Leerzeichen -> naechster Token:
				continue;
			}
			
			else if (chCurrentCharacter == ';') {
				//Kommentar -> Methode beenden:
				return lTokens;
			}
			
			else if (chCurrentCharacter == '+' || chCurrentCharacter == '-' || chCurrentCharacter == '*' || chCurrentCharacter == '/') {
				//Operator:
				i = tokenizeOperator(i);
			}
			
			else if (chCurrentCharacter == '=' || chCurrentCharacter == '<' || chCurrentCharacter == '>' || chCurrentCharacter == '!') {
				//Boolescher Operator:
				i = tokenizeOperatorBoolean(i);
			}
			
			else if (chCurrentCharacter == '\"') {
				//String
				i = tokenizeString(i);
			}
			
			else if (chCurrentCharacter >= 48 && chCurrentCharacter <= 57) {
				//Zahl:
				i = tokenizeNumber(i);
			}
			
			else {
				//Bezeichner:
				i = tokenizeIdentifier(i);
			}
		}
		
		return lTokens;
	}
}
