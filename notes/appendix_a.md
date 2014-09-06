# Appendix A


## Probabilities


**behavior of stochastic model**: depends on the realization ω
of a random phenomenon. It represents all sources of randomness in the
model. It must belong to the set Ω of all possible realizations (or outcomes),
called the sample space


**sample space (Ω)**: set of all possible realizations (or outcomes)


**probabilities**:
- let F = subsets Q for which the probabilities will be defined
- members of F are called the measurable sets, or events
    - not the same as events in discrete event simulation
- F must satisfy conditions of σ-field
    1. Ω itself must belong to F (all outcomes are possible)
    2. if B ∈ F then its complement Ω \ B is also in F
    3. if B 1 , B 2 , . . . are in F then their (denumerable) union is also in F
- pair (Ω, F) is called a measurable space

**signed measure**: function Q that assigns a value in (−∞, ∞] = R∪{∞}
to each event B ∈ F, and which is countably additive. The measure is positive if it
can never take negative values.


**probability measure**
A probability measure is a positive measure P for which P(Ω) = 1.
When P is a probability measure on (Ω, F), the mathematical structure
(Ω, F, P) is called a probability space.


### example A1

Suppose we throw two dice of different colors and observe the
two numbers showing up.

Ω can be defined as the set of all 36 possible pairs that we can obtain,
F can contain all subsets of Ω.

probability measure on F can be defined by
P(B) = |B|/36 for all B ⊆ Ω.

This corresponds to the idea of two independent fair dice.


## 

let Ω be finite or denumerable
F can be taken as the family of all its possible subsets

if Ω is non-denumerable (e.g an interval of the real line), cannot take F as
the set of all subsets of Ω, because many such subsets are too “weird” and
cause trouble.

This gives rise to technical subtleties studied by measure theory.
When Ω = R, F is usually taken as the Borel σ-field B,
defined as the smallest σ-field that contains all the intervals with rational end points.
-