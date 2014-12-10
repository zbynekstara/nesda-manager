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
 * The Database class is the basis of the hierarchical data structure of this
 * program. It takes care of the Participants tab – that means that it holds a
 * BinarySearchTree of schools (which then hold trees of teachers, students,
 * unpaired students and pairs), and methods to work upon them. It also holds
 * methods that relate to students or teachers, but whose scope includes all of
 * the database – like the global search of students.
 *
 * @author Zbynda
 */
public class Database {
    private BinarySearchTree schoolTree = new BinarySearchTree();

    // <editor-fold defaultstate="collapsed" desc="Default settings variables declaration">
    private final int defaultOOStudentRoomLimit = 6;
    private final int defaultOIStudentRoomLimit = 6;
    private final int defaultISStudentRoomLimit = 6;
    private final int defaultDAStudentRoomLimit = 4; // pairs
    private final int defaultOOFinalStudentRoomLimit = 6;
    private final int defaultOIFinalStudentRoomLimit = 6;
    private final int defaultISFinalStudentRoomLimit = 6;
    private final int defaultDAFinalStudentRoomLimit = 4; // pairs

    private final int defaultOOStudentSchoolLimit = 4;
    private final int defaultOIStudentSchoolLimit = 4;
    private final int defaultISStudentSchoolLimit = 4;
    private final int defaultDAStudentSchoolLimit = 2; // pairs
    private final int defaultDebateStudentSchoolLimit = 2; // pairs

    private final int defaultOOJudgeRoomLimit = 2;
    private final int defaultOIJudgeRoomLimit = 2;
    private final int defaultISJudgeRoomLimit = 2;
    private final int defaultDAJudgeRoomLimit = 2;
    private final int defaultDebateJudgeRoomLimit = 3;
    private final int defaultFinalsJudgeRoomLimit = 5;

    private final int defaultRoomLimit1 = 8;
    private final int defaultRoomLimit2 = 8;
    private final int defaultTimeLimit1 = 8; // hours
    private final int defaultTimeLimit2 = 8; // hours
    private final int defaultSchoolNumber = 12;
    private final int defaultTeacherSchoolNumber = 2;
    private final int defaultStudentSchoolNumber = 12;

    private final boolean defaultAllowCombinedEvents = true;
    
    private final int defaultCombinedEvent1 = 2;
    private final int defaultCombinedEvent2 = 3;// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Settings variables declaration">
    private int ooStudentRoomLimit = defaultOOStudentRoomLimit;
    private int oiStudentRoomLimit = defaultOIStudentRoomLimit;
    private int isStudentRoomLimit = defaultISStudentRoomLimit;
    private int daStudentRoomLimit = defaultDAStudentRoomLimit;
    private int ooFinalStudentRoomLimit = defaultOOFinalStudentRoomLimit;
    private int oiFinalStudentRoomLimit = defaultOIFinalStudentRoomLimit;
    private int isFinalStudentRoomLimit = defaultISFinalStudentRoomLimit;
    private int daFinalStudentRoomLimit = defaultDAFinalStudentRoomLimit;

    private int ooStudentSchoolLimit = defaultOOStudentSchoolLimit;
    private int oiStudentSchoolLimit = defaultOIStudentSchoolLimit;
    private int isStudentSchoolLimit = defaultISStudentSchoolLimit;
    private int daStudentSchoolLimit = defaultDAStudentSchoolLimit;
    private int debateStudentSchoolLimit = defaultDebateStudentSchoolLimit;

    private int ooJudgeRoomLimit = defaultOOJudgeRoomLimit;
    private int oiJudgeRoomLimit = defaultOIJudgeRoomLimit;
    private int isJudgeRoomLimit = defaultISJudgeRoomLimit;
    private int daJudgeRoomLimit = defaultDAJudgeRoomLimit;
    private int debateJudgeRoomLimit = defaultDebateJudgeRoomLimit;
    private int finalsJudgeRoomLimit = defaultFinalsJudgeRoomLimit;

    private int roomLimit1 = defaultRoomLimit1;
    private int roomLimit2 = defaultRoomLimit2;
    private int timeLimit1 = defaultTimeLimit1; // hours
    private int timeLimit2 = defaultTimeLimit2; // hours
    private int schoolNumber = defaultSchoolNumber;
    private int teacherSchoolNumber = defaultTeacherSchoolNumber;
    private int studentSchoolNumber = defaultStudentSchoolNumber;

