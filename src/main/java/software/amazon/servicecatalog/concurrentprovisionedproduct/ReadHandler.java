package software.amazon.servicecatalog.concurrentprovisionedproduct;

import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.DescribeProvisionedProductResponse;
import software.amazon.awssdk.services.servicecatalog.model.ProvisionedProductDetail;
import software.amazon.awssdk.services.servicecatalog.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.servicecatalog.concurrentprovisionedproduct.util.ClientBuilder;

import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.*;

public class ReadHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<ServiceCatalogClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        return ProgressEvent.progress(model, callbackContext)
                .then(progress -> getState(proxy, proxy.newProxy(ClientBuilder::getDdbClient), progress, model, logger))
                .then(progress -> {
                    AmazonWebServicesClientProxy _proxy = model.getRoleArn() != null ? retrieveCrossAccountProxy(
                            proxy,
                            (LoggerProxy) logger,
                            model
                    ) : proxy;
                    return _proxy
                            .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::DescribeProvisionedProduct", _proxy.newProxy(ClientBuilder::getClient), model, callbackContext)
                            .translateToServiceRequest(modelRequest -> createDescribeProvisionedProductRequest(model))
                            .backoffDelay(MULTIPLE_OF)
                            .makeServiceCall((modelRequest, proxyInvocation) -> {
                                final DescribeProvisionedProductResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::describeProvisionedProduct);
                                logger.log(String.format("%s [%s] DescribeProvisionedProduct initiated", ResourceModel.TYPE_NAME, model.getProvisionedProductId()));
                                return response;
                            })
                            .stabilize((_request, response, proxyInvocation, resourceModel, context) -> {
                                ProvisionedProductDetail provisionedProductDetail = response.provisionedProductDetail();
                                resourceModel.setProvisionedProductId(resourceModel.getProvisionedProductId());
                                resourceModel.setProvisionedProductName(provisionedProductDetail.name());
                                resourceModel.setRecordId(provisionedProductDetail.lastRecordId());
                                resourceModel.setProvisioningArtifactId(provisionedProductDetail.provisioningArtifactId());
                                resourceModel.setProductId(provisionedProductDetail.productId());
                                return isOperationStabilized(proxyInvocation, resourceModel, provisionedProductDetail.lastRecordId(), logger);
                            })
                            .handleError((_request, e, _proxyClient, _model, context) -> {
                                if (e instanceof ResourceNotFoundException) {
                                    return ProgressEvent.failed(null, context, HandlerErrorCode.NotFound, e.getMessage());
                                }
                                throw e;
                            })
                            .success();
                        }
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(progress.getResourceModel()));
    }
}
