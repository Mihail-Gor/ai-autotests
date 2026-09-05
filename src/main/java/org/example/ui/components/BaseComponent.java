package org.example.ui.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseComponent {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Page page;
    protected final Locator root;

    protected BaseComponent(Page page) {
        this.page = page;
        this.root = null;
    }

    protected BaseComponent(Page page, Locator root) {
        this.page = page;
        this.root = root;
    }

    public Page getPage() {
        return page;
    }

    public Locator getRoot() {
        return root;
    }
}
