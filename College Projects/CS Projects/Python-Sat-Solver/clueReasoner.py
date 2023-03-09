"""ClueReasoner.py - project skeleton for a propositional reasoner
for the game of Clue.  Unimplemented portions have the comment "TO
BE IMPLEMENTED AS AN EXERCISE".  The reasoner does not include
knowledge of how many cards each player holds.
Originally by Todd Neller
Ported to Python by Dave Musicant
Copyright (C) 2019 Dave Musicant
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
Information about the GNU General Public License is available online at:
  http://www.gnu.org/licenses/
To receive a copy of the GNU General Public License, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
02111-1307, USA."""

import SATSolver

# Initialize important variables
caseFile = "cf"
players = ["sc", "mu", "wh", "gr", "pe", "pl"]
extendedPlayers = players + [caseFile]
suspects = ["mu", "pl", "gr", "pe", "sc", "wh"]
weapons = ["kn", "ca", "re", "ro", "pi", "wr"]
rooms = ["ha", "lo", "di", "ki", "ba", "co", "bi", "li", "st"]
cards = suspects + weapons + rooms


def getPairNumFromNames(player, card):
    return getPairNumFromPositions(extendedPlayers.index(player), cards.index(card))


def getPairNumFromPositions(player, card):
    return player * len(cards) + card + 1


def addPairNumCannotBeInTwoPlacesRuleToClauses(clauses, pair_num_lst):
    """
    For each possible pair num, say if it is [A,B,C], we want to add
        (A and -B and -C) or (-A and B and -C) or (-A and -B and C)
    to the clauses. This can be converted to CNF as
       (A or B or C) and (-A or -B) and (-A or -C) and (-B or -C)
    Since the (A or B or C) is already added by the first rule, we only need to add
    the second bit of the CNF which is the combinations of all possible pairing of
    the possible pair numbers to the clauses.
    """
    for i in range(len(pair_num_lst)):
        for j in range(i + 1, len(pair_num_lst)):
            clauses.append([-pair_num_lst[i], -pair_num_lst[j]])


def initialClauses():
    clauses = []

    # Each card is in at least one place (including case file).
    for c in cards:
        clauses.append([getPairNumFromNames(p, c) for p in extendedPlayers])

    # A card cannot be in two places.
    # Iterates through clauses that were added from the previous rule
    for possible_num_pair_from_previous_rule in clauses[-len(clauses) :]:
        addPairNumCannotBeInTwoPlacesRuleToClauses(
            clauses, possible_num_pair_from_previous_rule
        )

    # At least one card of each category is in the case file.
    clauses.append([getPairNumFromNames("cf", s) for s in suspects])
    clauses.append([getPairNumFromNames("cf", w) for w in weapons])
    clauses.append([getPairNumFromNames("cf", r) for r in rooms])

    # No two cards in each category can both be in the case file.
    # Iterates through the last three elements from previous rule
    for possible_num_pair_from_previous_rule in clauses[-3:]:
        addPairNumCannotBeInTwoPlacesRuleToClauses(
            clauses, possible_num_pair_from_previous_rule
        )

    return clauses


def hand(player, cards):
    clauses = []
    clauses.extend([[getPairNumFromNames(player, c)] for c in cards])
    clauses.extend([[-getPairNumFromNames("cf", c)] for c in cards])
    return clauses


SUGGESTED_PLAYER = 0
REFUTER_PLAYER = 1
PASSED_PLAYER = 2
NOT_REACHED_PLAYER = 3


def getPlayerStatusAfterSuggest(index, suggester_index, refuter_index):
    """Returns numbers representing the position of the given index after the suggestion"""
    if index == suggester_index:
        return SUGGESTED_PLAYER
    elif refuter_index != None and index == refuter_index:
        return REFUTER_PLAYER
    elif refuter_index != None and (
        (suggester_index < index and index < refuter_index)
        or (
            # Suggester loops back to front of list to Refuter
            refuter_index < suggester_index
            and (suggester_index < index or index < refuter_index)
        )
    ):
        return PASSED_PLAYER
    else:
        return NOT_REACHED_PLAYER


def addClausesForRefuterPlayer(clauses, index, cardShown, suggested_cards):
    if cardShown != None:
        # Know the card this player shows
        clauses.append([getPairNumFromNames(players[index], cardShown)])
    else:
        # Know that this player has one of the suggested cards
        clauses.append(
            [getPairNumFromNames(players[index], c) for c in suggested_cards]
        )


def addClausesForPassedPlayer(clauses, index, suggested_cards):
    # Know that this player doesn't own any of the suggested cards
    clauses.extend([[-getPairNumFromNames(players[index], c)] for c in suggested_cards])


