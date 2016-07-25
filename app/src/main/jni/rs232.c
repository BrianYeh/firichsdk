#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include "stdio.h"
#include <unistd.h>
#include <fcntl.h>
#include "termios.h"

char data[256];
#define SLEEP_MSEC 1000

#define LOG_TAG "serialJNI."
/*
#ifndef EXEC
#include <android/log.h>
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#else
#define LOGD(...) printf(">==< %s >==< ",LOG_TAG),printf(__VA_ARGS__),printf("\n")
#endif
 */

static speed_t getBaudrate(jint baudrate)
{
    switch(baudrate) {
        case 0: return B0;
        case 50: return B50;
        case 75: return B75;
        case 110: return B110;
        case 134: return B134;
        case 150: return B150;
        case 200: return B200;
        case 300: return B300;
        case 600: return B600;
        case 1200: return B1200;
        case 1800: return B1800;
        case 2400: return B2400;
        case 4800: return B4800;
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        case 230400: return B230400;
        case 460800: return B460800;
        case 500000: return B500000;
        case 576000: return B576000;
        case 921600: return B921600;
        case 1000000: return B1000000;
        case 1152000: return B1152000;
        case 1500000: return B1500000;
        case 2000000: return B2000000;
        case 2500000: return B2500000;
        case 3000000: return B3000000;
        case 3500000: return B3500000;
        case 4000000: return B4000000;
        default: return -1;
    }
}

int openUart(char* path) {

    int fd = -1;
    //fd = open("/dev/ttyUSB2", O_RDWR|O_NOCTTY|O_NDELAY);
    fd = open(path, O_RDWR|O_NOCTTY|O_NDELAY);

    return fd;
}

void closeUart(int fd) {

    close(fd);
}


void msleep(int msec) {
    struct timeval delay;
    delay.tv_sec = 0;
    delay.tv_usec = msec * 1000;
    select(0, NULL, NULL, NULL, &delay);
}

int sendMsgUart(int fd, const char* buf, int len) {

    return write(fd, buf, len);
}

void dump_buffer(const char* message, char* buffer, int len) {
    int i = 0;

    //printf("===dump %s (%d)===\n", message, len);

    for(i=0;i<len;i++) {
        //printf("[%d]=0x%02x\n", i, buffer[i]);
    }
}

int receiveMsgUart(int fd, char* buffer, int buffer_size) {

    int total_len = 0;
    int len = 0;
    int i = 0;
    char tmp[50];
    int nRetry=0;



    memset(buffer, 0, buffer_size);
    memset(tmp, 0, buffer_size);

    // Receive buffer until start
    while (len <= 0) {
        /* Read UART data to tmp buffer */
        len = read(fd, tmp, sizeof(tmp));
        if (len > 0) {
            memcpy((void*)buffer,tmp,len);

        }

        nRetry++;
        msleep(SLEEP_MSEC);
        if (nRetry  == 3)
        {
            break;
        }
    }

    dump_buffer("receiveMsgUart #1", tmp, len);

    return len;
}

int sendAndRecvMesgUart(int fd, char* msg, int msg_size, char* buffer, int buffer_size) {

    int len = 0;
    int nSendLen=0;
    int nRetry=0;
    /* Send message */
    nSendLen = sendMsgUart(fd, msg, msg_size);
    msleep(SLEEP_MSEC);

    while (len == 0) {
        /* Receive message */
        len = receiveMsgUart(fd, buffer, buffer_size);
        nRetry++;
        msleep(SLEEP_MSEC);
        if (nRetry  == 2)
        {
            break;
        }
        printf("sendAndRecvMesgUart len = %d\n", len);
    }

    return len;
}

char calulateLRC(char* value, int lenth)
{
    char LRC = 0;
    int i;
    for (i = 0; i < lenth; i++)
    {
        LRC ^= value[i];
    }
    return LRC;
}

