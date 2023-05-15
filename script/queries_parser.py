from DatetimeConvertor import datetime_to_msec

output_file = open("../ParsedQueries/high_concurrency/parsed_queries_high.txt", "w")
# output_file = open("parsed_queries.txt", "w")


with open("../queries/high_concurrency/queries.txt", "r") as file:
# with open("sample.txt", "r") as file:
    is_date_line = True
    is_sql_lines = False

    sql_string = ""

    for line in file:
        line = line.strip()
        if line == "\"":
            is_date_line = True
            is_sql_lines = False
            if sql_string:
                output_file.write(sql_string)
                output_file.write("\n")
                # print(sql_string)
            continue

        if is_date_line:
            output_file.write(str(datetime_to_msec(line[:-2])))
            output_file.write("$")
            # print(round((datetime_timestamp - BEG_timestamp) * convert_ratio))
            is_date_line = False
            is_sql_lines = True
            sql_string = ""
            continue


        if is_sql_lines:
            sql_string += line + " "

print("done")