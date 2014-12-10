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

/**
 * Purpose:
 * The purpose of this program is to help the organizers of the NESDA Speech and
 * Debate tournaments with allocation of participants and judges to different
 * events, rounds and room. The allocation algorithm uses a user-filled database
 * of participating schools, teachers and students.
 */

// ACCOMPLISHED The GUI front end is done
// ACCOMPLISHED Code for several GUI buttons
// ACCOMPLISHED Basic functions for school
// ACCOMPLISHED Database functions almost completely written
// ACCOMPLISHED Database functions annotated with javadoc
// ACCOMPLISHED Resetting and approving of settings
// ACCOMPLISHED Information dialogs
// ACCOMPLISHED Wrap up the settingsApproveButton code
// ACCOMPLISHED JFileChooserDialog for saving
// ACCOMPLISHED Exception dialog
// ACCOMPLIAHED Saving of settings
// ACCOMPLISHED Loading of settinds
// ACCOMPLISHED Tree-balancing algorithm to be used before saving of trees

// CHECK-IN Nov 27, 2012:
// ACCOMPLISHED getIndex method for trees for selecting searched/added/edited elements in lists
// ACCOMPLISHED participants tab: schools buttons work completely

// CHECK-IN Dec 5, 2012:
// ACCOMPLISHED getIndex and insert methods for teachers and students
// ACCOMPLISHED teachersAddButton code adapted from schoolsAddButton's code

// ACCOMPLISHED Change school's teacher and student methods to correspond with database's (Jan-4-2013)
// ACCOMPLISHED Change database globalSearchTeacher methods to use school's searchTeacher
// ACCOMPLISHED Do the same for globalSearchStudent
// ACCOMPLISHED Global searching works for teachers and students
// ACCOMPLISHED StudentCode elements respond properly to other element changes
// ACCOMPLISHED StudentCode is entirely functional
// ACCOMPLISHED Clean up the MainGUI code (move Database-specific tasks and fix errors)
// ACCOMPLISHED Show which events are selected for which student

// ACCOMPLISHED Assign events button is only available when events were changed (Feb-14-2013)

// ACCOMPLISHED All buttons from participants tab now respond, except the technical ones (FE-25-2013)
// ACCOMPLISHED Make pairs section respond to removal of students
// ACCOMPLISHED Make Pairs respond to the changing of events of paired students

// ACCOMPLISHED Working with the participants database is now bug-free

// ACCOMPLISHED Allocation algorithm is done
// ACCOMPLISHED Saving, loading and exporting works

// TODO List updates in Qualification

// OLD:
// TODO Move database variables (studentsNumber etc.) from MainGUI
// TODO Edit assigning and searching student codes in Database
// TODO Confirmation dialog when removing a school with teachers and/or students
// TODO Move on to School, Student, and Teacher functions
// TODO Make a robust system for assigning noName numbers, their reuse, and saving - using dynamic queues?
// TODO Make a robust system for student code assignment - keep track of what is added, and if it seubsequently removed, do not require re-assignment
// TODO Rework the tres' handling of indices (now, they have to be converted to arrays to find them; what's the point of having trees, then?)
//          use link-list approach for nodes to link them by indices
//          re-work the getIndex method to work with this: otherwise, its work could be done with the search function
//          re-write the search index not to rely on the arrays to make use of the full use of the trees' search potential
// TODO Check the mainGUI methods for duplicacies in enabling/disabling codes and maybe create methods to group things that always go together

package historical;

import data.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * MainGUI is the main class of the program. The class controls the graphical
 * user interface. It contains listeners to the events triggered by user's
 * interaction with the program.
 *
 * @author Zbyněk Stara
 * @since Jan-28-2012
 */
public class MainGUI extends javax.swing.JFrame {
    // CUSTOM EXCEPTIONS:
    /**
     * The UserIOException is sent when an input/output operation fails because
     * the process is canceled by the user. It extends IOException.
     */
    private class UserIOException extends IOException {
        private void UserIOException() {
            throw new RuntimeException();
        }
    }

    /**
     * The FileIOException is sent when an input/output operation fails because
     * the file selected by the user is invalid. It extends IOException.
     */
    private class FileIOException extends IOException {

        private void FileIOException() {
            throw new RuntimeException();
        }
    }

    // ATTRIBUTES:
    private Database database = new Database(); // contains data of schools, teachers and students to be used by the program, and methods to manipulate the data
    private Qualification qualification; // contains data of events, rounds and rooms, which hold the allocated entities and judges, and methods to manipulate them

    // LIST MODELS:
    private DefaultListModel blankModel = new DefaultListModel(); // a blank list model
    private DefaultListModel emptyModel = new DefaultListModel(); // a list model reading <No elements>, used to show that the dispplayed list is empty
    private DefaultListModel noSelectionModel = new DefaultListModel(); // a list model reading <No selection>, used to show that the list requires a selection in other list higher in data hierarchy
    private DefaultListModel eventsListModel = new DefaultListModel(); // a list model featuring the events for participantsEventsList (Duet Acting and Debate)

    // SAVING/LOADING/RESETTING VARIABLES:
    private File loadFile = null; // the last used load file (used for resetting)
    private int approvedSectionsIndex = 0; // the last section to be saved/loaded

    // NO NAME VARIABLES:
    private int currentNewNoNameSchoolNumber = 1; // number of the new no name school
    private int currentNewNoNameTeacherNumber = 1; // number of the new no name teacher
    private int currentNewNoNameStudentNumber = 1; // number of the new no name student

    // NUMBERS OF DATABASE ENTRIES:
    private int schoolsNumber = 0; // current number of schools in the database
    private int teachersNumber = 0; // current number of teachers in the database
    private int studentsNumber = 0; // current number of students in the database

    // GUI VARIABLES:
    private boolean reassignmentText = false; // indicates whether the text on the assign codes button should read "Re-assign codes"
    private boolean studentCodes = false; // indicates whether there is a student code assignment effective at the current moment
    private boolean studentChanges = false; // indicates whether a new student codes assignment is needed (schools/teachers/students added, removed, or edited)

    // PARTICIPANTS LIST SELECTION VARIABLES:
    private int schoolsIndex = -1; // index of the school currently selected in the schoolsList
    private int teachersIndex = -1; // index of the teacher currently selected in the teachersList
    private int studentsIndex = -1; // index of the student currently selected in the studentsList
    private int studentCodesIndex = -1; // index of the student code currently selected in the studentCodesList

    // ASSIGN PAIRS LIST SELECTION VARAIBLES:
    private int assignPairsEventsIndex = -1; // index of the event currently selected in the assignPairsEventsList
    private int assignPairsLeftIndex = -1; // index of the student currently selected in the assignPairsLeftList
    private int assignPairsRightIndex = -1; // index of the student currently selected in the assignPairsRightList
    private int assignPairsPairsIndex = -1; // index of the student pair currently selected in the assignPairsPairsList

    // QUALIFICATION LIST SELECTION VARIABLES:
    private int eventsIndex = -1; // index of the event currently selected in the eventsList
    private int roundsIndex = -1; // index of the round currently selected in the roundsList
    private int roomsIndex = -1; // index of the room currently selected in the roomList
    private int judgesIndex = -1; // index of the judge currently selected in the judgesList
    private int qStudentCodesIndex = -1; // index of the entity currently selected in the (qualification) studentCodesList

    // INITIALIZATION:
    public MainGUI() {
        initComponents();
        myInitComponents();
    }

    private void myInitComponents() {
        emptyModel.addElement("<No elements>");
        noSelectionModel.addElement("<No selection>");

        eventsListModel.addElement("Duet Acting");
        eventsListModel.addElement("Debate");

        // PARTICIPANTS TAB INITIALIZATION
        participantsSchoolsList.setModel(emptyModel);

        participantsTeachersList.setModel(noSelectionModel);
        participantsStudentsList.setModel(noSelectionModel);
        participantsStudentCodesList.setModel(noSelectionModel);
    }

    // SETTINGS METHODS:
    /**
     * The getTextFieldValue method returns the value of a selected settings
     * text field.
     * <p>
     * The method attempts to parse the text contents of a text field as an
     * integer. If it succeeds, it returns that integer. If the text contents
     * cannot be parsed as an int, the value of -1 is returned instead.
     *
     * @param textField the textField whose value we are taking in
     *
     * @return an int with the numerical value of the specified settings text
     * field
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-18-2012)
     * @since Nov-18-2012
     */
    private int getTextFieldValue(javax.swing.JTextField textField) {
        String tempText = textField.getText();
        int value;
        try {
            value = Integer.parseInt(tempText);
        } catch (NumberFormatException ex) {
            value = -1;
        }
        return value;
    }

    /**
     * The changeDatabaseSettings method replaces the values of database
     * settings variables with the values of settings text fields.
     * <p>
     * The values of the settings variables are replaced by the values returned
     * by the getTextFieldValue function for each of the settings text fields.
     * This means that if there is an invalid value entered into a text field,
     * it is propagated into the database variables as -1. The values of the
     * settings variables are set by a call to Database's setSettings method.
     *
     * @param database a Database object
     *
     * @throws IllegalArgumentException if the array internally constructed to
     * hold the values of text fields does not have appropriate length for as
     * required by the Database.setSettings method.
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-18-2012)
     * @since Nov-18-2012
     *
     * @see data.Database.setSettings()
     */
    private void changeDatabaseSettings(Database database) throws IllegalArgumentException {
        Object[] valueArray = new Object[29];

        valueArray[0] = getTextFieldValue(eventSettingsOOSR);
        valueArray[1] = getTextFieldValue(eventSettingsOISR);
        valueArray[2] = getTextFieldValue(eventSettingsISSR);
        valueArray[3] = getTextFieldValue(eventSettingsDASR);
        valueArray[4] = getTextFieldValue(eventSettingsOOFinalSR);
        valueArray[5] = getTextFieldValue(eventSettingsOIFinalSR);
        valueArray[6] = getTextFieldValue(eventSettingsISFinalSR);
        valueArray[7] = getTextFieldValue(eventSettingsDAFinalSR);

        valueArray[8] = getTextFieldValue(eventSettingsOOSES);
        valueArray[9] = getTextFieldValue(eventSettingsOISES);
        valueArray[10] = getTextFieldValue(eventSettingsISSES);
        valueArray[11] = getTextFieldValue(eventSettingsDASES);
        valueArray[12] = getTextFieldValue(eventSettingsDebateSES);

        valueArray[13] = getTextFieldValue(eventSettingsOOJR);
        valueArray[14] = getTextFieldValue(eventSettingsOIJR);
        valueArray[15] = getTextFieldValue(eventSettingsISJR);
        valueArray[16] = getTextFieldValue(eventSettingsDAJR);
        valueArray[17] = getTextFieldValue(eventSettingsDebateJR);
        valueArray[18] = getTextFieldValue(eventSettingsFinalsJR);

        valueArray[19] = getTextFieldValue(generalSettingsRoomsAvailable1);
        valueArray[20] = getTextFieldValue(generalSettingsRoomsAvailable2);
        valueArray[21] = getTextFieldValue(generalSettingsMaximalTime1);
        valueArray[22] = getTextFieldValue(generalSettingsMaximalTime2);
        valueArray[23] = getTextFieldValue(generalSettingsSchoolsNumber);
        valueArray[24] = getTextFieldValue(generalSettingsTS);
        valueArray[25] = getTextFieldValue(generalSettingsSS);

        valueArray[26] = generalSettingsCECheckBox.isSelected();

        valueArray[27] = generalSettingsCEComboBox1.getSelectedIndex();
        valueArray[28] = generalSettingsCEComboBox2.getSelectedIndex();

        try {
            database.setSettings(valueArray);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * The resetDatabaseSettings method resets the values of the database's
     * settings variables to their default values.
     * <p>
     * This method calls the Database.resetSettings method.
     *
     * @param database a Database object
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-18-2012)
     * @since Nov-18-2012
     *
     * @see data.Database.resetSettings()
     */
    private void resetDatabaseSettings(Database database) {
        database.resetSettings();
    }

    /**
     * The approveDatabaseSettings method checks the correctness of database
     * settings values
     * <p>
     * This method utilizes a boolean array from Database.checkSettings method
     * to output a textual evaluation of the correctness of the value of a
     * specific setting.
     *
     * @param database a Database object
     *
     * @return String with the settings which have illegal values; may be empty
     * if all values were correct
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-18-2012)
     * @since Nov-18-2012
     *
     * @see data.Database.checkSettings()
     */
    private String approveDatabaseSettings(Database database) {
        boolean[] checkArray = database.checkSettings();

        String errorString = "";
        String returnString;

        errorString += checkArray[0] ? "" : "Illegal value: OO students/room limit\n"; // if the variable has a legal value, return true, else return false
        errorString += checkArray[1] ? "" : "Illegal value: OI students/room limit\n";
        errorString += checkArray[2] ? "" : "Illegal value: IS students/room limit\n";
        errorString += checkArray[3] ? "" : "Illegal value: DA students/room limit\n";
        errorString += checkArray[4] ? "" : "Illegal value: OO finals students/room limit\n";
        errorString += checkArray[5] ? "" : "Illegal value: OI finals students/room limit\n";
        errorString += checkArray[6] ? "" : "Illegal value: IS finals students/room limit\n";
        errorString += checkArray[7] ? "" : "Illegal value: DA finals students/room limit\n";

        errorString += checkArray[8] ? "" : "Illegal value: OO students/event/school limit\n";
        errorString += checkArray[9] ? "" : "Illegal value: OI students/event/school limit\n";
        errorString += checkArray[10] ? "" : "Illegal value: IS students/event/school limit\n";
        errorString += checkArray[11] ? "" : "Illegal value: DA students/event/school limit\n";
        errorString += checkArray[12] ? "" : "Illegal value: Debate student/event/school limit\n";

        errorString += checkArray[13] ? "" : "Illegal value: OO judges/room limit\n";
        errorString += checkArray[14] ? "" : "Illegal value: OI judges/room limit\n";
        errorString += checkArray[15] ? "" : "Illegal value: IS judges/room limit\n";
        errorString += checkArray[16] ? "" : "Illegal value: DA judges/room limit\n";
        errorString += checkArray[17] ? "" : "Illegal value: Debate judges/room limit\n";
        errorString += checkArray[18] ? "" : "Illegal value: Finals judges/room limit\n";

        errorString += checkArray[19] ? "" : "Illegal value: Rooms available day 1\n";
        errorString += checkArray[20] ? "" : "Illegal value: Rooms available day 2\n";
        errorString += checkArray[21] ? "" : "Illegal value: Tournament time day 1\n";
        errorString += checkArray[22] ? "" : "Illegal value: Tournament time day 2\n";
        errorString += checkArray[23] ? "" : "Illegal value: Schools in tournament\n";
        errorString += checkArray[24] ? "" : "Illegal value: Teachers/school\n";
        errorString += checkArray[25] ? "" : "Illegal value: Students/school\n";

        // checkArray[26] can only be true

        errorString += checkArray[27] ? "" : "Illegal value: Combined events 1\n";
        errorString += checkArray[28] ? "" : "Illegal value: Combined events 2\n";

        if (generalSettingsCEComboBox1.getSelectedIndex() == generalSettingsCEComboBox2.getSelectedIndex()) {
            errorString += "Illegal input: Combined events combo boxes both report the same event type\n";
        }

        if (!errorString.equals("")) {
            returnString = errorString.substring(0, (errorString.length() - 1));
        } else {
            returnString = errorString;
        }

        return returnString;
    }

    /**
     * The synchronizeTFSettings method correlates the text field values in the
     * program with database variable values.
     * <p>
     * This method takes the values of the database settings variables and puts
     * them into the appropriate settings text fields. In a sense, then, this
     * method is the opposite of the changeDatabaseSettings method, which takes
     * the text-field values and translates them into database variable values.
     *
     * @param database a Database object
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-18-2012)
     * @since Nov-18-2012
     *
     * @see gui.MainGUI.changeDatabaseSettings
     */
    private void synchronizeTFSettings(Database database) {
        Object[] settingsArray = database.getSettings();

        eventSettingsOOSR.setText((Integer) settingsArray[0] + "");
        eventSettingsOISR.setText((Integer) settingsArray[1] + "");
        eventSettingsISSR.setText((Integer) settingsArray[2] + "");
        eventSettingsDASR.setText((Integer) settingsArray[3] + "");
        eventSettingsOOFinalSR.setText((Integer) settingsArray[4] + "");
        eventSettingsOIFinalSR.setText((Integer) settingsArray[5] + "");
        eventSettingsISFinalSR.setText((Integer) settingsArray[6] + "");
        eventSettingsDAFinalSR.setText((Integer) settingsArray[7] + "");

        eventSettingsOOSES.setText((Integer) settingsArray[8] + "");
        eventSettingsOISES.setText((Integer) settingsArray[9] + "");
        eventSettingsISSES.setText((Integer) settingsArray[10] + "");
        eventSettingsDASES.setText((Integer) settingsArray[11] + "");
        eventSettingsDebateSES.setText((Integer) settingsArray[12] + "");

        eventSettingsOOJR.setText((Integer) settingsArray[13] + "");
        eventSettingsOIJR.setText((Integer) settingsArray[14] + "");
        eventSettingsISJR.setText((Integer) settingsArray[15] + "");
        eventSettingsDAJR.setText((Integer) settingsArray[16] + "");
        eventSettingsDebateJR.setText((Integer) settingsArray[17] + "");
        eventSettingsFinalsJR.setText((Integer) settingsArray[18] + "");

        generalSettingsRoomsAvailable1.setText((Integer) settingsArray[19] + "");
        generalSettingsRoomsAvailable2.setText((Integer) settingsArray[20] + "");
        generalSettingsMaximalTime1.setText((Integer) settingsArray[21] + "");
        generalSettingsMaximalTime2.setText((Integer) settingsArray[22] + "");
        generalSettingsSchoolsNumber.setText((Integer) settingsArray[23] + "");
        generalSettingsTS.setText((Integer) settingsArray[24] + "");
        generalSettingsSS.setText((Integer) settingsArray[25] + "");

        generalSettingsCECheckBox.setSelected((Boolean) settingsArray[26]);

        generalSettingsCEComboBox1.setSelectedIndex((Integer) settingsArray[27]);
        generalSettingsCEComboBox2.setSelectedIndex((Integer) settingsArray[28]);
    }

    // PARTICIPANTS METHODS:
    /**
     * The approveParticipants method checks the database and reports to the
     * user any unexpected values.
     *
     * @param database the current database
     * @return a string report with all the warnings to the user
     *
     * @author Zbyněk Stara
     */
    private String approveParticipants(Database database) { // initializes qualification
        qualification = new Qualification(database);

        Object[] settingsArray = database.getSettings();
        /*settingsArray[0] = ooStudentRoomLimit;
        settingsArray[1] = oiStudentRoomLimit;
        settingsArray[2] = isStudentRoomLimit;
        settingsArray[3] = daStudentRoomLimit;
        settingsArray[4] = ooFinalStudentRoomLimit;
        settingsArray[5] = oiFinalStudentRoomLimit;
        settingsArray[6] = isFinalStudentRoomLimit;
        settingsArray[7] = daFinalStudentRoomLimit;

        settingsArray[8] = ooStudentSchoolLimit; // THIS
        settingsArray[9] = oiStudentSchoolLimit; // THIS
        settingsArray[10] = isStudentSchoolLimit; // THIS
        settingsArray[11] = daStudentSchoolLimit; // THIS
        settingsArray[12] = debateStudentSchoolLimit; // THIS

        settingsArray[13] = ooJudgeRoomLimit;
        settingsArray[14] = oiJudgeRoomLimit;
        settingsArray[15] = isJudgeRoomLimit;
        settingsArray[16] = daJudgeRoomLimit;
        settingsArray[17] = debateJudgeRoomLimit;
        settingsArray[18] = finalsJudgeRoomLimit;

        settingsArray[19] = roomLimit1;
        settingsArray[20] = roomLimit2;
        settingsArray[21] = timeLimit1;
        settingsArray[22] = timeLimit2;
        settingsArray[23] = schoolNumber; // THIS
        settingsArray[24] = teacherSchoolNumber; // THIS
        settingsArray[25] = studentSchoolNumber; // THIS

        settingsArray[26] = allowCombinedEvents;

        settingsArray[27] = combinedEvent1;
        settingsArray[28] = combinedEvent2;*/

        String returnString = "";
        
        if (database.getSchoolTreeSize() < (Integer) settingsArray[23]) {
            returnString += "Fewer schools than expected.\n";
        } else if (database.getSchoolTreeSize() > (Integer) settingsArray[23]) {
            returnString += "More schools than expected.\n";
        }

        

        for (int i = 0; i < database.getSchoolTreeSize(); i++) {
            School currentSchool = (School) database.getSchoolTreeNodeData(i);

            returnString += this.approveSchool(database, currentSchool);
        }

        // additional judges calculations
        String judgesIntroString = "Not enough judges:\n";
        Boolean judgesIntroShown = false;

        int judgesTotal = qualification.getJudgeTreeSize();

        int ooEntitiesTotal = qualification.getOOEntityTreeSize();
        int ooRoomTotal = (int) Math.ceil(((double) ooEntitiesTotal) / ((double) ((Integer) settingsArray[0])));
        int ooJudgesRequired = ooRoomTotal * ((Integer) settingsArray[13]);

        int oiEntitiesTotal = qualification.getOIEntityTreeSize();
        int oiRoomTotal = (int) Math.ceil(((double) oiEntitiesTotal) / ((double) ((Integer) settingsArray[1])));
        int oiJudgesRequired = oiRoomTotal * ((Integer) settingsArray[14]);

        int isEntitiesTotal = qualification.getISEntityTreeSize();
        int isRoomTotal = (int) Math.ceil(((double) isEntitiesTotal) / ((double) ((Integer) settingsArray[2])));
        int isJudgesRequired = isRoomTotal * ((Integer) settingsArray[15]);

        int daEntitiesTotal = qualification.getDAEntityTreeSize();
        int daRoomTotal = (int) Math.ceil(((double) daEntitiesTotal) / ((double) ((Integer) settingsArray[3])));
        int daJudgesRequired = daRoomTotal * ((Integer) settingsArray[16]);

        int debateEntitiesTotal = qualification.getDebateEntityTreeSize();
        int debateRoomTotal = (int) Math.ceil(((double) debateEntitiesTotal) / ((double) ((Integer) settingsArray[4])));
        int debateJudgesRequired = debateRoomTotal * ((Integer) settingsArray[17]);

        if (ooJudgesRequired > judgesTotal) {
            if (!judgesIntroShown) {
                returnString += judgesIntroString;
                judgesIntroShown = true;
            }
            int ooDifference = ooJudgesRequired - judgesTotal;
            returnString += ooDifference + " more teachers needed to judge OO.\n";
        }

        if (oiJudgesRequired > judgesTotal) {
            if (!judgesIntroShown) {
                returnString += judgesIntroString;
                judgesIntroShown = true;
            }
            int oiDifference = oiJudgesRequired - judgesTotal;
            returnString += oiDifference + " more teachers needed to judge OI.\n";
        }

        if ((isJudgesRequired + daJudgesRequired) > judgesTotal) {
            if (!judgesIntroShown) {
                returnString += judgesIntroString;
                judgesIntroShown = true;
            }
            int isdaDifference = (isJudgesRequired + daJudgesRequired) - judgesTotal;
            returnString += isdaDifference + " more teachers needed to judge combined IS/DA.\n";
        }

        if (debateJudgesRequired > judgesTotal) {
            if (!judgesIntroShown) {
                returnString += judgesIntroString;
                judgesIntroShown = true;
            }
            int debateDifference = debateJudgesRequired - judgesTotal;
            returnString += debateDifference + " more teachers needed to judge Debate.\n";
        }
        
        return returnString;
    }

    /**
     * The approveSchool method is used by approveParticipants to check and
     * report warnings about a given school.
     *
     * @param database the current database
     * @param school the school to be checked
     * @return a string with the warnings about the given school
     *
     * @author Zbyněk Stara
     */
    private String approveSchool(Database database, School school) {
        Object[] settingsArray = database.getSettings();
        /*settingsArray[0] = ooStudentRoomLimit;
        settingsArray[1] = oiStudentRoomLimit;
        settingsArray[2] = isStudentRoomLimit;
        settingsArray[3] = daStudentRoomLimit;
        settingsArray[4] = ooFinalStudentRoomLimit;
        settingsArray[5] = oiFinalStudentRoomLimit;
        settingsArray[6] = isFinalStudentRoomLimit;
        settingsArray[7] = daFinalStudentRoomLimit;

        settingsArray[8] = ooStudentSchoolLimit; // THIS
        settingsArray[9] = oiStudentSchoolLimit; // THIS
        settingsArray[10] = isStudentSchoolLimit; // THIS
        settingsArray[11] = daStudentSchoolLimit; // THIS
        settingsArray[12] = debateStudentSchoolLimit; // THIS

        settingsArray[13] = ooJudgeRoomLimit;
        settingsArray[14] = oiJudgeRoomLimit;
        settingsArray[15] = isJudgeRoomLimit;
        settingsArray[16] = daJudgeRoomLimit;
        settingsArray[17] = debateJudgeRoomLimit;
        settingsArray[18] = finalsJudgeRoomLimit;

        settingsArray[19] = roomLimit1;
        settingsArray[20] = roomLimit2;
        settingsArray[21] = timeLimit1;
        settingsArray[22] = timeLimit2;
        settingsArray[23] = schoolNumber; // THIS
        settingsArray[24] = teacherSchoolNumber; // THIS
        settingsArray[25] = studentSchoolNumber; // THIS

        settingsArray[26] = allowCombinedEvents;

        settingsArray[27] = combinedEvent1;
        settingsArray[28] = combinedEvent2;*/

        String returnString = "";
        String firstString = school.getName() + ":\n";
        boolean somethingReturned = false;

        if (school.getTeacherTreeSize() < (Integer) settingsArray[24]) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tFewer teachers than expected.\n";
        } else if (school.getTeacherTreeSize() == 0) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tNo teachers.\n";
        }

