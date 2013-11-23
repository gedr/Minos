package ru.gazprom.gtnn.minos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Loader {

	class Node {
		public List<Node> child;		
		Path path;
		String name;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Loader l =new Loader();
		l.readDir("c:\\tmp\\Minos\\");
		//l.fileLoader(Paths.get("c:\\tmp\\Minos\\√–”œœ¿ › ŒÕŒÃ» ¿ » ‘»Õ¿Õ—€\\ÒÚ‡ıÓ‚‡ÌËÂ\\1.txt") );
	}
	
	public void readDir(String startPath) {		
		Node node = readDir(Paths.get(startPath), 1);
		printNode(node, 1);
	}
	
	public void printNode(Node node, int level) {
		String s = "";
		for(int i = 0; i < level * 3; i++)
			s += " ";
		System.out.println(s + "<dir> " + node.name);
		if(node.path != null ) {
			System.out.println(s + " <file> " + node.path);
			//fileLoader(node.path);
		}
		
		if(node.child == null) 
			return;
		
		
		for(Node n : node.child) {
			printNode(n, level + 1);
		}
		
		
		
	}
	
	private Node readDir(Path startPath, int level) {
		Node node = new Node();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(startPath)) {					
			node.name = startPath.toString();
		    for (Path file: stream) {		    	
		        if(Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {		        	
		        	if(node.child == null)
		        		node.child = new ArrayList<>();
		        	node.child.add(readDir(file, level + 1));
		        }

		        // System.out.println(s + (Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS) ? "<dir>" : "<file>" ) + file.getFileName());
		        if(file.getFileName().toString().equalsIgnoreCase("1.txt"))
		        	node.path = file;
		        	//System.out.println("˝ÚÓ Ì‡¯ Ù‡ÈÎ"); 
		    }
		} catch (IOException | DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		}
		return node;

	}
	
	public void fileLoader(Path file) {
		
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line = null;
		    int step = 1;
		    while ((line = reader.readLine()) != null) {
		    	if ( line.contains("$") ) {
		    			step = 1;
		    			continue;
		    	}
		    	if(line.isEmpty()) { 
		    		step++;
		    		continue;
		    	}
		    	switch(step) {
		    	case 1:
		    		System.out.println("<competence name> "  + line);
		    		break;
		    	case 2:
		    		System.out.println("<competence desc> "  + line);
		    		break;
		    	case 3:
		    	case 4:
		    	case 5:
		    	case 6:
		    	case 7:
		    		System.out.println("<indicaot level =" + (step - 2) + " > "  + line);
		    		break;

		    	}
		    		
		    	
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	static final int STEP_COUNT = 7;
		

}
