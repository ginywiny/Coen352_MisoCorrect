//Reference cited in report
//Red-Black tree and merge sort algorithms based on textbook
import java.io.*;
import java.util.*;

class Coen352ProjectPart3 {
    public static void main(String[] args){

        long startTime = System.currentTimeMillis(); //Start program time

        String dictionaryFile = args[0];
        String inputFile = args[1];
        StringBuilder fileContents = new StringBuilder(); //StringBuilder to append contents from file

        //----------------------------Reading from file from dictionary--------------------
        Scanner scan = null;
        try {
            File file = new File(dictionaryFile);
            scan = new Scanner(file); //Scan file contents
            
            while (scan.hasNextLine()) {
                fileContents.append(scan.nextLine());
            }
        }
        catch(Exception exception) {
            exception.fillInStackTrace();
        }  
        scan.close();

        //---------------------------Inputting Dictionary to tree------------------------------------
        RedBlackTree<String, Integer> tree = new RedBlackTree<>();  //Tree to hold dictionary words

        //Put to lowercase, and make string array
        String[] dictionaryWords = fileContents.toString().toLowerCase().replaceAll("[^a-zA-Z ]", "").split(" ");
        int dictionaryLength = dictionaryWords.length; 

        for (int i = 0; i < dictionaryLength; i++) {  //Insert dictionary in tree
                tree.put(dictionaryWords[i], 0);
        }

        //------------------------------------Inputting text file inputFile------------------------------
        StringBuilder textFile = new StringBuilder();
        try {
            File file = new File(inputFile);
            
            //Scan file contents
            scan = new Scanner(file);

            //Append each line and add a separator "," to split
            while (scan.hasNextLine()) {
                textFile.append(scan.nextLine() + " ");
            }
        }
        catch(Exception exception) {
            exception.fillInStackTrace();
        }
        scan.close();

        //Get words from clean text file, remove all punctuation, capitals, and split into string array
        String[] textFileWords = textFile.toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");
        int textFileWordsLength = textFileWords.length;

        StringBuilder unusedWords = new StringBuilder();
        
        //--------------------------Pass 1: Place words from textfile into tree------------------------
        for (int i = 0 ; i < textFileWordsLength; i++) {
            Integer temp = tree.get(textFileWords[i]);
            if (temp != null) {
                tree.put(textFileWords[i], ++temp);
                //Remove words that were already put in tree
                textFileWords[i] = "";
            }
            //Prepare new list for second pass
            if (!textFileWords[i].equals("")) {
                unusedWords.append(textFileWords[i] + ",");
            }
        }

        //-------------------------Pass 2: Check substrings in tree------------------------------------
        String[] unusedList = unusedWords.toString().split(",");
        int unusedLength = unusedList.length;
        String[] alphabetArray = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r",
                                    "s","t","u","v","w","x","y","z"};
        String mutatedWord = new String("");
        StringBuilder correctedWord = new StringBuilder();

        for (int i = 0; i < unusedLength; i++) {
            boolean putInTree = false;
            
            correctedWord.append(unusedList[i] + ", ");

            for (int j = 0; j < unusedList[i].length(); j++) {
                if (putInTree == true) {
                    break;
                }

                for (int k = 0; k < alphabetArray.length; k++) {
                    if (j == 0) {
                        mutatedWord = alphabetArray[k] + unusedList[i].substring(1,unusedList[i].length());
                    }
                    else {
                        mutatedWord = unusedList[i].substring(0,j) + alphabetArray[k] + unusedList[i].substring(j + 1, unusedList[i].length());
                    }
                    
                    Integer checkTree = tree.get(mutatedWord);
                    if (checkTree != null) {
                        tree.put(mutatedWord,++checkTree);
                        putInTree = true;
                        correctedWord.append(mutatedWord + " \n");
                        break;
                    }
                }
            }
            if (putInTree != true) {
                correctedWord.append("\n");
            }
        }

        //---------------------------------------Acquiring words from tree-----------------------------------
        String[] keyArray = new String[dictionaryLength];
        int[] valueArray = new int[dictionaryLength];
        int index = 0;
        for (int i = 0; i < dictionaryLength; i++) {
            Integer temp = tree.get(dictionaryWords[i]);
            if (temp != null) {
                keyArray[index] = dictionaryWords[i];
                valueArray[index++] = temp;
            }
        }

