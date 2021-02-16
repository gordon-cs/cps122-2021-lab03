/*
 * AddressBookApplication.java
 *
 * Main program for address book application
 *
 * Copyright (c) 2001, 2003, 2005, 2011 - Russell C. Bjork
 *
 */

package addressbook;

/** Main class for the Address Book example
 */
public class AddressBookApplication
{
    /** Main method for program
     */    
    public static void main(String [] args)
    {
        new AddressBookApplication();
    }
    
    /** Constructor - create the objects which do all the work.
     */    
    private AddressBookApplication()
    {
        fileSystem = new FileSystem();
        controller = new AddressBookController(fileSystem);                
    }
    
    private FileSystem fileSystem;
    private AddressBookController controller;
}

    
