/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package io.github.jeqo.talk.avro;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Tweet extends org.apache.avro.specific.SpecificRecordBase
    implements org.apache.avro.specific.SpecificRecord {

  private static final long serialVersionUID = 4573012774324103573L;

  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
      .parse(
          "{\"type\":\"record\",\"name\":\"Tweet\",\"namespace\":\"io.github.jeqo.talk.avro\",\"fields\":[{\"name\":\"text\",\"type\":\"string\"},{\"name\":\"username\",\"type\":\"string\"},{\"name\":\"lang\",\"type\":[\"string\",\"null\"]}]}");

  public static org.apache.avro.Schema getClassSchema() {
    return SCHEMA$;
  }

  @Deprecated
  public java.lang.CharSequence text;

  @Deprecated
  public java.lang.CharSequence username;

  @Deprecated
  public java.lang.CharSequence lang;

  /**
   * Default constructor. Note that this does not initialize fields to their default
   * values from the schema. If that is desired then one should use
   * <code>newBuilder()</code>.
   */
  public Tweet() {
  }

  /**
   * All-args constructor.
   * @param text The new value for text
   * @param username The new value for username
   * @param lang The new value for lang
   */
  public Tweet(java.lang.CharSequence text, java.lang.CharSequence username,
      java.lang.CharSequence lang) {
    this.text = text;
    this.username = username;
    this.lang = lang;
  }

  public org.apache.avro.Schema getSchema() {
    return SCHEMA$;
  }

  // Used by DatumWriter. Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
      case 0:
        return text;
      case 1:
        return username;
      case 2:
        return lang;
      default:
        throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader. Applications should not call.
  @SuppressWarnings(value = "unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
      case 0:
        text = (java.lang.CharSequence) value$;
        break;
      case 1:
        username = (java.lang.CharSequence) value$;
        break;
      case 2:
        lang = (java.lang.CharSequence) value$;
        break;
      default:
        throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'text' field.
   * @return The value of the 'text' field.
   */
  public java.lang.CharSequence getText() {
    return text;
  }

  /**
   * Sets the value of the 'text' field.
   * @param value the value to set.
   */
  public void setText(java.lang.CharSequence value) {
    this.text = value;
  }

  /**
   * Gets the value of the 'username' field.
   * @return The value of the 'username' field.
   */
  public java.lang.CharSequence getUsername() {
    return username;
  }

  /**
   * Sets the value of the 'username' field.
   * @param value the value to set.
   */
  public void setUsername(java.lang.CharSequence value) {
    this.username = value;
  }

  /**
   * Gets the value of the 'lang' field.
   * @return The value of the 'lang' field.
   */
  public java.lang.CharSequence getLang() {
    return lang;
  }

  /**
   * Sets the value of the 'lang' field.
   * @param value the value to set.
   */
  public void setLang(java.lang.CharSequence value) {
    this.lang = value;
  }

  /**
   * Creates a new Tweet RecordBuilder.
   * @return A new Tweet RecordBuilder
   */
  public static io.github.jeqo.talk.avro.Tweet.Builder newBuilder() {
    return new io.github.jeqo.talk.avro.Tweet.Builder();
  }

  /**
   * Creates a new Tweet RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Tweet RecordBuilder
   */
  public static io.github.jeqo.talk.avro.Tweet.Builder newBuilder(
      io.github.jeqo.talk.avro.Tweet.Builder other) {
    return new io.github.jeqo.talk.avro.Tweet.Builder(other);
  }

  /**
   * Creates a new Tweet RecordBuilder by copying an existing Tweet instance.
   * @param other The existing instance to copy.
   * @return A new Tweet RecordBuilder
   */
  public static io.github.jeqo.talk.avro.Tweet.Builder newBuilder(
      io.github.jeqo.talk.avro.Tweet other) {
    return new io.github.jeqo.talk.avro.Tweet.Builder(other);
  }

  /**
   * RecordBuilder for Tweet instances.
   */
  public static class Builder
      extends org.apache.avro.specific.SpecificRecordBuilderBase<Tweet>
      implements org.apache.avro.data.RecordBuilder<Tweet> {

    private java.lang.CharSequence text;

    private java.lang.CharSequence username;

    private java.lang.CharSequence lang;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(io.github.jeqo.talk.avro.Tweet.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.text)) {
        this.text = data().deepCopy(fields()[0].schema(), other.text);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.username)) {
        this.username = data().deepCopy(fields()[1].schema(), other.username);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.lang)) {
        this.lang = data().deepCopy(fields()[2].schema(), other.lang);
        fieldSetFlags()[2] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Tweet instance
     * @param other The existing instance to copy.
     */
    private Builder(io.github.jeqo.talk.avro.Tweet other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.text)) {
        this.text = data().deepCopy(fields()[0].schema(), other.text);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.username)) {
        this.username = data().deepCopy(fields()[1].schema(), other.username);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.lang)) {
        this.lang = data().deepCopy(fields()[2].schema(), other.lang);
        fieldSetFlags()[2] = true;
      }
    }

    /**
     * Gets the value of the 'text' field.
     * @return The value.
     */
    public java.lang.CharSequence getText() {
      return text;
    }

    /**
     * Sets the value of the 'text' field.
     * @param value The value of 'text'.
     * @return This builder.
     */
    public io.github.jeqo.talk.avro.Tweet.Builder setText(
        java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.text = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
     * Checks whether the 'text' field has been set.
     * @return True if the 'text' field has been set, false otherwise.
     */
    public boolean hasText() {
      return fieldSetFlags()[0];
    }

    /**
     * Clears the value of the 'text' field.
     * @return This builder.
     */
    public io.github.jeqo.talk.avro.Tweet.Builder clearText() {
      text = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
     * Gets the value of the 'username' field.
     * @return The value.
     */
    public java.lang.CharSequence getUsername() {
      return username;
    }

    /**
     * Sets the value of the 'username' field.
     * @param value The value of 'username'.
     * @return This builder.
     */
    public io.github.jeqo.talk.avro.Tweet.Builder setUsername(
        java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.username = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
     * Checks whether the 'username' field has been set.
     * @return True if the 'username' field has been set, false otherwise.
     */
    public boolean hasUsername() {
      return fieldSetFlags()[1];
    }

    /**
     * Clears the value of the 'username' field.
     * @return This builder.
     */
    public io.github.jeqo.talk.avro.Tweet.Builder clearUsername() {
      username = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
     * Gets the value of the 'lang' field.
     * @return The value.
     */
    public java.lang.CharSequence getLang() {
      return lang;
    }

    /**
     * Sets the value of the 'lang' field.
     * @param value The value of 'lang'.
     * @return This builder.
     */
    public io.github.jeqo.talk.avro.Tweet.Builder setLang(
        java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.lang = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
     * Checks whether the 'lang' field has been set.
     * @return True if the 'lang' field has been set, false otherwise.
     */
    public boolean hasLang() {
      return fieldSetFlags()[2];
    }

    /**
     * Clears the value of the 'lang' field.
     * @return This builder.
     */
    public io.github.jeqo.talk.avro.Tweet.Builder clearLang() {
      lang = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public Tweet build() {
      try {
        Tweet record = new Tweet();
        record.text = fieldSetFlags()[0] ? this.text
            : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.username = fieldSetFlags()[1] ? this.username
            : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.lang = fieldSetFlags()[2] ? this.lang
            : (java.lang.CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  private static final org.apache.avro.io.DatumWriter WRITER$ =
      new org.apache.avro.specific.SpecificDatumWriter(
          SCHEMA$);

  @Override
  public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  private static final org.apache.avro.io.DatumReader READER$ =
      new org.apache.avro.specific.SpecificDatumReader(
          SCHEMA$);

  @Override
  public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }
}