        if (school.getStudentTreeSize() > (Integer) settingsArray[25]) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tMore students than expected.\n";
        } else if (school.getStudentTreeSize() == 0) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tNo students.\n";
        }

        if (school.getOOStudentNumber() > (Integer) settingsArray[8]) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tMore OO students than expected.\n";
        } else if (school.getOOStudentNumber() == 0) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tNo OO students.\n";
        }

        if (school.getOIStudentNumber() > (Integer) settingsArray[9]) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tMore OI students than expected.\n";
        } else if (school.getOIStudentNumber() == 0) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tNo OI students.\n";
        }

        if (school.getISStudentNumber() > (Integer) settingsArray[10]) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tMore IS students than expected.\n";
        } else if (school.getISStudentNumber() == 0) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tNo IS students.\n";
        }

        if (school.getDAPairTreeSize() > (Integer) settingsArray[11]) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tMore DA pairs than expected.\n";
        } else if (school.getDAPairTreeSize() == 0) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tNo DA pairs.\n";
        }

        if (school.getDebatePairTreeSize() > (Integer) settingsArray[12]) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tMore Debate pairs than expected.\n";
        } else if (school.getDebatePairTreeSize() == 0) {
            if (!somethingReturned) {
                somethingReturned = true;
                returnString += firstString;
            }
            returnString += "\tNo Debate pairs.\n";
        }

        return returnString;
    }

    // SAVING/LOADING METHODS:
    /**
     * The loadFile method invokes a file chooser dialog, prompting the user to
     * select a file to be loaded. Then, it calls the readFile method to parse
     * the file contents.
     *
     * @throws UserIOException if the file chooser dialog is canceled by the
     * user
     * @throws FileIOException if the file format does not correspond to what
     * the program is expecting
     * @throws IOException if an error is encountered during the reading of
     * data from the file, or the save dialog returned the ERROR_OPTION
     *
     * @author Zbyněk Stara
     */
    private void loadFile() throws UserIOException, FileIOException, IOException {
        JFileChooser jfc = new JFileChooser();

        int loadDialogReturn = jfc.showOpenDialog(this);

        if (loadDialogReturn == JFileChooser.APPROVE_OPTION) {
            loadFile = jfc.getSelectedFile();
            readFile(loadFile);
        } else if (loadDialogReturn == JFileChooser.CANCEL_OPTION) {
            throw new UserIOException();
        } else {
            throw new IOException();
        }
    }

    /**
     * The readFile method tries to parse contents of a provided file. A certain
     * file format is expected; if the file provided is not in that format, an
     * exception is thrown.
     *
     * @param readFile the file to be read in
     * @throws FileIOException if the file cannot be parsed
     * @throws IOException if another unspecified input/output exception has
     * occurred during the parsing of the file
     *
     * @author Zbyněk Stara
     */
    private void readFile(File readFile) throws FileIOException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(readFile));

        String currentLine = "";

        // s<editor-fold defaultstate="collapsed" desc="Introduction">
        currentLine = br.readLine();
        if (currentLine == null || !currentLine.equals("{NESDA Tournament Manager Save File}")) {
            throw new FileIOException();
        }

        if (br.readLine() == null) {
            throw new FileIOException();
        }
        if (br.readLine() == null) {
            throw new FileIOException();
        }
        if (br.readLine() == null) {
            throw new FileIOException();
        }
        if (br.readLine() == null) {
            throw new FileIOException();
        }
        if (br.readLine() == null) {
            throw new FileIOException();
        }
        if (br.readLine() == null) {
            throw new FileIOException();
        }
        if (br.readLine() == null) {
            throw new FileIOException();
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="$Attributes">
        currentLine = br.readLine();
        if (currentLine == null || !currentLine.equals("$Attributes")) {
            throw new FileIOException();
        }

        currentLine = br.readLine();
        try {
        approvedSectionsIndex = Integer.parseInt(currentLine);
        } catch (NumberFormatException ex) {
            throw new FileIOException();
        }

        currentLine = br.readLine();
        if (currentLine != null) {
            String noNameNumbersString = currentLine;
            StringTokenizer st = new StringTokenizer(noNameNumbersString, ",");

            String noNameSchoolNumberString = st.nextToken();
            currentNewNoNameSchoolNumber = Integer.parseInt(noNameSchoolNumberString);

            String noNameTeacherNumberString = st.nextToken();
            currentNewNoNameTeacherNumber = Integer.parseInt(noNameTeacherNumberString);

            String noNameStudentNumberString = st.nextToken();
            currentNewNoNameStudentNumber = Integer.parseInt(noNameStudentNumberString);
        } else {
            throw new FileIOException();
        }

        currentLine = br.readLine();
        if (currentLine != null) {
            String numbersString = currentLine;
            StringTokenizer st = new StringTokenizer(numbersString, ",");

            String schoolsNumberString = st.nextToken();
            schoolsNumber = Integer.parseInt(schoolsNumberString);

            String teachersNumberString = st.nextToken();
            teachersNumber = Integer.parseInt(teachersNumberString);

            String studentsNumberString = st.nextToken();
            studentsNumber = Integer.parseInt(studentsNumberString);
        } else {
            throw new FileIOException();
        }

        currentLine = br.readLine();
        if (currentLine != null) {
            String booleansString = currentLine;
            StringTokenizer st = new StringTokenizer(booleansString, ",");

            String reassignmentTextString = st.nextToken();
            reassignmentText = Boolean.parseBoolean(reassignmentTextString);

            String studentCodesString = st.nextToken();
            studentCodes = Boolean.parseBoolean(studentCodesString);

            String studentChangesString = st.nextToken();
            studentChanges = Boolean.parseBoolean(studentChangesString);
        } else {
            throw new FileIOException();
        }

        if (br.readLine() == null) {
            throw new FileIOException();
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="$Settings">
        if (approvedSectionsIndex >= 0) {
            currentLine = br.readLine();
            if (currentLine == null || !currentLine.equals("$Settings")) {
                throw new FileIOException();
            }

            Object[] settingsArray = new Object[29];
            for (int i = 0; i < 29; i++) {
                currentLine = br.readLine();
                if (currentLine == null) {
                    throw new FileIOException();
                }
                if (currentLine.startsWith("#\t")) {
                    settingsArray[i] = Integer.parseInt(currentLine.substring(2));
                } else if (currentLine.startsWith(">\t")) {
                    settingsArray[i] = Boolean.parseBoolean(currentLine.substring(2));
                } else {
                    throw new FileIOException();
                }
            }
            database.setSettings(settingsArray);

            currentLine = br.readLine();
            if (currentLine == null) {
                throw new FileIOException();
            }
        } else {
            return;
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="$Participants">
        if (approvedSectionsIndex >= 1) {
            currentLine = br.readLine();
            if (currentLine == null || !currentLine.equals("$Participants")) {
                throw new FileIOException();
            }

            database = new Database();

            School currentSchool = null;
            boolean updateSchool = false;

            while (true) {

                currentLine = br.readLine();

                if (currentLine == null) {
                    throw new FileIOException();

                } else if (currentLine.equals("")) {
                    // insert the previous school to database
                    if (currentSchool != null && updateSchool) {
                        currentSchool.updateStudentPairs();
                        database.insertSchool(currentSchool);
                        updateSchool = false;
                    }

                    break;

                } else if (currentLine.startsWith(".\t")) { // SCHOOL
                    // insert the previous school to database
                    if (currentSchool != null && updateSchool) {
                        currentSchool.updateStudentPairs();
                        database.insertSchool(currentSchool);
                        updateSchool = false;
                    }

                    // read the current line
                    StringTokenizer st1 = new StringTokenizer(currentLine,":");

                    // get principal attribute (name)
                    String firstPart = st1.nextToken();
                    String schoolName = firstPart.substring(2);

                    // get the other attributes (behind the ":")
                    String secondPart = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(secondPart,",");

                    // (codeLetter)
                    char schoolCodeLetter;
                    try {
                        schoolCodeLetter = (st2.nextToken().toCharArray())[0];
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        throw new FileIOException();
                    }

                    // (beginCodeNumber)
                    int schoolBeginCodeNumber;
                    try {
                        schoolBeginCodeNumber = Integer.parseInt(st2.nextToken());
                    } catch (NumberFormatException ex) {
                        throw new FileIOException();
                    }

                    // (endCodeNumber)
                    int schoolEndCodeNumber;
                    try {
                        schoolEndCodeNumber = Integer.parseInt(st2.nextToken());
                    } catch (NumberFormatException ex) {
                        throw new FileIOException();
                    }

                    // construct the current school
                    currentSchool = new School(schoolName, schoolCodeLetter, schoolBeginCodeNumber, schoolEndCodeNumber);

                    updateSchool = true;

                } else if (currentLine.startsWith("?\t\t") && currentSchool != null) { // TEACHER
                    // get principal attribute (name)
                    String teacherName = currentLine.substring(3);

                    // construct the current teacher
                    Teacher currentTeacher = new Teacher(teacherName, currentSchool);

                    // insert the current teacher to current school
                    try {
                        currentSchool.insertTeacher(currentTeacher);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("!\t\t") && currentSchool != null) { // STUDENT
                    // read the current line
                    StringTokenizer st1 = new StringTokenizer(currentLine,":");

                    // get principal attribute (name)
                    String firstPart = st1.nextToken();
                    String studentName = firstPart.substring(3);

                    // get the other attributes (behind the ":")
                    String secondPart = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(secondPart,",");

                    // (code)
                    String studentCode = st2.nextToken();

                    // (events)
                    boolean[] studentEvents = new boolean[5];
                    for (int i = 0; i < 5; i++) {
                        String currentEventString = st2.nextToken();
                        if (currentEventString.equalsIgnoreCase("true")) {
                            studentEvents[i] = true;

                            switch (i) {
                                case 0: // oo
                                    currentSchool.incrementOOStudentNumber();
                                    break;
                                case 1: // oi
                                    currentSchool.incrementOIStudentNumber();
                                    break;
                                case 2: // is
                                    currentSchool.incrementISStudentNumber();
                                    break;
                            }
                        } else if (currentEventString.equalsIgnoreCase("false")) {
                            studentEvents[i] = false;
                        } else {
                            throw new FileIOException();
                        }
                    }

                    // (daUnpaired)
                    boolean studentDAUnpaired;
                    String studentDAUnpairedString = st2.nextToken();
                    if (studentDAUnpairedString.equalsIgnoreCase("true")) {
                        studentDAUnpaired = true;
                    } else if (studentDAUnpairedString.equalsIgnoreCase("false")) {
                        studentDAUnpaired = false;
                    } else {
                        throw new FileIOException();
                    }

                    // (debateUnpaired)
                    boolean studentDebateUnpaired;
                    String studentDebateUnpairedString = st2.nextToken();
                    if (studentDebateUnpairedString.equalsIgnoreCase("true")) {
                        studentDebateUnpaired = true;
                    } else if (studentDebateUnpairedString.equalsIgnoreCase("false")) {
                        studentDebateUnpaired = false;
                    } else {
                        throw new FileIOException();
                    }

                    // (daStudentPairTempString)
                    String daTokenString = st2.nextToken();
                    String studentDAStudentPairTempString;
                    if (!daTokenString.equals("null")) {
                        studentDAStudentPairTempString = daTokenString;
                    } else {
                        studentDAStudentPairTempString = "";
                    }

                    // (debateStudentPairTempString)
                    String debateTokenString = st2.nextToken();
                    String studentDebateStudentPairTempString;
                    if (!debateTokenString.equals("null")) {
                        studentDebateStudentPairTempString = debateTokenString;
                    } else {
                        studentDebateStudentPairTempString = "";
                    }

                    // (reassignmentText)
                    boolean studentReassignmentText;
                    String studentReassignmentTextString = st2.nextToken();
                    if (studentReassignmentTextString.equalsIgnoreCase("true")) {
                        studentReassignmentText = true;
                    } else if (studentReassignmentTextString.equalsIgnoreCase("false")) {
                        studentReassignmentText = false;
                    } else {
                        throw new FileIOException();
                    }

                    // construct the current student
                    Student currentStudent = new Student(studentName, currentSchool, studentCode, studentEvents, studentDAUnpaired, studentDebateUnpaired,
                            studentDAStudentPairTempString, studentDebateStudentPairTempString, studentReassignmentText);

                    // insert the current teacher to current school
                    try {
                        currentSchool.insertStudent(currentStudent);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("/\t\t") && currentSchool != null) { // UNPAIRED DA
                    // get principal attribute (name)
                    String unpairedDAStudentName = currentLine.substring(3);

                    // find a student with the same name
                    Student unpairedDAStudent;
                    try {
                        unpairedDAStudent = currentSchool.getStudent(unpairedDAStudentName);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }
                    
                    // insert the current unpaired da student to current school
                    try {
                        currentSchool.insertUnpairedDAStudent(unpairedDAStudent);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("|\t\t") && currentSchool != null) { // UNPAIRED DEBATE
                    // get principal attribute (name)
                    String unpairedDebateStudentName = currentLine.substring(3);

                    // find a student with the same name
                    Student unpairedDebateStudent;
                    try {
                        unpairedDebateStudent = currentSchool.getStudent(unpairedDebateStudentName);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // insert the current unpaired debate student to current school
                    try {
                        currentSchool.insertUnpairedDebateStudent(unpairedDebateStudent);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("-\t\t") && currentSchool != null) { // DA PAIR
                    // read the current line
                    StringTokenizer st1 = new StringTokenizer(currentLine,":");

                    // get principal attribute (originalCode)
                    String firstPart = st1.nextToken();
                    String daPairOriginalCode = firstPart.substring(2);

                    // get the other attributes (behind the ":")
                    String secondPart = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(secondPart,",");

                    // (student1)
                    String daPairStudent1Name = st2.nextToken();

                    Student daPairStudent1;
                    try {
                        daPairStudent1 = currentSchool.getStudent(daPairStudent1Name);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // (student2)
                    String daPairStudent2Name = st2.nextToken();

                    Student daPairStudent2;
                    try {
                        daPairStudent2 = currentSchool.getStudent(daPairStudent2Name);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // construct the current da pair
                    DAStudentPair daPair;
                    try {
                        daPair = new DAStudentPair(daPairStudent1, daPairStudent2);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                    // check that the constructed da pair has the same code as it should
                    if (!daPair.getOriginalCode().equals(daPairOriginalCode)) {
                        throw new FileIOException();
                    }

                    // insert the current da pair to current school
                    try {
                        currentSchool.insertDAPair(daPair);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("<\t\t") && currentSchool != null) { // DEBATE PAIR
                    // read the current line
                    StringTokenizer st1 = new StringTokenizer(currentLine,":");

                    // get principal attribute (originalCode)
                    String firstPart = st1.nextToken();
                    String debatePairOriginalCode = firstPart.substring(2);

                    // get the other attributes (behind the ":")
                    String secondPart = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(secondPart,",");

                    // (student1)
                    String debatePairStudent1Name = st2.nextToken();

                    Student debatePairStudent1;
                    try {
                        debatePairStudent1 = currentSchool.getStudent(debatePairStudent1Name);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // (student2)
                    String debatePairStudent2Name = st2.nextToken();

                    Student debatePairStudent2;
                    try {
                        debatePairStudent2 = currentSchool.getStudent(debatePairStudent2Name);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // construct the current debate pair
                    DebateStudentPair debatePair;
                    try {
                        debatePair = new DebateStudentPair(debatePairStudent1, debatePairStudent2);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                    // check that the constructed debate pair has the same code as it should
                    if (!debatePair.getOriginalCode().equals(debatePairOriginalCode)) {
                        throw new FileIOException();
                    }

                    // insert the current debate pair to current school
                    try {
                        currentSchool.insertDebatePair(debatePair);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else {
                    throw new FileIOException();
                }
            }
        } else {
            database.eraseSchoolTree();
            return;
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="$Qualification">
        if (approvedSectionsIndex >= 2) {
            currentLine = br.readLine();
            if (currentLine == null || !currentLine.equals("$Qualification")) {
                throw new FileIOException();
            }

            qualification = new Qualification(database);

            int currentEventIndex = -999;
            int eventJudgesPerRoom = -999;
            int eventEntitiesPerRoom = -999;
            BinarySearchTree currentJudgeTree = new BinarySearchTree();
            BinarySearchTree currentEntityTree = new BinarySearchTree();
            boolean updateEvent = false;

            int currentRoundIndex = 0;
            boolean updateRound = false;

            int currentRoomIndex = 0;
            boolean updateRoom = false;

            while (true) {
                currentLine = br.readLine();

                if (currentLine == null) {
                    throw new FileIOException();

                } else if (currentLine.equals("")) { // LAST LINE
                    if (updateEvent) {
                        currentRoundIndex = 0;

                        updateEvent = false;
                    }
                    if (updateRound) {
                        currentRoundIndex += 1;

                        currentRoomIndex = 0;

                        updateRound = false;
                    }
                    if (updateRoom) {
                        currentRoomIndex += 1;

                        updateRoom = false;
                    }

                    break;

                } if (currentLine.startsWith("@\t")) { // EVENT
                    // update event
                    if (updateEvent) {
                        currentEventIndex += 1;

                        currentRoundIndex = 0;

                        updateEvent = false;
                    }

                    // read the current line
                    StringTokenizer st1 = new StringTokenizer(currentLine,":");

                    // get principal attribute (typeEventName)
                    String firstPart = st1.nextToken();
                    String eventName = firstPart.substring(2);

                    // get the other attributes (behind the ":")
                    String secondPart = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(secondPart,",");

                    // (judgesPerRoom)
                    try {
                        String eventJudgesPerRoomString = st2.nextToken();
                        eventJudgesPerRoom = Integer.parseInt(eventJudgesPerRoomString);
                    } catch (NumberFormatException ex) {
                        throw new FileIOException();
                    }

                    // (entitiesPerRoom)
                    try {
                        String eventEntitiesPerRoomString = st2.nextToken();
                        eventEntitiesPerRoom = Integer.parseInt(eventEntitiesPerRoomString);
                    } catch (NumberFormatException ex) {
                        throw new FileIOException();
                    }

                    // get eventType and appropriate trees and event indices
                    currentJudgeTree = qualification.getJudgeTree();

                    Event.Type eventType;
                    if (eventName.equals(Event.Type.ORIGINAL_ORATORY.getEventName())) {
                        eventType = Event.Type.ORIGINAL_ORATORY;
                        currentEntityTree = qualification.getOOEntityTree();
                        currentEventIndex = 0;

                    } else if (eventName.equals(Event.Type.ORAL_INTERPRETATION.getEventName())) {
                        eventType = Event.Type.ORAL_INTERPRETATION;
                        currentEntityTree = qualification.getOIEntityTree();
                        currentEventIndex = 1;

                    } else if (eventName.equals(Event.Type.IMPROMPTU_SPEAKING.getEventName())) {
                        eventType = Event.Type.IMPROMPTU_SPEAKING;
                        currentEntityTree = qualification.getISEntityTree();
                        currentEventIndex = 2;

                    } else if (eventName.equals(Event.Type.DUET_ACTING.getEventName())) {
                        eventType = Event.Type.DUET_ACTING;
                        currentEntityTree = qualification.getDAEntityTree();
                        currentEventIndex = 3;

                    } else if (eventName.equals(Event.Type.DEBATE.getEventName())) {
                        eventType = Event.Type.DEBATE;
                        currentEntityTree = qualification.getDebateEntityTree();
                        currentEventIndex = 4;

                    } else {
                        throw new FileIOException();
                    }

                    // construct the current event
                    QualificationEvent currentEvent = new QualificationEvent(qualification, eventType, currentJudgeTree, currentEntityTree, eventJudgesPerRoom, eventEntitiesPerRoom);

                    // set the currentEvent to be the event at the correct place in eventArray
                    qualification.setEvent(currentEvent, currentEventIndex);

                    updateEvent = true;

                } else if (currentLine.startsWith("~\t\t")) { // ROUND
                    // update round
                    if (updateRound) {
                        currentRoundIndex += 1;

                        currentRoomIndex = 0;

                        updateRound = false;

                        updateRoom = false;
                    }

                    // read the current line and set name
                    String roundName = currentLine.substring(3);

                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);

                    // construct the current round
                    Round currentRound = new Round(currentEvent, currentRoundIndex, roundName, currentJudgeTree, currentEntityTree, eventJudgesPerRoom, eventEntitiesPerRoom);

                    // add the new current round to be the round at the correct place in roundArray
                    currentEvent.setRound(currentRound, currentRoundIndex);

                    updateRound = true;

                }  else if (currentLine.startsWith("%\t\t\t")) { // ROOM
                    // update room index
                    if (updateRoom) {
                        currentRoomIndex += 1;

                        updateRoom = false;
                    }

                    // read the current line and set name
                    String roomName = currentLine.substring(4);

                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);
                    Round currentRound = currentEvent.getRoundArrayElement(currentRoundIndex);

                    // construct the current room
                    Room currentRoom = new Room(currentRound, roomName, eventJudgesPerRoom, eventEntitiesPerRoom);

                    // add the new current room to be the room at the correct place in this round's roomArray
                    currentRound.setRoom(currentRoom, currentRoomIndex);

                    updateRoom = true;

                } else if (currentLine.startsWith("r\t\t\t\t")) { // JUDGE IN ROOM
                    // get principal attribute (ID)
                    int roomJudgeID;
                    try {
                        roomJudgeID = Integer.parseInt(currentLine.substring(5));
                    } catch (NumberFormatException ex) {
                        throw new FileIOException();
                    }

                    // get the judge under that ID
                    Judge roomJudge;
                    try {
                        roomJudge = qualification.getJudge(roomJudgeID);
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // insert the judge into room judges
                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);
                    Round currentRound = currentEvent.getRoundArrayElement(currentRoundIndex);
                    Room currentRoom = currentRound.getRoomArrayElement(currentRoomIndex);

                    try {
                        currentRoom.allocateJudge(roomJudge);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("o\t\t\t\t")) { // OO ENTITY IN ROOM
                    // get principal attribute (code)
                    String roomEntityCode;
                    //try {
                        roomEntityCode = currentLine.substring(5);
                    //} catch (NumberFormatException ex) {
                    //    throw new FileIOException();
                    //}

                    // get the entity under that code
                    Entity roomEntity;
                    try {
                        roomEntity = qualification.getOOEntity(roomEntityCode);
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // insert the entity into room entities
                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);
                    Round currentRound = currentEvent.getRoundArrayElement(currentRoundIndex);
                    Room currentRoom = currentRound.getRoomArrayElement(currentRoomIndex);

                    try {
                        currentRoom.allocateEntity(roomEntity);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("i\t\t\t\t")) { // OI ENTITY IN ROOM
                    // get principal attribute (code)
                    String roomEntityCode;
                    //try {
                        roomEntityCode = currentLine.substring(5);
                    //} catch (NumberFormatException ex) {
                    //    throw new FileIOException();
                    //}

                    // get the entity under that code
                    Entity roomEntity;
                    try {
                        roomEntity = qualification.getOIEntity(roomEntityCode);
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // insert the entity into room entities
                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);
                    Round currentRound = currentEvent.getRoundArrayElement(currentRoundIndex);
                    Room currentRoom = currentRound.getRoomArrayElement(currentRoomIndex);

                    try {
                        currentRoom.allocateEntity(roomEntity);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("m\t\t\t\t")) { // IS ENTITY IN ROOM
                    // get principal attribute (code)
                    String roomEntityCode;
                    //try {
                        roomEntityCode = currentLine.substring(5);
                    //} catch (NumberFormatException ex) {
                    //    throw new FileIOException();
                    //}

                    // get the entity under that code
                    Entity roomEntity;
                    try {
                        roomEntity = qualification.getISEntity(roomEntityCode);
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // insert the entity into room entities
                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);
                    Round currentRound = currentEvent.getRoundArrayElement(currentRoundIndex);
                    Room currentRoom = currentRound.getRoomArrayElement(currentRoomIndex);

                    try {
                        currentRoom.allocateEntity(roomEntity);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("d\t\t\t\t")) { // DA ENTITY IN ROOM
                    // get principal attribute (code)
                    String roomEntityCode;
                    //try {
                        roomEntityCode = currentLine.substring(5);
                    //} catch (NumberFormatException ex) {
                    //    throw new FileIOException();
                    //}

                    // get the entity under that code
                    Entity roomEntity;
                    try {
                        roomEntity = qualification.getDAEntity(roomEntityCode);
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // insert the entity into room entities
                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);
                    Round currentRound = currentEvent.getRoundArrayElement(currentRoundIndex);
                    Room currentRoom = currentRound.getRoomArrayElement(currentRoomIndex);

                    try {
                        currentRoom.allocateEntity(roomEntity);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else if (currentLine.startsWith("e\t\t\t\t")) { // DEBATE ENTITY IN ROOM
                    // get principal attribute (code)
                    String roomEntityCode;
                    //try {
                        roomEntityCode = currentLine.substring(5);
                    //} catch (NumberFormatException ex) {
                    //    throw new FileIOException();
                    //}

                    // get the entity under that code
                    Entity roomEntity;
                    try {
                        roomEntity = qualification.getDebateEntity(roomEntityCode);
                    } catch (NoSuchElementException ex) {
                        throw new FileIOException();
                    }

                    // insert the entity into room entities
                    Event currentEvent = qualification.getEventArrayElement(currentEventIndex);
                    Round currentRound = currentEvent.getRoundArrayElement(currentRoundIndex);
                    Room currentRoom = currentRound.getRoomArrayElement(currentRoomIndex);

                    try {
                        currentRoom.allocateEntity(roomEntity);
                    } catch (IllegalArgumentException ex) {
                        throw new FileIOException();
                    }

                } else {
                    throw new FileIOException();
                }
            }
        } else { // if this section was not approved
            return; // end reading
        }
        // </editor-fold>
    }

    /**
     * The saveFile saves the contents of the program into a user-specified
     * file.
     * <p>
     * A file chooser dialog is invoked, prompting the user to select a file or
     * create a new one. The sections of the program up to and including the one
     * specified by the value of the approvedSectionsIndex are saved.
     *
     * @throws UserIOException if the file chooser dialog is canceled by the
     * user
     * @throws IOException if an error is encountered during the writing of the
     * data into the file, or the save dialog returned the ERROR_OPTION
     *
     * @author Zbyněk Stara
     */
    private void saveFile() throws FileIOException, UserIOException, IOException {
        JFileChooser jfc = new JFileChooser();

        int saveDialogReturn = jfc.showSaveDialog(this);

        if (saveDialogReturn == JFileChooser.APPROVE_OPTION) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(jfc.getSelectedFile()));

            /* <editor-fold defaultstate="collapsed" desc="Symbols used for parsing">
             *
             * (WATCH OUT! Only first 256 Unicode symbols can be properly rendered in .txt files)
             *
             * $      - section
             *
             * #\t    - int setting
             * >\t    - boolean setting
             * &\t    - button selection control
             *
             * .\t    - school
             * ?\t\t  - teacher
             * !\t\t  - student
             * /\t\t  - unpairedDAStudent
             * |\t\t  - unpairedDebateStudent
             * -\t\t  - daPair
             * <\t\t  - debatePair
             *
             * J\t    - judge in tree
             * j\t\t  - judge ref
             * O\t    - oo entity in tree
             * jo\t\t - encountered judge for oo entity
             * I\t    - oi entity in tree
             * ji\t\t - encountered judge for oi entity
             * M\t    - is entity in tree
             * jm\t\t - encountered judge for is entity
             * D\t    - da entity in tree
             * jd\t\t - encountered judge for da entity
             * E\t    - debate entity int tree
             * je\t\t - encountered judge for debate entity
             * @\t    - event
             * ~\t\t  - round
             * f\t\t\t  - free judge tree
             * %\t\t\t  - room
             * r\t\t\t\t  - judge
             * o\t\t\t\t  - oo entity ref
             * i\t\t\t\t  - oi entity ref
             * m\t\t\t\t  - is entity ref
             * d\t\t\t\t  - da entity ref
             * e\t\t\t\t  - debate entity ref
             
             
             */ // </editor-fold>

            System.out.println("before intro");

            // <editor-fold defaultstate="collapsed" desc="Introduction (8 lines)">
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
            String currentTimestamp = dateFormat.format(currentDate);

            bw.write("{NESDA Tournament Manager Save File}\n");
            bw.write("\n");
            bw.write("Created by NESDA Tournament Manager 2012 on " + currentTimestamp + "\n");
            bw.write("\n");
            bw.write("IMPORTANT: Do not modify this file. Your modifications "
                    + "might disrupt the program's parsing of saved information "
                    + "and lead to errors or cause a possible loss of data.\n");
            bw.write("\n");
            bw.write("NOTE: This file is not an export file of the data "
                    + "inputted into the NESDA tournament manager. If you wish "
                    + "to obtain a plain-text copy of a select section of the "
                    + "program, click the appropriate \"Export\" button in the "
                    + "program's user interface.\n");
            bw.write("\n");// </editor-fold>

            System.out.println("before attributes");

            // <editor-fold defaultstate="collapsed" desc="$Attributes">
            bw.write("$Attributes\n");
            bw.write(approvedSectionsIndex + "\n");
            bw.write(currentNewNoNameSchoolNumber + "," + currentNewNoNameTeacherNumber + "," + currentNewNoNameStudentNumber + "\n");
            bw.write(schoolsNumber + "," + teachersNumber + "," + studentsNumber + "\n");
            bw.write(reassignmentText + "," + studentCodes +  "," + studentChanges + "\n");
            bw.write("\n");// </editor-fold>

            System.out.println("before settings");

            // <editor-fold defaultstate="collapsed" desc="$Settings">
            if (approvedSectionsIndex >= 0) {
                bw.write("$Settings\n");

                Object[] settingsArray = database.getSettings();

                String settingsHelperString = "";
                for (int i = 0; i < settingsArray.length; i++) {
                    if (settingsArray[i].getClass().equals(Integer.class)) {
                        settingsHelperString += ("#\t" + ((Integer) settingsArray[i]).toString() + "\n");
                    } else if (settingsArray[i].getClass().equals(Boolean.class)) {
                        settingsHelperString += (">\t" + ((Boolean) settingsArray[i]).toString() + "\n");
                    }
                }
                bw.write(settingsHelperString);

                bw.write("\n");
            } else {
                bw.close();
                return;
            } // </editor-fold>

            System.out.println("before participants");

            // <editor-fold defaultstate="collapsed" desc="$Participants">
            if (approvedSectionsIndex >= 0) {
                bw.write("$Participants\n");

                String participantsHelperString = "";
                for (int i = 0; i < database.getSchoolTreeSize(); i++) {
                    School school = (School) database.getSchoolTreeNodeData(i);

                    participantsHelperString += (".\t" + school.getName() + ":"
                            + school.getCodeLetter() + "," + school.getBeginCodeNumber() + "," + school.getEndCodeNumber() + "\n");

                    for (int j = 0; j < school.getTeacherTreeSize(); j++) {
                        Teacher teacher = (Teacher) school.getTeacherTreeNodeData(j);

                        participantsHelperString += ("?\t\t" + teacher.getName() + "\n");
                    }

                    for (int j = 0; j < school.getStudentTreeSize(); j++) {
                        Student student = (Student) school.getStudentTreeNodeData(j);

                        participantsHelperString += ("!\t\t" + student.getName() + ":"
                                + student.getCode() + "," + student.getEvents()[0] + ","
                                + student.getEvents()[1] + "," + student.getEvents()[2] + ","
                                + student.getEvents()[3] + "," + student.getEvents()[4] + ","
                                + student.getDAUnpaired() + "," + student.getDebateUnpaired() + ",");

                        if (student.getDAStudentPair() != null) {
                            participantsHelperString += student.getDAStudentPair().getOriginalCode() + ",";
                        } else {
                            participantsHelperString += "null" + ",";
                        }

                        if (student.getDebateStudentPair() != null) {
                            participantsHelperString += student.getDebateStudentPair().getOriginalCode();
                        } else {
                            participantsHelperString += "null" + ",";
                        }
                        
                        participantsHelperString += (student.getReassignmentText() + "\n");
                    }

                    for (int j = 0; j < school.getUnpairedDAStudentTreeSize(); j++) {
                        Student student = (Student) school.getUnpairedDAStudentTreeNodeData(j);

                        participantsHelperString += ("/\t\t" + student.getName() + "\n"); // found in students tree
                    }

                    for (int j = 0; j < school.getUnpairedDebateStudentTreeSize(); j++) {
                        Student student = (Student) school.getUnpairedDebateStudentTreeNodeData(j);

                        participantsHelperString += ("|\t\t" + student.getName() + "\n"); // found in students tree
                    }

                    for (int j = 0; j < school.getDAPairTreeSize(); j++) {
                        DAStudentPair daStudentPair = (DAStudentPair) school.getDAPairTreeNodeData(j);

                        participantsHelperString += ("-\t\t" + daStudentPair.getOriginalCode() + ":"
                                + daStudentPair.getStudentArray()[0].getName() + "," + daStudentPair.getStudentArray()[1].getName() + "\n");
                    }

                    for (int j = 0; j < school.getDebatePairTreeSize(); j++) {
                        DebateStudentPair debateStudentPair = (DebateStudentPair) school.getDebatePairTreeNodeData(j);

                        participantsHelperString += ("<\t\t" + debateStudentPair.getOriginalCode() + ":"
                                + debateStudentPair.getStudentArray()[0] + "," + debateStudentPair.getStudentArray()[1] + "\n");
                    }
                }
                bw.write(participantsHelperString);

                bw.write("\n");
            } else {
                bw.close();
                return;
            } // </editor-fold>

            System.out.println("before qualification");

            // <editor-fold defaultstate="collapsed" desc="$Qualification">
            if (approvedSectionsIndex >= 2) {
                bw.write("$Qualification\n");

                String qualificationHelperString = "";

                for (int i = 0; i < qualification.getEventArrayLength(); i++) {
                    Event currentEvent = qualification.getEventArrayElement(i);

                    qualificationHelperString += ("@\t" + currentEvent.getTypeEventName() + ":"
                            /*+ currentEvent.isSemifinal() + "," + currentEvent.isFinal() + ","*/
                            /*+ currentEvent.getShortEventName() + ","*/
                            + currentEvent.getJudgesPerRoom() + ","
                            + currentEvent.getEntitiesPerRoom() + "\n");

                    for (int j = 0; j < currentEvent.getRoundArrayLength(); j++) {
                        Round currentRound = currentEvent.getRoundArrayElement(j);

                        qualificationHelperString += ("~\t\t" + currentRound.getName() + "\n");

                        /*for (int k = 0; k < currentRound.getFreeJudgeTreeSize(); k++) {
                            Judge currentFreeJudge = currentRound.getFreeJudgeTreeNodeData(k);

                            qualificationHelperString += ("f\t\t\t" + currentFreeJudge.getID() + "\n");
                        }*/

                        for (int k = 0; k < currentRound.getRoomArrayLength(); k++) {
                            Room currentRoom = currentRound.getRoomArrayElement(k);

                            qualificationHelperString += ("%\t\t\t" + currentRoom.getName() + "\n");

                            for (int l = 0; l < currentRoom.getJudgeTreeSize(); l++) {
                                Judge currentJudge = (Judge) currentRoom.getJudgeTreeNodeData(l);

                                qualificationHelperString += ("r\t\t\t\t" + currentJudge.getID() + "\n");
                            }

                            for (int l = 0; l < currentRoom.getEntityTreeSize(); l++) {
                                Entity currentEntity = (Entity) currentRoom.getEntityTreeNodeData(l);

                                if (currentEntity.getType() == Event.Type.ORIGINAL_ORATORY) {
                                    qualificationHelperString += ("o\t\t\t\t" + currentEntity.getCode() + "\n");
                                } else if (currentEntity.getType() == Event.Type.ORAL_INTERPRETATION) {
                                    qualificationHelperString += ("i\t\t\t\t" + currentEntity.getCode() + "\n");
                                } else if (currentEntity.getType() == Event.Type.IMPROMPTU_SPEAKING) {
                                    qualificationHelperString += ("m\t\t\t\t" + currentEntity.getCode() + "\n");
                                } else if (currentEntity.getType() == Event.Type.DUET_ACTING) {
                                    qualificationHelperString += ("d\t\t\t\t" + currentEntity.getCode() + "\n");
                                } else if (currentEntity.getType() == Event.Type.DEBATE) {
                                    qualificationHelperString += ("e\t\t\t\t" + currentEntity.getCode() + "\n");
                                } else { // if type is UNDEFINED
                                    throw new FileIOException();
                                }
                            }
                        }
                    }
                }
                bw.write(qualificationHelperString);

                bw.write("\n");
            } else {
                bw.close();
                return;
            } // </editor-fold>

            System.out.println("after qualification");

            // NOT USED:

            // <editor-fold defaultstate="collapsed" desc="$Finalists">
            if (approvedSectionsIndex >= 3) {
                bw.write("$Finalists\n");

                bw.write("\n");
            } else {
                bw.close();
                return;
            } // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="$Finals">
            if (approvedSectionsIndex >= 4) {
                bw.write("$Finals\n");

                bw.write("\n");
            } else {
                bw.close();
                return;
            } // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="$Winners">
            if (approvedSectionsIndex >= 5) {
                bw.write("$Winners\n");

                bw.write("\n");
            } else {
                bw.close();
                return;
            } // </editor-fold>
        } else if (saveDialogReturn == JFileChooser.CANCEL_OPTION) {
            throw new UserIOException();
        } else {
            throw new IOException();
        }
    }

    // ACCESSING STATIC MAIN_GUI ELEMENTS:
    /**
     * This method allows access to the static allocationInfoDialog to other
     * parts of the program. It is used by the AllocationWorker as part of the
     * progress-reporting functionality.
     *
     * @return allocationInfoDialog
     *
     * @author Zbyněk Stara
     */
    public static javax.swing.JDialog getAllocationInfoDialog() {
        return allocationInfoDialog;
    }

    /**
     * This method allows access to the static allocationInfoTextField to other
     * parts of the program. It is used by the AllocationWorker as part of the
     * progress-reporting functionality.
     *
     * @return allocationInfoTextField
     *
     * @author Zbyněk Stara
     */
    public static javax.swing.JTextField getAllocationInfoTextField() {
        return allocationInfoTextField;
    }

    /**
     * This method allows access to the static allocationInfoProgressBar to
     * other parts of the program. It is used by the AllocationWorker as part of
     * the progress-reporting functionality.
     *
     * @return allocationInfoProgressBar
     *
     * @author Zbyněk Stara
     */
    public static javax.swing.JProgressBar getAllocationInfoProgressBar() {
        return allocationInfoProgressBar;
    }

    public static javax.swing.JTextField getAllocationInfoTimeTextField() {
        return allocationInfoTimeTextField;
    }

    // MAIN_GUI UPDATES:
    /**
     * This method changes the value of the overallProgressBar
     *
     * @param value an int from 0 to 100, indicating the new value
     * @throws IllegalArgumentException if the value is outside the acceptable
     * range (smaller than 0 or larger than 100)
     *
     * @author Zbyněk Stara
     */
    private void updateOverallProgressBar(int value) throws IllegalArgumentException {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Value passed to overall progress bar is not between 0 and 100.");
        } else {
            overallProgressBar.setValue(value);
        }
    }

    // PARTICIPANTS TAB UPDATES:
    /**
     * This method updates the value of the participantsProgressBar.
     *
     * @param value an int from 0 to 100, indicating the new value
     * @throws IllegalArgumentException if the value is outside the acceptable
     * range
     *
     * @author Zbyněk Stara
     */
    private void updateParticipantsProgressBar(int value) throws IllegalArgumentException {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Value passed to participants progress bar is not between 0 and 100.");
        } else {
            participantsProgressBar.setValue(value);
        }
    }

    /**
     * This method updates the participants progress bar according to the
     * current number of schools.
     *
     * @author Zbyněk Stara
     */
    private void updateParticipantsProgressBar() {
        Object[] settingsArray = database.getSettings();
        int expectedSchoolNumber = (Integer) settingsArray[23]; // the number of schools in the tournament, as set in the settings tab

        double progressBarValue = Math.round(((double) schoolsNumber/(double) expectedSchoolNumber) * 100);
        this.updateParticipantsProgressBar((int) progressBarValue);
    }

    /**
     * This method updates the schoolsListModel to reflect changes in the school
     * tree. After that, the listModel is applied to the schoolsList.
     *
     * @author Zbyněk Stara
     */
    private void updateSchoolsListModel() {
        DefaultListModel model = new DefaultListModel();

        School[] schoolArray = database.getSchoolArray();

        if (schoolArray.length != 0) {
            for (int i = 0; i < schoolArray.length; i++) {
                model.addElement(schoolArray[i].getName());
            }

            participantsSchoolsList.setModel(model);
        } else {
            participantsSchoolsList.setModel(emptyModel);
        }
    }

    /**
     * This method updates the teachersListModel to reflect changes in the
     * teacher tree of a specified school. After that, the listModel is applied
     * to the teachersList.
     *
     * @param currentSchool the school whose teacher tree is being used as a
     * basis of the teachers list
     *
     * @author Zbyněk Stara
     */
    private void updateTeachersListModel(School currentSchool) {
        DefaultListModel model = new DefaultListModel();

        Teacher[] teacherArray = currentSchool.getTeacherArray();

        if (teacherArray.length != 0) {
            for (int i = 0; i < teacherArray.length; i++) {
                model.addElement(teacherArray[i].getName());
            }

            participantsTeachersList.setModel(model);
        } else {
            participantsTeachersList.setModel(emptyModel);
        }
    }

    /**
     * This method updates the studentsListModel to reflect changes in the
     * student tree of a specified school. After that, the listModel is applied
     * to the studentsList.
     *
     * @param currentSchool the school whose student tree is being used as a
     * basis of the students list
     *
     * @author Zbyněk Stara
     */
    private void updateStudentsListModel(School currentSchool) {
        DefaultListModel model = new DefaultListModel();

        Student[] studentArray = currentSchool.getStudentArray();

        if (studentArray.length != 0) {
            for (int i = 0; i < studentArray.length; i++) {
                model.addElement(studentArray[i].getName());
            }

            participantsStudentsList.setModel(model);
        } else {
            participantsStudentsList.setModel(emptyModel);
        }
    }

    /**
     * This method updates the studentCodesListModel to reflect changes in the
     * student tree of a specified school. After that, the listModel is applied
     * to the studentCodesList.
     *
     * @param currentSchool the school whose student tree is being used as a
     * basis of the studentCodesList
     *
     * @author Zbyněk Stara
     */
    private void updateStudentCodesListModel(School currentSchool) {
        DefaultListModel model = new DefaultListModel();

        Student[] studentArray = currentSchool.getStudentArray();

        if (studentArray.length != 0) {
            for (int i = 0; i < studentArray.length; i++) {
                model.addElement(studentArray[i].getCode());
            }

            participantsStudentCodesList.setModel(model);
        } else {
            participantsStudentCodesList.setModel(emptyModel);
        }
    }

    /**
     * This method updates the assignPairsLeftListModel to reflect changes in
     * the pairs trees of a specified school. After that, the listModel is
     * applied to the assignPairsLeftList.
     *
     * @param currentSchool the school whose pairs trees are being used as a
     * basis of the assignPairsLeftList
     *
     * @author Zbyněk Stara
     */
    private void updateAssignPairsLeftListModel(School currentSchool) {
        if (assignPairsEventsIndex == 0) { // DA
            DefaultListModel model = new DefaultListModel();

            Student[] studentArray = currentSchool.getUnpairedDAStudentArray();

            for (int i = 0; i < studentArray.length; i++) {
                model.addElement(studentArray[i].getCode());
            }

            participantsAssignPairsLeftList.setModel(model);
        } else if (assignPairsEventsIndex == 1) { // Debate
            DefaultListModel model = new DefaultListModel();

            Student[] studentArray = currentSchool.getUnpairedDebateStudentArray();

            for (int i = 0; i < studentArray.length; i++) {
                model.addElement(studentArray[i].getCode());
            }

            participantsAssignPairsLeftList.setModel(model);
        } else { // No selection
            participantsAssignPairsLeftList.setModel(blankModel);
        }
    }

    /**
     * This method updates the assignPairsRightListModel to reflect changes in
     * the pairs trees of a specified school. After that, the listModel is
     * applied to the assignPairsRightList.
     *
     * @param currentSchool the school whose pairs trees are being used as a
     * basis of the assignPairsRightList
     *
     * @author Zbyněk Stara
     */
    private void updateAssignPairsRightListModel(School currentSchool) {
        if (assignPairsEventsIndex == 0) { // DA
            DefaultListModel model = new DefaultListModel();

            Student[] studentArray = currentSchool.getUnpairedDAStudentArray();

            for (int i = 0; i < studentArray.length; i++) {
                if (i == assignPairsLeftIndex) continue;
                else model.addElement(studentArray[i].getCode());
            }

            participantsAssignPairsRightList.setModel(model);
        } else if (assignPairsEventsIndex == 1) { // Debate
            DefaultListModel model = new DefaultListModel();

            Student[] studentArray = currentSchool.getUnpairedDebateStudentArray();

            for (int i = 0; i < studentArray.length; i++) {
                if (i == assignPairsLeftIndex) continue;
                else model.addElement(studentArray[i].getCode());
            }

            participantsAssignPairsRightList.setModel(model);
        } else { // No selection
            participantsAssignPairsRightList.setModel(blankModel);
        }
    }

    /**
     * This method updates the assignPairsPairsListModel to reflect changes in
     * the pairs trees of a specified school. After that, the listModel is
     * applied to the assignPairsPairsList.
     *
     * @param currentSchool the school whose pairs trees are being used as a
     * basis of the assignPairsPairsList
     *
     * @author Zbyněk Stara
     */
    private void updateAssignPairsPairsListModel(School currentSchool) {
        if (assignPairsEventsIndex == 0) { // DA
            DefaultListModel model = new DefaultListModel();

            DAStudentPair[] studentPairArray = currentSchool.getDAPairArray();

            for (int i = 0; i < studentPairArray.length; i++) {
                model.addElement(studentPairArray[i].getCode());
            }

            participantsAssignPairsPairsList.setModel(model);
        } else if (assignPairsEventsIndex == 1) { // Debate
            DefaultListModel model = new DefaultListModel();

            DebateStudentPair[] studentPairArray = currentSchool.getDebatePairArray();

            for (int i = 0; i < studentPairArray.length; i++) {
                model.addElement(studentPairArray[i].getCode());
            }

            participantsAssignPairsPairsList.setModel(model);
        } else { // No selection
            participantsAssignPairsPairsList.setModel(blankModel);
        }
    }

    // QUALIFICAITON TAB UPDATES:
    /**
     * This method updates the value of the qualificationProgressBar.
     *
     * @param value an int from 0 to 100, indicating the new value
     * @throws IllegalArgumentException if the value is outside the acceptable
     * range
     *
     * @author Zbyněk Stara
     */
    private void updateQualificationProgressBar(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Value passed to qualification progress bar is not between 0 and 100.");
        } else {
            qualificationProgressBar.setValue(value);
        }
    }

    /**
     * This method updates the qualificationEventsListModel
     *
     * @author Zbyněk Stara
     */
    private void updateEventsListModel() {
        DefaultListModel model = new DefaultListModel();

        Event[] eventArray = qualification.getEventArray();

        for (int i = 0; i < qualification.getEventArrayLength(); i++) {
            model.addElement(eventArray[i].type.getEventName());
        }

        qualificationEventsList.setModel(model);
    }

    /**
     * This method updates the qualificationRoundsListModel to display the
     * rounds of a specified event.
     *
     * @param currentEvent the event whose roundsArray will serve as a basis for
     * this listModel.
     * @throws NullPointerException if the currentEvent supplied is null
     *
     * @author Zbyněk Stara
     */
    private void updateRoundsListModel(Event currentEvent) throws NullPointerException {
        DefaultListModel model = new DefaultListModel();

        Round[] roundArray = currentEvent.getRoundArray();

        for (int i = 0; i < roundArray.length; i++) {
            model.addElement(roundArray[i].getName());
        }

        qualificationRoundsList.setModel(model);
    }

    /**
     * This method updates the qualificationRoomsListModel to display the
     * rooms of a specified round.
     *
     * @param currentROund the round whose roomsArray will serve as a basis for
     * this listModel.
     * @throws NullPointerException if the currentRound supplied is null
     *
     * @author Zbyněk Stara
     */
    private void updateRoomsListModel(Round currentRound) throws NullPointerException {
        DefaultListModel model = new DefaultListModel();

        Room[] roomArray = currentRound.getRoomArray();

        for (int i = 0; i < roomArray.length; i++) {
            model.addElement(roomArray[i].getName());
        }

        qualificationRoomsList.setModel(model);
    }

    /**
     * This method updates the qualificationJudgesListModel to display the
     * judges of a specified room.
     *
     * @param currentRoom the room whose judgesTree will serve as a basis for
     * this listModel.
     * @throws NullPointerException if the currentRoom supplied is null
     *
     * @author Zbyněk Stara
     */
    private void updateJudgesListModel(Room currentRoom) throws NullPointerException {
        DefaultListModel model = new DefaultListModel();

        Judge[] judgeArray = currentRoom.getJudgeArray();

        for (int i = 0; i < judgeArray.length; i++) {
            model.addElement(judgeArray[i].getName());
        }

        qualificationJudgesList.setModel(model);
    }

    /**
     * This method updates the qualificationStudentCodesListModel to display the
     * entities of a specified room.
     *
     * @param currentRoom the room whose entitiesTree will serve as a basis for
     * this listModel.
     * @throws NullPointerException if the currentRoom supplied is null
     *
     * @author Zbyněk Stara
     */
    private void updateQStudentCodesListModel(Room currentRoom) throws NullPointerException {
        DefaultListModel model = new DefaultListModel();

        Entity[] entityArray = currentRoom.getEntityArray();

        for (int i = 0; i < entityArray.length; i++) {
            model.addElement(entityArray[i].getCode());
        }

        qualificationStudentCodesList.setModel(model);
    }

    // UPDATE TABS ACTION BOXES:
    /**
     * This method groups together the often-used group of statements that
     * disable elements of the settings tab.
     *
     * @author Zbyněk Stara
     */
    private void disableSettingsTabActionBox() {
        // <editor-fold defaultstate="collapsed" desc="Disabling text fields for settings">
                    eventSettingsOOSR.setEnabled(false);
                    eventSettingsOISR.setEnabled(false);
                    eventSettingsISSR.setEnabled(false);
                    eventSettingsDASR.setEnabled(false);
                    eventSettingsOOFinalSR.setEnabled(false);
                    eventSettingsOIFinalSR.setEnabled(false);
                    eventSettingsISFinalSR.setEnabled(false);
                    eventSettingsDAFinalSR.setEnabled(false);

                    eventSettingsOOSES.setEnabled(false);
                    eventSettingsOISES.setEnabled(false);
                    eventSettingsISSES.setEnabled(false);
                    eventSettingsDASES.setEnabled(false);
                    eventSettingsDebateSES.setEnabled(false);

                    eventSettingsOOJR.setEnabled(false);
                    eventSettingsOIJR.setEnabled(false);
                    eventSettingsISJR.setEnabled(false);
                    eventSettingsDAJR.setEnabled(false);
                    eventSettingsDebateJR.setEnabled(false);
                    eventSettingsFinalsJR.setEnabled(false);

                    generalSettingsRoomsAvailable1.setEnabled(false);
                    generalSettingsRoomsAvailable2.setEnabled(false);
                    generalSettingsMaximalTime1.setEnabled(false);
                    generalSettingsMaximalTime2.setEnabled(false);
                    generalSettingsSchoolsNumber.setEnabled(false);
                    generalSettingsTS.setEnabled(false);
                    generalSettingsSS.setEnabled(false);
                    generalSettingsCECheckBox.setEnabled(false);
                    generalSettingsCEComboBox1.setEnabled(false);
                    generalSettingsCEComboBox2.setEnabled(false);// </editor-fold>

        settingsLoadButton.setEnabled(false);
        settingsResetButton.setEnabled(false);

        participantsSaveToggleButton.setEnabled(true);
        participantsApproveToggleButton.setEnabled(true);
    }

    /**
     * This method groups together the often-used group of statements that
     * enable elements of the settings tab. This method disables participants
     * and qualification, as well.
     *
     * @author Zbyněk Stara
     */
    private void enableSettingsTabActionBox() {
        this.updateOverallProgressBar(0);

        // <editor-fold defaultstate="collapsed" desc="Enabling text fields for settings">
            eventSettingsOOSR.setEnabled(true);
            eventSettingsOISR.setEnabled(true);
            eventSettingsISSR.setEnabled(true);
            eventSettingsDASR.setEnabled(true);
            eventSettingsOOFinalSR.setEnabled(true);
            eventSettingsOIFinalSR.setEnabled(true);
            eventSettingsISFinalSR.setEnabled(true);
            eventSettingsDAFinalSR.setEnabled(true);

            eventSettingsOOSES.setEnabled(true);
            eventSettingsOISES.setEnabled(true);
            eventSettingsISSES.setEnabled(true);
            eventSettingsDASES.setEnabled(true);
            eventSettingsDebateSES.setEnabled(true);

            eventSettingsOOJR.setEnabled(true);
            eventSettingsOIJR.setEnabled(true);
            eventSettingsISJR.setEnabled(true);
            eventSettingsDAJR.setEnabled(true);
            eventSettingsDebateJR.setEnabled(true);
            eventSettingsFinalsJR.setEnabled(true);

            //generalSettingsRoomsAvailable1.setEnabled(true);
            //generalSettingsRoomsAvailable2.setEnabled(true);
            //generalSettingsMaximalTime1.setEnabled(true);
            //generalSettingsMaximalTime2.setEnabled(true);
            generalSettingsSchoolsNumber.setEnabled(true);
            generalSettingsTS.setEnabled(true);
            generalSettingsSS.setEnabled(true);
            //generalSettingsCECheckBox.setEnabled(true);
            //generalSettingsCEComboBox1.setEnabled(true);
            //generalSettingsCEComboBox2.setEnabled(true);// </editor-fold>

        settingsLoadButton.setEnabled(true);
        settingsResetButton.setEnabled(true);

        this.disableParticipantsTabActionBox();
        
        this.disableQualificationTabActionBox();

        settingsSaveToggleButton.setSelected(false);
        settingsSaveToggleButton.setEnabled(false);

        settingsApproveToggleButton.setSelected(false);
    }

    /**
     * This method groups together the often-used group of statements that
     * disable elements of the participants tab.
     *
     * @author Zbyněk Stara
     */
    private void disableParticipantsTabActionBox() {
        participantsSchoolsTextField.setEnabled(false);

        participantsSchoolsList.setSelectedIndex(-1);
        participantsSchoolsList.setEnabled(false); // needed - #8
        updateSchoolsListModel();

        participantsSchoolsAddButton.setEnabled(false);
        participantsSchoolsRemoveButton.setEnabled(false);
        participantsSchoolsEditButton.setEnabled(false);
        participantsSchoolsSearchButton.setEnabled(false);

        participantsTeachersTextField.setEnabled(false); // needed - #9

        participantsTeachersList.setSelectedIndex(-1);
        participantsTeachersList.setEnabled(false);
        participantsTeachersList.setModel(noSelectionModel);

        participantsTeachersAddButton.setEnabled(false);
        participantsTeachersRemoveButton.setEnabled(false);
        participantsTeachersSearchButton.setEnabled(false);
        participantsTeachersEditButton.setEnabled(false);

        participantsStudentsTextField.setEnabled(false); // needed - #10

        participantsStudentsList.setSelectedIndex(-1);
        participantsStudentsList.setEnabled(false);
        participantsStudentsList.setModel(noSelectionModel);

        participantsStudentsAddButton.setEnabled(false);
        participantsStudentsRemoveButton.setEnabled(false);
        participantsStudentsSearchButton.setEnabled(false);
        participantsStudentsEditButton.setEnabled(false);

        this.disableStudentCodesActionBox(); // #7
        participantsStudentCodesList.setModel(noSelectionModel);

        this.disableAssignEventsActionBox(); // #12

        participantsAssignPairsEventsList.setSelectedIndex(-1);
        participantsAssignPairsEventsList.setEnabled(false);
        participantsAssignPairsEventsList.setModel(eventsListModel);

        participantsAssignPairsLeftList.setSelectedIndex(-1);
        participantsAssignPairsLeftList.setEnabled(false);
        participantsAssignPairsLeftList.setModel(blankModel);

        participantsAssignPairsRightList.setSelectedIndex(-1);
        participantsAssignPairsRightList.setEnabled(false);
        participantsAssignPairsRightList.setModel(blankModel);

        participantsAssignPairsPairsList.setSelectedIndex(-1);
        participantsAssignPairsPairsList.setEnabled(false);
        participantsAssignPairsPairsList.setModel(blankModel);

        participantsAssignPairsPairButton.setEnabled(false);
        participantsAssignPairsRemovePairButton.setEnabled(false);

        participantsExportButton.setEnabled(false);
        participantsResetButton.setEnabled(false);

        participantsSaveToggleButton.setEnabled(false);
        participantsSaveToggleButton.setSelected(false);
    }

    /**
     * This method groups together the often-used group of statements that
     * enable elements of the participants tab. This method disables settings
     * and qualification, as well.
     *
     * @author Zbyněk Stara
     */
    private void enableParticipantsTabActionBox() {
        this.disableSettingsTabActionBox();

        this.disableQualificationTabActionBox();

        this.updateOverallProgressBar(33);

        this.updateSchoolsListModel();

        settingsApproveToggleButton.setEnabled(true);
        settingsApproveToggleButton.setSelected(true);

        settingsSaveToggleButton.setEnabled(true);
        settingsSaveToggleButton.setSelected(false);

        participantsSchoolsTextField.setEnabled(true);
        participantsSchoolsAddButton.setEnabled(true);

        if (schoolsNumber > 0) {
            participantsSchoolsList.setEnabled(true);
            participantsSchoolsSearchButton.setEnabled(true);
        }

        if (teachersNumber > 0) {
            participantsTeachersTextField.setEnabled(true);
            participantsTeachersSearchButton.setEnabled(true);
        }

        if (studentsNumber > 0) {
            participantsStudentsTextField.setEnabled(true);
            participantsStudentsSearchButton.setEnabled(true);
        }

        recheckStudentCodesActionBox();

        if (studentCodes) {
            participantsStudentCodesTextField.setEnabled(true);
            participantsStudentCodesSearchButton.setEnabled(true);

            participantsApproveToggleButton.setEnabled(true);
        }
        if (studentChanges) {
            participantsStudentCodesAssignCodesButton.setEnabled(true);
        }

        participantsExportButton.setEnabled(true);
        participantsResetButton.setEnabled(true);

        participantsSaveToggleButton.setSelected(false);
        participantsSaveToggleButton.setEnabled(false);
    }

    /**
     * This method groups together the often-used group of statements that
     * disable elements of the qualification tab.
     *
     * @author Zbyněk Stara
     */
    private void disableQualificationTabActionBox() {
        qualificationEventsList.setModel(blankModel);
        qualificationEventsList.setEnabled(false);

        qualificationRoundsTextField.setText("");
        qualificationRoundsTextField.setEnabled(false);
        qualificationRoundsEditNameButton.setEnabled(false);

        qualificationRoomsList.setEnabled(false);
        //qualificationRoomList.setSelectedIndex(-1);
        qualificationRoomsList.setModel(blankModel);

        qualificationRoomsTextField.setText("");
        qualificationRoomsTextField.setEnabled(false);
        qualificationRoomsEditNameButton.setEnabled(false);

        qualificationJudgesList.setEnabled(false);
        //qualificationJudgesList.setSelectedIndex(-1);
        qualificationJudgesList.setModel(blankModel);

        qualificationJudgesSubstituteJudgeButton.setEnabled(false);

        qualificationStudentCodesList.setEnabled(false);
        //qualificationStudentCodesList.setSelectedIndex(-1);
        qualificationStudentCodesList.setModel(blankModel);

        qualificationStudentCodesTextField.setText("");
        qualificationStudentCodesTextField.setEnabled(false);
        qualificationStudentCodesSearchButton.setEnabled(false);

        qualificationExportButton.setEnabled(false);
        qualificationResetButton.setEnabled(false);

        qualificationSaveToggleButton.setEnabled(false);
        qualificationSaveToggleButton.setSelected(false);
    }

    /**
     * This method groups together the often-used group of statements that
     * enable elements of the qualification tab. This method disables settings
     * and participants, as well.
     *
     * @author Zbyněk Stara
     */
    private void enableQualificationTabActionBox() {
        this.disableSettingsTabActionBox();
        this.disableParticipantsTabActionBox();

        settingsApproveToggleButton.setEnabled(true);
        settingsApproveToggleButton.setSelected(true);

        settingsSaveToggleButton.setEnabled(true);
        settingsSaveToggleButton.setSelected(false);

        participantsApproveToggleButton.setEnabled(true);
        participantsApproveToggleButton.setSelected(true);

        participantsSaveToggleButton.setEnabled(true);
        participantsSaveToggleButton.setSelected(false);

        this.updateEventsListModel();

        qualificationRoundsList.setModel(noSelectionModel);
        qualificationRoomsList.setModel(noSelectionModel);
        qualificationJudgesList.setModel(noSelectionModel);
        qualificationStudentCodesList.setModel(noSelectionModel);

        qualificationEventsList.setEnabled(true);

        qualificationExportButton.setEnabled(true);
        qualificationResetButton.setEnabled(true);
        qualificationSaveToggleButton.setEnabled(true);

        this.updateOverallProgressBar(100);
    }

    // OTHER ACTION BOXES:
    /**
     * This method groups together the often-used group of statements that
     * automatically selects a school at a given index.
     *
     * @param schoolIndex the index of the school to be selected
     *
     * @author Zbyněk Stara
     */
    private void autoselectSchoolActionBox(int schoolIndex) {
        participantsSchoolsList.setEnabled(true);
        participantsSchoolsSearchButton.setEnabled(true);

        refreshSchoolActionBox(schoolIndex);
    }

    /**
     * This method groups together the often-used group of statements that
     * refreshes the school list (updates it and sets the selected school to be
     * the school at the specified index).
     *
     * @param schoolIndex the index of the school to be selected
     *
     * @author Zbyněk Stara
     */
    private void refreshSchoolActionBox(int schoolIndex) {
        this.updateSchoolsListModel();

        participantsSchoolsList.setSelectedIndex(schoolIndex);
        participantsSchoolsList.ensureIndexIsVisible(schoolIndex);
    }

    /**
     * This method groups together the often-used group of statements that
     * rechecks the current student codes status and enables/disables GUI
     * elements as necessary.
     *
     * @author Zbyněk Stara
     */
    private void recheckStudentCodesActionBox() {
        if (studentCodes && !studentChanges) {
            participantsStudentCodesTextField.setEnabled(true);
            participantsStudentCodesSearchButton.setEnabled(true);

            participantsApproveToggleButton.setEnabled(true);
        } else {
            participantsStudentCodesTextField.setEnabled(false);
            participantsStudentCodesSearchButton.setEnabled(false);

            participantsApproveToggleButton.setEnabled(false);
        }

        if (studentChanges && studentsNumber > 0) {
            participantsStudentCodesAssignCodesButton.setEnabled(true);
        } else {
            participantsStudentCodesAssignCodesButton.setEnabled(false);
        }
    }

    /**
     * This method groups together the often-used group of statements that
     * toggle the search-related GUI elements of students.
     *
     * @param state the boolean value specifying whether the elements should be
     * enabled or disabled
     *
     * @author Zbyněk Stara
     */
    private void toggleStudentSearchActionBox(boolean state) {
        participantsStudentsSearchButton.setEnabled(state);
        participantsStudentsTextField.setEnabled(state);
    }

    /**
     * This method groups together the often-used group of statements that
     * disables the student-codes-related GUI elements.
     * 
     * @author Zbyněk Stara
     */
    private void disableStudentCodesActionBox() {
        participantsStudentCodesTextField.setEnabled(false);
        participantsStudentCodesList.setEnabled(false);
        participantsStudentCodesSearchButton.setEnabled(false);
        participantsStudentCodesAssignCodesButton.setEnabled(false);
    }

    /**
     * This method groups together the often-used group of statements that
     * disables the assign-events-related GUI elements.
     *
     * @author Zbyněk Stara
     */
    private void disableAssignEventsActionBox() {
        participantsAssignEventsOriginalOratoryCheckBox.setEnabled(false);
        participantsAssignEventsOriginalOratoryCheckBox.setSelected(false);

        participantsAssignEventsOralInterpretationCheckBox.setEnabled(false);
        participantsAssignEventsOralInterpretationCheckBox.setSelected(false);

        participantsAssignEventsImpromptuSpeakingCheckBox.setEnabled(false);
        participantsAssignEventsImpromptuSpeakingCheckBox.setSelected(false);

        participantsAssignEventsDuetActingCheckBox.setEnabled(false);
        participantsAssignEventsDuetActingCheckBox.setSelected(false);

        participantsAssignEventsDebateCheckBox.setEnabled(false);
        participantsAssignEventsDebateCheckBox.setSelected(false);

        participantsAssignEventsAssignEventsButton.setEnabled(false);
    }

    /**
     * This method is called upon to export the participants tab into a
     * plaintext file. The user can then copy-paste the raw text and format it
     * as necessary for use in brochures and information materials for the
     * tournament
     *
     * @throws UserIOException if the user cancels the file chooser dialog
     * @throws IOException if a generic input/output exception has occurred
     *
     * @author Zbyněk Stara
     */
    private void exportParticipantsActionBox() throws UserIOException, IOException {
        JFileChooser jfc = new JFileChooser();

        int saveDialogReturn = jfc.showSaveDialog(this);

        if (saveDialogReturn == JFileChooser.APPROVE_OPTION) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(jfc.getSelectedFile()));

            for (int i = 0; i < database.getSchoolTreeSize(); i++) {
                School currentSchool = (School) database.getSchoolTreeNodeData(i);

                bw.write(currentSchool.getName() + "\n");

                for (int j = 0; j < currentSchool.getTeacherTreeSize(); j++) {
                    Teacher currentTeacher = (Teacher) currentSchool.getTeacherTreeNodeData(j);

                    bw.write("\t" + currentTeacher.getName() + "\n");
                }

                bw.write("\n");

                for (int j = 0; j < currentSchool.getStudentTreeSize(); j++) {
                    Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(j);

                    bw.write("\t" + currentStudent.getCode() + "\t" + currentStudent.getName() + "\n");
                }

                bw.write("\n");
            }

            bw.close();
        } else if (saveDialogReturn == JFileChooser.CANCEL_OPTION) {
            throw new UserIOException();
        } else {
            throw new IOException();
        }
    }

    /**
     * This method is called upon to export the qualification tab into a
     * plaintext file. The user can then copy-paste the raw text and format it
     * as necessary for use in brochures and information materials for the
     * tournament
     *
     * @throws UserIOException if the user cancels the file chooser dialog
     * @throws IOException if a generic input/output exception has occurred
     *
     * @author Zbyněk Stara
     */
    private void exportQualificationActionBox() throws UserIOException, IOException {
        JFileChooser jfc = new JFileChooser();

        int saveDialogReturn = jfc.showSaveDialog(this);

        if (saveDialogReturn == JFileChooser.APPROVE_OPTION) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(jfc.getSelectedFile()));

            for (int i = 0; i < qualification.getEventArrayLength(); i++) {
                Event currentEvent = qualification.getEventArrayElement(i);

                bw.write(currentEvent.type.getEventName() + "\n");

                for (int j = 0; j < currentEvent.getRoundArrayLength(); j++) {
                    Round currentRound = (Round) currentEvent.getRoundArrayElement(j);

                    if (currentRound != null) {
                        bw.write("\t" + currentRound.getName() + "\n");

                        for (int k = 0; k < currentRound.getRoomArrayLength(); k++) {
                            Room currentRoom = (Room) currentRound.getRoomArrayElement(k);

                            bw.write("\t\t" + currentRoom.getName() + "\n");

                            for (int l = 0; l < currentRoom.getJudgeTreeSize(); l++) {
                                Judge currentJudge = (Judge) currentRoom.getJudgeTreeNodeData(l);

                                bw.write("\t\t\t" + currentJudge.getName() + "\n");
                            }

                            bw.write("\n");

                            for (int l = 0; l < currentRoom.getEntityTreeSize(); l++) {
                                Entity currentEntity = (Entity) currentRoom.getEntityTreeNodeData(l);

                                bw.write("\t\t\t" + currentEntity.getCode() + "\n");
                            }

                            bw.write("\n");
                        }

                        bw.write("\n");
                    } else {
                        continue;
                    }
                }

                bw.write("\n");

            }

            bw.close();
        } else if (saveDialogReturn == JFileChooser.CANCEL_OPTION) {
            throw new UserIOException();
        } else {
            throw new IOException();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsAcceptDialog = new javax.swing.JDialog();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        settingsAcceptDialogConfirmButton = new javax.swing.JButton();
        settingsErrorDialog = new javax.swing.JDialog();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        settingsErrorDialogConfirmButton = new javax.swing.JButton();
        settingsErrorDialogScrollPane = new javax.swing.JScrollPane();
        settingsErrorDialogTextArea = new javax.swing.JTextArea();
        exceptionDialog = new javax.swing.JDialog();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        exceptionDialogConfirmButton = new javax.swing.JButton();
        exceptionDialogScrollPane = new javax.swing.JScrollPane();
        exceptionDialogTextArea = new javax.swing.JTextArea();
        illegalActionDialog = new javax.swing.JDialog();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        illegalActionDialogConfirmButton = new javax.swing.JButton();
        illegalActionDialogScrollPane = new javax.swing.JScrollPane();
        illegalActionDialogTextArea = new javax.swing.JTextArea();
        resetWarningDialog = new javax.swing.JDialog();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        resetWarningDialogConfirmButton = new javax.swing.JButton();
        resetWarningDialogScrollPane = new javax.swing.JScrollPane();
        resetWarningDialogTextArea = new javax.swing.JTextArea();
        resetWarningDialogCancelButton = new javax.swing.JButton();
        approveParticipantsDialog = new javax.swing.JDialog();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        approveParticipantsDialogIgnoreButton = new javax.swing.JButton();
        approveParticipantsDialogScrollPane = new javax.swing.JScrollPane();
        approveParticipantsDialogTextArea = new javax.swing.JTextArea();
        approveParticipantsDialogCancelButton = new javax.swing.JButton();
        allocationInfoDialog = new javax.swing.JDialog();
        jLabel73 = new javax.swing.JLabel();
        allocationInfoProgressBar = new javax.swing.JProgressBar();
        allocationInfoTextField = new javax.swing.JTextField();
        allocationInfoTimeTextField = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        participantsPane1 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        eventSettingsOOSR = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        eventSettingsOOSES = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        eventSettingsOOJR = new javax.swing.JTextField();
        settingsResetButton = new javax.swing.JButton();
        settingsLoadButton = new javax.swing.JButton();
        settingsApproveToggleButton = new javax.swing.JToggleButton();
        settingsSaveToggleButton = new javax.swing.JToggleButton();
        eventSettingsOOFinalSR = new javax.swing.JTextField();
        generalSettingsRoomsAvailable1 = new javax.swing.JTextField();
        generalSettingsMaximalTime1 = new javax.swing.JTextField();
        generalSettingsSchoolsNumber = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        eventSettingsDAFinalSR = new javax.swing.JTextField();
        generalSettingsSS = new javax.swing.JTextField();
        generalSettingsTS = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        eventSettingsOISR = new javax.swing.JTextField();
        eventSettingsISSR = new javax.swing.JTextField();
        eventSettingsDASR = new javax.swing.JTextField();
        eventSettingsOISES = new javax.swing.JTextField();
        eventSettingsISSES = new javax.swing.JTextField();
        eventSettingsDASES = new javax.swing.JTextField();
        eventSettingsDebateSES = new javax.swing.JTextField();
        eventSettingsOIJR = new javax.swing.JTextField();
        eventSettingsISJR = new javax.swing.JTextField();
        eventSettingsDAJR = new javax.swing.JTextField();
        eventSettingsFinalsJR = new javax.swing.JTextField();
        eventSettingsDebateJR = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        eventSettingsOIFinalSR = new javax.swing.JTextField();
        eventSettingsISFinalSR = new javax.swing.JTextField();
        generalSettingsRoomsAvailable2 = new javax.swing.JTextField();
        generalSettingsMaximalTime2 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        generalSettingsCEComboBox2 = new javax.swing.JComboBox();
        generalSettingsCEComboBox1 = new javax.swing.JComboBox();
        generalSettingsCECheckBox = new javax.swing.JCheckBox();
        participantsPane = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        participantsSchoolsTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        participantsSchoolsList = new javax.swing.JList();
        participantsSchoolsAddButton = new javax.swing.JButton();
        participantsSchoolsRemoveButton = new javax.swing.JButton();
        participantsSchoolsEditButton = new javax.swing.JButton();
        participantsSchoolsSearchButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        participantsTeachersTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        participantsTeachersList = new javax.swing.JList();
        participantsTeachersAddButton = new javax.swing.JButton();
        participantsTeachersEditButton = new javax.swing.JButton();
        participantsTeachersSearchButton = new javax.swing.JButton();
        participantsTeachersRemoveButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        participantsStudentsTextField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        participantsStudentsList = new javax.swing.JList();
        participantsStudentsAddButton = new javax.swing.JButton();
        participantsStudentsEditButton = new javax.swing.JButton();
        participantsStudentsSearchButton = new javax.swing.JButton();
        participantsStudentsRemoveButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        participantsStudentCodesTextField = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        participantsStudentCodesList = new javax.swing.JList();
        participantsStudentCodesSearchButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        participantsAssignEventsAssignEventsButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        participantsAssignPairsEventsList = new javax.swing.JList();
        participantsAssignPairsRemovePairButton = new javax.swing.JButton();
        participantsAssignPairsPairButton = new javax.swing.JButton();
        participantsResetButton = new javax.swing.JButton();
        participantsExportButton = new javax.swing.JButton();
        participantsApproveToggleButton = new javax.swing.JToggleButton();
        participantsSaveToggleButton = new javax.swing.JToggleButton();
        participantsAssignEventsOriginalOratoryCheckBox = new javax.swing.JCheckBox();
        participantsAssignEventsOralInterpretationCheckBox = new javax.swing.JCheckBox();
        participantsAssignEventsImpromptuSpeakingCheckBox = new javax.swing.JCheckBox();
        participantsAssignEventsDuetActingCheckBox = new javax.swing.JCheckBox();
        participantsAssignEventsDebateCheckBox = new javax.swing.JCheckBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        participantsAssignPairsLeftList = new javax.swing.JList();
        jScrollPane31 = new javax.swing.JScrollPane();
        participantsAssignPairsRightList = new javax.swing.JList();
        jScrollPane32 = new javax.swing.JScrollPane();
        participantsAssignPairsPairsList = new javax.swing.JList();
        participantsStudentCodesAssignCodesButton = new javax.swing.JButton();
        participantsProgressBar = new javax.swing.JProgressBar();
        qualificationPane = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        qualificationEventsList = new javax.swing.JList();
        qualificationProgressBar = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        qualificationRoundsTextField = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        qualificationRoundsList = new javax.swing.JList();
        qualificationRoundsEditNameButton = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        qualificationRoomsTextField = new javax.swing.JTextField();
        jScrollPane9 = new javax.swing.JScrollPane();
        qualificationRoomsList = new javax.swing.JList();
        qualificationRoomsEditNameButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        qualificationJudgesList = new javax.swing.JList();
        qualificationJudgesSubstituteJudgeButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        qualificationStudentCodesTextField = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        qualificationStudentCodesList = new javax.swing.JList();
        qualificationStudentCodesSearchButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        qualificationScoresTextField = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        qualificationScoresList = new javax.swing.JList();
        qualificationScoresAddButton = new javax.swing.JButton();
        qualificationScoresRemoveButton = new javax.swing.JButton();
        qualificationResetButton = new javax.swing.JButton();
        qualificationExportButton = new javax.swing.JButton();
        qualificationSaveToggleButton = new javax.swing.JToggleButton();
        qualificationApproveIncompleteToggleButton = new javax.swing.JToggleButton();
        qualificationApproveCompleteToggleButton = new javax.swing.JToggleButton();
        finalistsPane = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        finalistsOriginalOratoryList = new javax.swing.JList();
        finalistsProgressBar = new javax.swing.JProgressBar();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane14 = new javax.swing.JScrollPane();
        finalistsOralInterpretationList = new javax.swing.JList();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        finalistsImpromptuSpeakingList = new javax.swing.JList();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        finalistsDuetActingList = new javax.swing.JList();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        finalistsDebateSemifinalistsAList = new javax.swing.JList();
        finalistsDebateSemifinalistsSetWinnerAButton = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane18 = new javax.swing.JScrollPane();
        finalistsDebateFinalistsList = new javax.swing.JList();
        finalistsResetButton = new javax.swing.JButton();
        finalistsExportButton = new javax.swing.JButton();
        finalistsApproveToggleButton = new javax.swing.JToggleButton();
        finalistsSaveToggleButton = new javax.swing.JToggleButton();
        jScrollPane33 = new javax.swing.JScrollPane();
        finalistsDebateSemifinalistsBList = new javax.swing.JList();
        finalistsDebateSemifinalistsSetWinnerBButton = new javax.swing.JButton();
        finalsPane = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane19 = new javax.swing.JScrollPane();
        finalsEventList = new javax.swing.JList();
        finalsProgressBar = new javax.swing.JProgressBar();
        jLabel21 = new javax.swing.JLabel();
        finalsRoundTextField = new javax.swing.JTextField();
        jScrollPane20 = new javax.swing.JScrollPane();
        finalsRoundList = new javax.swing.JList();
        finalsRoundEditNameButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        finalsRoomTextField = new javax.swing.JTextField();
        jScrollPane21 = new javax.swing.JScrollPane();
        finalsRoomList = new javax.swing.JList();
        finalsRoomEditNameButton = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane22 = new javax.swing.JScrollPane();
        finalsJudgesList = new javax.swing.JList();
        finalsJudgesSubstituteJudgeButton = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        finalsStudentCodesTextField = new javax.swing.JTextField();
        jScrollPane23 = new javax.swing.JScrollPane();
        finalsStudentCodesList = new javax.swing.JList();
        finalsStudentCodesSearchButton = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        finalsRankingsTextField = new javax.swing.JTextField();
        jScrollPane24 = new javax.swing.JScrollPane();
        finalsRankingsList = new javax.swing.JList();
        finalsRankingsAddButton = new javax.swing.JButton();
        finalsRankingsRemoveButton = new javax.swing.JButton();
        finalsResetButton = new javax.swing.JButton();
        finalsExportButton = new javax.swing.JButton();
        finalsApproveToggleButton = new javax.swing.JToggleButton();
        finalsSaveToggleButton = new javax.swing.JToggleButton();
        jScrollPane34 = new javax.swing.JScrollPane();
        finalsStudentsVoteList = new javax.swing.JList();
        finalsStudentsVoteSetStudentsVoteButton = new javax.swing.JButton();
        winnersPane = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane26 = new javax.swing.JScrollPane();
        winnersOriginalOratoryFirstPlaceList = new javax.swing.JList();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        winnersSaveToggleButton = new javax.swing.JToggleButton();
        jScrollPane35 = new javax.swing.JScrollPane();
        winnersOriginalOratorySecondPlaceList = new javax.swing.JList();
        jScrollPane36 = new javax.swing.JScrollPane();
        winnersOriginalOratoryThirdPlaceList = new javax.swing.JList();
        jScrollPane37 = new javax.swing.JScrollPane();
        winnersOriginalOratoryStudentChoiceList = new javax.swing.JList();
        jScrollPane27 = new javax.swing.JScrollPane();
        winnersOralInterpretationFirstPlaceList = new javax.swing.JList();
        jScrollPane38 = new javax.swing.JScrollPane();
        winnersOralInterpretationSecondPlaceList = new javax.swing.JList();
        jScrollPane39 = new javax.swing.JScrollPane();
        winnersOralInterpretationThirdPlaceList = new javax.swing.JList();
        jScrollPane40 = new javax.swing.JScrollPane();
        winnersOralInterpretationStudentChoiceList = new javax.swing.JList();
        jScrollPane28 = new javax.swing.JScrollPane();
        winnersImpromptuSpeakingFirstPlaceList = new javax.swing.JList();
        jScrollPane41 = new javax.swing.JScrollPane();
        winnersImpromptuSpeakingSecondPlaceList = new javax.swing.JList();
        jScrollPane42 = new javax.swing.JScrollPane();
        winnersImpromptuSpeakingThirdPlaceList = new javax.swing.JList();
        jScrollPane43 = new javax.swing.JScrollPane();
        winnersImpromptuSpeakingStudentChoiceList = new javax.swing.JList();
        jScrollPane29 = new javax.swing.JScrollPane();
        winnersDuetActingFirstPlaceList = new javax.swing.JList();
        jScrollPane44 = new javax.swing.JScrollPane();
        winnersDuetActingSecondPlaceList = new javax.swing.JList();
        jScrollPane45 = new javax.swing.JScrollPane();
        winnersDuetActingThirdPlaceList = new javax.swing.JList();
        jScrollPane46 = new javax.swing.JScrollPane();
        winnersDuetActingStudentChoiceList = new javax.swing.JList();
        jScrollPane30 = new javax.swing.JScrollPane();
        winnersDebateFirstPlaceList = new javax.swing.JList();
        jScrollPane47 = new javax.swing.JScrollPane();
        winnersDebateSecondPlaceList = new javax.swing.JList();
        jScrollPane48 = new javax.swing.JScrollPane();
        winnersDebateThirdPlaceList = new javax.swing.JList();
        jScrollPane49 = new javax.swing.JScrollPane();
        winnersDebateStudentChoiceList = new javax.swing.JList();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        winnersExportButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        overallProgressBar = new javax.swing.JProgressBar();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        settingsAcceptDialog.setTitle("Information");
        settingsAcceptDialog.setAlwaysOnTop(true);
        settingsAcceptDialog.setIconImage(null);
        settingsAcceptDialog.setLocationByPlatform(true);
        settingsAcceptDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        settingsAcceptDialog.setResizable(false);
        settingsAcceptDialog.setSize(new java.awt.Dimension(411, 146));

        jLabel61.setText("No errors were found while checking the values.");

        jLabel62.setText("You are free to proceed to the next section of the program.");

        settingsAcceptDialogConfirmButton.setText("Confirm");
        settingsAcceptDialogConfirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                settingsAcceptDialogConfirmButtonMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout settingsAcceptDialogLayout = new org.jdesktop.layout.GroupLayout(settingsAcceptDialog.getContentPane());
        settingsAcceptDialog.getContentPane().setLayout(settingsAcceptDialogLayout);
        settingsAcceptDialogLayout.setHorizontalGroup(
            settingsAcceptDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsAcceptDialogLayout.createSequentialGroup()
                .add(settingsAcceptDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(settingsAcceptDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(settingsAcceptDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel62))
                    .add(settingsAcceptDialogLayout.createSequentialGroup()
                        .add(155, 155, 155)
                        .add(settingsAcceptDialogConfirmButton)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        settingsAcceptDialogLayout.setVerticalGroup(
            settingsAcceptDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsAcceptDialogLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jLabel61)
                .add(8, 8, 8)
                .add(jLabel62)
                .add(18, 18, 18)
                .add(settingsAcceptDialogConfirmButton))
        );

        settingsErrorDialog.setTitle("Error");
        settingsErrorDialog.setAlwaysOnTop(true);
        settingsErrorDialog.setLocationByPlatform(true);
        settingsErrorDialog.setMinimumSize(new java.awt.Dimension(411, 325));
        settingsErrorDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        settingsErrorDialog.setResizable(false);

        jLabel63.setText("There are errors in some of the settings.");

        jLabel64.setText("These need to be corrected before being able to proceed:");

        settingsErrorDialogConfirmButton.setText("Confirm");
        settingsErrorDialogConfirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                settingsErrorDialogConfirmButtonMouseReleased(evt);
            }
        });

        settingsErrorDialogTextArea.setColumns(20);
        settingsErrorDialogTextArea.setEditable(false);
        settingsErrorDialogTextArea.setLineWrap(true);
        settingsErrorDialogTextArea.setRows(5);
        settingsErrorDialogTextArea.setWrapStyleWord(true);
        settingsErrorDialogScrollPane.setViewportView(settingsErrorDialogTextArea);

        org.jdesktop.layout.GroupLayout settingsErrorDialogLayout = new org.jdesktop.layout.GroupLayout(settingsErrorDialog.getContentPane());
        settingsErrorDialog.getContentPane().setLayout(settingsErrorDialogLayout);
        settingsErrorDialogLayout.setHorizontalGroup(
            settingsErrorDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsErrorDialogLayout.createSequentialGroup()
                .add(settingsErrorDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(settingsErrorDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(settingsErrorDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(settingsErrorDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(settingsErrorDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(settingsErrorDialogLayout.createSequentialGroup()
                        .add(157, 157, 157)
                        .add(settingsErrorDialogConfirmButton)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        settingsErrorDialogLayout.setVerticalGroup(
            settingsErrorDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsErrorDialogLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jLabel63)
                .add(8, 8, 8)
                .add(jLabel64)
                .add(18, 18, 18)
                .add(settingsErrorDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(settingsErrorDialogConfirmButton)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        exceptionDialog.setTitle("Exception");
        exceptionDialog.setAlwaysOnTop(true);
        exceptionDialog.setLocationByPlatform(true);
        exceptionDialog.setMinimumSize(new java.awt.Dimension(411, 325));
        exceptionDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        exceptionDialog.setResizable(false);

        jLabel65.setText("An unexpected exception occurred.");

        jLabel66.setText("Specification and tips for resolution are shown below:");

        exceptionDialogConfirmButton.setText("Confirm");
        exceptionDialogConfirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                exceptionDialogConfirmButtonMouseReleased(evt);
            }
        });

        exceptionDialogTextArea.setColumns(20);
        exceptionDialogTextArea.setEditable(false);
        exceptionDialogTextArea.setLineWrap(true);
        exceptionDialogTextArea.setRows(5);
        exceptionDialogTextArea.setWrapStyleWord(true);
        exceptionDialogScrollPane.setViewportView(exceptionDialogTextArea);

        org.jdesktop.layout.GroupLayout exceptionDialogLayout = new org.jdesktop.layout.GroupLayout(exceptionDialog.getContentPane());
        exceptionDialog.getContentPane().setLayout(exceptionDialogLayout);
        exceptionDialogLayout.setHorizontalGroup(
            exceptionDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(exceptionDialogLayout.createSequentialGroup()
                .add(exceptionDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(exceptionDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(exceptionDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(exceptionDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(exceptionDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(exceptionDialogLayout.createSequentialGroup()
                        .add(157, 157, 157)
                        .add(exceptionDialogConfirmButton)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        exceptionDialogLayout.setVerticalGroup(
            exceptionDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(exceptionDialogLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jLabel65)
                .add(8, 8, 8)
                .add(jLabel66)
                .add(18, 18, 18)
                .add(exceptionDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(exceptionDialogConfirmButton)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        illegalActionDialog.setTitle("Illegal Action");
        illegalActionDialog.setAlwaysOnTop(true);
        illegalActionDialog.setLocationByPlatform(true);
        illegalActionDialog.setMinimumSize(new java.awt.Dimension(411, 325));
        illegalActionDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        illegalActionDialog.setResizable(false);

        jLabel67.setText("Sorry, you cannot do that.");

        jLabel68.setText("Details and tips on how to proceed are below.");

        illegalActionDialogConfirmButton.setText("Confirm");
        illegalActionDialogConfirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                illegalActionDialogConfirmButtonMouseReleased(evt);
            }
        });

        illegalActionDialogTextArea.setColumns(20);
        illegalActionDialogTextArea.setEditable(false);
        illegalActionDialogTextArea.setLineWrap(true);
        illegalActionDialogTextArea.setRows(5);
        illegalActionDialogTextArea.setWrapStyleWord(true);
        illegalActionDialogScrollPane.setViewportView(illegalActionDialogTextArea);

        org.jdesktop.layout.GroupLayout illegalActionDialogLayout = new org.jdesktop.layout.GroupLayout(illegalActionDialog.getContentPane());
        illegalActionDialog.getContentPane().setLayout(illegalActionDialogLayout);
        illegalActionDialogLayout.setHorizontalGroup(
            illegalActionDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(illegalActionDialogLayout.createSequentialGroup()
                .add(illegalActionDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(illegalActionDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(illegalActionDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(illegalActionDialogLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(illegalActionDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(illegalActionDialogLayout.createSequentialGroup()
                        .add(157, 157, 157)
                        .add(illegalActionDialogConfirmButton)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        illegalActionDialogLayout.setVerticalGroup(
            illegalActionDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(illegalActionDialogLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jLabel67)
                .add(8, 8, 8)
                .add(jLabel68)
                .add(18, 18, 18)
                .add(illegalActionDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(illegalActionDialogConfirmButton)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        resetWarningDialog.setTitle("Warning");
        resetWarningDialog.setAlwaysOnTop(true);
        resetWarningDialog.setLocationByPlatform(true);
        resetWarningDialog.setMinimumSize(new java.awt.Dimension(411, 325));
        resetWarningDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        resetWarningDialog.setResizable(false);

        jLabel69.setText("Warning: your action needs confirmation.");

        jLabel70.setText("Please read the following information:");

        resetWarningDialogConfirmButton.setText("Confirm");
        resetWarningDialogConfirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resetWarningDialogConfirmButtonMouseReleased(evt);
            }
        });

        resetWarningDialogTextArea.setColumns(20);
        resetWarningDialogTextArea.setEditable(false);
        resetWarningDialogTextArea.setLineWrap(true);
        resetWarningDialogTextArea.setRows(5);
        resetWarningDialogTextArea.setWrapStyleWord(true);
        resetWarningDialogScrollPane.setViewportView(resetWarningDialogTextArea);

        resetWarningDialogCancelButton.setText("Cancel");
        resetWarningDialogCancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resetWarningDialogCancelButtonMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout resetWarningDialogLayout = new org.jdesktop.layout.GroupLayout(resetWarningDialog.getContentPane());
        resetWarningDialog.getContentPane().setLayout(resetWarningDialogLayout);
        resetWarningDialogLayout.setHorizontalGroup(
            resetWarningDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(resetWarningDialogLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(resetWarningDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(resetWarningDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(resetWarningDialogLayout.createSequentialGroup()
                        .add(resetWarningDialogCancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(resetWarningDialogConfirmButton)))
                .add(20, 20, 20))
        );
        resetWarningDialogLayout.setVerticalGroup(
            resetWarningDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(resetWarningDialogLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jLabel69)
                .add(8, 8, 8)
                .add(jLabel70)
                .add(18, 18, 18)
                .add(resetWarningDialogScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(resetWarningDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(resetWarningDialogCancelButton)
                    .add(resetWarningDialogConfirmButton)))
        );

        approveParticipantsDialog.setTitle("Warning");
        approveParticipantsDialog.setAlwaysOnTop(true);
        approveParticipantsDialog.setLocationByPlatform(true);
        approveParticipantsDialog.setMinimumSize(new java.awt.Dimension(411, 325));
        approveParticipantsDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        approveParticipantsDialog.setResizable(false);
        approveParticipantsDialog.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel71.setText("Warning: some settings were not fulfilled for some entries.");
        approveParticipantsDialog.getContentPane().add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 371, -1));

        jLabel72.setText("Please read the following information:");
        approveParticipantsDialog.getContentPane().add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 44, 371, -1));

        approveParticipantsDialogIgnoreButton.setText("Ignore");
        approveParticipantsDialogIgnoreButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                approveParticipantsDialogIgnoreButtonMouseReleased(evt);
            }
        });
        approveParticipantsDialog.getContentPane().add(approveParticipantsDialogIgnoreButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(307, 257, -1, -1));

        approveParticipantsDialogTextArea.setColumns(20);
        approveParticipantsDialogTextArea.setEditable(false);
        approveParticipantsDialogTextArea.setLineWrap(true);
        approveParticipantsDialogTextArea.setRows(5);
        approveParticipantsDialogTextArea.setWrapStyleWord(true);
        approveParticipantsDialogScrollPane.setViewportView(approveParticipantsDialogTextArea);

        approveParticipantsDialog.getContentPane().add(approveParticipantsDialogScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 78, 371, 161));

        approveParticipantsDialogCancelButton.setText("Cancel");
        approveParticipantsDialogCancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                approveParticipantsDialogCancelButtonMouseReleased(evt);
            }
        });
        approveParticipantsDialog.getContentPane().add(approveParticipantsDialogCancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 257, -1, -1));

        allocationInfoDialog.setTitle("Information");
        allocationInfoDialog.setAlwaysOnTop(true);
        allocationInfoDialog.setIconImage(null);
        allocationInfoDialog.setLocationByPlatform(true);
        allocationInfoDialog.setMinimumSize(new java.awt.Dimension(411, 186));
        allocationInfoDialog.setModal(true);
        allocationInfoDialog.setPreferredSize(new java.awt.Dimension(735, 186));

        jLabel73.setText("Allocation in progress, please wait... Current situation:");

        allocationInfoProgressBar.setStringPainted(true);

        allocationInfoTextField.setEditable(false);
        allocationInfoTextField.setFont(new java.awt.Font("Courier", 0, 13)); // NOI18N
        allocationInfoTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        allocationInfoTextField.setAutoscrolls(false);
        allocationInfoTextField.setDragEnabled(false);
        allocationInfoTextField.setFocusTraversalKeysEnabled(false);
        allocationInfoTextField.setFocusable(false);
        allocationInfoTextField.setRequestFocusEnabled(false);

        allocationInfoTimeTextField.setEditable(false);
        allocationInfoTimeTextField.setFont(new java.awt.Font("Courier", 0, 13)); // NOI18N
        allocationInfoTimeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel74.setText("Time elapsed:");

        jLabel75.setText("seconds");

        org.jdesktop.layout.GroupLayout allocationInfoDialogLayout = new org.jdesktop.layout.GroupLayout(allocationInfoDialog.getContentPane());
        allocationInfoDialog.getContentPane().setLayout(allocationInfoDialogLayout);
        allocationInfoDialogLayout.setHorizontalGroup(
            allocationInfoDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, allocationInfoDialogLayout.createSequentialGroup()
                .add(allocationInfoDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(allocationInfoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(allocationInfoProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, allocationInfoDialogLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(allocationInfoTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, allocationInfoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel73, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, allocationInfoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel74)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(allocationInfoTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel75)))
                .addContainerGap())
        );
        allocationInfoDialogLayout.setVerticalGroup(
            allocationInfoDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(allocationInfoDialogLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(allocationInfoTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(allocationInfoProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(allocationInfoDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel74)
                    .add(allocationInfoTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel75))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NESDA Tournament Manager 2013");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(1180, 678));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        participantsPane1.setEnabled(false);
        participantsPane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel37.setText("Students / Room");
        participantsPane1.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 20, -1, -1));

        eventSettingsOOSR.setText("6");
        eventSettingsOOSR.setEnabled(false);
        participantsPane1.add(eventSettingsOOSR, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 44, 174, -1));

        jLabel38.setText("Students / Event / School");
        participantsPane1.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 20, -1, -1));

        eventSettingsOOSES.setText("4");
        eventSettingsOOSES.setEnabled(false);
        participantsPane1.add(eventSettingsOOSES, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 44, 174, -1));

        jLabel39.setText("Judges / Room");
        participantsPane1.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 20, -1, -1));

        eventSettingsOOJR.setText("2");
        eventSettingsOOJR.setEnabled(false);
        participantsPane1.add(eventSettingsOOJR, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 44, 174, -1));

        settingsResetButton.setText("Reset");
        settingsResetButton.setEnabled(false);
        settingsResetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                settingsResetButtonMouseReleased(evt);
            }
        });
        participantsPane1.add(settingsResetButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 344, 358, -1));

        settingsLoadButton.setText("Load");
        settingsLoadButton.setEnabled(false);
        settingsLoadButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                settingsLoadButtonMouseReleased(evt);
            }
        });
        participantsPane1.add(settingsLoadButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 344, 358, -1));

        settingsApproveToggleButton.setSelected(true);
        settingsApproveToggleButton.setText("Approve");
        settingsApproveToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                settingsApproveToggleButtonMouseReleased(evt);
            }
        });
        participantsPane1.add(settingsApproveToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 344, 358, -1));

        settingsSaveToggleButton.setText("Save");
        settingsSaveToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                settingsSaveToggleButtonMouseReleased(evt);
            }
        });
        participantsPane1.add(settingsSaveToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 377, 1094, -1));

        eventSettingsOOFinalSR.setText("6");
        eventSettingsOOFinalSR.setEnabled(false);
        participantsPane1.add(eventSettingsOOFinalSR, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 224, -1, -1));

        generalSettingsRoomsAvailable1.setText("8");
        generalSettingsRoomsAvailable1.setEnabled(false);
        participantsPane1.add(generalSettingsRoomsAvailable1, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 44, 81, -1));

        generalSettingsMaximalTime1.setText("8");
        generalSettingsMaximalTime1.setEnabled(false);
        participantsPane1.add(generalSettingsMaximalTime1, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 80, 34, -1));

        generalSettingsSchoolsNumber.setText("12");
        generalSettingsSchoolsNumber.setEnabled(false);
        participantsPane1.add(generalSettingsSchoolsNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 116, 174, -1));

        jLabel42.setText("Original Oratory");
        participantsPane1.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 174, -1));

        jLabel43.setText("Oral Interpretation");
        participantsPane1.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 86, 174, -1));

        jLabel44.setText("Impromptu Speaking");
        participantsPane1.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 122, 174, -1));

        jLabel45.setText("Duet Acting");
        participantsPane1.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 158, 174, -1));

        jLabel46.setText("Debate (Q & semifinals)");
        participantsPane1.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 194, 174, -1));

        jLabel47.setText("Finals (OO, OI, IS, DA)");
        participantsPane1.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 174, -1));

        jLabel48.setText("Rooms available (Day 1, 2)");
        participantsPane1.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 50, 174, -1));

        jLabel49.setText("Tournament time (Day 1, 2)");
        participantsPane1.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 86, 174, -1));

        jLabel50.setText("Schools in tournament");
        participantsPane1.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 122, -1, -1));

        jLabel51.setText("Students / School");
        participantsPane1.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 194, 121, -1));

        jLabel52.setText("Teachers / School");
        participantsPane1.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 158, 121, -1));

        jLabel53.setText("Combined Events");
        participantsPane1.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 230, 174, -1));

        jLabel54.setText("Event settings:");
        participantsPane1.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 103, -1));

        jLabel55.setText("General settings:");
        participantsPane1.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 20, 124, -1));

        eventSettingsDAFinalSR.setText("4");
        eventSettingsDAFinalSR.setEnabled(false);
        participantsPane1.add(eventSettingsDAFinalSR, new org.netbeans.lib.awtextra.AbsoluteConstraints(312, 224, -1, -1));

        generalSettingsSS.setText("12");
        generalSettingsSS.setEnabled(false);
        participantsPane1.add(generalSettingsSS, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 188, 174, -1));

        generalSettingsTS.setText("2");
        generalSettingsTS.setEnabled(false);
        participantsPane1.add(generalSettingsTS, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 152, 174, -1));

        jLabel57.setText("hours");
        participantsPane1.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(984, 86, 38, -1));

        eventSettingsOISR.setText("6");
        eventSettingsOISR.setEnabled(false);
        participantsPane1.add(eventSettingsOISR, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 80, 174, -1));

        eventSettingsISSR.setText("6");
        eventSettingsISSR.setEnabled(false);
        participantsPane1.add(eventSettingsISSR, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 116, 174, -1));

        eventSettingsDASR.setText("4");
        eventSettingsDASR.setEnabled(false);
        participantsPane1.add(eventSettingsDASR, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 152, 132, -1));

        eventSettingsOISES.setText("4");
        eventSettingsOISES.setEnabled(false);
        participantsPane1.add(eventSettingsOISES, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 80, 174, -1));

        eventSettingsISSES.setText("4");
        eventSettingsISSES.setEnabled(false);
        participantsPane1.add(eventSettingsISSES, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 116, 174, -1));

        eventSettingsDASES.setText("2");
        eventSettingsDASES.setEnabled(false);
        participantsPane1.add(eventSettingsDASES, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 152, 132, -1));

        eventSettingsDebateSES.setText("2");
        eventSettingsDebateSES.setEnabled(false);
        participantsPane1.add(eventSettingsDebateSES, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 188, 132, -1));

        eventSettingsOIJR.setText("2");
        eventSettingsOIJR.setEnabled(false);
        participantsPane1.add(eventSettingsOIJR, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 80, 174, -1));

        eventSettingsISJR.setText("2");
        eventSettingsISJR.setEnabled(false);
        participantsPane1.add(eventSettingsISJR, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 116, 174, -1));

        eventSettingsDAJR.setText("2");
        eventSettingsDAJR.setEnabled(false);
        participantsPane1.add(eventSettingsDAJR, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 152, 174, -1));

        eventSettingsFinalsJR.setText("5");
        eventSettingsFinalsJR.setEnabled(false);
        participantsPane1.add(eventSettingsFinalsJR, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 224, 174, -1));

        eventSettingsDebateJR.setText("3");
        eventSettingsDebateJR.setEnabled(false);
        participantsPane1.add(eventSettingsDebateJR, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 188, 174, -1));

        jLabel36.setText("pairs");
        participantsPane1.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(347, 230, -1, -1));

        jLabel56.setText("pairs");
        participantsPane1.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(347, 158, -1, -1));

        jLabel58.setText("pairs");
        participantsPane1.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(531, 158, -1, -1));

        jLabel59.setText("pairs");
        participantsPane1.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(531, 194, -1, -1));

        eventSettingsOIFinalSR.setText("6");
        eventSettingsOIFinalSR.setEnabled(false);
        participantsPane1.add(eventSettingsOIFinalSR, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 224, -1, -1));

        eventSettingsISFinalSR.setText("6");
        eventSettingsISFinalSR.setEnabled(false);
        participantsPane1.add(eventSettingsISFinalSR, new org.netbeans.lib.awtextra.AbsoluteConstraints(276, 224, -1, -1));

        generalSettingsRoomsAvailable2.setText("8");
        generalSettingsRoomsAvailable2.setEnabled(false);
        participantsPane1.add(generalSettingsRoomsAvailable2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1032, 44, 82, -1));

        generalSettingsMaximalTime2.setText("8");
        generalSettingsMaximalTime2.setEnabled(false);
        participantsPane1.add(generalSettingsMaximalTime2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1032, 80, 34, -1));

        jLabel60.setText("hours");
        participantsPane1.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(1076, 86, 38, -1));

        generalSettingsCEComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OO", "OI", "IS", "DA" }));
        generalSettingsCEComboBox2.setSelectedIndex(3);
        generalSettingsCEComboBox2.setEnabled(false);
        generalSettingsCEComboBox2.setMinimumSize(new java.awt.Dimension(58, 27));
        generalSettingsCEComboBox2.setPreferredSize(new java.awt.Dimension(68, 27));
        participantsPane1.add(generalSettingsCEComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1044, 224, 70, -1));

        generalSettingsCEComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "OO", "OI", "IS", "DA" }));
        generalSettingsCEComboBox1.setSelectedIndex(2);
        generalSettingsCEComboBox1.setEnabled(false);
        generalSettingsCEComboBox1.setMinimumSize(new java.awt.Dimension(58, 27));
        generalSettingsCEComboBox1.setPreferredSize(new java.awt.Dimension(58, 27));
        participantsPane1.add(generalSettingsCEComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(972, 224, 70, -1));

        generalSettingsCECheckBox.setSelected(true);
        generalSettingsCECheckBox.setEnabled(false);
        participantsPane1.add(generalSettingsCECheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 226, -1, -1));

        tabbedPane.addTab("Settings", participantsPane1);

        participantsPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Schools");
        participantsPane.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 174, -1));
        participantsPane.add(participantsSchoolsTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 44, 174, -1));

        participantsSchoolsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsSchoolsList.setEnabled(false);
        participantsSchoolsList.setFocusable(false);
        participantsSchoolsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsSchoolsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(participantsSchoolsList);

        participantsPane.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 174, 191));

        participantsSchoolsAddButton.setText("Add");
        participantsSchoolsAddButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsSchoolsAddButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsSchoolsAddButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 278, -1, -1));

        participantsSchoolsRemoveButton.setText("Remove");
        participantsSchoolsRemoveButton.setEnabled(false);
        participantsSchoolsRemoveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsSchoolsRemoveButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsSchoolsRemoveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 278, -1, -1));

        participantsSchoolsEditButton.setText("Edit");
        participantsSchoolsEditButton.setEnabled(false);
        participantsSchoolsEditButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsSchoolsEditButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsSchoolsEditButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 311, -1, -1));

        participantsSchoolsSearchButton.setText("Search");
        participantsSchoolsSearchButton.setEnabled(false);
        participantsSchoolsSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsSchoolsSearchButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsSchoolsSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 311, 93, -1));

        jLabel3.setText("Teachers");
        participantsPane.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 20, 174, -1));

        participantsTeachersTextField.setEnabled(false);
        participantsPane.add(participantsTeachersTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 44, 174, -1));

        participantsTeachersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsTeachersList.setEnabled(false);
        participantsTeachersList.setFocusable(false);
        participantsTeachersList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsTeachersListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(participantsTeachersList);

        participantsPane.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 80, 174, 191));

        participantsTeachersAddButton.setText("Add");
        participantsTeachersAddButton.setEnabled(false);
        participantsTeachersAddButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsTeachersAddButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsTeachersAddButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 278, -1, -1));

        participantsTeachersEditButton.setText("Edit");
        participantsTeachersEditButton.setEnabled(false);
        participantsTeachersEditButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsTeachersEditButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsTeachersEditButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 311, -1, -1));

        participantsTeachersSearchButton.setText("Search");
        participantsTeachersSearchButton.setEnabled(false);
        participantsTeachersSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsTeachersSearchButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsTeachersSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 311, 93, -1));

        participantsTeachersRemoveButton.setText("Remove");
        participantsTeachersRemoveButton.setEnabled(false);
        participantsTeachersRemoveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsTeachersRemoveButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsTeachersRemoveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 278, -1, -1));

        jLabel4.setText("Students");
        participantsPane.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 20, 174, -1));

        participantsStudentsTextField.setEnabled(false);
        participantsPane.add(participantsStudentsTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 44, 174, -1));

        participantsStudentsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsStudentsList.setEnabled(false);
        participantsStudentsList.setFocusable(false);
        participantsStudentsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsStudentsListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(participantsStudentsList);

        participantsPane.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 80, 174, 191));

        participantsStudentsAddButton.setText("Add");
        participantsStudentsAddButton.setEnabled(false);
        participantsStudentsAddButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsStudentsAddButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsStudentsAddButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 278, -1, -1));

        participantsStudentsEditButton.setText("Edit");
        participantsStudentsEditButton.setEnabled(false);
        participantsStudentsEditButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsStudentsEditButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsStudentsEditButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 311, -1, -1));

        participantsStudentsSearchButton.setText("Search");
        participantsStudentsSearchButton.setEnabled(false);
        participantsStudentsSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsStudentsSearchButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsStudentsSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(469, 311, 93, -1));

        participantsStudentsRemoveButton.setText("Remove");
        participantsStudentsRemoveButton.setEnabled(false);
        participantsStudentsRemoveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsStudentsRemoveButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsStudentsRemoveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(469, 278, -1, -1));

        jLabel5.setText("Student codes");
        participantsPane.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 20, 174, -1));

        participantsStudentCodesTextField.setEnabled(false);
        participantsPane.add(participantsStudentCodesTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 44, 174, -1));

        participantsStudentCodesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsStudentCodesList.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        participantsStudentCodesList.setEnabled(false);
        participantsStudentCodesList.setFocusable(false);
        participantsStudentCodesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsStudentCodesListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(participantsStudentCodesList);

        participantsPane.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 80, 174, 191));

        participantsStudentCodesSearchButton.setText("Search");
        participantsStudentCodesSearchButton.setEnabled(false);
        participantsStudentCodesSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsStudentCodesSearchButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsStudentCodesSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 311, 174, -1));

        jLabel6.setText("Assign events");
        participantsPane.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 20, 174, -1));

        participantsAssignEventsAssignEventsButton.setText("Assign events");
        participantsAssignEventsAssignEventsButton.setEnabled(false);
        participantsAssignEventsAssignEventsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignEventsAssignEventsButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignEventsAssignEventsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 278, 174, -1));

        jLabel7.setText("Assign pairs");
        participantsPane.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 20, 174, -1));

        participantsAssignPairsEventsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Duet Acting", "Debate" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        participantsAssignPairsEventsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsAssignPairsEventsList.setEnabled(false);
        participantsAssignPairsEventsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsAssignPairsEventsListValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(participantsAssignPairsEventsList);

        participantsPane.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 80, 174, 38));

        participantsAssignPairsRemovePairButton.setText("Remove pair");
        participantsAssignPairsRemovePairButton.setEnabled(false);
        participantsAssignPairsRemovePairButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignPairsRemovePairButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignPairsRemovePairButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 311, 174, -1));

        participantsAssignPairsPairButton.setText("Pair");
        participantsAssignPairsPairButton.setEnabled(false);
        participantsAssignPairsPairButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignPairsPairButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignPairsPairButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 278, 174, -1));

        participantsResetButton.setText("Reset");
        participantsResetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsResetButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsResetButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 344, 358, -1));

        participantsExportButton.setText("Export");
        participantsExportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsExportButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsExportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 344, 358, -1));

        participantsApproveToggleButton.setText("Approve");
        participantsApproveToggleButton.setEnabled(false);
        participantsApproveToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsApproveToggleButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsApproveToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 344, 358, -1));

        participantsSaveToggleButton.setText("Save");
        participantsSaveToggleButton.setEnabled(false);
        participantsSaveToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsSaveToggleButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsSaveToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 377, 1094, -1));

        participantsAssignEventsOriginalOratoryCheckBox.setText("Original Oratory");
        participantsAssignEventsOriginalOratoryCheckBox.setEnabled(false);
        participantsAssignEventsOriginalOratoryCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignEventsOriginalOratoryCheckBoxMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignEventsOriginalOratoryCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 146, 174, -1));

        participantsAssignEventsOralInterpretationCheckBox.setText("Oral Interpretation");
        participantsAssignEventsOralInterpretationCheckBox.setEnabled(false);
        participantsAssignEventsOralInterpretationCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignEventsOralInterpretationCheckBoxMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignEventsOralInterpretationCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 171, 174, -1));

        participantsAssignEventsImpromptuSpeakingCheckBox.setText("Impromptu Speaking");
        participantsAssignEventsImpromptuSpeakingCheckBox.setEnabled(false);
        participantsAssignEventsImpromptuSpeakingCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignEventsImpromptuSpeakingCheckBoxMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignEventsImpromptuSpeakingCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 196, -1, -1));

        participantsAssignEventsDuetActingCheckBox.setText("Duet Acting");
        participantsAssignEventsDuetActingCheckBox.setEnabled(false);
        participantsAssignEventsDuetActingCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignEventsDuetActingCheckBoxMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignEventsDuetActingCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 221, -1, -1));

        participantsAssignEventsDebateCheckBox.setText("Debate");
        participantsAssignEventsDebateCheckBox.setEnabled(false);
        participantsAssignEventsDebateCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsAssignEventsDebateCheckBoxMouseReleased(evt);
            }
        });
        participantsPane.add(participantsAssignEventsDebateCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 246, -1, -1));

        participantsAssignPairsLeftList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsAssignPairsLeftList.setEnabled(false);
        participantsAssignPairsLeftList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsAssignPairsLeftListValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(participantsAssignPairsLeftList);

        participantsPane.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 126, 80, 72));

        participantsAssignPairsRightList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsAssignPairsRightList.setEnabled(false);
        participantsAssignPairsRightList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsAssignPairsRightListValueChanged(evt);
            }
        });
        jScrollPane31.setViewportView(participantsAssignPairsRightList);

        participantsPane.add(jScrollPane31, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 126, 84, 72));

        participantsAssignPairsPairsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        participantsAssignPairsPairsList.setEnabled(false);
        participantsAssignPairsPairsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                participantsAssignPairsPairsListValueChanged(evt);
            }
        });
        jScrollPane32.setViewportView(participantsAssignPairsPairsList);

        participantsPane.add(jScrollPane32, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 206, 174, 65));

        participantsStudentCodesAssignCodesButton.setText("Assign codes");
        participantsStudentCodesAssignCodesButton.setEnabled(false);
        participantsStudentCodesAssignCodesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                participantsStudentCodesAssignCodesButtonMouseReleased(evt);
            }
        });
        participantsPane.add(participantsStudentCodesAssignCodesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 278, 174, -1));

        participantsProgressBar.setString("Progress");
        participantsProgressBar.setStringPainted(true);
        participantsPane.add(participantsProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 415, 1094, -1));

        tabbedPane.addTab("Participants", participantsPane);

        qualificationPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("Events");
        qualificationPane.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 174, -1));

        qualificationEventsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        qualificationEventsList.setEnabled(false);
        qualificationEventsList.setFocusable(false);
        qualificationEventsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                qualificationEventsListValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(qualificationEventsList);

        qualificationPane.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 174, 191));

        qualificationProgressBar.setString("Progress");
        qualificationProgressBar.setStringPainted(true);
        qualificationPane.add(qualificationProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 415, 1094, -1));

        jLabel9.setText("Rounds");
        qualificationPane.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 20, 174, -1));

        qualificationRoundsTextField.setEnabled(false);
        qualificationPane.add(qualificationRoundsTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 44, 174, -1));

        qualificationRoundsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        qualificationRoundsList.setEnabled(false);
        qualificationRoundsList.setFocusable(false);
        qualificationRoundsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                qualificationRoundsListValueChanged(evt);
            }
        });
        jScrollPane8.setViewportView(qualificationRoundsList);

        qualificationPane.add(jScrollPane8, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 80, 174, 191));

        qualificationRoundsEditNameButton.setText("Edit name");
        qualificationRoundsEditNameButton.setEnabled(false);
        qualificationRoundsEditNameButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                qualificationRoundsEditNameButtonMouseReleased(evt);
            }
        });
        qualificationPane.add(qualificationRoundsEditNameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 278, 174, -1));

        jLabel10.setText("Rooms");
        qualificationPane.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 20, 174, -1));

        qualificationRoomsTextField.setEnabled(false);
        qualificationPane.add(qualificationRoomsTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 44, 174, -1));

        qualificationRoomsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        qualificationRoomsList.setEnabled(false);
        qualificationRoomsList.setFocusable(false);
        qualificationRoomsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                qualificationRoomsListValueChanged(evt);
            }
        });
        jScrollPane9.setViewportView(qualificationRoomsList);

        qualificationPane.add(jScrollPane9, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 80, 174, 191));

        qualificationRoomsEditNameButton.setText("Edit name");
        qualificationRoomsEditNameButton.setEnabled(false);
        qualificationRoomsEditNameButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                qualificationRoomsEditNameButtonMouseReleased(evt);
            }
        });
        qualificationPane.add(qualificationRoomsEditNameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 278, 174, -1));

        jLabel11.setText("Judges");
        qualificationPane.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 20, 174, -1));

        qualificationJudgesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        qualificationJudgesList.setEnabled(false);
        qualificationJudgesList.setFocusable(false);
        qualificationJudgesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                qualificationJudgesListValueChanged(evt);
            }
        });
        jScrollPane10.setViewportView(qualificationJudgesList);

        qualificationPane.add(jScrollPane10, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 80, 174, 191));

        qualificationJudgesSubstituteJudgeButton.setText("Substitute judge");
        qualificationJudgesSubstituteJudgeButton.setEnabled(false);
        qualificationJudgesSubstituteJudgeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                qualificationJudgesSubstituteJudgeButtonMouseReleased(evt);
            }
        });
        qualificationPane.add(qualificationJudgesSubstituteJudgeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 278, 174, -1));

        jLabel12.setText("Student codes");
        qualificationPane.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 20, 174, -1));

        qualificationStudentCodesTextField.setEnabled(false);
        qualificationPane.add(qualificationStudentCodesTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 44, 174, -1));

        qualificationStudentCodesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        qualificationStudentCodesList.setEnabled(false);
        qualificationStudentCodesList.setFocusable(false);
        qualificationStudentCodesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                qualificationStudentCodesListValueChanged(evt);
            }
        });
        jScrollPane11.setViewportView(qualificationStudentCodesList);

        qualificationPane.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 80, 174, 191));

        qualificationStudentCodesSearchButton.setText("Search");
        qualificationStudentCodesSearchButton.setEnabled(false);
        qualificationStudentCodesSearchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                qualificationStudentCodesSearchButtonMouseReleased(evt);
            }
        });
        qualificationPane.add(qualificationStudentCodesSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 278, 174, -1));

        jLabel13.setText("Scores");
        qualificationPane.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 20, 174, -1));

        qualificationScoresTextField.setEnabled(false);
        qualificationPane.add(qualificationScoresTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 44, 174, -1));

        qualificationScoresList.setEnabled(false);
        jScrollPane12.setViewportView(qualificationScoresList);

        qualificationPane.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 80, 174, 191));

        qualificationScoresAddButton.setText("Add");
        qualificationScoresAddButton.setEnabled(false);
        qualificationPane.add(qualificationScoresAddButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 278, -1, -1));

        qualificationScoresRemoveButton.setText("Remove");
        qualificationScoresRemoveButton.setEnabled(false);
        qualificationPane.add(qualificationScoresRemoveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1021, 278, -1, -1));

        qualificationResetButton.setText("Reset");
        qualificationResetButton.setEnabled(false);
        qualificationResetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                qualificationResetButtonMouseReleased(evt);
            }
        });
        qualificationPane.add(qualificationResetButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 344, 358, -1));

        qualificationExportButton.setText("Export");
        qualificationExportButton.setEnabled(false);
        qualificationExportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                qualificationExportButtonMouseReleased(evt);
            }
        });
        qualificationPane.add(qualificationExportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 344, 358, -1));

        qualificationSaveToggleButton.setText("Save");
        qualificationSaveToggleButton.setEnabled(false);
        qualificationSaveToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                qualificationSaveToggleButtonMouseReleased(evt);
            }
        });
        qualificationPane.add(qualificationSaveToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 377, 1094, -1));

        qualificationApproveIncompleteToggleButton.setText("Approve incomplete");
        qualificationApproveIncompleteToggleButton.setEnabled(false);
        qualificationPane.add(qualificationApproveIncompleteToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 344, 174, -1));

        qualificationApproveCompleteToggleButton.setText("Approve complete");
        qualificationApproveCompleteToggleButton.setEnabled(false);
        qualificationPane.add(qualificationApproveCompleteToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 344, 174, -1));

        tabbedPane.addTab("Qualification", qualificationPane);

        jLabel14.setText("Original Oratory");

        finalistsOriginalOratoryList.setEnabled(false);
        jScrollPane13.setViewportView(finalistsOriginalOratoryList);

        finalistsProgressBar.setString("Progress");
        finalistsProgressBar.setStringPainted(true);

        jLabel15.setText("Oral Interpretation");

        finalistsOralInterpretationList.setEnabled(false);
        jScrollPane14.setViewportView(finalistsOralInterpretationList);

        jLabel16.setText("Impromptu Speaking");

        finalistsImpromptuSpeakingList.setEnabled(false);
        jScrollPane15.setViewportView(finalistsImpromptuSpeakingList);

        jLabel17.setText("Duet Acting");

        finalistsDuetActingList.setEnabled(false);
        jScrollPane16.setViewportView(finalistsDuetActingList);

        jLabel18.setText("Debate semifinalists");

        finalistsDebateSemifinalistsAList.setEnabled(false);
        jScrollPane17.setViewportView(finalistsDebateSemifinalistsAList);

        finalistsDebateSemifinalistsSetWinnerAButton.setText("Set winner A");
        finalistsDebateSemifinalistsSetWinnerAButton.setEnabled(false);

        jLabel19.setText("Debate Finalists");

        finalistsDebateFinalistsList.setEnabled(false);
        jScrollPane18.setViewportView(finalistsDebateFinalistsList);

        finalistsResetButton.setText("Reset");
        finalistsResetButton.setEnabled(false);

        finalistsExportButton.setText("Export");
        finalistsExportButton.setEnabled(false);

        finalistsApproveToggleButton.setText("Approve");
        finalistsApproveToggleButton.setEnabled(false);

        finalistsSaveToggleButton.setText("Save");
        finalistsSaveToggleButton.setEnabled(false);

        finalistsDebateSemifinalistsBList.setEnabled(false);
        jScrollPane33.setViewportView(finalistsDebateSemifinalistsBList);

        finalistsDebateSemifinalistsSetWinnerBButton.setText("Set winner B");
        finalistsDebateSemifinalistsSetWinnerBButton.setEnabled(false);

        org.jdesktop.layout.GroupLayout finalistsPaneLayout = new org.jdesktop.layout.GroupLayout(finalistsPane);
        finalistsPane.setLayout(finalistsPaneLayout);
        finalistsPaneLayout.setHorizontalGroup(
            finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(finalistsPaneLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(finalistsPaneLayout.createSequentialGroup()
                        .add(jLabel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(finalistsPaneLayout.createSequentialGroup()
                        .add(finalistsExportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 358, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(finalistsApproveToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 358, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(finalistsResetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 358, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(finalistsSaveToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1094, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(finalistsProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1094, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(finalistsPaneLayout.createSequentialGroup()
                        .add(jScrollPane13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jScrollPane14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jScrollPane15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jScrollPane16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(finalistsDebateSemifinalistsSetWinnerAButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(finalistsPaneLayout.createSequentialGroup()
                                .add(finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                                    .add(jScrollPane17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jScrollPane33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(finalistsDebateSemifinalistsSetWinnerBButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(49, 49, 49))
        );
        finalistsPaneLayout.setVerticalGroup(
            finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(finalistsPaneLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14)
                    .add(jLabel15)
                    .add(jLabel16)
                    .add(jLabel17)
                    .add(jLabel18)
                    .add(jLabel19))
                .add(44, 44, 44)
                .add(finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(finalistsPaneLayout.createSequentialGroup()
                        .add(jScrollPane17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane33, 0, 0, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(finalistsDebateSemifinalistsSetWinnerAButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(finalistsDebateSemifinalistsSetWinnerBButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(finalistsPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(finalistsExportButton)
                    .add(finalistsApproveToggleButton)
                    .add(finalistsResetButton))
                .add(4, 4, 4)
                .add(finalistsSaveToggleButton)
                .add(9, 9, 9)
                .add(finalistsProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        tabbedPane.addTab("Finalists", finalistsPane);

        finalsPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setText("Event");
        finalsPane.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 174, -1));

        finalsEventList.setEnabled(false);
        jScrollPane19.setViewportView(finalsEventList);

        finalsPane.add(jScrollPane19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 174, 191));

        finalsProgressBar.setString("Progress");
        finalsProgressBar.setStringPainted(true);
        finalsPane.add(finalsProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 415, 1094, -1));

        jLabel21.setText("Round");
        finalsPane.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 20, 174, -1));

        finalsRoundTextField.setEnabled(false);
        finalsPane.add(finalsRoundTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 44, 174, -1));

        finalsRoundList.setEnabled(false);
        jScrollPane20.setViewportView(finalsRoundList);

        finalsPane.add(jScrollPane20, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 80, 174, 191));

        finalsRoundEditNameButton.setText("Edit name");
        finalsRoundEditNameButton.setEnabled(false);
        finalsPane.add(finalsRoundEditNameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 278, 174, -1));

        jLabel22.setText("Room");
        finalsPane.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 20, 174, -1));

        finalsRoomTextField.setEnabled(false);
        finalsPane.add(finalsRoomTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 44, 174, -1));

        finalsRoomList.setEnabled(false);
        jScrollPane21.setViewportView(finalsRoomList);

        finalsPane.add(jScrollPane21, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 80, 174, 191));

        finalsRoomEditNameButton.setText("Edit name");
        finalsRoomEditNameButton.setEnabled(false);
        finalsPane.add(finalsRoomEditNameButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 278, 174, -1));

        jLabel23.setText("Judges");
        finalsPane.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 20, 174, -1));

        finalsJudgesList.setEnabled(false);
        jScrollPane22.setViewportView(finalsJudgesList);

        finalsPane.add(jScrollPane22, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 80, 174, 191));

        finalsJudgesSubstituteJudgeButton.setText("Substitute judge");
        finalsJudgesSubstituteJudgeButton.setEnabled(false);
        finalsPane.add(finalsJudgesSubstituteJudgeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(572, 278, 174, -1));

        jLabel24.setText("Student codes");
        finalsPane.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 20, 174, -1));

        finalsStudentCodesTextField.setEnabled(false);
        finalsPane.add(finalsStudentCodesTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 44, 174, -1));

        finalsStudentCodesList.setEnabled(false);
        jScrollPane23.setViewportView(finalsStudentCodesList);

        finalsPane.add(jScrollPane23, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 80, 174, 191));

        finalsStudentCodesSearchButton.setText("Search");
        finalsStudentCodesSearchButton.setEnabled(false);
        finalsPane.add(finalsStudentCodesSearchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 278, 174, -1));

        jLabel25.setText("Rankings");
        finalsPane.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 20, 174, -1));

        finalsRankingsTextField.setEnabled(false);
        finalsPane.add(finalsRankingsTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 44, 174, -1));

        finalsRankingsList.setEnabled(false);
        jScrollPane24.setViewportView(finalsRankingsList);

        finalsPane.add(jScrollPane24, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 80, 174, 145));

        finalsRankingsAddButton.setText("Add");
        finalsRankingsAddButton.setEnabled(false);
        finalsPane.add(finalsRankingsAddButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 278, -1, -1));

        finalsRankingsRemoveButton.setText("Remove");
        finalsRankingsRemoveButton.setEnabled(false);
        finalsPane.add(finalsRankingsRemoveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1021, 278, -1, -1));

        finalsResetButton.setText("Reset");
        finalsResetButton.setEnabled(false);
        finalsPane.add(finalsResetButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(756, 344, 358, -1));

        finalsExportButton.setText("Export");
        finalsExportButton.setEnabled(false);
        finalsPane.add(finalsExportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 344, 358, -1));

        finalsApproveToggleButton.setText("Approve");
        finalsApproveToggleButton.setEnabled(false);
        finalsPane.add(finalsApproveToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 344, 358, -1));

        finalsSaveToggleButton.setText("Save");
        finalsSaveToggleButton.setEnabled(false);
        finalsPane.add(finalsSaveToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 377, 1094, -1));

        finalsStudentsVoteList.setEnabled(false);
        jScrollPane34.setViewportView(finalsStudentsVoteList);

        finalsPane.add(jScrollPane34, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 233, 174, 38));

        finalsStudentsVoteSetStudentsVoteButton.setText("Set students' vote");
        finalsStudentsVoteSetStudentsVoteButton.setEnabled(false);
        finalsPane.add(finalsStudentsVoteSetStudentsVoteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 311, 174, -1));

        tabbedPane.addTab("Finals", finalsPane);

        jLabel27.setText("Original Oratory");

        winnersOriginalOratoryFirstPlaceList.setEnabled(false);
        jScrollPane26.setViewportView(winnersOriginalOratoryFirstPlaceList);

        jLabel28.setText("Oral Interpretation");

        jLabel29.setText("Impromptu Speaking");

        jLabel30.setText("Duet Acting");

        jLabel31.setText("Debate");

        winnersSaveToggleButton.setText("Save");
        winnersSaveToggleButton.setEnabled(false);

        winnersOriginalOratorySecondPlaceList.setEnabled(false);
        jScrollPane35.setViewportView(winnersOriginalOratorySecondPlaceList);

        winnersOriginalOratoryThirdPlaceList.setEnabled(false);
        jScrollPane36.setViewportView(winnersOriginalOratoryThirdPlaceList);

        winnersOriginalOratoryStudentChoiceList.setEnabled(false);
        jScrollPane37.setViewportView(winnersOriginalOratoryStudentChoiceList);

        winnersOralInterpretationFirstPlaceList.setEnabled(false);
        jScrollPane27.setViewportView(winnersOralInterpretationFirstPlaceList);

        winnersOralInterpretationSecondPlaceList.setEnabled(false);
        jScrollPane38.setViewportView(winnersOralInterpretationSecondPlaceList);

        winnersOralInterpretationThirdPlaceList.setEnabled(false);
        jScrollPane39.setViewportView(winnersOralInterpretationThirdPlaceList);

        winnersOralInterpretationStudentChoiceList.setEnabled(false);
        jScrollPane40.setViewportView(winnersOralInterpretationStudentChoiceList);

        winnersImpromptuSpeakingFirstPlaceList.setEnabled(false);
        jScrollPane28.setViewportView(winnersImpromptuSpeakingFirstPlaceList);

        winnersImpromptuSpeakingSecondPlaceList.setEnabled(false);
        jScrollPane41.setViewportView(winnersImpromptuSpeakingSecondPlaceList);

        winnersImpromptuSpeakingThirdPlaceList.setEnabled(false);
        jScrollPane42.setViewportView(winnersImpromptuSpeakingThirdPlaceList);

        winnersImpromptuSpeakingStudentChoiceList.setEnabled(false);
        jScrollPane43.setViewportView(winnersImpromptuSpeakingStudentChoiceList);

        winnersDuetActingFirstPlaceList.setEnabled(false);
        jScrollPane29.setViewportView(winnersDuetActingFirstPlaceList);

        winnersDuetActingSecondPlaceList.setEnabled(false);
        jScrollPane44.setViewportView(winnersDuetActingSecondPlaceList);

        winnersDuetActingThirdPlaceList.setEnabled(false);
        jScrollPane45.setViewportView(winnersDuetActingThirdPlaceList);

        winnersDuetActingStudentChoiceList.setEnabled(false);
        jScrollPane46.setViewportView(winnersDuetActingStudentChoiceList);

        winnersDebateFirstPlaceList.setEnabled(false);
        jScrollPane30.setViewportView(winnersDebateFirstPlaceList);

        winnersDebateSecondPlaceList.setEnabled(false);
        jScrollPane47.setViewportView(winnersDebateSecondPlaceList);

        winnersDebateThirdPlaceList.setEnabled(false);
        jScrollPane48.setViewportView(winnersDebateThirdPlaceList);

        winnersDebateStudentChoiceList.setEnabled(false);
        jScrollPane49.setViewportView(winnersDebateStudentChoiceList);

        jLabel32.setText("First place");

        jLabel33.setText("Second place");

        jLabel34.setText("Third place");

        jLabel35.setText("Student choice");

        jLabel26.setText("Congratulations!");

        winnersExportButton.setText("Export");
        winnersExportButton.setEnabled(false);

        org.jdesktop.layout.GroupLayout winnersPaneLayout = new org.jdesktop.layout.GroupLayout(winnersPane);
        winnersPane.setLayout(winnersPaneLayout);
        winnersPaneLayout.setHorizontalGroup(
            winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(winnersPaneLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(winnersPaneLayout.createSequentialGroup()
                        .add(jLabel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(72, 72, 72)
                        .add(jLabel27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(jLabel31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(winnersSaveToggleButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1094, Short.MAX_VALUE)
                    .add(winnersPaneLayout.createSequentialGroup()
                        .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(winnersPaneLayout.createSequentialGroup()
                                .add(jLabel32)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jScrollPane26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, winnersPaneLayout.createSequentialGroup()
                                .add(jLabel34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(79, 79, 79)
                                .add(jScrollPane36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, winnersPaneLayout.createSequentialGroup()
                                .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, Short.MAX_VALUE))
                                .add(58, 58, 58)
                                .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jScrollPane48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(winnersExportButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(49, 49, 49))
        );
        winnersPaneLayout.setVerticalGroup(
            winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(winnersPaneLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel27)
                        .add(jLabel26))
                    .add(jLabel28)
                    .add(jLabel29)
                    .add(jLabel30)
                    .add(jLabel31))
                .add(44, 44, 44)
                .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(winnersPaneLayout.createSequentialGroup()
                        .add(jScrollPane30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(winnersPaneLayout.createSequentialGroup()
                        .add(jScrollPane29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(winnersPaneLayout.createSequentialGroup()
                            .add(jScrollPane28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane43, 0, 0, Short.MAX_VALUE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, winnersPaneLayout.createSequentialGroup()
                            .add(jScrollPane27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane40, 0, 0, Short.MAX_VALUE))
                        .add(winnersPaneLayout.createSequentialGroup()
                            .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel32))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel33))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel34))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(winnersPaneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jScrollPane37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel35)))))
                .add(88, 88, 88)
                .add(winnersExportButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(winnersSaveToggleButton)
                .add(29, 29, 29))
        );

        tabbedPane.addTab("Winners", winnersPane);

        tabbedPane.setSelectedIndex(1);

        getContentPane().add(tabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 1158, 490));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NESDA Tournament Manager 2013 by Zbyněk Stara");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1140, -1));

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphics/NesdaHB2006-72_001small.jpg"))); // NOI18N
        getContentPane().add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 20, -1, -1));

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphics/NesdaHB2006-72_001small.jpg"))); // NOI18N
        getContentPane().add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, -1, -1));

        overallProgressBar.setValue(33);
        overallProgressBar.setString("Overall progress");
        overallProgressBar.setStringPainted(true);
        getContentPane().add(overallProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 600, 1140, -1));

        fileMenu.setText("File");

        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText("Save As ...");
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setText("Help");

        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

