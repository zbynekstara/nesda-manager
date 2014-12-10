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

/**
 * The QualificationEvent class is responsible for qualification-event-specific
 * tasks, mainly the creation of round/room/judge & entity hierarchy. This is
 * the class that makes the integral part of the program, allocation of judges
 * and entities, possible
 *
 * @author Zbyněk Stara
 */
public class QualificationEvent extends Event {
    /**
     * The AllocationWorker class is an extension of the SwingWorker<V, T> class
     * from the extension package javax.swing. Its purpose is to allow for a
     * computation-extensive part of the program to be executed in the
     * background, which is useful for the allocation algorithm.
     *
     * @author Zbyněk Stara
     */
    private class AllocationWorker extends javax.swing.SwingWorker<Boolean, Boolean> {
        AllocationWorker () {
            
        }

        /**
         * This method specifies what will be done in the background after the
         * allocation worker is executed with the aw.execute() method
         *
         * @return true in any case
         * @throws Exception if the allocation worker is unable to complete the
         * task
         *
         * @author Zbyněk Stara
         */
        @Override
        public Boolean doInBackground() throws Exception {
            try {
                startTime = System.currentTimeMillis();
                initializeRoundTryArray();
                allocate(thisEvent, judgeTree, entityTree, true, 0, 0, judgesPerRoom, entitiesPerRoom, false, 0, 0);
            } catch (NotEnoughJudgesException ex) {
                thisEvent.setNotEnoughJudges(true);
            } catch (ImpossibleToAllocateException ex) {
                thisEvent.setImpossibleToAllocate(true);
            }
            return true;
        }

        /**
         * This method specifies what the program will do after the task in
         * the doInBackground() method finishes.
         *
         * @author Zbyněk Stara
         */
        @Override
        protected void done() {
            // the allocation info dialog will be reset end hidden
            gui.MainGUI.getAllocationInfoTextField().setText("");
            gui.MainGUI.getAllocationInfoProgressBar().setValue(0);
            gui.MainGUI.getAllocationInfoDialog().setVisible(false);
        }
    }

    private class RoundTryRegister {
        private int [] judgeTryArray;
        private int [] entityTryArray;

        public RoundTryRegister(int numJudges, int numEntities) {
            judgeTryArray = new int[numJudges];
            entityTryArray = new int[numEntities];

            for (int i = 0; i < judgeTryArray.length; i++) {
                judgeTryArray[i] = 0;
            }
            for (int i = 0; i < entityTryArray.length; i++) {
                entityTryArray[i] = 0;
            }
        }

        public void setJudgeTry(int judgeTryIndex, int value) {
            judgeTryArray[judgeTryIndex] = value;

            // erasing subsequent judge tries
            for (int i = judgeTryIndex + 1; i < judgeTryArray.length; i++) {
                judgeTryArray[i] = 0;
            }

            // erasing entity tries
            eraseEntityTryArray();
        }
        public void setEntityTry(int entityTryIndex, int value) {
            entityTryArray[entityTryIndex] = value;

            // erasing subsequent entity tries
            for (int i = entityTryIndex + 1; i < entityTryArray.length; i++) {
                entityTryArray[i] = 0;
            }
        }

        public void eraseJudgeTryArray() {
            for (int i = 0; i < judgeTryArray.length; i++) {
                judgeTryArray[i] = 0;
            }
        }
        public void eraseEntityTryArray() {
            for (int i = 0; i < entityTryArray.length; i++) {
                entityTryArray[i] = 0;
            }
        }

        public int getJudgeTry(int judgeTryIndex) {
            return judgeTryArray[judgeTryIndex];
        }
        public int getEntityTry(int entityTryIndex) {
            return entityTryArray[entityTryIndex];
        }

        public int judgeTryArrayLength() {
            return judgeTryArray.length;
        }
        public int entityTryArrayLength() {
            return entityTryArray.length;
        }
    }

    private final QualificationEvent thisEvent = this; // this qualification, used for reference from the allocation worker

    private RoundTryRegister [] roundTryArray = new RoundTryRegister[roundArray.length];

    private AllocationWorker aw; // allocation worker to handle the allocation in the background

    private long startTime;

    private int updateCounter = 0;

    public QualificationEvent(Qualification qualification, Type eventType, BinarySearchTree judgeTree, BinarySearchTree entityTree, int judgesPerRoom, int entitiesPerRoom) {
        super(qualification, eventType, false, false, judgeTree, entityTree, judgesPerRoom, entitiesPerRoom);
    }

