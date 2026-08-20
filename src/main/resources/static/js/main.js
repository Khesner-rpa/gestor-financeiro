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

document.addEventListener("change", (event) => {
    const select = event.target.closest(".extract-filter select");
    if (!select) return;

    const filter = select.closest(".extract-filter");
    if (!filter) return;

    const mes = filter.querySelector("#filtro-mes")?.value || "";
    const ano = filter.querySelector("#filtro-ano")?.value || "";

    event.preventDefault();
    clickAjax({
        getAttribute: (name) => {
            if (name === "hx-get")    return `/dashboard?mes=${mes}&ano=${ano}`;
            if (name === "hx-target") return "#dashboard-content";
            if (name === "hx-swap")   return "outerHTML";
            return null;
        }
    });
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
