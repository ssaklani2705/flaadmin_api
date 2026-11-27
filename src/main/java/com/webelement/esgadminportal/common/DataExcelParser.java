package com.webelement.esgadminportal.common;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.webelement.esgadminportal.entity.DataEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.webelement.esgadminportal.common.DataExcelAllowedValues.*;
import java.time.ZoneId;

/**
 * Reads and validates the Data upload template (sheet "Data", 37 columns, row 1 = headers).
 * Pure parsing: no database access. Validation rules mirror the Add Data form (Submit rules).
 */
public class DataExcelParser {

    public static final int MAX_ROWS = 1000;
    public static final int MAX_ERRORS = 200;

    private static final String SHEET_NAME = "Data";
    private static final int MAX_TEXT = 1000;
    private static final Pattern AMOUNT = Pattern.compile("^\\d{1,12}$");
    
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private static final DateTimeFormatter[] DATE_FORMATS = {
        DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)
    };

    // Column positions in the template
    private static final int C_CODE = 0, C_STATE = 1, C_CAT_BRANCH = 2, C_PERP_STATUS = 3, C_ACC_TYPE = 4,
            C_ACC_OTHER = 5, C_SECTOR = 6, C_BORROWAL = 7, C_BANKING = 8, C_AREA = 9, C_AREA_OTHER = 10,
            C_CLASSIF = 11, C_CLASSIF_OTHER = 12, C_FRAUD_TYPE = 13, C_FRAUD_TYPE_OTHER = 14, C_TOTAL = 15,
            C_OCCURRENCE = 16, C_DETECTION = 17, C_CLASSIFICATION = 18, C_REPORTED_RBI = 19, C_DELAY = 20,
            C_FULL_PAYMENT = 21, C_DETECT_METHOD = 22, C_DETECT_OTHER = 23, C_COMMITTED_BY = 24,
            C_CONTROL_OFFICE = 25, C_AUDIT_CONDUCTED = 26, C_AUDIT_REASON = 27, C_AUDIT_ACTION = 28,
            C_LEA = 29, C_STEPS = 30, C_RECOVERED = 31, C_LOSS = 32, C_PROVISION = 33, C_WRITTEN_OFF = 34,
            C_COMPROMISE = 35, C_COMPROMISE_AMT = 36;

    /** Each header (normalized: lowercase letters/digits only) must contain this keyword. */
    private static final String[] HEADER_KEYS = {
        "codeoftheentity", "statebranch", "categoryofbranch", "statusofperpetrator", "typeofaccount",
        "ifother", "industry", "borrowal", "consortium", "areaofoperation", "ifother", "classificationoffraud",
        "ifother", "typeoffraud", "ifother", "totalamount", "dateofoccurrence", "dateofdetection",
        "dateofclassification", "reportedtorbi", "reasonsfordelay", "fullpayment", "howthefraudwasdetected",
        "othersources", "fraudcommittedby", "controllingoffice", "internalinspection", "ifyeswhy",
        "whatactionhasbeentaken", "lawenforcement", "stepstaken", "amountrecovered", "extentofloss",
        "provisionheld", "amountwrittenoff", "compromisesettlementdone", "compromisesettledamount"
    };

    private static final Map<String, String> CATS = index(BRANCH_CATEGORIES);
    private static final Map<String, String> PERPS = index(PERPETRATOR_STATUSES);
    private static final Map<String, String> YES_NO_MAP = index(YES_NO);
    private static final Map<String, String> BANKING = index(BANKING_ARRANGEMENTS);
    private static final Map<String, String> COMMITTED = index(COMMITTED_BY);
    private static final Map<String, String> ACC_TYPES = index(ACCOUNT_TYPES);
    private static final Map<String, String> SECTORS = index(INDUSTRY_SECTORS);
    private static final Map<String, String> AREAS = index(AREAS_OF_OPERATION);
    private static final Map<String, String> CLASSIFS = index(FRAUD_CLASSIFICATIONS);
    private static final Map<String, String> FRAUD_TYPE_MAP = index(FRAUD_TYPES);
    private static final Map<String, String> DETECTIONS = index(DETECTION_SOURCES);

    static {
        // Older Master sheet had this typo; accept it.
        DETECTIONS.put(normalize("Cusomer Complaints"), "Cusomer Complaints");
    }

    // ---------------------------------------------------------------- result types

    @Getter
    @AllArgsConstructor
    public static class RowError {
        private final int row;        // Excel row number (0 = file / header level)
        private final String column;
        private final String message;
    }

    @Getter
    public static class ParseResult {
        private int totalRows;
        private int errorCount;
        private final List<DataEntity> entities = new ArrayList<>();
        private final List<RowError> errors = new ArrayList<>();

        void addError(int row, String column, String message) {
            errorCount++;
            if (errors.size() < MAX_ERRORS) {
                errors.add(new RowError(row, column, message));
            }
        }

        public boolean hasErrors() {
            return errorCount > 0;
        }
    }

    // ---------------------------------------------------------------- entry point

    public static ParseResult parse(InputStream in, Set<String> stateNames,
                                    Integer status, Integer addedBy, Integer bankId) throws IOException {

        ParseResult result = new ParseResult();
        Map<String, String> states = index(stateNames);

        try (Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheet(SHEET_NAME);
            if (sheet == null) {
                sheet = wb.getSheetAt(0);
            }

            DataFormatter fmt = new DataFormatter();
            FormulaEvaluator ev = wb.getCreationHelper().createFormulaEvaluator();

            Row header = sheet.getRow(0);
            if (!headerMatches(header, fmt, ev)) {
                result.addError(0, "Header",
                        "Invalid template. Please download the latest template and do not change the column order.");
                return result;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                RowReader r = new RowReader(row, i + 1, header, fmt, ev, result);

                if (r.isBlank()) {
                    continue;
                }

                result.totalRows++;
//                if (result.totalRows > MAX_ROWS) {
//                    result.addError(0, "File", "Maximum " + MAX_ROWS + " rows are allowed per upload.");
//                    break;
//                }

                int errorsBefore = result.errorCount;
                DataEntity entity = readRow(r, states, status, addedBy, bankId);
                if (result.errorCount == errorsBefore) {
                    result.entities.add(entity);
                }
            }

            if (result.totalRows == 0) {
                result.addError(0, "File", "No data rows found. Fill the data from row 2 of the 'Data' sheet.");
            }
        }
        return result;
    }

    // ---------------------------------------------------------------- row mapping + rules

    private static DataEntity readRow(RowReader r, Map<String, String> states,
                                      Integer status, Integer addedBy, Integer bankId) {

        String code = r.text(C_CODE, true, 50);
        
        for (int c = 0; c < HEADER_KEYS.length; c++) {
            System.out.println("DEBUG col " + c + " [" + r.columnName(c) + "] = [" + r.raw(c) + "]");
        }
        
        String state = r.choice(C_STATE, true, states);
        String catOfBranch = r.choice(C_CAT_BRANCH, true, CATS);
        String perpStatus = r.choice(C_PERP_STATUS, false, PERPS);

        // Dropdown + "If Other (Please specify)" pairs.
        // If the "If Other" cell has text, the dropdown automatically becomes "Other".
        String[] accPair = choiceWithOther(r, C_ACC_TYPE, C_ACC_OTHER, ACC_TYPES);
        String accType = accPair[0];
        String accOther = accPair[1];

        String sector = r.choice(C_SECTOR, true, SECTORS);
        String borrowal = r.choice(C_BORROWAL, true, YES_NO_MAP);
        String banking = r.choice(C_BANKING, true, BANKING);

        String[] areaPair = choiceWithOther(r, C_AREA, C_AREA_OTHER, AREAS);
        String area = areaPair[0];
        String areaOther = areaPair[1];

        String[] classifPair = choiceWithOther(r, C_CLASSIF, C_CLASSIF_OTHER, CLASSIFS);
        String classif = classifPair[0];
        String classifOther = classifPair[1];

        String[] fraudPair = choiceWithOther(r, C_FRAUD_TYPE, C_FRAUD_TYPE_OTHER, FRAUD_TYPE_MAP);
        String fraudType = fraudPair[0];
        String fraudTypeOther = fraudPair[1];

        BigDecimal total = r.amount(C_TOTAL, true, true);

        LocalDate occurrence = r.date(C_OCCURRENCE, true);
        LocalDate detection = r.date(C_DETECTION, true);
        LocalDate classification = r.date(C_CLASSIFICATION, true);
        LocalDate reportedToRbi = r.date(C_REPORTED_RBI, true);
        String delay = r.text(C_DELAY, true, MAX_TEXT);
        LocalDate fullPayment = r.date(C_FULL_PAYMENT, false);

        String[] detectPair = choiceWithOther(r, C_DETECT_METHOD, C_DETECT_OTHER, DETECTIONS);
        String detectMethod = detectPair[0];
        String detectOther = detectPair[1];

        String committedBy = r.choice(C_COMMITTED_BY, true, COMMITTED);
        String controlOffice = r.text(C_CONTROL_OFFICE, true, 200);
        String auditConducted = r.text(C_AUDIT_CONDUCTED, true, 200);
        String auditReason = r.text(C_AUDIT_REASON, true, 200);
        String auditAction = r.text(C_AUDIT_ACTION, true, 200);
        String lea = r.choice(C_LEA, false, YES_NO_MAP);
        String steps = r.text(C_STEPS, true, MAX_TEXT);

        BigDecimal recovered = r.amount(C_RECOVERED, false, false);
        BigDecimal loss = r.amount(C_LOSS, true, false);
        BigDecimal provision = r.amount(C_PROVISION, true, false);
        BigDecimal writtenOff = r.amount(C_WRITTEN_OFF, true, false);

        String compromise = r.choice(C_COMPROMISE, true, YES_NO_MAP);
        BigDecimal compromiseAmt = "Yes".equals(compromise) ? r.amount(C_COMPROMISE_AMT, true, false) : null;

        // Date order (same rules as the form)
        if (occurrence != null && detection != null && detection.isBefore(occurrence)) {
            r.error(C_DETECTION, "Date of detection must be on or after Date of occurrence");
        }
        if (detection != null && classification != null && classification.isBefore(detection)) {
            r.error(C_CLASSIFICATION, "Date of classification must be on or after Date of detection");
        }
        if (classification != null && reportedToRbi != null && reportedToRbi.isBefore(classification)) {
            r.error(C_REPORTED_RBI, "Date reported to RBI must be on or after Date of classification");
        }
        if (occurrence != null && fullPayment != null && fullPayment.isBefore(occurrence)) {
            r.error(C_FULL_PAYMENT, "Date of full payment/settlement must be on or after Date of occurrence");
        }

        return DataEntity.builder()
                .code(code).state(state).catOfBranch(catOfBranch).statusOfPerpetrator(perpStatus)
                .typeOfAccount(accType).typeOfAccOther(accOther).secOfAcc(sector)
                .borrowalAcc(borrowal).bankingArrangement(banking)
                .areaOfOperation(area).areaOther(areaOther)
                .classificationOfFraud(classif).classificationOther(classifOther)
                .typeOfFraud(fraudType).fraudTypeOther(fraudTypeOther)
                .totalAmount(total)
                .occurrence(occurrence).detection(detection).classification(classification)
                .reportedToRbi(reportedToRbi).delayReason(delay).fullPaymentSettlement(fullPayment)
                .detectionMethod(detectMethod).detectionOtherSource(detectOther)
                .fraudCommittedBy(committedBy).controllingOfficeDetect(controlOffice)
                .internalAuditConducted(auditConducted).auditDetectionReason(auditReason)
                .auditActionTaken(auditAction).leaComplaint(lea).preventiveSteps(steps)
                .amountRecovered(recovered).lossToBank(loss).provisionHeld(provision)
                .amountWrittenOff(writtenOff)
                .compromiseSettlement(compromise).compromiseAmount(compromiseAmt)
                .status(status).bankId(bankId).addedBy(addedBy)
                .build();
    }

    /**
     * Dropdown + "If Other (Please specify)" pair.
     * - If the "If Other" cell has any text -> dropdown is forced to "Other"
     *   (even if the dropdown cell is blank, invalid or a different option).
     * - Else if the dropdown is "Other"     -> "If Other" is required.
     * - Else                                -> normal dropdown validation, "If Other" saved as null.
     *
     * @return {selectedValue, otherText}
     */
    
    private static String[] choiceWithOther(RowReader r, int selCol, int otherCol,
                                            Map<String, String> allowed) {
    	System.out.println("DEBUG row dropdown=[" + r.raw(selCol) + "] ifOther=[" + r.raw(otherCol) + "]");

        String canonicalOther = allowed.getOrDefault(normalize("Other"), "Other");

        if (r.raw(otherCol) != null) {
            String other = r.text(otherCol, true, 50);   // still checks the 50 character limit
            return new String[] { canonicalOther, other };
        }

        String selected = r.choice(selCol, true, allowed);
        if (selected != null && "Other".equalsIgnoreCase(selected)) {
            String other = r.text(otherCol, true, 50);   // "Other" selected, so the text is required
            return new String[] { selected, other };
        }
        return new String[] { selected, null };
    }

    private static boolean headerMatches(Row header, DataFormatter fmt, FormulaEvaluator ev) {
        if (header == null) {
            return false;
        }
        for (int c = 0; c < HEADER_KEYS.length; c++) {
            Cell cell = header.getCell(c);
            String text = cell == null ? "" : fmt.formatCellValue(cell, ev);
            if (!text.toLowerCase().replaceAll("[^a-z0-9]", "").contains(HEADER_KEYS[c])) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- cell helper

    private static class RowReader {
        private final Row row;
        private final int excelRow;
        private final Row header;
        private final DataFormatter fmt;
        private final FormulaEvaluator ev;
        private final ParseResult result;

        RowReader(Row row, int excelRow, Row header, DataFormatter fmt, FormulaEvaluator ev, ParseResult result) {
            this.row = row;
            this.excelRow = excelRow;
            this.header = header;
            this.fmt = fmt;
            this.ev = ev;
            this.result = result;
        }

        boolean isBlank() {
            if (row == null) {
                return true;
            }
            for (int c = 0; c < HEADER_KEYS.length; c++) {
                if (raw(c) != null) {
                    return false;
                }
            }
            return true;
        }

        private Cell cell(int c) {
            return row == null ? null : row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        }

        private String raw(int c) {
            Cell cell = cell(c);
            if (cell == null) {
                return null;
            }
            String s = fmt.formatCellValue(cell, ev);
            s = s == null ? "" : s.trim();
            return s.isEmpty() ? null : s;
        }

        private String columnName(int c) {
            Cell h = header.getCell(c);
            String name = h == null ? ("Column " + (c + 1)) : fmt.formatCellValue(h).replace("*", "").trim();
            return name.length() > 70 ? name.substring(0, 70) + "..." : name;
        }

        void error(int c, String msg) {
            result.addError(excelRow, columnName(c), msg);
        }

        String text(int c, boolean required, int maxLen) {
            String s = raw(c);
            if (s == null) {
                if (required) {
                    error(c, "is required");
                }
                return null;
            }
            if (s.length() > maxLen) {
                error(c, "must not exceed " + maxLen + " characters");
                return null;
            }
            return s;
        }

        String choice(int c, boolean required, Map<String, String> allowed) {
            String s = raw(c);
            if (s == null) {
                if (required) {
                    error(c, "is required [v2]");
                }
                return null;
            }
            String canonical = allowed.get(normalize(s));
            if (canonical == null) {
                error(c, "'" + (s.length() > 40 ? s.substring(0, 40) + "..." : s)
                        + "' is not a valid option (see the Master sheet)");
            }
            return canonical;
        }

//        BigDecimal amount(int c, boolean required, boolean mustBePositive) {
//            Cell cell = cell(c);
//            String s = raw(c);
//            if (cell == null || s == null) {
//                if (required) {
//                    error(c, "is required");
//                }
//                return null;
//            }
//            if (cell.getCellType() == CellType.NUMERIC) {
//                try {
//                    s = BigDecimal.valueOf(cell.getNumericCellValue()).toBigIntegerExact().toString();
//                } catch (ArithmeticException ex) {
//                    error(c, "must be a whole number (digits only, maximum 12 digits)");
//                    return null;
//                }
//            }
//            if (!AMOUNT.matcher(s).matches()) {
//                error(c, "must contain digits only (maximum 12 digits)");
//                return null;
//            }
//            BigDecimal value = new BigDecimal(s);
//            if (mustBePositive && value.signum() <= 0) {
//                error(c, "must be greater than zero");
//                return null;
//            }
//            return value;
//        }
        
        
        BigDecimal amount(int c, boolean required, boolean mustBePositive) {
            Cell cell = cell(c);
            String s = raw(c);
            if (cell == null || s == null) {
                if (required) {
                    error(c, "is required");
                }
                return null;
            }
            if (cell.getCellType() == CellType.NUMERIC) {
                try {
                    s = BigDecimal.valueOf(cell.getNumericCellValue()).toBigIntegerExact().toString();
                } catch (ArithmeticException ex) {
                    error(c, "must be a whole number (digits only, maximum 12 digits)");
                    return null;
                }
            } else {
                // Text cell like "1,00,00,00,00,000": remove commas and spaces before validating
                s = s.replace(",", "").replaceAll("\\s+", "");
            }
            if (!AMOUNT.matcher(s).matches()) {
                error(c, "must contain digits only (maximum 12 digits)");
                return null;
            }
            BigDecimal value = new BigDecimal(s);
            if (mustBePositive && value.signum() <= 0) {
                error(c, "must be greater than zero");
                return null;
            }
            return value;
        }
        
        LocalDate date(int c, boolean required) {
            LocalDate d = parseDate(c, required);
            if (d != null && d.isAfter(LocalDate.now(IST))) {
                error(c, "must not be a future date");
                return null;
            }
            return d;
        }

        private LocalDate parseDate(int c, boolean required) {
            Cell cell = cell(c);
            String s = raw(c);
            if (cell == null || s == null) {
                if (required) {
                    error(c, "is required");
                }
                return null;
            }
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                }
                error(c, "must be a valid date (dd-MM-yyyy)");
                return null;
            }
            for (DateTimeFormatter f : DATE_FORMATS) {
                try {
                    return LocalDate.parse(s, f);
                } catch (Exception ignored) {
                    // try next format
                }
            }
            error(c, "must be a valid date (dd-MM-yyyy)");
            return null;
        }

//        LocalDate date(int c, boolean required) {
//            Cell cell = cell(c);
//            String s = raw(c);
//            if (cell == null || s == null) {
//                if (required) {
//                    error(c, "is required");
//                }
//                return null;
//            }
//            if (cell.getCellType() == CellType.NUMERIC) {
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    return cell.getLocalDateTimeCellValue().toLocalDate();
//                }
//                error(c, "must be a valid date (dd-MM-yyyy)");
//                return null;
//            }
//            for (DateTimeFormatter f : DATE_FORMATS) {
//                try {
//                    return LocalDate.parse(s, f);
//                } catch (Exception ignored) {
//                    // try next format
//                }
//            }
//            error(c, "must be a valid date (dd-MM-yyyy)");
//            return null;
//        }
    }
}