package poslog

import groovy.transform.ToString

/**
 * Represents a binary data entry attached to a transaction or line item.
 * Used within SES:TransactionBinaryDataList and SES:LineItemBinaryDataList.
 */
@ToString(includeNames = true, ignoreNulls = true)
class BinaryData {
    /** The name of the binary data entry (e.g. Journal, GraphicalJournal) */
    String name
    /** The binary object encoded in Base64 */
    String content

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
