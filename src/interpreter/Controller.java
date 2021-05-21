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
	 * Dieses Attribut speichert alle Klassentypen (NICHT DIE INSTANZEN!)
	 */
	private LinkedList<Class> lClassesObj;
	
	/**
	 * Dieses Attribut speichert alle Instanzen von Klassen.
	 */
	private LinkedList<ClassInstance> lClassInstancesObj;
	
	/**
	 * Speichert den Dateinamen, aus welchem der Quellcode geladen werden soll:
	 */
	private String sFileName;
	
	
	
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
		lClassesObj = new LinkedList<Class>();
		lClassInstancesObj = new LinkedList<ClassInstance>();
		sFileName = psFileName;
	}
	
	
	
	/**
	 * Diese Methode startet die Rekursion, bei welcher der Quellcode schrittweise ausgefuehrt wird.
	 * 
	 * @return	Fehlermeldung
	 */
	public int startController() {
		//Den aktuellen Quellcode zerteilen:
		int nReturnValue = extractSourceCode(sFileName);
		if (nReturnValue != ReturnValueTypes.SUCCESS) {
			//Es ist ein Fehler aufgetreten:
			return nReturnValue;
		}
		
		//Herausfinden, mit welcher Funktion gestartet werden soll.
		for (int i = 0; i < lFunctionsObj.size(); i++) {
			if (lFunctionsObj.get(i).getName().equals(KeywordTypes.FUNCTION_MAIN)) {
				//Startfunktion gefunden:
				LinkedList<Atom> lParameterObj = new LinkedList<Atom>();
				lParameterObj = lFunctionsObj.get(i).getParameters();
				if (lParameterObj.size() != 0) {
					//Es sind Parameter vorhanden: Syntaxfehler (main-Funktion erhaelt keine Parameter).
					return ReturnValueTypes.ERROR_MAIN_FUNCTION_HAS_PARAMETER;
				}
				interpreterObj.changeFunctionAtoms(lFunctionsObj.get(i).getParameters());
				for (int j = 0; j < lFunctionsObj.get(i).getExpressionAmount(); j++) {
					//Ausdruecke verarbeiten:
					ReturnValue<Object> processReturnObj; //Speichert den Rueckgabewert der process()-Funktion.
					
					//Zum Abfangen eines StackoverflowErrors.
					try {
						processReturnObj = process(lFunctionsObj.get(i).getExpression(j));
					}
					catch (StackOverflowError exceptionObj) {
						//Es kam zu einem Stackoverflowerror:
						return ReturnValueTypes.ERROR_STACK_OVERFLOW;
					}
					
					if (processReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
						//Es kam zu einem Fehler:
						return processReturnObj.getExecutionInformation();
					}
				}
				return ReturnValueTypes.SUCCESS; //Beenden, nachdem alle Ausdruecke verarbeitet wurden.
			}
		}
		//Startfunktion nicht gefunden:
		return ReturnValueTypes.ERROR_NO_MAIN_FUNCTION;
	}
	
	
	
	/**
	 * Diese Methode durchlaeuft den Quellcode
	 * @param psFileName
	 * 
	 * @return	Fehlermeldung.
	 */
	private int extractSourceCode(String psFileName) {
		//Quellcode in Tokens umsetzten:
		ReturnValue<LinkedList<String>> outputFileScannerObj = new ReturnValue<LinkedList<String>>();
		outputFileScannerObj = FileScanner.readFile(psFileName);
		if (outputFileScannerObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es kam zu einem Fehler:
			return outputFileScannerObj.getExecutionInformation();
		}
		lsSourceCode.addAll(outputFileScannerObj.getReturnValue());
		
		//Quellcode Ausdruck fuer Ausdruck durchlaufen:
		for (int i = 0; i < lsSourceCode.size(); i++) {
			ReturnValue<Object> returnObj = new ReturnValue<Object>(); //Rueckgabewert des Tokenizers.
			
			LinkedList<Token> lTokensObj = new LinkedList<Token>(); //Speichert den aktuellen Ausdruck im Quellcode als Tokens.
			lTokensObj.addAll(tokenizerObj.tokenize(lsSourceCode.get(i)));
			
			//Jeden Ausdruck in eine Funktion umwandeln:
			Token tokenObj = lTokensObj.poll();
			if (!tokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				//Der erste Token ist keien geoeffnete Klammer: Syntaxfehler
				return ReturnValueTypes.ERROR_SYNTAX;
			}
			tokenObj = lTokensObj.poll();
			if (tokenObj.getValue().equals(KeywordTypes.KEYWORD_DEFINE)) {
				//Neue Funktion wird definiert:
				Function newFunctionObj = new Function(lTokensObj); //Speichert die Funktion.
				//Herausfinden, ob eine Funktion mit demselben Namen bereits definiert wurde:
				for (int j = 0; j < lFunctionsObj.size(); j++) {
					if (lFunctionsObj.get(j).getName().equals(newFunctionObj.getName())) {
						//Der Name der aktuellen Funktion wurde bereits durch eine andere Funktion registriert:
						return ReturnValueTypes.ERROR_FUNCTION_NAME_IS_IDENTICAL;
					}
				}
				
				lFunctionsObj.add(newFunctionObj);
			}
			else if (tokenObj.getValue().equals(KeywordTypes.KEYWORD_CLASS)) {
				//Neue Klasse wird definiert:
				lClassesObj.add(new Class(lTokensObj));
			}
			else if (tokenObj.getValue().equals(KeywordTypes.KEYWORD_IMPORT)) {
				//Es soll eine neue Quellcode-Datei importiert werden:
				if (lTokensObj.peek().getType().equals(TokenTypes.TOKEN_STRING)) {
					//Naechster Token stellt einen String dar, der moeglicherweise den Namen der Quellcode-Datei darstellt:
					try {
						LinkedList<String> lsOldSourceCode = new LinkedList<String>(); //Speichert den alten Quellcode.
						lsOldSourceCode.addAll(lsSourceCode);
						lsSourceCode.clear();
						int nReturnValue = extractSourceCode(lTokensObj.poll().getValue()); //Fuegt die neue Quellcode-Datei dieser Instanz des Interpreters hinzu.
						if (nReturnValue != ReturnValueTypes.SUCCESS) {
							//Es ist ein Fehler aufgetreten:
							return nReturnValue;
						}
						//Alten Quellcode wieder hinzufuegen:
						lsSourceCode.clear();
						lsSourceCode.addAll(lsOldSourceCode);
					}
					catch (StackOverflowError exceptionObj) {
						//Es kam zu einem StackOverflowError -> Die "import"-Schluesselwoerter wurden fehlerhaft verwendet:
						return ReturnValueTypes.ERROR_IMPORT_STACK_OVERFLOW;
					}
				}
			}
			else {
				//Erstes Schluesselwort ist nicht "defun" oder "class": Dyntaxfehler
				return ReturnValueTypes.ERROR_SYNTAX;
			}
		}
		//Der Quellcode wurde erfolgreich zerteilt und dem Interpreter hinzugefuegt:
		return ReturnValueTypes.SUCCESS;
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
					Atom atom;
					if (!plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
						//Die Variable soll ebenfalls instanziiert werden:
						
						Token variableValueObj = new Token(null, null); //Speichert den Wert der Variablen.
						variableValueObj = plTokensObj.poll();
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
							atom = new Atom(variableName.getValue(), calculationReturn.getReturnValue(), TokenTypes.TOKEN_NUMBER);
						}
						else if (variableValueObj.getType().equals(TokenTypes.TOKEN_STRING) || variableValueObj.getType().equals(TokenTypes.TOKEN_NUMBER) || variableValueObj.getType().equals(TokenTypes.TOKEN_BOOLEAN)) {
							//Es handelt sich um einen String oder eine Nummer oder einen Wahrheitswert:
							atom = new Atom(variableName.getValue(), variableValueObj.getValue(), variableValueObj.getType());
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
								atom = new Atom(variableName.getValue(), functionReturnObj.getReturnValue().getValue(), functionReturnObj.getReturnValue().getType());
							}
							else {
								//Es muss sich um eine Variable handeln:
								ReturnValue<Atom> atomSearchQueryObj = new ReturnValue<Atom>();
								atomSearchQueryObj = interpreterObj.searchAtom(variableValueObj.getValue());
								if (atomSearchQueryObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
									//Es ist ein Fehler aufgeteten:
									return new ReturnValue<Object>(null, atomSearchQueryObj.getExecutionInformation());
								}
								atom = new Atom(variableName.getValue(), atomSearchQueryObj.getReturnValue().getValue(), atomSearchQueryObj.getReturnValue().getType());
							}
						}
						else {
							//Es ist ein nicht angebrachter Token vorhanden -> SYNTAX FEHLER:
							return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_SYNTAX);
						}
					}
					else {
						//Die Variable soll nicht initialisiert werden:
						atom = new Atom(variableName.getValue(), "0.0", TokenTypes.TOKEN_NUMBER); //Neues Atom hat standartmaessig den Wert 0.0.
					}
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
				
				//String in der Konsole ausgeben:
				ReturnValue<Object> printReturnObj = printString(sPrint, false);
				if (printReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
					return printReturnObj;
				}
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
				
				//String in der Konsole ausgeben:
				ReturnValue<Object> printReturnObj = printString(sPrint, true);
				if (printReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
					return printReturnObj;
				}
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
				//Anweisungen der else-Verzweigung herausfinden:
				nBracketsClosed = 0;
				nBracketsOpened = 0;
				LinkedList<Token> lElseStatementObj = new LinkedList<Token>();
				if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Es existiert eien else-Verzweigung:
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
						lElseStatementObj.add(currentToken);
						if (nBracketsClosed == nBracketsOpened) {
							//Es wurden gleich viele Klammern geoeffnet und geschlossen:
							break;
						}
					}
					
				}
				ReturnValue<Object> ifReturn = new ReturnValue<Object>(); //Speichert den Rueckgabewert der if-Verzweigung.
				
				//Zum Vorbeugen eines Stackoverflowerrors:
				try {
					ifReturn = ifStatement(lConditionObj, lExpressionObj, lElseStatementObj);
				}
				catch (StackOverflowError exception) {
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_STACK_OVERFLOW);
				}
				
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
			//Der erste Token ist ein Bezeichner -> Aufruf einer Funktion, oder Deklaration eines Objektes einer Klasse:
			
			if (plTokensObj.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				//Es handelt sich um eine Funktion:
				
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
				//Es handelt sich um die instanziierung eines neuen Objektes.
				
				//Herausfinden, ob Klasse existiert:
				String sClassName = firstTokenObj.getValue(); //Speichert den Bezeichner der Klasse, dessen Objekt instanziiert werden soll.
				Class classTypeObj;
				boolean bClassAvailable = false;
				for (int i = 0; i < lClassesObj.size(); i++) {
					if (lClassesObj.get(i).getName().equals(sClassName)) {
						//Klasse existiert:
						bClassAvailable = true;
						classTypeObj = new Class(lClassesObj.get(i).getClassTokens());
						break;
					}
				}
				
				if (!bClassAvailable) {
					//Klasse existiert nicht:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_CLASS);
				}
				else {
					//Objekt "muss" instanziiert werden, da der JAVA-Compiler sonst einen Fehler ausgibt, wenn das Objekt der Lisp-Klasse
					//instanziiert wird. Eigentlich, sollte es dazu aber nicht kommen, da das Objekt classTypeObj instanziiert wird, wenn
					//der Klassentyp gefunden wird...
					//Machste nix nh... \(*_*)/
					classTypeObj = new Class(lClassesObj.peek().getClassTokens());
				}
				
				//Herausfinden, ob Instanzbezeichner verfuegbar ist:
				String sInstanceName = plTokensObj.poll().getValue(); //Speichert den Instanznamen des Objektes.
				ReturnValue<Atom> atomSearchQueryObj = new ReturnValue<Atom>();
				atomSearchQueryObj = interpreterObj.searchAtom(sInstanceName);
				if (atomSearchQueryObj.getExecutionInformation() == ReturnValueTypes.SUCCESS) {
					//Bezeichner existiert bereits als Variablenbezeichner:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_INSTANCE_NAME_DOES_EXIST);
				}
				for (int i = 0; i < lFunctionsObj.size(); i++) {
					if (lFunctionsObj.get(i).getName().equals(sInstanceName)) {
						//Bezeichner existiert als Funktionsname:
						return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_INSTANCE_NAME_DOES_EXIST);
					}
				}
				for (int i = 0; i < lClassesObj.size(); i++) {
					if (lClassesObj.get(i).getName().equals(sInstanceName)) {
						//Bezeichner existiert als Klassename:
						return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_INSTANCE_NAME_CANNOT_BE_CLASS_NAME);
					}
				}
				for (int i = 0; i < lClassInstancesObj.size(); i++) {
					if (lClassInstancesObj.get(i).getName().equals(sInstanceName)) {
						//Bezeichner existiert bereits als Instanzname:
						return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_INSTANCE_NAME_DOES_EXIST);
					}
				}
				
				//Instanzname kann vergeben werden:
				lClassInstancesObj.add(new ClassInstance(classTypeObj));
				//System.out.println("[DEBUG]: New instance of \"" + sClassName + "\" named \"" + sInstanceName + "\" created.");
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
		boolean bFunctionIsPreDefined = false; //Gibt an, ob es sich um eine vordefinierte Funktion handelt.
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
			
			//Herausgfinden, ob es sich um eine vordefinierte Funktion handelt:
			if (sFunctionName.equals(KeywordTypes.FUNCTION_LENGTH)) {
				bFunctionIsPreDefined = true;
			}
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_ISNUMBER)) {
				bFunctionIsPreDefined = true;
			}
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_SIN)) {
				bFunctionIsPreDefined = true;
			}
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_COS)) {
				bFunctionIsPreDefined = true;
			}
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_TAN)) {
				bFunctionIsPreDefined = true;
			}
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_SQRT)) {
				bFunctionIsPreDefined = true;
			}
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_CHARAT)) {
				bFunctionIsPreDefined = true;
			}
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_SUBSTRING)) {
				bFunctionIsPreDefined = true;
			}
			else {
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
			}
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
		
		//INHALT DIESER IF-VERZWEIGUNG WIRD AUSSCHLIESSLICH AUFGERUFEN, WENN DIE FUNKTION VORDEFINIERT IST:
		if (bFunctionIsPreDefined) {
			//Funktionsnamen verarbeiten:
			if (sFunctionName.equals(KeywordTypes.FUNCTION_LENGTH)) {
				//Die lenghth()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.LENGTH_PARAMETERS) {
					return new ReturnValue<Token>(new Token(String.valueOf(PreDefinedFunctions.length(lParametersObj.peek().getValue())), TokenTypes.TOKEN_NUMBER), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_ISNUMBER)) {
				//Die isNumber()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.ISNUMBER_PARAMETERS) {
					return new ReturnValue<Token>(new Token(PreDefinedFunctions.isNumber(lParametersObj.peek().getValue()), TokenTypes.TOKEN_BOOLEAN), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_SIN)) {
				//Die sin()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.SIN_PARAMETERS) {
					return new ReturnValue<Token>(new Token(PreDefinedFunctions.sin(lParametersObj.peek().getValue()), TokenTypes.TOKEN_NUMBER), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_COS)) {
				//Die sin()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.COS_PARAMETERS) {
					return new ReturnValue<Token>(new Token(PreDefinedFunctions.cos(lParametersObj.peek().getValue()), TokenTypes.TOKEN_NUMBER), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_TAN)) {
				//Die sin()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.TAN_PARAMETERS) {
					return new ReturnValue<Token>(new Token(PreDefinedFunctions.tan(lParametersObj.peek().getValue()), TokenTypes.TOKEN_NUMBER), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_SQRT)) {
				//Die sin()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.SQRT_PARAMETERS) {
					return new ReturnValue<Token>(new Token(PreDefinedFunctions.sqrt(lParametersObj.peek().getValue()), TokenTypes.TOKEN_NUMBER), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_CHARAT)) {
				//Die charAt()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.CHARAT_PARAMETERS) {
					return new ReturnValue<Token>(new Token(PreDefinedFunctions.charAt(lParametersObj.poll().getValue(), lParametersObj.poll().getValue()), TokenTypes.TOKEN_STRING), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else if (sFunctionName.equals(KeywordTypes.FUNCTION_SUBSTRING)) {
				//Die charAt()-Funktion:
				if (lParametersObj.size() == PreDefinedFunctions.SUBSTRING_PARAMETERS) {
					return new ReturnValue<Token>(new Token(PreDefinedFunctions.substring(lParametersObj.poll().getValue(), lParametersObj.poll().getValue(), lParametersObj.poll().getValue()), TokenTypes.TOKEN_STRING), ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER);
			}
			
			else {
				//Unbekannte Funktion:
				return new ReturnValue<Token>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
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
		
		//Rueckgabewerte von Funktionsaufrufen einfuegen:
		int nBracketsOpened = 0;
		int nBracketsClosed = 0;
		for (int i = 0; i < plConditionObj.size(); i++) {
			Token currentTokenObj = new Token(plConditionObj.get(i).getValue(), plConditionObj.get(i).getType());
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
				nBracketsClosed++;
			}
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
				nBracketsOpened++;
			}
			if (currentTokenObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
				//Es handelt sich um den Bezeichner einer Variablen oder Funktion:
				if (i + 2 <= plConditionObj.size() && plConditionObj.get(i + 1).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED) && !plConditionObj.get(i + 2).getType().equals(TokenTypes.TOKEN_OPERATOR)) {
					//Es handelt sich bei dem Bezeichner um eine Funktion:
					LinkedList<Token> lFunctionTokensObj = new LinkedList<Token>(); //Speichert die Tokens der Funktion.
					lFunctionTokensObj.add(plConditionObj.remove(i)); //Namen hinzufuegen
					lFunctionTokensObj.add(plConditionObj.remove(i)); //Klammer der Parameter hinzufuegen:
					int nFunctionBracketsClosed = 0;
					int nFunctionBracketsOpened = 1;
					//Tokens des Funktionsaufrufes herausfinden:
					while (!plConditionObj.isEmpty()) {
						Token currentFunctionTokenObj = new Token(plConditionObj.get(i).getValue(), plConditionObj.remove(i).getType());
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
						return new ReturnValue<Boolean>(false, functionExecutionQueryObj.getExecutionInformation());
					}
					plConditionObj.add(i, functionExecutionQueryObj.getReturnValue());
				}
			}
		}
		
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
	 * @param plIfStatement		Anweisungen der Verzweigung.
	 * @param plElseStatement	Anweisungen einer optionalen else-Verzweigung.
	 * @return					Gibt an, ob ein Fehler aufgetreten ist.
	 */
	private ReturnValue<Object> ifStatement(LinkedList<Token> plConditionObj, LinkedList<Token> plIfStatement, LinkedList<Token> plElseStatement) {
		//Ueberpruefen, ob die Bedingung wahr ist:
		ReturnValue<Boolean> bConditionObj = new ReturnValue<Boolean>();
		bConditionObj = condition(plConditionObj);
		if (bConditionObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es ist ein Fehler aufgetreten:
			return new ReturnValue<Object>(null, bConditionObj.getExecutionInformation());
		}
		
		//Ueberpruefen, ob die Bedingung wahr oder falsch ist:
		if (!bConditionObj.getReturnValue() && plElseStatement.isEmpty()) {
			//Die Bedingung ist falsch und es gibt keine else-Verzweigung:
			return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
		}
		
		//Variablen, welche NICHT in der Kontrollstruktur vorkommen speichern:
		LinkedList<Atom> lOldAtomsObj = new LinkedList<Atom>();
		lOldAtomsObj.addAll(interpreterObj.getAllAtoms());
		
		//Anweisungen in eine Liste an Listen an Tokens einordnen:
		plIfStatement.poll(); //Erste geoeffnete Klammer entfernen.
		int nBracketsOpened = 0; //Speichert die Anzahl der geoeffneten Klammern.
		int nBracketsClosed = 0; //Speichert die Anzahl der geschlossenen Klammern.
		LinkedList<Token> lCurrentExpressionObj = new LinkedList<Token>(); //Speichert den aktuellen Ausdruck.
		
		if (bConditionObj.getReturnValue()) {
			//Die Bedingung ist wahr:
			while (!plIfStatement.isEmpty()) {
				if (plIfStatement.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					nBracketsClosed++;
				}
				else if (plIfStatement.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					nBracketsOpened++;
				}
				lCurrentExpressionObj.add(plIfStatement.poll());
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
		
		else {
			//Die Bedingung ist falsch:
			plElseStatement.poll(); //Erste geoeffnete Klammer entfernen.
			while (!plElseStatement.isEmpty()) {
				if (plElseStatement.peek().getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					nBracketsClosed++;
				}
				else if (plElseStatement.peek().getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					nBracketsOpened++;
				}
				lCurrentExpressionObj.add(plElseStatement.poll());
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
		
		//Atomwerte der alten Variablen ueberschreiben, falls diese geaendert wurden:
		LinkedList<Atom> lCurrentAtomsObj = new LinkedList<Atom>();
		lCurrentAtomsObj = interpreterObj.getAllAtoms();
		for (int i = 0; i < lCurrentAtomsObj.size(); i++) {
			for (int j = 0; j < lOldAtomsObj.size(); j++) {
				if (lOldAtomsObj.get(j).getName().equals(lCurrentAtomsObj.get(i).getName())) {
					//Bezeichner der beiden Variablen stimmen ueberein:
					Atom nCurrentAtom = new Atom(lCurrentAtomsObj.get(i).getName(), lCurrentAtomsObj.get(i).getValue(), lCurrentAtomsObj.get(i).getType());
					lOldAtomsObj.remove(j);
					lOldAtomsObj.add(j, nCurrentAtom);
				}
			}
		}
		interpreterObj.changeFunctionAtoms(lOldAtomsObj); //Alte variablen wieder hinzufuegen.
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
		//Variablen, welche NICHT in der Kontrollstruktur vorkommen speichern:
		LinkedList<Atom> lOldAtomsObj = new LinkedList<Atom>();
		lOldAtomsObj.addAll(interpreterObj.changeFunctionAtoms(interpreterObj.getAllAtoms()));
		
		//Die Schleife laeuft immer weiter bis die Methode beendet wird, wenn die Bedingung falsch ist.
		while (true) {
			//Atomwerte der alten Variablen ueberschreiben, falls diese geaendert wurden:
			LinkedList<Atom> lCurrentAtomsObj = new LinkedList<Atom>();
			lCurrentAtomsObj = interpreterObj.getAllAtoms();
			for (int i = 0; i < lCurrentAtomsObj.size(); i++) {
				for (int j = 0; j < lOldAtomsObj.size(); j++) {
					if (lOldAtomsObj.get(j).getName().equals(lCurrentAtomsObj.get(i).getName())) {
						//Bezeichner der beiden Variablen stimmen ueberein:
						Atom nCurrentAtom = new Atom(lCurrentAtomsObj.get(i).getName(), lCurrentAtomsObj.get(i).getValue(), lCurrentAtomsObj.get(i).getType());
						lOldAtomsObj.remove(j);
						lOldAtomsObj.add(j, nCurrentAtom);
					}
				}
			}
			interpreterObj.changeFunctionAtoms(lOldAtomsObj); //Die alten Variablen wiederherstellen.
			
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
				interpreterObj.changeFunctionAtoms(lOldAtomsObj); //Die alten Variablen wiederherstellen.
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
	
	/**
	 * Diese Methode gibt einen String in der Konsole aus.
	 * 
	 * @param psString			String, welcher ausgegeben werden soll.
	 * @param pbAddLineBreak	Ob ein Zeilenumbruch am Ende angefuehrt werden soll.
	 */
	private ReturnValue<Object> printString(String psString, boolean pbAddLineBreak) {
		//String zeichenweise durchlaufen:
		for (int i = 0; i < psString.length(); i++) {
			char chCurrentCharacter = psString.charAt(i);
			if (chCurrentCharacter == '\\') {
				//Aktuelles Zeichen leitet ein Steuerzeichen ein:
				if (i >= psString.length() - 1) {
					//String ist nicht lang genug, um ein Steuerzeichen zu enthalten:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_STRING_TOO_SHORT);
				}
				i++;
				char chControlCharacter = psString.charAt(i); //Speichert das Steuerzeichen.
				
				switch (chControlCharacter) {
				case 'n':
					System.out.print('\n');
					break;
					
				case 't':
					System.out.print('\t');
					break;
					
				case '\"':
					System.out.print('\"');
					break;
					
				case '\'':
					System.out.print('\'');
					break;
					
				case 'b':
					System.out.print('\b');
					break;
					
				case 'f':
					System.out.print('\f');
					break;
					
				case 'r':
					System.out.print('\r');
					break;
					
				default:
					//Unbekanntes Steuerzeichen:
					return new ReturnValue<Object>(null, ReturnValueTypes.ERROR_UNKNOWN_CTRL_CHAR);
				}
			}
			
			else {
				//Es handelt sich nicht um ein Steuerzeichen:
				System.out.print(chCurrentCharacter);
			}
		}
		
		//Zeilenumbruch hinzufuegen, falls dies gewollt ist:
		if (pbAddLineBreak) {
			System.out.println();
		}
		return new ReturnValue<Object>(null, ReturnValueTypes.SUCCESS);
	}
	
}
