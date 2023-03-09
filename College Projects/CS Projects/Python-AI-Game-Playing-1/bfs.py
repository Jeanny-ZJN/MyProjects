import puzzle8 as p8
from typing import List
from collections import deque


def breadth_first_search(state: int) -> List[int]:
    """Finds path to solution via breadth-first search. Returns a list of
    squares that the blank moves to in order to get to solution.
    """
    #raise NotImplementedError("You must implement this method")

    # define variables
    past_states = deque()
    all_neighbors = deque()
    all_paths = deque()
    visited_states = set()

    # prepare for the first iteration
    past_states.append(state)
    blank_square = p8.blank_square(state)
    neighbors = p8.neighbors(blank_square)
    all_neighbors.append(neighbors)
    all_paths.append([])

    # BFS
    while all_neighbors:
        # get relavent variables
        current_state = past_states.popleft()
        next_neighbor = all_neighbors.popleft()
        current_path = all_paths.popleft()

        # check if the current state is visited
        if current_state in visited_states:
            continue
        else:
            visited_states.add(current_state)

        # visit each neighbor
        for neighbor in next_neighbor:
            # move tile
            next_state = p8.move_blank(current_state, neighbor)
            # save state
            past_states.append(next_state)
            # save current path
            current_path_copy = current_path.copy()
            current_path_copy.append(neighbor)

            # check if the goal is reached
            if next_state == p8.solution():
                return current_path_copy

            # prepare for the next iteration
            blank_square = p8.blank_square(next_state)
            neighbors = p8.neighbors(blank_square)
            all_neighbors.append(neighbors)
            all_paths.append(current_path_copy)

    return []
