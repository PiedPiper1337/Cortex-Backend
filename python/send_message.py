from googlevoice import Voice
import sys
import json
import time

if __name__ == "__main__":
    # if len(sys.argv) != 2:
    #     print 'no file given'

    recipient = None
    replies = None

    # print sys.argv[1]

    with open(sys.argv[1], 'r') as f:
        recipient = f.readline()
        replies = f.readline()

    list = json.loads(replies)['array']
    # print list

    voice = Voice()
    voice.login()
    for reply in list:
        voice.send_sms(recipient, reply)
        time.sleep(5)