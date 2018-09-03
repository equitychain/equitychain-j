package com.passport.webhandler;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import com.passport.core.Block;
import com.passport.core.BlockHeader;
import com.passport.core.Transaction;
import com.passport.db.dbhelper.DBAccess;
import com.passport.proto.BlockHeaderMessage;
import com.passport.proto.BlockMessage;
import com.passport.proto.TransactionMessage;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlockHandler {

  private static final Logger logger = LoggerFactory.getLogger(BlockHandler.class);

  @Autowired
  private DBAccess dbAccess;


  public boolean checkBlock(Block block) {
    Long blockHeight = block.getBlockHeight();

    if (blockHeight == 1) {
      return Objects.equal(block.getBlockHeader().getHash(), block.calculateFieldValueWithHash());
    }

    if (blockHeight > 1) {
      boolean flag = Objects
          .equal(block.getBlockHeader().getHash(), block.calculateFieldValueWithHash());
      if (!flag) {
        return false;
      }

      Optional<Block> prevBlock = dbAccess.getBlock(blockHeight - 1);
      if (prevBlock.isPresent()) {
        byte[] hashOfPrevBlock = prevBlock.get().getBlockHeader().getHash();
        byte[] prevHashOfCurrentBlock = block.getBlockHeader().getHashPrevBlock();
        if (hashOfPrevBlock.equals(prevHashOfCurrentBlock)) {
          return true;
        }
      }
    }

    return false;
  }


  public Block convertBlockMessage2Block(BlockMessage.Block blockMessage) {

    BlockHeader blockHeader = new BlockHeader();
    BlockHeaderMessage.BlockHeader blockHeaderMessage = blockMessage.getBlockHeader();
    blockHeader.setTimeStamp(blockHeaderMessage.getTimeStamp());
    blockHeader.setHashPrevBlock(blockHeaderMessage.getHashPrevBlock().toByteArray());
    blockHeader.setHashMerkleRoot(blockHeaderMessage.getHashMerkleRoot().toByteArray());
    blockHeader.setHash(blockHeaderMessage.getHash().toByteArray());

    Block block = new Block();
    block.setBlockSize(blockMessage.getBlockSize());
    block.setBlockHeader(blockHeader);
    block.setTransactionCount(blockMessage.getTransactionsCount());
    block.setBlockHeight(blockMessage.getBlockHeight());
    block.setTransactions(new ArrayList<>());

    blockMessage.getTransactionsList().forEach((TransactionMessage.Transaction trans) -> {
      Transaction transaction = new Transaction();
      transaction.setHash(trans.getHash().toByteArray());
      transaction.setSignature(trans.getSignature().toByteArray());
      transaction.setValue(trans.getValue().toByteArray());
      transaction.setPayAddress(trans.getPayAddress().toByteArray());
      transaction.setReceiptAddress(trans.getReceiptAddress().toByteArray());
      transaction.setEggPrice(trans.getEggPrice().toByteArray());
      transaction.setEggMax(trans.getEggMax().toByteArray());
      transaction.setTime(trans.getTimeStamp().toByteArray());

      block.getTransactions().add(transaction);
    });

    return block;
  }


  public BlockMessage.Block.Builder convertBlock2BlockMessage(Block block) {

    BlockHeaderMessage.BlockHeader.Builder blockHeaderBuilder = BlockHeaderMessage.BlockHeader
        .newBuilder();
    blockHeaderBuilder.setTimeStamp(block.getBlockHeader().getTimeStamp());
    blockHeaderBuilder
        .setHashPrevBlock(ByteString.copyFrom(block.getBlockHeader().getHashMerkleRoot()));
    blockHeaderBuilder
        .setHashMerkleRoot(ByteString.copyFrom(block.getBlockHeader().getHashMerkleRoot()));
    blockHeaderBuilder.setHash(ByteString.copyFrom(block.getBlockHeader().getHash()));

    BlockMessage.Block.Builder blockBuilder = BlockMessage.Block.newBuilder();
    blockBuilder.setBlockSize(block.getBlockSize());
    blockBuilder.setBlockHeader(blockHeaderBuilder.build());
    blockBuilder.setTransactionCount(block.getTransactionCount());
    blockBuilder.setBlockHeight(block.getBlockHeight());

    block.getTransactions().forEach((Transaction trans) -> {
      TransactionMessage.Transaction.Builder transactionBuilder = TransactionMessage.Transaction
          .newBuilder();
      if (trans.getPayAddress() != null) {
        transactionBuilder.setPayAddress(ByteString.copyFrom(trans.getPayAddress()));
      }
      transactionBuilder.setReceiptAddress(ByteString.copyFrom(trans.getReceiptAddress()));
      transactionBuilder.setValue(ByteString.copyFrom(trans.getValue()));
      transactionBuilder.setExtarData(ByteString.copyFrom(trans.getExtarData()));
      transactionBuilder.setTimeStamp(ByteString.copyFrom(trans.getTime()));
      transactionBuilder.setHash(ByteString.copyFrom(trans.getHash()));
      if (trans.getSignature() != null) {
        transactionBuilder.setSignature(ByteString.copyFrom(trans.getSignature()));
      }
      if (trans.getEggPrice() != null) {
        transactionBuilder.setEggPrice(ByteString.copyFrom(trans.getEggPrice()));
      }
      if (trans.getEggMax() != null) {
        transactionBuilder.setEggMax(ByteString.copyFrom(trans.getEggMax()));
      }

      blockBuilder.addTransactions(transactionBuilder.build());
    });

    return blockBuilder;
  }
}
