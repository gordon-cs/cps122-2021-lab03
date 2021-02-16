/**
 *  AddressBookController.java
 *
 *  Copyright (c) 2000, 2001, 2005, 2011 - Russell C. Bjork
 *
 */
 
package addressbook;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

/** An object of this class performs operations on the address book in
 *  response to user gestures on the GUI
 *
 */
public class AddressBookController
{
    /** Constructor
     *
     *  @param fileSystem the object to use for interacting with the file system
     */

    public AddressBookController(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
        this.gui = new AddressBookGUI(this);
        gui.setAddressBook(new AddressBook());
        gui.setVisible(true);
    }
    
    /** Do the Add a Person Use Case.
     */
    public void doAdd()
    { 
        // Set up for the the dialog box
        
        String [] fieldNames = { "First Name", "Last Name", "Address", 
                               "City", "State", "ZIP", "Phone" };

        // Get new person from the user

        String [] information = MultiInputPane.showMultiInputDialog(
            gui, fieldNames, "Enter new Person");
            
        // Add new person to book and update GUI, unless canceled or illegal
                
        if (information != null)
        {
            String first = information[0],
                   last = information[1],
                   address = information[2],
                   city = information[3],
                   state = information[4],
                   zip = information[5],
                   phone = information[6];
                
            AddressBook theBook = gui.getAddressBook();
            
            if (theBook.getPersonInformation(
    			Person.fullName(first, last)) != null)
                gui.reportError("A person of that name is already in the list");
            else        
                theBook.addPerson(first, last, address, city,
                                            state, zip, phone);
	}
    }
    
    /** Do the Edit a Person use case 
     *
     *  @param name the name of the person whose entry is to be edited
     *
     */
    public void doEdit(String name)
    {
        if (name == null)
        {
            gui.reportError("You must select a person in the list");
            return;
        }
        
        String [] initialValues = gui.getAddressBook().getPersonInformation(name);
        if (initialValues == null)
        {
            gui.reportError("Name not found");
            return;
        }
                    
        // Set up for the the dialog box
        
        String [] fieldNames = { "Address", "City", "State", "ZIP", "Phone" };
                                    
        // Get updated information from the user
        
        String [] updatedInformation = MultiInputPane.showMultiInputDialog(
            gui, fieldNames, initialValues, "Editing " + name);
        
        // Modify person in book, unless canceled
        
        if (updatedInformation != null)
            gui.getAddressBook().updatePerson(name, 
                                              updatedInformation[0],
                                              updatedInformation[1],
                                              updatedInformation[2],
                                              updatedInformation[3],
                                              updatedInformation[4]);
   }
    
    /** Do the Delete a Person use case 
     *
     *  @param name the name of the person whose entry is to be deleted
     */
    public void doDelete(String name)
    { 
        if (name == null)
        {
            gui.reportError("You must select a person in the list");
            return;
        }
        
        // Ask user to confirm

        if (JOptionPane.showConfirmDialog(
                    gui, "Are you sure you want to delete " + name + "?",
                    "Confirm delete", JOptionPane.YES_NO_OPTION) 
                == JOptionPane.YES_OPTION)
            gui.getAddressBook().removePerson(name);
    }
    
    /** Do the Sort Entries by Name Use Case
     */
    public void doSortByName()
    {
        gui.getAddressBook().sortByName(); 
    }

    /** Do the Sort Entries by ZIP Use Case
     */
    public void doSortByZip()
    {
        gui.getAddressBook().sortByZip(); 
    }
    
    /** Do the Find Use Case
     *
     *  @param startingIndex the first position in the address book to consider
     */
    public void doFind(int startingIndex)
    {
        lastSearchCriterion = JOptionPane.showInputDialog(gui, "Search for?");
        if (lastSearchCriterion != null)
        {
            lastSearchResult = gui.getAddressBook().search(lastSearchCriterion, 
                                                           startingIndex);
            gui.searchFound(lastSearchResult);
        }
    }
    
    /** Do the Find Again UseCase
     */
    public void doFindAgain()
    {
        if (lastSearchResult < 0)
            doFind(-1);
        else
        {
            lastSearchResult = gui.getAddressBook().search(lastSearchCriterion,
                                                           lastSearchResult + 1);
            gui.searchFound(lastSearchResult);
        }
    }   
    
