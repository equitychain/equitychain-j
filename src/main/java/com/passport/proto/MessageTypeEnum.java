// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MessageType.proto

package com.passport.proto;

public final class MessageTypeEnum {

  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;

  static {
    String[] descriptorData = {
        "\n\021MessageType.proto*Q\n\013MessageType\022\014\n\010DA" +
            "TA_REQ\020\000\022\r\n\tDATA_RESP\020\001\022\021\n\rHEARTBEAT_REQ" +
            "\020\002\022\022\n\016HEARTBEAT_RESP\020\003B%\n\022com.passport.p" +
            "rotoB\017MessageTypeEnumb\006proto3"
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

  private MessageTypeEnum() {
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

  /**
   * Protobuf enum {@code MessageType}
   */
  public enum MessageType
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>DATA_REQ = 0;</code>
     */
    DATA_REQ(0),
    /**
     * <code>DATA_RESP = 1;</code>
     */
    DATA_RESP(1),
    /**
     * <code>HEARTBEAT_REQ = 2;</code>
     */
    HEARTBEAT_REQ(2),
    /**
     * <code>HEARTBEAT_RESP = 3;</code>
     */
    HEARTBEAT_RESP(3),
    UNRECOGNIZED(-1),;

    /**
     * <code>DATA_REQ = 0;</code>
     */
    public static final int DATA_REQ_VALUE = 0;
    /**
     * <code>DATA_RESP = 1;</code>
     */
    public static final int DATA_RESP_VALUE = 1;
    /**
     * <code>HEARTBEAT_REQ = 2;</code>
     */
    public static final int HEARTBEAT_REQ_VALUE = 2;
    /**
     * <code>HEARTBEAT_RESP = 3;</code>
     */
    public static final int HEARTBEAT_RESP_VALUE = 3;
    private static final com.google.protobuf.Internal.EnumLiteMap<
        MessageType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<MessageType>() {
          public MessageType findValueByNumber(int number) {
            return MessageType.forNumber(number);
          }
        };
    private static final MessageType[] VALUES = values();
    private final int value;

    private MessageType(int value) {
      this.value = value;
    }

    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @Deprecated
    public static MessageType valueOf(int value) {
      return forNumber(value);
    }

    public static MessageType forNumber(int value) {
      switch (value) {
        case 0:
          return DATA_REQ;
        case 1:
          return DATA_RESP;
        case 2:
          return HEARTBEAT_REQ;
        case 3:
          return HEARTBEAT_RESP;
        default:
          return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<MessageType>
    internalGetValueMap() {
      return internalValueMap;
    }

    public static final com.google.protobuf.Descriptors.EnumDescriptor
    getDescriptor() {
      return MessageTypeEnum.getDescriptor().getEnumTypes().get(0);
    }

    public static MessageType valueOf(
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

    // @@protoc_insertion_point(enum_scope:MessageType)
  }

  // @@protoc_insertion_point(outer_class_scope)
}
