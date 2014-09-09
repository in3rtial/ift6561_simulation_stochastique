# Homework 1  --------------  Gabriel C-Parent, C5912

## 1


(a) What is the number of ways of choosing the first t cards, for t ≤ 52, taking order into account?


taking order into account
- we want the total number of t-permutations of n
- the intuition is that at each level of the tree, we branch each previous branch of n - level
    - e.g. if t = 3 : (3-permutations of 52) = 52 * 51 * 50
- this is usually denoted as a factorial which stops at n - t + 1
- 52! / (52 - t + 1)!


without taking order into account
- we want the total number of t-combinations of n
- the intuition is the same as for the t-permutation, but for each card there are multiple orders of taking which are the same
- 52! / (((52 - t + 1)!)* (t!))



(b) What is the minimal period length of the generator, and the minimal number of bits needed to represent its state,
to make sure that every possibility can happen for the first t cards? Give expressions that are functions of t.


Let us restate the definition of a random number generator:
- deterministic algorithm
- produce periodic sequences of numbers
- simulate squences of independent random variables
- finite set of states
- goes from state to state according to some deterministic transition function
    - the next state is a function of the current state only
- output function assigns to each state a real number between 0 and 1
- from any given starting state (seed), the generator always produces the same output sequence
- if a pseudorandom number generator has n states, (log2(n) bits if n is a binary number): the maximum period is n - 1


Assuming that the order is important, there are at 52! / (52 - t + 1)! possible realizations.

If there are 52! / (52 - t + 1)! states, the miminal period is 1 because it is possible to establish
a bijection between each state and each realization.

If the number of states is 52! / ((52 - t + 1)! * 2), the minimal period is of 2.



The states of the pseudorandom number generator can be tought as a digraph that obeys the following properties



- number of bits
    - the generator must be able to represent every possible state of the system
    - the number of states in the system is t!
    - therefore we need ceil(log2(t!)) bits

- periodicity
    - to make sure that every possible permutation of t cards happens
    - the minimal periodictiy must be of t+1

(c) What is that minimal number of bits for t = 52? (Give a numerical value.)

~226 bits


(d) If the period length is 2 31 −2 (many widely-used classical LCGs have that value),
what is the maximal value of t for which we can have all the possibilities?

~~~julia

    # easy way is to just try (and use bigints to avoid overflow on int)
    i = 1
    while factorial(BigInt(i)) < (2^31-2)
      i+=1
    end
    println(i-1)  # substract because

~~~

so it goes over at factorial(12). Therefore it should be 12.



## 2

1.4 (a) Implement the SWB generator of Example 1.16, whose parameters
are (b, r, k) = (2 31 , 8, 48), and real-valued output defined by u n = x 2n /2 62 +
x 2n+1 /2 31 , and use it to generate three-dimensional points in [0, 1) 3 , defined by
u i = (u 25i , u 25i+20 , u 25i+24 ) for i = 0, . . . , m − 1, for m = 10 4 . Partition the unit
cube into k = 10 6 subcubes by partitioning each axis into 100 equal intervals.
Number these subcubes from 0 to k − 1 (in any way), find the number of the
subcube in which each point u i has fallen, and count the number C of collisions
as in Example 1.6. Repeat this 10 times, to obtain 10 “independent” realizations
of C, and compare their distribution with the Poisson approximation given in
Example 1.6. You can do the latter comparison informally; there is no need to
perform a formal statistical test.
(b) Redo the same experiment, but this time using a better generator, such
as MRG32k3a in SSJ, for example. Discuss your results.


~~~

    # substract-with-borrow (SWB)
    type SWB
      state::BigInt
      b::Int
      r::Int
      k::Int

      function SWB(seed::Int, b::Int, r::Int, k::Int)
        # seed is integer
        @assert (k > 0 && r > 0 && 
        @assert k
        
      end

      function SWB(seed::BigInt, b::Int, r::Int, k::Int)
        # seed is BigInt
        state::BigInt(seed) = 
        
        
      end

    end


~~~


## 3

Voir l’exercice 1.18 des notes. Il n’y a pas de simulation a implanter pour cette question.
L’id ́ee est de comprendre ce qui se passe si on estime le volume d’une sphere de rayon 1 en s
dimensions par la m ́ethode Monte Carlo. Il s’agit bien sˆ
ur d’un exercice pˆ
urement acad ́emique,
puisqu’on connait deja le volume de cette sphere, mais il permet de comprendre un type de
difficult ́e qui survient dans de nombreuses applications pratiques. Pour estimer le volume, on
tire n points au hasard dans le cube (0, 1) s , on calcule la fraction p  ̃ n de ces points qui tombent
dans la sphere (pour estimer la fraction p du cube occup ́e par la sphere), et l’estimateur du
volume est μ
 ̃ n = 2 s p  ̃ n .
(a) Prouvez que cet estimateur est sans biais. Donnez aussi (avec preuve) des formules exactes
pour la variance et l’erreur relative de cet estimateur, en fonction de s.
(b) Pour avoir une erreur relative constante en fonction de s, disons inf ́erieure a  0.01 pour
tout s, a  quelle vitesse (ou de quelle maniere) doit-on augmenter n en fonction de s, lorsque
s est grand? Donnez une formule pour n en fonction de s et expliquez ce que cela implique
pour les grandes valeurs de s.

(c) Calculez les valeurs num ́eriques de p, V s , et de l’erreur relative au carr ́e de μ
 ̃ n , RE 2 [ ̃
μ n ],
pour s = 2, 5, 10, 20.
Aide: Pour cette question, vous pouvez utiliser le fait que le volume d’une sphere de rayon 1 √ en
s dimensions est V s = π s/2 /Γ(1+s/2), o
u Γ(·) est la fonction gamma, d ́efinie par Γ(1/2) = π,
Γ(1) = 1, et Γ(s + 1) = sΓ(s). Lorsque s est grand, l’approximation de Stirling donne
Γ(1 + s/2) ≈ s/2 ! ≈ (πs) 1/2 (s/(2e)) s/2 .


















