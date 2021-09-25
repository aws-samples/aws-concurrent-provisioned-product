package software.amazon.servicecatalog.concurrentprovisionedproduct.translator;

import com.amazonaws.transform.MapEntry;
import software.amazon.awssdk.services.servicecatalog.model.*;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PropertyTranslator {

    /**
     * Converts StackSet SDK ProvisioningPreferences to resource model ProvisioningPreferences
     *
     * @param provisioningPreferences ProvisioningPreferences collection from resource model
     * @return SDK Parameter list
     */
    static ProvisioningPreferences translateToSdkProvisioningPreferences(
            final software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningPreferences provisioningPreferences) {
        if (provisioningPreferences == null) return null;
        return ProvisioningPreferences.builder()
                .stackSetAccounts(provisioningPreferences.getStackSetAccounts())
                .stackSetRegions(provisioningPreferences.getStackSetRegions())
                .stackSetFailureToleranceCount(provisioningPreferences.getStackSetFailureToleranceCount())
                .stackSetFailureTolerancePercentage(provisioningPreferences.getStackSetFailureTolerancePercentage())
                .stackSetMaxConcurrencyCount(provisioningPreferences.getStackSetMaxConcurrencyCount())
                .stackSetMaxConcurrencyPercentage(provisioningPreferences.getStackSetMaxConcurrencyPercentage())
                .build();
    }

    /**
     * Converts StackSet SDK ProvisioningPreferences to resource model ProvisioningPreferences
     *
     * @param provisioningPreferences ProvisioningPreferences collection from resource model
     * @return SDK Parameter list
     */
    static UpdateProvisioningPreferences translateToSdkUpdateProvisioningPreferences(
            final software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningPreferences provisioningPreferences) {
        if (provisioningPreferences == null) return null;
        return UpdateProvisioningPreferences.builder()
                .stackSetAccounts(provisioningPreferences.getStackSetAccounts())
                .stackSetRegions(provisioningPreferences.getStackSetRegions())
                .stackSetFailureToleranceCount(provisioningPreferences.getStackSetFailureToleranceCount())
                .stackSetFailureTolerancePercentage(provisioningPreferences.getStackSetFailureTolerancePercentage())
                .stackSetMaxConcurrencyCount(provisioningPreferences.getStackSetMaxConcurrencyCount())
                .stackSetMaxConcurrencyPercentage(provisioningPreferences.getStackSetMaxConcurrencyPercentage())
                .build();
    }

    /**
     * Converts resource model ProvisioningPreferences to SDK ProvisioningPreferences
     *
     * @param provisioningPreferences ProvisioningPreferences from SDK
     * @return resource model Parameters
     */
    public static software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningPreferences translateFromSdkProvisioningPreferences(
            final ProvisioningPreferences provisioningPreferences) {
        if (provisioningPreferences == null) return null;
        return software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningPreferences.builder()
                .stackSetAccounts(new HashSet<>(provisioningPreferences.stackSetAccounts()))
                .stackSetRegions(new HashSet<>(provisioningPreferences.stackSetRegions()))
                .stackSetFailureToleranceCount(provisioningPreferences.stackSetFailureToleranceCount())
                .stackSetFailureTolerancePercentage(provisioningPreferences.stackSetFailureTolerancePercentage())
                .stackSetMaxConcurrencyCount(provisioningPreferences.stackSetMaxConcurrencyCount())
                .stackSetMaxConcurrencyPercentage(provisioningPreferences.stackSetMaxConcurrencyPercentage())
                .build();
    }

    /**
     * Converts SDK Parameters to resource model Parameters
     *
     * @param parameters Parameters collection from resource model
     * @return SDK Parameter list
     */
    static List<ProvisioningParameter> translateToSdkParameters(
            final Collection<software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningParameter> parameters) {
        if (CollectionUtils.isNullOrEmpty(parameters)) return Collections.emptyList();
        return parameters.stream()
                .map(parameter -> ProvisioningParameter.builder()
                        .key(parameter.getKey())
                        .value(parameter.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Converts SDK Parameters to resource model Parameters
     *
     * @param parameters Parameters collection from resource model
     * @return SDK Parameter list
     */
    static List<UpdateProvisioningParameter> translateToSdkUpdateParameters(
            final Collection<software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningParameter> parameters) {
        if (CollectionUtils.isNullOrEmpty(parameters)) return Collections.emptyList();
        return parameters.stream()
                .map(parameter -> UpdateProvisioningParameter.builder()
                        .key(parameter.getKey())
                        .value(parameter.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Converts resource model Parameters to SDK Parameters
     *
     * @param parameters Parameters from SDK
     * @return resource model Parameters
     */
    public static Set<software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningParameter> translateFromSdkParameters(
            final Collection<ProvisioningParameter> parameters) {
        if (CollectionUtils.isNullOrEmpty(parameters)) return null;
        return parameters.stream()
                .map(parameter -> software.amazon.servicecatalog.concurrentprovisionedproduct.ProvisioningParameter.builder()
                        .key(parameter.key())
                        .value(parameter.value())
                        .build())
                .collect(Collectors.toSet());
    }

    /**
     * Converts tags (from CFN resource model) to StackSet set (from StackSet SDK)
     *
     * @param tags Tags CFN resource model.
     * @return SDK Tags.
     */
    public static Set<Tag> translateToSdkTags(final List<software.amazon.servicecatalog.concurrentprovisionedproduct.Tag> tags) {
        if (tags == null) {
            return Collections.emptySet();
        }
        return Optional.of(tags).orElse(Collections.emptyList())
                .stream()
                .map(tag -> Tag.builder().key(tag.getKey()).value(tag.getValue()).build())
                .collect(Collectors.toSet());
    }

    /**
     * Converts a list of tags (from SC SDK) to Tag set (from CFN resource model)
     *
     * @param tags Tags from SC SDK.
     * @return A set of CFN SC Tag.
     */
    public static Set<software.amazon.servicecatalog.concurrentprovisionedproduct.Tag> translateFromSdkTags(final Collection<Tag> tags) {
        if (CollectionUtils.isNullOrEmpty(tags)) return null;
        return tags.stream().map(tag -> software.amazon.servicecatalog.concurrentprovisionedproduct.Tag.builder()
                .key(tag.key())
                .value(tag.value())
                .build())
                .collect(Collectors.toSet());
    }

    /**
     * Converts a list of Outputs (from SC SDK) to Oupput set (from CFN resource model)
     *
     * @param outputs Outputs from SC SDK.
     * @return A set of CFN SC Outputs.
     */
    public static Map<String, String> translateFromSdkOutputs(final Collection<RecordOutput> outputs) {
        if (CollectionUtils.isNullOrEmpty(outputs)) return null;
        return outputs.stream().collect(Collectors.toMap(RecordOutput::outputKey, RecordOutput::outputValue));
    }

    public static String extractProvisionedProductId(String input) {
        String[] splitted = input.split("-_-");
        return splitted.length > 1 ? splitted[1] : null;
    }

    public static String extractRoleArn(String input) {
        String[] splitted = input.split("-_-");
        return splitted.length > 1 ? splitted[0] : null;
    }


}