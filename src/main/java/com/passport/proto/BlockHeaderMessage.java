// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: BlockHeaderMessage.proto

package com.passport.proto;

public final class BlockHeaderMessage {
<<<<<<< HEAD
  private BlockHeaderMessage() {}
=======

  private static final com.google.protobuf.Descriptors.Descriptor
      internal_static_BlockHeader_descriptor;
  private static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_BlockHeader_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;

  static {
    String[] descriptorData = {
        "\n\030BlockHeaderMessage.proto\"]\n\013BlockHeade" +
            "r\022\021\n\ttimeStamp\030\001 \001(\003\022\025\n\rhashPrevBlock\030\002 " +
            "\001(\014\022\026\n\016hashMerkleRoot\030\003 \001(\014\022\014\n\004hash\030\004 \001("
            +
            "\014B(\n\022com.passport.protoB\022BlockHeaderMess" +
            "ageb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
        .internalBuildGeneratedFileFrom(descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[]{
            }, assigner);
    internal_static_BlockHeader_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_BlockHeader_fieldAccessorTable = new
        com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_BlockHeader_descriptor,
        new String[]{"TimeStamp", "HashPrevBlock", "HashMerkleRoot", "Hash",});
  }

  private BlockHeaderMessage() {
  }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
<<<<<<< HEAD
=======

  public static com.google.protobuf.Descriptors.FileDescriptor
  getDescriptor() {
    return descriptor;
  }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  public interface BlockHeaderOrBuilder extends
      // @@protoc_insertion_point(interface_extends:BlockHeader)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int64 timeStamp = 1;</code>
     */
    long getTimeStamp();

    /**
     * <code>bytes hashPrevBlock = 2;</code>
     */
    com.google.protobuf.ByteString getHashPrevBlock();

    /**
     * <code>bytes hashMerkleRoot = 3;</code>
     */
    com.google.protobuf.ByteString getHashMerkleRoot();

    /**
     * <code>bytes hash = 4;</code>
     */
    com.google.protobuf.ByteString getHash();
  }
<<<<<<< HEAD
  /**
   * Protobuf type {@code BlockHeader}
   */
  public  static final class BlockHeader extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:BlockHeader)
      BlockHeaderOrBuilder {
=======

  /**
   * Protobuf type {@code BlockHeader}
   */
  public static final class BlockHeader extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:BlockHeader)
      BlockHeaderOrBuilder {

    public static final int TIMESTAMP_FIELD_NUMBER = 1;
    public static final int HASHPREVBLOCK_FIELD_NUMBER = 2;
    public static final int HASHMERKLEROOT_FIELD_NUMBER = 3;
    public static final int HASH_FIELD_NUMBER = 4;
    private static final long serialVersionUID = 0L;
    // @@protoc_insertion_point(class_scope:BlockHeader)
    private static final BlockHeaderMessage.BlockHeader DEFAULT_INSTANCE;
    private static final com.google.protobuf.Parser<BlockHeader>
        PARSER = new com.google.protobuf.AbstractParser<BlockHeader>() {
      public BlockHeader parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new BlockHeader(input, extensionRegistry);
      }
    };

    static {
      DEFAULT_INSTANCE = new BlockHeaderMessage.BlockHeader();
    }

    private long timeStamp_;
    private com.google.protobuf.ByteString hashPrevBlock_;
    private com.google.protobuf.ByteString hashMerkleRoot_;
    private com.google.protobuf.ByteString hash_;
    private byte memoizedIsInitialized = -1;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    // Use BlockHeader.newBuilder() to construct.
    private BlockHeader(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    private BlockHeader() {
      timeStamp_ = 0L;
      hashPrevBlock_ = com.google.protobuf.ByteString.EMPTY;
      hashMerkleRoot_ = com.google.protobuf.ByteString.EMPTY;
      hash_ = com.google.protobuf.ByteString.EMPTY;
    }

<<<<<<< HEAD
    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    private BlockHeader(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 8: {

              timeStamp_ = input.readInt64();
              break;
            }
            case 18: {

              hashPrevBlock_ = input.readBytes();
              break;
            }
            case 26: {

              hashMerkleRoot_ = input.readBytes();
              break;
            }
            case 34: {

              hash_ = input.readBytes();
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
        makeExtensionsImmutable();
      }
    }
<<<<<<< HEAD
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return BlockHeaderMessage.internal_static_BlockHeader_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
=======

    public static final com.google.protobuf.Descriptors.Descriptor
    getDescriptor() {
      return BlockHeaderMessage.internal_static_BlockHeader_descriptor;
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static BlockHeaderMessage.BlockHeader parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static BlockHeaderMessage.BlockHeader parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }

    public static BlockHeaderMessage.BlockHeader parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(BlockHeaderMessage.BlockHeader prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    public static BlockHeaderMessage.BlockHeader getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    public static com.google.protobuf.Parser<BlockHeader> parser() {
      return PARSER;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }

    protected FieldAccessorTable
    internalGetFieldAccessorTable() {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      return BlockHeaderMessage.internal_static_BlockHeader_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              BlockHeaderMessage.BlockHeader.class, BlockHeaderMessage.BlockHeader.Builder.class);
    }

<<<<<<< HEAD
    public static final int TIMESTAMP_FIELD_NUMBER = 1;
    private long timeStamp_;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    /**
     * <code>int64 timeStamp = 1;</code>
     */
    public long getTimeStamp() {
      return timeStamp_;
    }

<<<<<<< HEAD
    public static final int HASHPREVBLOCK_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString hashPrevBlock_;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    /**
     * <code>bytes hashPrevBlock = 2;</code>
     */
    public com.google.protobuf.ByteString getHashPrevBlock() {
      return hashPrevBlock_;
    }

<<<<<<< HEAD
    public static final int HASHMERKLEROOT_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString hashMerkleRoot_;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    /**
     * <code>bytes hashMerkleRoot = 3;</code>
     */
    public com.google.protobuf.ByteString getHashMerkleRoot() {
      return hashMerkleRoot_;
    }

<<<<<<< HEAD
    public static final int HASH_FIELD_NUMBER = 4;
    private com.google.protobuf.ByteString hash_;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    /**
     * <code>bytes hash = 4;</code>
     */
    public com.google.protobuf.ByteString getHash() {
      return hash_;
    }

<<<<<<< HEAD
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;
=======
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) {
        return true;
      }
      if (isInitialized == 0) {
        return false;
      }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
