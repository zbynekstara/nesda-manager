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
 * This class represents the student participants in the debate tournaments. It
 * has all relevant attributes and methods to work on them.
 *
 * @author Zbyněk Stara
 */
public class Student {
    // Attributes:
    private String code = "<No code>";
    private String name = "<No name>";

    private School school = null;

    private boolean [] events = {false, false, false, false, false};

    private boolean daUnpaired = false;
    private boolean debateUnpaired = false;

    private DAStudentPair daStudentPair = null; // with whom is the student in pair
    private DebateStudentPair debateStudentPair = null;

    private String daStudentPairTempString = "";
    private String debateStudentPairTempString = "";

    private boolean reassignmentText = false; // whether the assignEvents button should read "Re-assign events"

    // Constructors:
    public Student() {

    }
    public Student(School school) {
        this.school = school;
    }
    public Student(String name, School school) {
        this.name = name;
        this.school = school;
    }
    public Student(String name, School school, String code, boolean [] events) {
        this.name = name;
        this.school = school;
        this.code = code;

        this.events[0] = events[0];
        this.events[1] = events[1];
        this.events[2] = events[2];
        this.events[3] = events[3];
        this.events[4] = events[4];
    }
    public Student(String name, School school, String code, boolean oo, boolean oi, boolean is, boolean da, boolean debate) {
        this.name = name;
        this.school = school;
        this.code = code;

        events[0] = oo;
        events[1] = oi;
        events[2] = is;
        events[3] = da;
        events[4] = debate;
    }
    public Student(String name, School school, String code, boolean [] events, boolean daUnpaired, boolean debateUnpaired,
            String daStudentPairTempString, String debateStudentPairTempString, boolean reassignmentText) {
        this.name = name;
        this.school = school;
        this.code = code;

        this.events[0] = events[0];
        this.events[1] = events[1];
        this.events[2] = events[2];
        this.events[3] = events[3];
        this.events[4] = events[4];

        this.daUnpaired = daUnpaired;
        this.debateUnpaired = debateUnpaired;
        this.daStudentPairTempString = daStudentPairTempString;
        this.debateStudentPairTempString = debateStudentPairTempString;
        this.reassignmentText = reassignmentText;
    }

    // Extra methods:
    /**
     * This method takes the entries in the da/debateStudentPairTempString and
     * uses them to determine the partners of this student for the respective
     * events (if the student participates in them).
     *
     * @throws IllegalStateException if an entry in one of the tempStrings
     * does not correspond to any student with which to pair this student.
     *
     * @author Zbyněk Stara
     */
    public void updateStudentPairs() throws IllegalStateException {
        try {
            String daStudentPairCode = daStudentPairTempString;
            daStudentPair = school.getDAPair(daStudentPairCode);
        } catch (IllegalArgumentException ex) {
            // the string is "" - nothing will be done
        } catch (NoSuchElementException ex) {
            throw new IllegalStateException();
        }

        try {
            String debateStudentPairCode = debateStudentPairTempString;
            debateStudentPair = school.getDebatePair(debateStudentPairCode);
        } catch (IllegalArgumentException ex) {
            // the string is "" - nothing will be done
        } catch (NoSuchElementException ex) {
            throw new IllegalStateException();
        }
    }

    // Get methods:
    /**
     * This method allows for an access to the name of the student.
     *
     * @return name of the student (string)
     *
     * @author Zbyněk Stara
     */
    public String getName() {
        return name;
    }

    /**
     * This method allows for an access to the school of the student.
     *
     * @return school this student belongs to
     *
     * @author Zbyněk Stara
     */
    public School getSchool() {
        return school;
    }

    /**
     * This method allows for an access to the code of the student.
     *
     * @return code of this student (string)
     *
     * @author Zbyněk Stara
     */
    public String getCode() {
        return code;
    }

    /**
     * This method allows for an access to events of this student.
     *
     * @return a boolean array of five elements, each of which indicates whether
     * the student participates in that event (0: oo, 1: oi, 2: is, 3: da,
     * 4: debate) – if the value at a given element is true, the student
     * participates in the element
     *
     * @author Zbyněk Stara
     */
    public boolean [] getEvents() {
        boolean [] returnArray = new boolean[5];
        
        returnArray[0] = events[0] ? true : false;
        returnArray[1] = events[1] ? true : false;
        returnArray[2] = events[2] ? true : false;
        returnArray[3] = events[3] ? true : false;
        returnArray[4] = events[4] ? true : false;

        return returnArray;
    }