int setUart(int fd, int speed_mode) {

    struct termios newtio, oldtio;
    int  speed_arr[] = {B1200, B2400, B4800, B9600, B19200, B38400, B57600, B115200, B230400, B921600};

    tcgetattr(fd, &oldtio);
    tcgetattr(fd, &newtio);
    cfsetispeed(&newtio, speed_arr[speed_mode]);
    cfsetospeed(&newtio, speed_arr[speed_mode]);

    //newtio.c_cflag = speed_arr[speed_mode] | CS8 | CRTSCTS| CREAD | CLOCAL|PARENB| -PARODD|-CSTOPB;
    //newtio.c_cflag = speed_arr[speed_mode] | CRTSCTS | CS8 | CREAD | CLOCAL;
    newtio.c_cflag = speed_arr[speed_mode] | CRTSCTS | CS8 | PARENB;
    newtio.c_cflag  &= ~PARODD; //set odd parity (or even parity with '-')
    newtio.c_cflag &= ~CSTOPB;

    newtio.c_iflag = IGNPAR ;
    newtio.c_lflag = 0;
    newtio.c_oflag = 00;
    newtio.c_line = 0;
    newtio.c_cc[7] = 255;
    newtio.c_cc[4] = 0;
    newtio.c_cc[5] = 0;

    if(tcsetattr(fd,TCSANOW, &newtio) < 0)
    {
        printf("tcsetattr fail !\n");
        return 0;
    }
    return 1;
}


JNIEXPORT jint JNICALL
Java_firich_com_firichsdk_SerialPort_openSerialPort(JNIEnv *env, jobject instance, jstring path_,
                                                    jint baudrate) {
    const char *path = (*env)->GetStringUTFChars(env, path_, 0); //Android Java String convert to C char*

    struct termios tio;

    memset(&tio,0,sizeof(tio));
    tio.c_iflag=0;
    tio.c_oflag=0;
    tio.c_cflag=CS8|CREAD|CLOCAL;           // 8n1, see termios.h for more information
    tio.c_lflag=0;
    tio.c_cc[VMIN]=1;
    tio.c_cc[VTIME]=5;
    int tty_fd=open(path, O_RDWR | O_NONBLOCK);
    cfsetospeed(&tio,getBaudrate(baudrate));
    cfsetispeed(&tio,getBaudrate(baudrate));

    tcsetattr(tty_fd,TCSANOW,&tio);
    return tty_fd; //  C char convert to Android Java String
}

JNIEXPORT jint JNICALL
Java_firich_com_firichsdk_SerialPort_openGPIOPort(JNIEnv *env, jobject instance, jstring path_,
                                                    jint bWrite) {
    const char *path = (*env)->GetStringUTFChars(env, path_,
                                                 0); //Android Java String convert to C char*




    int fd=-1;
    char buf[255];
  //  int gpio = XX;
    //fd = open(path, O_RDWR|O_NOCTTY|O_NDELAY);

    if (bWrite)
    {
        fd = open(path, O_WRONLY  | O_NONBLOCK);
    }else{
        fd = open(path, O_RDONLY  | O_NONBLOCK);
    }
//    sprintf(buf, "openGPIOPort:fd=%d", fd)
//    LOGD(buf );

 //   sprintf(buf, "%d", gpio);

 //   write(fd, buf, strlen(buf));

 //   close(fd);

    return fd; //  C char convert to Android Java String
}


JNIEXPORT jint JNICALL
Java_firich_com_firichsdk_SerialPort_openSerialPortSmartCard(JNIEnv *env, jobject instance, jstring path_,
                                                             jint baudrate) {
    const char *path = (*env)->GetStringUTFChars(env, path_, 0); //Android Java String convert to C char*

    struct termios tio;
    memset(&tio,0,sizeof(tio));

    int uart_file = -1;
    fd_set set;

    char lrc;
    char buffer[50];
    int recv_lenth;

    //return 0;

    uart_file = openUart(path);

    if (!setUart(uart_file,3))
    {
        closeUart(uart_file);
        return -1;
    }


    msleep(1000);

    //system ("stty -F /dev/ttyUSB2 cs8 crtscts parenb -parodd -cstopb");
    //system ("stty -F /dev/ttyUSB2 speed 9600 > /dev/null");

    //uart_file = openUart();

    FD_ZERO(&set); /* clear the set */ //Initializes the file descriptor set fdset to have zero bits for all file descriptors.
    FD_SET(uart_file, &set); /* add our file descriptor to the set */
    return uart_file;
}


