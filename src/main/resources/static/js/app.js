<<<<<<< HEAD
(function () {
    const storageKey = "gestor-financeiro-theme";
    const maxBotMessageLength = 220;

    const PANELS = {
        dashboard: { title: "Dashboard", eyebrow: "Resumo do mes" },
        planejamento: { title: "Metas e graficos", eyebrow: "Planejamento" },
        assistente: { title: "Assistente financeiro", eyebrow: "IA local" },
        perfil: { title: "Perfil", eyebrow: "Configuracoes" },
        futuro: { title: "Pix, cripto e acoes", eyebrow: "Recursos futuros" },
        seguranca: { title: "Seguranca e privacidade", eyebrow: "Privacidade" }
    };

    const BOT_PROMPTS = {
        resumo: "Resumir meu mes",
        otimizar: "Como reduzir gastos?",
        meta: "Como esta minha meta?",
        categoria: "Onde gasto mais?"
    };

    function csrf() {
        return {
            token: document.querySelector("meta[name='_csrf']")?.content,
            header: document.querySelector("meta[name='_csrf_header']")?.content
        };
    }

    function hydrate(root) {
        bindOnce(root, "[data-open-panel]", "boundOpenPanel", (button) => {
            button.addEventListener("click", () => openPanel(button.dataset.openPanel));
        });

        bindOnce(root, "[data-form-toggle]", "boundFormToggle", (button) => {
            button.addEventListener("click", () => {
                const form = button.closest(".transacao-form");
                if (!form) {
                    return;
                }
                const expanded = !form.classList.contains("is-expanded");
                form.classList.toggle("is-expanded", expanded);
                form.classList.toggle("is-collapsed", !expanded);
                button.setAttribute("aria-expanded", expanded ? "true" : "false");
            });
        });

        bindOnce(root, "[data-menu-toggle]", "boundMenuToggle", (button) => {
            button.addEventListener("click", () => document.body.classList.toggle("menu-open"));
        });

        bindOnce(root, "[data-theme-toggle]", "boundThemeToggle", (button) => {
            button.addEventListener("click", toggleTheme);
        });

        bindOnce(root, "[data-bot-action]", "boundBotAction", (button) => {
            button.addEventListener("click", () => {
                const prompt = BOT_PROMPTS[button.dataset.botAction] || button.textContent.trim();
                sendBotMessage(prompt, button.dataset.botAction);
            });
        });

        bindOnce(root, "[data-bot-send]", "boundBotSend", (button) => {
            button.addEventListener("click", handleBotSend);
        });

        bindOnce(root, "[data-bot-input]", "boundBotInput", (input) => {
            input.addEventListener("keydown", (event) => {
                if (event.key === "Enter" && !event.shiftKey) {
                    event.preventDefault();
                    handleBotSend();
                }
            });
        });

        bindOnce(root, "[data-gasto-select]", "boundGastoSelect", (select) => {
            select.addEventListener("change", () => {
                const option = select.options[select.selectedIndex];
                const display = document.querySelector("#categoria-display");
                
                if (select.value && display) {
                    display.style.display = "flex";
                    display.style.flexWrap = "wrap";
                    display.querySelector(".cat-nome").textContent = option.text;
                    display.querySelector(".cat-valor").textContent = "R$ " + option.dataset.valor + " (" + option.dataset.percentual + "%)";
                    display.querySelector(".cat-barra").value = option.dataset.percentual;
                    display.querySelector(".cat-barra").setAttribute("value", option.dataset.percentual);
                }
            });
        });

        bindOnce(root, "[data-goal-toggle]", "boundGoalToggle", (button) => {
            button.addEventListener("click", () => {
                const form = button.closest(".insight-card")?.querySelector("[data-goal-form]");
                if (!form) {
                    return;
                }

                const hidden = form.classList.toggle("is-hidden");
                button.textContent = hidden ? "Alterar meta" : "Fechar";
            });
        });
    }

    function bindOnce(root, selector, flag, handler) {
        root.querySelectorAll(selector).forEach((element) => {
            if (element.dataset[flag]) {
                return;
            }
            element.dataset[flag] = "true";
            handler(element);
        });
    }

    function openPanel(panelName) {
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
        if (!config) {
            return;
        }

        const eyebrow = document.querySelector("#page-eyebrow");
        const title = document.querySelector("#page-title");
        const period = document.querySelector("#page-period");

        if (eyebrow) {
            eyebrow.textContent = config.eyebrow;
        }
        if (title) {
            title.textContent = config.title;
        }
        if (period) {
            period.classList.toggle("is-hidden", panelName !== "dashboard");
        }
    }

    function applyTheme(theme) {
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

    function toggleTheme() {
        const current = document.documentElement.dataset.theme === "dark" ? "dark" : "light";
        applyTheme(current === "dark" ? "light" : "dark");
    }

    function replaceTarget(targetSelector, html, swap) {
        const target = document.querySelector(targetSelector);
        const activePanel = document.querySelector(".dashboard-panel.is-active")?.id.replace("panel-", "");

        if (!target) {
            return;
        }

        if (swap === "outerHTML") {
            target.outerHTML = html;
            hydrate(document);
            if (activePanel) {
                openPanel(activePanel);
            }
            return;
        }

        target.innerHTML = html;
        hydrate(target);
    }

    async function submitAjax(form) {
        const url = form.getAttribute("hx-post") || form.action;
        const target = form.getAttribute("hx-target") || "#dashboard-content";
        const swap = form.getAttribute("hx-swap") || "innerHTML";
        const security = csrf();
        const headers = {
            "Content-Type": "application/x-www-form-urlencoded",
            "HX-Request": "true"
        };

        if (security.token && security.header) {
            headers[security.header] = security.token;
        }

        form.classList.add("is-loading");

        try {
            const response = await fetch(url, {
                method: "POST",
                credentials: "same-origin",
                headers,
                body: new URLSearchParams(new FormData(form))
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

    async function clickAjax(trigger) {
        const url = trigger.getAttribute("hx-get");
        const target = trigger.getAttribute("hx-target") || "#dashboard-content";
        const swap = trigger.getAttribute("hx-swap") || "innerHTML";
        const response = await fetch(url, {
            method: "GET",
            credentials: "same-origin",
            headers: { "HX-Request": "true" }
        });

        replaceTarget(target, await response.text(), swap);
    }

    function botContext() {
        const bot = document.querySelector("#finance-bot");
        return {
            saldo: bot?.dataset.saldo || "0,00",
            receitas: bot?.dataset.receitas || "0,00",
            despesas: bot?.dataset.despesas || "0,00",
            categoria: bot?.dataset.categoria || "nenhuma categoria",
            economia: bot?.dataset.economia || "0,00",
            insight: bot?.dataset.insight || ""
        };
    }

    function buildBotAnswer(text, topic) {
        const ctx = botContext();
        const normalized = text.toLowerCase();

        if (topic === "resumo" || normalized.includes("resum")) {
            return "Seu saldo atual e R$ " + ctx.saldo + ". Neste mes voce recebeu R$ "
                + ctx.receitas + " e gastou R$ " + ctx.despesas + ".";
        }

        if (topic === "otimizar" || normalized.includes("reduz") || normalized.includes("econom")) {
            return "Comece revisando " + ctx.categoria + ". Separar R$ " + ctx.economia
                + " para a reserva ja melhora seu ritmo mensal.";
        }

        if (topic === "meta" || normalized.includes("meta") || normalized.includes("reserva")) {
            return "Sua meta de reserva ainda precisa de R$ " + ctx.economia + ". "
                + (ctx.insight || "Revise despesas variaveis para acelerar o progresso.");
        }

        if (topic === "categoria" || normalized.includes("categoria") || normalized.includes("gasto")) {
            return "A categoria com maior peso no mes e " + ctx.categoria + ". "
                + "Priorize cortes pequenos nela antes de mexer em gastos fixos.";
        }

        if (normalized.includes("saldo")) {
            return "Seu saldo atual e R$ " + ctx.saldo + ".";
        }

        if (normalized.includes("receita") || normalized.includes("entrada")) {
            return "Suas receitas do mes somam R$ " + ctx.receitas + ".";
        }

        if (normalized.includes("despesa") || normalized.includes("gastei")) {
            return "Suas despesas do mes somam R$ " + ctx.despesas + ".";
        }

        return ctx.insight || ("Com base nos seus dados: saldo R$ " + ctx.saldo
            + ", receitas R$ " + ctx.receitas + " e despesas R$ " + ctx.despesas + ".");
    }

    function appendBotMessage(text, className) {
        const feed = document.querySelector("#bot-feed");
        if (!feed) {
            return;
        }

        document.querySelector(".bot-welcome")?.classList.add("is-hidden");

        const message = document.createElement("div");
        message.className = className;
        message.textContent = text;
        feed.appendChild(message);
        feed.scrollTop = feed.scrollHeight;
    }

    function sendBotMessage(text, topic) {
        const question = text.trim().slice(0, maxBotMessageLength);
        if (!question) {
            return;
        }

        appendBotMessage(question, "bot-message user-message");
        window.setTimeout(() => {
            appendBotMessage(buildBotAnswer(question, topic), "bot-message bot-message--assistant");
        }, 280);
    }

    function handleBotSend() {
        const input = document.querySelector("#bot-input");
        if (!input) {
            return;
        }

        const text = input.value.trim();
        if (!text) {
            return;
        }

        sendBotMessage(text);
        input.value = "";
        input.focus();
    }

    document.addEventListener("submit", (event) => {
        const form = event.target.closest("form[hx-post]");
        if (!form) {
            return;
        }
        event.preventDefault();
        submitAjax(form);
    });

    document.addEventListener("click", (event) => {
        const trigger = event.target.closest("[hx-get]");
        if (!trigger) {
            return;
        }
        event.preventDefault();
        clickAjax(trigger);
    });

    document.addEventListener("DOMContentLoaded", () => {
        const isAuthPage = document.body.classList.contains("auth-page");
        const savedTheme = localStorage.getItem(storageKey);
        applyTheme(savedTheme || (isAuthPage ? "dark" : "light"));
        hydrate(document);

        if (!isAuthPage) {
            openPanel("dashboard");
        }
    });
})();
=======
(function () {
    const storageKey = "gestor-financeiro-theme";
    const maxBotMessageLength = 220;

    const PANELS = {
        dashboard: { title: "Dashboard", eyebrow: "Resumo do mes" },
        planejamento: { title: "Metas e graficos", eyebrow: "Planejamento" },
        assistente: { title: "Assistente financeiro", eyebrow: "IA local" },
        perfil: { title: "Perfil", eyebrow: "Configuracoes" },
        futuro: { title: "Pix, cripto e acoes", eyebrow: "Recursos futuros" },
        seguranca: { title: "Seguranca e privacidade", eyebrow: "Privacidade" }
    };

    const BOT_PROMPTS = {
        resumo: "Resumir meu mes",
        otimizar: "Como reduzir gastos?",
        meta: "Como esta minha meta?",
        categoria: "Onde gasto mais?"
    };

    function csrf() {
        return {
            token: document.querySelector("meta[name='_csrf']")?.content,
            header: document.querySelector("meta[name='_csrf_header']")?.content
        };
    }

    function hydrate(root) {
        bindOnce(root, "[data-open-panel]", "boundOpenPanel", (button) => {
            button.addEventListener("click", () => openPanel(button.dataset.openPanel));
        });

        bindOnce(root, "[data-form-toggle]", "boundFormToggle", (button) => {
            button.addEventListener("click", () => {
                const form = button.closest(".transacao-form");
                if (!form) {
                    return;
                }
                const expanded = !form.classList.contains("is-expanded");
                form.classList.toggle("is-expanded", expanded);
                form.classList.toggle("is-collapsed", !expanded);
                button.setAttribute("aria-expanded", expanded ? "true" : "false");
            });
        });

        bindOnce(root, "[data-menu-toggle]", "boundMenuToggle", (button) => {
            button.addEventListener("click", () => document.body.classList.toggle("menu-open"));
        });

        bindOnce(root, "[data-theme-toggle]", "boundThemeToggle", (button) => {
            button.addEventListener("click", toggleTheme);
        });

        bindOnce(root, "[data-bot-action]", "boundBotAction", (button) => {
            button.addEventListener("click", () => {
                const prompt = BOT_PROMPTS[button.dataset.botAction] || button.textContent.trim();
                sendBotMessage(prompt, button.dataset.botAction);
            });
        });

        bindOnce(root, "[data-bot-send]", "boundBotSend", (button) => {
            button.addEventListener("click", handleBotSend);
        });

        bindOnce(root, "[data-bot-input]", "boundBotInput", (input) => {
            input.addEventListener("keydown", (event) => {
                if (event.key === "Enter" && !event.shiftKey) {
                    event.preventDefault();
                    handleBotSend();
                }
            });
        });

        bindOnce(root, "[data-gasto-select]", "boundGastoSelect", (select) => {
            select.addEventListener("change", () => {
                const option = select.options[select.selectedIndex];
                const display = document.querySelector("#categoria-display");
                
                if (select.value && display) {
                    display.style.display = "flex";
                    display.style.flexWrap = "wrap";
                    display.querySelector(".cat-nome").textContent = option.text;
                    display.querySelector(".cat-valor").textContent = "R$ " + option.dataset.valor + " (" + option.dataset.percentual + "%)";
                    display.querySelector(".cat-barra").value = option.dataset.percentual;
                    display.querySelector(".cat-barra").setAttribute("value", option.dataset.percentual);
                }
            });
        });
    }

    function bindOnce(root, selector, flag, handler) {
        root.querySelectorAll(selector).forEach((element) => {
            if (element.dataset[flag]) {
                return;
            }
            element.dataset[flag] = "true";
            handler(element);
        });
    }

    function openPanel(panelName) {
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
        if (!config) {
            return;
        }

        const eyebrow = document.querySelector("#page-eyebrow");
        const title = document.querySelector("#page-title");
        const period = document.querySelector("#page-period");

        if (eyebrow) {
            eyebrow.textContent = config.eyebrow;
        }
        if (title) {
            title.textContent = config.title;
        }
        if (period) {
            period.classList.toggle("is-hidden", panelName !== "dashboard");
        }
    }

    function applyTheme(theme) {
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

    function toggleTheme() {
        const current = document.documentElement.dataset.theme === "dark" ? "dark" : "light";
        applyTheme(current === "dark" ? "light" : "dark");
    }

    function replaceTarget(targetSelector, html, swap) {
        const target = document.querySelector(targetSelector);
        const activePanel = document.querySelector(".dashboard-panel.is-active")?.id.replace("panel-", "");

        if (!target) {
            return;
        }

        if (swap === "outerHTML") {
            target.outerHTML = html;
            hydrate(document);
            if (activePanel) {
                openPanel(activePanel);
            }
            return;
        }

        target.innerHTML = html;
        hydrate(target);
    }

    async function submitAjax(form) {
        const url = form.getAttribute("hx-post") || form.action;
        const target = form.getAttribute("hx-target") || "#dashboard-content";
        const swap = form.getAttribute("hx-swap") || "innerHTML";
        const security = csrf();
        const headers = {
            "Content-Type": "application/x-www-form-urlencoded",
            "HX-Request": "true"
        };

        if (security.token && security.header) {
            headers[security.header] = security.token;
        }

        form.classList.add("is-loading");

        try {
            const response = await fetch(url, {
                method: "POST",
                credentials: "same-origin",
                headers,
                body: new URLSearchParams(new FormData(form))
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

    async function clickAjax(trigger) {
        const url = trigger.getAttribute("hx-get");
        const target = trigger.getAttribute("hx-target") || "#dashboard-content";
        const swap = trigger.getAttribute("hx-swap") || "innerHTML";
        const response = await fetch(url, {
            method: "GET",
            credentials: "same-origin",
            headers: { "HX-Request": "true" }
        });

        replaceTarget(target, await response.text(), swap);
    }

    function botContext() {
        const bot = document.querySelector("#finance-bot");
        return {
            saldo: bot?.dataset.saldo || "0,00",
            receitas: bot?.dataset.receitas || "0,00",
            despesas: bot?.dataset.despesas || "0,00",
            categoria: bot?.dataset.categoria || "nenhuma categoria",
            economia: bot?.dataset.economia || "0,00",
            insight: bot?.dataset.insight || ""
        };
    }

    function buildBotAnswer(text, topic) {
        const ctx = botContext();
        const normalized = text.toLowerCase();

        if (topic === "resumo" || normalized.includes("resum")) {
            return "Seu saldo atual e R$ " + ctx.saldo + ". Neste mes voce recebeu R$ "
                + ctx.receitas + " e gastou R$ " + ctx.despesas + ".";
        }

        if (topic === "otimizar" || normalized.includes("reduz") || normalized.includes("econom")) {
            return "Comece revisando " + ctx.categoria + ". Separar R$ " + ctx.economia
                + " para a reserva ja melhora seu ritmo mensal.";
        }

        if (topic === "meta" || normalized.includes("meta") || normalized.includes("reserva")) {
            return "Sua meta de reserva ainda precisa de R$ " + ctx.economia + ". "
                + (ctx.insight || "Revise despesas variaveis para acelerar o progresso.");
        }

        if (topic === "categoria" || normalized.includes("categoria") || normalized.includes("gasto")) {
            return "A categoria com maior peso no mes e " + ctx.categoria + ". "
                + "Priorize cortes pequenos nela antes de mexer em gastos fixos.";
        }

        if (normalized.includes("saldo")) {
            return "Seu saldo atual e R$ " + ctx.saldo + ".";
        }

        if (normalized.includes("receita") || normalized.includes("entrada")) {
            return "Suas receitas do mes somam R$ " + ctx.receitas + ".";
        }

        if (normalized.includes("despesa") || normalized.includes("gastei")) {
            return "Suas despesas do mes somam R$ " + ctx.despesas + ".";
        }

        return ctx.insight || ("Com base nos seus dados: saldo R$ " + ctx.saldo
            + ", receitas R$ " + ctx.receitas + " e despesas R$ " + ctx.despesas + ".");
    }

    function appendBotMessage(text, className) {
        const feed = document.querySelector("#bot-feed");
        if (!feed) {
            return;
        }

        document.querySelector(".bot-welcome")?.classList.add("is-hidden");

        const message = document.createElement("div");
        message.className = className;
        message.textContent = text;
        feed.appendChild(message);
        feed.scrollTop = feed.scrollHeight;
    }

    function sendBotMessage(text, topic) {
        const question = text.trim().slice(0, maxBotMessageLength);
        if (!question) {
            return;
        }

        appendBotMessage(question, "bot-message user-message");
        window.setTimeout(() => {
            appendBotMessage(buildBotAnswer(question, topic), "bot-message bot-message--assistant");
        }, 280);
    }

    function handleBotSend() {
        const input = document.querySelector("#bot-input");
        if (!input) {
            return;
        }

        const text = input.value.trim();
        if (!text) {
            return;
        }

        sendBotMessage(text);
        input.value = "";
        input.focus();
    }

    document.addEventListener("submit", (event) => {
        const form = event.target.closest("form[hx-post]");
        if (!form) {
            return;
        }
        event.preventDefault();
        submitAjax(form);
    });

    document.addEventListener("click", (event) => {
        const trigger = event.target.closest("[hx-get]");
        if (!trigger) {
            return;
        }
        event.preventDefault();
        clickAjax(trigger);
    });

    document.addEventListener("DOMContentLoaded", () => {
        const isAuthPage = document.body.classList.contains("auth-page");
        const savedTheme = localStorage.getItem(storageKey);
        applyTheme(savedTheme || (isAuthPage ? "dark" : "light"));
        hydrate(document);

        if (!isAuthPage) {
            openPanel("dashboard");
        }
    });
})();
>>>>>>> 6be877a264aef21474bf1ccbbf9c660e2ac5dcce