    private boolean allowCombinedEvents = defaultAllowCombinedEvents;

    private int combinedEvent1 = defaultCombinedEvent1;
    private int combinedEvent2 = defaultCombinedEvent2;// </editor-fold>

    public Database() {

    }

    /**
     * The setSettings method sets the values of the settings variables.
     * <p>
     * The values of text field settings all have to be higher than zero; if
     * some of them is not, it is recorded. In addition, the two timeLimit
     * variables both have to be less than or equal to 24. The two combinedEvent
     * combo boxes can only have values of 0 to 3 inclusive, therefore any other
     * value is illegal.
     *
     * @param valueArray an array of objects, storing the values of the settings
     * variables
     * @throws IllegalArgumentException if the Objects in the array are of wrong
     * class (they are mostly Integers, except for #26, which is a Boolean);
     * also thrown if the array has incorrect length (other than 29)
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-11-1012)
     * @since Nov-11-2012
     */
    public void setSettings(Object [] valueArray) throws IllegalArgumentException {
        if (valueArray.length == 29) {
            try{
                ooStudentRoomLimit = (Integer) valueArray[0];
                oiStudentRoomLimit = (Integer) valueArray[1];
                isStudentRoomLimit = (Integer) valueArray[2];
                daStudentRoomLimit = (Integer) valueArray[3];
                ooFinalStudentRoomLimit = (Integer) valueArray[4];
                oiFinalStudentRoomLimit = (Integer) valueArray[5];
                isFinalStudentRoomLimit = (Integer) valueArray[6];
                daFinalStudentRoomLimit = (Integer) valueArray[7];

                ooStudentSchoolLimit = (Integer) valueArray[8];
                oiStudentSchoolLimit = (Integer) valueArray[9];
                isStudentSchoolLimit = (Integer) valueArray[10];
                daStudentSchoolLimit = (Integer) valueArray[11];
                debateStudentSchoolLimit = (Integer) valueArray[12];

                ooJudgeRoomLimit = (Integer) valueArray[13];
                oiJudgeRoomLimit = (Integer) valueArray[14];
                isJudgeRoomLimit = (Integer) valueArray[15];
                daJudgeRoomLimit = (Integer) valueArray[16];
                debateJudgeRoomLimit = (Integer) valueArray[17];
                finalsJudgeRoomLimit = (Integer) valueArray[18];

                roomLimit1 = (Integer) valueArray[19];
                roomLimit2 = (Integer) valueArray[20];
                timeLimit1 = (Integer) valueArray[21]; // hours
                timeLimit2 = (Integer) valueArray[22]; // hours
                schoolNumber = (Integer) valueArray[23];
                teacherSchoolNumber = (Integer) valueArray[24];
                studentSchoolNumber = (Integer) valueArray[25];

                allowCombinedEvents = (Boolean) valueArray[26];

                combinedEvent1 = (Integer) valueArray[27];
                combinedEvent2 = (Integer) valueArray[28];
            } catch (ClassCastException ex) {
                throw new IllegalArgumentException();
            }
        } else throw new IllegalArgumentException();
    }

