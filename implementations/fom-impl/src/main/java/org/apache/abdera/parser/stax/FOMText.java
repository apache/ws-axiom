/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.parser.stax;

import static org.apache.abdera.util.Constants.BASE;
import static org.apache.abdera.util.Constants.LANG;
import static org.apache.abdera.util.Constants.TYPE;
import static org.apache.abdera.util.Constants.XHTML_NS;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.Constants;
import org.apache.axiom.fom.AbderaDiv;
import org.apache.axiom.fom.AbderaText;
import org.apache.axiom.fom.IRIUtil;
import org.apache.axiom.om.OMNode;

@SuppressWarnings("unchecked")
public class FOMText extends FOMElement implements AbderaText {
    protected Type type = Type.TEXT;

    public final Type getTextType() {
        return type;
    }

    public Text setTextType(Type type) {
        this.type = type;
        if (Type.TEXT.equals(type))
            setAttributeValue(TYPE, "text");
        else if (Type.HTML.equals(type))
            setAttributeValue(TYPE, "html");
        else if (Type.XHTML.equals(type))
            setAttributeValue(TYPE, "xhtml");
        else
            removeAttribute(TYPE);
        return this;
    }

    public Div getValueElement() {
        return (Div)_getFirstChildWithName(Constants.DIV);
    }

    public Text setValueElement(Div value) {
        if (value != null) {
            if (_getFirstChildWithName(Constants.DIV) != null)
                _getFirstChildWithName(Constants.DIV).discard();
            setTextType(Text.Type.XHTML);
            removeChildren();
            _addChild((AbderaDiv)value);
        } else
            _removeAllChildren();
        return this;
    }

    public String getValue() {
        String val = null;
        if (Type.TEXT.equals(type)) {
            val = getText();
        } else if (Type.HTML.equals(type)) {
            val = getText();
        } else if (Type.XHTML.equals(type)) {
            FOMDiv div = (FOMDiv)_getFirstChildWithName(Constants.DIV);
            val = (div != null) ? div.getInternalValue() : null;
        }
        return val;
    }

    // TODO: the AspectJ compiler doesn't like this
//    public <T extends Element> T setText(String value) {
//        return (T)setText(Text.Type.TEXT, value);
//    }

    public <T extends Element> T setText(Text.Type type, String value) {
        setTextType(type);
        if (value != null) {
            OMNode child = this.getFirstOMChild();
            while (child != null) {
                if (child.getType() == OMNode.TEXT_NODE) {
                    child.detach();
                }
                child = child.getNextOMSibling();
            }
            getOMFactory().createOMText(this, value);
        } else
            _removeAllChildren();
        return (T)this;
    }

    public Text setValue(String value) {
        if (value != null) {
            if (Type.TEXT.equals(type)) {
                setText(type, value);
            } else if (Type.HTML.equals(type)) {
                setText(type, value);
            } else if (Type.XHTML.equals(type)) {
                IRI baseUri = null;
                value = "<div xmlns=\"" + XHTML_NS + "\">" + value + "</div>";
                Element element = null;
                try {
                    baseUri = getResolvedBaseUri();
                    element = _parse(value, baseUri);
                } catch (Exception e) {
                }
                if (element != null && element instanceof Div)
                    setValueElement((Div)element);
            }
        } else
            _removeAllChildren();
        return this;
    }

    public String getWrappedValue() {
        if (Type.XHTML.equals(type)) {
            return _getFirstChildWithName(Constants.DIV).toString();
        } else {
            return getValue();
        }
    }

    public Text setWrappedValue(String wrappedValue) {
        if (Type.XHTML.equals(type)) {
            IRI baseUri = null;
            Element element = null;
            try {
                baseUri = getResolvedBaseUri();
                element = _parse(wrappedValue, baseUri);
            } catch (Exception e) {
            }

            if (element != null && element instanceof Div)
                setValueElement((Div)element);
        } else {
            setValue(wrappedValue);
        }
        return this;
    }

    @Override
    public IRI getBaseUri() {
        if (Type.XHTML.equals(type)) {
            Element el = getValueElement();
            if (el != null) {
                if (el.getAttributeValue(BASE) != null) {
                    if (getAttributeValue(BASE) != null)
                        return super.getBaseUri().resolve(el.getAttributeValue(BASE));
                    else
                        return IRIUtil.getUriValue(el.getAttributeValue(BASE));
                }
            }
        }
        return super.getBaseUri();
    }

    @Override
    public IRI getResolvedBaseUri() {
        if (Type.XHTML.equals(type)) {
            Element el = getValueElement();
            if (el != null) {
                if (el.getAttributeValue(BASE) != null) {
                    return super.getResolvedBaseUri().resolve(el.getAttributeValue(BASE));
                }
            }
        }
        return super.getResolvedBaseUri();
    }

    @Override
    public String getLanguage() {
        if (Type.XHTML.equals(type)) {
            Element el = getValueElement();
            if (el != null && el.getAttributeValue(LANG) != null)
                return el.getAttributeValue(LANG);
        }
        return super.getLanguage();
    }

    @Override
    public Object clone() {
        FOMText text = (FOMText)super.clone();
        text.type = type;
        return text;
    }

}
