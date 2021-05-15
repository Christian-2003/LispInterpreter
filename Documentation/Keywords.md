# Keywords:
With this Lisp dialect, a keyword must be placed at the first position of (most) of the expressions in Lisp. In some situations (such as boolean or arithmetic operations), a keyword must not take the first position in a list.

### Example:
```Lisp
(defun main () (
    (princ "Hello World.")
;    ^^^^^ <- This is the keyword.
))
```

<br/>
<br/>
<br/>

# Available keywords:
Currently, there are multiple keywords available with this Lisp dialect.

<br/>

### Keywords regarding variables:
- [`var`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#var)
<br/>
This keyword is used to declare a new variable.

- [`setf`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#setf)
<br/>
This keyword is used to change the value of any variable.

<br/>

### Keywords for in- and output:
- [`princ`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#princ-princln)
<br/>
This keyword is used to output anything in the terminal.

- [`princln`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#princ-princln)
<br/>
This keyword is used to output anything in the terminal and adds a line break afterwards.

- [`scan`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#scan)
<br/>
This keyword is used to scan an input from the user.

<br/>

### Keywords for control structures:
- [`if`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#if)
<br/>
This keyword indicates an if-statement.

- [`while`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#while)
<br/>
This keyword indicates a while-loop.

<br/>

### Keywords for functions:
- [`defun`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#defun)
<br/>
This keyword is used to define a new function.

- [`return`](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Keywords.md#return)
<br/>
This keyword is used to exit a function and return a value.

<br/>
<br/>
<br/>

## `var`:
### Remarks:
The keyword `var` is used to **declare a new variable**.
<br/>
**IMPORTANT:** Variables in Lisp do not have any datatype, so that you can store numbers, booleans, strings, etc. in them.

<br/>

### Further elements in this list:
_variable name_
<br/>
Represents the name of the declared variable. The variables name can be any word, as long as it does not represent any keyword, parenthesis, number, etc.

_variable value **(optional)**_
<br/>
Represents the value of the declared variable. If no value is given, the variable will be initialized with a value of "0.00".

<br/>

### Example:
```Lisp
(defun main () (
    ;Declare a new variable with no specified value:
    (var myVarWithoutValue)

    ;Declare a new variable with the value of 5:
    (var myVarWithValue 5)
))
```

<br/>
<br/>
<br/>

## `setf`:
### Remarks:
The keyword `setf` is used to **change a variable's value**.

<br/>

### Further elements in this list:
_variable name_
<br/>
Represents the name of the variable, whose value should be changed.

_variable value_
<br/>
Represents the new value, to which the variable should be changed to.

<br/>

### Example:
```Lisp
(defun main () (
    (var myVar) ;Declare a new variable.

    (setf myVar 15) ;Change variable's value to 15.
    (setf myVar "Hello World") ;Change variable's value to "Hello World"
    (setf myVar sqrt(25)) ;Change variable's value to the return value of the "sqrt(25)"-function
    (setf myVar (+ 7 13)) ;Change variable's value to the result of the arithmetic operation 7 + 13.
))
```

<br/>
<br/>
<br/>

## `princ`, `princln`:
### Remarks:
The keywords `princ` and `princln` are used to **output anything to the terminal**. `princ` simply prints any element into the console, while `princln` does the same and adds a line break afterwards.
<br/>
**IMPORTANT:** `princ` and `princln` are capable of processing control characters, such as `\n`, `\t`, etc. A list with available control characters can be found [here](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Control-characters.md).

<br/>

### Further elements in this list:
_output_
<br/>
This element is printed into the terminal.

<br/>

```Lisp
(defun main () (
    (princ "Hello World") ;Print the String "Hello World".
    (princ 15) ;Print the value 15.
    (princ (* 2 14)) ;Print the result of the arithmetic operation 2 * 17.
    (princ sqrt(9)) ;Print the return value of the "sqrt(9)"-function.

    ;Princln simply adds a line break afterwards:
    (princln "Hello World") ;Print the String "Hello World".
    (princln 15) ;Print the value 15.
    (princln (* 2 14)) ;Print the result of the arithmetic operation 2 * 17.
    (princln sqrt(9)) ;Print the return value of the "sqrt(9)"-function.
))
```

<br/>
<br/>
<br/>

## `scan`:
### Remarkds:
The keyword `scan` is used for **input actions**. With this, the user can input anything. The scanned user's input is then stored into a variable.
<br/>
After this keyword is encountered in the source code by the Interpreter, the user's input is scanned until the _ENTER_-button is pressed.

<br/>

### Further elements in this list:
_variable name_
<br/>
Represents the variable, in which the user's input should be stored.

<br/>

```Lisp
(defun main () (
    (var userInput) ;Declare a new variable, which is supposed to store the user's input.
    
    (scan userInput) ;Scan the user's input and store the result in the userInput-variable.
))
```

<br/>
<br/>
<br/>

## `if`:
### Remarks:
The keyword `if` indicates an **if-statement**. Such an if-statement executes the expressions in it's body, if the condition is considered to be true. Further information on conditions and boolean operations can be found [here](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Boolean-operations.md).
<br/>
If statements can be encapsulated.

<br/>

### Further elements in this list:
_condition_
<br/>
This list contains a boolean comparison, which represents the condition of the statement.

_body_
<br/>
This list contains all the expressions that are executed, if the condition is true.

_else **(optional)**_
<br/>
This list contains all the expressions that are executed, if the condition is false.

<br/>

### Example:
```Lisp
(defun main () (
    ;If-statement without optional else-branch:
    (if (= 5 5) (
    ;   ^^^^^^^ <- This represents the condition of the statment.
        (princ "5 is equal to 5.") ;Expression is executed if condition is true.
    ))

    ;If statement with optional else-branch:
    (if (> 3 5) (
        (princ "3 is greater than 5.") ;Expression is executed if condition is true.
    ) (
        (princ "3 is not greater than 5.") ;Expression is executed if condition is false.
    ))
))
```

<br/>
<br/>
<br/>

## `while`:
### Remarks:
The keyword `while` indicates an **while-loop**. Such a while-loop checks wether the condition is true, and executes all the expressions in it's body. This cycle is repeated, until the condition is no longer true.

<br/>

### Furter elements in this list:
_condition_
<br/>
This list contains a boolean comparison, which represents the condition of the loop.

_body_
<br/>
This list contains all the expressions that are executed for as long as the condition is true.

<br/>

### Example:
```Lisp
(defun main () (
    (var I 0)

    (while (< i 15) (
        ;  ^^^^^^^^ <- This represents the condition.

        ;These expressions are executed, for as long as the value of "i" is less than 15:
        (princ "iteration: ")
        (princ i)
        (setf i (+ i 1))
    ))
))
```

<br/>
<br/>
<br/>

## `defun`:
### Remarks:
The keyword `defun` is used to **define a new function**.
<br/>
Further informaion on functions can be found [here](https://github.com/ChosenChris/LispInterpreter/tree/main/Documentation/Functions.md).

<br/>

### Further elements in this list:
_function name_
<br/>
This represents the function's name.

_function parameters_
<br/>
This list contains all the parameters of the function.

_function body_
<br/>
This list contains all the expressions, that are executed whenever the function is called.

<br/>

### Example:
```Lisp
;main-function represents the starting point of the program:
(defun main () (
    (printSum(5 9)) ;Executes the "printSum()"-function.
))

;The "printSum()"-function exhibits the two parameters "a" and "b" and prints their sum:
(defun printSum (a b) (
    ;Expressions are executed whenever this function is called:
    (princ (+ a b))
))
```

<br/>
<br/>
<br/>

## `return`:
### Remarks:
The keyword `return` is used to exit a function. This keyword can be used as well to return any value from the currently executed function.

<br/>

### Further elements in this list:
_return value **(optional)**_
<br/>
This element is returned by the function.

<br/>

### Example:
```Lisp
(defun main () (
    (princ mul(2 7)) ;Prints the return value of the "mul()"-function.
))

(defun mul (a b) (
    (returns (* a b)) ;Returns the result of the arithmetic operation a * b.
))
```

<br/>
