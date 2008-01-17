package org.apache.axiom.attachments.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * BAAInputStream is like a ByteArrayInputStream.
 * A ByteArrayInputStream stores the backing data in a byte[].
 * BAAInputStream stores the backing data in a Array of 
 * 4K byte[].  Using several non-contiguous chunks reduces 
 * memory copy and resizing.
 */
public class BAAInputStream extends InputStream {

    ArrayList data = new ArrayList();
    int BUFFER_SIZE = 4 * 1024;
    int i;
    int size;
    int currIndex;
    int totalIndex;
    int mark = 0;
    byte[] currBuffer = null;
    byte[] read_byte = new byte[1];
    
    public BAAInputStream(ArrayList data, int size) {
        this.data = data;
        this.size = size;
        i = 0;
        currIndex = 0;
        totalIndex = 0;
        currBuffer = (byte[]) data.get(0);
    }

    public int read() throws IOException {
        int read = read(read_byte);

        if (read < 0) {
            return -1;
        } else {
            return read_byte[0];
        }
    }

    public int available() throws IOException {
        return size - totalIndex;
    }


    public synchronized void mark(int readlimit) {
        mark = totalIndex;
    }

    public boolean markSupported() {
        return true;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int total = 0;
        if (totalIndex >= size) {
            return -1;
        }
        while (total < len && totalIndex < size) {
            int copy = Math.min(len - total, BUFFER_SIZE - currIndex);
            copy = Math.min(copy, size - totalIndex);
            System.arraycopy(currBuffer, currIndex, b, off, copy);
            total += copy;
            currIndex += copy;
            totalIndex += copy;
            off += copy;
            if (currIndex >= BUFFER_SIZE) {
                if (i+1 < data.size()) {
                    currBuffer = (byte[]) data.get(i+1);
                    i++;
                    currIndex = 0;
                } else {
                    currBuffer = null;
                    currIndex = BUFFER_SIZE;
                } 
            }
        }
        return total;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public synchronized void reset() throws IOException {
        i = mark / BUFFER_SIZE;
        currIndex = mark - (i * BUFFER_SIZE);
        currBuffer = (byte[]) data.get(i);
        totalIndex = mark;
    }

}
