// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: DataType.proto

package com.passport.proto;

public final class DataTypeEnum {
<<<<<<< HEAD
  private DataTypeEnum() {}
  public static void registerAllExtensions(
          com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
          com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
            (com.google.protobuf.ExtensionRegistryLite) registry);
  }
=======

  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;

  static {
    String[] descriptorData = {
        "\n\016DataType.proto*}\n\010DataType\022\016\n\nHEART_BE" +
            "AT\020\000\022\023\n\017NEXT_BLOCK_SYNC\020\001\022\024\n\020ACCOUNTLIST" +
            "_SYNC\020\002\022\020\n\014ACCOUNT_SYNC\020\003\022\024\n\020SEND_TRANSA" +
            "CTION\020\004\022\016\n\nBLOCK_SYNC\020\005B\"\n\022com.passport." +
            "protoB\014DataTypeEnumb\006proto3"
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
  }

  private DataTypeEnum() {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
  getDescriptor() {
    return descriptor;
  }

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  /**
   * Protobuf enum {@code DataType}
   */
  public enum DataType
<<<<<<< HEAD
          implements com.google.protobuf.ProtocolMessageEnum {
=======
      implements com.google.protobuf.ProtocolMessageEnum {
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
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
<<<<<<< HEAD
    /**
     * <code>ACCOUNTIP_SYNC = 6;</code>
     */
    ACCOUNTIP_SYNC(6),
    /**
     * <code>ACCOUNT_MINER = 7;</code>
     */
    ACCOUNT_MINER(7),
    /**
     * <code>TRUSTEE_SYNC = 8;</code>
     */
    TRUSTEE_SYNC(8),
    UNRECOGNIZED(-1),
    ;
=======
    UNRECOGNIZED(-1),;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

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
<<<<<<< HEAD
    /**
     * <code>ACCOUNTIP_SYNC = 6;</code>
     */
    public static final int ACCOUNTIP_SYNC_VALUE = 6;
    /**
     * <code>ACCOUNT_MINER = 7;</code>
     */
    public static final int ACCOUNT_MINER_VALUE = 7;
    /**
     * <code>TRUSTEE_SYNC = 8;</code>
     */
    public static final int TRUSTEE_SYNC_VALUE = 8;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
                "Can't get the number of an unknown enum value.");
      }
      return value;
=======
    private static final com.google.protobuf.Internal.EnumLiteMap<
        DataType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<DataType>() {
          public DataType findValueByNumber(int number) {
            return DataType.forNumber(number);
          }
        };
    private static final DataType[] VALUES = values();
    private final int value;

    private DataType(int value) {
      this.value = value;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
<<<<<<< HEAD
    @java.lang.Deprecated
=======
    @Deprecated
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    public static DataType valueOf(int value) {
      return forNumber(value);
    }

    public static DataType forNumber(int value) {
      switch (value) {
<<<<<<< HEAD
        case 0: return HEART_BEAT;
        case 1: return NEXT_BLOCK_SYNC;
        case 2: return ACCOUNTLIST_SYNC;
        case 3: return ACCOUNT_SYNC;
        case 4: return SEND_TRANSACTION;
        case 5: return BLOCK_SYNC;
        case 6: return ACCOUNTIP_SYNC;
        case 7: return ACCOUNT_MINER;
        case 8: return TRUSTEE_SYNC;
        default: return null;
=======
        case 0:
          return HEART_BEAT;
        case 1:
          return NEXT_BLOCK_SYNC;
        case 2:
          return ACCOUNTLIST_SYNC;
        case 3:
          return ACCOUNT_SYNC;
        case 4:
          return SEND_TRANSACTION;
        case 5:
          return BLOCK_SYNC;
        default:
          return null;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<DataType>
    internalGetValueMap() {
      return internalValueMap;
    }
<<<<<<< HEAD
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
=======

    public static final com.google.protobuf.Descriptors.EnumDescriptor
    getDescriptor() {
      return DataTypeEnum.getDescriptor().getEnumTypes().get(0);
    }

    public static DataType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

<<<<<<< HEAD
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
            "\n\016DataType.proto*\266\001\n\010DataType\022\016\n\nHEART_B" +
                    "EAT\020\000\022\023\n\017NEXT_BLOCK_SYNC\020\001\022\024\n\020ACCOUNTLIS" +
                    "T_SYNC\020\002\022\020\n\014ACCOUNT_SYNC\020\003\022\024\n\020SEND_TRANS" +
                    "ACTION\020\004\022\016\n\nBLOCK_SYNC\020\005\022\022\n\016ACCOUNTIP_SY" +
                    "NC\020\006\022\021\n\rACCOUNT_MINER\020\007\022\020\n\014TRUSTEE_SYNC\020" +
                    "\010B\"\n\022com.passport.protoB\014DataTypeEnumb\006p" +
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
                    }, assigner);
=======
    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
    getValueDescriptor() {
      return getDescriptor().getValues().get(ordinal());
    }

    public final com.google.protobuf.Descriptors.EnumDescriptor
    getDescriptorForType() {
      return getDescriptor();
    }

    // @@protoc_insertion_point(enum_scope:DataType)
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  }

  // @@protoc_insertion_point(outer_class_scope)
}
