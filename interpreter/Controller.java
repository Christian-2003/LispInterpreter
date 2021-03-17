package interpreter;

import java.util.LinkedList;
import java.util.Scanner;

import errorHandling.*;
import parser.*;
import tokenizer.*;
import fileScanner.*;



/**
 * Ein Objekt dieser Klasse verwaltet den Quellcode. Es entscheidet, was mit einem Ausdruck weiterfuehrend
 * passiert, ob es in einen abstrakten Syntaxbaum umgewandelt wird, und so weiter.
 * Zudem ist diese Klasse dafuer zustaendig die Schluesselwoerter auszuwerten.
 * 
 * @author	Christian S
 * @version	13.02.2021
 */
public class Controller {
	/**
	 * Dieses Attribut stellt den Tokenizer dar, welcher aus dem Quellcode eine Liste an Tokens ertsellt.
	 */
	private Tokenizer tokenizerObj;
	
	/**
	 * Dieses Attribut stellt den Parser dar, welcher aus einem Ausdruck einen abstrakten Syntaxbaum
	 * erstellt.
	 */
	private Parser parserObj;
	
	/**
	 * Dieses Attribut stellt den Interpreter dar, welcher einen abstrakten Syntaxbaum auswertet. Zudem
	 * verwaltet der Interpreter eine Liste an verfuegbaren Variablen.
	 */
	private Interpreter interpreterObj;
	
	/**
	 * Dieses Attribut speichert den Quellcode. Jeder Ausdruck stellt ein Element in der Liste dar.
	 */
	private LinkedList<String> lsSourceCode;
	
	/**
	 * Dieses Attribut speichert alle Funktionen.
	 */
	private LinkedList<Function> lFunctionsObj;
	
	
	
	/**
	 * Konstruktor der Klasse "Controller"
	 * Erstellt einen neuen Controller welcher den Quellcode, der in der als Parameter angegebenen Datei
	 * gespeichert wird, verarbeitet.
	 * 
	 * @param psFileName	Datei, in welcher der Quellcode gespeichert wird.
	 */
	public Controller(String psFileName) {
		tokenizerObj = new Tokenizer();
		parserObj = new Parser();
		interpreterObj = new Interpreter();
		lsSourceCode = new LinkedList<String>();
		lFunctionsObj = new LinkedList<Function>();
		
		ReturnValue<LinkedList<String>> outputFileScannerObj = new ReturnValue<LinkedList<String>>();
		outputFileScannerObj = FileScanner.readFile(psFileName);
		if (outputFileScannerObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es kam zu einem Fehler:
			printErrorMessage("error> ", outputFileScannerObj.getExecutionInformation(), "");
			System.exit(0); //Programm beenden.
		}
		lsSourceCode.addAll(outputFileScannerObj.getReturnValue());
	}
	
	
	
	/**
	 * Diese Methode startet die Rekursion, bei welcher der Quellcode schrittweise ausgefuehrt wird.
	 */
	public void startController() {
		//Quellcode Ausdruck fuer Ausdruck durchlaufen:
		for (int i = 0; i < lsSourceCode.size(); i++) {
			ReturnValue<Object> returnObj = new ReturnValue<Object>(); //Rueckgabewert des Tokenizers.
			
			LinkedList<Token> lTokensObj = new LinkedList<Token>(); //Speichert den aktuellen Ausdruck im Quellcode als Tokens.
			lTokensObj.addAll(tokenizerObj.tokenize(lsSourceCode.get(i)));
			
			//Jeden Ausdruck in eine Funktion umwandeln:
			Token tokenObj = lTokensObj.poll();
			if (!tokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				//Der erste Token ist keien geoeffnete Klammer: Syntaxfehler
				printErrorMessage("error> ", ReturnValueTypes.ERROR_SYNTAX, "");
				return;
			}
			tokenObj = lTokensObj.poll();
			if (!tokenObj.getValue().equals(KeywordTypes.KEYWORD_DEFINE)) {
				//Erstes Schluesselwort ist nicht "defun": Dyntaxfehler
				printErrorMessage("error> ", ReturnValueTypes.ERROR_SYNTAX, "");
				return;
			}
			Function currentFunctionObj = new Function(lTokensObj);
			lFunctionsObj.add(currentFunctionObj);
		}
		
		//Herausfinden, mit welcher Funktion gestartet werden soll.
		for (int i = 0; i < lFunctionsObj.size(); i++) {
			if (lFunctionsObj.get(i).getName().equals(KeywordTypes.FUNCTION_MAIN)) {
				//Startfunktion gefunden:
				LinkedList<Atom> lParameterObj = new LinkedList<Atom>();
				lParameterObj = lFunctionsObj.get(i).getParameters();
				if (lParameterObj.size() != 0) {
					//Es sind Parameter vorhanden: Syntaxfehler (main-Funktion erhaelt keine Parameter).
					printErrorMessage("error> ", ReturnValueTypes.ERROR_MAIN_FUNCTION_HAS_PARAMETER, " no parameters are allowed.");
					return;
				}
				
				for (int j = 0; j < lFunctionsObj.get(i).getExpressionAmount(); j++) {
					//Ausdruecke verarbeiten:
					ReturnValue<Object> processReturnObj = process(lFunctionsObj.get(i).getExpression(j));
					if (processReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es kam zu einem Fehler:
						printErrorMessage("error> ", processReturnObj.getExecutionInformation(), "");
						return;
					}
				}
				return; //Beenden, nachdem alle Ausdruecke verarbeitet wurden.
			}
		}
		//Startfunktion nicht gefunden:
		printErrorMessage("fatalError> ", ReturnValueTypes.ERROR_NO_MAIN_FUNCTION, "");
		return;
	}
	
	
	
