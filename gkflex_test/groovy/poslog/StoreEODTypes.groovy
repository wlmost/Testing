package poslog

import groovy.transform.ToString

/**
 * Represents SES:StoreEODSummary within ControlTransaction.
 * Total/summary values (store related).
 * Corresponds to section 4.3.8.6 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesStoreEODSummary {
    /** Session settle container with sum values per accounting period of store */
    StoreEODSessionSettle sessionSettle
}

/**
 * Represents the SessionSettle container within SesStoreEODSummary.
 */
@ToString(includeNames = true, ignoreNulls = true)
class StoreEODSessionSettle {
    /** TenderSummary – not generated at this time (optional) */
    // TenderSummary tenderSummary
    /** Sum values of sales per accounting period of store (optional) */
    List<SesSalesSummary> sesSalesSummaries = []
    /** SES:RebateSummary – not generated at this time (optional) */
    // SesRebateSummary sesRebateSummary
    /** Sum values of taxes per accounting period of store (optional) */
    List<SesTaxSummary> sesTaxSummaries = []
}
