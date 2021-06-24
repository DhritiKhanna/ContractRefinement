## Contract Refinement

Implements the refinement operator from the paper: [__Contracts for System Design: Theory.__](https://hal.inria.fr/hal-01178467/document)

This project uses z3 solver to check if a contract refines another. 

### Rules for refinement. C1 refines C2 (C1 < C2) iff:
1. Assumption_C2 implies Assumption_C1
2. Assumption_C1 ==> Guarantee_C1 implies Assumption_C2 ==> Guarantee_C2

### Input format:

    input: <x, int>; <y, int>
    output: <area, int>
    assumption: x != 0 and y != 0
    guarantee: area = x * y
    -----
    input: <x, int>; <y, int>
    output: <area, int>
    assumption: x > 10 and y > 0
    guarantee: area > 10

### Assumptions:
1. Each input and output variable is specified as a pair of name and type inside angular brackets. Multiple variables are separated with ';'.
2. The assumption and guarantee formulas are written in infix notation and each assert is separated with an 'and'.

The code is properly commented. 
