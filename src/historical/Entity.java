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

package historical;

import java.util.*;

/**
 *
 * @author Zbyněk Stara
 */
public class Entity {
    // Attributes:
    private final Event.Type type;
    private final School school;
    private final boolean isPair;
    private final Student [] studentArray;
    private final String code;

    private int roundsNumber;

    private BinarySearchTree encounteredJudgeTree = new BinarySearchTree(); // = #2 priority; teachers from the same school are restricted automatically = #1 priority

    private BinarySearchTree [] roundNewEncounteredJudgesArray;
    private int roundNewEncounteredJudgesArrayLength;

    private BinarySearchTree blockedJudgeTree = new BinarySearchTree();

    public Entity(Event.Type type, School school, boolean isPair, Student[] studentArray, String code) {
        this.type = type;
        this.school = school;
        this.isPair = isPair;
        this.studentArray = studentArray;
        this.code = code;
    }

    public void setRoundsNumber(int roundsNumber) {
        this.roundsNumber = roundsNumber;
    }

    public void setRoundNewEncounteredJudgesArray() {
        roundNewEncounteredJudgesArray = new BinarySearchTree[roundsNumber];

        for (int i = 0; i < roundNewEncounteredJudgesArray.length; i++) {
            roundNewEncounteredJudgesArray[i] = new BinarySearchTree();
        }

        roundNewEncounteredJudgesArrayLength = 0;
    }

    public String getCode() {
        return code;
    }
    
    public Event.Type getType() {
        return type;
    }

    public String getSchoolName() {
        return school.getName();
    }

    public boolean isPair() {
        return isPair;
    }

    public Student getStudentArrayElement(int index) {
        return studentArray[index];
    }

    /*public void insertEncounteredJudgeToRound(Judge judge, int currentRoundIndex) throws IllegalArgumentException {
        if (!encounteredJudgeTree.contains(judge.getID() + "")) {
            encounteredJudgeTree.insert(judge, judge.getID() + "");

            roundNewEncounteredJudgesArray[currentRoundIndex].insert(judge.getID(), judge.getID() + "");

            if ((currentRoundIndex + 1) > roundNewEncounteredJudgesArrayLength) roundNewEncounteredJudgesArrayLength = (currentRoundIndex + 1);
        }
        else throw new IllegalArgumentException();
    }*/

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

    public void addEncounteredJudgeToRound(Judge judge, int currentRoundIndex) throws IllegalArgumentException {
        if (!encounteredJudgeTree.contains(judge.getID() + "")) {
            encounteredJudgeTree.insert(judge, judge.getID() + "");

            roundNewEncounteredJudgesArray[currentRoundIndex].insert(judge.getID(), judge.getID() + "");

            if ((currentRoundIndex + 1) > roundNewEncounteredJudgesArrayLength) roundNewEncounteredJudgesArrayLength = (currentRoundIndex + 1);
        } else {
            throw new IllegalArgumentException("Encountered judge is already in the tree.");
        }
    }

    public void removeEncounteredJudgeFromRound(Judge judge, int currentRoundIndex) throws NoSuchElementException {
        if (encounteredJudgeTree.contains(judge.getID() + "")) {
            encounteredJudgeTree.delete(judge.getID() + "");

            roundNewEncounteredJudgesArray[currentRoundIndex].delete(judge.getID() + "");
        } else {
            throw new NoSuchElementException("Encountered judge to be deleted was not found in the tree.");
        }
    }

    public void eraseEncounteredJudgeTreeFromRound(int currentRoundIndex) {
        for (int i = currentRoundIndex; i < roundNewEncounteredJudgesArrayLength; i++) {
            for (int j = 0; j < roundNewEncounteredJudgesArray[i].size(); j++) {
                int currentEncounteredJudgeID = (Integer) roundNewEncounteredJudgesArray[i].getNodeData(j);

                encounteredJudgeTree.delete(currentEncounteredJudgeID + "");
            }

            roundNewEncounteredJudgesArray[i] = new BinarySearchTree();
        }
    }

    public void eraseEncounteredJudgeTree() {
        encounteredJudgeTree = new BinarySearchTree();
        setRoundNewEncounteredJudgesArray();
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
        if (!blockedJudgeTree.contains(judge.getID() + "")) {
            blockedJudgeTree.insert(judge, judge.getID() + "");
        } else {
            throw new IllegalArgumentException("Encountered judge is already in the tree.");
        }
    }

    public void removeBlockedJudge(Judge judge) throws NoSuchElementException {
        if (encounteredJudgeTree.contains(judge.getID() + "")) {
            encounteredJudgeTree.delete(judge.getID() + "");
        } else {
            throw new NoSuchElementException("Encountered judge to be deleted was not found in the tree.");
        }
    }

    public void eraseBlockedJudgeTree() {
        blockedJudgeTree = new BinarySearchTree();
        //setRoundNewEncounteredJudgesArray(); - DON'T KNOW WHY I DID THIS; NECESSARY FOR BLOCKING AS WELL?
    }

    @Override
    public String toString() {
        return "Code: " + code;
    }
}
