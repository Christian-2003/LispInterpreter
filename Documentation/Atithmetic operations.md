# Arithmetic operations:
An arithmetic operation such as `7 + 15` is translated into this Lisp dialect to `(+ 7 5)`. This list contains the atoms _+-, _7_ and _5_. The atom _+_ represents the operator, through which the two operands _7_ and _5_, in this case, are added together. Arithmetic operations do work with as many operands as you like.
<br/>
You can add another operand, such as `(+ 7 5 -3)` (translates to `7 + 5 + (-3)`). This can be continued for as long as you want to.

Bear in mind that arithmetic operations only work with numeric operands, such as numbers, but not Strings or booleans!

If you want to encapsulate arithmetic operations, or use different operators for different operations, just add another list inside the operation: `(+ 7 5 (* 2 10) -7)`, which translates to `7 + 5 + (2 * 10) + (-7)`, or rather `7 + 5 + 2 * 10 + (-7)`, since the multiplication is solved before the addition.

<br/>

The result of such arithmetic operations can be used in the source code for different purposes. Here are a few examples, in which arithmetic operations are used:
```Lisp
(defun main () (
    ;Print the result:
    (princ (+ 7 5 (- 9 2)))

    ;Bind the result to a variable:
    (var sum (+ 7 5))

    ;Use in a condition:
    (if (> (- 8 2) 2) (
        (princ "The result of 8 - 2 is greater than 3.")
    ))

    ;...
))
```

<br/>
<br/>
<br/>

# Available arithmetic operators:
Currently, there are a total of four operators, which can be used in arithmetic operations:

<br/>

## The operator `+`:
The operator `+` is used to **add** multiple values.

### Example:
```Lisp
(defun main () (
    (princ (+ 2 5 7)) ;Translates to: 2 + 5 + 7
))
```

<br/>

## The operator `-`:
The operator `-` is used to **subtract** multiple values.

### Example:
```Lisp
(defun main () (
    (princ (- 26 7 2)) ;Translates to: 26 - 7 - 2
))
```

<br/>

## The operator `*`:
The operator `*` is used to multiply multiple values.

### Example:
```Lisp
(defun main () (
    (princ (* 8 6 1 2)) ;Translates to: 8 * 6 * 1 * 2
))
```

<br/>

## The operator `/`:
The operator `/` is used to divide multiple values. Remember that there is no known division through the value of 0.

### Example:
```Lisp
(defun main () (
    (princ (/ 10 2)) ;Translates to: 10 / 2
))
```

<br/>
