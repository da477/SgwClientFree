package org.da477.SgwClientFree.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.da477.SgwClientFree.model.MessageEntity;
import org.da477.SgwClientFree.service.BankApiService;
import org.da477.SgwClientFree.service.DbService;
import org.da477.SgwClientFree.utils.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.da477.SgwClientFree.utils.DateTimeHelper.VIEW_FORMATTER;
import static org.da477.SgwClientFree.utils.DateTimeHelper.ZONE_ID;

/**
 * DashboardView class
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("SGW Client DashBoard")
@Slf4j
public class DashboardView extends VerticalLayout {

    private final BankApiService bankApiService;
    private final DbService dbService;
    private DatePicker dtStart = new DatePicker("From:");
    private DatePicker dtEnd = new DatePicker("To:");
    private Grid<MessageEntity> grid = new Grid<>(MessageEntity.class, false);
    private final FileHelper fileHelper;

    @Autowired
    public DashboardView(BankApiService bankApiService, DbService dbService, FileHelper fileHelper) {
        this.bankApiService = bankApiService;
        this.dbService = dbService;
        this.fileHelper = fileHelper;

        setSizeFull();
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        getStyle().set("padding-left", "10px");
        getStyle().set("padding-right", "10px");

        adjustDtPickers();
        add(buildButtonLayout(), buildGrid());
    }

    private void adjustDtPickers() {
        AtomicReference<LocalDate> now = new AtomicReference<>(LocalDate.now(ZONE_ID));
        dtStart.setMax(now.get());
        dtStart.setWidth("120px");
        dtStart.addValueChangeListener(e -> {
            now.set(LocalDate.now(ZONE_ID));
            dtStart.setMax(now.get());

            LocalDate value = e.getValue();
            if (value != null) {
                dtStart.setMax(now.get().isBefore(value) ? now.get() : value);
                dtEnd.setMin(value);
            }
        });

        dtEnd.setMax(now.get());
        dtEnd.setWidth("120px");
        dtEnd.addValueChangeListener(e -> {
            now.set(LocalDate.now(ZONE_ID));
            dtEnd.setMax(now.get());

            LocalDate value = e.getValue();
            if (value != null) {
                dtStart.setMax(value);
            }
        });
    }

    private HorizontalLayout buildButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setAlignItems(Alignment.END);
        buttonLayout.setPadding(false);
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle()
                .set("margin-bottom", "10px");

        Button h2Btn = new Button("H2 Console", VaadinIcon.DATABASE.create());
        h2Btn.addClickListener(event ->
                UI.getCurrent().getPage().fetchCurrentURL(
                        currentURL ->
                        {
                            VaadinServletRequest vaadinRequest = VaadinServletRequest.getCurrent();
                            if (vaadinRequest != null) {
                                String h2ConsoleUrl = fileHelper.getProperty("spring.h2.console.path");
                                String contextPath = vaadinRequest.getHttpServletRequest().getContextPath();
                                UI.getCurrent().getPage().open(contextPath + h2ConsoleUrl);
                            }
                        }
                ));

        Button pingButton = new Button("Ping", VaadinIcon.EXCHANGE.create());
        pingButton.addClickListener(event -> handlePingClick());

        Button messagesButton = new Button("Messages", VaadinIcon.REFRESH.create());
        messagesButton.addClickListener(event -> handelMessagesClick());

        Button statementButton = createStatementButton();

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        buttonLayout.add(h2Btn, pingButton, messagesButton, spacer, dtStart, dtEnd, statementButton);
        return buttonLayout;
    }

    private Button createStatementButton() {
        Button statementBtn = new Button("Statement", VaadinIcon.PLAY.create());
        statementBtn.addClickListener(event -> handleStatementClick());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin) {
            statementBtn.setEnabled(false);
            statementBtn.getStyle().set("opacity", "0.5");
        }
        return statementBtn;
    }

    private Grid<MessageEntity> buildGrid() {
        grid.setSizeFull();
        grid.getStyle()
                .set("font-size", "11px");

        grid.addColumn(message -> {
                    Timestamp timestamp = message.getDateTime();
                    return timestamp != null ?
                            timestamp.toLocalDateTime().format(VIEW_FORMATTER) : "";
                })
                .setHeader("Date")
                .setKey("DateTime")
                .setFrozen(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(MessageEntity::getTrackingId)
                .setHeader("ID")
                .setKey("TrackingID")
                .setAutoWidth(true);

        grid.addColumn(MessageEntity::getStatus)
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addColumn(MessageEntity::getType)
                .setHeader("Type")
                .setKey("Type")
                .setAutoWidth(true);

        grid.addColumn(MessageEntity::getFilePath)
                .setHeader("File")
                .setKey("File")
                .setAutoWidth(true);

        updateTableData();

        return grid;
    }

    private void handlePingClick() {
        String response = bankApiService.ping();
        Notification.show("Ping response: " + response);
        updateTableData();
    }

    private void handelMessagesClick() {
        String response;
        try {
            Future<String> futureResult = bankApiService.getMessages();
            response = futureResult.get();
            updateTableData();
        } catch (Exception e) {
            response = e.getMessage();
        }
        Notification.show(response)
                .setDuration(10000);
    }

    private void handleStatementClick() {

        LocalDate now = LocalDate.now(ZONE_ID);
        LocalDate from = dtStart.getValue();
        LocalDate to = dtEnd.getValue();

        String textToShow = "Statement requested from " +
                (from == null ? now : from) +
                " to " +
                (to == null ? now : to);

        String answer = bankApiService.requestStatement(from, to);

        if (answer.startsWith("Timeout")) {
            Notification.show(answer);
            return;
        }

        Notification.show(textToShow);
        log.info(textToShow);

        updateTableData();

        Notification notification = new Notification();
        notification.setText(answer);
        notification.setDuration(10000);
        notification.setPosition(Notification.Position.BOTTOM_STRETCH);
        notification.open();

    }

    private void updateTableData() {
        grid.setItems(dbService.topByLastUpdate());
    }

}
