package ru.gazprom.gtnn.minos.models.entity;

import java.util.Date;

public class PersonNode {
	public int id;
	public String surname;		 
	public String name;			 
	public String patronymic;
	public String	sex;
	public Date birthDate;
	private static final Date TODAY = new Date();
	private static final long MSinYEAR = 1000*60*60*24*366;
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if(this.getClass() != obj.getClass())
			return false;		
			
		return (this.id == ((PersonNode)obj).id ? true : false);
	}

	@Override
	public int hashCode() {		
		return id;
	}

	@Override
	public String toString() {
		//long dif = (TODAY.getTime() - birthDate.getTime()) / MSinYEAR;
		
		/*
GregorianCalendar c1 = new GregorianCalendar();
GregorianCalendar c2 = new GregorianCalendar();
c1.set(2000, 1, 1);
c2.set(2010,1, 1);
c2.add(GregorianCalendar.MILLISECOND, -1 * c1.getTimeInMillis());
		 */
	
		StringBuilder sb = new StringBuilder();
		sb.append(surname).append(" ").
		append(name).append(" " ).
		append(patronymic).
		append("  [ Пол :").append(sex).append(" ]").		
		append("  [ Возраст :").append(birthDate).append(" ]");
		return sb.toString();
	}


}
