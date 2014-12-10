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
 * The Qualification class is the basis of the functionality of the
 * Qualification tab. It contains methods to initalize events, rounds and rooms,
 * and to create, handle and allocate entities and judges.
 *
 * @author Zbyněk Stara
 */
public class Qualification {
    public class NotEnoughJudgesException extends Exception {
        public void NotEnoughJudgesException() {
            throw new RuntimeException();
        }
    }

    public class ImpossibleToAllocateException extends Exception {
        public void ImpossibleToAllocateException() {
            throw new RuntimeException();
        }
    }

    private Database database;

    private QualificationEvent [] eventArray = new QualificationEvent[5];
    private int eventArrayLength = 0;

    private BinarySearchTree judgeTree; // ordered by school names

    private BinarySearchTree ooEntityTree; // ordered by codes
    private BinarySearchTree oiEntityTree;
    private BinarySearchTree isEntityTree;
    private BinarySearchTree daEntityTree; // ordered by pair codes
    private BinarySearchTree debateEntityTree;

    public Qualification(Database database) {
        this.database = database;

        generateJudges();
        generateEntities();
    }

    // INITIALIZATION
    private void generateJudges() {
        judgeTree = new BinarySearchTree();
        int currentID = 1;
        for (int i = 0; i < database.getSchoolTreeSize(); i++) {
            School currentSchool = (School) database.getSchoolTreeNodeData(i);

            BinarySearchTree currentSchoolJudgeTree = new BinarySearchTree();

            for (int j = 0; j < currentSchool.getTeacherTreeSize(); j++) {
                Teacher currentTeacher = (Teacher) currentSchool.getTeacherTreeNodeData(j);

                Judge newJudge = new Judge(currentID, currentSchool, currentTeacher);

                judgeTree.insert(newJudge, newJudge.getID() + "");
                currentSchoolJudgeTree.insert(newJudge, newJudge.getID() + "");

                currentID++;
            }

            for (int j = 0; j < currentSchoolJudgeTree.size(); j++) {
                Judge currentJudge = (Judge) currentSchoolJudgeTree.getNodeData(j);

                for (int k = 0; k < currentSchoolJudgeTree.size(); k++) {
                    Judge otherJudge = (Judge) currentSchoolJudgeTree.getNodeData(k);

                    if (otherJudge.getID() == currentJudge.getID()) {
                        continue;
                    } else {
                        currentJudge.addBlockedJudge(otherJudge); // set judges from the same school as blocked
                    }
                }
            }
        }
        judgeTree = judgeTree.balance();
    }

