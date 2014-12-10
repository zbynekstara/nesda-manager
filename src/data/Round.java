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
public class Round {
    public class NotEnoughJudgesException extends Exception {
        public void NotEnoughJudgesException() {
            throw new RuntimeException();
        }
    }

    public class NotEnoughEntitiesException extends Exception {
        public void NotEnoughEntitiesException() {
            throw new RuntimeException();
        }
    }

    public class ImpossibleToAllocateException extends Exception {
        public void ImpossibleToAllocateException() {
            throw new RuntimeException();
        }
    }

    private String name;

    private final Event event;

    private final int currentRoundIndex;

    private BinarySearchTree judgeTree; // backup - the judgeTree which gets passed down from Qualification
    private BinarySearchTree entityTree; // backup - the entityTree which gets passed down from Qualification

    private final int judgesPerRoom;
    private final int entitiesPerRoom;

    private BinarySearchTree freeJudgeTree = new BinarySearchTree(); // new tree
    private BinarySearchTree freeEntityTree = new BinarySearchTree(); // new tree

    private Room[] roomArray;
    //private int[] entitiesInRoom;

    public Round(Event event, int currentRoundIndex, String name, BinarySearchTree judgeTree, BinarySearchTree entityTree, int judgesPerRoom, int entitiesPerRoom) {
        this.event = event;

        this.currentRoundIndex = currentRoundIndex;

        this.name = name;

        this.judgeTree = judgeTree;
        this.entityTree = entityTree;

        this.judgesPerRoom = judgesPerRoom;
        this.entitiesPerRoom = entitiesPerRoom;

        // construct freeJudgeTree as duplicate of judgeTree
        Object[] judgeArray = judgeTree.getDataArray();
        for (int i = 0; i < judgeArray.length; i++) {
            Judge currentJudge = (Judge) judgeArray[i];

            freeJudgeTree.insert(currentJudge, currentJudge.getID() + "");
        }
        freeJudgeTree = freeJudgeTree.balance();

        // construct freeEntityTree as duplicate of entityTree
        Object[] entityArray = entityTree.getDataArray();
        for (int i = 0; i < entityArray.length; i++) {
            Entity currentEntity = (Entity) entityArray[i];

            freeEntityTree.insert(currentEntity, currentEntity.getCode() + "");
        }
        freeEntityTree = freeEntityTree.balance();

        // construct roomArray
        int roomArraySize;
        if (event.type == Event.Type.DEBATE && !evenNumber(entityTree.size())) {
            throw new IllegalStateException("Qualification cannot be constructed with an odd number of debate pairs");
        } else if (event.type == Event.Type.DEBATE && evenNumber(entityTree.size())) {
            roomArraySize = entityTree.size() / 2;
        } else {
            roomArraySize = (int) Math.ceil(((double) entityTree.size()) / ((double) entitiesPerRoom));
        }
        roomArray = new Room[roomArraySize];
    }

    public int getCurrentRoundIndex() {
        return this.currentRoundIndex;
    }

    protected void initializeRooms() {
        for (int i = 0; i < roomArray.length; i++) {
            String roomName = "Room " + (i + 1);

            roomArray[i] = new Room(this, roomName, judgesPerRoom, entitiesPerRoom);
        }
    }

    private boolean evenNumber(int number) {
        double doubleNumber = (double) number;

        double doubleNumberDividedBy2 = doubleNumber / 2;
        int intNumberDividedBy2 = (int) doubleNumberDividedBy2;

        if (doubleNumberDividedBy2 == intNumberDividedBy2) return true;
        else return false;
    }

    protected Judge getRandomFreeJudge() {
        if (freeJudgeTree.isEmpty()) {
            return null;
        } else {
            double randomDouble = Math.random() * (double) freeJudgeTree.size();
            int randomIndex = (int) (Math.floor(randomDouble));
            return (Judge) freeJudgeTree.getNodeData(randomIndex);
        }
    }

    protected Judge getRandomFreeUntriedJudge(BinarySearchTree triedJudgeTree) {
        BinarySearchTree untriedJudgeTree = new BinarySearchTree();

        for (int i = 0; i < freeJudgeTree.size(); i++) {
            Judge currentFreeJudge = (Judge) freeJudgeTree.getNodeData(i);

            if (!triedJudgeTree.contains(currentFreeJudge.getID() + "")) { // if the tried judges tree does not contain the current judge
                untriedJudgeTree.insert(currentFreeJudge, currentFreeJudge.getID() + "");
            }
        }

        if (untriedJudgeTree.isEmpty()) {
            return null;
        } else {
            double randomDouble = Math.random() * (double) untriedJudgeTree.size();
            int randomIndex = (int) (Math.floor(randomDouble));
            return (Judge) untriedJudgeTree.getNodeData(randomIndex);
        }
    }

