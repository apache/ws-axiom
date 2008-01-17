package org.apache.axiom.attachments.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * BAAOutputStream is like a ByteArrayOutputStream.
 * A ByteArrayOutputStream stores the backing data in a byte[].
 * BAAOutputStream stores the backing data in a Array of 
 * 4K byte[].  Using several non-contiguous chunks reduces 
 * memory copy and resizing.
 */
public class BAAOutputStream extends OutputStream {

    ArrayList data = new ArrayList();
    int BUFFER_SIZE = 4 * 1024;
    int index = 0;
    byte[] currBuffer = null;
    public BAAOutputStream() {
        super();
        addBuffer();
    }

    private void addBuffer() {
        currBuffer = new byte[BUFFER_SIZE];
        data.add(currBuffer);
        index = 0;
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
       int total = 0;
       while (total < len) {
           int copy = Math.min(len-total, BUFFER_SIZE-index);
           System.arraycopy(b, off, currBuffer, index, copy);
           total += copy;
           index += copy;
           off += copy;
           if (index >= BUFFER_SIZE) {
               addBuffer();
           }
       }
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    
    byte[] writeByte = new byte[1];
    public void write(int b) throws IOException {
        writeByte[0] = (byte) b;
        this.write(writeByte, 0, 1);
    }

    public ArrayList buffers() {
        return data;
    }
    
    public int length() {
        return (BUFFER_SIZE * (data.size()-1)) + index;
    }
}
