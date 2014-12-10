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
 * A school is the basic building block of the database's hierarchical data
 * structure. A school carries teachers, students, as well as unpaired duet
 * acting and debate students and duet acting and debate pairs. The class
 * contains all the necessary methods to work with and access the members of the
 * school.
 *
 * @author Zbyněk Stara
 */
public class School {
    // ATTRIBUTES:
    private String name = "<Not set yet>";
    private char codeLetter = ' ';

    private int beginCodeNumber = -999;
    private int endCodeNumber = -999;

    private int ooStudentNumber = 0;
    private int oiStudentNumber = 0;
    private int isStudentNumber = 0;

    private BinarySearchTree teacherTree = new BinarySearchTree(); // Teachers
    private BinarySearchTree studentTree = new BinarySearchTree();

    private BinarySearchTree unpairedDAStudentTree = new BinarySearchTree(); // Students
    private BinarySearchTree unpairedDebateStudentTree = new BinarySearchTree();

    private BinarySearchTree daPairTree = new BinarySearchTree(); // StudentPairs
    private BinarySearchTree debatePairTree = new BinarySearchTree();

    // CONSTRUCTORS:
    public School() {

    }
    public School(String name) {
        this.name = name;
    }
    public School(String name, char codeLetter, int beginCodeNumber, int endCodeNumber) {
        this.name = name;
        this.codeLetter = codeLetter;
        this.beginCodeNumber = beginCodeNumber;
        this.endCodeNumber = endCodeNumber;
    }

    // SCHOOL GETS:
    /**
     * The getName method returns the name of the school.
     *
     * @return string with the contents of the name attribute
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-7-2012)
     * @since Nov-7-2012
     */
    public String getName() {
        return name;
    }

    /**
     * The getCodeLetter method returns the code letter of the school.
     *
     * @return string with the contents of the codeLetter attribute
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-7-2012)
     * @since Nov-7-2012
     */
    public char getCodeLetter() {
        return codeLetter;
    }

    /**
     * The getBeginCodeNumber method returns the beginning code number of the
     * school.
     *
     * @return integer with the beginning of the code number range
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-5-2013)
     * @since Jan-5-2013
     */
    public int getBeginCodeNumber() {
        return beginCodeNumber;
    }

    /**
     * The getEndCodeNumber method returns the end code number of the school.
     *
     * @return integer with the end of the code number range
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-5-2013)
     * @since Jan-5-2013
     */
    public int getEndCodeNumber() {
        return endCodeNumber;
    }
    
    // SCHOOL SETS:
    /**
     * The setName method sets the name of the school to the contents of the
     * name parameter.
     *
     * @param name the new name for the school
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The setCodeLetter method sets the codeLetter of the school to a given
     * char.
     *
     * @param codeLetter a char with the new codeLetter of the school
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public void setCodeLetter(char codeLetter) {
        this.codeLetter = codeLetter;
    }

    /**
     * The setBeginCodeNumber method sets the beginning of the code number range
     * of the school to a given integer.
     *
     * @param beginCodeNumber an integer with the beginning of the code number
     * range
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-5-2013)
     * @since Jan-5-2013
     */
    public void setBeginCodeNumber(int beginCodeNumber) {
        this.beginCodeNumber = beginCodeNumber;
    }

    /**
     * The setEndCodeNumber method sets the end of the code number range of the
     * school to a given integer.
     *
     * @param endCodeNumber an integer with the end of the code number range
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-5-2013)
     * @since Jan-5-2013
     */
    public void setEndCodeNumber(int endCodeNumber) {
        this.endCodeNumber = endCodeNumber;
    }

    /**
     * This method increments the value of the ooStudentNumber variable.
     *
     * @author Zbyněk Stara
     */
    public void incrementOOStudentNumber() {
        this.ooStudentNumber += 1;
    }

    /**
     * This method decrements the value of the ooStudentNumber variable.
     *
     * @author Zbyněk Stara
     */
    public void decrementOOStudentNumber() throws IllegalStateException {
        if (ooStudentNumber == 0) {
            throw new IllegalStateException("Number of OO students cannot be lower than zero.");
        } else {
            this.ooStudentNumber -= 1;
        }
    }

    /**
     * This method returns the value of the ooStudentNumber variable.
     *
     * @return an int with the value of ooStudentNumber
     *
     * @author Zbyněk Stara
     */
    public int getOOStudentNumber() {
        return this.ooStudentNumber;
    }

    /**
     * This method increments the value of the oiStudentNumber variable.
     *
     * @author Zbyněk Stara
     */
    public void incrementOIStudentNumber() {
        this.oiStudentNumber += 1;
    }

    /**
     * This method decrements the value of the oiStudentNumber variable.
     *
     * @author Zbyněk Stara
     */
    public void decrementOIStudentNumber() throws IllegalStateException {
        if (oiStudentNumber == 0) {
            throw new IllegalStateException("Number of OI students cannot be lower than zero.");
        } else {
            this.oiStudentNumber -= 1;
        }
    }

    /**
     * This method returns the value of the oiStudentNumber variable.
     *
     * @return an int with the value of oiStudentNumber
     *
     * @author Zbyněk Stara
     */
    public int getOIStudentNumber() {
        return this.oiStudentNumber;
    }

    /**
     * This method increments the value of the isStudentNumber variable.
     *
     * @author Zbyněk Stara
     */
    public void incrementISStudentNumber() {
        this.isStudentNumber += 1;
    }

