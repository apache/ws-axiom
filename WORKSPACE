#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

workspace(name = "axiom")

load("//buildutils:workspace.bzl", "new_patched_http_archive")

maven_jar(
    name = "asm",
    artifact = "org.ow2.asm:asm:5.2",
    sha1 = "4ce3ecdc7115bcbf9d4ff4e6ec638e60760819df",
)

maven_jar(
    name = "aspectj_runtime",
    artifact = "org.aspectj:aspectjrt:1.8.7",
    sha1 = "c4b70e763194d274477da4da9b05ea913e877268",
)

maven_jar(
    name = "aspectj_tools",
    artifact = "org.aspectj:aspectjtools:1.8.7",
    sha1 = "67aa2f0aeb0e5c1ee7fadb5b0a29a999a31bb6e2",
)

maven_jar(
    name = "bnd",
    artifact = "biz.aQute.bnd:biz.aQute.bnd:3.3.0",
    sha1 = "aa45ab294fe338bf86485549e8996bee5a8765ce",
)

maven_jar(
    name = "byte_buddy",
    artifact = "net.bytebuddy:byte-buddy:jar:1.4.26",
    sha1 = "c56b0e90e3b6f9f117a0a0356674e86e86ba8652",
)

maven_jar(
    name = "commons_codec",
    artifact = "commons-codec:commons-codec:1.10",
    sha1 = "4b95f4897fa13f2cd904aee711aeafc0c5295cd8",
)

maven_jar(
    name = "commons_io",
    artifact = "commons-io:commons-io:2.2",
    sha1 = "83b5b8a7ba1c08f9e8c8ff2373724e33d3c1e22a",
)

maven_jar(
    name = "commons_logging",
    artifact = "commons-logging:commons-logging:1.2",
    sha1 = "4bfc12adfe4842bf07b657f0369c4cb522955686",
)

maven_jar(
    name = "crimson",
    artifact = "crimson:crimson:1.1.3",
    sha1 = "31e3dac9777abfec809ad9315f8b5d283cd46c40",
)

maven_jar(
    name = "dom4j",
    artifact = "dom4j:dom4j:1.6.1",
    sha1 = "5d3ccc056b6f056dbf0dddfdf43894b9065a8f94",
)

maven_jar(
    name = "easymock",
    artifact = "org.easymock:easymock:3.1",
    sha1 = "3e127311a86fc2e8f550ef8ee4abe094bbcf7e7e",
)

maven_jar(
    name = "felix",
    artifact = "org.apache.felix:org.apache.felix.framework:5.0.0",
    sha1 = "9fabb10642ca45792b0041d5a660b131282869db"
)

maven_jar(
    name = "guava",
    artifact = "com.google.guava:guava:20.0",
    sha1 = "89507701249388e1ed5ddcf8c41f4ce1be7831ef",
)

maven_jar(
    name = "hamcrest",
    artifact = "org.hamcrest:hamcrest-core:jar:1.3",
    sha1 = "42a25dc3219429f0e5d060061f71acb49bf010a0",
)

maven_jar(
    name = "httpclient",
    artifact = "org.apache.httpcomponents:httpclient:4.3.4",
    sha1 = "a9a1fef2faefed639ee0d0fba5b3b8e4eb2ff2d8",
)

maven_jar(
    name = "httpclient3",
    artifact = "commons-httpclient:commons-httpclient:3.1",
    sha1 = "964cd74171f427720480efdec40a7c7f6e58426a",
)

maven_jar(
    name = "httpcore",
    artifact = "org.apache.httpcomponents:httpcore:4.3.2",
    sha1 = "31fbbff1ddbf98f3aa7377c94d33b0447c646b6e",
)

maven_jar(
    name = "jacoco_agent",
    artifact = "org.jacoco:org.jacoco.agent:0.7.9",
    sha1 = "4a936caab50b117a14d9ca3a725fc9b54d0cc3d1",
)

maven_jar(
    name = "javamail",
    artifact = "org.apache.geronimo.specs:geronimo-javamail_1.4_spec:1.7.1",
    sha1 = "43ad4090b1a07a11c82ac40c01fc4e2fbad20013",
)

maven_jar(
    name = "jaxen",
    artifact = "jaxen:jaxen:1.1.6",
    sha1 = "3f8c36d9a0578e8e98f030c662b69888b1430ac0",
)

maven_jar(
    name = "jdom2",
    artifact = "org.jdom:jdom2:2.0.5",
    sha1 = "2001db51c131e555bafdb77fc52af6a9408c505e",
)

maven_jar(
    name = "jetty",
    artifact = "org.mortbay.jetty:jetty:6.1.26",
    sha1 = "2f546e289fddd5b1fab1d4199fbb6e9ef43ee4b0",
)

maven_jar(
    name = "jetty_util",
    artifact = "org.mortbay.jetty:jetty-util:6.1.26",
    sha1 = "e5642fe0399814e1687d55a3862aa5a3417226a9",
)

