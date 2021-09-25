# ProServe::ServiceCatalog::ConcurrentProvisionedProduct ProvisioningPreferences

StackSet preferences that are required for provisioning the product or updating a provisioned product.



## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#stacksetaccounts" title="StackSetAccounts">StackSetAccounts</a>" : <i>[ String, ... ]</i>,
    "<a href="#stacksetfailuretolerancecount" title="StackSetFailureToleranceCount">StackSetFailureToleranceCount</a>" : <i>Integer</i>,
    "<a href="#stacksetfailuretolerancepercentage" title="StackSetFailureTolerancePercentage">StackSetFailureTolerancePercentage</a>" : <i>Integer</i>,
    "<a href="#stacksetmaxconcurrencycount" title="StackSetMaxConcurrencyCount">StackSetMaxConcurrencyCount</a>" : <i>Integer</i>,
    "<a href="#stacksetmaxconcurrencypercentage" title="StackSetMaxConcurrencyPercentage">StackSetMaxConcurrencyPercentage</a>" : <i>Integer</i>,
    "<a href="#stacksetoperationtype" title="StackSetOperationType">StackSetOperationType</a>" : <i>String</i>,
    "<a href="#stacksetregions" title="StackSetRegions">StackSetRegions</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#stacksetaccounts" title="StackSetAccounts">StackSetAccounts</a>: <i>
      - String</i>
<a href="#stacksetfailuretolerancecount" title="StackSetFailureToleranceCount">StackSetFailureToleranceCount</a>: <i>Integer</i>
<a href="#stacksetfailuretolerancepercentage" title="StackSetFailureTolerancePercentage">StackSetFailureTolerancePercentage</a>: <i>Integer</i>
<a href="#stacksetmaxconcurrencycount" title="StackSetMaxConcurrencyCount">StackSetMaxConcurrencyCount</a>: <i>Integer</i>
<a href="#stacksetmaxconcurrencypercentage" title="StackSetMaxConcurrencyPercentage">StackSetMaxConcurrencyPercentage</a>: <i>Integer</i>
<a href="#stacksetoperationtype" title="StackSetOperationType">StackSetOperationType</a>: <i>String</i>
<a href="#stacksetregions" title="StackSetRegions">StackSetRegions</a>: <i>
      - String</i>
</pre>

## Properties

#### StackSetAccounts

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StackSetFailureToleranceCount

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StackSetFailureTolerancePercentage

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StackSetMaxConcurrencyCount

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StackSetMaxConcurrencyPercentage

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StackSetOperationType

_Required_: No

_Type_: String

_Allowed Values_: <code>CREATE</code> | <code>UPDATE</code> | <code>DELETE</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StackSetRegions

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