    /**
     * The checkSettings method checks the correctness of the setting values.
     * <p>
     * The values of text field settings all have to be higher than zero; if
     * some of them is not, it is recorded. In addition, the two timeLimit
     * variables both have to be less than or equal to 24. The two combinedEvent
     * combo boxes can only have values of 0 to 3 inclusive, therefore any other
     * value is illegal.
     *
     * @return array of booleans, showing if the values are legal (true means
     * "no problem", false means "this value is illegal")
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-11-1012)
     * @since Nov-11-2012
     */
    public boolean [] checkSettings() {
        boolean [] checkArray = new boolean[29]; // true means "no problem", false means "this value is illegal"

        checkArray[0] = (ooStudentRoomLimit > 0) ? true : false; // if the variable has a legal value, return true, else return false
        checkArray[1] = (oiStudentRoomLimit > 0) ? true : false;
        checkArray[2] = (isStudentRoomLimit > 0) ? true : false;
        checkArray[3] = (daStudentRoomLimit > 0) ? true : false;
        checkArray[4] = (ooFinalStudentRoomLimit > 0) ? true : false;
        checkArray[5] = (oiFinalStudentRoomLimit > 0) ? true : false;
        checkArray[6] = (isFinalStudentRoomLimit > 0) ? true : false;
        checkArray[7] = (daFinalStudentRoomLimit > 0) ? true : false;

        checkArray[8] = (ooStudentSchoolLimit > 0) ? true : false;
        checkArray[9] = (oiStudentSchoolLimit > 0) ? true : false;
        checkArray[10] = (isStudentSchoolLimit > 0) ? true : false;
        checkArray[11] = (daStudentSchoolLimit > 0) ? true : false;
        checkArray[12] = (debateStudentSchoolLimit > 0) ? true : false;

        checkArray[13] = (ooJudgeRoomLimit > 0) ? true : false;
        checkArray[14] = (oiJudgeRoomLimit > 0) ? true : false;
        checkArray[15] = (isJudgeRoomLimit > 0) ? true : false;
        checkArray[16] = (daJudgeRoomLimit > 0) ? true : false;
        checkArray[17] = (debateJudgeRoomLimit > 0) ? true : false;
        checkArray[18] = (finalsJudgeRoomLimit > 0) ? true : false;

        checkArray[19] = (roomLimit1 > 0) ? true : false;
        checkArray[20] = (roomLimit2 > 0) ? true : false;
        checkArray[21] = (timeLimit1 > 0 && timeLimit1 <= 24) ? true : false;
        checkArray[22] = (timeLimit2 > 0 && timeLimit2 <= 24) ? true : false;
        checkArray[23] = (schoolNumber > 0) ? true : false;
        checkArray[24] = (teacherSchoolNumber > 0) ? true : false;
        checkArray[25] = (studentSchoolNumber > 0) ? true : false;

        checkArray[26] = true; // this can only be true or false (which are both legal possibilities, so there cannot be any problems

        checkArray[27] = (allowCombinedEvents) ? ((combinedEvent1 >= 0 && combinedEvent1 < 4) ? true : false) : true; // only reports a problem if combined events are allowed
        checkArray[28] = (allowCombinedEvents) ? ((combinedEvent2 >= 0 && combinedEvent2 < 4) ? true : false) : true;

        return checkArray;
    }

