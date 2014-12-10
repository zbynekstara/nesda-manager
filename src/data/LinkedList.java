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
 * Little used data structure.
 *
 * @author Zbynda
 */
public class LinkedList {
    /**
     * The Node class is a private class of the LinkedLise that forms the
     * building blocks of the list. It stores data designated by a keyString
     * descriptor. Also, it stores references to the successive node of the
     * list.
     *
     * @author Zbyněk Stara
     */
    private class Node {
        private Object data = null;
        private Node next = null;

        public Node() {

        }

        public Node(Object data) {
            this.data = data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Object getData() {
            return data;
        }

        public Node getNext() {
            return next;
        }

        @Override
        public String toString() {
            return "Data: " + data.toString();
        }
    }

    private Node head = null;

    private int size = 0;

    private Object [] dataArray;
    private boolean dataArrayChanged = true;

    public LinkedList() {

    }

    public boolean isEmpty() {
        return (head == null);
    }

    public void insertAtFront(Object data) {
        Node temp = new Node(data);
        temp.setNext(head);
        head = temp;
        size += 1;
        dataArrayChanged = true;
    }

    public void insertAtEnd(Object data) {
        if (!isEmpty()) {
            Node previous = head;
            while (previous.getNext() != null) {
                previous = previous.getNext();
            }
            Node current = new Node(data);
            previous.setNext(current);
            size += 1;
            dataArrayChanged = true;
        } else {
            insertAtFront(data);
        }
    }

    public Object removeFirst() {
        size -= 1;
        dataArrayChanged = true;
        Node temp = head;
        head = head.getNext();
        return temp.getData();
    }

    public Object removeNode(int index) throws IllegalArgumentException {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index out of bounds.");
        } else if (index == 0) {
            return removeFirst();
        } else {
            size -= 1;
            dataArrayChanged = true;
            Node currentNode = head;
            Node nextNode = currentNode.getNext();
            for (int i = 0; i < index-1; i++) {
                currentNode = currentNode.getNext();
                nextNode = nextNode.getNext();
                // ends when nextNode is the node to delete and currentNode is the one before that
            }
            
            if (nextNode != null) currentNode.setNext(nextNode.getNext()); // set currentNode's next to be nextNode's next = close the gap
            else currentNode.setNext(null);

            return nextNode.getData(); // reutrn the data of the nextNode
        }
    }

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

    public Object [] getDataArray() {
        dataArray = new Object[size];

        if (!this.isEmpty()) {
            Node currentNode = head;
            dataArray[0] = currentNode.getData();

            int counter = 0;
            while (currentNode.next != null) {
                counter += 1;
                currentNode = currentNode.next;
                dataArray[counter] = currentNode.getData();
            }

            dataArrayChanged = false;
        }

        return dataArray;
    }

    public int size() {
        return size;
    }

    /*public void setHead(Node head) {
        this.head = head;
    }*/

    /*public Node getHead() {
        return head;
    }*/

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
