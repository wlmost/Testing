package poslog

import groovy.transform.ToString

/**
 * Represents SES:CashierStatistics within ControlTransaction.
 * Statistics about the accounting period of a drawer.
 * Corresponds to section 4.3.8.5 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesCashierStatistics {
    /** Session settle container with statistics */
    CashierSessionSettle sessionSettle
}

/**
 * Represents the SessionSettle container within SesCashierStatistics.
 */
@ToString(includeNames = true, ignoreNulls = true)
class CashierSessionSettle {
    /** Quantity of receipts with revision type = 'TRANSACTION_COUNT' (optional) */
    Integer transactionCount
    /** Some statistics (optional) */
    TotalMeasures totalMeasures
    /** Sum and quantity of line item voids */
    LineItemVoids lineItemVoids
    /** Sum and quantity of post item voids */
    PostTransactionVoids postTransactionVoids
    /** Sum and quantity of transaction cancellations */
    SesTransactionCancellations sesTransactionCancellations
    /** Sum and quantity of direct line item voids */
    SesDirectLineItemVoids sesDirectLineItemVoids
    /** OperatorID of operator with one-to-one assignment to drawer (optional) */
    String sesAccountedOperatorID
    /** Name attribute on SES:AccountedOperatorID (optional) */
    String sesAccountedOperatorName
}

/**
 * Represents TotalMeasures within CashierSessionSettle.
 * Corresponds to section 4.3.8.5 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TotalMeasures {
    /** Quantity of "open Till" receipts (optional) */
    Integer noSaleTransactionCount
    /** Quantity of scanned positions (optional) */
    Integer lineItemScannedCount
    /** Quantity of value items (optional) */
    Integer lineItemOpenDepartmentCount
    /** Total time of sign-on time (optional) */
    String sesLogonTime
    /** Total time of item registration (optional) */
    String sesRegistrationTime
    /** Total time of tender record (optional) */
    String sesTenderTime
}

/**
 * Represents LineItemVoids within CashierSessionSettle.
 */
@ToString(includeNames = true, ignoreNulls = true)
class LineItemVoids {
    /** Sum of line item voids */
    BigDecimal amount
    /** Quantity of line item voids */
    Integer count
}

/**
 * Represents PostTransactionVoids within CashierSessionSettle.
 */
@ToString(includeNames = true, ignoreNulls = true)
class PostTransactionVoids {
    /** Sum of post transaction voids */
    BigDecimal amount
    /** Quantity of post transaction voids */
    Integer count
}

/**
 * Represents SES:TransactionCancellations within CashierSessionSettle.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTransactionCancellations {
    /** Sum of post transaction cancellations */
    BigDecimal amount
    /** Quantity of post transaction cancellations */
    Integer count
}

/**
 * Represents SES:DirectLineItemVoids within CashierSessionSettle.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesDirectLineItemVoids {
    /** Sum of direct line item voids */
    BigDecimal amount
    /** Quantity of direct line item voids */
    Integer count
}
