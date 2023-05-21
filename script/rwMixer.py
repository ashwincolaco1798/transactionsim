from pandas import read_csv as pds_read_csv
from pandas import concat as pds_concat

path_R_low  = "../ParsedQueries/low_concurrency/parsed_queries.txt"
path_R_high = "../ParsedQueries/high_concurrency/parsed_queries.txt"

path_W_low_obs  = "../ParsedData/low_concurrency/parsed_data_observation.txt"
path_W_low_sem  = "../ParsedData/low_concurrency/parsed_data_semantic.txt"

path_W_high_obs = "../ParsedData/high_concurrency/parsed_data_observation.txt"
path_W_high_sem = "../ParsedData/high_concurrency/parsed_data_semantic.txt"

df1 = pds_read_csv(path_W_high_obs, header = None, sep = "$", names=["time", "command"], index_col=None)#, nrows=1000)

df2 = pds_read_csv(path_W_high_sem, header = None, sep = "$", names=["time", "command"], index_col=None)#, nrows=1000)

df3 = pds_read_csv(path_R_high, header = None, sep = "$", names=["time", "command"], index_col=None)#, nrows=1000)

sorted_mixed_df = pds_concat([df1, df2, df3]).sort_values(by=["time"])

del df1, df2, df3

print(sorted_mixed_df)
print(sorted_mixed_df.time.is_monotonic_increasing)

path_mixed_low = "../MixedSQLCommand/low_concurrency/mixed_commands.txt"
path_mixed_high = "../MixedSQLCommand/high_concurrency/mixed_commands.txt"

sorted_mixed_df.to_csv(path_mixed_high, sep = "$", header = False, index = False)