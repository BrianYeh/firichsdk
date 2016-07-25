package firich.com.firichsdk;

import android.util.Log;

/**
 * Created by nelson on 2016/3/29.
 */
public class SunComm {
    byte[] btyReceiveData0;
    byte[] btyReceiveData1;
    byte[] btyReceiveData2;
    byte[] btyReceiveData3;
    byte[] btyReceiveData4;
    byte[] btyReceiveData5;
    byte[] btyReceiveData6;
    byte[] btyReceiveData7;

    private byte[] getBtyReceiveDataByIndex(int index) {
        byte[] btyTmp = null;
        switch (index) {
            case 0 :
                btyTmp = btyReceiveData0;
            case 1 :
                btyTmp = btyReceiveData1;
            case 2 :
                btyTmp = btyReceiveData2;
            case 3 :
                btyTmp = btyReceiveData3;
            case 4 :
                btyTmp = btyReceiveData4;
            case 5 :
                btyTmp = btyReceiveData5;
            case 6 :
                btyTmp = btyReceiveData6;
            case 7 :
                btyTmp = btyReceiveData7;
        }
        return btyTmp;
    }

    private SerialPort.SerialPortListener splistener0 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData0 = btyData;
            Log.d("onDataReceive0", new String(btyData));
        }
    };
    private SerialPort.SerialPortListener splistener1 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData1 = btyData;
            Log.d("onDataReceive1", new String(btyData));
        }
    };
    private SerialPort.SerialPortListener splistener2 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData2 = btyData;
            Log.d("onDataReceive2", new String(btyData));
        }
    };
    private SerialPort.SerialPortListener splistener3 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData3 = btyData;
            Log.d("onDataReceive3", new String(btyData));
        }
    };
    private SerialPort.SerialPortListener splistener4 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData4 = btyData;
            Log.d("onDataReceive4", new String(btyData));
        }
    };
    private SerialPort.SerialPortListener splistener5 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData5 = btyData;
            Log.d("onDataReceive5", new String(btyData));
        }
    };
    private SerialPort.SerialPortListener splistener6 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData6 = btyData;
            Log.d("onDataReceive6", new String(btyData));
        }
    };
    private SerialPort.SerialPortListener splistener7 = new SerialPort.SerialPortListener() {
        public void onDataReceive(byte[] btyData){
            btyReceiveData7 = btyData;
            Log.d("onDataReceive7", new String(btyData));
        }
    };
    SerialPort sp[] = new SerialPort[8];
    int intSerialHandle[] = new int[8];
    public int CommOpen(int index, int com_port) {
//        [說明] 開啟Com Port。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. Com_Port 為要使用的Port /dev/ttyUSBx。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 開啟 “ttyUSB3”, 其以後的使用代號為 0。
//
//        CommOpen(0, 3);
//        [備註] DLL總共可同時開啟 8 個 Com Port. 其使用代號範圍為 0 – 7。
        sp[index] = new SerialPort();
        switch (index) {
            case 0 :
                sp[index].setListener(splistener0);
            case 1 :
                sp[index].setListener(splistener1);
            case 2 :
                sp[index].setListener(splistener2);
            case 3 :
                sp[index].setListener(splistener3);
            case 4 :
                sp[index].setListener(splistener4);
            case 5 :
                sp[index].setListener(splistener5);
            case 6 :
                sp[index].setListener(splistener6);
            case 7 :
                sp[index].setListener(splistener7);
        }

        int intReturnCode = sp[index].open("/dev/ttyUSB" + String.valueOf(com_port),9600);

        intSerialHandle[index] = intReturnCode;
        return  intReturnCode;
    }
    public int CommClose(int index) {
//        [說明] 關閉Com Port。
//        [參數] index 為此 Com Port的使用代號。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 關閉使用代號為 0 的 Com Port。
//
//        CommClose(0)
        sp[index].close(intSerialHandle[index]);
        int intReturnCode = 0;
        return  intReturnCode;
    }
    public int CommVersion(int index, StringBuffer version) {
//        [說明] 讀取讀卡機的軟體版本。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. version存放讀卡機的軟體版本。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 讀取讀卡機的軟體版本。
//
//        char version[15]
//
//        CommVersion(0, version);
//
//        [備註] 讀卡機的軟體版本格式為 V1.01 MD-150M。
        int intReturnCode = 0;
        byte[] btyCmd = new byte[8];

        sp[index].write(intSerialHandle[index], btyCmd);

        byte[] btyReceiveData = getBtyReceiveDataByIndex(index);
        String strVersion = "";

        version.setLength(0);
        version.append("V1.01 MD-150M");
        return  intReturnCode;
    }
    public int CommTagIDAutoGet(int index) {
//        [說明] 設定讀卡機為立即傳回卡號模式。
//        [參數] index 為此 Com Port的使用代號。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 設定讀卡機為立即傳回卡號模式。
//
//        CommTagIDAutoGet(0);
//
//        [備註] 立即傳回卡號模式 - 是指讀卡機讀到卡號即立刻主動傳回電腦。
//        可隨時利用 CommRece() 接收卡號。
        int intReturnCode = 0;
        return  intReturnCode;
    }
    public int CommTagIDGet(int index, StringBuffer tag_id, int second) {
//        [說明] 設定讀卡機為連線傳回卡號模式, 且取得已讀到卡號。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. tag_id 傳回的卡號。
//        3. second 卡號保留時間。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 設定讀卡機為連線傳回卡號模式, 且傳回已讀到卡號。
//
//        char tag_id[20];
//
//        CommTagIDGet(0, tag_id, 2);
//
//        [備註] 連線傳回卡號模式 - 是指讀卡機讀到卡會暫時保留卡號, 等電腦下達
//        CommTagIDGet() 函數時, 才將卡號傳回。
//
//        卡號保留時間 - 是指讀卡機讀到卡要暫時保留卡號多久, 此時若
//        電腦一直不來讀回, 則放棄此卡號, 再重新讀卡。
        int intReturnCode = 0;
        return  intReturnCode;
    }
    public int CommReadBlock(int index, int block, StringBuffer block_data) {
//        [說明] 讀取卡片某個 block 的資料。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. block 讀取的 block, 範圍 0 – 63。
//        3. block_data 存放在 block 內的資料。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 讀取卡片第 2 block 的資料。
//
//        char block_data[40];
//
//        CommReadBlock(0, 2, block_data);
//
//        [備註] 傳回的 block_data 資料, 第 4 個 byte 以後的資料才是資料內容。
//        如 “M0021234567890ABCDEF1234567890ABCDEF” 。
        int intReturnCode = 0;
        return  intReturnCode;
    }
    public int CommWriteBlock(int index, int block, StringBuffer block_data) {
//        [說明] 寫入卡片某個 block 的資料。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. block 寫入的 block, 範圍 0 – 63。
//        3. block_data 存放在 block 內的資料。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 寫入卡片第 9 block 的資料。
//
//        CommWriteBlock(0,9,“0123456789ABCDEF0123456789ABCDEF”);
//
//
//        [備註] 欲寫入Mifare卡片每個Sector的A,B Key值時需特別注意寫入的資料內容，因為一但寫入成功，此Sector的Key值馬上被改變，而寫入的Key值也無法由卡片裡讀出。
        int intReturnCode = 0;
        return  intReturnCode;
    }
    public int CommSelectKey(int index, int sector) {
//        [說明] 選擇要用來驗證的 Key, 此 Key由 CommLoadKey 事先存於讀卡機中。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. sector 要使用的第幾組 Key。範圍 1 - 32。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 選擇第 3 組 Key驗證。
//
//        CommSelectKey(0, 3);
//
//        [備註] 當sector = 0時，代表將無法再進行卡片的讀寫，除非重新設定要驗證的Key。
        int intReturnCode = 0;
        return  intReturnCode;
    }
    int CommLoadKey(int index, int sector, StringBuffer key_data) {
//        [說明] 設定卡機內部32組Sector的A,B Key值。(永存於內部記憶體)
//                [參數] 1. index 為此 Com Port的使用代號。
//        2. sector 要設定的第幾組 Key。範圍 1 - 32。
//        3. key_data 要設定的 A, B Key 資料。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 設定第 10 組 Key值。
//
//        CommLoadKey(0, 10, “12AB45EF36CC”);
//
//        [功能說明]
//        1.利用此功能可設定讀卡機內每一組Sector的Key 值。
//        2.需使用此功能後才可有效使用Read, Write, Select 等功能。
//        3.當欲修改 Mifare卡內部資料，必須先將該Sector的Key設定(Load)
//        到模組中，然後再選擇(Select)要認證的Key，這樣才能讀(Read)
//        寫(Write) Mifare卡內部資料。
        int intReturnCode = 0;
        return  intReturnCode;
    }
    int CommTempKey(int index, int sector, StringBuffer key_data) {
//        [說明] 設定卡機內部Sector的A (sector = 1), B (sector = 17) Key值。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. sector 要設定的 A Key (sector = 1) or B Key (sector = 17)。
//        3. key_data 要設定的 A or B Key 資料。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [功能] 1.此功能為 LoadKey & SelectKey 的組合。
//        2.關機後此 Key值會消失, 不存於卡機內部記憶體.
        int intReturnCode = 0;
        return  intReturnCode;
    }
    int CommAddValue(int index, int block, StringBuffer value_data) {
//        [說明] 針對卡片內所指定的Block作加值。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. block 選擇要加值的 block, 範圍 0 – 63。
//        3. value_data 欲要增加的數值。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 在卡片第 1 block 加值5。
//
//        CommAddValue(0,1, “00000005”);
        int intReturnCode = 0;
        return  intReturnCode;
    }
    int CommSubValue(int index, int block, StringBuffer value_data) {
//        [說明] 針對卡片內所指定的Block作減值。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. block 選擇要減值的 block, 範圍 0 – 63。
//        3. value_data 欲要減少的數值。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 在卡片第 2 block 減值8。
//
//        CommSubValue(0,2, “00000008”);
        int intReturnCode = 0;
        return  intReturnCode;
    }
    int CommRestoreValue (int index, int sour_block, int dest_block) {
//        [說明] 將卡片內所選擇的X Block資料拷貝到 Y Block。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. sour_block 選擇要拷貝的 block, 範圍 0 – 63。
//        3. dest_block 存放拷貝的 block, 範圍 0 – 63。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 將卡片第 1 block的資料拷貝到第 2 Block內。
//
//        CommBackupValue(1, 2);
//
//        [備註] 1.Block資料拷貝僅限於同一sector。
//        2.Block資料拷貝也不可以使用到Key block。
        int intReturnCode = 0;
        return  intReturnCode;
    }
    int CommBlockRead(int index, int type, int block, String block_data) {
//        [說明] 讀取卡片某個 block 的資料。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. type 讀取的Type,2:TI HF-I Plus 3: TI HF-I Pro 6:I-Code 2 10:Felica
//        3. block 讀取的 block, 範圍 0 – 63。
//        4. block_data 存放在 block 內的資料。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 讀取TI HF-I Plus卡片第 2 block 的資料。
//
//        char block_data[40];
//
//        CommBlockRead (0, 2 ,2, block_data);
        int intReturnCode = 0;
        return  intReturnCode;
    }
    int CommBlockWrite(int index, int type, int block, StringBuffer block_data) {
//        [說明] 寫入卡片某個 block 的資料。
//        [參數] 1. index 為此 Com Port的使用代號。
//        2. type 讀取的Type,2:TI HF-I Plus 3: TI HF-I Pro 6:I-Code 2 10:Felica
//        3. block 寫入的 block, 範圍 0 – 63。
//        4. block_data 存放在 block 內的資料。
//        [回傳] 0 表示失敗, 非 0 成功。
//        [範例] 寫入TI HF-I Plus卡片第 9 block 的資料。
//
//        CommBlockWrite(0, 2, 9, “01234567”);

        int intReturnCode = 0;
        return  intReturnCode;
    }


}
