import csv
import numpy as np
import pandas as pd
from scipy import stats
import matplotlib as mpl
import matplotlib.pyplot as plt
import seaborn as sns


# acquire the data form the csv file
data = dict()

with open("exercise2.csv", "r") as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=",")
    for line in csv_reader:
      data[(line[0], int(line[1]))] = np.array(line[2:], dtype="f8")



# plot the data
# general pyplot setup
sns.set(style="white", palette="muted")

f1, axes1 = plt.subplots(3, 1)
f2, axes2 = plt.subplots(2, 1)


# create the color palette
palette = sns.color_palette("muted", 3)


# add the waiting time data
sns.distplot(data[("W", 0)], color=palette[0], ax=axes1[0], axlabel="Average waiting times at station 1")
sns.distplot(data[("W", 1)], color=palette[1], ax=axes1[1], axlabel="Average waiting times at station 2")
sns.distplot(data[("W", 2)], color=palette[2], ax=axes1[2], axlabel="Average waiting times at station 3")
plt.setp(axes1, yticks=[])
f1.show()

# add the blocking time data
sns.distplot(data[("B", 0)], color=palette[0], ax=axes2[0], axlabel="Average blocking times at station 1")
sns.distplot(data[("B", 1)], color=palette[1], ax=axes2[1], axlabel="Average blocking times at station 2")


plt.setp(axes2, yticks=[])
f2.show()

# save both figures in .png formats
