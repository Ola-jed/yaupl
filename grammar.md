# Grammar of the yaupl language (EBNF Notation)

```
<program> ::= {<declaration>} EOF

<declaration>          ::= <class-declaration> | <function-declaration> | <variable-declaration> | <statement>
<class-declaration>    ::= "class" <identifier> [" : " <identifier>] | "{" {<function>} "}"
<function-declaration> ::= "fun" <function>
<variable-declaration> ::= "let"   <identifier> [ "="  <expression> ] ";"
<constant-declaration> ::= "const" <identifier> [ "="  <expression> ] ";"


<function>             ::= <identifier> "(" [parameters] ")" <statement-block>
<parameters>           ::= <identifier> {"," <identifier>}


<statement>            ::= <expression-statement> | <for-statement>    | <if-statement>
                         | <print-statement>      | <return-statement> | <while-statement>
                         | <do-while-statement>   | <break-statement>  | <continue-statement>
                         | <import-statement>     | <statement-block>
<expression-statement> ::= <expression> ";"
<for-statement>        ::= "for" "(" <variable-declaration>
                                  | <expression-statement> ";"
                                  [expression] ";"
                                  [expression] ")" <statement>
<if-statement>         ::= "if" "(" <expression> ")" <statement> ["else" <statement>]
<print-statement>      ::= "print" <expression> ";"
<return-statement>     ::= "return" [<expression>] ";"
<while-statement>      ::= "while" "(" <expression> ")" <statement>
<do-while-statement>   ::= "do" <statement> "while" "(" <expression> ")"
<break-statement>      ::= "break;"
<continue-statement>   ::= "continue;"
<import-statement>     ::= "import" <string> ";"
<statement-block>      ::= "{" {<declaration>} "}"


<expression>           ::= assignment";"
<assignment>           ::= [<call> "."] <identifier> "=" assignment | <or>
<or>                   ::= <and>        { "or"   <and>}
<and>                  ::= <xor>        { "and"  <xor>}
<xor>                  ::= <nor>        { "xor"  <nor>}
<nor>                  ::= <nand>       { "nor"  <nand>}
<nand>                 ::= <equality>   { "nand" <equality>}
<equality>             ::= <comparison> { "==" | "!=" <comparison> }
<comparison>           ::= <bitwise>    { ">" | ">=" | "<" | "<=" <bitwise>}
<bitwise>              ::= <term>       { "+" | "-" <term>}
<term>                 ::= <factor>     { "+" | "-" <factor>}
<factor>               ::= <exponent>   { "/" | "*" | "%" <exponent>}
<exponent>             ::= <unary>      { "^" <unary>}
<unary>                ::= ( "!" | "-") <unary> | <call>
<call>                 ::= <subscript> { "(" [<arguments>] ")" | "." <identifier> }
<subscript>            ::= <primary> { "[" <or> "]"}
<primary>              ::= "true"       | "false"              | "null"
                         | "this"       | <number>             | <string>
                         | <identifier> | "(" <expression> ")" | "super" "." <identifier>
                         | <array_literal>
                         
<arguments>            ::= <expression> { "," <expression>}
<array_literal>        ::= "[" <array_initializer> "]"
<array_initializer>    ::= "" | <or> { "," <or> } [ "," ]

<number>               ::= { <digit> } ["." { <digit> }]
<string>               ::= "\"" <chars != "\""> "\""
<identifier>           ::= <alpha> { <alpha> | <digit> }
<alpha>                ::= "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m"
                               | "n" | "o" | "p" | "q" | "r" | "s" | "t" | "u" | "v" | "w" | "x" | "y"
                               | "z" | "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" | "J" | "K"
                               | "L" | "M" | "N" | "O" | "P" | "Q" | "R" | "S" | "T" | "U" | "V" | "W"
                               | "X" | "Y" | "Z" | "_"
<digit>                ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
```
