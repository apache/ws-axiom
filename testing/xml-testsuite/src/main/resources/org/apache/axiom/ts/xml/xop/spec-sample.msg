--MIME_boundary
Content-Type: application/xop+xml; 
    charset=UTF-8; 
    type="text/xml"
Content-Transfer-Encoding: 8bit
Content-ID: <mymessage.xml@example.org>

<m:data xmlns:m='http://example.org/stuff'>
  <m:photo><xop:Include 
  xmlns:xop='http://www.w3.org/2004/08/xop/include' 
  href='cid:http://example.org/me.png'/></m:photo>
  <m:sig><xop:Include 
  xmlns:xop='http://www.w3.org/2004/08/xop/include' 
  href='cid:http://example.org/my.hsh'/></m:sig>
</m:data>

--MIME_boundary
Content-Type: image/png
Content-Transfer-Encoding: base64
Content-ID: <http://example.org/me.png>

/aWKKapGGyQ=

--MIME_boundary
Content-Type: application/pkcs7-signature
Content-Transfer-Encoding: base64
Content-ID: <http://example.org/my.hsh>

Faa7vROi2VQ=

--MIME_boundary--
