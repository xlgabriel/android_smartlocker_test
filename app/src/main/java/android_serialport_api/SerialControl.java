package android_serialport_api;

import android.util.Log;

import com.aituolink.smartbox.Public;

import java.nio.file.OpenOption;
import java.security.KeyStore;
import java.util.HashMap;

public class SerialControl extends SerialHelper {

    //锁板类型
    public enum LockBoardType
    {
        ATAT,  //锁板类型 不带红外
        ATZL,  //锁板类型 带红外
        ATJM  //锁板类型  不带红外
    }

    //电子锁状态
    public enum LockStatus
    {
        Close,  //默认是关闭，
        Open   //打开
    }

    //箱子是否为空
    public enum IsEmpty
    {
        Empty,   //默认是空，
        NoEmpty   //不为空
    }


    //错误类型
    public enum ErrorType
    {
        No_Error,                     // No Error
        Unknown_Error,              // Unknown Error
        No_Init,                    // No Init
        Invalid_Parameter,          // Invalid Parameter
        Invalid_COM_PORT,           // Invalid COM PORT
        Exception_Error,            // Exception Error
        Communication_Error,        // Communication Error
        Communication_Timeout,      // Communication Timeout
        Invalid_Lock_Board_Type     // Invalid Lock Board Type
     }

    //返回值
    public class ResultMsg{

        public ResultMsg(ErrorType errType)
        {
            this.errType = errType;
            this.errMsg = "";
            this.value = null;
        }
        public ResultMsg(ErrorType errType,String errMsg)
        {
            this.errType = errType;
            this.errMsg = ((errMsg==null)? "" : errMsg);
            this.value = null;
        }
        public ResultMsg(ErrorType errType,String errMsg,Object object)
        {
            this.errType = errType;
            this.errMsg = ((errMsg==null)? "" : errMsg);
            this.value = object;
        }

        private ErrorType errType;
        private String    errMsg = "";
        private Object    value = null;

        public ErrorType getErrType() {
            return errType;
        }

        public void setErrType(ErrorType errType) {
            this.errType = errType;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }

        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * 获取最后错误信息
         * @return 错误信息
         */
        public String getErrMsgById(ErrorType errorId)
        {
            switch (errorId)
            {
                case No_Error:
                    return "No Error";
                case Unknown_Error:
                    return "Unknown Error";
                case No_Init:
                    return "No Init";
                case Invalid_Parameter:
                    return "Invalid Parameter";
                case Exception_Error:
                    return "Exception Error";
                case Invalid_COM_PORT:
                    return "Invalid COM PORT";
                case Communication_Error:
                    return "Communication Error";
                case Communication_Timeout:
                    return "Communication Timeout";
                case Invalid_Lock_Board_Type:
                    return "Invalid Lock Board Type";
                default:
                    return "Unknown Error";
            }
        }
    }

    public SerialControl( ){

    }

    public static final int MAX_BOX_NUM = 15; //最大支持15块锁板  1 - 15
    public static final int MAX_DOOR_NUM = 50; //最大支持20门     1 - 20

    private static final long DeFaultDelayTime = 3000; //默认延迟时间


    private boolean lastRevMSGIsOk = false; //是否有数据到达
    private String lastRevMSG;                //最后一次接收串口数据

    private boolean initDll = false;     //是否初始化

    private LockBoardType lockBoardType = LockBoardType.ATAT; //锁板类型


    /**
     * 消息检查
     * @param delayTime 延时等待
     * @return 是否成功
     */
    private boolean GetReceivedMsg(long delayTime)
    {
        long nNum = delayTime / 10;

        while (nNum >= 0)
        {
            if (lastRevMSGIsOk)
            {
                return true;
            }

            nNum--;

            try {
                Thread.sleep(10);
            }catch (Exception e){

            }
        }

        return false;
    }

    public ResultMsg Init(String port, LockBoardType type)
    {
        //如果初始化，不再初始化
        if (initDll)
        {
            return new ResultMsg(ErrorType.No_Init);
        }

        lockBoardType = type;

        if (port == null || port.length() == 0)
        {
            return new ResultMsg(ErrorType.Invalid_COM_PORT);
        }

        this.setPort(port);

        //---- 打开串口
        try{
            this.open();
        }catch (Exception e){
            return new ResultMsg(ErrorType.Invalid_COM_PORT,e.getMessage());
        }

        initDll = true;
        return new ResultMsg(ErrorType.No_Error);
    }

