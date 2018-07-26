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
     * <code>BLOCK_SYNC = 1;</code>
     */
    BLOCK_SYNC(1),
    /**
     * <code>ACCOUNTLIST_SYNC = 2;</code>
     */
    ACCOUNTLIST_SYNC(2),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>HEART_BEAT = 0;</code>
     */
    public static final int HEART_BEAT_VALUE = 0;
    /**
     * <code>BLOCK_SYNC = 1;</code>
     */
    public static final int BLOCK_SYNC_VALUE = 1;
    /**
     * <code>ACCOUNTLIST_SYNC = 2;</code>
     */
    public static final int ACCOUNTLIST_SYNC_VALUE = 2;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static DataType valueOf(int value) {
      return forNumber(value);
    }

    public static DataType forNumber(int value) {
      switch (value) {
        case 0: return HEART_BEAT;
        case 1: return BLOCK_SYNC;
        case 2: return ACCOUNTLIST_SYNC;
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
      return DataTypeEnum.getDescriptor().getEnumTypes().get(0);
    }

    private static final DataType[] VALUES = values();

    public static DataType valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new IllegalArgumentException(
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
    String[] descriptorData = {
      "\n\016DataType.proto*@\n\010DataType\022\016\n\nHEART_BE" +
      "AT\020\000\022\016\n\nBLOCK_SYNC\020\001\022\024\n\020ACCOUNTLIST_SYNC" +
      "\020\002B\"\n\022com.passport.protoB\014DataTypeEnumb\006" +
      "proto3"
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
