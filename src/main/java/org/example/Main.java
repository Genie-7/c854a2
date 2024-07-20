package org.example;

// Necessary imports
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Represents a node in the AVL tree
class AVLNode {
    String word; // The word stored in the node
    int frequency; // Frequency of the word
    int height; // Height of the node in the tree
    AVLNode left, right; // Left and right child nodes

    AVLNode(String word) {
        this.word = word;
        this.frequency = 1;
        this.height = 1;
    }
}

// Represents an AVL tree
class AVLTree {
    private AVLNode root;

    // Returns the height of a node
    private int height(AVLNode N) {
        if (N == null) return 0;
        return N.height;
    }

    // Performs a right rotation on the given node
    private AVLNode rightRotate(AVLNode y) {
        if (y == null) return null;
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        //System.out.println("Performed right rotation on node: " + y.word);

        return x;
    }

    // Performs a left rotation on the given node
    private AVLNode leftRotate(AVLNode x) {
        if (x == null) return null;
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        //System.out.println("Performed left rotation on node: " + x.word);

        return y;
    }

    // Returns the balance factor of the given node
    private int getBalance(AVLNode N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    // Inserts a word into the AVL tree
    public void insert(String word) {
        root = insertRec(root, word);
    }

    // Recursive helper method to insert a word into the AVL tree
    private AVLNode insertRec(AVLNode node, String word) {
        if (node == null) {
            //System.out.println("Creating new node for word: " + word);
            return new AVLNode(word);
        }

        if (word.compareTo(node.word) < 0) {
            //System.out.println("Going left from node: " + node.word);
            node.left = insertRec(node.left, word);
        } else if (word.compareTo(node.word) > 0) {
            //System.out.println("Going right from node: " + node.word);
            node.right = insertRec(node.right, word);
        } else {
            node.frequency++;
            //System.out.println("Word already exists. Incrementing frequency: " + node.word + " (" + node.frequency + ")");
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);
        //System.out.println("Node: " + node.word + " has balance: " + balance);

        if (balance > 1 && word.compareTo(node.left.word) < 0) {
            //System.out.println("Left Left Case: " + node.word);
            return rightRotate(node);
        }

        if (balance < -1 && word.compareTo(node.right.word) > 0) {
            //System.out.println("Right Right Case: " + node.word);
            return leftRotate(node);
        }

        if (balance > 1 && word.compareTo(node.left.word) > 0) {
            //System.out.println("Left Right Case: " + node.word);
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && word.compareTo(node.right.word) < 0) {
            //System.out.println("Right Left Case: " + node.word);
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node; // Return the unchanged node pointer
    }

    // Finds words in the AVL tree that start with the given prefix
    public List<String> findWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        //System.out.println("Searching for prefix: " + prefix);
        findWordsWithPrefixRec(root, prefix, result);
        //System.out.println("Finished searching for prefix: " + prefix);
        return result;
    }

    private void findWordsWithPrefixRec(AVLNode node, String prefix, List<String> result) {
        if (node == null) {
            //System.out.println("Reached a null node, returning.");
            return;
        }

        // Traverse the left subtree if the prefix is smaller than or equal to the current node's word
        if (prefix.compareTo(node.word) <= 0) {
            //System.out.println("Traversing left from node: " + node.word);
            findWordsWithPrefixRec(node.left, prefix, result);
        }

        // Check if the current node's word starts with the prefix
        if (node.word.startsWith(prefix)) {
            //System.out.println("Found word with prefix: " + node.word + " (" + node.frequency + ")");
            result.add(node.word + " (" + node.frequency + ")");
        }

        // Traverse the right subtree regardless of the prefix comparison to ensure we get all matching words
        if (prefix.compareTo(node.word) <= 0 || prefix.compareTo(node.word) > 0) {
            //System.out.println("Traversing right from node: " + node.word);
            findWordsWithPrefixRec(node.right, prefix, result);
        }
    }

    // Prints all words in the AVL tree
    public void printAllWords() {
        List<String> allWords = new ArrayList<>();
        collectAllWords(root, allWords);
        System.out.println("All words in AVL tree: " + allWords);
    }

    // Recursive helper method to collect all words in the AVL tree
    private void collectAllWords(AVLNode node, List<String> result) {
        if (node == null) return;
        result.add(node.word + " (" + node.frequency + ")");
        collectAllWords(node.left, result);
        collectAllWords(node.right, result);
    }

    // Checks if the AVL tree contains the given word
    public boolean contains(String word) {
        return containsRec(root, word);
    }

    // Recursive helper method to check if the AVL tree contains the given word
    private boolean containsRec(AVLNode node, String word) {
        if (node == null) return false;
        if (word.equals(node.word)) return true;
        if (word.compareTo(node.word) < 0) return containsRec(node.left, word);
        return containsRec(node.right, word);
    }
}

class Autocomplete {
    private AVLTree avlTree;

    public Autocomplete() {
        avlTree = new AVLTree();
    }