<<<<<<< HEAD
                        throws java.io.IOException {
=======
        throws java.io.IOException {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      if (timeStamp_ != 0L) {
        output.writeInt64(1, timeStamp_);
      }
      if (!hashPrevBlock_.isEmpty()) {
        output.writeBytes(2, hashPrevBlock_);
      }
      if (!hashMerkleRoot_.isEmpty()) {
        output.writeBytes(3, hashMerkleRoot_);
      }
      if (!hash_.isEmpty()) {
        output.writeBytes(4, hash_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
<<<<<<< HEAD
      if (size != -1) return size;
=======
      if (size != -1) {
        return size;
      }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

      size = 0;
      if (timeStamp_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
<<<<<<< HEAD
          .computeInt64Size(1, timeStamp_);
      }
      if (!hashPrevBlock_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, hashPrevBlock_);
      }
      if (!hashMerkleRoot_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, hashMerkleRoot_);
      }
      if (!hash_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, hash_);
=======
            .computeInt64Size(1, timeStamp_);
      }
      if (!hashPrevBlock_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
            .computeBytesSize(2, hashPrevBlock_);
      }
      if (!hashMerkleRoot_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
            .computeBytesSize(3, hashMerkleRoot_);
      }
      if (!hash_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
            .computeBytesSize(4, hash_);
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      }
      memoizedSize = size;
      return size;
    }

<<<<<<< HEAD
    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
=======
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      }
      if (!(obj instanceof BlockHeaderMessage.BlockHeader)) {
        return super.equals(obj);
      }
      BlockHeaderMessage.BlockHeader other = (BlockHeaderMessage.BlockHeader) obj;

      boolean result = true;
      result = result && (getTimeStamp()
          == other.getTimeStamp());
      result = result && getHashPrevBlock()
          .equals(other.getHashPrevBlock());
      result = result && getHashMerkleRoot()
          .equals(other.getHashMerkleRoot());
      result = result && getHash()
          .equals(other.getHash());
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getTimeStamp());
      hash = (37 * hash) + HASHPREVBLOCK_FIELD_NUMBER;
      hash = (53 * hash) + getHashPrevBlock().hashCode();
      hash = (37 * hash) + HASHMERKLEROOT_FIELD_NUMBER;
      hash = (53 * hash) + getHashMerkleRoot().hashCode();
      hash = (37 * hash) + HASH_FIELD_NUMBER;
      hash = (53 * hash) + getHash().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

