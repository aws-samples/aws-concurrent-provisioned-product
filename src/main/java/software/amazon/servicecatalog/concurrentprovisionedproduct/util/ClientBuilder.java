package software.amazon.servicecatalog.concurrentprovisionedproduct.util;

import com.amazonaws.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.RetryPolicyContext;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.OrRetryCondition;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.cloudformation.LambdaWrapper;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.servicecatalog.concurrentprovisionedproduct.ResourceModel;

import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.RequestTranslator.createAssumeRoleRequest;

public class ClientBuilder {

    private ClientBuilder() {
    }

    public static ServiceCatalogClient getClient() {
        return LazyHolder.SERVICE_CLIENT;
    }

    public static StsClient getStsClient() {
        return LazyHolder.STS_CLIENT;
    }

    public static DynamoDbClient getDdbClient() {
        return LazyHolder.DDB_CLIENT;
    }

    /**
     * Get ServiceCatalogClient for requests to interact with SC client
     *
     * @return {@link ServiceCatalogClient}
     */
    private static class LazyHolder {

        private static final Integer MAX_RETRIES = 10;

        public static ServiceCatalogClient SERVICE_CLIENT = ServiceCatalogClient.builder()
                    .httpClient(LambdaWrapper.HTTP_CLIENT)
                    .overrideConfiguration(ClientOverrideConfiguration.builder()
                            .retryPolicy(RetryPolicy.builder()
                                    .backoffStrategy(BackoffStrategy.defaultThrottlingStrategy())
                                    .throttlingBackoffStrategy(BackoffStrategy.defaultThrottlingStrategy())
                                    .numRetries(MAX_RETRIES)
                                    .retryCondition(OrRetryCondition.create(new RetryCondition[]{
                                            RetryCondition.defaultRetryCondition(),
                                            ServiceCatalogClientRetryCondition.create()
                                    }))
                                    .build())
                            .build())
                    .build();

        public static StsClient STS_CLIENT = StsClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .retryPolicy(RetryPolicy.builder()
                                .backoffStrategy(BackoffStrategy.defaultThrottlingStrategy())
                                .throttlingBackoffStrategy(BackoffStrategy.defaultThrottlingStrategy())
                                .numRetries(MAX_RETRIES)
                                .retryCondition(OrRetryCondition.create(new RetryCondition[]{
                                        RetryCondition.defaultRetryCondition(),
                                        ServiceCatalogClientRetryCondition.create()
                                }))
                                .build())
                        .build())
                .build();

        public static DynamoDbClient DDB_CLIENT = DynamoDbClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .retryPolicy(RetryPolicy.builder()
                                .backoffStrategy(BackoffStrategy.defaultThrottlingStrategy())
                                .throttlingBackoffStrategy(BackoffStrategy.defaultThrottlingStrategy())
                                .numRetries(MAX_RETRIES)
                                .retryCondition(OrRetryCondition.create(new RetryCondition[]{
                                        RetryCondition.defaultRetryCondition(),
                                        ServiceCatalogClientRetryCondition.create()
                                }))
                                .build())
                        .build())
                .build();
    }

    /**
     * ServiceCatalogClient Throttling Exception StatusCode is 400 while default throttling code is 429
     * https://github.com/aws/aws-sdk-java-v2/blob/master/core/sdk-core/src/main/java/software/amazon/awssdk/core/exception/SdkServiceException.java#L91
     * which means we would need to customize a RetryCondition
     */
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class ServiceCatalogClientRetryCondition implements RetryCondition {

        public static ServiceCatalogClientRetryCondition create() {
            return new ServiceCatalogClientRetryCondition();
        }

        @Override
        public boolean shouldRetry(RetryPolicyContext context) {
            final String errorMessage = context.exception().getMessage();
            if (StringUtils.isNullOrEmpty(errorMessage)) return false;
            return errorMessage.contains("Rate exceeded");
        }
    }
}
