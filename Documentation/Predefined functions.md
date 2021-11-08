# Predefined functions:
Predefined functions are functions, which are encoded into the source code of the Interpreter. Such functions can be used in any program since they do not need to get implemented by the developer.

<br/>
<br/>
<br/>

# List of predefined functions:

### Mathematical functions:
[`sin`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#sin)
<br/>
Returns the sine value of a specified position.

[`cos`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#cos)
<br/>
Returns the cosine value of a specified position.

[`tan`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#tan)
<br/>
Returns the tangency value of a specified position.

[`sqrt`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#sqrt)
<br/>
Returns the square root of the specified number.

<br/>

### String functions:
[`length`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#length)
<br/>
Returns the length of the specified string.

[`substr`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#substr)
<br/>
Returns a substring.

[`charAt`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#charAt)
<br/>
Returns the character at the specified position of the string.

<br/>

### Not specified:
[`isNumber`](https://github.com/Christian-2003/LispInterpreter/blob/main/Documentation/Predefined%20functions.md#isNumber)
<br/>
Returns, wether the specified input represents a number.

<br/>
<br/>
<br/>

## `sin`:
### Remarks:
This function returns the sine value of the specified position x.
If an incorret argument is passed (e.g. a String), the value _0.00_ is returned.

<br/>

### Parameters:
_x_
<br/>
Represents the x-position of which the sine value should be dertermined.

<br/>

### Return value:
Sine value of the specified position.

<br/>

### Example:
```Lisp
(defun main () (
    (princ sin(1.2))
))
```

<br/>
<br/>
<br/>

## `cos`:
### Remarks:
This function returns the cosine value of the specified position x. If an incorrect argument is passed (e.g. a String), the value _0.00_ is returned.

<br/>

### Parameters:
_x_
<br/>
Represents the x-position of which the cosine value should be determined.

<br/>

### Return value:
Cosine value of the specified position.

<br/>

### Example:
```Lisp
(defun main () (
    (princ cos(0.5))
))
```

<br/>
<br/>
<br/>

## `tan`:
### Remarks:
This function returns the tangency value of the specified position x. If an incorrect argument os passed (e.g. a String), the value _0.00_ is returned.

<br/>

### Parameters:
_x_
<br/>
Represents the x-position of which the tangency value should be determined.

<br/>

### Return value:
Tangency value of the specified position.

<br/>

### Example:
```Lisp
(defun main () (
    (princ tan(1))
))
```

<br/>
<br/>
<br/>

## `sqrt`:
### Remarks:
This function returns the square root of the passed value. If an incorrect argument is passed (e.g. a String), the value _0.00_ is returned.

<br/>

### Parameters:
_radical_
<br/>
Represents the value, whoose square root should be determined.

<br/>

### Return value:
The square root of the passed radical.

<br/>

### Example:
```Lisp
(defun main () (
    (princ sqrt(25))
))
```

<br/>
<br/>
<br/>

## `length`:
### Remarks:
This function returns the length (in characters) of the passed String. This works as well with numbers or Booleans.

<br/>

### Parameters:
_string_
<br/>
Represents the string which length should be determined.

<br/>

### Return value:
The length of the passed String.

<br/>

### Example:
```Lisp
(defun main () (
    (princ length("Hello World"))
))
```

<br/>
<br/>
<br/>

## `substr`:
### Remarks:
Returns the substring between two indices of a passed String. If incorrect indices (e.g. to small or large) are passed, the entire String is returned.

<br/>

### Parameters:
_string_
<br/>
String, from which the substring should be created.

_begin_
<br/>
Index of the first character to be part of the substring.

_end_
<br/>
Index of the last character to be part of the substring.

<br/>

### Return value:
The String's substring of between the two indices.

<br/>

### Example:
```Lisp
(defun main () (
    (princ substr("Hello World" 2 7))
    ;                ^^^^^^^ <- These are part of the substring.
))
```

<br/>
<br/>
<br/>

## `charAt`:
### Remarks:
This function returns the character at the specified position of the specified String.

<br/>

### Parameters:
_string_
<br/>
String, in which the character should be determined.

_pos_
<br/>
Position, at which the the character should be determined.

<br/>

### Return value:
Character at the specified position in the specified String.

<br/>

### Example:
```Lisp
(defun main () (
    (princ charAt("Hello World" 3))
    ;                 ^ <- This is the returned character.
))
```

<br/>

## `isNumber`:
### Remarks:
This function returns, wether the passed argument resembles a number.

<br/>

### Parameters:
_input_
<br/>
Argument, that should be checked.

<br/>

### Return value:
Wether the passed argument resembles a String or not.

<br/>

### Example:
```Lisp
(defun main () (
    (if (= isNumber("4.56") T) (
        (princ "4.56 is a number.")
    ))
))
```

<br/>
