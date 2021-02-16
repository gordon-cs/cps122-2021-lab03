/**
 *  AddressBook.java 
 *
 *  Copyright (c) 2005, 2011 - Russell C. Bjork
 *
 */

package addressbook;

import java.io.File;
import java.io.Serializable;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;
import java.util.Observable;

/** An object of this class maintains the collection of Person objects that
 *  constitute an address book
 */
public class AddressBook extends Observable implements Serializable
{
    /** Constructor - create a new, empty address book
     */
    public AddressBook()
    {
        collection = new Vector<Person>();
        file = null;
        changedSinceLastSave = false;
    }
    
    /** Provide a list of the the names of all the persons in the collection
     *
     *  @return an array of Strings, each representing the name of one person
     */
    public String [] getNames()
    {
        String [] result = new String [collection.size()];
        for (int i = 0; i < collection.size(); i ++)
            result[i] = collection.elementAt(i).getFullName();
        return result;
    }
    
    /** Add a new Person to the collection
     *
     *  @param firstName the person's first name
     *  @param lastName the person's last name
     *  @param address the person's address
     *  @param city the person's city
     *  @param state the person's state
     *  @param zip the person's zip
     *  @param phone the person's phone
     */
    public void addPerson(String firstName,
                          String lastName,
                          String address,
                          String city,
                          String state,
                          String zip,
                          String phone)
    {
        Person newPerson = new Person(firstName, 
                                         lastName, 
                                         address, 
                                         city, 
                                         state, 
                                         zip, 
                                         phone);
        collection.addElement(newPerson);
        changedSinceLastSave = true;
        setChanged();
        notifyObservers();
    }
    
    /** Provide current information about a person in the address book.
     *
     *  @param name the desired name
     *  @return an array of Strings, each containing one piece of stored
     *          information about this person, or null if no such person exists
     */
    public String [] getPersonInformation(String name)
    {
        int index = findIndex(name);
        if (index >= 0)
        {
            Person person = collection.elementAt(index);
            String [] result = 
                { person.getAddress(),
                  person.getCity(),
                  person.getState(),
                  person.getZip(),
                  person.getPhone()
                };
            return result;
        }
        else
            return null;
    }
    
    /** Update stored information about a person
     *
     *  @param name the name of the person (which cannot be updated)
     *  @param address the person's new address
     *  @param city the person's new city
     *  @param state the person's new state
     *  @param zip the person's new zip
     *  @param phone the person's new phone
     *
     *  @exception IllegalArgumentException if the specified person does not exist
     */
    public void updatePerson(String name,
                             String address,
                             String city,
                             String state,
                             String zip,
                             String phone) throws IllegalArgumentException
    {
        int index = findIndex(name);
        if (index >= 0)
        {
            collection.elementAt(index).update(address, city, state, zip, phone);
            changedSinceLastSave = true;
            setChanged();
            notifyObservers();
        }
        else
            throw new IllegalArgumentException("No such person");
    }
    
    /** Remove a specific person from the collection
     *
     *  @param name the desired name
     *
     *  @exception IllegalArgumentException if the specified person does not exist
     */
    public void removePerson(String name) throws IllegalArgumentException
    {
        int index = findIndex(name);
        if (index >= 0)
        {
            collection.removeElementAt(index);
            changedSinceLastSave = true;
            setChanged();
            notifyObservers();
        }
        else
            throw new IllegalArgumentException("No such person");
    }
    
    /** Sort the collection by name
     */
    public void sortByName()
    {
        Collections.sort(collection, new Person.CompareByName());
        changedSinceLastSave = true;
        setChanged();
        notifyObservers();
    }
    
    /** Sort the collection by ZIP
     */
    public void sortByZip()
    {
        Collections.sort(collection, new Person.CompareByZip());
        changedSinceLastSave = true;
        setChanged();
        notifyObservers();
    }
    
    /** Search the collection for a person matching given criteria
     * 
     *  @param criterion the criterion for the search
     *  @param startingIndex the position to start the search from
     *  @return the index of the first person at or after startingIndex which
     *          has a field that contains the criterion, or -1 if there is no such person
     */
    public int search(String criterion, int startingIndex)
    {
        for (int i = startingIndex; i < collection.size(); i ++)
            if (collection.elementAt(i).contains(criterion))
                return i;
        return -1;
    }
    
    /** Get the File this address book was most recently read from or saved to
     *
     *  @return the most recent File - if any - null if none
     */
    public File getFile()
    {
        return file;
    }
    
    /** Set the File this address book was most recently read from or saved to
     *
     *  @param file the file just used to read or save this object
     */
    public void setFile(File file)
    {
        this.file = file;
    }
    
    /** Get the title of this address book - based on the most recently used file
     *
     *  @return the title of this address book - "Untitled" if none
     */
    public String getTitle()
    {
        if (file == null)
            return "Untitled";
        else
            return file.getName();
    }
    
    /** Print the collection of persons in order.
     *
     *  @param writer the writer to print to
     */
    public void printMailingLabels(PrintWriter writer)
    {
        for (int i = 0; i < collection.size(); i ++)
        {
            Person person = collection.elementAt(i);
            writer.println(person.getFullName());
            writer.println(person.getAddress());
            writer.println(person.getCity() + " " + person.getState() + " " +
                               person.getZip());
            writer.println();
        }
    }
    
    /** Find out whether this address book has been changed since last open / save
     *
     *  @return true if this address book has been changed since the last open / save;
     *          false if not 
     */
    public boolean getChangedSinceSaved()
    {
        return changedSinceLastSave;
    }
    
    /** Record that an open/save operation has taken place, rendering this
     *  address book unchanged since the last such operation
     */
    public void setUnchangedSinceLastSave()
    {
        changedSinceLastSave = false;
    }
    
    /** Auxiliary to various methods - get the Person object from the collection
     *  that corresponds to a given name
     *
     *  @param name the desired name
     *  @return the index where the corresponding person occurs, or -1 if the
     *          person does not occur
     */
    private int findIndex(String name)
    {
        for (int i = 0; i < collection.size(); i ++)
        {
            Person person = collection.elementAt(i);
            if (person.getFullName().equals(name))
                return i;
        }
        
        return -1;
    }
    
    // The collection of persons is stored in a vector
    
    private Vector<Person> collection;
    
    // Other information that must be maintained
    
    private File file;
    private boolean changedSinceLastSave;
    
    // Method to facilitate testing
    
    void setupTests()
    {
        addPerson("Anthony", "Aardvark", "10 Skunk Hollow Lane", "Wenham",
                             "MA", "01984", "927-2300");
        addPerson("Zelda", "Zebra", "5 Zoo Road", "Beverly",
                             "MA", "01915", "927-0001");
        addPerson("George", "Gopher", "Tunnel 37", "Hamilton",
                             "MA", "01936", "468-5555");
        addPerson("Clarence", "Cat", "127 Litter Box Ln", "Ipswich",
                             "MA", "01938", "356-9999");
        addPerson("Charlene", "Cat", "127 Litter Box Ln", "Ipswich",
                             "MA", "01938", "356-9999");
        addPerson("Boris", "Buffalo", "Town Common", "Hamilton",
                             "MA", "01936", "468-5555");
        addPerson("Bertha", "Buffalo", "14 Grassy Fields Rd", "Wenham",
                             "MA", "01984", "927-2300");
        addPerson("Maxwell", "Moose", "12 You Can't Get There From Here Rd",
                  "TAR2", "ME", "None", "None"); 
    }
}
    
    
    
    
    
    
    
    
    
    