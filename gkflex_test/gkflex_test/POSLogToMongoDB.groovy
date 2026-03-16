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
def newBinaryData = { [customFields: [:]] }



/**
 * Represents SES:CashierStatistics within ControlTransaction.
 * Statistics about the accounting period of a drawer.
 * Corresponds to section 4.3.8.5 of the GK POSLog Structure v3.
 */
def newSesCashierStatistics = { [:] }

/**
 * Represents the SessionSettle container within SesCashierStatistics.
 */
def newCashierSessionSettle = { [:] }

/**
 * Represents TotalMeasures within CashierSessionSettle.
 * Corresponds to section 4.3.8.5 of the GK POSLog Structure v3.
 */
def newTotalMeasures = { [:] }

/**
 * Represents LineItemVoids within CashierSessionSettle.
 */
def newLineItemVoids = { [:] }

/**
 * Represents PostTransactionVoids within CashierSessionSettle.
 */
def newPostTransactionVoids = { [:] }

/**
 * Represents SES:TransactionCancellations within CashierSessionSettle.
 */
def newSesTransactionCancellations = { [:] }

/**
 * Represents SES:DirectLineItemVoids within CashierSessionSettle.
 */
def newSesDirectLineItemVoids = { [:] }



/**
 * Represents a ControlTransaction in the POSLog export.
 * Corresponds to section 4.3.8 of the GK POSLog Structure v3.
 */
def newControlTransaction = { [sesTransactionLinks: [], customFields: [:]] }

/**
 * Represents BusinessEOD within ControlTransaction.
 * Corresponds to section 4.3.8.1 of the GK POSLog Structure v3.
 */
def newBusinessEOD = { [:] }

/**
 * Represents OperatorSignOn within ControlTransaction.
 * Corresponds to section 4.3.8.3 of the GK POSLog Structure v3.
 */
def newOperatorSignOn = { [:] }

/**
 * Represents OperatorSignOff within ControlTransaction.
 * Corresponds to section 4.3.8.4 of the GK POSLog Structure v3.
 */
def newOperatorSignOff = { [:] }

/**
 * Represents SES:TaxRefund within ControlTransaction.
 * Corresponds to section 4.3.8.7 of the GK POSLog Structure v3.
 */
def newSesTaxRefund = { [customFields: [:]] }



/**
 * Represents a single LineItem within RetailTransaction.
 * Corresponds to section 4.3.6.1 of the GK POSLog Structure v3.
 *
 * The choice among sale, returnItem, rounding, sesRounding, voids, loyaltyReward,
 * tax, and tender determines the line item type.
 */
def newLineItem = { [sesReceiptPositionAddonList: [], sesLineItemBinaryDataList: [], customFields: [:]] }



/**
 * Root container for the POSLog export.
 * Holds a list of Transaction elements as described in the GK POSLog v3 specification.
 */
def newPOSLog = { [transactions: []] }


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
 * <p>The mapping logic is implemented as script-scope closures (mapTransaction, etc.).
 */


    // =========================================================================
    // Transaction
    // =========================================================================

