package org.da477.SgwClientFree.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * InfoView class
 * <p>
 * The main view for displaying information about the application.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
@Route(value = "about", layout = MainLayout.class)
@PageTitle("About")
public class InfoView extends VerticalLayout {

    public InfoView() {

        UI.getCurrent().getElement().getStyle().set("font-size", "12px");
        UI.getCurrent().getElement().getStyle().set("max-height", "89vh");
        UI.getCurrent().getElement().getStyle().set("overflow", "hidden");

        add(new H5("About"));
        add(new Paragraph("SGWClient is a web application designed to facilitate communication with Swedbank SGW API 2.0."));

        add(new H5("Key Features"));
        add(createFeatureItem("-", "Direct connection to Swedbank's services through the SGW API."));
        add(createFeatureItem("-", "Implements stringent security measures including certificate-based authentication to ensure data integrity and confidentiality."));
        add(createFeatureItem("-", "Provides a real-time overview of transactions and communications with the bank."));
        add(createFeatureItem("-", "Includes automated tasks such as statement requests, message retrieval, and log management to facilitate operational efficiency."));

        add(new H5("License"));
        Image licenseImage = new Image("https://licensebuttons.net/l/by/4.0/88x31.png", "CC BY 4.0");
        Anchor licenseLink = new Anchor("https://creativecommons.org/licenses/by/4.0/", "");
        licenseLink.setTarget("_blank");
        licenseLink.getStyle()
                .set("margin-top", "8px");
        licenseLink.getElement().appendChild(licenseImage.getElement());
        Paragraph licenseText = new Paragraph("This project is licensed under the Creative Commons Attribution 4.0 International License. You may use, distribute, and modify it, provided you give attribution.");
        add(licenseLink, licenseText);

        add(new H5("Contact the Developer"));
        add(createFeatureItem("", "For questions or suggestions, feel free to reach out:"));
        add(createFeatureItem("-", "Post directly on GitHub Issues."));
        add(createFeatureItem("-", "Connect with me on LinkedIn."));

        add(copyRightText());

        setAlignItems(Alignment.STRETCH);
        setSpacing(false);
        setSizeFull();
    }

    public Component copyRightText() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setVerticalComponentAlignment(Alignment.CENTER);

        Span copyrightText = new Span("© 2024");
        copyrightText.addClassNames(LumoUtility.FontSize.SMALL);

        Anchor githubLink = new Anchor("https://github.com/da477", "@da477");
        githubLink.setTarget("_blank");
        githubLink.addClassNames(LumoUtility.FontSize.SMALL);

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        layout.add(copyrightText, spacer, githubLink);
        return layout;
    }

    private HorizontalLayout createFeatureItem(String title, String description) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setVerticalComponentAlignment(Alignment.CENTER);

        if (!title.isBlank()) {
            Span titleSpan = new Span(title.isEmpty() ? "" : title + " ");
            layout.add(titleSpan);
        }

        Span descSpan = new Span(description);
        layout.add(descSpan);

        return layout;
    }

}
