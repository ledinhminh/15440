JFLAGS =
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	MigratableProcess.java \
	TransactionalFileInputStream.java \
	TransactionalFileOutputStream.java \
	SlaveHost.java \
	SlaveMessage.java \
	ServeThread.java \
	ProcessServer.java \
	ProcessManager.java \
	TestMigratableProcess.java \
	MeterCheckProcess.java \
	RhymeProcess.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	cp ../processes/TestMigratableProcess.java .
	cp ../processes/MeterCheckProcess.java .
	cp ../processes/RhymeProcess.java .
	cp ../interfaces/MigratableProcess.java .
	$(RM) *.class
	
