import groovy.transform.ToString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClients
import org.bson.Document
import groovy.json.JsonOutput



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



/**
 * Represents a ControlTransaction in the POSLog export.
 * Corresponds to section 4.3.8 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ControlTransaction {

    /** Reason code in case of an existing reason (optional) */
    String reasonCode

    // ---- Transaction type choice (exactly one will be populated) ----
    /** End-of-day (EOD) transaction (optional) */
    BusinessEOD businessEOD
    /** Total/summary values (drawer related) (optional) */
    TillEOD tillEOD
    /** NoSale transaction: timestamp for NoSale (optional) */
    String noSaleTimestamp
    /** Operator log-in (optional) */
    OperatorSignOn operatorSignOn
    /** Operator log-out (optional) */
    OperatorSignOff operatorSignOff
    /** Cashier statistics values (optional) */
    SesCashierStatistics sesCashierStatistics
    /** Total/summary values (store related) (optional) */
    SesStoreEODSummary sesStoreEODSummary
    /** Tax refund issue values (optional) */
    SesTaxRefund sesTaxRefund
    /** Created for all transaction types not exported in other tags (optional) */
    String sesOtherTransactionType

    /**
     * Flag created in case of OperatorSignOff (optional).
     * "true" = forced logout; "false" = normal logout
     */
    Boolean sesForcedSignOffFlag
    /** Identifier of workstation forced to log out (optional; only for forced logout) */
    String sesForcedSignOffWorkstationID
    /** OperatorID of operator with one-to-one assignment to drawer (optional) */
    String sesAccountedOperatorID
    /** Name attribute on SES:AccountedOperatorID (optional) */
    String sesAccountedOperatorName

    /** Reference to another transaction (optional) */
    List<TransactionLink> sesTransactionLinks = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents BusinessEOD within ControlTransaction.
 * Corresponds to section 4.3.8.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class BusinessEOD {
    /** Date and time of beginning of business day (accounting period) */
    String startDateTimestamp
}

/**
 * Represents OperatorSignOn within ControlTransaction.
 * Corresponds to section 4.3.8.3 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class OperatorSignOn {
    /** Date and time of sign-on receipt (optional) */
    String startDateTimestamp
}

/**
 * Represents OperatorSignOff within ControlTransaction.
 * Corresponds to section 4.3.8.4 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class OperatorSignOff {
    /** Date and time of sign-off receipt (optional) */
    String startDateTimestamp
}

/**
 * Represents SES:TaxRefund within ControlTransaction.
 * Corresponds to section 4.3.8.7 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTaxRefund {
    /**
     * TypeCode attribute (optional).
     * Possible values: Refund, Sale
     */
    String typeCode
    /** Unique identifier of the tax refund document from the tax refund service */
    String sesTaxRefundDocumentID
    /** UUID for the transaction in the external system (optional) */
    String sesExternalTransactionID
    /** Fiscal invoice number (optional; for countries where required) */
    String sesStoreInvoiceID
    /** Summarized gross amount for all sale transactions (absolute value) */
    BigDecimal sesTotalGrossAmount
    /** Summarized tax amount for all sale transactions (absolute value, optional) */
    BigDecimal sesTotalTaxAmount
    /** Resulting tax refund amount (absolute value) */
    BigDecimal sesTotalRefundAmount

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents a single LineItem within RetailTransaction.
 * Corresponds to section 4.3.6.1 of the GK POSLog Structure v3.
 *
 * The choice among sale, returnItem, rounding, sesRounding, voids, loyaltyReward,
 * tax, and tender determines the line item type.
 */
@ToString(includeNames = true, ignoreNulls = true)
class LineItem {

    // ---- Attributes ----
    /**
     * "true" if this line item was voided (optional).
     */
    Boolean voidFlag
    /**
     * Entry method (optional; created only for sale and return).
     * Possible values: Scanned, SES:KeyedPerDuplicate, Keyed
     */
    String entryMethod

    // ---- XML elements ----
    /**
     * Sequence number of the receipt position.
     * For additional down payment line items for immediate pickups: starts at 100001.
     * Otherwise: sequence number of original receipt position (starts with '0').
     */
    String sequenceNumber
    /** Date and time of position creation (optional) */
    String beginDateTime

    // ---- Line item type choice (exactly one will be populated) ----
    /** Sale position (optional) */
    Sale sale
    /** Return position (optional) */
    Return returnItem
    /** Rounding total (optional) */
    Rounding rounding
    /** SES rounding line item (optional) */
    SesRounding sesRounding
    /** Void position (optional) */
    Voids voids
    /** Non-financial bonus (optional) */
    LoyaltyReward loyaltyReward
    /** Tax (one per used tax group, optional) */
    Tax tax
    /** Tender (one per used tender, optional) */
    Tender tender

    // ---- Common line item extensions ----
    /** Approval by another operator (optional) */
    SesOperatorBypassApproval sesOperatorBypassApproval
    /**
     * Direct void flag (optional); created in case of voided original receipt position.
     * true = direct void
     */
    Boolean sesDirectVoidFlag
    /**
     * Negative amount flag (optional).
     * For Rounding: true if rounding amount is negative.
     * For Tax: true if TaxableAmount < 0.
     */
    Boolean sesNegativeAmountFlag
    /**
     * Online/offline state when the line item was completed (optional).
     * Possible values: OnLineReferenceItem, OffLine, Both
     */
    String sesKeyedOfflineCode
    /** Position addon list (optional) */
    List<SesReceiptPositionAddon> sesReceiptPositionAddonList = []
    /** Binary data attached to the line item (optional) */
    List<BinaryData> sesLineItemBinaryDataList = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Root container for the POSLog export.
 * Holds a list of Transaction elements as described in the GK POSLog v3 specification.
 */
@ToString(includeNames = true, ignoreNulls = true)
class POSLog {
    List<Transaction> transactions = []
}


/**
 * Maps the JSON representation of a POSLog XML – as provided by the embedded Groovy
 * Interpreter via the {@code sources} variable – to POSLog Groovy classes.
 *
 * <p>The JSON source is a tree structure where each node carries:
 * <ul>
 *   <li>{@code id}           – the XML element name
 *   <li>{@code type}         – {@code "root"}, {@code "group"}, or {@code "element"}
 *   <li>{@code _attributes}  – map of XML attributes (may be absent)
 *   <li>{@code children}     – list of child nodes (group/root only)
 *   <li>{@code value}        – text content (element only)
 * </ul>
 *
 * <p>The interpreter exposes nodes through a dynamic proxy:
 * <ul>
 *   <li>{@code node.ChildName}               – first child matching that id (or null)
 *   <li>{@code node.forEach('Name', closure)} – iterates over all children with that id
 *   <li>{@code node.toString()}               – text value of an element node
 *   <li>{@code node._attributes}              – raw attribute map of a node
 * </ul>
 *
 * <p>Usage in the embedded interpreter:
 * <pre>
 *   def posLog = POSLogJsonMapper.map(sources)
 * </pre>
 */
class POSLogJsonMapper {

    // =========================================================================
    // Public entry point
    // =========================================================================

    /**
     * Maps the full {@code sources} object provided by the embedded Groovy Interpreter
     * to a {@link POSLog} instance.
     *
     * @param sources the root node exposed by the interpreter
     * @return the fully populated {@link POSLog}
     */
    static POSLog map(def source) {
        def posLog = new POSLog()
        def posLogNode = source.POSLog
        posLogNode.forEach 'Transaction', { txNode ->
            posLog.transactions << mapTransaction(txNode)
        }
        return posLog
    }

    // =========================================================================
    // Transaction
    // =========================================================================

    private static Transaction mapTransaction(def txNode) {
        def tx = new Transaction()

        def attrs = txNode._attributes
        if (attrs) {
            if (attrs.MajorVersion                != null) tx.majorVersion              = attrs.MajorVersion                as Integer
            if (attrs.MinorVersion                != null) tx.minorVersion              = attrs.MinorVersion                as Integer
            if (attrs.FixVersion                  != null) tx.fixVersion                = attrs.FixVersion                  as Integer
            if (attrs['SES:InternalMajorVersion'] != null) tx.sesInternalMajorVersion  = attrs['SES:InternalMajorVersion'] as Integer
            if (attrs['SES:InternalMinorVersion'] != null) tx.sesInternalMinorVersion  = attrs['SES:InternalMinorVersion'] as Integer
            if (attrs['SES:InternalFixVersion']   != null) tx.sesInternalFixVersion    = attrs['SES:InternalFixVersion']   as Integer
            if (attrs.CancelFlag       != null) tx.cancelFlag       = attrs.CancelFlag       == 'true'
            if (attrs.TrainingModeFlag != null) tx.trainingModeFlag = attrs.TrainingModeFlag == 'true'
        }

        tx.retailStoreID   = toStr(txNode.RetailStoreID)
        tx.workstationID   = toStr(txNode.WorkstationID)
        tx.tillID          = toStr(txNode.TillID)
        tx.sequenceNumber  = toStr(txNode.SequenceNumber)
        tx.businessDayDate = toStr(txNode.BusinessDayDate)
        tx.beginDateTime   = toStr(txNode.BeginDateTime)
        tx.endDateTime     = toStr(txNode.EndDateTime)

        def operatorIDNode = txNode.OperatorID
        if (operatorIDNode != null) {
            tx.operatorID = operatorIDNode.toString() ?: null
            def opAttrs = operatorIDNode._attributes
            if (opAttrs) {
                tx.operatorName = opAttrs.OperatorName ?: null
                tx.workerID     = opAttrs.WorkerID     ?: null
            }
        }

        tx.currencyCode = toStr(txNode.CurrencyCode)
        tx.sesTenantID  = toStr(txNode.TenantID)

        tx.sesReceiptReturnedFlag        = toBool(txNode.ReceiptReturnedFlag)
        tx.sesPostVoidedFlag             = toBool(txNode.PostVoidedFlag)
        tx.sesTransactionSoftwareVersion = toStr(txNode.TransactionSoftwareVersion)
        tx.sesSoftwareVersion            = toStr(txNode.SoftwareVersion)
        tx.sesKeyedOfflineCode           = toStr(txNode.KeyedOfflineCode)
        tx.sesInternalTransactionID      = toStr(txNode.InternalTransactionID)
        tx.sesFiscalFlag                 = toBool(txNode.FiscalFlag)
        tx.sesFiscalSequenceNumber       = toStr(txNode.FiscalSequenceNumber)
        tx.sesFiscalDayNumber            = toStr(txNode.FiscalDayNumber)
        tx.sesFiscalPrinterID            = toStr(txNode.FiscalPrinterID)
        tx.sesFiscalSignature            = toStr(txNode.FiscalSignature)
        tx.sesLayawayFlag                = toBool(txNode.LayawayFlag)
        tx.sesLayawayTransactionType     = toStr(txNode.LayawayTransactionType)
        def reasonCodeNode = txNode.ReasonCode
        if (reasonCodeNode != null) {
            tx.sesReasonCode        = reasonCodeNode.toString() ?: null
            def rcAttrs = reasonCodeNode._attributes
            if (rcAttrs) {
                tx.sesReasonType        = rcAttrs.ReasonType    ?: null
                tx.sesReasonDescription = rcAttrs.Description   ?: null
            }
        }
        tx.sesBusinessBeginDateTime      = toStr(txNode.BusinessBeginDateTime)
        tx.sesBusinessEndDateTime        = toStr(txNode.BusinessEndDateTime)

        def operatorBypassNode = txNode.OperatorBypassApproval
        if (operatorBypassNode != null) {
            tx.sesOperatorBypassApproval = mapOperatorBypassApproval(operatorBypassNode)
        }

        def headerAddonListNode = txNode.ReceiptHeaderAddonList
        if (headerAddonListNode != null) {
            headerAddonListNode.forEach 'Addon', { addonNode ->
                tx.sesReceiptHeaderAddonList << mapAddon(addonNode)
            }
        }

        def timerListNode = txNode.ReceiptTimerList
        if (timerListNode != null) {
            timerListNode.forEach 'Timer', { timerNode ->
                tx.sesReceiptTimerList << mapTimer(timerNode)
            }
        }

        def loyaltyAccountNode = txNode.LoyaltyAccount
        if (loyaltyAccountNode != null) {
            tx.sesLoyaltyAccount = mapLoyaltyAccount(loyaltyAccountNode)
        }

        def binaryDataListNode = txNode.TransactionBinaryDataList
        if (binaryDataListNode != null) {
            binaryDataListNode.forEach 'BinaryData', { bdNode ->
                tx.sesTransactionBinaryDataList << mapBinaryData(bdNode)
            }
        }

        // Custom fields CST:XXCustom01..15
        mapCustomFields(tx.customFields, txNode, 15)

        // Transaction type (mutually exclusive)
        def retailTransactionNode = txNode.RetailTransaction
        if (retailTransactionNode != null) {
            tx.retailTransaction = mapRetailTransaction(retailTransactionNode)
        }

        def tenderControlNode = txNode.TenderControlTransaction
        if (tenderControlNode != null) {
            tx.tenderControlTransaction = mapTenderControlTransaction(tenderControlNode)
        }

        def controlNode = txNode.ControlTransaction
        if (controlNode != null) {
            tx.controlTransaction = mapControlTransaction(controlNode)
        }

        return tx
    }

    // =========================================================================
    // RetailTransaction
    // =========================================================================

    private static RetailTransaction mapRetailTransaction(def rtNode) {
        def rt = new RetailTransaction()

        def attrs = rtNode._attributes
        if (attrs) {
            rt.transactionStatus = attrs.TransactionStatus ?: null
        }

        rt.receiptDateTime = toStr(rtNode.ReceiptDateTime)

        rtNode.forEach 'LineItem', { liNode ->
            rt.lineItems << mapLineItem(liNode)
        }

        rtNode.forEach 'Total', { totalNode ->
            def totalAttrs = totalNode._attributes
            def totalValue  = toDecimal(totalNode)
            switch (totalAttrs?.TotalType) {
                case 'TransactionGrandAmount': rt.totalGrandAmount = totalValue; break
                case 'TransactionNetAmount':   rt.totalNetAmount   = totalValue; break
                case 'TransactionTaxAmount':   rt.totalTaxAmount   = totalValue; break
                default:
                    if (totalAttrs?.TotalType) {
                        rt.totalCustomFields[totalAttrs.TotalType] = totalNode.toString()
                    }
            }
        }

        rt.sesNegativeTotalFlag      = toBool(rtNode.NegativeTotalFlag)
        rt.sesAmendmentFlag          = toBool(rtNode.AmendmentFlag)
        rt.sesEmailRequestedFlag     = toBool(rtNode.EmailRequestedFlag)
        rt.sesEmailAddress           = toStr(rtNode.EmailAddress)
        rt.sesInvoiceNumber          = toStr(rtNode.InvoiceNumber)
        rt.sesInvoicePrintoutTypeCode = toStr(rtNode.InvoicePrintoutTypeCode)

        def customerNode = rtNode.Customer
        if (customerNode != null) {
            rt.customer = mapCustomer(customerNode)
        }

        def txLinkNode = rtNode.TransactionLink
        if (txLinkNode != null) {
            rt.transactionLink = mapTransactionLink(txLinkNode)
        }

        def couponSummaryListNode = rtNode.CouponSummaryList
        if (couponSummaryListNode != null) {
            couponSummaryListNode.forEach 'CouponSummary', { csNode ->
                rt.sesCouponSummaryList << mapCouponSummary(csNode)
            }
        }

        mapCustomFields(rt.customFields, rtNode, 5)

        return rt
    }

    // =========================================================================
    // LineItem
    // =========================================================================

