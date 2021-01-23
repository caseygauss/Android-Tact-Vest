package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the HRate type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "HRates", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class HRate implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField CURRENT_RATE = field("currentRate");
  public static final QueryField TIME_RECORDED = field("timeRecorded");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Int") Integer currentRate;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime timeRecorded;
  public String getId() {
      return id;
  }
  
  public Integer getCurrentRate() {
      return currentRate;
  }
  
  public Temporal.DateTime getTimeRecorded() {
      return timeRecorded;
  }
  
  private HRate(String id, Integer currentRate, Temporal.DateTime timeRecorded) {
    this.id = id;
    this.currentRate = currentRate;
    this.timeRecorded = timeRecorded;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      HRate hRate = (HRate) obj;
      return ObjectsCompat.equals(getId(), hRate.getId()) &&
              ObjectsCompat.equals(getCurrentRate(), hRate.getCurrentRate()) &&
              ObjectsCompat.equals(getTimeRecorded(), hRate.getTimeRecorded());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getCurrentRate())
      .append(getTimeRecorded())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("HRate {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("currentRate=" + String.valueOf(getCurrentRate()) + ", ")
      .append("timeRecorded=" + String.valueOf(getTimeRecorded()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static HRate justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new HRate(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      currentRate,
      timeRecorded);
  }
  public interface BuildStep {
    HRate build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep currentRate(Integer currentRate);
    BuildStep timeRecorded(Temporal.DateTime timeRecorded);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private Integer currentRate;
    private Temporal.DateTime timeRecorded;
    @Override
     public HRate build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new HRate(
          id,
          currentRate,
          timeRecorded);
    }
    
    @Override
     public BuildStep currentRate(Integer currentRate) {
        this.currentRate = currentRate;
        return this;
    }
    
    @Override
     public BuildStep timeRecorded(Temporal.DateTime timeRecorded) {
        this.timeRecorded = timeRecorded;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, Integer currentRate, Temporal.DateTime timeRecorded) {
      super.id(id);
      super.currentRate(currentRate)
        .timeRecorded(timeRecorded);
    }
    
    @Override
     public CopyOfBuilder currentRate(Integer currentRate) {
      return (CopyOfBuilder) super.currentRate(currentRate);
    }
    
    @Override
     public CopyOfBuilder timeRecorded(Temporal.DateTime timeRecorded) {
      return (CopyOfBuilder) super.timeRecorded(timeRecorded);
    }
  }
  
}
