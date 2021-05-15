package interpreter;

import java.util.LinkedList;

import tokenizer.*;



/**
 * Objekte dieser Klasse stellen eine Instanz einer in Lisp dar.
 * 
 * @author	Christian S
 * @version	17.03.2021
 */
public class ClassInstance extends Class {
	/**
	 * Konstruktor erzeugt eine neue Instanz der als Parameter angegebenen Lisp-Klasse.
	 * 
	 * @param pClass	Klasse, von welcher eine Instanz erzeugt werden soll.
	 */
	public ClassInstance(Class pClass) {
		super(pClass.getClassTokens());
	}
	
}
