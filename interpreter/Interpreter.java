package interpreter;

import java.util.LinkedList;

import errorHandling.ReturnValueTypes;
import errorHandling.ReturnValue;
import tokenizer.*;
import parser.*;



/**
 * Stellt den Interpreter dar, welcher einen Abstrakten Syntaxbaum interpretiert.
 * 
 * @version	24.01.2021
 * 
 * @author	Christian S
 */
public class Interpreter {
	/**
	 * Speichert alle Atome, welche vom Interpreter erfasst werden.
	 */
	private LinkedList<Atom> lAtomsObj;
	
	
	
	/**
	 * Erzeugt einen neuen Interpreter.
	 * In dieser Methode werden alle Standartvariablen (pi, e, ...) eingefuegt.
	 */
	public Interpreter() {
		lAtomsObj = new LinkedList<Atom>();
		lAtomsObj.add(new Atom("pi", "3.14159", TokenTypes.TOKEN_NUMBER)); //PI als Variable hinzufuegen
		lAtomsObj.add(new Atom("e", "2.71828", TokenTypes.TOKEN_NUMBER)); //E als Variable hinzufuegen
		lAtomsObj.add(new Atom("g", "9.81", TokenTypes.TOKEN_NUMBER)); //Gravitationskonstante als Variable hinzufuegen
	}
	
	
	
	/**
	 * Diese Methode fuegt ein Atom der Liste an Atomen hinzu. Wenn der Bezeichner
	 * eines Atoms bereits vorkommt, so wird es nicht hinzugefuegt.
	 * 
	 * @param pAtomObj	Atom, welches hinzugefuegt werden soll.
	 * 
	 * @return			Gibt an, ob das Atom erfolgreich hinzugefuegt wurde.
	 */
	public boolean addAtom(Atom pAtomObj) {
		//Herausfinden, ob das Atom bereits existiert:
		for (int i = 0; i < lAtomsObj.size(); i++) {
			if (lAtomsObj.get(i).getName().equals(pAtomObj.getName())) {
				//Das Atom existiert:
				return false;
			}
		}
		//Das Atom existiert nicht:
		lAtomsObj.add(pAtomObj);
		return true;
	}
	
	
	
