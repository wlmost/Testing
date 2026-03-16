package poslog

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
        posLogNode.forEach('Transaction') { txNode ->
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
            headerAddonListNode.forEach('Addon') { addonNode ->
                tx.sesReceiptHeaderAddonList << mapAddon(addonNode)
            }
        }

        def timerListNode = txNode.ReceiptTimerList
        if (timerListNode != null) {
            timerListNode.forEach('Timer') { timerNode ->
                tx.sesReceiptTimerList << mapTimer(timerNode)
            }
        }

        def loyaltyAccountNode = txNode.LoyaltyAccount
        if (loyaltyAccountNode != null) {
            tx.sesLoyaltyAccount = mapLoyaltyAccount(loyaltyAccountNode)
        }

        def binaryDataListNode = txNode.TransactionBinaryDataList
        if (binaryDataListNode != null) {
            binaryDataListNode.forEach('BinaryData') { bdNode ->
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

        rtNode.forEach('LineItem') { liNode ->
            rt.lineItems << mapLineItem(liNode)
        }

        rtNode.forEach('Total') { totalNode ->
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
            couponSummaryListNode.forEach('CouponSummary') { csNode ->
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
