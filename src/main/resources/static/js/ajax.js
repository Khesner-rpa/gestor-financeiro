import { openPanel } from "./panels.js";
import { hydrate }   from "./hydrate.js";

export function csrf() {
    return {
        token:  document.querySelector("meta[name='_csrf']")?.content,
        header: document.querySelector("meta[name='_csrf_header']")?.content
    };
}

export function replaceTarget(targetSelector, html, swap) {
    const target      = document.querySelector(targetSelector);
    const activePanel = document.querySelector(".dashboard-panel.is-active")
                            ?.id.replace("panel-", "");

    if (!target) return;

    if (swap === "outerHTML") {
        target.outerHTML = html;
        hydrate(document);
        if (activePanel) openPanel(activePanel);
        return;
    }

    target.innerHTML = html;
    hydrate(target);
}

export async function submitAjax(form) {
    const url      = form.getAttribute("hx-post") || form.action;
    const target   = form.getAttribute("hx-target") || "#dashboard-content";
    const swap     = form.getAttribute("hx-swap")   || "innerHTML";
    const security = csrf();

    const headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "HX-Request":   "true"
    };

    if (security.token && security.header) {
        headers[security.header] = security.token;
    }

    form.classList.add("is-loading");

    try {
        const response = await fetch(url, {
            method:      "POST",
            credentials: "same-origin",
            headers,
            body:        new URLSearchParams(new FormData(form))
        });

        if (response.redirected) {
            window.location.href = response.url;
            return;
        }

        replaceTarget(target, await response.text(), swap);
    } finally {
        form.classList.remove("is-loading");
    }
}

export async function clickAjax(trigger) {
    const url    = trigger.getAttribute("hx-get");
    const target = trigger.getAttribute("hx-target") || "#dashboard-content";
    const swap   = trigger.getAttribute("hx-swap")   || "innerHTML";

    const response = await fetch(url, {
        method:      "GET",
        credentials: "same-origin",
        headers:     { "HX-Request": "true" }
    });

    replaceTarget(target, await response.text(), swap);
}
