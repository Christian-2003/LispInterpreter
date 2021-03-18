import java.util.Scanner;

import interpreter.*;



/**
 * Diese Klasse stellt den Startpunkt des Interpreters dar.
 * 
 * @author	Christian S
 * @version	13.02.2021
 */
public class Program {
	/**
	 * Diese Methode muss ausgefuehrt werden, um den Interpreter zu starten. Sie fordert
	 * den Benutzer dazu auf einen Dateinamen einzugeben, in welcher der Lisp-Quellcode
	 * gespeichert ist. Danach wird der Quellcode ausgefuehrt.
	 */
	public static void main(String[] args) {
		System.out.println("Interpreter for Lisp by Christian S\n");
		System.out.println("Please enter a filename in which the source code is located.");
		System.out.print("> ");
		
		//Eingane des Dateinamen durch den Benutzer:
		Scanner scannerObj = new Scanner(System.in);
		String sFileName = scannerObj.next(); //Speichert den Dateinamen.
		//Scanner nicht schliessen, da es sonst zu einem Fehler kommt: java.util.nosuchelementexception
		//scannerObj.close();
		
		//Prozess starten, bei welchem der Lisp-Quellcode ausgefuehrt wird:
		Controller controllerObj = new Controller(sFileName);
		controllerObj.startController();
	}
}
