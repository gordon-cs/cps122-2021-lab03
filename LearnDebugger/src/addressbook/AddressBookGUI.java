/**
 *  AddressBookGUI.java
 *
 *  Copyright (c) 2000, 2001, 2005, 2011 - Russell C. Bjork
 *
 */
 
package addressbook;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Observer;
import java.util.Observable;

/** An object of this class allows interaction between the program and the 
 *  human user.
 */
public class AddressBookGUI extends JFrame implements Observer
{
    /** Constructor
     *
     *  @param controller the controller which performs operations in
     *         response to user gestures on this GUI
     */

    public AddressBookGUI(final AddressBookController controller)
    {
        this.controller = controller;
        
        // Create and add components for the main window
        
        nameListContents = new DefaultListModel();
        nameList = new JList(nameListContents);
        JScrollPane listPane = new JScrollPane(nameList);
        nameList.setVisibleRowCount(10);
        listPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10), 
            BorderFactory.createLineBorder(Color.gray, 1)));
        getContentPane().add(listPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("     Add    ");
        buttonPanel.add(addButton);
        editButton = new JButton("    Edit    ");
        buttonPanel.add(editButton);
        deleteButton = new JButton("   Delete   ");
        buttonPanel.add(deleteButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        // Create and add File menu
        
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        newItem = new JMenuItem("New");
        newItem.setAccelerator(keystroke(KeyEvent.VK_N, 0));
        fileMenu.add(newItem);
        openItem = new JMenuItem("Open...");
        openItem.setAccelerator(keystroke(KeyEvent.VK_O, 0));
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        saveItem = new JMenuItem("Save");
        saveItem.setAccelerator(keystroke(KeyEvent.VK_S, 0));
        fileMenu.add(saveItem);
        saveAsItem = new JMenuItem("Save As...");
        saveAsItem.setAccelerator(keystroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK));
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        printMailingLabelsItem = new JMenuItem("Print Mailing Labels");
        printMailingLabelsItem.setAccelerator(keystroke(KeyEvent.VK_P, 0));
        fileMenu.add(printMailingLabelsItem);
        fileMenu.addSeparator();
        quitItem = new JMenuItem("Quit");
        quitItem.setAccelerator(keystroke(KeyEvent.VK_Q, 0));
        fileMenu.add(quitItem);
        
        // Create and add sort menu
        
        JMenu sortMenu = new JMenu("Sort");
        menuBar.add(sortMenu);
        sortByNameItem = new JMenuItem("Sort by name");
        sortMenu.add(sortByNameItem);
        sortByZipItem = new JMenuItem("Sort by ZIP");
        sortMenu.add(sortByZipItem);
       
        // Create and add search menu
        
        JMenu searchMenu = new JMenu("Search");
        menuBar.add(searchMenu);
        findItem = new JMenuItem("Find");
        findItem.setAccelerator(keystroke(KeyEvent.VK_F, 0));
        searchMenu.add(findItem);
        findAgainItem = new JMenuItem("Find Again");
        findAgainItem.setAccelerator(keystroke(KeyEvent.VK_G, 0));
        searchMenu.add(findAgainItem);
        
        // Add the action listeners for the buttons, menu items, and close box 
        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                controller.doAdd();
            }
        });
          
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                controller.doEdit((String) nameList.getSelectedValue());
            }
        });
          
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                controller.doDelete((String) nameList.getSelectedValue());
            }
        });
        
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            { 
                controller.doNew();
            }
        });
            
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    controller.doOpen();
                }
                catch(Exception exception)
                {
                    reportError("Problem reading the file: " +
                                 exception);
                } 
            }
        });
            
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            { 
                try
                {   
                    controller.doSave();
                    // The address book is not actually changed by saving it,
                    // but there are some GUI items that may need to change
                    // These will do the right thing even if the operation was
                    // cancelled
                    setTitle(addressBook.getTitle());
                    saveItem.setEnabled(addressBook.getChangedSinceSaved());
                }
                catch(Exception exception)
                {
                    reportError("Problem writing the file: " +
                                 exception);
                } 
            }
        });
            
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            { 
                try
                {
                    controller.doSaveAs();
                    // The address book is not actually changed by saving it,
                    // but there are some GUI items that may need to change
                    // These will do the right thing even if the operation was
                    // cancelled
                    setTitle(addressBook.getTitle());
                    saveItem.setEnabled(addressBook.getChangedSinceSaved());
               }
                catch(Exception exception)
                {
                    reportError("Problem writing the file: " +
                                 exception);
                } 
            }
        });
            
        printMailingLabelsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            { 
                try
                {
                    controller.doPrintMailingLabels();
                }
                catch(Exception exception)
                {
                    reportError("Problem writing the file: " +
                                 exception);
                } 
            }
        });
            
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            { 
                controller.doQuit();
            }
        });
            
        sortByNameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            { 
                controller.doSortByName();
            }
        });
              
        sortByZipItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            { 
                controller.doSortByZip();
            }
        });
        
        findItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                controller.doFind(nameList.getSelectedIndex() + 1);
            }
        });
                    
        findAgainItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                controller.doFindAgain();
            }
        });
        
        // Handle double-click on a person as equivalent to a request
        // to edit
        
        nameList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) 
            {
                if (e.getClickCount() == 2) 
                {
                    int index = nameList.locationToIndex(e.getPoint());
                    controller.doEdit((String) nameList.getModel().getElementAt(index));
                }
            }
        });
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            { 
                controller.doQuit();
            }
        });
        
        // Cannot be done until after components are created
        
        saveItem.setEnabled(false);
        findAgainItem.setEnabled(false);
        
        pack();
    }
    
    /** Accessor for the address book this GUI displays
     *
     *  @return the current address book for this GUI
     */
    public AddressBook getAddressBook()
    {
        return addressBook;
    }
    
    /** Mutator to change the address book this GUI displays
     *
     *  @param addressBook the new address book for this GUI
     */
    public void setAddressBook(AddressBook addressBook)
    {
        this.addressBook = addressBook;
        addressBook.addObserver(this);
        update(addressBook, null);
    }
    
    /** Report an error to the user
     *
     *  @param message the message to display
     */
    public void reportError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error message",
                                      JOptionPane.ERROR_MESSAGE);
    }
    
    /** Report the results of a search
     * 
     *  @param index the index of the person found, or -1 if no one
     *         was found
     */
    public void searchFound(int index)
    {
        if (index >= 0)
        {
            nameList.setSelectedIndex(index);
            nameList.ensureIndexIsVisible(index);
            findAgainItem.setEnabled(true);
        }
        else
        {
            nameList.clearSelection();
            reportError("No match found");
            findAgainItem.setEnabled(false);
        }
    }
    
   /** Create a platform-independent keystroke for menu accelerators
     *
     *  @param key the menu key
     *  @param modifiers other keys that must be pressed with this key - other
     *         than the platform specific prefix (e.g. control on many
     *         platforms, command on Macintoshes)
     *  @return a KeyStroke object representing this selection
     */
    private KeyStroke keystroke(int key, int modifiers) 
    {
        return KeyStroke.getKeyStroke(key, 
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | 
                    modifiers);
    }                                      
        
    /** Method required by the Observer interface - update the display
     *  in response to any change in the address book
     */
    public void update(Observable o, Object arg)
    {
        if (o == addressBook)
        {
            int currentIndex = nameList.getSelectedIndex();
            
            nameListContents.removeAllElements();
            String [] names = addressBook.getNames();
            for (int i = 0; i < names.length; i ++)
                nameListContents.addElement(names[i]);
            
            if (currentIndex >= 0)
                nameList.ensureIndexIsVisible(currentIndex);
            else
                nameList.ensureIndexIsVisible(nameListContents.getSize() - 1);
                
            nameList.repaint();
            
            setTitle(addressBook.getTitle());
            saveItem.setEnabled(addressBook.getChangedSinceSaved());
            findAgainItem.setEnabled(false);
        }
    }
    
    // GUI components and menu items
    
    private DefaultListModel nameListContents;
    private JList nameList;
    private JButton addButton, editButton, deleteButton;
    private JMenuItem newItem, openItem, saveItem, saveAsItem, printMailingLabelsItem, quitItem;
    private JMenuItem sortByNameItem, sortByZipItem;
    private JMenuItem findItem, findAgainItem;
    
    // The controller that performs operations in response to user gestures
    
    private AddressBookController controller;
    
    // The address book this GUI displays / operates on
    
    private AddressBook addressBook;    
}