    private static LineItem mapLineItem(def liNode) {
        def li = new LineItem()

        def attrs = liNode._attributes
        if (attrs) {
            if (attrs.VoidFlag    != null) li.voidFlag    = attrs.VoidFlag    == 'true'
            if (attrs.EntryMethod != null) li.entryMethod = attrs.EntryMethod
        }

        li.sequenceNumber = toStr(liNode.SequenceNumber)
        li.beginDateTime  = toStr(liNode.BeginDateTime)

        def saleNode = liNode.Sale
        if (saleNode != null) {
            li.sale = mapSale(saleNode)
        }

        def returnNode = liNode.Return
        if (returnNode != null) {
            li.returnItem = mapReturn(returnNode)
        }

        def taxNode = liNode.Tax
        if (taxNode != null) {
            li.tax = mapTax(taxNode)
        }

        def tenderNode = liNode.Tender
        if (tenderNode != null) {
            li.tender = mapTender(tenderNode)
        }

        def roundingNode = liNode.Rounding
        if (roundingNode != null) {
            li.rounding = new Rounding()
        }

        def sesRoundingNode = liNode.SesRounding
        if (sesRoundingNode != null) {
            li.sesRounding = mapSesRounding(sesRoundingNode)
        }

        def voidsNode = liNode.Voids
        if (voidsNode != null) {
            li.voids = mapVoids(voidsNode)
        }

        def loyaltyRewardNode = liNode.LoyaltyReward
        if (loyaltyRewardNode != null) {
            li.loyaltyReward = mapLoyaltyReward(loyaltyRewardNode)
        }

        li.sesKeyedOfflineCode   = toStr(liNode.KeyedOfflineCode)
        li.sesNegativeAmountFlag = toBool(liNode.NegativeAmountFlag)
        li.sesDirectVoidFlag     = toBool(liNode.DirectVoidFlag)

        def liOperatorBypassNode = liNode.OperatorBypassApproval
        if (liOperatorBypassNode != null) {
            li.sesOperatorBypassApproval = mapOperatorBypassApproval(liOperatorBypassNode)
        }

        def addonListNode = liNode.ReceiptPositionAddonList
        if (addonListNode != null) {
            addonListNode.forEach('Addon') { addonNode ->
                li.sesReceiptPositionAddonList << mapPositionAddon(addonNode)
            }
        }

        def liBinaryDataListNode = liNode.LineItemBinaryDataList
        if (liBinaryDataListNode != null) {
            liBinaryDataListNode.forEach('BinaryData') { bdNode ->
                li.sesLineItemBinaryDataList << mapBinaryData(bdNode)
            }
        }

        mapCustomFields(li.customFields, liNode, 5)

        return li
    }

    // =========================================================================
    // Sale
    // =========================================================================

    private static Sale mapSale(def saleNode) {
        def sale = new Sale()
        populateSaleFields(sale, saleNode)
        return sale
    }

    private static Return mapReturn(def returnNode) {
        def ret = new Return()
        populateSaleFields(ret, returnNode)

        def retLinkNode = returnNode.ReturnTransactionLink
        if (retLinkNode != null) {
            ret.returnTransactionLink = mapTransactionLink(retLinkNode)
        }
        ret.sesReturnReasonCode = toStr(returnNode.ReturnReasonCode)

        return ret
    }

    /** Populates Sale fields (and by inheritance Return fields) from a JSON node. */
    private static void populateSaleFields(Sale sale, def saleNode) {
        def attrs = saleNode._attributes
        if (attrs) {
            sale.itemType = attrs.ItemType ?: null
        }

        // First POSIdentity child (typically RegistrationNumber type)
        def posIdentityNode = saleNode.POSIdentity
        if (posIdentityNode != null) {
            sale.posIdentity = mapPOSIdentity(posIdentityNode)
        }

        def itemIDNode = saleNode.ItemID
        if (itemIDNode != null) {
            sale.itemID = itemIDNode.toString() ?: null
            def idAttrs = itemIDNode._attributes
            if (idAttrs) sale.itemIDName = idAttrs.Name ?: null
        }

        sale.specialOrderNumber   = toStr(saleNode.SpecialOrderNumber)
        sale.merchandiseHierarchy = toStr(saleNode.MerchandiseHierarchy)
        sale.epc                  = toStr(saleNode.EPC)
        sale.description          = toStr(saleNode.Description)
        sale.taxIncludedInPriceFlag = toBool(saleNode.TaxIncludedInPriceFlag)

        def regularPriceNode = saleNode.RegularSalesUnitPrice
        if (regularPriceNode != null) {
            sale.regularSalesUnitPrice = toDecimal(regularPriceNode)
            def priceAttrs = regularPriceNode._attributes
            if (priceAttrs?.Quantity != null) {
                sale.regularSalesUnitPriceQuantity = priceAttrs.Quantity as BigDecimal
            }
        }

        sale.extendedAmount = toDecimal(saleNode.ExtendedAmount)

        def quantityNode = saleNode.Quantity
        if (quantityNode != null) {
            sale.quantity = toDecimal(quantityNode)
            def qAttrs = quantityNode._attributes
            if (qAttrs) {
                sale.quantityUnits             = qAttrs.Units             ?: null
                sale.quantityUnitOfMeasureCode = qAttrs.UnitOfMeasureCode ?: null
            }
        }

        def taxNode = saleNode.Tax
        if (taxNode != null) {
            sale.tax = mapTax(taxNode)
        }

        sale.serialNumber                  = toStr(saleNode.SerialNumber)
        sale.sesExtendedPositionAmount     = toDecimal(saleNode.ExtendedPositionAmount)
        sale.sesMerchandiseStructureItemFlag = toBool(saleNode.MerchandiseStructureItemFlag)
        sale.sesAuthorizedForSalesFlag     = toBool(saleNode.AuthorizedForSalesFlag)
        sale.sesSpecialOfferNumber         = toStr(saleNode.SpecialOfferNumber)
        sale.sesReasonCode                 = toStr(saleNode.ReasonCode)
        sale.sesPriceGroupID               = toStr(saleNode.PriceGroupID)
        sale.sesInvoiceNumber              = toStr(saleNode.InvoiceNumber)
        sale.sesConcessionSupplierID       = toStr(saleNode.ConcessionSupplierID)

        def associateNode = saleNode.Associate
        if (associateNode != null) {
            sale.associate = mapAssociate(associateNode)
        }

        saleNode.forEach('RetailPriceModifier') { rpmNode ->
            sale.retailPriceModifiers << mapRetailPriceModifier(rpmNode)
        }

        def txLinkNode = saleNode.TransactionLink
        if (txLinkNode != null) {
            sale.transactionLink = mapTransactionLink(txLinkNode)
        }

        def itemLinkNode = saleNode.ItemLink
        if (itemLinkNode != null) {
            sale.sesItemLink = new SesItemLink(linkType: itemLinkNode._attributes?.LinkType ?: null)
        }

        saleNode.forEach('ExternalReceiptReference') { errNode ->
            sale.sesExternalReceiptReferenceList << mapExternalReceiptReference(errNode)
        }

        def giftCertDataNode = saleNode.GiftCertificateData
        if (giftCertDataNode != null) {
            sale.sesGiftCertificateData = new SesGiftCertificateData(
                sesGiftCertificateType: giftCertDataNode._attributes?.GiftCertificateType ?: null
            )
        }

        def salesOrderDataNode = saleNode.SalesOrderData
        if (salesOrderDataNode != null) {
            sale.sesSalesOrderData = mapSalesOrderData(salesOrderDataNode)
        }

        def loyaltyRewardNode = saleNode.LoyaltyReward
        if (loyaltyRewardNode != null) {
            sale.sesLoyaltyReward = mapSesLoyaltyReward(loyaltyRewardNode)
        }

        mapCustomFields(sale.customFields, saleNode, 15)
    }

    // =========================================================================
    // POSIdentity
    // =========================================================================

    private static POSIdentity mapPOSIdentity(def node) {
        def pi = new POSIdentity()
        def attrs = node._attributes
        if (attrs) pi.posIDType = attrs.POSIDType ?: null
        pi.posItemID  = toStr(node.POSItemID)
        pi.qualifier  = toStr(node.Qualifier)
        return pi
    }

    // =========================================================================
    // Associate
    // =========================================================================

    private static Associate mapAssociate(def node) {
        def assoc = new Associate()
        assoc.associateID   = toStr(node.AssociateID)
        def assocAttrs = node._attributes
        if (assocAttrs) {
            assoc.operatorName = assocAttrs.OperatorName ?: null
            assoc.workerID     = assocAttrs.WorkerID     ?: null
        }
        return assoc
    }

    // =========================================================================
    // RetailPriceModifier
    // =========================================================================

    private static RetailPriceModifier mapRetailPriceModifier(def node) {
        def rpm = new RetailPriceModifier()
        rpm.sequenceNumber = toStr(node.SequenceNumber)
        rpm.promotionID    = toStr(node.PromotionID)
        rpm.reasonCode     = toStr(node.ReasonCode)
        rpm.sesRebateMethod   = toStr(node.RebateMethod)
        rpm.sesRebateID       = toStr(node.RebateID)
        rpm.sesPercent        = toDecimal(node.Percent)
        rpm.sesExternalOfferID = toStr(node.ExternalOfferID)
        rpm.sesAdditionalPriceTypeCode = toStr(node.AdditionalPriceTypeCode)

        def amtNode = node.Amount
        if (amtNode != null) {
            rpm.amount = toDecimal(amtNode)
            def amtAttrs = amtNode._attributes
            if (amtAttrs) rpm.amountAction = amtAttrs.Action ?: null
        }

        def pdrNode = node.PriceDerivationRule
        if (pdrNode != null) {
            rpm.priceDerivationRule = mapPriceDerivationRule(pdrNode)
        }

        mapCustomFields(rpm.customFields, node, 5)
        return rpm
    }

    // =========================================================================
    // PriceDerivationRule
    // =========================================================================

    private static PriceDerivationRule mapPriceDerivationRule(def node) {
        def pdr = new PriceDerivationRule()
        pdr.priceDerivationRuleID = toStr(node.PriceDerivationRuleID)
        pdr.sesOrigin             = toStr(node.Origin)
        pdr.sesAppliedQuantity    = toDecimal(node.AppliedQuantity)
        pdr.sesRuleDescription    = toStr(node.RuleDescription)

        def amtNode = node.Amount
        if (amtNode != null) {
            pdr.amount = toDecimal(amtNode)
            def amtAttrs = amtNode._attributes
            if (amtAttrs) pdr.amountAction = amtAttrs.Action ?: null
        }

        def eligNode = node.Eligibility
        if (eligNode != null) {
            def elig = new Eligibility()
            def refNode = eligNode.ReferenceID
            if (refNode != null) {
                elig.referenceID = refNode.toString() ?: null
                elig.referenceIDType = refNode._attributes?.Type ?: null
            }
            pdr.eligibility = elig
        }

        mapCustomFields(pdr.customFields, node, 15)
        return pdr
    }

    // =========================================================================
    // SES LoyaltyReward (within Sale)
    // =========================================================================

    private static SesLoyaltyReward mapSesLoyaltyReward(def node) {
        def lr = new SesLoyaltyReward()
        lr.promotionID       = toStr(node.PromotionID)
        lr.reasonCode        = toStr(node.ReasonCode)
        lr.pointsAwarded     = toDecimal(node.PointsAwarded)
        lr.sequenceNumber    = toStr(node.SequenceNumber)
        lr.sesRebateMethod   = toStr(node.RebateMethod)
        lr.sesRebateID       = toStr(node.RebateID)
        lr.sesExternalOfferID = toStr(node.ExternalOfferID)

        def gcNode = node.GiftCertificate
        if (gcNode != null) lr.giftCertificate = mapGiftCertificate(gcNode)

        def voucherNode = node.Voucher
        if (voucherNode != null) lr.sesVoucher = mapSesVoucher(voucherNode)

        def pdrNode = node.PriceDerivationRule
        if (pdrNode != null) lr.sesPriceDerivationRule = mapPriceDerivationRule(pdrNode)

        mapCustomFields(lr.customFields, node, 5)
        return lr
    }

    // =========================================================================
    // SalesOrderData
    // =========================================================================

    private static SesSalesOrderData mapSalesOrderData(def node) {
        def sod = new SesSalesOrderData()
        sod.sesSpecialOrderSystem          = toStr(node.SpecialOrderSystem)
        sod.sesSpecialOrderType            = toStr(node.SpecialOrderType)
        sod.sesSpecialOrderPositionNumber  = toStr(node.SpecialOrderPositionNumber)
        mapCustomFields(sod.customFields, node, 15)
        return sod
    }

    // =========================================================================
    // ExternalReceiptReference
    // =========================================================================

    private static SesExternalReceiptReference mapExternalReceiptReference(def node) {
        def err = new SesExternalReceiptReference()
        err.sesItemOrigin                            = toStr(node.ItemOrigin)
        err.sesReceiptReferenceNumber                = toStr(node.ReceiptReferenceNumber)
        err.sesExternalTransactionOfflineRedemptionFlag = toBool(node.ExternalTransactionOfflineRedemptionFlag)
        mapCustomFields(err.customFields, node, 5)
        return err
    }

    // =========================================================================
    // Tax
    // =========================================================================

    private static Tax mapTax(def taxNode) {
        def tax = new Tax()
        def attrs = taxNode._attributes
        if (attrs) {
            tax.taxType    = attrs.TaxType    ?: null
            tax.taxSubType = attrs.TaxSubType ?: null
            tax.typeCode   = attrs.TypeCode   ?: null
        }
        tax.taxAuthority  = toStr(taxNode.TaxAuthority)
        tax.taxableAmount = toDecimal(taxNode.TaxableAmount)
        tax.amount        = toDecimal(taxNode.Amount)
        tax.percent       = toDecimal(taxNode.Percent)
        tax.taxGroupID    = toStr(taxNode.TaxGroupID)

        def taxExemptNode = taxNode.TaxExemption
        if (taxExemptNode != null) {
            def te = new TaxExemption()
            te.customerExemptionID = toStr(taxExemptNode.CustomerExemptionID)
            te.exemptTaxAmount     = toDecimal(taxExemptNode.ExemptTaxAmount) ?: BigDecimal.ZERO
            te.reasonCode          = toStr(taxExemptNode.ReasonCode)
            tax.taxExemption       = te
        }

        def taxOverrideNode = taxNode.TaxOverride
        if (taxOverrideNode != null) {
            def to = new TaxOverride()
            to.originalPercent    = toDecimal(taxOverrideNode.OriginalPercent)
            to.originalTaxAmount  = toDecimal(taxOverrideNode.OriginalTaxAmount)
            to.newTaxPercent      = toDecimal(taxOverrideNode.NewTaxPercent)
            to.newTaxAmount       = toDecimal(taxOverrideNode.NewTaxAmount)
            to.reasonCode         = toStr(taxOverrideNode.ReasonCode)
            tax.taxOverride       = to
        }

        mapCustomFields(tax.customFields, taxNode, 5)
        return tax
    }

    // =========================================================================
    // Tender
    // =========================================================================

    private static Tender mapTender(def tenderNode) {
        def tender = new Tender()

        def attrs = tenderNode._attributes
        if (attrs) {
            tender.tenderType        = attrs.TenderType        ?: null
            tender.typeCode          = attrs.TypeCode          ?: null
            tender.tenderDescription = attrs.TenderDescription ?: null
        }

        tender.tenderID = toStr(tenderNode.TenderID)

        def amtNode = tenderNode.Amount
        if (amtNode != null) {
            tender.amount = toDecimal(amtNode)
            def amtAttrs = amtNode._attributes
            if (amtAttrs) {
                tender.amountCurrency = amtAttrs.Currency      ?: null
                tender.foreignAmount  = amtAttrs.ForeignAmount != null ? amtAttrs.ForeignAmount as BigDecimal : null
            }
        }

        def tenderChangeNode = tenderNode.TenderChange
        if (tenderChangeNode != null) {
            def tc = new TenderChange()
            def tcAttrs = tenderChangeNode._attributes
            if (tcAttrs) {
                tc.tenderType = tcAttrs.TenderType ?: null
            }
            tc.tenderID = toStr(tenderChangeNode.TenderID)
            def tcAmtNode = tenderChangeNode.Amount
            if (tcAmtNode != null) {
                tc.amount = toDecimal(tcAmtNode)
                def tcAmtAttrs = tcAmtNode._attributes
                if (tcAmtAttrs) {
                    tc.amountCurrency = tcAmtAttrs.Currency      ?: null
                    tc.foreignAmount  = tcAmtAttrs.ForeignAmount != null ? tcAmtAttrs.ForeignAmount as BigDecimal : null
                }
            }
            tender.tenderChange = tc
        }

        def cashbackNode = tenderNode.Cashback
        if (cashbackNode != null) {
            tender.cashback = toDecimal(cashbackNode)
            def cbAttrs = cashbackNode._attributes
            if (cbAttrs) {
                tender.cashbackCurrency      = cbAttrs.Currency      ?: null
                tender.cashbackForeignAmount = cbAttrs.ForeignAmount != null ? cbAttrs.ForeignAmount as BigDecimal : null
            }
        }

        def tipNode = tenderNode.Tip
        if (tipNode != null) {
            tender.tip = toDecimal(tipNode)
            def tipAttrs = tipNode._attributes
            if (tipAttrs) {
                tender.tipCurrency      = tipAttrs.Currency      ?: null
                tender.tipForeignAmount = tipAttrs.ForeignAmount != null ? tipAttrs.ForeignAmount as BigDecimal : null
            }
        }

        def authNode = tenderNode.Authorization
        if (authNode != null) {
            tender.authorization = mapAuthorization(authNode)
        }

        def foreignCurrencyNode = tenderNode.ForeignCurrency
        if (foreignCurrencyNode != null) {
            tender.foreignCurrency = mapForeignCurrency(foreignCurrencyNode)
        }

        def checkNode = tenderNode.Check
        if (checkNode != null) {
            tender.check = mapCheck(checkNode)
        }

        def creditDebitNode = tenderNode.CreditDebit
        if (creditDebitNode != null) {
            tender.creditDebit = mapCreditDebit(creditDebitNode)
        }

        def couponNode = tenderNode.Coupon
        if (couponNode != null) {
            tender.coupon = mapCoupon(couponNode)
        }

        def voucherNode = tenderNode.Voucher
        if (voucherNode != null) {
            tender.voucher = mapVoucher(voucherNode)
        }

        def loyaltyRedemptionNode = tenderNode.LoyaltyRedemption
        if (loyaltyRedemptionNode != null) {
            def lr = new SesLoyaltyRedemption()
            lr.pointsRedeemed = toDecimal(loyaltyRedemptionNode.PointsRedeemed)
            lr.transactionID  = toStr(loyaltyRedemptionNode.TransactionID)
            tender.sesLoyaltyRedemption = lr
        }

        mapCustomFields(tender.customFields, tenderNode, 15)
        return tender
    }

