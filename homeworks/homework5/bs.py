import math

# Define "convenience" labels or time steps                                                                  
one_month   = 1.0 / 12.0
two_month   = 2.0 / 12.0
three_month = 3.0 / 12.0
four_month  = 4.0 / 12.0
six_month   = 6.0 / 12.0
one_year    = 1.0

# ------------------------------                                                                             
# Term in Black-Scholes solution                                                                             
# ------------------------------                                                                             
def d1(S,K,sigma,r,t):
  v1 = math.log(S/K)
  v2 = (r + (sigma*sigma)/2.0) * t
  v3 = sigma * math.sqrt(t)
  return (v1+v2)/v3

# ------------------------------                                                                             
# Term in Black-Scholes solution                                                                             
# ------------------------------                                                                             
def d2(S,K,sigma,r,t):
  return d1(S,K,sigma,r,t) - sigma * math.sqrt(t)

# ---------------------------------------                                                                    
# Price a call option using Black-Scholes                                                                    
# ---------------------------------------                                                                    
def call(S,K,sigma,r,t):
  """ Price a call option using Black-Sholes """
  v1 = d1(S,K,sigma,r,t)
  v2 = d2(S,K,sigma,r,t)

  return S * erf(v1) - K * math.exp(-r*t) * erf(v2)

# --------------------------------------                                                                     
# Price a put option using Black-Scholes                                                                     
# --------------------------------------                                                                     
def put(S,K,sigma,r,t):
  """ Price a put option using Black-Sholes """
  v1 = d1(S,K,sigma,r,t)
  v2 = d2(S,K,sigma,r,t)

  return K * math.exp(-r*t) * erf(-v2) - S * erf(-v1)

# ---------------------------------------                                                                    
# Cumulative normal distribution function                                                                    
# (distributed between 0 and 1)                                                                              
# ---------------------------------------                                                                    
def erf(x):
  """ CDF distributed between 0 and 1 """
  gamma = 0.2316419
  k = 1.0 / (1.0 + x * gamma)

  a1 = 0.319381530
  a2 = -0.356563782
  a3 = 1.781477937
  a4 = -1.821255978
  a5 = 1.330274429

  q = 1.0 / math.sqrt(2 * math.pi)
  N = q * math.exp(-(x*x)/2.0)

  if x >= 0:
    return 1 - (N) * (a1 * k + a2 * math.pow(k,2) + a3 * math.pow(k,3) + a4 * math.pow(k,4) + a5 * math.pow(\
k,5))
  return 1 - erf(-x)