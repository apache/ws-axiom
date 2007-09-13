package org.apache.axiom.attachments;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;

import org.apache.axiom.om.AbstractTestCase;

public class PdfAttachmentStreamingTest extends AbstractTestCase {

	String contentType = "multipart/related;type=\"text/xml\";boundary=\"----=_Part_0_3437046.1188904239130\";start=__WLS__1188904239161__SOAP__";
	String inputFile = "mtom/msg-soap-wls81.txt";
	
	public PdfAttachmentStreamingTest(String name) {
		super(name);
	}
	
	public void testStreamingAttachments() throws Exception {
		FileInputStream inStream = new FileInputStream(getTestResourceFile(inputFile));
		// creating attachments using that stream
		Attachments attachments = new Attachments(inStream, contentType);

		// getting attachments as streams
		IncomingAttachmentStreams attachStreams = attachments.getIncomingAttachmentStreams();
		

		// getting first attachments after the soap part
		IncomingAttachmentInputStream firstAttach = attachStreams.getNextStream();
		
		// coping contents of the attachment to byte array
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(firstAttach, output);

		// reading the message again, getting second attachment using datahandlers
		inStream = new FileInputStream(getTestResourceFile(inputFile));
		attachments = new Attachments(inStream, contentType);
		DataHandler h = attachments.getDataHandler((String)attachments.getAllContentIDs()[1]);

		ByteArrayOutputStream input = new ByteArrayOutputStream();
		copy(h.getInputStream(), input);

		assertEquals(input.toString("UTF-8"), output.toString("UTF-8"));
	}
	


	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[4096];
		while(true) {
			int len = in.read(buf);
			if (len != -1) {
				out.write(buf, 0, len);
			} else {
				break;
			}
		}
	}
}
