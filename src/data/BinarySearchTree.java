/**
 * NESDA Tournament Manager 2013
 *
 * By Zbyněk Stara, 000889-045
 * International School of Prague
 * Czech Republic
 *
 * Computer used:
 * Macbook unibody aluminum late 2008
 * Mac OS X 10.6.8
 * 8 GiB of RAM
 *
 * IDE used:
 * NetBeans 6.9.1
 */

package data;

import java.util.*;

/**
 * The BinarySearchTree is a heavily used abstract data type in this program. It
 * features all necessary functions, including insert(), delete(), search(),
 * contains(), size(), isEmpty(), getNodeData(), getDataArray() or
 * balance().
 *
 * @author Zbyněk Stara
 */
public class BinarySearchTree {
    /**
     * The Node class is a private class of the BinarySearchTree that forms the
     * building blocks of the tree. It stores data designated by a keyString
     * descriptor. Also, it stores references to the successive nodes of the
     * tree hierarchy.
     *
     * @author Zbyněk Stara
     */
    private class Node {
        private Object data = null;
        private String keyString = null;

        private Node left = null; // the smaller one
        private Node right = null; // the larger one

        private Node() {

        }

        private Node(Object data, String keyString) {
            this.data = data;
            this.keyString = keyString;
        }

        /**
         * This method directly sets the data and keyString of this node to be
         * the same as those of the node passed as a parameter.
         *
         * @param node the node whose contents will be copied
         *
         * @author Zbyněk Stara
         */
        private void setNodeContents(Node node) {
            this.data = node.data;
            this.keyString = node.keyString;
        }

