package poslog

import groovy.transform.ToString

/**
 * Root container for the POSLog export.
 * Holds a list of Transaction elements as described in the GK POSLog v3 specification.
 */
@ToString(includeNames = true, ignoreNulls = true)
class POSLog {
    List<Transaction> transactions = []
}
