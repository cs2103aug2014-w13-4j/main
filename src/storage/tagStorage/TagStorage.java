package storage.tagStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
*
* @author Chuyu 
* This class reads/writes tags to file.
*/
public class TagStorage {
    private File dataFile;
    private ArrayList<String> tagBuffer;

    /**
     * constructor
     */
    public TagStorage(String fileName) throws IOException {
    	String tag;
        dataFile = new File(fileName);

        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }

        Scanner fileScanner = new Scanner(dataFile);
        tagBuffer =  new ArrayList<String>();
        while (fileScanner.hasNextLine()) {
            tag = fileScanner.nextLine();
            tagBuffer.add(tag);
        }   
    }

    // Get all tags
    public ArrayList<String> getAllTags() {
        return tagBuffer;
    }

    // Update tag list when adding or updating tasks
    public void updateTagToFile(ArrayList<String> tags) throws IOException {
        for (String tag: tags) {
            if (tagBuffer.contains(tag)) {
                continue;
            } else {
                addToFile(tag);
                tagBuffer.add(tag);
            }
        }        
    }
    
    private void addToFile(String tag) throws IOException {
    	BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(dataFile, true));
            bufferedWriter.write(tag + "\r\n"); 
        	bufferedWriter.close();            
        } finally { 
        }
    }
}