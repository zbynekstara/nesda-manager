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
public class Room {
    private final Round round;

    private String name;

    private final int judgesLimit;
    private final int entitiesLimit;

    private BinarySearchTree judgeTree;
    private BinarySearchTree entityTree;

    public Room(Round round, String name, int judgesLimit, int entitiesLimit) {
        this.round = round;

        this.name = name;

        this.judgesLimit = judgesLimit;
        this.entitiesLimit = entitiesLimit;

        judgeTree = new BinarySearchTree();
        entityTree = new BinarySearchTree();
    }

    public void allocateJudge(Judge judge, int roundIndex) throws IllegalArgumentException { // this version is strict about judges not judging together
        if (judgeTree.contains(judge.getID() + "")) {
            throw new IllegalArgumentException("The judge is already allocated to the room.");
        } else {
            // check judges
            for (int i = 0; i < judgeTree.size(); i++) {
                Judge otherJudge = (Judge) judgeTree.getNodeData(i);

                if (judge.isJudgeBlocked(otherJudge)) {
                    throw new IllegalArgumentException("The judge is blocked by a judge in the room.");
                } else if (otherJudge.isJudgeBlocked(judge)) {
                    throw new IllegalArgumentException("The judge is blocked by a judge in the room.");
                }

                if (judge.isJudgeEncountered(otherJudge)) {
                    throw new IllegalArgumentException("The judge was encountered by a judge in the room.");
                } else if (otherJudge.isJudgeEncountered(judge)) {
                    throw new IllegalArgumentException("The judge was encountered by a judge in the room.");
                }
            }

            // doesn't check entities - WRONG

            // change judges
            for (int i = 0; i < judgeTree.size(); i++) {
                Judge otherJudge = (Judge) judgeTree.getNodeData(i);

                if (judge.getID() == otherJudge.getID()) {
                    continue;
                } else {
                    judge.addEncounteredJudge(otherJudge);
                    otherJudge.addEncounteredJudge(judge);
                }
            }

            // change entities
            for (int i = 0; i < entityTree.size(); i++) {
                Entity currentEntity = (Entity) entityTree.getNodeData(i);

                currentEntity.addEncounteredJudgeToRound(judge, roundIndex);
            }

            judgeTree.insert(judge, judge.getID() + "");
        }
    }

    public void allocateEntity(Entity entity, int roundIndex) throws IllegalArgumentException {
        if (entityTree.contains(entity.getCode())) {
            throw new IllegalArgumentException("Tne entity is already allocated to this room.");
        } else {
            // check judges
            for (int i = 0; i < judgeTree.size(); i++) {
                Judge currentJudge = (Judge) judgeTree.getNodeData(i);

                if (entity.isJudgeBlocked(currentJudge)) {
                    throw new IllegalArgumentException("The entity is blocked by a judge in the room.");
                } else if (entity.isJudgeEncountered(currentJudge)) {
                    throw new IllegalArgumentException("The entity was encountered by a judge in the room.");
                }
            }

            // check entities
            // is this too strict? - it definitely sets the minimum school number to judgesPerRoom + studentsPerRoom
            for (int i = 0; i < entityTree.size(); i++) {
                Entity otherEntity = (Entity) entityTree.getNodeData(i);

                if (entity.isEntityBlocked(otherEntity)) {
                    throw new IllegalArgumentException("The entity is blocked by an entity in the room.");
                } else if (otherEntity.isEntityBlocked(entity)) {
                    throw new IllegalArgumentException("The entity is blocked by an entity in the room.");
                }
            }

            // doesn't change judges

            // change entities
            for (int i = 0; i < judgeTree.size(); i++) {
                Judge currentJudge = (Judge) judgeTree.getNodeData(i);

                try {
                    entity.addEncounteredJudgeToRound(currentJudge, roundIndex);
                } catch (IllegalArgumentException ex) {
                    continue;
                }
            }

            entityTree.insert(entity, entity.getCode());
        }
    }

    public Judge removeJudge(Judge judge, int roundIndex) throws NoSuchElementException {
        if (!judgeTree.contains(judge.getID() + "")) {
            throw new NoSuchElementException("The judge was not found in the room.");
        } else {
            // change judges
            for (int i = 0; i < judgeTree.size(); i++) {
                Judge otherJudge = (Judge) judgeTree.getNodeData(i);

                if (judge.getID() == otherJudge.getID()) {
                    continue;
                } else {
                    judge.removeEncounteredJudge(otherJudge);
                    otherJudge.removeEncounteredJudge(judge);
                }
            }

            // change entities
            for (int i = 0; i < entityTree.size(); i++) {
                Entity currentEntity = (Entity) entityTree.getNodeData(i);

                currentEntity.removeEncounteredJudgeFromRound(judge, roundIndex);
            }

            return (Judge) judgeTree.delete(judge.getID() + "");
        }
    }

    public Entity removeEntity(Entity entity, int roundIndex) throws NoSuchElementException {
        if (!entityTree.contains(entity.getCode())) {
            throw new NoSuchElementException("The entity was not found in the room.");
        } else {
            // doesn't change judges

            // change entities
            for (int i = 0; i < judgeTree.size(); i++) {
                Judge currentJudge = (Judge) judgeTree.getNodeData(i);

                entity.removeEncounteredJudgeFromRound(currentJudge, roundIndex);
            }

            return (Entity) entityTree.delete(entity.getCode());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJudgesLimit() {
        return judgesLimit;
    }

    public int getEntitiesLimit() {
        return entitiesLimit;
    }

    public int getJudgeTreeSize() {
        return judgeTree.size();
    }

    public int getEntityTreeSize() {
        return entityTree.size();
    }

    public Judge [] getJudgeArray() throws ArrayIndexOutOfBoundsException {
        Object [] tempArray = judgeTree.getDataArray();
        Judge [] returnArray = new Judge[tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Judge) tempArray[i];
        }

        return returnArray;
    }

    public Object getJudgeTreeNodeData(int index) {
        return judgeTree.getNodeData(index);
    }

    public Entity [] getEntityArray() {
        Object [] tempArray = entityTree.getDataArray();
        Entity [] returnArray = new Entity[tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Entity) tempArray[i];
        }

        return returnArray;
    }

    public Object getEntityTreeNodeData(int index) {
        return entityTree.getNodeData(index);
    }

    public int searchJudge(int ID) throws NoSuchElementException {
        Object [] judgeTreeArray = judgeTree.getDataArray();
        boolean judgeFound = false;
        int judgeIndex = -999;
        for (int i = 0; i < judgeTreeArray.length; i++) {
            if (((Judge) judgeTreeArray[i]).getID() == ID) {
                judgeFound = true;
                judgeIndex = i;
                break;
            }
        }
        if (judgeFound) {
            return judgeIndex;
        } else throw new NoSuchElementException();
    }

    public int searchEntity(String code) throws NoSuchElementException {
        Object [] entityTreeArray = entityTree.getDataArray();
        boolean entityFound = false;
        int entityIndex = -999;
        for (int i = 0; i < entityTreeArray.length; i++) {
            if (((Entity) entityTreeArray[i]).getCode().equals(code)) {
                entityFound = true;
                entityIndex = i;
                break;
            }
        }
        if (entityFound) {
            return entityIndex;
        }
        else throw new NoSuchElementException();
    }

    @Override
    public String toString() {
        return "Name: " + name;
    }
}
