# Boolean comparison:
A boolean comparison **compares always two values** and returns a boolean as a result. Depending on the boolean operator, different types of values (e.g. numbers, Strings, booleans) can be compared.

<br/>
<br/>
<br/>

# Booleans:
As in (most) other programming languages, there are two booleans: _true_ and _false_.
<br/>
The boolean _true_ is in this Lisp Interpreter represented through the value `T`, while the boolean _false_ is represented through the value `NIL`.

<br/>

If a condition is true, the value `T` is returned. On the other hand, if a condition is considered to be false, the value `NIL` will be returned. Such conditions can be found in [if-statements](https://github.com/ChosenChris/LispInterpreter/blob/main/Documentation/Keywords.md#if) or [while-loops](https://github.com/ChosenChris/LispInterpreter/blob/main/Documentation/Keywords.md#while).

<br/>

### Example:
```Lisp
(defun main () (
    (var A 15) ;Declare a new variable with the value 15.

    ;If-statement checks, wether the value of A is equal to 15:
    (if (= A 15) (
    ;   ^^^^^^^^ <- This is a condition, which returns the boolean "T" if A is equal to 15,
    ;               and "NIL" if A is not equal to 15.
        (princ "A is equal to 15.")
    ))
))
```
Since the value _A_ is equal to 15, the expression in the if-statement's body is executed. If you change the value of _A_, the condition won't be true anymore, so that the statements in the body won't be executed.

<br/>
<br/>
<br/>

# Boolean operators:
Currently, there are six operators, which can be used with this Lisp Interpreter.

<br/>

## The operator `=`:
The operator `=` returns _true_ if the two next values will be **identical**. If the two next values are not identical, _false_ is returned.

### Possible operands:
- Numbers
- Strings
- Booleans

### Example:
```Lisp
(defun main () (
    ;Condition checks, wether the value of 3 is equal to the value of 3:
    (if (= 3 3) (
        (princ "The condition is true.")
    ))
))
```

<br/>

## The operator `!`:
The operator `!` returns _true_ if the two next values **won't be identical**. If the next two values are identical, _false_ is returned.

### Possible operands:
- Numbers
- Strings
- Booleans

### Example:
```Lisp
(defun main () (
    ;Condition checks, wether the value of 5 is not equal to 3:
    (if (! 5 3) (
        (princ "The condition is true.")
    ))
))
```

<br/>

## The operator `<`:
The operator `<` returns _true_ if the first of the next values is less than the second of the next values. If the first value is equal to, or greater than the second value, _false_ will be returned.

### Possible operands:
- Numbers

### Example:
```Lisp
(defun main () (
    ;Condition checks, wether the value of 3 is less than 7:
    (if (< 3 7) (
        (princ "The condition is true.")
    ))
))
```

<br/>

## The operator `>`:
The operator `>` returns _true_ if the first of the next values is greater than the second of the next values. If the first value is equal to, or less than the second value, _false_ will be returned.

### Possible operands:
- Numbers

### Example:
```Lisp
(defun main () (
    ;Condition checks, wether the value 5 is greater than 3:
    (if (> 5 3) (
        (princ "The condition is true.")
    ))
))
```

<br/>
<br/>
<br/>

## The operator `<=`:
The operator `<=` returns _true_ if the first of the next two values is less than, or equal to the second of the next values. If the first value is greater than the second value, _false_ will be returned.

### Possible operands:
- Numbers


### Example:
```Lisp
(defun main () (
    ;Condition checks, wether the value 1 is equal to, or less than the value 3:
    (if (<= 1 3) (
        (princ "The condition is true.")
    ))
))
```

<br/>

## The operator `>=`:
The operator `>=` returns _true_ if the first of the next two values is greater than, or equal to the second of the second of the next two values. If the first value is less than the second value, _false_ will be returned.

### Possible operands:
- Numbers

### Example:
```Lisp
(defun main () (
    ;Condition checks, wether the value of 5 is greater than, or equal to the value of 3:
    (if (>= 5 3) (
        (princ "The condition is true.")
    ))
))
```

<br/>
