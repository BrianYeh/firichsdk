package firich.com.firichsdk;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by nelson on 2016/3/18.
 */
public class SerialPort extends Thread{
    boolean blnClassRun = true;
    public interface SerialPortListener {
        public void onDataReceive(byte[] btyData);
    }
    private SerialPortListener listener;
    private int intPortHandle = -1;
    private boolean blnRunning = false;
    boolean blnStartLsten = false;
    private boolean bDebugOn = true;
    private byte[] bytDataReceived = new byte[256];
    private int intDataReceivedLen=0;

    private void dump_trace(String bytTag, String bytTrace)
    {
        if (bDebugOn)
        Log.d(bytTag, bytTrace);
    }
    public static String hex(int n) {
        // call toUpperCase() if that's required
        return String.format("0x%4s", Integer.toHexString(n)).replace(' ', '0');
    }

    public void setListener(SerialPortListener listener) {
        Log.d("SerialPort.setListener","blnStartLsten=true");
        this.listener = listener;
        blnStartLsten = true;
    }
    private void setDataReceived(byte[] bytDataReceive, int intDataLen)
    {
        dump_trace("SerialPort.setDataReceive",": bytDataReceive ="+new String(bytDataReceive));
        bytDataReceived =  Arrays.copyOf(bytDataReceive,intDataLen);
        intDataReceivedLen = intDataLen;
        dump_trace("SerialPort.setDataReceive",": bytDataReceived ="+new String(bytDataReceived)+ "; intDataReceivedLen="+ intDataReceivedLen);
    }
    public byte[] getBytDataReceived()
    {
        return bytDataReceived;
    }
    public int getDataReceivedLength()
    {
        return intDataReceivedLen;
    }

    public int open(String path, int baudrate) {
        int intReturnCode = -1;
        intReturnCode = openSerialPort(path, baudrate);
        intPortHandle = intReturnCode;
        dump_trace("SerialPort.open", "intPortHandle="+intPortHandle);
        blnRunning = true;
        if (this.getState() == Thread.State.NEW)
        {
            this.start();
            Log.d("SerialPort.open", "first run");
        }
        return intReturnCode;
    }
    public int openGPIO(String path, int bWrite) {
        int intReturnCode = -1;
        intReturnCode = openGPIOPort(path, bWrite);
        intPortHandle = intReturnCode;
        dump_trace("SerialPort.openGPIO", "intPortHandle="+intPortHandle);
        blnRunning = true;
        if (this.getState() == Thread.State.NEW)
        {
            this.start();
            Log.d("SerialPort.openGPIO", "first run");
        }
        return intReturnCode;
    }
    public int openSmartCard(String path, int baudrate) {
        int intReturnCode = -1;
        dump_trace("SerialPort.SerialPortSmartCardTest", "start");
       // intReturnCode = openSerialPortSmartCardTest(path, baudrate);
        intReturnCode = openSerialPortSmartCard(path, baudrate);
        intPortHandle = intReturnCode;
        dump_trace("SerialPort.openSmartCard", "intPortHandle test LRC="+hex(intPortHandle));

         blnRunning = true;
        if (this.getState() == Thread.State.NEW)
        {
            this.start();
            dump_trace("SerialPort.openSmartCard", "first run");
        }

        return intReturnCode;
    }

    public void close(int intSerialPortHandle) {
        if (blnRunning) {
            blnStartLsten = false;
            blnRunning = false;
            closeSerialPort(intSerialPortHandle);
            blnClassRun = false;
        }
    }
    public int write(int intSerialPortHandle, byte[] btyData)
    {
        int intReturnCode = -1;
        dump_trace("SerialPort.write", "intSerialPortHandle="+intSerialPortHandle);
        if (blnRunning) {
            Log.d("SerialPort.write",new String(btyData));
            intReturnCode = writeSerialPort(intSerialPortHandle, btyData);
        }
        return intReturnCode;
    }
    public void run() {
        while (blnClassRun) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dump_trace("SerialPort.run","idle");
            if (blnRunning) {
                if (blnStartLsten) {
                    dump_trace("SerialPort.run","listening");
                    int intLen = readSerialPort(intPortHandle);
                    dump_trace("SerialPort.run","readSerialPort; intLen="+intLen+"; intPortHandle="+ intPortHandle);
                    if (intLen>0) {
                        Log.d("intLen",String.valueOf(intLen));
                        byte[] btyTmp = getReceiveData(intLen);
                        dump_trace("SerialPort.run","getReceiveData; btyTmp="+new String(btyTmp));
                        setDataReceived(btyTmp, intLen);

                        listener.onDataReceive(btyTmp);
                    }
                }
            }
        }
        Log.d("SerialPort.run","destroy");

    }

    // JNI
    static {
        System.loadLibrary("rs232");
    }
    private native int openSerialPort(String path, int baudrate);
    private native int openGPIOPort(String path, int bWrite);
    private native int openSerialPortSmartCard(String path, int baudrate);
    private native int openSerialPortSmartCardTest(String path, int baudrate);
    private native void closeSerialPort(int intSerialPortHandle);
    private native int writeSerialPort(int intSerialPortHandle, byte[] btyData);
    private native int readSerialPort(int intSerialPortHandle);
    private native byte[] getReceiveData(int intReceiveDataLength);
}
