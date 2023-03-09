from SATSolver import testKb, testLiteral

if __name__ == '__main__':
    # build knowledge base
    clauses = [[-1,3],[-2, -3], [2, 3], [-1, 2, -3], [-2, 3], [1, 3]]
    print('Knowledge base is satisfiable:',testKb(clauses))

    # check if Amy is a truthteller
    print('Is Amy a truth-teller?', end=' ')
    result1 = testLiteral(1, clauses)
    if result1==True:
        print('Yes.')
    elif result1==False:
        print('No.')
    else:
        print('Unknown.')

    # check if Bob is a truthteller
    print('Is Bob a truth-teller?', end=' ')
    result2 = testLiteral(2, clauses)
    if result2==True:
        print('Yes.')
    elif result2==False:
        print('No.')
    else:
        print('Unknown.')
        
    # check if Cal is a truthteller
    print('Is Cal a truth-teller?', end=' ')
    result3 = testLiteral(3, clauses)
    if result3==True:
        print('Yes.')
    elif result3==False:
        print('No.')
    else:
        print('Unknown.')