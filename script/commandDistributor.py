from pandas import read_csv as pds_read_csv
from pandas import concat as pds_concat
from pandas import DataFrame

path_mixed_low = "../MixedSQLCommand/low_concurrency/mixed_commands.txt"
path_mixed_high = "../MixedSQLCommand/high_concurrency/mixed_commands.txt"
path_sample = "../MixedSQLCommand/low_concurrency/sample_100.txt"

df_whole = pds_read_csv(path_mixed_low, header = None, sep = "$", names=["time", "command"], index_col=None)#, nrows=1000)


Multi_Programming_Level = 50

for i in range(Multi_Programming_Level):

    df_subset = df_whole.loc[df_whole.index % Multi_Programming_Level == i]

    path = "../MixedSQLCommand/low_concurrency/MPL_" + str(Multi_Programming_Level) + "/Process_" + str(i) + ".txt"
    df_subset.to_csv(path, sep = "$", header = False, index = False)

    print(df_subset.time.is_monotonic_increasing)

del df_whole