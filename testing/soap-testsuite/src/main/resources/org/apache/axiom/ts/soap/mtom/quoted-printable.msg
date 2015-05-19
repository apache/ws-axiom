------=_Part_542_1447667749.1430736561148
Content-Type: application/xop+xml; charset=UTF-8; type="application/soap+xml"; action="receive"
Content-Transfer-Encoding: 8bit
Content-ID: <rootpart@soapui.org>

<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ser="http://services.test.wsstack.softwareag.com">
   <soap:Header/>
   <soap:Body>
      <ser:receive>
         <!--Optional:-->
         <ser:data><inc:Include href="cid:SDESS_COREP_00000_KO_SNG.xml" xmlns:inc="http://www.w3.org/2004/08/xop/include"/></ser:data>
      </ser:receive>
   </soap:Body>
</soap:Envelope>
------=_Part_542_1447667749.1430736561148
Content-Type: text/xml; charset=Cp1252; name=SDESS_COREP_00000_KO_SNG.xml
Content-Transfer-Encoding: quoted-printable
Content-ID: <SDESS_COREP_00000_KO_SNG.xml>
Content-Disposition: attachment; name="SDESS_COREP_00000_KO_SNG.xml"; filename="SDESS_COREP_00000_KO_SNG.xml"

<?xml version=3D"1.0" encoding=3D"UTF-8"?>
<ds:Signature xmlns:ds=3D"http://www.w3.org/2000/09/xmldsig#" Id=3D"Indepen=
dentSignature-5817F18CC37E">
<ds:SignedInfo>
<ds:CanonicalizationMethod Algorithm=3D"http://www.w3.org/2001/10/xml-exc-c=
14n#"></ds:CanonicalizationMethod>
<ds:SignatureMethod Algorithm=3D"http://www.w3.org/2000/09/xmldsig#rsa-sha1=
"></ds:SignatureMethod>
<ds:Reference Id=3D"IndependentSignature-5817F18CC37E-REF-1" Type=3D"http:/=
/uri.etsi.org/01903/v1.3.2#SignedProperties" URI=3D"#IndependentSignature-5=
817F18CC37E-SignedProperties">
<ds:Transforms>
<ds:Transform Algorithm=3D"http://www.w3.org/2001/10/xml-exc-c14n#"></ds:Tr=
ansform>
</ds:Transforms>
<ds:DigestMethod Algorithm=3D"http://www.w3.org/2000/09/xmldsig#sha1"></ds:=
DigestMethod>
<ds:DigestValue>+thKIXdsC1ogxhEYW2AXPvK23Vw=3D</ds:DigestValue>
</ds:Reference>
<ds:Reference Id=3D"IndependentSignature-5817F18CC37E-REF-2" Type=3D"http:/=
/www.w3.org/2000/09/xmldsig#Object" URI=3D"#SignedContent-69A51171D70E">
<ds:Transforms>
<ds:Transform Algorithm=3D"http://www.w3.org/TR/2001/REC-xml-c14n-20010315"=
></ds:Transform>
</ds:Transforms>
<ds:DigestMethod Algorithm=3D"http://www.w3.org/2000/09/xmldsig#sha1"></ds:=
DigestMethod>
<ds:DigestValue>UFu2qiO9DOAY2topqusDB9EsOvE=3D</ds:DigestValue>
</ds:Reference>
</ds:SignedInfo>
<ds:SignatureValue Id=3D"IndependentSignature-5817F18CC37E-SignatureValue">
LLCONe29/erEr5Gi7bFLdIUybsl7YCc8+HBBikkn4+aglLowivXbzwPHHAPVqLRLAV1AMt2+ztv=
t
DR99ms75s3P1VKK2iFwRS4DXOlhq05iD2aHLoopiZDqDqLAl5FhSFOo4r5IwX/EoiDR4Jp6QvVO=
D
S5sO5weQk7fUaY9utkw=3D
</ds:SignatureValue>
<ds:KeyInfo>
<ds:X509Data>
<ds:X509Certificate>
MIIGSTCCBTGgAwIBAgIQAgFFs/Vj3nL7sQ0ci1iYWTANBgkqhkiG9w0BAQUFADCBkDELMAkGA1U=
E
BhMCRlIxDjAMBgNVBAcTBVBhcmlzMRkwFwYDVQQKExBCYW5xdWUgZGUgRnJhbmNlMSYwJAYDVQQ=
L
Ex1LRVlORUNUSVMgcG91ciBURVNUIHNldWxlbWVudDEuMCwGA1UEAxMlQXV0b3JpdGUgZGUgQ2V=
y
dGlmaWNhdGlvbiAtIFNpZ25hdHVyZTAeFw0wOTA0MjQwODU4MDdaFw0xMTA0MjQwODU4MDdaMIG=
T
MQswCQYDVQQGEwJGUjEZMBcGA1UEChMQQmFucXVlIGRlIEZyYW5jZTEcMBoGA1UECxMTU0RFU1M=
g
LSBJTlRFR1JBVElPTjEZMBcGA1UEAxMQREFTU0lFIERvbWluaXF1ZTEwMC4GCSqGSIb3DQEJARY=
h
ZG9taW5pcXVlLmRhc3NpZUBiYW5xdWUtZnJhbmNlLmZyMIGfMA0GCSqGSIb3DQEBAQUAA4GNADC=
B
iQKBgQC8r48rPq5kYQc463CP0A4l1uXMfilYsMgXOkoY+hYYUdhVo3oVJf8xA7IEeAFZiRqkAVo=
c
L/ZjMoft8BKsCA6vUreLEk/I7GVt+AaUq+YHbvh031NP46nWULomr1I22xaNZGh4pEkp8qdC9HQ=
1
sghojyCL8EJZyGMLoWVY1+IvPwIDAQABo4IDHDCCAxgwCwYDVR0PBAQDAgbAMBMGA1UdJQQMMAo=
G
CCsGAQUFBwMEMEgGA1UdEQRBMD+BIWRvbWluaXF1ZS5kYXNzaWVAYmFucXVlLWZyYW5jZS5mcoE=
a
ZG9taW5pcXVlLmRhc3NpZUBiZGYubG9jYWwwFwYDVR0gBBAwDjAMBgoqgXoBc4FIZAECMIICQQY=
D
VR0fBIICODCCAjQwNqA0oDKGMGh0dHA6Ly9hZG9uaXMucGs3LmNlcnRwbHVzLm5ldC9CREYvaWd=
j
YmRmYWMyLmNybDBCoECgPoY8aHR0cDovL2luaWdjYmRmLXV0LmludHJhLWludC5iZGYtaW50Lmx=
v
Y2FsL2NybC9pZ2NiZGZhYzIuY3JsMIG0oIGxoIGuhoGrbGRhcDovLy9DTj1JR0NCREYsQ049Q0E=
y
LENOPUNEUCxDTj1QdWJsaWMlMjBLZXklMjBTZXJ2aWNlcyxDTj1TZXJ2aWNlcyxDTj1Db25maWd=
1
cmF0aW9uLERDPWJkZi1pbnQsREM9bG9jYWw/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXN=
l
P29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MEagRKBChkBodHRwOi8vaW5pZ2NiZGY=
t
dXQuaW50cmEtZGV2MDEuYmRmLWRldjAxLmxvY2FsL2NybC9pZ2NiZGZhYzIuY3JsMIG2oIGzoIG=
w
hoGtbGRhcDovLy9DTj1JR0NCREYsQ049Q0EyLENOPUNEUCxDTj1QdWJsaWMlMjBLZXklMjBTZXJ=
2
aWNlcyxDTj1TZXJ2aWNlcyxDTj1Db25maWd1cmF0aW9uLERDPWJkZi1kZXYwMSxEQz1sb2NhbD9=
j
ZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9=
u
UG9pbnQwHwYDVR0jBBgwFoAUE7ylIIbFUTk9YVNkQsnhCLgP/3cwHQYDVR0OBBYEFIk9kljtNBD=
y
4fZpeRr//F56y7jrMAwGA1UdEwEB/wQCMAAwDQYJKoZIhvcNAQEFBQADggEBAIB4z7CHKThkWYO=
d
Brg9J+FDXqbmtCC2DbVFhq6YRrKiLpiq6CM0/COoQUtsIRdTtovMZtTJMrQSCut+aIxVnvETqko=
m
iD64+gwCjiDqUMw2y9iIMyxFry6G1iPLtNpbhcRcGx9flkf+xGG4FlxdNunJ4zdj4I/R4cpDdp6=
i
0Ag+HEgHfJZWwe0s5PXAIo6jrSD+Meei3rMZeSGiiEAXGTHrbNkJFMGalT9gpw+jP1YnPA5gIwp=
z
UIjhiC2t62i9bFR8GFh5nS8iKcHbk7C3CVaSetgtI/RR/3cRLb7aGe0OwSl6wWNG3Cq5wLVzWB6=
C
LDN3c5wxO2lviARyfrI8+MY=3D
</ds:X509Certificate>
<ds:X509Certificate>
MIIEyzCCA7OgAwIBAgIQAQABARUPg1G2Hi7i10LffTANBgkqhkiG9w0BAQUFADCBmjELMAkGA1U=
E
BhMCRlIxDjAMBgNVBAcTBVBhcmlzMRgwFgYDVQQKEw9CYW5xdWUgZGUgRmFuY2UxJjAkBgNVBAs=
T
HUtFWU5FQ1RJUyBwb3VyIFRFU1Qgc2V1bGVtZW50MTkwNwYDVQQDEzBBdXRvcml0ZSBkZSBDZXJ=
0
aWZpY2F0aW9uIGRlIGxhIEJhbnF1ZSBkZSBGcmFuY2UwHhcNMDQxMjI3MTUwNTI3WhcNMTQxMjI=
3
MTUwNTI3WjCBkDELMAkGA1UEBhMCRlIxDjAMBgNVBAcTBVBhcmlzMRkwFwYDVQQKExBCYW5xdWU=
g
ZGUgRnJhbmNlMSYwJAYDVQQLEx1LRVlORUNUSVMgcG91ciBURVNUIHNldWxlbWVudDEuMCwGA1U=
E
AxMlQXV0b3JpdGUgZGUgQ2VydGlmaWNhdGlvbiAtIFNpZ25hdHVyZTCCASIwDQYJKoZIhvcNAQE=
B
BQADggEPADCCAQoCggEBAOcDbk34aDULkurFE4rmaDw43tyswcuvlm7sKlIy6Iqx9djOitIhYlG=
h
/UauE6v+O9JwribJHaqG1wF8sPY1Og/4ZsF7H6/lsJcZ0fEQuNvNGi8a4viskf0aAnWS0dyLmLj=
A
vwX9DJuO7UcyIfZzdos/5gBrso8vlmpHl6PoNEciJM5nYlJJX7u5r2gsG7lA3rt3MOk7/5qAhxH=
Z
7vCp4+Flv5QCZ3e6Zl+uO8vC2I7MluJrtgzPib12MuRKfy43hPyLg21fIegjWPgw+NvYBMCfuB5=
Z
2EPRVTXI4Bcc1/g/aATYdZ5pzPnAJfvm5PP0R0JGJchGT6cfzs5PbmVk+E8CAwEAAaOCARMwggE=
P
MBIGA1UdEwEB/wQIMAYBAf8CAQAwCwYDVR0PBAQDAgEGMBEGCWCGSAGG+EIBAQQEAwIBBjBDBgN=
V
HR8EPDA6MDigNqA0hjJodHRwOi8vc2VxdW9pYS5wazcuY2VydHBsdXMubmV0L2lnY2JkZi9jcmw=
v
YWMwLmNybDBUBgNVHSAETTBLMEkGCiqBegFzgUgBAQEwOzA5BggrBgEFBQcCARYtaHR0cDovL3d=
3
dy5iYW5xdWUtZnJhbmNlLmZyL2lnY2JkZi9wYy9wYzAucGRmMB0GA1UdDgQWBBQTvKUghsVROT1=
h
U2RCyeEIuA//dzAfBgNVHSMEGDAWgBTiW5kXIcioDJZPj/5jvmRjn0tOmDANBgkqhkiG9w0BAQU=
F
AAOCAQEAKNokCfHKTONNK5p8B2ueI9K07PvSI/nWjtf8KEvSVtaNgbti5C32sYI5EUYP3Z0vcV7=
5
MWfSURuZMXvfQFpvI1Avf2ilJODoJhKqr6pQBoLHvMMftB7WfnoUih0osbNsg8peod18+i/waMV=
4
GMCsOzSUS8uPfG1WDS6HRcVxhn2Q9bZA1YjRVT/kyrKmkwgAR4hOYHBqzYjTOdHBzQv6L7TOvpB=
e
PxeDVemTu33nUE0uWLpr6QOrGaa5+wVMFE+pE8Nu7g+c+iWd9s4whDvBv1dE14ssxI7KMKILJy/=
v
KSwbTvWk6tbu5r/KS3rKus5Vzz40VzOOgKRpBi+eL/wANA=3D=3D
</ds:X509Certificate>
<ds:X509Certificate>
MIIEvTCCA6WgAwIBAgIQAQABARUMQ0J/8yJrX1NN7jANBgkqhkiG9w0BAQUFADCBmjELMAkGA1U=
E
BhMCRlIxDjAMBgNVBAcTBVBhcmlzMRgwFgYDVQQKEw9CYW5xdWUgZGUgRmFuY2UxJjAkBgNVBAs=
T
HUtFWU5FQ1RJUyBwb3VyIFRFU1Qgc2V1bGVtZW50MTkwNwYDVQQDEzBBdXRvcml0ZSBkZSBDZXJ=
0
aWZpY2F0aW9uIGRlIGxhIEJhbnF1ZSBkZSBGcmFuY2UwHhcNMDQxMjI3MTUwMjAwWhcNMjQxMjI=
3
MTUwMjAwWjCBmjELMAkGA1UEBhMCRlIxDjAMBgNVBAcTBVBhcmlzMRgwFgYDVQQKEw9CYW5xdWU=
g
ZGUgRmFuY2UxJjAkBgNVBAsTHUtFWU5FQ1RJUyBwb3VyIFRFU1Qgc2V1bGVtZW50MTkwNwYDVQQ=
D
EzBBdXRvcml0ZSBkZSBDZXJ0aWZpY2F0aW9uIGRlIGxhIEJhbnF1ZSBkZSBGcmFuY2UwggEiMA0=
G
CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCe5WUTFO7pAY50SsfczkebVrrYVpHhUrSJPps0bAX=
t
e5/e7vN9uG9zhUozB68nB+nXpFLhq1okRyKZRhy94OfiHg4uYybypbokg1SMqTrGC/I+aj46aDG=
f
jZ02I1lWyNTurVPkRnCn7gDPpOpuC8oR6DcNt+UgPIRbmTxuHj6zxocMriI2nV7A6HaO0aft49h=
D
1zAmXI6/msr99zHzot7rEAdaB4dVXM+zJEbICHBz//Q4nXhPJ3mDwJYDz8r/Rci6dSY2Qd9xAMD=
J
MuyBqFjQcF1WzSvxpB9RyvnPeo+VdheIB0v65ddpClg+7xCxIyEedWSr7ZggmnZr5vPeZGXJAgM=
B
AAGjgfwwgfkwDwYDVR0TAQH/BAUwAwEB/zALBgNVHQ8EBAMCAQYwQwYDVR0fBDwwOjA4oDagNIY=
y
aHR0cDovL3NlcXVvaWEucGs3LmNlcnRwbHVzLm5ldC9pZ2NiZGYvY3JsL2FjMC5jcmwwVAYDVR0=
g
BE0wSzBJBgoqgXoBc4FIAQEBMDswOQYIKwYBBQUHAgEWLWh0dHA6Ly93d3cuYmFucXVlLWZyYW5=
j
ZS5mci9pZ2NiZGYvcGMvcGMwLnBkZjAdBgNVHQ4EFgQU4luZFyHIqAyWT4/+Y75kY59LTpgwHwY=
D
VR0jBBgwFoAU4luZFyHIqAyWT4/+Y75kY59LTpgwDQYJKoZIhvcNAQEFBQADggEBAFMdzKGPVVl=
O
0UwfOJvQlgwiduX0DWYa6xYRsd57t+8NeXAMiGNhzCs4zPB9OUyplL8wB3WjUafBVAnbUuFvKgl=
n
hcoFbgXAzWuQ2wWXoMBOqI3v+IRa+T885r4o05utdp+cYLRnlj+pxsANBdcHvSMZalk9Kk3Zqi1=
q
ii/gFLDX3+oTNhc3Vl4GU8abtx2Djx1Su76VrxpjOfBPmmwNQTSblxMbBI/ftMSITvu9MuD9bja=
K
yJHuC1ZCy69OO9oVJGRi2Y/LETaHkpPPV78uiZ0JnHIWYGcV4pQNU4Z94YPnHBMQbMILhDVQ7ZF=
/
id1X4i+wOJHBlq67cDsGjJlFfj8=3D
</ds:X509Certificate>
</ds:X509Data>
<ds:KeyValue>
<ds:RSAKeyValue>
<ds:Modulus>
vK+PKz6uZGEHOOtwj9AOJdblzH4pWLDIFzpKGPoWGFHYVaN6FSX/MQOyBHgBWYkapAFaHC/2YzK=
H
7fASrAgOr1K3ixJPyOxlbfgGlKvmB274dN9TT+Op1lC6Jq9SNtsWjWRoeKRJKfKnQvR0NbIIaI8=
g
i/BCWchjC6FlWNfiLz8=3D
</ds:Modulus>
<ds:Exponent>AQAB</ds:Exponent>
</ds:RSAKeyValue>
</ds:KeyValue>
</ds:KeyInfo>
</ds:Signature>
------=_Part_542_1447667749.1430736561148--