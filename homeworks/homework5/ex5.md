
## Exercise 5 (6.16)

\pagenumbering{gobble}

### Data


~~~
    =======================================================================
    VALIDATION

    Value expected without barrier 18.656769422297188


    REPORT on Tally stat. collector ==> Value without barrier (validation)
        num. obs.      min          max        average     standard dev.
        10000       18.657       18.657       18.657        0.000


    REPORT on Tally stat. collector ==> Value without barrier (validation)
        num. obs.      min          max        average     standard dev.
        10000        0.000      118.004       18.663       17.952

    =======================================================================
    BARRIERS

    REPORT on Tally stat. collector ==> barrier = 0.0
        num. obs.      min          max        average     standard dev.
        10000        0.000        0.000        0.000        0.000

    REPORT on Tally stat. collector ==> barrier = 0.0 with CMC
        num. obs.      min          max        average     standard dev.
        10000        0.000        0.000        0.000        0.000



    REPORT on Tally stat. collector ==> barrier = 75.0
        num. obs.      min          max        average     standard dev.
        10000        0.000       15.080        0.014        0.311

    REPORT on Tally stat. collector ==> barrier = 75.0 with CMC
        num. obs.      min          max        average     standard dev.
        10000        0.000        2.128        0.020        0.131



    REPORT on Tally stat. collector ==> barrier = 80.0
        num. obs.      min          max        average     standard dev.
        10000        0.000       34.577        0.104        1.138

    REPORT on Tally stat. collector ==> barrier = 80.0 with CMC
        num. obs.      min          max        average     standard dev.
        10000        0.000        4.438        0.130        0.495



    REPORT on Tally stat. collector ==> barrier = 90.0
        num. obs.      min          max        average     standard dev.
        10000        0.000       56.555        2.031        6.006

    REPORT on Tally stat. collector ==> barrier = 90.0 with CMC
        num. obs.      min          max        average     standard dev.
        10000        0.000       10.152        2.116        3.133



    REPORT on Tally stat. collector ==> barrier = 95.0
        num. obs.      min          max        average     standard dev.
        10000        0.000       79.248        5.172        9.963

    REPORT on Tally stat. collector ==> barrier = 95.0 with CMC
        num. obs.      min          max        average     standard dev.
        10000        0.000       13.812        5.361        5.203

~~~

I used the equation as described by Okten *et al.* (On pricing discrete barrier options
using condigional expectation and importance sampling in Monte Carlo, Okten G, Salta E, Goncu A., 2008),
end of p.485.


Basically, the value is

 \begin{equation}
   X_e=
   \begin{cases}
   BSM(S(t_k), t_k, T), & \text{if the barrier is crossed at time } t_k \\
   0, & \text{otherwise}
  \end{cases}
\end{equation}
  



the idea is that we know the expectation of the European call option, so if the barrier is crossed 
( becomes an Europen call option) we calculate directly the exact value of the option's expected payoff
on the remaining trajectory instead of continuing.

It still is Monte Carlo but in the event of s_0 < barrier, then it is exactly the same as calculating by BSM (Black-Scholes).


As can be seen in the data, the improvement is much better when the barrier is hit early and frequently
(as said, in the extreme case of starting under the barrier, it is equivalent to direct calculation by BSM).

So the experiments match the expectation (the CMC is always a better estimator).
