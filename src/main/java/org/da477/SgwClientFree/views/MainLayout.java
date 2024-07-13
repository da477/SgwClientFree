package org.da477.SgwClientFree.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * MainLayout class
 * <p>
 * The main view is a top-level placeholder for other views.
 *
 * @author da477
 * @version 1.0
 * @since 5/19/24
 */
public class MainLayout extends AppLayout implements RouterLayout, BeforeEnterObserver {

    private Button logoutButton;
    private Button backButton;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addDrawerContent() {

        Image logo = new Image("images/logo.webp", "SGWClient");
        logo.setHeight("45px");
        logo.getStyle()
                .set("margin-top", "5px");

        Span appName = new Span("SGW Client Free");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        HorizontalLayout headerContent = new HorizontalLayout(logo, appName);
        headerContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        headerContent.setSpacing(true);

        Header header = new Header(headerContent);

        Scroller scroller = new Scroller(createNavigation());
        addToDrawer(header, scroller, createFooter());
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();

        Image logo = new Image("images/logo.webp", "SGWClient");
        logo.setHeight("45px");
        logo.getStyle()
                .set("margin-top", "5px");

        Anchor linkToHome = new Anchor("/", logo);

        Div spacer = new Div();
        spacer.getStyle()
                .set("flex-grow", "1");

        logoutButton = new Button("", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        logoutButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        logoutButton.addClassNames(LumoUtility.Margin.Right.AUTO);
        logoutButton.getStyle()
                .set("margin-right", "5px");
        logoutButton.addClickListener(e -> getUI().ifPresent(ui ->
                ui.getPage().executeJs("location.href='logout'")
        ));

        backButton = new Button("", VaadinIcon.ARROW_BACKWARD.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.getStyle()
                .set("margin-right", "5px");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        addToNavbar(true, toggle, linkToHome, spacer, logoutButton, backButton);
    }

    private Component createFooter() {
        return copyRightFooter();
    }

    private Component copyRightFooter() {
        Footer layout = new Footer();
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

    private SideNav createNavigation() {
        SideNav sideNav = new SideNav();
        sideNav.addItem(
                new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()),
                new SideNavItem("LogFile", LogFileView.class, VaadinIcon.COMMENTS.create()),
                new SideNavItem("About", InfoView.class, VaadinIcon.INFO_CIRCLE.create()));
        return sideNav;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean isIndexPage = event.getLocation().getPath().equals("");
        logoutButton.setVisible(isIndexPage);
        backButton.setVisible(!isIndexPage);
    }

}
