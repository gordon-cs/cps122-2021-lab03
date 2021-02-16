/**
 *  Person.java
 *
 *  Copyright (c) 2005, 2011 - Russell C. Bjork
 *
 */
 
package addressbook;

import java.io.Serializable;
import java.util.Comparator;

/** An object of this class maintains information about a single individual
 *  in the address book
 */
public class Person implements Serializable
{
    /** Constructor - attributes specified individually
     *
     *  @param firstName the person's first name
     *  @param lastName the person's last name
     *  @param address the person's address
     *  @param city the person's city
     *  @param state the person's state
     *  @param zip the person's zip
     *  @param phone the person's phone
     */
    public Person(String firstName,
                  String lastName, 
                  String address,
                  String city,
                  String state,
                  String phone,
                  String zip)
   {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
    }
    
    /** Get the full name of a person in the form last, first
     * 
     *  @return the person's name
     */
    public String getFullName()
    {
          return fullName(firstName, lastName);
    }
    
    /** Get the full name corresponding to a given first and last name
     *	
     *	@param firstName the first name
     *	@param lastName the last name
     *	@return the corresponding full name, formatted appropriately
     */
    public static String fullName(String firstName, String lastName)
    {
		return lastName + ", " + firstName;    
    }
     
    /** Accessor for the person's address
     *
     *  @return the person's address
     */
    public String getAddress()
    {
        return address;
    }
    
    /** Accessor for the person's city
     *
     *  @return the person's city
     */
    public String getCity()
    {
        return city;
    }
    
    /** Accessor for the person's state
     *
     *  @return the person's state
     */
    public String getState()
    {
        return state;
    }
    
    /** Accessor for the person's zip
     *
     *  @return the person's zip
     */
    public String getZip()
    {
        return zip;
    }
    
    /** Accessor for the person's phone
     *
     *  @return the person's phone
     */
    public String getPhone()
    {
        return phone;
    }
    
    /** Update the person with new information.  Note that the name
     *  cannot be changed, but the other information can be
     *
     *  @param address the person's new address
     *  @param city the person's new city
     *  @param state the person's new state
     *  @param zip the person's new zip
     *  @param phone the person's new phone
     */
    public void update(String address,
                       String city,
                       String state,
                       String zip,
                       String phone)
    {
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
    }
    
    /** Test to see whether this person contains a search criterion
     * 
     *  @param criterion the search criterion
     *  @return true if the criterion is contained in any field
     */
    public boolean contains(String criterion)
    {
        return firstName.contains(criterion) ||
               lastName.contains(criterion) ||
               address.contains(criterion) ||
               city.contains(criterion) ||
               state.contains(criterion) ||
               zip.contains(criterion) ||
               phone.contains(criterion);
    }
    
    // Stored information about the person
    
    private String firstName, lastName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    
    /** Comparator for comparing two persons by alphabetical order of name
     */
    public static class CompareByName implements Comparator<Person>
    {
        /** Compare two Persons by name
         *
         *  @param person1 the first person
         *  @param person2 the second person
         *  @return a negative number if person1 belongs before person2 in
         *          alphabetical order of name; 0 if they are equal; a
         *          positive number if person1 belongs after person2
         */
        public int compare(Person person1, Person person2)
        {
            int result = person1.lastName.compareTo(person2.lastName);
            if (result == 0)
                return person1.firstName.compareTo(person2.firstName);
            else
               return result;
        }       
    }
    
    /** Comparator for comparing two persons by order of zip code, with ties
     *  broken by name
     */
    public static class CompareByZip implements Comparator<Person>
    {
        /** Compare two Persons by zip, with ties broken by name
         *
         *  @param person1 the first person
         *  @param person2 the second person
         *  @return a negative number if person1 belongs before person2 in
         *          order of zip with ties broken by name; 0 if they are equal
         *          on both name and zip; a positive number if person1 belongs 
         *          after person2 in order of zip with ties broken by name.  
         */
        public int compare(Person person1, Person person2)
        {
            int result = person1.zip.compareTo(person2.zip);
            if (result == 0)
                return new CompareByName().compare(person1, person2);
            else
                return result;
        }       
    }
}

    
    
      