        //-------------------------------------Substring Method--------------------------------------
        StringBuilder keyValueString = new StringBuilder();
        for (int i = 0; i < dictionaryLength; i++) {
            keyValueString.append(keyArray[i] + " " + valueArray[i] + ",");
        }
        Comparable<String>[] inputKeyValue = keyValueString.toString().split(",");
        String[] reapeatedKeyValue = keyValueString.toString().split(",");
        String[] correctedWordSorted = correctedWord.toString().split("\n");

        //---------------------------------------------------Merge Sort---------------------------------------------
        MergeSort.sort(inputKeyValue);
        MergeSort.sortWords(correctedWordSorted);

        StringBuilder outputFrequency = new StringBuilder();
        StringBuilder outputRepeated = new StringBuilder();
        StringBuilder outputCorrected = new StringBuilder();

        //------------------------------------Create frequency.txt--------------------------------------------------
        for (int i = 0; i < dictionaryLength; i++) {
            if (Integer.parseInt(inputKeyValue[i].toString().substring(inputKeyValue[i].toString().lastIndexOf(" ") + 1)) > 0) {
                outputFrequency.append(inputKeyValue[i]).append("\n");
            }
        }

        //------------------------------------Create repeated.txt--------------------------------------------------
        for (int i = 0; i < dictionaryLength; i++) {
            int tempValue = Integer.parseInt(reapeatedKeyValue[i].toString().substring(reapeatedKeyValue[i].toString().lastIndexOf(" ") + 1));
            if (tempValue > 0) { 
                for (int j = 0; j < tempValue; j++) {
                    outputRepeated.append(reapeatedKeyValue[i].substring(0, reapeatedKeyValue[i].toString().lastIndexOf(" "))).append(" ");
                }
            }
        }

        //---------------------------------Create correct_words_detected.txt--------------------------------------
        for (int i = 0; i < correctedWordSorted.length; i++) {
            if (!correctedWordSorted[i].substring(correctedWordSorted[i].lastIndexOf(" ") - 1, correctedWordSorted[i].lastIndexOf(" ")).equals(",") ) {
                outputCorrected.append(correctedWordSorted[i] + "\n");
            }
        }

        //-----------------------------Writing outputs to text files---------------------------------
        try {
            FileWriter writerFrequency = new FileWriter("./Part C - Mutated [BONUS]/frequencies.txt");
            FileWriter writerRepeated = new FileWriter("./Part C - Mutated [BONUS]/repeated.txt");
            FileWriter writerCorrectedWordsDetected = new FileWriter("./Part C - Mutated [BONUS]/corrected_words_detected.txt");
            writerFrequency.write(outputFrequency.toString());
            writerFrequency.close();
            writerRepeated.write(outputRepeated.toString());
            writerRepeated.close();
            writerCorrectedWordsDetected.write(outputCorrected.toString());
            writerCorrectedWordsDetected.close();
        }
        catch(Exception exception) {
            exception.fillInStackTrace();
        }

        long endTime = System.currentTimeMillis();  //End program time
        System.out.println("Program runtime: " + (endTime - startTime));
    }

//--------------------------------------Merge Sort Class------------------------------------------
public static class MergeSort {

    private static Comparable[] aux;

    public static void sort(Comparable[] a) {
        aux = new Comparable[a.length];
        sort(a, 0, a.length - 1);
    }

    public static void sortWords(Comparable[] a) {
        aux = new Comparable[a.length];
        sortWords(a, 0, a.length - 1);
    }
    
    private static void sort(Comparable[] a, int low, int high) {
        if (high <= low) {
            return;
        }
        int middle = low + (high - low) / 2;
        sort(a, low, middle);
        sort(a, middle + 1, high);
        merge(a, low, middle, high);
    }

    private static void sortWords(Comparable[] a, int low, int high) {
        if (high <= low) {
            return;
        }
        int middle = low + (high - low) / 2;
        sortWords(a, low, middle);
        sortWords(a, middle + 1, high);
        mergeWords(a, low, middle, high);
    }