    // Builds vocabulary from my(Brendan's) remax_listings CSV file
    public void buildVocabularyFromRemaxFile(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            boolean isHeader = true;
            for (String[] record : records) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (record.length > 2) {
                    String address = record[1];
                    String details = record[2];

                    String combined = address + " " + details;

                    String[] words = combined.split("\\W+");
                    for (String word : words) {
                        if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                            //System.out.println("Inserting word: " + word.toLowerCase());
                            avlTree.insert(word.toLowerCase());
                        }
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // Builds vocabulary from Rishabh's combined_scraped_data CSV file
    public void buildVocabularyFromCombinedFile(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            boolean isHeader = true;
            for (String[] record : records) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                for (String cell : record) {
                    String[] words = cell.split("\\W+");
                    for (String word : words) {
                        if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                            avlTree.insert(word.toLowerCase());
                        }
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // Builds vocabulary from Sindhuja's scraped_data CSV file
    public void buildVocabularyFromScrapedDataFile(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            boolean isHeader = true;
            for (String[] record : records) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (record.length > 4) {
                    String address = record[1];
                    String location = record[2];
                    String type = record[3];
                    String listing = record[4];

                    String combined = address + " " + location + " " + type + " " + listing;

                    String[] words = combined.split("\\W+");
                    for (String word : words) {
                        if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                            //System.out.println("Inserting word: " + word.toLowerCase());
                            avlTree.insert(word.toLowerCase());
                        }
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // Builds vocabulary from Supriya's ScrapedData Excel file
    public void buildVocabularyFromExcelFile(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;

            for (Row row : sheet) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                Cell firstColumnCell = row.getCell(0);

                if (firstColumnCell != null) {
                    String combinedText = getCellValue(firstColumnCell);

                    //System.out.println("Read row: " + combinedText);

                    String[] words = combinedText.split("\\W+");
                    for (String word : words) {
                        if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                            //System.out.println("Inserting word: " + word.toLowerCase());
                            avlTree.insert(word.toLowerCase());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Extracts the value from an Excel file's cell
    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        return String.valueOf(cell.getNumericCellValue());
                    case BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                }
            default:
                return "";
        }
    }

    // Builds vocabulary from Sushant's zolo_windsor_listings CSV file
    public void buildVocabularyFromZoloWindsorListingsFile(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            boolean isHeader = true;
            for (String[] record : records) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                for (int i = 4; i <= 9; i++) {
                    if (i < record.length && record[i] != null) {
                        String[] words = record[i].split("\\W+");
                        for (String word : words) {
                            if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                                //System.out.println("Inserting word: " + word.toLowerCase());
                                avlTree.insert(word.toLowerCase());
                            }
                        }
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    // Returns suggestions for the given prefix
    public List<String> getSuggestions(String prefix) {
        prefix = prefix.toLowerCase();
        //System.out.println("Getting suggestions for prefix: " + prefix);
        List<String> wordsWithPrefix = avlTree.findWordsWithPrefix(prefix);

        //System.out.println("Words with prefix '" + prefix + "': " + wordsWithPrefix);

        // Create a min-heap to store the top suggestions
        PriorityQueue<String> minHeap = new PriorityQueue<>((a, b) -> {
            int freqA = Integer.parseInt(a.substring(a.lastIndexOf('(') + 1, a.lastIndexOf(')')));
            int freqB = Integer.parseInt(b.substring(b.lastIndexOf('(') + 1, b.lastIndexOf(')')));
            return Integer.compare(freqA, freqB);
        });

        // Insert words into the min-heap
        for (String word : wordsWithPrefix) {
            //System.out.println("Adding to min-heap: " + word);
            minHeap.offer(word);
            if (minHeap.size() > 5) {
                //System.out.println("Removing from min-heap: " + minHeap.peek());
                minHeap.poll();
            }
        }

        // Extract top suggestions from the min-heap
        List<String> topSuggestions = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            //System.out.println("Extracting from min-heap: " + minHeap.peek());
            topSuggestions.add(minHeap.poll());
        }

        // Sort the top suggestions by frequency and then alphabetically
        Collections.sort(topSuggestions, (a, b) -> {
            int freqA = Integer.parseInt(a.substring(a.lastIndexOf('(') + 1, a.lastIndexOf(')')));
            int freqB = Integer.parseInt(b.substring(b.lastIndexOf('(') + 1, b.lastIndexOf(')')));
            int freqComparison = Integer.compare(freqB, freqA);
            if (freqComparison != 0) {
                return freqComparison;
            }
            return a.compareTo(b);
        });

        //System.out.println("Top suggestions: " + topSuggestions);
        return topSuggestions;
    }

    // Prints all words in the AVL tree
    public void printAllWords() {
        avlTree.printAllWords();
    }

    // Checks if the AVL tree contains the given word
    public boolean contains(String word) {
        return avlTree.contains(word);
    }
}

public class Main {
    public static void main(String[] args) {
        Autocomplete autocomplete = new Autocomplete();

        // Build vocabulary from various sources
        // Since all our groups spreadsheets were formatted differently, each required its own processing algorithm
        autocomplete.buildVocabularyFromRemaxFile("src/main/resources/remax_listings.csv");
        autocomplete.buildVocabularyFromCombinedFile("src/main/resources/combined_scraped_data.csv");
        autocomplete.buildVocabularyFromScrapedDataFile("src/main/resources/scraped_data.csv");
        autocomplete.buildVocabularyFromExcelFile("src/main/resources/ScrapedData.xlsx");
        autocomplete.buildVocabularyFromZoloWindsorListingsFile("src/main/resources/zolo_windsor_listings.csv");

        // Print all words in the AVL tree
        //autocomplete.printAllWords();

        // Prompt the user to enter a prefix for word completions
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a prefix to get word completions:");
        String prefix = scanner.nextLine();

        // Get and print suggestions based on the prefix
        List<String> suggestions = autocomplete.getSuggestions(prefix);
        System.out.println("Suggestions:");
        for (String suggestion : suggestions) {
            System.out.println(suggestion);
        }
    }
}