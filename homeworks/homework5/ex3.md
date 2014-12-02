
## Exercise 3 (6.6)

\pagenumbering{gobble}

To maximize the correlation between pairs of variables, theorem 2.5 states that
they should be generated using

(1)   $(X_1, X_2) = (F_1^{-1}(U), F_2^{-1}(U))$

when working with inversion, which is equivalent to
generating first $X_1$ and then $X_2$ according to the relationship:

(2)  $X_2 = F_2^{-1}(F_1(X_1))$


To generate an $Erlang(k, \gamma)$ it is possible to generate first an $Erlang(k, 1.0)$ and then
multiply the resulting values by $1/\gamma$ to scale the distribution.
This can be demonstrated by taking the $\gamma$ term out of the equation to generate the
Erlang using a sum of exponential of $\lambda = \frac {1} {\gamma}$.


This ressembles the second way of generating a correlated variable.
In fact, it is equivalent because the linear scaling of the Erlang distributions
preserves the quantile order in (eq 2). It's like using a modified quantile function which
maps to the "standard" $Erlang(k, \gamma = 1.0)$ and then remapping to the correct
quantile of $Erlang(k, \gamma_2)$ by multiplying by $\frac {1} {\gamma_2}$.


We have all the pieces of the puzzle. Using the non-inversion method, we get
$Erlang(k, 1.0)$ by taking

$X = -\sum_{i=1}^k-ln(1-U_i)$ (eq 3)

where U~$U(0, 1)^k$

This is equivalent to generating $X = X_1 \gamma_1$, and then $X_2 = \frac {X} {\gamma_2}$.
Since this is pretty much the same as (eq 2), then the variance maximization property is the
same as the one we get using inversion.