JFLAGS =
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	RMIRegistry.java \
	RMIClient1.java \
	RMIMessage.java \
	RMIRegistryMessage.java \
	RMIServer.java \
	RMIServerThread.java \
	Remote440.java \
	Remote440Exception.java \
	RemoteObjectRef.java \
	RemoteStub.java \
	TestRemoteObject.java \
	RemoteStub.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	
