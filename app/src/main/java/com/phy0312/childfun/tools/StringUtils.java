package com.phy0312.childfun.tools;

/**
 * description: <br/>
 * author: Administrator<br/>
 * data: 2014/11/26<br/>
 */
public class StringUtils {

    /**
     * 文件大小转换，单位：B KB MB GB
     * @param num long
     * @param scale 小数位精确值
     * @return String
     */
    public static String parseLongToKbOrMb(long num, int scale) {
        try {
            float scaleNum;
            switch (scale) {
                case 0:
                    scaleNum = 1;
                    break;
                case 1:
                    scaleNum = 10f;
                    break;
                case 2:
                    scaleNum = 100f;
                    break;
                case 3:
                    scaleNum = 1000f;
                    break;
                case 4:
                    scaleNum = 10000f;
                    break;
                default:
                    scaleNum = 1;
            }
            float n = num;
            if (n < 1024) {
                return Math.round(n * scaleNum) / scaleNum + "B";
            }
            n = n / 1024;
            if (n < 1024) {
                return Math.round(n * scaleNum) / scaleNum + "KB";
            }
            n = n / 1024;
            if (n < 1024) {
                return Math.round(n * scaleNum) / scaleNum + "MB";
            }
            n = n / 1024;
            return Math.round(n * scaleNum) / scaleNum + "GB";
        } catch (Exception e) {
            e.printStackTrace();
            return -1 + "B";
        }
    }

    /**
     * 检查字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.equals(""));
    }


}
