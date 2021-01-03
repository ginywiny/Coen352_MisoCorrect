//Reference cited in report
//Red-Black tree and merge sort algorithms based on textbook
import java.io.*;
import java.util.*;

class Coen352ProjectPart1 {
    public static void main(String[] args){

        long startTime = System.currentTimeMillis(); //Start program time

        String dictionaryFile = args[0];
        String inputFile = args[1];
        StringBuilder fileContents = new StringBuilder(); //StringBuilder to append contents from file

        //----------------------------Reading from dictionary--------------------
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
        int test = 0;
        try {
            File file = new File(inputFile);
            
            //Scan file contents
            scan = new Scanner(file);

            //Append each line and add a separator "," to split
            while (scan.hasNextLine()) {
                textFile.append(scan.nextLine() + " ");
                test++;
            }
        }
        catch(Exception exception) {
            exception.fillInStackTrace();
            System.out.println(test);
        }
        scan.close();

        //Get words from clean text file, remove all punctuation, capitals, and split into string array
        String[] textFileWords = textFile.toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().split(" ");
        int textFileWordsLength = textFileWords.length;
        
        //----------------------------------Place words from textfile into tree------------------------
        for (int i = 0 ; i < textFileWordsLength; i++) {
            Integer temp = tree.get(textFileWords[i]);
            if (temp != null) {
                tree.put(textFileWords[i], ++temp);
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
        Comparable[] inputKeyValue = keyValueString.toString().split(",");
        String[] reapeatedKeyValue = keyValueString.toString().split(",");


        //---------------------------------------------------Merge Sort---------------------------------------------
        MergeSort.sort(inputKeyValue);

        StringBuilder outputFrequency = new StringBuilder();
        StringBuilder outputRepeated = new StringBuilder();

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

        //-----------------------------Writing outputs to text files---------------------------------
        try {
            FileWriter writerFrequency = new FileWriter("./Part A - Clean/frequencies.txt");
            FileWriter writerRepeated = new FileWriter("./Part A - Clean/repeated.txt");
            writerFrequency.write(outputFrequency.toString());
            writerRepeated.write(outputRepeated.toString());
            writerFrequency.close();
            writerRepeated.close();
        }
        catch(Exception exception) {
            exception.fillInStackTrace();
        }

        long endTime = System.currentTimeMillis();  //End program time
        System.out.println("Program runtime: " + (endTime - startTime));
    }


//--------------------------------------Merge Sort----------------------------------------------
    public static class MergeSort {

        private static Comparable[] aux;

        public static void sort(Comparable[] a) {
            aux = new Comparable[a.length];
            sort(a, 0, a.length - 1);
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

        private int size() {
            return size(root);
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