def suggest(suggester, card1, card2, card3, refuter, cardShown):
    clauses = []
    suggested_cards = [card1, card2, card3]

    suggester_index = players.index(suggester)
    refuter_index = players.index(refuter) if refuter != None else None

    # Check what to do for each player
    for i in range(len(players)):
        cur_player_status = getPlayerStatusAfterSuggest(
            i, suggester_index, refuter_index
        )

        # Ignore if current player is the suggester or is unreached
        if cur_player_status == REFUTER_PLAYER:
            addClausesForRefuterPlayer(clauses, i, cardShown, suggested_cards)
        elif cur_player_status == PASSED_PLAYER:
            addClausesForPassedPlayer(clauses, i, suggested_cards)

    return clauses


def accuse(accuser, card1, card2, card3, isCorrect):
    accused_cards = [card1, card2, card3]

    # Accuser does not have any of the card
    pair_nums_not_on_accuser_hand = [
        [-getPairNumFromNames(accuser, c)] for c in accused_cards
    ]

    if isCorrect:
        # All the cards are in the case file
        res = [
            [getPairNumFromNames("cf", c)] for c in accused_cards
        ] + pair_nums_not_on_accuser_hand
        return res

    # At least one of the cards is not in the case file
    res = [
        [-getPairNumFromNames("cf", c) for c in accused_cards]
    ] + pair_nums_not_on_accuser_hand
    return res


def query(player, card, clauses):
    return SATSolver.testLiteral(getPairNumFromNames(player, card), clauses)


def queryString(returnCode):
    if returnCode == True:
        return "Y"
    elif returnCode == False:
        return "N"
    else:
        return "-"


def printNotepad(clauses):
    for player in players:
        print("\t", player, end=" ")
    print("\t", caseFile)
    for card in cards:
        print(card, "\t", end=" ")
        for player in players:
            print(queryString(query(player, card, clauses)), "\t", end=" ")
        print(queryString(query(caseFile, card, clauses)))


def playClue():
    clauses = initialClauses()
    clauses.extend(hand("sc", ["wh", "li", "st"]))
    clauses.extend(suggest("sc", "sc", "ro", "lo", "mu", "sc"))
    clauses.extend(suggest("mu", "pe", "pi", "di", "pe", None))
    clauses.extend(suggest("wh", "mu", "re", "ba", "pe", None))
    clauses.extend(suggest("gr", "wh", "kn", "ba", "pl", None))
    clauses.extend(suggest("pe", "gr", "ca", "di", "wh", None))
    clauses.extend(suggest("pl", "wh", "wr", "st", "sc", "wh"))
    clauses.extend(suggest("sc", "pl", "ro", "co", "mu", "pl"))
    clauses.extend(suggest("mu", "pe", "ro", "ba", "wh", None))
    clauses.extend(suggest("wh", "mu", "ca", "st", "gr", None))
    clauses.extend(suggest("gr", "pe", "kn", "di", "pe", None))
    clauses.extend(suggest("pe", "mu", "pi", "di", "pl", None))
    clauses.extend(suggest("pl", "gr", "kn", "co", "wh", None))
    clauses.extend(suggest("sc", "pe", "kn", "lo", "mu", "lo"))
    clauses.extend(suggest("mu", "pe", "kn", "di", "wh", None))
    clauses.extend(suggest("wh", "pe", "wr", "ha", "gr", None))
    clauses.extend(suggest("gr", "wh", "pi", "co", "pl", None))
    clauses.extend(suggest("pe", "sc", "pi", "ha", "mu", None))
    clauses.extend(suggest("pl", "pe", "pi", "ba", None, None))
    clauses.extend(suggest("sc", "wh", "pi", "ha", "pe", "ha"))
    clauses.extend(suggest("wh", "pe", "pi", "ha", "pe", None))
    clauses.extend(suggest("pe", "pe", "pi", "ha", None, None))
    clauses.extend(suggest("sc", "gr", "pi", "st", "wh", "gr"))
    clauses.extend(suggest("mu", "pe", "pi", "ba", "pl", None))
    clauses.extend(suggest("wh", "pe", "pi", "st", "sc", "st"))
    clauses.extend(suggest("gr", "wh", "pi", "st", "sc", "wh"))
    clauses.extend(suggest("pe", "wh", "pi", "st", "sc", "wh"))
    clauses.extend(suggest("pl", "pe", "pi", "ki", "gr", None))
    print("Before accusation: should show a single solution.")
    printNotepad(clauses)
    print()
    clauses.extend(accuse("sc", "pe", "pi", "bi", True))
    print("After accusation: if consistent, output should remain unchanged.")
    printNotepad(clauses)


# =======================================================================================
# ====================================== Test Code ======================================
# =======================================================================================

# Got this from https://stackoverflow.com/questions/287871/how-do-i-print-colored-text-to-the-terminal
BOLD = "\033[1m"
OKGREEN = "\033[92m"
FAIL = "\033[91m"
ENDC = "\033[0m"


def printTest(name, is_true, expected):
    if is_true == expected:
        print(f"{BOLD :<5}Test{ENDC :<5}{name :<100}: {OKGREEN}Passed{ENDC}")
    else:
        print(f"{BOLD :<5}Test{ENDC :<5}{name :<100}: {FAIL}Failed{ENDC}")


