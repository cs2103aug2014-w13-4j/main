package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.Test;

import storage.tagStorage.TagStorage;

public class TagStorageTest {

	@Test
	public void testCanAddAndUpdateTags() {
		try {
            // clear the file before testing
            PrintWriter writer = new PrintWriter("tagStorage.data");
            writer.print("");
            writer.close();

			TagStorage tagStorage = new TagStorage("tagStorage.data");
            ArrayList<String> tags = new ArrayList<String>();
            int tagsBufferSize = tagStorage.getAllTags().size();

            // add in two new tags, it should increase the tag buffer size
            tags.add("CS2103");
            tags.add("Software Engineering");
            tagStorage.updateTagToFile(tags);
            tagsBufferSize += 2;
            assertEquals(tagsBufferSize, tagStorage.getAllTags().size());
            assertEquals("CS2103", tagStorage.getAllTags().get(tagsBufferSize - 2));
            assertEquals("Software Engineering", tagStorage.getAllTags().get(tagsBufferSize - 1));

            // add in two existing tags and one new tag, it should be updated automatically
            tags.add("I like Software Engineering");
            tagStorage.updateTagToFile(tags);
            assertEquals(tagsBufferSize + 1, tagStorage.getAllTags().size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @Test
    public void testCanReadTagsFromFile() {
        try {
            TagStorage tagStorage = new TagStorage("tagStorage.data");
            int tagsBufferSize = tagStorage.getAllTags().size();
            assertNotEquals(0, tagsBufferSize);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
