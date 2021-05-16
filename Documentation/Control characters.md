# Escape sequences:
Escape sequences are characters that can be used for different purposes in output operations. Such sequencees can be used in any String, but are only displayed when these Strings are displayed in the terminal.

Escape sequences always begin with a `\`.

<br/>
<br/>
<br/>

# List of available escape sequences:

## `\n`:
This character creates a line break in the console.

### Example:
```Lisp
(defun main () (
    (princ "Hello\nWorld")
))
```
### Output:
```
Hello
World
```

<br/>

## `\t`:
This character creates a tabulator in the terminal.

### Example:
```Lisp
(defun main () (
    (princ "Hello\t World")
))
```

### Output
```
Hello    World
```

<br/>

## `\"`
This character creates a quotation mark inside a String.

### Example:
```Lisp
(defun main () (
    (princ "Hello \"World\"")
))
```

### Output:
```
Hello "World"
```

<br/>

## `\'`:
This character creates a single quotation mark inside a String.

### Example:
```Lisp
(defun main () (
    (princ "Hello \'World\'")
))
```

### Output:
```
Hello 'World'
```

<br/>

## `\b`:
This character creates a backspace.

### Example:
```Lisp
(defun main () (
    (princ "Hello Wo\brld")
))
```

### Output:
```
Hello Wrld
```

<br/>

## `\f`:
This character represents a form feed.

<br/>

## `r`:
This character represents a carriage return.

<br/>