    // =========================================================================
    // Authorization
    // =========================================================================

    private static Authorization mapAuthorization(def authNode) {
        def auth = new Authorization()

        def attrs = authNode._attributes
        if (attrs) {
            if (attrs.HostAuthorized      != null) auth.hostAuthorized      = attrs.HostAuthorized      == 'true'
            if (attrs.ElectronicSignature != null) auth.electronicSignature = attrs.ElectronicSignature == 'true'
            if (attrs.ForceOnline         != null) auth.forceOnline         = attrs.ForceOnline         == 'true'
        }

        def reqAmtNode = authNode.RequestedAmount
        if (reqAmtNode != null) {
            auth.requestedAmount = toDecimal(reqAmtNode)
            def reqAttrs = reqAmtNode._attributes
            if (reqAttrs) {
                auth.requestedAmountCurrency = reqAttrs.Currency      ?: null
                auth.requestedForeignAmount  = reqAttrs.ForeignAmount != null ? reqAttrs.ForeignAmount as BigDecimal : null
            }
        }

        auth.authorizationCode            = toStr(authNode.AuthorizationCode)
        auth.referenceNumber              = toStr(authNode.ReferenceNumber)
        auth.authorizationDateTime        = toStr(authNode.AuthorizationDateTime)
        auth.authorizingTermID            = toStr(authNode.AuthorizingTermID)
        auth.applicationID                = toStr(authNode.ApplicationID)
        auth.sesTransactionType           = toStr(authNode.TransactionType)
        auth.sesTerminalTenderDescription = toStr(authNode.TerminalTenderDescription)
        auth.sesEncryptedPAN              = toStr(authNode.EncryptedPAN)
        auth.sesTransactionCurrencyCode   = toStr(authNode.TransactionCurrencyCode)
        auth.sesTerminalTransactionToken  = toStr(authNode.TerminalTransactionToken)
        auth.sesApprovalCode              = toStr(authNode.ApprovalCode)
        auth.sesActivationSequenceNumber  = toStr(authNode.ActivationSequenceNumber)
        auth.sesTransactionReferenceNumber = toStr(authNode.TransactionReferenceNumber)

        mapCustomFields(auth.customFields, authNode, 5)
        return auth
    }

    // =========================================================================
    // CreditDebit
    // =========================================================================

    private static CreditDebit mapCreditDebit(def node) {
        def cd = new CreditDebit()
        cd.issuerIdentificationNumber          = toStr(node.IssuerIdentificationNumber)
        cd.primaryAccountNumber               = toStr(node.PrimaryAccountNumber)
        cd.issueSequence                      = toStr(node.IssueSequence)
        cd.expirationDate                     = toStr(node.ExpirationDate)
        cd.reconciliationCode                 = toStr(node.ReconciliationCode)
        cd.startDate                          = toStr(node.StartDate)
        cd.sesTraceNumber                     = toStr(node.TraceNumber)
        cd.sesInternationalBankAccountNumber  = toStr(node.InternationalBankAccountNumber)
        cd.sesBankIdentifierCode              = toStr(node.BankIdentifierCode)
        cd.sesCreditorID                      = toStr(node.CreditorID)
        cd.sesMandateID                       = toStr(node.MandateID)
        cd.sesPrenotificationText             = toStr(node.PrenotificationText)
        return cd
    }

    // =========================================================================
    // Check
    // =========================================================================

    private static Check mapCheck(def node) {
        def check = new Check()
        check.bankID          = toStr(node.BankID)
        check.accountNumber   = toStr(node.AccountNumber)
        check.checkCardNumber = toStr(node.CheckCardNumber)
        check.fullMICR        = toStr(node.FullMICR)
        check.sesBankIdentifierCode            = toStr(node.BankIdentifierCode)
        check.sesInternationalBankAccountNumber = toStr(node.InternationalBankAccountNumber)
        check.sesCheckNumber                   = toStr(node.CheckNumber)
        mapCustomFields(check.customFields, node, 5)
        return check
    }

    // =========================================================================
    // ForeignCurrency
    // =========================================================================

    private static ForeignCurrency mapForeignCurrency(def node) {
        def fc = new ForeignCurrency()
        fc.currencyCode       = toStr(node.CurrencyCode)
        fc.originalFaceAmount = toDecimal(node.OriginalFaceAmount)
        fc.exchangeRate       = toDecimal(node.ExchangeRate)
        return fc
    }

    // =========================================================================
    // Coupon
    // =========================================================================

    private static Coupon mapCoupon(def node) {
        def coupon = new Coupon()
        def qtyStr = toStr(node.Quantity)
        coupon.quantity       = qtyStr ? qtyStr as Integer : 1
        coupon.primaryLabel   = toStr(node.PrimaryLabel)
        coupon.manufacturerID = toStr(node.ManufacturerID)
        coupon.promotionCode  = toStr(node.PromotionCode)
        coupon.sesCouponSingleAmount = toDecimal(node.CouponSingleAmount)
        coupon.sesTaxPercent         = toDecimal(node.TaxPercent)
        coupon.sesOrigin             = toStr(node.Origin)

        def refItemListNode = node.ReferenceItemList
        if (refItemListNode != null) {
            refItemListNode.forEach('ReferenceItem') { riNode ->
                def ri = new SesReferenceItem()
                ri.itemID          = toStr(riNode.ItemID)
                ri.itemQuantity    = toDecimal(riNode.ItemQuantity)
                ri.itemRebateShare = toDecimal(riNode.ItemRebateShare)
                def linkNode = riNode.ItemLink
                if (linkNode != null) {
                    ri.itemLink = linkNode.toString() ?: null
                    ri.itemLinkType = linkNode._attributes?.LinkType ?: 'Coupon'
                }
                coupon.sesReferenceItemList << ri
            }
        }
        return coupon
    }

    // =========================================================================
    // Voucher
    // =========================================================================

    private static Voucher mapVoucher(def node) {
        def voucher = new Voucher()
        def attrs = node._attributes
        if (attrs) voucher.typeCode = attrs.TypeCode ?: null
        voucher.serialNumber = toStr(node.SerialNumber)
        return voucher
    }

    // =========================================================================
    // GiftCertificate / SesVoucher (within LoyaltyReward)
    // =========================================================================

    private static GiftCertificate mapGiftCertificate(def node) {
        def gc = new GiftCertificate()
        def attrs = node._attributes
        if (attrs) gc.mediaType = attrs.MediaType ?: null
        gc.serialNumber      = toStr(node.SerialNumber)
        gc.faceValue         = toDecimal(node.FaceValue)
        gc.giftCertificateID = toStr(node.GiftCertificateID) ?: ''
        mapCustomFields(gc.customFields, node, 5)
        return gc
    }

    private static SesVoucher mapSesVoucher(def node) {
        def voucher = new SesVoucher()
        voucher.sesVoucherID = toStr(node.VoucherID)
        voucher.sesAmount    = toDecimal(node.Amount)

        def couponSerialNode = node.CouponSerial
        if (couponSerialNode != null) {
            def cs = new SesCouponSerial()
            cs.sesCouponSerialNumber        = toStr(couponSerialNode.CouponSerialNumber)
            cs.sesBookingSuccessfulTypeCode = toStr(couponSerialNode.BookingSuccessfulTypeCode)
            cs.sesBookingTransactionUUID    = toStr(couponSerialNode.BookingTransactionUUID)
            voucher.sesCouponSerial = cs
        }
        return voucher
    }

    // =========================================================================
    // LoyaltyReward (in LineItem)
    // =========================================================================

    private static LoyaltyReward mapLoyaltyReward(def node) {
        def lr = new LoyaltyReward()
        lr.promotionID     = toStr(node.PromotionID)
        lr.reasonCode      = toStr(node.ReasonCode)
        lr.pointsAwarded   = toDecimal(node.PointsAwarded)
        lr.sequenceNumber  = toStr(node.SequenceNumber)
        lr.sesRebateMethod = toStr(node.RebateMethod)
        lr.sesRebateID     = toStr(node.RebateID)
        lr.sesExternalOfferID = toStr(node.ExternalOfferID)

        def gcNode = node.GiftCertificate
        if (gcNode != null) lr.giftCertificate = mapGiftCertificate(gcNode)

        def voucherNode = node.Voucher
        if (voucherNode != null) lr.sesVoucher = mapSesVoucher(voucherNode)

        def pdrNode = node.PriceDerivationRule
        if (pdrNode != null) lr.sesPriceDerivationRule = mapPriceDerivationRule(pdrNode)

        mapCustomFields(lr.customFields, node, 5)
        return lr
    }

    // =========================================================================
    // Rounding (SES variant)
    // =========================================================================

    private static SesRounding mapSesRounding(def node) {
        def sr = new SesRounding()
        sr.sesRoundingTypeCode  = toStr(node.RoundingTypeCode)
        sr.sesAmount            = toDecimal(node.Amount)
        sr.sesRoundingDirection = toStr(node.RoundingDirection)
        mapCustomFields(sr.customFields, node, 5)
        return sr
    }

    // =========================================================================
    // Voids
    // =========================================================================

    private static Voids mapVoids(def node) {
        def voids = new Voids()
        def itemLinkNode = node.ItemLink
        if (itemLinkNode != null) {
            def il = new ItemLink()
            def ilAttrs = itemLinkNode._attributes
            if (ilAttrs) il.reasonCode = ilAttrs.ReasonCode ?: 'Voided'
            il.sequenceNumber         = toStr(itemLinkNode.SequenceNumber)
            il.lineItemSequenceNumber = toStr(itemLinkNode.LineItemSequenceNumber)
            il.sesVoidReasonCode      = toStr(itemLinkNode.VoidReasonCode)
            def vrAttrs = itemLinkNode.VoidReasonCode?._attributes
            if (vrAttrs) {
                il.sesVoidReasonType        = vrAttrs.ReasonType    ?: null
                il.sesVoidReasonDescription = vrAttrs.Description   ?: null
            }
            mapCustomFields(il.customFields, itemLinkNode, 5)
            voids.itemLink = il
        }
        return voids
    }

    // =========================================================================
    // Addon (header/transaction level)
    // =========================================================================

    private static SesOperatorBypassApproval mapOperatorBypassApproval(def node) {
        def approval = new SesOperatorBypassApproval()
        def attrs = node._attributes
        if (attrs) {
            approval.approvalTypeCode = attrs.ApprovalTypeCode ?: null
        }
        approval.approvalCode = toStr(node.ApprovalCode)
        def approverIDNode = node.ApproverID
        if (approverIDNode != null) {
            approval.approverID   = approverIDNode.toString() ?: null
            def apAttrs = approverIDNode._attributes
            if (apAttrs) {
                approval.workerID     = apAttrs.WorkerID     ?: null
                approval.approverName = apAttrs.ApproverName ?: null
            }
        }
        mapCustomFields(approval.customFields, node, 5)
        return approval
    }

    private static Addon mapAddon(def addonNode) {
        def addon = new Addon()
        addon.addonPos = toStr(addonNode.AddonPos)
        addon.key      = toStr(addonNode.Key)
        addon.value    = toStr(addonNode.Value)
        mapCustomFields(addon.customFields, addonNode, 5)
        return addon
    }

    // =========================================================================
    // SesReceiptPositionAddon (line item level)
    // =========================================================================

    private static SesReceiptPositionAddon mapPositionAddon(def addonNode) {
        def addon = new SesReceiptPositionAddon()
        addon.addonPos = toStr(addonNode.AddonPos)
        addon.key      = toStr(addonNode.Key)
        addon.value    = toStr(addonNode.Value)
        mapCustomFields(addon.customFields, addonNode, 5)
        return addon
    }

    // =========================================================================
    // Timer
    // =========================================================================

    private static Timer mapTimer(def timerNode) {
        def timer = new Timer()
        timer.timerID        = toStr(timerNode.TimerID)
        timer.startTimestamp = toStr(timerNode.StartTimestamp)
        timer.duration       = toDecimal(timerNode.Duration)
        return timer
    }

    // =========================================================================
    // BinaryData
    // =========================================================================

    private static BinaryData mapBinaryData(def bdNode) {
        def bd = new BinaryData()
        bd.name    = toStr(bdNode.Name)
        bd.content = toStr(bdNode.Content)
        mapCustomFields(bd.customFields, bdNode, 5)
        return bd
    }

    // =========================================================================
    // TransactionLink
    // =========================================================================

    private static TransactionLink mapTransactionLink(def node) {
        def tl = new TransactionLink()
        def attrs = node._attributes
        if (attrs) tl.reasonCode = attrs.ReasonCode ?: null
        tl.retailStoreID            = toStr(node.RetailStoreID)
        tl.workstationID            = toStr(node.WorkstationID)
        tl.sequenceNumber           = toStr(node.SequenceNumber)
        tl.lineItemSequenceNumber   = toStr(node.LineItemSequenceNumber)
        tl.businessDayDate          = toStr(node.BusinessDayDate)
        tl.beginDateTime            = toStr(node.BeginDateTime)
        tl.endDateTime              = toStr(node.EndDateTime)
        tl.sesReceiptDateTime       = toStr(node.ReceiptDateTime)
        tl.sesInternalTransactionID = toStr(node.InternalTransactionID)
        return tl
    }

    // =========================================================================
    // Customer
    // =========================================================================

