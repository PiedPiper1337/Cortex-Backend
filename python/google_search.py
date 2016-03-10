import sys
import urllib

from bs4 import BeautifulSoup
from googlesearch import GoogleSearch
from readability.readability import Document


def remove_non_ascii(text):
    return ''.join(i for i in text if ord(i) < 128)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print 'no urls found'
        sys.exit(0)

    query = ''
    for i in range(1, len(sys.argv)):
        query += sys.argv[i] + ' '

    gs = GoogleSearch(query)
    if len(gs.top_urls()) < 1:
        print 'no urls found'
        sys.exit(0)
    urls = gs.top_urls()

    #	for url in urls:
    #		print url

    try:
        html = urllib.urlopen(urls[0]).read()
        soup = BeautifulSoup(Document(html).summary(), "lxml")
        print remove_non_ascii(soup.get_text()[0:10000])
    except:
        print 'Sorry cortex had an error retrieving your result'