maven_jar(
    name = "junit",
    artifact = "junit:junit:4.12",
    sha1 = "2973d150c0dc1fefe998f834810d68f278ea58ec",
)

maven_jar(
    name = "log4j",
    artifact = "log4j:log4j:1.2.17",
    sha1 = "5af35056b4d257e4b64b9e8069c0746e8b08629f",
)

maven_jar(
    name = "mime4j_core",
    artifact = "org.apache.james:apache-mime4j-core:0.8.0",
    sha1 = "d54f45fca44a2f210569656b4ca3574b42911c95",
)

maven_jar(
    name = "mockito_core",
    artifact = "org.mockito:mockito-core:2.1.0",
    sha1 = "a8ca233b9d89b6e610b2e29a33ee78698f804843",
)

maven_jar(
    name = "objenesis",
    artifact = "org.objenesis:objenesis:jar:2.4",
    sha1 = "2916b6c96b50c5b3ec4452ed99401db745aabb27",
)

maven_jar(
    name = "ops4j_io",
    artifact = "org.ops4j.base:ops4j-base-io:1.5.0",
    sha1 = "15acc9a1b56c8963db471cee926d7001591e6b4d",
)

maven_jar(
    name = "ops4j_lang",
    artifact = "org.ops4j.base:ops4j-base-lang:1.5.0",
    sha1 = "da31d176ffa8b78c0b83e183951c86cbd7bfb0b9",
)

maven_jar(
    name = "ops4j_store",
    artifact = "org.ops4j.base:ops4j-base-store:1.5.0",
    sha1 = "7c5d6ed88638a61b15b3c285b8c16eee7753de1c",
)

maven_jar(
    name = "ops4j_util_property",
    artifact = "org.ops4j.base:ops4j-base-util-property:1.5.0",
    sha1 = "10a2f7cfa055e776eb996ca456747a07fdf2015e",
)

maven_jar(
    name = "osgi_core",
    artifact = "org.osgi:org.osgi.core:5.0.0",
    sha1 = "6e5e8cd3c9059c08e1085540442a490b59a7783c",
)

maven_jar(
    name = "pax_exam",
    artifact = "org.ops4j.pax.exam:pax-exam:4.9.1",
    sha1 = "3311a0d0e4e949fcebd332511c8ba1911e289cf7",
)

maven_jar(
    name = "pax_exam_container_native",
    artifact = "org.ops4j.pax.exam:pax-exam-container-native:4.9.1",
    sha1 = "8d8b17bba21e9c4a79633d337104515aa79089b4",
)

maven_jar(
    name = "pax_exam_junit4",
    artifact = "org.ops4j.pax.exam:pax-exam-junit4:4.9.1",
    sha1 = "8341f035345f7ffa89db0b24bbd7b6ddc52c2cec",
)

maven_jar(
    name = "pax_exam_link_assembly",
    artifact = "org.ops4j.pax.exam:pax-exam-link-assembly:4.9.1",
    sha1 = "b2b002626b77ce7e2aa236af12c8328df014162a",
)

maven_jar(
    name = "pax_exam_spi",
    artifact = "org.ops4j.pax.exam:pax-exam-spi:4.9.1",
    sha1 = "211d0d89b05842a88451bd6bb4723270a10aae6c",
)

maven_jar(
    name = "pax_swissbox_tracker",
    artifact = "org.ops4j.pax.swissbox:pax-swissbox-tracker:1.8.2",
    sha1 = "699d52d350f2377b30a1927a52c4b43ca4d8e5c5",
)

maven_jar(
    name = "pax_tinybundles",
    artifact = "org.ops4j.pax.tinybundles:tinybundles:2.1.1",
    sha1 = "d894c29d13f0d7a9094793c25a0a7723b9537c0b",
)

maven_jar(
    name = "pax_url_classpath",
    artifact = "org.ops4j.pax.url:pax-url-classpath:2.4.5",
    sha1 = "fa99960fad95b2f8cf64c380a74bf845d9084a27",
)

maven_jar(
    name = "pax_url_commons",
    artifact = "org.ops4j.pax.url:pax-url-commons:2.4.5",
    sha1 = "342030b66367f84c82ca5b82cb7e230660156766",
)

maven_jar(
    name = "pax_url_link",
    artifact = "org.ops4j.pax.url:pax-url-link:2.4.5",
    sha1 = "eb9065c74a008e641389366748dba5cc6630ce8c",
)

maven_jar(
    name = "saxon",
    artifact = "net.sf.saxon:Saxon-HE:9.7.0-18",
    sha1 = "b1742ac9973d71bc23b1e8bc3e870a0994fc0ab2",
)

maven_jar(
    name = "servlet",
    artifact = "javax.servlet:javax.servlet-api:3.1.0",
    sha1 = "3cd63d075497751784b2fa84be59432f4905bf7c",
)

maven_jar(
    name = "slf4j_api",
    artifact = "org.slf4j:slf4j-api:1.5.11",
    sha1 = "d6a855b608971025b4fbb0970f829391cc6f727a",
)