    private static Customer mapCustomer(def node) {
        def customer = new Customer()
        customer.customerID               = toStr(node.CustomerID)
        customer.birthdate                = toStr(node.Birthdate)
        customer.gender                   = toStr(node.Gender)
        customer.sesCustomerType          = toStr(node.CustomerType)
        customer.sesCustomerTypeDescription = toStr(node.CustomerTypeDescription)
        customer.sesCustomerIdentifier    = toStr(node.CustomerIdentifier)
        customer.sesEntryMethod           = toStr(node.EntryMethod)
        customer.sesCustomerTaxID         = toStr(node.CustomerTaxID)
        customer.sesCustomerGenericFlag   = toStr(node.CustomerGenericFlag)
        customer.sesCustomerBusinessNumber = toStr(node.CustomerBusinessNumber)
        customer.sesCustomerLocationNumber = toStr(node.CustomerLocationNumber)
        customer.sesCustomerGroup         = toStr(node.CustomerGroup)
        customer.sesCustomerBuyerName     = toStr(node.CustomerBuyerName)
        customer.sesCustomerContactPersonName = toStr(node.CustomerContactPersonName)
        customer.sesParentCustomerID      = toStr(node.ParentCustomerID)
        customer.sesParentCustomerName1   = toStr(node.ParentCustomerName1)
        customer.sesParentCustomerName2   = toStr(node.ParentCustomerName2)
        customer.sesCustomerLockingTypeCode = toStr(node.CustomerLockingTypeCode)
        customer.sesCustomerRequisitionRequired = toBool(node.CustomerRequisitionRequired)
        customer.sesCustomerBuyerRequiredFlag   = toBool(node.CustomerBuyerRequired)
        customer.sesCustomerContactRequiredFlag = toBool(node.CustomerContactRequired)

        def grpDefaultNode = node.CustomerGroupDefaultBonusPointsCount
        if (grpDefaultNode != null) {
            def s = grpDefaultNode.toString()
            if (s) customer.sesCustomerGroupDefaultBonusPointsCount = s as Integer
        }

        def nameNode = node.Name
        if (nameNode != null) {
            def cn = new CustomerName()
            cn.fullName = nameNode.toString() ?: null
            def salNode = node.Salutation
            if (salNode != null) cn.salutation = salNode.toString() ?: null
            nameNode.forEach('Name') { innerNameNode ->
                def loc = innerNameNode._attributes?.Location
                def val = innerNameNode.toString() ?: null
                if (loc == 'First') cn.firstName = val
                else if (loc == 'Last') cn.lastName = val
            }
            customer.customerName = cn
        }

        def addrNode = node.Address
        if (addrNode != null) {
            def addr = new Address()
            def addrLineNode = addrNode.AddressLine
            if (addrLineNode != null) {
                addr.addressLine = addrLineNode.toString() ?: null
                addr.addressLineTypeCode = addrLineNode._attributes?.TypeCode ?: null
            }
            addr.city       = toStr(addrNode.City)
            addr.postalCode = toStr(addrNode.PostalCode)
            addr.country    = toStr(addrNode.Country)
            addr.name       = toStr(addrNode.Name)
            customer.address = addr
        }

        def teleNode = node.Telephone
        if (teleNode != null) {
            def tele = new Telephone()
            tele.typeCode            = teleNode._attributes?.TypeCode ?: null
            tele.fullTelephoneNumber = toStr(teleNode.FullTelephoneNumber)
            customer.telephone = tele
        }

        def emailNode = node.Email
        if (emailNode != null) {
            customer.email = new Email(emailAddress: toStr(emailNode.EmailAddress))
        }

        mapCustomFields(customer.customFields, node, 5)
        return customer
    }

    // =========================================================================
    // SesCouponSummary
    // =========================================================================

    private static SesCouponSummary mapCouponSummary(def node) {
        def cs = new SesCouponSummary()
        cs.sesCouponNumber            = toStr(node.CouponNumber)
        cs.sesCustomerID              = toStr(node.CustomerID)
        cs.sesCustomerAddressTypeCode = toStr(node.CustomerAddressTypeCode)

        def inputCntStr = toStr(node.InputCount)
        if (inputCntStr) cs.sesInputCount = inputCntStr as Integer

        def appliedCntStr = toStr(node.AppliedCount)
        if (appliedCntStr) cs.sesAppliedCount = appliedCntStr as Integer

        node.forEach('CouponSerialSummary') { cssNode ->
            def css = new SesCouponSerialSummary()
            css.sesCouponSerialNumber        = toStr(cssNode.CouponSerialNumber)
            css.sesBookingSuccessfulTypeCode = toStr(cssNode.BookingSuccessfulTypeCode)
            css.sesBookingTransactionUUID    = toStr(cssNode.BookingTransactionUUID)
            css.sesUsedFlag                  = toBool(cssNode.UsedFlag)
            mapCustomFields(css.customFields, cssNode, 30)
            cs.sesCouponSerialSummaries << css
        }

        mapCustomFields(cs.customFields, node, 5)
        return cs
    }

    // =========================================================================
    // LoyaltyAccount (transaction level)
    // =========================================================================

    private static SesLoyaltyAccount mapLoyaltyAccount(def node) {
        def la = new SesLoyaltyAccount()
        la.customerID = toStr(node.CustomerID)
        node.forEach('LoyaltyProgram') { lpNode ->
            def lp = new LoyaltyProgram()
            lp.loyaltyAccountID            = toStr(lpNode.LoyaltyAccountID)
            lp.effectiveDate               = toStr(lpNode.EffectiveDate)
            lp.loyaltyAccountTypeCode      = toStr(lpNode.LoyaltyAccountTypeCode)
            lp.loyaltyAccountTransactionID = toStr(lpNode.LoyaltyAccountTransactionID)
            def pointsNode = lpNode.Points
            if (pointsNode != null) {
                lp.points      = pointsNode.toString() ?: null
                lp.pointsType  = pointsNode._attributes?.Type ?: null
            }
            mapCustomFields(lp.customFields, lpNode, 5)
            la.loyaltyPrograms << lp
        }
        return la
    }

    // =========================================================================
    // TenderControlTransaction
    // =========================================================================

    private static TenderControlTransaction mapTenderControlTransaction(def node) {
        def tct = new TenderControlTransaction()

        tct.sesAccountedOperatorID   = toStr(node.AccountedOperatorID)
        tct.sesAccountedOperatorName = node.AccountedOperatorID?._attributes?.Name ?: null

        def tenderLoanNode = node.TenderLoan
        if (tenderLoanNode != null) {
            def tl = new SesTenderLoan()
            def tlAttrs = tenderLoanNode._attributes
            if (tlAttrs) {
                tl.tenderType        = tlAttrs.TenderType        ?: null
                tl.tenderDescription = tlAttrs.TenderDescription ?: null
            }
            def totalsNode = tenderLoanNode.Totals
            if (totalsNode != null) tl.sesTotals = mapSesTotals(totalsNode)
            mapCustomFields(tl.customFields, tenderLoanNode, 5)
            tct.sesTenderLoan = tl
        }

        def depositNode = node.Deposit
        if (depositNode != null) {
            def dep = new Deposit()
            dep.bank      = toStr(depositNode.Bank)      ?: ''
            dep.account   = toStr(depositNode.Account)   ?: ''
            dep.bagID     = toStr(depositNode.BagID)
            dep.amount    = toDecimal(depositNode.Amount)
            def depAmtAttrs = depositNode.Amount?._attributes
            if (depAmtAttrs) {
                dep.amountCurrency = depAmtAttrs.Currency      ?: null
                dep.foreignAmount  = depAmtAttrs.ForeignAmount != null ? depAmtAttrs.ForeignAmount as BigDecimal : null
            }
            def depAttrs = depositNode._attributes
            if (depAttrs) dep.depositorWorkerID = depAttrs.WorkerID ?: null
            def depDetailListNode = depositNode.DepositDetailList
            if (depDetailListNode != null) {
                depDetailListNode.forEach('DepositDetail') { ddNode ->
                    def dd = new DepositDetail()
                    def ttNode = ddNode.TenderTotal
                    if (ttNode != null) {
                        dd.tenderTotal = toDecimal(ttNode)
                        dd.tenderType  = ttNode._attributes?.TenderType ?: null
                    }
                    dd.reason = toStr(ddNode.Reason) ?: '0000'
                    dep.depositDetails << dd
                }
            }
            mapCustomFields(dep.customFields, depositNode, 5)
            tct.deposit = dep
        }

        def tillSettleNode = node.TillSettle
        if (tillSettleNode != null) {
            def ts = new TillSettle()
            ts.sesTransactionCategoryCode = toStr(tillSettleNode.TransactionCategoryCode)
            def tsSummaryListNode = tillSettleNode.TenderSummaryList
            if (tsSummaryListNode != null) {
                tsSummaryListNode.forEach('TenderSummary') { tsSummaryNode ->
                    ts.tenderSummaries << mapTenderSummary(tsSummaryNode)
                }
            }
            mapCustomFields(ts.customFields, tillSettleNode, 5)
            tct.tillSettle = ts
        }

        def safeDropNode = node.SafeDrop
        if (safeDropNode != null) {
            def sd = new SafeDrop()
            sd.dropAmount  = toDecimal(safeDropNode.DropAmount)
            sd.envelopeID  = toStr(safeDropNode.EnvelopeID)  ?: ''
            sd.dropNumber  = toStr(safeDropNode.DropNumber)   ?: ''
            tct.safeDrop = sd
        }

        def tenderPickupNode = node.TenderPickup
        if (tenderPickupNode != null) {
            def tp = new SesTenderPickup()
            tp.sesTotalAmount         = toDecimal(tenderPickupNode.TotalAmount)
            tp.sesTotalAmountTypeCode = tenderPickupNode.TotalAmount?._attributes?.TypeCode ?: null
            tp.sesEnvelopeID          = toStr(tenderPickupNode.EnvelopeID)
            def tpaListNode = tenderPickupNode.TenderAmountList
            if (tpaListNode != null) {
                tpaListNode.forEach('TenderAmount') { taNode ->
                    def ta = new SesTenderAmount()
                    ta.amount    = toDecimal(taNode.Amount)
                    def taAttrs = taNode._attributes
                    if (taAttrs) {
                        ta.tenderType        = taAttrs.TenderType        ?: null
                        ta.typeCode          = taAttrs.TypeCode          ?: null
                        ta.currency          = taAttrs.Currency          ?: null
                        ta.tenderDescription = taAttrs.TenderDescription ?: null
                    }
                    def taAmtAttrs = taNode.Amount?._attributes
                    if (taAmtAttrs) ta.foreignAmount = taAmtAttrs.ForeignAmount != null ? taAmtAttrs.ForeignAmount as BigDecimal : null
                    mapCustomFields(ta.customFields, taNode, 5)
                    tp.sesTenderAmounts << ta
                }
            }
            mapCustomFields(tp.customFields, tenderPickupNode, 5)
            tct.sesTenderPickup = tp
        }

        def paidInNode = node.PaidIn
        if (paidInNode != null) {
            def pi = new SesPaidIn()
            pi.sesAmount                  = toDecimal(paidInNode.Amount)
            pi.sesReason                  = toStr(paidInNode.Reason)
            pi.sesTransactionCategoryCode = toStr(paidInNode.TransactionCategoryCode)
            paidInNode.forEach('Tender') { tenderNode ->
                pi.sesTenders << mapTender(tenderNode)
            }
            mapCustomFields(pi.customFields, paidInNode, 5)
            tct.sesPaidIn = pi
        }

        def paidOutNode = node.PaidOut
        if (paidOutNode != null) {
            def po = new SesPaidOut()
            po.amount                    = toDecimal(paidOutNode.Amount)
            po.reason                    = toStr(paidOutNode.Reason)
            po.sesTransactionCategoryCode = toStr(paidOutNode.TransactionCategoryCode)
            paidOutNode.forEach('Tender') { tenderNode ->
                po.tenders << mapTender(tenderNode)
            }
            mapCustomFields(po.customFields, paidOutNode, 5)
            tct.sesPaidOut = po
        }

        def tenderLoanCarriedForwardNode = node.TenderLoanCarriedForward
        if (tenderLoanCarriedForwardNode != null) {
            def tlcf = new SesTenderLoanCarriedForward()
            tlcf.amount = toDecimal(tenderLoanCarriedForwardNode.Amount)
            mapCustomFields(tlcf.customFields, tenderLoanCarriedForwardNode, 5)
            tct.sesTenderLoanCarriedForward = tlcf
        }

        def safeSettleNode = node.SafeSettle
        if (safeSettleNode != null) {
            def ss = new SesSafeSettle()
            def ssSummaryListNode = safeSettleNode.TenderSummaryList
            if (ssSummaryListNode != null) {
                ssSummaryListNode.forEach('TenderSummary') { tsSummaryNode ->
                    ss.tenderSummaries << mapTenderSummary(tsSummaryNode)
                }
            }
            mapCustomFields(ss.customFields, safeSettleNode, 5)
            tct.sesSafeSettle = ss
        }

        def txLinkNode = node.TransactionLink
        if (txLinkNode != null) {
            tct.sesTransactionLink = mapTransactionLink(txLinkNode)
        }

        def addonListNode = node.ReceiptPositionAddonList
        if (addonListNode != null) {
            addonListNode.forEach('Addon') { addonNode ->
                tct.sesReceiptPositionAddonList << mapPositionAddon(addonNode)
            }
        }

        mapCustomFields(tct.customFields, node, 5)
        return tct
    }

    private static SesTotals mapSesTotals(def node) {
        def totals = new SesTotals()
        totals.amount          = toDecimal(node.Amount)
        def amtAttrs = node.Amount?._attributes
        if (amtAttrs) {
            totals.amountCurrency = amtAttrs.Currency      ?: null
            totals.foreignAmount  = amtAttrs.ForeignAmount != null ? amtAttrs.ForeignAmount as BigDecimal : null
        }
        def reasonNode = node.Reason
        if (reasonNode != null) {
            totals.reason            = reasonNode.toString() ?: null
            def rAttrs = reasonNode._attributes
            if (rAttrs) {
                totals.reasonDescription = rAttrs.Description ?: null
                totals.reasonName        = rAttrs.Name        ?: null
            }
        }
        mapCustomFields(totals.customFields, node, 5)
        return totals
    }

    private static TenderSummary mapTenderSummary(def node) {
        def ts = new TenderSummary()

        def overNode = node.Over
        if (overNode != null) {
            def over = new Over()
            def oAttrs = overNode._attributes
            if (oAttrs) over.tenderType = oAttrs.TenderType ?: null
            over.amount = toDecimal(overNode.Amount)
            def oAmtAttrs = overNode.Amount?._attributes
            if (oAmtAttrs) {
                over.amountCurrency = oAmtAttrs.Currency      ?: null
                over.foreignAmount  = oAmtAttrs.ForeignAmount != null ? oAmtAttrs.ForeignAmount as BigDecimal : null
            }
            def oCountStr = toStr(overNode.Count)
            if (oCountStr) over.count = oCountStr as Integer
            ts.over = over
        }

        def shortNode = node.Short
        if (shortNode != null) {
            def shortSum = new ShortSummary()
            def sAttrs = shortNode._attributes
            if (sAttrs) shortSum.tenderType = sAttrs.TenderType ?: null
            shortSum.amount = toDecimal(shortNode.Amount)
            def sAmtAttrs = shortNode.Amount?._attributes
            if (sAmtAttrs) {
                shortSum.amountCurrency = sAmtAttrs.Currency      ?: null
                shortSum.foreignAmount  = sAmtAttrs.ForeignAmount != null ? sAmtAttrs.ForeignAmount as BigDecimal : null
            }
            def sCountStr = toStr(shortNode.Count)
            if (sCountStr) shortSum.count = sCountStr as Integer
            ts.shortSummary = shortSum
        }

        def nominalNode = node.Nominal
        if (nominalNode != null) {
            def nom = new SesNominal()
            def nAttrs = nominalNode._attributes
            if (nAttrs) {
                nom.tenderType = nAttrs.TenderType ?: null
                nom.typeCode   = nAttrs.TypeCode   ?: null
            }
            nom.amount = toDecimal(nominalNode.Amount)
            def nAmtAttrs = nominalNode.Amount?._attributes
            if (nAmtAttrs) {
                nom.amountCurrency = nAmtAttrs.Currency      ?: null
                nom.foreignAmount  = nAmtAttrs.ForeignAmount != null ? nAmtAttrs.ForeignAmount as BigDecimal : null
            }
            def nCountStr = toStr(nominalNode.Count)
            if (nCountStr) nom.count = nCountStr as Integer
            ts.sesNominal = nom
        }

        def endingNode = node.Ending
        if (endingNode != null) {
            def ending = new SesEnding()
            def eAttrs = endingNode._attributes
            if (eAttrs) {
                ending.tenderType = eAttrs.TenderType ?: null
                ending.typeCode   = eAttrs.TypeCode   ?: null
            }
            ending.amount = toDecimal(endingNode.Amount)
            def eAmtAttrs = endingNode.Amount?._attributes
            if (eAmtAttrs) {
                ending.amountCurrency = eAmtAttrs.Currency      ?: null
                ending.foreignAmount  = eAmtAttrs.ForeignAmount != null ? eAmtAttrs.ForeignAmount as BigDecimal : null
            }
            def eCountStr = toStr(endingNode.Count)
            if (eCountStr) ending.count = eCountStr as Integer
            ts.sesEnding = ending
        }

        mapCustomFields(ts.customFields, node, 5)
        return ts
    }

    // =========================================================================
    // ControlTransaction
    // =========================================================================

