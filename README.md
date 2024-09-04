# yaupl

A small interpreted language written in Kotlin

## Requirements

- [Kotlin](https://kotlinlang.org/)

## Project structure

The root repertory is a gradle multi-module project

- `code-samples` : Some samples illustrating the features of the language
- `lang` (Gradle sub-project) : Class library which contains all the code responsible for lexing, parsing and interpreting the language
- `runner` (Gradle sub-project) : Application that runs yaupl programs from the source code or in REPL mode
- `jvm` (Gradle sub-project) : Application that allows compiling yaupl code to jvm bytecode (TODO)
- `virtual-machine` (C++ project) : An implementation of a compiler using a VM written in C++


## Setup

```shell
git clone https://github.com/Ola-jed/yaupl
cd yaupl
./gradlew :runner:run --console=plain --args="[file.y]"

# Or run the jar directly at the repository's root
java -jar yaupl.main.jar <options>

# The ast classes are created by the gradle task "astGen" defined in the lang's build.gradle.kts
# To regen the ast, run
./gradlew :lang:astGen
```

## [Grammar](https://github.com/Ola-jed/yaupl/blob/master/grammar.md)

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
  
  print arr; // Array [1, 1, 1]
  print literal[0]; // 1
  print literal.get(0); // 1
  print literal.length; // 3
  ```
- `List` : Dynamic lists
  ```
  let list = List();

  list.add(0);
  list.add(1);
  list.add(2);
  
  
  print list; // List[0, 1, 2]
  
  list.remove(0);
  print list; // List[1, 2]
  
  print list.reverse(); // List[2, 1]
  
  list.fill(99);
  print list; // List[99, 99]
  ```

- `String` : Strings instances created from the given string literal. Literal strings are supported so the use of the `String()`
  constructor is mainly for casting purposes
  ```
  let str = String("Hello world");
  
  // Equivalent to
  let literal = "Hello world";
  
  print(str.length()); // 11
  print(str.lowercase()); // hello world
  print(str.uppercase()); // HELLO WORLD
  ```

### Native functions

- `Clock` : Get the current timestamp in seconds

### Features
- Operations (numeric, binary)
- Output
- Functions definition (with recursion and anonymous functions)
- Classes definition
- Predefined classes (`Array`,  `List`, `String`)
- Imports