maven_jar(
    name = "slf4j_jdk14",
    artifact = "org.slf4j:slf4j-jdk14:1.5.11",
    sha1 = "a2106f2feaea391154e3a47d3db667b45519231f",
)

maven_jar(
    name = "spring_aop",
    artifact = "org.springframework:spring-aop:4.3.9.RELEASE",
    sha1 = "95f5f5cf3cae64266a89dc1bc9e0484425cd8358",
)

maven_jar(
    name = "spring_beans",
    artifact = "org.springframework:spring-beans:4.3.9.RELEASE",
    sha1 = "daa5abf3779c8cad1a2910e1ea08e4272489d8ae",
)

maven_jar(
    name = "spring_context",
    artifact = "org.springframework:spring-context:4.3.9.RELEASE",
    sha1 = "a186823724f03b98becd5f93b1fa107fe6f7a7ff",
)

maven_jar(
    name = "spring_core",
    artifact = "org.springframework:spring-core:4.3.9.RELEASE",
    sha1 = "430b7298bfb85d66fb61e19ca8f06231b911e9f5",
)

maven_jar(
    name = "spring_oxm",
    artifact = "org.springframework:spring-oxm:4.3.9.RELEASE",
    sha1 = "73ada2015865c0aaff419c4ed28bac7257f1bc85",
)

maven_jar(
    name = "spring_test",
    artifact = "org.springframework:spring-test:4.3.9.RELEASE",
    sha1 = "35bf4c38c9245f5baeeda4bea7c41f4f33c5daf3",
)

maven_jar(
    name = "spring_web",
    artifact = "org.springframework:spring-web:4.3.9.RELEASE",
    sha1 = "91dae64c4280093ad5fb4736a10913c9233479c1",
)

maven_jar(
    name = "spring_webmvc",
    artifact = "org.springframework:spring-webmvc:4.3.9.RELEASE",
    sha1 = "ca80b4a00abc388d8046bf372099f35564371c47",
)

new_patched_http_archive(
    name = "spring_ws",
    urls = ["https://github.com/spring-projects/spring-ws/archive/v2.4.0.RELEASE.tar.gz"],
    sha256 = "83fe955ae3fc0e437e460164bb942ad3307d3942fe01b8e93a7c5335618dbb67",
    build_file = "//third_party:spring-ws.BUILD",
    strip_prefix = "spring-ws-2.4.0.RELEASE",
    patch = "//third_party:spring-ws.patch",
)

maven_jar(
    name = "stax2_api",
    artifact = "org.codehaus.woodstox:stax2-api:3.1.1",
    sha1 = "0466eab062e9d1a3ce2c4631b6d09b5e5c0cbd1b",
)

maven_jar(
    name = "truth",
    artifact = "com.google.truth:truth:0.33",
    sha1 = "66c978542e1c4c0b72508c5e3bd7d36481090171",
)

maven_jar(
    name = "woodstox",
    artifact = "org.codehaus.woodstox:woodstox-core-asl:4.2.0",
    sha1 = "7a3784c65cfa5c0553f31d000b43346feb1f4ee3",
)

maven_jar(
    name = "wsdl4j",
    artifact = "wsdl4j:wsdl4j:1.6.3",
    sha1 = "6d106a6845a3d3477a1560008479312888e94f2f",
)

maven_jar(
    name = "xalan",
    artifact = "xalan:xalan:2.7.1",
    sha1 = "75f1d83ce27bab5f29fff034fc74aa9f7266f22a",
)

maven_jar(
    name = "xalan_serializer",
    artifact = "xalan:serializer:2.7.1",
    sha1 = "4b4b18df434451249bb65a63f2fb69e215a6a020",
)

maven_jar(
    name = "xerces",
    artifact = "xerces:xercesImpl:2.11.0",
    sha1 = "9bb329db1cfc4e22462c9d6b43a8432f5850e92c",
)

maven_jar(
    name = "xml_apis",
    artifact = "xml-apis:xml-apis:jar:1.4.01",
    sha1 = "3789d9fada2d3d458c4ba2de349d48780f381ee3",
)

maven_jar(
    name = "xmlbeans",
    artifact = "org.apache.xmlbeans:xmlbeans:2.3.0",
    sha1 = "8704dcf5c9f10265a08f5020b0fab70eb64ac3c4",
)

maven_jar(
    name = "xmlschema",
    artifact = "org.apache.ws.xmlschema:xmlschema-core:2.1.0",
    sha1 = "93415557e2867469c33be98ab330655dd714297d",
)

maven_jar(
    name = "xmlunit",
    artifact = "xmlunit:xmlunit:1.5",
    sha1 = "7789cef5caffdecab50fd6099535ad2bc2e98044",
)

maven_jar(
    name = "xom",
    artifact = "xom:xom:1.2.5",
    sha1 = "4166493b9f04e91b858ba4150b28b4d197f8f8ea",
)
