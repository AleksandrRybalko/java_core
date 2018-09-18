package task1;

import org.junit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by arybalko on 17/09/2018.
 */

public class ExternalSortingTest {
    private final static String[] VALID_DATA = new String[]{"1,test", "4,ram", "2,rty", "2,pbt", "3,testing"};
    private final static String[] CORRECT_DATA_SORT = new String[]{"1,test", "2,pbt", "2,rty", "3,testing", "4,ram"};
    private final static String INVALID_DATA_STRING = "java";
    private final static String INVALID_NUMBER_OF_COLUMN_DATA_STRING = "1";
    private final static String EMPTY_COLUMN_DATA_STRING = ",java";
    private final static String TESTING_FILE_NAME = "testFile.csv";

    @Before
    public void insertValidData() {
        File testingFile = new File(TESTING_FILE_NAME);
        if (!testingFile.exists()) {
            try {
                testingFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (testingFile.exists() && !testingFile.isDirectory()) {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(testingFile)))) {
                boolean firstLine = true;
                for (String data : VALID_DATA) {
                    if (firstLine) {
                        firstLine = false;
                    } else {
                        writer.println();
                    }
                    writer.print(data);
                }
                writer.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void deleteFile() {
        File testingFile = new File(TESTING_FILE_NAME);
        if (testingFile.exists()) {
            testingFile.delete();
        }
    }

    @Test
    public void sortValidData() {
        try {
            ExternalSorting.sort(TESTING_FILE_NAME, new TestComparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] dataFromFile = readDataFromFile(TESTING_FILE_NAME);
        Assert.assertArrayEquals(CORRECT_DATA_SORT, dataFromFile);
    }

    @Test
    public void sortEmptyFile() {
        File file = new File(TESTING_FILE_NAME);
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ExternalSorting.sort(TESTING_FILE_NAME, new TestComparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] dataFromFile = readDataFromFile(TESTING_FILE_NAME);
        Assert.assertArrayEquals(new String[]{}, dataFromFile);
    }

    @Test(expected = NumberFormatException.class)
    public void sortInvalidFormatData() throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(TESTING_FILE_NAME, true)))) {
            writer.append("\n" + INVALID_DATA_STRING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExternalSorting.sort(TESTING_FILE_NAME, new TestComparator());
    }

    @Test(expected = NumberFormatException.class)
    public void sortInvalidCountOfData() throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(TESTING_FILE_NAME, true)))) {
            writer.append("\n" + EMPTY_COLUMN_DATA_STRING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExternalSorting.sort(TESTING_FILE_NAME, new TestComparator());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void sortInvalidCountOfColumnOfData() throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(TESTING_FILE_NAME, true)))) {
            writer.append("\n" + INVALID_NUMBER_OF_COLUMN_DATA_STRING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExternalSorting.sort(TESTING_FILE_NAME, new TestComparator());
    }

    @Test
    public void checkInitialDataAfterException() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(TESTING_FILE_NAME, true)))) {
            writer.append("\n" + INVALID_DATA_STRING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] expectedData = readDataFromFile(TESTING_FILE_NAME);
        try {
            ExternalSorting.sort(TESTING_FILE_NAME, new TestComparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] dataFromFile = readDataFromFile(TESTING_FILE_NAME);
        Assert.assertArrayEquals(expectedData, dataFromFile);
    }


    private String[] readDataFromFile(String fileName) {
        ArrayList<String> buffer = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            String s;
            while ((s = reader.readLine()) != null) {
                buffer.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toArray(new String[buffer.size()]);
    }

    private class TestComparator implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            Integer o11 = Integer.parseInt(o1[0].toString());
            String o12 = o1[1].toString();


            Integer o21 = Integer.parseInt(o2[0].toString());
            String o22 = o2[1].toString();
            if (o11.compareTo(o21) != 0) {
                return o11.compareTo(o21);
            } else {
                return o12.compareTo(o22);
            }
        }
    }
}
