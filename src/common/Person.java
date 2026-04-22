package common;



import java.io.Serializable;

import java.util.ArrayList;

import java.util.List;



public class Person implements Serializable {

private static final long serialVersionUID = 1L;



private final String id;

private final String firstName;

private final String lastName;

private final String dateOfBirth;


private final List<String> diseases;



public Person(String id, String firstName, String lastName, String dateOfBirth) {

this.id = id;

this.firstName = firstName;

this.lastName = lastName;

this.dateOfBirth = dateOfBirth;

this.diseases = new ArrayList<>();

}



public String id() {

return id;

}



public String firstName() {

return firstName;

}



public String lastName() {

return lastName;

}



public String dateOfBirth() {

return dateOfBirth;

}



public List<String> diseases() {

return diseases;

}



public void addDisease(String disease) {

diseases.add(disease);

}



@Override

public String toString() {

return "[" + id + "] " + firstName + " " + lastName + " (DOB: " + dateOfBirth + ")";

}

}