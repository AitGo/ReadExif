package com.ly.readexif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @创建者 ly
 * @创建时间 2019/8/14
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class BmpUtils {

    /**
     * 保存8位位图，需要添加颜色调色板，文件信息头+位图信息头+调色板+位图数据
     * @param bufferSrc
     * @param imgPath
     */
    public static void getBmpWith8(byte[] bufferSrc, String imgPath,int w,int h) {

        byte[] color_table = addBMP8ImageInfosHeaderTable(w, h);          //颜色表，8位图必须有
        byte[] infos = addBMP8ImageInfosHeader(w, h);                     //文件信息头
        byte[] header = addBMP8ImageHeader(bufferSrc.length, infos.length +
                color_table.length);                             //文件头
        byte[] buffer = new byte[header.length + infos.length + color_table.length
                + bufferSrc.length];                                   //申请用来组合上面四个部分的空间，这个空间直接保存就是bmp图了
        byte[] bytes = addBMP_8(bufferSrc, w, h);
        System.arraycopy(header, 0, buffer, 0, header.length);           //复制文件头
        System.arraycopy(infos, 0, buffer, header.length, infos.length); //复制文件信息头
        System.arraycopy(color_table, 0, buffer, header.length + infos.length,
                color_table.length);//复制颜色表
        System.arraycopy(bytes, 0, buffer, header.length + infos.length +
                color_table.length, bufferSrc.length);
        FileOutputStream fos = null;

        try {
            File file = new File(imgPath);
            if(!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(imgPath);
            fos.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *
     * @param b
     * @param w
     * @param h
     * @return
     */
    private static byte[] addBMP_8(byte[] b, int w, int h) {
        int len = b.length;
        System.out.println(b.length);
        byte[] buffer = new byte[w * h];
        int offset = 0;
        for (int i = len - 1; i >= (w - 1); i -= w) {
            // 对于bmp图，DIB文件格式最后一行为第一行，每行按从左到右顺序
            int end = i, start = i - w + 1;
            for (int j = start; j <= end; j++) {
                buffer[offset] = b[j];
                offset++;
            }
        }
        return buffer;
    }

    /**
     * 8位位图的文件头
     * @param size
     * @param lenH
     * @return
     */
    private static byte[] addBMP8ImageHeader(int size, int lenH) {
        byte[] buffer = new byte[14];
        int m_lenH = lenH + buffer.length;      //lenH:文件信息头
        //和颜色表长度之和
        size += m_lenH;     //size:颜色数据的长度+两个文件头长度+颜色表长度
        buffer[0] = 0x42;   //WORD 固定为0x4D42;
        buffer[1] = 0x4D;
        buffer[2] = (byte) (size >> 0);    //DWORD 文件大小
        buffer[3] = (byte) (size >> 8);
        buffer[4] = (byte) (size >> 16);
        buffer[5] = (byte) (size >> 24);
        buffer[6] = 0x00;    //WORD 保留字，不考虑
        buffer[7] = 0x00;
        buffer[8] = 0x00;    //WORD 保留字，不考虑
        buffer[9] = 0x00;
        buffer[10] = (byte) (m_lenH >> 0);      //DWORD 实际位图数据的偏移字
        buffer[11] = (byte) (m_lenH >> 8);      //节数，即所有三个头（文件头、
        buffer[12] = (byte) (m_lenH >> 16);     //文件信息头、颜色表）之和
        buffer[13] = (byte) (m_lenH >> 24);     //14 + 40 + 1024 = 1078
        //0x0436   0x0036=40+14=54
        return buffer;
    }

    /**
     * 8位位图的位图信息头
     * @param w
     * @param h
     * @return
     */
    private static byte[] addBMP8ImageInfosHeader(int w, int h) {
        byte[] buffer = new byte[40];
        int ll = buffer.length;
        buffer[0] = (byte) (ll >> 0);    //DWORD：本段头长度：40   0x0028
        buffer[1] = (byte) (ll >> 8);
        buffer[2] = (byte) (ll >> 16);
        buffer[3] = (byte) (ll >> 24);
        buffer[4] = (byte) (w >> 0);    //long：图片宽度
        buffer[5] = (byte) (w >> 8);
        buffer[6] = (byte) (w >> 16);
        buffer[7] = (byte) (w >> 24);
        buffer[8] = (byte) (h >> 0);    //long：图片高度
        buffer[9] = (byte) (h >> 8);
        buffer[10] = (byte) (h >> 16);
        buffer[11] = (byte) (h >> 24);
        buffer[12] = 0x01;           //WORD:平面数：1
        buffer[13] = 0x00;
        buffer[14] = 0x08;           //WORD:图像位数：8位
        buffer[15] = 0x00;
        buffer[16] = 0x00;           //DWORD：压缩方式，可以是0，1，2，
        buffer[17] = 0x00;           //其中0表示不压缩
        buffer[18] = 0x00;
        buffer[19] = 0x00;
        buffer[20] = 0x00;           //DWORD；实际位图数据占用的字节数,当上一个数值
        buffer[21] = 0x00;           //biCompression等于0时，这里的值可以省略不填
        buffer[22] = 0x00;
        buffer[23] = 0x00;
        buffer[24] = (byte) 0x20;    //LONG：X方向分辨率
        buffer[25] = 0x4E;           //20000(0x4E20) dpm  1 in = 0.0254 m
        buffer[26] = 0x00;
        buffer[27] = 0x00;
        buffer[28] = (byte) 0x20;    //LONG：Y方向分辨率
        buffer[29] = 0x4E;           //20000(0x4E20) dpm  1 in = 0.0254 m
        buffer[30] = 0x00;
        buffer[31] = 0x00;
        buffer[32] = 0x00;           //DWORD：使用的颜色数，如果为0，
        buffer[33] = 0x00;           //则表示默认值(2^颜色位数)
        buffer[34] = 0x00;
        buffer[35] = 0x00;
        buffer[36] = 0x00;           //DWORD：重要颜色数，如果为0,
        buffer[37] = 0x00;           //则表示所有颜色都是重要的
        buffer[38] = 0x00;
        buffer[39] = 0x00;

        return buffer;
    }

    /**
     * 8位位图的颜色调板
     * @param w
     * @param h
     * @return
     */
    private static byte[] addBMP8ImageInfosHeaderTable(int w, int h) {
        byte[] buffer = new byte[256 * 4];

        //生成颜色表
        for (int i = 0; i < 256; i++) {
            buffer[0 + 4 * i] = (byte) i;   //Blue
            buffer[1 + 4 * i] = (byte) i;   //Green
            buffer[2 + 4 * i] = (byte) i;   //Red
            buffer[3 + 4 * i] = (byte) 0x00;   //保留值
        }

        return buffer;
    }
}
