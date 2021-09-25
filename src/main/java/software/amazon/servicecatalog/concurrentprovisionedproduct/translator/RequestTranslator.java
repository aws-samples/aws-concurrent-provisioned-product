package software.amazon.servicecatalog.concurrentprovisionedproduct.translator;

import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.servicecatalog.model.*;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.servicecatalog.concurrentprovisionedproduct.ResourceModel;
import software.amazon.servicecatalog.concurrentprovisionedproduct.Tag;

import java.util.*;

import static software.amazon.servicecatalog.concurrentprovisionedproduct.translator.PropertyTranslator.*;

public class RequestTranslator {

    private static final String LOCKS_DDB_TABLE_NAME = "concurrent-sc-product-locks";
    private static final String STATE_DDB_TABLE_NAME = "concurrent-sc-product-state";

    public static ProvisionProductRequest createProvisionProductRequest(final ResourceModel model) {
        List<Tag> tags = model.getTags() == null ? new LinkedList<>() : model.getTags();
        if (model.getOutputKey() != null) {
            tags.add(Tag.builder().key("proserve:CfnOutputKey").value(model.getOutputKey()).build());
        } else {
            tags = null;
        }
        return ProvisionProductRequest.builder()
                .provisionedProductName(model.getProvisionedProductName())
                .productId(model.getProductId())
                .productName(model.getProductName())
                .provisioningArtifactId(model.getProvisioningArtifactId())
                .provisioningArtifactName(model.getProvisioningArtifactName())
                .pathId(model.getPathId())
                .pathName(model.getPathName())
                .notificationArns(model.getNotificationArns())
                .acceptLanguage(model.getAcceptLanguage())
                .tags(translateToSdkTags(tags))
                .provisioningParameters(translateToSdkParameters(model.getProvisioningParameters()))
                .provisioningPreferences(translateToSdkProvisioningPreferences(model.getProvisioningPreferences()))
                .build();

    }

    public static UpdateProvisionedProductRequest updateProvisionProductRequest(final ResourceModel model) {
        List<Tag> tags = model.getTags() == null ? new LinkedList<>() : model.getTags();
        if (model.getOutputKey() != null) {
            tags.add(Tag.builder().key("proserve:CfnOutputKey").value(model.getOutputKey()).build());
        } else {
            tags = null;
        }
        return UpdateProvisionedProductRequest.builder()
                .provisionedProductName(model.getProvisionedProductName())
                .productId(model.getProductId())
                .productName(model.getProductName())
                .provisioningArtifactId(model.getProvisioningArtifactId())
                .provisioningArtifactName(model.getProvisioningArtifactName())
                .pathId(model.getPathId())
                .pathName(model.getPathName())
                .acceptLanguage(model.getAcceptLanguage())
                .tags(translateToSdkTags(tags))
                .provisioningParameters(translateToSdkUpdateParameters(model.getProvisioningParameters()))
                .provisioningPreferences(translateToSdkUpdateProvisioningPreferences(model.getProvisioningPreferences()))
                .build();

    }

    public static TerminateProvisionedProductRequest terminateProvisionProductRequest(final ResourceModel model) {
        return TerminateProvisionedProductRequest.builder()
                .provisionedProductName(model.getProvisionedProductName())
                .acceptLanguage(model.getAcceptLanguage())
                .build();

    }

    public static DescribeRecordRequest createDescribeRecordRequest(final ResourceModel model, final String recordId) {
        return DescribeRecordRequest.builder()
                .id(recordId)
                .acceptLanguage(model.getAcceptLanguage())
                .build();
    }

    public static DescribeProvisionedProductRequest createDescribeProvisionedProductRequest(final ResourceModel model) {
        return DescribeProvisionedProductRequest.builder()
                .id(model.getProvisionedProductId())
                .build();
    }

    public static GetProvisionedProductOutputsRequest createGetProvisionedProductOutputsRequest(final ResourceModel model) {
        return GetProvisionedProductOutputsRequest.builder()
                .provisionedProductId(model.getProvisionedProductId())
                .acceptLanguage(model.getAcceptLanguage())
                .build();
    }

    public static SearchProvisionedProductsRequest createSearchProvisionedProductsRequest(final ResourceModel model) {
        Map<ProvisionedProductViewFilterBy, List<String>> filter = new HashMap<>();
        List<String> filterValues = new LinkedList<>();
        if (model.getProductName() != null) {
            filterValues.add("productName:" + model.getProductName());
        } else {
            filterValues.add("productId:" + model.getProductId());
        }
        filter.put(ProvisionedProductViewFilterBy.SEARCH_QUERY, filterValues);
        return SearchProvisionedProductsRequest.builder()
                .acceptLanguage(model.getAcceptLanguage())
                .filters(filter)
                .accessLevelFilter(
                        AccessLevelFilter.builder()
                                .key(AccessLevelFilterKey.ACCOUNT)
                                .value("self")
                                .build()
                )
                .build();
    }

    public static AssumeRoleRequest createAssumeRoleRequest(final ResourceModel model) {
        return AssumeRoleRequest.builder()
                .roleArn(model.getRoleArn())
                .roleSessionName("proServe-serviceCatalog-concurrentProvisionedProduct-" + model.getProvisionedProductName())
                .build();
    }

    public static PutItemRequest createAcquireLockItemRequest(final ResourceModel model) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("sc-product", AttributeValue.builder()
                .s(model.getProductName() != null ? model.getProductName() : model.getProductId())
                .build()
        );
        item.put("pp-name", AttributeValue.builder().s(model.getProvisionedProductName()).build());
        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#r", "sc-product");
        return PutItemRequest.builder()
                .tableName(LOCKS_DDB_TABLE_NAME)
                .item(item)
                .conditionExpression("attribute_not_exists(#r)")
                .expressionAttributeNames(attributeNames)
                .build();
    }

    public static DeleteItemRequest createReleaseLockItemRequest(final ResourceModel model) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("sc-product", AttributeValue.builder()
                .s(model.getProductName() != null ? model.getProductName() : model.getProductId())
                .build()
        );
        return DeleteItemRequest.builder()
                .tableName(LOCKS_DDB_TABLE_NAME)
                .key(key)
                .build();
    }

    public static PutItemRequest createPutStateItemRequest(final ResourceModel model) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder()
                .s(model.getId())
                .build()
        );
        item.put("pp-name", AttributeValue.builder().s(model.getProvisionedProductName()).build());
        item.put("pp-id", AttributeValue.builder().s(model.getProvisionedProductId()).build());
        item.put("role-arn", AttributeValue.builder().s(model.getRoleArn() != null ? model.getRoleArn() : "null").build());

        Map<String, String> attributeNames = new HashMap<>();
        attributeNames.put("#r", "id");
        return PutItemRequest.builder()
                .tableName(STATE_DDB_TABLE_NAME)
                .item(item)
                .conditionExpression("attribute_not_exists(#r)")
                .expressionAttributeNames(attributeNames)
                .build();
    }

    public static GetItemRequest createGetStateItemRequest(final ResourceModel model) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder()
                .s(model.getId())
                .build()
        );
        return GetItemRequest.builder()
                .tableName(STATE_DDB_TABLE_NAME)
                .key(key)
                .build();
    }

    public static DeleteItemRequest createDeleteStateItemRequest(final ResourceModel model) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder()
                .s(model.getId())
                .build()
        );
        return DeleteItemRequest.builder()
                .tableName(STATE_DDB_TABLE_NAME)
                .key(key)
                .build();
    }
}
