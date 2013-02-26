JFLAGS =
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	RMIRegistryMessage.java \
	RMIRegistry.java \
	RMIMessage.java \
	Remote440.java \
	RemoteObjectRef.java \
	RemoteStub.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	
