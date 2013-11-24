package ru.gazprom.gtnn.minos.entity;

import java.util.Map;

public abstract class BasicNode<T> {
	abstract public T getID();
	public static Map<String, String> names;
}
