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

/**
 *
 * @author Zbyněk Stara
 */
abstract public class Event {
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

    public enum Type {
        ORIGINAL_ORATORY("Original Oratory", "OO", 2),
        ORAL_INTERPRETATION("Oral Interpretation", "OI", 2),
        IMPROMPTU_SPEAKING("Impromptu Speaking", "IS", 2),
        DUET_ACTING("Duet Acting", "DA", 2),
        DEBATE("Debate", "Debate", 3),
        UNDEFINED("Undefined", "UNDEF", -999);

        private String eventName;
        private String shortEventName;
        private int numRounds;

        private Type(String eventName, String shortEventName, int numRounds) {
            this.eventName = eventName;
            this.shortEventName = shortEventName;
            this.numRounds = numRounds;
        }

        public String getEventName() {
            return eventName;
        }

        public String getShortEventName() {
            return shortEventName;
        }

        public int getNumRounds() {
            return numRounds;
        }
    }

    protected final String[] ooRoundTimes = {"Day 1, 11-12", "Day 1, 16-17"};
    protected final String[] oiRoundTimes = {"Day 1, 15-16", "Day 2, 09-10"};
    protected final String[] isRoundTimes = {"Day 1, 10-11", "Day 1, 14-15"};
    protected final String[] daRoundTimes = {"Day 1, 10-11", "Day 1, 14-15"};
    protected final String[] debateRoundTimes = {"Day 1, 09-10", "Day 1, 13-14", "Day 1, 17-18"};

    /*protected final String debateSemifinalTime = "Day 2, 08-09";

    protected final String ooFinalTime = "Day 2, 10-11";
    protected final String oiFinalTime = "Day 2, 11-12";
    protected final String isFinalTime = "Day 2, 13-14";
    protected final String daFinalTime = "Day 2, 14-15";
    protected final String debateFinalTime = "Day 2, 15-16";*/

    public final Qualification qualification;

    public final Type type;

    //private EventPair eventPair;
    private final boolean isSemifinal;
    private final boolean isFinal;

    protected BinarySearchTree judgeTree;
    protected BinarySearchTree entityTree;

    protected int judgesPerRoom;
    protected int entitiesPerRoom;

    private boolean notEnoughJudges = false;
    private boolean impossibleToAllocate = false;

    protected Round[] roundArray;

