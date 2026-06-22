import { applyTheme, getSavedTheme } from "./theme.js";
import { openPanel }                 from "./panels.js";
import { submitAjax, clickAjax }     from "./ajax.js";
import { hydrate }                   from "./hydrate.js";

document.addEventListener("submit", (event) => {
    const form = event.target.closest("form[hx-post]");
    if (!form) return;
    event.preventDefault();
    submitAjax(form);
});

document.addEventListener("click", (event) => {
    const trigger = event.target.closest("[hx-get]");
    if (!trigger) return;
    event.preventDefault();
    clickAjax(trigger);
});

document.addEventListener("DOMContentLoaded", () => {
    const isAuthPage  = document.body.classList.contains("auth-page");
    const savedTheme  = getSavedTheme();

    applyTheme(savedTheme || (isAuthPage ? "dark" : "light"));
    hydrate(document);

    if (!isAuthPage) {
        openPanel("dashboard");
    }
});
