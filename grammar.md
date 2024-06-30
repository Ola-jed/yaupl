# Grammar of the yaupl language (EBNF Notation)

```
<program> ::= {<declaration>} EOF

<declaration>          ::= <class-declaration> | <function-declaration> | <variable-declaration> | <statement>
<class-declaration>    ::= "class" <identifier> [" : " <identifier>] | "{" {<function>} "}"
<function-declaration> ::= "fun" <function>
<variable-declaration> ::= "let" <identifier> [ "="  <expression> ] ";"


<statement>            ::= <expression-statement> | <for-statement>    | <if-statement>
                         | <print-statement>      | <return-statement> | <while-statement>
                         | <do-while-statement>   | <break-statement>  | <continue-statement>
                         | <statement-block>
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
<statement-block>      ::= "{" {<declaration>} "}"


<expression>           ::= 

```
