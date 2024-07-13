package org.da477.SgwClientFree.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.da477.SgwClientFree.utils.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * LogFileView class
 * <p>
 * The main view for displaying log files in the application.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Route(value = "/command", layout = MainLayout.class)
@PageTitle("SGW Client Log File")
@CssImport("./styles/log-view.css")
@Slf4j
public class LogFileView extends VerticalLayout {

    private final FileHelper fileHelper;

    @Autowired
    public LogFileView(FileHelper fileHelper) {
        this.fileHelper = fileHelper;

        setSizeFull();
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        getStyle().set("padding-left", "10px");
        getStyle().set("padding-right", "10px");

        String logHtml = "Logs will appear here";

        Div logContainer = new Div();
        logContainer.addClassName("log-container");
        logContainer.getStyle()
                .set("margin-top", "10px")
                .set("font-size", "11px");
        logContainer.getElement().setProperty("innerHTML", logHtml);

        add(logContainer);
    }

}