    private static ControlTransaction mapControlTransaction(def node) {
        def ct = new ControlTransaction()
        ct.reasonCode                  = toStr(node.ReasonCode)
        ct.sesOtherTransactionType     = toStr(node.OtherTransactionType)
        ct.sesForcedSignOffFlag        = toBool(node.ForcedSignOffFlag)
        ct.sesForcedSignOffWorkstationID = toStr(node.ForcedSignOffWorkstationID)
        ct.sesAccountedOperatorID      = toStr(node.AccountedOperatorID)
        ct.sesAccountedOperatorName    = node.AccountedOperatorID?._attributes?.Name ?: null
        ct.noSaleTimestamp             = toStr(node.NoSaleTimestamp)

        def businessEODNode = node.BusinessEOD
        if (businessEODNode != null) {
            ct.businessEOD = new BusinessEOD(startDateTimestamp: toStr(businessEODNode.StartDateTimestamp))
        }

        def tillEODNode = node.TillEOD
        if (tillEODNode != null) {
            ct.tillEOD = mapTillEOD(tillEODNode)
        }

        def signOnNode = node.OperatorSignOn
        if (signOnNode != null) {
            ct.operatorSignOn = new OperatorSignOn(startDateTimestamp: toStr(signOnNode.StartDateTimestamp))
        }

        def signOffNode = node.OperatorSignOff
        if (signOffNode != null) {
            ct.operatorSignOff = new OperatorSignOff(startDateTimestamp: toStr(signOffNode.StartDateTimestamp))
        }

        def taxRefundNode = node.TaxRefund
        if (taxRefundNode != null) {
            def tr = new SesTaxRefund()
            def trAttrs = taxRefundNode._attributes
            if (trAttrs) tr.typeCode = trAttrs.TypeCode ?: null
            tr.sesTaxRefundDocumentID  = toStr(taxRefundNode.TaxRefundDocumentID)
            tr.sesExternalTransactionID = toStr(taxRefundNode.ExternalTransactionID)
            tr.sesStoreInvoiceID       = toStr(taxRefundNode.StoreInvoiceID)
            tr.sesTotalGrossAmount     = toDecimal(taxRefundNode.TotalGrossAmount)
            tr.sesTotalTaxAmount       = toDecimal(taxRefundNode.TotalTaxAmount)
            tr.sesTotalRefundAmount    = toDecimal(taxRefundNode.TotalRefundAmount)
            mapCustomFields(tr.customFields, taxRefundNode, 5)
            ct.sesTaxRefund = tr
        }

        node.forEach('TransactionLink') { txLinkNode ->
            ct.sesTransactionLinks << mapTransactionLink(txLinkNode)
        }

        def cashierStatisticsNode = node.CashierStatistics
        if (cashierStatisticsNode != null) {
            ct.sesCashierStatistics = mapCashierStatistics(cashierStatisticsNode)
        }

        def storeEODSummaryNode = node.StoreEODSummary
        if (storeEODSummaryNode != null) {
            ct.sesStoreEODSummary = mapStoreEODSummary(storeEODSummaryNode)
        }

        mapCustomFields(ct.customFields, node, 5)
        return ct
    }

    private static TillEOD mapTillEOD(def node) {
        def tillEOD = new TillEOD()
        def sessionSettleNode = node.SessionSettle
        if (sessionSettleNode != null) {
            def ss = new TillEODSessionSettle()
            sessionSettleNode.forEach('TenderSummary') { tsSummaryNode ->
                def tts = new TillEODTenderSummary()
                def beginningNode = tsSummaryNode.Beginning
                if (beginningNode != null) {
                    def beg = new Beginning()
                    def bAttrs = beginningNode._attributes
                    if (bAttrs) beg.tenderType = bAttrs.TenderType ?: null
                    beg.amount = toDecimal(beginningNode.Amount)
                    def bCountStr = toStr(beginningNode.Count)
                    if (bCountStr) beg.count = bCountStr as Integer
                    tts.beginning = beg
                }
                // Pickup, Over, Short, Nominal, Ending follow the same pattern;
                // map them via the generic TenderSummary mapper and copy across
                def genericTs = mapTenderSummary(tsSummaryNode)
                tts.over         = genericTs.over
                tts.shortEntry   = genericTs.shortSummary
                tts.sesNominal   = genericTs.sesNominal
                tts.sesEnding    = genericTs.sesEnding

                def pickupNode = tsSummaryNode.Pickup
                if (pickupNode != null) {
                    def pu = new Pickup()
                    def puAttrs = pickupNode._attributes
                    if (puAttrs) pu.tenderType = puAttrs.TenderType ?: null
                    pu.amount = toDecimal(pickupNode.Amount)
                    def puCountStr = toStr(pickupNode.Count)
                    if (puCountStr) pu.count = puCountStr as Integer
                    tts.pickup = pu
                }

                def salesTenderNominalNode = tsSummaryNode.SalesTenderNominal
                if (salesTenderNominalNode != null) {
                    def stn = new SesSalesTenderNominal()
                    def stnAttrs = salesTenderNominalNode._attributes
                    if (stnAttrs) {
                        stn.tenderType = stnAttrs.TenderType ?: null
                        stn.typeCode   = stnAttrs.TypeCode   ?: null
                    }
                    stn.amount = toDecimal(salesTenderNominalNode.Amount)
                    tts.sesSalesTenderNominal = stn
                }

                ss.tenderSummaries << tts
            }

            sessionSettleNode.forEach('SalesSummary') { salesSumNode ->
                def salesSum = new SesSalesSummary()
                salesSum.typeCode = salesSumNode._attributes?.TypeCode ?: null
                salesSum.amount   = toDecimal(salesSumNode.Amount)
                def cntStr = toStr(salesSumNode.Count)
                if (cntStr) salesSum.count = cntStr as Integer
                def reasonNode = salesSumNode.Reason
                if (reasonNode != null) {
                    salesSum.reason     = reasonNode.toString() ?: null
                    salesSum.reasonName = reasonNode._attributes?.Name ?: 'SalesID'
                }
                ss.sesSalesSummaries << salesSum
            }

            sessionSettleNode.forEach('TaxSummary') { taxSumNode ->
                def taxSum = new SesTaxSummary()
                taxSum.tenderType      = taxSumNode._attributes?.TenderType ?: null
                taxSum.amount          = toDecimal(taxSumNode.Amount)
                def cntStr = toStr(taxSumNode.Count)
                if (cntStr) taxSum.count = cntStr as Integer
                taxSum.sesTaxAuthorityID = toStr(taxSumNode.TaxAuthorityID)
                taxSum.sesTaxGroupID     = toStr(taxSumNode.TaxGroupID)
                ss.sesTaxSummaries << taxSum
            }

            tillEOD.sessionSettle = ss
        }
        return tillEOD
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private static SesCashierStatistics mapCashierStatistics(def node) {
        def cs = new SesCashierStatistics()
        def sessionSettleNode = node.SessionSettle
        if (sessionSettleNode != null) {
            def ss = new CashierSessionSettle()
            def txCntStr = toStr(sessionSettleNode.TransactionCount)
            if (txCntStr) ss.transactionCount = txCntStr as Integer

            def totalMeasuresNode = sessionSettleNode.TotalMeasures
            if (totalMeasuresNode != null) {
                def tm = new TotalMeasures()
                def noSaleCntStr = toStr(totalMeasuresNode.NoSaleTransactionCount)
                if (noSaleCntStr) tm.noSaleTransactionCount = noSaleCntStr as Integer
                def scannedCntStr = toStr(totalMeasuresNode.LineItemScannedCount)
                if (scannedCntStr) tm.lineItemScannedCount = scannedCntStr as Integer
                def openDeptCntStr = toStr(totalMeasuresNode.LineItemOpenDepartmentCount)
                if (openDeptCntStr) tm.lineItemOpenDepartmentCount = openDeptCntStr as Integer
                tm.sesLogonTime       = toStr(totalMeasuresNode.LogonTime)
                tm.sesRegistrationTime = toStr(totalMeasuresNode.RegistrationTime)
                tm.sesTenderTime      = toStr(totalMeasuresNode.TenderTime)
                ss.totalMeasures = tm
            }

            def liVoidsNode = sessionSettleNode.LineItemVoids
            if (liVoidsNode != null) {
                def liv = new LineItemVoids()
                liv.amount = toDecimal(liVoidsNode.Amount)
                def cntStr = toStr(liVoidsNode.Count)
                if (cntStr) liv.count = cntStr as Integer
                ss.lineItemVoids = liv
            }

            def ptVoidsNode = sessionSettleNode.PostTransactionVoids
            if (ptVoidsNode != null) {
                def ptv = new PostTransactionVoids()
                ptv.amount = toDecimal(ptVoidsNode.Amount)
                def cntStr = toStr(ptVoidsNode.Count)
                if (cntStr) ptv.count = cntStr as Integer
                ss.postTransactionVoids = ptv
            }

            def txCancNode = sessionSettleNode.TransactionCancellations
            if (txCancNode != null) {
                def tc = new SesTransactionCancellations()
                tc.amount = toDecimal(txCancNode.Amount)
                def cntStr = toStr(txCancNode.Count)
                if (cntStr) tc.count = cntStr as Integer
                ss.sesTransactionCancellations = tc
            }

            def directVoidsNode = sessionSettleNode.DirectLineItemVoids
            if (directVoidsNode != null) {
                def dliv = new SesDirectLineItemVoids()
                dliv.amount = toDecimal(directVoidsNode.Amount)
                def cntStr = toStr(directVoidsNode.Count)
                if (cntStr) dliv.count = cntStr as Integer
                ss.sesDirectLineItemVoids = dliv
            }

            ss.sesAccountedOperatorID   = toStr(sessionSettleNode.AccountedOperatorID)
            ss.sesAccountedOperatorName = sessionSettleNode.AccountedOperatorID?._attributes?.Name ?: null
            cs.sessionSettle = ss
        }
        return cs
    }

    private static SesStoreEODSummary mapStoreEODSummary(def node) {
        def storeEOD = new SesStoreEODSummary()
        def sessionSettleNode = node.SessionSettle
        if (sessionSettleNode != null) {
            def ss = new StoreEODSessionSettle()
            sessionSettleNode.forEach('SalesSummary') { salesSumNode ->
                def salesSum = new SesSalesSummary()
                salesSum.typeCode = salesSumNode._attributes?.TypeCode ?: null
                salesSum.amount   = toDecimal(salesSumNode.Amount)
                def cntStr = toStr(salesSumNode.Count)
                if (cntStr) salesSum.count = cntStr as Integer
                def reasonNode = salesSumNode.Reason
                if (reasonNode != null) {
                    salesSum.reason     = reasonNode.toString() ?: null
                    salesSum.reasonName = reasonNode._attributes?.Name ?: 'SalesID'
                }
                ss.sesSalesSummaries << salesSum
            }
            sessionSettleNode.forEach('TaxSummary') { taxSumNode ->
                def taxSum = new SesTaxSummary()
                taxSum.tenderType        = taxSumNode._attributes?.TenderType ?: null
                taxSum.amount            = toDecimal(taxSumNode.Amount)
                def cntStr = toStr(taxSumNode.Count)
                if (cntStr) taxSum.count = cntStr as Integer
                taxSum.sesTaxAuthorityID = toStr(taxSumNode.TaxAuthorityID)
                taxSum.sesTaxGroupID     = toStr(taxSumNode.TaxGroupID)
                ss.sesTaxSummaries << taxSum
            }
            storeEOD.sessionSettle = ss
        }
        return storeEOD
    }

    /**
     * Safely converts a node to a non-empty String, or returns null.
     */
    private static String toStr(def node) {
        if (node == null) return null
        def s = node.toString()
        return (s != null && !s.isEmpty()) ? s : null
    }

    /**
     * Safely converts a node's text value to Boolean.
     * Returns null when the node is absent or its text is empty.
     */
    private static Boolean toBool(def node) {
        if (node == null) return null
        def s = node.toString()
        if (s == null || s.isEmpty()) return null
        return s == 'true'
    }

    /**
     * Safely converts a node's text value to BigDecimal.
     * Returns null when the node is absent or its text is empty.
     */
    private static BigDecimal toDecimal(def node) {
        if (node == null) return null
        def s = node.toString()
        if (s == null || s.isEmpty()) return null
        return s as BigDecimal
    }

    /**
     * Reads custom fields CST:XXCustom01 … CST:XXCustomXX from a node and
     * populates the provided map.
     *
     * @param target  the customFields map to populate
     * @param node    the parent node to read from
     * @param count   the highest custom-field index to check (e.g. 5, 15, 30)
     */
    private static void mapCustomFields(Map<String, String> target, def node, int count) {
        for (int i = 1; i <= count; i++) {
            def fieldName = i < 10 ? "XXCustom0${i}" : "XXCustom${i}"
            def fieldNode = node."${fieldName}"
            if (fieldNode != null) {
                def val = fieldNode.toString()
                if (val != null) target[fieldName] = val
            }
        }
    }
}



/**
 * Represents a RetailTransaction in the POSLog export.
 * Corresponds to section 4.3.6 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class RetailTransaction {

    // ---- Attributes ----
    /**
     * Status of transaction.
     * Possible values: PostVoided, Suspended, Unknown, SES:ItemInfo, Replaced,
     *                  SES:Invoice, Finished, SES:LayawayPostVoided, SES:Ordered,
     *                  SES:Moved, SES:Scale, SES:ScalePostVoided
     */
    String transactionStatus

    // ---- XML elements ----
    /** Date and time of receipt (optional) */
    String receiptDateTime

    /** Line items and tender line items (optional) */
    List<LineItem> lineItems = []

    /** Total amount of the transaction (TotalType = TransactionGrandAmount, optional) */
    BigDecimal totalGrandAmount
    /** Total net amount of the transaction (TotalType = TransactionNetAmount, optional) */
    BigDecimal totalNetAmount
    /** Total tax amount of the transaction (TotalType = TransactionTaxAmount, optional) */
    BigDecimal totalTaxAmount
    /** CST:XXCustom attributes on Total elements (optional) */
    Map<String, String> totalCustomFields = [:]

    /** Customer data, created when the receipt includes customer data (optional) */
    Customer customer

    /** Transaction link (optional); created for voiding, invoice, offline rebooking, etc. */
    TransactionLink transactionLink

    /** "false" if total >= 0; "true" if total < 0 */
    Boolean sesNegativeTotalFlag
    /** "true" in case of amendment (optional) */
    Boolean sesAmendmentFlag
    /** "true" if sending receipt as e-mail is activated (optional) */
    Boolean sesEmailRequestedFlag
    /** E-mail address (optional) */
    String sesEmailAddress
    /** Invoice printout type code (optional) */
    String sesInvoicePrintoutTypeCode
    /** Number of the invoice, if invoice number was generated (optional) */
    String sesInvoiceNumber
    /** Coupon summary entries (optional) */
    List<SesCouponSummary> sesCouponSummaryList = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents Customer data within RetailTransaction.
 * Corresponds to section 4.3.6.3 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Customer {

    /** Customer ID (optional) */
    String customerID
    /** Customer name container (optional) */
    CustomerName customerName
    /** Customer address container (optional) */
    Address address
    /** Customer telephone container (optional) */
    Telephone telephone
    /** Customer email address container (optional) */
    Email email
    /** Customer birthdate (optional) */
    String birthdate
    /** Customer gender (optional) */
    String gender
    /**
     * Customer type.
     * Possible values: "EM" for employees, otherwise address type
     */
    String sesCustomerType
    /** Address type description (optional) */
    String sesCustomerTypeDescription
    /** Customer identifier, created only if address type exists (optional) */
    String sesCustomerIdentifier
    /**
     * Entry method (optional; not created for employee customers).
     * Possible values: Scanned, SES:Searched, Keyed
     */
    String sesEntryMethod
    /** Customer locking type code (optional) */
    String sesCustomerLockingTypeCode
    /** Customer tax ID (optional) */
    String sesCustomerTaxID
    /** Customer generic flag (optional) */
    String sesCustomerGenericFlag
    /** Customer business number (optional) */
    String sesCustomerBusinessNumber
    /** Customer location number (optional) */
    String sesCustomerLocationNumber
    /** Customer requisition required flag (optional, XML element: SES:CustomerRequisationRequired) */
    Boolean sesCustomerRequisitionRequired
    /** Customer buyer required flag (optional) */
    Boolean sesCustomerBuyerRequiredFlag
    /** Customer buyer name (optional) */
    String sesCustomerBuyerName
    /** Customer contact required flag (optional) */
    Boolean sesCustomerContactRequiredFlag
    /** Customer contact person name (optional) */
    String sesCustomerContactPersonName
    /** Customer group (optional) */
    String sesCustomerGroup
    /** Customer group default bonus points count (optional) */
    Integer sesCustomerGroupDefaultBonusPointsCount
    /** Parent customer ID (optional) */
    String sesParentCustomerID
    /** First name line of parent customer (optional) */
    String sesParentCustomerName1
    /** Second name line of parent customer (optional) */
    String sesParentCustomerName2

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents customer name data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class CustomerName {
    /** Salutation of customer (optional) */
    String salutation
    /** Customer first name (Location = "First", optional) */
    String firstName
    /** Customer last name (Location = "Last", optional) */
    String lastName
    /** Full name string – max. both customer name entries or organization name */
    String fullName
}

/**
 * Represents customer address data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Address {
    /**
     * Address line with TypeCode attribute (optional).
     * TypeCode possible values: Street, SES:District, SES:AdressLine1, SES:AdressLine2
     * (Note: 'AdressLine' is intentionally kept as-is to match the GK spec XML values.)
     */
    String addressLine
    /** TypeCode attribute on AddressLine (optional) */
    String addressLineTypeCode
    /** City (optional) */
    String city
    /** Postal code (optional) */
    String postalCode
    /** Country (optional) */
    String country
    /** Name of organisation (optional) */
    String name
}

/**
 * Represents customer telephone data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Telephone {
    /**
     * TypeCode attribute (optional).
     * Possible values: Work, Mobile, Home, WorkFax
     */
    String typeCode
    /** Full telephone number */
    String fullTelephoneNumber
}

/**
 * Represents customer email address data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Email {
    /** Email address */
    String emailAddress
}

