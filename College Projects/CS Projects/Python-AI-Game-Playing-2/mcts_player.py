"""MCTS game player starter code. Fill in the methods as indicated below.

Feel free to add additional helper methods or functions.

Originally based on work by:
@author Bryce Wiedenbeck
@author Anna Rafferty
@author Dave Musicant
"""

from __future__ import annotations
import random
from game_board import GameBoard, Location
from typing import Optional
from player import Player
import math


class MctsPlayer(Player):
    """Uses MCTS to find the best move.

    Plays random games from the root node to a terminal state. In each rollout,
    play proceeds according to UCB while all children have been expanded. The
    first node with unexpanded children has a random child expanded. After
    expansion, play proceeds by selecting uniform random moves. Upon reaching a
    terminal state, values are propagated back along the expanded portion of
    the path. After all rollouts are completed, the move generating the highest
    value child of root is returned.
    """

    def __init__(self, rollouts, ucb_const):
        self.rollouts = rollouts
        self.ucb_const = ucb_const

    def choose_move(self, board: GameBoard) -> Optional[Location]:
        root = MctsNode(board, None, self.ucb_const)
        return root.choose_move_via_mcts(self.rollouts)


class MctsNode:
    """Node used in MCTS. It is a wrapper to contain a board/state as a node
    within a tree."""

    def __init__(self, state: GameBoard, parent: Optional[MctsNode],
                 ucb_const: float) -> None:
        """Constructor for a new node representing game state
        state. parent_node is the Node that is the parent of this
        one in the MCTS tree.
        """

        self.state = state
        self.parent = parent
        self.ucb_const = ucb_const

        # All of the known children for this node. To get to each child, a move
        # (specificed by a Location) is used.
        self.children: dict[Location, MctsNode] = {}

        # Stats of games played out from this node, from the perspective of the
        # player at this node.
        self.wins_for_this_player = 0
        self.total_games_for_this_player = 0

        # All legal moves that can me made from this node; useful to have once
        # to avoid recalculating later. Your code will be faster if you use
        # this value rather than calculating it when you need it.
        self.legal_moves = self.state.get_legal_moves()

        # You may add additional fields if needed below.


    def get_win_percentage_if_chosen_by_parent(self) -> float:
        """Gets the win percentage for the current node, from the perspective
        of the parent node that is trying to decide whether or not to select
        this node.

        You will need this for computing the UCB weight when doing rollouts,
        and also for making the final choice on which move to make.
        """

        return (self.total_games_for_this_player - self.wins_for_this_player)/self.total_games_for_this_player


    def get_UCB_weight_from_parent_perspective(self) -> float:
        """Weight from the UCB formula for this node, when used by its parent
        to select a node proportionally to its weight. The win percentage
        aspect of this formula must be from the parent's perspective, since
        that is the node making the decision.

        You will need to use this as part of the selection phase when doing
        rollouts.
        """

        win_percentage = self.get_win_percentage_if_chosen_by_parent()

        return win_percentage + self.ucb_const*math.sqrt((math.log(self.parent.total_games_for_this_player)/self.total_games_for_this_player))


    def update_play_counts(self, outcome: int) -> None:
        """Updates the total games played from this node, as well as the number
        of wins from this node for the current player.

        You will need this for backpropagating wins/losses back up the tree.

        outcome: +1 for 1st player win, -1 for 2nd player win.
        """

        if self.state.get_active_player() == outcome:
            self.wins_for_this_player += 1
        
        self.total_games_for_this_player += 1


    def choose_move_via_mcts(self, rollouts: int) -> Optional[Location]:
        """Select a move by Monte Carlo tree search. Plays rollouts random
        games from the root node to a terminal state. In each rollout, play
        proceeds according to UCB while all children have been expanded. The
        first node with unexpanded children has a random child expanded. After
        expansion, play proceeds by selecting uniform random moves. Upon
        reaching a terminal state, values are propagated back along the
        expanded portion of the path. After all rollouts are completed, the
        move generating the highest value child of root is returned.

        Returns None if no legal moves are available. If rollouts is 0, returns
        a random choice from the legal moves.

        You will undoubtedly want to use helper functions when writing this,
        both some that I've provided, as well as helper functions of your own.
        """

        # return None when the state is already terminal
        if self.state.is_terminal():
            return None

        # return random move if rollouts is 0
        if rollouts == 0:
            return random.choice(self.legal_moves)

        for _ in range(rollouts):
            node = self

            # selection
            while len(node.legal_moves) == len(node.children) and not node.state.is_terminal():
                ucb1 = lambda n: n.get_UCB_weight_from_parent_perspective()
                node = max(node.children.values(), key=ucb1)

            # expansion
            if not node.state.is_terminal():
                # randomly pick a move that is not visited
                legal_moves = list(set(node.legal_moves) - set(node.children.keys()))
                move = random.choice(legal_moves)
                new_board = node.state.make_move(move)
                # create a new node for the new board
                new_node = MctsNode(new_board, node, node.ucb_const)
                # save the new node in children
                node.children[move] = new_node
                # simulation
                outcome = self.random_playout(new_node)
                # update node to new node
                node = new_node
            else:
                outcome = node.state.value()

            # backpropagation
            while node:
                node.update_play_counts(outcome)
                node = node.parent

        # return the move with the highest winning rate
        highest_win_pct = lambda n: self.children[n].get_win_percentage_if_chosen_by_parent()
        return max(self.children.keys(), key=highest_win_pct)


    def random_playout(self, node: MctsNode) -> int:
        '''Randomly makes the next move until a terminal state is found.
        Returns the outcome of the game.
        '''

        current_state = node.state

        # keep playing until a terminal state is found
        while not current_state.is_terminal():
            move = current_state.get_random_legal_move()
            current_state = current_state.make_move(move)
        
        # return the outcome of the game
        return current_state.value()
