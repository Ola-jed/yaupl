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
- Native functions :
  - `Clock` : Get the current timestamp in seconds 
  - `Array` : Create a fixed size array of the given length, the supported operations are `get`, `set` and `size`  
