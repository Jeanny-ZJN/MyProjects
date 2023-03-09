import puzzle8 as p8
from typing import List
import time


should_report_time = False

def iterative_deepening_search(state: int) -> List[int]:
    """Finds path to solution via iterative deepening. Returns a list of
    squares that the blank moves to in order to get to solution.
    """
    time_before_itdeep = time.time()

    cur_max_level = 1

    # there will always be a solution for a puzzle8, so can keep running
    while True:
        time_before_dfs = time.time()
        output = dfs(state, 0, cur_max_level, [])

        if should_report_time:
            print(f'Time to run dfs at max level {cur_max_level} is {(time.time() - time_before_dfs) * 10**3} millisec')

        # return when a solution is found
        if output:

            if should_report_time:
                print(f'Time to run itdeep {(time.time() - time_before_itdeep) * 10**3} millisec')

            return output
        cur_max_level += 1


def dfs(state, cur_level, max_level, path):
    # no solution is found when the max level is reached
    if cur_level == max_level:
        return []

    if state == p8.solution():
        return path

    blank_square = p8.blank_square(state)
    neighbors = p8.neighbors(blank_square)

    path.append(state) # saves visited states

    # iterate through the neighbors recursively
    for neighbor in neighbors:
        next_state = p8.move_blank(state, neighbor)
        res = dfs(next_state, cur_level+1, max_level, path)
        # break out of recursion when a solution is found
        if res:
            return res
    
    path.pop() # discards a state that doesn't lead to soln
    return [] # can't find anything


if __name__ == "__main__":
    # experiment the run time of the last iteration of itdeep (dfs) to 
    # the entire itdeep's
    should_report_time = True
    for _ in range(5):
        iterative_deepening_search(p8.random_state(15))
