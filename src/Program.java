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
		System.out.println("\n\nChosenChris' LispInterpreter [Version 4.0]\n");
		
		//Unendlichschleife laeuft solange, bis der user durch die Eingabe von "exit" den Interpreter beendet:
		while (true) {
			System.out.print("LispInterpreter>");
			Scanner scannerObj = new Scanner(System.in);
			String sInput = scannerObj.nextLine(); //Speichert die Eingabe des Benutzers.
			String sLowerCaseInput = sInput.toLowerCase(); //Speichert die Eingabe ohne Grossbuchstaben.
			//Scanner nicht schliessen, da es sonst zu einem Fehler kommt: java.util.nosuchelementexception
			
			//Input verarbeiten:
			if (sLowerCaseInput.equals("exit")) {
				//Programm soll beendet werden:
				System.out.println("The execution of the program is terminated.");
				return;
			}
			
			else if (sLowerCaseInput.equals("help")) {
				//Es sollen alle moeglichen Befehle ausgegeben werden:
				System.out.println("HELP            Displays this overview of all possible inputs.");
				System.out.println("EXIT            Terminates the execution of this application.");
				System.out.println("CODE <file>     Loads and executes the Lisp source code from the respective file.");
				System.out.println();
			}
			
			else if (sLowerCaseInput.length() >= 4 && sLowerCaseInput.substring(0, 4).equals("code")) {
				//Es soll Quellcode geladen werden:
				if (sInput.length() == 4) {
					//Es ist kein Dateiname vorhanden:
					System.out.println("Error: No file name found.\n");
					continue;
				}
				if (sInput.indexOf(' ') == -1) {
					//Es existiert keine Trennung zwischen "code" und dem Dateinamen:
					System.out.println("Error: Cannot differentiate between command and file name.\n");
					continue;
				}
				String sFilename = sInput.substring(sInput.indexOf(' ') + 1, sInput.length());
				//Quellcode ueber den Controller ausfueheren:
				Controller controllerObj = new Controller(sFilename);
				controllerObj.startController();
				System.out.println("\n");
			}
			
			else {
				//Unbekannter Befehl:
				System.out.println("Error: The command \"" + sInput + "\" is incorrect.\n");
			}
		}
	}
}
