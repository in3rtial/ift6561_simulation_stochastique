
## Exercise 1 (6.1)

### Data

~~~
    CRNs with the (a + c) synchronization
    delta = 10.0 avg = 55.84410000000003 var = 2113.079303120312
    delta = 1.0 avg = 6.254299999999966 var = 38.38847035703572
    delta = 0.1 avg = 0.643300000000003 var = 2.013266436643664

    CRNs without synchronization
    delta = 10.0 avg = 55.35869999999988 var = 3673.855819891997
    delta = 1.0 avg = 5.370500000000023 var = 1902.0580355535608
    delta = 0.1 avg = 0.011500000000000265 var = 1946.5368214321438

    IRNs (with Bi set to 1.0)
    delta = 10.0 avg = 47.285200000000145 var = 2859.504211381143
    delta = 1.0 avg = 5.932600000000001 var = 2941.1475719972022
    delta = 0.1 avg = 1.0009000000000035 var = 2934.330532243232

    IRNs
    delta = 10.0 avg = 61.31770000000015 var = 56509.30329704001
    delta = 1.0 avg = 8.670700000000007 var = 46397.589820491965
    delta = 0.1 avg = 2.9493000000000125 var = 44282.17194670484
~~~

\pagenumbering{gobble}

### Conclusion

We want to compare the 4 types of synchronization tried.

First of all, as expected, the CRN (a+c) strategy worked best. This is the
same conclusion as the one reached in the book (so it's also kind of unexpected,
these simulations never seem to work out as expected).

Contrary to the result from the book, the variance of the CRN without synchronization
didn't get so low for the small values of $\delta$ (for $\delta=0.1$, the value from the
book was 726, much smaller than 1946).
This suggest that some implementation detail was different (not sure which one though).

The IRNs worked as expected, that is to say the variance was huge when
$B_i$ isn't fixed, as in the empirical observations in the book. When $B_i = 1.0$ for all $B_i$, the
variance was much smaller, and this makes a lot of sense since one of the sources
of variablility has been disabled and we get its average.
This practice (substituting a source of variability for the value of its mean) could make
sense in some scenarios, depending on what parameter is observed, but would
be horrible for example if we want to estimate some failure rate where the variability could provoke
some system-wide changes).


As expected, as $\delta\rightarrow 0$,  the difference gets harder to estimate
and the noisier estimators (particularly IRNs) become pretty useless. The CRN (a+c)
tend to keep up much better as it is supposed to do.

This is unfortunate, because the IRN simulations are much easier to implement...

