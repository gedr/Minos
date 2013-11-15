package ru.gazprom.gtnn.minos.models.entity;

import java.util.Date;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "Person")
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
/*
	@Override
	public String toString() {
	
		StringBuilder sb = new StringBuilder();
		sb.append(surname).append(" ").
		append(name).append(" " ).
		append(patronymic).
		append("  [ Пол :").append(sex).append(" ]").		
		append("  [ Возраст :").append(birthDate).append(" ]");
		return sb.toString();
	}
*/
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(personID).append("\n").
		append("surname=").append(personSurname).append("\n").
		append("name=").append(personName).append("\n").
		append("patronymic=").append(personPatronymic).append("\n").
		append("sex=").append(personSex).append("\n").
		append("birthDate=").append(personBirthDate).append("\n");
		
		return sb.toString();
	}

	
}