    /** Do the Create New Address Book Use Case
     */
    public void doNew()
    {
        if (gui.getAddressBook().getChangedSinceSaved())
        {
            if (doOfferSaveChanges() != PROCEED)
                return;
        }
        gui.setAddressBook(new AddressBook());        
    }
    
    /** Do the Open Existing Address Book Use Case
     */
    public void doOpen() throws IOException, ClassNotFoundException
    {
        if (gui.getAddressBook().getChangedSinceSaved())
        {
            if (doOfferSaveChanges() != PROCEED)
                return;
        }
        
		JFileChooser chooser = 
			new JFileChooser(System.getProperty("user.dir"));
			
		if (chooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION)
		{
			File toOpen = chooser.getSelectedFile();
			gui.setAddressBook(fileSystem.readFile(toOpen));
		}
     }

    /** Do the Save Address Book Use Case
     *
     *  @return true if the save operation completed successfully
     */
    public boolean doSave() throws IOException
    {
        File file = gui.getAddressBook().getFile();
        if (file == null)
            return doSaveAs();
        else
        {
            fileSystem.saveFile(gui.getAddressBook(), file);
            return true;
        }
    }
    
    /** Do the Save Address Book As use case  
     *
     *  @return true if the save operation completed successfully
     */
    public boolean doSaveAs() throws IOException
    { 
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        if (chooser.showSaveDialog(gui) == JFileChooser.APPROVE_OPTION)
        {
            File saveTo = chooser.getSelectedFile();
            fileSystem.saveFile(gui.getAddressBook(), saveTo);
            return true;
        }
        else
            return false;
    }
    
    /** Do the Print Mailing Labels Use Case
     *
     *  @exception IOException if there was any problem writing the address book
     */
    public void doPrintMailingLabels() throws IOException
    {
        String directory = fileSystem.getDefaultDirectory();
        JFileChooser chooser =  new JFileChooser(directory);
        if (chooser.showDialog(gui, "Print") == JFileChooser.APPROVE_OPTION)
        {
            PrintWriter writer = 
                new PrintWriter(new FileWriter(chooser.getSelectedFile()));
            gui.getAddressBook().printMailingLabels(writer);
            if (writer.checkError())
                throw new IOException();
            writer.close();
        }
    }
    
    /** Do the Quit Program Use Case.  This one needs to be public, since,
     *  on the Mac platform, a Quit can be chosen in the Application menu
     *
     *  @exception IllegalStateException if the user cancels
     */
    public void doQuit()
    {
        if (gui.getAddressBook().getChangedSinceSaved())
            if (doOfferSaveChanges() == DONT_PROCEED)
                throw new IllegalStateException();
                
        System.exit(0);
    }

    /** Do Offer to Save Changes extension.  This method is called if the user
     *  initiates a new, open, or quit operation with unsaved changes to the
     *  address book.  The user is offered an opportunity to save those changes
     *  before proceeding. 
     *
     *  @return one of the following:
     *
     *      PROCEED - if the user answers "yes" to the question and the file is
     *                saved successfully, or the user answers "no" to the question
     *      DONT_PROCEED - if the user answers "yes" to the question and the file
     *                is not saved successfully, or the user answers "cancel" to
     *                the question.  The original new, open,   or quit operation
     *                should be cancelled.
     *       
     */
    private boolean doOfferSaveChanges()
    { 
        int answer = JOptionPane.showConfirmDialog(gui,
            "There are unsaved changes.  Save them?", "Save changes",
            JOptionPane.YES_NO_CANCEL_OPTION);
            
        switch (answer)
        {
            case JOptionPane.NO_OPTION:
            
                return PROCEED;
                
            case JOptionPane.YES_OPTION:
            
                try
                {
                    if (doSave())
                        return PROCEED;
                    else
                        return DONT_PROCEED;
                }
                catch(IOException e)
                {
                    gui.reportError("Problem writing the file: " +
                                     e.getMessage());
                    return DONT_PROCEED;
                }
                    
            case JOptionPane.CANCEL_OPTION:
            default:                            // To keep compiler happy
                    
                return DONT_PROCEED;
        }
    }
    
    // Possible return values for the above
    
    private static boolean PROCEED = true;
    private static boolean DONT_PROCEED = false;

    // The interface to the file system
    
    private FileSystem fileSystem;
    
    // The GUI
    
    private AddressBookGUI gui;
    
    // The criterion and location of the last successful search, if any
    // null and -1 respectively initially or if the last search was not
    // successful
    
    private String lastSearchCriterion;
    private int lastSearchResult;
}
