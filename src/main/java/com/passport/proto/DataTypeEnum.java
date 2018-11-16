// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: DataType.proto

package com.passport.proto;

public final class DataTypeEnum {
  private DataTypeEnum() {}
  public static void registerAllExtensions(
          com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
          com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
            (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf enum {@code DataType}
   */
  public enum DataType
          implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>HEART_BEAT = 0;</code>
     */
    HEART_BEAT(0),
    /**
     * <code>NEXT_BLOCK_SYNC = 1;</code>
     */
    NEXT_BLOCK_SYNC(1),
    /**
     * <code>ACCOUNTLIST_SYNC = 2;</code>
     */
    ACCOUNTLIST_SYNC(2),
    /**
     * <code>ACCOUNT_SYNC = 3;</code>
     */
    ACCOUNT_SYNC(3),
    /**
     * <code>SEND_TRANSACTION = 4;</code>
     */
    SEND_TRANSACTION(4),
    /**
     * <code>BLOCK_SYNC = 5;</code>
     */
    BLOCK_SYNC(5),
    /**
     * <code>ACCOUNTIP_SYNC = 6;</code>
     */
    ACCOUNTIP_SYNC(6),
    /**
     * <code>ACCOUNT_MINER = 7;</code>
     */
    ACCOUNT_MINER(7),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>HEART_BEAT = 0;</code>
     */
    public static final int HEART_BEAT_VALUE = 0;
    /**
     * <code>NEXT_BLOCK_SYNC = 1;</code>
     */
    public static final int NEXT_BLOCK_SYNC_VALUE = 1;
    /**
     * <code>ACCOUNTLIST_SYNC = 2;</code>
     */
    public static final int ACCOUNTLIST_SYNC_VALUE = 2;
    /**
     * <code>ACCOUNT_SYNC = 3;</code>
     */
    public static final int ACCOUNT_SYNC_VALUE = 3;
    /**
     * <code>SEND_TRANSACTION = 4;</code>
     */
    public static final int SEND_TRANSACTION_VALUE = 4;
    /**
     * <code>BLOCK_SYNC = 5;</code>
     */
    public static final int BLOCK_SYNC_VALUE = 5;
    /**
     * <code>ACCOUNTIP_SYNC = 6;</code>
     */
    public static final int ACCOUNTIP_SYNC_VALUE = 6;
    /**
     * <code>ACCOUNT_MINER = 7;</code>
     */
    public static final int ACCOUNT_MINER_VALUE = 7;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
                "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static DataType valueOf(int value) {
      return forNumber(value);
    }

    public static DataType forNumber(int value) {
      switch (value) {
        case 0: return HEART_BEAT;
        case 1: return NEXT_BLOCK_SYNC;
        case 2: return ACCOUNTLIST_SYNC;
        case 3: return ACCOUNT_SYNC;
        case 4: return SEND_TRANSACTION;
        case 5: return BLOCK_SYNC;
        case 6: return ACCOUNTIP_SYNC;
        case 7: return ACCOUNT_MINER;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<DataType>
    internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
            DataType> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<DataType>() {
              public DataType findValueByNumber(int number) {
                return DataType.forNumber(number);
              }
            };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
    getValueDescriptor() {
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
    getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
    getDescriptor() {
      return com.passport.proto.DataTypeEnum.getDescriptor().getEnumTypes().get(0);
    }

    private static final DataType[] VALUES = values();

    public static DataType valueOf(
            com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
                "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private DataType(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:DataType)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
  getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
          descriptor;
  static {
    java.lang.String[] descriptorData = {
            "\n\016DataType.proto*\244\001\n\010DataType\022\016\n\nHEART_B" +
                    "EAT\020\000\022\023\n\017NEXT_BLOCK_SYNC\020\001\022\024\n\020ACCOUNTLIS" +
                    "T_SYNC\020\002\022\020\n\014ACCOUNT_SYNC\020\003\022\024\n\020SEND_TRANS" +
                    "ACTION\020\004\022\016\n\nBLOCK_SYNC\020\005\022\022\n\016ACCOUNTIP_SY" +
                    "NC\020\006\022\021\n\rACCOUNT_MINER\020\007B\"\n\022com.passport." +
                    "protoB\014DataTypeEnumb\006proto3"
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
  }

  // @@protoc_insertion_point(outer_class_scope)
}
