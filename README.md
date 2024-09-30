# yaupl

A small interpreted language written in Kotlin

## Requirements

- [Kotlin](https://kotlinlang.org/)

## Project structure

The root repertory is a gradle multi-module project

- `code-samples` : Some samples illustrating the features of the language
- `lang` (Gradle sub-project) : Class library which contains all the code responsible for lexing, parsing and
  interpreting the language
- `runner` (Gradle sub-project) : Application that runs yaupl programs from the source code or in REPL mode
- `jvm` (Gradle sub-project) : Application that allows compiling yaupl code to jvm bytecode (In progress)
- `virtual-machine` (C++ project) : An implementation of a compiler using a VM written in C++

## Setup

```shell
git clone https://github.com/Ola-jed/yaupl
cd yaupl
./gradlew :runner:run --console=plain --args="[file.y] [--help] [--print-ast-only] [--error-log=logfile.log]"

# The --print-ast-only allows to only print the generated ast in the console without running the code

# Or run the jar directly at the repository's root
java -jar yaupl.main.jar <options>

# The ast classes are created by the gradle task "astGen" defined in the lang's build.gradle.kts
# To regen the ast, run
./gradlew :lang:astGen

# Compile the file to a jar
./gradlew :jvm:run --console=plain --args="file.y [--help] [--help] [--package=com.foo.bar] [--output-jar=name] [--error-log=logfile.log]"
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
  
  
  print list; // List [0, 1, 2]
  
  list.remove(0);
  print list; // List [1, 2]
  
  print list.reverse(); // List [2, 1]
  
  list.fill(99);
  print list; // List [99, 99]
  ```

- `Set` : Collection of unique elements with efficient adding, removing, and checking for the presence of items, with no guaranteed order.
  ```
  let set = Set();
  
  set.add(0);
  set.add(1);
  set.add(2);
  set.add(2);
  
  print set; // Set {0, 1, 2} (Note that the order is not guaranteed to be the same)
  
  set.remove(0);
  print set; // Set {1, 2} (Note that the order is not guaranteed to be the same)
  
  set.clear();
  print set; // Set {}
  
  print set.intersection(Set(1, 2, 3)); // Set {}
  print set.union(Set(1, 2, 3)); // Set {1, 2, 3} (Note that the order is not guaranteed to be the same)
  print set.difference(Set(1, 2, 3)); // Set {}
  print Set(1, 2, 3).difference(set); // Set {1, 2, 3} (Note that the order is not guaranteed to be the same)
  ```

- `File` : Represents files or directories in the filesystem
  ```
  let file = File("test.ypl");
  
  file.create();
  file.write("print x");
  print file.exists(); // true
  print file.read(); // print x
  print file.readLines(); // List [print x]
  ```

- `String` : Strings instances created from the given string literal. Literal strings are supported so the use of the
  `String()`
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
- Predefined classes (`Array`, `List`, `Set`, `String`, `File`)
- Imports
- Compilation to jvm bytecode (In progress)