package poslog

import groovy.transform.ToString

/**
 * Example script demonstrating how to use the POSLog Groovy classes
 * in an embedded Groovy interpreter context.
 *
 * Usage in embedded Groovy interpreter:
 *
 *   GroovyClassLoader classLoader = new GroovyClassLoader()
 *   // Add the poslog package directory to the class loader
 *   classLoader.addClasspath('/path/to/groovy')
 *   // Load the script as a class
 *   Class scriptClass = classLoader.parseClass(new File('POSLogExample.groovy'))
 *
 * The interpreter uses the script internally as a class; all POSLog types
 * are resolved from the poslog package on the class loader's search path.
 */
class POSLogExample {

    /**
     * Builds a minimal RetailTransaction POSLog entry for demonstration.
     */
    static POSLog buildSampleRetailTransaction() {

        // --- Sale line item ---
        def saleTax = new Tax(
            taxType: 'Common',
            taxSubType: 'Standard',
            typeCode: 'Sale',
            taxAuthority: 'DE',
            taxableAmount: 10.08,
            amount: 1.61,
            percent: 19.0,
            taxGroupID: '1'
        )

        def priceModifier = new RetailPriceModifier(
            sequenceNumber: '1',
            amount: -1.00,
            amountAction: 'Substract',
            reasonCode: '0000',
            sesRebateMethod: '0000',
            sesRebateID: '0'
        )

        def sale = new Sale(
            itemType: 'Stock',
            itemID: '4005808002528',
            description: 'Coca Cola 0.5l',
            taxIncludedInPriceFlag: false,
            regularSalesUnitPrice: 1.99,
            extendedAmount: 0.99,
            quantity: 1,
            retailPriceModifiers: [priceModifier],
            tax: saleTax,
            sesExtendedPositionAmount: 0.99
        )

        def lineItemSale = new LineItem(
            sequenceNumber: '1',
            entryMethod: 'Scanned',
            sale: sale
        )

        // --- Tax line item on transaction level ---
        def transactionTax = new Tax(
            taxType: 'Common',
            taxSubType: 'Standard',
            typeCode: 'Sale',
            taxableAmount: 0.99,
            amount: 0.16,
            percent: 19.0,
            taxGroupID: '1'
        )
        def lineItemTax = new LineItem(
            sequenceNumber: '25001',
            tax: transactionTax
        )

        // --- Tender line item ---
        def tender = new Tender(
            tenderType: 'Cash',
            typeCode: 'Sale',
            tenderID: '1',
            amount: 1.00
        )
        def lineItemTender = new LineItem(
            sequenceNumber: '50001',
            tender: tender
        )

        // --- RetailTransaction ---
        def retailTransaction = new RetailTransaction(
            transactionStatus: 'Finished',
            receiptDateTime: '2025-10-09T10:15:00.000',
            lineItems: [lineItemSale, lineItemTax, lineItemTender],
            totalGrandAmount: 0.99,
            totalNetAmount: 0.83,
            totalTaxAmount: 0.16,
            sesNegativeTotalFlag: false
        )

        // --- Transaction ---
        def transaction = new Transaction(
            retailStoreID: '0001',
            workstationID: '1',
            tillID: '1',
            sequenceNumber: '1001',
            businessDayDate: '2025-10-09',
            beginDateTime: '2025-10-09T10:14:50.000',
            endDateTime: '2025-10-09T10:15:05.000',
            currencyCode: 'EUR',
            sesInternalTransactionID: '550e8400-e29b-41d4-a716-446655440000',
            sesFiscalFlag: false,
            sesLayawayFlag: false,
            sesPostVoidedFlag: false,
            sesReceiptReturnedFlag: false,
            retailTransaction: retailTransaction
        )

        return new POSLog(transactions: [transaction])
    }

    /**
     * Builds a minimal TenderControlTransaction (PaidIn) for demonstration.
     */
    static POSLog buildSamplePaidIn() {
        def sesPaidIn = new SesPaidIn(
            sesAmount: 200.00,
            sesReason: '0000',
            sesTransactionCategoryCode: 'PAYIN'
        )

        def tenderControlTransaction = new TenderControlTransaction(
            sesPaidIn: sesPaidIn
        )

        def transaction = new Transaction(
            retailStoreID: '0001',
            workstationID: '1',
            tillID: '1',
            sequenceNumber: '1002',
            businessDayDate: '2025-10-09',
            beginDateTime: '2025-10-09T08:00:00.000',
            endDateTime: '2025-10-09T08:00:05.000',
            currencyCode: 'EUR',
            sesInternalTransactionID: '550e8400-e29b-41d4-a716-446655440001',
            tenderControlTransaction: tenderControlTransaction
        )

        return new POSLog(transactions: [transaction])
    }

    /**
     * Entry point for standalone execution.
     */
    static void main(String[] args) {
        def retailLog = buildSampleRetailTransaction()
        println "=== RetailTransaction POSLog ==="
        retailLog.transactions.each { tx ->
            println "Transaction ${tx.sequenceNumber} (${tx.retailTransaction?.transactionStatus})"
            tx.retailTransaction?.lineItems?.each { li ->
                if (li.sale) println "  Sale: ${li.sale.description} - ${li.sale.extendedAmount}"
                if (li.tax)  println "  Tax: ${li.tax.percent}% -> ${li.tax.amount}"
                if (li.tender) println "  Tender: ${li.tender.tenderType} -> ${li.tender.amount}"
            }
        }

        def paidInLog = buildSamplePaidIn()
        println "\n=== TenderControlTransaction (PaidIn) ==="
        paidInLog.transactions.each { tx ->
            def pi = tx.tenderControlTransaction?.sesPaidIn
            println "PaidIn: ${pi?.sesAmount} (Reason: ${pi?.sesReason})"
        }
    }
}
