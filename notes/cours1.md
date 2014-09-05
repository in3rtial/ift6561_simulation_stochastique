# Cours 1

# Introduction

## systems, models and simulation


### purpose of modeling and simulation


- simulation = running computer programs to imitate (simulate) behavior of certain systems
- model = simplified abstract (conceptual, mathematical) representation of a system
    - conceptual/mathematical models = abstract constructions
    - simplify reality (model better defined than the real system)
    - must be well defined in all relevant behaviors and properties
    - good abstrat model often improves understanding of the system's behavior
- physical model = scaled-down versions of the system


### types of mathematical models


- (mathematical models((analytical formula) (numerical resolution) (simulation)))
- analytical models = simplest to handle, analytical solution by mathematical formula
    - e.g. balistics equations
    - only availabe for really simple systems
    - deriving an analytical formula is usually *fuckin hard*

- deterministic numerical algorithms
    - e.g. linear-nonlinear-dynamic programming, differential-equation solvers
    - sometimes referred to as simulation, not in our context

- simulation
    - Monte Carlo methods
    - can be used to validate simple analytical models (example of hashing table collisions)


- stochastic vs deterministic
    - based on wether or not its specification involves randomness
    - simulating stochastic models involves generation of random variable, complex on computer


- static vs dynamic
    - static = time independent, e.g. evaluating P(nodes are connected) in a graph
    - dynamic = time dependent, e.g. queues
        - continuous simulation = dynamic with differential equations
        - discrete-event simulation = evenly spaced time points (event times)

- hybrid models = model may change from continuous <-> discrete, deterministic <->continuous


### advantages and disadvantages of simulation


pros:


- non-destructive
- system can be abstract
- can experiment at will (e.g. slightly different input value to evaluate sensitivity)
- faster than irl
- can allow visualization of the inner workings to gain insights (teaching, optimization)


cons:


- time and money
- modeling is complicated (specialized training and experience)
- usefulness is highly dependent on the skill of the modeler
- time needed to compute statistically significant results can be prohibitive
- statistical analysis can become very complicated
- doesn't yield an optimal soltion, optimization is harder than with mathematical optimization


### models and programs


- sometimes programs can only approximate the model
    - e.g. random number generation sucks), don't expect true random variables
    - e.g. infinite horizon, can only simulate for finite time...

- modeling = building mathematical model of real-life system
- programming = writing a simulation program of a model
- verification = verifying that a program is correctly simulating the model
- validation = verifying that the model is truly representative of modeled system

Proper modeling and validation is really the hard part compared to programming and validating.
Restricting a model by neglecting some aspects of it is an important part of building a model.


### Monte Carlo methods


- general family of techniques that involve random sampling for solving (approximatly) a problem
- often used in:
    - estimation of high-dimensional integrals
    - solving linear systems of equations
    - solving PDE
    - estimating the cardinality of sets of combinatorial objects (counting)
    - optimizing a function

- Markov Chain Monte Carlo (MCMC)
    - construct artificial ergodic Markov chain whose steady-state distribution is related to probability of distribution
    - construction of the object is done by Metropolis-Hasting algorithm


### examples of simple simulation models


**Stochastic Activity Network**


Directed Acyclic Graph with origin and destination. The set of nodes represent states
and the set of arcs represents activities. The idea is that to reach the final state,
each previous states must be reached in the path to it.
Completin time is the length of the longest path from the source to the sink (critical path).
PERT/CPM methods address this type of problems when the time values are exact.


**Static reliability of multicomponent system**

- communication networks, computer systems in banks, power plants, production systems, aircrafts, military devices, etc.
- m components, which can be in state 0 (failed) or 1 (operational)
- time plays no role as if 
- This could mean that the system it sis observed at a fixed point in time

- states::Vector{Bool}
- working(states) -> system_operational::Bool
- reliability = P(working(system)) = true
- assume that the components are independent
- possible to simulate the system and estimate average reliability if working(states) is simple enough
    - usually presented in the form of a graph
- real problem is evaluating unreliability (1 - reliability)
    - this is know as rare event simulation


**queueing system**

- each station is a single-server queueing with potentially finite capacity
- FIFO
- processes sequentially t

~~~
    Let T 0 = 0 and D 0,0 = D 1,0 = 0;
    For j = 2, . . . , m,
    For i = −c j + 1, . . . , 0, let D j,i = 0;
    For i = 1, . . . , N c ,
    Generate A i from its distribution and let D 0,i = T i = T i−1 + A i ;
    Let W i = B i = 0;
    For j = 1, . . . , m,
    Generate S j,i from its distribution;
    Let D j,i = max[D j−1,i + S j,i , D j,i−1 + S j,i , D j+1,i−c j+1 ];
    Let W j,i = max[0, D j,i−1 − D j−1,i ] and W i = W i + W j,i ;
    Let B j,i = D j,i − D j−1,i − W j,i − S j,i and B i = B i + B j,i ;
    Compute and return the averages
    Wnc = (W 1 + · · · + W N c )/N c and B
    Wnc = (B 1 + · · · + B N c )/N c
~~~




