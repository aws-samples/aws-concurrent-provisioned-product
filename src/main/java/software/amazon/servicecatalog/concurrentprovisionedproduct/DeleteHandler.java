package software.amazon.servicecatalog.concurrentprovisionedproduct;

import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.ProvisionProductResponse;
import software.amazon.awssdk.services.servicecatalog.model.ResourceNotFoundException;
import software.amazon.awssdk.services.servicecatalog.model.TerminateProvisionedProductResponse;
import software.amazon.cloudformation.proxy.*;
import software.amazon.servicecatalog.concurrentprovisionedproduct.util.ClientBuilder;

import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.createProvisionProductRequest;
import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.terminateProvisionProductRequest;

public class DeleteHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<ServiceCatalogClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        AmazonWebServicesClientProxy _proxy = model.getRoleArn() != null ? retrieveCrossAccountProxy(
                proxy,
                (LoggerProxy) logger,
                model
        ) : proxy;
        return ProgressEvent.progress(model, callbackContext)
                .then(progress -> acquireLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> _proxy
                    .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::TerminateProvisionedProduct", _proxy.newProxy(ClientBuilder::getClient), model, callbackContext)
                    .translateToServiceRequest(modelRequest -> terminateProvisionProductRequest(model))
                    .backoffDelay(MULTIPLE_OF)
                    .makeServiceCall((modelRequest, proxyInvocation) -> {
                        final TerminateProvisionedProductResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::terminateProvisionedProduct);
                        logger.log(String.format("%s [%s] TerminateProvisionedProduct initiated", ResourceModel.TYPE_NAME, model.getProvisionedProductName()));
                        return response;
                    })
                    .stabilize((_request, response, proxyInvocation, resourceModel, context) -> isOperationStabilized(proxyInvocation, resourceModel, response.recordDetail().recordId(), logger))
                    .handleError((_request, e, _proxyClient, _model, context) -> {
                        releaseLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger);
                        if (e instanceof ResourceNotFoundException) {
                            return ProgressEvent.failed(null, context, HandlerErrorCode.NotFound, e.getMessage());
                        }
                        throw e;
                    })
                    .progress()
                )
                .then(progress -> deleteState(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> releaseLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .status(OperationStatus.SUCCESS)
                        .build()
                );
    }
}
