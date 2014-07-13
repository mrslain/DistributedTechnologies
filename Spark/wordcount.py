import sys
from pyspark import SparkContext


sc = SparkContext(sys.argv[1], "PythonWordCount2")

def getKeys(fileName, content):
	lines = content.splitlines()
	keys = []
	for line in lines:
		for word in line.split(' '):
			keys.append((fileName, word))
	return keys

files = sc.wholeTextFiles(sys.argv[2])

countsByFile = files.flatMap(lambda (fileName, content): getKeys(fileName, content)) \
		.map(lambda key : (key, 1)) \
		.reduceByKey(lambda a,b: a + b)

counts = countsByFile.map(lambda ((fileName, word), value): (word, value)) \
		.reduceByKey(lambda a,b: a + b) 

output = counts.collect()

with open("output", "w") as out:
	for (word, count) in output:
		nW = word.encode('utf-8')
		out.write("%s: %i" % (nW, count))
