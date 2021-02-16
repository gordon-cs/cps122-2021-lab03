/**
 *  FileSystem.java
 *
 * Copyright (c) 2005, 2011 - Russell C. Bjork
 *
 */
 
package addressbook;

import java.io.*;

/** An object of this class manages interaction between the address book
 *  program and the file system of the computer it is running on.
 */

public class FileSystem
{
    /** Read a stored file
     *
     *  @param file the file specification for the file to read
     *  @return the AddressBook object stored in the file
     *
     *  @exception IOException if there is a problem reading the file
     *  @exception ClassCastException if the file does not contain an
     *             AddressBook
     *  @exception ClassNotFoundException if the file does not contain
     *             an AddressBook, and the class it does contain is not
     *             found - this should never happen
     */
    public AddressBook readFile(File file) throws IOException, 
                                                  ClassNotFoundException
    {
        ObjectInputStream stream = 
            new ObjectInputStream(new FileInputStream(file));
        AddressBook result = (AddressBook) stream.readObject();
        result.setFile(file);
        result.setUnchangedSinceLastSave();
        defaultDirectory = file.getParent();
        return result;
    }
    
    /** Save an address book to a file
     *
     *  @param addressBook the AddressBook to save
     *  @param file the file specification for the file to create
     *
     *  @exception IOException if there is a problem writing the file
     */
    public void  saveFile(AddressBook addressBook, File file) throws IOException
    {
        ObjectOutputStream stream = 
            new ObjectOutputStream(new FileOutputStream(file));
        stream.writeObject(addressBook);
        addressBook.setFile(file);
        addressBook.setUnchangedSinceLastSave();
        defaultDirectory = file.getParent();
    }

    /** Get the default directory for open/save/print
     * 
     *  @return the default directory, or the user's home directory if there
     *          is none
     */
    public String getDefaultDirectory()
    {
        return defaultDirectory;
    }
        
    // The default directory to use for open/save/print
    
    private String defaultDirectory;
}