# Homework 1
# Gabriel C-Parent, C5912

## [1]


(a) What is the number of ways of choosing the first t cards, for t ≤ 52,
taking order into account?


**taking order into account (permutations)**
- we want the total number of t-permutations of n
- the state space can be represented by a tree
    - starting with 52 nodes, then 51 for each of the 52...
    - e.g. if t = 3 : (3-permutations of 52) = 52 * 51 * 50
- this is usually denoted as a factorial which stops at n - t 
- 52! / (52 - t )!


**without taking order into account (combinations)**
- we want the total number of t-combinations of n
- the intuition is the same as for the t-permutation
    - there are multiple repetitions in the permutations
- 52! / (((52 - t)!)* ( t!))


(b) What is the minimal period length of the generator, and the minimal number
of bits needed to represent its state, to make sure that every possibility can
happen for the first t cards? Give expressions that are functions of t.


Let us restate the properties of a pseudorandom number generator:

- deterministic algorithm
- produces periodic sequences of numbers
- simulate squences of independent random variables
- finite set of states
- goes from state to state according to some deterministic transition function
    - the next state is a function of the current state only
- output function assigns to each state a real number between 0 and 1
- from a given starting state, the generator produces the same output sequence
- with n states, the maximum period is n - 1

If "to make sure that every possibility can happen for the first t cards" means
that every possible realization can happen, given every seed:

- The simplest way would be to have 52! / (52 - t)! states.
- These states can be encoded in ceiling(log2(number_of_states)) bits.


- The shortest periodicity of such PRNG would have to be of number_of_states - 1
    - otherwise some state would be impossible to reach (violating the condition)


However, if "to make sure that every possibility can happen for the first t cards"
means that every possible realization can happen, given some some seed:

- The simplest way would be to have 52! / (52 - t)! states.
- These states can be encoded in ceiling(log2(number_of_states)) bits.

- The shortest periodicity of such PRNG could be of 0
    - there could be a state which comes back to itself
    - but the PRNG would be called only once


(c) What is that minimal number of bits for t = 52? (Give a numerical value.)

~~~python
    # the dumb iterative way

    def factorial(n):
      """iterative factorial"""
      ret = 1
      for i in range(1, n+1):
        ret *= i
      return ret

    threshold = factorial(52) / factorial(52 - 52 )

    necessary_bits = 1
    while 2**necessary_bits < threshold:
      necessary_bits += 1

    print(necessary_bits)
~~~


~~~python
    # the more mathy way (but not exact)

    from math import ceil, e, pi, sqrt, log2  # get the necessary

    def stirling(n):
      """stirling approximation of factorial(n)"""
      assert n>0 and isinstance(n, int)
      return sqrt(2*pi*n) * ((n / e)**n)

    print(ceil(log2(stirling(52))))
~~~

Both of them yield a value of 226.


(d) If the period length is 2^31 −2 (many widely-used classical LCGs have that value),
what is the maximal value of t for which we can have all the possibilities?


~~~python
    # in the same fashion as (c)

    from math import e, pi, sqrt, log2  # get the necessary

    def stirling(n):
      """stirling approximation of factorial(n)"""
      assert n>0 and isinstance(n, int)
      return sqrt(2*pi*n) * ((n / e)**n)

    def number_of_states(t):
      """calculate the number of states needed for t"""
      assert t>0 and isinstance(t, int)
      return stirling(52) / stirling(52 - t )

    t = 1
    while number_of_states(t+1) < ((2**31)-2):
      t += 1

    print(t)
~~~

This yields a value of t = 6.


## [2]

1.4 (a) Implement the SWB generator of Example 1.16, whose parameters
are (b, r, k) = (2^31 , 8, 48), and real-valued output defined by
un = x 2n /2 62 + x2n+1 /2 31 , and use it to generate three-dimensional
points in [0, 1) 3 , defined by ui = (u25i, u25i+20, u25i+24)
for i = 0, ..., m−1, for m = 10 4.

Partition the unit cube into k = 10^6 subcubes by partitioning each axis
into 100 equal intervals.

Number these subcubes from 0 to k − 1 (in any way), find the number of the
subcube in which each point u i has fallen, and count the number C of collisions
as in Example 1.6. Repeat this 10 times, to obtain 10 “independent” realizations
of C, and compare their distribution with the Poisson approximation given in
Example 1.6. You can do the latter comparison informally; there is no need to
perform a formal statistical test.


(b) Redo the same experiment, but this time using a better generator, such
as MRG32k3a in SSJ, for example. Discuss your results.





## 3

Voir l’exercice 1.18 des notes. Il n’y a pas de simulation a implanter pour cette question.
L’idee est de comprendre ce qui se passe si on estime le volume d’une sphere de rayon 1 en s
dimensions par la m ́ethode Monte Carlo. Il s’agit bien sur d’un exercice purement academique,
puisqu’on connait deja le volume de cette sphere, mais il permet de comprendre un type de
difficult ́e qui survient dans de nombreuses applications pratiques. Pour estimer le volume, on
tire n points au hasard dans le cube (0, 1) s , on calcule la fraction p  ̃ n de ces points qui tombent
dans la sphere (pour estimer la fraction p du cube occup ́e par la sphere), et l’estimateur du
volume est μn = 2 s pn .
(a) Prouvez que cet estimateur est sans biais. Donnez aussi (avec preuve) des formules exactes
pour la variance et l’erreur relative de cet estimateur, en fonction de s.
(b) Pour avoir une erreur relative constante en fonction de s, disons inf ́erieure a  0.01 pour
tout s, a  quelle vitesse (ou de quelle maniere) doit-on augmenter n en fonction de s, lorsque
s est grand? Donnez une formule pour n en fonction de s et expliquez ce que cela implique
pour les grandes valeurs de s.

(c) Calculez les valeurs numeriques de p, V s , et de l’erreur relative au carre de μ
n , RE 2 [ ̃μ n ], pour s = 2, 5, 10, 20.

















