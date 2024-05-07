import java.io.*;
import java.util.*;

public class WordLadderAStar {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        System.out.println("Selamat Datang di Miftahul Jannah Word Ladder.");
        System.out.println("Beri aku dua kata dan aku akan mengubah dunia");
        System.out.println("File dictionary mempengaruhi panjang kata, pilih yang kamu butuhkan.");

        Set<String> dictionary = getDict(input); // get kamus(dictn.dat) semua kata
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
                System.out.println("word harus sama panjang!");
            } else if (word1.equals(word2)) {
                System.out.println("word sama, ganti!"); // word 1 dan word 2 tidak boleh sama
            } else {
                long startTime = System.currentTimeMillis(); // waktu mulai
                Stack<String> result = findLadder(word1, word2, dictionary);
                long endTime = System.currentTimeMillis(); // waktu berakhir
                long duration = endTime - startTime; // panjang durasi
                if (result == null) {
                    System.out.println("tidak ditemukan ladder antara " + word1 + " dan " + word2);
                } else {
                    for (int i = 0; i < result.size() - 1; i++) {
                        System.out.print(result.get(i) + " -> "); // Get word ladder
                    }
                    System.out.println(result.peek());
                    System.out.println("Panjang node: " + result.size()); // Output panjang node
                    System.out.println("Solusi ditemukan dalam " + duration + " ms.");
                }
            }
        }

        System.out.println("Akhrinya.");
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
            if (!dictionary.contains(word)) { // memastikan word ada di dictionary
                System.out.println("Kata itu tidak ada di dictionary. tolong hanya input English word yang valid.");
            } else {
                return word;
            }
        }
    }

    private static Stack<String> findLadder(String word1, String word2, Set<String> dictionary) {

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(o -> o.fCost));
        Map<String, Node> nodeMap = new HashMap<>();
        Set<String> closedSet = new HashSet<>();

        Node start = new Node(word1, null, 0, heuristic(word1, word2));
        openSet.add(start);
        nodeMap.put(word1, start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.word.equals(word2)) {
                return reconstructPath(current);
            }

            closedSet.add(current.word);

            for (String neighbor : getNeighbors(current.word, dictionary)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                int gCost = current.gCost + 1;
                int hCost = heuristic(neighbor, word2);
                int fCost = gCost + hCost;

                Node neighborNode = nodeMap.getOrDefault(neighbor, new Node(neighbor, null, Integer.MAX_VALUE, Integer.MAX_VALUE));
                if (gCost < neighborNode.gCost) {
                    neighborNode.parent = current;
                    neighborNode.gCost = gCost;
                    neighborNode.fCost = fCost;
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                    nodeMap.put(neighbor, neighborNode);
                }
            }
        }

        return null;
    }

    private static Set<String> getNeighbors(String word, Set<String> dictionary) {
        Set<String> neighbors = new HashSet<>();
        char[] chars = word.toCharArray();
        for (int i = 0; i < word.length(); i++) {
            char originalChar = chars[i];
            for (char c = 'a'; c <= 'z'; c++) {
                chars[i] = c;
                String neighbor = new String(chars);
                if (!neighbor.equals(word) && dictionary.contains(neighbor)) {
                    neighbors.add(neighbor);
                }
            }
            chars[i] = originalChar;
        }
        return neighbors;
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

    private static Stack<String> reconstructPath(Node node) {
        Stack<String> path = new Stack<>();
        while (node != null) {
            path.push(node.word);
            node = node.parent;
        }
        return path;
    }

    static class Node {
        String word;
        Node parent;
        int gCost;
        int fCost;

        Node(String word, Node parent, int gCost, int fCost) {
            this.word = word;
            this.parent = parent;
            this.gCost = gCost;
            this.fCost = fCost;
        }
    }
}
