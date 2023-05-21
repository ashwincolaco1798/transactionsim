from DatetimeConvertor import datetime_to_msec_V2

output_file = open("../ParsedData/high_concurrency/parsed_data_semantic.txt", "w")
# output_file = open("parsed_data.txt", "w")


with open("../data/high_concurrency/semantic_observation_high_concurrency.sql", "r") as file:
# with open("sample.txt", "r") as file:
    for line in file:
        datetime_string_start_index = line.find("2017-11-")
        if datetime_string_start_index != -1:
            output_file.write(str(datetime_to_msec_V2(line[datetime_string_start_index: datetime_string_start_index + 19])))
            output_file.write("$")
            output_file.write(line)
            
output_file.close()
file.close()

print("done")