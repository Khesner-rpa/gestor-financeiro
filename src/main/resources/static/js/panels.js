export const PANELS = {
    dashboard:   { title: "Dashboard",                eyebrow: "Resumo do mes" },
    planejamento:{ title: "Metas e graficos",          eyebrow: "Planejamento" },
    assistente:  { title: "Assistente financeiro",     eyebrow: "IA local" },
    perfil:      { title: "Perfil",                    eyebrow: "Configuracoes" },
    futuro:      { title: "Pix, cripto e acoes",       eyebrow: "Recursos futuros" },
    seguranca:   { title: "Seguranca e privacidade",   eyebrow: "Privacidade" }
};

export function openPanel(panelName) {
    document.querySelectorAll(".dashboard-panel").forEach((panel) => {
        panel.classList.toggle("is-active", panel.id === "panel-" + panelName);
    });

    document.querySelectorAll("[data-open-panel]").forEach((button) => {
        button.classList.toggle("is-active", button.dataset.openPanel === panelName);
    });

    document.body.classList.remove("menu-open");
    updatePageHeader(panelName);
}

function updatePageHeader(panelName) {
    const config = PANELS[panelName];
    if (!config) return;

    const eyebrow = document.querySelector("#page-eyebrow");
    const title   = document.querySelector("#page-title");
    const period  = document.querySelector("#page-period");

    if (eyebrow) eyebrow.textContent = config.eyebrow;
    if (title)   title.textContent   = config.title;
    if (period)  period.classList.toggle("is-hidden", panelName !== "dashboard");
}
