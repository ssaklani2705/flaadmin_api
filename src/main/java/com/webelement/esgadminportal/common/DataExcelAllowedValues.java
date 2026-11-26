package com.webelement.esgadminportal.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dropdown values accepted in the Excel upload. Keep in sync with the lists in add-data.component.ts.
 * Matching is case-insensitive and ignores spaces, e.g. "advances -other term loans" == "Advances-Other Term loans".
 */
public final class DataExcelAllowedValues {

    private DataExcelAllowedValues() {}

    public static String normalize(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
    }

    /** normalized value -> canonical value */
    public static Map<String, String> index(Collection<String> values) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) {
                map.put(normalize(v), v.trim());
            }
        }
        return map;
    }

    public static final List<String> BRANCH_CATEGORIES = Arrays.asList("Metro", "Urban", "Semi Urban", "Rural");

    public static final List<String> PERPETRATOR_STATUSES = Arrays.asList("Known", "Unknown");

    public static final List<String> YES_NO = Arrays.asList("Yes", "No");

    public static final List<String> BANKING_ARRANGEMENTS = Arrays.asList(
            "Consortium", "Multiple Banking Arrangement (MBA)", "Sole banking");

    public static final List<String> COMMITTED_BY = Arrays.asList("Staff", "Customers", "Outsiders");

    public static final List<String> ACCOUNT_TYPES = Arrays.asList(
            "Corporate",
            "Individuals/HUF",
            "Sole proprietorship",
            "Partnership firm",
            "Limited Liability Partnership firm",
            "Society/non-profit organisations (including NGOs)",
            "Trust",
            "Other");