// GENERAL:
    // hadling the event when the user clicks the exitMenuItem
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

// SETTINGS TAB:
    // hadling the event when the user clicks the settingsLoadButton
    private void settingsLoadButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsLoadButtonMouseReleased
        try { // try to load a file
            this.loadFile();

            // set the setting tab's settings values to be the same as those in database
            this.synchronizeTFSettings(database);

            // which tabs were saved in the file?
            if (approvedSectionsIndex == 0) { // only settings
                this.enableSettingsTabActionBox();
            } else if(approvedSectionsIndex == 1) { // settings and participants
                tabbedPane.setSelectedIndex(1);

                this.enableParticipantsTabActionBox();

                this.updateParticipantsProgressBar();
            } else if(approvedSectionsIndex == 2) { // settings, participants, and qualification
                tabbedPane.setSelectedIndex(2);

                this.enableQualificationTabActionBox();

                this.updateQualificationProgressBar(100);
            }
        } catch (UserIOException ex) { // if the user cancels the file chooser window
            // this is acceptable - no exception dialog is invoked
        } catch (FileIOException ex) { // if the file is illegal
            // show the following exception dialog
            exceptionDialogTextArea.setText("The file you tried to open is not "
                    + "saved in a format compatible with this program. Make "
                    + "sure you are trying to load in a valid file and try "
                    + "to open it once more.");

            exceptionDialog.setVisible(true);
        } catch (IOException ex) { // if there is a general IOException
            // show the following exception dialog
            exceptionDialogTextArea.setText("An input/output exception has "
                    + "occurred during the loading of the file. Try to load "
                    + "the file once more.");

            exceptionDialog.setVisible(true);
        }
    }//GEN-LAST:event_settingsLoadButtonMouseReleased

    // hadling the event when the user clicks the settingsApproveToggleButton
    private void settingsApproveToggleButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsApproveToggleButtonMouseReleased
        if (settingsApproveToggleButton.isSelected()) { // if the approve button was selected by this action
            try { // try to approve settings
                // set the database settings to correspond to settings in the settings tab
                this.changeDatabaseSettings(database);

                // see if the settings are correct
                if (this.approveDatabaseSettings(database).equals("")) { // if the settings are correct
                    this.enableParticipantsTabActionBox();

                    tabbedPane.setSelectedIndex(1);
                } else { // if the settings are incorrect
                    // show an exception dialog listing all the settings that are incorrect
                    settingsErrorDialogTextArea.setText(this.approveDatabaseSettings(database));
                    settingsErrorDialog.setVisible(true);

                    // deselect the approve button
                    settingsApproveToggleButton.setSelected(false);
                }
            } catch (IllegalArgumentException ex) { // if, for whatever reason, the error handling failed at previous levels
                // show the following exception dialog
                exceptionDialogTextArea.setText("Some of the settings values "
                        + "were incorrect and this could not be resolved by "
                        + "the program. Look for  erroneous values in the "
                        + "settings text fields and correct them yourself.");

                exceptionDialog.setVisible(true);

                settingsApproveToggleButton.setSelected(false);
            }
        } else { // if approveToggleButton was deselected by this action
            this.enableSettingsTabActionBox();
        }
    }//GEN-LAST:event_settingsApproveToggleButtonMouseReleased

    // hadling the event when the user clicks the settingsResetButton
    private void settingsResetButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsResetButtonMouseReleased
        // reset database settings to be the same as the default values in database
        this.resetDatabaseSettings(database);

        // set the settings tab's values to correspond to those in the database
        this.synchronizeTFSettings(database);
    }//GEN-LAST:event_settingsResetButtonMouseReleased

    // hadling the event when the user clicks the settingsSaveToggleButton
    private void settingsSaveToggleButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsSaveToggleButtonMouseReleased
        if (settingsSaveToggleButton.isSelected()) { // if the save button was selected by this action
            try { // try to save the settings
                approvedSectionsIndex = 0; // current section is settings = 0

                this.saveFile();

                settingsApproveToggleButton.setEnabled(false); // disable approve button until save button is deselected
            } catch (UserIOException ex) { // if the file chooser dialog was cancelled by the user
                settingsSaveToggleButton.setSelected(false);
            } catch (IOException ex) { // if there was a general IO exception
                settingsSaveToggleButton.setSelected(false);

                // show the following exception dialog
                exceptionDialogTextArea.setText("An input/output exception has "
                        + "occurred during the saving of the file. Try to save "
                        + "the file once more.");

                exceptionDialog.setVisible(true);
            }
        } else { // if the save button was deselected by this action
            settingsApproveToggleButton.setEnabled(true); // enable the approve button
        }
    }//GEN-LAST:event_settingsSaveToggleButtonMouseReleased

