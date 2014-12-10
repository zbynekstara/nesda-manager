/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import data.*;
import java.awt.Toolkit;

/**
 *
 * @author Zbyněk Stara
 */
public class Test {

    private enum Bla {
        BLA,
        ABC,
        DEF,
        GHI;
    }

    public static void main(String[] args) {
        Toolkit.getDefaultToolkit().beep();
        
        String[] stringArray = {"abc", "def", "ghi", "jkl", "mno"};
        Integer foo = 0;
        System.out.println(stringArray.toString());
        System.out.println(foo.getClass());

        String agd = "GHI";

        System.out.println("GHI");
        System.out.println(Bla.GHI.name().equals(agd));

        Object[] settingsArray = new Object[3];

        System.out.println(stringArray[0].compareTo(stringArray[1]));

        settingsArray[0] = new Integer(1);
        settingsArray[1] = 1;
        settingsArray[2] = true;

        for (int i = 0; i < settingsArray.length; i++) {
            Class bla = settingsArray[i].getClass();
            Class abc = Integer.TYPE;
            Class aaa = Integer.class;

            if (settingsArray[i].getClass().equals(Integer.class)) {
                System.out.println("bla");
            } else if (settingsArray[i].getClass().equals(Boolean.class)) {
                System.out.println("bool");
            }

            //bw.write("\t" + ((gotClass) settingsArray[i]).toString() + "\n");
        }

        char x = 's';
        int p;
        p = x;
        System.out.println(p);
        p = p+2;
        System.out.println(p);

        BinarySearchTree testTree = new BinarySearchTree();

        testTree.insert("C", "C");
        testTree.insert("D", "D");
        testTree.insert("A", "A");
        testTree.insert("B", "B");
        testTree.insert("F", "F");
        testTree.insert("E", "E");
        
        System.out.println("bla");

        testTree.delete("C");
        testTree.delete("D");
        testTree.delete("A");
        testTree.delete("B");
        testTree.delete("F");
        testTree.delete("E");

        System.out.println("bla");
    }
}
