package parser;

import java.util.LinkedList;

import errorHandling.ReturnValueTypes;
import errorHandling.ReturnValue;
//import parser.BinaryTree;
import tokenizer.*;

/**
 * Der Parser entwirft anhand einer Liste an Tokens einen abstrakten Syntaxbaum.
 * 
 * @version	05.01.2021
 * @author	Christian S
 */
public class Parser {
	/**
	 * Erstellt einen abstrakten Syntaxbaum, anhand der als Parameter angegebenen Liste.
	 * 
	 * @param ptAST		Abstrakter Syntaxbaum, welcher durch neuen Durchlauf der Methode erweitert werden soll.
	 * 					Dieser AST darf nicht leer sein!
	 * @param plTokens	Liste an Tokens, welche fuer die Entwicklung des AST verwendet werden soll.
	 * 
	 * @return			Abstrakter Syntaxbaum.
	 */
	private ReturnValue<BinaryTree<Token>> createBinaryTree(BinaryTree<Token> ptAST, LinkedList<Token> plTokens) {
		if (plTokens.isEmpty()) {
			//Liste an Tokens ist leer:
			return new ReturnValue<BinaryTree<Token>>(ptAST, ReturnValueTypes.SUCCESS);
		}
		
		if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
			//Geschlossene Klammer gefunden -> Rekursion beenden:
			return new ReturnValue<BinaryTree<Token>>(ptAST, ReturnValueTypes.SUCCESS);
		}
		
		BinaryTree<Token> tNewAST = new BinaryTree<Token>(); //Speichert den neuen AST.
		tNewAST.setContent(ptAST.getContent()); //Operator setzten.
		
		if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
			//Neue Klammer geoeffnet -> Neue Rechnung starten:
			plTokens.remove(0); //Klammer entfernen:
			BinaryTree<Token> tNewAST_RightSubTree = new BinaryTree<Token>(plTokens.get(0)); //Rechter Teilbaum
			
			ReturnValue<BinaryTree<Token>> rekursionReturnObj = new ReturnValue<BinaryTree<Token>>();
			rekursionReturnObj = createBinaryTree(plTokens); //REKURSION :O
			
			if (rekursionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es gab einen Fehler:
				return rekursionReturnObj;
			}
			
			tNewAST_RightSubTree = rekursionReturnObj.getReturnValue();
			
