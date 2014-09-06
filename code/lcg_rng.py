"""linear congruential generator (example 1.12)"""

class linearCongruentialGenerator(object):
    """linear congruential generator"""
    def __init__(self, seed, a, m, c):
        assert a > 0
        assert m > 0
        # every input must be an integer
        assert isinstance(seed, int)
        assert isinstance(a, int)
        assert isinstance(m, int)
        assert isinstance(c, int)
        self.a = a
        self.m = m
        self.c = c
        # map the seed to the state
        self.state = seed % m

    def yield_random(self):
        """returns an output and transition state"""
        previous_state = self.state
        self.state = (self.a * self.state + self.c) % self.m
        return(previous_state / self.m)


# the one given as example in the book
random_generator = linearCongruentialGenerator(10, 12, 101, 0)
print(random_generator.yield_random) # 0.0990099
print(random_generator.yield_random) # 0.1881188
print(random_generator.yield_random) # 0.2574257
print(random_generator.yield_random) # 0.0891089
