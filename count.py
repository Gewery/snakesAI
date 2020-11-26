file = open('output.txt', 'r')

gen = -1
lines = 0
for line in file.readlines():
    if line[0:11] == 'Generation:':
        print('Generation: ' + str(gen) + ' : ' + str(lines - 2) + ' nodes')
        lines = 0
        gen += 1
    else:
        lines += 1