    /**
     * This method decrements the value of the isStudentNumber variable.
     *
     * @author Zbyněk Stara
     */
    public void decrementISStudentNumber() throws IllegalStateException {
        if (isStudentNumber == 0) {
            throw new IllegalStateException("Number of IS students cannot be lower than zero.");
        } else {
            this.isStudentNumber -= 1;
        }
    }

    /**
     * This method returns the value of the isStudentNumber variable.
     *
     * @return an int with the value of isStudentNumber
     *
     * @author Zbyněk Stara
     */
    public int getISStudentNumber() {
        return this.isStudentNumber;
    }

    // EXTRA METHODS:
    /**
     * This method calls for all students of this school the updateStudentPairs
     * method.
     *
     * @author Zbyněk Stara
     */
    public void updateStudentPairs() {
        for (int i = 0; i < this.getStudentTreeSize(); i++) {
            Student currentStudent = (Student) this.getStudentTreeNodeData(i);

            currentStudent.updateStudentPairs();
        }
    }

    // TEACHER METHODS:
    /**
     * The getTeacherArray method returns an array of teachers, corresponding to
     * the teacherTree
     * <p>
     * The teacherTree is traversed in order and its every member is added to an
     * array. This array is then returned by the function
     *
     * @return array of teachers
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public Teacher [] getTeacherArray() {
        Object [] tempArray = teacherTree.getDataArray();
        Teacher [] returnArray = new Teacher [tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Teacher) tempArray[i];
        }

        return returnArray;
    }

    /**
     * This method returns the teacher from this school that has a given name.
     *
     * @param name the name of the teacher to search for
     * @return the teacher with the corresponding name
     * @throws IllegalArgumentException if the name given to this method is an
     * empty string
     * @throws NoSuchElementException if the name could not be found in this
     * school
     *
     * @author Zbyněk Stara
     */
    public Teacher getTeacher(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            int teacherIndex = this.searchTeacher(name);
            Teacher teacher = (Teacher) this.getTeacherTreeNodeData(teacherIndex);
            return teacher;
        } else throw new IllegalArgumentException();
    }

    /**
     * The getTeacherTreeNodeData method returns the data for a given node of
     * the teacherTree.
     * <p>
     * A node of the teacherTree, identified by the index parameter, is examined
     * and the content of its data attribute is returned as object.
     *
     * @param index the node which will be examined
     * @return the data this node carries
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public Object getTeacherTreeNodeData(int index) {
        return teacherTree.getNodeData(index);
    }

    /**
     * The getTeacherTreeSize returns the size of the teacherTree.
     *
     * @return the size of the teacherTree, as determined by the size() function
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public int getTeacherTreeSize() {
        return teacherTree.size();
    }

    /**
     * This insertTecaher method adds a new teacher to the teacherTree.
     * <p>
     * The teacher is added with the name specified in the parameter.
     *
     * @param name the name of the teacher to be added
     * @throws IllegalArgumentException if there is already a teacher of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public void insertTeacher(String name) throws IllegalArgumentException {
        if (!teacherTree.contains(name) && !name.equals("")) {
            Teacher newTeacher = new Teacher(name, this);
            teacherTree.insert(newTeacher, name);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * This insertTeacher method adds a specified teacher to the teacherTree.
     *
     * @param teacher the teacher to be added
     * @throws IllegalArgumentException if there is already a teacher of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Dec-5-2012
     */
    public void insertTeacher(Teacher teacher) throws IllegalArgumentException {
        if (!teacherTree.contains(teacher.getName()) && !teacher.getName().equals("")) {
            teacherTree.insert(teacher, teacher.getName());
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The deleteTeacher method removes a specific teacher from the teacherTree.
     * <p>
     * The teacher of the specified name is deleted, provided that it exists. It
     * is then returned by the function.
     *
     * @param name the name of the teacher to be deleted
     * @return the deleted teacher
     * @throws NoSuchElementException if there is no teacher of the specified
     * name
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Dec-5-2012
     */
    public Teacher deleteTeacher(String name) throws NoSuchElementException {
        return (Teacher) teacherTree.delete(name);
    }

    /**
     * The deleteTeacher method removes a specific teacher from the teacherTree.
     * <p>
     * The teacher at the specified index is removed from the teacherTree and is
     * returned by this function
     *
     * @param index the index of the teacher in the teacherTree
     * @return the deleted teacher
     * @throws IllegalArgumentException if the index is not within the bounds of
     * the teacherTree
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public Teacher deleteTeacher(int index) throws IllegalArgumentException {
        if (index >= 0 && index < teacherTree.size()) {
            Teacher deletedTeacher = (Teacher) teacherTree.getNodeData(index);
            String deleteKey = deletedTeacher.getName();
            teacherTree.delete(deleteKey);
            return deletedTeacher;
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The editTeacher method renames a specific teacher.
     * <p>
     * The teacher is first removed from the teacherTree, renamed, and then
     * re-inserted under a new name.
     *
     * @param index the teacher's index in the teacherTree
     * @param name new name for the teacher
     * @throws IllegalArgumentException if the name is already used in the
     * teacherTree or the new name is an empty string or the index is not
     * within the bounds of the teacherTree
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public void editTeacher(int index, String name) throws IllegalArgumentException {
        if (!teacherTree.contains(name) && !name.equals("") && index >= 0 && index < teacherTree.size()) {
            Teacher editedTeacher = (Teacher) teacherTree.getNodeData(index);
            String editKey = editedTeacher.getName();
            teacherTree.delete(editKey);
            editedTeacher.setName(name);
            teacherTree.insert(editedTeacher, name);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The searchTeacher method finds a specific teacher.
     * <p>
     * If the teacher exists, this method finds it and returns its index in the
     * teacherTree.
     *
     * @param name name of teacher to search for
     * @return teacher's index in the teacherTree
     * @throws IllegalArgumentException if the name is an empty string
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Jan-3-2013
     */
    public int searchTeacher(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            Object [] teacherTreeArray = teacherTree.getDataArray();
            boolean teacherFound = false;
            int teacherIndex = -999;
            for (int i = 0; i < teacherTreeArray.length; i++) {
                if (((Teacher) teacherTreeArray[i]).getName().equals(name)) {
                    teacherFound = true;
                    teacherIndex = i;
                    break;
                }
            }
            if (teacherFound) {
                return teacherIndex;
            }
            else throw new NoSuchElementException();
        }
        else throw new IllegalArgumentException();
    }

    // STUDENT METHODS:
    /**
     * The getStudentArray method returns an array of students, corresponding to
     * the studentTree
     * <p>
     * The studentTree is traversed in order and its every member is added to an
     * array. This array is then returned by the function
     *
     * @return array of students
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public Student [] getStudentArray() {
        Object [] tempArray = studentTree.getDataArray();
        Student [] returnArray = new Student [tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Student) tempArray[i];
        }

        return returnArray;
    }

    /**
     * This method returns the student from this school that has a given name.
     *
     * @param name the name of the student to search for
     * @return the student with the corresponding name
     * @throws IllegalArgumentException if the name given to this method is an
     * empty string
     * @throws NoSuchElementException if the name could not be found in this
     * school
     *
     * @author Zbyněk Stara
     */
    public Student getStudent(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            int studentIndex = this.searchStudent(name);
            Student student = (Student) this.getStudentTreeNodeData(studentIndex);
            return student;
        } else throw new IllegalArgumentException();
    }

    /**
     * The getStudentTreeNodeData method returns the data for a given node of
     * the studentTree.
     * <p>
     * A node of the studentTree, identified by the index parameter, is examined
     * and the content of its data attribute is returned as object.
     *
     * @param index the node which will be examined
     * @return the data this node carries
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public Object getStudentTreeNodeData(int index) {
        return studentTree.getNodeData(index);
    }

    /**
     * The getStudentTreeSize returns the size of the studentTree.
     *
     * @return the size of the studentTree, as determined by the size() function
     *
     * @author Zbyněk Stara
     * @version 1.0 (Nov-8-2012)
     * @since Nov-8-2012
     */
    public int getStudentTreeSize() {
        return studentTree.size();
    }

    /**
     * This insertStudent method adds a new student to the studentTree.
     * <p>
     * The student is added with the name specified in the parameter.
     *
     * @param name the name of the student to be added
     * @throws IllegalArgumentException if there is already a student of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public void insertStudent(String name) throws IllegalArgumentException {
        if (!studentTree.contains(name) && !name.equals("")) {
            Student newStudent = new Student(name, this);
            studentTree.insert(newStudent, name);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * This insertStudent method adds a specified student to the studentTree.
     *
     * @param student the student to be added
     * @throws IllegalArgumentException if there is already a student of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Dec-5-2012
     */
    public void insertStudent(Student student) throws IllegalArgumentException {
        if (!studentTree.contains(student.getName()) && !student.getName().equals("")) {
            studentTree.insert(student, student.getName());
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The deleteStudent method removes a specific student from the studentTree.
     * <p>
     * The student of the specified name is deleted, provided that it exists. It
     * is then returned by the function.
     *
     * @param name the name of the student to be deleted
     * @return the deleted student
     * @throws NoSuchElementException if there is no student of the specified
     * name
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Dec-5-2012
     */
    public Student deleteStudent(String name) throws NoSuchElementException {
        return (Student) studentTree.delete(name);
    }

    /**
     * The deleteStudent method removes a specific student from the studentTree.
     * <p>
     * The student at the specified index is removed from the studentTree and is
     * returned by this function
     *
     * @param index the index of the student in the studentTree
     * @return the deleted student
     * @throws IllegalArgumentException if the index is not within the bounds of
     * the studentTree
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public Student deleteStudent(int index) throws IllegalArgumentException {
        if (index >= 0 && index < studentTree.size()) {
            Student deletedStudent = (Student) studentTree.getNodeData(index);
            String deleteKey = deletedStudent.getName();
            studentTree.delete(deleteKey);
            return deletedStudent;
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The editStudent method renames a specific student.
     * <p>
     * The student is first removed from the studentTree, renamed, and then
     * re-inserted under a new name.
     *
     * @param index the student's index in the studentTree
     * @param name new name for the student
     * @throws IllegalArgumentException if the name is already used in the
     * studentTree or the new name is an empty string or the index is not
     * within the bounds of the studentTree
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-4-2013)
     * @since Jan-4-2013
     */
    public void editStudent(int index, String name) throws IllegalArgumentException {
        if (!studentTree.contains(name) && !name.equals("") && index >= 0 && index < studentTree.size()) {
            Student editedStudent = (Student) studentTree.getNodeData(index);
            String editKey = editedStudent.getName();
            studentTree.delete(editKey);

            editedStudent.setName(name);
            studentTree.insert(editedStudent, name);

            try {
                Student editedStudentDA = (Student) unpairedDAStudentTree.delete(editKey);
                unpairedDAStudentTree.insert(editedStudentDA, name);
            } catch (NoSuchElementException ex){
                // continue
            }

            try {
                Student editedStudentDebate = (Student) unpairedDebateStudentTree.delete(editKey);
                unpairedDebateStudentTree.insert(editedStudentDebate, name);
            } catch (NoSuchElementException ex) {
                // continue
            }
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The searchStudent method finds a specific student.
     * <p>
     * If the student exists, this method finds it and returns its index in the
     * studentTree.
     *
     * @param name name of student to search for
     * @return student's index in the studentTree
     * @throws IllegalArgumentException if the name is an empty string
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Jan-3-2013
     */
    public int searchStudent(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            Object [] studentTreeArray = studentTree.getDataArray();
            boolean studentFound = false;
            int studentIndex = -999;
            for (int i = 0; i < studentTreeArray.length; i++) {
                if (((Student) studentTreeArray[i]).getName().equals(name)) {
                    studentFound = true;
                    studentIndex = i;
                    break;
                }
            }
            if (studentFound) {
                return studentIndex;
            }
            else throw new NoSuchElementException();
        }
        else throw new IllegalArgumentException();
    }

    // UNPAIRED DA STUDENT METHODS:
    /**
     * The getUnpairedDAStudentArray method returns an array of students,
     * corresponding to the unpairedDAStudentTree.
     * <p>
     * The unpairedDAStudentTree is traversed in order and its every member is
     * added to an array. This array is then returned by the function.
     *
     * @return array of students
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public Student [] getUnpairedDAStudentArray() {
        Object [] tempArray = unpairedDAStudentTree.getDataArray();
        Student [] returnArray = new Student [tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Student) tempArray[i];
        }

        return returnArray;
    }

    /**
     * This method returns the unpaired DA student from this school that has
     * a given name.
     *
     * @param name the name of the unpaired DA student to search for
     * @return the unpaired DA student with the corresponding name
     * @throws IllegalArgumentException if the name given to this method is an
     * empty string
     * @throws NoSuchElementException if the name could not be found in this
     * school
     *
     * @author Zbyněk Stara
     */
    public Student getUnpairedDAStudent(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            int unpairedDAStudentIndex = this.searchUnpairedDAStudent(name);
            Student unpairedDAStudent = (Student) this.getUnpairedDAStudentTreeNodeData(unpairedDAStudentIndex);
            return unpairedDAStudent;
        } else throw new IllegalArgumentException();
    }

    /**
     * The getUnpairedDAStudentTreeNodeData method returns the data for a given
     * node of the unpairedDAStudentTree.
     * <p>
     * A node of the unpairedDAStudentTree, identified by the index parameter,
     * is examined and the content of its data attribute is returned as object.
     *
     * @param index the node which will be examined
     * @return the data this node carries
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public Object getUnpairedDAStudentTreeNodeData(int index) {
        return unpairedDAStudentTree.getNodeData(index);
    }

    /**
     * The getUnpairedDAStudentTreeSize returns the size of the
     * unpairedDAstudentTree.
     *
     * @return the size of the unpairedDAStudentTree, as determined by the
     * size() function
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public int getUnpairedDAStudentTreeSize() {
        return unpairedDAStudentTree.size();
    }

    /**
     * The insertUnpairedDAStudent method adds a specified student to the
     * unpairedDAStudentTree.
     *
     * @param student the student to be added
     * @throws IllegalArgumentException if there is already a student of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public void insertUnpairedDAStudent(Student student) throws IllegalArgumentException {
        if (!unpairedDAStudentTree.contains(student.getName()) && !student.getName().equals("")) {
            unpairedDAStudentTree.insert(student, student.getName());

            Student currentStudent1 = (Student) unpairedDAStudentTree.search(student.getName());
            currentStudent1.setDAUnpaired(true);

            Student currentStudent2 = (Student) studentTree.search(student.getName());
            currentStudent2.setDAUnpaired(true);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The deleteUnpairedDAStudent method removes a specific student from the
     * unpairedDAStudentTree.
     * <p>
     * The student of the specified name is deleted, provided that it exists. It
     * is then returned by the function.
     *
     * @param name the name of the student to be deleted
     * @return the deleted student
     * @throws NoSuchElementException if there is no student of the specified
     * name
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Dec-11-2012
     */
    public Student deleteUnpairedDAStudent(String name) throws NoSuchElementException {
        Student deletedStudent = (Student) unpairedDAStudentTree.delete(name);
        Student currentStudent = (Student) studentTree.search(deletedStudent.getName());
        currentStudent.setDAUnpaired(false);

        return deletedStudent;
    }

    /**
     * The deleteUnpairedDAStudent method removes a specific student from the
     * unpairedDAStudentTree.
     * <p>
     * The student at the specified index is removed from the
     * unpairedDAStudentTree and is returned by this function.
     *
     * @param index the index of the student in the unpairedDAStudentTree
     * @return the deleted student
     * @throws IllegalArgumentException if the index is not within the bounds of
     * the unpairedDAStudentTree
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public Student deleteUnpairedDAStudent(int index) throws IllegalArgumentException {
        if (index >= 0 && index < unpairedDAStudentTree.size()) {
            Student deletedStudent = (Student) unpairedDAStudentTree.getNodeData(index);
            String deleteKey = deletedStudent.getName();
            unpairedDAStudentTree.delete(deleteKey);

            Student currentStudent = (Student) studentTree.search(deletedStudent.getName());
            currentStudent.setDAUnpaired(false);

            return deletedStudent;
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The searchUnpairedDAStudent method finds a specific unpaired DA student.
     * <p>
     * If the student exists, this method finds it and returns its index in the
     * unpairedDAStudentTree.
     *
     * @param name name of student to search for
     * @return student's index in the unpairedDAStudentTree
     * @throws IllegalArgumentException if the name is an empty string
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public int searchUnpairedDAStudent(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            Object [] unpairedDAStudentTreeArray = unpairedDAStudentTree.getDataArray();
            boolean unpairedDAStudentFound = false;
            int unpairedDAStudentIndex = -999;
            for (int i = 0; i < unpairedDAStudentTreeArray.length; i++) {
                if (((Student) unpairedDAStudentTreeArray[i]).getName().equals(name)) {
                    unpairedDAStudentFound = true;
                    unpairedDAStudentIndex = i;
                    break;
                }
            }
            if (unpairedDAStudentFound) {
                return unpairedDAStudentIndex;
            }
            else throw new NoSuchElementException();
        }
        else throw new IllegalArgumentException();
    }

    // UNPAIRED DEBATE STUDENT METHODS:
    /**
     * The getUnpairedDebateStudentArray method returns an array of students,
     * corresponding to the unpairedDebateStudentTree.
     * <p>
     * The unpairedDebateStudentTree is traversed in order and its every member
     * is added to an array. This array is then returned by the function.
     *
     * @return array of students
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public Student [] getUnpairedDebateStudentArray() {
        Object [] tempArray = unpairedDebateStudentTree.getDataArray();
        Student [] returnArray = new Student [tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (Student) tempArray[i];
        }

        return returnArray;
    }

    /**
     * This method returns the unpaired debate student from this school that has
     * a given name.
     *
     * @param name the name of the unpaired debate student to search for
     * @return the unpaired debate student with the corresponding name
     * @throws IllegalArgumentException if the name given to this method is an
     * empty string
     * @throws NoSuchElementException if the name could not be found in this
     * school
     *
     * @author Zbyněk Stara
     */
    public Student getUnpairedDebateStudent(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            int unpairedDebateStudentIndex = this.searchUnpairedDebateStudent(name);
            Student unpairedDebateStudent = (Student) this.getUnpairedDebateStudentTreeNodeData(unpairedDebateStudentIndex);
            return unpairedDebateStudent;
        } else throw new IllegalArgumentException();
    }

    /**
     * The getUnpairedDebateStudentTreeNodeData method returns the data for a
     * given node of the unpairedDebateStudentTree.
     * <p>
     * A node of the unpairedDebateStudentTree, identified by the index
     * parameter, is examined and the content of its data attribute is returned
     * as object.
     *
     * @param index the node which will be examined
     * @return the data this node carries
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public Object getUnpairedDebateStudentTreeNodeData(int index) {
        return unpairedDebateStudentTree.getNodeData(index);
    }

    /**
     * The getUnpairedDebateStudentTreeSize returns the size of the
     * unpairedDebatestudentTree.
     *
     * @return the size of the unpairedDebateStudentTree, as determined by the
     * size() function
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public int getUnpairedDebateStudentTreeSize() {
        return unpairedDebateStudentTree.size();
    }

    /**
     * The insertUnpairedDebateStudent method adds a specified student to the
     * unpairedDebateStudentTree.
     *
     * @param student the student to be added
     * @throws IllegalArgumentException if there is already a student of the
     * specified name or the name is an empty string
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public void insertUnpairedDebateStudent(Student student) throws IllegalArgumentException {
        if (!unpairedDebateStudentTree.contains(student.getName()) && !student.getName().equals("")) {
            unpairedDebateStudentTree.insert(student, student.getName());

            Student currentStudent1 = (Student) unpairedDebateStudentTree.search(student.getName());
            currentStudent1.setDebateUnpaired(true);

            Student currentStudent2 = (Student) studentTree.search(student.getName());
            currentStudent2.setDebateUnpaired(true);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The deleteUnpairedDebateStudent method removes a specific student from
     * the unpairedDebateStudentTree.
     * <p>
     * The student of the specified name is deleted, provided that it exists. It
     * is then returned by the function.
     *
     * @param name the name of the student to be deleted
     * @return the deleted student
     * @throws NoSuchElementException if there is no student of the specified
     * name
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-4-2013)
     * @since Dec-5-2012
     */
    public Student deleteUnpairedDebateStudent(String name) throws NoSuchElementException {
        Student deletedStudent = (Student) unpairedDebateStudentTree.delete(name);
        Student currentStudent = (Student) studentTree.search(deletedStudent.getName());
        currentStudent.setDebateUnpaired(false);

        return deletedStudent;
    }

    /**
     * The deleteUnpairedDebateStudent method removes a specific student from
     * the unpairedDebateStudentTree.
     * <p>
     * The student at the specified index is removed from the
     * unpairedDebateStudentTree and is returned by this function.
     *
     * @param index the index of the student in the unpairedDebateStudentTree
     * @return the deleted student
     * @throws IllegalArgumentException if the index is not within the bounds of
     * the unpairedDebateStudentTree
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-8-2013)
     * @since Jan-8-2013
     */
    public Student deleteUnpairedDebateStudent(int index) throws IllegalArgumentException {
        if (index >= 0 && index < unpairedDebateStudentTree.size()) {
            Student deletedStudent = (Student) unpairedDebateStudentTree.getNodeData(index);
            String deleteKey = deletedStudent.getName();
            unpairedDebateStudentTree.delete(deleteKey);

            Student currentStudent = (Student) studentTree.search(deletedStudent.getName());
            currentStudent.setDebateUnpaired(false);

            return deletedStudent;
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The searchUnpairedDebateStudent method finds a specific unpaired debate
     * student.
     * <p>
     * If the student exists, this method finds it and returns its index in the
     * unpairedDebateStudentTree.
     *
     * @param name name of student to search for
     * @return student's index in the unpairedDebateStudentTree
     * @throws IllegalArgumentException if the name is an empty string
     * @throws NoSuchElementException if the name has not been found
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public int searchUnpairedDebateStudent(String name) throws IllegalArgumentException, NoSuchElementException {
        if (!name.equals("")) {
            Object [] unpairedDebateStudentTreeArray = unpairedDebateStudentTree.getDataArray();
            boolean unpairedDebateStudentFound = false;
            int unpairedDebateStudentIndex = -999;
            for (int i = 0; i < unpairedDebateStudentTreeArray.length; i++) {
                if (((Student) unpairedDebateStudentTreeArray[i]).getName().equals(name)) {
                    unpairedDebateStudentFound = true;
                    unpairedDebateStudentIndex = i;
                    break;
                }
            }
            if (unpairedDebateStudentFound) {
                return unpairedDebateStudentIndex;
            }
            else throw new NoSuchElementException();
        }
        else throw new IllegalArgumentException();
    }

    // DA PAIR METHODS:
    /**
     * The getDAPairArray method returns an array of student pairs,
     * corresponding to the daPairTree.
     * <p>
     * The daPairTree is traversed in order and its every member is
     * added to an array. This array is then returned by the function.
     *
     * @return array of student pairs
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public DAStudentPair [] getDAPairArray() {
        Object [] tempArray = daPairTree.getDataArray();
        DAStudentPair [] returnArray = new DAStudentPair [tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (DAStudentPair) tempArray[i];
        }

        return returnArray;
    }

    /**
     * This method returns the DA pair that has a given code.
     *
     * @param code the code of the pair to search for
     * @return the pair with the corresponding code
     * @throws IllegalArgumentException if the code given to this method is an
     * empty string
     * @throws NoSuchElementException if the code could not be found in this
     * school
     *
     * @author Zbyněk Stara
     */
    public DAStudentPair getDAPair(String code) throws IllegalArgumentException, NoSuchElementException {
        if (!code.equals("")) {
            int daPairIndex = this.searchDAPair(code);
            DAStudentPair daPair = (DAStudentPair) this.getDAPairTreeNodeData(daPairIndex);
            return daPair;
        } else throw new IllegalArgumentException();
    }

    /**
     * The getDAPairTreeNodeData method returns the data for a given node of the
     * daPairStudentTree.
     * <p>
     * A node of the daPairStudentTree, identified by the index parameter,
     * is examined and the content of its data attribute is returned as object.
     *
     * @param index the node which will be examined
     * @return the data this node carries
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public Object getDAPairTreeNodeData(int index) {
        return daPairTree.getNodeData(index);
    }

    /**
     * The getDAPairTreeSize returns the size of the daPairTree.
     *
     * @return the size of the daPairTree, as determined by the size() function
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public int getDAPairTreeSize() {
        return daPairTree.size();
    }

    /**
     * This insertDAPair method adds a specified student pair to the daPairTree.
     *
     * @param studentPair the student pair to be added
     * @throws IllegalArgumentException if there is already a student pair with
     * the same students
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public void insertDAPair(DAStudentPair studentPair) throws IllegalArgumentException {
        if (!daPairTree.contains(studentPair.getCode())) {
            daPairTree.insert(studentPair, studentPair.getCode());

            Student[] pairStudentArray = studentPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            Student currentStudent = (Student) studentTree.search(leftStudent.getName());
            currentStudent.setDAStudentPair(studentPair);

            Student rightStudent = pairStudentArray[1];
            currentStudent = (Student) studentTree.search(rightStudent.getName());
            currentStudent.setDAStudentPair(studentPair);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The insertDAPair method adds a student pair of the specified students to
     * the daPairTree.
     *
     * @param student1 the first student to be added
     * @param student2 the second student to be added
     * @throws IllegalArgumentException if there is already a student pair with
     * the same students or codes were not assigned to the students
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public void insertDAPair(Student student1, Student student2) throws IllegalArgumentException {
        DAStudentPair newStudentPair;
        try {
            newStudentPair = new DAStudentPair(student1, student2);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException();
        }

        if (!daPairTree.contains(newStudentPair.getCode())) {
            daPairTree.insert(newStudentPair, newStudentPair.getCode());

            Student[] pairStudentArray = newStudentPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            Student currentStudent = (Student) studentTree.search(leftStudent.getName());
            currentStudent.setDAStudentPair(newStudentPair);

            Student rightStudent = pairStudentArray[1];
            currentStudent = (Student) studentTree.search(rightStudent.getName());
            currentStudent.setDAStudentPair(newStudentPair);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The deleteDAPair method removes a specific DA pair from the daPairTree.
     * <p>
     * The pair of the specified code is deleted, provided that it exists. It is
     * then returned by the function.
     *
     * @param code the code of the pair to be deleted
     * @return the deleted student pair
     * @throws NoSuchElementException if there is no student pair of the
     * specified code
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public DAStudentPair deleteDAPair(String code) throws NoSuchElementException {
        DAStudentPair deletedPair = (DAStudentPair) daPairTree.delete(code);

        Student[] pairStudentArray = deletedPair.getStudentArray();

        Student leftStudent = pairStudentArray[0];
        Student currentStudent = (Student) studentTree.search(leftStudent.getName());
        currentStudent.setDAStudentPair(null);

        Student rightStudent = pairStudentArray[1];
        currentStudent = (Student) studentTree.search(rightStudent.getName());
        currentStudent.setDAStudentPair(null);

        return deletedPair;
    }

    /**
     * The deleteDAPair method removes a specific student pair from the
     * daPairTree.
     * <p>
     * The student pair at the specified index is removed from the daPairTree
     * and is returned by this function.
     *
     * @param index the index of the student pair in the daPairTree
     * @return the deleted student pair
     * @throws IllegalArgumentException if the index is not within the bounds of
     * the daPairTree
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public DAStudentPair deleteDAPair(int index) throws IllegalArgumentException {
        if (index >= 0 && index < daPairTree.size()) {
            DAStudentPair deletedPair = (DAStudentPair) daPairTree.getNodeData(index);
            String deleteKey = deletedPair.getCode();
            daPairTree.delete(deleteKey);

            Student[] pairStudentArray = deletedPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            Student currentStudent = (Student) studentTree.search(leftStudent.getName());
            currentStudent.setDAStudentPair(null);

            Student rightStudent = pairStudentArray[1];
            currentStudent = (Student) studentTree.search(rightStudent.getName());
            currentStudent.setDAStudentPair(null);

            return deletedPair;
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The searchDAPair method finds a specific student.
     * <p>
     * If the student pair exists, this method finds it and returns its index in
     * the daPairTree.
     *
     * @param code code of the student pair to search for
     * @return student's index in the daPairTree
     * @throws IllegalArgumentException if the code is an empty string
     * @throws NoSuchElementException if the code has not been found
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public int searchDAPair(String code) throws IllegalArgumentException, NoSuchElementException {
        if (!code.equals("")) {
            Object [] daPairTreeArray = daPairTree.getDataArray();
            boolean studentPairFound = false;
            int studentPairIndex = -999;
            for (int i = 0; i < daPairTreeArray.length; i++) {
                if (((DAStudentPair) daPairTreeArray[i]).getCode().equals(code)) {
                    studentPairFound = true;
                    studentPairIndex = i;
                    break;
                }
            }
            if (studentPairFound) {
                return studentPairIndex;
            }
            else throw new NoSuchElementException();
        }
        else throw new IllegalArgumentException();
    }

    // DEBATE PAIR METHODS:
    /**
     * The getDebatePairArray method returns an array of student pairs,
     * corresponding to the debatePairTree.
     * <p>
     * The debatePairTree is traversed in order and its every member is
     * added to an array. This array is then returned by the function.
     *
     * @return array of student pairs
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public DebateStudentPair [] getDebatePairArray() {
        Object [] tempArray = debatePairTree.getDataArray();
        DebateStudentPair [] returnArray = new DebateStudentPair [tempArray.length];

        for (int i = 0; i < tempArray.length; i++) {
            returnArray[i] = (DebateStudentPair) tempArray[i];
        }

        return returnArray;
    }

    /**
     * This method returns the debate pair that has a given code.
     *
     * @param code the code of the pair to search for
     * @return the pair with the corresponding code
     * @throws IllegalArgumentException if the code given to this method is an
     * empty string
     * @throws NoSuchElementException if the code could not be found in this
     * school
     *
     * @author Zbyněk Stara
     */
    public DebateStudentPair getDebatePair(String code) throws IllegalArgumentException, NoSuchElementException {
        if (!code.equals("")) {
            int debatePairIndex = this.searchDebatePair(code);
            DebateStudentPair debatePair = (DebateStudentPair) this.getDebatePairTreeNodeData(debatePairIndex);
            return debatePair;
        } else throw new IllegalArgumentException();
    }

    /**
     * The getDebatePairTreeNodeData method returns the data for a given node of the
     * debatePairStudentTree.
     * <p>
     * A node of the debatePairStudentTree, identified by the index parameter,
     * is examined and the content of its data attribute is returned as object.
     *
     * @param index the node which will be examined
     * @return the data this node carries
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public Object getDebatePairTreeNodeData(int index) {
        return debatePairTree.getNodeData(index);
    }

    /**
     * The getDebatePairTreeSize returns the size of the debatePairTree.
     *
     * @return the size of the debatePairTree, as determined by the size() function
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public int getDebatePairTreeSize() {
        return debatePairTree.size();
    }

    /**
     * This insertDebatePair method adds a specified student pair to the
     * debatePairTree.
     *
     * @param studentPair the student pair to be added
     * @throws IllegalArgumentException if there is already a student pair with
     * the same students
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public void insertDebatePair(DebateStudentPair studentPair) throws IllegalArgumentException {
        if (!debatePairTree.contains(studentPair.getCode())) {
            debatePairTree.insert(studentPair, studentPair.getCode());

            Student[] pairStudentArray = studentPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            Student currentStudent = (Student) studentTree.search(leftStudent.getName());
            currentStudent.setDebateStudentPair(studentPair);

            Student rightStudent = pairStudentArray[1];
            currentStudent = (Student) studentTree.search(rightStudent.getName());
            currentStudent.setDebateStudentPair(studentPair);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The insertDebatePair method adds a student pair of the specified students to
     * the debatePairTree.
     *
     * @param student1 the first student to be added
     * @param student2 the second student to be added
     * @throws IllegalArgumentException if there is already a student pair with
     * the same students or codes were not assigned to the students
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public void insertDebatePair(Student student1, Student student2) throws IllegalArgumentException {
        DebateStudentPair newStudentPair;
        try {
            newStudentPair = new DebateStudentPair(student1, student2);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException();
        }

        if (!debatePairTree.contains(newStudentPair.getCode())) {
            debatePairTree.insert(newStudentPair, newStudentPair.getCode());

            Student[] pairStudentArray = newStudentPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            Student currentStudent = (Student) studentTree.search(leftStudent.getName());
            currentStudent.setDebateStudentPair(newStudentPair);

            Student rightStudent = pairStudentArray[1];
            currentStudent = (Student) studentTree.search(rightStudent.getName());
            currentStudent.setDebateStudentPair(newStudentPair);
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The deleteDebatePair method removes a specific debate pair from the
     * daPairTree.
     * <p>
     * The student pair of the specified code is deleted, provided that it
     * exists. It is then returned by the function.
     *
     * @param code the code of the student pair to be deleted
     * @return the deleted student pair
     * @throws NoSuchElementException if there is no student pair of the
     * specified code
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public DebateStudentPair deleteDebatePair(String code) throws NoSuchElementException {
        DebateStudentPair deletedPair = (DebateStudentPair) debatePairTree.delete(code);

        Student[] pairStudentArray = deletedPair.getStudentArray();

        Student leftStudent = pairStudentArray[0];
        Student currentStudent = (Student) studentTree.search(leftStudent.getName());
        currentStudent.setDebateStudentPair(null);

        Student rightStudent = pairStudentArray[1];
        currentStudent = (Student) studentTree.search(rightStudent.getName());
        currentStudent.setDebateStudentPair(null);

        return deletedPair;
    }

    /**
     * The deleteDebatePair method removes a specific student pair from the
     * debatePairTree.
     * <p>
     * The student pair at the specified index is removed from the debatePairTree
     * and is returned by this function.
     *
     * @param index the index of the student pair in the debatePairTree
     * @return the deleted student pair
     * @throws IllegalArgumentException if the index is not within the bounds of
     * the debatePairTree
     *
     * @author Zbyněk Stara
     * @version 1.1 (Jan-10-2013)
     * @since Jan-8-2013
     */
    public DebateStudentPair deleteDebatePair(int index) throws IllegalArgumentException {
        if (index >= 0 && index < debatePairTree.size()) {
            DebateStudentPair deletedPair = (DebateStudentPair) debatePairTree.getNodeData(index);
            String deleteKey = deletedPair.getCode();
            debatePairTree.delete(deleteKey);

            Student[] pairStudentArray = deletedPair.getStudentArray();

            Student leftStudent = pairStudentArray[0];
            Student currentStudent = (Student) studentTree.search(leftStudent.getName());
            currentStudent.setDebateStudentPair(null);

            Student rightStudent = pairStudentArray[1];
            currentStudent = (Student) studentTree.search(rightStudent.getName());
            currentStudent.setDebateStudentPair(null);

            return deletedPair;
        }
        else throw new IllegalArgumentException();
    }

    /**
     * The searchDebatePair method finds a specific student.
     * <p>
     * If the student pair exists, this method finds it and returns its index in
     * the debatePairTree.
     *
     * @param code code of the student pair to search for
     * @return student's index in the daPairTree
     * @throws IllegalArgumentException if the code is an empty string
     * @throws NoSuchElementException if the code has not been found
     *
     * @author Zbyněk Stara
     * @version 1.0 (Jan-11-2013)
     * @since Jan-11-2013
     */
    public int searchDebatePair(String code) throws IllegalArgumentException, NoSuchElementException {
        if (!code.equals("")) {
            Object [] debatePairTreeArray = debatePairTree.getDataArray();
            boolean studentPairFound = false;
            int studentPairIndex = -999;
            for (int i = 0; i < debatePairTreeArray.length; i++) {
                if (((DebateStudentPair) debatePairTreeArray[i]).getCode().equals(code)) {
                    studentPairFound = true;
                    studentPairIndex = i;
                    break;
                }
            }
            if (studentPairFound) {
                return studentPairIndex;
            }
            else throw new NoSuchElementException();
        }
        else throw new IllegalArgumentException();
    }

    /**
     * This method returns a string summary of this school:
     * name, ooStudentNumber, oiStudentNumber, isStudentNumber,
     * daPairTree.size(), debatePairTree.size().
     *
     * @return a string with the information about the school
     *
     * @author Zbyněk Stara
     */
    @Override
    public String toString() {
        return "Name: " + name + "; " +
                "OO: " + ooStudentNumber + ", " +
                "OI: " + oiStudentNumber + ", " +
                "IS: " + isStudentNumber + ", " +
                "DA: " + daPairTree.size() + ", " +
                "Debate: " + debatePairTree.size();
    }
}
