package com.lucas.opa.service;

import com.lucas.opa.EmployeeRequest;
import com.lucas.opa.EmployeeResponse;
import com.lucas.opa.EmployeeServiceGrpc;
import com.lucas.opa.model.OpaRequest;
import com.lucas.opa.model.OpaRequestBodyModel;
import io.grpc.Status;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import io.grpc.stub.StreamObserver;


@GrpcService
@Slf4j
public class EmployeeService extends EmployeeServiceGrpc.EmployeeServiceImplBase {

	private final OpaWebClientService opaWebClientService;

	@Value("${opa.path}")
	private List<String> opaPath;

	@Value("${opa.method}")
	private String opaMethod;

	public EmployeeService(OpaWebClientService opaWebClientService) {
		this.opaWebClientService = opaWebClientService;
	}

	@Override
	public void checkEmployeeAccess(EmployeeRequest request, StreamObserver<EmployeeResponse> responseObserver) {
		List<String> fullPath = new ArrayList<>(opaPath);
		fullPath.add(request.getEmployee());

		OpaRequestBodyModel requestBody = new OpaRequestBodyModel(
				request.getCurrentUser(),
				fullPath,
				opaMethod);
		OpaRequest opaRequest = new OpaRequest(requestBody);
		log.info("Sending request to OPA: {}", opaRequest);

		opaWebClientService.postForOpaResponse(opaRequest)
				.doOnNext(response -> log.info("Received response from OPA: {}", response))
				.map(response -> {
					// 在构建响应之前进行日志记录
					log.info("Mapping OPA response to EmployeeResponse: isAllow={}", response.getResult().isAllow());
					return EmployeeResponse.newBuilder()
							.setIsAllowed(response.getResult().isAllow())
							.build();
				})
				.doOnError(e -> {
					log.error("Error in checkEmployeeAccess: {}, {}", e.getMessage(), e);
					responseObserver.onError(Status.INTERNAL
							.withDescription("Error processing the request")
							.withCause(e) // only for debugging purposes
							.asRuntimeException());
				})
				.subscribe(
						response -> {
							log.info("Response from OPA: {}", response);
							responseObserver.onNext(response);
							responseObserver.onCompleted();
						},
						error -> {
							log.error("Subscription error in checkEmployeeAccess: {}, {}", error.getMessage(), error);
							responseObserver.onError(error);
						}
				);
	}
}