def mapTransaction = { txNode ->
        def tx = newTransaction()

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

def mapRetailTransaction = { rtNode ->
        def rt = newRetailTransaction()

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

def mapLineItem = { liNode ->
        def li = newLineItem()

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
            li.rounding = newRounding()
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

def mapSale = { saleNode ->
        def sale = newSale()
        populateSaleFields(sale, saleNode)
        return sale
    }

def mapReturn = { returnNode ->
        def ret = newReturn()
        populateSaleFields(ret, returnNode)

        def retLinkNode = returnNode.ReturnTransactionLink
        if (retLinkNode != null) {
            ret.returnTransactionLink = mapTransactionLink(retLinkNode)
        }
        ret.sesReturnReasonCode = toStr(returnNode.ReturnReasonCode)

        return ret
    }

    /** Populates Sale fields (and by inheritance Return fields) from a JSON node. */
def populateSaleFields = { sale, saleNode ->
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
            sale.sesItemLink = [linkType: itemLinkNode._attributes?.LinkType ?: null]
        }

        saleNode.forEach('ExternalReceiptReference') { errNode ->
            sale.sesExternalReceiptReferenceList << mapExternalReceiptReference(errNode)
        }

        def giftCertDataNode = saleNode.GiftCertificateData
        if (giftCertDataNode != null) {
            sale.sesGiftCertificateData = [sesGiftCertificateType: giftCertDataNode._attributes?.GiftCertificateType ?: null]
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

def mapPOSIdentity = { node ->
        def pi = newPOSIdentity()
        def attrs = node._attributes
        if (attrs) pi.posIDType = attrs.POSIDType ?: null
        pi.posItemID  = toStr(node.POSItemID)
        pi.qualifier  = toStr(node.Qualifier)
        return pi
    }

    // =========================================================================
    // Associate
    // =========================================================================

def mapAssociate = { node ->
        def assoc = newAssociate()
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

def mapRetailPriceModifier = { node ->
        def rpm = newRetailPriceModifier()
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

def mapPriceDerivationRule = { node ->
        def pdr = newPriceDerivationRule()
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
            def elig = newEligibility()
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

def mapSesLoyaltyReward = { node ->
        def lr = newSesLoyaltyReward()
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

def mapSalesOrderData = { node ->
        def sod = newSesSalesOrderData()
        sod.sesSpecialOrderSystem          = toStr(node.SpecialOrderSystem)
        sod.sesSpecialOrderType            = toStr(node.SpecialOrderType)
        sod.sesSpecialOrderPositionNumber  = toStr(node.SpecialOrderPositionNumber)
        mapCustomFields(sod.customFields, node, 15)
        return sod
    }

    // =========================================================================
    // ExternalReceiptReference
    // =========================================================================

def mapExternalReceiptReference = { node ->
        def err = newSesExternalReceiptReference()
        err.sesItemOrigin                            = toStr(node.ItemOrigin)
        err.sesReceiptReferenceNumber                = toStr(node.ReceiptReferenceNumber)
        err.sesExternalTransactionOfflineRedemptionFlag = toBool(node.ExternalTransactionOfflineRedemptionFlag)
        mapCustomFields(err.customFields, node, 5)
        return err
    }

    // =========================================================================
    // Tax
    // =========================================================================

def mapTax = { taxNode ->
        def tax = newTax()
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
            def te = newTaxExemption()
            te.customerExemptionID = toStr(taxExemptNode.CustomerExemptionID)
            te.exemptTaxAmount     = toDecimal(taxExemptNode.ExemptTaxAmount) ?: BigDecimal.ZERO
            te.reasonCode          = toStr(taxExemptNode.ReasonCode)
            tax.taxExemption       = te
        }

        def taxOverrideNode = taxNode.TaxOverride
        if (taxOverrideNode != null) {
            def to = newTaxOverride()
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

def mapTender = { tenderNode ->
        def tender = newTender()

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
            def tc = newTenderChange()
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
            def lr = newSesLoyaltyRedemption()
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

def mapAuthorization = { authNode ->
        def auth = newAuthorization()

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

def mapCreditDebit = { node ->
        def cd = newCreditDebit()
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

def mapCheck = { node ->
        def check = newCheck()
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

def mapForeignCurrency = { node ->
        def fc = newForeignCurrency()
        fc.currencyCode       = toStr(node.CurrencyCode)
        fc.originalFaceAmount = toDecimal(node.OriginalFaceAmount)
        fc.exchangeRate       = toDecimal(node.ExchangeRate)
        return fc
    }

    // =========================================================================
    // Coupon
    // =========================================================================

def mapCoupon = { node ->
        def coupon = newCoupon()
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
                def ri = newSesReferenceItem()
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

def mapVoucher = { node ->
        def voucher = newVoucher()
        def attrs = node._attributes
        if (attrs) voucher.typeCode = attrs.TypeCode ?: null
        voucher.serialNumber = toStr(node.SerialNumber)
        return voucher
    }

    // =========================================================================
    // GiftCertificate / SesVoucher (within LoyaltyReward)
    // =========================================================================

def mapGiftCertificate = { node ->
        def gc = newGiftCertificate()
        def attrs = node._attributes
        if (attrs) gc.mediaType = attrs.MediaType ?: null
        gc.serialNumber      = toStr(node.SerialNumber)
        gc.faceValue         = toDecimal(node.FaceValue)
        gc.giftCertificateID = toStr(node.GiftCertificateID) ?: ''
        mapCustomFields(gc.customFields, node, 5)
        return gc
    }

def mapSesVoucher = { node ->
        def voucher = newSesVoucher()
        voucher.sesVoucherID = toStr(node.VoucherID)
        voucher.sesAmount    = toDecimal(node.Amount)

        def couponSerialNode = node.CouponSerial
        if (couponSerialNode != null) {
            def cs = newSesCouponSerial()
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

def mapLoyaltyReward = { node ->
        def lr = newLoyaltyReward()
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

def mapSesRounding = { node ->
        def sr = newSesRounding()
        sr.sesRoundingTypeCode  = toStr(node.RoundingTypeCode)
        sr.sesAmount            = toDecimal(node.Amount)
        sr.sesRoundingDirection = toStr(node.RoundingDirection)
        mapCustomFields(sr.customFields, node, 5)
        return sr
    }

    // =========================================================================
    // Voids
    // =========================================================================

def mapVoids = { node ->
        def voids = newVoids()
        def itemLinkNode = node.ItemLink
        if (itemLinkNode != null) {
            def il = newItemLink()
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

def mapOperatorBypassApproval = { node ->
        def approval = newSesOperatorBypassApproval()
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

def mapAddon = { addonNode ->
        def addon = newAddon()
        addon.addonPos = toStr(addonNode.AddonPos)
        addon.key      = toStr(addonNode.Key)
        addon.value    = toStr(addonNode.Value)
        mapCustomFields(addon.customFields, addonNode, 5)
        return addon
    }

    // =========================================================================
    // SesReceiptPositionAddon (line item level)
    // =========================================================================

def mapPositionAddon = { addonNode ->
        def addon = newSesReceiptPositionAddon()
        addon.addonPos = toStr(addonNode.AddonPos)
        addon.key      = toStr(addonNode.Key)
        addon.value    = toStr(addonNode.Value)
        mapCustomFields(addon.customFields, addonNode, 5)
        return addon
    }

    // =========================================================================
    // Timer
    // =========================================================================

def mapTimer = { timerNode ->
        def timer = newTimer()
        timer.timerID        = toStr(timerNode.TimerID)
        timer.startTimestamp = toStr(timerNode.StartTimestamp)
        timer.duration       = toDecimal(timerNode.Duration)
        return timer
    }

    // =========================================================================
    // BinaryData
    // =========================================================================

def mapBinaryData = { bdNode ->
        def bd = newBinaryData()
        bd.name    = toStr(bdNode.Name)
        bd.content = toStr(bdNode.Content)
        mapCustomFields(bd.customFields, bdNode, 5)
        return bd
    }

    // =========================================================================
    // TransactionLink
    // =========================================================================

def mapTransactionLink = { node ->
        def tl = newTransactionLink()
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

def mapCustomer = { node ->
        def customer = newCustomer()
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
            def cn = newCustomerName()
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
            def addr = newAddress()
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
            def tele = newTelephone()
            tele.typeCode            = teleNode._attributes?.TypeCode ?: null
            tele.fullTelephoneNumber = toStr(teleNode.FullTelephoneNumber)
            customer.telephone = tele
        }

        def emailNode = node.Email
        if (emailNode != null) {
            customer.email = [emailAddress: toStr(emailNode.EmailAddress)]
        }

        mapCustomFields(customer.customFields, node, 5)
        return customer
    }

    // =========================================================================
    // SesCouponSummary
    // =========================================================================

def mapCouponSummary = { node ->
        def cs = newSesCouponSummary()
        cs.sesCouponNumber            = toStr(node.CouponNumber)
        cs.sesCustomerID              = toStr(node.CustomerID)
        cs.sesCustomerAddressTypeCode = toStr(node.CustomerAddressTypeCode)

        def inputCntStr = toStr(node.InputCount)
        if (inputCntStr) cs.sesInputCount = inputCntStr as Integer

        def appliedCntStr = toStr(node.AppliedCount)
        if (appliedCntStr) cs.sesAppliedCount = appliedCntStr as Integer

        node.forEach('CouponSerialSummary') { cssNode ->
            def css = newSesCouponSerialSummary()
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

def mapLoyaltyAccount = { node ->
        def la = newSesLoyaltyAccount()
        la.customerID = toStr(node.CustomerID)
        node.forEach('LoyaltyProgram') { lpNode ->
            def lp = newLoyaltyProgram()
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

def mapTenderControlTransaction = { node ->
        def tct = newTenderControlTransaction()

        tct.sesAccountedOperatorID   = toStr(node.AccountedOperatorID)
        tct.sesAccountedOperatorName = node.AccountedOperatorID?._attributes?.Name ?: null

        def tenderLoanNode = node.TenderLoan
        if (tenderLoanNode != null) {
            def tl = newSesTenderLoan()
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
            def dep = newDeposit()
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
                    def dd = newDepositDetail()
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
            def ts = newTillSettle()
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
            def sd = newSafeDrop()
            sd.dropAmount  = toDecimal(safeDropNode.DropAmount)
            sd.envelopeID  = toStr(safeDropNode.EnvelopeID)  ?: ''
            sd.dropNumber  = toStr(safeDropNode.DropNumber)   ?: ''
            tct.safeDrop = sd
        }

        def tenderPickupNode = node.TenderPickup
        if (tenderPickupNode != null) {
            def tp = newSesTenderPickup()
            tp.sesTotalAmount         = toDecimal(tenderPickupNode.TotalAmount)
            tp.sesTotalAmountTypeCode = tenderPickupNode.TotalAmount?._attributes?.TypeCode ?: null
            tp.sesEnvelopeID          = toStr(tenderPickupNode.EnvelopeID)
            def tpaListNode = tenderPickupNode.TenderAmountList
            if (tpaListNode != null) {
                tpaListNode.forEach('TenderAmount') { taNode ->
                    def ta = newSesTenderAmount()
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
            def pi = newSesPaidIn()
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
            def po = newSesPaidOut()
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
            def tlcf = newSesTenderLoanCarriedForward()
            tlcf.amount = toDecimal(tenderLoanCarriedForwardNode.Amount)
            mapCustomFields(tlcf.customFields, tenderLoanCarriedForwardNode, 5)
            tct.sesTenderLoanCarriedForward = tlcf
        }

        def safeSettleNode = node.SafeSettle
        if (safeSettleNode != null) {
            def ss = newSesSafeSettle()
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

def mapSesTotals = { node ->
        def totals = newSesTotals()
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

def mapTenderSummary = { node ->
        def ts = newTenderSummary()

        def overNode = node.Over
        if (overNode != null) {
            def over = newOver()
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
            def shortSum = newShortSummary()
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
            def nom = newSesNominal()
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
            def ending = newSesEnding()
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

def mapControlTransaction = { node ->
        def ct = newControlTransaction()
        ct.reasonCode                  = toStr(node.ReasonCode)
        ct.sesOtherTransactionType     = toStr(node.OtherTransactionType)
        ct.sesForcedSignOffFlag        = toBool(node.ForcedSignOffFlag)
        ct.sesForcedSignOffWorkstationID = toStr(node.ForcedSignOffWorkstationID)
        ct.sesAccountedOperatorID      = toStr(node.AccountedOperatorID)
        ct.sesAccountedOperatorName    = node.AccountedOperatorID?._attributes?.Name ?: null
        ct.noSaleTimestamp             = toStr(node.NoSaleTimestamp)

        def businessEODNode = node.BusinessEOD
        if (businessEODNode != null) {
            ct.businessEOD = [startDateTimestamp: toStr(businessEODNode.StartDateTimestamp)]
        }

        def tillEODNode = node.TillEOD
        if (tillEODNode != null) {
            ct.tillEOD = mapTillEOD(tillEODNode)
        }

        def signOnNode = node.OperatorSignOn
        if (signOnNode != null) {
            ct.operatorSignOn = [startDateTimestamp: toStr(signOnNode.StartDateTimestamp)]
        }

        def signOffNode = node.OperatorSignOff
        if (signOffNode != null) {
            ct.operatorSignOff = [startDateTimestamp: toStr(signOffNode.StartDateTimestamp)]
        }

        def taxRefundNode = node.TaxRefund
        if (taxRefundNode != null) {
            def tr = newSesTaxRefund()
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

def mapTillEOD = { node ->
        def tillEOD = newTillEOD()
        def sessionSettleNode = node.SessionSettle
        if (sessionSettleNode != null) {
            def ss = newTillEODSessionSettle()
            sessionSettleNode.forEach('TenderSummary') { tsSummaryNode ->
                def tts = newTillEODTenderSummary()
                def beginningNode = tsSummaryNode.Beginning
                if (beginningNode != null) {
                    def beg = newBeginning()
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
                    def pu = newPickup()
                    def puAttrs = pickupNode._attributes
                    if (puAttrs) pu.tenderType = puAttrs.TenderType ?: null
                    pu.amount = toDecimal(pickupNode.Amount)
                    def puCountStr = toStr(pickupNode.Count)
                    if (puCountStr) pu.count = puCountStr as Integer
                    tts.pickup = pu
                }

                def salesTenderNominalNode = tsSummaryNode.SalesTenderNominal
                if (salesTenderNominalNode != null) {
                    def stn = newSesSalesTenderNominal()
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
                def salesSum = newSesSalesSummary()
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
                def taxSum = newSesTaxSummary()
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

def mapCashierStatistics = { node ->
        def cs = newSesCashierStatistics()
        def sessionSettleNode = node.SessionSettle
        if (sessionSettleNode != null) {
            def ss = newCashierSessionSettle()
            def txCntStr = toStr(sessionSettleNode.TransactionCount)
            if (txCntStr) ss.transactionCount = txCntStr as Integer

            def totalMeasuresNode = sessionSettleNode.TotalMeasures
            if (totalMeasuresNode != null) {
                def tm = newTotalMeasures()
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
                def liv = newLineItemVoids()
                liv.amount = toDecimal(liVoidsNode.Amount)
                def cntStr = toStr(liVoidsNode.Count)
                if (cntStr) liv.count = cntStr as Integer
                ss.lineItemVoids = liv
            }

            def ptVoidsNode = sessionSettleNode.PostTransactionVoids
            if (ptVoidsNode != null) {
                def ptv = newPostTransactionVoids()
                ptv.amount = toDecimal(ptVoidsNode.Amount)
                def cntStr = toStr(ptVoidsNode.Count)
                if (cntStr) ptv.count = cntStr as Integer
                ss.postTransactionVoids = ptv
            }

            def txCancNode = sessionSettleNode.TransactionCancellations
            if (txCancNode != null) {
                def tc = newSesTransactionCancellations()
                tc.amount = toDecimal(txCancNode.Amount)
                def cntStr = toStr(txCancNode.Count)
                if (cntStr) tc.count = cntStr as Integer
                ss.sesTransactionCancellations = tc
            }

            def directVoidsNode = sessionSettleNode.DirectLineItemVoids
            if (directVoidsNode != null) {
                def dliv = newSesDirectLineItemVoids()
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

def mapStoreEODSummary = { node ->
        def storeEOD = newSesStoreEODSummary()
        def sessionSettleNode = node.SessionSettle
        if (sessionSettleNode != null) {
            def ss = newStoreEODSessionSettle()
            sessionSettleNode.forEach('SalesSummary') { salesSumNode ->
                def salesSum = newSesSalesSummary()
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
                def taxSum = newSesTaxSummary()
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
def toStr = { node ->
        if (node == null) return null
        def s = node.toString()
        return (s != null && !s.isEmpty()) ? s : null
    }

    /**
     * Safely converts a node's text value to Boolean.
     * Returns null when the node is absent or its text is empty.
     */
def toBool = { node ->
        if (node == null) return null
        def s = node.toString()
        if (s == null || s.isEmpty()) return null
        return s == 'true'
    }

    /**
     * Safely converts a node's text value to BigDecimal.
     * Returns null when the node is absent or its text is empty.
     */
def toDecimal = { node ->
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
def mapCustomFields = { target, node, count ->
        for (int i = 1; i <= count; i++) {
            def fieldName = i < 10 ? "XXCustom0${i}" : "XXCustom${i}"
            def fieldNode = node."${fieldName}"
            if (fieldNode != null) {
                def val = fieldNode.toString()
                if (val != null) target[fieldName] = val
            }
        }
    }



/**
 * Represents a RetailTransaction in the POSLog export.
 * Corresponds to section 4.3.6 of the GK POSLog Structure v3.
 */
def newRetailTransaction = { [lineItems: [], totalCustomFields: [:], sesCouponSummaryList: [], customFields: [:]] }



/**
 * Represents Customer data within RetailTransaction.
 * Corresponds to section 4.3.6.3 of the GK POSLog Structure v3.
 */
def newCustomer = { [customFields: [:]] }

/**
 * Represents customer name data within Customer.
 */
def newCustomerName = { [:] }

/**
 * Represents customer address data within Customer.
 */
def newAddress = { [:] }

/**
 * Represents customer telephone data within Customer.
 */
def newTelephone = { [:] }

/**
 * Represents customer email address data within Customer.
 */
def newEmail = { [:] }

/**
 * Represents SES:CouponSummary within RetailTransaction.
 * Corresponds to section 4.3.6.5 of the GK POSLog Structure v3.
 */
def newSesCouponSummary = { [sesCouponSerialSummaries: [], customFields: [:]] }

/**
 * Represents SES:CouponSerialSummary within SES:CouponSummary.
 */
def newSesCouponSerialSummary = { [customFields: [:]] }



/**
 * Represents a Sale line item in a RetailTransaction.
 * Created in case of sales transaction (no returns, no empties, no voids).
 * Corresponds to the Sale section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newSale = { [retailPriceModifiers: [], sesExternalReceiptReferenceList: [], customFields: [:]] }

/**
 * Represents a Return line item in a RetailTransaction.
 * Shares the same structure as Sale with minor deviations.
 * Corresponds to the Return section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newReturn = { newSale() }

/**
 * Represents a POSIdentity element within Sale.
 */
def newPOSIdentity = { [:] }

/**
 * Represents an Associate (seller) element within Sale.
 */
def newAssociate = { [:] }

/**
 * Represents a RetailPriceModifier element within Sale.
 */
def newRetailPriceModifier = { [customFields: [:]] }

/**
 * Represents a PriceDerivationRule element within RetailPriceModifier or LoyaltyReward.
 */
def newPriceDerivationRule = { [customFields: [:]] }

/**
 * Represents an Eligibility element within PriceDerivationRule.
 */
def newEligibility = { [customFields: [:]] }

/**
 * Represents SES:ItemLink within Sale (link to empties item).
 */
def newSesItemLink = { [:] }

/**
 * Represents SES:LoyaltyReward within Sale (position-specific bonus points).
 */
def newSesLoyaltyReward = { [customFields: [:]] }

/**
 * Represents SES:GiftCertificateData within Sale (additional gift certificate data).
 * Corresponds to the SES:GiftCertificateData section in the GK POSLog Structure v3.
 */
def newSesGiftCertificateData = { [customFields: [:]] }

/**
 * Represents SES:SalesOrderData within Sale (additional sales order data).
 * Corresponds to the SES:SalesOrderData section in the GK POSLog Structure v3.
 */
def newSesSalesOrderData = { [customFields: [:]] }

/**
 * Represents a single entry in SES:ExternalReceiptReferenceList within Sale.
 * Corresponds to the SES:ExternalReceiptReferenceList section in the GK POSLog Structure v3.
 */
def newSesExternalReceiptReference = { [customFields: [:]] }



/**
 * Represents SES:StoreEODSummary within ControlTransaction.
 * Total/summary values (store related).
 * Corresponds to section 4.3.8.6 of the GK POSLog Structure v3.
 */
def newSesStoreEODSummary = { [:] }

/**
 * Represents the SessionSettle container within SesStoreEODSummary.
 */
def newStoreEODSessionSettle = { [sesSalesSummaries: [], sesTaxSummaries: []] }



/**
 * Represents a Tax element in a LineItem (line item level or transaction level).
 * Corresponds to the Tax sections in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newTax = { [customFields: [:]] }

/**
 * Represents a TaxExemption element within Tax.
 */
def newTaxExemption = { [exemptTaxAmount: BigDecimal.ZERO, customFields: [:]] }

/**
 * Represents a TaxOverride element within Tax.
 */
def newTaxOverride = { [customFields: [:]] }



/**
 * Represents a TenderControlTransaction in the POSLog export.
 * Corresponds to section 4.3.7 of the GK POSLog Structure v3.
 *
 * The choice among the various sub-elements determines the type of tender control transaction.
 */
def newTenderControlTransaction = { [sesReceiptPositionAddonList: [], customFields: [:]] }

/**
 * Represents SES:TenderLoan within TenderControlTransaction.
 * Tender transfer from safe to drawer.
 * Corresponds to section 4.3.7.1 of the GK POSLog Structure v3.
 */
def newSesTenderLoan = { [customFields: [:]] }

/**
 * Represents SES:Totals within SesTenderLoan.
 */
def newSesTotals = { [customFields: [:]] }

/**
 * Represents a Deposit element within TenderControlTransaction.
 * Safe bag or bank deposit – tender transfer from safe to bank.
 * Corresponds to section 4.3.7.2 of the GK POSLog Structure v3.
 */
def newDeposit = { [bank: '', account: '', depositDetails: [], sesDepositForeignCurrencies: [], customFields: [:]] }

/**
 * Represents DepositDetail within Deposit.
 */
def newDepositDetail = { [reason: '0000'] }

/**
 * Represents SES:DepositForeignCurrency within Deposit.
 */
def newSesDepositForeignCurrency = { [:] }

/**
 * Represents a TillSettle element within TenderControlTransaction.
 * Drawer settlement or drawer cash check.
 * Corresponds to section 4.3.7.3 of the GK POSLog Structure v3.
 */
def newTillSettle = { [tenderSummaries: [], customFields: [:]] }

/**
 * Represents SafeDrop within TenderControlTransaction.
 * Cash transfer from bank to safe.
 * Corresponds to section 4.3.7.4 of the GK POSLog Structure v3.
 */
def newSafeDrop = { [envelopeID: '', dropNumber: ''] }

/**
 * Represents SES:TenderPickup within TenderControlTransaction.
 * Tender transfer from drawer to safe.
 * Corresponds to section 4.3.7.5 of the GK POSLog Structure v3.
 */
def newSesTenderPickup = { [sesTenderAmounts: [], customFields: [:]] }

/**
 * Represents a single SES:TenderAmount within SesTenderPickup.
 */
def newSesTenderAmount = { [customFields: [:]] }

/**
 * Represents SES:PaidIn within TenderControlTransaction.
 * Paid-in, safe opening balance, or safe correction pay-in transaction.
 * Corresponds to section 4.3.7.6 of the GK POSLog Structure v3.
 */
def newSesPaidIn = { [sesTenders: [], customFields: [:]] }

/**
 * Represents SES:PaidOut within TenderControlTransaction.
 * Paid-out or safe correction pay-out transaction.
 * Corresponds to section 4.3.7.7 of the GK POSLog Structure v3.
 */
def newSesPaidOut = { [tenders: [], customFields: [:]] }

/**
 * Represents SES:TenderLoanCarriedForward within TenderControlTransaction.
 * Tender amount in the drawer at the beginning of an accounting period.
 * Corresponds to section 4.3.7.9 of the GK POSLog Structure v3.
 */
def newSesTenderLoanCarriedForward = { [customFields: [:]] }

/**
 * Represents SES:SafeSettle within TenderControlTransaction.
 * Safe settlement / safe accounting transaction.
 * Corresponds to section 4.3.7.10 of the GK POSLog Structure v3.
 */
def newSesSafeSettle = { [tenderSummaries: [], customFields: [:]] }



/**
 * Represents a Tender element in a LineItem.
 * Created for each used tender.
 * Corresponds to the Tender section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newTender = { [customFields: [:]] }

/**
 * Represents a TenderChange element within Tender.
 */
def newTenderChange = { [:] }

/**
 * Represents an Authorization element within Tender.
 * Corresponds to the Authorization section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newAuthorization = { [customFields: [:]] }

/**
 * Represents a ForeignCurrency element within Tender.
 */
def newForeignCurrency = { [:] }

/**
 * Represents a Check element within Tender.
 * Corresponds to the Check section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newCheck = { [customFields: [:]] }

/**
 * Represents a CreditDebit element within Tender.
 * Corresponds to the CreditDebit section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newCreditDebit = { [:] }

/**
 * Represents a Coupon element within Tender.
 * Corresponds to the Coupon section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newCoupon = { [quantity: 1, sesReferenceItemList: []] }

/**
 * Represents a single entry in SES:ReferenceItemList within Coupon.
 */
def newSesReferenceItem = { [itemLinkType: 'Coupon'] }

/**
 * Represents a Voucher element within Tender.
 */
def newVoucher = { [:] }

/**
 * Represents SES:LoyaltyRedemption within Tender.
 * Corresponds to the SES:LoyaltyRedemption section in 4.3.6.1.
 */
def newSesLoyaltyRedemption = { [customFields: [:]] }



/**
 * Represents TillEOD within ControlTransaction.
 * Total/summary values (drawer related).
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
def newTillEOD = { [:] }

/**
 * Represents the SessionSettle container within TillEOD.
 */
def newTillEODSessionSettle = { [tenderSummaries: [], sesSalesSummaries: [], sesTaxSummaries: []] }

/**
 * Represents TenderSummary within TillEOD's SessionSettle.
 * Contains opening/closing/pickup/over/short/ending balance per tender.
 */
def newTillEODTenderSummary = { [:] }

/**
 * Represents Beginning within TillEODTenderSummary (opening balance).
 */
def newBeginning = { [:] }

/**
 * Represents Pickup within TillEODTenderSummary (manual pickup amount).
 */
def newPickup = { [:] }

/**
 * Represents Over within TillEODTenderSummary (positive difference).
 */
def newEODOver = { [:] }

/**
 * Represents Short within TillEODTenderSummary (negative difference).
 * Named EODShort to avoid collision with java.lang.Short.
 */
def newEODShort = { [:] }

/**
 * Represents SES:SalesTenderNominal within TillEODTenderSummary.
 * Tender totals of all sales/return receipts.
 */
def newSesSalesTenderNominal = { [:] }

/**
 * Represents SES:SalesSummary within TillEOD or SesStoreEODSummary SessionSettle.
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
def newSesSalesSummary = { [reasonName: 'SalesID'] }

/**
 * Represents SES:TaxSummary within TillEOD or SesStoreEODSummary SessionSettle.
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
def newSesTaxSummary = { [:] }



/**
 * Represents a TenderSummary element used in TillSettle and SesSafeSettle.
 * Corresponds to the TenderSummary section in 4.3.7.3 and 4.3.7.10.
 */
def newTenderSummary = { [customFields: [:]] }

/**
 * Represents an Over element within TenderSummary (positive count difference).
 */
def newOver = { [:] }

/**
 * Represents a Short element within TenderSummary (negative count difference).
 * Named ShortSummary to avoid collision with java.lang.Short.
 */
def newShortSummary = { [:] }

/**
 * Represents SES:Nominal within TenderSummary (closing/target balance).
 */
def newSesNominal = { [:] }

/**
 * Represents SES:Ending within TenderSummary (counted/actual balance).
 */
def newSesEnding = { [:] }



/**
 * Represents a single Transaction in the POSLog export.
 * Corresponds to section 4.3 of the GK POSLog Structure v3 specification.
 *
 * The choice between retailTransaction, tenderControlTransaction and controlTransaction
 * determines the type of transaction.
 */
def newTransaction = { [majorVersion: 3, minorVersion: 0, fixVersion: 0, sesInternalMajorVersion: 2, sesInternalMinorVersion: 5, sesInternalFixVersion: 9, sesReceiptHeaderAddonList: [], sesReceiptTimerList: [], sesTransactionBinaryDataList: [], customFields: [:]] }



/**
 * Represents approval by another operator (SES:OperatorBypassApproval).
 * Used in Transaction and LineItem.
 * Corresponds to section 4.3.1 of the GK POSLog Structure v3.
 */
def newSesOperatorBypassApproval = { [customFields: [:]] }

/**
 * Represents a single addon entry within SES:ReceiptHeaderAddonList.
 * Corresponds to section 4.3.2.1 of the GK POSLog Structure v3.
 */
def newAddon = { [customFields: [:]] }

/**
 * Represents a single timer entry within SES:ReceiptTimerList.
 * Corresponds to section 4.3.3.1 of the GK POSLog Structure v3.
 */
def newTimer = { [:] }

/**
 * Represents SES:LoyaltyAccount on transaction level.
 * Corresponds to section 4.3.4 of the GK POSLog Structure v3.
 */
def newSesLoyaltyAccount = { [loyaltyPrograms: []] }

/**
 * Represents a loyalty program within SES:LoyaltyAccount.
 * Corresponds to section 4.3.4.1 of the GK POSLog Structure v3.
 */
def newLoyaltyProgram = { [customFields: [:]] }



/**
 * Represents a shared link to another transaction.
 * Used within RetailTransaction, TenderControlTransaction, and ControlTransaction.
 * Corresponds to multiple TransactionLink sections in the GK POSLog Structure v3.
 */
def newTransactionLink = { [:] }



/**
 * Represents a Rounding element in a LineItem.
 * Rounding total; created only when there is an existing rounding difference
 * for the sum of subtotal rounding and tender change rounding.
 */
def newRounding = { [:] }

/**
 * Represents an SES:Rounding element in a LineItem.
 * Rounding line item; created only when there is an existing rounding difference
 * for subtotal rounding or tender change rounding.
 * Corresponds to the SES:Rounding section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newSesRounding = { [customFields: [:]] }

/**
 * Represents a Voids element in a LineItem.
 * Created only in case of a void position.
 * Corresponds to the Voids section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newVoids = { [:] }

/**
 * Represents an ItemLink element within Voids.
 */
def newItemLink = { [reasonCode: 'Voided', customFields: [:]] }

/**
 * Represents a LoyaltyReward element in a LineItem.
 * Created only in case of non-financial bonus.
 * Corresponds to the LoyaltyReward section in 4.3.6.1 of the GK POSLog Structure v3.
 */
def newLoyaltyReward = { [customFields: [:]] }

/**
 * Represents a GiftCertificate element within LoyaltyReward.
 */
def newGiftCertificate = { [giftCertificateID: '', customFields: [:]] }

/**
 * Represents an SES:Voucher element within LoyaltyReward.
 */
def newSesVoucher = { [:] }

/**
 * Represents SES:CouponSerial within SES:Voucher.
 */
def newSesCouponSerial = { [customFields: [:]] }

/**
 * Represents a receipt position addon entry within SES:ReceiptPositionAddonList.
 * Corresponds to the SES:ReceiptPositionAddonList section in 4.3.6.1.
 */
def newSesReceiptPositionAddon = { [customFields: [:]] }


// ============================================================
// Script entry point
// ============================================================

def user     = System.properties.'mongo.primary-server-user'
def password = System.properties.'mongo.primary-server-password'

// Map the source tree to a POSLog object
def posLog = newPOSLog()
def posLogNode = source.POSLog
posLogNode.forEach 'Transaction', { txNode ->
    posLog.transactions << mapTransaction(txNode)
}

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
