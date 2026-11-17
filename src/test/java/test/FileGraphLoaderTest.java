
package test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import software.ulpgc.pathfinder.FileGraphLoader;
import software.ulpgc.pathfinder.GraphContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileGraphLoaderTest {

    @TempDir
    Path tempDir;

    private File createFile(String content) throws IOException {
        Path path = tempDir.resolve("graph.csv");
        Files.writeString(path, content);
        return path.toFile();
    }

    @Test
    void testLoadValidGraph() throws IOException {
        File file = createFile("A,B,1.0\nB,C,2.0\nA,C,5.0");

        GraphContainer graph = new FileGraphLoader(file).load();

        assertEquals(3.0, graph.pathWeightBetween("A", "C"));
        assertEquals(3, graph.shortestPathBetween("A", "C").size());
    }

    @Test
    void testInvalidNumberOfFields() throws IOException {
        File file = createFile("INVALID\nA,B,2.0");

        GraphContainer graph = new FileGraphLoader(file).load();

        assertEquals(2.0, graph.pathWeightBetween("A", "B"));
    }

    @Test
    void testInvalidWeight() throws IOException {
        File file = createFile("A,B,abc\nA,B,4.0");

        GraphContainer graph = new FileGraphLoader(file).load();

        assertEquals(4.0, graph.pathWeightBetween("A", "B"));
    }

    @Test
    void testEmptyFile() throws IOException {
        File file = createFile("");

        GraphContainer graph = new FileGraphLoader(file).load();

        assertNotNull(graph);
    }

    @Test
    void testMissingFile() {
        File file = tempDir.resolve("missing.csv").toFile();

        assertThrows(IOException.class, () ->
                new FileGraphLoader(file).load()
        );
    }
}