    private void generateEntities() {
        /*settingsArray[0] = ooStudentRoomLimit; // USED
        settingsArray[1] = oiStudentRoomLimit; // USED
        settingsArray[2] = isStudentRoomLimit; // USED
        settingsArray[3] = daStudentRoomLimit; // USED
        settingsArray[4] = ooFinalStudentRoomLimit;
        settingsArray[5] = oiFinalStudentRoomLimit;
        settingsArray[6] = isFinalStudentRoomLimit;
        settingsArray[7] = daFinalStudentRoomLimit;

        settingsArray[8] = ooStudentSchoolLimit; // CHECKED ALREADY
        settingsArray[9] = oiStudentSchoolLimit; // CHECKED ALREADY
        settingsArray[10] = isStudentSchoolLimit; // CHECKED ALREADY
        settingsArray[11] = daStudentSchoolLimit; // CHECKED ALREADY
        settingsArray[12] = debateStudentSchoolLimit; // CHECKED ALREADY

        settingsArray[13] = ooJudgeRoomLimit; // USED
        settingsArray[14] = oiJudgeRoomLimit; // USED
        settingsArray[15] = isJudgeRoomLimit; // USED
        settingsArray[16] = daJudgeRoomLimit; // USED
        settingsArray[17] = debateJudgeRoomLimit; // USED
        settingsArray[18] = finalsJudgeRoomLimit; // USED

        settingsArray[19] = roomLimit1; // NOT USED
        settingsArray[20] = roomLimit2; // NOT USED
        settingsArray[21] = timeLimit1; // NOT USED
        settingsArray[22] = timeLimit2; // NOT USED
        settingsArray[23] = schoolNumber; // CHECKED ALREADY
        settingsArray[24] = teacherSchoolNumber; // CHECKED ALREADY
        settingsArray[25] = studentSchoolNumber; // CHECKED ALREADY

        settingsArray[26] = allowCombinedEvents; // ALWAYS TRUE

        settingsArray[27] = combinedEvent1; // ALWAYS 2
        settingsArray[28] = combinedEvent2; // ALWAYS 3*/

        Object[] settingsArray = database.getSettings();

        ooEntityTree = new BinarySearchTree();
        for (int i = 0; i < database.getSchoolTreeSize(); i++) {
            School currentSchool = (School) database.getSchoolTreeNodeData(i);

            for (int j = 0; j < currentSchool.getStudentTreeSize(); j++) {
                Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(j);

                if (currentStudent.getEvents()[0] == true) {
                    Student[] studentList = {currentStudent};
                    Entity newEntity = new Entity(Event.Type.ORIGINAL_ORATORY, currentSchool, false, studentList, currentStudent.getCode());

                    ooEntityTree.insert(newEntity, newEntity.getCode());
                }
            }
        }
        ooEntityTree = ooEntityTree.balance();

        oiEntityTree = new BinarySearchTree();
        for (int i = 0; i < database.getSchoolTreeSize(); i++) {
            School currentSchool = (School) database.getSchoolTreeNodeData(i);

            for (int j = 0; j < currentSchool.getStudentTreeSize(); j++) {
                Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(j);

                if (currentStudent.getEvents()[1] == true) {
                    Student[] studentList = {currentStudent};
                    Entity newEntity = new Entity(Event.Type.ORAL_INTERPRETATION, currentSchool, false, studentList, currentStudent.getCode());

                    oiEntityTree.insert(newEntity, newEntity.getCode());
                }
            }
        }
        oiEntityTree = oiEntityTree.balance();

        isEntityTree = new BinarySearchTree();
        for (int i = 0; i < database.getSchoolTreeSize(); i++) {
            School currentSchool = (School) database.getSchoolTreeNodeData(i);

            for (int j = 0; j < currentSchool.getStudentTreeSize(); j++) {
                Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(j);

                if (currentStudent.getEvents()[2] == true) {
                    Student[] studentList = {currentStudent};
                    Entity newEntity = new Entity(Event.Type.IMPROMPTU_SPEAKING, currentSchool, false, studentList, currentStudent.getCode());

                    isEntityTree.insert(newEntity, newEntity.getCode());
                }
            }
        }
        isEntityTree = isEntityTree.balance();

        daEntityTree = new BinarySearchTree();
        for (int i = 0; i < database.getSchoolTreeSize(); i++) {
            School currentSchool = (School) database.getSchoolTreeNodeData(i);

            for (int j = 0; j < currentSchool.getDAPairTreeSize(); j++) {
                DAStudentPair currentStudentPair = (DAStudentPair) currentSchool.getDAPairTreeNodeData(j);

                Student[] studentList = currentStudentPair.getStudentArray();
                Entity newEntity = new Entity(Event.Type.DUET_ACTING, currentSchool, false, studentList, currentStudentPair.getCode());

                daEntityTree.insert(newEntity, newEntity.getCode());
            }
        }
        daEntityTree = daEntityTree.balance();

        debateEntityTree = new BinarySearchTree();
        for (int i = 0; i < database.getSchoolTreeSize(); i++) {
            School currentSchool = (School) database.getSchoolTreeNodeData(i);

            for (int j = 0; j < currentSchool.getDAPairTreeSize(); j++) {
                DebateStudentPair currentStudentPair = (DebateStudentPair) currentSchool.getDebatePairTreeNodeData(j);

                Student[] studentList = currentStudentPair.getStudentArray();
                Entity newEntity = new Entity(Event.Type.DEBATE, currentSchool, false, studentList, currentStudentPair.getCode());

                debateEntityTree.insert(newEntity, newEntity.getCode());
            }
        }
        debateEntityTree = debateEntityTree.balance();
    }