    /**
     * 查询锁板是否存在
     * @param box
     * @param delayTime
     * @return
     */
    public ResultMsg hasBoard(int box, long delayTime)
    {
        lastRevMSGIsOk = false;

        if (!initDll)
        {
            return new ResultMsg(ErrorType.No_Init);
        }

        if (lockBoardType == LockBoardType.ATZL)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
        else if (lockBoardType == LockBoardType.ATJM)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
        else if (lockBoardType == LockBoardType.ATAT)
        {
            return hasBoardAT(box, delayTime);
        }
        else
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
    }
    private ResultMsg hasBoardAT(int box, long delayTime)
    {

        if (box < 1 || box > MAX_BOX_NUM)
        {
            return new ResultMsg(ErrorType.Invalid_Parameter);
        }


        StringBuilder sb = new StringBuilder();

        int nByte1 = 0x5A; //固定
        int nByte2 = 0x5A; //固定
        int nByte3 = 0x00; //固定  源地址
        int nByte4 = box;  //      目的地址
        int nByte5 = 0x00;  //固定 序号
        int nByte6 = 0x02;  //     命令 查询
        int nByte7 = 0x00;  //固定 结果
        int nByte8 = 0x00;  //     数据长度
        int nByteXor = nByte3 ^ nByte4 ^ nByte5 ^ nByte6 ^ nByte7 ^ nByte8;  //     数据

        sb.append(String.format("%02X", nByte1));
        sb.append(String.format("%02X", nByte2));
        sb.append(String.format("%02X", nByte3));
        sb.append(String.format("%02X", nByte4));
        sb.append(String.format("%02X", nByte5));
        sb.append(String.format("%02X", nByte6));
        sb.append(String.format("%02X", nByte7));
        sb.append(String.format("%02X", nByte8));
        sb.append(String.format("%02X", nByteXor));

        String strCMD = sb.toString();
        try
        {
            sendHex(strCMD);
        }
        catch (Exception ex)
        {
            return new ResultMsg(ErrorType.Exception_Error,ex.getMessage());
        }
        //-----------------------

        boolean bOK = GetReceivedMsg(delayTime);
        if (!bOK) //如果没有返回消息，超时
        {
            return new ResultMsg(ErrorType.Communication_Timeout);
        }

        if (lastRevMSG.length() <18)
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        // 18 24位锁板
        // 02命令

        //5A5A010000020001181A
        if (!lastRevMSG.substring(0, 2).equals("5A")
                || !lastRevMSG.substring(2, 2+2).equals("5A")
                || !lastRevMSG.substring(12, 12+2).equals("00")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        //Invalid_Parameter
        if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("00")
        )
        {

        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("01")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("02")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication data error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("03")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Lock Board firmware error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("04")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Opening another lock");
        }
        else
        {
            return new ResultMsg(ErrorType.Unknown_Error);
        }


        //5A5A010000020001181A
        if (lastRevMSG.length() != 20)
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        int count = 0;

        try{
            count  = Integer.parseInt(lastRevMSG.substring(16, 16+2),16);

        }  catch (Exception e){

        }

        return new ResultMsg(ErrorType.No_Error,"",count);
    }


    /**
     * 开箱
     * @param box 柜子锁板编号
     * @param door 柜子电磁锁编号
     * @param delayTime 命令超时等待
     * @return 0成功，其他失败
     */
    public ResultMsg OpenBox(int box, int door, long delayTime){

        lastRevMSGIsOk = false;

        if (!initDll)
        {
            return new ResultMsg(ErrorType.No_Init);
        }

        if (lockBoardType == LockBoardType.ATZL)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
        else if (lockBoardType == LockBoardType.ATJM)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
        else if (lockBoardType == LockBoardType.ATAT)
        {
            return OpenBoxAT(box, door, delayTime);
        }
        else
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
    }