			tNewAST.setLeftSubTree(ptAST); //Linken Teilbaum dem neuen AST hinzufuegen.
			tNewAST.setRightSubTree(tNewAST_RightSubTree); //Rechten Teilbaum hinzufuegen.
			//Rechnung aus der Liste an Tokens entfernen:
			int nBracketBalance = 1;
			while (!plTokens.isEmpty()) {
				if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Geoeffnete Klammer:
					nBracketBalance++;
				}
				else if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					//Geschlossene Klammer:
					nBracketBalance--;
				}
				plTokens.remove(0);
				if (nBracketBalance == 0) {
					//Es wurden gleich viele Klammern geoeffnet und geschlossen -> Erster Operand wurde aus der Liste entfernt.
					break;
				}
			}
		}
		else {
			//Es wird keine neue Klammer geoeffnet, sodass keine neue Rechnung gestartet wird:
			BinaryTree<Token> tNewAST_RightSubTree = new BinaryTree<Token>(plTokens.get(0)); //Rechter Teilbaum.
			tNewAST.setLeftSubTree(ptAST); //Linken Teilbaum dem neuen AST hinzufuegen.
			tNewAST.setRightSubTree(tNewAST_RightSubTree); //Rechten Teilbaum hinzufuegen.
			plTokens.remove(0); //Token aus der Liste entfernen.
		}
		
		ReturnValue<BinaryTree<Token>> rekursionReturnObj = new ReturnValue<BinaryTree<Token>>();
		rekursionReturnObj = createBinaryTree(tNewAST, plTokens); //REKURSION :O
		
		if (rekursionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			//Es ist ein Fehler aufgetreten:
			return rekursionReturnObj;
		}
		
		tNewAST = rekursionReturnObj.getReturnValue();
		
		ReturnValue<BinaryTree<Token>> returnASTObj = new ReturnValue<BinaryTree<Token>>(tNewAST, ReturnValueTypes.SUCCESS);
		
		return returnASTObj;
	}
	
	/**
	 * Startet den rekursiven Prozess, bei welchem ein abstrakter Syntaxbaum erstellt wird.
	 * 
	 * @param plTokens	Liste an Tokens, aus welchen der AST erstellt werden soll. Der Operator muss sich an der
	 * 					ersten Position in der Liste befinden, sonst wird null zurueckgegeben.
	 * 
	 * @return			Abstrakter Syntaxbaum.
	 */
	private ReturnValue<BinaryTree<Token>> createBinaryTree(LinkedList<Token> plTokens) {
		if (plTokens.size() <= 3) {
			//Liste an Tokens ist leer, oder enthaelt nicht genug Operanden:
			ReturnValue<BinaryTree<Token>> tEmptyAST = new ReturnValue<BinaryTree<Token>>(new BinaryTree<Token>(), ReturnValueTypes.ERROR_NOT_ENOUGH_OPERANDS);
			return tEmptyAST;
		}
		else if (!plTokens.get(0).getType().equals(TokenTypes.TOKEN_OPERATOR) && !plTokens.get(0).getType().equals(TokenTypes.TOKEN_OPERATOR_BOOLEAN)) {
			//Es befindet sich kein Operator an der ersten Stelle in der Liste:
			ReturnValue<BinaryTree<Token>> tEmptyAST = new ReturnValue<BinaryTree<Token>>(new BinaryTree<Token>(), ReturnValueTypes.ERROR_SYNTAX);
			return tEmptyAST;
		}
		
		Token operatorTokenObj = new Token(plTokens.get(0).getValue(), plTokens.get(0).getType()); //Speichert den Operator als Token.
		plTokens.remove(0); //Operator entfernen.
		
		//Ersten Operanden herausfinden:
		BinaryTree<Token> tOperand1AST = new BinaryTree<Token>(); //Speichert den Teilbaum des ersten Operanden.
		if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
			//Fuer den ersten Operanden muss eine weitere Rechnung durchgefuehrt werden:
			LinkedList<Token> lTokensOperand1 = new LinkedList<Token>();
			plTokens.remove(0); //Klammer entfernen.
			lTokensOperand1.addAll(plTokens);
			
			ReturnValue<BinaryTree<Token>> tReturnOperand1 = new ReturnValue<BinaryTree<Token>>();
			tReturnOperand1 = createBinaryTree(lTokensOperand1); //REKURSION :O
			if (tReturnOperand1.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist bei der Rekursion zu einem Fehler gekommen:
				return tReturnOperand1;
			}
			tOperand1AST = tReturnOperand1.getReturnValue();
			
			//Rechnung des ersten Operanden aus der Liste an Tokens entfernen:
			int nBracketBalance = 1;
			while (!plTokens.isEmpty()) {
				if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Geoeffnete Klammer:
					nBracketBalance++;
				}
				else if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					//Geschlossene Klammer:
					nBracketBalance--;
				}
				plTokens.remove(0);
				if (nBracketBalance == 0) {
					//Es wurden gleich viele Klammern geoeffnet und geschlossen -> Erster Operand wurde aus der Liste entfernt.
					break;
				}
			}
		}
		else {
			//Fuer den ersten Operanden muss keine weitere Rechnung durchgefuehrt werden:
			tOperand1AST.setContent(plTokens.get(0));
			plTokens.remove(0); //Operanden entfernen.
		}
		
		//Zweiten Operanden herausfinden:
		BinaryTree<Token> tOperand2AST = new BinaryTree<Token>(); //Speichert den Teilbaum des zweiten Operanden.
		if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
			//Fuer den zweiten Operanden muss eine weitere Rechnung durchgefuehrt werden:
			LinkedList<Token> lTokensOperand2 = new LinkedList<Token>();
			plTokens.remove(0); //Klammer entfernen.
			lTokensOperand2.addAll(plTokens);

			ReturnValue<BinaryTree<Token>> tReturnOperand2 = new ReturnValue<BinaryTree<Token>>();
			tReturnOperand2 = createBinaryTree(lTokensOperand2); //REKURSION :O;
			if (tReturnOperand2.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist bei der Rekursion zu einem Fehler gekommen:
				return tReturnOperand2;
			}
			tOperand2AST = tReturnOperand2.getReturnValue();
			
			//Rechnung des zweiten Operanden aus der Liste an Tokens entfernen:
			int nBracketBalance = 1;
			while (!plTokens.isEmpty()) {
				if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					//Geoeffnete Klammer:
					nBracketBalance++;
				}
				else if (plTokens.get(0).getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					//Geschlossene Klammer:
					nBracketBalance--;
				}
				plTokens.remove(0);
				if (nBracketBalance == 0) {
					//Es wurden gleich viele Klammern geoeffnet und geschlossen -> Zweiter Operand wurde aus der Liste entfernt.
					break;
				}
			}
		}
		else {
			//Fuer den zweiten Operanden muss keine weitere Rechnung durchgefuehrt werden:
			tOperand2AST.setContent(plTokens.get(0));
			plTokens.remove(0); //Operanden entfernen.
		}
		
		//Operanden wurden herausgefunden:
		BinaryTree<Token> tAbstractSyntaxTree = new BinaryTree<Token>(operatorTokenObj, tOperand1AST, tOperand2AST); //Abstrakter Syntaxbaum, welcher fuer die Rekursion verwendet wird.
		
		//Rekursion starten:
		ReturnValue<BinaryTree<Token>> rekursionReturnObj = new ReturnValue<BinaryTree<Token>>(); //Speichert den Rueckgabewert der Rekursion.
		rekursionReturnObj = createBinaryTree(tAbstractSyntaxTree, plTokens); //REKURSION :O
		if (rekursionReturnObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
			return rekursionReturnObj;
		}
		tAbstractSyntaxTree = rekursionReturnObj.getReturnValue();
		
		return new ReturnValue<BinaryTree<Token>>(tAbstractSyntaxTree, ReturnValueTypes.SUCCESS);
	}
	
	
	
	/**
	 * Erstellt einen abstrakten Syntaxbaum, anhand einer Liste an Tokens.
	 * 
	 * @param plTokens	Liste an Tokens, welche verarbeitet werden sollen.
	 * @return			Abstrakter Syntaxbaum.
	 */
	public ReturnValue<BinaryTree<Token>> parse(LinkedList<Token> plTokens) {
		if (plTokens.isEmpty()) {
			//Liste an Tokens ist leer:
			return new ReturnValue<BinaryTree<Token>>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		else {
			int nOpenedBrackets = 0;
			int nClosedBrackets = 0;
			for (int i = 0; i < plTokens.size(); i++) {
				if (plTokens.get(i).getType().equals(TokenTypes.TOKEN_BRACKET_OPENED)) {
					nOpenedBrackets++;
				}
				else if (plTokens.get(i).getType().equals(TokenTypes.TOKEN_BRACKET_CLOSED)) {
					nClosedBrackets++;
				}
			}
			if (nOpenedBrackets != nClosedBrackets) {
				//Die Anzahl an geoeffneten und geschlossenen Klammern stimmen nicht ueberein:
				return new ReturnValue<BinaryTree<Token>>(null, ReturnValueTypes.ERROR_SYNTAX);
			}
		}
		
		plTokens.remove(0);
		
		if (plTokens.isEmpty()) {
			//Keine Tokens vorhanden:
			return new ReturnValue(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		
		ReturnValue<BinaryTree<Token>> rekursionReturn = new ReturnValue<BinaryTree<Token>>();
		rekursionReturn = createBinaryTree(plTokens); //REKURSION :O
		
		return rekursionReturn;
	}
}
