package com.lucas.opa.service;

import com.lucas.opa.model.OpaRequestBodyModel;
import java.util.ArrayList;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;


@GrpcService
public class EmployeeService extends EmployeeServiceGrpc.EmployeeServiceImplBase {

	private final OpaWebClientService opaWebClientService;
	private final List<String> opaPath;
	private final String opaMethod;

	public EmployeeService(OpaWebClientService opaWebClientService,
			@Value("${opa.path}") List<String> opaPath,
			@Value("${opa.method}") String opaMethod) {
		this.opaWebClientService = opaWebClientService;
		this.opaPath = opaPath;
		this.opaMethod = opaMethod;
	}

	@Override
	public void checkEmployeeAccess(EmployeeRequest request,
			StreamObserver<EmployeeResponse> responseObserver) {
		List<String> fullPath = new ArrayList<>(opaPath);
		fullPath.add(request.getEmployee());

		OpaRequestBodyModel requestBody = new OpaRequestBodyModel(
				request.getCurrentUser(),
				fullPath, opaMethod);

		opaWebClientService.postForOpaResponse(requestBody)
				.map(response -> EmployeeResponse
						.newBuilder()
						.setIsAuthorized(response.getResult().isAllow()).build())
				.subscribe(
						response -> {
							responseObserver.onNext(response);
							responseObserver.onCompleted();
						},
						responseObserver::onError
				);
	}
}
