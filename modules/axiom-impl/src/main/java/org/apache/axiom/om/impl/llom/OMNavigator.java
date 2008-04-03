/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;

/**
 * Refer to the test, org.apache.axiom.om.OMNavigatorTest, to find out how to use features like
 * isNavigable, isComplete and step.
 */
public class OMNavigator {
    /** Field node */
    protected OMNode node;

    /** Field visited */
    private boolean visited;

    /** Field next */
    private OMNode next;

    // root is the starting element. Once the navigator comes back to the
    // root, the traversal is terminated

    /** Field root */
    private OMNode root;

    /** Field backtracked */
    private boolean backtracked;

    // flags that tell the status of the navigator

    /** Field end */
    private boolean end = false;

    /** Field start */
    private boolean start = true;

    /** Constructor OMNavigator. */
    public OMNavigator() {
    }

    /**
     * Constructor OMNavigator.
     *
     * @param node
     */
    public OMNavigator(OMNode node) {
        init(node);
    }

    /**
     * Method init.
     *
     * @param node
     */
    public void init(OMNode node) {
        next = node;
        root = node;
        backtracked = false;
    }

    /**
     * Gets the next node.
     *
     * @return Returns OMnode in the sequence of preorder traversal. Note however that an element
     *         node is treated slightly differently. Once the element is passed it returns the same
     *         element in the next encounter as well.
     */
    public OMNode next() {
        if (next == null) {
            return null;
        }
        node = next;
        visited = backtracked;
        backtracked = false;
        updateNextNode();

        // set the starting and ending flags
        if (root.equals(node)) {
            if (!start) {
                end = true;
            } else {
                start = false;
            }
        }
        return node;
    }

    /** Private method to encapsulate the searching logic. */
    private void updateNextNode() {

        if ((next instanceof OMElement) && !visited) {
            OMNode firstChild = _getFirstChild((OMElement) next);
            if (firstChild != null) {
                next = firstChild;
            } else if (next.isComplete()) {
                backtracked = true;
            } else {
                next = null;
            }
        } else {
            OMContainer parent = next.getParent();
            OMNode nextSibling = getNextSibling(next);
            if (nextSibling != null) {
                next = nextSibling;
            } else if ((parent != null) && parent.isComplete() && !(parent instanceof OMDocument)) {
                next = (OMNodeImpl) parent;
                backtracked = true;
            } else {
                next = null;
            }
        }
    }

    /**
     * @param node
     * @return first child or null
     */
    private OMNode _getFirstChild(OMElement node) {
        if (node instanceof OMSourcedElement) {
            OMNode first = node.getFirstOMChild();
            OMNode sibling = first;
            while (sibling != null) {
                sibling = sibling.getNextOMSibling();
            }
            return first;
        } else {
            // Field access is used to prevent advancing the parser.
            // Some tests fail if the following is used
            // return node.getFirstOMChild()
            return ((OMElementImpl) node).firstChild;
        }
    }

    /**
     * @param node
     * @return next sibling or null
     */
    private OMNode getNextSibling(OMNode node) {
        if (node instanceof OMSourcedElement) {
            return node.getNextOMSibling();
        } else {
            // Field access is used to prevent advancing the parser.
            // Some tests fail if the following is used
            // return node.getNextOMSibling()
            return ((OMNodeImpl) node).nextSibling;
        }
    }

    /**
     * Method visited.
     *
     * @return Returns boolean.
     */
    public boolean visited() {
        return visited;
    }

    /**
     * This is a very special method. This allows the navigator to step once it has reached the
     * existing OM. At this point the isNavigable method will return false but the isComplete method
     * may return false which means that the navigating the given element is not complete and the
     * navigator cannot proceed.
     */
    public void step() {
        if (!end) {
            next = node;
            updateNextNode();
        }
    }

    /**
     * Returns the navigable status.
     *
     * @return Returns boolean.
     */
    public boolean isNavigable() {
        if (end) {
            return false;
        } else {
            return !(next == null);
        }
    }

    /**
     * Returns the completed status.
     *
     * @return Returns boolean.
     */
    public boolean isCompleted() {
        return end;
    }
}
