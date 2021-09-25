package software.amazon.servicecatalog.concurrentprovisionedproduct;

import com.google.common.annotations.VisibleForTesting;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.servicecatalog.model.*;
import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.cloudformation.exceptions.TerminalException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.cloudformation.proxy.delay.MultipleOf;
import software.amazon.servicecatalog.concurrentprovisionedproduct.util.ClientBuilder;

import java.time.Duration;
import java.util.*;

import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.PropertyTranslator.*;
import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.*;

/**
 * Placeholder for the functionality that could be shared across Create/Read/Update/Delete/List Handlers
 */
public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {

    protected static final MultipleOf MULTIPLE_OF = MultipleOf.multipleOf()
            .multiple(2)
            .timeout(Duration.ofHours(24L))
            .delay(Duration.ofSeconds(2L))
            .build();

    @Override
    public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        logger.log(request.getDesiredResourceState().toString());
        return handleRequest(proxy, request, callbackContext != null ?
                callbackContext : new CallbackContext(), proxy.newProxy(ClientBuilder::getClient), logger);
    }

    protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<ServiceCatalogClient> proxyClient,
            final Logger logger);

    protected static AmazonWebServicesClientProxy retrieveCrossAccountProxy(AmazonWebServicesClientProxy proxy, LoggerProxy loggerProxy, ResourceModel model) {
        ProxyClient<StsClient> proxyClient = proxy.newProxy(ClientBuilder::getStsClient);
        AssumeRoleResponse assumeRoleResponse = proxyClient.injectCredentialsAndInvokeV2(
                createAssumeRoleRequest(model),
                proxyClient.client()::assumeRole
        );

        software.amazon.awssdk.services.sts.model.Credentials credentials = assumeRoleResponse.credentials();
        Credentials cfnCredentials = new Credentials(credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken());
        return new AmazonWebServicesClientProxy(
                loggerProxy,
                cfnCredentials,
                DelayFactory.CONSTANT_DEFAULT_DELAY_FACTORY,
                WaitStrategy.scheduleForCallbackStrategy()
        );
    }
    /**
     * Retrieves the {@link RecordDetail} from {@link DescribeRecordResponse}
     *
     * @param model  {@link ResourceModel}}
     * @param recordId Record ID
     * @return {@link RecordDetail}
     */
    private static RecordDetail describeRecord(
            final ProxyClient<ServiceCatalogClient> proxyClient,
            final ResourceModel model,
            final String recordId) {

        final DescribeRecordResponse response = proxyClient.injectCredentialsAndInvokeV2(
                createDescribeRecordRequest(model, recordId),
                proxyClient.client()::describeRecord);
        return response.recordDetail();
    }

    /**
     * Retrieves the {@link List<RecordOutput>} from {@link GetProvisionedProductOutputsResponse}
     *
     * @param model  {@link ResourceModel}}
     * @return {@link List<RecordOutput>}
     */
    protected static List<RecordOutput> getProvisionedProductOutputs(
            final ProxyClient<ServiceCatalogClient> proxyClient,
            final ResourceModel model) {

        final GetProvisionedProductOutputsResponse response = proxyClient.injectCredentialsAndInvokeV2(
                createGetProvisionedProductOutputsRequest(model),
                proxyClient.client()::getProvisionedProductOutputs);
        return response.outputs();
    }

    /**
     * Retrieves the {@link ProvisionedProductDetail} from {@link DescribeRecordResponse}
     *
     * @param model  {@link ResourceModel}}
     * @return {@link ProvisionedProductDetail}
     */
    protected static ProvisionedProductDetail describeProvisionedProduct(
            final ProxyClient<ServiceCatalogClient> proxyClient,
            final ResourceModel model) {

        final DescribeProvisionedProductResponse response = proxyClient.injectCredentialsAndInvokeV2(
                createDescribeProvisionedProductRequest(model),
                proxyClient.client()::describeProvisionedProduct);
        return response.provisionedProductDetail();
    }

    /**
     * Compares {@link RecordStatus} with specific statuses
     *
     * @param recordDetail      {@link RecordDetail}
     * @return boolean
     */
    @VisibleForTesting
    protected static boolean isRecordSucceeded(
            final RecordDetail recordDetail, final Logger logger) {

        switch (recordDetail.status()) {
            case SUCCEEDED:
                logger.log(String.format("Record [%s] has been successfully stabilized.", recordDetail.recordId()));
                return true;
            case IN_PROGRESS:
            case CREATED:
                return false;
            default:
                logger.log(String.format("Record [%s] unexpected status [%s]", recordDetail.recordId(), recordDetail.status()));
                throw new TerminalException(
                        String.format("Record [%s] was unexpectedly stopped or failed, reason: [%s]", recordDetail.recordId(), recordDetail.recordErrors().get(0)));
        }
    }

    /**
     * Checks if the operation is stabilized using OperationId to interact with
     * {@link DescribeRecordResponse}
     *
     * @param model       {@link ResourceModel}
     * @param recordId RecordId from operation response
     * @param logger      Logger
     * @return A boolean value indicates if operation is complete
     */
    protected boolean isOperationStabilized(final ProxyClient<ServiceCatalogClient> proxyClient,
                                            final ResourceModel model,
                                            final String recordId,
                                            final Logger logger) {

        final RecordDetail recordDetail = describeRecord(proxyClient, model, recordId);
        final boolean isSucceeded = isRecordSucceeded(recordDetail, logger);

        if (isSucceeded && recordDetail.recordType().compareTo("TERMINATE_PROVISIONED_PRODUCT") != 0) {
            Map<String, String> outputs = translateFromSdkOutputs(getProvisionedProductOutputs(proxyClient, model));

            model.setOutputs(outputs);
            List<RecordTag> tags = recordDetail.recordTags() == null ? new LinkedList<>() : recordDetail.recordTags();
            Optional<RecordTag> cfnOutputKeyTag = tags.stream().filter(tag -> tag.key().compareTo("proserve:CfnOutputKey") == 0).findFirst();
            String outputValue = cfnOutputKeyTag.isPresent() ? Objects.requireNonNull(outputs).get(cfnOutputKeyTag.get().value()) : recordId;
            model.setOutputValue(outputValue);
        }

        return isSucceeded;
    }

    protected boolean filterException(AwsRequest request, Exception e, ProxyClient<DynamoDbClient> client, ResourceModel model, CallbackContext context) {
        return e instanceof ConditionalCheckFailedException;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> isNoProvisionedProductUnderChange(final AmazonWebServicesClientProxy proxy,
                                                      final ProxyClient<ServiceCatalogClient> proxyClient,
                                                      final ProgressEvent<ResourceModel, CallbackContext> progress,
                                                      final ResourceModel model,
                                                      final Logger logger) {
        final CallbackContext callbackContext = progress.getCallbackContext();

        return proxy
                .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::SearchProvisionedProducts", proxyClient, model, callbackContext)
                .translateToServiceRequest(modelRequest -> createSearchProvisionedProductsRequest(model))
                .backoffDelay(MULTIPLE_OF)
                .makeServiceCall((modelRequest, proxyInvocation) -> {
                    final Iterable<SearchProvisionedProductsResponse> productIterator = proxyInvocation.injectCredentialsAndInvokeIterableV2(modelRequest, proxyInvocation.client()::searchProvisionedProductsPaginator);
                    logger.log(String.format("[%s] SearchProvisionProducts initiated", ResourceModel.TYPE_NAME));
                    return productIterator;
                })
                .stabilize((_request, response, proxyInvocation, resourceModel, context) -> {
                    for (Iterator<SearchProvisionedProductsResponse> i = response.iterator(); i.hasNext();) {
                        for (ProvisionedProductAttribute attr : i.next().provisionedProducts()) {
                            if (attr.status() == ProvisionedProductStatus.UNDER_CHANGE) return false;
                        }
                    }
                    return true;
                })
                .progress();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> acquireLock(final AmazonWebServicesClientProxy proxy,
                                                                          final ProxyClient<DynamoDbClient> proxyClient,
                                                                          final ProgressEvent<ResourceModel, CallbackContext> progress,
                                                                          final ResourceModel model,
                                                                          final Logger logger) {
        final CallbackContext callbackContext = progress.getCallbackContext();
        return proxy
                .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::AcquireLock", proxyClient, model, callbackContext)
                .translateToServiceRequest(modelRequest -> createAcquireLockItemRequest(model))
                .backoffDelay(MULTIPLE_OF)
                .makeServiceCall((modelRequest, proxyInvocation) -> {
                    final PutItemResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::putItem);
                    logger.log(String.format("[%s] AcquireLock Done", ResourceModel.TYPE_NAME));
                    return response;
                })
                .retryErrorFilter(this::filterException)
                .progress();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> releaseLock(final AmazonWebServicesClientProxy proxy,
                                                                        final ProxyClient<DynamoDbClient> proxyClient,
                                                                        final ProgressEvent<ResourceModel, CallbackContext> progress,
                                                                        final ResourceModel model,
                                                                        final Logger logger) {
        final CallbackContext callbackContext = progress.getCallbackContext();

        return proxy
                .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::ReleaseLock", proxyClient, model, callbackContext)
                .translateToServiceRequest(modelRequest -> createReleaseLockItemRequest(model))
                .backoffDelay(MULTIPLE_OF)
                .makeServiceCall((modelRequest, proxyInvocation) -> {
                    final DeleteItemResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::deleteItem);
                    logger.log(String.format("[%s] AcquireLock Done", ResourceModel.TYPE_NAME));
                    return response;
                })
                .retryErrorFilter(this::filterException)
                .progress();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> setState(final AmazonWebServicesClientProxy proxy,
                                                                        final ProxyClient<DynamoDbClient> proxyClient,
                                                                        final ProgressEvent<ResourceModel, CallbackContext> progress,
                                                                        final ResourceModel model,
                                                                        final Logger logger) {
        final CallbackContext callbackContext = progress.getCallbackContext();
        return proxy
                .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::SetState", proxyClient, model, callbackContext)
                .translateToServiceRequest(modelRequest -> createPutStateItemRequest(model))
                .backoffDelay(MULTIPLE_OF)
                .makeServiceCall((modelRequest, proxyInvocation) -> {
                    final PutItemResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::putItem);
                    logger.log(String.format("[%s] PutState Done", ResourceModel.TYPE_NAME));
                    return response;
                })
                .progress();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> getState(final AmazonWebServicesClientProxy proxy,
                                                                     final ProxyClient<DynamoDbClient> proxyClient,
                                                                     final ProgressEvent<ResourceModel, CallbackContext> progress,
                                                                     final ResourceModel model,
                                                                     final Logger logger) {
        final CallbackContext callbackContext = progress.getCallbackContext();
        return proxy
                .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::GetState", proxyClient, model, callbackContext)
                .translateToServiceRequest(modelRequest -> createGetStateItemRequest(model))
                .backoffDelay(MULTIPLE_OF)
                .makeServiceCall((modelRequest, proxyInvocation) -> {
                    final GetItemResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::getItem);
                    logger.log(String.format("[%s] GetState Done", ResourceModel.TYPE_NAME));
                    if (response.item().isEmpty()) {
                        throw software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException
                                .builder()
                                .message("Resource not found")
                                .build();
                    }
                    return response;
                })
                .stabilize((_request, response, proxyInvocation, resourceModel, context) -> {
                    String provisionedProductId  = response.item().get("pp-id").s();
                    String roleArn  = response.item().get("role-arn").s();
                    resourceModel.setProvisionedProductId(provisionedProductId);
                    if (roleArn.compareTo("null") != 0) {
                        resourceModel.setRoleArn(roleArn);
                    }
                    return true;
                })
                .handleError((_request, e, _proxyClient, _model, context) -> {
                    if (e instanceof software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException) {
                        return ProgressEvent.failed(null, context, HandlerErrorCode.NotFound, e.getMessage());
                    }
                    throw e;
                })
                .progress();
    }

    protected ProgressEvent<ResourceModel, CallbackContext> deleteState(final AmazonWebServicesClientProxy proxy,
                                                                     final ProxyClient<DynamoDbClient> proxyClient,
                                                                     final ProgressEvent<ResourceModel, CallbackContext> progress,
                                                                     final ResourceModel model,
                                                                     final Logger logger) {
        final CallbackContext callbackContext = progress.getCallbackContext();
        return proxy
                .initiate("AWS-ServiceCatalog-ConcurrentProvisionedProduct::DeleteState", proxyClient, model, callbackContext)
                .translateToServiceRequest(modelRequest -> createDeleteStateItemRequest(model))
                .backoffDelay(MULTIPLE_OF)
                .makeServiceCall((modelRequest, proxyInvocation) -> {
                    final DeleteItemResponse response = proxyInvocation.injectCredentialsAndInvokeV2(modelRequest, proxyInvocation.client()::deleteItem);
                    logger.log(String.format("[%s] DeleteState Done", ResourceModel.TYPE_NAME));
                    return response;
                })
                .progress();
    }
}