/**
 * Represents SES:CouponSummary within RetailTransaction.
 * Corresponds to section 4.3.6.5 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesCouponSummary {
    /** The coupon number */
    String sesCouponNumber
    /** Count of registered coupons */
    Integer sesInputCount
    /** Number of used coupons (optional) */
    Integer sesAppliedCount
    /** Identifier of the customer (optional) */
    String sesCustomerID
    /** The customer type code (optional) */
    String sesCustomerAddressTypeCode
    /** Coupon serial summaries (optional) */
    List<SesCouponSerialSummary> sesCouponSerialSummaries = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:CouponSerialSummary within SES:CouponSummary.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesCouponSerialSummary {
    /** The coupon serial number */
    String sesCouponSerialNumber
    /**
     * Whether the coupon redemption was successfully posted to the Couponing Service (optional).
     * Possible values: null (no service), "00" (ok), "01" (error)
     */
    String sesBookingSuccessfulTypeCode
    /** The couponing service transaction ID (optional) */
    String sesBookingTransactionUUID
    /** Defines if the coupon serial triggered a promotion price derivation rule (optional) */
    Boolean sesUsedFlag

    /** CST:XXCustom01 ... CST:XXCustom30 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents a Sale line item in a RetailTransaction.
 * Created in case of sales transaction (no returns, no empties, no voids).
 * Corresponds to the Sale section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Sale {

    // ---- Attributes ----
    /**
     * Type of line item.
     * Possible values: Stock, Deposit, ItemCollection, SES:GiftCertificate,
     *                  SES:Inpayment, SES:Outpayment, SES:PhoneCard,
     *                  SES:RebateGiftCertificate, SES:CustomerOrder, SES:Pickup,
     *                  SES:Downpayment, SES:DownpaymentClearing, SES:InvoicePayment,
     *                  SES:Donation, SES:Other, Fee, SES:ComboMeal
     */
    String itemType

    // ---- XML elements ----
    /** POS identity (optional) */
    POSIdentity posIdentity
    /** Item ID (optional) */
    String itemID
    /** Name attribute on ItemID (optional) */
    String itemIDName
    /** Special order number: invoice number or customer order number (optional) */
    String specialOrderNumber
    /** Merchandise hierarchy group (optional) */
    String merchandiseHierarchy
    /** EPC – used to identify individual items, e.g. from RFID scanning (optional) */
    String epc
    /** Item description (optional) */
    String description
    /** true if tax is included in price; otherwise false (optional) */
    Boolean taxIncludedInPriceFlag
    /** Regular sales unit price (or package price) */
    BigDecimal regularSalesUnitPrice
    /** Currency attribute on RegularSalesUnitPrice (not created) */
    String regularSalesUnitPriceCurrency
    /** Quantity attribute on RegularSalesUnitPrice for package prices (optional) */
    BigDecimal regularSalesUnitPriceQuantity
    /** Discounted price (ExtendedAmount) */
    BigDecimal extendedAmount
    /** Quantity of item (optional) */
    BigDecimal quantity
    /** Units attribute on Quantity (optional, e.g. for length, area, weight) */
    String quantityUnits
    /** UnitOfMeasureCode attribute on Quantity (optional) */
    String quantityUnitOfMeasureCode
    /** Seller information (optional) */
    Associate associate
    /** Price modifications (optional) */
    List<RetailPriceModifier> retailPriceModifiers = []
    /** Line item related tax (optional) */
    Tax tax
    /** Serial number or gift certificate number (optional) */
    String serialNumber
    /** Transaction link for food order or order quantity change (optional) */
    TransactionLink transactionLink
    /** Discounted price of position */
    BigDecimal sesExtendedPositionAmount
    /** true if generic article number (optional) */
    Boolean sesMerchandiseStructureItemFlag
    /** Link to empties item (optional) */
    SesItemLink sesItemLink
    /** Supplier ID of concessionaire (optional) */
    String sesConcessionSupplierID
    /** Special offer number (optional) */
    String sesSpecialOfferNumber
    /** false if blocking period was discontinued by operator (optional) */
    Boolean sesAuthorizedForSalesFlag
    /** "1" if price was revaluated (optional) */
    String sesPriceOrigin
    /** Position specific bonus points (optional) */
    SesLoyaltyReward sesLoyaltyReward
    /** External receipt reference list (optional) */
    List<SesExternalReceiptReference> sesExternalReceiptReferenceList = []
    /** Invoice number for bill payment on POS (optional) */
    String sesInvoiceNumber
    /** Reason code for paid in/out (optional) */
    String sesReasonCode
    /** ReasonType attribute on SES:ReasonCode – fix value "InOutpayment" (optional) */
    String sesReasonType
    /** Additional data for gift certificate line items (optional) */
    SesGiftCertificateData sesGiftCertificateData
    /** Additional data for sales order line items (optional) */
    SesSalesOrderData sesSalesOrderData
    /** Identifier of the price group for split pricing (optional) */
    String sesPriceGroupID

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a Return line item in a RetailTransaction.
 * Shares the same structure as Sale with minor deviations.
 * Corresponds to the Return section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Return extends Sale {
    /**
     * Reference to the original sales transaction and line item (optional).
     * Note: ReasonCode fix value = "Return"
     */
    TransactionLink returnTransactionLink
    /**
     * Return reason code (optional). Default: "0000".
     * ReasonType attribute fix value: "Return"
     */
    String sesReturnReasonCode
}

/**
 * Represents a POSIdentity element within Sale.
 */
@ToString(includeNames = true, ignoreNulls = true)
class POSIdentity {
    /**
     * POSIDType attribute.
     * Fix values: RegistrationNumber, MasterData
     */
    String posIDType
    /**
     * POSIDType="RegistrationNumber": on-POS entered registration number.
     * POSIDType="MasterData": main POS item ID.
     */
    String posItemID
    /**
     * Qualifier (optional); created only for magazine/newspaper line item.
     * Value: registration number from digit 14 to end.
     */
    String qualifier
}

/**
 * Represents an Associate (seller) element within Sale.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Associate {
    /** Seller ID */
    String associateID
    /** OperatorName attribute (optional) */
    String operatorName
    /** WorkerID attribute (optional, not created) */
    String workerID
}

/**
 * Represents a RetailPriceModifier element within Sale.
 */
