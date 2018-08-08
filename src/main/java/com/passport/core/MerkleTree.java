package com.passport.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wu Created by SKINK on 2018/7/16.
 */
public class MerkleTree {

  private List<Transaction> transactions;

  public MerkleTree(List<Transaction> list){
    this.transactions = list;
  }

  public List<byte[]> buildMerkleTree() {
    // The Merkle root is based on a tree of hashes calculated from the transactions:
    //
    //     root
    //      / \
    //   A      B
    //  / \    / \
    // t1 t2 t3 t4
    //
    // The tree is represented as a list: t1,t2,t3,t4,A,B,root where each
    // entry is a hash.
    //
    // The hashing algorithm is double SHA-256. The leaves are a hash of the serialized contents of the transaction.
    // The interior nodes are hashes of the concenation of the two child hashes.
    //
    // This structure allows the creation of proof that a transaction was included into a block without having to
    // provide the full block contents. Instead, you can provide only a Merkle branch. For example to prove tx2 was
    // in a block you can just provide tx2, the hash(tx1) and B. Now the other party has everything they need to
    // derive the root, which can be checked against the block header. These proofs aren't used right now but
    // will be helpful later when we want to download partial block contents.
    //
    // Note that if the number of transactions is not even the last tx is repeated to make it so (see
    // tx3 above). A tree with 5 transactions would look like this:
    //
    //         root
    //        /     \
    //       1        5
    //     /   \     / \
    //    2     3    4  4
    //  / \   / \   / \
    // t1 t2 t3 t4 t5 t5
    ArrayList<byte[]> tree = new ArrayList<>();
    // Start by adding all the hashes of the transactions as leaves of the tree.

    for (Transaction transaction : transactions) {
      tree.add(transaction.getHash());
    }
    // Offset in the list where the currently processed level starts.
    int levelOffset = 0;
    // Step through each level, stopping when we reach the root (levelSize == 1).
    for (int levelSize = transactions.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
      // For each pair of nodes on that level:
      for (int left = 0; left < levelSize; left += 2) {
        // The right hand node can be the same as the left hand, in the case where we don't have enough
        // transactions.
        int right = Math.min(left + 1, levelSize - 1);
        byte[] leftBytes = reverseBytes(tree.get(levelOffset + left));
        byte[] rightBytes = reverseBytes(tree.get(levelOffset + right));
        tree.add(reverseBytes(hashTwice(leftBytes, 0, 32, rightBytes, 0, 32)));
      }
      // Move to the next level.
      levelOffset += levelSize;
    }
    return tree;
  }

  private byte[] reverseBytes(byte[] bytes) {
    // We could use the XOR trick here but it's easier to understand if we don't. If we find this is really a
    // performance issue the matter can be revisited.
    byte[] buf = new byte[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      buf[i] = bytes[bytes.length - 1 - i];
    }
    return buf;
  }

  /**
   * Calculates the hash of hash on the given byte ranges.
   */
  private byte[] hashTwice(byte[] input1, int offset1, int length1,
      byte[] input2, int offset2, int length2) {
    MessageDigest digest = newDigest();
    digest.update(input1, offset1, length1);
    digest.update(input2, offset2, length2);
    return digest.digest(digest.digest());
  }

  private MessageDigest newDigest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      // Can't happen.
      throw new RuntimeException(e);
    }
  }
}
