package ru.gazprom.gtnn.minos.entity;

import java.util.Calendar;
import java.util.Date;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "PersonTable")
public class PersonNode extends BasicNode<Integer> {
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
	
	//public PositionNode personPosition;
	public int personPositionID = 0;
	public int personDivisionID = 0;
		
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
		cal1.setTime(personBirthDate != null ? personBirthDate : new Date());

		StringBuilder sb = new StringBuilder();
		sb.append("[id=").append(personID).append(" ] ").
		append("[surname=").append(personSurname).append(" ] ").
		append("[name=").append(personName).append(" ] ").
		append("[patronymic : ").append(personPatronymic).append(" ] ").
		append("[ sex :").append(personSex).append(" ] ").
		append("[ birthDate :").append(personBirthDate).append(" ] ").
		append("[ Age : ").append(currentDate.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)).append(" ] ").
		append("PositionID : ").append(personPositionID).
		append("DivisionID : ").append(personDivisionID);
	//	append("[ Position : ").append(personPosition == null ? " null" : personPosition.positionName).append(" ] ");		
		
		return sb.toString();
	}

	private static Calendar currentDate;
	static { // static initialization
		currentDate = Calendar.getInstance();
		currentDate.setTime(new Date());
	}
	@Override
	public Integer getID() {		
		return personID;
	}
}
