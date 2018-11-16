package com.reign.gcld.common.file;

import java.util.*;
import com.reign.gcld.common.log.*;
import org.apache.commons.collections.map.*;
import com.reign.util.*;
import com.reign.gcld.common.util.*;
import java.io.*;

public class FileReader
{
    private static final Logger log;
    private static FileReader fr;
    private Map<String, RandomAccessFile> MAP;
    private byte[] bytes;
    private static final int MAX_FILE_LEN = 10000000;
    
    static {
        log = CommonLog.getLog(FileReader.class);
    }
    
    public FileReader() {
        this.MAP = new LRUMap(100);
        this.bytes = new byte[4];
    }
    
    public static FileReader getInstance() {
        if (FileReader.fr == null) {
            FileReader.fr = new FileReader();
        }
        return FileReader.fr;
    }
    
    public byte[] readFile(final String id) {
        byte[] result = null;
        try {
            final String fid = id.substring(0, 8);
            RandomAccessFile raf = this.MAP.get(fid);
            if (raf == null) {
                raf = this.getAccessFile(fid);
                if (raf != null) {
                    this.MAP.put(fid, raf);
                }
            }
            if (raf != null) {
                synchronized (raf) {
                    final long offset = Long.parseLong(id.substring(fid.length()));
                    raf.seek(offset);
                    raf.read(this.bytes, 0, this.bytes.length);
                    final int length = ByteUtil.bytesToInt(this.bytes);
                    if (length > 10000000 || length <= 0) {
                        // monitorexit(raf)
                        return null;
                    }
                    result = new byte[length];
                    raf.read(result, 0, length);
                    // monitorexit(raf)
                    return result;
                }
            }
        }
        catch (Exception e) {
            FileReader.log.error("readFile ", e);
        }
        return result;
    }
    
    private RandomAccessFile getAccessFile(final String fid) throws FileNotFoundException {
        final String filePath = String.valueOf(FileUtil.getPath()) + File.separator + fid;
        final File file = new File(filePath);
        if (file.exists()) {
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            return raf;
        }
        return null;
    }
}
