package com.mageddo.jvmti.classdelegate;

import com.mageddo.jvmti.ClassId;
import com.mageddo.jvmti.ClassInstanceService;
import com.mageddo.jvmti.FieldId;
import com.mageddo.jvmti.InstanceValue;
import com.mageddo.jvmti.JvmtiClass;
import com.mageddo.jvmti.classdelegate.scanning.InstanceFilter;
import com.mageddo.jvmti.classdelegate.scanning.ReferenceFilterFactory;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Singleton
public class LocalClassInstanceService implements ClassInstanceService {

  private final ReferenceFilterFactory referenceFilterFactory;
  private final Map<InstanceId, WeakReference<ObjectReference>> instanceStore = new HashMap<>();

  @Inject
  public LocalClassInstanceService(ReferenceFilterFactory referenceFilterFactory) {
    this.referenceFilterFactory = referenceFilterFactory;
  }

  @Override
  public InstanceValue getFieldValue(InstanceId id, FieldId fieldId) {
    return this.getReference(id)
      .getFieldValue(fieldId.getName())
      .toInstanceValue()
      ;
  }

  @Override
  public void setFieldValue(InstanceId id, FieldId fieldId, InstanceValue value) {
    this.getReference(id).setFieldValue(fieldId.getName(), this.toArg(value));
    log.debug("status=success, id={}, fieldId={}, value={}", id, fieldId, value);
  }

  @Override
  public InstanceValue methodInvoke(InstanceId id, String name, List<InstanceValue> args) {
    final ObjectReference objectReference = this.getReference(id);
    final Object[] parsedArgs = args
      .stream()
      .map(this::toArg)
      .toArray();
    final ObjectReference reference = objectReference.invoke(name, parsedArgs);
    this.putToStore(reference);
    return reference.toInstanceValue();
  }

  public int filter(InstanceFilter filter) {
    return this.referenceFilterFactory.filter(this.instanceStore.values(), filter);
  }

  @Override
  public int scanInstances(ClassId classId) {
    return scanAndGetValues(classId).size();
  }

  @Override
  public List<InstanceValue> scanAndGetValues(ClassId classId) {
    final List<InstanceValue> references = new ArrayList<>();
    Stream
      .of(JvmtiClass.getClassInstances(classId.toClass()))
      .forEach(it -> {
        final ObjectReference reference = new ObjectReference(it);
        final WeakReference<ObjectReference> weakRef = new WeakReference<>(reference);
        this.instanceStore.put(reference.id(), weakRef);
        references.add(reference.toInstanceValue());
      });
    log.info("status=scanned, instances={}", references.size());
    return references;
  }

  ObjectReference getReference(InstanceId id) {
    if(!this.instanceStore.containsKey(id)){
      throw new IllegalArgumentException(String.format("Can't find instance for %s", id));
    }
    return Objects.requireNonNull(this.instanceStore.get(id).get(), "Instance was garbage collected: " + id);
  }

  void putToStore(ObjectReference reference) {
    this.instanceStore.put(reference.id(), new WeakReference<>(reference));
  }

  Object toArg(InstanceValue value) {
    final Object o = value.toArg();
    if(o.getClass() == InstanceId.class){
      return this.getReference((InstanceId) o).getInstance();
    }
    return o;
  }

}
