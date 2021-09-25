package software.amazon.servicecatalog.concurrentprovisionedproduct;

import software.amazon.awssdk.services.cloudformation.model.CreateStackInstancesResponse;
import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.InvalidParametersException;
import software.amazon.awssdk.services.servicecatalog.model.ProvisionProductResponse;
import software.amazon.awssdk.services.servicecatalog.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.servicecatalog.concurrentprovisionedproduct.util.ClientBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.PropertyTranslator.translateFromSdkOutputs;
import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.createProvisionProductRequest;

public class CreateHandler extends BaseHandlerStd {

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<ServiceCatalogClient> proxyClient,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        if (model.getId() == null) {
            model.setId(UUID.randomUUID().toString());
        }
        AmazonWebServicesClientProxy _proxy = model.getRoleArn() != null ? retrieveCrossAccountProxy(
                proxy,
                (LoggerProxy) logger,
                model
        ) : proxy;
        return ProgressEvent.progress(model, callbackContext)
                .then(progress -> acquireLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> _proxy
                        .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::ProvisionProduct", _proxy.newProxy(ClientBuilder::getClient), model, callbackContext)
                        .translateToServiceRequest(modelRequest -> createProvisionProductRequest(model))
                        .backoffDelay(MULTIPLE_OF)
                        .makeServiceCall((modelRequest, proxyInvocation) -> {
                            final ProvisionProductResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::provisionProduct);
                            logger.log(String.format("%s [%s] (%s) ProvisionProduct initiated", ResourceModel.TYPE_NAME, response.recordDetail().provisionedProductName(), response.recordDetail().provisionedProductId()));
                            return response;
                        })
                        .stabilize((_request, response, proxyInvocation, resourceModel, context) -> {
                            resourceModel.setProvisionedProductId(response.recordDetail().provisionedProductId());
                            resourceModel.setRecordId(response.recordDetail().recordId());
                            return isOperationStabilized(proxyInvocation, resourceModel, response.recordDetail().recordId(), logger);
                        })
                        .handleError((_request, e, _proxyClient, _model, context) -> {
                            releaseLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger);
                            throw e;
                        })
                        .progress())
                .then(progress -> setState(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> releaseLock(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> ProgressEvent.defaultSuccessHandler(progress.getResourceModel()));

    }
}
