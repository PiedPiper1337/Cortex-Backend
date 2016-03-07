from googlesearch import GoogleSearch
from readability.readability import Document
from bs4 import BeautifulSoup
import re
import sys
import requests
import urllib

def remove_non_ascii(text):
    return ''.join(i for i in text if ord(i)<128)


if __name__ == "__main__":
	if len(sys.argv) < 2:
		print 'no urls found'
		sys.exit(0)
	query = ''
        for i in range (1,len(sys.argv)):
                query += sys.argv[i] + ' '
	gs = GoogleSearch(query)
	if len(gs.top_urls()) < 1:
		print 'no urls found'
		sys.exit(0)
	urls = gs.top_urls()
#	for url in urls:
#		print url
	html = urllib.urlopen(urls[0]).read()
	soup = BeautifulSoup(Document(html).summary(), "lxml")
	print remove_non_ascii(soup.get_text()[0:1000])
