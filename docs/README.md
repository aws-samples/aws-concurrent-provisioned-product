# ProServe::ServiceCatalog::ConcurrentProvisionedProduct

Resource Schema for ProServe::ServiceCatalog::ConcurrentProvisionedProduct

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "ProServe::ServiceCatalog::ConcurrentProvisionedProduct",
    "Properties" : {
        "<a href="#rolearn" title="RoleArn">RoleArn</a>" : <i>String</i>,
        "<a href="#acceptlanguage" title="AcceptLanguage">AcceptLanguage</a>" : <i>String</i>,
        "<a href="#notificationarns" title="NotificationArns">NotificationArns</a>" : <i>[ String, ... ]</i>,
        "<a href="#pathid" title="PathId">PathId</a>" : <i>String</i>,
        "<a href="#pathname" title="PathName">PathName</a>" : <i>String</i>,
        "<a href="#productid" title="ProductId">ProductId</a>" : <i>String</i>,
        "<a href="#productname" title="ProductName">ProductName</a>" : <i>String</i>,
        "<a href="#provisionedproductname" title="ProvisionedProductName">ProvisionedProductName</a>" : <i>String</i>,
        "<a href="#provisioningartifactid" title="ProvisioningArtifactId">ProvisioningArtifactId</a>" : <i>String</i>,
        "<a href="#provisioningartifactname" title="ProvisioningArtifactName">ProvisioningArtifactName</a>" : <i>String</i>,
        "<a href="#provisioningparameters" title="ProvisioningParameters">ProvisioningParameters</a>" : <i>[ <a href="provisioningparameter.md">ProvisioningParameter</a>, ... ]</i>,
        "<a href="#provisioningpreferences" title="ProvisioningPreferences">ProvisioningPreferences</a>" : <i><a href="provisioningpreferences.md">ProvisioningPreferences</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#recordid" title="RecordId">RecordId</a>" : <i>String</i>,
        "<a href="#outputkey" title="OutputKey">OutputKey</a>" : <i>String</i>,
    }
}
</pre>

### YAML

<pre>
Type: ProServe::ServiceCatalog::ConcurrentProvisionedProduct
Properties:
    <a href="#rolearn" title="RoleArn">RoleArn</a>: <i>String</i>
    <a href="#acceptlanguage" title="AcceptLanguage">AcceptLanguage</a>: <i>String</i>
    <a href="#notificationarns" title="NotificationArns">NotificationArns</a>: <i>
      - String</i>
    <a href="#pathid" title="PathId">PathId</a>: <i>String</i>
    <a href="#pathname" title="PathName">PathName</a>: <i>String</i>
    <a href="#productid" title="ProductId">ProductId</a>: <i>String</i>
    <a href="#productname" title="ProductName">ProductName</a>: <i>String</i>
    <a href="#provisionedproductname" title="ProvisionedProductName">ProvisionedProductName</a>: <i>String</i>
    <a href="#provisioningartifactid" title="ProvisioningArtifactId">ProvisioningArtifactId</a>: <i>String</i>
    <a href="#provisioningartifactname" title="ProvisioningArtifactName">ProvisioningArtifactName</a>: <i>String</i>
    <a href="#provisioningparameters" title="ProvisioningParameters">ProvisioningParameters</a>: <i>
      - <a href="provisioningparameter.md">ProvisioningParameter</a></i>
    <a href="#provisioningpreferences" title="ProvisioningPreferences">ProvisioningPreferences</a>: <i><a href="provisioningpreferences.md">ProvisioningPreferences</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#recordid" title="RecordId">RecordId</a>: <i>String</i>
    <a href="#outputkey" title="OutputKey">OutputKey</a>: <i>String</i>
</pre>

## Properties

#### RoleArn

Role ARN to be assumed in order to launch the product.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AcceptLanguage

The language code.

_Required_: No

_Type_: String

_Allowed Values_: <code>en</code> | <code>jp</code> | <code>zh</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### NotificationArns

Passed to AWS CloudFormation. The SNS topic ARNs to which to publish stack-related events.

_Required_: No

_Type_: List of String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### PathId

The path identifier of the product. This value is optional if the product has a default path, and required if the product has more than one path. To list the paths for a product, use ListLaunchPaths.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>100</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PathName

The name of the path. This value is optional if the product has a default path, and required if the product has more than one path. To list the paths for a product, use ListLaunchPaths.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>100</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ProductId

The product identifier.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>100</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ProductName

A user-friendly name for the provisioned product. This value must be unique for the AWS account and cannot be updated after the product is provisioned.

Each time a stack is created or updated, if ProductName is provided it will successfully resolve to ProductId as long as only one product exists in the account or Region with that ProductName.

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>128</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ProvisionedProductName

A user-friendly name for the provisioned product. This value must be unique for the AWS account and cannot be updated after the product is provisioned.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>128</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### ProvisioningArtifactId

The identifier of the provisioning artifact (also known as a version).

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>100</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ProvisioningArtifactName

The name of the provisioning artifact (also known as a version) for the product. This name must be unique for the product.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ProvisioningParameters

Parameters specified by the administrator that are required for provisioning the product.

_Required_: No

_Type_: List of <a href="provisioningparameter.md">ProvisioningParameter</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ProvisioningPreferences

StackSet preferences that are required for provisioning the product or updating a provisioned product.



_Required_: No

_Type_: <a href="provisioningpreferences.md">ProvisioningPreferences</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

One or more tags.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RecordId

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>50</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### OutputKey

The output key defines one key of an output of the launched product which should be returned as OutputValue

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the Id.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### Id

Unique internal identifier, used to properly track state

#### ProvisionedProductId

Returns the <code>ProvisionedProductId</code> value.

#### Outputs

List of key-value pair outputs.

#### OutputValue

The output value returned from provisioned product, key defined in OutputKey property

