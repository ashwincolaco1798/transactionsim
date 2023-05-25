import numpy as np
import pandas as pd
from matplotlib import pyplot as plt


mpl_levels = [5, 50,100,150,200,300]

transaction_sizes = [2,5,10]

isolation_modes = ['READ_COMMITTED', 'REPEATABLE_READ', 'SERIALIZABLE']


for transaction_size in transaction_sizes:
    for isolation_mode in isolation_modes:

        #dataframes
        throughput_dataframe = pd.DataFrame(columns = ['mpl_level','Throughput'])
        query_response_dataframe = pd.DataFrame(columns = ['mpl_level','Query Response Time'])
        insert_response_dataframe = pd.DataFrame(columns = ['mpl_level','Insert Response Time'])

        
        for level in mpl_levels:
            total_throughput = 0
            #arrays
            insert_average = list()
            query_average = list()
        
            # print(level)
            for fileLevels in range(level):
                commit_times = np.loadtxt("/home/yap/tippers/Results/High Concurrency/"+str(transaction_size)+"/MPL_"+str(level)+"/TRANSACTION_"+isolation_mode+"/commitTimeThread_"+str(fileLevels))
                with open("/home/yap/tippers/Results/High Concurrency/"+str(transaction_size)+"/MPL_"+str(level)+"/TRANSACTION_"+isolation_mode+"/insertResponseThread_"+str(fileLevels)) as f:
                    insert_average.append([int(x) for x in f.read().split()])
                with open("/home/yap/tippers/Results/High Concurrency/"+str(transaction_size)+"/MPL_"+str(level)+"/TRANSACTION_"+isolation_mode+"/queryResponseThread_"+str(fileLevels)) as f:
                    query_average.append([int(x) for x in f.read().split()])
                
                if(np.any(commit_times)):
                    thread_throughput = (commit_times.size*1000)/(np.max(commit_times))
                #print(thread_throughput)
                total_throughput += thread_throughput
            
            # print(insert_average)
            # print(query_average)
            query_flat_list = [num for sublist in insert_average for num in sublist]
            insert_flat_list = [num for sublist in query_average for num in sublist]
            if(len(query_flat_list)):
                query_average_value = np.average(query_flat_list)
            else:
                query_average_value = 0
            if(len(insert_flat_list)):
                insert_average_value = np.average(insert_flat_list)
            else:
                insert_average_value = 0
            

            throughput_row = {'mpl_level': level, 'Throughput': total_throughput}
            query_response_row = {'mpl_level': level, 'Query Response Time': query_average_value}
            insert_response_row = {'mpl_level': level, 'Insert Response Time': insert_average_value}

            throughput_dataframe.loc[len(throughput_dataframe.index)] = throughput_row
            query_response_dataframe.loc[len(query_response_dataframe.index)] = query_response_row
            insert_response_dataframe.loc[len(insert_response_dataframe.index)] = insert_response_row

        plt.scatter(throughput_dataframe['mpl_level'], throughput_dataframe['Throughput'])
        plt.xlabel('MPL Level')
        plt.ylabel('Throughput in Transactions/Second')
        plt.title("Throughput vs MPL for Transaction size "+str(transaction_size)+" and Isolation Mode "+isolation_mode)
        plt.savefig("/home/yap/tippers/dataprocessing/Graphs High Concurrency/Throughput vs MPL "+str(transaction_size)+" and Isolation Mode "+isolation_mode+".png",bbox_inches='tight', dpi=300)
        plt.close()
        # print(query_response_dataframe)
        # print(insert_response_dataframe)
        plt.scatter(query_response_dataframe['mpl_level'], query_response_dataframe['Query Response Time'])
        plt.xlabel('MPL Level')
        plt.ylabel('Average response time in ms')
        plt.title("Query Response Time vs MPL for Transaction size "+str(transaction_size)+" and Isolation Mode "+isolation_mode)
        plt.savefig("/home/yap/tippers/dataprocessing/Graphs High Concurrency/Average Query Response Time vs MPL "+str(transaction_size)+" and Isolation Mode "+isolation_mode+".png",bbox_inches='tight', dpi=300)
        plt.close()
        plt.scatter(insert_response_dataframe['mpl_level'], insert_response_dataframe['Insert Response Time'])
        plt.xlabel('MPL Level')
        plt.ylabel('Average response time in ms')
        plt.title("Insert Response Time vs MPL for Transaction size "+str(transaction_size)+" and Isolation Mode "+isolation_mode)
        plt.savefig("/home/yap/tippers/dataprocessing/Graphs High Concurrency/Average Insert Response Time vs MPL "+str(transaction_size)+ " and Isolation Mode "+isolation_mode+".png",bbox_inches='tight', dpi=300)
        plt.close()






