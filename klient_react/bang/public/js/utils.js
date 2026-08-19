(function() {
    // Translations placeholder - will be populated by postbuild.js
    const translations = /* TRANSLATIONS_START */ {
        "cs": {
                "title": "Nastavení soukromí a herních dat",
                "text": "Pro bezproblémový chod hry ukládáme nezbytná technická data (pro znovupřipojení a jazyk). Pro vylepšování hry využíváme analytické nástroje v rámci EU. Standardně sbíráme pouze anonymní data. Udělením souhlasu nám pomůžete s vývojem povolením podrobnější analytiky a nahrávání chybových relací.",
                "show_details": "Zobrazit podrobnosti a právní upozornění",
                "hide_details": "Skrýt podrobnosti a právní upozornění",
                "legal_text": "Původní verze tohoto projektu vznikla jako školní dílo na Gymnáziu, Praha 6, Arabská 14, v roce 2026. <br><br>Tento web je nekomerční fanouškovský a osobní projekt. Není nijak spojen s původními vydavateli her. BANG!® je registrovaná ochranná známka společnosti daVinci Editrice S.r.l. (dV Giochi), v ČR vydává ALBI Česká republika a.s. UNO® je registrovaná ochranná známka společnosti Mattel, Inc. Veškerá autorská práva k ilustracím karet, názvům a herním mechanismům patří jejich příslušným majitelům. Použité ikony: <a href=\"https://www.flaticon.com/free-icons/fire\" title=\"fire icons\" target=\"_blank\" rel=\"noopener noreferrer\">Freepik - Flaticon</a>. Vlastní kód a zbytek obsahu podléhá licenci v souboru <a href=\"https://github.com/honzaHlavnicka/Bang/blob/master/LICENSE\" target=\"_blank\">LICENSE</a>. Připojením ke hře stvrzujete, že nebudete bez výslovného souhlasu stahovat ani dále šířit žádné obrázky či autorsky chráněný obsah z tohoto webu, a zavazujete se k dodržování zásad slušného chování i zákonů ČR. Pro právní záležitosti kontaktujte prava@honzaa.cz. <br><br><strong>Ochrana soukromí a data:</strong> Web nevyužívá žádné marketingové ani reklamní cookies. V úložišti prohlížeče (LocalStorage) ukládáme pouze nezbytné funkční identifikátory sloužící pro zachování zvoleného jazyka a znovupřipojení k rozehrané hře po odpojení. Pro analýzu chyb a vylepšování hry využíváme nástroj PostHog, přičemž veškerá data jsou zpracovávána výhradně na serverech v rámci Evropské unie. Bez vašeho souhlasu sbíráme pouze základní, plně anonymní technická data bez identifikace uživatele. Rozšířenou analytiku a nahrávání relací pro odhalování chyb spouštíme výhradně až na základě vašeho aktivního souhlasu.",
                "agree": "Souhlasím",
                "decline": "Nesouhlasím",
                "settings": "Nastavení soukromí",
                "warning": "Upozornění:"
        },
        "en": {
                "title": "Privacy & Game Data Settings",
                "text": "To ensure seamless game performance, we store essential technical data (for reconnection and language settings). We use analytics tools hosted within the EU to improve the game. By default, we only collect anonymous data. By granting consent, you help our development by enabling advanced analytics and error session recording.",
                "show_details": "Show details and legal notices",
                "hide_details": "Hide details and legal notices",
                "legal_text": "The original version of this project was created as a school project at Gymnázium, Praha 6, Arabská 14, in 2026. <br><br>This website is a non-commercial, fan-made, and personal project. It is not affiliated with the original game publishers in any way. BANG!® is a registered trademark of daVinci Editrice S.r.l. (dV Giochi). UNO® is a registered trademark of Mattel, Inc. All copyrights to the card illustrations, names, and game mechanics belong to their respective owners. Icons used: <a href=\"https://www.flaticon.com/free-icons/fire\" title=\"fire icons\" target=\"_blank\" rel=\"noopener noreferrer\">Freepik - Flaticon</a>. The source code and remaining content are licensed under the terms found in the <a href=\"https://github.com/honzaHlavnicka/Bang/blob/master/LICENSE\" target=\"_blank\">LICENSE</a> file. By connecting to the game, you confirm that you will not download or further distribute any images or copyrighted content from this website without explicit consent, and you agree to abide by the principles of decent behavior and applicable laws. For legal inquiries, please contact prava@honzaa.cz. <br><br><strong>Privacy and Data Protection:</strong> This website does not use any marketing or advertising cookies. We only store essential functional identifiers in your browser's local storage (LocalStorage) to preserve your selected language and to allow reconnection to an active game session if disconnected. To analyze errors and improve performance, we utilize PostHog, with all data being processed exclusively on servers located within the European Union. Without your consent, we only collect basic, fully anonymized technical data without any user identification. Advanced analytics and session recording for bug tracking are strictly enabled only upon your explicit active consent.",
                "agree": "I agree",
                "decline": "I disagree",
                "settings": "Privacy settings",
                "warning": "Warning:"
        }
} /* TRANSLATIONS_END */;

    // Injected CSS to match React implementation 1:1
    const css = `
        .cookie-banner-wrapper {
            position: fixed;
            bottom: 24px;
            right: 24px;
            z-index: 10000;
            max-width: 420px;
            width: calc(100% - 48px);
            background: var(--panel-bg, #ffffff);
            border: 1px solid var(--border, rgba(139, 90, 43, 0.2));
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
            padding: 24px;
            display: flex;
            flex-direction: column;
            gap: 14px;
            font-size: 14px;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            color: var(--text-color, #2c2c2c);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            animation: slideUpCookieBanner 0.4s ease-out;
            box-sizing: border-box;
        }

        .cookie-banner-wrapper * {
            box-sizing: border-box;
        }

        @keyframes slideUpCookieBanner {
            from {
                transform: translateY(30px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }

        @media (max-width: 600px) {
            .cookie-banner-wrapper {
                bottom: 16px;
                right: 16px;
                left: 16px;
                width: calc(100% - 32px);
                padding: 18px;
                max-width: none;
            }
        }

        .cookie-banner-title {
            font-weight: 700;
            font-size: 16px;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 8px;
            color: var(--text-color, #2c2c2c);
        }

        .cookie-banner-text {
            line-height: 1.5;
            color: var(--text-color, #64748b);
            opacity: 0.9;
            margin: 0;
        }

        .cookie-banner-details-toggle {
            background: none;
            border: none;
            color: var(--link-color, #b06a20);
            cursor: pointer;
            padding: 0;
            font-size: 13px;
            text-align: left;
            text-decoration: underline;
            font-weight: 500;
        }

        .cookie-banner-details-toggle:hover {
            color: var(--link-color, #8b5a2b);
        }

        .cookie-banner-details-content {
            max-height: 140px;
            overflow-y: auto;
            font-size: 11px;
            border-top: 1px dashed var(--border, rgba(0,0,0,0.1));
            padding-top: 10px;
            margin-top: 4px;
            color: var(--text-color, #64748b);
            opacity: 0.85;
            line-height: 1.4;
        }

        .cookie-banner-details-content::-webkit-scrollbar {
            width: 6px;
        }
        .cookie-banner-details-content::-webkit-scrollbar-track {
            background: transparent;
        }
        .cookie-banner-details-content::-webkit-scrollbar-thumb {
            background: var(--border, rgba(0,0,0,0.1));
            border-radius: 3px;
        }

        .cookie-banner-buttons {
            display: flex;
            gap: 12px;
            margin-top: 8px;
        }

        @media (max-width: 400px) {
            .cookie-banner-buttons {
                flex-direction: column;
                gap: 8px;
            }
        }

        .cookie-banner-btn-accept {
            flex: 1.3;
            background-color: #764300;
            border: none;
            color: white;
            padding: 12px 20px;
            font-weight: 700;
            font-size: 14px;
            cursor: pointer;
            border-radius: 10px;
            text-align: center;
            transition: background-color 0.2s, transform 0.1s, box-shadow 0.2s;
            box-shadow: 0 4px 10px rgba(118, 67, 0, 0.2);
        }

        .cookie-banner-btn-accept:hover {
            background-color: #9c5900;
            transform: translateY(-1.5px);
            box-shadow: 0 6px 14px rgba(118, 67, 0, 0.3);
        }

        .cookie-banner-btn-accept:active {
            transform: translateY(0);
        }

        .darkMode .cookie-banner-btn-accept,
        html.darkMode .cookie-banner-btn-accept,
        body.darkMode .cookie-banner-btn-accept {
            background-color: #037da5;
            box-shadow: 0 4px 10px rgba(3, 125, 165, 0.2);
        }

        .darkMode .cookie-banner-btn-accept:hover,
        html.darkMode .cookie-banner-btn-accept:hover,
        body.darkMode .cookie-banner-btn-accept:hover {
            background-color: #0497c7;
            box-shadow: 0 6px 14px rgba(3, 125, 165, 0.3);
        }

        @media (prefers-color-scheme: dark) {
            /* Fallback dark theme styles if darkMode class is missing but prefers-color-scheme is active */
            html:not(.lightMode) .cookie-banner-btn-accept {
                background-color: #037da5;
                box-shadow: 0 4px 10px rgba(3, 125, 165, 0.2);
            }
            html:not(.lightMode) .cookie-banner-btn-accept:hover {
                background-color: #0497c7;
                box-shadow: 0 6px 14px rgba(3, 125, 165, 0.3);
            }
        }

        .cookie-banner-btn-decline {
            flex: 1;
            background-color: transparent;
            border: 1px solid var(--border, rgba(139, 90, 43, 0.2));
            color: var(--text-color, #64748b);
            opacity: 0.9;
            padding: 12px 20px;
            font-weight: 500;
            font-size: 14px;
            cursor: pointer;
            border-radius: 10px;
            text-align: center;
            transition: background-color 0.2s, color 0.2s, border-color 0.2s;
        }

        .cookie-banner-btn-decline:hover {
            background-color: rgba(100, 100, 100, 0.08);
            color: var(--text-color, #2c2c2c);
            border-color: var(--border, rgba(139, 90, 43, 0.4));
        }

        .cookie-settings-trigger {
            position: fixed;
            bottom: 15px;
            left: 15px;
            z-index: 1000;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: rgba(0, 0, 0, 0.5);
            border: 1px solid rgba(255, 255, 255, 0.2);
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 0;
            transition: transform 0.2s, background 0.2s;
            box-shadow: 0 2px 10px rgba(0,0,0,0.3);
        }

        .cookie-settings-trigger:hover {
            background: rgba(0, 0, 0, 0.7);
            transform: scale(1.1);
        }
    `;

    // Detect language
    function getLang() {
        let lang = document.documentElement.lang || 'en';
        if (window.location.pathname.includes('/cs/')) {
            lang = 'cs';
        } else if (window.location.pathname.includes('/en/')) {
            lang = 'en';
        }
        return translations[lang] ? lang : 'en';
    }

    function init() {
        // Inject styles
        const styleSheet = document.createElement("style");
        styleSheet.innerText = css;
        document.head.appendChild(styleSheet);

        const currentConsent = localStorage.getItem("souhlas");

        if (currentConsent === null) {
            showBanner();
        } else {
            showSettingsButton();
            applyConsentToPostHog(currentConsent === "true");
        }
    }

    function applyConsentToPostHog(consent) {
        if (window.posthog) {
            if (consent) {
                window.posthog.set_config({
                    persistence: 'localStorage+cookie',
                    disable_persistence: false,
                    disable_session_recording: false,
                    autocapture: true,
                    capture_performance: true
                });
                if (typeof window.posthog.startSessionRecording === 'function') {
                    window.posthog.startSessionRecording();
                }
            } else {
                window.posthog.set_config({
                    persistence: 'memory',
                    disable_persistence: true,
                    disable_session_recording: true,
                    autocapture: false,
                    capture_performance: false
                });
                if (typeof window.posthog.stopSessionRecording === 'function') {
                    window.posthog.stopSessionRecording();
                }
            }
        }
    }

    function showBanner() {
        // Remove existing banner or settings button if present
        const oldBanner = document.getElementById("cookie-consent-banner");
        if (oldBanner) oldBanner.remove();
        const oldTrigger = document.getElementById("cookie-settings-btn");
        if (oldTrigger) oldTrigger.remove();

        const lang = getLang();
        const t = translations[lang];

        const banner = document.createElement("div");
        banner.id = "cookie-consent-banner";
        banner.className = "cookie-banner-wrapper";
        banner.innerHTML = `
            <h3 class="cookie-banner-title">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="color: orange;">
                    <path d="M12 2a10 10 0 1 0 10 10 4 4 0 0 1-5-5 4 4 0 0 1-5-5" />
                    <path d="M8.5 8.5v.01" />
                    <path d="M16 15.5v.01" />
                    <path d="M12 12v.01" />
                    <path d="M11 17v.01" />
                    <path d="M7 14v.01" />
                </svg>
                ${t.title}
            </h3>
            <p class="cookie-banner-text">${t.text}</p>
            <div>
                <button type="button" id="cookie-details-toggle" class="cookie-banner-details-toggle">${t.show_details}</button>
                <div id="cookie-details-content" class="cookie-banner-details-content" style="display: none;">
                    <strong>${t.warning}</strong> ${t.legal_text}
                </div>
            </div>
            <div class="cookie-banner-buttons">
                <button id="cookie-decline-btn" class="cookie-banner-btn-decline">${t.decline}</button>
                <button id="cookie-accept-btn" class="cookie-banner-btn-accept">${t.agree}</button>
            </div>
        `;

        document.body.appendChild(banner);

        // Details toggle listener
        const toggleBtn = document.getElementById("cookie-details-toggle");
        const detailsDiv = document.getElementById("cookie-details-content");
        toggleBtn.addEventListener("click", () => {
            const isHidden = detailsDiv.style.display === "none";
            detailsDiv.style.display = isHidden ? "block" : "none";
            toggleBtn.innerText = isHidden ? t.hide_details : t.show_details;
            if (window.posthog && typeof window.posthog.capture === 'function') {
                window.posthog.capture('cookie_bar_details_toggled', { show_details: isHidden });
            }
        });

        // Accept listener
        document.getElementById("cookie-accept-btn").addEventListener("click", () => {
            localStorage.setItem("souhlas", "true");
            applyConsentToPostHog(true);
            if (window.posthog && typeof window.posthog.capture === 'function') {
                window.posthog.capture('consent_accepted');
            }
            banner.remove();
            showSettingsButton();
        });

        // Decline listener
        document.getElementById("cookie-decline-btn").addEventListener("click", () => {
            localStorage.setItem("souhlas", "false");
            applyConsentToPostHog(false);
            if (window.posthog && typeof window.posthog.capture === 'function') {
                window.posthog.capture('consent_declined');
            }
            banner.remove();
            showSettingsButton();
        });
    }

    function showSettingsButton() {
        const oldTrigger = document.getElementById("cookie-settings-btn");
        if (oldTrigger) return; // Already exists

        const lang = getLang();
        const t = translations[lang];

        const btn = document.createElement("button");
        btn.id = "cookie-settings-btn";
        btn.className = "cookie-settings-trigger";
        btn.title = t.settings;
        btn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: #fff;">
                <path d="M12 2a10 10 0 1 0 10 10 4 4 0 0 1-5-5 4 4 0 0 1-5-5"/>
                <path d="M8.5 8.5v.01"/>
                <path d="M16 15.5v.01"/>
                <path d="M12 12v.01"/>
                <path d="M11 17v.01"/>
                <path d="M7 14v.01"/>
            </svg>
        `;

        document.body.appendChild(btn);

        btn.addEventListener("click", () => {
            if (window.posthog && typeof window.posthog.capture === 'function') {
                window.posthog.capture('cookie_bar_reopened');
            }
            showBanner();
            btn.remove();
        });
    }

    // Auto-run on DOM ready
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