// PARTICIPANTS TAB:
    // SCHOOLS:
    // hadling the event when the user clicks the participantsSchoolsAddButton
    private void participantsSchoolsAddButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsSchoolsAddButtonMouseReleased
        String newSchoolName = participantsSchoolsTextField.getText();

        try {
            database.searchSchool(newSchoolName); // should result in a NoSuchElement exception
            
            // if an element with the same name is already in the database:
            // show the following exception dialog
            illegalActionDialogTextArea.setText("The name you typed in is "
                    + "already taken. All schools must have unique names. If "
                    + "you really wish to add a school with this name, first "
                    + "locate the existing school and rename it.");

            illegalActionDialog.setVisible(true);
        } catch (IllegalArgumentException ex) { // if the name is an empty string
            // set the new school's name to a generic name with the current new no name school number and insert the new school
            newSchoolName = "<No name " + currentNewNoNameSchoolNumber + ">";
            database.insertSchool(newSchoolName);

            // increment the current new no name school number
            currentNewNoNameSchoolNumber += 1;

            // increment the number of schools
            schoolsNumber += 1;

            // set the progress bar to reflect the change in the number of schools
            this.updateParticipantsProgressBar();

            // new assignment of codes will be needed
            studentChanges = true;

            // empty the schools text field
            participantsSchoolsTextField.setText("");

            // find the new school in the database
            int newSchoolIndex = database.searchSchool(newSchoolName);
            this.autoselectSchoolActionBox(newSchoolIndex);

            // set the student codes button to the correct status
            this.recheckStudentCodesActionBox();
        } catch (NoSuchElementException ex) { // if the name was not encountered in the database
            // insert the school
            database.insertSchool(newSchoolName);

            // increment the number of schools in the database
            schoolsNumber += 1;

            // new assignment of codes will be needed
            studentChanges = true;

            // empty the schools text field
            participantsSchoolsTextField.setText("");

            // find the new school in the database
            int newSchoolIndex = database.searchSchool(newSchoolName);
            this.autoselectSchoolActionBox(newSchoolIndex);

            // set the student codes button to the correct status
            this.recheckStudentCodesActionBox();
        } finally { // do in any case
            participantsSchoolsTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsSchoolsAddButtonMouseReleased

    // hadling the event when the user clicks the participantsSchoolsRemoveButton
    private void participantsSchoolsRemoveButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsSchoolsRemoveButtonMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        if (schoolsIndex >= 0) { // if a school is selected in the schoolsList
            // delete the currently selected school
            School deletedSchool = (School) database.deleteSchool(currentSchool.getName());

            // decrement the number of schools
            schoolsNumber -= 1;

            // set the progress bar to reflect the situation
            this.updateParticipantsProgressBar();

            // decrease the number of teachers and students by their numbers in the deleted school
            teachersNumber -= deletedSchool.getTeacherTreeSize();
            studentsNumber -= deletedSchool.getStudentTreeSize();

            // reorganization of student codes will be necessary (a letter disappeared)
            studentChanges = true;
        }

        if (database.getSchoolTreeSize() != 0) { // if there is still a school in the schoolTree after the removal
            // select the first school from the list
            this.autoselectSchoolActionBox(0);

            if (studentsNumber != 0) { // if thee are still some students in the database
                // enable searching for students
                this.toggleStudentSearchActionBox(true);

                // enable the assign codes button
                this.recheckStudentCodesActionBox();

            } else { // if there are no more students in the database
                // disable searching for students
                this.toggleStudentSearchActionBox(false);

                // disable the assign codes button
                this.disableStudentCodesActionBox();
            }
        } else { // if there is no school left in the schoolTree after the removal
            // disable everything (but text field and add button) about schools
            participantsSchoolsList.setEnabled(false);
            participantsSchoolsRemoveButton.setEnabled(false);
            participantsSchoolsEditButton.setEnabled(false);
            participantsSchoolsSearchButton.setEnabled(false);

            // set the schoolsList to be emptyModel
            participantsSchoolsList.setModel(emptyModel);

            // disable everything about teachers
            participantsTeachersTextField.setEnabled(false);
            participantsTeachersList.setEnabled(false);
            participantsTeachersAddButton.setEnabled(false);
            participantsTeachersRemoveButton.setEnabled(false);
            participantsTeachersSearchButton.setEnabled(false);
            participantsTeachersEditButton.setEnabled(false);

            // set the teachersList to be noSelectionModel
            participantsTeachersList.setModel(noSelectionModel);

            // disable everything about students
            participantsStudentsTextField.setEnabled(false);
            participantsStudentsList.setEnabled(false);
            participantsStudentsAddButton.setEnabled(false);
            participantsStudentsRemoveButton.setEnabled(false);
            participantsStudentsSearchButton.setEnabled(false);
            participantsStudentsEditButton.setEnabled(false);

            // set the studentsList to be noSelectionModel
            participantsStudentsList.setModel(noSelectionModel);

            // disable student codes
            this.disableStudentCodesActionBox();

            // set the studentCodesList to be noSelectionModel
            participantsStudentsList.setModel(noSelectionModel);

            // disable assign events
            this.disableAssignEventsActionBox();

            // disable assign pairs
            participantsAssignPairsEventsList.setEnabled(false);
            participantsAssignPairsLeftList.setEnabled(false);
            participantsAssignPairsRightList.setEnabled(false);
            participantsAssignPairsPairsList.setEnabled(false);
            participantsAssignPairsPairButton.setEnabled(false);
            participantsAssignPairsRemovePairButton.setEnabled(false);

            // set the lists to be blankModels
            participantsAssignPairsLeftList.setModel(blankModel);
            participantsAssignPairsRightList.setModel(blankModel);
            participantsAssignPairsPairsList.setModel(blankModel);
        }

        participantsSchoolsTextField.grabFocus();
    }//GEN-LAST:event_participantsSchoolsRemoveButtonMouseReleased

    // hadling the event when the user clicks the participantsSchoolsEditButton
    private void participantsSchoolsEditButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsSchoolsEditButtonMouseReleased
        String newName = participantsSchoolsTextField.getText();

        try {
            database.searchSchool(newName); // should result in a NoSuchElement exception

            // if an element with the same name is already in the database:
            // show the following exception dialog
            illegalActionDialogTextArea.setText("The name you typed in is "
                    + "already taken. All schools must have unique names. If "
                    + "you really wish to add a school with this name, first "
                    + "locate the existing school and rename it.");

            illegalActionDialog.setVisible(true);
        } catch (IllegalArgumentException ex) { // if the name is an empty string
            // do nothing
        } catch (NoSuchElementException ex) { // if the name was not encountered in the database
            // rename the school at the schools index
            database.editSchool(schoolsIndex, newName);

            // changes were made to the database - new assignment of student codes will be needed
            studentChanges = true;

            // empty the school text field
            participantsSchoolsTextField.setText("");

            // find the edited school in the database
            int editedSchoolIndex = database.searchSchool(newName);
            this.refreshSchoolActionBox(editedSchoolIndex);

            // set the student codes button to the correct status
            this.recheckStudentCodesActionBox();
        } finally { // do in any case
            participantsSchoolsTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsSchoolsEditButtonMouseReleased

    // hadling the event when the user clicks the participantsSchoolsSearchButton
    private void participantsSchoolsSearchButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsSchoolsSearchButtonMouseReleased
        String searchedName = participantsSchoolsTextField.getText();
        int searchedSchoolIndex;

        try {
            // empty the school text field
            participantsSchoolsTextField.setText("");

            // search for the school
            searchedSchoolIndex = database.searchSchool(searchedName); // should continue

            // select the found school
            this.refreshSchoolActionBox(searchedSchoolIndex);
        } catch (IllegalArgumentException ex) { // if the name is an empty string
            // do nothing
        } catch (NoSuchElementException ex) { // if the name was not encountered in the database
            // empty the schools text field
            participantsSchoolsTextField.setText("");

            // set schools index to be -1
            this.refreshSchoolActionBox(-1);
        } finally { // do in any case
            participantsSchoolsTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsSchoolsSearchButtonMouseReleased

    // hadling the event when the user makes a selection in the participantsSchoolsList
    private void participantsSchoolsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsSchoolsListValueChanged
        schoolsIndex = participantsSchoolsList.getSelectedIndex();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        if (schoolsIndex != -1) { // if schools index was set to be something (not -1)
            // SCHOOLS:
            // enable the selected-school-specific buttons (remove, edit)
            participantsSchoolsRemoveButton.setEnabled(true);
            participantsSchoolsEditButton.setEnabled(true);

            // TEACHERS:
            // enable the addition of teachers to the school
            participantsTeachersTextField.setEnabled(true);
            participantsTeachersAddButton.setEnabled(true);

            if (currentSchool.getTeacherTreeSize() > 0) { // if there are some teachers in the selected school
                // update and enable the teachers list
                participantsTeachersList.setEnabled(true);
                this.updateTeachersListModel(currentSchool);
            } else { // if teacherTree is empty
                // disable teachers list and set the teacher model to be the empty model
                participantsTeachersList.setEnabled(false);
                participantsTeachersRemoveButton.setEnabled(false);
                participantsTeachersEditButton.setEnabled(false);

                participantsTeachersList.setModel(emptyModel);
            }

            // STUDENTS (+ ASSIGN EVENTS):
            // enable adding of students
            participantsStudentsTextField.setEnabled(true);
            participantsStudentsAddButton.setEnabled(true);

            if (currentSchool.getStudentTreeSize() > 0) { // if there are some students in the database
                // enable studentsList
                participantsStudentsList.setEnabled(true);

                // show students and studentCodes for current school
                this.updateStudentsListModel(currentSchool);
                this.updateStudentCodesListModel(currentSchool);

                // if there is a potential for assigning pairs (minimum of 2 DA or debate students and codes assigned)
                if ((((currentSchool.getUnpairedDAStudentTreeSize() / 2) > 0) || ((currentSchool.getUnpairedDebateStudentTreeSize() / 2) > 0)) && studentCodes) {
                    // enable assign pairs events list
                    participantsAssignPairsEventsList.setSelectedIndex(-1);
                    participantsAssignPairsEventsList.setModel(eventsListModel);
                    participantsAssignPairsEventsList.setEnabled(true);
                } else if ((currentSchool.getDAPairTreeSize() > 0) || (currentSchool.getDebatePairTreeSize() > 0)) { // if some pairs were assigned
                    // enable assign pairs events list
                    participantsAssignPairsEventsList.setSelectedIndex(-1);
                    participantsAssignPairsEventsList.setModel(eventsListModel);
                    participantsAssignPairsEventsList.setEnabled(true);
                } else { // if there is no potential for pairing or removing pairs
                    // disable assign pairs events list
                    participantsAssignPairsEventsList.setSelectedIndex(-1);
                    participantsAssignPairsEventsList.setModel(eventsListModel);
                    participantsAssignPairsEventsList.setEnabled(false);
                }

                // disable the other assign pairs lists and set their models to be blank
                participantsAssignPairsLeftList.setSelectedIndex(-1);
                participantsAssignPairsLeftList.setModel(blankModel);
                participantsAssignPairsLeftList.setEnabled(false);

                participantsAssignPairsRightList.setSelectedIndex(-1);
                participantsAssignPairsRightList.setModel(blankModel);
                participantsAssignPairsRightList.setEnabled(false);

                participantsAssignPairsPairsList.setSelectedIndex(-1);
                participantsAssignPairsPairsList.setModel(blankModel);
                participantsAssignPairsPairsList.setEnabled(false);

                // disable the pair and remove pair buttons
                participantsAssignPairsPairButton.setEnabled(false);
                participantsAssignPairsRemovePairButton.setEnabled(false);
            } else { // if studentTree is empty
                participantsStudentsList.setEnabled(false);
                participantsStudentsRemoveButton.setEnabled(false);
                participantsStudentsEditButton.setEnabled(false);

                this.disableAssignEventsActionBox(); // #12

                participantsStudentsList.setModel(emptyModel); // needed - #20
                participantsStudentCodesList.setModel(emptyModel);

                participantsAssignPairsEventsList.setSelectedIndex(-1);
                participantsAssignPairsEventsList.setModel(eventsListModel);
                participantsAssignPairsEventsList.setEnabled(false);

                participantsAssignPairsLeftList.setSelectedIndex(-1);
                participantsAssignPairsLeftList.setModel(blankModel);
                participantsAssignPairsLeftList.setEnabled(false);

                participantsAssignPairsRightList.setSelectedIndex(-1);
                participantsAssignPairsRightList.setModel(blankModel);
                participantsAssignPairsRightList.setEnabled(false);

                participantsAssignPairsPairsList.setSelectedIndex(-1);
                participantsAssignPairsPairsList.setModel(blankModel);
                participantsAssignPairsPairsList.setEnabled(false);

                participantsAssignPairsPairButton.setEnabled(false);
                participantsAssignPairsRemovePairButton.setEnabled(false);
            }
        } else { // if schoolsIndex == -1
            // SCHOOLS:
            participantsSchoolsRemoveButton.setEnabled(false); // needed - #21 (inverse of #13)
            participantsSchoolsEditButton.setEnabled(false);

            // TEACHERS:
            participantsTeachersList.setModel(noSelectionModel); // needed - #22
            participantsTeachersList.setEnabled(false);

            if (teachersNumber != 0) { // needed - #23
                participantsTeachersTextField.setEnabled(true); // #23a
                participantsTeachersSearchButton.setEnabled(true);
            } else {
                participantsTeachersTextField.setEnabled(false);
                participantsTeachersSearchButton.setEnabled(false);
            }

            participantsTeachersAddButton.setEnabled(false); // needed - #24 + #24a
            participantsTeachersRemoveButton.setEnabled(false); // #24a
            participantsTeachersEditButton.setEnabled(false);

            // STUDENTS (+ ASSIGN EVENTS):
            this.disableAssignEventsActionBox(); // #12

            participantsStudentsList.setModel(noSelectionModel); // needed - #25 + #26 (both modif #22)
            participantsStudentsList.setEnabled(false);

            participantsStudentCodesList.setModel(noSelectionModel);
            participantsStudentCodesList.setEnabled(false);

            if (studentsNumber != 0) { // needed - #27 (modif #23)
                participantsStudentsTextField.setEnabled(true);
                participantsStudentsSearchButton.setEnabled(true);
            } else {
                participantsStudentsTextField.setEnabled(false);
                participantsStudentsSearchButton.setEnabled(false);
            }

            participantsStudentsAddButton.setEnabled(false); // needed - #28 (modif #24) + #28a (modif #24a)
            participantsStudentsRemoveButton.setEnabled(false); // #28a
            participantsStudentsEditButton.setEnabled(false);

            // ASSIGN PAIRS:
            participantsAssignPairsEventsList.setSelectedIndex(-1);
            participantsAssignPairsEventsList.setModel(eventsListModel);
            participantsAssignPairsEventsList.setEnabled(false);

            participantsAssignPairsLeftList.setSelectedIndex(-1);
            participantsAssignPairsLeftList.setModel(blankModel);
            participantsAssignPairsLeftList.setEnabled(false);

            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsRightList.setModel(blankModel);
            participantsAssignPairsRightList.setEnabled(false);

            participantsAssignPairsPairsList.setSelectedIndex(-1);
            participantsAssignPairsPairsList.setModel(blankModel);
            participantsAssignPairsPairsList.setEnabled(false);

            participantsAssignPairsPairButton.setEnabled(false);
            participantsAssignPairsRemovePairButton.setEnabled(false);
        }
    }//GEN-LAST:event_participantsSchoolsListValueChanged

    // Teachers:
    // hadling the event when the user clicks the participantsTeachersAddButton
    private void participantsTeachersAddButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsTeachersAddButtonMouseReleased
        String newTeacherName = participantsTeachersTextField.getText();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        try {
            currentSchool.searchTeacher(newTeacherName);    // should result in a NoSuchElement exception
            // if not, then:
            illegalActionDialogTextArea.setText("The name you typed in is "
                    + "already taken. All teachers must have unique names. If "
                    + "you really wish to add a teacher with this name, first "
                    + "locate the existing teacher and rename them.");

            illegalActionDialog.setVisible(true);
        } catch (IllegalArgumentException ex) { // if the name is an empty string (= OK)
            newTeacherName = "<No name " + currentNewNoNameTeacherNumber + ">";
            currentSchool.insertTeacher(newTeacherName);

            currentNewNoNameTeacherNumber += 1;

            teachersNumber += 1;

            participantsTeachersTextField.setText("");

            participantsTeachersList.setEnabled(true); // needed - #29 + #30 (modif #1 + #2)
            participantsTeachersSearchButton.setEnabled(true);

            this.updateTeachersListModel(currentSchool);

            int newTeacherIndex = currentSchool.searchTeacher(newTeacherName);

            participantsTeachersList.setSelectedIndex(newTeacherIndex);
            participantsTeachersList.ensureIndexIsVisible(newTeacherIndex);
        } catch (NoSuchElementException ex) { // if the name was not encountered (= GOOD)
            currentSchool.insertTeacher(newTeacherName);

            teachersNumber += 1;

            participantsTeachersTextField.setText("");

            participantsTeachersList.setEnabled(true); // #29 + #30
            participantsTeachersSearchButton.setEnabled(true);

            this.updateTeachersListModel(currentSchool);

            int newTeacherIndex = currentSchool.searchTeacher(newTeacherName);

            participantsTeachersList.setSelectedIndex(newTeacherIndex);
            participantsTeachersList.ensureIndexIsVisible(newTeacherIndex);
        } finally { // do in any case
            participantsTeachersTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsTeachersAddButtonMouseReleased

    // hadling the event when the user clicks the participantsTeachersRemoveButton
    private void participantsTeachersRemoveButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsTeachersRemoveButtonMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);
        Teacher currentTeacher = (Teacher) currentSchool.getTeacherTreeNodeData(teachersIndex);

        if (teachersIndex >= 0) {
            Teacher deletedTeacher = (Teacher) currentSchool.deleteTeacher(currentTeacher.getName());

            teachersNumber -= 1;
        }

        if (currentSchool.getTeacherTreeSize() != 0) {
            participantsTeachersList.setEnabled(true); // #29 + #30
            participantsTeachersSearchButton.setEnabled(true);

            this.updateTeachersListModel(currentSchool);

            participantsTeachersList.setSelectedIndex(0);
            participantsTeachersList.ensureIndexIsVisible(0);
        } else {
            participantsTeachersList.setEnabled(false); // #16
            participantsTeachersRemoveButton.setEnabled(false);
            participantsTeachersEditButton.setEnabled(false);

            participantsTeachersList.setModel(emptyModel);

            if (teachersNumber == 0) {
                participantsTeachersSearchButton.setEnabled(false); // #16a
            }
        }

        participantsTeachersTextField.grabFocus();
    }//GEN-LAST:event_participantsTeachersRemoveButtonMouseReleased

    // hadling the event when the user clicks the participantsTeachersEditButton
    private void participantsTeachersEditButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsTeachersEditButtonMouseReleased
        String newName = participantsTeachersTextField.getText();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        try {
            currentSchool.searchTeacher(newName); // should result in a NoSuchElement exception
            // if not, then:
            illegalActionDialogTextArea.setText("The name you typed in is "
                    + "already taken. All teachers must have unique names. If "
                    + "you really wish to add a teacher with this name, first "
                    + "locate the existing teacher and rename them.");

            illegalActionDialog.setVisible(true);
        } catch (IllegalArgumentException ex) { // if the name is an empty string (= BAD)
            // do nothing
        } catch (NoSuchElementException ex) { // if the name was not encountered (= GOOD)
            currentSchool.editTeacher(teachersIndex, newName);

            studentChanges = true;

            participantsTeachersTextField.setText("");

            this.updateTeachersListModel(currentSchool); // #30

            int editedTeacherIndex = currentSchool.searchTeacher(newName);

            participantsTeachersList.setSelectedIndex(editedTeacherIndex);
            participantsTeachersList.ensureIndexIsVisible(editedTeacherIndex);
        } finally { // do in any case
            participantsTeachersTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsTeachersEditButtonMouseReleased

    // hadling the event when the user clicks the participantsTeachersSearchButton
    private void participantsTeachersSearchButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsTeachersSearchButtonMouseReleased
        String searchedName = participantsTeachersTextField.getText();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        int searchedTeacherSchoolIndex;
        int searchedTeacherIndex;

        try {
            participantsTeachersTextField.setText("");

            int[] searchedTeacherIndices = database.globalSearchTeacher(searchedName, schoolsIndex); // should continue

            searchedTeacherSchoolIndex = searchedTeacherIndices[0]; // the schoolsList index

            School teacherSchool = (School) database.getSchoolTreeNodeData(searchedTeacherSchoolIndex);

            this.updateSchoolsListModel(); // #2

            participantsSchoolsList.setSelectedIndex(searchedTeacherSchoolIndex);
            participantsSchoolsList.ensureIndexIsVisible(searchedTeacherSchoolIndex);

            searchedTeacherIndex = searchedTeacherIndices[1]; // the teachersList index

            this.updateTeachersListModel(teacherSchool); // #30

            participantsTeachersList.setSelectedIndex(searchedTeacherIndex);
            participantsTeachersList.ensureIndexIsVisible(searchedTeacherIndex);
        } catch (IllegalArgumentException ex) { // if the name is an empty string (= BAD)
            // do nothing
        } catch (NoSuchElementException ex) { // if the name was not encountered (= BAD)
            participantsTeachersTextField.setText("");

            searchedTeacherIndex = -1;

            this.updateTeachersListModel(currentSchool); // #30

            participantsTeachersList.setSelectedIndex(searchedTeacherIndex);
        } finally { // do in any case
            participantsTeachersTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsTeachersSearchButtonMouseReleased

    // hadling the event when the user makes a selection in the participantsTeachersList
    private void participantsTeachersListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsTeachersListValueChanged
        teachersIndex = participantsTeachersList.getSelectedIndex();

        if (teachersIndex != -1) { // needed - #31
            participantsTeachersRemoveButton.setEnabled(true); // #24a
            participantsTeachersEditButton.setEnabled(true);
        } else { // if teachersIndex == -1
            participantsTeachersRemoveButton.setEnabled(false);
            participantsTeachersEditButton.setEnabled(false);
        }
    }//GEN-LAST:event_participantsTeachersListValueChanged

    // Students:
    // hadling the event when the user clicks the participantsStudentsAddButton
    private void participantsStudentsAddButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsStudentsAddButtonMouseReleased
        String newStudentName = participantsStudentsTextField.getText();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        try {
            currentSchool.searchStudent(newStudentName);    // should result in a NoSuchElement exception
            // if not, then:
            illegalActionDialogTextArea.setText("The name you typed in is "
                    + "already taken. All students must have unique names. If "
                    + "you really wish to add a student with this name, first "
                    + "locate the existing student and rename them.");

            illegalActionDialog.setVisible(true);
        } catch (IllegalArgumentException ex) { // if the name is an empty string (= OK)
            newStudentName = "<No name " + currentNewNoNameStudentNumber + ">";
            currentSchool.insertStudent(newStudentName);

            currentNewNoNameStudentNumber += 1;

            studentsNumber += 1;

            studentChanges = true;

            participantsStudentsTextField.setText("");
            participantsStudentCodesTextField.setText("");

            participantsStudentsList.setEnabled(true); // #32
            participantsStudentsSearchButton.setEnabled(true);

            participantsStudentCodesAssignCodesButton.setEnabled(true); // #4 - redundant

            this.updateStudentsListModel(currentSchool); // #34
            this.updateStudentCodesListModel(currentSchool);

            int newStudentIndex = currentSchool.searchStudent(newStudentName);

            participantsStudentsList.setSelectedIndex(newStudentIndex);
            participantsStudentsList.ensureIndexIsVisible(newStudentIndex);

            participantsStudentCodesList.setSelectedIndex(newStudentIndex);
            participantsStudentCodesList.ensureIndexIsVisible(newStudentIndex);

            recheckStudentCodesActionBox();
        } catch (NoSuchElementException ex) { // if the name was not encountered (= GOOD)
            currentSchool.insertStudent(newStudentName);

            studentsNumber += 1;

            studentChanges = true;

            participantsStudentsTextField.setText("");
            participantsStudentCodesTextField.setText("");

            participantsStudentsList.setEnabled(true); // #32
            participantsStudentsSearchButton.setEnabled(true);

            this.updateStudentsListModel(currentSchool); // 34
            this.updateStudentCodesListModel(currentSchool);

            int newStudentIndex = currentSchool.searchStudent(newStudentName);

            participantsStudentsList.setSelectedIndex(newStudentIndex);
            participantsStudentsList.ensureIndexIsVisible(newStudentIndex);

            participantsStudentCodesList.setSelectedIndex(newStudentIndex);
            participantsStudentCodesList.ensureIndexIsVisible(newStudentIndex);

            recheckStudentCodesActionBox();
        } finally { // do in any case
            participantsStudentsTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsStudentsAddButtonMouseReleased

    // hadling the event when the user clicks the participantsStudentsRemoveButton
    private void participantsStudentsRemoveButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsStudentsRemoveButtonMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        Student deletedStudent = (Student) currentSchool.deleteStudent(studentsIndex);

        studentChanges = true;

        studentsNumber -= 1;

        if (deletedStudent.getDAUnpaired() == true) {
            currentSchool.deleteUnpairedDAStudent(deletedStudent.getName());
        }
        if (deletedStudent.getDebateUnpaired() == true) {
            currentSchool.deleteUnpairedDebateStudent(deletedStudent.getName());
        }

        if (deletedStudent.getDAStudentPair() != null) {
            Student deletedPairOtherStudent = deletedStudent.getDAStudentPair().getOtherStudent(deletedStudent);

            // GET CORRECT CODE:

            /*Student student1;
            Student student2;

            if (deletedStudent.getCode().compareTo(deletedPairOtherStudent.getCode()) <= 0) {
                student1 = deletedStudent; // keep the alphabetical order
                student2 = deletedPairOtherStudent;
            } else {
                student1 = deletedPairOtherStudent; // flip them to be alphabetically ordered
                student2 = deletedStudent;
            }*/

            currentSchool.deleteDAPair(deletedStudent.getDAStudentPair().getOriginalCode());

            currentSchool.insertUnpairedDAStudent(deletedPairOtherStudent);

            deletedPairOtherStudent.setDAUnpaired(true);
        }
        if (deletedStudent.getDebateStudentPair() != null) {
            Student deletedPairOtherStudent = deletedStudent.getDebateStudentPair().getOtherStudent(deletedStudent);

            // GET CORRECT CODE:

            /*Student student1;
            Student student2;

            if (deletedStudent.getCode().compareTo(deletedPairStudent.getCode()) <= 0) {
                student1 = deletedStudent; // keep the alphabetical order
                student2 = deletedPairStudent;
            } else {
                student1 = deletedPairStudent; // flip them to be alphabetically ordered
                student2 = deletedStudent;
            }*/

            currentSchool.deleteDebatePair(deletedStudent.getDAStudentPair().getOriginalCode());

            currentSchool.insertUnpairedDebateStudent(deletedPairOtherStudent);

            deletedPairOtherStudent.setDebateUnpaired(true);
        }

        if (currentSchool.getStudentTreeSize() != 0) { // if the list for this school is not empty
            participantsStudentsList.setEnabled(true); // #32
            participantsStudentsSearchButton.setEnabled(true);

            this.updateStudentsListModel(currentSchool); // #34
            this.updateStudentCodesListModel(currentSchool);

            participantsStudentsList.setSelectedIndex(0);
            participantsStudentsList.ensureIndexIsVisible(0);

            participantsStudentCodesList.setSelectedIndex(0);
            participantsStudentCodesList.ensureIndexIsVisible(0);

            if ((((currentSchool.getUnpairedDAStudentTreeSize() / 2) > 0) || ((currentSchool.getUnpairedDebateStudentTreeSize() / 2) > 0)) && studentCodes) {
                participantsAssignPairsEventsList.setSelectedIndex(-1);
                participantsAssignPairsEventsList.setModel(eventsListModel);
                participantsAssignPairsEventsList.setEnabled(true);
            } else if ((currentSchool.getDAPairTreeSize() > 0) || (currentSchool.getDebatePairTreeSize() > 0)) {
                participantsAssignPairsEventsList.setSelectedIndex(-1);
                participantsAssignPairsEventsList.setModel(eventsListModel);
                participantsAssignPairsEventsList.setEnabled(true);
            } else {
                participantsAssignPairsEventsList.setSelectedIndex(-1); // => disable everything
                participantsAssignPairsEventsList.setModel(eventsListModel);
                participantsAssignPairsEventsList.setEnabled(false);
            }

            participantsAssignPairsLeftList.setSelectedIndex(-1);
            participantsAssignPairsLeftList.setModel(blankModel);
            participantsAssignPairsLeftList.setEnabled(false);

            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsRightList.setModel(blankModel);
            participantsAssignPairsRightList.setEnabled(false);

            participantsAssignPairsPairsList.setSelectedIndex(-1);
            participantsAssignPairsPairsList.setModel(blankModel);
            participantsAssignPairsPairsList.setEnabled(false);

            participantsAssignPairsPairButton.setEnabled(false);
            participantsAssignPairsRemovePairButton.setEnabled(false);
        } else { // if the list for this school is empty
            participantsStudentsList.setEnabled(false); // #35
            participantsStudentsRemoveButton.setEnabled(false);
            participantsStudentsEditButton.setEnabled(false);

            participantsStudentCodesList.setEnabled(false); // #20

            participantsStudentsList.setModel(emptyModel); // #35 cont.
            participantsStudentCodesList.setModel(emptyModel); // #20 cont.

            participantsStudentsList.setSelectedIndex(-1);
            participantsStudentCodesList.setSelectedIndex(-1);

            if (studentsNumber != 0) { // if there are still some students
                participantsStudentsSearchButton.setEnabled(true); // #27
                participantsStudentsTextField.setEnabled(true);

                recheckStudentCodesActionBox();
            } else { // if there are no students whatsoever
                studentCodes = false;
                studentChanges = false;

                participantsStudentsSearchButton.setEnabled(false); // #36

                participantsStudentCodesTextField.setEnabled(false); // #7
                participantsStudentCodesSearchButton.setEnabled(false);
                participantsStudentCodesAssignCodesButton.setEnabled(false);
            }

            participantsAssignPairsEventsList.setEnabled(false);
            
            participantsAssignPairsEventsList.setSelectedIndex(-1); // => disable everything
            participantsAssignPairsEventsList.setModel(eventsListModel);
            participantsAssignPairsEventsList.setEnabled(false);

            participantsAssignPairsLeftList.setSelectedIndex(-1);
            participantsAssignPairsLeftList.setModel(blankModel);
            participantsAssignPairsLeftList.setEnabled(false);

            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsRightList.setModel(blankModel);
            participantsAssignPairsRightList.setEnabled(false);

            participantsAssignPairsPairsList.setSelectedIndex(-1);
            participantsAssignPairsPairsList.setModel(blankModel);
            participantsAssignPairsPairsList.setEnabled(false);

            participantsAssignPairsPairButton.setEnabled(false);
            participantsAssignPairsRemovePairButton.setEnabled(false);
        }

        recheckStudentCodesActionBox();

        participantsStudentsTextField.grabFocus();

        // TODO MAKE PAIRS RESPOND TO THIS
    }//GEN-LAST:event_participantsStudentsRemoveButtonMouseReleased

    // hadling the event when the user clicks the participantsStudentsEditButton
    private void participantsStudentsEditButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsStudentsEditButtonMouseReleased
        String newName = participantsStudentsTextField.getText();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        try {
            currentSchool.searchStudent(newName); // should result in a NoSuchElement exception
            // if not, then:
            illegalActionDialogTextArea.setText("The name you typed in is "
                    + "already taken. All students must have unique names. If "
                    + "you really wish to add a student with this name, first "
                    + "locate the existing student and rename them.");

            illegalActionDialog.setVisible(true);
        } catch (IllegalArgumentException ex) { // if the name is an empty string (= BAD)
            // do nothing
        } catch (NoSuchElementException ex) { // if the name was not encountered (= GOOD)
            currentSchool.editStudent(studentsIndex, newName);

            studentChanges = true;

            participantsStudentsTextField.setText("");

            this.updateStudentsListModel(currentSchool); // #34
            this.updateStudentCodesListModel(currentSchool);

            int editedStudentIndex = currentSchool.searchStudent(newName);

            participantsStudentsList.setSelectedIndex(editedStudentIndex);
            participantsStudentsList.ensureIndexIsVisible(editedStudentIndex);

            participantsStudentCodesList.setSelectedIndex(editedStudentIndex);
            participantsStudentCodesList.ensureIndexIsVisible(editedStudentIndex);

            recheckStudentCodesActionBox();
        } finally { // do in any case
            participantsStudentsTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsStudentsEditButtonMouseReleased

    // hadling the event when the user clicks the participantsStudentsSearchButton
    private void participantsStudentsSearchButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsStudentsSearchButtonMouseReleased
        String searchedName = participantsStudentsTextField.getText();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        int searchedStudentSchoolIndex;
        int searchedStudentIndex;

        try {
            participantsStudentsTextField.setText("");

            int[] searchedStudentIndices = database.globalSearchStudent(searchedName, schoolsIndex); // should continue

            searchedStudentSchoolIndex = searchedStudentIndices[0]; // the schoolsList index

            School studentSchool = (School) database.getSchoolTreeNodeData(searchedStudentSchoolIndex);

            this.updateSchoolsListModel(); // #2

            participantsSchoolsList.setSelectedIndex(searchedStudentSchoolIndex);
            participantsSchoolsList.ensureIndexIsVisible(searchedStudentSchoolIndex);

            searchedStudentIndex = searchedStudentIndices[1]; // the studentsList index

            this.updateStudentsListModel(studentSchool); // #34
            this.updateStudentCodesListModel(studentSchool);

            participantsStudentsList.setSelectedIndex(searchedStudentIndex);
            participantsStudentsList.ensureIndexIsVisible(searchedStudentIndex);

            participantsStudentCodesList.setSelectedIndex(searchedStudentIndex);
            participantsStudentCodesList.ensureIndexIsVisible(searchedStudentIndex);
        } catch (IllegalArgumentException ex) { // if the name is an empty string (= BAD)
            // do nothing
        } catch (NoSuchElementException ex) { // if the name was not encountered (= BAD)
            participantsStudentsTextField.setText("");

            searchedStudentIndex = -1;

            this.updateStudentsListModel(currentSchool); // #34
            this.updateStudentCodesListModel(currentSchool);

            participantsStudentsList.setSelectedIndex(searchedStudentIndex);
            participantsStudentCodesList.setSelectedIndex(searchedStudentIndex);
        } finally { // do in any case
            participantsStudentsTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsStudentsSearchButtonMouseReleased

    // hadling the event when the user makes a selection in the participantsStudentsList
    private void participantsStudentsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsStudentsListValueChanged
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        studentsIndex = participantsStudentsList.getSelectedIndex();

        participantsStudentCodesList.setSelectedIndex(studentsIndex); // #38
        participantsStudentCodesList.ensureIndexIsVisible(studentsIndex);

        if (studentsIndex != -1) { // #39
            Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex);

            participantsStudentsRemoveButton.setEnabled(true); // #28a
            participantsStudentsEditButton.setEnabled(true);

            if (!studentChanges && studentCodes) {
                participantsAssignEventsOriginalOratoryCheckBox.setEnabled(true); // #33
                participantsAssignEventsOriginalOratoryCheckBox.setSelected(currentStudent.getEvents()[0]);

                participantsAssignEventsOralInterpretationCheckBox.setEnabled(true);
                participantsAssignEventsOralInterpretationCheckBox.setSelected(currentStudent.getEvents()[1]);

                participantsAssignEventsImpromptuSpeakingCheckBox.setEnabled(true);
                participantsAssignEventsImpromptuSpeakingCheckBox.setSelected(currentStudent.getEvents()[2]);

                if (currentStudent.getDAStudentPair() != null) {
                    participantsAssignEventsDuetActingCheckBox.setEnabled(false);
                    participantsAssignEventsDuetActingCheckBox.setText("Duet Acting (paired)");
                }
                else {
                    participantsAssignEventsDuetActingCheckBox.setEnabled(true);
                    participantsAssignEventsDuetActingCheckBox.setText("Duet Acting");
                }
                participantsAssignEventsDuetActingCheckBox.setSelected(currentStudent.getEvents()[3]);

                if (currentStudent.getDebateStudentPair() != null) {
                    participantsAssignEventsDebateCheckBox.setEnabled(false);
                    participantsAssignEventsDebateCheckBox.setText("Debate (paired)");
                }
                else {
                    participantsAssignEventsDebateCheckBox.setEnabled(true);
                    participantsAssignEventsDebateCheckBox.setText("Debate");
                }
                participantsAssignEventsDebateCheckBox.setSelected(currentStudent.getEvents()[4]);

                // check-box-item-change events only allow the button if changes were made

                if (currentStudent.getReassignmentText() == true) participantsAssignEventsAssignEventsButton.setText("Re-assign events");
                else participantsAssignEventsAssignEventsButton.setText("Assign events");
            } else {
                participantsAssignEventsOriginalOratoryCheckBox.setEnabled(false); // #12
                participantsAssignEventsOriginalOratoryCheckBox.setSelected(false);

                participantsAssignEventsOralInterpretationCheckBox.setEnabled(false);
                participantsAssignEventsOralInterpretationCheckBox.setSelected(false);

                participantsAssignEventsImpromptuSpeakingCheckBox.setEnabled(false);
                participantsAssignEventsImpromptuSpeakingCheckBox.setSelected(false);

                participantsAssignEventsDuetActingCheckBox.setEnabled(false);
                participantsAssignEventsDuetActingCheckBox.setSelected(false);

                participantsAssignEventsDebateCheckBox.setEnabled(false);
                participantsAssignEventsDebateCheckBox.setSelected(false);

                participantsAssignEventsAssignEventsButton.setEnabled(false);

                participantsAssignEventsAssignEventsButton.setText("Assign events");
            }
        } else { // if studentsIndex == -1
            participantsStudentsRemoveButton.setEnabled(false); // #28a
            participantsStudentsEditButton.setEnabled(false);

            participantsAssignEventsOriginalOratoryCheckBox.setEnabled(false); // #12
            participantsAssignEventsOriginalOratoryCheckBox.setSelected(false);

            participantsAssignEventsOralInterpretationCheckBox.setEnabled(false);
            participantsAssignEventsOralInterpretationCheckBox.setSelected(false);

            participantsAssignEventsImpromptuSpeakingCheckBox.setEnabled(false);
            participantsAssignEventsImpromptuSpeakingCheckBox.setSelected(false);

            participantsAssignEventsDuetActingCheckBox.setEnabled(false);
            participantsAssignEventsDuetActingCheckBox.setSelected(false);

            participantsAssignEventsDebateCheckBox.setEnabled(false);
            participantsAssignEventsDebateCheckBox.setSelected(false);

            participantsAssignEventsAssignEventsButton.setEnabled(false);

            participantsAssignEventsAssignEventsButton.setText("Assign events");
        }
    }//GEN-LAST:event_participantsStudentsListValueChanged

    // Dialogs:
    // hadling the event when the user clicks the settingsAcceptDialogConfirmButton
    private void settingsAcceptDialogConfirmButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsAcceptDialogConfirmButtonMouseReleased
        settingsAcceptDialog.setVisible(false);
}//GEN-LAST:event_settingsAcceptDialogConfirmButtonMouseReleased

    // hadling the event when the user clicks the settingsErrorDialogConfirmButton
    private void settingsErrorDialogConfirmButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingsErrorDialogConfirmButtonMouseReleased
        settingsErrorDialog.setVisible(false);

        settingsErrorDialogTextArea.setText("");
}//GEN-LAST:event_settingsErrorDialogConfirmButtonMouseReleased

    // hadling the event when the user clicks the ExceptionDialogConfirmButton
    private void exceptionDialogConfirmButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exceptionDialogConfirmButtonMouseReleased
        exceptionDialog.setVisible(false);

        exceptionDialogTextArea.setText("");
    }//GEN-LAST:event_exceptionDialogConfirmButtonMouseReleased

    // hadling the event when the user clicks the illegalActionDialogConfirmButton
    private void illegalActionDialogConfirmButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_illegalActionDialogConfirmButtonMouseReleased
        illegalActionDialog.setVisible(false);

        illegalActionDialogTextArea.setText("");
    }//GEN-LAST:event_illegalActionDialogConfirmButtonMouseReleased

    // Student codes:
    // hadling the event when the user clicks the participantsStudentCodesAssignCodesButton
    private void participantsStudentCodesAssignCodesButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsStudentCodesAssignCodesButtonMouseReleased
        try {
            School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

            database.assignStudentCodes(studentsNumber);
            database.correctPairCodes(currentSchool);

            if (schoolsIndex != -1) {
                if (currentSchool.getStudentTreeSize() != 0) {
                    this.updateStudentCodesListModel(currentSchool); // #40

                    participantsStudentCodesList.setSelectedIndex(studentsIndex);
                    participantsStudentCodesList.ensureIndexIsVisible(studentsIndex);
                } else {
                    participantsStudentCodesList.setModel(emptyModel); // #20
                }
            }

            reassignmentText = true;
            studentCodes = true;
            studentChanges = false;

            participantsStudentCodesAssignCodesButton.setText("Re-assign codes");

            recheckStudentCodesActionBox();

            if (schoolsIndex != -1) {
                if ((((currentSchool.getUnpairedDAStudentTreeSize() / 2) > 0) || ((currentSchool.getUnpairedDebateStudentTreeSize() / 2) > 0)) && studentCodes) {
                    participantsAssignPairsEventsList.setSelectedIndex(-1);
                    participantsAssignPairsEventsList.setModel(eventsListModel);
                    participantsAssignPairsEventsList.setEnabled(true);
                } else if ((currentSchool.getDAPairTreeSize() > 0) || (currentSchool.getDebatePairTreeSize() > 0)) {
                    participantsAssignPairsEventsList.setSelectedIndex(-1);
                    participantsAssignPairsEventsList.setModel(eventsListModel);
                    participantsAssignPairsEventsList.setEnabled(true);
                } else {
                    participantsAssignPairsEventsList.setSelectedIndex(-1); // => disable everything
                    participantsAssignPairsEventsList.setModel(eventsListModel);
                    participantsAssignPairsEventsList.setEnabled(false);
                }
            }

            participantsAssignPairsLeftList.setSelectedIndex(-1);
            participantsAssignPairsLeftList.setModel(blankModel);
            participantsAssignPairsLeftList.setEnabled(false);

            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsRightList.setModel(blankModel);
            participantsAssignPairsRightList.setEnabled(false);

            participantsAssignPairsPairsList.setSelectedIndex(-1);
            participantsAssignPairsPairsList.setModel(blankModel);
            participantsAssignPairsPairsList.setEnabled(false);

            participantsAssignPairsPairButton.setEnabled(false);
            participantsAssignPairsRemovePairButton.setEnabled(false);
        } catch (IllegalStateException ex) {
            // do nothing
        }
    }//GEN-LAST:event_participantsStudentCodesAssignCodesButtonMouseReleased

    // hadling the event when the user clicks the participantsStudentCodesSearchButton
    private void participantsStudentCodesSearchButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsStudentCodesSearchButtonMouseReleased
        String searchedCode = participantsStudentCodesTextField.getText();

        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        int searchedStudentCodeSchoolIndex;
        int searchedStudentCodeIndex;

        try {
            participantsStudentCodesTextField.setText("");

            int[] searchedStudentCodeIndices = database.searchStudentCode(searchedCode); // should continue

            searchedStudentCodeSchoolIndex = searchedStudentCodeIndices[0]; // the schoolsList index

            School studentCodeSchool = (School) database.getSchoolTreeNodeData(searchedStudentCodeSchoolIndex);

            this.updateSchoolsListModel(); // #2

            participantsSchoolsList.setSelectedIndex(searchedStudentCodeSchoolIndex);
            participantsSchoolsList.ensureIndexIsVisible(searchedStudentCodeSchoolIndex);

            searchedStudentCodeIndex = searchedStudentCodeIndices[1]; // the studentsList index

            if (studentCodeSchool.getStudentTreeSize() != 0) {
                this.updateStudentsListModel(studentCodeSchool); // #18
                this.updateStudentCodesListModel(studentCodeSchool);
            } else { // if studentTree is empty
                participantsStudentsList.setModel(emptyModel); // #19a
                participantsStudentCodesList.setModel(emptyModel); // #20
            }

            participantsStudentsList.setSelectedIndex(searchedStudentCodeIndex); // #41
            participantsStudentsList.ensureIndexIsVisible(searchedStudentCodeIndex);

            participantsStudentCodesList.setSelectedIndex(searchedStudentCodeIndex); // #38
            participantsStudentCodesList.ensureIndexIsVisible(searchedStudentCodeIndex);
        } catch (IllegalArgumentException ex) { // if the name is an empty string (= BAD)
            // do nothing
        } catch (NoSuchElementException ex) { // if the name was not encountered (= BAD)
            participantsStudentsTextField.setText("");

            participantsStudentsList.setSelectedIndex(-1); // #41
            participantsStudentCodesList.setSelectedIndex(-1); // #38

            if (currentSchool.getStudentTreeSize() != 0) {
                this.updateStudentsListModel(currentSchool); // #18
                this.updateStudentCodesListModel(currentSchool);
            } else {
                participantsStudentsList.setModel(emptyModel); // #19a
                participantsStudentCodesList.setModel(emptyModel); // #20
            }
        } finally { // do in any case
            participantsStudentCodesTextField.grabFocus();
        }
    }//GEN-LAST:event_participantsStudentCodesSearchButtonMouseReleased

    // hadling the event when the user makes a selection in the participantsStudentCodesList
    private void participantsStudentCodesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsStudentCodesListValueChanged
        // as of the moment, the list is always disabled, but this might be changed upon consideration
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        studentCodesIndex = participantsStudentCodesList.getSelectedIndex();

        participantsStudentsList.setSelectedIndex(studentCodesIndex); // #41
        participantsStudentsList.ensureIndexIsVisible(studentCodesIndex);

        if (studentCodesIndex != -1) { // #39
            Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex);

            participantsStudentsRemoveButton.setEnabled(true); // #28a
            participantsStudentsEditButton.setEnabled(true);

            participantsAssignEventsOriginalOratoryCheckBox.setEnabled(true); // #33
            participantsAssignEventsOriginalOratoryCheckBox.setSelected(currentStudent.getEvents()[0]);

            participantsAssignEventsOralInterpretationCheckBox.setEnabled(true);
            participantsAssignEventsOralInterpretationCheckBox.setSelected(currentStudent.getEvents()[1]);

            participantsAssignEventsImpromptuSpeakingCheckBox.setEnabled(true);
            participantsAssignEventsImpromptuSpeakingCheckBox.setSelected(currentStudent.getEvents()[2]);

            if (currentStudent.getDAStudentPair() != null) {
                participantsAssignEventsDuetActingCheckBox.setEnabled(false);
                participantsAssignEventsDuetActingCheckBox.setText("Duet Acting (paired)");
            }
            else {
                participantsAssignEventsDuetActingCheckBox.setEnabled(true);
                participantsAssignEventsDuetActingCheckBox.setText("Duet Acting");
            }
            participantsAssignEventsDuetActingCheckBox.setSelected(currentStudent.getEvents()[3]);

            if (currentStudent.getDebateStudentPair() != null) {
                participantsAssignEventsDebateCheckBox.setEnabled(false);
                participantsAssignEventsDebateCheckBox.setText("Debate (paired)");
            }
            else {
                participantsAssignEventsDebateCheckBox.setEnabled(true);
                participantsAssignEventsDebateCheckBox.setText("Debate");
            }
            participantsAssignEventsDebateCheckBox.setSelected(currentStudent.getEvents()[4]);

            if (currentStudent.getReassignmentText() == true) participantsAssignEventsAssignEventsButton.setText("Re-assign events");
            else participantsAssignEventsAssignEventsButton.setText("Assign events");
        } else { // if studentCodesIndex == -1
            participantsStudentsRemoveButton.setEnabled(false); // #28a
            participantsStudentsEditButton.setEnabled(false);

            participantsAssignEventsOriginalOratoryCheckBox.setEnabled(false); // #12
            participantsAssignEventsOriginalOratoryCheckBox.setSelected(false);

            participantsAssignEventsOralInterpretationCheckBox.setEnabled(false);
            participantsAssignEventsOralInterpretationCheckBox.setSelected(false);

            participantsAssignEventsImpromptuSpeakingCheckBox.setEnabled(false);
            participantsAssignEventsImpromptuSpeakingCheckBox.setSelected(false);

            participantsAssignEventsDuetActingCheckBox.setEnabled(false);
            participantsAssignEventsDuetActingCheckBox.setSelected(false);

            participantsAssignEventsDebateCheckBox.setEnabled(false);
            participantsAssignEventsDebateCheckBox.setSelected(false);

            participantsAssignEventsAssignEventsButton.setEnabled(false);

            participantsAssignEventsAssignEventsButton.setText("Assign events");
        }
    }//GEN-LAST:event_participantsStudentCodesListValueChanged

    // Assign events:
    // hadling the event when the user clicks the participantsAssignEventsAssignEventsButton
    private void participantsAssignEventsAssignEventsButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignEventsAssignEventsButtonMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);
        Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex);

        boolean[] originalEvents = currentStudent.getEvents();

        boolean addedDAStudent = false;
        boolean removedDAStudent = false;

        boolean addedDebateStudent = false;
        boolean removedDebateStudent = false;

        boolean oo = participantsAssignEventsOriginalOratoryCheckBox.isSelected();
        boolean oi = participantsAssignEventsOralInterpretationCheckBox.isSelected();
        boolean is = participantsAssignEventsImpromptuSpeakingCheckBox.isSelected();
        boolean da = participantsAssignEventsDuetActingCheckBox.isSelected();
        boolean debate = participantsAssignEventsDebateCheckBox.isSelected();

        boolean[] newEvents = {oo, oi, is, da, debate};

        currentStudent.setEvents(newEvents);

        participantsAssignEventsAssignEventsButton.setText("Re-assign events");

        currentStudent.setReassignmentText(true);

        if (originalEvents[0] != newEvents[0] && oo == true) {
            currentSchool.incrementOOStudentNumber();
        } else if (originalEvents[0] != newEvents[0] && oo == false) {
            currentSchool.decrementOOStudentNumber();
        }

        if (originalEvents[1] != newEvents[1] && oi == true) {
            currentSchool.incrementOIStudentNumber();
        } else if (originalEvents[1] != newEvents[1] && oi == false) {
            currentSchool.decrementOIStudentNumber();
        }

        if (originalEvents[2] != newEvents[2] && is == true) {
            currentSchool.incrementISStudentNumber();
        } else if (originalEvents[2] != newEvents[2] && is == false) {
            currentSchool.decrementISStudentNumber();
        }

        if (originalEvents[3] != newEvents[3] && da == true) {
            if (currentStudent.getDAUnpaired() == false && currentStudent.getDAStudentPair() == null) {
                currentSchool.insertUnpairedDAStudent(currentStudent);
                currentStudent.setDAUnpaired(true);
                addedDAStudent = true;
            }
        } else if (originalEvents[3] != newEvents[3] && da == false) {
            if (currentStudent.getDAUnpaired() == true) {
                currentSchool.deleteUnpairedDAStudent(currentStudent.getName());
                currentStudent.setDAUnpaired(false);
            } else if (currentStudent.getDAStudentPair() != null) {
                Student deletedPairStudent = currentStudent.getDAStudentPair().getOtherStudent(currentStudent);
                currentSchool.deleteDAPair(currentStudent.getDAStudentPair().getOriginalCode());
                deletedPairStudent.setDAStudentPair(null);
                currentSchool.insertUnpairedDAStudent(deletedPairStudent);
                deletedPairStudent.setDAUnpaired(true);
            }
            removedDAStudent = true;
        }

        if (originalEvents[4] != newEvents[4] && debate == true) {
            if (currentStudent.getDebateUnpaired() == false && currentStudent.getDebateStudentPair() == null) {
                currentSchool.insertUnpairedDebateStudent(currentStudent);
                currentStudent.setDebateUnpaired(true);
                addedDebateStudent = true;
            }
        } else if (originalEvents[4] != newEvents[4] && debate == false) {
            if (currentStudent.getDebateUnpaired() == true) {
                currentSchool.deleteUnpairedDebateStudent(currentStudent.getName());
                currentStudent.setDebateUnpaired(false);
            } else if (currentStudent.getDebateStudentPair() != null) {
                Student deletedPairStudent = currentStudent.getDebateStudentPair().getOtherStudent(currentStudent);
                currentSchool.deleteDebatePair(currentStudent.getDebateStudentPair().getOriginalCode());
                deletedPairStudent.setDebateStudentPair(null);
                currentSchool.insertUnpairedDebateStudent(deletedPairStudent);
                deletedPairStudent.setDebateUnpaired(true);
            }
            removedDebateStudent = true;
        }

        if (studentsIndex + 1 < currentSchool.getStudentTreeSize()) {
            int newStudentIndex = studentsIndex + 1;

            this.updateStudentsListModel(currentSchool);
            this.updateStudentCodesListModel(currentSchool);
            participantsStudentsList.setSelectedIndex(newStudentIndex);
            participantsStudentsList.ensureIndexIsVisible(newStudentIndex);
            participantsStudentCodesList.setSelectedIndex(newStudentIndex);
            participantsStudentCodesList.ensureIndexIsVisible(newStudentIndex);
        } else {
            int newSchoolIndex = (schoolsIndex + 1 < database.getSchoolTreeSize() ? (schoolsIndex + 1) : 0);

            this.updateSchoolsListModel();
            participantsSchoolsList.setSelectedIndex(newSchoolIndex);
            participantsSchoolsList.ensureIndexIsVisible(newSchoolIndex);

            currentSchool = (School) database.getSchoolTreeNodeData(newSchoolIndex);

            this.updateTeachersListModel(currentSchool);
            participantsTeachersList.setSelectedIndex(-1);

            int newStudentIndex = 0;

            this.updateStudentsListModel(currentSchool);
            this.updateStudentCodesListModel(currentSchool);
            participantsStudentsList.setSelectedIndex(newStudentIndex);
            participantsStudentsList.ensureIndexIsVisible(newStudentIndex);
            participantsStudentCodesList.setSelectedIndex(newStudentIndex);
            participantsStudentCodesList.ensureIndexIsVisible(newStudentIndex);
        }

        if ((((currentSchool.getUnpairedDAStudentTreeSize() / 2) > 0) || ((currentSchool.getUnpairedDebateStudentTreeSize() / 2) > 0)) && studentCodes) {
            participantsAssignPairsEventsList.setSelectedIndex(-1);
            participantsAssignPairsEventsList.setModel(eventsListModel);
            participantsAssignPairsEventsList.setEnabled(true);
        } else if ((currentSchool.getDAPairTreeSize() > 0) || (currentSchool.getDebatePairTreeSize() > 0)) {
            participantsAssignPairsEventsList.setSelectedIndex(-1);
            participantsAssignPairsEventsList.setModel(eventsListModel);
            participantsAssignPairsEventsList.setEnabled(true);
        } else {
            participantsAssignPairsEventsList.setSelectedIndex(-1); // => disable everything
            participantsAssignPairsEventsList.setModel(eventsListModel);
            participantsAssignPairsEventsList.setEnabled(false);

            participantsAssignPairsLeftList.setSelectedIndex(-1);
            participantsAssignPairsLeftList.setModel(blankModel);
            participantsAssignPairsLeftList.setEnabled(false);

            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsRightList.setModel(blankModel);
            participantsAssignPairsRightList.setEnabled(false);

            participantsAssignPairsPairsList.setSelectedIndex(-1);
            participantsAssignPairsPairsList.setModel(blankModel);
            participantsAssignPairsPairsList.setEnabled(false);

            participantsAssignPairsPairButton.setEnabled(false);
            participantsAssignPairsRemovePairButton.setEnabled(false);
        }

        participantsAssignEventsAssignEventsButton.setEnabled(false);
    }//GEN-LAST:event_participantsAssignEventsAssignEventsButtonMouseReleased
    
    // Assign pairs:
    // hadling the event when the user makes a selection in the participantsAssignPairsEventsList
    private void participantsAssignPairsEventsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsAssignPairsEventsListValueChanged
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        assignPairsEventsIndex = participantsAssignPairsEventsList.getSelectedIndex();

        if (assignPairsEventsIndex == 0) { // Duet Acting
            if (currentSchool.getUnpairedDAStudentTreeSize() >= 2) { // if there are at least two unpaired DA students in the school
                // set the contents of left list accordingly
                participantsAssignPairsLeftList.setEnabled(true);
                this.updateAssignPairsLeftListModel(currentSchool);
            } else if (currentSchool.getUnpairedDAStudentTreeSize() == 1) { // if there is just one unpaired DA student in the school
                // write him down, but not allow him to be selected
                participantsAssignPairsLeftList.setEnabled(false);
                this.updateAssignPairsLeftListModel(currentSchool);
            } else { // if there are no unpaired DA students in the school
                // disable left list and ensure it is erased
                participantsAssignPairsLeftList.setEnabled(false);
                participantsAssignPairsLeftList.setModel(blankModel);
            }

            if (currentSchool.getDAPairTreeSize() >= 1) { // if there is at least one DA pair in the school
                // set the contents of the pairs list to reflect the fact
                participantsAssignPairsPairsList.setEnabled(true);
                this.updateAssignPairsPairsListModel(currentSchool);
            } else { // if there are no DA pairs to write down
                // disable the pairs list and erase it
                participantsAssignPairsPairsList.setEnabled(false);
                participantsAssignPairsPairsList.setModel(blankModel);
            }
        } else if (assignPairsEventsIndex == 1) { // Debate
            if (currentSchool.getUnpairedDebateStudentTreeSize() >= 2) { // if there are at least two unpaired debate students in the school
                // set the contents of left list accordingly
                participantsAssignPairsLeftList.setEnabled(true);
                this.updateAssignPairsLeftListModel(currentSchool);
            } else if (currentSchool.getUnpairedDebateStudentTreeSize() == 1) { // if there is just one unpaired debate student in the school
                // write him down, but not allow him to be selected
                participantsAssignPairsLeftList.setEnabled(false);
                this.updateAssignPairsLeftListModel(currentSchool);
            } else { // if there are no unpaired Debate students in the school
                // disable left list and ensure it is erased
                participantsAssignPairsLeftList.setEnabled(false);
                participantsAssignPairsLeftList.setModel(blankModel);
            }

            if (currentSchool.getDebatePairTreeSize() >= 1) { // if there is at least one debate pair in the school
                // set the contents of the pairs list to reflect the fact
                participantsAssignPairsPairsList.setEnabled(true);
                this.updateAssignPairsPairsListModel(currentSchool);
            } else { // if there are no debate pairs to write down
                // disable the pairs list and erase it
                participantsAssignPairsPairsList.setEnabled(false);
                participantsAssignPairsPairsList.setModel(blankModel);
            }
        } else { // if events list has the value of -1
            // THE PROPAGATION OF THINGS DOES NOT WORK !!! - FIXED?
            participantsAssignPairsLeftList.setSelectedIndex(-1);
            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsPairButton.setEnabled(false);

            participantsAssignPairsPairsList.setSelectedIndex(-1);
            participantsAssignPairsRemovePairButton.setEnabled(false);
        }
    }//GEN-LAST:event_participantsAssignPairsEventsListValueChanged

    // hadling the event when the user makes a selection in the participantsAssignPairsLeftList
    private void participantsAssignPairsLeftListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsAssignPairsLeftListValueChanged
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        assignPairsLeftIndex = participantsAssignPairsLeftList.getSelectedIndex();

        if (assignPairsLeftIndex != -1) {
            Student[] studentArray;

            if (assignPairsEventsIndex == 0) { // DA
                // set the contents of right list
                participantsAssignPairsRightList.setEnabled(true);
                this.updateAssignPairsRightListModel(currentSchool);
            } else { // DEBATE
                // set the contents of right list
                participantsAssignPairsRightList.setEnabled(true);
                this.updateAssignPairsRightListModel(currentSchool);
            }
        } else { // if leftIndex is -1
            // erase contents of right list
            participantsAssignPairsRightList.setEnabled(false);
            participantsAssignPairsRightList.setModel(blankModel);

            // THE PROPAGATION OF THINGS DOES NOT WORK !!! - FIXED?
            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsPairButton.setEnabled(false);
        }
    }//GEN-LAST:event_participantsAssignPairsLeftListValueChanged

    // hadling the event when the user makes a selection in the participantsAssignPairsRightList
    private void participantsAssignPairsRightListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsAssignPairsRightListValueChanged
        assignPairsRightIndex = participantsAssignPairsRightList.getSelectedIndex();

        // THE PROPAGATION OF THINGS DOES NOT WORK !!! - FIXED?
        if (assignPairsRightIndex != -1) {
            participantsAssignPairsPairButton.setEnabled(true);
            //participantsAssignPairsPairsList.setSelectedIndex(-1); // => disable remove pair button
        } else {
            participantsAssignPairsPairButton.setEnabled(false);
        }
    }//GEN-LAST:event_participantsAssignPairsRightListValueChanged

    // hadling the event when the user clicks the participantsAssignPairsPairButton
    private void participantsAssignPairsPairButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignPairsPairButtonMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        Student[] unpairedStudentArray;
        StudentPair[] studentPairArray;

        if (assignPairsEventsIndex == 0) { // DA - FIXED
            Student leftStudent = (Student) currentSchool.getUnpairedDAStudentTreeNodeData(assignPairsLeftIndex);

            Student rightStudent;
            if (assignPairsLeftIndex > assignPairsRightIndex) { // if left comes after right (missing piece in right is beyond selection)
                rightStudent = (Student) currentSchool.getUnpairedDAStudentTreeNodeData(assignPairsRightIndex);
            } else { // if right comes at or after the missing piece, we must compensate for the erroneous indexes
                rightStudent = (Student) currentSchool.getUnpairedDAStudentTreeNodeData(assignPairsRightIndex + 1);
            }

            currentSchool.deleteUnpairedDAStudent(leftStudent.getName()); // remove the two unpaired students
            currentSchool.deleteUnpairedDAStudent(rightStudent.getName());
            unpairedStudentArray = currentSchool.getUnpairedDAStudentArray();

            DAStudentPair newDAPair = new DAStudentPair(rightStudent, leftStudent);
            currentSchool.insertDAPair(newDAPair); // add the newly-made pair
            studentPairArray = currentSchool.getDAPairArray();
            int newStudentPairIndex = currentSchool.searchDAPair(newDAPair.getCode()); // the index of the new DA pair in the array

            // FIXED
            if (currentSchool.getUnpairedDAStudentTreeSize() >= 2) { // still more than two DA unpaired students
                participantsAssignPairsLeftList.setSelectedIndex(-1);
                participantsAssignPairsRightList.setSelectedIndex(-1);
                
                DefaultListModel model;

                // left list update:
                model = new DefaultListModel();
                for (int i = 0; i < unpairedStudentArray.length; i++) {
                    model.addElement(unpairedStudentArray[i].getCode());
                }
                participantsAssignPairsLeftList.setModel(model);

                // right list update:
                model = new DefaultListModel();
                for (int i = 0; i < unpairedStudentArray.length; i++) {
                    if (i == assignPairsLeftIndex) continue;
                    else model.addElement(unpairedStudentArray[i].getCode());
                }
                participantsAssignPairsRightList.setModel(model);

                participantsAssignPairsLeftList.setSelectedIndex(0);
                participantsAssignPairsRightList.setSelectedIndex(0);

                // pairs list enablement and update
                participantsAssignPairsPairsList.setEnabled(true);

                model = new DefaultListModel();
                for (int i = 0; i < studentPairArray.length; i++) {
                    model.addElement(studentPairArray[i].getCode());
                }
                participantsAssignPairsPairsList.setModel(model);

                participantsAssignPairsPairsList.setSelectedIndex(newStudentPairIndex);
            } else { // less than two unpaired DA students
                participantsAssignPairsLeftList.setSelectedIndex(-1);
                participantsAssignPairsRightList.setSelectedIndex(-1);

                DefaultListModel model = new DefaultListModel();

                // left list update:
                model = new DefaultListModel();
                for (int i = 0; i < unpairedStudentArray.length; i++) {
                    model.addElement(unpairedStudentArray[i].getCode());
                }
                participantsAssignPairsLeftList.setModel(model);

                // right list update:
                model = new DefaultListModel();
                participantsAssignPairsRightList.setModel(model);

                // disablement of the pairs list
                participantsAssignPairsLeftList.setEnabled(false);
                participantsAssignPairsRightList.setEnabled(false);

                // pairs list enablement and update
                participantsAssignPairsPairsList.setEnabled(true);

                model = new DefaultListModel();
                for (int i = 0; i < studentPairArray.length; i++) {
                    model.addElement(studentPairArray[i].getCode());
                }
                participantsAssignPairsPairsList.setModel(model);

                participantsAssignPairsPairsList.setSelectedIndex(newStudentPairIndex);
            }
            
            participantsAssignPairsRemovePairButton.setEnabled(true);
        } else if (assignPairsEventsIndex == 1) { // DEBATE = FIXED
            Student leftStudent = (Student) currentSchool.getUnpairedDebateStudentTreeNodeData(assignPairsLeftIndex);

            Student rightStudent;
            if (assignPairsLeftIndex > assignPairsRightIndex) { // if left comes after right (missing piece in right is beyond selection)
                rightStudent = (Student) currentSchool.getUnpairedDebateStudentTreeNodeData(assignPairsRightIndex);
            } else { // if right comes at or after the missing piece, we must compensate for the erroneous indexes
                rightStudent = (Student) currentSchool.getUnpairedDebateStudentTreeNodeData(assignPairsRightIndex + 1);
            }

            currentSchool.deleteUnpairedDebateStudent(leftStudent.getName()); // remove the two unpaired students
            currentSchool.deleteUnpairedDebateStudent(rightStudent.getName());
            unpairedStudentArray = currentSchool.getUnpairedDebateStudentArray();

            DebateStudentPair newDebatePair = new DebateStudentPair(rightStudent, leftStudent);
            currentSchool.insertDebatePair(newDebatePair); // add the newly-made pair
            studentPairArray = currentSchool.getDebatePairArray();
            int newStudentPairIndex = currentSchool.searchDebatePair(newDebatePair.getCode()); // the index of the new Debate pair in the array

            // FIXED
            if (currentSchool.getUnpairedDebateStudentTreeSize() >= 2) { // still more than two Debate unpaired students
                participantsAssignPairsLeftList.setSelectedIndex(-1);
                participantsAssignPairsRightList.setSelectedIndex(-1);

                DefaultListModel model;

                // left list update:
                model = new DefaultListModel();
                for (int i = 0; i < unpairedStudentArray.length; i++) {
                    model.addElement(unpairedStudentArray[i].getCode());
                }
                participantsAssignPairsLeftList.setModel(model);

                // right list update:
                model = new DefaultListModel();
                for (int i = 0; i < unpairedStudentArray.length; i++) {
                    if (i == assignPairsLeftIndex) continue;
                    else model.addElement(unpairedStudentArray[i].getCode());
                }
                participantsAssignPairsRightList.setModel(model);

                participantsAssignPairsLeftList.setSelectedIndex(0);
                participantsAssignPairsRightList.setSelectedIndex(0);

                // pairs list enablement and update
                participantsAssignPairsPairsList.setEnabled(true);

                model = new DefaultListModel();
                for (int i = 0; i < studentPairArray.length; i++) {
                    model.addElement(studentPairArray[i].getCode());
                }
                participantsAssignPairsPairsList.setModel(model);

                participantsAssignPairsPairsList.setSelectedIndex(newStudentPairIndex);
            } else { // less than two unpaired Debate students
                participantsAssignPairsLeftList.setSelectedIndex(-1);
                participantsAssignPairsRightList.setSelectedIndex(-1);

                DefaultListModel model = new DefaultListModel();

                // left list update:
                model = new DefaultListModel();
                for (int i = 0; i < unpairedStudentArray.length; i++) {
                    model.addElement(unpairedStudentArray[i].getCode());
                }
                participantsAssignPairsLeftList.setModel(model);

                // right list update:
                model = new DefaultListModel();
                participantsAssignPairsRightList.setModel(model);

                // disablement of the left and right list
                participantsAssignPairsLeftList.setEnabled(false);
                participantsAssignPairsRightList.setEnabled(false);

                // pairs list enablement and update
                participantsAssignPairsPairsList.setEnabled(true);

                model = new DefaultListModel();
                for (int i = 0; i < studentPairArray.length; i++) {
                    model.addElement(studentPairArray[i].getCode());
                }
                participantsAssignPairsPairsList.setModel(model);

                participantsAssignPairsPairsList.setSelectedIndex(newStudentPairIndex);
            }

            participantsAssignPairsRemovePairButton.setEnabled(true);
        }

        participantsStudentsList.setSelectedIndex(-1);
        participantsStudentsList.setSelectedIndex(-1);
        
        this.updateStudentsListModel(currentSchool);
        this.updateStudentCodesListModel(currentSchool);
    }//GEN-LAST:event_participantsAssignPairsPairButtonMouseReleased

    // hadling the event when the user makes a selection in the participantsAssignPairsPairsList
    private void participantsAssignPairsPairsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_participantsAssignPairsPairsListValueChanged
        assignPairsPairsIndex = participantsAssignPairsPairsList.getSelectedIndex();

        // THE PROPAGATION OF THINGS DOES NOT WORK !!!
        if (assignPairsPairsIndex != -1) {
            participantsAssignPairsRemovePairButton.setEnabled(true);

            // participantsAssignPairsLeftList.setSelectedIndex(-1); // => right(-1) => disable pair button
        } else {
            participantsAssignPairsRemovePairButton.setEnabled(false);
        }
    }//GEN-LAST:event_participantsAssignPairsPairsListValueChanged

    // hadling the event when the user clicks the participantsAssignPairsRemovePairButton
    private void participantsAssignPairsRemovePairButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignPairsRemovePairButtonMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);

        if (assignPairsEventsIndex == 0) { // DA - FIXED
            DAStudentPair deletedPair = (DAStudentPair) currentSchool.deleteDAPair(assignPairsPairsIndex);

            Student[] pairStudentArray = deletedPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            currentSchool.insertUnpairedDAStudent(leftStudent);

            Student rightStudent = pairStudentArray[1];
            currentSchool.insertUnpairedDAStudent(rightStudent);

            // LIST UPDATES:
            Student [] unpairedStudentArray = currentSchool.getUnpairedDAStudentArray();
            DAStudentPair [] studentPairArray = currentSchool.getDAPairArray();

            DefaultListModel model = new DefaultListModel();

            // left list enablement and update:
            participantsAssignPairsLeftList.setEnabled(true);

            model = new DefaultListModel();
            for (int i = 0; i < unpairedStudentArray.length; i++) {
                model.addElement(unpairedStudentArray[i].getCode());
            }
            participantsAssignPairsLeftList.setModel(model);

            int leftStudentIndex = currentSchool.searchUnpairedDAStudent(leftStudent.getName());
            participantsAssignPairsLeftList.setSelectedIndex(leftStudentIndex);

            // right list enablement and update:
            participantsAssignPairsRightList.setEnabled(true);

            model = new DefaultListModel();
            for (int i = 0; i < unpairedStudentArray.length; i++) {
                if (i == assignPairsLeftIndex) continue;
                else model.addElement(unpairedStudentArray[i].getCode());
            }
            participantsAssignPairsRightList.setModel(model);

            int rightStudentIndex = currentSchool.searchUnpairedDAStudent(rightStudent.getName()) - 1; // it is beyond left, correction needed
            participantsAssignPairsRightList.setSelectedIndex(rightStudentIndex);

            // pairs list update
            model = new DefaultListModel();
            for (int i = 0; i < studentPairArray.length; i++) {
                model.addElement(studentPairArray[i].getCode());
            }
            participantsAssignPairsPairsList.setModel(model);

            if (studentPairArray.length >= 1) {
                participantsAssignPairsPairsList.setEnabled(true);
                participantsAssignPairsPairsList.setSelectedIndex(0);
            } else {
                participantsAssignPairsPairsList.setEnabled(false);
                participantsAssignPairsPairsList.setSelectedIndex(-1);
            }
        } else if (assignPairsEventsIndex == 1) { // DEBATE
            DebateStudentPair deletedPair = (DebateStudentPair) currentSchool.deleteDebatePair(assignPairsPairsIndex);

            Student[] pairStudentArray = deletedPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            currentSchool.insertUnpairedDebateStudent(leftStudent);
            leftStudent.setDebateUnpaired(true);
            leftStudent.setDebateStudentPair(null);

            Student rightStudent = pairStudentArray[1];
            currentSchool.insertUnpairedDebateStudent(rightStudent);
            rightStudent.setDebateUnpaired(true);
            rightStudent.setDebateStudentPair(null);

            // LIST UPDATES:
            Student [] unpairedStudentArray = currentSchool.getUnpairedDebateStudentArray();
            DebateStudentPair [] studentPairArray = currentSchool.getDebatePairArray();

            DefaultListModel model = new DefaultListModel();

            // left list enablement and update:
            participantsAssignPairsLeftList.setEnabled(true);

            model = new DefaultListModel();
            for (int i = 0; i < unpairedStudentArray.length; i++) {
                model.addElement(unpairedStudentArray[i].getCode());
            }
            participantsAssignPairsLeftList.setModel(model);

            int leftStudentIndex = currentSchool.searchUnpairedDebateStudent(leftStudent.getName());
            participantsAssignPairsLeftList.setSelectedIndex(leftStudentIndex);

            // right list enablement and update:
            participantsAssignPairsRightList.setEnabled(true);

            model = new DefaultListModel();
            for (int i = 0; i < unpairedStudentArray.length; i++) {
                if (i == assignPairsLeftIndex) continue;
                else model.addElement(unpairedStudentArray[i].getCode());
            }
            participantsAssignPairsRightList.setModel(model);

            int rightStudentIndex = currentSchool.searchUnpairedDebateStudent(rightStudent.getName()) - 1; // it is beyond left, correction needed
            participantsAssignPairsRightList.setSelectedIndex(rightStudentIndex);

            // pairs list update
            model = new DefaultListModel();
            for (int i = 0; i < studentPairArray.length; i++) {
                model.addElement(studentPairArray[i].getCode());
            }
            participantsAssignPairsPairsList.setModel(model);

            if (studentPairArray.length >= 1) {
                participantsAssignPairsPairsList.setEnabled(true);
                participantsAssignPairsPairsList.setSelectedIndex(0);
            } else {
                participantsAssignPairsPairsList.setEnabled(false);
                participantsAssignPairsPairsList.setSelectedIndex(-1);
            }
        }

        participantsStudentsList.setSelectedIndex(-1);
        participantsStudentsList.setSelectedIndex(-1);

        this.updateStudentsListModel(currentSchool);
        this.updateStudentCodesListModel(currentSchool);
    }//GEN-LAST:event_participantsAssignPairsRemovePairButtonMouseReleased
    
    // Assign events continued - checkboxes:
    // hadling the event when the user clicks the participantsAssignEventsOriginalOratoryCheckBox
    private void participantsAssignEventsOriginalOratoryCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignEventsOriginalOratoryCheckBoxMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);
        Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex);

        boolean [] originalEvents = new boolean[5];
        for (int i = 0; i < 5; i++) { // change-protected clone of the events array before changes
            originalEvents[i] = currentStudent.getEvents()[i] ? true : false;
        }

        boolean [] newEvents = new boolean[5]; // the events array after the changes
        newEvents[0] = participantsAssignEventsOriginalOratoryCheckBox.isSelected();
        newEvents[1] = participantsAssignEventsOralInterpretationCheckBox.isSelected();
        newEvents[2] = participantsAssignEventsImpromptuSpeakingCheckBox.isSelected();
        newEvents[3] = participantsAssignEventsDuetActingCheckBox.isSelected();
        newEvents[4] = participantsAssignEventsDebateCheckBox.isSelected();

        boolean changesMade = false;

        for (int i = 0; i < 5; i++) {
            if (newEvents[i] != originalEvents[i]) {
                changesMade = true;
                break;
            }
        }

        participantsAssignEventsAssignEventsButton.setEnabled(changesMade); // set enabled if changes were made
    }//GEN-LAST:event_participantsAssignEventsOriginalOratoryCheckBoxMouseReleased

    // hadling the event when the user clicks the participantsAssignEventsOralInterpretationCheckBox
    private void participantsAssignEventsOralInterpretationCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignEventsOralInterpretationCheckBoxMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);
        Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex);

        boolean [] originalEvents = new boolean[5];
        for (int i = 0; i < 5; i++) { // change-protected clone of the events array before changes
            originalEvents[i] = currentStudent.getEvents()[i] ? true : false;
        }

        boolean [] newEvents = new boolean[5]; // the events array after the changes
        newEvents[0] = participantsAssignEventsOriginalOratoryCheckBox.isSelected();
        newEvents[1] = participantsAssignEventsOralInterpretationCheckBox.isSelected();
        newEvents[2] = participantsAssignEventsImpromptuSpeakingCheckBox.isSelected();
        newEvents[3] = participantsAssignEventsDuetActingCheckBox.isSelected();
        newEvents[4] = participantsAssignEventsDebateCheckBox.isSelected();

        boolean changesMade = false;

        for (int i = 0; i < 5; i++) {
            if (newEvents[i] != originalEvents[i]) {
                changesMade = true;
                break;
            }
        }

        participantsAssignEventsAssignEventsButton.setEnabled(changesMade); // set enabled if changes were made
    }//GEN-LAST:event_participantsAssignEventsOralInterpretationCheckBoxMouseReleased

    // hadling the event when the user clicks the participantsAssignEventsImpromptuSpeakingCheckBox
    private void participantsAssignEventsImpromptuSpeakingCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignEventsImpromptuSpeakingCheckBoxMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);
        Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex);

        boolean [] originalEvents = new boolean[5];
        for (int i = 0; i < 5; i++) { // change-protected clone of the events array before changes
            originalEvents[i] = currentStudent.getEvents()[i] ? true : false;
        }

        boolean [] newEvents = new boolean[5]; // the events array after the changes
        newEvents[0] = participantsAssignEventsOriginalOratoryCheckBox.isSelected();
        newEvents[1] = participantsAssignEventsOralInterpretationCheckBox.isSelected();
        newEvents[2] = participantsAssignEventsImpromptuSpeakingCheckBox.isSelected();
        newEvents[3] = participantsAssignEventsDuetActingCheckBox.isSelected();
        newEvents[4] = participantsAssignEventsDebateCheckBox.isSelected();

        boolean changesMade = false;

        for (int i = 0; i < 5; i++) {
            if (newEvents[i] != originalEvents[i]) {
                changesMade = true;
                break;
            }
        }

        participantsAssignEventsAssignEventsButton.setEnabled(changesMade); // set enabled if changes were made
    }//GEN-LAST:event_participantsAssignEventsImpromptuSpeakingCheckBoxMouseReleased

    // hadling the event when the user clicks the participantsAssignEventsDuetActingCheckBox
    private void participantsAssignEventsDuetActingCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignEventsDuetActingCheckBoxMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);
        Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex);

        boolean [] originalEvents = new boolean[5];
        for (int i = 0; i < 5; i++) { // change-protected clone of the events array before changes
            originalEvents[i] = currentStudent.getEvents()[i] ? true : false;
        }

        boolean [] newEvents = new boolean[5]; // the events array after the changes
        newEvents[0] = participantsAssignEventsOriginalOratoryCheckBox.isSelected();
        newEvents[1] = participantsAssignEventsOralInterpretationCheckBox.isSelected();
        newEvents[2] = participantsAssignEventsImpromptuSpeakingCheckBox.isSelected();
        newEvents[3] = participantsAssignEventsDuetActingCheckBox.isSelected();
        newEvents[4] = participantsAssignEventsDebateCheckBox.isSelected();

        boolean changesMade = false;

        for (int i = 0; i < 5; i++) {
            if (newEvents[i] != originalEvents[i]) {
                changesMade = true;
                break;
            }
        }

        participantsAssignEventsAssignEventsButton.setEnabled(changesMade); // set enabled if changes were made
    }//GEN-LAST:event_participantsAssignEventsDuetActingCheckBoxMouseReleased

    // hadling the event when the user clicks the participantsAssignEventsDebateCheckBox
    private void participantsAssignEventsDebateCheckBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsAssignEventsDebateCheckBoxMouseReleased
        School currentSchool = (School) database.getSchoolTreeNodeData(schoolsIndex);
        Student currentStudent = (Student) currentSchool.getStudentTreeNodeData(studentsIndex); // THIS IS PRONE TO PROBLEMS

        boolean [] originalEvents = new boolean[5];
        for (int i = 0; i < 5; i++) { // change-protected clone of the events array before changes
            originalEvents[i] = currentStudent.getEvents()[i] ? true : false;
        }

        boolean [] newEvents = new boolean[5]; // the events array after the changes
        newEvents[0] = participantsAssignEventsOriginalOratoryCheckBox.isSelected();
        newEvents[1] = participantsAssignEventsOralInterpretationCheckBox.isSelected();
        newEvents[2] = participantsAssignEventsImpromptuSpeakingCheckBox.isSelected();
        newEvents[3] = participantsAssignEventsDuetActingCheckBox.isSelected();
        newEvents[4] = participantsAssignEventsDebateCheckBox.isSelected();

        boolean changesMade = false;

        for (int i = 0; i < 5; i++) {
            if (newEvents[i] != originalEvents[i]) {
                changesMade = true;
                break;
            }
        }

        participantsAssignEventsAssignEventsButton.setEnabled(changesMade); // set enabled if changes were made
    }//GEN-LAST:event_participantsAssignEventsDebateCheckBoxMouseReleased

    // Technical buttons:
    // hadling the event when the user clicks the participantsExportButton
    private void participantsExportButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsExportButtonMouseReleased
        try {
            this.exportParticipantsActionBox();
        } catch (UserIOException ex) {
            // do nothing
        } catch (IOException ex) {
            exceptionDialogTextArea.setText("An unexpected error occurred "
                    + "during the accessing of the specified file. Data was "
                    + "not exported. Please try again.");

            exceptionDialog.setVisible(true);
        }
    }//GEN-LAST:event_participantsExportButtonMouseReleased

    // hadling the event when the user clicks the participantsResetButton
    private void participantsResetButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsResetButtonMouseReleased
        resetWarningDialogTextArea.setText("You clicked the Reset button, which "
                + "means that you are about to erase all new content added to "
                + "the database since the latest file-load. (Note that this "
                + "applies globally - contents of all tabs will be affected.) "
                + "If you click confirm, all of this data will be irrevocably "
                + "erased. Click Confirm now only if you are sure this is "
                + "exactly what you want to do. Otherwise, click Cancel.");

        resetWarningDialog.setVisible(true);
    }//GEN-LAST:event_participantsResetButtonMouseReleased

    // hadling the event when the user clicks the participantsApproveToggleButton
    private void participantsApproveToggleButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsApproveToggleButtonMouseReleased
        if (participantsApproveToggleButton.isSelected()) {
            // SHOW (= in a window) INFORMATION ABOUT THE THINGS THAT DO NOT GO ACCORDNING TO THE SETTINGS

            // IF THAT IS IGNORED, APPROVE IT AND DESELECT EVERYTHING APART FROM THE SAVE BUTTON
            String approveText = approveParticipants(database);

            approveParticipantsDialogTextArea.setText(approveText);
            approveParticipantsDialog.setVisible(true);
        } else { // if the approve button is deselected
            this.enableParticipantsTabActionBox(); // QUALIFICATION IS DISABLED AS WELL
        }
    }//GEN-LAST:event_participantsApproveToggleButtonMouseReleased

    // hadling the event when the user clicks the participantsSaveToggleButton
    private void participantsSaveToggleButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_participantsSaveToggleButtonMouseReleased
        if (participantsSaveToggleButton.isSelected()) {
            try {
                approvedSectionsIndex = 1;

                this.saveFile();

                participantsApproveToggleButton.setEnabled(false);
            } catch (UserIOException ex) {
                participantsSaveToggleButton.setSelected(false);
            } catch (IOException ex) {
                participantsSaveToggleButton.setSelected(false);

                exceptionDialogTextArea.setText("An input/output exception has "
                        + "occurred during the saving of the file. Try to save "
                        + "the file once more.");

                exceptionDialog.setVisible(true);
            }
        } else {
            participantsApproveToggleButton.setEnabled(true);
        }
    }//GEN-LAST:event_participantsSaveToggleButtonMouseReleased

    // Technical dialogs:
    // hadling the event when the user clicks the resetWarningDialogConfirmButton
    private void resetWarningDialogConfirmButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetWarningDialogConfirmButtonMouseReleased
        resetWarningDialog.setVisible(false);

        resetWarningDialogTextArea.setText("");

        try {
            if (loadFile != null) {
                this.readFile(loadFile);
                this.synchronizeTFSettings(database);

                participantsSchoolsList.setSelectedIndex(-1);
                this.updateSchoolsListModel();

                participantsSchoolsTextField.setEnabled(true);
                participantsSchoolsAddButton.setEnabled(true);

                if (schoolsNumber > 0) {
                    participantsSchoolsList.setEnabled(true);
                    participantsSchoolsSearchButton.setEnabled(true);
                }

                if (teachersNumber > 0) {
                    participantsTeachersTextField.setEnabled(true);
                    participantsTeachersSearchButton.setEnabled(true);
                }

                if (studentsNumber > 0) {
                    participantsStudentsTextField.setEnabled(true);
                    participantsStudentsSearchButton.setEnabled(true);
                }

                recheckStudentCodesActionBox();

                if (studentCodes) {
                    participantsStudentCodesTextField.setEnabled(true);
                    participantsStudentCodesSearchButton.setEnabled(true);
                }
                if (studentChanges) {
                    participantsStudentCodesAssignCodesButton.setEnabled(true);
                }
            } else {
                this.resetDatabaseSettings(database);
                this.synchronizeTFSettings(database);

                database.eraseSchoolTree();

                currentNewNoNameSchoolNumber = 1;

                currentNewNoNameTeacherNumber = 1;

                currentNewNoNameStudentNumber = 1;

                schoolsNumber = 0;
                teachersNumber = 0;
                studentsNumber = 0;

                reassignmentText = false;
                studentCodes = false;
                studentChanges = false;

                schoolsIndex = -1;
                teachersIndex = -1;
                studentsIndex = -1;
                studentCodesIndex = -1;

                assignPairsEventsIndex = -1;
                assignPairsLeftIndex = -1;
                assignPairsRightIndex = -1;
                assignPairsPairsIndex = -1;

                participantsSchoolsList.setSelectedIndex(-1);
                participantsSchoolsList.setEnabled(false);
                participantsSchoolsList.setModel(emptyModel);

                participantsSchoolsRemoveButton.setEnabled(false);
                participantsSchoolsEditButton.setEnabled(false);
                participantsSchoolsSearchButton.setEnabled(false);
            }

            participantsTeachersTextField.setEnabled(false); // needed - #9

            participantsTeachersList.setSelectedIndex(-1);
            participantsTeachersList.setEnabled(false);
            participantsTeachersList.setModel(noSelectionModel);

            participantsTeachersAddButton.setEnabled(false);
            participantsTeachersRemoveButton.setEnabled(false);
            participantsTeachersSearchButton.setEnabled(false);
            participantsTeachersEditButton.setEnabled(false);

            participantsStudentsTextField.setEnabled(false); // needed - #10

            participantsStudentsList.setSelectedIndex(-1);
            participantsStudentsList.setEnabled(false);
            participantsStudentsList.setModel(noSelectionModel);

            participantsStudentsAddButton.setEnabled(false);
            participantsStudentsRemoveButton.setEnabled(false);
            participantsStudentsSearchButton.setEnabled(false);
            participantsStudentsEditButton.setEnabled(false);

            this.disableStudentCodesActionBox(); // #7
            participantsStudentCodesList.setModel(noSelectionModel);

            this.disableAssignEventsActionBox(); // #12

            participantsAssignPairsEventsList.setSelectedIndex(-1);
            participantsAssignPairsEventsList.setEnabled(false);
            participantsAssignPairsEventsList.setModel(eventsListModel);

            participantsAssignPairsLeftList.setSelectedIndex(-1);
            participantsAssignPairsLeftList.setEnabled(false);
            participantsAssignPairsLeftList.setModel(blankModel);

            participantsAssignPairsRightList.setSelectedIndex(-1);
            participantsAssignPairsRightList.setEnabled(false);
            participantsAssignPairsRightList.setModel(blankModel);

            participantsAssignPairsPairsList.setSelectedIndex(-1);
            participantsAssignPairsPairsList.setEnabled(false);
            participantsAssignPairsPairsList.setModel(blankModel);

            participantsAssignPairsPairButton.setEnabled(false);
            participantsAssignPairsRemovePairButton.setEnabled(false);
        } catch (FileIOException ex) {
            exceptionDialogTextArea.setText("The previous saved version of the "
                    + "database could not be located, possibly because of a "
                    + "filename or destination change. The operation could not "
                    + "be completed. Try to load the previous file manually "
                    + "using the Load button in the Settings tab.");

            exceptionDialog.setVisible(true);
        } catch (IOException ex) {
            exceptionDialogTextArea.setText("An input/output exception has "
                    + "occurred during the resetting of the file. Try to reset "
                    + "the file once more.");

            exceptionDialog.setVisible(true);
        }
    }//GEN-LAST:event_resetWarningDialogConfirmButtonMouseReleased

    // hadling the event when the user clicks the resetWarningDialogCancelButton
    private void resetWarningDialogCancelButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetWarningDialogCancelButtonMouseReleased
        resetWarningDialog.setVisible(false);

        resetWarningDialogTextArea.setText("");
    }//GEN-LAST:event_resetWarningDialogCancelButtonMouseReleased

    // hadling the event when the user clicks the approveParticipantsDialogIgnoreButton
    private void approveParticipantsDialogIgnoreButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approveParticipantsDialogIgnoreButtonMouseReleased
        long time1 = System.currentTimeMillis();

        try {
            qualification.deinitialize();
            qualification.initializeEvents();
            
            disableParticipantsTabActionBox();

            approveParticipantsDialog.setVisible(false);

            tabbedPane.setSelectedIndex(2);

            this.enableQualificationTabActionBox();

            this.updateQualificationProgressBar(100);
        } catch (Qualification.ImpossibleToAllocateException ex) {
            qualification.deinitialize();

            exceptionDialogTextArea.setText("The allocation of entities has "
                    + "failed. Please try again. It is possible, though, that "
                    + "there is no possible allocation arrangement for the "
                    + "database entries you have provided.");

            exceptionDialog.setVisible(true);
        } catch (Qualification.NotEnoughJudgesException ex) {
            qualification.deinitialize();

            exceptionDialogTextArea.setText("The allocation of entities has "
                    + "failed because of the lack of judges for some of the "
                    + "events. Please check the approve-participants warning "
                    + "message for more information.");

            exceptionDialog.setVisible(true);
        }

        long time2 = System.currentTimeMillis();
        System.out.println("Total calculation time: " + ((time2 - time1) / 1000) + " seconds");
    }//GEN-LAST:event_approveParticipantsDialogIgnoreButtonMouseReleased

    // hadling the event when the user clicks the approveParticipantsDialogCancelButton
    private void approveParticipantsDialogCancelButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_approveParticipantsDialogCancelButtonMouseReleased
        approveParticipantsDialog.setVisible(false);

        approveParticipantsDialogTextArea.setText("");
        
        participantsApproveToggleButton.setSelected(false);
    }//GEN-LAST:event_approveParticipantsDialogCancelButtonMouseReleased

