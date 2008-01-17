package org.apache.axiom.attachments;

import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.axiom.attachments.utils.BAAOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

/**
 * UnitTest for the BAAInputStream and BAAOutputStream classes.
 */
public class BAATest extends TestCase {

    static final int  INPUT_SIZE = 100 * 1024;
    static final int ITERATIONS = 10;
    static final int PRIME = ITERATIONS/10;
    static final int BAOS_SIZE = 4 * 1024;
    byte[] input;
    ByteArrayInputStream inputBAIS;
    
    public BAATest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        input = new byte[INPUT_SIZE];
        for (int i=0; i < INPUT_SIZE; i++) {
            input[i] = 25;
        }
        inputBAIS = new ByteArrayInputStream(input);
    }

    public void test() throws Exception {
        for (int i=0; i<5; i++) {
            normal();
            enhanced();
        }
    }
    
    public void normal() throws Exception {
        long time = System.currentTimeMillis();
        for (int i=0; i<ITERATIONS+PRIME; i++) {
            if (i == PRIME) {
                time = System.currentTimeMillis();
            }
            inputBAIS.reset();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BAOS_SIZE);
            BufferUtils.inputStream2OutputStream(inputBAIS, baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            baos = new ByteArrayOutputStream(BAOS_SIZE);
            BufferUtils.inputStream2OutputStream(bais, baos);
            bais = new ByteArrayInputStream(baos.toByteArray());
            baos = new ByteArrayOutputStream(BAOS_SIZE);
            for (int j= 1; j < 1000; j++) {
                baos.write(bais.read());
            }
            BufferUtils.inputStream2OutputStream(bais, baos);
            assertTrue(baos.toByteArray().length == INPUT_SIZE);
        }
        // Uncomment the follow
        //System.out.println("Normal time = " + (System.currentTimeMillis()-time));
    }
    
    public void enhanced() throws Exception {
        long time = System.currentTimeMillis();
        for (int i=0; i<ITERATIONS+PRIME; i++) {
            if (i == PRIME) {
                time = System.currentTimeMillis();
            }
            inputBAIS.reset();
            BAAOutputStream baaos = new BAAOutputStream();
            BufferUtils.inputStream2OutputStream(inputBAIS, baaos);
            assertTrue("1: " + baaos.length() + " " + INPUT_SIZE, baaos.length() == INPUT_SIZE);

            BAAInputStream baais = new BAAInputStream(baaos.buffers(), baaos.length());
            baaos = new BAAOutputStream();
            BufferUtils.inputStream2OutputStream(baais, baaos);
            baais = new BAAInputStream(baaos.buffers(), baaos.length());
            baaos = new BAAOutputStream();
            for (int j= 1; j < 1000; j++) {
                baaos.write(baais.read());
            }
            BufferUtils.inputStream2OutputStream(baais, baaos);
            assertTrue("" + baaos.length() + " " + INPUT_SIZE, baaos.length() == INPUT_SIZE);
        }
        //System.out.println("Enhanced time = " + (System.currentTimeMillis()-time));
    }
    
}
