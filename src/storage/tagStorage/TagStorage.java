package storage.tagStorage;

public class TagStorage {
    private File dataFile;
    private ArrayList<String> tagBuffer;

    /**
     * constructor``
     */
    public TagStorage(String fileName) {
        dataFile = new File(fileName);

        if (!dataFile.exist()) {
            dataFile.createNewFile();
        }

        Scanner fileScanner = new Scanner(dataFile);
        tagBuffer =  new ArrayList<Task>();
        while (fileScanner.hasNextLine()) {
            tag = fileScanner.nextLine();
            tagBuffer.add(tag);
        }   
    }

    // Get all tags
    ArrayList<String> getAllTags() {
        return tagBuffer;
    }

    // Update tag list when adding or updating tasks
    private void updateTagToFile(ArrayList<String> tags) {
        for (String tag: task.tags) {
            if (tagBuffer.contains(tag)) {
                continue;
            } else {
                tagFile.addToFile(tag);
                tagBuffer.add(tag);
            }
        }        
    }
}