def areClausesCorrect(name, test_clauses, correct_clauses):
    """Sorts inner lists then outer lists and check if equal"""
    is_equal = sorted(map(sorted, test_clauses)) == sorted(map(sorted, correct_clauses))
    printTest(name, is_equal, True)
    if not is_equal:
        print(f"Have:\n{test_clauses}\nShould be\n{correct_clauses}")


def areClausesSatisfiable(test_clauses, clauses):
    copy_clauses = clauses.copy()
    copy_clauses.extend(test_clauses)
    return SATSolver.testKb(copy_clauses)


def testInitialClauses():
    clauses = initialClauses()

    printTest("Initial Clauses Satisfiable", SATSolver.testKb(clauses), True)
    printTest(
        "Not Satisfiable When Card Not In Any Place",
        areClausesSatisfiable(
            [[-getPairNumFromNames(p, "sc")] for p in extendedPlayers], clauses
        ),
        False,
    )
    printTest(
        "Not Satisfiable When Same Card In Two Places",
        areClausesSatisfiable(
            [[getPairNumFromNames("sc", "sc")], [getPairNumFromNames("mu", "sc")]],
            clauses,
        ),
        False,
    )
    printTest(
        "Not Satisfiable When No Card In A Category Is In Case File",
        areClausesSatisfiable(
            [[-getPairNumFromNames("cf", s)] for s in suspects],
            clauses,
        ),
        False,
    )
    printTest(
        "Not Satisfiable When Same Category Cards In Case File",
        areClausesSatisfiable(
            [[getPairNumFromNames("cf", "sc")], [getPairNumFromNames("cf", "mu")]],
            clauses,
        ),
        False,
    )


def testHand():
    areClausesCorrect(
        "Hand Clauses Have Only Cards On Hand And Not In Case File",
        hand("sc", ["mu", "re", "st"]),
        [
            [getPairNumFromNames("sc", "mu")],
            [getPairNumFromNames("sc", "re")],
            [getPairNumFromNames("sc", "st")],
            [-getPairNumFromNames("cf", "mu")],
            [-getPairNumFromNames("cf", "re")],
            [-getPairNumFromNames("cf", "st")],
        ],
    )


def testSuggest():
    areClausesCorrect(
        "Suggest Clauses Have Only Class Shown",
        suggest("sc", "", "", "", "mu", "ha"),
        [[getPairNumFromNames("mu", "ha")]],
    )
    areClausesCorrect(
        "Suggest Clauses Have Speculation On One Of The Suggested Cards",
        suggest("sc", "mu", "ca", "bi", "mu", None),
        [
            [
                getPairNumFromNames("mu", "mu"),
                getPairNumFromNames("mu", "ca"),
                getPairNumFromNames("mu", "bi"),
            ],
        ],
    )
    areClausesCorrect(
        "Suggest Clauses Have Passed Player Not Have Suggested Cards",
        suggest("sc", "mu", "ca", "ha", "wh", "ha"),
        [
            [getPairNumFromNames("wh", "ha")],
            [-getPairNumFromNames("mu", "mu")],
            [-getPairNumFromNames("mu", "ca")],
            [-getPairNumFromNames("mu", "ha")],
        ],
    )
    areClausesCorrect(
        "Suggest Clauses Have Passed Player Not Have Suggested Cards Start At The End Of List",
        suggest("pl", "mu", "ca", "ha", "mu", "ha"),
        [
            [getPairNumFromNames("mu", "ha")],
            [-getPairNumFromNames("sc", "mu")],
            [-getPairNumFromNames("sc", "ca")],
            [-getPairNumFromNames("sc", "ha")],
        ],
    )


def testAccuse():
    areClausesCorrect(
        "Accuse Clauses Correct",
        accuse("sc", "pe", "pi", "bi", True),
        [
            [getPairNumFromNames("cf", "pe")],
            [getPairNumFromNames("cf", "pi")],
            [getPairNumFromNames("cf", "bi")],
            [-getPairNumFromNames("sc", "pe")],
            [-getPairNumFromNames("sc", "pi")],
            [-getPairNumFromNames("sc", "bi")],
        ],
    )

    areClausesCorrect(
        "Accuse Clauses Not Correct",
        accuse("sc", "pe", "pi", "bi", False),
        [
            [
                -getPairNumFromNames("cf", "pe"),
                -getPairNumFromNames("cf", "pi"),
                -getPairNumFromNames("cf", "bi"),
            ],
            [-getPairNumFromNames("sc", "pe")],
            [-getPairNumFromNames("sc", "pi")],
            [-getPairNumFromNames("sc", "bi")],
        ],
    )


if __name__ == "__main__":
    run_test = True
    if run_test:
        testInitialClauses()
        testHand()
        testSuggest()
        testAccuse()
    else:
        playClue()