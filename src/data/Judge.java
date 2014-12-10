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
 *
 * @author Zbyněk Stara
 */
public class Judge {
    private final int ID;
    private final School school;
    private final Teacher teacher;

    private BinarySearchTree blockedJudgeTree = new BinarySearchTree(); // these judges can never be with this one because of restrictions (same school, for example)

    private BinarySearchTree encounteredJudgeTree = new BinarySearchTree(); // these judges can no longer be with this one (already together once)

    public Judge(int ID, School school, Teacher teacher) {
        this.ID = ID;
        this.school = school;
        this.teacher = teacher;
    }

    public int getID() {
        return ID;
    }

    public String getSchoolName() {
        return school.getName();
    }

    public String getName() {
        return teacher.getName();
    }

     // BLOCKED JUDGE TREE:
    public int getBlockedJudgeTreeSize() {
        return blockedJudgeTree.size();
    }

    public Judge getBlockedJudgeTreeNodeData(int index) {
        return (Judge) blockedJudgeTree.getNodeData(index);
    }

    public boolean isJudgeBlocked(Judge judge) {
        if (!blockedJudgeTree.contains(judge.getID() + "")) {
            return false;
        } else {
            return true;
        }
    }

    public void addBlockedJudge(Judge judge) throws IllegalArgumentException {
        if (judge.getID() == ID) {
            throw new IllegalArgumentException("Cannot add itself as blocked.");
        } else if (!blockedJudgeTree.contains(judge.getID() + "")) {
            blockedJudgeTree.insert(judge, judge.getID() + "");
        } else {
            throw new IllegalArgumentException("Blocked judge is already in the tree.");
        }
    }

    public void removeBlockedJudge(Judge judge) throws NoSuchElementException {
        if (blockedJudgeTree.contains(judge.getID() + "")) {
            blockedJudgeTree.delete(judge.getID() + "");
        } else {
            throw new NoSuchElementException("Blocked judge to be deleted was not found.");
        }
    }

    public void eraseBlockedJudgeTree() {
        for (int i = 0; i < blockedJudgeTree.size(); i++) {
            Judge currentBlockedJudge = (Judge) blockedJudgeTree.getNodeData(i);

            currentBlockedJudge.removeBlockedJudge(this);
        }

        blockedJudgeTree = new BinarySearchTree();
    }

    // ENCOUNTERED JUDGE TREE:
    public int getEncounteredJudgeTreeSize() {
        return encounteredJudgeTree.size();
    }

    public Judge getEncounteredJudgeTreeNodeData(int index) {
        return (Judge) encounteredJudgeTree.getNodeData(index);
    }

    public boolean isJudgeEncountered(Judge judge) {
        if (!encounteredJudgeTree.contains(judge.getID() + "")) {
            return false;
        } else {
            return true;
        }
    }

    public void addEncounteredJudge(Judge judge) throws IllegalArgumentException {
        if (judge.getID() == ID) {
            throw new IllegalArgumentException("Cannot add itself as encountered.");
        } else if (!encounteredJudgeTree.contains(judge.getID() + "")) {
            encounteredJudgeTree.insert(judge, judge.getID() + "");
        } else {
            throw new IllegalArgumentException("Encountered judge is already in the tree.");
        }
    }
    
    public void removeEncounteredJudge(Judge judge) throws NoSuchElementException {
        if (encounteredJudgeTree.contains(judge.getID() + "")) {
            encounteredJudgeTree.delete(judge.getID() + "");
        } else {
            throw new NoSuchElementException("Encountered judge to be deleted was not found.");
        }
    }

    public void eraseEncounteredJudgeTree() { // MAY CAUSE DISCREPANCY IN ENCOUNTERED JUDGES BETWEEN TWO JUDGES - CORRECTED BACK
        for (int i = 0; i < encounteredJudgeTree.size(); i++) {
            Judge currentEncounteredJudge = (Judge) encounteredJudgeTree.getNodeData(i);

            currentEncounteredJudge.removeEncounteredJudge(this);
        }

        encounteredJudgeTree = new BinarySearchTree();
    }

    @Override
    public String toString() {
        return "ID: " + ID + ", School: " + school.getName() + ", Teacher: " + teacher.getName();
    }
}