@ToString(includeNames = true, ignoreNulls = true)
class RetailPriceModifier {
    /** Continuous number over all price modifications of one position, starting with "1" */
    String sequenceNumber
    /** Absolute amount of price modification */
    BigDecimal amount
    /**
     * Action attribute on Amount.
     * Possible values: Add, Substract
     */
    String amountAction
    /** Description of position condition (optional) */
    String promotionID
    /** Price derivation rule data (optional) */
    PriceDerivationRule priceDerivationRule
    /** Reason of corresponding price modification (default "0000") */
    String reasonCode
    /**
     * Type code for position condition, property value for price change.
     * Otherwise "0000"
     */
    String sesRebateMethod
    /**
     * External promotion ID for position condition.
     * Property value for price change. Otherwise "0"
     */
    String sesRebateID
    /** Discount percentage (optional; only for promotions with percentage rule) */
    BigDecimal sesPercent
    /** Identifier of the external offer for position condition (optional) */
    String sesExternalOfferID
    /** Price type code of the additional applied price for customer-specific prices (optional) */
    String sesAdditionalPriceTypeCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a PriceDerivationRule element within RetailPriceModifier or LoyaltyReward.
 */
@ToString(includeNames = true, ignoreNulls = true)
class PriceDerivationRule {
    /** First 40 digits of price derivation rule ID */
    String priceDerivationRuleID
    /** Eligibility (optional); created only if coupon number exists */
    Eligibility eligibility
    /** Amount */
    BigDecimal amount
    /**
     * Action attribute on Amount.
     * Possible values: Substract (negative amount), Add (positive amount)
     */
    String amountAction
    /** Origin (optional) – "ORIGIN" in case of loyalty trigger */
    String sesOrigin
    /** Applied quantity (optional) */
    BigDecimal sesAppliedQuantity
    /** Rule description (optional) */
    String sesRuleDescription

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents an Eligibility element within PriceDerivationRule.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Eligibility {
    /** Coupon reference number (optional) */
    String referenceID
    /** Type attribute on ReferenceID – fix value "StoreCoupon" (optional) */
    String referenceIDType

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:ItemLink within Sale (link to empties item).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesItemLink {
    /** LinkType attribute */
    String linkType
}

/**
 * Represents SES:LoyaltyReward within Sale (position-specific bonus points).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesLoyaltyReward {
    /** Description of receipt condition (optional) */
    String promotionID
    /** Reason code according to db entry (default "0000") */
    String reasonCode
    /** Bonus points awarded (optional) */
    BigDecimal pointsAwarded
    /** Discount as gift certificate (optional) */
    GiftCertificate giftCertificate
    /** Discount as coupon (optional) */
    SesVoucher sesVoucher
    /**
     * Continuous number over all LoyaltyReward elements of one line item.
     * Starting with "1"; used only for position-specific bonus points.
     */
    String sequenceNumber
    /** Price derivation rule data (optional) */
    PriceDerivationRule sesPriceDerivationRule
    /** Type code of position condition or "0000" */
    String sesRebateMethod
    /** External promotion ID or "0" */
    String sesRebateID
    /** Identifier of external offer for loyalty trigger (optional) */
    String sesExternalOfferID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:GiftCertificateData within Sale (additional gift certificate data).
 * Corresponds to the SES:GiftCertificateData section in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesGiftCertificateData {
    /**
     * Gift certificate type (optional).
     * Possible values: SES:PaperGiftCertificate, SES:ElectronicGiftCertificate
     */
    String sesGiftCertificateType

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:SalesOrderData within Sale (additional sales order data).
 * Corresponds to the SES:SalesOrderData section in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSalesOrderData {
    /**
     * Special order system (optional).
     * Possible values: ERP, Store
     */
    String sesSpecialOrderSystem
    /**
     * Special order type (optional).
     * Possible values: Reservation, SalesSelection, Pickup, Delivery,
     *                  ImmediatePickup, SES:Layaway
     */
    String sesSpecialOrderType
    /** External position numbers of customer order (optional) */
    String sesSpecialOrderPositionNumber

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single entry in SES:ExternalReceiptReferenceList within Sale.
 * Corresponds to the SES:ExternalReceiptReferenceList section in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesExternalReceiptReference {
    /**
     * Item origin (optional).
     * Possible values: 06 (offline rebooking of suspended position), 05 (empties),
     *                  04 (scale), otherwise type code of line item association
     *                  (e.g. SUSP, RETU, LIRE, DEPO)
     */
    String sesItemOrigin
    /** Reference to external document (invoice, customer order, etc.) */
    String sesReceiptReferenceNumber
    /** External transaction offline redemption flag (optional) */
    Boolean sesExternalTransactionOfflineRedemptionFlag

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



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



/**
 * Represents a Tax element in a LineItem (line item level or transaction level).
 * Corresponds to the Tax sections in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Tax {

    // ---- Attributes ----
    /** TaxType attribute – fix value "Common" (optional) */
    String taxType
    /**
     * TaxSubType attribute (optional).
     * "ZeroRated" if rate of tax = 0; otherwise "Standard"
     */
    String taxSubType
    /**
     * TypeCode attribute (optional).
     * Possible values: Refund, Sale
     */
    String typeCode

    // ---- XML elements ----
    /** ID of the tax authority (optional) */
    String taxAuthority
    /** Taxable amount without sign (optional) */
    BigDecimal taxableAmount
    /** Tax amount (optional) */
    BigDecimal amount
    /** Tax percentage (optional) */
    BigDecimal percent
    /** Tax exemption data (optional) */
    TaxExemption taxExemption
    /** Tax override data (optional; not used for tax on transaction level) */
    TaxOverride taxOverride
    /** Tax group ID (optional) */
    String taxGroupID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a TaxExemption element within Tax.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TaxExemption {
    /** Tax certificate ID */
    String customerExemptionID
    /** Fix value = 0 (not supported) */
    BigDecimal exemptTaxAmount = BigDecimal.ZERO
    /** Reason code for the tax exemption (optional) */
    String reasonCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a TaxOverride element within Tax.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TaxOverride {
    /** Original tax percentage */
    BigDecimal originalPercent
    /** Original tax amount */
    BigDecimal originalTaxAmount
    /** New tax percentage */
    BigDecimal newTaxPercent
    /** New tax amount */
    BigDecimal newTaxAmount
    /** Reason code for the tax override (optional) */
    String reasonCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents a TenderControlTransaction in the POSLog export.
 * Corresponds to section 4.3.7 of the GK POSLog Structure v3.
 *
 * The choice among the various sub-elements determines the type of tender control transaction.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TenderControlTransaction {

    // ---- Transaction type choice (exactly one will be populated) ----
    /** Change transaction – tender transfer from safe to drawer (optional) */
    SesTenderLoan sesTenderLoan
    /** Safe bag or bank deposit transaction – tender transfer from safe to bank (optional) */
    Deposit deposit
    /** Drawer settlement or cash check transaction (optional) */
    TillSettle tillSettle
    /** Safe drop transaction – cash transfer from bank to safe (optional) */
    SafeDrop safeDrop
    /** Tender pickup transaction – tender transfer from drawer to safe (optional) */
    SesTenderPickup sesTenderPickup
    /** Paid-in transaction or safe opening balance or safe correction pay-in (optional) */
    SesPaidIn sesPaidIn
    /** Paid-out transaction or safe correction pay-out (optional; will not be generated as TenderInterchange) */
    SesPaidOut sesPaidOut
    /** Tender loan carried forward – opening balance of next accounting period (optional) */
    SesTenderLoanCarriedForward sesTenderLoanCarriedForward
    /** Safe settlement transaction (optional) */
    SesSafeSettle sesSafeSettle

    /** Reference to another transaction (optional) */
    TransactionLink sesTransactionLink
    /** Receipt position addons (optional) */
    List<SesReceiptPositionAddon> sesReceiptPositionAddonList = []
    /** OperatorID of operator with one-to-one assignment to drawer (optional) */
    String sesAccountedOperatorID
    /** Name attribute on SES:AccountedOperatorID (optional) */
    String sesAccountedOperatorName

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:TenderLoan within TenderControlTransaction.
 * Tender transfer from safe to drawer.
 * Corresponds to section 4.3.7.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderLoan {
    /** Tender type attribute */
    String tenderType
    /** Tender description attribute (optional) */
    String tenderDescription
    /** Sum of tenders of tender group cash in main currency */
    SesTotals sesTotals

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:Totals within SesTenderLoan.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTotals {
    /** Amount (= 0 if no tender line item in transaction) */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Reason element created in case of change correction (optional) */
    String reason
    /** Description attribute on Reason (optional) */
    String reasonDescription
    /** Name attribute on Reason (optional) */
    String reasonName

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a Deposit element within TenderControlTransaction.
 * Safe bag or bank deposit – tender transfer from safe to bank.
 * Corresponds to section 4.3.7.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Deposit {
    /** Not used (empty string) */
    String bank = ''
    /** Not used (empty string) */
    String account = ''
    /** Safe bag number (safe bag) or empty string (bank deposit) */
    String bagID
    /** Amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Employee who transferred safe bag to bank (WorkerID attribute) */
    String depositorWorkerID
    /** Totals of tenders */
    List<DepositDetail> depositDetails = []
    /** Foreign currency tenders (optional) */
    List<SesDepositForeignCurrency> sesDepositForeignCurrencies = []
    /**
     * Safe bag status (optional).
     * 1 = created, 2 = deleted, 3 = picked up, 4 = created caused by change order
     */
    Integer sesSafebagStatus
    /** Safe bag document number (optional) */
    String sesSafebagDocumentNumber
    /** Pickup safe bag number (optional; created if sesSafebagStatus = 3) */
    String sesPickupSafebagNumber
    /**
     * Safe bag type code (optional).
     * 1 = common safe bag, 3 = safe bag caused by change order
     */
    Integer sesSafebagTypeCode
    /** Timestamp of safe bag status change (optional) */
    String sesStatusTimestamp

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents DepositDetail within Deposit.
 */
@ToString(includeNames = true, ignoreNulls = true)
class DepositDetail {
    /** Amount of tender of picked up safe bag in main currency */
    BigDecimal tenderTotal
    /** TenderType attribute on TenderTotal */
    String tenderType
    /** Fix value "0000" */
    String reason = '0000'
}

/**
 * Represents SES:DepositForeignCurrency within Deposit.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesDepositForeignCurrency {
    /** Amount of tender of picked up safe bag in main currency */
    BigDecimal tenderTotal
    /** TenderType attribute on TenderTotal */
    String tenderType
    /** Currency attribute on TenderTotal */
    String currency
    /** Amount in foreign currency (ForeignAmount attribute) */
    BigDecimal foreignAmount
}

/**
 * Represents a TillSettle element within TenderControlTransaction.
 * Drawer settlement or drawer cash check.
 * Corresponds to section 4.3.7.3 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillSettle {
    /** Tender summary entries (one per tender) */
    List<TenderSummary> tenderSummaries = []
    /**
     * Category of drawer counting transaction (optional).
     * "CHECK" = cash check transaction; "SETTLE" = settlement transaction
     */
    String sesTransactionCategoryCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SafeDrop within TenderControlTransaction.
 * Cash transfer from bank to safe.
 * Corresponds to section 4.3.7.4 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SafeDrop {
    /** Amount of cash receipt */
    BigDecimal dropAmount
    /** Empty string (mandatory field) */
    String envelopeID = ''
    /** Empty string (mandatory field) */
    String dropNumber = ''
}

/**
 * Represents SES:TenderPickup within TenderControlTransaction.
 * Tender transfer from drawer to safe.
 * Corresponds to section 4.3.7.5 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderPickup {
    /** Amount of tender in main currency (TenderType attribute, optional TypeCode, etc.) */
    List<SesTenderAmount> sesTenderAmounts = []
    /** Sum of all tenders (optional TypeCode = "Refund") */
    BigDecimal sesTotalAmount
    /** TypeCode attribute on SesTotalAmount (optional) – "Refund" */
    String sesTotalAmountTypeCode
    /** Identifier of the Envelope used for this Pickup (optional) */
    String sesEnvelopeID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single SES:TenderAmount within SesTenderPickup.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderAmount {
    /** Amount in main currency */
    BigDecimal amount
    /** TenderType attribute */
    String tenderType
    /** TypeCode attribute (optional; "Refund" only) */
    String typeCode
    /** Currency attribute (optional; foreign currency only) */
    String currency
    /** ForeignAmount attribute (optional; foreign currency only) */
    BigDecimal foreignAmount
    /** TenderDescription attribute (optional) */
    String tenderDescription

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:PaidIn within TenderControlTransaction.
 * Paid-in, safe opening balance, or safe correction pay-in transaction.
 * Corresponds to section 4.3.7.6 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesPaidIn {
    /** Amount of paid-in */
    BigDecimal sesAmount
    /** Entered reason for paid-in (default "0000") */
    String sesReason
    /** Tender positions of paid-in transaction (optional) */
    List<Tender> sesTenders = []
    /**
     * Category of PaidIn transaction (optional).
     * "PAYIN" = paid-in; "OPEN" = safe opening balance; "CORR" = safe correction
     */
    String sesTransactionCategoryCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:PaidOut within TenderControlTransaction.
 * Paid-out or safe correction pay-out transaction.
 * Corresponds to section 4.3.7.7 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesPaidOut {
    /** Amount of paid-out */
    BigDecimal amount
    /** Entered reason for paid-out (default "0000") */
    String reason
    /** Tender positions of paid-out transaction (optional) */
    List<Tender> tenders = []
    /**
     * Category of PaidOut transaction (optional).
     * "PAYOUT" = paid-out; "CORR" = safe correction
     */
    String sesTransactionCategoryCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:TenderLoanCarriedForward within TenderControlTransaction.
 * Tender amount in the drawer at the beginning of an accounting period.
 * Corresponds to section 4.3.7.9 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderLoanCarriedForward {
    /** Carryforward of tender loan ending balance (opening balance of next period) */
    BigDecimal amount

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:SafeSettle within TenderControlTransaction.
 * Safe settlement / safe accounting transaction.
 * Corresponds to section 4.3.7.10 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSafeSettle {
    /** Tender summary entries for the safe accounting transaction */
    List<TenderSummary> tenderSummaries = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents a Tender element in a LineItem.
 * Created for each used tender.
 * Corresponds to the Tender section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Tender {

    // ---- Attributes ----
    /** Tender type */
    String tenderType
    /** TypeCode – possible values: Refund, Sale */
    String typeCode
    /** Tender description (optional) */
    String tenderDescription

    // ---- XML elements ----
    /** Tender ID for main or foreign currency (optional) */
    String tenderID
    /** Amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount for foreign currency (optional) */
    String amountCurrency
    /** ForeignAmount attribute on Amount for foreign currency (optional) */
    BigDecimal foreignAmount
    /** Tender change (optional; only within RetailTransaction) */
    TenderChange tenderChange
    /** Cashback amount in main currency (optional; only within RetailTransaction) */
    BigDecimal cashback
    /** Cashback currency attribute (optional) */
    String cashbackCurrency
    /** Cashback foreign amount attribute (optional) */
    BigDecimal cashbackForeignAmount
    /** Tip amount (optional; in main currency) */
    BigDecimal tip
    /** Tip currency attribute (optional) */
    String tipCurrency
    /** Tip foreign amount attribute (optional) */
    BigDecimal tipForeignAmount
    /** Authorization data (optional; created for tenders with external authorization) */
    Authorization authorization
    /** Foreign currency data (optional; created only for foreign currency) */
    ForeignCurrency foreignCurrency
    /** Check tender data (optional) */
    Check check
    /** Credit/debit tender data (optional) */
    CreditDebit creditDebit
    /** Coupon tender data (optional) */
    Coupon coupon
    /** Voucher tender data (optional) */
    Voucher voucher
    /** Loyalty redemption tender data (optional) */
    SesLoyaltyRedemption sesLoyaltyRedemption

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a TenderChange element within Tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TenderChange {
    /** Tender type attribute */
    String tenderType
    /** Tender ID for main or foreign currency (optional) */
    String tenderID
    /** Amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount for foreign currency (optional) */
    String amountCurrency
    /** ForeignAmount attribute on Amount for foreign currency (optional) */
    BigDecimal foreignAmount
}

/**
 * Represents an Authorization element within Tender.
 * Corresponds to the Authorization section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Authorization {

    // ---- Attributes ----
    /**
     * HostAuthorized: true (auth codes 3, 8, 10 or 11); false (all other codes) (optional)
     */
    Boolean hostAuthorized
    /**
     * ElectronicSignature: true (auth codes 3, 10 or 11); false (all other codes) (optional)
     */
    Boolean electronicSignature
    /**
     * ForceOnline: true (online); false (offline) (optional)
     */
    Boolean forceOnline

    // ---- XML elements ----
    /** Absolute value of transaction (optional) */
    BigDecimal requestedAmount
    /** Currency attribute on RequestedAmount (optional) */
    String requestedAmountCurrency
    /** ForeignAmount attribute on RequestedAmount (optional) */
    BigDecimal requestedForeignAmount
    /** Code of authorization code */
    String authorizationCode
    /** Number of transaction (optional) */
    String referenceNumber
    /** Timestamp of authorization (optional) */
    String authorizationDateTime
    /** ID of used terminal (optional) */
    String authorizingTermID
    /** Activation sequence number (optional) */
    String sesActivationSequenceNumber
    /** Transaction reference number for 'Purchase Reservation' and 'Reservation Adjustment' (optional) */
    String sesTransactionReferenceNumber
    /**
     * Transaction type (optional).
     * Possible values: credit, debit
     */
    String sesTransactionType
    /** Terminal tender description (optional) */
    String sesTerminalTenderDescription
    /** Application identifier (optional) */
    String applicationID
    /** Application PAN coded (optional) */
    String sesEncryptedPAN
    /** Transaction currency (optional) */
    String sesTransactionCurrencyCode
    /** Unique number assigned by payment processor when EFT transaction is processed (optional) */
    String sesTerminalTransactionToken
    /** Terminal authorization number (optional) */
    String sesApprovalCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a ForeignCurrency element within Tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ForeignCurrency {
    /** Code of foreign currency (optional) */
    String currencyCode
    /** Origin amount in foreign currency */
    BigDecimal originalFaceAmount
    /** Exchange rate, rounded to 5 decimals (optional) */
    BigDecimal exchangeRate
}

/**
 * Represents a Check element within Tender.
 * Corresponds to the Check section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Check {
    /** Bank ID (optional) */
    String bankID
    /** Account number (optional) */
    String accountNumber
    /** Bank card number (optional) */
    String checkCardNumber
    /** Full string of characters read from MICR strip (optional) */
    String fullMICR
    /** Bank identifier code (SWIFT code) (optional) */
    String sesBankIdentifierCode
    /** International bank account number (optional) */
    String sesInternationalBankAccountNumber
    /** Cheque number (optional; use SES:CheckNumber instead) */
    String sesCheckNumber

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a CreditDebit element within Tender.
 * Corresponds to the CreditDebit section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class CreditDebit {
    /** Bank ID (optional) */
    String issuerIdentificationNumber
    /** Account number / bank card number (default "0000") */
    String primaryAccountNumber
    /** Card suffix (optional) */
    String issueSequence
    /** Expiration date of card (optional) */
    String expirationDate
    /** Code of transaction result (optional) */
    String reconciliationCode
    /** Date of transaction start (optional) */
    String startDate
    /** Trace number (optional) */
    String sesTraceNumber
    /** IBAN for SEPA Direct Debit (ELV) (optional) */
    String sesInternationalBankAccountNumber
    /** BIC / SWIFT Code for SEPA Direct Debit (ELV) (optional) */
    String sesBankIdentifierCode
    /** Creditor ID for SEPA Direct Debit (ELV) (optional) */
    String sesCreditorID
    /** Mandate ID for SEPA Direct Debit (ELV) (optional) */
    String sesMandateID
    /** Pre-notification text for SEPA Direct Debit (ELV) (optional) */
    String sesPrenotificationText
}

/**
 * Represents a Coupon element within Tender.
 * Corresponds to the Coupon section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Coupon {
    /** Fix value: 1 */
    Integer quantity = 1
    /** Coupon number */
    String primaryLabel
    /** Manufacturer ID that funds the offer (optional; for manufacturer coupons) */
    String manufacturerID
    /** Offer number (promotion code) (optional; for manufacturer coupons) */
    String promotionCode
    /** Single amount of coupon */
    BigDecimal sesCouponSingleAmount
    /** Rate of taxation */
    BigDecimal sesTaxPercent
    /** Flag: internal or external coupon */
    String sesOrigin
    /** Reference item list (optional) */
    List<SesReferenceItem> sesReferenceItemList = []
}

/**
 * Represents a single entry in SES:ReferenceItemList within Coupon.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesReferenceItem {
    /** EAN of discounted item */
    String itemID
    /** Quantity of discounted item */
    BigDecimal itemQuantity
    /** Rebate share of item */
    BigDecimal itemRebateShare
    /** Link to position number of item in POSLog (LinkType fix value "Coupon") */
    String itemLink
    /** LinkType attribute on ItemLink – fix value "Coupon" */
    String itemLinkType = 'Coupon'
}

/**
 * Represents a Voucher element within Tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Voucher {
    /**
     * TypeCode attribute.
     * Possible values: SES:ElectronicGiftCertificate, SES:PaperGiftCertificate
     */
    String typeCode
    /** EAN of voucher */
    String serialNumber
}

/**
 * Represents SES:LoyaltyRedemption within Tender.
 * Corresponds to the SES:LoyaltyRedemption section in 4.3.6.1.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesLoyaltyRedemption {
    /** Redeemed bonus points */
    BigDecimal pointsRedeemed
    /** ID of bonus point redemption transaction */
    String transactionID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents TillEOD within ControlTransaction.
 * Total/summary values (drawer related).
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillEOD {
    /** Session settle container with sum values per accounting period */
    TillEODSessionSettle sessionSettle
}

/**
 * Represents the SessionSettle container within TillEOD.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillEODSessionSettle {
    /** Sum values of tenders per accounting period (optional) */
    List<TillEODTenderSummary> tenderSummaries = []
    /** Sum values of sales per accounting period (optional) */
    List<SesSalesSummary> sesSalesSummaries = []
    /** Not generated at this time (optional) */
    // SesRebateSummary sesRebateSummary
    /** Sum values of taxes per accounting period (optional) */
    List<SesTaxSummary> sesTaxSummaries = []
}

/**
 * Represents TenderSummary within TillEOD's SessionSettle.
 * Contains opening/closing/pickup/over/short/ending balance per tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillEODTenderSummary {
    /** Opening balance (optional) */
    Beginning beginning
    /** Tender pickup (optional) */
    Pickup pickup
    /** Positive difference (created if over) */
    EODOver over
    /** Negative difference (created if short) */
    EODShort shortEntry
    /** Closing/target balance of drawer (optional) */
    SesNominal sesNominal
    /** Tender totals of all sales/return receipts (optional) */
    SesSalesTenderNominal sesSalesTenderNominal
    /** Counted/actual balance of drawer (optional) */
    SesEnding sesEnding
}

/**
 * Represents Beginning within TillEODTenderSummary (opening balance).
 */
@ToString(includeNames = true, ignoreNulls = true)
class Beginning {
    /** Tender type attribute */
    String tenderType
    /** Opening balance in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents Pickup within TillEODTenderSummary (manual pickup amount).
 */
@ToString(includeNames = true, ignoreNulls = true)
class Pickup {
    /** Tender type attribute */
    String tenderType
    /** Manual pickup amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents Over within TillEODTenderSummary (positive difference).
 */
@ToString(includeNames = true, ignoreNulls = true)
class EODOver {
    /** Tender type attribute */
    String tenderType
    /** Positive amount of the drawer count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents Short within TillEODTenderSummary (negative difference).
 * Named EODShort to avoid collision with java.lang.Short.
 */
@ToString(includeNames = true, ignoreNulls = true)
class EODShort {
    /** Tender type attribute */
    String tenderType
    /** Negative amount of the drawer count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents SES:SalesTenderNominal within TillEODTenderSummary.
 * Tender totals of all sales/return receipts.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSalesTenderNominal {
    /** Tender type attribute */
    String tenderType
    /**
     * TypeCode attribute.
     * Possible values: "Refund" (negative), "Sale" (positive)
     */
    String typeCode
    /** Tender totals of all sales/return receipts in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents SES:SalesSummary within TillEOD or SesStoreEODSummary SessionSettle.
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSalesSummary {
    /**
     * TypeCode attribute (optional).
     * Possible values: Refund, Sale
     */
    String typeCode
    /** Sales of sales receipts */
    BigDecimal amount
    /** Quantity of sales receipts */
    Integer count
    /**
     * Reason for the sales entry (attribute Name fix value "SalesID").
     * Examples: "2001" (Sales), "2011" (Subtotal rounding), "2020" (Sold gift certs),
     *           "3101" (Down payments), "3102" (Cleared down payments),
     *           "3103" (Invoices), "3104" (Pay-ins), "3201" (Pay-outs)
     */
    String reason
    /** Name attribute on Reason – fix value "SalesID" */
    String reasonName = 'SalesID'
}

/**
 * Represents SES:TaxSummary within TillEOD or SesStoreEODSummary SessionSettle.
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTaxSummary {
    /**
     * TenderType attribute (optional).
     * Possible values: Refund, Sale
     */
    String tenderType
    /** Sales per tax code */
    BigDecimal amount
    /** Quantity of taxed positions */
    Integer count
    /** ID of tax authority */
    String sesTaxAuthorityID
    /** ID of tax group */
    String sesTaxGroupID
}



/**
 * Represents a TenderSummary element used in TillSettle and SesSafeSettle.
 * Corresponds to the TenderSummary section in 4.3.7.3 and 4.3.7.10.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TenderSummary {
    /** Over-difference entry (created if count difference >= 0) */
    Over over
    /** Short-difference entry (created if count difference < 0) */
    ShortSummary shortSummary
    /** Closing/target balance of drawer or safe (optional) */
    SesNominal sesNominal
    /** Counted/actual balance of drawer or safe (optional) */
    SesEnding sesEnding

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents an Over element within TenderSummary (positive count difference).
 */
@ToString(includeNames = true, ignoreNulls = true)
class Over {
    /** Tender type */
    String tenderType
    /** Positive amount of the drawer/safe count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if count difference >= 0) */
    Integer count
}

/**
 * Represents a Short element within TenderSummary (negative count difference).
 * Named ShortSummary to avoid collision with java.lang.Short.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ShortSummary {
    /** Tender type */
    String tenderType
    /** Negative amount of the drawer/safe count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if count difference < 0) */
    Integer count
}

/**
 * Represents SES:Nominal within TenderSummary (closing/target balance).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesNominal {
    /** Tender type attribute */
    String tenderType
    /**
     * TypeCode attribute to distinguish positive from negative amount.
     * Possible values: "Refund" (negative), "Sale" (positive)
     */
    String typeCode
    /** Closing/target amount of the drawer or safe in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents SES:Ending within TenderSummary (counted/actual balance).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesEnding {
    /** Tender type attribute */
    String tenderType
    /**
     * TypeCode attribute to distinguish positive from negative amount.
     * Possible values: "Refund" (negative), "Sale" (positive)
     */
    String typeCode
    /** Counted/actual amount of the drawer or safe in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}



/**
 * Represents a single Transaction in the POSLog export.
 * Corresponds to section 4.3 of the GK POSLog Structure v3 specification.
 *
 * The choice between retailTransaction, tenderControlTransaction and controlTransaction
 * determines the type of transaction.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Transaction {

    // ---- Attributes ----
    /** Major version of POSLog export – fix value = 3 */
    Integer majorVersion = 3
    /** Minor version of POSLog export – fix value = 0 */
    Integer minorVersion = 0
    /** Fix version of POSLog export – fix value = 0 */
    Integer fixVersion = 0
    /** Internal major version of POSLog export interface – fix value = 2 */
    Integer sesInternalMajorVersion = 2
    /** Internal minor version of POSLog export interface – fix value = 5 */
    Integer sesInternalMinorVersion = 5
    /** Internal fix version of POSLog export interface – fix value = 9 */
    Integer sesInternalFixVersion = 9
    /** Flag for canceled transaction (optional) */
    Boolean cancelFlag
    /** Flag for transaction made in training mode (optional) */
    Boolean trainingModeFlag

    // ---- XML elements ----
    /** A unique retailer-assigned identifier for a RetailStore */
    String retailStoreID
    /** Identifier for the workstation/POS within the store */
    String workstationID
    /** Identifier for the tender repository (drawer) within the store (optional) */
    String tillID
    /** Receipt number / sequence counter (optional prefix added for offline rebooking/void) */
    String sequenceNumber
    /** Calendar date of the BusinessDay */
    String businessDayDate
    /** Time and date a transaction is initiated */
    String beginDateTime
    /** Time and date a transaction is completed */
    String endDateTime
    /** ID of the operator who created the transaction (optional) */
    String operatorID
    /** Operator name attribute of OperatorID (optional) */
    String operatorName
    /** WorkerID attribute of OperatorID (optional) */
    String workerID
    /** Unique identifier of the currency (3-digit ISO code, e.g. EUR, USD) */
    String currencyCode

    /** The unique identifier for the TENANT (optional) */
    String sesTenantID
    /** The time and date when the accounting period started (optional) */
    String sesBusinessBeginDateTime
    /** The time and date when the accounting period ended (optional) */
    String sesBusinessEndDateTime
    /** True in case of return transaction */
    Boolean sesReceiptReturnedFlag
    /** Reason code for canceled or voided transactions (optional) */
    String sesReasonCode
    /** ReasonType attribute on SES:ReasonCode (optional) */
    String sesReasonType
    /** Description attribute on SES:ReasonCode (optional) */
    String sesReasonDescription
    /** True when the entire original transaction has been post-voided */
    Boolean sesPostVoidedFlag
    /** Application version with which the transaction was created (optional) */
    String sesTransactionSoftwareVersion
    /** Composite version string (GK + release version) */
    String sesSoftwareVersion
    /**
     * Online/offline state when transaction was completed (optional).
     * Possible values: OnLineReferenceItem, OffLine, Both
     */
    String sesKeyedOfflineCode
    /** Universally unique identifier (UUID) for the Transaction */
    String sesInternalTransactionID
    /** Identifier created by POS, fiscal printer, or other fiscalization device (optional) */
    String sesFiscalSequenceNumber
    /** Flag denoting that this is a fiscal transaction */
    Boolean sesFiscalFlag
    /** Fiscal day counter (optional) */
    String sesFiscalDayNumber
    /** Identifier of the fiscal printer (optional) */
    String sesFiscalPrinterID
    /** Fiscal signature of the transaction (optional) */
    String sesFiscalSignature
    /** Flag denoting that this is a layaway transaction */
    Boolean sesLayawayFlag
    /**
     * Type of the layaway transaction (optional).
     * Possible values: FullyPay, Claiming, DownPayment, Modify, Void, Terminate,
     *                  Expire, Rebooking, Create, Extend
     */
    String sesLayawayTransactionType

    // ---- Sub-element containers ----
    /** XML-container: created in case of approval by another operator (optional) */
    SesOperatorBypassApproval sesOperatorBypassApproval
    /** XML-container: additional header information (optional) */
    List<Addon> sesReceiptHeaderAddonList = []
    /** XML-container: time information (optional) */
    List<Timer> sesReceiptTimerList = []
    /** XML-container: created in case of loyalty program (optional) */
    SesLoyaltyAccount sesLoyaltyAccount
    /** XML-container: binary data attached to the transaction (optional) */
    List<BinaryData> sesTransactionBinaryDataList = []

    // ---- Transaction type choice (exactly one will be populated) ----
    /** Created in case of retail transaction */
    RetailTransaction retailTransaction
    /** Created in case of tender control transaction */
    TenderControlTransaction tenderControlTransaction
    /** Created in case of control transaction */
    ControlTransaction controlTransaction

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents approval by another operator (SES:OperatorBypassApproval).
 * Used in Transaction and LineItem.
 * Corresponds to section 4.3.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesOperatorBypassApproval {
    /**
     * Shows how authorization was triggered.
     * Fix value: "CODE" (operator number)
     */
    String approvalTypeCode

    /** Approval code (optional, not created) */
    String approvalCode
    /** ID of authorizing operator (optional) */
    String approverID
    /** WorkerID attribute on ApproverID (optional) */
    String workerID
    /** ApproverName attribute on ApproverID (optional) */
    String approverName

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single addon entry within SES:ReceiptHeaderAddonList.
 * Corresponds to section 4.3.2.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Addon {
    /** Addon sequence number per key (optional) */
    String addonPos
    /**
     * Addon key.
     * Examples: TransactionTypeCode, TransactionCategoryCode, FIRSTLOGIN, ONLINE, OFFLINE
     */
    String key
    /**
     * Addon value.
     * Examples: value of TransactionTypeCode, "true" (FIRSTLOGIN),
     *           timestamp of Online/Offline status change
     */
    String value

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single timer entry within SES:ReceiptTimerList.
 * Corresponds to section 4.3.3.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Timer {
    /**
     * Timer identifier.
     * "76" = first item registration to first tender registration.
     * "77" = first tender registration to end of receipt.
     */
    String timerID
    /**
     * Receipt begin timestamp (for "76") or tender begin timestamp (for "77").
     */
    String startTimestamp
    /**
     * Duration in seconds with 3 decimals.
     * For "76": duration from receipt begin to tender begin.
     * For "77": duration from tender begin to end of receipt.
     */
    BigDecimal duration
}

/**
 * Represents SES:LoyaltyAccount on transaction level.
 * Corresponds to section 4.3.4 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesLoyaltyAccount {
    /** Customer ID (optional) */
    String customerID
    /** Loyalty program details (optional) */
    List<LoyaltyProgram> loyaltyPrograms = []
}

/**
 * Represents a loyalty program within SES:LoyaltyAccount.
 * Corresponds to section 4.3.4.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class LoyaltyProgram {
    /** Identifier of the loyalty account (optional) */
    String loyaltyAccountID
    /** Effective date (optional) */
    String effectiveDate
    /** Type of points (optional) */
    String points
    /** Type attribute on Points element (optional) */
    String pointsType
    /**
     * Determines the kind of customer account.
     * Examples: 00 (turnover of current year), 01 (rebate amount), 02 (bonus points),
     *           VP (Valuephone), GC (gift certificates)
     */
    String loyaltyAccountTypeCode
    /** External identifier of the accounting transaction (optional) */
    String loyaltyAccountTransactionID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}



/**
 * Represents a shared link to another transaction.
 * Used within RetailTransaction, TenderControlTransaction, and ControlTransaction.
 * Corresponds to multiple TransactionLink sections in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TransactionLink {
    /**
     * Reason code attribute (optional).
     * Possible values: Voided, SES:Invoice, SES:OfflineRebooking, LayAway,
     *                  SES:RetrievedSuspend, SES:Replace, SES:Order,
     *                  SES:OrderQuantityChange, SES:InvoiceDuplicate, SES:TaxRefund
     */
    String reasonCode

    /** Retail store number of the original transaction */
    String retailStoreID
    /** Workstation/POS number of the original transaction */
    String workstationID
    /** Receipt number of the original transaction */
    String sequenceNumber
    /** Line item sequence number of the original line item (optional) */
    String lineItemSequenceNumber
    /** Date of business day of the original transaction */
    String businessDayDate
    /** Begin date and time of the original transaction */
    String beginDateTime
    /** End date and time of the original transaction (optional) */
    String endDateTime
    /** Receipt date and time of the original transaction (optional) */
    String sesReceiptDateTime
    /** Universally unique identifier (UUID) of the original transaction */
    String sesInternalTransactionID
}



/**
 * Represents a Rounding element in a LineItem.
 * Rounding total; created only when there is an existing rounding difference
 * for the sum of subtotal rounding and tender change rounding.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Rounding {
    // Currency and ForeignAmount attributes are not created
}

/**
 * Represents an SES:Rounding element in a LineItem.
 * Rounding line item; created only when there is an existing rounding difference
 * for subtotal rounding or tender change rounding.
 * Corresponds to the SES:Rounding section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesRounding {
    /**
     * Type of rounding.
     * Possible values: "00" (subtotal rounding), "01" (tender change rounding)
     */
    String sesRoundingTypeCode
    /** Rounding amount */
    BigDecimal sesAmount
    /**
     * Rounding direction.
     * Possible values: Up, Down
     */
    String sesRoundingDirection

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a Voids element in a LineItem.
 * Created only in case of a void position.
 * Corresponds to the Voids section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Voids {
    /** Item link referencing the voided position */
    ItemLink itemLink
}

/**
 * Represents an ItemLink element within Voids.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ItemLink {
    /** ReasonCode attribute – fix value "Voided" */
    String reasonCode = 'Voided'
    /** Same content as Transaction.SequenceNumber */
    String sequenceNumber
    /** LineItem SequenceNumber of the voided original position */
    String lineItemSequenceNumber
    /** Void reason code (default "0000") */
    String sesVoidReasonCode
    /**
     * ReasonType attribute on SES:VoidReasonCode.
     * Possible values: Void, SES:BelatedVoid, SES:BelatedInternalVoid
     */
    String sesVoidReasonType
    /** Description attribute on SES:VoidReasonCode (optional) */
    String sesVoidReasonDescription

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a LoyaltyReward element in a LineItem.
 * Created only in case of non-financial bonus.
 * Corresponds to the LoyaltyReward section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class LoyaltyReward {
    /** Description of receipt condition (optional) */
    String promotionID
    /** Reason code according to db entry (default "0000") */
    String reasonCode
    /** Bonus points awarded (optional) */
    BigDecimal pointsAwarded
    /** Discount as gift certificate (optional) */
    GiftCertificate giftCertificate
    /** Discount as coupon (optional) */
    SesVoucher sesVoucher
    /**
     * Continuous number over all LoyaltyReward elements of one line item.
     * Starting with "1"; used only for position-specific bonus points.
     */
    String sequenceNumber
    /** Price derivation rule data (optional) */
    PriceDerivationRule sesPriceDerivationRule
    /** Type code of position condition or "0000" */
    String sesRebateMethod
    /**
     * Possible values: "0" (non-loyalty trigger), external promotion ID (first 40 digits)
     */
    String sesRebateID
    /** Identifier of external offer for loyalty trigger (optional) */
    String sesExternalOfferID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a GiftCertificate element within LoyaltyReward.
 */
@ToString(includeNames = true, ignoreNulls = true)
class GiftCertificate {
    /**
     * MediaType attribute.
     * Possible values: SES:ElectronicGiftCertificate, SES:PaperGiftCertificate
     */
    String mediaType
    /** Gift card number (optional) */
    String serialNumber
    /** Gift card value */
    BigDecimal faceValue
    /** Empty string */
    String giftCertificateID = ''

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents an SES:Voucher element within LoyaltyReward.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesVoucher {
    /** Coupon number */
    String sesVoucherID
    /** Coupon amount */
    BigDecimal sesAmount
    /** Coupon serial (optional); created only if coupon serial exists */
    SesCouponSerial sesCouponSerial
}

/**
 * Represents SES:CouponSerial within SES:Voucher.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesCouponSerial {
    /** The coupon serial number */
    String sesCouponSerialNumber
    /**
     * Whether the coupon redemption was successfully posted to the Couponing Service (optional).
     * Possible values: null (no service), "00" (ok), "01" (error)
     */
    String sesBookingSuccessfulTypeCode
    /** The couponing service transaction ID (optional) */
    String sesBookingTransactionUUID

    /** CST:XXCustom01 ... CST:XXCustom30 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a receipt position addon entry within SES:ReceiptPositionAddonList.
 * Corresponds to the SES:ReceiptPositionAddonList section in 4.3.6.1.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesReceiptPositionAddon {
    /** Addon position (optional) */
    String addonPos
    /** Addon name (key) */
    String key
    /** Addon text (value) */
    String value

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}


// ============================================================
// Script entry point
// ============================================================

def user     = System.properties.'mongo.primary-server-user'
def password = System.properties.'mongo.primary-server-password'

// Map the source tree to a POSLog object
def posLog = POSLogJsonMapper.map(source)

// Serialize the POSLog to JSON and wrap it as a BSON Document
def json     = JsonOutput.toJson(posLog)
def document = Document.parse(json)

// Build the MongoDB client with credentials (auth database: admin)
def credential = MongoCredential.createCredential(
    user,
    'admin',
    password.toCharArray()
)

def settings = MongoClientSettings.builder()
    .applyToClusterSettings { builder ->
        builder.hosts([new ServerAddress('127.0.0.1', 27017)])
    }
    .credential(credential)
    .build()

def mongoClient = MongoClients.create(settings)

try {
    def collection = mongoClient
        .getDatabase('global_process_storage')
        .getCollection('gkflex_data')
    collection.insertOne(document)
} finally {
    mongoClient.close()
}
