import wikipedia
import sys

if __name__ == "__main__":
	if len(sys.argv) < 2:
		print 'no summary found'
		sys.exit(0)
	query = ''
	for i in range (1,len(sys.argv)):
		query += sys.argv[i] + ' '
	print wikipedia.summary(query.lower())
