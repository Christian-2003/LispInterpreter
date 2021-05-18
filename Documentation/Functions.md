# Functions:
In Lisp, every piece of source code is written into functions. The first function that is executed by the Interpreter is the **main**-function. From there on, you can execute any other function or source code inside said function.

<br/>
<br/>
<br/>

# Syntax:
As always with Lisp, everything is written into lists.

Functions can never be defined inside other functions.

<br/>
<br/>
<br/>

## Function definition:
A function definition consists of exactly four elements: The keyword `defun`, the function's name, a list of parameters and a list of expressions that are executed whenever a function is called.

<br/>

### The keyword:
The keyword `defun` takes the first place in a function definition. It indicates that a new function is defined. For further information, look up the [defun](https://github.com/ChosenChris/LispInterpreter/blob/main/Documentation/Keywords.md#defun) keyword.

<br/>

### The function's name:
The functions name can be any String, that does not start with a number, boolean, does not contain any parenthesis, etc. Through this name, the function can be called at any other point in the Lisp source code.

<br/>

### A list of parameters:
The next element represents a list of parameters. Everytime a function is called, an equal number of arguments must be passed onto the function. Since parameters are handled like any other variable in this Lisp dialect, you need to remember that these parameters do not have any specified data type.

<br/>

### A list of expressions:
The last element of the function definition represents a list with all the expressions, that are executed whenever the function is called. For further information on possible expressions, look up the [list of available keywords](https://github.com/ChosenChris/LispInterpreter/blob/main/Documentation/Keywords.md).

<br/>

### Example:
```Lisp
;The function "printSum()" has the two parameters a and b:
(defun printSum (a b) (
    ;These expressions are executed whenever the function is called.
    (princ a)
    (princ " + ")
    (princ b)
    (princ " = ")
    (princ (+ a b))
))
```

<br/>
<br/>
<br/>

## Return values:
Whenever the interpreter encounters the [`return`](https://github.com/ChosenChris/LispInterpreter/blob/main/Documentation/Keywords.md#return)-keyword, the function's execution is terminated.

This keyword can be used to return any value (e.g. a number, a String, a Boolean) can be returned by the function and used within another point of the source code. If you do not specify any value to be returned, the value _0.00_ is simply returned.

<br/>

### Example:
```Lisp
(defun sum (a b) (
    (return (+ a b)) ;Returns the result of the calculation a + b.
))
```

<br/>
<br/>
<br/>

## Function call:
Functions can be called from any other function. For that, you have to use the function's name. Right after this name, you have to put the arguments you want to pass onto the function inside parenthesis. If the called function does not have any parameters, you need to add empty parenthesis.

<br/>

### Example:
```Lisp
;The main-function represents the starting point of the program:
(defun main () (
    (princln "before function call")
    (printSum(5 8)) ;Executes the "printSum"-function with the arguments 5 and 8.
    (princln "\nafter function call")
))

;The function "printSum()" has the two parameters a and b:
(defun printSum (a b) (
    ;These expressions are executed whenever the function is called.
    (princ a)
    (princ " + ")
    (princ b)
    (princ " = ")
    (princ (+ a b))
))
```

### Output:
```
before function call
5 + 8 = 13
after function call
```

<br/>

### Example with return value:
```Lisp
;The main-function represents the starting point of the program:
(defun main () (
    (princ "sum = ")
    (princ sum(5 8)) ;Executes the "sum"-function with the arguments 5 and 8
                     ;and prints it's return value.
))

;The function "sum()" has the two parameters a and b:
(defun sum (a b) (
    (return (+ a b))
))
```

### Output:
```
sum = 13
```

<br/>
<br/>
<br/>

## The `main`-function:
The main function represents the starting point of every program. Unlike other functions, the `main`-function never has any parameters or return value!

If you return a value from this function, the returned value is ignored.

<br/>

### Example:
```Lisp
(defun main () (
    (princ "This is the main-function.")
))
```

<br/>
<br/>
<br/>

## Recursion:
The Interpreter supports recursion with all it's beauty. I won't go into any further details about recursion, since it works as in most other programming languages.

<br/>

### Example:
```Lisp
;This recursive algorithm calculates the first 15 fibonacci numbers:
(defun main () (
    (var I 0)
	(while (< I 15) (
        (var RESULT fib(i))
        (princ "fib(")
        (princ I)
        (princ ") = ")
        (princ RESULT)
    ))
))

(defun fib (n) (
	(if (< n 2) (
		(if (= n 0) (
			(return 0)
		))
		(if (= n 1) (
			(return 1)
		))
	))
	(if (> n 1) (
		(return (+ fib((- n 2)) fib((- n 1))))
	))
))
```

<br/>
<br/>
<br/>

## Predefined functions:
There are multiple predefined functions that can be used with the Lisp interpreter.

The documentation of these functions can be found [here](https://github.com/ChosenChris/LispInterpreter/blob/main/Documentation/Predefined%20functions.md).

<br/>
