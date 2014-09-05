import os
import sys
import re
				
def update_project():
	rev = os.popen("android list").read()
	m = re.search(r'id:\s*([0-9]*)\s*or.*(android-18).*', rev)
	if m == None:
		print "We need android-18"
		return False
	id = m.group(1)
	os.system("android update project -p . --target " + id)
	return True
	
def main():
	if os.path.exists("build.xml") == False:
		print "=====android update project========="	
		if update_project() == False:
			return
	if os.path.exists("build.xml") == False:
		print "update project failed. please check your sdk"	
	debug = False

	print "=====Building========="
	if debug:
		os.system("ant clean && ant debug")
	else:
		os.system("ant clean && ant release")
	
if __name__ == "__main__":
	main()	
