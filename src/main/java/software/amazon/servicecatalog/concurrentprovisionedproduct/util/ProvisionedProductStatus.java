package software.amazon.servicecatalog.concurrentprovisionedproduct.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ProvisionedProductStatus {
    AVAILABLE("AVAILABLE"),

    UNDER_CHANGE("UNDER_CHANGE"),

    TAINTED("TAINTED"),

    ERROR("ERROR"),

    PLAN_IN_PROGRESS("PLAN_IN_PROGRESS"),

    UNKNOWN_TO_SDK_VERSION(null);

    private final String value;

    private ProvisionedProductStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Use this in place of valueOf to convert the raw string returned by the service into the enum value.
     *
     * @param value
     *        real value
     * @return ProvisionedProductStatus corresponding to the value
     */
    public static ProvisionedProductStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Stream.of(ProvisionedProductStatus.values()).filter(e -> e.toString().equals(value)).findFirst()
                .orElse(UNKNOWN_TO_SDK_VERSION);
    }

    /**
     * Use this in place of {@link #values()} to return a {@link Set} of all values known to the SDK. This will return
     * all known enum values except {@link #UNKNOWN_TO_SDK_VERSION}.
     *
     * @return a {@link Set} of known {@link ProvisionedProductStatus}s
     */
    public static Set<ProvisionedProductStatus> knownValues() {
        return Stream.of(values()).filter(v -> v != UNKNOWN_TO_SDK_VERSION).collect(Collectors.toSet());
    }
}
