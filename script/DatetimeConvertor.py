from datetime import datetime

# 21 days ==> 1824400 seconds
INPUT_TIME_RANGE_IN_SECS = 1824400

# 20 minutes ==> 1200000 mseconds
OUPUT_TIME_RANGE_IN_MSECS = 1200000

convert_ratio = OUPUT_TIME_RANGE_IN_MSECS / INPUT_TIME_RANGE_IN_SECS


BEG_DATETIME_STRING = "2017-11-07T23:59:00Z"
BEG_timestamp = datetime.strptime(BEG_DATETIME_STRING, '%Y-%m-%dT%H:%M:%SZ').timestamp()


"""
Input format:2017-11-07T23:59:00Z, 
  - Input Datetime SHOULD be 
    earlier than 2017-11-28T23:59:00Z and 
    later than   2017-11-07T23:59:00Z
Ouput format: a int in range [0, OUPUT_TIME_RANGE_IN_MSECS] inclusive, 

To use this function: 
    from DatetimeConvertor import datetime_to_msec
"""
def datetime_to_msec(datetime_string: str):
    datetime_timestamp = datetime.strptime(datetime_string, '%Y-%m-%dT%H:%M:%SZ').timestamp()
    return round((datetime_timestamp - BEG_timestamp) * convert_ratio)