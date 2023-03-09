The measured run time for my A* search algorithm are:

    Time (s)          Heuristic       Solution Path Length
1.   0.0             wrong_tiles               2
2.   0.0          manhattan_distance           2
3.   0.0             wrong_tiles               4  
4.   0.0          manhattan_distance           4
5.   0.00203         wrong_tiles               8 
6.   0.0          manhattan_distance           8
7.   0.0300          wrong_tiles               10
8.   0.00398      manhattan_distance           10
9.   0.257           wrong_tiles               12
10.  0.0430       manhattan_distance           12
11.  0.798           wrong_tiles               16
12.  0.00400      manhattan_distance           16

It appears that Manhattan distance is a better heuristic as it results in
faster run time. This is due to the fact that Manhattan distance dominates
the number of wrong tiles.

A* search appears to be much faster than iterative deepening when the solution
path length is the same. For example, when the solution path length is 12, 
it takes iterative deepening around 5 seconds to solve the problem, yet it
takes A* search less than 1 second. Although the big-O time complexity is the
same for both algorithms, A* search is a lot faster than iterative deepening
in reality.
