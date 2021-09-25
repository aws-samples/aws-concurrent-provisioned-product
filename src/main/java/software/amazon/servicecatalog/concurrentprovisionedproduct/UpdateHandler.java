package software.amazon.servicecatalog.concurrentprovisionedproduct;

import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.servicecatalog.concurrentprovisionedproduct.util.ClientBuilder;

import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.terminateProvisionProductRequest;
import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.updateProvisionProductRequest;

public class UpdateHandler extends BaseHandlerStd {

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
                    .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::UpdateProvisionedProduct" + model.getProvisionedProductName().hashCode(), _proxy.newProxy(ClientBuilder::getClient), model, callbackContext)
                    .translateToServiceRequest(modelRequest -> updateProvisionProductRequest(model))
                    .backoffDelay(MULTIPLE_OF)
                    .makeServiceCall((modelRequest, proxyInvocation) -> {
                        final UpdateProvisionedProductResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::updateProvisionedProduct);
                        logger.log(String.format("%s [%s] (%s) UpdateProvisionedProduct initiated", ResourceModel.TYPE_NAME, response.recordDetail().provisionedProductName(), response.recordDetail().provisionedProductId()));
                        return response;
                    })
                    .stabilize((_request, response, proxyInvocation, resourceModel, context) -> {
                        resourceModel.setProvisionedProductId(response.recordDetail().provisionedProductId());
                        resourceModel.setRecordId(response.recordDetail().recordId());
                        return isOperationStabilized(proxyInvocation, resourceModel, response.recordDetail().recordId(), logger);
                    })
                    .handleError((_request, e, _proxyClient, _model, context) -> {
                        releaseLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger);
                        if (e instanceof ResourceNotFoundException) {
                            return ProgressEvent.failed(null, context, HandlerErrorCode.NotFound, e.getMessage());
                        }
                        if (e instanceof InvalidParametersException && e.getMessage().contains("doesn't exist")) {
                            return ProgressEvent.failed(null, context, HandlerErrorCode.NotFound, e.getMessage());
                        }
                        throw e;
                    })
                    .progress()
                )
                .then(progress -> releaseLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> ProgressEvent.defaultSuccessHandler(progress.getResourceModel()));
    }
}
