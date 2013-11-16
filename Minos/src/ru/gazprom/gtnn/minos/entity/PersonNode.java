package ru.gazprom.gtnn.minos.entity;

import java.util.Calendar;
import java.util.Date;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "PersonTable")
public class PersonNode {
	@TableColumn
	public int personID;
	@TableColumn
	public String personSurname;		 
	@TableColumn
	public String personName;			 
	@TableColumn
	public String personPatronymic;
	@TableColumn
	public String personSex;
	@TableColumn
	public Date personBirthDate;
	
	public PersonNode() { }
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if(!(obj instanceof PersonNode))		
			return false;		
			
		return (this.personID == ((PersonNode)obj).personID ? true : false);
	}

	@Override
	public int hashCode() {		
		return personID;
	}

	@Override
	public String toString() {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(personBirthDate);

		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(personID).append("\n").
		append("surname=").append(personSurname).append("\n").
		append("name=").append(personName).append("\n").
		append("patronymic=").append(personPatronymic).append("\n").
		append("sex=").append(personSex).append("\n").
		append("birthDate=").append(personBirthDate).append("\n").
		append(currentDate.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)).append("\n");
		
		return sb.toString();
	}

	private static Calendar currentDate;
	static { // static initialization
		currentDate = Calendar.getInstance();
		currentDate.setTime(new Date());
	}
}
