# Gabriel C-Parent    Homework 3


# Exercise 1


## Data


    REPORT on Tally stat. collector ==> HIT-OR-MISS {a= 0.0, b= 2.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        10000        0.000        2.766        0.017        0.166
      95.0% confidence interval for mean (student): (     0.014,     0.021 )

    REPORT on Tally stat. collector ==> IS {a= 0.0, b= 2.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        10000       1.4E-6        0.082        0.019        0.013
      95.0% confidence interval for mean (student): (     0.018,     0.019 )


    REPORT on Tally stat. collector ==> HIT-OR-MISS {a= 0.0, b= 3.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        10000        0.000        3.287       3.2E-3        0.091
      95.0% confidence interval for mean (student): (    1.5E-3,    5.0E-3 )

    REPORT on Tally stat. collector ==> IS {a= 0.0, b= 3.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        10000       7.9E-8        0.014       3.4E-3       2.6E-3
      95.0% confidence interval for mean (student): (    3.3E-3,    3.4E-3 )


    REPORT on Tally stat. collector ==> HIT-OR-MISS {a= 0.0, b= 4.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        10000        0.000        0.000        0.000        0.000
      95.0% confidence interval for mean (student): (     0.000,     0.000 )

    REPORT on Tally stat. collector ==> IS {a= 0.0, b= 4.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        10000      9.6E-11       9.8E-4       2.3E-4       2.1E-4
      95.0% confidence interval for mean (student): (    2.2E-4,    2.3E-4 )



/newpage

## Discussion


### ratios between the variances (for same b and n)


- let's see the ratios between the standard deviations
    - b = 2: 12.76
    - b = 3: 35.00
    - b = 4: NA, couldn't get an observation of nonzero X for the hit-or-miss estimator


so, as b gets bigger, the number of observations above zero tends to become a rare event
(at least, with the distributions we are using).

The relative error gets really big.
Taking 10 times as much samples (100000), we get for b = 4:


    REPORT on Tally stat. collector ==> HIT-OR-MISS {a= 0.0, b= 4.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        100000        0.000        3.642       1.0E-4        0.019
      95.0% confidence interval for mean (student): (   -1.4E-5,    2.2E-4 )

    REPORT on Tally stat. collector ==> IS {a= 0.0, b= 4.0, k= 1.0}
        num. obs.      min          max        average     standard dev.
        100000      3.7E-12       1.0E-3       2.3E-4       2.1E-4
      95.0% confidence interval for mean (student): (    2.3E-4,    2.3E-4 )

As expected, the efficiency of the IS estimator is much better than the hit-or-miss.
Its variance is much lower, at the cost of perhaps having trouble sometimes to figure out how
to truncate (if possible at all).

