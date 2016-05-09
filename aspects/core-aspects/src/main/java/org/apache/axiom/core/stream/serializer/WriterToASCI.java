/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.axiom.core.stream.serializer;

import java.io.IOException;
import java.io.OutputStream;



/**
 * This class writes ASCII to a byte stream as quickly as possible.  For the
 * moment it does not do buffering, though I reserve the right to do some
 * buffering down the line if I can prove that it will be faster even if the
 * output stream is buffered.
 * 
 * This class is only used internally within Xalan.
 * 
 * @xsl.usage internal
 */
final class WriterToASCI extends XmlWriter
{

  /** The byte stream to write to.  */
  private final OutputStream m_os;

  /**
   * Create an unbuffered ASCII writer.
   *
   *
   * @param os The byte stream to write to.
   */
  WriterToASCI(OutputStream os)
  {
    m_os = os;
  }

  /**
   * Write a portion of an array of characters.
   *
   * @param  chars  Array of characters
   * @param  start   Offset from which to start writing characters
   * @param  length   Number of characters to write
   *
   * @exception  IOException  If an I/O error occurs
   *
   * @throws java.io.IOException
   */
  void write(char chars[], int start, int length)
          throws java.io.IOException
  {

    int n = length+start;

    for (int i = start; i < n; i++)
    {
      m_os.write(chars[i]);
    }
  }

  /**
   * Write a single character.  The character to be written is contained in
   * the 16 low-order bits of the given integer value; the 16 high-order bits
   * are ignored.
   *
   * <p> Subclasses that intend to support efficient single-character output
   * should override this method.
   *
   * @param c  int specifying a character to be written.
   * @exception  IOException  If an I/O error occurs
   */
  void write(char c) throws IOException
  {
    m_os.write(c);
  }

  /**
   * Write a string.
   *
   * @param  s String to be written
   *
   * @exception  IOException  If an I/O error occurs
   */
  void write(String s) throws IOException
  {
    int n = s.length();
    for (int i = 0; i < n; i++)
    {
      m_os.write(s.charAt(i));
    }
  }

  void flushBuffer() {
  }
}
