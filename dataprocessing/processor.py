import numpy as np
import pandas as pd
from matplotlib import pyplot as plt

mpl_levels = [5,50,100,150,200,300]

throughput_dataframe = pd.DataFrame(columns = ['mpl_level','Throughput'])

for level in mpl_levels:
    commitTimes = np.loadtxt("/home/yap/tippers/project1/MixedSQLCommand/low_concurrency/MPL_"+str(level)+"/Read_Committed/commitTimesMerged")
    throughput = (commitTimes.size*1000*level)/(np.max(commitTimes))
    row = {'mpl_level': level, 'Throughput': throughput}
    throughput_dataframe.loc[len(throughput_dataframe.index)] = row

plt.scatter(throughput_dataframe['mpl_level'], throughput_dataframe['Throughput'])
plt.savefig("Throughput vs MPL")



