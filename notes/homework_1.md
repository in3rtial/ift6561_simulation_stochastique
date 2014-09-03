# Homework 1
## Gabriel C-Parent, C5912

### 1.1


(a) What is the number of ways of choosing the first t cards, for t ≤ 52, taking order into account?


taking order into account
- we want the total number of t-permutations of n
- the intuition is that at each level of the tree, we branch each previous branch of n - level
    - e.g. if t = 3 : t-permutations of 52 = 52 * 51 * 50
- this is usually denoted as a factorial which stops at n - t + 1
- 52! / (52 - t + 1)!


without taking order into account
- we want the total number of t-combinations of n
- the intuition is the same as for the t-permutation, but for each card there are multiple orders of taking which are the same
- 52! / (((52 - t + 1)!)* (t!))



(b) What is the minimal period length of the generator, and the minimal number of bits needed to represent its state,
to make sure that every possibility can happen for the first t cards? Give expressions that are functions of t.


- number of bits
    - the generator must be able to represent every possible state of the system
    - the number of states in the system is t!
    - therefore we need ceil(log2(t!)) bits

- periodicity
    - to make sure that every possible permutation of t cars happens


(c) What is that minimal number of bits for t = 52? (Give a numerical value.)


~226 bits


(d) If the period length is 2 31 −2 (many widely-used classical LCGs have that value), what is the maximal value of t for which we can have all the possibilities?