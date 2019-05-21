Scenario: Evaluating grammatically correct numerical expressions
Given 2+1 as input
When numerical expression is evaluated
Then result should equal 3

Given 5*3-2 as input
When numerical expression is evaluated
Then result should equal 13

Given 0.5*10%3 as input
When numerical expression is evaluated
Then result should equal 2

Given (40/8+5)*5 as input
When numerical expression is evaluated
Then result should equal 50

Given (2)*(10) as input
When numerical expression is evaluated
Then result should equal 20

Given (20000/2/10)/100 as input
When numerical expression is evaluated
Then result should equal 10






Scenario: Evaluating grammatically incorrect numerical expressions
Given 2*+3 as input
When numerical expression is evaluated
Then evaluation exception should be thrown

Given (2)-(2+3)) as input
When numerical expression is evaluated
Then evaluation exception should be thrown

Given + as input
When numerical expression is evaluated
Then evaluation exception should be thrown

Given (5*3+1 as input
When numerical expression is evaluated
Then evaluation exception should be thrown

Given RaNdOmTeXt as input
When numerical expression is evaluated
Then evaluation exception should be thrown






Scenario: Evaluating grammatically correct relational expressions
Given 2+1==8 as input
When relational expression is evaluated
Then result should be false

Given 6*5>=30 as input
When relational expression is evaluated
Then result should be true

Given 6*5<30 as input
When relational expression is evaluated
Then result should be false

Given 800/400==10*2/10 as input
When relational expression is evaluated
Then result should be true

Given 0!=0 as input
When relational expression is evaluated
Then result should be false





Scenario: Evaluating grammatically incorrect relational expressions
Given 2+1==8>2 as input
When relational expression is evaluated
Then evaluation exception should be thrown

Given 6*5-30 as input
When relational expression is evaluated
Then evaluation exception should be thrown

Given 6*5$30 as input
When relational expression is evaluated
Then evaluation exception should be thrown