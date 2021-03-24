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
	 * Speichert den Rueckgabewert der aktuellen Funktion.
	 */
	private Token functionReturnValueObj;
	
	
	
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
		functionReturnValueObj = new Token("0.00", TokenTypes.TOKEN_NUMBER);
		
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
				interpreterObj.changeFunctionAtoms(lFunctionsObj.get(i).getParameters());
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
			//An dieser Stelle wird der Syntaxfehler erzeugt, welcher eine erfolgreiche Rekursion verhindert! WARUM? I DO NOT KNOW :( <-----------------------------------------
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
				variableValueObj = plTokensObj.poll();
				Atom newAtomObj;
				if (variableValueObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Es handelt sich um eine Subrechnung:
					LinkedList<Token> lCalculationTokensObj = new LinkedList<Token>();
					lCalculationTokensObj.add(variableValueObj);
					int nBracketsOpened = 1;
					int nBracketsClosed = 0;
					while (!plTokensObj.isEmpty()) {
						Token currentTokenObj = plTokensObj.poll();
						if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
							nBracketsClosed++;
						}
						else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
							nBracketsOpened++;
						}
						lCalculationTokensObj.add(currentTokenObj);
						if (nBracketsOpened == nBracketsClosed) {
							//Tokens vollstaendig herausgefunden:
							break;
						}
					}
					ReturnValue<String> calculationReturn = new ReturnValue<String>(); //Speichert den Rueckgabewert der Rechnung.
					calculationReturn = calculate(lCalculationTokensObj);
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
				else if (variableValueObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
					//Es handelt sich um einen Bezeichner (einer Variablen oder Funktion):
					if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Beim naechsten Token handelt es sich um eine geoeffnete Klammer (FUNKTIONSAUFRUF):
						LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>();
						lFunctionTokensObj.add(variableValueObj);
						while (!plTokensObj.isEmpty()) {
							Token currentTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
							lFunctionTokensObj.add(currentTokenObj);
							if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
								//Parameter herausgefunden:
								break;
							}
						}
						ReturnValue<Token> functionReturnObj = new ReturnValue<Token>();
						functionReturnObj = executeFunction(lFunctionTokensObj);
						if (functionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgetreten:
							return new ReturnValue<Object>(null, functionReturnObj.getExecutionInformation());
						}
						newAtomObj = new Atom(variableNameObj.getValue(), functionReturnObj.getReturnValue().getValue(), functionReturnObj.getReturnValue().getType());
					}
					else {
						//Es muss sich um eine Variable handeln:
						ReturnValue<Atom> atomSearchQueryObj = new ReturnValue<Atom>();
						atomSearchQueryObj = interpreterObj.searchAtom(variableValueObj.getValue());
						if (atomSearchQueryObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgeteten:
							return new ReturnValue<Object>(null, atomSearchQueryObj.getExecutionInformation());
						}
						newAtomObj = new Atom(variableNameObj.getValue(), atomSearchQueryObj.getReturnValue().getValue(), atomSearchQueryObj.getReturnValue().getType());
					}
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
					//Es handelt sich um einen Bezeichner (einer Variablen oder Funktion):
					if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Beim naechsten Token handelt es sich um eine geoeffnete Klammer (FUNKTIONSAUFRUF):
						LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>();
						lFunctionTokensObj.add(printTokenObj);
						while (!plTokensObj.isEmpty()) {
							Token currentTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
							lFunctionTokensObj.add(currentTokenObj);
							if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
								//Parameter herausgefunden:
								break;
							}
						}
						ReturnValue<Token> functionReturnObj = new ReturnValue<Token>();
						functionReturnObj = executeFunction(lFunctionTokensObj);
						if (functionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgetreten:
							return new ReturnValue<Object>(null, functionReturnObj.getExecutionInformation());
						}
						sPrint = functionReturnObj.getReturnValue().getValue();
					}
					else {
						//Es muss sich um eine Variable handeln:
						ReturnValue<Atom> atomSearchQueryObj = new ReturnValue<Atom>();
						atomSearchQueryObj = interpreterObj.searchAtom(printTokenObj.getValue());
						if (atomSearchQueryObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgeteten:
							return new ReturnValue<Object>(null, atomSearchQueryObj.getExecutionInformation());
						}
						sPrint = atomSearchQueryObj.getReturnValue().getValue();
					}
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
					//Es handelt sich um einen Bezeichner (einer Variablen oder Funktion):
					if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Beim naechsten Token handelt es sich um eine geoeffnete Klammer (FUNKTIONSAUFRUF):
						LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>();
						lFunctionTokensObj.add(printTokenObj);
						while (!plTokensObj.isEmpty()) {
							Token currentTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
							lFunctionTokensObj.add(currentTokenObj);
							if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
								//Parameter herausgefunden:
								break;
							}
						}
						ReturnValue<Token> functionReturnObj = new ReturnValue<Token>();
						functionReturnObj = executeFunction(lFunctionTokensObj);
						if (functionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgetreten:
							return new ReturnValue<Object>(null, functionReturnObj.getExecutionInformation());
						}
						sPrint = functionReturnObj.getReturnValue().getValue();
					}
					else {
						//Es muss sich um eine Variable handeln:
						ReturnValue<Atom> atomSearchQueryObj = new ReturnValue<Atom>();
						atomSearchQueryObj = interpreterObj.searchAtom(printTokenObj.getValue());
						if (atomSearchQueryObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgeteten:
							return new ReturnValue<Object>(null, atomSearchQueryObj.getExecutionInformation());
						}
						sPrint = atomSearchQueryObj.getReturnValue().getValue();
					}
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
			
			else if (firstTokenObj.getValue().equals(KeywordTypes.KEYWORD_RETURN)) {
				//Der aktuelle Funktionsaufruf soll beendet werden:
				Token returnValueObj;
				//Herausfinden, um welchen Wert es sich beim Rueckgabewert handelt:
				Token nextTokenObj = plTokensObj.poll();
				if (nextTokenObj.getType().equals(TokenTypes.TOKEN_NUMBER) || nextTokenObj.getType().equals(TokenTypes.TOKEN_STRING) || nextTokenObj.getType().equals(TokenTypes.TOKEN_BOOLEAN)) {
					//Es handelt sich um eine Zahl, einen String, oder einen Wahrheitswert:
					returnValueObj = new Token(nextTokenObj.getValue(), nextTokenObj.getType());
					functionReturnValueObj = returnValueObj;
					return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
				}
				else if (nextTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
					//Es handelt sich um einen Bezeichner (einer Variablen oder Funktion):
					if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						//Beim naechsten Token handelt es sich um eine geoeffnete Klammer (FUNKTIONSAUFRUF):
						LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>();
						lFunctionTokensObj.add(nextTokenObj);
						while (!plTokensObj.isEmpty()) {
							Token currentTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
							lFunctionTokensObj.add(currentTokenObj);
							if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
								//Parameter herausgefunden:
								break;
							}
						}
						ReturnValue<Token> functionReturnObj = new ReturnValue<Token>();
						functionReturnObj = executeFunction(lFunctionTokensObj);
						if (functionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgetreten:
							return new ReturnValue<Object>(null, functionReturnObj.getExecutionInformation());
						}
						functionReturnValueObj = functionReturnObj.getReturnValue();
					}
					else {
						//Es muss sich um eine Variable handeln:
						ReturnValue<Atom> atomSearchQueryObj = new ReturnValue<Atom>();
						atomSearchQueryObj = interpreterObj.searchAtom(nextTokenObj.getValue());
						if (atomSearchQueryObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgeteten:
							return new ReturnValue<Object>(null, atomSearchQueryObj.getExecutionInformation());
						}
						returnValueObj = new Token(atomSearchQueryObj.getReturnValue().getValue(), atomSearchQueryObj.getReturnValue().getType());
						functionReturnValueObj = returnValueObj;
						return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
					}
				}
				else if (nextTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Es handelt sich um eine Rechnung:
					LinkedList<Token> lCalculationTokensObj = new LinkedList<Token>();
					lCalculationTokensObj.add(nextTokenObj);
					int nBracketsClosed = 0;
					int nBracketsOpened = 1;
					while (!plTokensObj.isEmpty()) {
						Token currentTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
						if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
							nBracketsClosed++;
						}
						else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
							nBracketsOpened++;
						}
						lCalculationTokensObj.add(currentTokenObj);
						if (nBracketsOpened == nBracketsClosed) {
							//Tokens wurden vollstaendig herausgefunden:
							break;
						}
					}
					ReturnValue<String> calculateReturnObj = new ReturnValue<String>();
					calculateReturnObj = calculate(lCalculationTokensObj);
					if (calculateReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<Object>(null, calculateReturnObj.getExecutionInformation());
					}
					returnValueObj = new Token(calculateReturnObj.getReturnValue(), TokenTypes.TOKEN_NUMBER);
					functionReturnValueObj = returnValueObj;
					return new ReturnValue<Object>(null, ReturnValueTypes.INFO_FUNCTION_RETURN);
				}
				
				else {
					//Unbekannter Token
					returnValueObj = new Token("0.00", TokenTypes.TOKEN_NUMBER);
					functionReturnValueObj = returnValueObj;
					return new ReturnValue<Object>(null, ReturnValueTypes.INFO_FUNCTION_RETURN);
				}
			}
			
			else {
				//Unbekanntes Schluesselwort -> FEHLER:
				return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_TOKEN);
			}
			
			//Ueberpruefen, ob noch weitere Tokens in der Liste vorhanden sind:
			if (plTokensObj.peek() != null && !plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				//Es kommen noch weitere Tokens vor, bei welchen es sich nicht um geschlossene Klammern handelt (SYNTAXFEHLER):
				return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
			}
		}
		else if (firstTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
			//Der erste Token ist ein Bezeichner -> Aufruf einer Funktion:
			
			//Funktions (inkl. Parameter) herausfinden:
			LinkedList<Token> lFunctionObj = new LinkedList<Token>();
			lFunctionObj.add(firstTokenObj);
			int nBracketsOpened = 0;
			int nBracketsClosed = 0;
			while (!plTokensObj.isEmpty()) {
				Token currentTokenObj = plTokensObj.poll();
				if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					nBracketsClosed++;
				}
				else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					nBracketsOpened++;
				}
				lFunctionObj.add(currentTokenObj);
				if ((nBracketsOpened != 0 && nBracketsClosed != 0) && (nBracketsOpened == nBracketsClosed)) {
					//Funktionsaufruf herausgefiltert:
					break;
				}
			}
			
			//Funktion aufrufen:
			ReturnValue<Token> functionReturnObj = new ReturnValue<Token>();
			functionReturnObj = executeFunction(lFunctionObj);
			if (functionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Object>(null, functionReturnObj.getExecutionInformation());
			}
		}
		else {
			//Der erste Token ist kein Schluesselwort und kein Bezeichner -> SYNTAX FEHLER:
			return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
	}
	
	
	
	/**
	 * Diese Funktion fuehrt eine Funktion in Lisp aus, dazu werden ihr der Bezeichner und die Parameter - wie diese
	 * im Quellcode vorkommen ( z.B. sin(15) ) uebergeben.
	 * 
	 * @param plTokensObj	Tokens des Funktionsaufrufes.
	 * @return				Rueckgabewert der Funktion, falls vorhanden.
	 */
	private ReturnValue<Token> executeFunction(LinkedList<Token> plTokensObj) {
		//Herausfinden, ob die Funktion existiert:
		boolean bFunctionFound = false;
		String sFunctionName = plTokensObj.poll().getValue();
		Function currentFunctionInUse = new Function();
		for (int i = 0; i < lFunctionsObj.size(); i++) {
			if (lFunctionsObj.get(i).getName().equals(sFunctionName)) {
				//Funktion existiert:
				bFunctionFound = true;
				currentFunctionInUse = lFunctionsObj.get(i);
				break;
			}
		}
		if (!bFunctionFound) {
			//Funktion wurde nicht gefunden:
			return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
		}
		
		//Parameter herausfinden:
		LinkedList<Token> lParametersObj = new LinkedList<Token>();
		if (!plTokensObj.poll().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
			//Erster Token nach Bezeichner ist KEINE Klammer: SYNTAXFEHLER:
			return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		while (!plTokensObj.isEmpty()) {
			Token currentTokenObj = new Token(plTokensObj.peek().getValue(), plTokensObj.poll().getType());
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				//Geschlossene Klammer indiziert, dass alle Parameter gelesen wurden!
				break;
			}
			//Herausfinden, um welchen Token es sich handelt:
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_STRING) || currentTokenObj.getType().equals(TokenTypes.TOKEN_NUMBER) || currentTokenObj.getType().equals(TokenTypes.TOKEN_BOOLEAN)) {
				//Es handelt sich um eine Zahl, einen String oder einen Wahrheitswert:
				lParametersObj.add(currentTokenObj);
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
				//Es handelt sich um einen Bezeichner -> Wert der Variablen aus Verzeichnis laden:
				ReturnValue<Atom> atomSearchQueryObj = new ReturnValue<Atom>();
				atomSearchQueryObj = interpreterObj.searchAtom(currentTokenObj.getValue());
				if (atomSearchQueryObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
					//Es ist ein Fehler aufgetreten:
					return new ReturnValue<Token>(null, atomSearchQueryObj.getExecutionInformation());
				}
				lParametersObj.add(new Token(atomSearchQueryObj.getReturnValue().getValue(), atomSearchQueryObj.getReturnValue().getType()));
			}
			else if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				//Es handelt sich um eine Rechnung:
				int nBracketsOpened = 1;
				int nBracketsClosed = 0;
				LinkedList<Token> lCalculationTokensObj = new LinkedList<Token>();
				lCalculationTokensObj.add(currentTokenObj);
				while (!plTokensObj.isEmpty()) {
					Token currentCalculationTokenObj = plTokensObj.poll();
					if (currentCalculationTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
						nBracketsOpened++;
					}
					else if (currentCalculationTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
						nBracketsClosed++;
					}
					lCalculationTokensObj.add(currentCalculationTokenObj);
					if (nBracketsOpened == nBracketsClosed) {
						//Tokens der Rechnung vollstaending herausgefunden:
						break;
					}
				}
				ReturnValue<String> calculationReturnObj = new ReturnValue<String>();
				calculationReturnObj = calculate(lCalculationTokensObj);
				if (calculationReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
					//Es ist ein Fehler aufgetreten:
					return new ReturnValue<Token>(null, calculationReturnObj.getExecutionInformation());
				}
				lParametersObj.add(new Token(calculationReturnObj.getReturnValue(), TokenTypes.TOKEN_NUMBER));
			}
		}
		
		//Herausfinden, die Parameter in korrekter Anzahl angegeben wurden:
		if (currentFunctionInUse.getParameterAmount() != lParametersObj.size()) {
			//Es wurde eine inkorrekte Anzahl an Parametern angegeben:
			return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
		}
		
		//Variablen der Funktion definieren:
		LinkedList<Atom> lOldFunctionAtomsObj = new LinkedList<Atom>();
		LinkedList<Atom> lNewFunctionAtomsObj = new LinkedList<Atom>();
		LinkedList<Atom> lFunctionParametersObj = new LinkedList<Atom>(currentFunctionInUse.getParameters());
		for (int i = 0; i < lParametersObj.size(); i++) {
			lNewFunctionAtomsObj.add(new Atom(lFunctionParametersObj.get(i).getName(), lParametersObj.get(i).getValue(), lParametersObj.get(i).getType()));
		}
		lOldFunctionAtomsObj.addAll(interpreterObj.changeFunctionAtoms(lNewFunctionAtomsObj));
		//Ausdruecke der Funktion ausfuehren:
		for (int i = 0; i < currentFunctionInUse.getExpressionAmount(); i++) {
			LinkedList<Token> currentExpressionObj = new LinkedList<Token>();
			currentExpressionObj.addAll(currentFunctionInUse.getExpression(i));
			ReturnValue<Object> processReturnObj = new ReturnValue<Object>();
			processReturnObj = process(currentExpressionObj);
			if (processReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				if (processReturnObj.getExecutionInformation() == ReturnValueTypes.INFO_FUNCTION_RETURN) {
					//Die Funktion soll beendet werden:
					break;
				}
				else {
					//Es ist ein Fehler aufgetreten:
					//An dieser Stelle wird der Fehler zurueckgegeben, der eine erfolgreiche Rekursion verhindert! TODO: Fehler beheben! <------------------------------------------------
					return new ReturnValue<Token>(null, processReturnObj.getExecutionInformation());
				}
			}
		}
		
		//Variablen der vorherigen Funktion wiedereinfuehren:
		interpreterObj.changeFunctionAtoms(lOldFunctionAtomsObj);
		
		//Funktion wurde erfolgreich ausgefuehrt:
		Token newReturnTokenObj = new Token(functionReturnValueObj.getValue(), functionReturnValueObj.getType());
		functionReturnValueObj = new Token("0.00", TokenTypes.TOKEN_NUMBER);
		return new ReturnValue<Token>(newReturnTokenObj, ReturnValueTypes.SUCCESS);
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
		
		//Rueckgabewerte von Funktionsaufrufen einfuegen:
		nBracketsOpened = 0;
		nBracketsClosed = 0;
		for (int i = 0; i < lTokensObj.size(); i++) {
			Token currentTokenObj = new Token(lTokensObj.get(i).getValue(), lTokensObj.get(i).getType());
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
				//Es handelt sich um den Bezeichner einer Variablen oder Funktion:
				if (i + 2 <= lTokensObj.size() && lTokensObj.get(i + 1).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED) && !lTokensObj.get(i + 2).getType().equals(TokenTypes.TOKEN_OPERATOR)) {
					//Es handelt sich bei dem Bezeichner um eine Funktion:
					LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>(); //Speichert die Tokens der Funktion.
					lFunctionTokensObj.add(lTokensObj.remove(i)); //Namen hinzufuegen
					lFunctionTokensObj.add(lTokensObj.remove(i)); //Klammer der Parameter hinzufuegen:
					int nFunctionBracketsClosed = 0;
					int nFunctionBracketsOpened = 1;
					//Tokens des Funktionsaufrufes herausfinden:
					while (!lTokensObj.isEmpty()) {
						Token currentFunctionTokenObj = new Token(lTokensObj.get(i).getValue(), lTokensObj.remove(i).getType());
						if (currentFunctionTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
							nFunctionBracketsClosed++;
						}
						if (currentFunctionTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
							nFunctionBracketsOpened++;
						}
						lFunctionTokensObj.add(currentFunctionTokenObj);
						if (nFunctionBracketsOpened == nFunctionBracketsClosed) {
							break;
						}
					}
					//Funktion ausfuehren:
					ReturnValue<Token> functionExecutionQueryObj = new ReturnValue<Token>();
					functionExecutionQueryObj = executeFunction(lFunctionTokensObj);
					if (functionExecutionQueryObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es ist ein Fehler aufgetreten:
						return new ReturnValue<String>(null, functionExecutionQueryObj.getExecutionInformation());
					}
					lTokensObj.add(i, functionExecutionQueryObj.getReturnValue());
				}
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
		else if (pnErrorMessage == ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER) {
			System.out.print("the function call has an incorrect number of arguments.");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_NO_RETURN_VALUE) {
			System.out.print("non-existing return value was expected.");
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