    public void initializeRounds() {
        // make a new allocation worker that will do the allocation in the background
        aw = new AllocationWorker();

        // this listener is added to update the numbers reported by the progress dialog
        aw.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if ("update".equals(evt.getPropertyName())) {
                    updateCounter += 1;
                    if (updateCounter == 100) {
                        thisEvent.updateAllocationInfo(thisEvent);
                        updateCounter = 0;
                    }
                }
            }
        });

        // this is to execute the allocation in the background
        // calls the doInBackground() function of the AllocationWorker
        aw.execute();
        
        // set up the allocation info dialog
        gui.MainGUI.getAllocationInfoTextField().setText("");
        gui.MainGUI.getAllocationInfoProgressBar().setValue(0);
        gui.MainGUI.getAllocationInfoDialog().setVisible(true);
    }

    // PROBLEM WITH ALLOCATION: ENCOUNTERED JUDGE TREES ARE USED IN AN UNINTENDED WAY
    // WHEN A JUDGE IS TRIED, HE IS KEPT IN THE LISTS
    // THEREFORE, IN THE ENCOUNTERED TREES, THERE ARE JUDGES THAT WERE ENCOUNTERED IN THE ALLOCATION STAGE
    // BUT AFTER THAT, MOST OF THEM ARE IN DIFFERENT ROUNDS
    // THAT IS WHY BLOCKED TREES WERE DEVISED
    /**
     * This method is the heart of the program, it is responsible for allocating
     * judges and entities to rounds and rooms so that:<br />
     * (1) no two judges from the same school are in one room,<br />
     * (2) no two judges who have already met in one round can meet in another,<br />
     * (3) no entity can meet a judge from the same school,<br />
     * (4) no two entities from the same school are in one room.
     *
     * @param event this event
     * @param judgeTree BinarySearchTree of judges to be used for allocation
     * @param entityTree BinarySearchTree of entities to be used for allocation
     * @param allocatingRound boolean value indicating if a round is being
     * allocated in this iteration
     * @param currentRoundIndex int value with the index of the current round
     * @param currentRoomIndex int value with the index of the current room
     * @param judgesPerRoom int value with maximum number of judges per room
     * @param entitiesPerRoom int value with maximum number of entities per room
     * @param allocatingJudge boolean value indicating if a judge is being
     * allocated in this iteration
     * @param numAllocatedJudges int value with the number of judges currently
     * allocated
     * @param numAllocatedEntities int value with the number of entities
     * currently allocated
     * @param currentCombination int value with the number of the current
     * combination
     * @throws NotEnoughJudgesException if it is found during allocation that
     * there is not enough judges in the judgeTree to fill the judgesPerRoom
     * limits of the rooms
     * @throws ImpossibleToAllocateException if no solution to the allocation
     * problem was found. This usually means that there is not enough variety
     * in terms of the schools from which the judges and entities come. However,
     * it is possible that no solution was found because of the limited number
     * of tries. That is why the exception text should suggest to the user to
     * try again.
     *
     * @author Zbyněk Stara
     */
    @Override
    public void allocate(Event event, BinarySearchTree judgeTree, BinarySearchTree entityTree,
            boolean allocatingRound, int currentRoundIndex, int currentRoomIndex,
            int judgesPerRoom, int entitiesPerRoom,
            boolean allocatingJudge, int numAllocatedJudges, int numAllocatedEntities)
            throws NotEnoughJudgesException, ImpossibleToAllocateException {
        if (allocatingRound && currentRoundIndex < roundArray.length) { // if the allocation of judges and entities has ended for the previous round and other round still needs allocation
            // set up the round name
            String roundName = "<Unnamed>";
            switch (event.type) {
                case ORIGINAL_ORATORY:
                    roundName = event.getShortEventName() + " R" + (currentRoundIndex + 1) + ": " + event.ooRoundTimes[currentRoundIndex];
                    break;
                case ORAL_INTERPRETATION:
                    roundName = event.getShortEventName() + " R" + (currentRoundIndex + 1) + ": " + event.oiRoundTimes[currentRoundIndex];
                    break;
                case IMPROMPTU_SPEAKING:
                    roundName = event.getShortEventName() + " R" + (currentRoundIndex + 1) + ": " + event.isRoundTimes[currentRoundIndex];
                    break;
                case DUET_ACTING:
                    roundName = event.getShortEventName() + " R" + (currentRoundIndex + 1) + ": " + event.daRoundTimes[currentRoundIndex];
                    break;
                case DEBATE:
                    if (currentRoundIndex != 1) {
                        roundName = event.getShortEventName() + " R" + (currentRoundIndex + 1) + ": " + event.debateRoundTimes[currentRoundIndex];
                    } else {
                        roundName = "Impromptu " + event.getShortEventName() + ": " + event.debateRoundTimes[currentRoundIndex];
                    }
                    break;
            }

            // make a new round and add it to this event's round array, then set up the room array of the round
	    Round newRound = new Round(event, currentRoundIndex, roundName, judgeTree, entityTree, judgesPerRoom, entitiesPerRoom);
	    roundArray[currentRoundIndex] = newRound;
            roundArray[currentRoundIndex].initializeRooms();

            // change the numbers in the allocation info dialog
            aw.firePropertyChange("update", 0, 1); // old- and newValue both zero

            // start the allocation proper of this round
	    allocate(event, judgeTree, entityTree, false, currentRoundIndex, 0, judgesPerRoom, entitiesPerRoom, true, 0, 0);
	} else if (allocatingRound && currentRoundIndex >= roundArray.length) { // if allocation of judges and entities has ended in previous round and there is no other round to allocate
	    // finish the recursion
	} else if (allocatingJudge) { // if we are allocating a judge in this iteration of the recursive allocate() method
            // set up the do-while try loop
            boolean judgeAllocationSuccessful = false;
            int judgeAllocationTryCounter = 0;
            BinarySearchTree triedJudges = new BinarySearchTree();
            do {
                // increment the current try (maximum is 2)
                judgeAllocationTryCounter += 1;

                // update info
                changeJudgeTry(currentRoundIndex, numAllocatedJudges, judgeAllocationTryCounter);

                // send an update to the progress dialog
                aw.firePropertyChange("update", 0, 1); // abuse of notation here: old- and newValue used to send different attributes to the update function

                // try to allocate a new randomly chosen judge
                Judge randomJudge = null;
                try {
                    // get a new random judge
                    randomJudge = roundArray[currentRoundIndex].getRandomFreeUntriedJudge(triedJudges);
                    if (randomJudge == null) {
                        if (triedJudges.isEmpty()) {
                            // there are no more judges to use
                            // cannot continue with allocation
                            throw new NotEnoughJudgesException();
                        } else {
                            // break the loop; this leads to an ImpossibleToAllocateException
                            break;
                        }
                    } else {
                        // insert the judge to tried judges
                        triedJudges.insert(randomJudge, randomJudge.getID() + "");
                        
                        // add the judge to current room in current round
                        roundArray[currentRoundIndex].getRoomArrayElement(currentRoomIndex).allocateJudge(randomJudge); // IllegalArgumentException if impossible (encountered or same school)
                        // remove the judge from the free judge tree
                        roundArray[currentRoundIndex].removeFreeJudge(randomJudge);
                        //increment number of allocated judges
                        numAllocatedJudges += 1;
                    }

                    /*randomJudge = roundArray[currentRoundIndex].getRandomFreeJudge(); // REPLACED
                    if (randomJudge == null) { // if no judge was accessed
                        // there are no more judges to use
                        // cannot continue with allocation
                        throw new NotEnoughJudgesException();
                    } else if (!triedJudges.contains(randomJudge.getID() + "")) { // if the judge was not tried yet
                        // insert the judge to tried judges
                        triedJudges.insert(randomJudge, randomJudge.getID() + "");

                        // add the judge to current room in current round, remove the judge from free judge tree, increment number of allocated judges
                        roundArray[currentRoundIndex].getRoomArrayElement(currentRoomIndex).allocateJudge(randomJudge); // throws IllegalArgumentException if it cannot be done (encountered or same school)
                        roundArray[currentRoundIndex].removeFreeJudge(randomJudge);
                        numAllocatedJudges += 1;
                    } else { // if the judge was already tried
                        // decrement the try counter (this try did not count)
                        judgeAllocationTryCounter -= 1;
                        continue;
                    }*/

                    // increment the room index (loop around back to zero if necessary)
                    int newRoomIndex = currentRoomIndex;
                    newRoomIndex += 1;
                    if (newRoomIndex >= roundArray[currentRoundIndex].getRoomArrayLength()) {
                        newRoomIndex = 0;
                    }

                    // determine what action will be done in the next iteration
                    if (numAllocatedJudges < (judgesPerRoom * roundArray[currentRoundIndex].getRoomArrayLength())) { // if number of allocated judges is lower than the number that needs to be allocated
                        // allocate anoteher judge in the next iteration (in the next room)
                        allocate(event, judgeTree, entityTree, false, currentRoundIndex, newRoomIndex, judgesPerRoom, entitiesPerRoom, true, numAllocatedJudges, 0);
                        judgeAllocationSuccessful = true;
                    } else { // if the number of allocated judges is exactly what it needs to be
                        // allocate new entity in the recursion
                        allocate(event, judgeTree, entityTree, false, currentRoundIndex, newRoomIndex, judgesPerRoom, entitiesPerRoom, false, numAllocatedJudges, 0);
                        judgeAllocationSuccessful = true;
                    }
                } catch (IllegalArgumentException ex) {
                    // caused by a bad allocation of this judge
                    // will try the allocation again
                    judgeAllocationSuccessful = false;
                    continue;
                } catch (ImpossibleToAllocateException ex) {
                    // caused by two bad allocation attempts in the upper iteration of the algorithm
                    // remove the current judge from the room and add him back among the free judges (plus decrement the number of allocated judges)
                    roundArray[currentRoundIndex].getRoomArrayElement(currentRoomIndex).removeJudge(randomJudge);
                    roundArray[currentRoundIndex].addFreeJudge(randomJudge);
                    numAllocatedJudges -= 1;
                    judgeAllocationSuccessful = false;
                }
            } while (!judgeAllocationSuccessful && triedJudges.size() < roundArray[currentRoundIndex].getFreeJudgeTreeSize() && judgeAllocationTryCounter < 2);
            // break the loop if: judge was successfully allocated, number of tried judges is equal to the number of free judges, or there were already two tries to allocate the judge
            
            if (!judgeAllocationSuccessful) { // if the allocation was not successful
                // throw a new exception saying so to the lower iteration of the algorithm
                throw new ImpossibleToAllocateException();
            }
        } else { // if allocating an entity
            // set up the do-while try loop
            boolean entityAllocationSuccessful = false;
            int entityAllocationTryCounter = 0;
            BinarySearchTree triedEntities = new BinarySearchTree();
            do {
                // increment the entity try counter (maximum is two)
                entityAllocationTryCounter += 1;

                // update info
                changeEntityTry(currentRoundIndex, numAllocatedEntities, entityAllocationTryCounter);

                // send an update to the progress dialog
                aw.firePropertyChange("update", 0, 1);  // old- and newValue both zero

                // try to allocate a new reandomly chosen entity
                Entity randomEntity = null;
                try {
                    // allocate new entity
                    randomEntity = roundArray[currentRoundIndex].getRandomFreeUntriedEntity(triedEntities);
                    if (randomEntity == null) {
                        // break the loop; this leads to an ImpossibleToAllocateException
                        break;
                    } else {
                        // insert the entity to tried entities
                        triedEntities.insert(randomEntity, randomEntity.getCode());

                        // add the judge to current room in current round
                        roundArray[currentRoundIndex].getRoomArrayElement(currentRoomIndex).allocateEntity(randomEntity); // IllegalArgumentException if impossible (encountered or same school)
                        // remove the judge from the free judge tree
                        roundArray[currentRoundIndex].removeFreeEntity(randomEntity);
                        //increment number of allocated judges
                        numAllocatedEntities += 1;
                    }

                    // increment the room index (loop around back to zero if necessary)
                    int newRoomIndex = currentRoomIndex;
                    newRoomIndex += 1;
                    if (newRoomIndex >= roundArray[currentRoundIndex].getRoomArrayLength()) {
                        newRoomIndex = 0;
                    }

                    // determine what action needs to be done in the next iteration of the algorithm
                    if (numAllocatedEntities < entityTree.size()) { // if not all entities were allocated yet
                        // allocate a new random entity in the next iteration of the algorithm
                        allocate(event, judgeTree, entityTree, false, currentRoundIndex, newRoomIndex, judgesPerRoom, entitiesPerRoom, false, numAllocatedJudges, numAllocatedEntities);
                        entityAllocationSuccessful = true;
                    } else { // if all the entities were successfully allocated
                        // allocate new round in the next iteration of the algorithm
                        allocate(event, judgeTree, entityTree, true, currentRoundIndex + 1, currentRoomIndex, judgesPerRoom, entitiesPerRoom, false, numAllocatedJudges, numAllocatedEntities);
                        entityAllocationSuccessful = true;
                    }
                } catch (IllegalArgumentException ex) {
                    // caused by a bad allocation of this entity
                    // will try the allocation again
                    entityAllocationSuccessful = false;
                    continue;
                } catch (ImpossibleToAllocateException ex) {
                    // caused by two bad allocation attempts in the upper iteration of the algorithm
                    // remove the current entity from the room and add it back among the free entites (plus decrement the number of allocated entities)
                    roundArray[currentRoundIndex].getRoomArrayElement(currentRoomIndex).removeEntity(randomEntity);
                    roundArray[currentRoundIndex].addFreeEntity(randomEntity);
                    numAllocatedEntities -= 1;
                    entityAllocationSuccessful = false;
                }
            } while (!entityAllocationSuccessful && triedEntities.size() < roundArray[currentRoundIndex].getFreeEntityTreeSize() && entityAllocationTryCounter < 2);
            // break the loop if: judge was successfully allocated, number of tried judges is equal to the number of free judges, or there were already two tries to allocate the judge
            
            if (!entityAllocationSuccessful) { // if the allocation was not successful
                 // throw a new exception saying so to the lower iteration of the algorithm
                throw new ImpossibleToAllocateException();
            }
        }
    }

    private void initializeRoundTryArray() { // SIMPLE VERSION, DOES NOT TAKE INTO ACCOUNT DIFFERENT ROOM NUMBERS ON DIFFERENT DAYS!!!
        Round [] tempRoundArray = new Round[roundArray.length];

        for (int i = 0; i < roundTryArray.length; i++) {
            Round newRound = new Round(thisEvent, i, "", judgeTree, entityTree, judgesPerRoom, entitiesPerRoom);
	    tempRoundArray[i] = newRound;
            tempRoundArray[i].initializeRooms(); // this would have to be modified to allow differentiation by days

            int numJudgesToAllocate = judgesPerRoom * tempRoundArray[i].getRoomArrayLength();
            int numEntitiesToAllocate = entityTree.size();

            roundTryArray[i] = new RoundTryRegister(numJudgesToAllocate, numEntitiesToAllocate);
        }
    }

    /**
     * This method is responsible for the actual updating of the
     * allocationInfoDialog to show the progress in the allocation to the user.
     *
     * @param currentRoundIndex int with the index of the current round
     * @param currentCombination long with the current combination explored by
     * the algorithm
     *
     * @author Zbyněk Stara
     */
    private void updateAllocationInfo(Event event) {
        String output = "";

        int totalNumbers = 0;
        int totalNonZeroes = 0;

        for (int i = 0; i < roundTryArray.length; i++) {
            totalNumbers += roundTryArray[i].judgeTryArrayLength();
            totalNumbers += roundTryArray[i].entityTryArrayLength();
        }

        for (int i = 0; i < roundTryArray.length; i++) {
            if (i > 0) output += " / ";

            output += event.getShortEventName() + i + ": J";

            for (int j = 0; j < roundTryArray[i].judgeTryArrayLength(); j++) { // judge tries
                int currentNumber = roundTryArray[i].getJudgeTry(j);
                output += currentNumber + "";

                if (currentNumber != 0) totalNonZeroes += 1;
            }

            output += " E";

            for (int j = 0; j < roundTryArray[i].entityTryArrayLength(); j++) { // entity tries
                int currentNumber = roundTryArray[i].getEntityTry(j);
                output += currentNumber + "";

                if (currentNumber != 0) totalNonZeroes += 1;
            }
        }
        
        gui.MainGUI.getAllocationInfoTextField().setText(output);

        int progressBarValue = ((totalNonZeroes * 100) / totalNumbers);
        gui.MainGUI.getAllocationInfoProgressBar().setValue(progressBarValue);

        long currentTime = System.currentTimeMillis();
        gui.MainGUI.getAllocationInfoTimeTextField().setText(((currentTime - startTime) / 1000) + "");
    }

    private void changeJudgeTry(int roundIndex, int judgeTryIndex, int value) {
        roundTryArray[roundIndex].setJudgeTry(judgeTryIndex, value);

        // erase all tries in subsequent rounds
        for (int i = roundIndex + 1; i < roundArray.length; i++) {
            roundTryArray[i].eraseJudgeTryArray();
            roundTryArray[i].eraseEntityTryArray();
        }
    }

    private void changeEntityTry(int roundIndex, int entityTryIndex, int value) {
        roundTryArray[roundIndex].setEntityTry(entityTryIndex, value);

        // erase all tries in subsequent rounds
        for (int i = roundIndex + 1; i < roundArray.length; i++) {
            roundTryArray[i].eraseJudgeTryArray();
            roundTryArray[i].eraseEntityTryArray();
        }
    }
}