    /**
     * The resetSettings() resets all database attributes back to their default
     * values.
     * <p>
     * Along with resetting the database attributes back to default values,
     * this method returns an array of the default values themselves.
     *
     * @return array of objects with the default values of the settings
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public Object [] resetSettings() {
        Object [] settingsArray = new Object [29];

        settingsArray[0] = ooStudentRoomLimit = defaultOOStudentRoomLimit;
        settingsArray[1] = oiStudentRoomLimit = defaultOIStudentRoomLimit;
        settingsArray[2] = isStudentRoomLimit = defaultISStudentRoomLimit;
        settingsArray[3] = daStudentRoomLimit = defaultDAStudentRoomLimit;
        settingsArray[4] = ooFinalStudentRoomLimit = defaultOOFinalStudentRoomLimit;
        settingsArray[5] = oiFinalStudentRoomLimit = defaultOIFinalStudentRoomLimit;
        settingsArray[6] = isFinalStudentRoomLimit = defaultISFinalStudentRoomLimit;
        settingsArray[7] = daFinalStudentRoomLimit = defaultDAFinalStudentRoomLimit;

        settingsArray[8] = ooStudentSchoolLimit = defaultOOStudentSchoolLimit;
        settingsArray[9] = oiStudentSchoolLimit = defaultOIStudentSchoolLimit;
        settingsArray[10] = isStudentSchoolLimit = defaultISStudentSchoolLimit;
        settingsArray[11] = daStudentSchoolLimit = defaultDAStudentSchoolLimit;
        settingsArray[12] = debateStudentSchoolLimit = defaultDebateStudentSchoolLimit;

        settingsArray[13] = ooJudgeRoomLimit = defaultOOJudgeRoomLimit;
        settingsArray[14] = oiJudgeRoomLimit = defaultOIJudgeRoomLimit;
        settingsArray[15] = isJudgeRoomLimit = defaultISJudgeRoomLimit;
        settingsArray[16] = daJudgeRoomLimit = defaultDAJudgeRoomLimit;
        settingsArray[17] = debateJudgeRoomLimit = defaultDebateJudgeRoomLimit;
        settingsArray[18] = finalsJudgeRoomLimit = defaultFinalsJudgeRoomLimit;

        settingsArray[19] = roomLimit1 = defaultRoomLimit1;
        settingsArray[20] = roomLimit2 = defaultRoomLimit2;
        settingsArray[21] = timeLimit1 = defaultTimeLimit1; // hours
        settingsArray[22] = timeLimit2 = defaultTimeLimit2; // hours
        settingsArray[23] = schoolNumber = defaultSchoolNumber;
        settingsArray[24] = teacherSchoolNumber = defaultTeacherSchoolNumber;
        settingsArray[25] = studentSchoolNumber = defaultStudentSchoolNumber;

        settingsArray[26] = allowCombinedEvents = defaultAllowCombinedEvents;
        
        settingsArray[27] = combinedEvent1 = defaultCombinedEvent1;
        settingsArray[28] = combinedEvent2 = defaultCombinedEvent2;

        return settingsArray;
    }

    /**
     * The getSettings() returns the values of the settings attributes.
     *
     * @return array of objects with the values of the settings
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-14-2012)
     * @since Nov-14-2012
     */
    public Object [] getSettings() {
        Object [] settingsArray = new Object [29];

        settingsArray[0] = ooStudentRoomLimit;
        settingsArray[1] = oiStudentRoomLimit;
        settingsArray[2] = isStudentRoomLimit;
        settingsArray[3] = daStudentRoomLimit;
        settingsArray[4] = ooFinalStudentRoomLimit;
        settingsArray[5] = oiFinalStudentRoomLimit;
        settingsArray[6] = isFinalStudentRoomLimit;
        settingsArray[7] = daFinalStudentRoomLimit;

        settingsArray[8] = ooStudentSchoolLimit;
        settingsArray[9] = oiStudentSchoolLimit;
        settingsArray[10] = isStudentSchoolLimit;
        settingsArray[11] = daStudentSchoolLimit;
        settingsArray[12] = debateStudentSchoolLimit;

        settingsArray[13] = ooJudgeRoomLimit;
        settingsArray[14] = oiJudgeRoomLimit;
        settingsArray[15] = isJudgeRoomLimit;
        settingsArray[16] = daJudgeRoomLimit;
        settingsArray[17] = debateJudgeRoomLimit;
        settingsArray[18] = finalsJudgeRoomLimit;

        settingsArray[19] = roomLimit1;
        settingsArray[20] = roomLimit2;
        settingsArray[21] = timeLimit1; // hours
        settingsArray[22] = timeLimit2; // hours
        settingsArray[23] = schoolNumber;
        settingsArray[24] = teacherSchoolNumber;
        settingsArray[25] = studentSchoolNumber;

        settingsArray[26] = allowCombinedEvents;

        settingsArray[27] = combinedEvent1;
        settingsArray[28] = combinedEvent2;

        return settingsArray;
    }

    /**
     * This method sets the school tree of the database to be a new
     * BinarySchoolTree. That means that the original contents of the tree are
     * erased.
     *
     * @author Zbyněk Stara
     */
    public void eraseSchoolTree() {
        schoolTree = new BinarySearchTree();
    }

    /**
     * The getSchoolArray returns the schoolArray in a sorted fashion.
     * <p>
     * The schoolTree is traversed in order and returned as an array of schools.
     *
     * @return array of schools reflecting the schoolTree
     *
     * @author Zbyněk Stara
     * @version 1.1 (Nov-6-1012)
     * @since Nov-6-2012
     */
    public School [] getSchoolArray() {
        Object [] objectArray = schoolTree.getDataArray();
        School [] schoolArray = new School [objectArray.length];

        for (int i = 0; i < objectArray.length; i++) {
            schoolArray[i] = (School) objectArray[i];
        }

        return schoolArray;
    }

