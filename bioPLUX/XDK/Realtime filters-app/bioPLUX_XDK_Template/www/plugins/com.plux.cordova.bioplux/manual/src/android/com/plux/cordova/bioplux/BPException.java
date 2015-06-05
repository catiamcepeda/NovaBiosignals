package org.apache.cordova.bioplux;

public class BPException extends java.lang.Exception {
   public BPException(BPErrorTypes errorType)
   {
      super(errorType.getName());
      code = errorType.getValue();
   }
   public int code;
}
