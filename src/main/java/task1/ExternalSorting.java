package task1;

import java.io.*;
import java.util.*;

/**
 * Created by arybalko on 17/09/2018.
 */
public class ExternalSorting {
    private final static String TMP_ADDITION = "_tmp{id}";

    private ExternalSorting() {
    }

    public static void sort(String fileName, Comparator<Object[]> comparator) throws Exception {
        String[] supportFileNames = null;
        String supportFIleForInitData = null;
        try {
            //prepare suppFiles, series
            supportFileNames = initSupportFiles(fileName);
            long series = 1L;

            supportFIleForInitData = supportFileNames[supportFileNames.length - 1];
            String[] supportFileForSeries = Arrays.copyOfRange(supportFileNames, 0, supportFileNames.length - 1);
            copyInitialData(fileName, supportFIleForInitData);
            long elementCount;
            do {
                //1 step
                splitFileWithSeries(fileName, supportFileForSeries, series);

                //2 step
                elementCount = mergeSupportFilesWithSeries(fileName, supportFileForSeries, series, comparator);
                series = doubleSeries(series);
            } while (series < elementCount);
        } catch (Exception e) {
            if (supportFileNames != null && supportFIleForInitData != null) {
                copyInitialData(supportFIleForInitData, fileName);
            }
            throw e;
        } finally {
            deleteSupportFiles(supportFileNames);
        }
    }