        /**
         * This method sets the node's data to be the object specified in
         * parameter.
         *
         * @param data a data object to be stored in this node
         *
         * @author Zbyněk Stara
         */
        private void setNodeData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Key: " + keyString + ", Data: " + data.toString();
        }
    }

    private Node root = null; // the root node of the tree

    private int size = 0; // size of the tree

    private Object [] dataArray; // an array of data objects of nodes in the tree
    private int dataArrayPositionCounter = 0; // the current element in the data array to which to write (used by getDataArrayHelper)
    private boolean dataArrayChanged = true; // indication whether a new dataArray will have to be made (changes were done to the tree)

    private Node [] nodeArray; // an array of nodes in the tree
    private int nodeArrayPositionCounter = 0; // the current element in the node array to which to write (used by getNodeArrayHelper)

    public BinarySearchTree() {

    }

    /**
     * The insert method inserts given data to the tree.
     *
     * @param data object to be stored in the tree
     * @param keyString string identifier of the data, used for initial
     * placement and reference
     * @throws IllegalArgumentException if the keyString is already used in the
     * tree (duplicates are not allowed)
     *
     * @author Zbyněk Stara
     */
    public void insert(Object data, String keyString) throws IllegalArgumentException {
        if (isEmpty()) root = new Node(data, keyString); // if the tree is empty, set the root to be a new node with the data provided
        else root = insertHelper(data, keyString, root); // else, call insert helper (throws IllegalArgumentException if keyString is used)

        // if there was no exception, increase the size of the tree and acknowledge the change in the tree
        size += 1;
        dataArrayChanged = true;
    }

    /**
     * The insertHelper is a recursive method that facilitates the insertion of
     * data to a non-empty tree (it recursively finds an empty spot to add the
     * data to).
     *
     * @param data object to be stored in the tree
     * @param keyString string identifier of the data
     * @param currentNode current node the insertHelper is on
     * @return the current node with the modifications due to the addition
     * @throws IllegalArgumentException if the keyString is already used in the
     * tree (duplicates are not allowed)
     *
     * @author Zbyněk Stara
     */
    private Node insertHelper(Object data, String keyString, Node currentNode) throws IllegalArgumentException {
        if (keyString.compareTo(currentNode.keyString) < 0) { // if the key of the current node is higher than the one that will be added
            // go to the left
            if (currentNode.left == null) { // if the left node is currently null
                // construct a new node there
                currentNode.left = new Node(data, keyString);
                return currentNode;
            } else { // if there is something to the left
                // iterate for that node
                currentNode.left = insertHelper(data, keyString, currentNode.left);
                return currentNode;
            }
        } else if (keyString.compareTo(currentNode.keyString) > 0) { // if the key of the current node is lower than the one that will be added
            // go to the right
            if (currentNode.right == null) { // if the right node is currently null
                // construct a new node there
                currentNode.right = new Node(data, keyString);
                return currentNode;
            } else { // if there is something to the right
                // iterate for that node
                currentNode.right = insertHelper(data, keyString, currentNode.right);
                return currentNode;
            }
        } else { // if key of the current node is the same as the one that is being added
            throw new IllegalArgumentException("The same keyString is aleready used in the tree. They need unique values.");
        }
    }

    /**
     * This method checks whether the tree contains a specified key.
     *
     * @param keyString the string whose presence we want to check for
     * @return a boolean value indicating whether the key was found
     *
     * @author Zbyněk Stara
     */
    public boolean contains(String keyString) {
        // try to find the key, if that fails, return false, else return true
        try {
            searchHelper(keyString, root);
            return true;
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    /**
     * This method looks for a specified key in the tree and returns the data
     * object it is associated with.
     *
     * @param keyString the key to look for
     * @return the data object in the node identified by the key
     * @throws NoSuchElementException if the key was not found
     *
     * @author Zbyněk Stara
     */
    public Object search(String keyString) throws NoSuchElementException {
        // search helper is called
        Node searchNode = searchHelper(keyString, root);
        return searchNode.data; // throws NoSuchElementException if the data does not exist
    }

    /**
     * The searchHelper is a recursive method that facilitates the searching of
     * keys in the tree (it recursively checks whether the key would be to the
     * left or to the right of the current node). If it finds the node with
     * the key, it returns it.
     *
     * @param keyString the string identifier to look for in the tree
     * @param currentNode the node the searchHelper is currently on
     * @return the node that was searched for
     * @throws NoSuchElementException if the key could not be found
     *
     * @author Zbyněk Stara
     */
    private Node searchHelper(String keyString, Node currentNode) throws NoSuchElementException {
        if (currentNode == null) { // if the search has led to an empty node
            throw new NoSuchElementException("The searched data is not in the tree.");
        } else { // if the current node is not an empty node
            if (keyString.compareTo(currentNode.keyString) == 0) { // if the current node is the node that is looked for
                return currentNode;
            } else if (keyString.compareTo(currentNode.keyString) < 0) { // if the key of the current node is higher than the one searched for
                // iterate for the node to the left
                return searchHelper(keyString, currentNode.left);
            } else { // if the key of the current node is lower than the one searched for
                // iterate for the node to the right
                return searchHelper(keyString, currentNode.right);
            }
        }
    }

    /**
     * This method deletes a node with a specified key from the tree.
     *
     * @param keyString the string identifier of the node to be deleted
     * @return the data object of the deleted node
     * @throws NoSuchElementException if the specified key was not found in the
     * tree
     *
     * @author Zbyněk Stara
     */
    public Object delete(String keyString) throws NoSuchElementException {
        if (isEmpty()) { // if the tree is empty
            throw new NoSuchElementException("The data to be deleted is not in the tree.");
        } else if (root.keyString.equals(keyString)) { // if the root will have to be deleted
            // make a temporary root with the current root as a left node
            Node tempRoot = new Node();
            tempRoot.left = root;

            // then call the delete helper on the tempRoot
            Node deletedNode = deleteHelper(keyString, tempRoot, root);

            // after that, set the root to be the left node of the tempRoot
            root = tempRoot.left;

            // decrement size, acknowledge changes
            size -= 1;
            dataArrayChanged = true;

            // return the data object of the deleted node
            return deletedNode.data;
        } else { // if the node to be deleted is any other node than the root
            // call the delete helper (throws NoSuchElementException if the key to be deleted is not in the tree
            Node deletedNode = deleteHelper(keyString, null, root);

            // decrement size, acknowledge changes
            size -= 1;
            dataArrayChanged = true;

            // return the data object of the deleted node
            return deletedNode.data;
        }
    }

    /**
     * The deleteHelper is a recursive method that facilitates the deletion of
     * nodes in the tree (it recursively checks whether the key to be deleted
     * would be to the left or the right of the current node). If it finds the
     * node, it deletes it, and returns its object data. After the deletion, the
     * tree still has a valid organization.
     *
     * @param keyString the key of the node to delete from the tree
     * @param parent the parent node of the current node
     * @param currentNode the currently investigated node
     * @return the deleted node
     * @throws NoSuchElementException if the node to be deleted cannot be found
     * in the tree
     *
     * @author Zbyněk Stara
     */
    private Node deleteHelper(String keyString, Node parent, Node currentNode) throws NoSuchElementException {
        if (keyString.compareTo(currentNode.keyString) < 0) { // if the key of the current node is higher than the one searched for
            //
            if (currentNode.left != null) {
                return deleteHelper(keyString, currentNode, currentNode.left);
            } else {
                throw new NoSuchElementException("The data to be deleted is not in the tree.");
            }

        } else if (keyString.compareTo(currentNode.keyString) > 0) { // if the key of the current node is lower than the one searched for
            if (currentNode.right != null) {
                return deleteHelper(keyString, currentNode, currentNode.right);
            } else {
                throw new NoSuchElementException("The data to be deleted is not in the tree.");
            }
            
        } else { // key is equal to what was being looked for - correct data found
            Node deletedNode = new Node();
            deletedNode.setNodeContents(currentNode);

            if (currentNode.left != null && currentNode.right != null) {
                currentNode.setNodeContents(minNode(currentNode.right)); // set currentNode to have data of the minimunm node to the right
                
                deleteHelper(currentNode.keyString, currentNode, currentNode.right); // delete the smallest value on its original position
            }

            // one or both of the left/right nodes is missing
            else if (parent.left == currentNode) { // if we are to the left from the parent
                parent.left = (currentNode.left != null) ? currentNode.left : currentNode.right;
                    // if left is not null, set this to be left
                    // else set this to be right
            }
            
            else if (parent.right == currentNode) { // if we are to the right from the parent
                parent.right = (currentNode.left != null) ? currentNode.left : currentNode.right;
                    // if left is not null, set this to be left
                    // else set this to be right
            }
            return deletedNode;
        }
    }

    /**
     * This method returns the smallest node from the current node.
     *
     * @param currentNode the node from which to start looking
     * @return the smallest node (leftmost) from the current node; if the left
     * node is null, the current node is returned
     *
     * @author Zbyněk Stara
     */
    private Node minNode(Node currentNode) {
        if (currentNode.left != null) {
            return minNode(currentNode.left);
        } else { // if the node to the left is null
            return currentNode;
        }
    }

    /**
     * This method sets the data object of a node at a given index
     *
     * @param index int value specifying the index of the node that should be
     * changed
     * @param data object with the data to be stored at that node
     * @throws IllegalArgumentException if illegal value of index is provided
     *
     * @author Zbyněk Stara
     */
    public void setNodeData(int index, Object data) throws IllegalArgumentException {
        if (index >= size || index < 0) {
            throw new IllegalArgumentException();
        } else {
            Node [] localNodeArray = getNodeArray();
            localNodeArray[index].setNodeData(data);

            dataArrayChanged = true;
        }
    }

    /**
     * This method checks whether the tree is empty.
     *
     * @return true if the tree is empty, false if it is not
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * This method returns the number of nodes in the tree.
     *
     * @return an int value with the size of the tree.
     */
    public int size() {
        return size;
    }

    /**
     * This method returns the data object of a node at a specified index.
     *
     * @param index an int value specifying the index of the node whose data is
     * requested
     * @return the data object of the node at the index
     * @throws IllegalArgumentException if the index has an illegal value
     *
     * @author Zbyněk Stara
     */
    public Object getNodeData(int index) throws IllegalArgumentException {
        if (index >= size || index < 0) {
            throw new IllegalArgumentException();
        } else {
            if (!dataArrayChanged) {
                return dataArray[index];
            } else {
                getDataArray();
                return dataArray[index];
            }
        }
    }

    /**
     * This method returns an array of the data stored in all nodes in the tree,
     * in order according to the nodes' key strings.
     *
     * @return an object array with the data of all nodes in the tree
     *
     * @author Zbyněk Stara
     */
    public Object [] getDataArray() {
        dataArray = new Object [size];

        if (!this.isEmpty()) {
            dataArrayPositionCounter = 0;
            getDataArrayHelper(root);
            dataArrayChanged = false;
        }

        return dataArray;
    }

    /**
     * This is a helper method that facilitates the getting of the data array of
     * the tree. It recursively traverses the tree and adds every node's data
     * object to the data array.
     *
     * @param node the currently explored node
     *
     * @author Zbyněk Stara
     */
    private void getDataArrayHelper(Node node) {
        if (node.left != null) getDataArrayHelper(node.left);
        dataArray[dataArrayPositionCounter] = node.data;
        dataArrayPositionCounter += 1;
        if (node.right != null) getDataArrayHelper(node.right);
    }

    /**
     * This method facilitates the getting of a node array of the tree.
     *
     * @return a node array with all nodes of the tree
     *
     * @author Zbyněk Stara
     */
    private Node [] getNodeArray() {
        nodeArray = new Node [size];
        nodeArrayPositionCounter = 0;
        getNodeArrayHelper(root);
        return nodeArray;
    }

    /**
     * This is a helper method that facilitates the getting of the node array of
     * the tree. It recursively traverses the tree and adds every node to the
     * node array
     *
     * @param node the current node
     */
    private void getNodeArrayHelper(Node node) {
        if (node.left != null) getNodeArrayHelper(node.left);
        nodeArray[nodeArrayPositionCounter] = new Node(node.data, node.keyString);
        nodeArrayPositionCounter += 1;
        if (node.right != null) getNodeArrayHelper(node.right);
    }

    /**
     * This method "balances" the tree. That means that the tree is made as
     * compact as possible by adding nodes from the node array in an organized
     * fashion. The root is the node in the middle of the tree, its two children
     * are nodes at the boundary of the first and third quartile, and so on,
     * until there are no more nodes left in the node array.
     * <p>
     * Since this algorithm is not very effective (converting a tree to an array
     * and then back), it should be used scarcely.
     *
     * @return a BinarySearchTree with the new organization of nodes
     *
     * @author Zbyněk Stara
     */
    public BinarySearchTree balance() {
        BinarySearchTree newTree = new BinarySearchTree();
        if (!this.isEmpty()) {
            Node[] elementArray = this.getNodeArray();

            int exp = 0;
            int num = 0;
            int remainder = 0;

            exp = (int) Math.floor(Math.log(elementArray.length)/Math.log(2));

            num = ((int) Math.pow(2, exp)) - 1;
            remainder = elementArray.length - num;

            exp -= 1;

            for (; exp >= 0; exp--) {
                int indexInterval = (int) Math.pow(2, exp);
                int preElementIndex = indexInterval;
                while ((preElementIndex + remainder - 1) < elementArray.length) {
                    int elementIndex;

                    if (remainder >= preElementIndex) {
                        elementIndex = preElementIndex + preElementIndex;
                    } else {
                        elementIndex = preElementIndex + remainder;
                    }
                    elementIndex -= 1;

                    if (elementIndex < elementArray.length && elementIndex >= 0) {
                        if (elementArray[elementIndex] != null) {
                            newTree.insert(elementArray[elementIndex].data, elementArray[elementIndex].keyString);
                            elementArray[elementIndex] = null;
                        } else {
                            preElementIndex += indexInterval;
                            continue;
                        }
                    } else break;

                    preElementIndex += indexInterval;
                }
            }

            int i = 0;
            while (remainder > 0) {
                if (elementArray[i] != null) {
                    newTree.insert(elementArray[i].data, elementArray[i].keyString);
                    elementArray[i] = null;
                    remainder -= 1;
                }

                i += 2;
            }

            return newTree;
        } else {
            return newTree;
        }
    }

    /**
     * This method is used for debugging. It returns a string that is the size
     * of the tree.
     *
     * @return string with the size of the tree
     */
    @Override
    public String toString() {
        Object [] tempDataArray = getDataArray();
        String dataArrayString = "";
        if (isEmpty()) dataArrayString = "<empty>";
        else {
            for (int i = 0; i < tempDataArray.length; i++) {
                if (i > 0) dataArrayString += ", ";
                dataArrayString += "{" + tempDataArray[i].toString() + "}";
            }
        }
        return "Size: " + size + ", Data: {" + dataArrayString + "}";
    }
}
