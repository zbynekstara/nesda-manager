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
public class Entity {
    // Attributes:
    private final Event.Type type;
    private final School school;
    private final boolean isPair;
    private final Student [] studentArray;
    private final String code;

    private int roundsNumber;

    private BinarySearchTree blockedJudgeTree = new BinarySearchTree();

    private BinarySearchTree blockedEntityTree = new BinarySearchTree();

    private BinarySearchTree encounteredJudgeTree = new BinarySearchTree(); // = #2 priority; teachers from the same school are restricted automatically = #1 priority

    private BinarySearchTree [] roundNewEncounteredJudgesArray;
    private int roundNewEncounteredJudgesArrayLength;

    private LinkedList [] qualificationScoresListArray; // each round gets a LinkedList
    private final int numQualificationScoresPerRound;
    private final int minQualificationScore = 0;
    private final int maxQualificationScore = 30;

    private LinkedList finalRankingsList = new LinkedList();
    private final int numFinalRankings;
    private final int minFinalRanking = 1;
    private final int maxFinalRanking = 4;

    public Entity(Event.Type type, School school, boolean isPair, Student[] studentArray, String code, int numQualificationScoresPerRound, int numFinalRankings) {
        this.type = type;
        this.school = school;
        this.isPair = isPair;
        this.studentArray = studentArray;
        this.code = code;

        qualificationScoresListArray = new LinkedList[this.type.getNumRounds()];
        for (int i = 0; i < qualificationScoresListArray.length; i++) {
            qualificationScoresListArray[i] = new LinkedList();
        }

        this.numQualificationScoresPerRound = numQualificationScoresPerRound;
        this.numFinalRankings = numFinalRankings;
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
            throw new IllegalArgumentException("Blocked judge is already in the tree.");
        }
    }

    public void removeBlockedJudge(Judge judge) throws NoSuchElementException {
        if (blockedJudgeTree.contains(judge.getID() + "")) {
            blockedJudgeTree.delete(judge.getID() + "");
        } else {
            throw new NoSuchElementException("Blocked judge to be deleted was not found in the tree.");
        }
    }

    public void eraseBlockedJudgeTree() {
        blockedJudgeTree = new BinarySearchTree();
        //setRoundNewEncounteredJudgesArray(); - DON'T KNOW WHY I DID THIS; NECESSARY FOR BLOCKING AS WELL?
    }

    // BLOCKED ENTITY TREE:
    public int getBlockedEntityTreeSize() {
        return blockedEntityTree.size();
    }

    public Entity getBlockedEntityTreeNodeData(int index) {
        return (Entity) blockedEntityTree.getNodeData(index);
    }

    public boolean isEntityBlocked(Entity entity) {
        if (!blockedEntityTree.contains(entity.getCode())) {
            return false;
        } else {
            return true;
        }
    }

    public void addBlockedEntity(Entity entity) throws IllegalArgumentException {
        if (!blockedEntityTree.contains(entity.getCode())) {
            blockedEntityTree.insert(entity, entity.getCode());
        } else {
            throw new IllegalArgumentException("Blocked entity is already in the tree.");
        }
    }

    public void removeBlockedEntity(Entity entity) throws NoSuchElementException {
        if (blockedEntityTree.contains(entity.getCode())) {
            blockedEntityTree.delete(entity.getCode());
        } else {
            throw new NoSuchElementException("Blocked entity to be deleted was not found in the tree.");
        }
    }

    public void eraseBlockedEntityTree() {
        blockedEntityTree = new BinarySearchTree();
        //setRoundNewEncounteredJudgesArray(); - DON'T KNOW WHY I DID THIS; NECESSARY FOR BLOCKING AS WELL?
    }

    public boolean qualificationScoresAllocated(int roundIndex) {
        return (!qualificationScoresListArray[roundIndex].isEmpty());
    }

    public boolean qualificationScoresFull(int roundIndex) {
        return (qualificationScoresListArray[roundIndex].size() == numQualificationScoresPerRound);
    }

    public void addQualificationScore(int score, int roundIndex) throws IllegalStateException, IllegalArgumentException {
        if (qualificationScoresFull(roundIndex)) {
            throw new IllegalStateException("All scores were already set for this round.");
        } else if (score > maxQualificationScore || score < minQualificationScore) {
            throw new IllegalArgumentException("Invalid score value.");
        } else {
            qualificationScoresListArray[roundIndex].insertAtEnd(score);
        }
    }

    public Object removeQualificationScore(int roundIndex, int index) throws IllegalArgumentException {
        return qualificationScoresListArray[roundIndex].removeNode(index);
    }

    public int [] getQualificationScoresArray(int roundIndex) {
        Object [] tempArray = qualificationScoresListArray[roundIndex].getDataArray();
        int [] returnArray = new int[tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Integer) tempArray[i];
        }

        return returnArray;
    }

    public int getQualificationScoresListSize(int roundIndex) {
        return qualificationScoresListArray[roundIndex].size();
    }

    public boolean finalRankingsAllocated() {
        return (!finalRankingsList.isEmpty());
    }

    public boolean finalRankingsFull() {
        return (finalRankingsList.size() == numFinalRankings);
    }

    public void addFinalRanking(int ranking) throws IllegalStateException, IllegalArgumentException {
        if (finalRankingsFull()) {
            throw new IllegalStateException("All rankings were already set.");
        } else if (ranking > maxFinalRanking || ranking < minFinalRanking) {
            throw new IllegalArgumentException("Invalid ranking value.");
        } else {
            finalRankingsList.insertAtEnd(ranking);
        }
    }

    public Object removeFinalRanking(int index) throws IllegalArgumentException {
        return finalRankingsList.removeNode(index);
    }

    public int [] getFinalRankingsArray() {
        Object [] tempArray = finalRankingsList.getDataArray();
        int [] returnArray = new int[tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Integer) tempArray[i];
        }

        return returnArray;
    }
    public int getFinalRankingsListSize() {
        return finalRankingsList.size();
    }

    @Override
    public String toString() {
        return "Code: " + code;
    }
}
