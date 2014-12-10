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

    public void allocateJudge(Judge currentJudge) throws IllegalArgumentException { // ADD IF BLOCKED // this version is strict about judges not judging together
        if (judgeTree.contains(currentJudge.getID() + "")) {
            throw new IllegalArgumentException("Judge is already allocated to this room.");
        } else {
            for (int i = 0; i < judgeTree.size(); i++) {
                Judge otherJudge = (Judge) judgeTree.getNodeData(i);

                /*if (currentJudge.getSchoolName().equals(otherJudge.getSchoolName())) {
                    throw new IllegalArgumentException("Judge from the same school is already allocated to the room.");
                } else {
                    continue;
                }*/

                if (currentJudge.isJudgeBlocked(otherJudge)) {
                    throw new IllegalArgumentException("Blocked judges cannot be added to the room.");
                } else if (otherJudge.isJudgeBlocked(currentJudge)) {
                    throw new IllegalArgumentException("Judge is blocked by another judge in the room.");
                }
                if (currentJudge.isJudgeEncountered(otherJudge)) {
                    throw new IllegalArgumentException("Encountered judges cannot be added to the room.");
                } else if (otherJudge.isJudgeEncountered(currentJudge)) {
                    throw new IllegalArgumentException("Judges is encountered by another judge in the room.");
                }
            }

            judgeTree.insert(currentJudge, currentJudge.getID() + ""); // THIS DOESN'T UPDATE ANY LISTS (ENCOUNTERED OR BLOCKED

            /*for (int i = 0; i < judgeTree.size(); i++) {
            Judge currentJudge = (Judge) judgeTree.getNodeData(i);

                for (int j = 0; j < judgeTree.size(); j++) {
                    Judge currentEncounteredJudge = (Judge) judgeTree.getNodeData(j);

                    try {
                        currentJudge.addEncounteredJudge(currentEncounteredJudge);
                    } catch (IllegalArgumentException ex) {
                        continue; // this judge was already encountered by the current judge
                    }
                }
            }*/
        }
    }

    public Judge removeJudge(Judge judge) throws NoSuchElementException { // REPAIR THIS
        if (!judgeTree.contains(judge.getID() + "")) {
            throw new NoSuchElementException("The requested judge was not found in this room.");
        } else {
            return (Judge) judgeTree.delete(judge.getID() + "");
        }
        
        /*if (!judgeTree.contains(judge.getID() + "")) {
            throw new NoSuchElementException("The requested judge was not found in this room.");
        } else {
            Judge deletedJudge =  (Judge) judgeTree.delete(judge.getID() + "");

            for (int i = 0; i < deletedJudge.getEncounteredJudgeTreeSize(); i++) {
                Judge currentEncounteredJudge = deletedJudge.getEncounteredJudgeTreeNodeData(i);

                currentEncounteredJudge.removeEncounteredJudge(deletedJudge);
            }

            return deletedJudge;
        }*/
    }

    public void allocateEntity(Entity entity) throws IllegalArgumentException { // ADD IF BLOCKED
        if (entityTree.contains(entity.getCode())) {
            throw new IllegalArgumentException("Entity is already allocated to this room.");
        } else {
            Object[] judgeArray = judgeTree.getDataArray();
            for (int i = 0; i < judgeArray.length; i++) {
                Judge currentJudge = (Judge) judgeArray[i];

                if (entity.getSchoolName().equals(currentJudge.getSchoolName())) {
                    throw new IllegalArgumentException("Judge from the same school is already allocated to the room.");
                } else if (entity.isJudgeEncountered(currentJudge)) {
                    throw new IllegalArgumentException("Judge has already been encountered by this entity.");
                } else {
                    continue;
                }
            }

            // is this too strict? - it definitely sets the minimum school number to judgesPerRoom + studentsPerRoom
            Object[] entityArray = entityTree.getDataArray();
            for (int i = 0; i < entityArray.length; i++) {
                Entity currentEntity = (Entity) entityArray[i];

                if (entity.getSchoolName().equals(currentEntity.getSchoolName())) {
                    throw new IllegalArgumentException("Entity from the same school is already allocated to the room.");
                } else {
                    continue;
                }
            }

            for (int j = 0; j < judgeTree.size(); j++) {
                Judge currentEncounteredJudge = (Judge) judgeTree.getNodeData(j);

                try {
                    entity.addEncounteredJudgeToRound(currentEncounteredJudge, round.getCurrentRoundIndex());
                } catch (IllegalArgumentException ex) {
                    continue;
                }
            }

            entityTree.insert(entity, entity.getCode());
        }
    }

    public Entity removeEntity(Entity entity) throws NoSuchElementException {
        if (!entityTree.contains(entity.getCode())) {
            throw new NoSuchElementException("The requested entity was not found in this room.");
        } else {
            Entity deletedEntity = (Entity) entityTree.delete(entity.getCode());
            deletedEntity.eraseEncounteredJudgeTreeFromRound(round.getCurrentRoundIndex());

            return deletedEntity;
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

    public Entity [] getEntityArray() throws ArrayIndexOutOfBoundsException {
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
    
    public int searchEntity(String code) {
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