    private ResultMsg OpenBoxAT(int box, int door, long delayTime){

        if (box < 1 || box > MAX_BOX_NUM)
        {
            return  new ResultMsg(ErrorType.Invalid_Parameter);
        }

        if (door < 1 || door > MAX_DOOR_NUM)
        {
            return  new ResultMsg(ErrorType.Invalid_Parameter);

        }

        StringBuilder sb = new StringBuilder();

        int nByte1 = 0x5A; //固定
        int nByte2 = 0x5A; //固定
        int nByte3 = 0x00; //固定  源地址
        int nByte4 = box;  //      目的地址
        int nByte5 = 0x00;  //固定 序号
        int nByte6 = 0x04;  //     命令 开锁
        int nByte7 = 0x00;  //固定 结果
        int nByte8 = 0x01;  //     数据长度
        int nByte9 = door;  //     数据
        int nByteXor = nByte3 ^ nByte4 ^ nByte5 ^ nByte6 ^ nByte7 ^ nByte8 ^ nByte9;  //     数据

        sb.append(String.format("%02X", nByte1));
        sb.append(String.format("%02X", nByte2));
        sb.append(String.format("%02X", nByte3));
        sb.append(String.format("%02X", nByte4));
        sb.append(String.format("%02X", nByte5));
        sb.append(String.format("%02X", nByte6));
        sb.append(String.format("%02X", nByte7));
        sb.append(String.format("%02X", nByte8));
        sb.append(String.format("%02X", nByte9));
        sb.append(String.format("%02X", nByteXor));

        //5A5A0001000400010105
        String strCMD = sb.toString();
        try
        {
            Log.i("LockTest", ">> " + strCMD);
            sendHex(strCMD);
        }
        catch (Exception ex)
        {
            return  new ResultMsg(ErrorType.Exception_Error,ex.getMessage());
        }
        //-----------------------

        boolean bOK = GetReceivedMsg(delayTime);
        if (!bOK) //如果没有返回消息，超时
        {
            return new ResultMsg(ErrorType.Communication_Timeout);
        }

        Log.i("LockTest", "<< " + lastRevMSG);

        if (lastRevMSG.length() <18)
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        // 00 开门 02 锁门 11 锁没接

        //5A5A010000040002010107
        //5A5A010000040002020005
        //5A5A01000004020007

        //Invalid_Parameter
        if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("00")
        )
        {
            //5A5A010000040002010107
            if (lastRevMSG.length() != 22)
            {
                return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
            }
            else
            {
                return new ResultMsg(ErrorType.No_Error);
            }
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("01")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("02")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication data error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("03")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Lock Board firmware error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("04")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Opening another lock");
        }
        else
        {
            return new ResultMsg(ErrorType.Unknown_Error);
        }
    }


    /**
     * 查询箱门状态
     * @param box
     * @param door
     * @param delayTime
     * @return
     */
    public ResultMsg IsBoxOpen(int box, int door,  long delayTime)
    {
        lastRevMSGIsOk = false;

        if (!initDll)
        {
            return new ResultMsg(ErrorType.No_Init);
        }

        if (lockBoardType == LockBoardType.ATZL)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
        else if (lockBoardType == LockBoardType.ATJM)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
        else if (lockBoardType == LockBoardType.ATAT)
        {
            return IsBoxOpenAT(box, door, delayTime);
        }
        else
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
    }

    private ResultMsg IsBoxOpenAT(int box, int door, long delayTime)
    {

        if (box < 1 || box > MAX_BOX_NUM)
        {
            return new ResultMsg(ErrorType.Invalid_Parameter);
        }

        if (door < 1 || door > MAX_DOOR_NUM)
        {
            return new ResultMsg(ErrorType.Invalid_Parameter);
        }

        StringBuilder sb = new StringBuilder();

        int nByte1 = 0x5A; //固定
        int nByte2 = 0x5A; //固定
        int nByte3 = 0x00; //固定  源地址
        int nByte4 = box;  //      目的地址
        int nByte5 = 0x00;  //固定 序号
        int nByte6 = 0x06;  //     命令 开锁
        int nByte7 = 0x00;  //固定 结果
        int nByte8 = 0x01;  //     数据长度
        int nByte9 = door;  //     数据
        int nByteXor = nByte3 ^ nByte4 ^ nByte5 ^ nByte6 ^ nByte7 ^ nByte8 ^ nByte9;  //     数据

        sb.append(String.format("%02X", nByte1));
        sb.append(String.format("%02X", nByte2));
        sb.append(String.format("%02X", nByte3));
        sb.append(String.format("%02X", nByte4));
        sb.append(String.format("%02X", nByte5));
        sb.append(String.format("%02X", nByte6));
        sb.append(String.format("%02X", nByte7));
        sb.append(String.format("%02X", nByte8));
        sb.append(String.format("%02X", nByte9));
        sb.append(String.format("%02X", nByteXor));

        String strCMD = sb.toString();
        try
        {
            sendHex(strCMD);
        }
        catch (Exception ex)
        {
            return new ResultMsg(ErrorType.Exception_Error,ex.getMessage());
        }
        //-----------------------

        boolean bOK = GetReceivedMsg(delayTime);
        if (!bOK) //如果没有返回消息，超时
        {
            return new ResultMsg(ErrorType.Communication_Timeout);
        }

        if (lastRevMSG.length() <18)
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        // 00 开门 02 锁门 11 锁没接
        //5A5A010000060002020007
        if (!lastRevMSG.substring(0, 2).equals("5A")
                || !lastRevMSG.substring(2, 2+2).equals("5A")
                || !lastRevMSG.substring(12, 12+2).equals("00")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        //Invalid_Parameter
        if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("00")
        )
        {

        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("01")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("02")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication data error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("03")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Lock Board firmware error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("04")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Opening another lock");
        }
        else
        {
            return new ResultMsg(ErrorType.Unknown_Error);
        }


        //5A5A010000060002030006
        //5A5A010000060002020007
        //5A5A010000060002010105
        if (lastRevMSG.length() != 22)
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        LockStatus result;

        if (lastRevMSG.substring(18, 18+2).equals("00"))
        {
            result = LockStatus.Close;
        }
        else
        {
            result = LockStatus.Open;
        }

        return new ResultMsg(ErrorType.No_Error,"",result);
    }

    /**
     * 查询箱门所有状态
     * @param box 柜子锁板编号
     * @param delayTime 命令超时等待
     * @return
     */
    public ResultMsg IsBoxsOpen(int box,   long delayTime)
    {
        lastRevMSGIsOk = false;

        if (!initDll)
        {
            return new ResultMsg(ErrorType.No_Init);
        }

        if (lockBoardType == LockBoardType.ATZL)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);

        }
        else if (lockBoardType == LockBoardType.ATJM)
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
        else if (lockBoardType == LockBoardType.ATAT)
        {
            return IsBoxsOpenAT(box, delayTime);
        }
        else
        {
            return new ResultMsg(ErrorType.Invalid_Lock_Board_Type);
        }
    }
    private ResultMsg IsBoxsOpenAT(int box, long delayTime)
    {
        if (box < 1 || box > MAX_BOX_NUM)
        {
            return new ResultMsg(ErrorType.Invalid_Parameter);
        }

        StringBuilder sb = new StringBuilder();

        int nByte1 = 0x5A; //固定
        int nByte2 = 0x5A; //固定
        int nByte3 = 0x00; //固定  源地址
        int nByte4 = box;  //      目的地址
        int nByte5 = 0x00;  //固定 序号
        int nByte6 = 0x07;  //     命令 开锁
        int nByte7 = 0x00;  //固定 结果
        int nByte8 = 0x00;  //     数据长度
        //int nByte9 = door;  //     数据
        //int nByteXor = nByte3 ^ nByte4 ^ nByte5 ^ nByte6 ^ nByte7 ^ nByte8 ^ nByte9;  //     数据
        int nByteXor = nByte3 ^ nByte4 ^ nByte5 ^ nByte6 ^ nByte7 ^ nByte8;  //     数据

        sb.append(String.format("%02X", nByte1));
        sb.append(String.format("%02X", nByte2));
        sb.append(String.format("%02X", nByte3));
        sb.append(String.format("%02X", nByte4));
        sb.append(String.format("%02X", nByte5));
        sb.append(String.format("%02X", nByte6));
        sb.append(String.format("%02X", nByte7));
        sb.append(String.format("%02X", nByte8));
        //sb.append(String.format("%02X", nByte9));
        sb.append(String.format("%02X", nByteXor));

        String strCMD = sb.toString();


        try
        {
            sendHex(strCMD);
        }
        catch (Exception ex)
        {
            return new ResultMsg(ErrorType.Exception_Error,ex.getMessage());
        }
        //-----------------------

        boolean bOK = GetReceivedMsg(delayTime);
        if (!bOK) //如果没有返回消息，超时
        {
            return new ResultMsg(ErrorType.Communication_Timeout);
        }


        if (lastRevMSG.length() <18)
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        //Invalid_Parameter
        if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("00")
        )
        {

        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("01")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("02")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication data error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("03")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Lock Board firmware error");
        }
        else    if (lastRevMSG.substring(0, 2).equals("5A")
                && lastRevMSG.substring(2, 2+2).equals("5A")
                && lastRevMSG.substring(12, 12+2).equals("04")
        )
        {
            return new ResultMsg(ErrorType.Communication_Error,"Opening another lock");
        }
        else
        {
            return new ResultMsg(ErrorType.Unknown_Error);
        }

        //5A5A010000060002030006
        //5A5A010000060002020007
        //5A5A010000060002010105
        if (lastRevMSG.length() != 22 && lastRevMSG.length() != 24 && lastRevMSG.length() != 32)
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }

        if (lastRevMSG.substring(14, 14+2).equals("02") && lastRevMSG.length() == 22)
        {
            LockStatus result[] = new LockStatus[MAX_DOOR_NUM];
            for(int i = 0 ; i < MAX_DOOR_NUM; i++)
            {
                result[i] = LockStatus.Close;
            }
            for (int i = 0; i < 2; i++)
            {
                String sResult = Public.HextoBinary(lastRevMSG.substring(16 + i * 2, 16 + i * 2+2));
                {
                    //0 锁住 0 默认锁住 1 打开
                    for (int j = 0; j < 8; j++)
                    {
                        if (sResult.substring(8 - j - 1, 8 - j - 1+1).equals("0"))
                        {
                            result[i * 8 + j] = LockStatus.Close;
                        }
                        else
                        {
                            result[i * 8 + j] = LockStatus.Open;
                        }
                    }
                }
            }
            return new ResultMsg(ErrorType.No_Error, "",result);
        }
        else if (lastRevMSG.substring(14, 14+2).equals("03") && lastRevMSG.length() == 24)
        {
            LockStatus result[] = new LockStatus[MAX_DOOR_NUM];
            for(int i = 0 ; i < MAX_DOOR_NUM; i++)
            {
                result[i] = LockStatus.Close;
            }

            for (int i = 0; i < 3; i++)
            {
                String sResult = Public.HextoBinary(lastRevMSG.substring(16 + i * 2, 16 + i * 2+2));
                {
                    //0 锁住 0 默认锁住 1 打开
                    for (int j = 0; j < 8; j++)
                    {
                        if (sResult.substring(8 - j - 1, 8 - j - 1+1).equals("0"))
                        {
                            result[i * 8 + j] = LockStatus.Close;
                        }
                        else
                        {
                            result[i * 8 + j] = LockStatus.Open;
                        }
                    }
                }
            }
            return new ResultMsg(ErrorType.No_Error,"", result);

        }
        else if (lastRevMSG.substring(14, 2).equals("07") && lastRevMSG.length() == 32)
        {
            LockStatus result[] = new LockStatus[MAX_DOOR_NUM];
            for(int i = 0 ; i < MAX_DOOR_NUM; i++)
            {
                result[i] = LockStatus.Close;
            }
            for (int i = 0; i < 3; i++)
            {
                String sResult = Public.HextoBinary(lastRevMSG.substring(16 + i * 2, 16 + i * 2+2));
                {
                    //0 锁住 0 默认锁住 1 打开
                    for (int j = 0; j < 8; j++)
                    {
                        if (sResult.substring(8 - j - 1, 8 - j - 1+1).equals("0"))
                        {
                            result[i * 8 + j] = LockStatus.Close;
                        }
                        else
                        {
                            result[i * 8 + j] = LockStatus.Open;
                        }
                    }
                }
            }
            return new ResultMsg(ErrorType.No_Error,"", result);
        }
        else
        {
            return new ResultMsg(ErrorType.Communication_Error,"Communication protocol error");
        }
    }


    /**
     * 串口接收数据
     * @param ComRecData
     */
    @Override
    protected void onDataReceived(final ComBean ComRecData)
    {
        try
        {
            lastRevMSG = Public.ByteArrToHex(ComRecData.bRec);
            lastRevMSG = lastRevMSG.trim();

            lastRevMSGIsOk = true;


            Log.i("LockTest",lastRevMSG);
        }
        catch(Exception e)
        {
            lastRevMSGIsOk = false;
        }
    }
}
