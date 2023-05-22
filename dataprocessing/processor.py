import numpy as np
import pandas as pd
from matplotlib import pyplot as plt

query_response_times = np.loadtxt("/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_100/RepeatableRead/queryResponseTimesRR.txt", dtype = 'int')

insert_response_times = np.loadtxt("/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_100/RepeatableRead/insertResponseTimesRR.txt", dtype = 'int')

commit_times = np.loadtxt("/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_100/RepeatableRead/commitTimesMPL100RR.txt", dtype = 'int')

number_of_data_points = int(query_response_times.size/1000)
query_response_times = query_response_times[:(number_of_data_points*1000)]
number_of_data_points = int(insert_response_times.size/1000)
insert_response_times = insert_response_times[:number_of_data_points*1000]

query_averages = np.average(query_response_times.reshape(-1, 1000), axis=1)
print(query_averages)
insert_averages = np.average(insert_response_times.reshape(-1, 1000), axis=1)

query_plot = pd.DataFrame(columns = ['Number of Operations','Response Time Average'])
insert_plot = pd.DataFrame(columns = ['Number of Operations','Response Time Average'])

for point in range(query_averages.size):
    row = {'Number of Operations': point*1000, 'Response Time Average': query_averages[point]}
    query_plot.loc[len(query_plot.index)] = row

for point in range(insert_averages.size):
    row = {'Number of Operations': point*1000, 'Response Time Average': insert_averages[point]}
    insert_plot.loc[len(insert_plot.index)] = row

plt.plot(query_plot["Number of Operations"],query_plot['Response Time Average'])
plt.savefig("TestQueriesRR.png")

plt.cla()
plt.plot(insert_plot["Number of Operations"],insert_plot['Response Time Average'])
plt.savefig("TestInsertsRR.png")


