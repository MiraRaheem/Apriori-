/*
* Name: Mira Mohamed
* ID: 20170305
* Group: IS_DS_41
* */

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {
    static Map<Integer, Vector> itemSet = new HashMap<Integer, Vector>();
    static HashMap<Vector<String>, Double> allItemFreq = new HashMap<Vector<String>, Double>();
    static HashMap<Vector<String>, Double> itemFreq = new HashMap<Vector<String>, Double>();
    static HashMap<Vector<String>, Double> finalItemFreq = new HashMap<Vector<String>, Double>();
    static Vector<Vector<String>> dataPerm = new Vector<Vector<String>>();
    static Double Support;
    static Double Conf;
    static int NoOfRows;

    public static void main(String[] args) throws IOException {


        Scanner input = new Scanner(System.in);

        System.out.println("Enter number of rows you want to calc: ");
        NoOfRows = Integer.parseInt(input.nextLine());

        System.out.println("Enter the Support: ");
        Support = Double.valueOf(input.nextLine());

        System.out.println("Enter the Total Confidance: ");
        Conf = Double.valueOf(input.nextLine());
        input.close();

        // read file content
        itemSet = readFile();
        //System.out.println(Support+" "+Conf);


        System.out.println("iteration 1");
        itemFreq = getDistMap();
        System.out.println(itemFreq);

        System.out.println();
        System.out.println();
        System.out.println();
        int i = 2;
        while (!(itemFreq.isEmpty())) {
            System.out.println("iteration" + i);
            finalItemFreq = itemFreq;
            itemFreq = getItemSets();
            System.out.println(itemFreq);

            i++;
            System.out.println();
            System.out.println();
            System.out.println();
        }


        System.out.println("finalItemFreq" + finalItemFreq);
        GetSuppourt();
    }

    public static Map<Integer, Vector> readFile() throws IOException {
        File myFile = new File("C://Users//MIRA//Desktop//Y4 T1//Assignment1/CoffeeShopTransactions.xlsx");
        FileInputStream fis = new FileInputStream(myFile);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = mySheet.iterator();
        Map<Integer, Vector> map = new HashMap<Integer, Vector>();
        for (int i = 0; i < NoOfRows; i++) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Vector<String> v = new Vector<String>();
            if (i > 0) {
                for (int j = 0; j < 6; j++) {
                    if (j < 6) {
                        Cell cell = cellIterator.next();
                        if (j > 2) {

                            v.add(cell.toString());
                            //System.out.println(i + " " + cell.toString() + " " + cell.getColumnIndex() + " " + j);
                        }

                    }
                }
                LinkedHashSet<String> lhSet = new LinkedHashSet<String>(v);

                //clear the vector
                v.clear();

                //add all unique elements back to the vector
                v.addAll(lhSet);
                Collections.sort(v);
                map.put(i, v);
            }
        }
        return map;
    }

    public static HashMap<Vector<String>, Double> getDistMap() {
        //Vector<String> Allitems = new Vector<String>();
        HashMap<String, Double> Allitems = new HashMap<String, Double>();
        HashMap<Vector<String>, Double> allitems = new HashMap<>();
        Vector<String> items = new Vector<String>();
        for (Map.Entry<Integer, Vector> entry : itemSet.entrySet()) {
            items = entry.getValue();
            for (int i = 0; i < items.size(); i++) {
                if (Allitems.containsKey(items.get(i))) {
                    Double counter = Allitems.get(items.get(i));
                    counter = counter + 1;
                    Allitems.put(items.get(i), counter);
                } else {
                    Allitems.put(items.get(i), 1.0);
                }

            }
        }

        Vector<String> delete = new Vector<>();
        for (Map.Entry<String, Double> entry : Allitems.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            if (value < Support) {
                //Allitems.remove(key);
                delete.add(key);
            }
        }

        for (int i = 0; i < delete.size(); i++) {
            Allitems.remove(delete.get(i));
        }

        for (Map.Entry<String, Double> entry : Allitems.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            Vector<String> v = new Vector<String>();
            v.add(key);
            allitems.put(v, value);
        }
        allItemFreq.putAll(allitems);
        return allitems;
    }

    public static HashMap<Vector<String>, Double> getItemSets() {
        HashMap<Vector<String>, Double> newValuesFreq = new HashMap<Vector<String>, Double>();
        Vector<Vector<String>> oldValues = new Vector<Vector<String>>();
        Vector<Vector<String>> newValues = new Vector<Vector<String>>();
        for (Map.Entry<Vector<String>, Double> entry : itemFreq.entrySet()) {
            oldValues.add(entry.getKey());
        }
        for (int i = 0; i < oldValues.size(); i++) {
            for (int j = i + 1; j < oldValues.size(); j++) {
                for (int k = 0; k < oldValues.get(j).size(); k++) {
                    Vector<String> concat = new Vector<>();
                    concat.addAll(oldValues.get(i));
                    concat.add(oldValues.get(j).get(k));
                    Collections.sort(concat);

                    LinkedHashSet<String> lhSet = new LinkedHashSet<String>(concat);
                    concat.clear();
                    concat.addAll(lhSet);
                    if (concat.size() > oldValues.get(i).size() && (!(newValues.contains(concat)))) {

                        newValues.add(concat);
                    }
                }
            }
        }
        for (int j = 0; j < newValues.size(); j++) {
            Vector<String> set_one = new Vector<>();
            Vector<String> set_two = new Vector<>();
            for (Map.Entry<Integer, Vector> entry : itemSet.entrySet()) {
                set_one = newValues.get(j);
                set_two = entry.getValue();
                if (isSubset(set_two, set_one)) {
                    if (newValuesFreq.containsKey(set_one)) {
                        Double counter = newValuesFreq.get(set_one);
                        counter = counter + 1;
                        newValuesFreq.put(set_one, counter);
                    } else {
                        newValuesFreq.put(set_one, 1.0);
                    }
                } else {
                    continue;
                }
            }
        }

        Vector<Vector<String>> delete = new Vector<>();
        for (Map.Entry<Vector<String>, Double> entry : newValuesFreq.entrySet()) {
            Vector<String> key = entry.getKey();
            Double value = entry.getValue();
            if (value < Support) {
                //Allitems.remove(key);
                delete.add(key);
            }
        }
        for (int i = 0; i < delete.size(); i++) {
            newValuesFreq.remove(delete.get(i));
        }

        allItemFreq.putAll(newValuesFreq);

        return newValuesFreq;
    }

    static boolean isSubset(Vector S1, Vector S2) {
        if (S1.size() > S2.size()) {
            for (int i = 0; i < S2.size(); i++) {
                if (!(S1.contains(S2.get(i)))) {
                    return false;
                }
            }
            return true;
        } else if (S1.size() == S2.size()) {
            if (S1.equals(S2)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static void GetSuppourt() throws IOException {
        for (Map.Entry<Vector<String>, Double> entry : finalItemFreq.entrySet()) {
            Vector<String> Set = entry.getKey();
            int r = Set.size() - 1;
            int n = Set.size();
            String[] array = Set.toArray(new String[Set.size()]);
            Permutation.printCombination(array, n, r);
            for (int i = 0; i < dataPerm.size(); i++) {
                Vector<String> tempPerm = dataPerm.get(i);

                String wildCard = new String();
                for (int j = 0; j < Set.size(); j++) {
                    if (!(tempPerm.contains(Set.get(j)))) {
                        Vector<String> WC = new Vector<>();
                        wildCard = Set.get(j);
                        WC.add(wildCard);
                        Double conf = allItemFreq.get(Set) / allItemFreq.get(WC);
                        System.out.println(tempPerm + " => " + wildCard);
                        System.out.println("Suppourt: " + allItemFreq.get(Set));
                        System.out.println("Confidance: " + (allItemFreq.get(Set) / allItemFreq.get(WC)));
                        if (conf >= Conf)
                            System.out.println("Strong Rule");
                        else
                            System.out.println("Weak Rule");
                        System.out.println();
                        System.out.println();
                        conf = allItemFreq.get(Set) / allItemFreq.get(tempPerm);
                        System.out.println(wildCard + " => " + tempPerm);
                        System.out.println("Suppourt: " + allItemFreq.get(Set));
                        System.out.println("Confidance: " + (allItemFreq.get(Set) / allItemFreq.get(tempPerm)));
                        if (conf >= Conf)
                            System.out.println("Strong Rule");
                        else
                            System.out.println("Weak Rule");
                        break;
                    }
                }

                System.out.println();
                System.out.println();
            }
            dataPerm.clear();
        }
    }

    static class Permutation {

        /* arr[]  ---> Input Array
        data[] ---> Temporary array to store current combination
        start & end ---> Staring and Ending indexes in arr[]
        index  ---> Current index in data[]
        r ---> Size of a combination to be printed */
        static void combinationUtil(String arr[], int n, int r,
                                    int index, String data[], int i) {
            // Current combination is ready to be printed,
            // print it
            if (index == r) {

                if (data.length >= 2) {
                    for (int j = 0; j < r; j += 2) {
                        Vector<String> temp = new Vector<String>();
                        temp.add(data[j]);
                        //System.out.print(data[j] + " ");
                        temp.add(data[j + 1]);
                        dataPerm.add(temp);

                    }
                } else if (data.length < 2) {
                    for (int j = 0; j < r; j++) {
                        Vector<String> temp = new Vector<String>();
                        temp.add(data[j]);
                        dataPerm.add(temp);

                    }
                } else {
                    System.out.println("There's no associtions... IE all elements comes in ones");
                }
                return;
            }

            // When no more elements are there to put in data[]
            if (i >= n)
                return;

            // current is included, put next at next
            // location
            data[index] = arr[i];
            combinationUtil(arr, n, r, index + 1,
                    data, i + 1);

            // current is excluded, replace it with
            // next (Note that i+1 is passed, but
            // index is not changed)
            combinationUtil(arr, n, r, index, data, i + 1);
        }

        // The main function that prints all combinations
        // of size r in arr[] of size n. This function
        // mainly uses combinationUtil()
        static void printCombination(String arr[], int n, int r) {
            // A temporary array to store all combination
            // one by one
            String data[] = new String[r];

            // Print all combination using temprary
            // array 'data[]'
            combinationUtil(arr, n, r, 0, data, 0);
        }

    }

}