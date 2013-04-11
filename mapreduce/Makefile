JFLAGS =
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	FilePartition.java \
	MasterSlaveMessage.java \
	FileRecordReader.java \
    SlaveHost.java \
	SlaveConnection.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	