    protected Entity getRandomFreeEntity() {
        if (freeEntityTree.isEmpty()) {
            return null;
        } else {
            double randomDouble = Math.random() * (double) freeEntityTree.size();
            int randomIndex = (int) (Math.floor(randomDouble));
            return (Entity) freeEntityTree.getNodeData(randomIndex);
        }
    }

    protected Entity getRandomFreeUntriedEntity(BinarySearchTree triedEntityTree) {
        BinarySearchTree untriedEntityTree = new BinarySearchTree();

        for (int i = 0; i < freeEntityTree.size(); i++) {
            Entity currentFreeEntity = (Entity) freeEntityTree.getNodeData(i);

            if (!triedEntityTree.contains(currentFreeEntity.getCode())) { // if the tried entities tree does not contain the current entity
                untriedEntityTree.insert(currentFreeEntity, currentFreeEntity.getCode());
            }
        }

        if (untriedEntityTree.isEmpty()) {
            return null;
        } else {
            double randomDouble = Math.random() * (double) untriedEntityTree.size();
            int randomIndex = (int) (Math.floor(randomDouble));
            return (Entity) untriedEntityTree.getNodeData(randomIndex);
        }
    }

    public void addFreeJudge(Judge judge) throws IllegalArgumentException {
        if (!freeJudgeTree.contains(judge.getID() + "")) {
            freeJudgeTree.insert(judge, judge.getID() + "");
        } else {
            throw new IllegalArgumentException("Provided judge is already in the freeJudgeTree.");
        }
    }

    public void addFreeEntity(Entity entity) throws IllegalArgumentException {
        if (!freeEntityTree.contains(entity.getCode() + "")) {
            freeEntityTree.insert(entity, entity.getCode() + "");
        } else {
            throw new IllegalArgumentException("Provided entity is already in the freeEntityTree.");
        }
    }

    public Judge removeFreeJudge(Judge judge) throws NoSuchElementException {
        if (freeJudgeTree.contains(judge.getID() + "")) {
            return (Judge) freeJudgeTree.delete(judge.getID() + "");
        } else {
            throw new NoSuchElementException("Free judge to be removed was not found.");
        }
    }

    public Entity removeFreeEntity(Entity entity) throws NoSuchElementException {
        if (freeEntityTree.contains(entity.getCode())) {
            return (Entity) freeEntityTree.delete(entity.getCode() + "");
        } else {
            throw new NoSuchElementException("Free entity to be removed was not found.");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJudgesPerRoom() {
        return judgesPerRoom;
    }

    public int getFreeJudgeTreeSize() {
        return freeJudgeTree.size();
    }

    public Judge getFreeJudgeTreeNodeData(int index) {
        return (Judge) freeJudgeTree.getNodeData(index);
    }

    public int getFreeEntityTreeSize() {
        return freeEntityTree.size();
    }

    public int getRoomArrayLength() {
        return roomArray.length;
    }

    public Room[] getRoomArray() {
        Room [] returnRoomArray = new Room[roomArray.length];

        for (int i = 0; i < roomArray.length; i++) {
            returnRoomArray[i] = roomArray[i];
        }

        return returnRoomArray;
    }

    public Room getRoomArrayElement(int index) throws ArrayIndexOutOfBoundsException {
        return roomArray[index];
    }

    public void setRoom(Room room, int index) {
        roomArray[index] = room;
    }

    public int[] searchJudge(int ID) throws IllegalArgumentException, NoSuchElementException {
        if (ID >= 1) {
            for (int i = 0; i < getRoomArrayLength(); i++) {
                Room currentRoom = getRoomArrayElement(i);

                try {
                    int judgeIndex = currentRoom.searchJudge(ID);

                    int [] returnArray = {i, judgeIndex};

                    return returnArray;
                } catch (NoSuchElementException ex) {
                    continue;
                }
            }
            throw new NoSuchElementException();
        } else throw new IllegalArgumentException();
    }

    public int[] searchStudentCode(String code) throws IllegalArgumentException, NoSuchElementException {
        if (!code.equals("")) {
            for (int i = 0; i < getRoomArrayLength(); i++) {
                Room currentRoom = getRoomArrayElement(i);

                try {
                    int entityIndex = currentRoom.searchEntity(code);

                    int[] returnArray = {i, entityIndex};

                    return returnArray;
                } catch (NoSuchElementException ex) {
                    continue;
                }
            }
            throw new NoSuchElementException();
        } else throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return "Name: " + name;
    }
}
