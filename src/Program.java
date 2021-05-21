import java.util.Scanner;

import errorHandling.ReturnValueTypes;
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
				int nReturnValue = controllerObj.startController();
				System.out.println("\n");
				if (nReturnValue != ReturnValueTypes.SUCCESS) {
					printErrorMessage("Error: ", nReturnValue, "\n");
				}
			}
			
			else {
				//Unbekannter Befehl:
				System.out.println("Error: The command \"" + sInput + "\" is incorrect.\n");
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
	private static void printErrorMessage(String psPrefix, int pnErrorMessage, String psSuffix) {
		System.out.print(psPrefix); //Prefix ausgeben.
		
		//Fehlermeldung ausgeben:
		if (pnErrorMessage == ReturnValueTypes.SUCCESS) {
			System.out.print("Success");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN) {
			System.out.print("Unknown");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_DIVIDE_BY_ZERO) {
			System.out.print("Cannot divide by zero");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNEQUAL_DATA) {
			System.out.print("Operands are of different type");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_SYNTAX) {
			System.out.print("Syntax");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_OPERATOR) {
			System.out.print("Unknown operator");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_IDENTIFIER) {
			System.out.print("Unknown identifier");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_NOT_ENOUGH_OPERANDS) {
			System.out.print("The operation needs more operands");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_CANNOT_OFFSET_STRING_TO_NUMBER) {
			System.out.print("Cannot offset String to number");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_TOKEN) {
			System.out.print("Encountered unknown token.");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_KEYWORD) {
			System.out.print("Encountered onknown keyword");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_VARIABLE_NAME_DOES_EXIST) {
			System.out.print("Variable name does already exist");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_FILE_DOES_NOT_EXIST) {
			System.out.print("File does not exist");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_FILE_CANNOT_BE_READ) {
			System.out.print("Cannot read file");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_NO_MAIN_FUNCTION) {
			System.out.print("Main function is missing");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_MAIN_FUNCTION_HAS_PARAMETER) {
			System.out.print("Main function has too many parameters");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_INCORRECT_PARAMETER_NUMBER) {
			System.out.print("Function call has an incorrect number of arguments");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_NO_RETURN_VALUE) {
			System.out.print("Non-existing return value was expected");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_INSTANCE_NAME_DOES_EXIST) {
			System.out.print("The name of an instance is already used");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_INSTANCE_NAME_CANNOT_BE_CLASS_NAME) {
			System.out.print("Instance name cannot be class name");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_UNKNOWN_CLASS) {
			System.out.print("Unknown class type");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_STACK_OVERFLOW) {
			System.out.print("StackOverflow");
		}
		else if (pnErrorMessage == ReturnValueTypes.ERROR_FUNCTION_NAME_IS_IDENTICAL) {
			System.out.println("Multiple functions are defined with the same name");
		}
		else {
			System.out.print("unknwon error occured. Error message: " + pnErrorMessage);
		}
		
		System.out.println(psSuffix); //Suffix ausgeben.
	}
}
