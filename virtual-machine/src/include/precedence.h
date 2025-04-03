#ifndef PRECEDENCE_H
#define PRECEDENCE_H

enum class Precedence: uint8_t
{
    None,
    Assignment,
    Or,
    And,
    Xor,
    Nor,
    Nand,
    Equality,
    Comparison,
    Bitwise,
    Term,
    Factor,
    Exponent,
    Unary,
    Call,
    Primary
};
#endif //PRECEDENCE_H
