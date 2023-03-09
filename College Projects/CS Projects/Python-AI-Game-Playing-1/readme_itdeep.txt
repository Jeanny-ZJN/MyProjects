Use >>> python itdeep.py to run the test code.

We used the random_state function in puzzle8.py to randomly generate states with num_moves = 15.
We tested our iterative deepening search algorithm on the randomly generated states 5 times.

We added print statements to keep track of the time required to complete a dfs at each specified 
level and the time required to finish the entire iterative deepening. 

Note that by having "timer" for dfs on each level, we are also slowing down the iterative deepening's
"timer" by a bit. This may cause some inconsistency in the timing, when we have to go very deep into
the solution path. (Itdeep might take more time than it should if the solution is found at a high level.)

Here are the results (in millisec):

    Last Iteration         Total         Total # of Levels
1.       40.797           143.725               8
2.     1243.916          5709.190              12
3.       22.499            31.235               6
4.        0.0830            0.160               2
5.       21.126            35.461               6

As shown, since the experiment is timed in milisec, the time it takes to run the last iteration vs. in 
total are not drastically different.