    private static void copyInitialData(String fromFileName, String toFileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fromFileName)));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(toFileName)))) {
            String s;
            boolean firstLine = true;
            while ((s = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    writer.println();
                }
                writer.print(s);
            }
        }
    }

    private static long mergeSupportFilesWithSeries(String fileName, String[] supportFileNames,
                                                    long series, Comparator<Object[]> comparator)
            throws Exception {
        File file = new File(fileName);
        long currentElementCount = 0L;
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            Scanner[] supportScanners = openSupportScanners(supportFileNames);

            boolean firstLine = true;
            while (supportScanners[0].hasNextLine() && supportScanners[1].hasNextLine()) {

                String strFromSupportFile1 = supportScanners[0].nextLine();
                String strFromSupportFile2 = supportScanners[1].nextLine();

                Object[] dataFromSupportFile1 = strFromSupportFile1.split(",");
                Object[] dataFromSupportFile2 = strFromSupportFile2.split(",");

                int currentCountOfSeriesFile1 = 0;
                int currentCountOfSeriesFile2 = 0;

                while (currentCountOfSeriesFile1 < series && currentCountOfSeriesFile2 < series) {
                    if (comparator.compare(dataFromSupportFile1, dataFromSupportFile2) <= 0) {
                        if (!firstLine) {
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.write(strFromSupportFile1);
                        currentElementCount++;
                        currentCountOfSeriesFile1++;
                        firstLine = false;
                        if (currentCountOfSeriesFile1 < series && supportScanners[0].hasNextLine()) {
                            strFromSupportFile1 = supportScanners[0].nextLine();
                            dataFromSupportFile1 = strFromSupportFile1.split(",");
                        } else {
                            strFromSupportFile1 = null;
                            dataFromSupportFile1 = null;
                            break;
                        }
                    } else {
                        if (!firstLine) {
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.write(strFromSupportFile2);
                        currentElementCount++;
                        currentCountOfSeriesFile2++;
                        firstLine = false;
                        if (currentCountOfSeriesFile2 < series && supportScanners[1].hasNextLine()) {
                            strFromSupportFile2 = supportScanners[1].nextLine();
                            dataFromSupportFile2 = strFromSupportFile2.split(",");
                        } else {
                            strFromSupportFile2 = null;
                            dataFromSupportFile2 = null;
                            break;
                        }
                    }
                }

                while (currentCountOfSeriesFile1 < series && strFromSupportFile1 != null) {
                    if (!firstLine) {
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.write(strFromSupportFile1);
                    currentElementCount++;
                    currentCountOfSeriesFile1++;
                    firstLine = false;
                    if (currentCountOfSeriesFile1 < series && supportScanners[0].hasNextLine()) {
                        strFromSupportFile1 = supportScanners[0].nextLine();
                        dataFromSupportFile1 = strFromSupportFile1.split(",");
                    } else {
                        break;
                    }
                }

                while (currentCountOfSeriesFile2 < series && strFromSupportFile2 != null) {
                    if (!firstLine) {
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.write(strFromSupportFile2);
                    currentElementCount++;
                    currentCountOfSeriesFile2++;
                    firstLine = false;
                    if (currentCountOfSeriesFile2 < series && supportScanners[1].hasNextLine()) {
                        strFromSupportFile2 = supportScanners[1].nextLine();
                        dataFromSupportFile2 = strFromSupportFile2.split(",");
                    } else {
                        break;
                    }
                }
            }

            while (supportScanners[0].hasNextLine()) {
                if (!firstLine) {
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(supportScanners[0].nextLine());
                currentElementCount++;
                firstLine = false;
            }

            while (supportScanners[1].hasNextLine()) {
                if (!firstLine) {
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(supportScanners[1].nextLine());
                currentElementCount++;
                firstLine = false;
            }
            closeSupportScanners(supportScanners);

        }
        return currentElementCount;
    }

    private static void splitFileWithSeries(String fileName, String[] supportFileNames, long series) throws IOException {
        File file = new File(fileName);
        try (Scanner scanner = new Scanner(file)) {
            BufferedWriter[] supportWriters = openSupportWriters(supportFileNames);

            long currentElementCount = 0L;
            String currentString;
            int currentIdSupportFile = 0;

            boolean[] firstLine = new boolean[]{true, true};
            while (scanner.hasNextLine()) {
                currentString = scanner.nextLine();
                currentElementCount++;
                if (!firstLine[currentIdSupportFile]) {
                    supportWriters[currentIdSupportFile].newLine();
                }
                supportWriters[currentIdSupportFile].write(currentString);

                firstLine[currentIdSupportFile] = false;
                if (currentElementCount % series == 0) {
                    currentIdSupportFile = changeCurrentSupportFile(currentIdSupportFile);
                }
            }

            closeSupportWriters(supportWriters);
        }
    }

    private static int changeCurrentSupportFile(int currentIdSupportFile) {
        return (currentIdSupportFile + 1) % 2;
    }

    private static long doubleSeries(long series) {
        return series * 2;
    }

    private static String[] initSupportFiles(String fileName) throws IOException {
        List<String> supportFileNames = new ArrayList<>();

        String[] splitFileName = fileName.split("\\.");
        for (int i = 0; i < 3; i++) {
            String supportFileName = splitFileName[0] + TMP_ADDITION.replaceAll("\\{id\\}", String.valueOf(i)) + "." + splitFileName[1];
            supportFileNames.add(supportFileName);
            File supportFile = new File(supportFileName);
            if (!supportFile.exists()) {
                supportFile.createNewFile();
            }
        }
        return supportFileNames.toArray(new String[supportFileNames.size()]);
    }

    private static void deleteSupportFiles(String[] supportFileNames) {
        if (supportFileNames != null) {
            for (String supportFileName : supportFileNames) {
                File supportFile = new File(supportFileName);
                if (supportFile.exists()) {
                    supportFile.delete();
                }
            }
        }
    }

    private static Scanner[] openSupportScanners(String[] supportFileNames) throws FileNotFoundException {
        List<Scanner> scanners = new ArrayList<>();
        for (String supportFileName : supportFileNames) {
            scanners.add(new Scanner(new File(supportFileName)));
        }
        return scanners.toArray(new Scanner[scanners.size()]);
    }

    private static void closeSupportScanners(Scanner[] supportScanners) {
        for (Scanner scanner : supportScanners) {
            scanner.close();
        }
    }

    private static void closeSupportWriters(BufferedWriter[] supportWriters) throws IOException {
        for (BufferedWriter bufferedWriter : supportWriters) {
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    private static BufferedWriter[] openSupportWriters(String[] supportFileNames) throws FileNotFoundException {
        List<BufferedWriter> bufferedWriters = new ArrayList<>();
        for (String supportFileName : supportFileNames) {
            bufferedWriters.add(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(supportFileName))));
        }
        return bufferedWriters.toArray(new BufferedWriter[bufferedWriters.size()]);
    }
}
