# CS 223 Project: Transaction Simulator

## VS Code Method
Install VS code, git clone the repository at https://github.com/ashwincolaco1798/transactionsim.git

Install Java extensions for VS code and your favourite jdk version.

Use the play button :smile:

## Manual Method Simulator Installation
To install the simulator, git clone the repository at https://github.com/ashwincolaco1798/transactionsim.git

```sh 
cd transactionsim/transactionsim; mvn install
```

## Run the simulator
```sh 
java -classpath /path/to/install/transactionsim/target/classes:~/.m2/repository/org/postgresql/postgresql/42.6.0/postgresql-42.6.0.jar:/~/.m2/repository/org/checkerframework/checker-qual/3.31.0/checker-qual-3.31.0.jar:~/.m2/repository/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar com.yap.project.App
```

## Graph Generator

Install your favourite python version 3.7 or higher(I use 3.10.x)

Create a venv using ```sh python3 -m venv venv``` or don't at your own risk

Install dependencies by
```sh
cd dataprocessing
pip install -r requirements.txt
python3 processor.py
```
To switch between low and high concurrency graphs use replace and replace Low by High in all places :smile:


