package com.mageddo.jvmti.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mageddo.jvmti.entrypoint.vo.InstanceFilterReq;
import com.mageddo.jvmti.classdelegate.LocalClassInstanceService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.metzweb.tinyserver.Request;
import net.metzweb.tinyserver.Response;
import net.metzweb.tinyserver.TinyServer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class ClassInstancesFilterResource implements Response {

  private final LocalClassInstanceService localClassInstanceService;
  private final ObjectMapper objectMapper;

  @Inject
  public ClassInstancesFilterResource(
    TinyServer tinyServer,
    LocalClassInstanceService localClassInstanceService,
    ObjectMapper objectMapper
  ) {
    this.localClassInstanceService = localClassInstanceService;
    this.objectMapper = objectMapper;
    tinyServer.post("/class-instances/filter", this);
  }

  @Override
  @SneakyThrows
  public void callback(Request request) {
    try {
      final int removed = this.localClassInstanceService.filter(
        this.objectMapper
          .readValue(request.getData(), InstanceFilterReq.class)
          .toInstanceFilter()
      );
      request.write("removed instances after filtering: " + removed);
    } catch (Exception e){
      log.warn("status=can't-filter, req={}", request.getData(), e);
    }
  }
}