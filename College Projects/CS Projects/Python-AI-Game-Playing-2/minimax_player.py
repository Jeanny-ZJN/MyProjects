"""Minimax game player. You should modify the choose_move code for the
MinimaxPlayer class. You should also modify the heuristic function, which
should return a number indicating the value of that board position.

Feel free to add additional helper methods or functions.
"""

from __future__ import annotations
from game_board import GameBoard, Location
from typing import Optional, Callable
from player import Player
from common_values import EMPTY, MAX_PLAYER, YELLOW, RED
import math


def is_legal_move_for_a_piece_in_second_stage(board, location, piece) -> bool:
    """Returns True or False for whether a move is legal for
    the given player in a second stage.
    """
    row = location.row
    col = location.column

    # A move cannot be made if a piece is already there.
    if board.grid[row, col] != EMPTY:
        return False

    # only legal when there are at least 2 or more neighbors
    if board.num_adjacent_friendlies(location, piece) < 2:
        return False

    return True


def get_all_legal_moves_for_a_player_in_second_stage(board, player) -> int:
    """Returns an integer of the number of all legal moves for
    the given player.
    """
    sum = 0
    for row in range(1, board.size + 1):
        for col in range(1, board.size + 1):
            if is_legal_move_for_a_piece_in_second_stage(
                board, Location(row, col), player
            ):
                sum += 1
    return sum


def get_num_legal_moves_for_red_and_yellow_in_second_stage(board) -> tuple(int, int):
    """Returns two intergers that represent all legal moves for
    both RED and YELLOW players
    """
    return get_all_legal_moves_for_a_player_in_second_stage(
        board, RED
    ), get_all_legal_moves_for_a_player_in_second_stage(board, YELLOW)


def heuristic(board: GameBoard) -> float:
    """
    This heuristic calculates the number of legal moves for both YELLOW (MIN)
    and RED (MAX) players on a given board. When the number of legal moves
    for the MAX player is larger than that for the MIN player, the heuristic
    returns a positive number between 0 and 1. When the number of legal moves
    for the MIN player is larger than that for the MAX player, the heuristic
    returns a negative number between -1 and 0. If the heuristic is 1, it
    means that MIN player can no longer make any legal move (MAX player is
    guaranteed to win) and vice versa.
    """

    (
        num_legal_moves_max,
        num_legal_moves_min,
    ) = get_num_legal_moves_for_red_and_yellow_in_second_stage(board)

    if num_legal_moves_max == 0 and num_legal_moves_min == 0:
        return 0

    if board.is_terminal():
        return -1 if board.get_active_player() == RED else 1

    return (num_legal_moves_max - num_legal_moves_min) / (
        num_legal_moves_max + num_legal_moves_min
    )


class MinimaxPlayer(Player):
    """Minimax player: uses minimax to find the best move."""

    def __init__(self, heuristic: Callable[[GameBoard], float], plies: int) -> None:
        self.heuristic = heuristic
        self.plies = plies

    def choose_move(self, board: GameBoard) -> Optional[Location]:
        if board.is_terminal():
            return None

        # call different functions for different players
        active_player = board.get_active_player()
        if active_player == MAX_PLAYER:
            _, best_location = self.max_values(board, 0, -math.inf, math.inf)
            return best_location
        else:
            _, best_location = self.min_values(board, 0, -math.inf, math.inf)
            return best_location

    def max_values(self, board: GameBoard, current_plies: int, alpha: int, beta: int):
        if current_plies == self.plies:
            # will know the location from where this function had been
            # recursively called
            return self.heuristic(board), None

        # define variables
        best_h = -math.inf
        best_location = None

        # iterate through all the possible moves
        for possible_move in board.get_legal_moves():
            board_copy = board.make_move(possible_move)
            # mutual recursion
            local_best_h, _ = self.min_values(board_copy, current_plies + 1, alpha, beta)

            # keep track of best_h and best_location
            if local_best_h > best_h:
                best_h = local_best_h
                best_location = possible_move

                # alpha-beta pruning
                if best_h >= beta:
                    return best_h, possible_move

                # update local alpha
                alpha = max(alpha, best_h)

        return best_h, best_location

    def min_values(self, board: GameBoard, current_plies: int, alpha: int, beta: int):
        if current_plies == self.plies:
            # will know the location from where this function had been
            # recursively called
            return self.heuristic(board), None

        # define variables
        best_h = math.inf
        best_location = None

        # iterate through all the possible moves
        for possible_move in board.get_legal_moves():
            board_copy = board.make_move(possible_move)
            # mutual recursion
            local_best_h, _ = self.max_values(board_copy, current_plies + 1, alpha, beta)

            # keep track of best_h and best_location
            if local_best_h < best_h:
                best_h = local_best_h
                best_location = possible_move

                # alpha-beta pruning
                if best_h <= alpha:
                    return best_h, possible_move

                # update local beta
                beta = min(beta, best_h)

        return best_h, best_location
