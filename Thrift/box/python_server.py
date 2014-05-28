#!/usr/bin/env python

import sys
sys.path.append('./gen-py')

from myservice import MyService
from myservice.ttypes import *

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer
import urllib2

users = []

class MyServiceHandler:
	def __init__(self):
		pass
		#self.log = {}

	def get_htmlPage(self, htmlUrl):
		try:
			response = urllib2.urlopen(htmlUrl)
			html = response.read()
		except:
			raise MyException('eception')
		return html

handler = MyServiceHandler()
#print handler.get_htmlPage("http://python.org")
processor = MyService.Processor(handler)
#transport = TSocket.TServerSocket(port=3146)
transport = TSocket.TServerSocket("0.0.0.0", 3146)
tfactory = TTransport.TBufferedTransportFactory()
pfactory = TBinaryProtocol.TBinaryProtocolFactory()

server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

# You could do one of these for a multithreaded server
#server = TServer.TThreadedServer(processor, transport, tfactory, pfactory)
#server = TServer.TThreadPoolServer(processor, transport, tfactory, pfactory)

print('Starting the server...')
server.serve()
print('done.')