# LispInterpreter
An interpreter for somewhat adapted Lisp sourcecode. I have worked on this program for a school project.

This interpreter can process simple source code, which is written in the programming language Lisp.

Lisp source code consists of multiple lists in a single file, which are sourrounded by parenthesis:
```lisp
(var A)       ;;Declare a new variable with the name A.
(setf A 2.5)  ;;Set the value of A to 2.5
(print A)     ;;Print the value of A
```

## Keyords
Keywords always take the first position in any list.
There are a variety of keywords, that are currently usable with my interpreter:

### The Keyword "var":
This keyword is used to declare a new variable. The name of the variable is always the next element, that is in the list.
After the declaration, the variable has the value "0.00".
```lisp
(var A)       ;;Declare a new variable with the name A.
```

### The Keyword "setf":
This keyword is used to set (or change) the value of a variable to the next element in the list.
The next element can be:
- another variable
- a number (e.g. double)
- a string
- a boolean (T or NIL)
- a calculation
```lisp
(setf A (* 2 5))  ;;Set the value of A to the result of the term 2 * 5.
```

### The keywords "print" and "println":
These keywords print the next element in the list. "print" just prints the element while "println" adds a new line afterwards.
The next element can be:
- another variable
- a number (e.g. double)
- a string
- a boolean (T or NIL)
- a calculation
```lisp
(print A)   ;;Print the value of A.
(println A) ;;Print the value of A, and add a new line.
```

### The keyword "scan":
This keyword scans the users input and stores it in the variable that is in the next space in the list.
```lisp
(scan A)  ;;Scans the user's input and stores it in variable A.
```

### The keyword "if":
This keyword represents an if-statement. The second element in the list is a boolean comparison (more on that later). The third element
in the list is a list of expressions which are executed if the boolean comparison is true. If-statements can be encapsulated.
```lisp
(if (< 1 5) ( ;;Checks if the value 1 is less than 5.
  (print "1 is smaller than 5!") ;;Statement is executed if 1 is less than 5.
))
```

### The keyword "while":
This keyword represents an while-loop. The second element in the list is a boolean comparison. The third element in the list is a list of
expressions which are repeatedly executed if the boolean comparison is true. While-statements can be encapsulated.
```lisp
(while (< A 5) (      ;;While-loop runs as long as the value of A is less than 5. 
  (setf A (+ A 1))
  (print "Iteration = ")
  (println A)
))
```

## Boolean comparison
A boolean comparison compares two (ALWAYS TWO) values, through one operator. Such operators are used in the condition of if-statements and while-loops.
There are multiple operators that are currently useable in my interpreter.

### The operator "=":
This operator checks if the two values are identical. If this is the case, the boolean "T" (which is the same as true) is returned. Otherwise
the boolean "NIL" (which is the same as false) is returned.

### The operator "!":
This operator checks if the two values are NOT identical.

### The operator "<":
This operator checks if the first value is less than the second value. This operator only works with numeric values.

### The operator ">":
This operator checks if the first value is more than the second value. This operator only works with numeric values.
