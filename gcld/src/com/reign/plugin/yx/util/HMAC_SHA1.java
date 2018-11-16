package com.reign.plugin.yx.util;

public class HMAC_SHA1
{
    public static byte[] getHmacSHA1(final String data, final String key) {
        final byte[] ipadArray = new byte[64];
        final byte[] opadArray = new byte[64];
        final byte[] keyArray = new byte[64];
        int ex = key.length();
        final SHA1 sha1 = new SHA1();
        if (key.length() > 64) {
            final byte[] temp = sha1.getDigestOfBytes(key.getBytes());
            ex = temp.length;
            for (int i = 0; i < ex; ++i) {
                keyArray[i] = temp[i];
            }
        }
        else {
            final byte[] temp = key.getBytes();
            for (int i = 0; i < temp.length; ++i) {
                keyArray[i] = temp[i];
            }
        }
        for (int j = ex; j < 64; ++j) {
            keyArray[j] = 0;
        }
        for (int k = 0; k < 64; ++k) {
            ipadArray[k] = (byte)(keyArray[k] ^ 0x36);
            opadArray[k] = (byte)(keyArray[k] ^ 0x5C);
        }
        final byte[] tempResult = sha1.getDigestOfBytes(join(ipadArray, data.getBytes()));
        return sha1.getDigestOfBytes(join(opadArray, tempResult));
    }
    
    private static byte[] join(final byte[] b1, final byte[] b2) {
        final int length = b1.length + b2.length;
        final byte[] newer = new byte[length];
        for (int i = 0; i < b1.length; ++i) {
            newer[i] = b1[i];
        }
        for (int i = 0; i < b2.length; ++i) {
            newer[i + b1.length] = b2[i];
        }
        return newer;
    }
    
    static class SHA1
    {
        private final int[] abcde;
        private int[] digestInt;
        private int[] tmpData;
        
        SHA1() {
            this.abcde = new int[] { 1732584193, -271733879, -1732584194, 271733878, -1009589776 };
            this.digestInt = new int[5];
            this.tmpData = new int[80];
        }
        
        private int process_input_bytes(final byte[] bytedata) {
            System.arraycopy(this.abcde, 0, this.digestInt, 0, this.abcde.length);
            final byte[] newbyte = this.byteArrayFormatData(bytedata);
            for (int MCount = newbyte.length / 64, pos = 0; pos < MCount; ++pos) {
                for (int j = 0; j < 16; ++j) {
                    this.tmpData[j] = this.byteArrayToInt(newbyte, pos * 64 + j * 4);
                }
                this.encrypt();
            }
            return 20;
        }
        
        private byte[] byteArrayFormatData(final byte[] bytedata) {
            int zeros = 0;
            int size = 0;
            final int n = bytedata.length;
            final int m = n % 64;
            if (m < 56) {
                zeros = 55 - m;
                size = n - m + 64;
            }
            else if (m == 56) {
                zeros = 63;
                size = n + 8 + 64;
            }
            else {
                zeros = 63 - m + 56;
                size = n + 64 - m + 64;
            }
            final byte[] newbyte = new byte[size];
            System.arraycopy(bytedata, 0, newbyte, 0, n);
            int l = n;
            newbyte[l++] = -128;
            for (int i = 0; i < zeros; ++i) {
                newbyte[l++] = 0;
            }
            final long N = n * 8L;
            final byte h8 = (byte)(N & 0xFFL);
            final byte h9 = (byte)(N >> 8 & 0xFFL);
            final byte h10 = (byte)(N >> 16 & 0xFFL);
            final byte h11 = (byte)(N >> 24 & 0xFFL);
            final byte h12 = (byte)(N >> 32 & 0xFFL);
            final byte h13 = (byte)(N >> 40 & 0xFFL);
            final byte h14 = (byte)(N >> 48 & 0xFFL);
            final byte h15 = (byte)(N >> 56);
            newbyte[l++] = h15;
            newbyte[l++] = h14;
            newbyte[l++] = h13;
            newbyte[l++] = h12;
            newbyte[l++] = h11;
            newbyte[l++] = h10;
            newbyte[l++] = h9;
            newbyte[l++] = h8;
            return newbyte;
        }
        
