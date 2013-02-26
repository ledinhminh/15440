import java.io.*;
import java.lang.*;

public class RemoteStub implements Remote440 {
    private RemoteObjectRef ror;

    public void setRemoteRef(RemoteObjectRef _ror) {
        ror = _ror;
    }

    public RemoteObjectRef getRemoteRef() {
        return ror;
    }
}