	/**
	 * Diese Methode ersetzt ein Atom in der Liste an Atomen.
	 * 
	 * @param pAtomObj	Atom, durch welches ersetzt werden soll.
	 * 
	 * @return			Gibt an, ob das Atom erfolgreich ersetzt wurde.
	 */
	public boolean overrideAtom(Atom pAtomObj) {
		//Herausfinden, ob das Atom bereits existiert:
		for (int i = 0; i < lAtomsObj.size(); i++) {
			if (lAtomsObj.get(i).getName().equals(pAtomObj.getName())) {
				//Atom existiert an der Stelle i:
				lAtomsObj.set(i, pAtomObj);
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Diese Methode sucht das als Parameter angegebene Atom und gibt dessen
	 * Wert zurueck.
	 * 
	 * @param psName	Bezeichner des Atoms.
	 * @return			Atom.
	 */
	public ReturnValue<Atom> searchAtom(String psName) {
		//Herausfinden, ob das Atom existiert:
		for (int i = 0; i < lAtomsObj.size(); i++) {
			if (lAtomsObj.get(i).getName().equals(psName)) {
				//Atom gefunden:
				return new ReturnValue<Atom>(lAtomsObj.get(i), ReturnValueTypes.SUCCESS);
			}
		}
		//Atom wurde nicht gefunden:
		return new ReturnValue<Atom>(null, ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER);
	}
	
	
	/**
	 * Diese Funktion startet den rekursiven Prozess, bei welchem ein abstrakter Syntaxbaum
	 * ausgerechnet wird.
	 * 
	 * @param ptAbstractSyntaxTree	Abstrakter Syntaxbaum, welcher ausgerechnet werden soll.
	 * 
	 * @return						Ergebnis.
	 */
	private ReturnValue<Double> interpret_calculation(BinaryTree<Token> ptAbstractSyntaxTree) {
		double nLeftOperand = 0.00; //Speichert den Wert des linken Operand.
		double nRightOperand = 0.00; //Speichert den Wert des rechten Operand.
		
		if (ptAbstractSyntaxTree.getLeftChild() == null || ptAbstractSyntaxTree.getRightChild() == null) {
			//Es gibt keinen Teilbaum:
			return new ReturnValue<Double>(null, ReturnValueTypes.ERROR_SYNTAX);
		}
		
		
		
		//--------------- LINKEN OPERAND HERAUSFINDEN ---------------
		if (ptAbstractSyntaxTree.getLeftChild().getType().equals(TokenTypes.TOKEN_NUMBER)) {
			//Linker Operand ist eine Zahl:
			nLeftOperand = Double.parseDouble(ptAbstractSyntaxTree.getLeftChild().getValue());
		}
		else if (ptAbstractSyntaxTree.getLeftChild().getType().equals(TokenTypes.TOKEN_OPERATOR)) {
			//Linker Teilbaum stellt eine neue Rechnung dar:
			ReturnValue<String> leftSubTreeReturnValueObj = new ReturnValue<String>();
			leftSubTreeReturnValueObj = interpret(ptAbstractSyntaxTree.getLeftSubTree()); //REKURSION :O
			if (leftSubTreeReturnValueObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Bei der Rekursion ist ein Fehler aufgetreten:
				return new ReturnValue<Double>(null, leftSubTreeReturnValueObj.getExecutionInformation());
			}
			nLeftOperand = Double.parseDouble(leftSubTreeReturnValueObj.getReturnValue());
		}
		else if (ptAbstractSyntaxTree.getLeftChild().getType().equals(TokenTypes.TOKEN_STRING)){
			//Ein String kommt vor:
			//Aktuell: String kann nicht verrechnet werden -> FEHLER:
			return new ReturnValue<Double>(null, ReturnValueTypes.ERROR_CANNOT_OFFSET_STRING_TO_NUMBER);
		}
		else if (ptAbstractSyntaxTree.getLeftChild().getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
			//Ein Bezeichner (einer Variablen oder Funktion) kommt vor:
			ReturnValue<Atom> atomLeftOperandObj = searchAtom(ptAbstractSyntaxTree.getLeftChild().getValue()); //Speichert das Atom.
			if (atomLeftOperandObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es gab einen Fehler -> Rekursion beenden:
				return new ReturnValue<Double>(null, atomLeftOperandObj.getExecutionInformation());
			}
			else if (atomLeftOperandObj.getReturnValue().getType().equals(TokenTypes.TOKEN_STRING)) {
				//String soll zu einer Zahl umgewandelt werden -> FEHLER:
				return new ReturnValue<Double>(null, ReturnValueTypes.ERROR_CANNOT_OFFSET_STRING_TO_NUMBER);
			}
			nLeftOperand = Double.parseDouble(atomLeftOperandObj.getReturnValue().getValue());
		}
		else  {
			//Ein unbekannter Token kommt vor -> FEHLER:
			return new ReturnValue<Double>(null, ReturnValueTypes.ERROR_UNKNOWN_TOKEN);
		}
		
		
		
		//--------------- RECHTEN OPERAND HERAUSFINDEN ---------------
		if (ptAbstractSyntaxTree.getRightChild().getType().equals(TokenTypes.TOKEN_NUMBER)) {
			//Rechter Operand ist eine Zahl:
			nRightOperand = Double.parseDouble(ptAbstractSyntaxTree.getRightChild().getValue());
		}
		else if (ptAbstractSyntaxTree.getRightChild().getType().equals(TokenTypes.TOKEN_OPERATOR)) {
			//Rechter Teilbaum stellt eine neue Rechnung dar:
			ReturnValue<String> rightSubTreeReturnValueObj = new ReturnValue<String>();
			rightSubTreeReturnValueObj = interpret(ptAbstractSyntaxTree.getRightSubTree()); //REKURSION :O
			if (rightSubTreeReturnValueObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Bei der Rekursion ist ein Fehler aufgetreten:
				return new ReturnValue<Double>(null, rightSubTreeReturnValueObj.getExecutionInformation());
			}
			nRightOperand = Double.parseDouble(rightSubTreeReturnValueObj.getReturnValue());
		}
		else if (ptAbstractSyntaxTree.getRightChild().getType().equals(TokenTypes.TOKEN_STRING)){
			//Ein String kommt vor:
			//Aktuell: String kann nicht verrechnet werden -> FEHLER:
			return new ReturnValue<Double>(null, ReturnValueTypes.ERROR_CANNOT_OFFSET_STRING_TO_NUMBER);
		}
		else if (ptAbstractSyntaxTree.getRightChild().getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
			//Ein Bezeichner (einer Variablen oder Funktion) kommt vor:
			ReturnValue<Atom> atomRightOperandObj = searchAtom(ptAbstractSyntaxTree.getRightChild().getValue()); //Speichert das Atom.
			if (atomRightOperandObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es gab einen Fehler -> Rekursion beenden:
				return new ReturnValue<Double>(null, atomRightOperandObj.getExecutionInformation());
			}
			else if (atomRightOperandObj.getReturnValue().getType().equals(TokenTypes.TOKEN_STRING)) {
				//String soll zu einer Zahl umgewandelt werden -> FEHLER:
				return new ReturnValue<Double>(null, ReturnValueTypes.ERROR_CANNOT_OFFSET_STRING_TO_NUMBER);
			}
			nRightOperand = Double.parseDouble(atomRightOperandObj.getReturnValue().getValue());
		}
		else  {
			//Ein unbekannter Token kommt vor -> FEHLER:
			return new ReturnValue<Double>(null, ReturnValueTypes.ERROR_UNKNOWN_TOKEN);
		}
		
		
		
		//Zahlen verrechnen:
		if (ptAbstractSyntaxTree.getContent().getType().equals(TokenTypes.TOKEN_OPERATOR)) {
			switch (ptAbstractSyntaxTree.getContent().getValue()) {
			case "+":
				//Addition:
				ReturnValue<Double> r1 = new ReturnValue<Double>((double)(nLeftOperand + nRightOperand), ReturnValueTypes.SUCCESS);
				return r1;
			
			case "-":
				//Subtraktion:
				ReturnValue<Double> r2 = new ReturnValue<Double>((double)(nLeftOperand - nRightOperand), ReturnValueTypes.SUCCESS);
				return r2;
				
			case "*":
				//Multiplikation:
				ReturnValue<Double> r3 = new ReturnValue<Double>((double)(nLeftOperand * nRightOperand), ReturnValueTypes.SUCCESS);
				return r3;
				
			case "/":
				//Division:
				//Sonderfaelle:
				if (nRightOperand == 0) {
					//Rechter Operand 0 -> FEHLER:
					ReturnValue<Double> r4 = new ReturnValue<Double>(0.0, ReturnValueTypes.ERROR_DIVIDE_BY_ZERO);
					return r4;
				}
				ReturnValue<Double> r5 = new ReturnValue<Double>((double)(nLeftOperand / nRightOperand), ReturnValueTypes.SUCCESS);
				return r5;
				
			default:
				//Unbekannter Operator -> FEHLER:
				ReturnValue<Double> r6 = new ReturnValue<Double>(0.0, ReturnValueTypes.ERROR_UNKNOWN_OPERATOR);
				return r6;
			}
		}
		else {
			//Es gibt keinen gueltigen Operator -> FEHLER:
			ReturnValue<Double> r7 = new ReturnValue<Double>(0.0, ReturnValueTypes.ERROR_UNKNOWN_OPERATOR);
			return r7;
		}
	}
	
	
	
	/**
	 * Diese Funktion startet den rekursiven Porzes, bei welchem ein abstrakter Syntaxbaum, welcher
	 * eine boolesche Operation darstellt, ausgerechnet wird.
	 * 
	 * @param ptAbstractSyntaxTree	Abstrakter Syntaxbaum.
	 * @return						Ergebnis der boolschen Rechnung (true / false).
	 */
	private ReturnValue<Boolean> interpret_booleanComparison(BinaryTree<Token> ptAbstractSyntaxTree) {
		Token leftOperandObj; //Speichert den linken Operanden als Token.
		Token rightOperandObj; //Speichert den rechten Operanden als Token.
		
		
		
		//--------------- LINKEN OPERAND HERAUSFINDEN ---------------
		Token leftChildObj = new Token(ptAbstractSyntaxTree.getLeftChild().getValue(), ptAbstractSyntaxTree.getLeftChild().getType()); //Speichert den linken Knoten.
		if (leftChildObj.getType().equals(TokenTypes.TOKEN_BOOLEAN) || leftChildObj.getType().equals(TokenTypes.TOKEN_STRING) || leftChildObj.getType().equals(TokenTypes.TOKEN_NUMBER)) {
			//Es handelt sich um einen boolschen Wert oder einen String oder eine Nummer:
			leftOperandObj = new Token(leftChildObj.getValue(), leftChildObj.getType());
		}
		else if (leftChildObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
			//Es handelt sich um den Bezeichner eines Atoms:
			ReturnValue<Atom> returnAtomObj = new ReturnValue<Atom>(); //Speichert das gesuchte Atom.
			returnAtomObj = searchAtom(leftChildObj.getValue());
			if (returnAtomObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Boolean>(false, returnAtomObj.getExecutionInformation());
			}
			leftOperandObj = new Token(returnAtomObj.getReturnValue().getValue(), returnAtomObj.getReturnValue().getType());
		}
		else if (leftChildObj.getType().equals(TokenTypes.TOKEN_OPERATOR)) {
			//Es handelt sich um eine Subrechnung:
			ReturnValue<String> sReturnCalculationObj = new ReturnValue<String>();
			sReturnCalculationObj = interpret(ptAbstractSyntaxTree.getLeftSubTree());
			if (sReturnCalculationObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Boolean>(false, sReturnCalculationObj.getExecutionInformation());
			}
			leftOperandObj = new Token(sReturnCalculationObj.getReturnValue(),TokenTypes.TOKEN_NUMBER);
		}
		else if (leftChildObj.getType().equals(TokenTypes.TOKEN_OPERATOR_BOOLEAN)) {
			//Es handelt sich um einen Vergleich:
			ReturnValue<String> sReturnComparisonObj = new ReturnValue<String>();
			sReturnComparisonObj = interpret(ptAbstractSyntaxTree.getLeftSubTree());
			if (sReturnComparisonObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Boolean>(false, sReturnComparisonObj.getExecutionInformation());
			}
			leftOperandObj = new Token(sReturnComparisonObj.getReturnValue(),TokenTypes.TOKEN_BOOLEAN);
		}
		else {
			//Unbekannter Token:
			return new ReturnValue<Boolean>(false, ReturnValueTypes.ERROR_UNKNOWN_TOKEN);
		}
		
		
		
		//--------------- RECHTEN OPERAND HERAUSFINDEN ---------------
		Token rightChildObj = new Token(ptAbstractSyntaxTree.getRightChild().getValue(), ptAbstractSyntaxTree.getRightChild().getType()); //Speichert den linken Knoten.
		if (rightChildObj.getType().equals(TokenTypes.TOKEN_BOOLEAN) || rightChildObj.getType().equals(TokenTypes.TOKEN_STRING) || rightChildObj.getType().equals(TokenTypes.TOKEN_NUMBER)) {
			//Es handelt sich um einen boolschen Wert oder einen String oder eine Nummer:
			rightOperandObj = new Token(rightChildObj.getValue(), rightChildObj.getType());
		}
		else if (rightChildObj.getType().equals(TokenTypes.TOKEN_IDENTIFIER)) {
			//Es handelt sich um den Bezeichner eines Atoms:
			ReturnValue<Atom> returnAtomObj = new ReturnValue<Atom>(); //Speichert das gesuchte Atom.
			returnAtomObj = searchAtom(rightChildObj.getValue());
			if (returnAtomObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Boolean>(false, returnAtomObj.getExecutionInformation());
			}
			rightOperandObj = new Token(returnAtomObj.getReturnValue().getValue(), returnAtomObj.getReturnValue().getType());
		}
		else if (rightChildObj.getType().equals(TokenTypes.TOKEN_OPERATOR)) {
			//Es handelt sich um eine Subrechnung:
			ReturnValue<String> sReturnCalculationObj = new ReturnValue<String>();
			sReturnCalculationObj = interpret(ptAbstractSyntaxTree.getRightSubTree());
			if (sReturnCalculationObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Boolean>(false, sReturnCalculationObj.getExecutionInformation());
			}
			rightOperandObj = new Token(sReturnCalculationObj.getReturnValue(),TokenTypes.TOKEN_NUMBER);
		}
		else if (rightChildObj.getType().equals(TokenTypes.TOKEN_OPERATOR_BOOLEAN)) {
			//Es handelt sich um einen Vergleich:
			ReturnValue<String> sReturnComparisonObj = new ReturnValue<String>();
			sReturnComparisonObj = interpret(ptAbstractSyntaxTree.getRightSubTree());
			if (sReturnComparisonObj.getExecutionInformation() != ReturnValueTypes.SUCCESS) {
				//Es ist ein Fehler aufgetreten:
				return new ReturnValue<Boolean>(false, sReturnComparisonObj.getExecutionInformation());
			}
			rightOperandObj = new Token(sReturnComparisonObj.getReturnValue(),TokenTypes.TOKEN_BOOLEAN);
		}
		else {
			//Unbekannter Token:
			return new ReturnValue<Boolean>(false, ReturnValueTypes.ERROR_UNKNOWN_TOKEN);
		}
		
		
		
		//Werte miteinander vergleichen:
		if (!leftOperandObj.getType().equals(rightOperandObj.getType())) {
			//Operanden sind nicht vom selben Typen:
			return new ReturnValue<Boolean>(false, ReturnValueTypes.ERROR_UNEQUAL_DATA);
		}
		
		switch(ptAbstractSyntaxTree.getContent().getValue()) {
		case "=":
			//Ueberpruefen, ob die Werte identisch sind:
			if (leftOperandObj.getType().equals(TokenTypes.TOKEN_NUMBER)) {
				double nLeftOperand = Double.parseDouble(leftOperandObj.getValue()); //Speichert den linken Operanden als Zahl.
				double nRightOperand = Double.parseDouble(rightOperandObj.getValue()); //Speichert den rechten Operanden als Zahl.
				if (nLeftOperand == nRightOperand) {
					//Werte sind identisch:
					return new ReturnValue<Boolean>(true, ReturnValueTypes.SUCCESS);
				}
			}
			if (leftOperandObj.getValue().equals(rightOperandObj.getValue())) {
				//Werte sind identisch:
				return new ReturnValue<Boolean>(true, ReturnValueTypes.SUCCESS);
			}
			return new ReturnValue<Boolean>(false, ReturnValueTypes.SUCCESS);
			
		case "<":
			//Ueberpruefen, ob der linke Operand kleiner als der rechte ist:
			if (leftOperandObj.getType().equals(TokenTypes.TOKEN_NUMBER)) {
				//Es handelt sich um Zahlen:
				if (Double.parseDouble(leftOperandObj.getValue()) < Double.parseDouble(rightOperandObj.getValue())) {
					//Der linke Operand ist kleiner als der rechte:
					return new ReturnValue<Boolean>(true, ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Boolean>(false, ReturnValueTypes.SUCCESS);
			}
			else {
				//Opernaden koennen ueber diesen Operator nicht verglichen werden:
				return new ReturnValue<Boolean>(false, ReturnValueTypes.ERROR_SYNTAX);
			}
			
		case ">":
			//Ueberpruefen, ob der linke Operand groesser als der rechte ist:
			if (leftOperandObj.getType().equals(TokenTypes.TOKEN_NUMBER)) {
				//Es handelt sich um Zahlen:
				if (Double.parseDouble(leftOperandObj.getValue()) > Double.parseDouble(rightOperandObj.getValue())) {
					//Der linke Operand ist groesser als der rechte:
					return new ReturnValue<Boolean>(true, ReturnValueTypes.SUCCESS);
				}
				return new ReturnValue<Boolean>(false, ReturnValueTypes.SUCCESS);
			}
			else {
				//Opernaden koennen ueber diesen Operator nicht verglichen werden:
				return new ReturnValue<Boolean>(false, ReturnValueTypes.ERROR_SYNTAX);
			}
			
		case "!":
			//Ueberpruefen, ob die Werte nicht identisch sind:
			if (!leftOperandObj.getValue().equals(rightOperandObj.getValue())) {
				//Werte sind nicht identisch:
				return new ReturnValue<Boolean>(true, ReturnValueTypes.SUCCESS);
			}
			return new ReturnValue<Boolean>(false, ReturnValueTypes.SUCCESS);
			
		default:
			//Unbekannter Operator wurde verwendet:
			return new ReturnValue<Boolean>(false, ReturnValueTypes.ERROR_UNKNOWN_OPERATOR);
		}
	}
	
	
	
	/**
	 * Startet den Vorgang, bei welchem eine Codezeile interpretiert wird.
	 * 
	 * @param plAbstractSyntaxTree	Abstrakter Syntaxbaum, welcher interpretiert werden soll.
	 * 
	 * @return						0, wenn der Vorgang erfolgreich abgeschlossen wurde, sonst
	 * 								wird hier der Fehler uebergeben.
	 */
	public ReturnValue<String> interpret(BinaryTree<Token> ptAbstractSyntaxTree) {
		ReturnValue<String> r = new ReturnValue<String>();
		
		if (ptAbstractSyntaxTree.getContent() != null) {
			
			if (ptAbstractSyntaxTree.getContent().getType().equals(TokenTypes.TOKEN_OPERATOR)) {
				//Es handelt sich um eine Rechenoperation:
				ReturnValue<Double> r2 = interpret_calculation(ptAbstractSyntaxTree);
				r = new ReturnValue<String>(String.valueOf(r2.getReturnValue()), r2.getExecutionInformation());
			}
			else if (ptAbstractSyntaxTree.getContent().getType().equals(TokenTypes.TOKEN_OPERATOR_BOOLEAN)) {
				//Es handelt sich um einen Vergleich:
				ReturnValue<Boolean> returnBooleanComparison = interpret_booleanComparison(ptAbstractSyntaxTree);
				if (returnBooleanComparison.getReturnValue() == true) {
					//Der Wert lautet "true":
					r = new ReturnValue<String>(KeywordTypes.BOOLEAN_T, returnBooleanComparison.getExecutionInformation());
				}
				else {
					//Der Wert lautet "false":
					r = new ReturnValue<String>(KeywordTypes.BOOLEAN_F, returnBooleanComparison.getExecutionInformation());
				}
			}
			else {
				r = new ReturnValue<String>(null, ReturnValueTypes.ERROR_UNKNOWN);
			}
		}
		else {
			return new ReturnValue<String>(null, ReturnValueTypes.ERROR_UNKNOWN);
		}
		
		return r;
	}
	
}
