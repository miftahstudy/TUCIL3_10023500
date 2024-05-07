import java.io.*;
import java.util.*;

public class WordLadderUCS {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.println("Selamat Datang di Miftahul Jannah Word Ladder.");
        System.out.println("Beri aku dua kata dan aku akan mengubah dunia");
        System.out.println("File dictionary mempengaruhi panjang kata, pilih yang kamu butuhkan ");

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
                System.out.println("Panjang kata harus sama!");
            } else if (word1.equals(word2)) {
                System.out.println("Kata tidak boleh sama"); // word 1 dan word 2 tidak boleh sama
            } else {
                long startTime = System.currentTimeMillis(); // waktu mulai
                List<String> result = findLadder(word1, word2, dictionary);
                long endTime = System.currentTimeMillis(); // waktu berakhir
                if (result == null) {
                    System.out.println("Unable to find ladder between " + word1 + " and " + word2);
                } else {
                    for (int i = 0; i < result.size() - 1; i++) {
                        System.out.print(result.get(i) + " -> "); // Get word ladder
                    }
                    System.out.println(result.get(result.size() - 1));
                    System.out.println("Panjang node: " + (result.size())); // Output panjang node
                    System.out.println("Solusi ditemukan dalam " + (endTime - startTime) + " ms"); // durasi
                }
            }
        }

        System.out.println("Akhirnya.");
    }

    private static Set<String> getDict(Scanner scanner) {
        Set<String> dictionary = new HashSet<>(); 
        while (true) {
            System.out.println("Pilih file dictionary yang ingin kamu gunakan sesuai panjang kata");
            System.out.print("Masukkan nama file dictionary secara lengkap:  ");
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

    private static List<String> findLadder(String word1, String word2, Set<String> dictionary) {
        PriorityQueue<WordNode> queue = new PriorityQueue<>(Comparator.comparingInt(w -> w.cost));
        Map<String, Integer> costs = new HashMap<>();
        Map<String, String> parents = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.offer(new WordNode(word1, 0));
        costs.put(word1, 0);
        parents.put(word1, null);

        while (!queue.isEmpty()) {
            WordNode current = queue.poll();
            String currentWord = current.word;
            visited.add(currentWord);

            if (currentWord.equals(word2)) {
                return reconstructPath(parents, currentWord);
            }

            for (String neighbor : getNeighbors(currentWord, dictionary)) {
                if (!visited.contains(neighbor)) {
                    int newCost = costs.get(currentWord) + 1; // Assuming cost of each step is 1
                    if (!costs.containsKey(neighbor) || newCost < costs.get(neighbor)) {
                        costs.put(neighbor, newCost);
                        parents.put(neighbor, currentWord);
                        queue.offer(new WordNode(neighbor, newCost));
                    }
                }
            }
        }

        return null; 
    }

    private static List<String> getNeighbors(String word, Set<String> dictionary) {
        List<String> neighbors = new ArrayList<>();
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char originalChar = chars[i];
            for (char c = 'a'; c <= 'z'; c++) {
                if (c != originalChar) {
                    chars[i] = c;
                    String neighbor = new String(chars);
                    if (dictionary.contains(neighbor)) {
                        neighbors.add(neighbor);
                    }
                }
            }
            chars[i] = originalChar; 
        }
        return neighbors;
    }

    private static List<String> reconstructPath(Map<String, String> parents, String end) {
        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(current);
            current = parents.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    static class WordNode {
        String word;
        int cost;

        WordNode(String word, int cost) {
            this.word = word;
            this.cost = cost;
        }
    }
}