    public Event(Qualification qualification, Type type, boolean isSemifinal, boolean isFinal, BinarySearchTree judgeTree, BinarySearchTree entityTree, int judgesPerRoom, int entitiesPerRoom) throws IllegalArgumentException {
        this.qualification = qualification;
        this.type = type;
        this.isSemifinal = isSemifinal;
        this.isFinal = isFinal;

        if (!isSemifinal && isFinal) {
            roundArray = new Round[1];
            if (this.type == Event.Type.UNDEFINED) {
                throw new IllegalArgumentException("Undefined types are not allowed.");
            }
        } else if (isSemifinal && !isFinal) {
            roundArray = new Round[1];
            if (type != Type.DEBATE) {
                throw new IllegalArgumentException("Semifinals can only be of the Debate type.");
            }
        } else if (!isSemifinal && !isFinal) {
            if (this.type == Event.Type.DEBATE) {
                roundArray = new Round[this.type.getNumRounds()];

                for (int i = 0; i < qualification.getDebateEntityTreeSize(); i++) {
                    Entity editedEntity = qualification.getDebateEntityTreeNodeData(i);

                    editedEntity.setRoundsNumber(roundArray.length);
                    editedEntity.setRoundNewEncounteredJudgesArray();

                    qualification.setDebateEntityTreeNodeData(i, editedEntity);
                }
            } else if (this.type == Event.Type.ORIGINAL_ORATORY) {
                roundArray = new Round[this.type.getNumRounds()];

                for (int i = 0; i < qualification.getOOEntityTreeSize(); i++) {
                    Entity editedEntity = qualification.getOOEntityTreeNodeData(i);

                    editedEntity.setRoundsNumber(roundArray.length);
                    editedEntity.setRoundNewEncounteredJudgesArray();

                    qualification.setOOEntityTreeNodeData(i, editedEntity);
                }
            } else if (this.type == Event.Type.ORAL_INTERPRETATION) {
                roundArray = new Round[this.type.getNumRounds()];

                for (int i = 0; i < qualification.getOIEntityTreeSize(); i++) {
                    Entity editedEntity = qualification.getOIEntityTreeNodeData(i);

                    editedEntity.setRoundsNumber(roundArray.length);
                    editedEntity.setRoundNewEncounteredJudgesArray();

                    qualification.setOIEntityTreeNodeData(i, editedEntity);
                }
            } else if (this.type == Event.Type.IMPROMPTU_SPEAKING) {
                roundArray = new Round[this.type.getNumRounds()];

                for (int i = 0; i < qualification.getISEntityTreeSize(); i++) {
                    Entity editedEntity = qualification.getISEntityTreeNodeData(i);

                    editedEntity.setRoundsNumber(roundArray.length);
                    editedEntity.setRoundNewEncounteredJudgesArray();

                    qualification.setISEntityTreeNodeData(i, editedEntity);
                }
            } else if (this.type == Event.Type.DUET_ACTING) {
                roundArray = new Round[this.type.getNumRounds()];

                for (int i = 0; i < qualification.getDAEntityTreeSize(); i++) {
                    Entity editedEntity = qualification.getDAEntityTreeNodeData(i);

                    editedEntity.setRoundsNumber(roundArray.length);
                    editedEntity.setRoundNewEncounteredJudgesArray();

                    qualification.setDAEntityTreeNodeData(i, editedEntity);
                }
            } else {
                throw new IllegalArgumentException("Undefined types are not allowed.");
            }
        } else { // isSemifinal && isFinal
            throw new IllegalArgumentException("One event cannot be a semifinal and a final at the same time.");
        }
        
        this.judgeTree = judgeTree;
        this.entityTree = entityTree;

        this.judgesPerRoom = judgesPerRoom;
        this.entitiesPerRoom = entitiesPerRoom;
    }

    /*public void setEventPair(EventPair eventPair) { // NOT YET
        this.eventPair = eventPair;
    }*/

    public String getTypeEventName() {
        return type.getEventName();
    }

    /*public EventPair getEventPair() { // NOT YET
        return eventPair;
    }*/

    public boolean isSemifinal() {
        return isSemifinal;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public String getShortEventName() {
        return type.getShortEventName();
    }

    public int getJudgesPerRoom() {
        return judgesPerRoom;
    }

    public int getEntitiesPerRoom() {
        return entitiesPerRoom;
    }

    public Round[] getRoundArray() throws IllegalStateException {
        try {
            Round [] returnRoundArray = new Round[roundArray.length];

            for (int i = 0; i < roundArray.length; i++) {
                returnRoundArray[i] = roundArray[i];
            }

            return returnRoundArray;
        } catch (NullPointerException ex) {
            throw new IllegalStateException();
        }
    }

    public int getRoundArrayLength() {
        return roundArray.length;
    }

    public Round getRoundArrayElement(int index) throws ArrayIndexOutOfBoundsException {
        return roundArray[index];
    }

    public void setRound(Round round, int index) {
        roundArray[index] = round;
    }

    abstract public void initializeRounds();

    abstract public void allocate(
            Event event, BinarySearchTree judgeTree, BinarySearchTree entityTree,
            boolean allocatingRound, int currentRoundIndex, int currentRoomIndex,
            int judgesPerRoom, int entitiesPerRoom,
            boolean allocatingJudge, int numAllocatedJudges, int numAllocatedEntities)
            throws IllegalStateException /*because of odd number for debate*/, NotEnoughJudgesException, ImpossibleToAllocateException;

    public boolean getNotEnoughJudges() {
        return notEnoughJudges;
    }

    public boolean getImpossibleToAllocate() {
        return impossibleToAllocate;
    }

    protected void setNotEnoughJudges(boolean notEnoughJudges) {
        this.notEnoughJudges = notEnoughJudges;
    }

    protected void setImpossibleToAllocate(boolean impossibleToAllocate) {
        this.impossibleToAllocate = impossibleToAllocate;
    }

    @Override
    public String toString() {
        return "Type: " + type.getEventName();
    }
}