JNIEXPORT jint JNICALL
Java_firich_com_firichsdk_SerialPort_openSerialPortSmartCardTest(JNIEnv *env, jobject instance, jstring path_,
                                                    jint baudrate) {
    const char *path = (*env)->GetStringUTFChars(env, path_, 0); //Android Java String convert to C char*

    struct termios tio;
    memset(&tio,0,sizeof(tio));

    /*
     *
     *
     */
    int uart_file = -1;
    fd_set set;
    char version_msg[5] = {0x02,0x01,0x00,0x00,0x03};
    char activation_msg[5] = {0x02,0x10,0x00,0x00,0x12};
    char deactivation_msg[5] = {0x02,0x20,0x00,0x00,0x22};
    char lrc;
    char buffer[50];
    int recv_lenth;

    //return 0;

    uart_file = openUart(path);

    if (!setUart(uart_file,3))
    {
        closeUart(uart_file);
        return -1;
    }


    //closeUart(uart_file);
    msleep(1000);

    //system ("stty -F /dev/ttyUSB2 cs8 crtscts parenb -parodd -cstopb");
    //system ("stty -F /dev/ttyUSB2 speed 9600 > /dev/null");

    //uart_file = openUart();

    FD_ZERO(&set); /* clear the set */ //Initializes the file descriptor set fdset to have zero bits for all file descriptors.
    FD_SET(uart_file, &set); /* add our file descriptor to the set */

    //recv_lenth = sendAndRecvMesgUart(uart_file,version_msg, sizeof(version_msg),buffer,sizeof(buffer));

    //memcpy((void*)data,buffer,recv_lenth);

    //closeUart(uart_file); //debug
    return  uart_file; //debug

    lrc = calulateLRC(&buffer[0], recv_lenth-1);
    printf("LRC of Version = 0x%x\n", lrc);


    closeUart(uart_file);
/*
    if(0 != memcmp(&lrc, &buffer[rec_lenth-1],1))
    {
        printf("Get Version LRC error\n");
        return 0;
    }
    else
    {
        return (int)recv_lenth;
    }
*/
    //return lrc; //  C char convert to Android Java String
/*
 *
 * uart_file = openUart();

    setUart(uart_file,3);	//3 for 9600

    closeUart(uart_file);
    msleep(1000);
 */



}

JNIEXPORT void JNICALL
Java_firich_com_firichsdk_SerialPort_closeSerialPort(JNIEnv *env, jobject instance,
                                                     jint intSerialPortHandle) {

    int tty_fd = intSerialPortHandle;
    close(tty_fd);

}

JNIEXPORT jint JNICALL
Java_firich_com_firichsdk_SerialPort_writeSerialPort(JNIEnv *env, jobject instance,
                                                     jint intSerialPortHandle,
                                                     jbyteArray btyData_) {
    int intReturnCode=-1;
    int len = (*env)->GetArrayLength (env, btyData_);
    unsigned char data[len];
    //unsigned char* buf = new unsigned char[len];
    (*env)->GetByteArrayRegion (env, btyData_, 0, len, (jbyte*)(data));
    int tty_fd = intSerialPortHandle;
    intReturnCode = write(tty_fd,&data,len);
}

JNIEXPORT jint JNICALL
Java_firich_com_firichsdk_SerialPort_readSerialPort(JNIEnv *env, jobject instance,
                                                    jint intSerialPortHandle) {
    int intLen = -1;
    int tty_fd = intSerialPortHandle;
    intLen=read(tty_fd,&data,256);
    return intLen;
}

JNIEXPORT jbyteArray JNICALL
Java_firich_com_firichsdk_SerialPort_getReceiveData(JNIEnv *env, jobject instance,
                                                    jint intReceiveDataLength) {
    jbyteArray arr = (*env)->NewByteArray(env, intReceiveDataLength);
    (*env)->SetByteArrayRegion(env,arr,0,intReceiveDataLength, data);
    return arr;
}