	/**
	 * Diese Methode verarbeitet jeweils einen Ausdruck des Quellcodes. Dabei wird das Schluesselwort verwertet und dementsprechend
	 * werden weiteren Schritte eingeleitet.
	 * 
	 * @param plTokensObj	Ausdruck im Quellcode, welcher verarbeitet werden soll als Liste von Tokens.
	 */
	private ReturnValue<Object> process(LinkedList<Token> plTokensObj) {
		//Ueberpruefen, ob gleich viele Klammern geoeffnet und geschlossen werden:
		int nOpenBrackets = 0; //Speichert die Anzahl der geoeffneten Klammern.
		int nCloseBrackets = 0; //Speichert die Anzahl der geschlossenen Klammern.
		for (int i = 0; i < plTokensObj.size(); i++) {
			if (plTokensObj.get(i).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nOpenBrackets++;
			}
			else if (plTokensObj.get(i).getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nCloseBrackets++;
			}
		}
		if (nOpenBrackets != nCloseBrackets) {
			//Es wurden nicht gleich viele Klammern geoeffnet und geschlossen:
			return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		
		//Da ein Ausdruck in Lisp immer mit einer geoeffneten Klammer beginnt, kann diese entfernt werden:
		if (!plTokensObj.poll().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
			//Das erste Element ist keine geoeffnete Klammer -> SYNTAX FEHLER:
			return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		
		//Herausfinden, was der erste Token (Schluesselwort) darstellt:
		Token firstTokenObj = new Token("", ""); //Speichert das Schluesselwort.
		firstTokenObj = plTokensObj.poll();
		
		if (firstTokenObj.getType().equals(TokenTypes.TOKEN_KEYWORD)) {
			//Der erste Token ist ein Schluesselwort (z.B. "setf"):
			if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_VAR)) {
				//Es soll eine neue Variable deklariert werden:
				Token variableName = new Token(null, null); //Speichert den Token des Variablennamens.
				variableName = plTokensObj.poll();
				if (variableName.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
					//Beim Variablennamen handelt es sich um einen Bezeichner:
					boolean bVariableIsAddedSuccessfully = false; //Speichert, ob der Variablenname hinzugefuegt wurde.
					Atom atom = new Atom(variableName.getValue(), "0.0", TokenTypes.TOKEN_NUMBER); //Neues Atom hat standartmaessig den Wert 0.0.
					bVariableIsAddedSuccessfully = interpreterObj.addAtom(atom);
					if (bVariableIsAddedSuccessfully) {
						//Atom wurde erfolgreich hinzugefuegt:
						return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
					}
					else {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_VARIABLE_NAME_DOES_EXIST);
					}
				}
				else {
					//Beim Variablennamen handelt es sich nicht um einen Bezeichner -> SYNTAX FEHLER:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
				}
			}
			
			else if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_SETF)) {
				//Der Wert einer Variablen soll geaendert werden:
				Token variableNameObj = new Token(null, null); //Speichert den Token des Variablennamens.
				variableNameObj = plTokensObj.poll();
				Token variableValueObj = new Token(null, null); //Speichert den neuen Wert der Variablen.
				variableValueObj = plTokensObj.peek();
				Atom newAtomObj;
				if (variableValueObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Es handelt sich um eine Subrechnung:
					ReturnValue<String> calculationReturn = new ReturnValue<String>(); //Speichert den Rueckgabewert der Rechnung.
					calculationReturn = calculate(plTokensObj);
					if (calculationReturn.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<Object>(null, calculationReturn.getExecutionInformation());
					}
					newAtomObj = new Atom(variableNameObj.getValue(), calculationReturn.getReturnValue(), TokenTypes.TOKEN_NUMBER);
				}
				else if (variableValueObj.getType().equals(TokenTypes.TOKEN_STRING) || variableValueObj.getType().equals(TokenTypes.TOKEN_NUMBER) || variableValueObj.getType().equals(TokenTypes.TOKEN_BOOLEAN)) {
					//Es handelt sich um einen String oder eine Nummer oder einen Wahrheitswert:
					newAtomObj = new Atom(variableNameObj.getValue(), variableValueObj.getValue(), variableValueObj.getType());
				}
				else {
					//Es ist ein nicht angebrachter Token vorhanden -> SYNTAX FEHLER:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
				}
				boolean bVariableIsChangedSuccessfully = false; //Speichert, ob der Wert der Variablen erfolgreich geaendert wurde.
				bVariableIsChangedSuccessfully = interpreterObj.overrideAtom(newAtomObj);
				if (bVariableIsChangedSuccessfully) {
					//Wert wurde erfolgreich geaendert:
					return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
				}
				else {
					//Es ist ein Fehler aufgetreten:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
				}
			}
			
			else if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_PRINT)) {
				Token printTokenObj = new Token(null, null); //Speichert den Token, welcher ausgegeben werden soll.
				printTokenObj = plTokensObj.poll();
				String sPrint = ""; //Speichert den Inhalt, welcher in der Konsole ausgegeben werden soll.
				if (printTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
					//Es handelt sich um einen Variablennamen:
					ReturnValue<Atom> atomObj = new ReturnValue<Atom>();
					atomObj = interpreterObj.searchAtom(printTokenObj.getValue());
					if (atomObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<Object>(null, atomObj.getExecutionInformation());
					}
					sPrint = atomObj.getReturnValue().getValue();
				}
				else if (printTokenObj.getType().equals(TokenTypes.TOKEN_STRING)) {
					//Es handelt sich um einen String:
					sPrint = printTokenObj.getValue();
				}
				else if (printTokenObj.getType().equals(TokenTypes.TOKEN_NUMBER) || printTokenObj.getType().equals(TokenTypes.TOKEN_BOOLEAN)) {
					//Es handelt sich um eine Zahl oder einen Wahrheitswert:
					sPrint = printTokenObj.getValue();
				}
				else if (printTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Es handelt sich um eine Rechnung:
					ReturnValue<String> calculateReturn = new ReturnValue<String>(); //Speichert das Ergebnis der Rechnung.
					//Tokens heraussuchen, die zur Rechnung gehoeren:
					LinkedList<Token> lTokensCalculationObj = new LinkedList<Token>(); //Speichert die Tokens, die zur Rechnung gehoeren.
					lTokensCalculationObj.add(printTokenObj);
					int nBracketsOpened = 1; //Speichert die Anzahl der geoeffneten Klammern.
					int nBracketsClosed = 0; //Speichert die Anzahl der geschlossenen Klammern.
					while (plTokensObj.size() != 0) {
						Token currentTokenObj = plTokensObj.poll();
						if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
							//Es wurde eine Klammer geoeffnet:
							nBracketsOpened++;
						}
						else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
							//Es wurde eine Klammer geschlossen:
							nBracketsClosed++;
						}
						lTokensCalculationObj.add(currentTokenObj);
						if (nBracketsOpened == nBracketsClosed) {
							//Es wurde gleich viele Klammern geoeffnet und geschlossen:
							break; //Schleife beenden.
						}
					}
					calculateReturn = calculate(lTokensCalculationObj);
					if (calculateReturn.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<Object>(null, calculateReturn.getExecutionInformation());
					}
					sPrint = calculateReturn.getReturnValue();
				}
				else {
					//Es handelt sich um einen unangebrachten Token -> SYNTAX FEHLER:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
				}
				System.out.print(sPrint);
			}
			
			else if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_PRINTLN)) {
				Token printTokenObj = new Token(null, null); //Speichert den Token, welcher ausgegeben werden soll.
				printTokenObj = plTokensObj.poll();
				String sPrint = ""; //Speichert den Inhalt, welcher in der Konsole ausgegeben werden soll.
				if (printTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
					//Es handelt sich um einen Variablennamen:
					ReturnValue<Atom> atomObj = new ReturnValue<Atom>();
					atomObj = interpreterObj.searchAtom(printTokenObj.getValue());
					if (atomObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<Object>(null, atomObj.getExecutionInformation());
					}
					sPrint = atomObj.getReturnValue().getValue();
				}
				else if (printTokenObj.getType().equals(TokenTypes.TOKEN_STRING)) {
					//Es handelt sich um einen String:
					sPrint = printTokenObj.getValue();
				}
				else if (printTokenObj.getType().equals(TokenTypes.TOKEN_NUMBER) || printTokenObj.getType().equals(TokenTypes.TOKEN_BOOLEAN)) {
					//Es handelt sich um eine Zahl oder einen Wahrheitswert:
					sPrint = printTokenObj.getValue();
				}
				else if (printTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Es handelt sich um eine Rechnung:
					ReturnValue<String> calculateReturn = new ReturnValue<String>(); //Speichert das Ergebnis der Rechnung.
					//Tokens heraussuchen, die zur Rechnung gehoeren:
					LinkedList<Token> lTokensCalculationObj = new LinkedList<Token>(); //Speichert die Tokens, die zur Rechnung gehoeren.
					lTokensCalculationObj.add(printTokenObj);
					int nBracketsOpened = 1; //Speichert die Anzahl der geoeffneten Klammern.
					int nBracketsClosed = 0; //Speichert die Anzahl der geschlossenen Klammern.
					while (plTokensObj.size() != 0) {
						Token currentTokenObj = plTokensObj.poll();
						if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
							//Es wurde eine Klammer geoeffnet:
							nBracketsOpened++;
						}
						else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
							//Es wurde eine Klammer geschlossen:
							nBracketsClosed++;
						}
						lTokensCalculationObj.add(currentTokenObj);
						if (nBracketsOpened == nBracketsClosed) {
							//Es wurde gleich viele Klammern geoeffnet und geschlossen:
							break; //Schleife beenden.
						}
					}
					calculateReturn = calculate(lTokensCalculationObj);
					if (calculateReturn.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<Object>(null, calculateReturn.getExecutionInformation());
					}
					sPrint = calculateReturn.getReturnValue();
				}
				else {
					//Es handelt sich um einen unangebrachten Token -> SYNTAX FEHLER:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
				}
				System.out.println(sPrint);
			}
			
			else if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_SCAN)) {
				//Die eingabe des Benutzers soll eingelesen werden:
				Token variableObj = plTokensObj.poll(); //Speichert den Bezeichner der Variablen, in der die Eingabe gespeichert werden soll.
				if (!variableObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
					//FEHLER: Es handelt sich nicht um einen gueltigen Bezeichner:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
				}
				Scanner inputScannerObj = new Scanner(System.in); //Liest die Eingabe des Benutzers.
				String sInput = ""; //Speichert die Eingabe des Benutzers.
				if (inputScannerObj.hasNext()) {
					sInput = inputScannerObj.nextLine(); //Liest die Eingabe des Benutzers.
				}
				else {
					inputScannerObj.close();
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN);
				}
				//Scanner nicht schliessen, da es sonst zu einem Fehler kommt: java.util.nosuchelementexception
				//inputScannerObj.close();
				Atom newVariableValueObj;
				if (isNumber(sInput)) {
					//Bei der Eingabe handelt es sich um eine Zahl:
					newVariableValueObj = new Atom(variableObj.getValue(), sInput, TokenTypes.TOKEN_NUMBER);
				}
				else if (sInput.equals(KeywordTypes.BOOLEAN_T) || sInput.equals(KeywordTypes.BOOLEAN_F)) {
					//Bei der Eingabe handelt es sich um einen Wahrheitswert:
					newVariableValueObj = new Atom(variableObj.getValue(), sInput, TokenTypes.TOKEN_BOOLEAN);
				}
				else {
					//Bei der Eingabe handelt es sich um einen String:
					newVariableValueObj = new Atom(variableObj.getValue(), sInput, TokenTypes.TOKEN_STRING);
				}
				boolean bOverwrittenAtom = interpreterObj.overrideAtom(newVariableValueObj); //Gibt an, ob der Wert des Atoms erfolgreich geaendert wurde.
				if (!bOverwrittenAtom) {
					//Es ist ein Fehler aufgetreten:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
				}
			}
			
			else if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_IF)) {
				//Es handelt sich um eine if-Verzweigung:
				LinkedList<Token> lConditionObj = new LinkedList<Token>(); //Speichert die Bedingung der Verzweigung.
				LinkedList<Token> lExpressionObj = new LinkedList<Token>(); //Speichert die Anweisungen.
				int nBracketsClosed = 0;
				int nBracketsOpened = 0;
				if (!plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Die Bedingung der Verzweigun ist fehlerhaft angegeben -> SYNTAX FEHLER:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
				}
				//Bedingung der Verzweigung herausfinden:
				while (!plTokensObj.isEmpty()) {
					if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
						//Geschlossene Klammer:
						nBracketsClosed++;
					}
					else if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Geschlossene Klammer:
						nBracketsOpened++;
					}
					lConditionObj.add(plTokensObj.poll());
					if (nBracketsClosed == nBracketsOpened) {
						//Es wurden gleich viele Klammern geoeffnet und geschlossen:
						break;
					}
				}
				//Anweisungen der Verzweigung herausfinden:
				nBracketsClosed = 0;
				nBracketsOpened = 0;
				while (!plTokensObj.isEmpty()) {
					Token currentToken = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
					if (currentToken.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
						//Geschlossene Klammer:
						nBracketsClosed++;
					}
					else if (currentToken.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Geschlossene Klammer:
						nBracketsOpened++;
					}
					lExpressionObj.add(currentToken);
					if (nBracketsClosed == nBracketsOpened) {
						//Es wurden gleich viele Klammern geoeffnet und geschlossen:
						break;
					}
				}
				ReturnValue<Object> ifReturn = new ReturnValue<Object>(); //Speichert den Rueckgabewert der if-Verzweigung.
				ifReturn = ifStatement(lConditionObj, lExpressionObj);
				if (ifReturn.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
					//Es ist ein Fehler aufgetreten:
					return new ReturnValue<Object>(null, ifReturn.getExecutionInformation());
				}
			}
			
			else if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_WHILE)) {
				//Es handelt sich um eine while-Schleife:
				LinkedList<Token> lConditionObj = new LinkedList<Token>(); //Speichert die Bedingung der Schleife.
				LinkedList<Token> lExpressionObj = new LinkedList<Token>(); //Speichert die Anweisungen.
				int nBracketsClosed = 0;
				int nBracketsOpened = 0;
				if (!plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Die Bedingung der Schleife ist fehlerhaft angegeben -> SYNTAX FEHLER:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
				}
				//Bedingung der Schleife herausfinden:
				while (!plTokensObj.isEmpty()) {
					if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
						//Geschlossene Klammer:
						nBracketsClosed++;
					}
					else if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Geschlossene Klammer:
						nBracketsOpened++;
					}
					lConditionObj.add(plTokensObj.poll());
					if (nBracketsClosed == nBracketsOpened) {
						//Es wurden gleich viele Klammern geoeffnet und geschlossen:
						break;
					}
				}
				//Anweisungen der Schleife herausfinden:
				nBracketsClosed = 0;
				nBracketsOpened = 0;
				while (!plTokensObj.isEmpty()) {
					Token currentToken = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
					if (currentToken.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
						//Geschlossene Klammer:
						nBracketsClosed++;
					}
					else if (currentToken.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Geschlossene Klammer:
						nBracketsOpened++;
					}
					lExpressionObj.add(currentToken);
					if (nBracketsClosed == nBracketsOpened) {
						//Es wurden gleich viele Klammern geoeffnet und geschlossen:
						break;
					}
				}
				ReturnValue<Object> ifReturn = new ReturnValue<Object>(); //Speichert den Rueckgabewert der while-Schleife.
				ifReturn = whileLoop(lConditionObj, lExpressionObj);
				if (ifReturn.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
					//Es ist ein Fehler aufgetreten:
					return new ReturnValue<Object>(null, ifReturn.getExecutionInformation());
				}
			}
			
			else {
				//Unbekanntes Schluesselwort -> FEHLER:
				return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_TOKEN);
			}
			
			//Ueberpruefen, ob noch weitere Tokens in der Liste vorhanden sind:
			if (plTokensObj.peek() != null && !plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				//Es kommen noch weitere Tokens vor, bei welchen es sich nicht um geschlossene Klammern handelt (SYNTAXFEHLER):
				//System.out.println("DEBUG = " + plTokensObj.peek().getValue());
				return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
			}
		}
		else if (firstTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
			//Der erste Token ist ein Bezeichner -> Aufruf einer Funktion:
			
			//Funktion herausfinden:
			boolean bFunctionFound = false;
			for (int i = 0; i < lFunctionsObj.size(); i++) {
				if (lFunctionsObj.get(i).getName().equals(firstTokenObj.getValue())) {
					//Funktion ist vorhanden:
					bFunctionFound = true;
					for (int j = 0; j < lFunctionsObj.get(i).getExpressionAmount(); j++) {
						ReturnValue<Object> processReturnObj = process(lFunctionsObj.get(i).getExpression(j));
						if (processReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Ein Fehler ist aufgetreten:
							return processReturnObj;
						}
					}
				}
			}
			if (!bFunctionFound) {
				//Funktion wurde nicht gefunden:
				return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
			}
			
		}
		else {
			//Der erste Token ist kein Schluesselwort und kein Bezeichner -> SYNTAX FEHLER:
			return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
	}
	
	
	
	/**
	 * Diese Methode erhaelt als Parameter eine Liste an Tokens, wessen erster Token eine Klammer ist,
	 * gefolgt vom Rechenoperator und allen darauffolgenden Tokens (welche nicht zwingend zur Rechnung
	 * gehoeren muessen). Sie findet heraus, welche der Tokens zu Rechnung gehoeren, fuehrt diese
	 * durch und gibt das Ergebnis als String zurueck.
	 * 
	 * @param plTokensObj	Liste an Tokens, welche die Rechnung beinhalten.
	 * @return				Ergebnis der Rechnung als String.
	 */
	private ReturnValue<String> calculate(LinkedList<Token> plTokensObj) {
		if (plTokensObj.isEmpty()) {
			//Die Liste an Tokens ist leer:
			return new ReturnValue<String>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		if (!plTokensObj.get(1).getType().equals(TokenTypes.TOKEN_OPERATOR)) {
			//Es ist kein Operand vorhanden:
			return new ReturnValue<String>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		
		//Herausfinden, welcher Teil der Liste an Tokens zur Rechnung gehoert:
		LinkedList<Token> lTokensObj = new LinkedList<Token>(); //Speichert alle Tokens, welche zur Rechnung gehoeren.
		int nBracketsOpened = 0; //Speichert die Anzahl der geoeffneten Klammern.
		int nBracketsClosed = 0; //Speichert die Anzahl der geoeffneten Klammern.
		//Liste an Tokens durchlaufen:
		for (int i = 0; i < plTokensObj.size(); i++) {
			Token currentTokenObj = new Token(plTokensObj.get(i).getValue(), plTokensObj.get(i).getType()); //Speichert den aktuellen Token.
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			lTokensObj.add(currentTokenObj);
			if (nBracketsOpened == nBracketsClosed) {
				//Es wurden gleich viele Klammern geoeffnet und geschlossen:
				break; //Schleife beenden.
			}
		}
		
		//Rechnung durchfuehren:
		ReturnValue<BinaryTree<Token>> tAbstractSyntaxTree = new ReturnValue<BinaryTree<Token>>(); //Speichert den abstrakten Syntaxbaum.
		tAbstractSyntaxTree = parserObj.parse(lTokensObj);
		if (tAbstractSyntaxTree.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es ist ein Fehler aufgetreten:
			return new ReturnValue<String>(null, tAbstractSyntaxTree.getExecutionInformation());
		}
		ReturnValue<String> resultObj = new ReturnValue<String>(); //Speichert das Ergebnis der Rechenoperation.
		resultObj = interpreterObj.interpret(tAbstractSyntaxTree.getReturnValue());
		return resultObj; //Ergebnis zurueckgeben.
	}
	
	
	
	/**
	 * Diese Methode berechnet das Ergebnis einer Bedingung von zum Beispiel einer Verzweigung und gibt
	 * dieses zurueck.
	 * 
	 * @param plConditionObj	Bedingung als Tokens.
	 * @return					Ergebnis der Bedingung.
	 */
	private ReturnValue<Boolean> condition(LinkedList<Token> plConditionObj) {
		ReturnValue<BinaryTree<Token>> tAbstractSyntaxTreeObj = new ReturnValue<BinaryTree<Token>>(); //Speichert den abstrakten Syntaxbaum.
		tAbstractSyntaxTreeObj = parserObj.parse(plConditionObj);
		if (tAbstractSyntaxTreeObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es ist ein Fehler aufgetreten:
			return new ReturnValue<Boolean>(false, tAbstractSyntaxTreeObj.getExecutionInformation());
		}
		
		ReturnValue<String> sResultObj = new ReturnValue<String>();
		sResultObj = interpreterObj.interpret(tAbstractSyntaxTreeObj.getReturnValue());
		if (sResultObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es ist ein Fehler aufgetreten:
			return new ReturnValue<Boolean>(false, sResultObj.getExecutionInformation());
		}
		
		if (sResultObj.getReturnValue().equals(KeywordTypes.BOOLEAN_T)) {
			//Die Bedingung ist wahr:
			return new ReturnValue<Boolean>(true, ReturnValueTypes.SUCCESS);
		}
		else if (sResultObj.getReturnValue().equals(KeywordTypes.BOOLEAN_F)) {
			//Die Bedingung ist falsch:
			return new ReturnValue<Boolean>(false, ReturnValueTypes.SUCCESS);
		}
		else {
			//Die Bedingung macht keinen Sinn \(°_°)/:
			return new ReturnValue<Boolean>(false, ReturnValueTypes.ERROR_SYNTAX);
		}
	}
	
	
	
	/**
	 * Diese Methode fuehrt alle Anweisungen innerhalb einer if-Verzweigung aus, falls die angegebene
	 * Bedingung wahr (T) und nicht falsch (NIL) ist.
	 * 
	 * @param plConditionObj	Bedingung der Verzweigung.
	 * @param plExpressionObj	Anweisungen der Verzweigung.
	 * @return					Gibt an, ob ein Fehler aufgetreten ist.
	 */
	private ReturnValue<Object> ifStatement(LinkedList<Token> plConditionObj, LinkedList<Token> plExpressionObj) {
		//Ueberpruefen, ob die Bedingung wahr ist:
		ReturnValue<Boolean> bConditionObj = new ReturnValue<Boolean>();
		bConditionObj = condition(plConditionObj);
		if (bConditionObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es ist ein Fehler aufgetreten:
			return new ReturnValue<Object>(null, bConditionObj.getExecutionInformation());
		}
		
		//Ueberpruefen, ob die Bedingung wahr oder falsch ist:
		if (!bConditionObj.getReturnValue()) {
			//Die Bedingung ist falsch:
			return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
		}
		
		//Anweisungen in eine Liste an Listen an Tokens einordnen:
		plExpressionObj.poll(); //Erste geoeffnete Klammer entfernen.
		int nBracketsOpened = 0; //Speichert die Anzahl der geoeffneten Klammern.
		int nBracketsClosed = 0; //Speichert die Anzahl der geschlossenen Klammern.
		LinkedList<Token> lCurrentExpressionObj = new LinkedList<Token>(); //Speichert den aktuellen Ausdruck.
		while (!plExpressionObj.isEmpty()) {
			if (plExpressionObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			else if (plExpressionObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			lCurrentExpressionObj.add(plExpressionObj.poll());
			if (nBracketsClosed == nBracketsOpened) {
				//Es wurden gleich viele Klammern geoeffnet und geschlossen:
				
				//Ausdruck Ausfuehren:
				ReturnValue<Object> expressionReturn = new ReturnValue<Object>();
				expressionReturn = process(lCurrentExpressionObj);
				if (expressionReturn.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
					//Es kam zu einem Fehler:
					return new ReturnValue<Object>(null, expressionReturn.getExecutionInformation());
				}
				
				lCurrentExpressionObj.clear();
				nBracketsClosed = 0;
				nBracketsOpened = 0;
			}
		}
		return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
	}
	
	
	
	/**
	 * Diese Methode fuehrt alle Anweisungen innerhalb eine while-Schleife immer weiter aus, solange die angegebene
	 * Bedingung wahr ist. Ist die Bedingung zu Beginn nicht wahr, so erden die Anweisungen keinmal ausgefuehrt.
	 * 
	 * @param plConditionObj	Bedingung der Schleife.
	 * @param plExpressionObj	Anweisungen der Schleife.
	 * @return					Gibr an, ob ein Fehler aufgetreten ist.
	 */
	private ReturnValue<Object> whileLoop(LinkedList<Token> plConditionObj, LinkedList<Token> plExpressionObj) {
		//Die Schleife laeuft immer weiter bis die Methode beendet wird, wenn die Bedingung falsch ist.
		while (true) {
			//Ueberpruefen, ob die Bedingung wahr ist:
			LinkedList<Token> lConditionObj = new LinkedList<Token>();
			lConditionObj.addAll(plConditionObj);
			ReturnValue<Boolean> bConditionObj = new ReturnValue<Boolean>();
			bConditionObj = condition(lConditionObj);
				if (bConditionObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Object>(null, bConditionObj.getExecutionInformation());
			}
			
			//Ueberpruefen, ob die Bedingung wahr oder falsch ist:
			if (!bConditionObj.getReturnValue()) {
				//Die Bedingung ist falsch:
				return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
			}
			
			//Anweisungen ausfuehren:
			LinkedList<Token> lExpressionsObj = new LinkedList<Token>(); //Speichert die Anweisungen.
			lExpressionsObj.addAll(plExpressionObj);
			lExpressionsObj.poll(); //Erste geoeffnete Klammer entfernen.
			int nBracketsOpened = 0; //Speichert die Anzahl der geoeffneten Klammern.
			int nBracketsClosed = 0; //Speichert die Anzahl der geschlossenen Klammern.
			LinkedList<Token> lCurrentExpressionObj = new LinkedList<Token>(); //Speichert den aktuellen Ausdruck.
			while (!lExpressionsObj.isEmpty()) {
				if (lExpressionsObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					nBracketsClosed++;
				}
				else if (lExpressionsObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					nBracketsOpened++;
				}
				lCurrentExpressionObj.add(lExpressionsObj.poll());
				if (nBracketsClosed == nBracketsOpened) {
					//Es wurden gleich viele Klammern geoeffnet und geschlossen:
					
					//Ausdruck Ausfuehren:
					ReturnValue<Object> expressionReturn = new ReturnValue<Object>();
					expressionReturn = process(lCurrentExpressionObj);
					if (expressionReturn.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es kam zu einem Fehler:
						return new ReturnValue<Object>(null, expressionReturn.getExecutionInformation());
					}
					
					lCurrentExpressionObj.clear();
					nBracketsClosed = 0;
					nBracketsOpened = 0;
				}
			}
		}
	}
	
	
	
	/**
	 * Diese Methode gibt Fehlermeldungen in der Konsole aus.
	 * 
	 * @param psPrefix			Text, welcher vor der Fehlermeldung angezeigt werden soll.
	 * @param pnErrorMessage	Fehlermeldung.
	 * @param psSuffix			Text, welcher nach der Fehlermeldung angezeigt werden soll.
	 */
	private void printErrorMessage(String psPrefix, int pnErrorMessage, String psSuffix) {
		System.out.print(psPrefix); //Prefix ausgeben.
		
		//Fehlermeldung ausgeben:
		if (pnErrorMessage == ReturnValueTypes.SUCCESS) {
			System.out.print("success");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN) {
			System.out.print("unknown error");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_DIVIDE_BY_ZERO) {
			System.out.print("cannot divide by zero");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNEQUAL_DATA) {
			System.out.print("operands have different type");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_SYNTAX) {
			System.out.print("syntax error");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_OPERATOR) {
			System.out.print("unknown operator");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER) {
			System.out.print("unknown identifier");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_NOT_ENOUGH_OPERANDS) {
			System.out.print("the operation does not have enough operands");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_CANNOT_OFFSET_STRING_TO_NUMBER) {
			System.out.print("cannot offset String to number");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_TOKEN) {
			System.out.print("unknown token");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_KEYWORD) {
			System.out.print("unknown keyword found");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_VARIABLE_NAME_DOES_EXIST) {
			System.out.print("variable name does already exist");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_FILE_DOES_NOT_EXIST) {
			System.out.print("the file does not exist");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_FILE_CANNOT_BE_READ) {
			System.out.print("cannot read file");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_NO_MAIN_FUNCTION) {
			System.out.print("main function is missing");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_MAIN_FUNCTION_HAS_PARAMETER) {
			System.out.print("main function has too many parameters.");
		}
		else {
			System.out.print("unknwon error occured. Error message: " + pnErrorMessage);
		}
		
		System.out.println(psSuffix); //Suffix ausgeben.
	}
	
	
	
	/**
	 * Diese Methode ueberpruft, ob es sich bei dem als Parameter angegebenen String um eine Zahl handelt.
	 * 
	 * @param psNumber	String, welcher ueberprueft werden soll.
	 * @return			Gibt an, ob es sich bei dem String um eine Zahl handelt.
	 */
	private boolean isNumber(String psNumber) {
		try {
			double nNumber = Double.parseDouble(psNumber);
		}
		catch (NumberFormatException exceptionObj) {
			return false;
		}
		return true;
	}
	
}