<<<<<<< HEAD
    public static BlockHeaderMessage.BlockHeader parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static BlockHeaderMessage.BlockHeader parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static BlockHeaderMessage.BlockHeader parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static BlockHeaderMessage.BlockHeader parseFrom(
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
    public static Builder newBuilder(BlockHeaderMessage.BlockHeader prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
=======
    public Builder newBuilderForType() {
      return newBuilder();
    }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
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
<<<<<<< HEAD
=======

    @Override
    public com.google.protobuf.Parser<BlockHeader> getParserForType() {
      return PARSER;
    }

    public BlockHeaderMessage.BlockHeader getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    /**
     * Protobuf type {@code BlockHeader}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:BlockHeader)
        BlockHeaderMessage.BlockHeaderOrBuilder {
<<<<<<< HEAD
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return BlockHeaderMessage.internal_static_BlockHeader_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return BlockHeaderMessage.internal_static_BlockHeader_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                BlockHeaderMessage.BlockHeader.class, BlockHeaderMessage.BlockHeader.Builder.class);
      }
=======

      private long timeStamp_;
      private com.google.protobuf.ByteString hashPrevBlock_ = com.google.protobuf.ByteString.EMPTY;
      private com.google.protobuf.ByteString hashMerkleRoot_ = com.google.protobuf.ByteString.EMPTY;
      private com.google.protobuf.ByteString hash_ = com.google.protobuf.ByteString.EMPTY;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

      // Construct using com.passport.proto.BlockHeaderMessage.BlockHeader.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
<<<<<<< HEAD
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
=======

      public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
        return BlockHeaderMessage.internal_static_BlockHeader_descriptor;
      }

      protected FieldAccessorTable
      internalGetFieldAccessorTable() {
        return BlockHeaderMessage.internal_static_BlockHeader_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                BlockHeaderMessage.BlockHeader.class, BlockHeaderMessage.BlockHeader.Builder.class);
      }

      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
            .alwaysUseFieldBuilders) {
        }
      }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      public Builder clear() {
        super.clear();
        timeStamp_ = 0L;

        hashPrevBlock_ = com.google.protobuf.ByteString.EMPTY;

        hashMerkleRoot_ = com.google.protobuf.ByteString.EMPTY;

        hash_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
<<<<<<< HEAD
          getDescriptorForType() {
=======
      getDescriptorForType() {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
        return BlockHeaderMessage.internal_static_BlockHeader_descriptor;
      }

      public BlockHeaderMessage.BlockHeader getDefaultInstanceForType() {
        return BlockHeaderMessage.BlockHeader.getDefaultInstance();
      }

      public BlockHeaderMessage.BlockHeader build() {
        BlockHeaderMessage.BlockHeader result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public BlockHeaderMessage.BlockHeader buildPartial() {
        BlockHeaderMessage.BlockHeader result = new BlockHeaderMessage.BlockHeader(this);
        result.timeStamp_ = timeStamp_;
        result.hashPrevBlock_ = hashPrevBlock_;
        result.hashMerkleRoot_ = hashMerkleRoot_;
        result.hash_ = hash_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
<<<<<<< HEAD
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof BlockHeaderMessage.BlockHeader) {
          return mergeFrom((BlockHeaderMessage.BlockHeader)other);
=======

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof BlockHeaderMessage.BlockHeader) {
          return mergeFrom((BlockHeaderMessage.BlockHeader) other);
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(BlockHeaderMessage.BlockHeader other) {
<<<<<<< HEAD
        if (other == BlockHeaderMessage.BlockHeader.getDefaultInstance()) return this;
=======
        if (other == BlockHeaderMessage.BlockHeader.getDefaultInstance()) {
          return this;
        }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
        if (other.getTimeStamp() != 0L) {
          setTimeStamp(other.getTimeStamp());
        }
        if (other.getHashPrevBlock() != com.google.protobuf.ByteString.EMPTY) {
          setHashPrevBlock(other.getHashPrevBlock());
        }
        if (other.getHashMerkleRoot() != com.google.protobuf.ByteString.EMPTY) {
          setHashMerkleRoot(other.getHashMerkleRoot());
        }
        if (other.getHash() != com.google.protobuf.ByteString.EMPTY) {
          setHash(other.getHash());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        BlockHeaderMessage.BlockHeader parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (BlockHeaderMessage.BlockHeader) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

<<<<<<< HEAD
      private long timeStamp_ ;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>int64 timeStamp = 1;</code>
       */
      public long getTimeStamp() {
        return timeStamp_;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>int64 timeStamp = 1;</code>
       */
      public Builder setTimeStamp(long value) {

        timeStamp_ = value;
        onChanged();
        return this;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>int64 timeStamp = 1;</code>
       */
      public Builder clearTimeStamp() {

        timeStamp_ = 0L;
        onChanged();
        return this;
      }

<<<<<<< HEAD
      private com.google.protobuf.ByteString hashPrevBlock_ = com.google.protobuf.ByteString.EMPTY;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hashPrevBlock = 2;</code>
       */
      public com.google.protobuf.ByteString getHashPrevBlock() {
        return hashPrevBlock_;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hashPrevBlock = 2;</code>
       */
      public Builder setHashPrevBlock(com.google.protobuf.ByteString value) {
        if (value == null) {
<<<<<<< HEAD
    throw new NullPointerException();
  }
=======
          throw new NullPointerException();
        }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

        hashPrevBlock_ = value;
        onChanged();
        return this;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hashPrevBlock = 2;</code>
       */
      public Builder clearHashPrevBlock() {

        hashPrevBlock_ = getDefaultInstance().getHashPrevBlock();
        onChanged();
        return this;
      }

<<<<<<< HEAD
      private com.google.protobuf.ByteString hashMerkleRoot_ = com.google.protobuf.ByteString.EMPTY;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hashMerkleRoot = 3;</code>
       */
      public com.google.protobuf.ByteString getHashMerkleRoot() {
        return hashMerkleRoot_;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hashMerkleRoot = 3;</code>
       */
      public Builder setHashMerkleRoot(com.google.protobuf.ByteString value) {
        if (value == null) {
<<<<<<< HEAD
    throw new NullPointerException();
  }
=======
          throw new NullPointerException();
        }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

        hashMerkleRoot_ = value;
        onChanged();
        return this;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hashMerkleRoot = 3;</code>
       */
      public Builder clearHashMerkleRoot() {

        hashMerkleRoot_ = getDefaultInstance().getHashMerkleRoot();
        onChanged();
        return this;
      }

<<<<<<< HEAD
      private com.google.protobuf.ByteString hash_ = com.google.protobuf.ByteString.EMPTY;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hash = 4;</code>
       */
      public com.google.protobuf.ByteString getHash() {
        return hash_;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hash = 4;</code>
       */
      public Builder setHash(com.google.protobuf.ByteString value) {
        if (value == null) {
<<<<<<< HEAD
    throw new NullPointerException();
  }
=======
          throw new NullPointerException();
        }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

        hash_ = value;
        onChanged();
        return this;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      /**
       * <code>bytes hash = 4;</code>
       */
      public Builder clearHash() {

        hash_ = getDefaultInstance().getHash();
        onChanged();
        return this;
      }
<<<<<<< HEAD
=======

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

<<<<<<< HEAD

      // @@protoc_insertion_point(builder_scope:BlockHeader)
    }

    // @@protoc_insertion_point(class_scope:BlockHeader)
    private static final BlockHeaderMessage.BlockHeader DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new BlockHeaderMessage.BlockHeader();
    }

    public static BlockHeaderMessage.BlockHeader getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<BlockHeader>
        PARSER = new com.google.protobuf.AbstractParser<BlockHeader>() {
      public BlockHeader parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new BlockHeader(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<BlockHeader> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<BlockHeader> getParserForType() {
      return PARSER;
    }

    public BlockHeaderMessage.BlockHeader getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_BlockHeader_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_BlockHeader_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\030BlockHeaderMessage.proto\"]\n\013BlockHeade" +
      "r\022\021\n\ttimeStamp\030\001 \001(\003\022\025\n\rhashPrevBlock\030\002 " +
      "\001(\014\022\026\n\016hashMerkleRoot\030\003 \001(\014\022\014\n\004hash\030\004 \001(" +
      "\014B(\n\022com.passport.protoB\022BlockHeaderMess" +
      "ageb\006proto3"
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
    internal_static_BlockHeader_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_BlockHeader_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_BlockHeader_descriptor,
        new String[] { "TimeStamp", "HashPrevBlock", "HashMerkleRoot", "Hash", });
=======
      // @@protoc_insertion_point(builder_scope:BlockHeader)
    }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  }

  // @@protoc_insertion_point(outer_class_scope)
}
