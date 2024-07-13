package org.da477.SgwClientFree.utils;

import org.da477.SgwClientFree.model.MessageDTO;

/**
 * BankReqTemplate class
 * <p>
 * Utility class for generating XML templates for bank requests.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
public class BankReqTemplate {

    private static final String TEMPLATE_STATEMENT = "";

    private static final String TEMPLATE_PING = "";

    public static String genStmt(MessageDTO message) {
        return String.format(TEMPLATE_STATEMENT,
                message.getMessageId(),
                message.getCreDtTm(),
                message.getMessageId(),
                message.getIban(),
                message.getFromDate(),
                message.getToDate(),
                message.getFromTime(),
                message.getToTime());
    }

    public static String genPing(MessageDTO message) {
        return String.format(TEMPLATE_PING, message.getMessageId());
    }

}
