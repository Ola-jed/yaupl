# yaupl

A small interpreted language written in Kotlin

## Requirements

- [Kotlin](https://kotlinlang.org/)

## Run

```shell
git clone https://github.com/Ola-jed/yaupl
cd yaupl
./gradlew run --console=plain [file.y]
# Or run the jar directly at the repository's root
java -jar yaupl.main.jar <options>
```

## Language description

- [Grammar](https://github.com/Ola-jed/yaupl/blob/master/grammar.md)

### Native functions

- `Clock` : Get the current timestamp in seconds

### Native classes

- `Array` : Fixed size array of the given length, the supported operations are `get`, `set` and `length` (property).
  Array literals are also supported using the construct `[]`
  ```
  let arr = Array(3);
  arr.set(0, 1);
  arr.set(1, 1);
  arr.set(2, 1);
  
  // Equivalent to
  let literal = [1, 1, 1];
  
  print arr; // Will show "Array [1, 1, 1]"
  print literal[0]; // Will show "1"
  print literal.get(0); // Will show "1"
  print literal.length; // Will show "3"
  ```


- `String` : Strings instances created from the given string literal. Literal strings are supported so the use of the `String()`
  constructor is mainly for casting purposes
  ```
  let str = String("Hello world");
  
  // Equivalent to
  let literal = "Hello world";
  
  print(str.length()); // Will show "11"
  print(str.lowercase()); // Will show "hello world"
  print(str.uppercase()); // Will show "HELLO WORLD"
  ```