import csv
import numpy as np
import pandas as pd
from scipy import stats
import matplotlib as mpl
import matplotlib.pyplot as plt
import seaborn as sns


# acquire the data form the csv file
blocking_data = np.loadtxt("exercise3_blocking.csv",delimiter=",")

waiting_data = np.loadtxt("exercise3_wait.csv", delimiter=",")

client_data = np.loadtxt("exercise3_numclients.csv")

block_avg = np.mean(blocking_data, axis=0)

wait_avg = np.mean(waiting_data, axis=0)

client_avg = np.mean(client_data)


# plot the data
# general pyplot setup
sns.set(style="white", palette="muted")

f1, axes1 = plt.subplots(3, 1)
f2, axes2 = plt.subplots(2, 1)


# create the color palette
palette = sns.color_palette("muted", 3)


# add the waiting time data
sns.distplot(waiting_data[:,0:1], color=palette[0], ax=axes1[0], axlabel="Sum of waiting times at station 1")
sns.distplot(waiting_data[:,1:2], color=palette[1], ax=axes1[1], axlabel="Sum of waiting times at station 2")
sns.distplot(waiting_data[:,2:3], color=palette[2], ax=axes1[2], axlabel="Sum of waiting times at station 3")
plt.setp(axes1, yticks=[])
f1.show()

# add the blocking time data
sns.distplot(blocking_data[:,0:1], color=palette[0], ax=axes2[0], axlabel="Sum of blocking times at station 1")
sns.distplot(blocking_data[:,1:2], color=palette[1], ax=axes2[1], axlabel="Sum of blocking times at station 2")


plt.setp(axes2, yticks=[])
f2.show()

# save both figures in .png formats


# figure out the expectation

# waiting averages
print(np.mean(waiting_data, axis=0) / client_avg)
print(np.mean(blocking_data, axis=0) / client_avg)


# yields
#[ 4.06066608  1.92368352  2.31880874]
#[ 0.39191884  0.08863788  0.        ]
