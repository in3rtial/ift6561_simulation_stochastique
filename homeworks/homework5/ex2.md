
## Exercise 2 (6.2)

### Data

~~~
    Variance reduction for delta = 10.0
    Mean with propor. allocation + CV       =    55.650
    Variance with propor. allocation + CV   =   207.017

    Variance reduction for delta = 1.0
    Mean with propor. allocation + CV       =     6.270
    Variance with propor. allocation + CV   =    15.704

    Variance reduction for delta = 0.1
    Mean with propor. allocation + CV       =     0.638
    Variance with propor. allocation + CV   =     1.607

~~~

\pagenumbering{gobble}

### Conclusion

So I used the (a + c) synchronization for the CRNs, the same as for exercise 1.

If we check the results for exercise 1:

~~~
    CRNs with the (a + c) synchronization
    delta = 10.0 avg = 55.84410000000003 var = 2113.079303120312
    delta = 1.0 avg = 6.254299999999966 var = 38.38847035703572
    delta = 0.1 avg = 0.643300000000003 var = 2.013266436643664
~~~

We can see that the variance was reduced quite a bit compared to exercise 1:

$\delta = 10$: 2113.079 $\rightarrow$ 207.017

$\delta = 1$: 38.388 $\rightarrow$ 15.704

$\delta = 0.1$: 2.013 $\rightarrow$ 1.607


The ratio grows bigger as $\delta$ grows so the control variable helps a lot when
the difference between the two systems gets bigger (although at some point, I expect
it to make not much of a difference if $\delta$ gets too big, because the estimate would be pretty useless for both I guess).


If delta is small (0.1), the extra work might not make much sense, but for bigger values, it
sure does.

This is a sad conclusion, since programming these things tends to give me headaches.
