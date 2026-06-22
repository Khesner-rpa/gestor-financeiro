const storageKey = "gestor-financeiro-theme";

export function applyTheme(theme) {
    const isDark = theme === "dark";
    document.documentElement.dataset.theme = isDark ? "dark" : "light";
    localStorage.setItem(storageKey, theme);

    document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
        button.setAttribute("aria-checked", isDark ? "true" : "false");
        button.setAttribute(
            "aria-label",
            isDark
                ? "Tema escuro ativo. Clique para ativar o tema claro"
                : "Tema claro ativo. Clique para ativar o tema escuro"
        );
    });
}

export function toggleTheme() {
    const current = document.documentElement.dataset.theme === "dark" ? "dark" : "light";
    applyTheme(current === "dark" ? "light" : "dark");
}

export function getSavedTheme() {
    return localStorage.getItem(storageKey);
}