    private static void merge(Comparable[] a, int low, int middle, int high) {
        int i = low;
        int j = middle + 1;

        for (int k = low; k <= high; k++) {
            aux[k] = a[k];
        }
        for (int k = low; k <= high; k++) {
            if (i > middle) {
                a[k] = aux[j++];
            }
            else if (j > high) {
                a[k] = aux[i++];
            }
            else if (lessSubstring(aux[j], aux[i])) {
                a[k] = aux[j++];
            }
            else {
                a[k] = aux[i++];
            }
        }
    }

    private static void mergeWords(Comparable[] a, int low, int middle, int high) {
        int i = low;
        int j = middle + 1;

        for (int k = low; k <= high; k++) {
            aux[k] = a[k];
        }
        for (int k = low; k <= high; k++) {
            if (i > middle) {
                a[k] = aux[j++];
            }
            else if (j > high) {
                a[k] = aux[i++];
            }
            else if (less(aux[j], aux[i])) {
                a[k] = aux[j++];
            }
            else {
                a[k] = aux[i++];
            }
        }
    }
    
    public static boolean less(Comparable v, Comparable w) {
        Comparable key1 = v.toString().substring(0, v.toString().lastIndexOf(","));
        Comparable key2 = w.toString().substring(0, w.toString().lastIndexOf(","));
        return key1.compareTo(key2) < 0;
    } 

    public static boolean lessSubstring(Comparable v, Comparable w) {
        int val1 = Integer.parseInt(v.toString().substring(v.toString().lastIndexOf(" ") + 1));
        int val2 = Integer.parseInt(w.toString().substring(w.toString().lastIndexOf(" ") + 1));
        return val1 < val2;
    } 
}

    //----------------------------------------Tree Class--------------------------------------------------
    public static class RedBlackTree<Key extends Comparable<Key>, Value> {
        private Node root;

        private static final boolean RED = true;
        private static final boolean BLACK = false;

        private class Node {
            Key key;
            Value val;
            Node left;
            Node right;
            int N;
            boolean colour;

            Node(Key key, Value val, int N, boolean colour) {
                this.key = key;
                this.val = val;
                this.N = N;
                this.colour = colour;
            }
        }

        private boolean isRed(Node node) {
            if (node == null) {
                return false;
            }
            return node.colour == RED;
        }

        private Node rotateLeft(Node node) {
            Node x = node.right;
            node.right = x.left;
            x.left = node;
            x.colour = node.colour;
            node.colour = RED;
            x.N = node.N;
            node.N = 1 + size(node.left) + size(node.right);
            return x;
        }

        private Node rotateRight(Node node) {
            Node x = node.left;
            node.left = x.right;
            x.right = node;
            x.colour = node.colour;
            node.colour = RED;
            x.N = node.N;
            node.N = 1 + size(node.left) + size(node.right);
            return x;
        }
        private void flipColours(Node node) {
            node.colour = RED;
            node.left.colour = BLACK;
            node.right.colour = BLACK;

        }

        private int size(Node node) {
            if (node == null) {
                return 0;
            }
            else {
                return node.N;
            }
        }

        public void put(Key key, Value val) {
            root = put(root, key, val);
            root.colour = BLACK;
        }

    private Node put(Node h, Key key, Value val) {
    if (h == null) {
        return new Node(key, val, 1, RED);
    }
        int cmp = key.compareTo(h.key);

            if (cmp < 0) {
                h.left = put(h.left, key, val);
            }
            else if (cmp > 0) {
                h.right = put(h.right, key, val); 
            }
            else {
                h.val = val;
            }

            if (isRed(h.right) && !isRed(h.left)) {
                h = rotateLeft(h);
            }
            if (isRed(h.left) && isRed(h.left.left)) {
                h = rotateRight(h);
            }
            if (isRed(h.left) && isRed(h.right)) {
                flipColours(h);
            }

            h.N = size(h.left) + size(h.right) + 1;
            return h;
        }

        public Value get(Key key) {
            return get(root,key);
        }

        private Value get(Node x, Key key) {
            if (x == null) {
                return null;
            }
            int cmp = key.compareTo(x.key);
            if (cmp < 0) {
                return get(x.left, key);
            }
            if (cmp > 0) {
                return get(x.right, key);
            }
            else {
                return x.val;
            }
        }
    }   
}