    // JUDGES
    public BinarySearchTree getJudgeTree() {
        return judgeTree;
    }

    public void insertJudge(Judge judge) throws IllegalArgumentException {
        judgeTree.insert(judge, judge.getID() + "");
    }

    public Judge [] getJudgeArray() {
        Object [] tempArray = judgeTree.getDataArray();
        Judge [] returnArray = new Judge [tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Judge) tempArray[i];
        }

        return returnArray;
    }

    public Judge getJudge(int ID) throws NoSuchElementException {
        int judgeIndex = this.searchJudge(ID);
        Judge judge = (Judge) this.getJudgeTreeNodeData(judgeIndex);
        return judge;
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
        }
        else throw new NoSuchElementException();
    }

    public int getJudgeTreeSize() {
        return judgeTree.size();
    }

    public Judge getJudgeTreeNodeData(int index) {
        return (Judge) judgeTree.getNodeData(index);
    }

    /*public void completeJudgeTree() {
        for (int i = 0; i < getJudgeTreeSize(); i++) {
            Judge currentJudge = (Judge) getJudgeTreeNodeData(i);

            for (int j = 0; j < currentJudge.getEncounteredJudgeTempIDTreeSize(); i++) {
                int currentEncounteredJudgeTempID = currentJudge.getEncounteredJudgeTempID(j);
                Judge currentEncounteredJudge = getJudge(currentEncounteredJudgeTempID);

                currentJudge.insertEncounteredJudge(currentEncounteredJudge);
            }
        }
    }*/

    // OO ENTITIES:
    public BinarySearchTree getOOEntityTree() {
        return ooEntityTree;
    }

    public void insertOOEntity(Entity entity) throws IllegalArgumentException {
        ooEntityTree.insert(entity, entity.getCode());
    }

    public Entity getOOEntity(String code) throws NoSuchElementException {
        int entityIndex = this.searchOOEntity(code);
        Entity entity = (Entity) this.getOOEntityTreeNodeData(entityIndex);
        return entity;
    }

    public int searchOOEntity(String code) throws NoSuchElementException {
        Object [] ooEntityTreeArray = ooEntityTree.getDataArray();
        boolean entityFound = false;
        int entityIndex = -999;
        for (int i = 0; i < ooEntityTreeArray.length; i++) {
            if (((Entity) ooEntityTreeArray[i]).getCode().equals(code)) {
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

    public int getOOEntityTreeSize() {
        return ooEntityTree.size();
    }

    public Entity getOOEntityTreeNodeData(int index) {
        return (Entity) ooEntityTree.getNodeData(index);
    }

    protected void setOOEntityTreeNodeData(int index, Object data) {
        ooEntityTree.setNodeData(index, data);
    }

    // OI ENTITIES:
    public BinarySearchTree getOIEntityTree() {
        return ooEntityTree;
    }

    public void insertOIEntity(Entity entity) throws IllegalArgumentException {
        oiEntityTree.insert(entity, entity.getCode());
    }

    public Entity getOIEntity(String code) throws NoSuchElementException {
        int entityIndex = this.searchOIEntity(code);
        Entity entity = (Entity) this.getOIEntityTreeNodeData(entityIndex);
        return entity;
    }

    public int searchOIEntity(String code) throws NoSuchElementException {
        Object [] oiEntityTreeArray = oiEntityTree.getDataArray();
        boolean entityFound = false;
        int entityIndex = -999;
        for (int i = 0; i < oiEntityTreeArray.length; i++) {
            if (((Entity) oiEntityTreeArray[i]).getCode().equals(code)) {
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

    public int getOIEntityTreeSize() {
        return oiEntityTree.size();
    }

    public Entity getOIEntityTreeNodeData(int index) {
        return (Entity) oiEntityTree.getNodeData(index);
    }

    protected void setOIEntityTreeNodeData(int index, Object data) {
        oiEntityTree.setNodeData(index, data);
    }

    // IS ENTITIES:
    public BinarySearchTree getISEntityTree() {
        return ooEntityTree;
    }

    public void insertISEntity(Entity entity) throws IllegalArgumentException {
        isEntityTree.insert(entity, entity.getCode());
    }

    public Entity getISEntity(String code) throws NoSuchElementException {
        int entityIndex = this.searchISEntity(code);
        Entity entity = (Entity) this.getISEntityTreeNodeData(entityIndex);
        return entity;
    }

    public int searchISEntity(String code) throws NoSuchElementException {
        Object [] isEntityTreeArray = isEntityTree.getDataArray();
        boolean entityFound = false;
        int entityIndex = -999;
        for (int i = 0; i < isEntityTreeArray.length; i++) {
            if (((Entity) isEntityTreeArray[i]).getCode().equals(code)) {
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

    public int getISEntityTreeSize() {
        return isEntityTree.size();
    }

    public Entity getISEntityTreeNodeData(int index) {
        return (Entity) isEntityTree.getNodeData(index);
    }

    protected void setISEntityTreeNodeData(int index, Object data) {
        isEntityTree.setNodeData(index, data);
    }

    // DA ENTITIES:
    public BinarySearchTree getDAEntityTree() {
        return ooEntityTree;
    }

    public void insertDAEntity(Entity entity) throws IllegalArgumentException {
        daEntityTree.insert(entity, entity.getCode());
    }

    public Entity getDAEntity(String code) throws NoSuchElementException {
        int entityIndex = this.searchDAEntity(code);
        Entity entity = (Entity) this.getDAEntityTreeNodeData(entityIndex);
        return entity;
    }

    public int searchDAEntity(String code) throws NoSuchElementException {
        Object [] daEntityTreeArray = daEntityTree.getDataArray();
        boolean entityFound = false;
        int entityIndex = -999;
        for (int i = 0; i < daEntityTreeArray.length; i++) {
            if (((Entity) daEntityTreeArray[i]).getCode().equals(code)) {
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

    public int getDAEntityTreeSize() {
        return daEntityTree.size();
    }

    public Entity getDAEntityTreeNodeData(int index) {
        return (Entity) daEntityTree.getNodeData(index);
    }

    protected void setDAEntityTreeNodeData(int index, Object data) {
        daEntityTree.setNodeData(index, data);
    }

    // DEBATE ENTITIES:
    public BinarySearchTree getDebateEntityTree() {
        return ooEntityTree;
    }

    public void insertDebateEntity(Entity entity) throws IllegalArgumentException {
        debateEntityTree.insert(entity, entity.getCode());
    }

    public Entity getDebateEntity(String code) throws NoSuchElementException {
        int entityIndex = this.searchDebateEntity(code);
        Entity entity = (Entity) this.getDebateEntityTreeNodeData(entityIndex);
        return entity;
    }

    public int searchDebateEntity(String code) throws NoSuchElementException {
        Object [] debateEntityTreeArray = debateEntityTree.getDataArray();
        boolean entityFound = false;
        int entityIndex = -999;
        for (int i = 0; i < debateEntityTreeArray.length; i++) {
            if (((Entity) debateEntityTreeArray[i]).getCode().equals(code)) {
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

    public int getDebateEntityTreeSize() {
        return debateEntityTree.size();
    }

    public Entity getDebateEntityTreeNodeData(int index) {
        return (Entity) debateEntityTree.getNodeData(index);
    }

    protected void setDebateEntityTreeNodeData(int index, Object data) {
        debateEntityTree.setNodeData(index, data);
    }

    // EVENTS:
    public void initializeEvents() throws ImpossibleToAllocateException, NotEnoughJudgesException {
        Object[] settingsArray = database.getSettings();

        // TESTING:
        if (ooEntityTree.size() >= (Integer) settingsArray[0]) {
            eraseEncounteredJudgeTrees();

            eventArray[eventArrayLength] = new QualificationEvent(this, Event.Type.ORIGINAL_ORATORY, judgeTree, ooEntityTree, (Integer) settingsArray[13], (Integer) settingsArray[0]);

            eventArray[eventArrayLength].initializeRounds();

            if (eventArray[eventArrayLength].getNotEnoughJudges()) {
                throw new NotEnoughJudgesException();
            } else if (eventArray[eventArrayLength].getImpossibleToAllocate()) {
                throw new ImpossibleToAllocateException();
            } else {
                setBlockedJudgesForEvent(eventArray[eventArrayLength]);
                eventArrayLength += 1;
            }
        } else {
            System.out.println("Skipping OO");
        }

        if (oiEntityTree.size() >= (Integer) settingsArray[1]) {
            eraseEncounteredJudgeTrees();

            eventArray[eventArrayLength] = new QualificationEvent(this, Event.Type.ORAL_INTERPRETATION, judgeTree, oiEntityTree, (Integer) settingsArray[14], (Integer) settingsArray[1]);
            
            eventArray[eventArrayLength].initializeRounds();

            if (eventArray[eventArrayLength].getNotEnoughJudges()) {
                throw new NotEnoughJudgesException();
            } else if (eventArray[eventArrayLength].getImpossibleToAllocate()) {
                throw new ImpossibleToAllocateException();
            } else {
                setBlockedJudgesForEvent(eventArray[eventArrayLength]);
                eventArrayLength += 1;
            }
        } else {
            System.out.println("Skipping OI");
        }

        if (isEntityTree.size() >= (Integer) settingsArray[2]) {
            eraseEncounteredJudgeTrees();

            eventArray[eventArrayLength] = new QualificationEvent(this, Event.Type.IMPROMPTU_SPEAKING, judgeTree, isEntityTree, (Integer) settingsArray[15], (Integer) settingsArray[2]);

            eventArray[eventArrayLength].initializeRounds();

            if (eventArray[eventArrayLength].getNotEnoughJudges()) {
                throw new NotEnoughJudgesException();
            } else if (eventArray[eventArrayLength].getImpossibleToAllocate()) {
                throw new ImpossibleToAllocateException();
            } else {
                setBlockedJudgesForEvent(eventArray[eventArrayLength]);
                eventArrayLength += 1;
            }
        } else {
            System.out.println("Skipping IS");
        }

        if (daEntityTree.size() >= (Integer) settingsArray[3]) {
            eraseEncounteredJudgeTrees();

            eventArray[eventArrayLength] = new QualificationEvent(this, Event.Type.DUET_ACTING, judgeTree, daEntityTree, (Integer) settingsArray[16], (Integer) settingsArray[3]);

            eventArray[eventArrayLength].initializeRounds();

            if (eventArray[eventArrayLength].getNotEnoughJudges()) {
                throw new NotEnoughJudgesException();
            } else if (eventArray[eventArrayLength].getImpossibleToAllocate()) {
                throw new ImpossibleToAllocateException();
            } else {
                setBlockedJudgesForEvent(eventArray[eventArrayLength]);
                eventArrayLength += 1;
            }
        } else {
            System.out.println("Skipping DA");
        }

        if (debateEntityTree.size() >= 2) {
            eraseEncounteredJudgeTrees();

            eventArray[eventArrayLength] = new QualificationEvent(this, Event.Type.DEBATE, judgeTree, debateEntityTree, (Integer) settingsArray[17], 2);

            eventArray[eventArrayLength].initializeRounds();

            if (eventArray[eventArrayLength].getNotEnoughJudges()) {
                throw new NotEnoughJudgesException();
            } else if (eventArray[eventArrayLength].getImpossibleToAllocate()) {
                throw new ImpossibleToAllocateException();
            } else {
                setBlockedJudgesForEvent(eventArray[eventArrayLength]);
                eventArrayLength += 1;
            }
        } else {
            System.out.println("Skipping Debate");
        }
    }

    public void deinitialize() {
        eraseEncounteredJudgeTrees();
        eraseBlockedJudgeTrees();
    }

    // sets the judges who really cannot judge with the judge in question because they are in the same room
    // according to the event in question
    private void setBlockedJudgesForEvent(QualificationEvent currentEvent) {
        for (int i = 0; i < currentEvent.getRoundArrayLength(); i++) { // for every round in the event
            Round currentRound = currentEvent.getRoundArrayElement(i);

            for (int j = 0; j < currentRound.getRoomArrayLength(); j++) { // for every room in the round
                Room currentRoom = currentRound.getRoomArrayElement(j);

                Judge [] judgeArray = currentRoom.getJudgeArray();
                Entity [] entityArray = currentRoom.getEntityArray();

                for (int k = 0; k < judgeArray.length; k++) { // for every judge in the room
                    Judge currentJudge = judgeArray[k];

                    for (int l = 0; l < judgeArray.length; l++) {
                        Judge otherJudge = judgeArray[l];
                        if (otherJudge.getID() == currentJudge.getID()) {
                            continue;
                        } else {
                            currentJudge.addBlockedJudge(otherJudge); // IF THERE IS AN ERROR HERE, IT MEANS THAT ALLOCATION WAS WRONG
                        }
                    }
                }

                for (int k = 0; k < entityArray.length; k++) { // for every entity in the room
                    Entity currentEntity = entityArray[k];

                    for (int l = 0; l < judgeArray.length; l++) {
                        Judge currentJudge = judgeArray[l];
                        currentEntity.addBlockedJudge(currentJudge); // IF THERE IS AN ERROR HERE, IT MEANS THAT ALLOCATION WAS WRONG
                    }
                }
            }
        }
    }

    private void eraseEncounteredJudgeTrees() {
        for (int i = 0; i < judgeTree.size(); i++) {
            Judge currentJudge = (Judge) judgeTree.getNodeData(i);
            currentJudge.eraseEncounteredJudgeTree();
        }

        for (int i = 0; i < ooEntityTree.size(); i++) {
            Entity currentEntity = (Entity) ooEntityTree.getNodeData(i);
            currentEntity.eraseEncounteredJudgeTree();
        }

        for (int i = 0; i < oiEntityTree.size(); i++) {
            Entity currentEntity = (Entity) oiEntityTree.getNodeData(i);
            currentEntity.eraseEncounteredJudgeTree();
        }

        for (int i = 0; i < isEntityTree.size(); i++) {
            Entity currentEntity = (Entity) isEntityTree.getNodeData(i);
            currentEntity.eraseEncounteredJudgeTree();
        }

        for (int i = 0; i < daEntityTree.size(); i++) {
            Entity currentEntity = (Entity) daEntityTree.getNodeData(i);
            currentEntity.eraseEncounteredJudgeTree();
        }

        for (int i = 0; i < debateEntityTree.size(); i++) {
            Entity currentEntity = (Entity) debateEntityTree.getNodeData(i);
            currentEntity.eraseEncounteredJudgeTree();
        }
    }

    private void eraseBlockedJudgeTrees() {
        for (int i = 0; i < judgeTree.size(); i++) {
            Judge currentJudge = (Judge) judgeTree.getNodeData(i);
            currentJudge.eraseBlockedJudgeTree();
        }

        for (int i = 0; i < ooEntityTree.size(); i++) {
            Entity currentEntity = (Entity) ooEntityTree.getNodeData(i);
            currentEntity.eraseBlockedJudgeTree();
        }

        for (int i = 0; i < oiEntityTree.size(); i++) {
            Entity currentEntity = (Entity) oiEntityTree.getNodeData(i);
            currentEntity.eraseBlockedJudgeTree();
        }

        for (int i = 0; i < isEntityTree.size(); i++) {
            Entity currentEntity = (Entity) isEntityTree.getNodeData(i);
            currentEntity.eraseBlockedJudgeTree();
        }

        for (int i = 0; i < daEntityTree.size(); i++) {
            Entity currentEntity = (Entity) daEntityTree.getNodeData(i);
            currentEntity.eraseBlockedJudgeTree();
        }

        for (int i = 0; i < debateEntityTree.size(); i++) {
            Entity currentEntity = (Entity) debateEntityTree.getNodeData(i);
            currentEntity.eraseBlockedJudgeTree();
        }
    }

    // changes the contents of the encountered judge trees to be the same as the contents of the blocked judge trees
    // so that the allocation of successive events is possible
    /*private void harmonizeEncounteredAndBlockedJudges() {
        for (int i = 0; i < judgeTree.size(); i++) { // for every judge
            Judge currentJudge = (Judge) judgeTree.getNodeData(i);

            currentJudge.eraseEncounteredJudgeTree();

            for (int j = 0; j < currentJudge.getBlockedJudgeTreeSize(); j++) {
                Judge blockedJudge = currentJudge.getBlockedJudgeTreeNodeData(j);
                currentJudge.addEncounteredJudge(blockedJudge);
            }
        }

        for (int i = 0; i < ooEntityTree.size(); i++) {
            Entity currentEntity = (Entity) ooEntityTree.getNodeData(i);

            currentEntity.eraseEncounteredJudgeTree();

            for (int j = 0; j < currentEntity.getBlockedJudgeTreeSize(); j++) {
                Judge blockedJudge = currentEntity.getBlockedJudgeTreeNodeData(j);
                currentEntity.addEncounteredJudgeToRound(blockedJudge, 0); // SINCE IT DOESN'T MATTER WHERE THEY ARE, THEY ARE PUT TO ROUND 0
            }
        }

        for (int i = 0; i < oiEntityTree.size(); i++) {
            Entity currentEntity = (Entity) oiEntityTree.getNodeData(i);

            currentEntity.eraseEncounteredJudgeTree();

            for (int j = 0; j < currentEntity.getBlockedJudgeTreeSize(); j++) {
                Judge blockedJudge = currentEntity.getBlockedJudgeTreeNodeData(j);
                currentEntity.addEncounteredJudgeToRound(blockedJudge, 0); // SINCE IT DOESN'T MATTER WHERE THEY ARE, THEY ARE PUT TO ROUND 0
            }
        }

        for (int i = 0; i < isEntityTree.size(); i++) {
            Entity currentEntity = (Entity) isEntityTree.getNodeData(i);

            currentEntity.eraseEncounteredJudgeTree();

            for (int j = 0; j < currentEntity.getBlockedJudgeTreeSize(); j++) {
                Judge blockedJudge = currentEntity.getBlockedJudgeTreeNodeData(j);
                currentEntity.addEncounteredJudgeToRound(blockedJudge, 0); // SINCE IT DOESN'T MATTER WHERE THEY ARE, THEY ARE PUT TO ROUND 0
            }
        }

        for (int i = 0; i < daEntityTree.size(); i++) {
            Entity currentEntity = (Entity) daEntityTree.getNodeData(i);

            currentEntity.eraseEncounteredJudgeTree();

            for (int j = 0; j < currentEntity.getBlockedJudgeTreeSize(); j++) {
                Judge blockedJudge = currentEntity.getBlockedJudgeTreeNodeData(j);
                currentEntity.addEncounteredJudgeToRound(blockedJudge, 0); // SINCE IT DOESN'T MATTER WHERE THEY ARE, THEY ARE PUT TO ROUND 0
            }
        }

        for (int i = 0; i < debateEntityTree.size(); i++) {
            Entity currentEntity = (Entity) debateEntityTree.getNodeData(i);

            currentEntity.eraseEncounteredJudgeTree();

            for (int j = 0; j < currentEntity.getBlockedJudgeTreeSize(); j++) {
                Judge blockedJudge = currentEntity.getBlockedJudgeTreeNodeData(j);
                currentEntity.addEncounteredJudgeToRound(blockedJudge, 0); // SINCE IT DOESN'T MATTER WHERE THEY ARE, THEY ARE PUT TO ROUND 0
            }
        }
    }*/

    public Event[] getEventArray() {
        Event [] returnEventArray = new Event[eventArrayLength];

        for (int i = 0; i < eventArrayLength; i++) {
            returnEventArray[i] = eventArray[i];
        }

        return returnEventArray;
    }
    public int getEventArrayLength() {
        return eventArrayLength;
    }
    public Event getEventArrayElement(int index) throws ArrayIndexOutOfBoundsException {
        return eventArray[index];
    }

    public void setEvent(QualificationEvent event, int index) {
        if (eventArray[index] == null && event != null) {
            eventArrayLength += 1;
        }

        eventArray[index] = event;
    }
}
