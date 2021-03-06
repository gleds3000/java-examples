package com.mageddo.jvmti;

import com.mageddo.jvmti.classdelegate.InstanceId;
import com.mageddo.jvmti.classdelegate.scanning.InstanceFilter;

import java.util.List;

public interface ClassInstanceService {

  InstanceValue getFieldValue(InstanceId id, FieldId fieldId);

  void setFieldValue(InstanceId id, FieldId fieldId, InstanceValue value);

  InstanceValue methodInvoke(InstanceId id, String name, List<InstanceValue> args);

  int filter(InstanceFilter filter);

  int scanInstances(ClassId classId);

  List<InstanceValue> scanAndGetValues(ClassId classId);
}
