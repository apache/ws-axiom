from subprocess import *
from os import *
from os.path import *
from sys import *
from shutil import *

srcroot = argv[1]
dstroot = argv[2]

svndirs = set()
svnfiles = set()
proc = Popen([ "svn", "status", "-v", dstroot ], stdout=PIPE)
for line in proc.stdout.readlines():
	if line[0] != "?" and line[0] != "D":
		path = line[5:].split()[3]
		item = relpath(path, dstroot)
		if item != ".":
			if isdir(path):
				svndirs.add(item)
			else:
				svnfiles.add(item)

def scan(arg, directory, files):
	global srcroot
	global dstroot
	global svndirs
	global svnfiles
	dir = relpath(directory, srcroot)
	if dir == ".":
		dir = ""
	else:
		if dir in svndirs:
			svndirs.remove(dir)
		else:
			dstpath = join(dstroot, dir)
			mkdir(dstpath)
			call(["svn", "add", dstpath])
		
	for file in files:
		file = join(dir, file)
		srcpath = join(srcroot, file)
		if not isdir(srcpath):
			dstpath = join(dstroot, file)
			copyfile(srcpath, dstpath)
			if file in svnfiles:
				svnfiles.remove(file)
			else:
				call(["svn", "add", dstpath])

walk(srcroot, scan, 0)

for file in svnfiles:
	call(["svn", "remove", join(dstroot, file)])

for dir in svndirs:
	call(["svn", "remove", join(dstroot, dir)])
