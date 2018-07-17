// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: BlockMessage.proto

package com.passport.proto;

public final class BlockMessage {
  private BlockMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface BlockOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Block)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int64 blockHeight = 1;</code>
     */
    boolean hasBlockHeight();
    /**
     * <code>required int64 blockHeight = 1;</code>
     */
    long getBlockHeight();

    /**
     * <code>required int64 blockSize = 2;</code>
     */
    boolean hasBlockSize();
    /**
     * <code>required int64 blockSize = 2;</code>
     */
    long getBlockSize();

    /**
     * <code>required int64 totalAmount = 3;</code>
     */
    boolean hasTotalAmount();
    /**
     * <code>required int64 totalAmount = 3;</code>
     */
    long getTotalAmount();

    /**
     * <code>required int64 totalFee = 4;</code>
     */
    boolean hasTotalFee();
    /**
     * <code>required int64 totalFee = 4;</code>
     */
    long getTotalFee();
  }
  /**
   * Protobuf type {@code Block}
   */
  public  static final class Block extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Block)
      BlockOrBuilder {
    // Use Block.newBuilder() to construct.
    private Block(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Block() {
      blockHeight_ = 0L;
      blockSize_ = 0L;
      totalAmount_ = 0L;
      totalFee_ = 0L;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Block(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              blockHeight_ = input.readInt64();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              blockSize_ = input.readInt64();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              totalAmount_ = input.readInt64();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              totalFee_ = input.readInt64();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return BlockMessage.internal_static_Block_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return BlockMessage.internal_static_Block_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              BlockMessage.Block.class, BlockMessage.Block.Builder.class);
    }

    private int bitField0_;
    public static final int BLOCKHEIGHT_FIELD_NUMBER = 1;
    private long blockHeight_;
    /**
     * <code>required int64 blockHeight = 1;</code>
     */
    public boolean hasBlockHeight() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int64 blockHeight = 1;</code>
     */
    public long getBlockHeight() {
      return blockHeight_;
    }

    public static final int BLOCKSIZE_FIELD_NUMBER = 2;
    private long blockSize_;
    /**
     * <code>required int64 blockSize = 2;</code>
     */
    public boolean hasBlockSize() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int64 blockSize = 2;</code>
     */
    public long getBlockSize() {
      return blockSize_;
    }

    public static final int TOTALAMOUNT_FIELD_NUMBER = 3;
    private long totalAmount_;
    /**
     * <code>required int64 totalAmount = 3;</code>
     */
    public boolean hasTotalAmount() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required int64 totalAmount = 3;</code>
     */
    public long getTotalAmount() {
      return totalAmount_;
    }

    public static final int TOTALFEE_FIELD_NUMBER = 4;
    private long totalFee_;
    /**
     * <code>required int64 totalFee = 4;</code>
     */
    public boolean hasTotalFee() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required int64 totalFee = 4;</code>
     */
    public long getTotalFee() {
      return totalFee_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasBlockHeight()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasBlockSize()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasTotalAmount()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasTotalFee()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt64(1, blockHeight_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt64(2, blockSize_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt64(3, totalAmount_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt64(4, totalFee_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, blockHeight_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, blockSize_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, totalAmount_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(4, totalFee_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof BlockMessage.Block)) {
        return super.equals(obj);
      }
      BlockMessage.Block other = (BlockMessage.Block) obj;

      boolean result = true;
      result = result && (hasBlockHeight() == other.hasBlockHeight());
      if (hasBlockHeight()) {
        result = result && (getBlockHeight()
            == other.getBlockHeight());
      }
      result = result && (hasBlockSize() == other.hasBlockSize());
      if (hasBlockSize()) {
        result = result && (getBlockSize()
            == other.getBlockSize());
      }
      result = result && (hasTotalAmount() == other.hasTotalAmount());
      if (hasTotalAmount()) {
        result = result && (getTotalAmount()
            == other.getTotalAmount());
      }
      result = result && (hasTotalFee() == other.hasTotalFee());
      if (hasTotalFee()) {
        result = result && (getTotalFee()
            == other.getTotalFee());
      }
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasBlockHeight()) {
        hash = (37 * hash) + BLOCKHEIGHT_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getBlockHeight());
      }
      if (hasBlockSize()) {
        hash = (37 * hash) + BLOCKSIZE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getBlockSize());
      }
      if (hasTotalAmount()) {
        hash = (37 * hash) + TOTALAMOUNT_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getTotalAmount());
      }
      if (hasTotalFee()) {
        hash = (37 * hash) + TOTALFEE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getTotalFee());
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static BlockMessage.Block parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BlockMessage.Block parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BlockMessage.Block parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BlockMessage.Block parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BlockMessage.Block parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BlockMessage.Block parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BlockMessage.Block parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static BlockMessage.Block parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static BlockMessage.Block parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static BlockMessage.Block parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static BlockMessage.Block parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static BlockMessage.Block parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(BlockMessage.Block prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Block}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Block)
        BlockMessage.BlockOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return BlockMessage.internal_static_Block_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return BlockMessage.internal_static_Block_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                BlockMessage.Block.class, BlockMessage.Block.Builder.class);
      }

      // Construct using com.passport.proto.BlockMessage.Block.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        blockHeight_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000001);
        blockSize_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        totalAmount_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000004);
        totalFee_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return BlockMessage.internal_static_Block_descriptor;
      }

      public BlockMessage.Block getDefaultInstanceForType() {
        return BlockMessage.Block.getDefaultInstance();
      }

      public BlockMessage.Block build() {
        BlockMessage.Block result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public BlockMessage.Block buildPartial() {
        BlockMessage.Block result = new BlockMessage.Block(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.blockHeight_ = blockHeight_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.blockSize_ = blockSize_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.totalAmount_ = totalAmount_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.totalFee_ = totalFee_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof BlockMessage.Block) {
          return mergeFrom((BlockMessage.Block)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(BlockMessage.Block other) {
        if (other == BlockMessage.Block.getDefaultInstance()) return this;
        if (other.hasBlockHeight()) {
          setBlockHeight(other.getBlockHeight());
        }
        if (other.hasBlockSize()) {
          setBlockSize(other.getBlockSize());
        }
        if (other.hasTotalAmount()) {
          setTotalAmount(other.getTotalAmount());
        }
        if (other.hasTotalFee()) {
          setTotalFee(other.getTotalFee());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasBlockHeight()) {
          return false;
        }
        if (!hasBlockSize()) {
          return false;
        }
        if (!hasTotalAmount()) {
          return false;
        }
        if (!hasTotalFee()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        BlockMessage.Block parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (BlockMessage.Block) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private long blockHeight_ ;
      /**
       * <code>required int64 blockHeight = 1;</code>
       */
      public boolean hasBlockHeight() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int64 blockHeight = 1;</code>
       */
      public long getBlockHeight() {
        return blockHeight_;
      }
      /**
       * <code>required int64 blockHeight = 1;</code>
       */
      public Builder setBlockHeight(long value) {
        bitField0_ |= 0x00000001;
        blockHeight_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 blockHeight = 1;</code>
       */
      public Builder clearBlockHeight() {
        bitField0_ = (bitField0_ & ~0x00000001);
        blockHeight_ = 0L;
        onChanged();
        return this;
      }

      private long blockSize_ ;
      /**
       * <code>required int64 blockSize = 2;</code>
       */
      public boolean hasBlockSize() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int64 blockSize = 2;</code>
       */
      public long getBlockSize() {
        return blockSize_;
      }
      /**
       * <code>required int64 blockSize = 2;</code>
       */
      public Builder setBlockSize(long value) {
        bitField0_ |= 0x00000002;
        blockSize_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 blockSize = 2;</code>
       */
      public Builder clearBlockSize() {
        bitField0_ = (bitField0_ & ~0x00000002);
        blockSize_ = 0L;
        onChanged();
        return this;
      }

      private long totalAmount_ ;
      /**
       * <code>required int64 totalAmount = 3;</code>
       */
      public boolean hasTotalAmount() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required int64 totalAmount = 3;</code>
       */
      public long getTotalAmount() {
        return totalAmount_;
      }
      /**
       * <code>required int64 totalAmount = 3;</code>
       */
      public Builder setTotalAmount(long value) {
        bitField0_ |= 0x00000004;
        totalAmount_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 totalAmount = 3;</code>
       */
      public Builder clearTotalAmount() {
        bitField0_ = (bitField0_ & ~0x00000004);
        totalAmount_ = 0L;
        onChanged();
        return this;
      }

      private long totalFee_ ;
      /**
       * <code>required int64 totalFee = 4;</code>
       */
      public boolean hasTotalFee() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>required int64 totalFee = 4;</code>
       */
      public long getTotalFee() {
        return totalFee_;
      }
      /**
       * <code>required int64 totalFee = 4;</code>
       */
      public Builder setTotalFee(long value) {
        bitField0_ |= 0x00000008;
        totalFee_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 totalFee = 4;</code>
       */
      public Builder clearTotalFee() {
        bitField0_ = (bitField0_ & ~0x00000008);
        totalFee_ = 0L;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Block)
    }

    // @@protoc_insertion_point(class_scope:Block)
    private static final BlockMessage.Block DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new BlockMessage.Block();
    }

    public static BlockMessage.Block getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<Block>
        PARSER = new com.google.protobuf.AbstractParser<Block>() {
      public Block parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Block(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Block> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Block> getParserForType() {
      return PARSER;
    }

    public BlockMessage.Block getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Block_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Block_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\022BlockMessage.proto\"V\n\005Block\022\023\n\013blockHe" +
      "ight\030\001 \002(\003\022\021\n\tblockSize\030\002 \002(\003\022\023\n\013totalAm" +
      "ount\030\003 \002(\003\022\020\n\010totalFee\030\004 \002(\003B\"\n\022com.pass" +
      "port.protoB\014BlockMessage"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_Block_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Block_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Block_descriptor,
        new String[] { "BlockHeight", "BlockSize", "TotalAmount", "TotalFee", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
