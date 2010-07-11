from sys import *
from os import *
from os.path import *
from urllib import *

release = argv[1]
dir = release.replace(".", "_")
mkdir(dir)

for classifier in [ "bin", "source-release" ]:
	for suffix in [ "zip", "zip.asc", "zip.md5"]:
		file = "axiom-" + release + "-" + classifier + "." + suffix
		urlretrieve("http://repository.apache.org/content/repositories/releases/org/apache/ws/commons/axiom/axiom/" + release + "/" + file, join(dir, file))

