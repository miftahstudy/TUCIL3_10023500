import java.io.*;
import java.util.*;

public class WordLadderGBFS {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.println("Selamat Datang di Miftahul Jannah Word Ladder.");
        System.out.println("Beri aku dua kata dan aku akan mengubah dunia");
        System.out.println("File dictionary mempengaruhi panjang kata, pilih yang kamu butuhkan.");

        Set<String> dictionary = getDict(input); //get kamus(dictn.dat) semua kata
        while (true) {
            String word1 = checkValidity(1, input, dictionary); // check validasi word 1 dan word 2
            if (word1 == null) { // enter
                break;
            }
            String word2 = checkValidity(2, input, dictionary);
            if (word2 == null) {
                break;
            }
            if (word1.length() != word2.length()) { // word 1 dan word 2 harus sama panjangnya
                System.out.println("Words must be the same length!");
            } else if (word1.equals(word2)) {
                System.out.println("You cannot have two of the same word!"); // word 1 dan word 2 tidak boleh sama
            } else {
                long startTime = System.currentTimeMillis(); // waktu mulai
                Stack<String> result = findLadder(word1, word2, dictionary);
                long endTime = System.currentTimeMillis(); // waktu berakhir
                if (result == null) {
                    System.out.println("Tidak ditemukan " + word1 + " dan " + word2);
                } else {
                    for (int i = 0; i < result.size() - 1; i++) {
                        System.out.print(result.get(i) + " -> "); // Get word ladder
                    }
                    System.out.println(result.peek());
                    System.out.println("Panjang node: " + result.size());
                    System.out.println("Solusi ditemukan dalam " + (endTime - startTime) + " ms."); // durasi
                }
            }
        }

        System.out.println("Akhirnya.");
    }

    private static Set<String> getDict(Scanner scanner) {
        Set<String> dictionary = new HashSet<>(); 
        while (true) {
            System.out.println("Pilih file dictionary yang ingin kamu gunakan sesuai panjang kata");
            System.out.print("Masukkan nama file dictionary secara lengkap: ");
            String fileName = scanner.nextLine();
            try (Scanner fileScanner = new Scanner(new File(fileName))) {
                while (fileScanner.hasNextLine()) { 
                    String line = fileScanner.nextLine();
                    dictionary.add(line.toLowerCase()); 
                }
                return dictionary;
            } catch (FileNotFoundException e) {
                System.err.println("Error membaca file dictionary: " + e.getMessage()); // Throws exception 
            }
        }
    }

    private static String checkValidity(int x, Scanner scanner, Set<String> dictionary) {
        while (true) {
            System.out.print("Word #" + x + " (or Enter to quit): ");
            String word = scanner.nextLine().toLowerCase();
            if (word.isEmpty()) { 
                return null;
            }
            if (!dictionary.contains(word)) { 
                System.out.println("Kata itu tidak ada di dictionary. tolong hanya input English word yang valid.");
            } else {
                return word; 
            }
        }

    }

    private static Stack<String> findLadder(String word1, String word2, Set<String> dictionary) {
        PriorityQueue<Stack<String>> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(o -> heuristic(o.peek(), word2))); // Queue with priority based on heuristic
        Set<String> usedWords = new HashSet<>(); 
        Stack<String> initialStack = new Stack<>(); 
        initialStack.push(word1);
        priorityQueue.add(initialStack);

        while (!priorityQueue.isEmpty()) {
            Stack<String> currentStack = priorityQueue.poll(); 
            String currentWord = currentStack.peek(); 
            for (int i = 0; i < currentWord.length(); i++) { 
                StringBuilder strWord = new StringBuilder(currentWord);
                for (char j = 'a'; j <= 'z'; j++) { 
                    strWord.setCharAt(i, j);
                    String neighbor = strWord.toString();
                    if (dictionary.contains(neighbor) && !usedWords.contains(neighbor)) { 
                        if (neighbor.equals(word2)) { 
                            currentStack.push(neighbor);
                            return currentStack;
                        } else {
                            usedWords.add(neighbor); 
                            Stack<String> newStack = new Stack<>(); 
                            newStack.addAll(currentStack);
                            newStack.push(neighbor);
                            priorityQueue.add(newStack); 
                        }
                    }
                }
            }
        }
        return null; 
    }

    private static int heuristic(String word, String target) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) != target.charAt(i)) {
                count++;
            }
        }
        return count;
    }
}