//    public static final List<String> INDUSTRY_SECTORS = Arrays.asList(
//            "Crop and animal production, hunting and related service activitie",
//            "Forestry and logging",
//            "Fishing and aquaculture",
//            "Mining of coal and lignite",
//            "Extraction of crude petroleum and natural gas",
//            "Mining of metal ores",
//            "Other mining and quarrying",
//            "Manufacture of food products",
//            "Manufacture of beverages",
//            "Manufacture of tobacco products",
//            "Manufacture of textiles",
//            "Manufacture of wearing apparel",
//            "Manufacture of leather and related products and harness; dressing and dyeing of fur",
//            "Manufacture of wood and products of wood and cork, except furniture",
//            "Manufacture of paper and paper products",
//            "Printing and reproduction of recorded media",
//            "Manufacture of coke and refined petroleum products",
//            "Manufacture of chemicals and chemical products and synthetic rubber in primary forms",
//            "Manufacture of pharmaceuticals, medicinal chemical and botanical products",
//            "Manufacture of rubber and plastics products",
//            "Manufacture of other non-metallic mineral products",
//            "Manufacture of basic metals",
//            "Manufacture of fabricated metal products, except machinery and equipment",
//            "Manufacture of computer, electronic and optical products",
//            "Manufacture of electrical equipment");
    
    public static final List<String> INDUSTRY_SECTORS = Arrays.asList(
            "Division 01 Crop and animal production, hunting and related service activitie",
            "Division 02 Forestry and logging",
            "Division 03 Fishing and aquaculture",
            "Division 05 Mining of coal and lignite",
            "Division 06 Extraction of crude petroleum and natural gas",
            "Division 07 Mining of metal ores",
            "Division 08 Other mining and quarrying",
            "Division 10 Manufacture of food products",
            "Division 11 Manufacture of beverages",
            "Division 12 Manufacture of tobacco products",
            "Division 13 Manufacture of textiles",
            "Division 14 Manufacture of wearing apparel",
            "Division 15 Manufacture of leather and related products and harness; dressing and dyeing of fur",
            "Division 16 Manufacture of wood and products of wood and cork, except furniture",
            "Division 17 Manufacture of paper and paper products",
            "Division 18 Printing and reproduction of recorded media",
            "Division 19 Manufacture of coke and refined petroleum products",
            "Division 20 Manufacture of chemicals and chemical products and synthetic rubber in primary forms",
            "Division 21 Manufacture of pharmaceuticals, medicinal chemical and botanical products",
            "Division 22 Manufacture of rubber and plastics products",
            "Division 23 Manufacture of other non-metallic mineral products",
            "Division 24 Manufacture of basic metals",
            "Division 25 Manufacture of fabricated metal products, except machinery and equipment",
            "Division 26 Manufacture of computer, electronic and optical products",
            "Division 27 Manufacture of electrical equipment"
    );

    public static final List<String> AREAS_OF_OPERATION = Arrays.asList(
            "Advances-Agriculture",
            "Advances-Bills",
            "Advances-Cash Credit",
            "Advances-against gold/gold ornament",
            "Advances-MSME",
            "Advances-Vehicle",
            "advances-Housing Loan",
            "advances-Personal loans",
            "Advances-Educational loans",
            "Advances-Digital Lending (App based)",
            "Advances-credit card (Using Fake Documents)",
            "Advances-Other Term loans",
            "Digital Payments-Credit Card",
            "Digital Payments-Debit/ATM cards",
            "Digital Payments-Internet Banking",
            "Digital Payments-UPI",
            "Digital Payments-Wallet/Prepaid Cards",
            "Digital Payments-IMPS/NEFT/RTGS",
            "Digital Payments-AEPS/Other Aadhar Related Frauds",
            "Digital Payments-USSD",
            "Cash Shortage",
            "Cheque/Demand Drafts",
            "Clearing Operations",
            "Deposits-Current",
            "Deposits-Saving",
            "Deposits-Term",
            "Foreign Exchange Transactions",
            "Internal Accounts",
            "Off Balance sheet- Letter of credit",
            "Off Balance Sheet - LOU",
            "Off Balance Sheet - BG's",
            "Other");

    public static final List<String> FRAUD_CLASSIFICATIONS = Arrays.asList(
            "Misappropriation of funds and criminal breach of trust",
            "Fraudulent encashment through forged instruments",
            "Manipulation of books of accounts or through fictitious accounts and conversion of property",
            "Cheating by concealment of facts with the intention to deceive any person and cheating by impersonation",
            "Forgery with the intention to commit fraud by making any false documents/electronic records",
            "Willful falsification, destruction, alteration, mutilations of any book, electronic record, paper, writing, valuable security or account with intent to defraud",
            "Fraudulent credit facilities extended for illegal gratification",
            "Cash shortages on account of frauds",
            "Fraudulent transactions involving foreign exchange",
            "Fraudulent electronic banking/ digital payment related transactions",
            "Other");

    public static final List<String> FRAUD_TYPES = Arrays.asList(
            "Collateral (Fake title deeds, spurious gold and/or its appraisal, FDRs, Bank Guarantees, Multiple mortgages)",
            "Documents (Fake invoices, LCs, transport documents, shipping bills, warehouse receipts, creation of charge in RC books; etc)",
            "Financial statements (Stock statements, inventory, debtors/creditors/receivables statements, fabricated balance sheets, manipulation of accounts etc)",
            "Related Party transactions (Round tripping of funds, fake sales and purchases to inflate turnover)",
            "Clandestine sale of hypothecated/mortgaged security (Stocks, securities, machineries and equipment, vehicles, immovable property)",
            "Violations of FEMA / PMLA / Illegal claim of Input Credit in GST etc",
            "Non-resident accounts",
            "Fake KYC",
            "Overvaluation/Misrepresentation by Third Party Entities",
            "Cash Shortage-Cash Collection",
            "Cash Shortage-Embezzlement",
            "Cash Shortage-Manipulation",
            "Cash Shortage-Currency Chest",
            "Cheques/demand drafts-Fraudulent encashment",
            "Cheques/demand drafts-Cloned/ forged",
            "Cheques/demand drafts-Demand drafts",
            "Routing business transactions outside consortium/Non- routing through TRA & CC accounts",
            "Account Hacking / Compromise / Identity theft",
            "Phishing",
            "Vishing",
            "Smishing",
            "Remote Capture of Device",
            "Lost / Stolen Device / Instrument",
            "Card Skimming",
            "SIM Swap",
            "Fraudulent App",
            "Forgery / Modification of Payment",
            "Collect Payment Request",
            "Merchant Collusion",
            "New type of digital frauds",
            "Other");

    public static final List<String> DETECTION_SOURCES = Arrays.asList(
            "Cusomer Complaints",
            "Internal inspections/audit",
            "External audit",
            "MIS of bank",
            "News and Media",
            "CFR/CRILC",
            "Whistleblower",
            "Intimation/notice from Law Enforcement Agencies",
            "Intimation from other banks",
            "EWS triggering",
            "Other");
}