    /**
     * This method returns the school from the database that has a given name.
     *
     * @param name the name of the school to search for
     * @return the school with the corresponding name
     * @throws IllegalArgumentException if the name given to this method is an
     * empty string
     * @throws NoSuchElementException if the name could not be found in the
     * database
     *
     * @author Zbyněk Stara
     */
    public School getSchool(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            int schoolIndex = this.searchSchool(name);
            School school = (School) this.getSchoolTreeNodeData(schoolIndex);
            return school;
        } else throw new IllegalArgumentException();
    }

    /**
     * The getSchoolTreeNodeData method returns the data for a given node of
     * the schoolTree.
     * <p>
     * A node of the schoolTree, identified by the index parameter, is examined
     * and the content of its data attribute is returned as object.
     *
     * @param index the node which will be examined
     * @return the data this node carries
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-3-2013)
     * @since Jan-3-2013
     */
    public Object getSchoolTreeNodeData(int index) {
        try {
            return schoolTree.getNodeData(index);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * The getSchoolTreeSize returns the number of schools in the schoolTree
     *
     * @return the size of the schoolTree, as an integer value
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-3-2013)
     * @since Jan-3-2013
     */
    public int getSchoolTreeSize() {
        return schoolTree.size();
    }

    /**
     * This insertSchool method adds a new school to the schoolTree.
     * <p>
     * The school is added with the name specified in the parameter.
     *
     * @param name the name of the school to be added
     * @throws IllegalArgumentException if there is already a school of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.6 (Jan-3-2012)
     * @since Nov-6-2012
     */
    public void insertSchool(String name) throws IllegalArgumentException {
        if (!name.equals("")) {
            School newSchool = new School(name);
            schoolTree.insert(newSchool, name);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * This insertSchool method adds a specified school to the schoolTree.
     *
     * @param school the school to be added
     * @throws IllegalArgumentException if there is already a school of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-3-2012)
     * @since Jan-3-2012
     */
    public void insertSchool(School school) throws IllegalArgumentException {
        if (!school.getName().equals("")) {
            schoolTree.insert(school, school.getName());
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The deleteSchool method removes a specific school from the schoolTree.
     * <p>
     * The school of the specified name is deleted, provided that it exists. It
     * is then returned by the function.
     *
     * @param name the name of the school to be deleted
     * @return the deleted school
     * @throws NoSuchElementException if there is no school of the specified
     * name
     *
     * @author Zbyněk Stara
     * @version 1.4 (Jan-3-2013)
     * @since Nov-6-2012
     */
    public School deleteSchool(String name) throws NoSuchElementException {
        return (School) schoolTree.delete(name);
    }

    /**
     * The deleteSchool method removes a specific school from the schoolTree.
     * <p>
     * The school at the specified index is removed from the schoolTree and is
     * returned by this function
     *
     * @param index the index of the school in the shoolTree
     * @return the deleted school
     * @throws IllegalArgumentException if the index is not within the bounds of
     * the schoolTree
     *
     * @author Zbyněk Stara
     * @version 1.2 (Jan-3-2013)
     * @since Nov-6-2012
     */
    public School deleteSchool(int index) throws IllegalArgumentException {
        if (index >= 0 && index < schoolTree.size()) {
            School deletedSchool = (School) schoolTree.getNodeData(index);
            String deleteKey = deletedSchool.getName();
            schoolTree.delete(deleteKey);
            return deletedSchool;
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The editSchool renames a specific school.
     * <p>
     * The school is first removed from the schoolTree, renamed, and then
     * re-inserted under a new name.
     *
     * @param index the school's index in the shoolTree
     * @param name new name for the school
     * @throws IllegalArgumentException if the name is already used in the
     * schoolTree or the new name is an empty string or the index is not within
     * the bounds of the schoolTree
     *
     * @author Zbyněk Stara
     * @version 1.1 (Nov-6-2012)
     * @since Nov-6-2012
     */
    public void editSchool(int index, String name) throws IllegalArgumentException {
        if (!schoolTree.contains(name) && !name.equals("") && index >= 0 && index < schoolTree.size()) {
            School editedSchool = (School) schoolTree.getNodeData(index);
            String editKey = editedSchool.getName();
            schoolTree.delete(editKey);
            editedSchool.setName(name);
            schoolTree.insert(editedSchool, name);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The searchSchool finds a specific school.
     * <p>
     * If the school exists, this method finds it and returns its index in the
     * schoolTree.
     *
     * @param name name of school to search for
     * @return school's index in the schoolTree
     * @throws IllegalArgumentException if the name is an empty string
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.1 (Nov-6-2012)
     * @since Nov-6-2012
     */
    public int searchSchool(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            Object [] schoolTreeArray = schoolTree.getDataArray();
            boolean schoolFound = false;
            int schoolIndex = -999;
            for (int i = 0; i < schoolTreeArray.length; i++) {
                if (((School) schoolTreeArray[i]).getName().equals(name)) {
                    schoolFound = true;
                    schoolIndex = i;
                    break;
                }
            }
            if (schoolFound) {
                return schoolIndex;
            }
            else throw new NoSuchElementException();
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The globalSearchTeacher finds a specific teacher.
     * <p>
     * If the teacher exists, this method finds him and returns an array of
     * indices to show where he is. The first element refers to the schoolTree
     * and the second element is the position in the teacherTree of that school
     * where the teacher is.
     * <p>
     * This method starts from the first entry in the schoolTree and goes
     * sequentially forward.
     *
     * @param name name of teacher to search for
     * @return array of indices of the found teacher (schoolTree, teacherTree)
     * @throws IllegalArgumentException if the name is an empty string
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.4 (Jan-4-2013)
     * @since Nov-6-2012
     */
    public int [] globalSearchTeacher(String name) throws IllegalArgumentException, NoSuchElementException {
        if (name.equals("")) throw new IllegalArgumentException();
        
        Object[] schoolTreeArray = schoolTree.getDataArray();

        for (int schoolIndex = 0; schoolIndex < schoolTreeArray.length; schoolIndex++) {
            try {
                School currentSchool = (School) schoolTreeArray[schoolIndex];
                
                int teacherIndex = currentSchool.searchTeacher(name);

                int [] teacherIndices = {schoolIndex, teacherIndex};
                return teacherIndices;
            } catch (IllegalArgumentException ex) { // the teacher name was an empty string
                throw new IllegalArgumentException();
            } catch (NoSuchElementException ex) { // teacher not found in this shool
                continue;
            }
        }

        throw new NoSuchElementException(); // if NoSuchElementExceptions encountered for all schools
    }

    /**
     * The globalSearchTeacher finds a specific teacher.
     * <p>
     * If the teacher exists, this method finds him and returns an array of
     * indices to show where he is. The first element refers to the schoolTree
     * and the second element is the position in the teacherTree of that school
     * where the teacher is.
     * <p>
     * This method starts with the specified entry in the schoolTree; if teacher
     * was not found in there, it goes from the first entry and sequentially
     * forward, skipping the specified entry in the process.
     *
     * @param name name of teacher to search for
     * @param preferredSchoolIndex index of the school with which to start the
     * search (value of -1 is accepted and treated as 0); in these two cases,
     * this method behaves exactly as the method without preferredSchoolIndex
     * parameter.
     * @return array of indices of the found teacher (schoolTree, teacherTree)
     * @throws IllegalArgumentException if the name is an empty string or the
     * index is not within the bounds of the schoolTree
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public int [] globalSearchTeacher(String name, int preferredSchoolIndex) throws IllegalArgumentException, NoSuchElementException {
        if (name.equals("")) throw new IllegalArgumentException();

        Object[] schoolTreeArray = schoolTree.getDataArray();

        if (preferredSchoolIndex == -1) {
            preferredSchoolIndex = 0;
        } else if (preferredSchoolIndex >= 0 && preferredSchoolIndex < schoolTreeArray.length) {
            // do nothing
        } else {
            throw new IllegalArgumentException();
        }

        try {
            School currentSchool = (School) schoolTreeArray[preferredSchoolIndex];

            int teacherIndex = currentSchool.searchTeacher(name);

            int[] teacherIndices = {preferredSchoolIndex, teacherIndex};
            return teacherIndices;
        } catch (IllegalArgumentException ex) { // the teacher name was an empty string
            throw new IllegalArgumentException();
        } catch (NoSuchElementException ex) { // teacher not found in this shool
            // do nothing, let it continue
        }

        for (int schoolIndex = 0; schoolIndex < schoolTreeArray.length; schoolIndex++) {
            if (schoolIndex == preferredSchoolIndex) continue;

            try {
                School currentSchool = (School) schoolTreeArray[schoolIndex];

                int teacherIndex = currentSchool.searchTeacher(name);

                int [] teacherIndices = {schoolIndex, teacherIndex};
                return teacherIndices;
            } catch (IllegalArgumentException ex) { // the teacher name was an empty string
                throw new IllegalArgumentException();
            } catch (NoSuchElementException ex) { // teacher not found in this shool
                continue;
            }
        }

        throw new NoSuchElementException(); // if NoSuchElementExceptions encountered for all schools
    }

    /**
     * The globalSearchStudent finds a specific student.
     * <p>
     * If the student exists, this method finds him and returns an array of
     * indices to show where he is. The first element refers to the schoolTree
     * and the second element is the position in the studentTree of that school
     * where the student is.
     * <p>
     * This method starts from the first entry in the schoolTree and goes
     * sequentially forward.
     *
     * @param name name of student to search for
     * @return array of indices of the found student (schoolTree, studentTree)
     * @throws IllegalArgumentException if the name is an empty string
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.3 (Jan-4-2013)
     * @since Nov-6-2012
     */
    public int [] globalSearchStudent(String name) throws IllegalArgumentException, NoSuchElementException {
        if (name.equals("")) throw new IllegalArgumentException();

        Object[] schoolTreeArray = schoolTree.getDataArray();

        for (int schoolIndex = 0; schoolIndex < schoolTreeArray.length; schoolIndex++) {
            try {
                School currentSchool = (School) schoolTreeArray[schoolIndex];

                int studentIndex = currentSchool.searchStudent(name); // only if the student was found, the following comes to happen:

                int [] studentIndices = {schoolIndex, studentIndex};
                return studentIndices;
            } catch (IllegalArgumentException ex) { // the student name was an empty string
                throw new IllegalArgumentException();
            } catch (NoSuchElementException ex) { // student not found in this shool
                continue;
            }
        }

        throw new NoSuchElementException(); // if NoSuchElementExceptions encountered for all schools
    }

    /**
     * The globalSearchStudent finds a specific student.
     * <p>
     * If the student exists, this method finds him and returns an array of
     * indices to show where he is. The first element refers to the schoolTree
     * and the second element is the position in the studentTree of that school
     * where the student is.
     * <p>
     * This method starts with the specified entry in the schoolTree; if student
     * was not found in there, it goes from the first entry and sequentially
     * forward, skipping the specified entry in the process.

     * @param name name of student to search for
     * @param preferredSchoolIndex index of the school with which to start the
     * search (value of -1 is accepted and treated as 0); in these two cases,
     * this method behaves exactly as the method without preferredSchoolIndex
     * parameter.
     * @return array of indices of the found student (schoolTree, studentTree)
     * @throws IllegalArgumentException if the name is an empty string or the
     * index is not within the bounds of the schoolTree
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public int [] globalSearchStudent(String name, int preferredSchoolIndex) throws IllegalArgumentException, NoSuchElementException {
        if (name.equals("")) throw new IllegalArgumentException();

        Object[] schoolTreeArray = schoolTree.getDataArray();

        try {
            School currentSchool = (School) schoolTreeArray[preferredSchoolIndex];

            int studentIndex = currentSchool.searchStudent(name);

            int[] studentIndices = {preferredSchoolIndex, studentIndex};
            return studentIndices;
        } catch (IllegalArgumentException ex) { // the student name was an empty string
            throw new IllegalArgumentException();
        } catch (NoSuchElementException ex) { // student not found in this shool
            // do nothing, let it continue
        }

        for (int schoolIndex = 0; schoolIndex < schoolTreeArray.length; schoolIndex++) {
            if (schoolIndex == preferredSchoolIndex) continue;

            try {
                School currentSchool = (School) schoolTreeArray[schoolIndex];

                int studentIndex = currentSchool.searchStudent(name);

                int [] studentIndices = {schoolIndex, studentIndex};
                return studentIndices;
            } catch (IllegalArgumentException ex) { // the student name was an empty string
                throw new IllegalArgumentException();
            } catch (NoSuchElementException ex) { // student not found in this shool
                continue;
            }
        }

        throw new NoSuchElementException(); // if NoSuchElementExceptions encountered for all schools
    }

    /**
     * The assignStudentCodes assigns student codes to students.
     * <p>
     * All students are assigned a code consisting of two parts: a letter, and
     * a number. The letter identifies the school the student comes from, while
     * the number uniquely identifies the student in the tournament.
     *
     * @param studentsNumber the number of students in the database
     *
     * @throws IllegalStateException if the schoolTree is empty.
     *
     * @author Zbyněk Stara
     * @version 1.3 (Jan-5-2013)
     * @since Nov-6-2012
     */
    public void assignStudentCodes(int studentsNumber) throws IllegalStateException {
        if (!schoolTree.isEmpty() && studentsNumber > 0) {
            char currentLetter = 'A';
            int currentNumber = 1;

            for (int i = 0; i < schoolTree.size(); i++) {
                String firstPart = currentLetter + "";

                School currentSchool = (School) schoolTree.getNodeData(i);

                currentSchool.setCodeLetter(currentLetter);

                currentSchool.setBeginCodeNumber(currentNumber);

                for (int j = 0; j < currentSchool.getStudentTreeSize(); j++) {
                    currentSchool.setEndCodeNumber(currentNumber);

                    String secondPart = currentNumber + "";

                    String currentCode = firstPart + secondPart;
                    Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(j);

                    currentStudent.setCode(currentCode);

                    currentNumber++;
                }

                currentLetter++;
            }
        }
        else throw new IllegalStateException();
    }

    /**
     * This method is called upon to ensure that pair codes are correct after
     * a re-assignment of student codes.
     *
     * @param currentSchool the school whose pairs are to be checked
     *
     * @author Zbyněk Stara
     */
    public void correctPairCodes(School currentSchool) {
        for (int i = 0; i < schoolTree.size(); i++) {
            for (int j = 0; j < currentSchool.getDAPairTreeSize(); j++) {
                StudentPair currentPair = (DAStudentPair) currentSchool.getDAPairTreeNodeData(j);
                if (!currentPair.getCode().equals(currentPair.getOriginalCode())) {
                    DAStudentPair incorrectPair = currentSchool.deleteDAPair(currentPair.getOriginalCode());
                    DAStudentPair correctPair = new DAStudentPair(incorrectPair.getStudentArray()[0], incorrectPair.getStudentArray()[1]);
                    currentSchool.insertDAPair(correctPair);
                }
            }

            for (int j = 0; j < currentSchool.getDebatePairTreeSize(); j++) {
                StudentPair currentPair = (DebateStudentPair) currentSchool.getDebatePairTreeNodeData(j);
                if (!currentPair.getCode().equals(currentPair.getOriginalCode())) {
                    DebateStudentPair incorrectPair = currentSchool.deleteDebatePair(currentPair.getOriginalCode());
                    DebateStudentPair correctPair = new DebateStudentPair(incorrectPair.getStudentArray()[0], incorrectPair.getStudentArray()[1]);
                    currentSchool.insertDebatePair(correctPair);
                }
            }
        }
    }

    /**
     * The searchStudentCode finds a specific student by his code.
     * <p>
     * If the student code exists, this method finds it and returns an array of
     * indices to show which student it refers to. The first element refers to
     * the schoolTree and the second element is the position in the studentTree
     * of that school where the student is.
     *
     * @param code student code to search for
     * @return array of indices of the found student (schoolTree, studentTree)
     * @throws IllegalArgumentException if the code is an empty string
     * @throws NoSuchElementException if the code letter is not a valid letter
     *
     * @author Zbyněk Stara
     * @version 1.5 (Jan-6-2013)
     * @since Nov-6-2012
     */
    public int [] searchStudentCode(String code) throws IllegalArgumentException, NoSuchElementException {
        if (!code.equals("")) {
            char firstPart = code.charAt(0);

            if (firstPart >= 'A' && firstPart <= 'Z') {
                // do nothing
            } else if (firstPart >= 'a' && firstPart <= 'z') {
                firstPart -= 32;
            } else throw new IllegalArgumentException();

            int schoolIndex = firstPart - 65;

            School currentSchool = (School) schoolTree.getNodeData(schoolIndex);

            if (currentSchool == null) throw new NoSuchElementException();

            int minCurrentSchoolCode = currentSchool.getBeginCodeNumber(); // do not forget that these start with 1, not 0 !!!
            int maxCurrentSchoolCode = currentSchool.getEndCodeNumber();

            String secondPart = code.substring(1);

            int codeNumber;

            int studentCodeIndex;

            try {
                codeNumber = Integer.parseInt(secondPart);

                if ((maxCurrentSchoolCode - minCurrentSchoolCode) >= (codeNumber - minCurrentSchoolCode) && (codeNumber - minCurrentSchoolCode) >= 0) {
                    studentCodeIndex = codeNumber - minCurrentSchoolCode; // this produces numbers in the 0-based format
                } else {
                    studentCodeIndex = -1;
                }
            } catch (NumberFormatException ex) {
                studentCodeIndex = -1;
            }
            
            int[] studentCodeIndicesArray = {schoolIndex, studentCodeIndex};

            return studentCodeIndicesArray;
        }
        else throw new IllegalArgumentException();
    }
}