    /**
     * This method allows for an access to the daUnpaired attribute of the
     * student.
     *
     * @return a boolean value reflecting the value of the daUnpaired attribute.
     *
     * @author Zbyněk Stara
     */
    public boolean getDAUnpaired() {
        return daUnpaired;
    }

    /**
     * This method allows for an access to the debateUnpaired attribute of the
     * student.
     *
     * @return a boolean value reflecting the value of the debateUnpaired
     * attribute.
     *
     * @author Zbyněk Stara
     */
    public boolean getDebateUnpaired() {
        return debateUnpaired;
    }

    /**
     * This method allows for an access to the daStudentPair of which the
     * student is a part.
     *
     * @return the daStudentPair attribute
     *
     * @author Zbyněk Stara
     */
    public DAStudentPair getDAStudentPair() {
        return daStudentPair;
    }

    /**
     * This method allows for an access to the debateStudentPair of which the
     * student is a part.
     *
     * @return the debateStudentPair attribute
     *
     * @author Zbyněk Stara
     */
    public DebateStudentPair getDebateStudentPair() {
        return debateStudentPair;
    }

    /**
     * This method allows for an access to the reassignmentText attribute.
     *
     * @return the reassignmentText attribute
     *
     * @author Zbyněk Stara
     */
    public boolean getReassignmentText() {
        return reassignmentText;
    }

    // Set methods:
    /**
     * This method allows for changing the name of the student
     *
     * @param name the new name of the student
     *
     * @author Zbyněk Stara
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method allows for changing the school of the student
     *
     * @param school the new school of the student
     *
     * @author Zbyněk Stara
     */
    public void setSchool(School school) {
        this.school = school;
    }

    /**
     * This method allows for changing the code of the student
     *
     * @param code the new code of the student
     *
     * @author Zbyněk Stara
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * This method allows setting the student's events.
     *
     * @param eventsArray a boolean array of length 5 - each element represents
     * the student's participation in an event (0: oo, 1: oi, 2: is, 3: da,
     * 4: debate)
     * @throws IllegalArgumentException if the supplied eventsArray does not
     * have 5 elements
     *
     * @author Zbyněk Stara
     */
    public void setEvents(boolean[] eventsArray) throws IllegalArgumentException {
        if (eventsArray.length ==  5) {
            events[0] = eventsArray[0];
            events[1] = eventsArray[1];
            events[2] = eventsArray[2];
            events[3] = eventsArray[3];
            events[4] = eventsArray[4];
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method allows setting the student's events.
     *
     * @param oo whether the student participates in original oratory
     * @param oi whether the student participates in oral interpretation
     * @param is whether the student participates in impromptu speaking
     * @param da whether the student participates in duet acting
     * @param debate whether the student participates in debate
     *
     * @author Zbyněk Stara
     */
    public void setEvents(boolean oo, boolean oi, boolean is, boolean da, boolean debate) {
        events[0] = oo;
        events[1] = oi;
        events[2] = is;
        events[3] = da;
        events[4] = debate;
    }

    /**
     * This method allows for setting the daUnpaired attribute of the student.
     *
     * @param daUnpaired the new boolean value of the attribute
     *
     * @author Zbyněk Stara
     */
    public void setDAUnpaired(boolean daUnpaired) {
        this.daUnpaired = daUnpaired;
    }

    /**
     * This method allows for setting the debateUnpaired attribute of the
     * student.
     *
     * @param debateUnpaired the new boolean value of the attribute
     *
     * @author Zbyněk Stara
     */
    public void setDebateUnpaired(boolean debateUnpaired) {
        this.debateUnpaired = debateUnpaired;
    }

    /**
     * This method allows for setting the student's DA student pair.
     *
     * @param daStudentPair the student's new DA student pair
     *
     * @author Zbyněk Stara
     */
    public void setDAStudentPair(DAStudentPair daStudentPair) {
        this.daStudentPair = daStudentPair;
    }

    /**
     * This method allows for setting the student's debate student pair.
     *
     * @param debateStudentPair the student's new debate student pair
     *
     * @author Zbyněk Stara
     */
    public void setDebateStudentPair(DebateStudentPair debateStudentPair) {
        this.debateStudentPair = debateStudentPair;
    }

    /**
     * This method allows for setting the student's reassignmentText attribute.
     *
     * @param reassignmentText the new boolean value of the attribute
     *
     * @author Zbyněk Stara
     */
    public void setReassignmentText(boolean reassignmentText) {
        this.reassignmentText = reassignmentText;
    }

    /**
     * This method represents the student with a string
     *
     * @return a string with the student's code and name
     *
     * @author Zbyněk Stara
     */
    @Override
    public String toString() {
        return "Name: " + name + ", Code: " + code;
    }
}
