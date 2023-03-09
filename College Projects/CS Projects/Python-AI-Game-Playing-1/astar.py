import puzzle8 as p8
import heapq
from typing import List


def num_wrong_tiles(state) -> int:
    """Given a puzzle, returns the number of tiles that are in the wrong
    location. Does not count the blank space.
    """

    # define variable
    wrong_tile = 0

    # iterate through all tiles
    for i in range(9):
        current_tile = p8.get_tile(state, i)

        # skip blank space
        if current_tile == 0:
            continue

        # check if the current tile is the same as the correct tile
        correct_tile = p8.get_tile(p8._goal, i)
        if current_tile != correct_tile:
            wrong_tile += 1

    return wrong_tile


def correct_xy_location(square):
    '''Return the correct (x,y) location of a square number, where x
    represents the column number, and y represents the row number.'''
    return [(1, 1), (0, 0), (1, 0),
            (2, 0), (2, 1), (2, 2),
            (1, 2), (0, 2), (0, 1)][square]


def manhattan_distance(state) -> int:
    """Given a puzzle, returns the Manhattan distance to the solution state.
    Does not count the distance from blank space to its correct location as
    part of the distance.
    """

    # define variable
    distance = 0

    # iterate through all tiles
    for i in range(9):
        current_tile = p8.get_tile(state, i)

        # skip the blank space
        if current_tile == 0:
            continue

        # get the xy location for the current tile and its correct location
        current_location = p8.xy_location(i)
        correct_location = correct_xy_location(current_tile)

        # calculate the distance between the two locations
        distance += sum(map(lambda i, j: abs(i - j), current_location, correct_location))

    return distance


def astar_search(state: int, heuristic) -> List[int]:
    """Finds path to solution via A* search, using the provided heuristic.
    Returns a list of squares that the blank moves to in order to get to
    solution.
    """
    
    # define variables
    pq = []
    visited_states = set()

    # initialize the priority queue
    heapq.heappush(pq, (0, 0, state, []))

    # A* search
    while pq:
        # get the node with the lowest f(n)
        _, g, current_state, current_path = heapq.heappop(pq)

        # check if the current state is visited
        if current_state in visited_states:
            continue
        
        # get neighbors
        blank_square = p8.blank_square(current_state)
        neighbors = p8.neighbors(blank_square)

        # iterate through the neighbors
        for neighbor in neighbors:
            # check heuristic
            proposed_state = p8.move_blank(current_state, neighbor)
            h = heuristic(proposed_state)

            # keep track of current path
            current_path_copy = current_path.copy()
            current_path_copy.append(neighbor)

            # solution is found when heuristic = 0
            if h == 0:
                return current_path_copy

            heapq.heappush(pq, (h+g, g+1, proposed_state, current_path_copy))

    # no solution
    return []
