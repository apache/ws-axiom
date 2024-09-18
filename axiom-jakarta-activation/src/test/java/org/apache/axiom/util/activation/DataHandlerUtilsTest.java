package org.apache.axiom.util.activation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.testutils.io.ExceptionOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.jupiter.api.Test;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

public class DataHandlerUtilsTest {
    @Test
    public void testToBlobWriteError() {
        ExceptionOutputStream out = new ExceptionOutputStream(0);
        StreamCopyException ex =
                assertThrows(
                        StreamCopyException.class,
                        () -> {
                            DataHandlerUtils.toBlob(
                                            new DataHandler(
                                                    new ByteArrayDataSource(
                                                            new byte[10],
                                                            "application/octet-stream")))
                                    .writeTo(out);
                        });
        assertThat(ex.getOperation()).isEqualTo(StreamCopyException.WRITE);
        assertThat(ex.getCause()).isSameAs(out.getException());
    }

    @Test
    public void testToBlobReadError() {
        DataSource ds =
                new DataSource() {
                    @Override
                    public String getContentType() {
                        return "application/octet-stream";
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        throw new IOException("Read error");
                    }

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        throw new UnsupportedOperationException();
                    }
                };
        StreamCopyException ex =
                assertThrows(
                        StreamCopyException.class,
                        () -> {
                            DataHandlerUtils.toBlob(new DataHandler(ds))
                                    .writeTo(NullOutputStream.INSTANCE);
                        });
        assertThat(ex.getOperation()).isEqualTo(StreamCopyException.READ);
        assertThat(ex.getCause().getMessage()).isEqualTo("Read error");
    }
}
