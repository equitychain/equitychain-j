// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NettyData.proto

package com.passport.proto;

public final class NettyData {
  private NettyData() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface DataOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Data)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>.DataType dataType = 1;</code>
     */
    int getDataTypeValue();
    /**
     * <code>.DataType dataType = 1;</code>
     */
    DataTypeEnum.DataType getDataType();

    /**
     * <code>.Block block = 2;</code>
     */
    boolean hasBlock();
    /**
     * <code>.Block block = 2;</code>
     */
    BlockMessage.Block getBlock();
    /**
     * <code>.Block block = 2;</code>
     */
    BlockMessage.BlockOrBuilder getBlockOrBuilder();

    /**
     * <code>bool heartBeatState = 3;</code>
     */
    boolean getHeartBeatState();
  }
  /**
   * Protobuf type {@code Data}
   */
  public  static final class Data extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Data)
      DataOrBuilder {
    // Use Data.newBuilder() to construct.
    private Data(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Data() {
      dataType_ = 0;
      heartBeatState_ = false;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private Data(
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
              int rawValue = input.readEnum();

              dataType_ = rawValue;
              break;
            }
            case 18: {
              BlockMessage.Block.Builder subBuilder = null;
              if (block_ != null) {
                subBuilder = block_.toBuilder();
              }
              block_ = input.readMessage(BlockMessage.Block.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(block_);
                block_ = subBuilder.buildPartial();
              }

              break;
            }
            case 24: {

              heartBeatState_ = input.readBool();
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
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return NettyData.internal_static_Data_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return NettyData.internal_static_Data_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              NettyData.Data.class, NettyData.Data.Builder.class);
    }

    public static final int DATATYPE_FIELD_NUMBER = 1;
    private int dataType_;
    /**
     * <code>.DataType dataType = 1;</code>
     */
    public int getDataTypeValue() {
      return dataType_;
    }
    /**
     * <code>.DataType dataType = 1;</code>
     */
    public DataTypeEnum.DataType getDataType() {
      DataTypeEnum.DataType result = DataTypeEnum.DataType.valueOf(dataType_);
      return result == null ? DataTypeEnum.DataType.UNRECOGNIZED : result;
    }

    public static final int BLOCK_FIELD_NUMBER = 2;
    private BlockMessage.Block block_;
    /**
     * <code>.Block block = 2;</code>
     */
    public boolean hasBlock() {
      return block_ != null;
    }
    /**
     * <code>.Block block = 2;</code>
     */
    public BlockMessage.Block getBlock() {
      return block_ == null ? BlockMessage.Block.getDefaultInstance() : block_;
    }
    /**
     * <code>.Block block = 2;</code>
     */
    public BlockMessage.BlockOrBuilder getBlockOrBuilder() {
      return getBlock();
    }

    public static final int HEARTBEATSTATE_FIELD_NUMBER = 3;
    private boolean heartBeatState_;
    /**
     * <code>bool heartBeatState = 3;</code>
     */
    public boolean getHeartBeatState() {
      return heartBeatState_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (hasBlock()) {
        if (!getBlock().isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (dataType_ != DataTypeEnum.DataType.HEART_BEAT.getNumber()) {
        output.writeEnum(1, dataType_);
      }
      if (block_ != null) {
        output.writeMessage(2, getBlock());
      }
      if (heartBeatState_ != false) {
        output.writeBool(3, heartBeatState_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (dataType_ != DataTypeEnum.DataType.HEART_BEAT.getNumber()) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, dataType_);
      }
      if (block_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getBlock());
      }
      if (heartBeatState_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(3, heartBeatState_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof NettyData.Data)) {
        return super.equals(obj);
      }
      NettyData.Data other = (NettyData.Data) obj;

      boolean result = true;
      result = result && dataType_ == other.dataType_;
      result = result && (hasBlock() == other.hasBlock());
      if (hasBlock()) {
        result = result && getBlock()
            .equals(other.getBlock());
      }
      result = result && (getHeartBeatState()
          == other.getHeartBeatState());
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + DATATYPE_FIELD_NUMBER;
      hash = (53 * hash) + dataType_;
      if (hasBlock()) {
        hash = (37 * hash) + BLOCK_FIELD_NUMBER;
        hash = (53 * hash) + getBlock().hashCode();
      }
      hash = (37 * hash) + HEARTBEATSTATE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getHeartBeatState());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static NettyData.Data parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static NettyData.Data parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static NettyData.Data parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static NettyData.Data parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static NettyData.Data parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static NettyData.Data parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static NettyData.Data parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static NettyData.Data parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static NettyData.Data parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static NettyData.Data parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static NettyData.Data parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static NettyData.Data parseFrom(
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
    public static Builder newBuilder(NettyData.Data prototype) {
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
     * Protobuf type {@code Data}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Data)
        NettyData.DataOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return NettyData.internal_static_Data_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return NettyData.internal_static_Data_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                NettyData.Data.class, NettyData.Data.Builder.class);
      }

      // Construct using com.passport.proto.NettyData.Data.newBuilder()
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
        dataType_ = 0;

        if (blockBuilder_ == null) {
          block_ = null;
        } else {
          block_ = null;
          blockBuilder_ = null;
        }
        heartBeatState_ = false;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return NettyData.internal_static_Data_descriptor;
      }

      public NettyData.Data getDefaultInstanceForType() {
        return NettyData.Data.getDefaultInstance();
      }

      public NettyData.Data build() {
        NettyData.Data result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public NettyData.Data buildPartial() {
        NettyData.Data result = new NettyData.Data(this);
        result.dataType_ = dataType_;
        if (blockBuilder_ == null) {
          result.block_ = block_;
        } else {
          result.block_ = blockBuilder_.build();
        }
        result.heartBeatState_ = heartBeatState_;
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
        if (other instanceof NettyData.Data) {
          return mergeFrom((NettyData.Data)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(NettyData.Data other) {
        if (other == NettyData.Data.getDefaultInstance()) return this;
        if (other.dataType_ != 0) {
          setDataTypeValue(other.getDataTypeValue());
        }
        if (other.hasBlock()) {
          mergeBlock(other.getBlock());
        }
        if (other.getHeartBeatState() != false) {
          setHeartBeatState(other.getHeartBeatState());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (hasBlock()) {
          if (!getBlock().isInitialized()) {
            return false;
          }
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        NettyData.Data parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (NettyData.Data) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int dataType_ = 0;
      /**
       * <code>.DataType dataType = 1;</code>
       */
      public int getDataTypeValue() {
        return dataType_;
      }
      /**
       * <code>.DataType dataType = 1;</code>
       */
      public Builder setDataTypeValue(int value) {
        dataType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>.DataType dataType = 1;</code>
       */
      public DataTypeEnum.DataType getDataType() {
        DataTypeEnum.DataType result = DataTypeEnum.DataType.valueOf(dataType_);
        return result == null ? DataTypeEnum.DataType.UNRECOGNIZED : result;
      }
      /**
       * <code>.DataType dataType = 1;</code>
       */
      public Builder setDataType(DataTypeEnum.DataType value) {
        if (value == null) {
          throw new NullPointerException();
        }

        dataType_ = value.getNumber();
        onChanged();
        return this;
      }
      /**
       * <code>.DataType dataType = 1;</code>
       */
      public Builder clearDataType() {

        dataType_ = 0;
        onChanged();
        return this;
      }

      private BlockMessage.Block block_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          BlockMessage.Block, BlockMessage.Block.Builder, BlockMessage.BlockOrBuilder> blockBuilder_;
      /**
       * <code>.Block block = 2;</code>
       */
      public boolean hasBlock() {
        return blockBuilder_ != null || block_ != null;
      }
      /**
       * <code>.Block block = 2;</code>
       */
      public BlockMessage.Block getBlock() {
        if (blockBuilder_ == null) {
          return block_ == null ? BlockMessage.Block.getDefaultInstance() : block_;
        } else {
          return blockBuilder_.getMessage();
        }
      }
      /**
       * <code>.Block block = 2;</code>
       */
      public Builder setBlock(BlockMessage.Block value) {
        if (blockBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          block_ = value;
          onChanged();
        } else {
          blockBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.Block block = 2;</code>
       */
      public Builder setBlock(
          BlockMessage.Block.Builder builderForValue) {
        if (blockBuilder_ == null) {
          block_ = builderForValue.build();
          onChanged();
        } else {
          blockBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.Block block = 2;</code>
       */
      public Builder mergeBlock(BlockMessage.Block value) {
        if (blockBuilder_ == null) {
          if (block_ != null) {
            block_ =
              BlockMessage.Block.newBuilder(block_).mergeFrom(value).buildPartial();
          } else {
            block_ = value;
          }
          onChanged();
        } else {
          blockBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.Block block = 2;</code>
       */
      public Builder clearBlock() {
        if (blockBuilder_ == null) {
          block_ = null;
          onChanged();
        } else {
          block_ = null;
          blockBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.Block block = 2;</code>
       */
      public BlockMessage.Block.Builder getBlockBuilder() {

        onChanged();
        return getBlockFieldBuilder().getBuilder();
      }
      /**
       * <code>.Block block = 2;</code>
       */
      public BlockMessage.BlockOrBuilder getBlockOrBuilder() {
        if (blockBuilder_ != null) {
          return blockBuilder_.getMessageOrBuilder();
        } else {
          return block_ == null ?
              BlockMessage.Block.getDefaultInstance() : block_;
        }
      }
      /**
       * <code>.Block block = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          BlockMessage.Block, BlockMessage.Block.Builder, BlockMessage.BlockOrBuilder>
          getBlockFieldBuilder() {
        if (blockBuilder_ == null) {
          blockBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              BlockMessage.Block, BlockMessage.Block.Builder, BlockMessage.BlockOrBuilder>(
                  getBlock(),
                  getParentForChildren(),
                  isClean());
          block_ = null;
        }
        return blockBuilder_;
      }

      private boolean heartBeatState_ ;
      /**
       * <code>bool heartBeatState = 3;</code>
       */
      public boolean getHeartBeatState() {
        return heartBeatState_;
      }
      /**
       * <code>bool heartBeatState = 3;</code>
       */
      public Builder setHeartBeatState(boolean value) {

        heartBeatState_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool heartBeatState = 3;</code>
       */
      public Builder clearHeartBeatState() {

        heartBeatState_ = false;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:Data)
    }

    // @@protoc_insertion_point(class_scope:Data)
    private static final NettyData.Data DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new NettyData.Data();
    }

    public static NettyData.Data getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Data>
        PARSER = new com.google.protobuf.AbstractParser<Data>() {
      public Data parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Data(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Data> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Data> getParserForType() {
      return PARSER;
    }

    public NettyData.Data getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Data_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Data_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\017NettyData.proto\032\016DataType.proto\032\022Block" +
      "Message.proto\032\031google/protobuf/any.proto" +
      "\"R\n\004Data\022\033\n\010dataType\030\001 \001(\0162\t.DataType\022\025\n" +
      "\005block\030\002 \001(\0132\006.Block\022\026\n\016heartBeatState\030\003" +
      " \001(\010B\037\n\022com.passport.protoB\tNettyDatab\006p" +
      "roto3"
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
          DataTypeEnum.getDescriptor(),
          BlockMessage.getDescriptor(),
          com.google.protobuf.AnyProto.getDescriptor(),
        }, assigner);
    internal_static_Data_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Data_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Data_descriptor,
        new String[] { "DataType", "Block", "HeartBeatState", });
    DataTypeEnum.getDescriptor();
    BlockMessage.getDescriptor();
    com.google.protobuf.AnyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