// QUALIFICATION TAB:
    // Events:
    // hadling the event when the user makes a selection in the qualificationEventsList
    private void qualificationEventsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_qualificationEventsListValueChanged
        eventsIndex = qualificationEventsList.getSelectedIndex();

        if (eventsIndex != -1) { // if events index was set to be something (not -1)
            Event currentEvent = qualification.getEventArrayElement(eventsIndex);

            roundsIndex = -1;
            roomsIndex = -1;
            judgesIndex = -1;
            qStudentCodesIndex = -1;

            qualificationRoundsList.setEnabled(true);
            this.updateRoundsListModel(currentEvent);
        } else {
            qualificationRoundsList.setEnabled(false);
            qualificationRoundsList.setModel(noSelectionModel);            
        }

        qualificationRoundsTextField.setText("");
        qualificationRoundsTextField.setEnabled(false);
        qualificationRoundsEditNameButton.setEnabled(false);

        qualificationRoomsList.setEnabled(false);
        //qualificationRoomList.setSelectedIndex(-1);
        qualificationRoomsList.setModel(noSelectionModel);

        qualificationRoomsTextField.setText("");
        qualificationRoomsTextField.setEnabled(false);
        qualificationRoomsEditNameButton.setEnabled(false);

        qualificationJudgesList.setEnabled(false);
        //qualificationJudgesList.setSelectedIndex(-1);
        qualificationJudgesList.setModel(noSelectionModel);

        qualificationJudgesSubstituteJudgeButton.setEnabled(false);

        qualificationStudentCodesList.setEnabled(false);
        //qualificationStudentCodesList.setSelectedIndex(-1);
        qualificationStudentCodesList.setModel(noSelectionModel);

        qualificationStudentCodesTextField.setText("");
        qualificationStudentCodesTextField.setEnabled(false);
        qualificationStudentCodesSearchButton.setEnabled(false);
    }//GEN-LAST:event_qualificationEventsListValueChanged

    // Rounds:
    // hadling the event when the user clicks the qualificationRoundsEditNameButton
    private void qualificationRoundsEditNameButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationRoundsEditNameButtonMouseReleased
        Event currentEvent = qualification.getEventArrayElement(eventsIndex);
        Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);
        
        String newName = qualificationRoundsTextField.getText();

        if (!newName.equals("")) {
            currentRound.setName(newName);

            qualificationRoundsTextField.setText("");

            this.updateRoundsListModel(currentEvent);
        }
    }//GEN-LAST:event_qualificationRoundsEditNameButtonMouseReleased

    // hadling the event when the user makes a selection in the qualificationRoundsList
    private void qualificationRoundsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_qualificationRoundsListValueChanged
        roundsIndex = qualificationRoundsList.getSelectedIndex();

        if (roundsIndex != -1) { // if rounds index was set to be something (not -1)
            Event currentEvent = qualification.getEventArrayElement(eventsIndex);
            Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);

            roomsIndex = -1;
            judgesIndex = -1;
            qStudentCodesIndex = -1;

            qualificationRoundsTextField.setEnabled(true);
            qualificationRoundsEditNameButton.setEnabled(true);

            qualificationRoomsList.setEnabled(true);
            this.updateRoomsListModel(currentRound);

            qualificationStudentCodesTextField.setEnabled(true);
            qualificationStudentCodesSearchButton.setEnabled(true);
        } else {
            qualificationRoundsTextField.setText("");
            qualificationRoundsTextField.setEnabled(false);
            qualificationRoundsEditNameButton.setEnabled(false);

            qualificationRoomsList.setEnabled(false);
            //qualificationRoomList.setSelectedIndex(-1);
            qualificationRoomsList.setModel(noSelectionModel);

            qualificationStudentCodesTextField.setText("");
            qualificationStudentCodesTextField.setEnabled(false);
            qualificationStudentCodesSearchButton.setEnabled(false);
        }

        qualificationRoomsTextField.setText("");
        qualificationRoomsTextField.setEnabled(false);
        qualificationRoomsEditNameButton.setEnabled(false);

        qualificationJudgesList.setEnabled(false);
        //qualificationJudgesList.setSelectedIndex(-1);
        qualificationJudgesList.setModel(noSelectionModel);

        qualificationJudgesSubstituteJudgeButton.setEnabled(false);

        qualificationStudentCodesList.setEnabled(false);
        //qualificationStudentCodesList.setSelectedIndex(-1);
        qualificationStudentCodesList.setModel(noSelectionModel);
    }//GEN-LAST:event_qualificationRoundsListValueChanged

    // Rooms:
    // hadling the event when the user clicks the qualificationRoomsEditNameButton
    private void qualificationRoomsEditNameButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationRoomsEditNameButtonMouseReleased
        Event currentEvent = qualification.getEventArrayElement(eventsIndex);
        Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);
        Room currentRoom = currentRound.getRoomArrayElement(roomsIndex);

        String newName = qualificationRoomsTextField.getText();

        if (!newName.equals("")) {
            currentRoom.setName(newName);

            qualificationRoomsTextField.setText("");

            this.updateRoomsListModel(currentRound);
        }
    }//GEN-LAST:event_qualificationRoomsEditNameButtonMouseReleased

    // hadling the event when the user makes a selection in the qualificationRoomsList
    private void qualificationRoomsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_qualificationRoomsListValueChanged
        roomsIndex = qualificationRoomsList.getSelectedIndex();

        if (roomsIndex != -1) { // if rounds index was set to be something (not -1)
            Event currentEvent = qualification.getEventArrayElement(eventsIndex);
            Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);
            Room currentRoom = currentRound.getRoomArrayElement(roomsIndex);

            judgesIndex = -1;
            qStudentCodesIndex = -1;

            qualificationRoomsTextField.setEnabled(true);
            qualificationRoomsEditNameButton.setEnabled(true);

            qualificationJudgesList.setEnabled(true);
            this.updateJudgesListModel(currentRoom);

            qualificationStudentCodesList.setEnabled(true);
            this.updateQStudentCodesListModel(currentRoom);
        } else {
            qualificationRoomsTextField.setText("");
            qualificationRoomsTextField.setEnabled(false);
            qualificationRoomsEditNameButton.setEnabled(false);

            qualificationJudgesList.setEnabled(false);
            //qualificationJudgesList.setSelectedIndex(-1);
            qualificationJudgesList.setModel(noSelectionModel);

            qualificationStudentCodesList.setEnabled(false);
            //qualificationStudentCodesList.setSelectedIndex(-1);
            qualificationStudentCodesList.setModel(noSelectionModel);
        }

        qualificationJudgesSubstituteJudgeButton.setEnabled(false);
    }//GEN-LAST:event_qualificationRoomsListValueChanged

    // Judges:
    // hadling the event when the user clicks the qualificationJudgesSubstituteJudgeButton
    private void qualificationJudgesSubstituteJudgeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationJudgesSubstituteJudgeButtonMouseReleased
        if (judgesIndex != -1) { // if rounds index was set to be something (not -1)
            Event currentEvent = qualification.getEventArrayElement(eventsIndex);
            Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);
            Room currentRoom = currentRound.getRoomArrayElement(roomsIndex);
            Judge currentJudge = (Judge) currentRoom.getJudgeTreeNodeData(judgesIndex);

            Judge substituteJudge = null;
            boolean substituteJudgeFound = false;
            for (int i = 0; i < currentRound.getFreeJudgeTreeSize(); i++) {
                substituteJudge = currentRound.getFreeJudgeTreeNodeData(i);

                boolean otherJudgeEncountered = false; // SHOULD NOT THIS BE BLOCKED??
                for (int j = 0; j < currentRoom.getJudgeTreeSize(); j++) {
                    Judge otherJudge = (Judge) currentRoom.getJudgeTreeNodeData(j);

                    if (otherJudge.getID() == currentJudge.getID()) {
                        continue;
                    } else {
                        for (int k = 0; k < substituteJudge.getEncounteredJudgeTreeSize(); k++) {
                            Judge encounteredJudge = (Judge) substituteJudge.getEncounteredJudgeTreeNodeData(k);

                            if (encounteredJudge == otherJudge) {
                                otherJudgeEncountered = true;
                                break;
                                // double-break = a new substitute judge will be needed
                            }
                        }
                    }

                    if (otherJudgeEncountered) {
                        break; // take a new substitute judge
                    }
                }

                if (otherJudgeEncountered) {
                    continue;
                }

                boolean roomEntityEncountered = false;
                for (int j = 0; j < currentRoom.getEntityTreeSize(); j++) {
                    Entity roomEntity = (Entity) currentRoom.getEntityTreeNodeData(j);

                    for (int k = 0; k < roomEntity.getEncounteredJudgeTreeSize(); k++) {
                        Judge encounteredJudge = (Judge) roomEntity.getEncounteredJudgeTreeNodeData(k);

                        if (encounteredJudge == substituteJudge) {
                            roomEntityEncountered = true;
                            break;
                        }
                    }

                    if (roomEntityEncountered) {
                        break; // take a new substitute judge
                    }
                }

                if (roomEntityEncountered) {
                    continue;
                }

                substituteJudgeFound = true; // only accessed if substitute judge has not met any of the entities and judges
                break;
            }

            if (substituteJudgeFound) {
                currentRoom.removeJudge(currentJudge);
                for (int i = 0; i < currentRoom.getJudgeTreeSize(); i++) {
                    Judge otherJudge = (Judge) currentRoom.getJudgeTreeNodeData(i);
                    if (otherJudge.getID() == currentJudge.getID()) {
                        continue;
                    } else {
                        currentJudge.removeEncounteredJudge(otherJudge);
                        otherJudge.removeEncounteredJudge(currentJudge);
                    }
                }
                for (int i = 0; i < currentRoom.getEntityTreeSize(); i++) {
                    Entity roomEntity = (Entity) currentRoom.getEntityTreeNodeData(i);
                    roomEntity.removeEncounteredJudgeFromRound(currentJudge, roundsIndex);
                }
                currentRound.addFreeJudge(currentJudge);

                currentRound.removeFreeJudge(substituteJudge); // was initialized to null at the start
                for (int i = 0; i < currentRoom.getJudgeTreeSize(); i++) {
                    Judge roomJudge = (Judge) currentRoom.getJudgeTreeNodeData(i);
                    substituteJudge.addEncounteredJudge(roomJudge);
                    roomJudge.addEncounteredJudge(substituteJudge);
                }
                for (int i = 0; i < currentRoom.getEntityTreeSize(); i++) {
                    Entity roomEntity = (Entity) currentRoom.getEntityTreeNodeData(i);
                    roomEntity.addEncounteredJudgeToRound(substituteJudge, roundsIndex);
                }
                currentRoom.allocateJudge(substituteJudge);

            } else { // if no suitable substitute was found
                exceptionDialogTextArea.setText("No suitable substitute was found "
                        + "for the judge you selected. It might be necessary to "
                        + "enlist a new judge for this round or to break one of the "
                        + "allocation rules. Unfortunately, the program cannot help "
                        + "you with that.");

                exceptionDialog.setVisible(true);
            }
        } else {
            // the button should not be enabled
        }
        
        
        /*if (judgesIndex != -1) { // if rounds index was set to be something (not -1) // ORIGINAL VERSION
            Event currentEvent = qualification.getEventArrayElement(eventsIndex);
            Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);
            Room currentRoom = currentRound.getRoomArrayElement(roomsIndex);
            Judge currentJudge = (Judge) currentRoom.getJudgeTreeNodeData(judgesIndex);

            Judge substituteJudge = null;
            boolean substituteJudgeFound = false;
            for (int i = 0; i < currentRound.getFreeJudgeTreeSize(); i++) {
                substituteJudge = currentRound.getFreeJudgeTreeNodeData(i);

                boolean otherJudgeEncountered = false; // SHOULD NOT THIS BE BLOCKED??
                for (int j = 0; j < currentRoom.getJudgeTreeSize(); j++) {
                    Judge otherJudge = (Judge) currentRoom.getJudgeTreeNodeData(j);

                    if (otherJudge.getID() == currentJudge.getID()) {
                        continue;
                    } else {
                        for (int k = 0; k < substituteJudge.getEncounteredJudgeTreeSize(); k++) {
                            Judge encounteredJudge = (Judge) substituteJudge.getEncounteredJudgeTreeNodeData(k);

                            if (encounteredJudge == otherJudge) {
                                otherJudgeEncountered = true;
                                break;
                                // double-break = a new substitute judge will be needed
                            }
                        }
                    }

                    if (otherJudgeEncountered) {
                        break; // take a new substitute judge
                    }
                }

                if (otherJudgeEncountered) {
                    continue;
                }

                boolean roomEntityEncountered = false;
                for (int j = 0; j < currentRoom.getEntityTreeSize(); j++) {
                    Entity roomEntity = (Entity) currentRoom.getEntityTreeNodeData(j);

                    for (int k = 0; k < roomEntity.getEncounteredJudgeTreeSize(); k++) {
                        Judge encounteredJudge = (Judge) roomEntity.getEncounteredJudgeTreeNodeData(k);

                        if (encounteredJudge == substituteJudge) {
                            roomEntityEncountered = true;
                            break;
                        }
                    }

                    if (roomEntityEncountered) {
                        break; // take a new substitute judge
                    }
                }

                if (roomEntityEncountered) {
                    continue;
                }

                substituteJudgeFound = true; // only accessed if substitute judge has not met any of the entities and judges
                break;
            }

            if (substituteJudgeFound) {
                currentRoom.removeJudge(currentJudge);
                for (int i = 0; i < currentRoom.getJudgeTreeSize(); i++) {
                    Judge otherJudge = (Judge) currentRoom.getJudgeTreeNodeData(i);
                    if (otherJudge.getID() == currentJudge.getID()) {
                        continue;
                    } else {
                        currentJudge.removeEncounteredJudge(otherJudge);
                        otherJudge.removeEncounteredJudge(currentJudge);
                    }
                }
                for (int i = 0; i < currentRoom.getEntityTreeSize(); i++) {
                    Entity roomEntity = (Entity) currentRoom.getEntityTreeNodeData(i);
                    roomEntity.removeEncounteredJudgeFromRound(currentJudge, roundsIndex);
                }
                currentRound.addFreeJudge(currentJudge);

                currentRound.removeFreeJudge(substituteJudge); // was initialized to null at the start
                for (int i = 0; i < currentRoom.getJudgeTreeSize(); i++) {
                    Judge roomJudge = (Judge) currentRoom.getJudgeTreeNodeData(i);
                    substituteJudge.addEncounteredJudge(roomJudge);
                    roomJudge.addEncounteredJudge(substituteJudge);
                }
                for (int i = 0; i < currentRoom.getEntityTreeSize(); i++) {
                    Entity roomEntity = (Entity) currentRoom.getEntityTreeNodeData(i);
                    roomEntity.addEncounteredJudgeToRound(substituteJudge, roundsIndex);
                }
                currentRoom.allocateJudge(substituteJudge);

            } else { // if no suitable substitute was found
                exceptionDialogTextArea.setText("No suitable substitute was found "
                        + "for the judge you selected. It might be necessary to "
                        + "enlist a new judge for this round or to break one of the "
                        + "allocation rules. Unfortunately, the program cannot help "
                        + "you with that.");

                exceptionDialog.setVisible(true);
            }
        } else {
            // the button should not be enabled
        }*/
    }//GEN-LAST:event_qualificationJudgesSubstituteJudgeButtonMouseReleased

    // hadling the event when the user makes a selection in the qualificationJudgesList
    private void qualificationJudgesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_qualificationJudgesListValueChanged
        judgesIndex = qualificationJudgesList.getSelectedIndex();

        if (judgesIndex != -1) { // if rounds index was set to be something (not -1)
            Event currentEvent = qualification.getEventArrayElement(eventsIndex);
            Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);
            Room currentRoom = currentRound.getRoomArrayElement(roomsIndex);
            Judge currentJudge = (Judge) currentRoom.getJudgeTreeNodeData(judgesIndex);

            qualificationJudgesSubstituteJudgeButton.setEnabled(true);
        } else {
            qualificationJudgesSubstituteJudgeButton.setEnabled(false);
        }
    }//GEN-LAST:event_qualificationJudgesListValueChanged

    // Student Codes:
    // hadling the event when the user clicks the qualificationStudentCodesSearchButton
    private void qualificationStudentCodesSearchButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationStudentCodesSearchButtonMouseReleased
        String code = qualificationStudentCodesTextField.getText();

        Event currentEvent = qualification.getEventArrayElement(eventsIndex);
        Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);

        try {
            qualificationRoundsTextField.setText("");

            int[] searchIndices = currentRound.searchStudentCode(code);
            int searchedRoomIndex = searchIndices[0];
            int searchedEntityIndex = searchIndices[1];

            this.updateRoomsListModel(currentRound);
            qualificationRoomsList.setSelectedIndex(searchedRoomIndex);
            qualificationRoomsList.ensureIndexIsVisible(searchedRoomIndex);

            Room searchedRoom = currentRound.getRoomArrayElement(searchedRoomIndex);

            this.updateJudgesListModel(searchedRoom);

            this.updateQStudentCodesListModel(searchedRoom);
            qualificationStudentCodesList.setSelectedIndex(searchedEntityIndex);
            qualificationStudentCodesList.ensureIndexIsVisible(searchedEntityIndex);
        } catch (IllegalArgumentException ex) {
            // empty string – do nothing
        } catch (NoSuchElementException ex) {
            Room currentRoom = currentRound.getRoomArrayElement(roomsIndex);

            qualificationRoundsTextField.setText("");

            qualificationStudentCodesList.setSelectedIndex(-1);

            this.updateQStudentCodesListModel(currentRoom);
        } finally { // do in any case
            qualificationStudentCodesTextField.grabFocus();
        }
    }//GEN-LAST:event_qualificationStudentCodesSearchButtonMouseReleased

    // hadling the event when the user makes a selection in the qualificationStudentCodesList
    private void qualificationStudentCodesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_qualificationStudentCodesListValueChanged
        qStudentCodesIndex = qualificationStudentCodesList.getSelectedIndex();

        if (qStudentCodesIndex != -1) { // if rounds index was set to be something (not -1)
            Event currentEvent = qualification.getEventArrayElement(eventsIndex);
            Round currentRound = currentEvent.getRoundArrayElement(roundsIndex);
            Room currentRoom = currentRound.getRoomArrayElement(roomsIndex);
            Entity currentEntity = (Entity) currentRoom.getEntityTreeNodeData(qStudentCodesIndex);
        } else {

        }
    }//GEN-LAST:event_qualificationStudentCodesListValueChanged

    // Technical Buttons:
    // hadling the event when the user clicks the qualificationExportButton
    private void qualificationExportButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationExportButtonMouseReleased
        try {
            this.exportQualificationActionBox();
        } catch (UserIOException ex) {
            // do nothing
        } catch (IOException ex) {
            exceptionDialogTextArea.setText("An unexpected error occurred "
                    + "during the accessing of the specified file. Data was "
                    + "not exported. Please try again.");

            exceptionDialog.setVisible(true);
        }
    }//GEN-LAST:event_qualificationExportButtonMouseReleased

    // hadling the event when the user clicks the qualificationResetButton
    private void qualificationResetButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationResetButtonMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_qualificationResetButtonMouseReleased

    // hadling the event when the user clicks the qualificationSaveToggleButton
    private void qualificationSaveToggleButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qualificationSaveToggleButtonMouseReleased
        if (qualificationSaveToggleButton.isSelected()) {
            try {
                approvedSectionsIndex = 2;

                this.saveFile();

                // deactivate the whole qualification tab
            } catch (UserIOException ex) {
                qualificationSaveToggleButton.setSelected(false);
            } catch (IOException ex) {
                qualificationSaveToggleButton.setSelected(false);

                exceptionDialogTextArea.setText("An input/output exception has "
                        + "occurred during the saving of the file. Try to save "
                        + "the file once more.");

                exceptionDialog.setVisible(true);
            }
        } else {
            // reactivate the whole qualification tab
        }
    }//GEN-LAST:event_qualificationSaveToggleButtonMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainGUI().setVisible(true);
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Variables Declaration">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private static javax.swing.JDialog allocationInfoDialog;
    private static javax.swing.JProgressBar allocationInfoProgressBar;
    private static javax.swing.JTextField allocationInfoTextField;
    private static javax.swing.JTextField allocationInfoTimeTextField;
    private javax.swing.JDialog approveParticipantsDialog;
    private javax.swing.JButton approveParticipantsDialogCancelButton;
    private javax.swing.JButton approveParticipantsDialogIgnoreButton;
    private javax.swing.JScrollPane approveParticipantsDialogScrollPane;
    private javax.swing.JTextArea approveParticipantsDialogTextArea;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JTextField eventSettingsDAFinalSR;
    private javax.swing.JTextField eventSettingsDAJR;
    private javax.swing.JTextField eventSettingsDASES;
    private javax.swing.JTextField eventSettingsDASR;
    private javax.swing.JTextField eventSettingsDebateJR;
    private javax.swing.JTextField eventSettingsDebateSES;
    private javax.swing.JTextField eventSettingsFinalsJR;
    private javax.swing.JTextField eventSettingsISFinalSR;
    private javax.swing.JTextField eventSettingsISJR;
    private javax.swing.JTextField eventSettingsISSES;
    private javax.swing.JTextField eventSettingsISSR;
    private javax.swing.JTextField eventSettingsOIFinalSR;
    private javax.swing.JTextField eventSettingsOIJR;
    private javax.swing.JTextField eventSettingsOISES;
    private javax.swing.JTextField eventSettingsOISR;
    private javax.swing.JTextField eventSettingsOOFinalSR;
    private javax.swing.JTextField eventSettingsOOJR;
    private javax.swing.JTextField eventSettingsOOSES;
    private javax.swing.JTextField eventSettingsOOSR;
    private javax.swing.JDialog exceptionDialog;
    private javax.swing.JButton exceptionDialogConfirmButton;
    private javax.swing.JScrollPane exceptionDialogScrollPane;
    private javax.swing.JTextArea exceptionDialogTextArea;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToggleButton finalistsApproveToggleButton;
    private javax.swing.JList finalistsDebateFinalistsList;
    private javax.swing.JList finalistsDebateSemifinalistsAList;
    private javax.swing.JList finalistsDebateSemifinalistsBList;
    private javax.swing.JButton finalistsDebateSemifinalistsSetWinnerAButton;
    private javax.swing.JButton finalistsDebateSemifinalistsSetWinnerBButton;
    private javax.swing.JList finalistsDuetActingList;
    private javax.swing.JButton finalistsExportButton;
    private javax.swing.JList finalistsImpromptuSpeakingList;
    private javax.swing.JList finalistsOralInterpretationList;
    private javax.swing.JList finalistsOriginalOratoryList;
    private javax.swing.JPanel finalistsPane;
    private javax.swing.JProgressBar finalistsProgressBar;
    private javax.swing.JButton finalistsResetButton;
    private javax.swing.JToggleButton finalistsSaveToggleButton;
    private javax.swing.JToggleButton finalsApproveToggleButton;
    private javax.swing.JList finalsEventList;
    private javax.swing.JButton finalsExportButton;
    private javax.swing.JList finalsJudgesList;
    private javax.swing.JButton finalsJudgesSubstituteJudgeButton;
    private javax.swing.JPanel finalsPane;
    private javax.swing.JProgressBar finalsProgressBar;
    private javax.swing.JButton finalsRankingsAddButton;
    private javax.swing.JList finalsRankingsList;
    private javax.swing.JButton finalsRankingsRemoveButton;
    private javax.swing.JTextField finalsRankingsTextField;
    private javax.swing.JButton finalsResetButton;
    private javax.swing.JButton finalsRoomEditNameButton;
    private javax.swing.JList finalsRoomList;
    private javax.swing.JTextField finalsRoomTextField;
    private javax.swing.JButton finalsRoundEditNameButton;
    private javax.swing.JList finalsRoundList;
    private javax.swing.JTextField finalsRoundTextField;
    private javax.swing.JToggleButton finalsSaveToggleButton;
    private javax.swing.JList finalsStudentCodesList;
    private javax.swing.JButton finalsStudentCodesSearchButton;
    private javax.swing.JTextField finalsStudentCodesTextField;
    private javax.swing.JList finalsStudentsVoteList;
    private javax.swing.JButton finalsStudentsVoteSetStudentsVoteButton;
    private javax.swing.JCheckBox generalSettingsCECheckBox;
    private javax.swing.JComboBox generalSettingsCEComboBox1;
    private javax.swing.JComboBox generalSettingsCEComboBox2;
    private javax.swing.JTextField generalSettingsMaximalTime1;
    private javax.swing.JTextField generalSettingsMaximalTime2;
    private javax.swing.JTextField generalSettingsRoomsAvailable1;
    private javax.swing.JTextField generalSettingsRoomsAvailable2;
    private javax.swing.JTextField generalSettingsSS;
    private javax.swing.JTextField generalSettingsSchoolsNumber;
    private javax.swing.JTextField generalSettingsTS;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JDialog illegalActionDialog;
    private javax.swing.JButton illegalActionDialogConfirmButton;
    private javax.swing.JScrollPane illegalActionDialogScrollPane;
    private javax.swing.JTextArea illegalActionDialogTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane28;
    private javax.swing.JScrollPane jScrollPane29;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane30;
    private javax.swing.JScrollPane jScrollPane31;
    private javax.swing.JScrollPane jScrollPane32;
    private javax.swing.JScrollPane jScrollPane33;
    private javax.swing.JScrollPane jScrollPane34;
    private javax.swing.JScrollPane jScrollPane35;
    private javax.swing.JScrollPane jScrollPane36;
    private javax.swing.JScrollPane jScrollPane37;
    private javax.swing.JScrollPane jScrollPane38;
    private javax.swing.JScrollPane jScrollPane39;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane40;
    private javax.swing.JScrollPane jScrollPane41;
    private javax.swing.JScrollPane jScrollPane42;
    private javax.swing.JScrollPane jScrollPane43;
    private javax.swing.JScrollPane jScrollPane44;
    private javax.swing.JScrollPane jScrollPane45;
    private javax.swing.JScrollPane jScrollPane46;
    private javax.swing.JScrollPane jScrollPane47;
    private javax.swing.JScrollPane jScrollPane48;
    private javax.swing.JScrollPane jScrollPane49;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JProgressBar overallProgressBar;
    private javax.swing.JToggleButton participantsApproveToggleButton;
    private javax.swing.JButton participantsAssignEventsAssignEventsButton;
    private javax.swing.JCheckBox participantsAssignEventsDebateCheckBox;
    private javax.swing.JCheckBox participantsAssignEventsDuetActingCheckBox;
    private javax.swing.JCheckBox participantsAssignEventsImpromptuSpeakingCheckBox;
    private javax.swing.JCheckBox participantsAssignEventsOralInterpretationCheckBox;
    private javax.swing.JCheckBox participantsAssignEventsOriginalOratoryCheckBox;
    private javax.swing.JList participantsAssignPairsEventsList;
    private javax.swing.JList participantsAssignPairsLeftList;
    private javax.swing.JButton participantsAssignPairsPairButton;
    private javax.swing.JList participantsAssignPairsPairsList;
    private javax.swing.JButton participantsAssignPairsRemovePairButton;
    private javax.swing.JList participantsAssignPairsRightList;
    private javax.swing.JButton participantsExportButton;
    private javax.swing.JPanel participantsPane;
    private javax.swing.JPanel participantsPane1;
    private javax.swing.JProgressBar participantsProgressBar;
    private javax.swing.JButton participantsResetButton;
    private javax.swing.JToggleButton participantsSaveToggleButton;
    private javax.swing.JButton participantsSchoolsAddButton;
    private javax.swing.JButton participantsSchoolsEditButton;
    private javax.swing.JList participantsSchoolsList;
    private javax.swing.JButton participantsSchoolsRemoveButton;
    private javax.swing.JButton participantsSchoolsSearchButton;
    private javax.swing.JTextField participantsSchoolsTextField;
    private javax.swing.JButton participantsStudentCodesAssignCodesButton;
    private javax.swing.JList participantsStudentCodesList;
    private javax.swing.JButton participantsStudentCodesSearchButton;
    private javax.swing.JTextField participantsStudentCodesTextField;
    private javax.swing.JButton participantsStudentsAddButton;
    private javax.swing.JButton participantsStudentsEditButton;
    private javax.swing.JList participantsStudentsList;
    private javax.swing.JButton participantsStudentsRemoveButton;
    private javax.swing.JButton participantsStudentsSearchButton;
    private javax.swing.JTextField participantsStudentsTextField;
    private javax.swing.JButton participantsTeachersAddButton;
    private javax.swing.JButton participantsTeachersEditButton;
    private javax.swing.JList participantsTeachersList;
    private javax.swing.JButton participantsTeachersRemoveButton;
    private javax.swing.JButton participantsTeachersSearchButton;
    private javax.swing.JTextField participantsTeachersTextField;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JToggleButton qualificationApproveCompleteToggleButton;
    private javax.swing.JToggleButton qualificationApproveIncompleteToggleButton;
    private javax.swing.JList qualificationEventsList;
    private javax.swing.JButton qualificationExportButton;
    private javax.swing.JList qualificationJudgesList;
    private javax.swing.JButton qualificationJudgesSubstituteJudgeButton;
    private javax.swing.JPanel qualificationPane;
    private javax.swing.JProgressBar qualificationProgressBar;
    private javax.swing.JButton qualificationResetButton;
    private javax.swing.JButton qualificationRoomsEditNameButton;
    private javax.swing.JList qualificationRoomsList;
    private javax.swing.JTextField qualificationRoomsTextField;
    private javax.swing.JButton qualificationRoundsEditNameButton;
    private javax.swing.JList qualificationRoundsList;
    private javax.swing.JTextField qualificationRoundsTextField;
    private javax.swing.JToggleButton qualificationSaveToggleButton;
    private javax.swing.JButton qualificationScoresAddButton;
    private javax.swing.JList qualificationScoresList;
    private javax.swing.JButton qualificationScoresRemoveButton;
    private javax.swing.JTextField qualificationScoresTextField;
    private javax.swing.JList qualificationStudentCodesList;
    private javax.swing.JButton qualificationStudentCodesSearchButton;
    private javax.swing.JTextField qualificationStudentCodesTextField;
    private javax.swing.JDialog resetWarningDialog;
    private javax.swing.JButton resetWarningDialogCancelButton;
    private javax.swing.JButton resetWarningDialogConfirmButton;
    private javax.swing.JScrollPane resetWarningDialogScrollPane;
    private javax.swing.JTextArea resetWarningDialogTextArea;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JDialog settingsAcceptDialog;
    private javax.swing.JButton settingsAcceptDialogConfirmButton;
    private javax.swing.JToggleButton settingsApproveToggleButton;
    private javax.swing.JDialog settingsErrorDialog;
    private javax.swing.JButton settingsErrorDialogConfirmButton;
    private javax.swing.JScrollPane settingsErrorDialogScrollPane;
    private javax.swing.JTextArea settingsErrorDialogTextArea;
    private javax.swing.JButton settingsLoadButton;
    private javax.swing.JButton settingsResetButton;
    private javax.swing.JToggleButton settingsSaveToggleButton;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JList winnersDebateFirstPlaceList;
    private javax.swing.JList winnersDebateSecondPlaceList;
    private javax.swing.JList winnersDebateStudentChoiceList;
    private javax.swing.JList winnersDebateThirdPlaceList;
    private javax.swing.JList winnersDuetActingFirstPlaceList;
    private javax.swing.JList winnersDuetActingSecondPlaceList;
    private javax.swing.JList winnersDuetActingStudentChoiceList;
    private javax.swing.JList winnersDuetActingThirdPlaceList;
    private javax.swing.JButton winnersExportButton;
    private javax.swing.JList winnersImpromptuSpeakingFirstPlaceList;
    private javax.swing.JList winnersImpromptuSpeakingSecondPlaceList;
    private javax.swing.JList winnersImpromptuSpeakingStudentChoiceList;
    private javax.swing.JList winnersImpromptuSpeakingThirdPlaceList;
    private javax.swing.JList winnersOralInterpretationFirstPlaceList;
    private javax.swing.JList winnersOralInterpretationSecondPlaceList;
    private javax.swing.JList winnersOralInterpretationStudentChoiceList;
    private javax.swing.JList winnersOralInterpretationThirdPlaceList;
    private javax.swing.JList winnersOriginalOratoryFirstPlaceList;
    private javax.swing.JList winnersOriginalOratorySecondPlaceList;
    private javax.swing.JList winnersOriginalOratoryStudentChoiceList;
    private javax.swing.JList winnersOriginalOratoryThirdPlaceList;
    private javax.swing.JPanel winnersPane;
    private javax.swing.JToggleButton winnersSaveToggleButton;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
