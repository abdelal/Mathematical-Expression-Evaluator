# Mathematical-Expression-calculator

### About 
This is a simple Math Expression cacluator for the grammer given speicfied bellow.


##### What does it do
evaluates a given mathmatical expression.


#### Features
- evaluates a given mathmatical expression.
- saves the prevoius expression result in a special variable called _.
- bind variables to values .


##### How it works
the given expression is tokenized, and then evaluated directly(as an infix) using stacks.



#### grammer :-
      expr ::= factor | expr ('+' | '-') expr
      factor ::= term | factor ('' | '/') factor
      term ::= '-' term | '(' expr ')' | number | id | function | binding
      number ::= int | decimal
      int ::= '0' | posint
      posint ::= ('1' - '9') | posint ('0' - '9')
      decimal ::= int '.' ('0' - '9') | '.' ('0' - '9')
      id ::= ('a' - 'z' | 'A' - 'Z' | '_') | id ('a' - 'z' | 'A' - 'Z' | '_' | '0' - '9')
      function ::= ('sqrt' | 'log' | 'sin' | 'cos') '(' expr ')'
      binding ::= id '=' expr

      The binary operators are left-associative, with multiplication and division
      taking precedence over addition and subtraction.

##### Example :-
 ``` 
 > 5*2+(((5+7)*2)-5+6)-(((4+5-10+20)-10)*3)*2 
  -19 
  >   \_
  -19 
 >  x= _
 -19 
  >  x 
 -19 
```
