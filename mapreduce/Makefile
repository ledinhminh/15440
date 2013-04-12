JFLAGS =
JC = javac
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $./*/*.java

CLASSES = \
        Configuration/Configuration.java \
        Examples/WordCount.java \
        Master/MasterCoordinator.java \
        Master/MasterCommThread.java \
        Master/MasterController.java \
        Master/MasterDispatchThread.java \
        Master/MasterServerThread.java \
        Slave/SlaveCommThread.java \
        Slave/SlaveController.java \
        Slave/SlaveCoordinator.java \
        Slave/SlaveServerThread.java \
        Util/FilePartition.java \
        Util/FileRecordReader.java \
        Util/FileRecordWriter.java \
        Util/MapReduceJob.java \
        Util/NetworkMessage.java \
        Util/ReduceTask.java \
        Util/Task.java \
        
default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) ./*/*.class