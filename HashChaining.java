import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class MerkleTreeIntegrityCheck {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java MerkleTreeIntegrityCheck <file1> <file2> ... <fileN>");
            return;
        }

        List<String> filePaths = Arrays.asList(args);

        // Compute the original Top Hash
        String originalTopHash = computeMerkleRoot(filePaths);
        System.out.println("Original Top Hash: " + originalTopHash);

        System.out.println("\nModify a file and re-run to see hash changes.");
    }

    // Compute Merkle Root
    public static String computeMerkleRoot(List<String> filePaths) throws Exception {
        List<String> hashes = new ArrayList<>();

        // Compute hash for each file
        for (String path : filePaths) {
            hashes.add(computeSHA1Hash(readFile(path)));
        }

        // Build Merkle Tree and compute the root hash
        return buildMerkleTree(hashes);
    }

    // Read file content
    public static byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    // Compute SHA-1 hash
    public static String computeSHA1Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(data);
        return bytesToHex(hash);
    }

    // Convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // Build Merkle Tree and return root hash
    public static String buildMerkleTree(List<String> hashes) throws NoSuchAlgorithmException {
        if (hashes.isEmpty()) return "";

        while (hashes.size() > 1) {
            List<String> newHashes = new ArrayList<>();

            // Pairwise hashing
            for (int i = 0; i < hashes.size(); i += 2) {
                if (i + 1 < hashes.size()) {
                    newHashes.add(computeSHA1Hash((hashes.get(i) + hashes.get(i + 1)).getBytes()));
                } else {
                    newHashes.add(hashes.get(i)); // Carry forward last unpaired hash
                }
            }

            hashes = newHashes; // Move to the next level
        }

        return hashes.get(0); // Root hash
    }
}
