import sys

import wikipedia
from bs4 import BeautifulSoup


def remove_non_ascii(text):
    return ''.join(i for i in text if ord(i) < 128)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print 'no summary found'
        sys.exit(0)
    query = ''
    for i in range(1, len(sys.argv)):
        query += sys.argv[i] + ' '


    try:
        result = wikipedia.summary(query.lower())[0:5000]
        print remove_non_ascii(result)
    except wikipedia.DisambiguationError as e:
        print 'Sorry,', query, 'may refer to: '
        print ','.join(e.options)
        print 'Please query us again with one of these options.'
    except wikipedia.exceptions.PageError as e:
        print "Sorry, we couldn't find the specified page. Please try another query!"