        private int f1(final int x, final int y, final int z) {
            return (x & y) | (~x & z);
        }
        
        private int f2(final int x, final int y, final int z) {
            return x ^ y ^ z;
        }
        
        private int f3(final int x, final int y, final int z) {
            return (x & y) | (x & z) | (y & z);
        }
        
        private int f4(final int x, final int y) {
            return x << y | x >>> 32 - y;
        }
        
        private void encrypt() {
            for (int i = 16; i <= 79; ++i) {
                this.tmpData[i] = this.f4(this.tmpData[i - 3] ^ this.tmpData[i - 8] ^ this.tmpData[i - 14] ^ this.tmpData[i - 16], 1);
            }
            final int[] tmpabcde = new int[5];
            for (int i2 = 0; i2 < tmpabcde.length; ++i2) {
                tmpabcde[i2] = this.digestInt[i2];
            }
            for (int j = 0; j <= 19; ++j) {
                final int tmp = this.f4(tmpabcde[0], 5) + this.f1(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + this.tmpData[j] + 1518500249;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = this.f4(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }
            for (int k = 20; k <= 39; ++k) {
                final int tmp = this.f4(tmpabcde[0], 5) + this.f2(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + this.tmpData[k] + 1859775393;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = this.f4(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }
            for (int l = 40; l <= 59; ++l) {
                final int tmp = this.f4(tmpabcde[0], 5) + this.f3(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + this.tmpData[l] - 1894007588;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = this.f4(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }
            for (int m = 60; m <= 79; ++m) {
                final int tmp = this.f4(tmpabcde[0], 5) + this.f2(tmpabcde[1], tmpabcde[2], tmpabcde[3]) + tmpabcde[4] + this.tmpData[m] - 899497514;
                tmpabcde[4] = tmpabcde[3];
                tmpabcde[3] = tmpabcde[2];
                tmpabcde[2] = this.f4(tmpabcde[1], 30);
                tmpabcde[1] = tmpabcde[0];
                tmpabcde[0] = tmp;
            }
            for (int i3 = 0; i3 < tmpabcde.length; ++i3) {
                this.digestInt[i3] += tmpabcde[i3];
            }
            for (int n = 0; n < this.tmpData.length; ++n) {
                this.tmpData[n] = 0;
            }
        }
        
        private int byteArrayToInt(final byte[] bytedata, final int i) {
            return (bytedata[i] & 0xFF) << 24 | (bytedata[i + 1] & 0xFF) << 16 | (bytedata[i + 2] & 0xFF) << 8 | (bytedata[i + 3] & 0xFF);
        }
        
        private void intToByteArray(final int intValue, final byte[] byteData, final int i) {
            byteData[i] = (byte)(intValue >>> 24);
            byteData[i + 1] = (byte)(intValue >>> 16);
            byteData[i + 2] = (byte)(intValue >>> 8);
            byteData[i + 3] = (byte)intValue;
        }
        
        private static String byteToHexString(final byte ib) {
            final char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            final char[] ob = { Digit[ib >>> 4 & 0xF], Digit[ib & 0xF] };
            final String s = new String(ob);
            return s;
        }
        
        private static String byteArrayToHexString(final byte[] bytearray) {
            String strDigest = "";
            for (int i = 0; i < bytearray.length; ++i) {
                strDigest = String.valueOf(strDigest) + byteToHexString(bytearray[i]);
            }
            return strDigest;
        }
        
        public byte[] getDigestOfBytes(final byte[] byteData) {
            this.process_input_bytes(byteData);
            final byte[] digest = new byte[20];
            for (int i = 0; i < this.digestInt.length; ++i) {
                this.intToByteArray(this.digestInt[i], digest, i * 4);
            }
            return digest;
        }
        
        public String getDigestOfString(final byte[] byteData) {
            return byteArrayToHexString(this.getDigestOfBytes(byteData));
        }
    }
}
